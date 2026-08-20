package net.minecraft.client.gui;

import net.minecraft.client.PvPClient;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import java.io.IOException;

public class GuiHudEditor extends GuiScreen {
    private final GuiScreen parent;
    
    private boolean draggingFps = false;
    private boolean draggingKeystrokes = false;
    private boolean draggingArmor = false;
    private boolean draggingPotion = false;
    private boolean draggingScoreboard = false;

    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public GuiHudEditor(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(200, width / 2 - 100, height / 2 + 70, 200, 20, "Save & Exit"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 2 + 95, 200, 20, "Reset Positions"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 200) {
            mc.displayGuiScreen(parent);
        } else if (button.id == 1) {
            PvPClient.instance.fpsX = 5; PvPClient.instance.fpsY = 5; PvPClient.instance.fpsScale = 1.0F;
            PvPClient.instance.keystrokesX = 5; PvPClient.instance.keystrokesY = 40; PvPClient.instance.keystrokesScale = 1.0F;
            PvPClient.instance.armorX = 5; PvPClient.instance.armorY = 130; PvPClient.instance.armorScale = 1.0F;
            PvPClient.instance.potionX = 5; PvPClient.instance.potionY = 195; PvPClient.instance.potionScale = 1.0F;
            PvPClient.instance.scoreboardX = 0; PvPClient.instance.scoreboardY = 0; PvPClient.instance.scoreboardScale = 1.0F;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();

        drawCenteredString(fontRendererObj, "\u00a7b\u00a7lDARVY HUD Editor", width / 2, 12, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "\u00a77Click & drag any box to reposition \u00a78| \u00a77Scroll over a box to scale", width / 2, 26, 0xFFFFFF);

        // 1. FPS & CPS Box
        if (PvPClient.instance.pvp_fpsHud || PvPClient.instance.pvp_cpsHud) {
            int fx = PvPClient.instance.fpsX, fy = PvPClient.instance.fpsY;
            int fw = (int)(65 * PvPClient.instance.fpsScale);
            int fh = (int)(26 * PvPClient.instance.fpsScale);
            drawRect(fx, fy, fx + fw, fy + fh, draggingFps ? 0x9900E5FF : 0x6600E5FF);
            drawOutline(fx, fy, fx + fw, fy + fh, 0xFF00E5FF);
            fontRendererObj.drawStringWithShadow("FPS / CPS", fx + 4, fy + 4, 0xFFFFFF);
        }

        // 2. Keystrokes Box
        if (PvPClient.instance.pvp_keystrokesHud) {
            int kx = PvPClient.instance.keystrokesX, ky = PvPClient.instance.keystrokesY;
            int kw = (int)(54 * PvPClient.instance.keystrokesScale);
            int kh = (int)(66 * PvPClient.instance.keystrokesScale);
            drawRect(kx, ky, kx + kw, ky + kh, draggingKeystrokes ? 0x999D4EDD : 0x669D4EDD);
            drawOutline(kx, ky, kx + kw, ky + kh, 0xFF9D4EDD);
            fontRendererObj.drawStringWithShadow("Keystrokes", kx + 4, ky + 4, 0xFFFFFF);
        }

        // 3. Armor HUD Box
        if (PvPClient.instance.pvp_armorStatus) {
            int ax = PvPClient.instance.armorX, ay = PvPClient.instance.armorY;
            int aw = (int)(55 * PvPClient.instance.armorScale);
            int ah = (int)(72 * PvPClient.instance.armorScale);
            drawRect(ax, ay, ax + aw, ay + ah, draggingArmor ? 0x993B82F6 : 0x663B82F6);
            drawOutline(ax, ay, ax + aw, ay + ah, 0xFF3B82F6);
            fontRendererObj.drawStringWithShadow("Armor HUD", ax + 4, ay + 4, 0xFFFFFF);
        }

        // 4. Potion HUD Box
        if (PvPClient.instance.pvp_potionHud) {
            int px = PvPClient.instance.potionX, py = PvPClient.instance.potionY;
            int pw = (int)(85 * PvPClient.instance.potionScale);
            int ph = (int)(45 * PvPClient.instance.potionScale);
            drawRect(px, py, px + pw, py + ph, draggingPotion ? 0x9910B981 : 0x6610B981);
            drawOutline(px, py, px + pw, py + ph, 0xFF10B981);
            fontRendererObj.drawStringWithShadow("Potion HUD", px + 4, py + 4, 0xFFFFFF);
        }

        // 5. Scoreboard Box
        int sx = width - 100 + PvPClient.instance.scoreboardX;
        int sy = height / 2 - 50 + PvPClient.instance.scoreboardY;
        int sw = (int)(95 * PvPClient.instance.scoreboardScale);
        int sh = (int)(100 * PvPClient.instance.scoreboardScale);
        drawRect(sx, sy, sx + sw, sy + sh, draggingScoreboard ? 0x99F59E0B : 0x66F59E0B);
        drawOutline(sx, sy, sx + sw, sy + sh, 0xFFF59E0B);
        fontRendererObj.drawStringWithShadow("Scoreboard", sx + 5, sy + 45, 0xFFFFFF);

        // Update Position while Dragging
        if (draggingFps) {
            PvPClient.instance.fpsX = mouseX - dragOffsetX;
            PvPClient.instance.fpsY = mouseY - dragOffsetY;
        } else if (draggingKeystrokes) {
            PvPClient.instance.keystrokesX = mouseX - dragOffsetX;
            PvPClient.instance.keystrokesY = mouseY - dragOffsetY;
        } else if (draggingArmor) {
            PvPClient.instance.armorX = mouseX - dragOffsetX;
            PvPClient.instance.armorY = mouseY - dragOffsetY;
        } else if (draggingPotion) {
            PvPClient.instance.potionX = mouseX - dragOffsetX;
            PvPClient.instance.potionY = mouseY - dragOffsetY;
        } else if (draggingScoreboard) {
            PvPClient.instance.scoreboardX = mouseX - dragOffsetX - (width - 100);
            PvPClient.instance.scoreboardY = mouseY - dragOffsetY - (height / 2 - 50);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawOutline(int left, int top, int right, int bottom, int color) {
        drawHorizontalLine(left, right, top, color);
        drawHorizontalLine(left, right, bottom, color);
        drawVerticalLine(left, top, bottom, color);
        drawVerticalLine(right, top, bottom, color);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            int mx = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int my = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            float delta = wheel > 0 ? 0.05F : -0.05F;

            // 1. FPS Scale
            int fx = PvPClient.instance.fpsX, fy = PvPClient.instance.fpsY;
            if (isHovered(fx, fy, (int)(65 * PvPClient.instance.fpsScale), (int)(26 * PvPClient.instance.fpsScale), mx, my)) {
                PvPClient.instance.fpsScale = Math.max(0.5F, Math.min(2.0F, PvPClient.instance.fpsScale + delta));
                return;
            }
            // 2. Keystrokes Scale
            int kx = PvPClient.instance.keystrokesX, ky = PvPClient.instance.keystrokesY;
            if (isHovered(kx, ky, (int)(54 * PvPClient.instance.keystrokesScale), (int)(66 * PvPClient.instance.keystrokesScale), mx, my)) {
                PvPClient.instance.keystrokesScale = Math.max(0.5F, Math.min(2.0F, PvPClient.instance.keystrokesScale + delta));
                return;
            }
            // 3. Armor Scale
            int ax = PvPClient.instance.armorX, ay = PvPClient.instance.armorY;
            if (isHovered(ax, ay, (int)(55 * PvPClient.instance.armorScale), (int)(72 * PvPClient.instance.armorScale), mx, my)) {
                PvPClient.instance.armorScale = Math.max(0.5F, Math.min(2.0F, PvPClient.instance.armorScale + delta));
                return;
            }
            // 4. Potion Scale
            int px = PvPClient.instance.potionX, py = PvPClient.instance.potionY;
            if (isHovered(px, py, (int)(85 * PvPClient.instance.potionScale), (int)(45 * PvPClient.instance.potionScale), mx, my)) {
                PvPClient.instance.potionScale = Math.max(0.5F, Math.min(2.0F, PvPClient.instance.potionScale + delta));
                return;
            }
            // 5. Scoreboard Scale
            int sx = width - 100 + PvPClient.instance.scoreboardX;
            int sy = height / 2 - 50 + PvPClient.instance.scoreboardY;
            if (isHovered(sx, sy, (int)(95 * PvPClient.instance.scoreboardScale), (int)(100 * PvPClient.instance.scoreboardScale), mx, my)) {
                PvPClient.instance.scoreboardScale = Math.max(0.5F, Math.min(2.0F, PvPClient.instance.scoreboardScale + delta));
            }
        }
    }

    @Override
    protected void mouseClicked(int mx, int my, int btn) {
        if (btn == 0) {
            int fx = PvPClient.instance.fpsX, fy = PvPClient.instance.fpsY;
            if (isHovered(fx, fy, (int)(65 * PvPClient.instance.fpsScale), (int)(26 * PvPClient.instance.fpsScale), mx, my)) {
                draggingFps = true; dragOffsetX = mx - fx; dragOffsetY = my - fy; super.mouseClicked(mx, my, btn); return;
            }
            int kx = PvPClient.instance.keystrokesX, ky = PvPClient.instance.keystrokesY;
            if (isHovered(kx, ky, (int)(54 * PvPClient.instance.keystrokesScale), (int)(66 * PvPClient.instance.keystrokesScale), mx, my)) {
                draggingKeystrokes = true; dragOffsetX = mx - kx; dragOffsetY = my - ky; super.mouseClicked(mx, my, btn); return;
            }
            int ax = PvPClient.instance.armorX, ay = PvPClient.instance.armorY;
            if (isHovered(ax, ay, (int)(55 * PvPClient.instance.armorScale), (int)(72 * PvPClient.instance.armorScale), mx, my)) {
                draggingArmor = true; dragOffsetX = mx - ax; dragOffsetY = my - ay; super.mouseClicked(mx, my, btn); return;
            }
            int px = PvPClient.instance.potionX, py = PvPClient.instance.potionY;
            if (isHovered(px, py, (int)(85 * PvPClient.instance.potionScale), (int)(45 * PvPClient.instance.potionScale), mx, my)) {
                draggingPotion = true; dragOffsetX = mx - px; dragOffsetY = my - py; super.mouseClicked(mx, my, btn); return;
            }
            int sx = width - 100 + PvPClient.instance.scoreboardX;
            int sy = height / 2 - 50 + PvPClient.instance.scoreboardY;
            if (isHovered(sx, sy, (int)(95 * PvPClient.instance.scoreboardScale), (int)(100 * PvPClient.instance.scoreboardScale), mx, my)) {
                draggingScoreboard = true; dragOffsetX = mx - sx; dragOffsetY = my - sy; super.mouseClicked(mx, my, btn); return;
            }
        }
        super.mouseClicked(mx, my, btn);
    }

    @Override
    protected void mouseReleased(int mx, int my, int state) {
        draggingFps = false;
        draggingKeystrokes = false;
        draggingArmor = false;
        draggingPotion = false;
        draggingScoreboard = false;
        super.mouseReleased(mx, my, state);
    }

    private boolean isHovered(int x, int y, int w, int h, int mx, int my) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}