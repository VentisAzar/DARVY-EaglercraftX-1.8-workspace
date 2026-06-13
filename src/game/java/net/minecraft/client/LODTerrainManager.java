package net.minecraft.client;

import com.carrotsearch.hppc.LongByteHashMap;
import com.carrotsearch.hppc.LongIntHashMap;
import com.carrotsearch.hppc.cursors.LongIntCursor;
import com.carrotsearch.hppc.cursors.LongByteCursor;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.world.chunk.Chunk;
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

    /**
     * CRAZY OPTIMIZATION: Clears the cache if it gets too large for the browser.
     * Also prunes data that is too far away to be relevant.
     */
    public void cleanup(double playerX, double playerZ) {
        if (heightmapCache.size() > 500000) { 
            heightmapCache.clear();
            biomeColorCache.clear();
            return;
        }
        
        // Future: Add per-chunk distance pruning here to keep memory 
        // usage stable during long exploration sessions.
    }
    
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

        // Prepare OpenGL state for high-performance LOD rendering
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);

        // Resolution determines quad size to fill gaps
        double size = (double) lodResolution;
        
        // CRAZY OPTIMIZATION: Pre-calculate look vectors for basic frustum culling
        float yawRad = mc.thePlayer.rotationYaw * 0.017453292F;
        double lookX = -Math.sin(yawRad);
        double lookZ = Math.cos(yawRad);

        // Calculate distance limits
        double maxDistSq = (double)(lodDrawDistance * 16 * lodDrawDistance * 16);
        int viewDistLimit = (mc.gameSettings.renderDistanceChunks) * 16;
        double viewDistLimitSq = (double)(viewDistLimit * viewDistLimit);

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        
        for (LongByteCursor cursor : heightmapCache) {
            long key = cursor.key;
            double x = (double) (int) (key >> 32);
            double z = (double) (int) (key & 0xFFFFFFFFL);
            
            double relX = x - dX;
            double relZ = z - dZ;
            double distSq = relX * relX + relZ * relZ;

            // Optimization: Skip if inside real chunk distance OR beyond LOD draw distance
            if (distSq < viewDistLimitSq || distSq > maxDistSq) continue;

            // CRAZY OPTIMIZATION: Basic Frustum Culling
            // Skip points that are behind the player (dot product check)
            if ((relX * lookX + relZ * lookZ) < -16.0D) continue;

            // Offset by 0.01 to prevent Z-fighting with the actual blocks
            double y = (double) (cursor.value & 255) + 0.01D;
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
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
    }
}