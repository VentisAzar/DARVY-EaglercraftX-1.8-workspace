package net.minecraft.client.gui;

import net.minecraft.client.PvPClient;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import java.io.IOException;

public class GuiHudEditor extends GuiScreen {
    private final GuiScreen parent;
    private boolean draggingFps = false;
    private boolean draggingScore = false;

    public GuiHudEditor(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(200, width / 2 - 100, height / 2 + 50, "Save & Exit"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 2 + 75, "Reset Positions"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 200) mc.displayGuiScreen(parent);
        if (button.id == 1) {
            PvPClient.instance.fpsX = 5; PvPClient.instance.fpsY = 5;
            PvPClient.instance.scoreboardX = 0; PvPClient.instance.scoreboardY = 0;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawCenteredString(fontRendererObj, "HUD Editor - Drag boxes to move elements", width / 2, 20, 0x00FFFF);
        drawCenteredString(fontRendererObj, "Scroll to change scale (Not implemented yet)", width / 2, 32, 0xAAAAAA);

        // FPS Box
        int fx = PvPClient.instance.fpsX, fy = PvPClient.instance.fpsY;
        drawRect(fx, fy, fx + 50, fy + 15, 0x7700FFFF);
        fontRendererObj.drawString("FPS HUD", fx + 2, fy + 4, 0xFFFFFF);

        // Scoreboard Box (Representative)
        int sx = width - 100 + PvPClient.instance.scoreboardX;
        int sy = height / 2 - 50 + PvPClient.instance.scoreboardY;
        drawRect(sx, sy, sx + 90, sy + 100, 0x77FF00FF);
        fontRendererObj.drawString("Scoreboard", sx + 5, sy + 45, 0xFFFFFF);

        if (draggingFps) { PvPClient.instance.fpsX = mouseX - 25; PvPClient.instance.fpsY = mouseY - 7; }
        if (draggingScore) { PvPClient.instance.scoreboardX = mouseX - (width - 55); PvPClient.instance.scoreboardY = mouseY - (height / 2); }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mx, int my, int btn) {
        if (mx >= PvPClient.instance.fpsX && mx <= PvPClient.instance.fpsX + 50 && my >= PvPClient.instance.fpsY && my <= PvPClient.instance.fpsY + 15) draggingFps = true;
        int sx = width - 100 + PvPClient.instance.scoreboardX;
        int sy = height / 2 - 50 + PvPClient.instance.scoreboardY;
        if (mx >= sx && mx <= sx + 90 && my >= sy && my <= sy + 100) draggingScore = true;
        super.mouseClicked(mx, my, btn);
    }

    @Override
    protected void mouseReleased(int mx, int my, int state) {
        draggingFps = false;
        draggingScore = false;
        super.mouseReleased(mx, my, state);
    }
}