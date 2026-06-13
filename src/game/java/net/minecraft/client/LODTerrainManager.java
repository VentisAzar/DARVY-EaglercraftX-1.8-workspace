package net.minecraft.client;

import com.carrotsearch.hppc.LongByteHashMap;
import com.carrotsearch.hppc.LongIntHashMap;
import com.carrotsearch.hppc.cursors.LongByteCursor;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.util.BlockPos;
import net.minecraft.client.renderer.Tessellator;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * Distant Darvy Manager - High Performance LOD System for EaglercraftX
 * Optimized for WebGL/WASM-GC resource constraints.
 */
public class LODTerrainManager {

    public static final LODTerrainManager instance = new LODTerrainManager();

    // Configuration settings - Source of truth for Distant Darvy
    public static boolean enableLODSystem = true;
    public static int lodResolution = 2; // 2x2 sampling
    public static int lodDrawDistance = 128; // Chunks

    // Store X,Z chunk coords as a long key, height as a byte (0-255)
    private final LongByteHashMap heightmapCache = new LongByteHashMap();
    private final LongIntHashMap biomeColorCache = new LongIntHashMap();

    private static final int CHUNK_SIZE = 16;
    private static final int REGION_SIZE = 8; // 8x8 chunks per LOD mesh

    /**
     * Scans a standard Minecraft chunk and compresses it into a surface map.
     * Uses mutable objects to prevent browser memory leaks.
     */
    public synchronized void processChunk(Chunk chunk) {
        if (!enableLODSystem) return;

        int chunkX = chunk.xPosition;
        int chunkZ = chunk.zPosition;

        // CRAZY OPTIMIZATION: Access raw biome data array directly to avoid getBiome() overhead
        byte[] biomeArray = chunk.getBiomeArray();
        BiomeGenBase[] biomeList = BiomeGenBase.getBiomeGenArray();

        int step = lodResolution; 
        for (int x = 0; x < CHUNK_SIZE; x += step) {
            for (int z = 0; z < CHUNK_SIZE; z += step) {
                // We calculate height directly to avoid BlockPos churn
                int y = chunk.getHeightValue(x, z);
                
                // CRAZY OPTIMIZATION: Ultra-fast bitwise index for 16x16 array
                int biomeId = biomeArray[(z << 4) | x] & 0xFF;
                int color = (biomeId < biomeList.length && biomeList[biomeId] != null)
                        ? biomeList[biomeId].color
                        : 0xFFFFFF; // Default white if biome is null

                long blockKey = ((long)(chunkX * CHUNK_SIZE + x) << 32) | ((chunkZ * CHUNK_SIZE + z) & 0xFFFFFFFFL);
                heightmapCache.put(blockKey, (byte)y);
                biomeColorCache.put(blockKey, color);
            }
        }
    }

    public void cleanup(int playerX, int playerZ, int maxDistance) {}
    
    /**
     * Renders the cached terrain data using the Tessellator.
     * Optimized to draw simple quads at a distance.
     */
    public synchronized void renderLODs() {
        if (!enableLODSystem || heightmapCache.isEmpty()) return;

        Minecraft mc = Minecraft.getMinecraft();
        double dX = mc.getRenderManager().viewerPosX;
        double dY = mc.getRenderManager().viewerPosY;
        double dZ = mc.getRenderManager().viewerPosZ;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        // Fix GUI state leakage and prepare for LOD draw
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515); // GL_LEQUAL

        // Resolution determines quad size to fill gaps
        double size = (double) lodResolution;
        
        // Calculate current view distance to cull LODs that are already rendered as real blocks
        int viewDistLimit = (mc.gameSettings.renderDistanceChunks) * 16;
        double viewDistLimitSq = (double)(viewDistLimit * viewDistLimit);

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        
        for (LongByteCursor cursor : heightmapCache) {
            long key = cursor.key;
            double x = (double) (int) (key >> 32);
            double z = (double) (int) (key & 0xFFFFFFFFL);
            
            // Optimization: Skip rendering LODs if they are inside the standard chunk render distance
            double relX = x - dX;
            double relZ = z - dZ;
            if ((relX * relX + relZ * relZ) < viewDistLimitSq) continue;

            double y = (double) (cursor.value & 255);
            int color = biomeColorCache.get(key);

            float r = (float)(color >> 16 & 255) / 255.0F;
            float g = (float)(color >> 8 & 255) / 255.0F;
            float b = (float)(color & 255) / 255.0F;

            // CRAZY OPTIMIZATION: Quads are sized based on resolution to ensure a solid terrain look
            worldrenderer.pos(x - dX, y - dY, z - dZ).color(r, g, b, 1.0F).endVertex();
            worldrenderer.pos(x - dX, y - dY, z + size - dZ).color(r, g, b, 1.0F).endVertex();
            worldrenderer.pos(x + size - dX, y - dY, z + size - dZ).color(r, g, b, 1.0F).endVertex();
            worldrenderer.pos(x + size - dX, y - dY, z - dZ).color(r, g, b, 1.0F).endVertex();
        }
        
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }
}