package net.minecraft.client.gui;

import net.minecraft.client.PvPClient;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.Mouse;
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
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); // Fix: Reset color state to stop black boxes
        GlStateManager.enableBlend();
        drawCenteredString(fontRendererObj, "HUD Editor - Drag boxes to move elements", width / 2, 20, 0x00FFFF);
        drawCenteredString(fontRendererObj, "Hover + Scroll to change Scale", width / 2, 32, 0xAAAAAA);

        // FPS Box
        int fx = PvPClient.instance.fpsX, fy = PvPClient.instance.fpsY;
        drawRect(fx, fy, fx + (int)(50 * PvPClient.instance.fpsScale), fy + (int)(15 * PvPClient.instance.fpsScale), 0x7700FFFF);
        fontRendererObj.drawString("FPS HUD", fx + 2, fy + 4, 0xFFFFFF);

        // Scoreboard Box (Representative)
        int sx = width - 100 + PvPClient.instance.scoreboardX;
        int sy = height / 2 - 50 + PvPClient.instance.scoreboardY;
        drawRect(sx, sy, sx + (int)(90 * PvPClient.instance.scoreboardScale), sy + (int)(100 * PvPClient.instance.scoreboardScale), 0x77FF00FF);
        fontRendererObj.drawString("Scoreboard", sx + 5, sy + 45, 0xFFFFFF);

        if (draggingFps) { PvPClient.instance.fpsX = mouseX - 25; PvPClient.instance.fpsY = mouseY - 7; }
        if (draggingScore) { PvPClient.instance.scoreboardX = mouseX - (width - 55); PvPClient.instance.scoreboardY = mouseY - (height / 2); }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            int mx = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int my = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

            // Account for scale in hover detection
            int fw = (int)(50 * PvPClient.instance.fpsScale);
            if (mx >= PvPClient.instance.fpsX && mx <= PvPClient.instance.fpsX + fw) {
                PvPClient.instance.fpsScale = Math.max(0.5F, PvPClient.instance.fpsScale + (wheel > 0 ? 0.1F : -0.1F));
            }
            int sx = width - 100 + PvPClient.instance.scoreboardX;
            int sy = height / 2 - 50 + PvPClient.instance.scoreboardY;
            int sw = (int)(90 * PvPClient.instance.scoreboardScale);
            int sh = (int)(100 * PvPClient.instance.scoreboardScale);
            if (mx >= sx && mx <= sx + sw && my >= sy && my <= sy + sh) {
                PvPClient.instance.scoreboardScale = Math.max(0.5F, PvPClient.instance.scoreboardScale + (wheel > 0 ? 0.1F : -0.1F));
            }
        }
    }

    @Override
    protected void mouseClicked(int mx, int my, int btn) {
        int fw = (int)(50 * PvPClient.instance.fpsScale);
        int fh = (int)(15 * PvPClient.instance.fpsScale);
        if (mx >= PvPClient.instance.fpsX && mx <= PvPClient.instance.fpsX + fw && my >= PvPClient.instance.fpsY && my <= PvPClient.instance.fpsY + fh) draggingFps = true;

        int sx = width - 100 + PvPClient.instance.scoreboardX;
        int sy = height / 2 - 50 + PvPClient.instance.scoreboardY;
        int sw = (int)(90 * PvPClient.instance.scoreboardScale);
        int sh = (int)(100 * PvPClient.instance.scoreboardScale);
        if (mx >= sx && mx <= sx + sw && my >= sy && my <= sy + sh) draggingScore = true;
        super.mouseClicked(mx, my, btn);
    }

    @Override
    protected void mouseReleased(int mx, int my, int state) {
        draggingFps = false;
        draggingScore = false;
        super.mouseReleased(mx, my, state);
    }
}