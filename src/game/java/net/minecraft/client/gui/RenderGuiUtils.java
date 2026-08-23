package net.minecraft.client.gui;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * Modern 2D UI & Glassmorphic Shader/Rendering Utilities for DARVY Client.
 * Fully compatible with EaglercraftX WebGL and Desktop GL pipeline.
 */
public class RenderGuiUtils {

    /**
     * Primary Glass Card renderer:
     * Dark translucent rounded rectangle (0xAA101014) with a subtle light outline (0x25FFFFFF).
     */
    public static void drawGlassCard(float x, float y, float width, float height, float radius) {
        drawRoundedRect(x, y, width, height, radius, 0xAA101014);
        drawRoundedOutline(x, y, width, height, radius, 1.0F, 0x25FFFFFF);
    }

    /**
     * Glass Card with custom background and outline colors.
     */
    public static void drawGlassCard(float x, float y, float width, float height, float radius, int bgColor, int outlineColor) {
        drawRoundedRect(x, y, width, height, radius, bgColor);
        if ((outlineColor & 0xFF000000) != 0) {
            drawRoundedOutline(x, y, width, height, radius, 1.0F, outlineColor);
        }
    }

    /**
     * Draws a filled rounded rectangle with smooth corner arcs.
     */
    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        if (width <= 0 || height <= 0) return;
        radius = Math.min(radius, Math.min(width / 2.0F, height / 2.0F));
        if (radius <= 0.5F) {
            drawRectFast(x, y, x + width, y + height, color);
            return;
        }

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.color(red, green, blue, alpha);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer wr = tessellator.getWorldRenderer();

        // 1. Center & edge cross rectangles
        drawRectFast(x + radius, y, x + width - radius, y + height, color);
        drawRectFast(x, y + radius, x + radius, y + height - radius, color);
        drawRectFast(x + width - radius, y + radius, x + width, y + height - radius, color);

        // 2. Corner Fan Arcs
        drawArc(x + radius, y + radius, radius, 180, 270, color); // Top-Left
        drawArc(x + width - radius, y + radius, radius, 270, 360, color); // Top-Right
        drawArc(x + width - radius, y + height - radius, radius, 0, 90, color); // Bottom-Right
        drawArc(x + radius, y + height - radius, radius, 90, 180, color); // Bottom-Left

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Draws a rounded rectangle outline.
     */
    public static void drawRoundedOutline(float x, float y, float width, float height, float radius, float lineWidth, int color) {
        if (width <= 0 || height <= 0) return;
        radius = Math.min(radius, Math.min(width / 2.0F, height / 2.0F));

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.color(red, green, blue, alpha);

        // Straight segments
        drawRectFast(x + radius, y, x + width - radius, y + lineWidth, color); // Top
        drawRectFast(x + radius, y + height - lineWidth, x + width - radius, y + height, color); // Bottom
        drawRectFast(x, y + radius, x + lineWidth, y + height - radius, color); // Left
        drawRectFast(x + width - lineWidth, y + radius, x + width, y + height - radius, color); // Right

        // Corner curve strokes
        drawArcOutline(x + radius, y + radius, radius, 180, 270, lineWidth, color);
        drawArcOutline(x + width - radius, y + radius, radius, 270, 360, lineWidth, color);
        drawArcOutline(x + width - radius, y + height - radius, radius, 0, 90, lineWidth, color);
        drawArcOutline(x + radius, y + height - radius, radius, 90, 180, lineWidth, color);

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Draws a vertical gradient rounded rectangle.
     */
    public static void drawGradientRoundedRect(float x, float y, float width, float height, float radius, int topColor, int bottomColor) {
        drawRoundedRect(x, y, width, height, radius, topColor);
        // Overlay gradient blending
        float alphaT = (float) (topColor >> 24 & 255) / 255.0F;
        float redT = (float) (topColor >> 16 & 255) / 255.0F;
        float greenT = (float) (topColor >> 8 & 255) / 255.0F;
        float blueT = (float) (topColor & 255) / 255.0F;

        float alphaB = (float) (bottomColor >> 24 & 255) / 255.0F;
        float redB = (float) (bottomColor >> 16 & 255) / 255.0F;
        float greenB = (float) (bottomColor >> 8 & 255) / 255.0F;
        float blueB = (float) (bottomColor & 255) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer wr = tessellator.getWorldRenderer();
        wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
        wr.pos(x + width, y, 0.0D).color(redT, greenT, blueT, alphaT).endVertex();
        wr.pos(x, y, 0.0D).color(redT, greenT, blueT, alphaT).endVertex();
        wr.pos(x, y + height, 0.0D).color(redB, greenB, blueB, alphaB).endVertex();
        wr.pos(x + width, y + height, 0.0D).color(redB, greenB, blueB, alphaB).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Compact Info Pill renderer.
     */
    public static void drawPill(float x, float y, float width, float height, int bgColor, int outlineColor) {
        float radius = height / 2.0F;
        drawRoundedRect(x, y, width, height, radius, bgColor);
        if ((outlineColor & 0xFF000000) != 0) {
            drawRoundedOutline(x, y, width, height, radius, 1.0F, outlineColor);
        }
    }

    /**
     * Draws a filled solid circle.
     */
    public static void drawCircle(float cx, float cy, float radius, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.color(red, green, blue, alpha);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer wr = tessellator.getWorldRenderer();
        wr.begin(GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        wr.pos(cx, cy, 0.0D).endVertex();

        int segments = 36;
        for (int i = 0; i <= segments; i++) {
            double theta = (i * 2.0 * Math.PI) / segments;
            wr.pos(cx + Math.cos(theta) * radius, cy + Math.sin(theta) * radius, 0.0D).endVertex();
        }
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Draws a circular outline / ring.
     */
    public static void drawCircleOutline(float cx, float cy, float radius, float lineWidth, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.color(red, green, blue, alpha);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer wr = tessellator.getWorldRenderer();
        wr.begin(GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION);

        float rInner = radius - lineWidth / 2.0F;
        float rOuter = radius + lineWidth / 2.0F;
        int segments = 40;

        for (int i = 0; i <= segments; i++) {
            double theta = (i * 2.0 * Math.PI) / segments;
            double cos = Math.cos(theta);
            double sin = Math.sin(theta);
            wr.pos(cx + cos * rOuter, cy + sin * rOuter, 0.0D).endVertex();
            wr.pos(cx + cos * rInner, cy + sin * rInner, 0.0D).endVertex();
        }
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Draws an arc progress bar (e.g. for cooldown timers).
     */
    public static void drawArcProgress(float cx, float cy, float radius, float lineWidth, float progress, int color) {
        if (progress <= 0.0F) return;
        progress = Math.min(1.0F, progress);

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.color(red, green, blue, alpha);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer wr = tessellator.getWorldRenderer();
        wr.begin(GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION);

        float rInner = radius - lineWidth / 2.0F;
        float rOuter = radius + lineWidth / 2.0F;
        int segments = (int) (40 * progress);
        if (segments < 3) segments = 3;

        double startAngle = -Math.PI / 2.0; // Start at top (12 o'clock)
        double totalAngle = 2.0 * Math.PI * progress;

        for (int i = 0; i <= segments; i++) {
            double theta = startAngle + (i * totalAngle) / segments;
            double cos = Math.cos(theta);
            double sin = Math.sin(theta);
            wr.pos(cx + cos * rOuter, cy + sin * rOuter, 0.0D).endVertex();
            wr.pos(cx + cos * rInner, cy + sin * rInner, 0.0D).endVertex();
        }
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Draws a line between two points with thickness.
     */
    public static void drawLine(float x1, float y1, float x2, float y2, float width, int color) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len <= 0.0001F) return;

        float nx = -dy / len * (width / 2.0F);
        float ny = dx / len * (width / 2.0F);

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.color(red, green, blue, alpha);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer wr = tessellator.getWorldRenderer();
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(x1 + nx, y1 + ny, 0.0D).endVertex();
        wr.pos(x2 + nx, y2 + ny, 0.0D).endVertex();
        wr.pos(x2 - nx, y2 - ny, 0.0D).endVertex();
        wr.pos(x1 - nx, y1 - ny, 0.0D).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    // Helper: Filled Arc
    private static void drawArc(float cx, float cy, float radius, float startDeg, float endDeg, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer wr = tessellator.getWorldRenderer();
        wr.begin(GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        wr.pos(cx, cy, 0.0D).endVertex();

        int segments = 10;
        for (int i = 0; i <= segments; i++) {
            float deg = startDeg + (endDeg - startDeg) * (i / (float) segments);
            double rad = Math.toRadians(deg);
            wr.pos(cx + Math.cos(rad) * radius, cy + Math.sin(rad) * radius, 0.0D).endVertex();
        }
        tessellator.draw();
    }

    // Helper: Arc Outline Stroke
    private static void drawArcOutline(float cx, float cy, float radius, float startDeg, float endDeg, float lineWidth, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer wr = tessellator.getWorldRenderer();
        wr.begin(GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION);

        float rInner = radius - lineWidth / 2.0F;
        float rOuter = radius + lineWidth / 2.0F;
        int segments = 10;

        for (int i = 0; i <= segments; i++) {
            float deg = startDeg + (endDeg - startDeg) * (i / (float) segments);
            double rad = Math.toRadians(deg);
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);
            wr.pos(cx + cos * rOuter, cy + sin * rOuter, 0.0D).endVertex();
            wr.pos(cx + cos * rInner, cy + sin * rInner, 0.0D).endVertex();
        }
        tessellator.draw();
    }

    // Helper: Fast quad fill
    public static void drawRectFast(float left, float top, float right, float bottom, int color) {
        if (left < right) {
            float i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            float j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.color(f, f1, f2, f3);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer wr = tessellator.getWorldRenderer();
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(left, bottom, 0.0D).endVertex();
        wr.pos(right, bottom, 0.0D).endVertex();
        wr.pos(right, top, 0.0D).endVertex();
        wr.pos(left, top, 0.0D).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
