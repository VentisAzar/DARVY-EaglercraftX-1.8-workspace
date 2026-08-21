package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.PvPClient;
import net.minecraft.client.renderer.SodiumOptimizer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class GuiClickGUI extends GuiScreen {

    // Active Category Tab (0: Combat, 1: HUD Modules, 2: Visuals, 3: Engine)
    private int selectedTab = 0;

    // UI Dimensions
    private int guiWidth = 470;
    private int guiHeight = 275;
    private int guiLeft;
    private int guiTop;
    private int sidebarWidth = 110;

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.guiLeft = (this.width - this.guiWidth) / 2;
        this.guiTop = (this.height - this.guiHeight) / 2;

        int bottomY = guiTop + guiHeight - 26;
        int contentX = guiLeft + sidebarWidth + 14;
        
        // Sleek Action Buttons at bottom of content panel
        this.buttonList.add(new GuiButton(100, contentX, bottomY, 110, 18, "\u00a7b\u00a7l\u2726 Edit HUD"));
        this.buttonList.add(new GuiButton(101, contentX + 118, bottomY, 100, 18, "\u00a77Reset HUD"));
        this.buttonList.add(new GuiButton(200, guiLeft + guiWidth - 80, bottomY, 68, 18, "\u00a7fClose"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        // Apple style dark translucent blur backdrop
        drawRect(0, 0, width, height, 0x60000000);

        // Main Dashboard Canvas (Deep frosted glass slate)
        drawRect(guiLeft, guiTop, guiLeft + guiWidth, guiTop + guiHeight, 0xF20B0E15);
        
        // Sleek outer border
        drawOutline(guiLeft, guiTop, guiLeft + guiWidth, guiTop + guiHeight, 0xFF1E2433);

        // Sidebar Panel
        drawRect(guiLeft, guiTop, guiLeft + sidebarWidth, guiTop + guiHeight, 0xFF0E111A);
        drawVerticalLine(guiLeft + sidebarWidth, guiTop, guiTop + guiHeight, 0xFF181D2A);

        // Brand Badge in Sidebar
        drawRect(guiLeft + 10, guiTop + 10, guiLeft + sidebarWidth - 10, guiTop + 32, 0xFF1E2638);
        drawOutline(guiLeft + 10, guiTop + 10, guiLeft + sidebarWidth - 10, guiTop + 32, 0xFF2A364F);
        drawCenteredString(fontRendererObj, "\u00a7b\u00a7lDARVY \u00a7fSTUDIO", guiLeft + sidebarWidth / 2, guiTop + 17, 0xFFFFFF);

        // Category Tab Definitions
        String[] tabNames = { "PvP Combat", "HUD Elements", "Visuals", "Engine & FPS" };
        String[] tabCounts = { "4 MODS", "6 MODS", "4 MODS", "4 OPTS" };

        for (int i = 0; i < tabNames.length; i++) {
            int tabY = guiTop + 44 + (i * 36);
            boolean hovered = mouseX >= guiLeft + 8 && mouseX <= guiLeft + sidebarWidth - 8 && mouseY >= tabY && mouseY <= tabY + 30;
            boolean selected = selectedTab == i;

            int bgCol = selected ? 0xFF1C2538 : (hovered ? 0x401C2538 : 0x00000000);
            drawRect(guiLeft + 8, tabY, guiLeft + sidebarWidth - 8, tabY + 30, bgCol);

            if (selected) {
                // Active indicator pill on left edge
                drawRect(guiLeft + 8, tabY + 2, guiLeft + 11, tabY + 28, 0xFF3B82F6);
                drawOutline(guiLeft + 8, tabY, guiLeft + sidebarWidth - 8, tabY + 30, 0xFF2B3A54);
            } else if (hovered) {
                drawOutline(guiLeft + 8, tabY, guiLeft + sidebarWidth - 8, tabY + 30, 0xFF1E283A);
            }

            drawString(fontRendererObj, (selected ? "\u00a7b\u00a7l" : "\u00a77") + tabNames[i], guiLeft + 16, tabY + 6, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a78" + tabCounts[i], guiLeft + 16, tabY + 18, 0x888888);
        }

        // Header Divider & Title
        int contentLeft = guiLeft + sidebarWidth + 14;
        int contentTop = guiTop + 12;
        String tabTitle = selectedTab == 0 ? "PvP Combat Mechanics" : (selectedTab == 1 ? "Heads-Up Display Modules" : (selectedTab == 2 ? "Visuals & Typography" : "Engine & Performance"));
        drawString(fontRendererObj, "\u00a7f\u00a7l" + tabTitle, contentLeft, contentTop, 0xFFFFFF);
        drawString(fontRendererObj, "\u00a78Custom Client Modules", guiLeft + guiWidth - 14 - fontRendererObj.getStringWidth("Custom Client Modules"), contentTop, 0xFFFFFF);
        drawHorizontalLine(contentLeft, guiLeft + guiWidth - 14, contentTop + 14, 0xFF181D2A);

        // Render Active Tab Content
        renderTabContent(mouseX, mouseY);

        // Performance Metrics Footer
        int metricsY = guiTop + guiHeight - 40;
        String metricsText = "\u00a78FPS: \u00a7a" + Minecraft.getDebugFPS() + " \u00a78| Ping: \u00a7b" + PvPClient.instance.getPing() + "ms \u00a78| Culled: \u00a7e" + SodiumOptimizer.instance.culledEntitiesThisFrame;
        drawString(fontRendererObj, metricsText, contentLeft, metricsY, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.enableDepth();
    }

    private void renderTabContent(int mouseX, int mouseY) {
        int contentLeft = guiLeft + sidebarWidth + 14;
        int contentTop = guiTop + 32;

        if (selectedTab == 0) { // COMBAT TAB
            renderModuleCard(contentLeft, contentTop, 162, 36, "Toggle Sprint", "Hold sprint automatically", PvPClient.instance.pvp_toggleSprint, mouseX, mouseY);
            renderModuleCard(contentLeft + 172, contentTop, 162, 36, "1.7 Animations", "1.7 Blockhit & swing anim", PvPClient.instance.pvp_animations17, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 42, 162, 36, "No Hurt Cam", "Removes camera shake on hit", PvPClient.instance.pvp_noHurtCam, mouseX, mouseY);
            renderModuleCard(contentLeft + 172, contentTop + 42, 162, 36, "Low Fire Overlay", "Lowers fire view blockage", PvPClient.instance.pvp_lowFire, mouseX, mouseY);

        } else if (selectedTab == 1) { // HUD ELEMENTS TAB
            // Separate FPS and CPS toggles
            renderModuleCard(contentLeft, contentTop, 162, 36, "FPS Counter", "Renders current framerate", PvPClient.instance.pvp_fpsHud, mouseX, mouseY);
            renderModuleCard(contentLeft + 172, contentTop, 162, 36, "CPS Counter", "Left & right click speed", PvPClient.instance.pvp_cpsHud, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 42, 162, 36, "Ping Display", "Real-time network latency", PvPClient.instance.pvp_pingHud, mouseX, mouseY);
            renderModuleCard(contentLeft + 172, contentTop + 42, 162, 36, "Keystrokes HUD", "WASD & mouse click status", PvPClient.instance.pvp_keystrokesHud, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 84, 162, 36, "Armor Durability", "Equipped armor status HUD", PvPClient.instance.pvp_armorStatus, mouseX, mouseY);
            renderModuleCard(contentLeft + 172, contentTop + 84, 162, 36, "Potion Status", "Active potion buff timers", PvPClient.instance.pvp_potionHud, mouseX, mouseY);

        } else if (selectedTab == 2) { // VISUALS & FONTS TAB
            renderModuleCard(contentLeft, contentTop, 334, 32, "Fullbright", "Maximum brightness in dark caves & night", PvPClient.instance.pvp_fullbright, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 38, 334, 32, "Custom Crosshair", "Clean PvP crosshair reticle", PvPClient.instance.pvp_customCrosshair, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 76, 334, 32, "White Block Highlight", "Sleek bright block selection highlight", PvPClient.instance.pvp_blockOutline, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 114, 334, 32, "Samsung HD Sans Font", "Smooth high-definition Sans typography", PvPClient.instance.pvp_customFont, mouseX, mouseY);

        } else if (selectedTab == 3) { // ENGINE & PERFORMANCE TAB
            renderModuleCard(contentLeft, contentTop, 334, 32, "Chunk Batching", "Smooth chunk updates distribution across frames", SodiumOptimizer.instance.sodiumChunkRebuilding, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 38, 334, 32, "Entity Frustum Culling", "Skip rendering entities outside field of view", SodiumOptimizer.instance.entityFrustumCulling, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 76, 334, 32, "Fast Math Lookup", "Accelerated sin/cos trigonometric cache tables", SodiumOptimizer.instance.fastMath, mouseX, mouseY);
            
            // Rebuild Budget Card
            int budgetY = contentTop + 114;
            drawCardBackground(contentLeft, budgetY, 334, 32);
            drawString(fontRendererObj, "\u00a7f\u00a7lChunk Rebuild Budget", contentLeft + 10, budgetY + 6, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a78Maximum chunk updates limit per frame", contentLeft + 10, budgetY + 18, 0x888888);
            
            String budgetStr = "\u00a7e" + SodiumOptimizer.instance.getRebuildBudgetName();
            int btnW = fontRendererObj.getStringWidth(budgetStr) + 14;
            int btnX = contentLeft + 334 - btnW - 8;
            drawRect(btnX, budgetY + 6, btnX + btnW, budgetY + 26, 0xFF242C3F);
            drawOutline(btnX, budgetY + 6, btnX + btnW, budgetY + 26, 0xFF35415C);
            drawString(fontRendererObj, budgetStr, btnX + 7, budgetY + 11, 0xFFFFFF);
        }
    }

    private void drawCardBackground(int x, int y, int w, int h) {
        drawRect(x, y, x + w, y + h, 0xFF131620);
        drawOutline(x, y, x + w, y + h, 0xFF1C2230);
    }

    private void renderModuleCard(int x, int y, int width, int height, String title, String subtitle, boolean enabled, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        
        int cardBg = enabled ? (hovered ? 0xFF192336 : 0xFF141C2B) : (hovered ? 0xFF171B26 : 0xFF12151E);
        int borderCol = enabled ? (hovered ? 0xFF3B82F6 : 0xFF253754) : (hovered ? 0xFF2B3245 : 0xFF1C212E);

        drawRect(x, y, x + width, y + height, cardBg);
        drawOutline(x, y, x + width, y + height, borderCol);

        if (enabled) {
            drawRect(x, y, x + 3, y + height, 0xFF3B82F6);
        }

        drawString(fontRendererObj, (enabled ? "\u00a7f\u00a7l" : "\u00a77") + title, x + 8, y + 6, 0xFFFFFF);
        drawString(fontRendererObj, "\u00a78" + subtitle, x + 8, y + 19, 0x888888);

        // Apple-style Pill Toggle Switch
        int switchW = 34;
        int switchH = 16;
        int switchX = x + width - switchW - 8;
        int switchY = y + (height - switchH) / 2;

        if (enabled) {
            drawRect(switchX, switchY, switchX + switchW, switchY + switchH, 0xFF10B981);
            drawOutline(switchX, switchY, switchX + switchW, switchY + switchH, 0xFF059669);
            // Pill circle on right
            drawRect(switchX + switchW - 14, switchY + 2, switchX + switchW - 2, switchY + switchH - 2, 0xFFFFFFFF);
        } else {
            drawRect(switchX, switchY, switchX + switchW, switchY + switchH, 0xFF222838);
            drawOutline(switchX, switchY, switchX + switchW, switchY + switchH, 0xFF2D354A);
            // Pill circle on left
            drawRect(switchX + 2, switchY + 2, switchX + 14, switchY + switchH - 2, 0xFF6B7280);
        }
    }

    private void drawOutline(int left, int top, int right, int bottom, int color) {
        drawHorizontalLine(left, right, top, color);
        drawHorizontalLine(left, right, bottom, color);
        drawVerticalLine(left, top, bottom, color);
        drawVerticalLine(right, top, bottom, color);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton != 0) return;

        // Sidebar Navigation Clicks
        for (int i = 0; i < 4; i++) {
            int tabY = guiTop + 44 + (i * 36);
            if (mouseX >= guiLeft + 8 && mouseX <= guiLeft + sidebarWidth - 8 && mouseY >= tabY && mouseY <= tabY + 30) {
                selectedTab = i;
                playClickSound();
                return;
            }
        }

        int contentLeft = guiLeft + sidebarWidth + 14;
        int contentTop = guiTop + 32;

        if (selectedTab == 0) { // COMBAT TAB
            if (isHovered(contentLeft, contentTop, 162, 36, mouseX, mouseY)) { PvPClient.instance.toggleSprint(); playClickSound(); }
            else if (isHovered(contentLeft + 172, contentTop, 162, 36, mouseX, mouseY)) { PvPClient.instance.toggleAnimations17(); playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 42, 162, 36, mouseX, mouseY)) { PvPClient.instance.toggleNoHurtCam(); playClickSound(); }
            else if (isHovered(contentLeft + 172, contentTop + 42, 162, 36, mouseX, mouseY)) { PvPClient.instance.toggleLowFire(); playClickSound(); }

        } else if (selectedTab == 1) { // HUD ELEMENTS TAB
            if (isHovered(contentLeft, contentTop, 162, 36, mouseX, mouseY)) { PvPClient.instance.pvp_fpsHud = !PvPClient.instance.pvp_fpsHud; playClickSound(); }
            else if (isHovered(contentLeft + 172, contentTop, 162, 36, mouseX, mouseY)) { PvPClient.instance.pvp_cpsHud = !PvPClient.instance.pvp_cpsHud; playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 42, 162, 36, mouseX, mouseY)) { PvPClient.instance.pvp_pingHud = !PvPClient.instance.pvp_pingHud; playClickSound(); }
            else if (isHovered(contentLeft + 172, contentTop + 42, 162, 36, mouseX, mouseY)) { PvPClient.instance.pvp_keystrokesHud = !PvPClient.instance.pvp_keystrokesHud; playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 84, 162, 36, mouseX, mouseY)) { PvPClient.instance.pvp_armorStatus = !PvPClient.instance.pvp_armorStatus; playClickSound(); }
            else if (isHovered(contentLeft + 172, contentTop + 84, 162, 36, mouseX, mouseY)) { PvPClient.instance.pvp_potionHud = !PvPClient.instance.pvp_potionHud; playClickSound(); }

        } else if (selectedTab == 2) { // VISUALS & FONTS TAB
            if (isHovered(contentLeft, contentTop, 334, 32, mouseX, mouseY)) { PvPClient.instance.toggleFullbright(); playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 38, 334, 32, mouseX, mouseY)) { PvPClient.instance.pvp_customCrosshair = !PvPClient.instance.pvp_customCrosshair; playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 76, 334, 32, mouseX, mouseY)) { PvPClient.instance.pvp_blockOutline = !PvPClient.instance.pvp_blockOutline; playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 114, 334, 32, mouseX, mouseY)) { PvPClient.instance.toggleCustomFont(); playClickSound(); }

        } else if (selectedTab == 3) { // ENGINE & PERFORMANCE TAB
            if (isHovered(contentLeft, contentTop, 334, 32, mouseX, mouseY)) {
                SodiumOptimizer.instance.sodiumChunkRebuilding = !SodiumOptimizer.instance.sodiumChunkRebuilding;
                playClickSound();
            } else if (isHovered(contentLeft, contentTop + 38, 334, 32, mouseX, mouseY)) {
                SodiumOptimizer.instance.entityFrustumCulling = !SodiumOptimizer.instance.entityFrustumCulling;
                playClickSound();
            } else if (isHovered(contentLeft, contentTop + 76, 334, 32, mouseX, mouseY)) {
                SodiumOptimizer.instance.fastMath = !SodiumOptimizer.instance.fastMath;
                playClickSound();
            } else if (isHovered(contentLeft, contentTop + 114, 334, 32, mouseX, mouseY)) {
                SodiumOptimizer.instance.cycleRebuildBudget();
                playClickSound();
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