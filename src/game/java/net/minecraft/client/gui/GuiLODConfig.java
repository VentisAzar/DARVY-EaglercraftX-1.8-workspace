package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.LODTerrainManager;

/**
 * GuiLODConfig - The "Distant Darvy" settings menu.
 * Provides a centralized place to configure distant terrain rendering.
 */
public class GuiLODConfig extends GuiScreen {
    private final GuiScreen parentScreen;
    protected String screenTitle = "Distant Darvy Config";

    public GuiLODConfig(GuiScreen parent) {
        this.parentScreen = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        int yBase = this.height / 4 + 24;

        // Column 1
        this.buttonList.add(new GuiButton(1, this.width / 2 - 155, yBase, 150, 20, 
                "Enable System: " + (LODTerrainManager.enableLODSystem ? "YES" : "OFF")));
        
        this.buttonList.add(new GuiButton(2, this.width / 2 - 155, yBase + 24, 150, 20, 
                "LOD Resolution: " + LODTerrainManager.lodResolution + "x"));

        // Column 2
        this.buttonList.add(new GuiButton(3, this.width / 2 + 5, yBase, 150, 20, 
                "Draw Distance: " + LODTerrainManager.lodDrawDistance + " Chunks"));

        this.buttonList.add(new GuiButton(4, this.width / 2 + 5, yBase + 24, 150, 20, 
                "Cache Storage: IndexedDB"));

        // Navigation
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 192, I18n.format("gui.done")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 200) {
            this.mc.displayGuiScreen(parentScreen);
        } else if (button.id == 1) {
            LODTerrainManager.enableLODSystem = !LODTerrainManager.enableLODSystem;
        } else if (button.id == 2) {
            // Cycle resolution: 1x -> 2x -> 4x -> 8x
            if (LODTerrainManager.lodResolution == 1) LODTerrainManager.lodResolution = 2;
            else if (LODTerrainManager.lodResolution == 2) LODTerrainManager.lodResolution = 4;
            else if (LODTerrainManager.lodResolution == 4) LODTerrainManager.lodResolution = 8;
            else LODTerrainManager.lodResolution = 1;
        } else if (button.id == 3) {
            // Cycle draw distance
            if (LODTerrainManager.lodDrawDistance == 32) LODTerrainManager.lodDrawDistance = 64;
            else if (LODTerrainManager.lodDrawDistance == 64) LODTerrainManager.lodDrawDistance = 128;
            else if (LODTerrainManager.lodDrawDistance == 128) LODTerrainManager.lodDrawDistance = 256;
            else if (LODTerrainManager.lodDrawDistance == 256) LODTerrainManager.lodDrawDistance = 512;
            else LODTerrainManager.lodDrawDistance = 32;
        }

        if (button.id > 0 && button.id < 200) {
            this.initGui(); // Refresh button text
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 15, 16777215);
        
        // Sodium-style description area
        int boxX = this.width / 2 - 155;
        int boxY = this.height / 4 + 85;
        drawRect(boxX, boxY, boxX + 310, boxY + 70, 0x99000000); // Darker background
        drawHorizontalLine(boxX, boxX + 310, boxY, 0xFFAA00); // Orange Sodium accent
        this.drawString(this.fontRendererObj, "Distant Darvy v1.0", boxX + 5, boxY + 8, 0xFFAA00);
        this.fontRendererObj.drawSplitString("Renders terrain far beyond Minecraft's limits using LOD geometry. Optimization: Skip " + (LODTerrainManager.lodResolution - 1) + " blocks during scanning to save memory.", 
                boxX + 5, boxY + 22, 300, 0xBBBBBB);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}