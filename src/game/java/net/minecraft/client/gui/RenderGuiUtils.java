package net.minecraft.client.gui;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * Clean & High-Performance 2D UI Utilities for DARVY Client.
 * Simplified to fast rectangular boxes with no glassmorphic or rounded-corner overhead.
 */
public class RenderGuiUtils {

    /**
     * Primary HUD & Card Box renderer:
     * Crisp rectangular box with optional 1px border.
     */
    public static void drawGlassCard(float x, float y, float width, float height, float radius) {
        drawBox(x, y, width, height, 0x850A0E18, 0x403B82F6);
    }

    /**
     * Card with custom background and outline colors.
     */
    public static void drawGlassCard(float x, float y, float width, float height, float radius, int bgColor, int outlineColor) {
        drawBox(x, y, width, height, bgColor, outlineColor);
    }

    /**
     * Draws a crisp filled rectangular box with a 1px border.
     */
    public static void drawBox(float x, float y, float width, float height, int bgColor, int outlineColor) {
        if (width <= 0 || height <= 0) return;
        // Background fill
        drawRectFast(x, y, x + width, y + height, bgColor);
        // 1px Border
        if ((outlineColor & 0xFF000000) != 0) {
            drawBoxOutline(x, y, width, height, 1.0F, outlineColor);
        }
    }

    /**
     * Draws a filled rectangle (flat, no rounded corners).
     */
    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        if (width <= 0 || height <= 0) return;
        drawRectFast(x, y, x + width, y + height, color);
    }

    /**
     * Draws a rectangular outline (flat, no rounded corners).
     */
    public static void drawRoundedOutline(float x, float y, float width, float height, float radius, float lineWidth, int color) {
        drawBoxOutline(x, y, width, height, lineWidth, color);
    }

    /**
     * Draws a sharp rectangular outline with given line width.
     */
    public static void drawBoxOutline(float x, float y, float width, float height, float lineWidth, int color) {
        if (width <= 0 || height <= 0) return;
        drawRectFast(x, y, x + width, y + lineWidth, color); // Top
        drawRectFast(x, y + height - lineWidth, x + width, y + height, color); // Bottom
        drawRectFast(x, y + lineWidth, x + lineWidth, y + height - lineWidth, color); // Left
        drawRectFast(x + width - lineWidth, y + lineWidth, x + width, y + height - lineWidth, color); // Right
    }

    /**
     * Draws a vertical gradient rectangle.
     */
    public static void drawGradientRoundedRect(float x, float y, float width, float height, float radius, int topColor, int bottomColor) {
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
     * Compact Info Pill / Box renderer.
     */
    public static void drawPill(float x, float y, float width, float height, int bgColor, int outlineColor) {
        drawBox(x, y, width, height, bgColor, outlineColor);
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

    /**
     * Fast flat quad fill with color.
     */
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
