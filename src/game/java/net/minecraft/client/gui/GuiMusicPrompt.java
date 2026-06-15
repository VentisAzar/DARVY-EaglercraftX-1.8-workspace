package net.minecraft.client.gui;

import net.minecraft.client.PvPClient;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

public class GuiMusicPrompt extends GuiScreen {
    private final GuiScreen parent;
    private GuiTextField urlField;

    public GuiMusicPrompt(GuiScreen parent) { this.parent = parent; }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.urlField = new GuiTextField(0, fontRendererObj, width / 2 - 100, height / 2 - 10, 200, 20);
        this.urlField.setFocused(true);
        this.urlField.setText("Enter URL...");
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 2 + 20, "Play Music"));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, height / 2 + 45, "Cancel"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            PvPClient.instance.playMusic(urlField.getText());
            mc.displayGuiScreen(parent);
        } else if (button.id == 2) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void keyTyped(char c, int k) {
        urlField.textboxKeyTyped(c, k);
        if (k == 28) actionPerformed(buttonList.get(0));
    }

    @Override
    protected void mouseClicked(int x, int y, int b) {
        super.mouseClicked(x, y, b);
        urlField.mouseClicked(x, y, b);
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        drawDefaultBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); // Fix: Reset color state to stop black boxes
        GlStateManager.enableBlend();
        drawCenteredString(fontRendererObj, "Enter Music Link (YT/SoundCloud/Spotify)", width / 2, height / 2 - 30, 0xFFFFFF);
        urlField.drawTextBox();
        super.drawScreen(mx, my, pt);
    }
}