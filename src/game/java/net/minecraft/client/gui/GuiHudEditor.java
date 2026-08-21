package net.minecraft.client.gui;

import net.minecraft.client.PvPClient;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import java.io.IOException;

public class GuiHudEditor extends GuiScreen {
    private final GuiScreen parent;
    
    private boolean draggingFps = false;
    private boolean draggingCps = false;
    private boolean draggingPing = false;
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
        this.buttonList.clear();
        int btnW = 140;
        int btnH = 20;
        int btnY = height - 36;
        this.buttonList.add(new GuiButton(200, width / 2 - btnW - 8, btnY, btnW, btnH, "\u00a7a\u00a7lSave & Return"));
        this.buttonList.add(new GuiButton(1, width / 2 + 8, btnY, btnW, btnH, "\u00a7cReset Layout"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 200) {
            mc.displayGuiScreen(parent);
        } else if (button.id == 1) {
            PvPClient.instance.resetDefaultPositions();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();

        // Apple style sleek top control banner
        drawRect(width / 2 - 160, 8, width / 2 + 160, 42, 0xDD0D111A);
        drawOutline(width / 2 - 160, 8, width / 2 + 160, 42, 0xFF1F293D);
        drawCenteredString(fontRendererObj, "\u00a7f\u00a7lDARVY \u00a7bHUD Studio", width / 2, 14, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "\u00a77Drag any widget to move \u00a78| \u00a77Scroll over box to scale size", width / 2, 27, 0xCCCCCC);

        // 1. Independent FPS HUD Box
        if (PvPClient.instance.pvp_fpsHud) {
            int fx = PvPClient.instance.fpsX, fy = PvPClient.instance.fpsY;
            int fw = (int)(62 * PvPClient.instance.fpsScale);
            int fh = (int)(15 * PvPClient.instance.fpsScale);
            drawCard(fx, fy, fw, fh, draggingFps ? 0x9900E5FF : 0x4400E5FF, 0xFF00E5FF, "FPS Counter (" + (int)(PvPClient.instance.fpsScale * 100) + "%)");
        }

        // 2. Independent CPS HUD Box
        if (PvPClient.instance.pvp_cpsHud) {
            int cx = PvPClient.instance.cpsX, cy = PvPClient.instance.cpsY;
            int cw = (int)(80 * PvPClient.instance.cpsScale);
            int ch = (int)(15 * PvPClient.instance.cpsScale);
            drawCard(cx, cy, cw, ch, draggingCps ? 0x993B82F6 : 0x443B82F6, 0xFF3B82F6, "CPS Counter (" + (int)(PvPClient.instance.cpsScale * 100) + "%)");
        }

        // 3. Independent Ping HUD Box
        if (PvPClient.instance.pvp_pingHud) {
            int px = PvPClient.instance.pingX, py = PvPClient.instance.pingY;
            int pw = (int)(68 * PvPClient.instance.pingScale);
            int ph = (int)(15 * PvPClient.instance.pingScale);
            drawCard(px, py, pw, ph, draggingPing ? 0x9910B981 : 0x4410B981, 0xFF10B981, "Ping HUD (" + (int)(PvPClient.instance.pingScale * 100) + "%)");
        }

        // 4. Keystrokes Box
        if (PvPClient.instance.pvp_keystrokesHud) {
            int kx = PvPClient.instance.keystrokesX, ky = PvPClient.instance.keystrokesY;
            int kw = (int)(54 * PvPClient.instance.keystrokesScale);
            int kh = (int)(66 * PvPClient.instance.keystrokesScale);
            drawCard(kx, ky, kw, kh, draggingKeystrokes ? 0x998B5CF6 : 0x448B5CF6, 0xFF8B5CF6, "Keystrokes");
        }

        // 5. Armor HUD Box
        if (PvPClient.instance.pvp_armorStatus) {
            int ax = PvPClient.instance.armorX, ay = PvPClient.instance.armorY;
            int aw = (int)(55 * PvPClient.instance.armorScale);
            int ah = (int)(72 * PvPClient.instance.armorScale);
            drawCard(ax, ay, aw, ah, draggingArmor ? 0x9906B6D4 : 0x4406B6D4, 0xFF06B6D4, "Armor Status");
        }

        // 6. Potion HUD Box
        if (PvPClient.instance.pvp_potionHud) {
            int px = PvPClient.instance.potionX, py = PvPClient.instance.potionY;
            int pw = (int)(85 * PvPClient.instance.potionScale);
            int ph = (int)(45 * PvPClient.instance.potionScale);
            drawCard(px, py, pw, ph, draggingPotion ? 0x99EC4899 : 0x44EC4899, 0xFFEC4899, "Potion Status");
        }

        // 7. Scoreboard Box
        int sx = width - 100 + PvPClient.instance.scoreboardX;
        int sy = height / 2 - 50 + PvPClient.instance.scoreboardY;
        int sw = (int)(95 * PvPClient.instance.scoreboardScale);
        int sh = (int)(100 * PvPClient.instance.scoreboardScale);
        drawCard(sx, sy, sw, sh, draggingScoreboard ? 0x99F59E0B : 0x44F59E0B, 0xFFF59E0B, "Scoreboard");

        // Drag updates
        if (draggingFps) {
            PvPClient.instance.fpsX = mouseX - dragOffsetX;
            PvPClient.instance.fpsY = mouseY - dragOffsetY;
        } else if (draggingCps) {
            PvPClient.instance.cpsX = mouseX - dragOffsetX;
            PvPClient.instance.cpsY = mouseY - dragOffsetY;
        } else if (draggingPing) {
            PvPClient.instance.pingX = mouseX - dragOffsetX;
            PvPClient.instance.pingY = mouseY - dragOffsetY;
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

    private void drawCard(int x, int y, int w, int h, int bg, int border, String label) {
        drawRect(x, y, x + w, y + h, bg);
        drawOutline(x, y, x + w, y + h, border);
        fontRendererObj.drawStringWithShadow("\u00a7f" + label, x + 3, y + (h > 18 ? 4 : (h - 8) / 2), 0xFFFFFF);
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
            if (isHovered(PvPClient.instance.fpsX, PvPClient.instance.fpsY, (int)(62 * PvPClient.instance.fpsScale), (int)(15 * PvPClient.instance.fpsScale), mx, my)) {
                PvPClient.instance.fpsScale = Math.max(0.5F, Math.min(2.5F, PvPClient.instance.fpsScale + delta));
                return;
            }
            // 2. CPS Scale
            if (isHovered(PvPClient.instance.cpsX, PvPClient.instance.cpsY, (int)(80 * PvPClient.instance.cpsScale), (int)(15 * PvPClient.instance.cpsScale), mx, my)) {
                PvPClient.instance.cpsScale = Math.max(0.5F, Math.min(2.5F, PvPClient.instance.cpsScale + delta));
                return;
            }
            // 3. Ping Scale
            if (isHovered(PvPClient.instance.pingX, PvPClient.instance.pingY, (int)(68 * PvPClient.instance.pingScale), (int)(15 * PvPClient.instance.pingScale), mx, my)) {
                PvPClient.instance.pingScale = Math.max(0.5F, Math.min(2.5F, PvPClient.instance.pingScale + delta));
                return;
            }
            // 4. Keystrokes Scale
            if (isHovered(PvPClient.instance.keystrokesX, PvPClient.instance.keystrokesY, (int)(54 * PvPClient.instance.keystrokesScale), (int)(66 * PvPClient.instance.keystrokesScale), mx, my)) {
                PvPClient.instance.keystrokesScale = Math.max(0.5F, Math.min(2.5F, PvPClient.instance.keystrokesScale + delta));
                return;
            }
            // 5. Armor Scale
            if (isHovered(PvPClient.instance.armorX, PvPClient.instance.armorY, (int)(55 * PvPClient.instance.armorScale), (int)(72 * PvPClient.instance.armorScale), mx, my)) {
                PvPClient.instance.armorScale = Math.max(0.5F, Math.min(2.5F, PvPClient.instance.armorScale + delta));
                return;
            }
            // 6. Potion Scale
            if (isHovered(PvPClient.instance.potionX, PvPClient.instance.potionY, (int)(85 * PvPClient.instance.potionScale), (int)(45 * PvPClient.instance.potionScale), mx, my)) {
                PvPClient.instance.potionScale = Math.max(0.5F, Math.min(2.5F, PvPClient.instance.potionScale + delta));
                return;
            }
            // 7. Scoreboard Scale
            int sx = width - 100 + PvPClient.instance.scoreboardX;
            int sy = height / 2 - 50 + PvPClient.instance.scoreboardY;
            if (isHovered(sx, sy, (int)(95 * PvPClient.instance.scoreboardScale), (int)(100 * PvPClient.instance.scoreboardScale), mx, my)) {
                PvPClient.instance.scoreboardScale = Math.max(0.5F, Math.min(2.5F, PvPClient.instance.scoreboardScale + delta));
            }
        }
    }

    @Override
    protected void mouseClicked(int mx, int my, int btn) {
        if (btn == 0) {
            // Check FPS
            int fx = PvPClient.instance.fpsX, fy = PvPClient.instance.fpsY;
            if (isHovered(fx, fy, (int)(62 * PvPClient.instance.fpsScale), (int)(15 * PvPClient.instance.fpsScale), mx, my)) {
                draggingFps = true; dragOffsetX = mx - fx; dragOffsetY = my - fy; super.mouseClicked(mx, my, btn); return;
            }
            // Check CPS
            int cx = PvPClient.instance.cpsX, cy = PvPClient.instance.cpsY;
            if (isHovered(cx, cy, (int)(80 * PvPClient.instance.cpsScale), (int)(15 * PvPClient.instance.cpsScale), mx, my)) {
                draggingCps = true; dragOffsetX = mx - cx; dragOffsetY = my - cy; super.mouseClicked(mx, my, btn); return;
            }
            // Check Ping
            int px = PvPClient.instance.pingX, py = PvPClient.instance.pingY;
            if (isHovered(px, py, (int)(68 * PvPClient.instance.pingScale), (int)(15 * PvPClient.instance.pingScale), mx, my)) {
                draggingPing = true; dragOffsetX = mx - px; dragOffsetY = my - py; super.mouseClicked(mx, my, btn); return;
            }
            // Check Keystrokes
            int kx = PvPClient.instance.keystrokesX, ky = PvPClient.instance.keystrokesY;
            if (isHovered(kx, ky, (int)(54 * PvPClient.instance.keystrokesScale), (int)(66 * PvPClient.instance.keystrokesScale), mx, my)) {
                draggingKeystrokes = true; dragOffsetX = mx - kx; dragOffsetY = my - ky; super.mouseClicked(mx, my, btn); return;
            }
            // Check Armor
            int ax = PvPClient.instance.armorX, ay = PvPClient.instance.armorY;
            if (isHovered(ax, ay, (int)(55 * PvPClient.instance.armorScale), (int)(72 * PvPClient.instance.armorScale), mx, my)) {
                draggingArmor = true; dragOffsetX = mx - ax; dragOffsetY = my - ay; super.mouseClicked(mx, my, btn); return;
            }
            // Check Potion
            int potX = PvPClient.instance.potionX, potY = PvPClient.instance.potionY;
            if (isHovered(potX, potY, (int)(85 * PvPClient.instance.potionScale), (int)(45 * PvPClient.instance.potionScale), mx, my)) {
                draggingPotion = true; dragOffsetX = mx - potX; dragOffsetY = my - potY; super.mouseClicked(mx, my, btn); return;
            }
            // Check Scoreboard
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
        draggingCps = false;
        draggingPing = false;
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