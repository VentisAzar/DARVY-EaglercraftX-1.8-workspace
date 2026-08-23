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
    private int guiWidth = 490;
    private int guiHeight = 310;
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

        // Backdrop
        drawRect(0, 0, width, height, 0x60000000);

        // Main Dashboard Canvas using glass card
        RenderGuiUtils.drawGlassCard(guiLeft, guiTop, guiWidth, guiHeight, 6.0F, 0xF20B0E15, 0xFF1E2433);

        // Sidebar Panel
        RenderGuiUtils.drawRoundedRect(guiLeft, guiTop, sidebarWidth, guiHeight, 6.0F, 0xFF0E111A);
        RenderGuiUtils.drawLine(guiLeft + sidebarWidth, guiTop + 4, guiLeft + sidebarWidth, guiTop + guiHeight - 4, 1.0F, 0xFF181D2A);

        // Brand Badge in Sidebar
        RenderGuiUtils.drawGlassCard(guiLeft + 10, guiTop + 10, sidebarWidth - 20, 22, 4.0F, 0xFF1E2638, 0xFF2A364F);
        drawCenteredString(fontRendererObj, "\u00a7b\u00a7lDARVY \u00a7fSTUDIO", guiLeft + sidebarWidth / 2, guiTop + 17, 0xFFFFFF);

        // Category Tab Definitions
        String[] tabNames = { "PvP Combat", "HUD Elements", "Visuals", "Engine & FPS" };
        String[] tabCounts = { "4 MODS", "10 MODS", "2 MODS", "4 OPTS" };

        for (int i = 0; i < tabNames.length; i++) {
            int tabY = guiTop + 44 + (i * 36);
            boolean hovered = mouseX >= guiLeft + 8 && mouseX <= guiLeft + sidebarWidth - 8 && mouseY >= tabY && mouseY <= tabY + 30;
            boolean selected = selectedTab == i;

            int bgCol = selected ? 0xFF1C2538 : (hovered ? 0x401C2538 : 0x00000000);
            RenderGuiUtils.drawRoundedRect(guiLeft + 8, tabY, sidebarWidth - 16, 30, 4.0F, bgCol);

            if (selected) {
                // Active indicator pill on left edge
                RenderGuiUtils.drawRoundedRect(guiLeft + 8, tabY + 2, 3, 26, 1.5F, 0xFF3B82F6);
                RenderGuiUtils.drawRoundedOutline(guiLeft + 8, tabY, sidebarWidth - 16, 30, 4.0F, 1.0F, 0xFF2B3A54);
            } else if (hovered) {
                RenderGuiUtils.drawRoundedOutline(guiLeft + 8, tabY, sidebarWidth - 16, 30, 4.0F, 1.0F, 0xFF1E283A);
            }

            drawString(fontRendererObj, (selected ? "\u00a7b\u00a7l" : "\u00a77") + tabNames[i], guiLeft + 16, tabY + 6, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a78" + tabCounts[i], guiLeft + 16, tabY + 18, 0x888888);
        }

        // Header Divider & Title
        int contentLeft = guiLeft + sidebarWidth + 14;
        int contentTop = guiTop + 12;
        String tabTitle = selectedTab == 0 ? "PvP Combat Mechanics" : (selectedTab == 1 ? "Heads-Up Display Modules" : (selectedTab == 2 ? "Visuals & Appearance" : "Engine & Performance"));
        drawString(fontRendererObj, "\u00a7f\u00a7l" + tabTitle, contentLeft, contentTop, 0xFFFFFF);
        drawString(fontRendererObj, "\u00a78Custom Client Modules", guiLeft + guiWidth - 14 - fontRendererObj.getStringWidth("Custom Client Modules"), contentTop, 0xFFFFFF);
        RenderGuiUtils.drawLine(contentLeft, contentTop + 13, guiLeft + guiWidth - 14, contentTop + 13, 1.0F, 0xFF181D2A);

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
        int cardW = 168;

        if (selectedTab == 0) { // COMBAT TAB
            renderModuleCard(contentLeft, contentTop, cardW, 36, "Toggle Sprint", "Hold sprint automatically", PvPClient.instance.pvp_toggleSprint, mouseX, mouseY);
            renderModuleCard(contentLeft + cardW + 8, contentTop, cardW, 36, "1.7 Animations", "1.7 Blockhit & swing anim", PvPClient.instance.pvp_animations17, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 42, cardW, 36, "No Hurt Cam", "Removes camera shake on hit", PvPClient.instance.pvp_noHurtCam, mouseX, mouseY);
            renderModuleCard(contentLeft + cardW + 8, contentTop + 42, cardW, 36, "Low Fire Overlay", "Lowers fire view blockage", PvPClient.instance.pvp_lowFire, mouseX, mouseY);

        } else if (selectedTab == 1) { // HUD ELEMENTS TAB (10 modules, 5 rows x 2 columns)
            renderModuleCard(contentLeft, contentTop, cardW, 32, "FPS Counter", "Renders current framerate", PvPClient.instance.pvp_fpsHud, mouseX, mouseY);
            renderModuleCard(contentLeft + cardW + 8, contentTop, cardW, 32, "CPS Counter", "Left & right click speed", PvPClient.instance.pvp_cpsHud, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 36, cardW, 32, "Ping Display", "Real-time network latency", PvPClient.instance.pvp_pingHud, mouseX, mouseY);
            renderModuleCard(contentLeft + cardW + 8, contentTop + 36, cardW, 32, "Keystrokes HUD", "WASD & mouse click status", PvPClient.instance.pvp_keystrokesHud, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 72, cardW, 32, "Armor Durability", "Equipped armor status HUD", PvPClient.instance.pvp_armorStatus, mouseX, mouseY);
            renderModuleCard(contentLeft + cardW + 8, contentTop + 72, cardW, 32, "Potion Status", "Active potion buff timers", PvPClient.instance.pvp_potionHud, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 108, cardW, 32, "Digital Clock", "Displays current time", PvPClient.instance.pvp_clockHud, mouseX, mouseY);
            renderModuleCard(contentLeft + cardW + 8, contentTop + 108, cardW, 32, "Coordinates", "XYZ position & facing", PvPClient.instance.pvp_infoPills, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 144, cardW, 32, "Clock & Calendar", "Analog clock with calendar", PvPClient.instance.pvp_calendarHud, mouseX, mouseY);
            renderModuleCard(contentLeft + cardW + 8, contentTop + 144, cardW, 32, "Cooldown Trackers", "Pearl & Gapple timers", PvPClient.instance.pvp_cooldownsHud, mouseX, mouseY);

        } else if (selectedTab == 2) { // VISUALS TAB (no more crosshair or custom font)
            int fullW = cardW * 2 + 8;
            renderModuleCard(contentLeft, contentTop, fullW, 32, "Fullbright", "Maximum brightness in dark caves & night", PvPClient.instance.pvp_fullbright, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 38, fullW, 32, "White Block Highlight", "Sleek bright block selection highlight", PvPClient.instance.pvp_blockOutline, mouseX, mouseY);

        } else if (selectedTab == 3) { // ENGINE & PERFORMANCE TAB
            int fullW = cardW * 2 + 8;
            renderModuleCard(contentLeft, contentTop, fullW, 32, "Chunk Batching", "Smooth chunk updates per frame", SodiumOptimizer.instance.sodiumChunkRebuilding, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 38, fullW, 32, "Entity Frustum Culling", "Skip entities outside FOV", SodiumOptimizer.instance.entityFrustumCulling, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 76, fullW, 32, "Fast Math Lookup", "Accelerated trig cache tables", SodiumOptimizer.instance.fastMath, mouseX, mouseY);
            
            // Rebuild Budget Card
            int budgetY = contentTop + 114;
            RenderGuiUtils.drawGlassCard(contentLeft, budgetY, fullW, 32, 4.0F, 0xFF131620, 0xFF1C2230);
            drawString(fontRendererObj, "\u00a7f\u00a7lChunk Rebuild Budget", contentLeft + 10, budgetY + 6, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a78Maximum chunk updates limit per frame", contentLeft + 10, budgetY + 18, 0x888888);
            
            String budgetStr = "\u00a7e" + SodiumOptimizer.instance.getRebuildBudgetName();
            int btnW = fontRendererObj.getStringWidth(budgetStr) + 14;
            int btnX = contentLeft + fullW - btnW - 8;
            RenderGuiUtils.drawGlassCard(btnX, budgetY + 6, btnW, 20, 3.0F, 0xFF242C3F, 0xFF35415C);
            drawString(fontRendererObj, budgetStr, btnX + 7, budgetY + 11, 0xFFFFFF);
        }
    }

    private void renderModuleCard(int x, int y, int width, int height, String title, String subtitle, boolean enabled, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        
        int cardBg = enabled ? (hovered ? 0xFF192336 : 0xFF141C2B) : (hovered ? 0xFF171B26 : 0xFF12151E);
        int borderCol = enabled ? (hovered ? 0xFF3B82F6 : 0xFF253754) : (hovered ? 0xFF2B3245 : 0xFF1C212E);

        RenderGuiUtils.drawGlassCard(x, y, width, height, 4.0F, cardBg, borderCol);

        if (enabled) {
            RenderGuiUtils.drawRoundedRect(x, y, 3, height, 1.5F, 0xFF3B82F6);
        }

        drawString(fontRendererObj, (enabled ? "\u00a7f\u00a7l" : "\u00a77") + title, x + 8, y + 5, 0xFFFFFF);
        if (height > 24) {
            drawString(fontRendererObj, "\u00a78" + subtitle, x + 8, y + 17, 0x888888);
        }

        // Apple-style Pill Toggle Switch
        int switchW = 30;
        int switchH = 14;
        int switchX = x + width - switchW - 6;
        int switchY = y + (height - switchH) / 2;

        if (enabled) {
            RenderGuiUtils.drawRoundedRect(switchX, switchY, switchW, switchH, switchH / 2.0F, 0xFF10B981);
            RenderGuiUtils.drawRoundedOutline(switchX, switchY, switchW, switchH, switchH / 2.0F, 1.0F, 0xFF059669);
            RenderGuiUtils.drawCircle(switchX + switchW - switchH / 2 - 1, switchY + switchH / 2, switchH / 2 - 2, 0xFFFFFFFF);
        } else {
            RenderGuiUtils.drawRoundedRect(switchX, switchY, switchW, switchH, switchH / 2.0F, 0xFF222838);
            RenderGuiUtils.drawRoundedOutline(switchX, switchY, switchW, switchH, switchH / 2.0F, 1.0F, 0xFF2D354A);
            RenderGuiUtils.drawCircle(switchX + switchH / 2 + 1, switchY + switchH / 2, switchH / 2 - 2, 0xFF6B7280);
        }
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
        int cardW = 168;

        if (selectedTab == 0) { // COMBAT TAB
            if (isHovered(contentLeft, contentTop, cardW, 36, mouseX, mouseY)) { PvPClient.instance.toggleSprint(); playClickSound(); }
            else if (isHovered(contentLeft + cardW + 8, contentTop, cardW, 36, mouseX, mouseY)) { PvPClient.instance.toggleAnimations17(); playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 42, cardW, 36, mouseX, mouseY)) { PvPClient.instance.toggleNoHurtCam(); playClickSound(); }
            else if (isHovered(contentLeft + cardW + 8, contentTop + 42, cardW, 36, mouseX, mouseY)) { PvPClient.instance.toggleLowFire(); playClickSound(); }

        } else if (selectedTab == 1) { // HUD ELEMENTS TAB (10 modules)
            if (isHovered(contentLeft, contentTop, cardW, 32, mouseX, mouseY)) { PvPClient.instance.pvp_fpsHud = !PvPClient.instance.pvp_fpsHud; playClickSound(); }
            else if (isHovered(contentLeft + cardW + 8, contentTop, cardW, 32, mouseX, mouseY)) { PvPClient.instance.pvp_cpsHud = !PvPClient.instance.pvp_cpsHud; playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 36, cardW, 32, mouseX, mouseY)) { PvPClient.instance.pvp_pingHud = !PvPClient.instance.pvp_pingHud; playClickSound(); }
            else if (isHovered(contentLeft + cardW + 8, contentTop + 36, cardW, 32, mouseX, mouseY)) { PvPClient.instance.pvp_keystrokesHud = !PvPClient.instance.pvp_keystrokesHud; playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 72, cardW, 32, mouseX, mouseY)) { PvPClient.instance.pvp_armorStatus = !PvPClient.instance.pvp_armorStatus; playClickSound(); }
            else if (isHovered(contentLeft + cardW + 8, contentTop + 72, cardW, 32, mouseX, mouseY)) { PvPClient.instance.pvp_potionHud = !PvPClient.instance.pvp_potionHud; playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 108, cardW, 32, mouseX, mouseY)) { PvPClient.instance.pvp_clockHud = !PvPClient.instance.pvp_clockHud; playClickSound(); }
            else if (isHovered(contentLeft + cardW + 8, contentTop + 108, cardW, 32, mouseX, mouseY)) { PvPClient.instance.pvp_infoPills = !PvPClient.instance.pvp_infoPills; playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 144, cardW, 32, mouseX, mouseY)) { PvPClient.instance.pvp_calendarHud = !PvPClient.instance.pvp_calendarHud; playClickSound(); }
            else if (isHovered(contentLeft + cardW + 8, contentTop + 144, cardW, 32, mouseX, mouseY)) { PvPClient.instance.pvp_cooldownsHud = !PvPClient.instance.pvp_cooldownsHud; playClickSound(); }

        } else if (selectedTab == 2) { // VISUALS TAB
            int fullW = cardW * 2 + 8;
            if (isHovered(contentLeft, contentTop, fullW, 32, mouseX, mouseY)) { PvPClient.instance.toggleFullbright(); playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 38, fullW, 32, mouseX, mouseY)) { PvPClient.instance.pvp_blockOutline = !PvPClient.instance.pvp_blockOutline; playClickSound(); }

        } else if (selectedTab == 3) { // ENGINE & PERFORMANCE TAB
            int fullW = cardW * 2 + 8;
            if (isHovered(contentLeft, contentTop, fullW, 32, mouseX, mouseY)) {
                SodiumOptimizer.instance.sodiumChunkRebuilding = !SodiumOptimizer.instance.sodiumChunkRebuilding;
                playClickSound();
            } else if (isHovered(contentLeft, contentTop + 38, fullW, 32, mouseX, mouseY)) {
                SodiumOptimizer.instance.entityFrustumCulling = !SodiumOptimizer.instance.entityFrustumCulling;
                playClickSound();
            } else if (isHovered(contentLeft, contentTop + 76, fullW, 32, mouseX, mouseY)) {
                SodiumOptimizer.instance.fastMath = !SodiumOptimizer.instance.fastMath;
                playClickSound();
            } else if (isHovered(contentLeft, contentTop + 114, fullW, 32, mouseX, mouseY)) {
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