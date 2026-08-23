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
    private boolean draggingClock = false;
    private boolean draggingCoords = false;
    private boolean draggingKeystrokes = false;
    private boolean draggingArmor = false;
    private boolean draggingPotion = false;
    private boolean draggingCalendar = false;
    private boolean draggingCooldowns = false;
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

        // Control banner
        RenderGuiUtils.drawGlassCard(width / 2 - 175, 6, 350, 38, 6.0F, 0xDD0D111A, 0xFF1F293D);
        drawCenteredString(fontRendererObj, "\u00a7f\u00a7lDARVY \u00a7bHUD Studio", width / 2, 12, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "\u00a77Drag widgets to reposition \u00a78| \u00a77Scroll to resize", width / 2, 26, 0xCCCCCC);

        // 1. FPS HUD Box
        if (PvPClient.instance.pvp_fpsHud) {
            drawCard(PvPClient.instance.fpsX, PvPClient.instance.fpsY, scl(65, PvPClient.instance.fpsScale), scl(16, PvPClient.instance.fpsScale),
                draggingFps, 0xFF00E5FF, "FPS (" + pct(PvPClient.instance.fpsScale) + "%)");
        }

        // 2. CPS HUD Box
        if (PvPClient.instance.pvp_cpsHud) {
            drawCard(PvPClient.instance.cpsX, PvPClient.instance.cpsY, scl(82, PvPClient.instance.cpsScale), scl(16, PvPClient.instance.cpsScale),
                draggingCps, 0xFF3B82F6, "CPS (" + pct(PvPClient.instance.cpsScale) + "%)");
        }

        // 3. Ping HUD Box
        if (PvPClient.instance.pvp_pingHud) {
            drawCard(PvPClient.instance.pingX, PvPClient.instance.pingY, scl(70, PvPClient.instance.pingScale), scl(16, PvPClient.instance.pingScale),
                draggingPing, 0xFF10B981, "Ping (" + pct(PvPClient.instance.pingScale) + "%)");
        }

        // 4. Clock HUD Box
        if (PvPClient.instance.pvp_clockHud) {
            drawCard(PvPClient.instance.clockX, PvPClient.instance.clockY, scl(80, PvPClient.instance.clockScale), scl(16, PvPClient.instance.clockScale),
                draggingClock, 0xFFF59E0B, "Clock (" + pct(PvPClient.instance.clockScale) + "%)");
        }

        // 5. Coordinates HUD Box
        if (PvPClient.instance.pvp_infoPills) {
            drawCard(PvPClient.instance.coordsX, PvPClient.instance.coordsY, scl(120, PvPClient.instance.coordsScale), scl(16, PvPClient.instance.coordsScale),
                draggingCoords, 0xFF38BDF8, "Coordinates (" + pct(PvPClient.instance.coordsScale) + "%)");
        }

        // 6. Keystrokes Box
        if (PvPClient.instance.pvp_keystrokesHud) {
            drawCard(PvPClient.instance.keystrokesX, PvPClient.instance.keystrokesY, scl(54, PvPClient.instance.keystrokesScale), scl(66, PvPClient.instance.keystrokesScale),
                draggingKeystrokes, 0xFF8B5CF6, "Keystrokes");
        }

        // 7. Armor HUD Box
        if (PvPClient.instance.pvp_armorStatus) {
            drawCard(PvPClient.instance.armorX, PvPClient.instance.armorY, scl(65, PvPClient.instance.armorScale), scl(88, PvPClient.instance.armorScale),
                draggingArmor, 0xFF06B6D4, "Armor Status");
        }

        // 8. Potion HUD Box
        if (PvPClient.instance.pvp_potionHud) {
            drawCard(PvPClient.instance.potionX, PvPClient.instance.potionY, scl(85, PvPClient.instance.potionScale), scl(45, PvPClient.instance.potionScale),
                draggingPotion, 0xFFEC4899, "Potion Status");
        }

        // 9. Calendar & Clock Widget Box
        if (PvPClient.instance.pvp_calendarHud) {
            drawCard(PvPClient.instance.calendarX, PvPClient.instance.calendarY, scl(168, PvPClient.instance.calendarScale), scl(82, PvPClient.instance.calendarScale),
                draggingCalendar, 0xFF60A5FA, "Clock & Calendar");
        }

        // 10. Cooldowns Widget Box
        if (PvPClient.instance.pvp_cooldownsHud) {
            drawCard(PvPClient.instance.cooldownsX, PvPClient.instance.cooldownsY, scl(100, PvPClient.instance.cooldownsScale), scl(54, PvPClient.instance.cooldownsScale),
                draggingCooldowns, 0xFFEF4444, "Cooldown Trackers");
        }

        // 11. Scoreboard Box
        int sx = width - 100 + PvPClient.instance.scoreboardX;
        int sy = height / 2 - 50 + PvPClient.instance.scoreboardY;
        drawCard(sx, sy, scl(95, PvPClient.instance.scoreboardScale), scl(100, PvPClient.instance.scoreboardScale),
            draggingScoreboard, 0xFFF59E0B, "Scoreboard");

        // Drag updates
        if (draggingFps) { PvPClient.instance.fpsX = mouseX - dragOffsetX; PvPClient.instance.fpsY = mouseY - dragOffsetY; }
        else if (draggingCps) { PvPClient.instance.cpsX = mouseX - dragOffsetX; PvPClient.instance.cpsY = mouseY - dragOffsetY; }
        else if (draggingPing) { PvPClient.instance.pingX = mouseX - dragOffsetX; PvPClient.instance.pingY = mouseY - dragOffsetY; }
        else if (draggingClock) { PvPClient.instance.clockX = mouseX - dragOffsetX; PvPClient.instance.clockY = mouseY - dragOffsetY; }
        else if (draggingCoords) { PvPClient.instance.coordsX = mouseX - dragOffsetX; PvPClient.instance.coordsY = mouseY - dragOffsetY; }
        else if (draggingKeystrokes) { PvPClient.instance.keystrokesX = mouseX - dragOffsetX; PvPClient.instance.keystrokesY = mouseY - dragOffsetY; }
        else if (draggingArmor) { PvPClient.instance.armorX = mouseX - dragOffsetX; PvPClient.instance.armorY = mouseY - dragOffsetY; }
        else if (draggingPotion) { PvPClient.instance.potionX = mouseX - dragOffsetX; PvPClient.instance.potionY = mouseY - dragOffsetY; }
        else if (draggingCalendar) { PvPClient.instance.calendarX = mouseX - dragOffsetX; PvPClient.instance.calendarY = mouseY - dragOffsetY; }
        else if (draggingCooldowns) { PvPClient.instance.cooldownsX = mouseX - dragOffsetX; PvPClient.instance.cooldownsY = mouseY - dragOffsetY; }
        else if (draggingScoreboard) {
            PvPClient.instance.scoreboardX = mouseX - dragOffsetX - (width - 100);
            PvPClient.instance.scoreboardY = mouseY - dragOffsetY - (height / 2 - 50);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawCard(int x, int y, int w, int h, boolean dragging, int accentColor, String label) {
        int bg = dragging ? ((accentColor & 0x00FFFFFF) | 0x99000000) : ((accentColor & 0x00FFFFFF) | 0x44000000);
        RenderGuiUtils.drawGlassCard(x, y, w, h, 4.0F, bg, accentColor);
        fontRendererObj.drawStringWithShadow("\u00a7f" + label, x + 3, y + (h > 18 ? 4 : (h - 8) / 2), 0xFFFFFF);
    }

    private int scl(int base, float scale) { return (int)(base * scale); }
    private int pct(float scale) { return (int)(scale * 100); }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            int mx = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int my = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            float delta = wheel > 0 ? 0.05F : -0.05F;

            if (tryScale(PvPClient.instance.fpsX, PvPClient.instance.fpsY, 65, 16, PvPClient.instance.fpsScale, mx, my)) { PvPClient.instance.fpsScale = clampScale(PvPClient.instance.fpsScale + delta); return; }
            if (tryScale(PvPClient.instance.cpsX, PvPClient.instance.cpsY, 82, 16, PvPClient.instance.cpsScale, mx, my)) { PvPClient.instance.cpsScale = clampScale(PvPClient.instance.cpsScale + delta); return; }
            if (tryScale(PvPClient.instance.pingX, PvPClient.instance.pingY, 70, 16, PvPClient.instance.pingScale, mx, my)) { PvPClient.instance.pingScale = clampScale(PvPClient.instance.pingScale + delta); return; }
            if (tryScale(PvPClient.instance.clockX, PvPClient.instance.clockY, 80, 16, PvPClient.instance.clockScale, mx, my)) { PvPClient.instance.clockScale = clampScale(PvPClient.instance.clockScale + delta); return; }
            if (tryScale(PvPClient.instance.coordsX, PvPClient.instance.coordsY, 120, 16, PvPClient.instance.coordsScale, mx, my)) { PvPClient.instance.coordsScale = clampScale(PvPClient.instance.coordsScale + delta); return; }
            if (tryScale(PvPClient.instance.keystrokesX, PvPClient.instance.keystrokesY, 54, 66, PvPClient.instance.keystrokesScale, mx, my)) { PvPClient.instance.keystrokesScale = clampScale(PvPClient.instance.keystrokesScale + delta); return; }
            if (tryScale(PvPClient.instance.armorX, PvPClient.instance.armorY, 65, 88, PvPClient.instance.armorScale, mx, my)) { PvPClient.instance.armorScale = clampScale(PvPClient.instance.armorScale + delta); return; }
            if (tryScale(PvPClient.instance.potionX, PvPClient.instance.potionY, 85, 45, PvPClient.instance.potionScale, mx, my)) { PvPClient.instance.potionScale = clampScale(PvPClient.instance.potionScale + delta); return; }
            if (tryScale(PvPClient.instance.calendarX, PvPClient.instance.calendarY, 168, 82, PvPClient.instance.calendarScale, mx, my)) { PvPClient.instance.calendarScale = clampScale(PvPClient.instance.calendarScale + delta); return; }
            if (tryScale(PvPClient.instance.cooldownsX, PvPClient.instance.cooldownsY, 100, 54, PvPClient.instance.cooldownsScale, mx, my)) { PvPClient.instance.cooldownsScale = clampScale(PvPClient.instance.cooldownsScale + delta); return; }

            int sx = width - 100 + PvPClient.instance.scoreboardX;
            int sy = height / 2 - 50 + PvPClient.instance.scoreboardY;
            if (isHovered(sx, sy, scl(95, PvPClient.instance.scoreboardScale), scl(100, PvPClient.instance.scoreboardScale), mx, my)) {
                PvPClient.instance.scoreboardScale = clampScale(PvPClient.instance.scoreboardScale + delta);
            }
        }
    }

    private boolean tryScale(int x, int y, int baseW, int baseH, float scale, int mx, int my) {
        return isHovered(x, y, scl(baseW, scale), scl(baseH, scale), mx, my);
    }

    private float clampScale(float v) { return Math.max(0.5F, Math.min(2.5F, v)); }

    @Override
    protected void mouseClicked(int mx, int my, int btn) {
        if (btn == 0) {
            if (tryDrag(PvPClient.instance.fpsX, PvPClient.instance.fpsY, 65, 16, PvPClient.instance.fpsScale, mx, my)) { draggingFps = true; super.mouseClicked(mx, my, btn); return; }
            if (tryDrag(PvPClient.instance.cpsX, PvPClient.instance.cpsY, 82, 16, PvPClient.instance.cpsScale, mx, my)) { draggingCps = true; super.mouseClicked(mx, my, btn); return; }
            if (tryDrag(PvPClient.instance.pingX, PvPClient.instance.pingY, 70, 16, PvPClient.instance.pingScale, mx, my)) { draggingPing = true; super.mouseClicked(mx, my, btn); return; }
            if (tryDrag(PvPClient.instance.clockX, PvPClient.instance.clockY, 80, 16, PvPClient.instance.clockScale, mx, my)) { draggingClock = true; super.mouseClicked(mx, my, btn); return; }
            if (tryDrag(PvPClient.instance.coordsX, PvPClient.instance.coordsY, 120, 16, PvPClient.instance.coordsScale, mx, my)) { draggingCoords = true; super.mouseClicked(mx, my, btn); return; }
            if (tryDrag(PvPClient.instance.keystrokesX, PvPClient.instance.keystrokesY, 54, 66, PvPClient.instance.keystrokesScale, mx, my)) { draggingKeystrokes = true; super.mouseClicked(mx, my, btn); return; }
            if (tryDrag(PvPClient.instance.armorX, PvPClient.instance.armorY, 65, 88, PvPClient.instance.armorScale, mx, my)) { draggingArmor = true; super.mouseClicked(mx, my, btn); return; }
            if (tryDrag(PvPClient.instance.potionX, PvPClient.instance.potionY, 85, 45, PvPClient.instance.potionScale, mx, my)) { draggingPotion = true; super.mouseClicked(mx, my, btn); return; }
            if (tryDrag(PvPClient.instance.calendarX, PvPClient.instance.calendarY, 168, 82, PvPClient.instance.calendarScale, mx, my)) { draggingCalendar = true; super.mouseClicked(mx, my, btn); return; }
            if (tryDrag(PvPClient.instance.cooldownsX, PvPClient.instance.cooldownsY, 100, 54, PvPClient.instance.cooldownsScale, mx, my)) { draggingCooldowns = true; super.mouseClicked(mx, my, btn); return; }

            int sx = width - 100 + PvPClient.instance.scoreboardX;
            int sy = height / 2 - 50 + PvPClient.instance.scoreboardY;
            if (isHovered(sx, sy, scl(95, PvPClient.instance.scoreboardScale), scl(100, PvPClient.instance.scoreboardScale), mx, my)) {
                draggingScoreboard = true; dragOffsetX = mx - sx; dragOffsetY = my - sy; super.mouseClicked(mx, my, btn); return;
            }
        }
        super.mouseClicked(mx, my, btn);
    }

    private boolean tryDrag(int x, int y, int baseW, int baseH, float scale, int mx, int my) {
        if (isHovered(x, y, scl(baseW, scale), scl(baseH, scale), mx, my)) {
            dragOffsetX = mx - x; dragOffsetY = my - y;
            return true;
        }
        return false;
    }

    @Override
    protected void mouseReleased(int mx, int my, int state) {
        draggingFps = false;
        draggingCps = false;
        draggingPing = false;
        draggingClock = false;
        draggingCoords = false;
        draggingKeystrokes = false;
        draggingArmor = false;
        draggingPotion = false;
        draggingCalendar = false;
        draggingCooldowns = false;
        draggingScoreboard = false;
        super.mouseReleased(mx, my, state);
    }

    private boolean isHovered(int x, int y, int w, int h, int mx, int my) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}