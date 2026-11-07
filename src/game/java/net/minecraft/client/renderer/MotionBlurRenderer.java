package net.minecraft.client.renderer;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;
import static net.lax1dude.eaglercraft.v1_8.internal.PlatformOpenGL.*;

import net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PvPClient;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * Motion Blur (Monkey Blur) accumulation renderer for DARVY Client.
 * 
 * Ported to Eaglercraft using native WebGL/GLES texture copy and blending.
 * Strength 1-10 controls the blur intensity.
 */
public class MotionBlurRenderer {

    private static int blurTexture = -1;
    private static int currentWidth = -1;
    private static int currentHeight = -1;
    private static boolean hasData = false;

    /**
     * Called after the 3D world is rendered, before 2D overlays and HUD.
     * Blends previous frames onto the current frame and captures the result.
     */
    public static void applyMotionBlur() {
        PvPClient pvp = PvPClient.instance;
        if (!pvp.pvp_motionBlur) {
            hasData = false;
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        int width = mc.displayWidth;
        int height = mc.displayHeight;

        if (width <= 0 || height <= 0) return;

        // Initialize texture if needed
        if (blurTexture == -1) {
            initTexture(width, height);
        }

        // Handle window/viewport resizing
        if (currentWidth != width || currentHeight != height) {
            resizeTexture(width, height);
        }

        // Strength 1-10 mapped to alpha (0.10 to 0.90)
        // Strength 5 = 0.46 (ideal responsive motion blur)
        int strength = Math.max(1, Math.min(10, pvp.motionBlurStrength));
        float alpha = 0.10f + (strength - 1) * (0.80f / 9.0f);

        if (hasData) {
            // Draw the accumulated previous frame over the current frame
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableLighting();

            GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
            GlStateManager.bindTexture(blurTexture);

            GlStateManager.matrixMode(GL_PROJECTION);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, width, height, 0.0D, -1.0D, 1.0D);

            GlStateManager.matrixMode(GL_MODELVIEW);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();

            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer wr = tessellator.getWorldRenderer();
            wr.begin(7, DefaultVertexFormats.POSITION_TEX);
            wr.pos(0.0D, (double) height, 0.0D).tex(0.0D, 0.0D).endVertex();
            wr.pos((double) width, (double) height, 0.0D).tex(1.0D, 0.0D).endVertex();
            wr.pos((double) width, 0.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
            wr.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
            tessellator.draw();

            GlStateManager.matrixMode(GL_PROJECTION);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL_MODELVIEW);
            GlStateManager.popMatrix();

            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }

        // Capture screen to texture for next frame's accumulation
        captureScreen(width, height);
        hasData = true;
    }

    private static void initTexture(int width, int height) {
        blurTexture = GlStateManager.generateTexture();
        GlStateManager.bindTexture(blurTexture);
        _wglTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        _wglTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        _wglTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        _wglTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        EaglercraftGPU.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        currentWidth = width;
        currentHeight = height;
        hasData = false;
    }

    private static void resizeTexture(int width, int height) {
        GlStateManager.bindTexture(blurTexture);
        EaglercraftGPU.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        currentWidth = width;
        currentHeight = height;
        hasData = false;
    }

    private static void captureScreen(int width, int height) {
        GlStateManager.bindTexture(blurTexture);
        _wglCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, width, height);
    }

    public static void destroy() {
        if (blurTexture != -1) {
            GlStateManager.deleteTexture(blurTexture);
            blurTexture = -1;
        }
        currentWidth = -1;
        currentHeight = -1;
        hasData = false;
    }

    public static void reset() {
        hasData = false;
    }
}
