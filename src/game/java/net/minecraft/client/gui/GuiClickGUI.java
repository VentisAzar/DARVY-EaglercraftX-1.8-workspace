package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.PvPClient;
import net.minecraft.client.renderer.SodiumOptimizer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class GuiClickGUI extends GuiScreen {

    private int selectedTab = 0; // 0: Optimizations, 1: PvP Modules, 2: HUD Options, 3: Visuals

    // UI Dimensions
    private int guiWidth = 420;
    private int guiHeight = 250;
    private int guiLeft;
    private int guiTop;
    private int sidebarWidth = 110;

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.guiLeft = (this.width - this.guiWidth) / 2;
        this.guiTop = (this.height - this.guiHeight) / 2;

        int bottomY = guiTop + guiHeight - 25;
        // Action Buttons at bottom
        this.buttonList.add(new GuiButton(100, guiLeft + sidebarWidth + 10, bottomY, 85, 18, "\u00a7bEdit HUD"));
        this.buttonList.add(new GuiButton(101, guiLeft + sidebarWidth + 100, bottomY, 85, 18, "\u00a7dMusic"));
        this.buttonList.add(new GuiButton(200, guiLeft + guiWidth - 95, bottomY, 85, 18, "\u00a7cClose"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        // Dark Semi-Transparent Main Dashboard Card
        drawRect(guiLeft, guiTop, guiLeft + guiWidth, guiTop + guiHeight, 0xEE0B0E14);
        
        // Neon Cyan-Purple Border Accent
        drawHorizontalLine(guiLeft, guiLeft + guiWidth, guiTop, 0xFF00E5FF);
        drawHorizontalLine(guiLeft, guiLeft + guiWidth, guiTop + guiHeight, 0xFF9D4EDD);
        drawVerticalLine(guiLeft, guiTop, guiTop + guiHeight, 0xFF00E5FF);
        drawVerticalLine(guiLeft + guiWidth, guiTop, guiTop + guiHeight, 0xFF9D4EDD);

        // Sidebar Background
        drawRect(guiLeft, guiTop, guiLeft + sidebarWidth, guiTop + guiHeight, 0xAA121620);
        drawVerticalLine(guiLeft + sidebarWidth, guiTop, guiTop + guiHeight, 0xFF1E2638);

        // Title Header in Sidebar
        drawCenteredString(fontRendererObj, "\u00a7b\u00a7lDARVY \u00a7fPvP", guiLeft + sidebarWidth / 2, guiTop + 12, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "\u00a78Sodium v2.5", guiLeft + sidebarWidth / 2, guiTop + 24, 0xFFFFFF);

        // Sidebar Navigation Tabs
        String[] tabs = { "Optimizations", "PvP Modules", "HUD Options", "Visuals" };
        for (int i = 0; i < tabs.length; i++) {
            int tabY = guiTop + 45 + (i * 32);
            boolean hovered = mouseX >= guiLeft + 5 && mouseX <= guiLeft + sidebarWidth - 5 && mouseY >= tabY && mouseY <= tabY + 26;
            boolean selected = selectedTab == i;

            drawRect(guiLeft + 6, tabY, guiLeft + sidebarWidth - 6, tabY + 26, selected ? 0xFF0A2239 : (hovered ? 0x50121A28 : 0x20121A28));
            
            if (selected) {
                drawRect(guiLeft + 6, tabY, guiLeft + 9, tabY + 26, 0xFF00E5FF);
            }

            drawString(fontRendererObj, (selected ? "\u00a7b\u00a7l" : "\u00a77") + tabs[i], guiLeft + 14, tabY + 9, 0xFFFFFF);
        }

        // Render Active Tab Content
        renderTabContent(mouseX, mouseY);

        // Performance Metrics Bar at Bottom of Content Panel
        int metricsY = guiTop + guiHeight - 45;
        String metricsText = "\u00a78FPS: \u00a7a" + Minecraft.getDebugFPS() + " \u00a78| Culled: \u00a7b" + SodiumOptimizer.instance.culledEntitiesThisFrame + " \u00a78| Rebuild Q: \u00a7e" + SodiumOptimizer.instance.pendingChunkRebuilds;
        drawString(fontRendererObj, metricsText, guiLeft + sidebarWidth + 12, metricsY, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.enableDepth();
    }

    private void renderTabContent(int mouseX, int mouseY) {
        int contentLeft = guiLeft + sidebarWidth + 12;
        int contentTop = guiTop + 15;

        if (selectedTab == 0) { // OPTIMIZATIONS (Sodium / Lithium)
            drawString(fontRendererObj, "\u00a7b\u00a7lSodium Engine Optimizations", contentLeft, contentTop, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a77Accelerate rendering, reduced GC & amortized chunk updates", contentLeft, contentTop + 12, 0xFFFFFF);

            renderModuleCard(contentLeft, contentTop + 32, 280, 32, "Sodium Chunk Batching", "Spreads chunk rebuilds across frames", SodiumOptimizer.instance.sodiumChunkRebuilding, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 68, 280, 32, "Entity Frustum Culling", "Skips rendering off-screen mobs & entities", SodiumOptimizer.instance.entityFrustumCulling, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 104, 280, 32, "Fast Math Lookup Tables", "Uses Lithium fast sin/cos lookup tables", SodiumOptimizer.instance.fastMath, mouseX, mouseY);
            
            // Chunk Budget Card
            int budgetY = contentTop + 140;
            drawRect(contentLeft, budgetY, contentLeft + 280, budgetY + 30, 0x4015151A);
            drawString(fontRendererObj, "\u00a7f\u00a7lChunk Rebuild Budget", contentLeft + 8, budgetY + 5, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a77Max chunk updates limit per frame", contentLeft + 8, budgetY + 16, 0xFFFFFF);
            
            String budgetStr = "\u00a7e" + SodiumOptimizer.instance.getRebuildBudgetName();
            int btnX = contentLeft + 280 - fontRendererObj.getStringWidth(budgetStr) - 16;
            drawRect(btnX - 4, budgetY + 6, contentLeft + 276, budgetY + 24, 0xFF1E2638);
            drawString(fontRendererObj, budgetStr, btnX, budgetY + 10, 0xFFFFFF);

        } else if (selectedTab == 1) { // PVP MODULES
            drawString(fontRendererObj, "\u00a7b\u00a7lPvP Enhancements", contentLeft, contentTop, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a77Core combat mechanics and animation tweaks", contentLeft, contentTop + 12, 0xFFFFFF);

            renderModuleCard(contentLeft, contentTop + 32, 136, 32, "Toggle Sprint", "Auto-sprints when moving", PvPClient.instance.pvp_toggleSprint, mouseX, mouseY);
            renderModuleCard(contentLeft + 144, contentTop + 32, 136, 32, "1.7 Animations", "1.7 Blockhit & swing", PvPClient.instance.pvp_animations17, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 68, 136, 32, "No Hurt Cam", "Disables damage camera shake", PvPClient.instance.pvp_noHurtCam, mouseX, mouseY);
            renderModuleCard(contentLeft + 144, contentTop + 68, 136, 32, "Low Fire Overlay", "Lowers fire height on screen", PvPClient.instance.pvp_lowFire, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 104, 136, 32, "Hit Combo Display", "Tracks hit streak counter", PvPClient.instance.pvp_comboDisplay, mouseX, mouseY);
            renderModuleCard(contentLeft + 144, contentTop + 104, 136, 32, "CPS Counter", "Shows Left & Right CPS", PvPClient.instance.pvp_cpsHud, mouseX, mouseY);

        } else if (selectedTab == 2) { // HUD OPTIONS
            drawString(fontRendererObj, "\u00a7b\u00a7lHUD & Overlay Modules", contentLeft, contentTop, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a77Customize on-screen heads up displays", contentLeft, contentTop + 12, 0xFFFFFF);

            renderModuleCard(contentLeft, contentTop + 32, 136, 32, "FPS Display", "Renders current FPS", PvPClient.instance.pvp_fpsHud, mouseX, mouseY);
            renderModuleCard(contentLeft + 144, contentTop + 32, 136, 32, "Keystrokes HUD", "WASD & Click overlay", PvPClient.instance.pvp_keystrokesHud, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 68, 136, 32, "Armor Durability", "Displays equipped armor status", PvPClient.instance.pvp_armorStatus, mouseX, mouseY);
            renderModuleCard(contentLeft + 144, contentTop + 68, 136, 32, "Compass HUD", "Cardinal direction overlay", PvPClient.instance.pvp_compassHud, mouseX, mouseY);

        } else if (selectedTab == 3) { // VISUALS
            drawString(fontRendererObj, "\u00a7b\u00a7lVisual & Environment Options", contentLeft, contentTop, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a77Lighting, crosshair, and visual customization", contentLeft, contentTop + 12, 0xFFFFFF);

            renderModuleCard(contentLeft, contentTop + 32, 280, 32, "Fullbright", "Maximum brightness in caves & dark areas", PvPClient.instance.pvp_fullbright, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 68, 280, 32, "Custom Crosshair", "Renders custom PvP crosshair", PvPClient.instance.pvp_customCrosshair, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 104, 280, 32, "Block Selection Outline", "Custom color highlighted block outline", PvPClient.instance.pvp_blockOutline, mouseX, mouseY);
        }
    }

    private void renderModuleCard(int x, int y, int width, int height, String title, String subtitle, boolean enabled, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        
        int cardBg = enabled ? (hovered ? 0x5000E5FF : 0x3000E5FF) : (hovered ? 0x6015151A : 0x4015151A);
        drawRect(x, y, x + width, y + height, cardBg);
        
        if (enabled) {
            drawRect(x, y, x + 3, y + height, 0xFF00E5FF);
        }

        drawString(fontRendererObj, (enabled ? "\u00a7f\u00a7l" : "\u00a77") + title, x + 8, y + 5, 0xFFFFFF);
        drawString(fontRendererObj, "\u00a78" + subtitle, x + 8, y + 17, 0xFFFFFF);

        String badge = enabled ? "\u00a7b\u00a7l[ ON ]" : "\u00a77[ OFF ]";
        int badgeX = x + width - fontRendererObj.getStringWidth(badge) - 8;
        drawString(fontRendererObj, badge, badgeX, y + 10, 0xFFFFFF);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton != 0) return;

        // Sidebar Tab Clicks
        for (int i = 0; i < 4; i++) {
            int tabY = guiTop + 45 + (i * 32);
            if (mouseX >= guiLeft + 5 && mouseX <= guiLeft + sidebarWidth - 5 && mouseY >= tabY && mouseY <= tabY + 26) {
                selectedTab = i;
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                return;
            }
        }

        int contentLeft = guiLeft + sidebarWidth + 12;
        int contentTop = guiTop + 15;

        if (selectedTab == 0) { // Optimizations tab clicks
            if (isHovered(contentLeft, contentTop + 32, 280, 32, mouseX, mouseY)) {
                SodiumOptimizer.instance.sodiumChunkRebuilding = !SodiumOptimizer.instance.sodiumChunkRebuilding;
                playClickSound();
            } else if (isHovered(contentLeft, contentTop + 68, 280, 32, mouseX, mouseY)) {
                SodiumOptimizer.instance.entityFrustumCulling = !SodiumOptimizer.instance.entityFrustumCulling;
                playClickSound();
            } else if (isHovered(contentLeft, contentTop + 104, 280, 32, mouseX, mouseY)) {
                SodiumOptimizer.instance.fastMath = !SodiumOptimizer.instance.fastMath;
                playClickSound();
            } else if (isHovered(contentLeft, contentTop + 140, 280, 30, mouseX, mouseY)) {
                SodiumOptimizer.instance.cycleRebuildBudget();
                playClickSound();
            }

        } else if (selectedTab == 1) { // PvP Modules tab clicks
            if (isHovered(contentLeft, contentTop + 32, 136, 32, mouseX, mouseY)) { PvPClient.instance.toggleSprint(); playClickSound(); }
            else if (isHovered(contentLeft + 144, contentTop + 32, 136, 32, mouseX, mouseY)) { PvPClient.instance.toggleAnimations17(); playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 68, 136, 32, mouseX, mouseY)) { PvPClient.instance.toggleNoHurtCam(); playClickSound(); }
            else if (isHovered(contentLeft + 144, contentTop + 68, 136, 32, mouseX, mouseY)) { PvPClient.instance.toggleLowFire(); playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 104, 136, 32, mouseX, mouseY)) { PvPClient.instance.pvp_comboDisplay = !PvPClient.instance.pvp_comboDisplay; playClickSound(); }
            else if (isHovered(contentLeft + 144, contentTop + 104, 136, 32, mouseX, mouseY)) { PvPClient.instance.pvp_cpsHud = !PvPClient.instance.pvp_cpsHud; playClickSound(); }

        } else if (selectedTab == 2) { // HUD Options tab clicks
            if (isHovered(contentLeft, contentTop + 32, 136, 32, mouseX, mouseY)) { PvPClient.instance.pvp_fpsHud = !PvPClient.instance.pvp_fpsHud; playClickSound(); }
            else if (isHovered(contentLeft + 144, contentTop + 32, 136, 32, mouseX, mouseY)) { PvPClient.instance.pvp_keystrokesHud = !PvPClient.instance.pvp_keystrokesHud; playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 68, 136, 32, mouseX, mouseY)) { PvPClient.instance.pvp_armorStatus = !PvPClient.instance.pvp_armorStatus; playClickSound(); }
            else if (isHovered(contentLeft + 144, contentTop + 68, 136, 32, mouseX, mouseY)) { PvPClient.instance.pvp_compassHud = !PvPClient.instance.pvp_compassHud; playClickSound(); }

        } else if (selectedTab == 3) { // Visuals tab clicks
            if (isHovered(contentLeft, contentTop + 32, 280, 32, mouseX, mouseY)) { PvPClient.instance.toggleFullbright(); playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 68, 280, 32, mouseX, mouseY)) { PvPClient.instance.pvp_customCrosshair = !PvPClient.instance.pvp_customCrosshair; playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 104, 280, 32, mouseX, mouseY)) { PvPClient.instance.pvp_blockOutline = !PvPClient.instance.pvp_blockOutline; playClickSound(); }
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
            mc.displayGuiScreen(new GuiMusicPrompt(this));
        } else if (button.id == 200) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}