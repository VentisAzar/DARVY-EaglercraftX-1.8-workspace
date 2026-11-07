package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.PvPClient;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.MotionBlurRenderer;

public class GuiClickGUI extends GuiScreen {

    // Active Category Tab (0: Combat, 1: HUD Modules, 2: Visuals)
    private int selectedTab = 0;

    // UI Dimensions
    private int guiWidth = 490;
    private int guiHeight = 290;
    private int guiLeft;
    private int guiTop;
    private int sidebarWidth = 110;

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.guiLeft = (this.width - this.guiWidth) / 2;
        this.guiTop = (this.height - this.guiHeight) / 2;

        int bottomY = guiTop + guiHeight - 24;
        int contentX = guiLeft + sidebarWidth + 14;
        
        // Clean Action Buttons at bottom of content panel
        this.buttonList.add(new GuiButton(100, contentX, bottomY, 110, 18, "\u00a7b\u00a7l\u2726 Edit HUD"));
        this.buttonList.add(new GuiButton(101, contentX + 118, bottomY, 100, 18, "\u00a77Reset HUD"));
        this.buttonList.add(new GuiButton(200, guiLeft + guiWidth - 80, bottomY, 68, 18, "\u00a7fClose"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        // Backdrop
        drawRect(0, 0, width, height, 0x60000000);

        // Main Dashboard Box
        RenderGuiUtils.drawBox(guiLeft, guiTop, guiWidth, guiHeight, 0xF20B0E15, 0xFF1E2433);

        // Sidebar Panel
        RenderGuiUtils.drawBox(guiLeft, guiTop, sidebarWidth, guiHeight, 0xFF0E111A, 0xFF181D2A);

        // Brand Badge in Sidebar - DARVY CLIENT
        RenderGuiUtils.drawBox(guiLeft + 10, guiTop + 10, sidebarWidth - 20, 22, 0xFF1E2638, 0xFF2A364F);
        drawCenteredString(fontRendererObj, "\u00a7b\u00a7lDARVY \u00a7fCLIENT", guiLeft + sidebarWidth / 2, guiTop + 17, 0xFFFFFF);

        // Category Tabs (No Engine tab, headers removed)
        String[] tabNames = { "PvP Combat", "HUD Elements", "Visuals" };
        String[] tabCounts = { "4 MODS", "9 MODS", "3 MODS" };

        for (int i = 0; i < tabNames.length; i++) {
            int tabY = guiTop + 42 + (i * 34);
            boolean hovered = mouseX >= guiLeft + 8 && mouseX <= guiLeft + sidebarWidth - 8 && mouseY >= tabY && mouseY <= tabY + 28;
            boolean selected = selectedTab == i;

            int bgCol = selected ? 0xFF1C2538 : (hovered ? 0x401C2538 : 0x00000000);
            int borderCol = selected ? 0xFF2B3A54 : (hovered ? 0xFF1E283A : 0x00000000);
            RenderGuiUtils.drawBox(guiLeft + 8, tabY, sidebarWidth - 16, 28, bgCol, borderCol);

            if (selected) {
                RenderGuiUtils.drawBox(guiLeft + 8, tabY + 2, 3, 24, 0xFF3B82F6, 0);
            }

            drawString(fontRendererObj, (selected ? "\u00a7b\u00a7l" : "\u00a77") + tabNames[i], guiLeft + 16, tabY + 5, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a78" + tabCounts[i], guiLeft + 16, tabY + 16, 0x888888);
        }

        // Render Active Tab Content directly (headers removed as requested)
        renderTabContent(mouseX, mouseY);

        // Performance Metrics Footer (without culled counter)
        int contentLeft = guiLeft + sidebarWidth + 14;
        int metricsY = guiTop + guiHeight - 38;
        String metricsText = "\u00a78FPS: \u00a7a" + Minecraft.getDebugFPS() + " \u00a78| Ping: \u00a7b" + PvPClient.instance.getPing() + "ms";
        drawString(fontRendererObj, metricsText, contentLeft, metricsY, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.enableDepth();
    }

    private void renderTabContent(int mouseX, int mouseY) {
        int contentLeft = guiLeft + sidebarWidth + 14;
        int contentTop = guiTop + 14;
        int cardW = 168;

        if (selectedTab == 0) { // COMBAT TAB
            renderModuleCard(contentLeft, contentTop, cardW, 36, "Toggle Sprint", "Hold sprint automatically", PvPClient.instance.pvp_toggleSprint, mouseX, mouseY);
            renderModuleCard(contentLeft + cardW + 8, contentTop, cardW, 36, "1.7 Animations", "1.7 Blockhit & swing anim", PvPClient.instance.pvp_animations17, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 42, cardW, 36, "No Hurt Cam", "Removes camera shake on hit", PvPClient.instance.pvp_noHurtCam, mouseX, mouseY);
            renderModuleCard(contentLeft + cardW + 8, contentTop + 42, cardW, 36, "Low Fire Overlay", "Lowers fire view blockage", PvPClient.instance.pvp_lowFire, mouseX, mouseY);

        } else if (selectedTab == 1) { // HUD ELEMENTS TAB (9 modules, clean rectangular grid)
            renderModuleCard(contentLeft, contentTop, cardW, 30, "FPS Counter", "Renders current framerate", PvPClient.instance.pvp_fpsHud, mouseX, mouseY);
            renderModuleCard(contentLeft + cardW + 8, contentTop, cardW, 30, "CPS Counter", "Left & right click speed", PvPClient.instance.pvp_cpsHud, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 34, cardW, 30, "Ping Display", "Real-time network latency", PvPClient.instance.pvp_pingHud, mouseX, mouseY);
            renderModuleCard(contentLeft + cardW + 8, contentTop + 34, cardW, 30, "Keystrokes HUD", "WASD & mouse click status", PvPClient.instance.pvp_keystrokesHud, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 68, cardW, 30, "Armor Durability", "Equipped armor status HUD", PvPClient.instance.pvp_armorStatus, mouseX, mouseY);
            renderModuleCard(contentLeft + cardW + 8, contentTop + 68, cardW, 30, "Potion Status", "Active potion buff timers", PvPClient.instance.pvp_potionHud, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 102, cardW, 30, "Digital Clock", "12-hour AM/PM time", PvPClient.instance.pvp_clockHud, mouseX, mouseY);
            renderModuleCard(contentLeft + cardW + 8, contentTop + 102, cardW, 30, "Coordinates", "XYZ position & facing", PvPClient.instance.pvp_infoPills, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 136, cardW, 30, "Compass HUD", "Cardinal direction & degree", PvPClient.instance.pvp_compassHud, mouseX, mouseY);

        } else if (selectedTab == 2) { // VISUALS TAB
            int fullW = cardW * 2 + 8;
            renderModuleCard(contentLeft, contentTop, fullW, 32, "Fullbright", "Maximum brightness in dark caves & night", PvPClient.instance.pvp_fullbright, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 38, fullW, 32, "White Block Highlight", "Sleek bright block selection highlight", PvPClient.instance.pvp_blockOutline, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 76, fullW, 32, "Motion Blur", "Monkey blur trail effect \u00a78[\u00a7bStrength: " + PvPClient.instance.motionBlurStrength + "\u00a78] \u00a78(Right-click)", PvPClient.instance.pvp_motionBlur, mouseX, mouseY);
        }
    }

    private void renderModuleCard(int x, int y, int width, int height, String title, String subtitle, boolean enabled, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        
        int cardBg = enabled ? (hovered ? 0xFF192336 : 0xFF141C2B) : (hovered ? 0xFF171B26 : 0xFF12151E);
        int borderCol = enabled ? (hovered ? 0xFF3B82F6 : 0xFF253754) : (hovered ? 0xFF2B3245 : 0xFF1C212E);

        RenderGuiUtils.drawBox(x, y, width, height, cardBg, borderCol);

        if (enabled) {
            RenderGuiUtils.drawBox(x, y, 3, height, 0xFF3B82F6, 0);
        }

        drawString(fontRendererObj, (enabled ? "\u00a7f\u00a7l" : "\u00a77") + title, x + 8, y + 4, 0xFFFFFF);
        if (height > 26) {
            drawString(fontRendererObj, "\u00a78" + subtitle, x + 8, y + 16, 0x888888);
        }

        // Clean rectangular toggle box
        int switchW = 32;
        int switchH = 14;
        int switchX = x + width - switchW - 6;
        int switchY = y + (height - switchH) / 2;

        if (enabled) {
            RenderGuiUtils.drawBox(switchX, switchY, switchW, switchH, 0xFF10B981, 0xFF059669);
            drawCenteredString(fontRendererObj, "\u00a7fON", switchX + switchW / 2, switchY + 3, 0xFFFFFF);
        } else {
            RenderGuiUtils.drawBox(switchX, switchY, switchW, switchH, 0xFF222838, 0xFF2D354A);
            drawCenteredString(fontRendererObj, "\u00a78OFF", switchX + switchW / 2, switchY + 3, 0x888888);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // Sidebar Navigation Clicks (Left click only)
        if (mouseButton == 0) {
            for (int i = 0; i < 3; i++) {
                int tabY = guiTop + 42 + (i * 34);
                if (mouseX >= guiLeft + 8 && mouseX <= guiLeft + sidebarWidth - 8 && mouseY >= tabY && mouseY <= tabY + 28) {
                    selectedTab = i;
                    playClickSound();
                    return;
                }
            }
        }

        int contentLeft = guiLeft + sidebarWidth + 14;
        int contentTop = guiTop + 14;
        int cardW = 168;

        if (selectedTab == 0) { // COMBAT TAB
            if (mouseButton == 0) {
                if (isHovered(contentLeft, contentTop, cardW, 36, mouseX, mouseY)) { PvPClient.instance.toggleSprint(); playClickSound(); }
                else if (isHovered(contentLeft + cardW + 8, contentTop, cardW, 36, mouseX, mouseY)) { PvPClient.instance.toggleAnimations17(); playClickSound(); }
                else if (isHovered(contentLeft, contentTop + 42, cardW, 36, mouseX, mouseY)) { PvPClient.instance.toggleNoHurtCam(); playClickSound(); }
                else if (isHovered(contentLeft + cardW + 8, contentTop + 42, cardW, 36, mouseX, mouseY)) { PvPClient.instance.toggleLowFire(); playClickSound(); }
            }

        } else if (selectedTab == 1) { // HUD ELEMENTS TAB
            if (mouseButton == 0) {
                if (isHovered(contentLeft, contentTop, cardW, 30, mouseX, mouseY)) { PvPClient.instance.pvp_fpsHud = !PvPClient.instance.pvp_fpsHud; playClickSound(); }
                else if (isHovered(contentLeft + cardW + 8, contentTop, cardW, 30, mouseX, mouseY)) { PvPClient.instance.pvp_cpsHud = !PvPClient.instance.pvp_cpsHud; playClickSound(); }
                else if (isHovered(contentLeft, contentTop + 34, cardW, 30, mouseX, mouseY)) { PvPClient.instance.pvp_pingHud = !PvPClient.instance.pvp_pingHud; playClickSound(); }
                else if (isHovered(contentLeft + cardW + 8, contentTop + 34, cardW, 30, mouseX, mouseY)) { PvPClient.instance.pvp_keystrokesHud = !PvPClient.instance.pvp_keystrokesHud; playClickSound(); }
                else if (isHovered(contentLeft, contentTop + 68, cardW, 30, mouseX, mouseY)) { PvPClient.instance.pvp_armorStatus = !PvPClient.instance.pvp_armorStatus; playClickSound(); }
                else if (isHovered(contentLeft + cardW + 8, contentTop + 68, cardW, 30, mouseX, mouseY)) { PvPClient.instance.pvp_potionHud = !PvPClient.instance.pvp_potionHud; playClickSound(); }
                else if (isHovered(contentLeft, contentTop + 102, cardW, 30, mouseX, mouseY)) { PvPClient.instance.pvp_clockHud = !PvPClient.instance.pvp_clockHud; playClickSound(); }
                else if (isHovered(contentLeft + cardW + 8, contentTop + 102, cardW, 30, mouseX, mouseY)) { PvPClient.instance.pvp_infoPills = !PvPClient.instance.pvp_infoPills; playClickSound(); }
                else if (isHovered(contentLeft, contentTop + 136, cardW, 30, mouseX, mouseY)) { PvPClient.instance.pvp_compassHud = !PvPClient.instance.pvp_compassHud; playClickSound(); }
            }

        } else if (selectedTab == 2) { // VISUALS TAB
            int fullW = cardW * 2 + 8;
            if (mouseButton == 0) {
                if (isHovered(contentLeft, contentTop, fullW, 32, mouseX, mouseY)) { PvPClient.instance.toggleFullbright(); playClickSound(); }
                else if (isHovered(contentLeft, contentTop + 38, fullW, 32, mouseX, mouseY)) { PvPClient.instance.pvp_blockOutline = !PvPClient.instance.pvp_blockOutline; playClickSound(); }
                else if (isHovered(contentLeft, contentTop + 76, fullW, 32, mouseX, mouseY)) {
                    PvPClient.instance.toggleMotionBlur();
                    if (!PvPClient.instance.pvp_motionBlur) {
                        MotionBlurRenderer.reset();
                    }
                    playClickSound();
                }
            } else if (mouseButton == 1) { // Right-click opens Motion Blur Settings
                if (isHovered(contentLeft, contentTop + 76, fullW, 32, mouseX, mouseY)) {
                    playClickSound();
                    mc.displayGuiScreen(new GuiMotionBlurSettings(this));
                }
            }
        }
    }

    private boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void playClickSound() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 100) {
            mc.displayGuiScreen(new GuiHudEditor(this));
        } else if (button.id == 101) {
            PvPClient.instance.resetDefaultPositions();
            playClickSound();
        } else if (button.id == 200) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}