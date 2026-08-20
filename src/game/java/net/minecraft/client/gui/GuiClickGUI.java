package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.PvPClient;
import net.minecraft.client.renderer.SodiumOptimizer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class GuiClickGUI extends GuiScreen {

    private int selectedTab = 0; // 0: Overview, 1: Optimizations, 2: PvP Modules, 3: HUD Options, 4: Visuals & Font

    // UI Dimensions
    private int guiWidth = 460;
    private int guiHeight = 270;
    private int guiLeft;
    private int guiTop;
    private int sidebarWidth = 60;

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.guiLeft = (this.width - this.guiWidth) / 2;
        this.guiTop = (this.height - this.guiHeight) / 2;

        int bottomY = guiTop + guiHeight - 24;
        int contentX = guiLeft + sidebarWidth + 12;
        // Action Buttons at bottom of content panel
        this.buttonList.add(new GuiButton(100, contentX, bottomY, 95, 18, "\u00a7bEdit HUD"));
        this.buttonList.add(new GuiButton(101, contentX + 105, bottomY, 95, 18, "\u00a7dMusic Player"));
        this.buttonList.add(new GuiButton(200, guiLeft + guiWidth - 90, bottomY, 80, 18, "\u00a7cClose"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        // Glassmorphic Outer Dashboard Card
        drawRect(guiLeft, guiTop, guiLeft + guiWidth, guiTop + guiHeight, 0xEE0B0E14);
        
        // Sleek Subtle Outer Border
        drawHorizontalLine(guiLeft, guiLeft + guiWidth, guiTop, 0xFF2A2E3D);
        drawHorizontalLine(guiLeft, guiLeft + guiWidth, guiTop + guiHeight, 0xFF2A2E3D);
        drawVerticalLine(guiLeft, guiTop, guiTop + guiHeight, 0xFF2A2E3D);
        drawVerticalLine(guiLeft + guiWidth, guiTop, guiTop + guiHeight, 0xFF2A2E3D);

        // Sidebar Navigation Panel
        drawRect(guiLeft, guiTop, guiLeft + sidebarWidth, guiTop + guiHeight, 0xFF12151F);
        drawVerticalLine(guiLeft + sidebarWidth, guiTop, guiTop + guiHeight, 0xFF1E2230);

        // Top Logo Badge in Sidebar
        drawRect(guiLeft + 14, guiTop + 12, guiLeft + sidebarWidth - 14, guiTop + 44, 0xFF4F46E5);
        drawCenteredString(fontRendererObj, "\u00a7f\u00a7lD", guiLeft + sidebarWidth / 2, guiTop + 22, 0xFFFFFF);

        // Sidebar Navigation Icons/Tabs (0: Home, 1: Opts, 2: PvP, 3: HUD, 4: Visuals)
        String[] tabIcons = { "HM", "OP", "PV", "HD", "VS" };
        String[] tabLabels = { "Home", "Engine", "PvP", "HUD", "Visual" };

        for (int i = 0; i < tabIcons.length; i++) {
            int tabY = guiTop + 56 + (i * 38);
            boolean hovered = mouseX >= guiLeft + 6 && mouseX <= guiLeft + sidebarWidth - 6 && mouseY >= tabY && mouseY <= tabY + 32;
            boolean selected = selectedTab == i;

            int bgCol = selected ? 0xFF3B82F6 : (hovered ? 0x403B82F6 : 0x15FFFFFF);
            drawRect(guiLeft + 8, tabY, guiLeft + sidebarWidth - 8, tabY + 32, bgCol);

            drawCenteredString(fontRendererObj, (selected ? "\u00a7f\u00a7l" : "\u00a77") + tabIcons[i], guiLeft + sidebarWidth / 2, tabY + 6, 0xFFFFFF);
            drawCenteredString(fontRendererObj, (selected ? "\u00a7b" : "\u00a78") + tabLabels[i], guiLeft + sidebarWidth / 2, tabY + 18, 0xFFFFFF);
        }

        // Main Dashboard Header Title
        int contentLeft = guiLeft + sidebarWidth + 12;
        int contentTop = guiTop + 12;
        drawString(fontRendererObj, "\u00a7f\u00a7lDARVY \u00a7bClient Dashboard", contentLeft, contentTop, 0xFFFFFF);
        drawString(fontRendererObj, "\u00a78Darvy Engine v1.8 | Custom Client", contentLeft + 180, contentTop, 0xFFFFFF);
        drawHorizontalLine(contentLeft, guiLeft + guiWidth - 12, contentTop + 14, 0xFF1E2230);

        // Render Active Tab Content Cards
        renderTabContent(mouseX, mouseY);

        // Performance Metrics Footer
        int metricsY = guiTop + guiHeight - 38;
        String metricsText = "\u00a78FPS: \u00a7a" + Minecraft.getDebugFPS() + " \u00a78| Culled: \u00a7b" + SodiumOptimizer.instance.culledEntitiesThisFrame + " \u00a78| Rebuild Q: \u00a7e" + SodiumOptimizer.instance.pendingChunkRebuilds;
        drawString(fontRendererObj, metricsText, contentLeft, metricsY, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.enableDepth();
    }

    private void renderTabContent(int mouseX, int mouseY) {
        int contentLeft = guiLeft + sidebarWidth + 12;
        int contentTop = guiTop + 32;

        if (selectedTab == 0) { // OVERVIEW / HOME
            // Card 1: News
            drawCardBackground(contentLeft, contentTop, 185, 180);
            drawString(fontRendererObj, "\u00a7f\u00a7lNews & Announcements", contentLeft + 10, contentTop + 10, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a7bDarvy Client Release", contentLeft + 10, contentTop + 26, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a77Welcome to Darvy Client!", contentLeft + 10, contentTop + 40, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a78Engine optimizations,", contentLeft + 10, contentTop + 52, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a780-delay hit registration,", contentLeft + 10, contentTop + 64, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a78and customizable HUD elements.", contentLeft + 10, contentTop + 76, 0xFFFFFF);

            // Card 2: Changelog
            int card2X = contentLeft + 195;
            drawCardBackground(card2X, contentTop, 180, 115);
            drawString(fontRendererObj, "\u00a7f\u00a7lChangelog", card2X + 10, contentTop + 10, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a7a+ \u00a77Movable Keystrokes & Armor HUD", card2X + 10, contentTop + 26, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a7a+ \u00a77Added Potion HUD module", card2X + 10, contentTop + 40, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a7a+ \u00a77White Block Selection Highlight", card2X + 10, contentTop + 54, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a7a+ \u00a77Removed Hit Delay & Click Lag", card2X + 10, contentTop + 68, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a7a+ \u00a77Samsung Sharp Sans Font Toggle", card2X + 10, contentTop + 82, 0xFFFFFF);

            // Card 3: Community Discord
            drawCardBackground(card2X, contentTop + 123, 180, 57);
            drawString(fontRendererObj, "\u00a7f\u00a7lJoin Discord Server!", card2X + 10, contentTop + 131, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a78Get support & talk to players", card2X + 10, contentTop + 144, 0xFFFFFF);
            drawRect(card2X + 120, contentTop + 132, card2X + 172, contentTop + 152, 0xFF3B82F6);
            drawString(fontRendererObj, "\u00a7f\u00a7lJoin >", card2X + 126, contentTop + 138, 0xFFFFFF);

        } else if (selectedTab == 1) { // ENGINE OPTIMIZATIONS
            drawString(fontRendererObj, "\u00a7b\u00a7lDarvy Engine Performance", contentLeft, contentTop, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a77Accelerate rendering, reduced GC & amortized chunk updates", contentLeft, contentTop + 12, 0xFFFFFF);

            renderModuleCard(contentLeft, contentTop + 28, 375, 30, "Chunk Batching", "Spreads chunk rebuilds smoothly across frames", SodiumOptimizer.instance.sodiumChunkRebuilding, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 62, 375, 30, "Entity Frustum Culling", "Skips rendering off-screen mobs & entities", SodiumOptimizer.instance.entityFrustumCulling, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 96, 375, 30, "Fast Math Tables", "Accelerated sin/cos trigonometry lookup tables", SodiumOptimizer.instance.fastMath, mouseX, mouseY);
            
            // Chunk Budget Card
            int budgetY = contentTop + 130;
            drawCardBackground(contentLeft, budgetY, 375, 30);
            drawString(fontRendererObj, "\u00a7f\u00a7lChunk Rebuild Budget", contentLeft + 8, budgetY + 5, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a77Max chunk updates limit per frame", contentLeft + 8, budgetY + 16, 0xFFFFFF);
            
            String budgetStr = "\u00a7e" + SodiumOptimizer.instance.getRebuildBudgetName();
            int btnX = contentLeft + 375 - fontRendererObj.getStringWidth(budgetStr) - 16;
            drawRect(btnX - 4, budgetY + 5, contentLeft + 371, budgetY + 23, 0xFF1E2638);
            drawString(fontRendererObj, budgetStr, btnX, budgetY + 9, 0xFFFFFF);

        } else if (selectedTab == 2) { // PVP MODULES
            drawString(fontRendererObj, "\u00a7b\u00a7lPvP Combat Mechanics", contentLeft, contentTop, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a77Instant hit registration, camera shake & animations", contentLeft, contentTop + 12, 0xFFFFFF);

            renderModuleCard(contentLeft, contentTop + 28, 182, 32, "Toggle Sprint", "Auto-sprints while moving", PvPClient.instance.pvp_toggleSprint, mouseX, mouseY);
            renderModuleCard(contentLeft + 190, contentTop + 28, 185, 32, "1.7 Animations", "1.7 Blockhit & swing anim", PvPClient.instance.pvp_animations17, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 64, 182, 32, "No Hurt Cam", "Disables camera shake on hit", PvPClient.instance.pvp_noHurtCam, mouseX, mouseY);
            renderModuleCard(contentLeft + 190, contentTop + 64, 185, 32, "Low Fire Overlay", "Lowers fire height on screen", PvPClient.instance.pvp_lowFire, mouseX, mouseY);

        } else if (selectedTab == 3) { // HUD OPTIONS
            drawString(fontRendererObj, "\u00a7b\u00a7lHeads-Up Display Modules", contentLeft, contentTop, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a77Customize on-screen HUD elements (Use Edit HUD to move)", contentLeft, contentTop + 12, 0xFFFFFF);

            renderModuleCard(contentLeft, contentTop + 28, 182, 32, "FPS & CPS Display", "Renders current FPS & CPS", PvPClient.instance.pvp_fpsHud, mouseX, mouseY);
            renderModuleCard(contentLeft + 190, contentTop + 28, 185, 32, "Keystrokes HUD", "WASD & Mouse click overlay", PvPClient.instance.pvp_keystrokesHud, mouseX, mouseY);

            renderModuleCard(contentLeft, contentTop + 64, 182, 32, "Armor Durability", "Displays equipped armor status", PvPClient.instance.pvp_armorStatus, mouseX, mouseY);
            renderModuleCard(contentLeft + 190, contentTop + 64, 185, 32, "Potion Status HUD", "Displays active potion effects", PvPClient.instance.pvp_potionHud, mouseX, mouseY);

        } else if (selectedTab == 4) { // VISUALS & FONT
            drawString(fontRendererObj, "\u00a7b\u00a7lVisuals & Custom Typography", contentLeft, contentTop, 0xFFFFFF);
            drawString(fontRendererObj, "\u00a77Lighting, crosshair, outline & font settings", contentLeft, contentTop + 12, 0xFFFFFF);

            renderModuleCard(contentLeft, contentTop + 28, 375, 30, "Fullbright", "Maximum brightness in caves & dark areas", PvPClient.instance.pvp_fullbright, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 62, 375, 30, "Custom Crosshair", "Renders custom PvP crosshair", PvPClient.instance.pvp_customCrosshair, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 96, 375, 30, "White Block Highlight", "Renders bright white block selection outline", PvPClient.instance.pvp_blockOutline, mouseX, mouseY);
            renderModuleCard(contentLeft, contentTop + 130, 375, 30, "Samsung Sharp Sans Font", "Toggle default vs Custom Bold Sans font", PvPClient.instance.pvp_customFont, mouseX, mouseY);
        }
    }

    private void drawCardBackground(int x, int y, int w, int h) {
        drawRect(x, y, x + w, y + h, 0xFF161923);
        drawHorizontalLine(x, x + w, y, 0xFF222636);
        drawHorizontalLine(x, x + w, y + h, 0xFF222636);
        drawVerticalLine(x, y, y + h, 0xFF222636);
        drawVerticalLine(x + w, y, y + h, 0xFF222636);
    }

    private void renderModuleCard(int x, int y, int width, int height, String title, String subtitle, boolean enabled, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        
        int cardBg = enabled ? (hovered ? 0x503B82F6 : 0x303B82F6) : (hovered ? 0xFF1E2230 : 0xFF161923);
        drawRect(x, y, x + width, y + height, cardBg);
        
        drawHorizontalLine(x, x + width, y, 0xFF222636);
        drawHorizontalLine(x, x + width, y + height, 0xFF222636);
        drawVerticalLine(x, y, y + height, 0xFF222636);
        drawVerticalLine(x + width, y, y + height, 0xFF222636);

        if (enabled) {
            drawRect(x, y, x + 3, y + height, 0xFF3B82F6);
        }

        drawString(fontRendererObj, (enabled ? "\u00a7f\u00a7l" : "\u00a77") + title, x + 8, y + 4, 0xFFFFFF);
        drawString(fontRendererObj, "\u00a78" + subtitle, x + 8, y + 16, 0xFFFFFF);

        String badge = enabled ? "\u00a7b\u00a7l[ ON ]" : "\u00a77[ OFF ]";
        int badgeX = x + width - fontRendererObj.getStringWidth(badge) - 8;
        drawString(fontRendererObj, badge, badgeX, y + (height - 8) / 2, 0xFFFFFF);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton != 0) return;

        // Sidebar Tab Clicks
        for (int i = 0; i < 5; i++) {
            int tabY = guiTop + 56 + (i * 38);
            if (mouseX >= guiLeft + 6 && mouseX <= guiLeft + sidebarWidth - 6 && mouseY >= tabY && mouseY <= tabY + 32) {
                selectedTab = i;
                playClickSound();
                return;
            }
        }

        int contentLeft = guiLeft + sidebarWidth + 12;
        int contentTop = guiTop + 32;

        if (selectedTab == 0) { // Home tab clicks
            int card2X = contentLeft + 195;
            if (isHovered(card2X + 120, contentTop + 132, 52, 20, mouseX, mouseY)) {
                PvPClient.instance.playMusic("https://discord.gg");
                playClickSound();
            }

        } else if (selectedTab == 1) { // Optimizations tab clicks
            if (isHovered(contentLeft, contentTop + 28, 375, 30, mouseX, mouseY)) {
                SodiumOptimizer.instance.sodiumChunkRebuilding = !SodiumOptimizer.instance.sodiumChunkRebuilding;
                playClickSound();
            } else if (isHovered(contentLeft, contentTop + 62, 375, 30, mouseX, mouseY)) {
                SodiumOptimizer.instance.entityFrustumCulling = !SodiumOptimizer.instance.entityFrustumCulling;
                playClickSound();
            } else if (isHovered(contentLeft, contentTop + 96, 375, 30, mouseX, mouseY)) {
                SodiumOptimizer.instance.fastMath = !SodiumOptimizer.instance.fastMath;
                playClickSound();
            } else if (isHovered(contentLeft, contentTop + 130, 375, 30, mouseX, mouseY)) {
                SodiumOptimizer.instance.cycleRebuildBudget();
                playClickSound();
            }

        } else if (selectedTab == 2) { // PvP Modules tab clicks
            if (isHovered(contentLeft, contentTop + 28, 182, 32, mouseX, mouseY)) { PvPClient.instance.toggleSprint(); playClickSound(); }
            else if (isHovered(contentLeft + 190, contentTop + 28, 185, 32, mouseX, mouseY)) { PvPClient.instance.toggleAnimations17(); playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 64, 182, 32, mouseX, mouseY)) { PvPClient.instance.toggleNoHurtCam(); playClickSound(); }
            else if (isHovered(contentLeft + 190, contentTop + 64, 185, 32, mouseX, mouseY)) { PvPClient.instance.toggleLowFire(); playClickSound(); }

        } else if (selectedTab == 3) { // HUD Options tab clicks
            if (isHovered(contentLeft, contentTop + 28, 182, 32, mouseX, mouseY)) { PvPClient.instance.pvp_fpsHud = !PvPClient.instance.pvp_fpsHud; playClickSound(); }
            else if (isHovered(contentLeft + 190, contentTop + 28, 185, 32, mouseX, mouseY)) { PvPClient.instance.pvp_keystrokesHud = !PvPClient.instance.pvp_keystrokesHud; playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 64, 182, 32, mouseX, mouseY)) { PvPClient.instance.pvp_armorStatus = !PvPClient.instance.pvp_armorStatus; playClickSound(); }
            else if (isHovered(contentLeft + 190, contentTop + 64, 185, 32, mouseX, mouseY)) { PvPClient.instance.pvp_potionHud = !PvPClient.instance.pvp_potionHud; playClickSound(); }

        } else if (selectedTab == 4) { // Visuals & Font tab clicks
            if (isHovered(contentLeft, contentTop + 28, 375, 30, mouseX, mouseY)) { PvPClient.instance.toggleFullbright(); playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 62, 375, 30, mouseX, mouseY)) { PvPClient.instance.pvp_customCrosshair = !PvPClient.instance.pvp_customCrosshair; playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 96, 375, 30, mouseX, mouseY)) { PvPClient.instance.pvp_blockOutline = !PvPClient.instance.pvp_blockOutline; playClickSound(); }
            else if (isHovered(contentLeft, contentTop + 130, 375, 30, mouseX, mouseY)) { PvPClient.instance.toggleCustomFont(); playClickSound(); }
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