package net.minecraft.client.gui;

import net.minecraft.client.PvPClient;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class GuiClickGUI extends GuiScreen {
    
    @Override
    public void initGui() {
        this.buttonList.clear(); // Clear existing buttons to prevent duplicates on refresh
        int x = width / 2 - 100;
        int y = height / 6;
        // Read state from GameSettings for display
        this.buttonList.add(new GuiButton(1, x, y + 25, 200, 20, getButtonText("Fullbright", PvPClient.instance.pvp_fullbright)));
        this.buttonList.add(new GuiButton(2, x, y + 50, 200, 20, getButtonText("1.7 Animations", PvPClient.instance.pvp_animations17)));
        this.buttonList.add(new GuiButton(3, x, y + 75, 200, 20, getButtonText("Toggle Sprint", PvPClient.instance.pvp_toggleSprint)));
        this.buttonList.add(new GuiButton(4, x, y + 100, 200, 20, getButtonText("FPS HUD", PvPClient.instance.pvp_fpsHud)));
        this.buttonList.add(new GuiButton(100, x, y + 130, 98, 20, "\u00a7bEdit HUD"));
        this.buttonList.add(new GuiButton(101, x + 102, y + 130, 98, 20, "\u00a7dMusic"));
        this.buttonList.add(new GuiButton(200, x, y + 155, 200, 20, I18n.format("gui.done")));
    }

    private String getButtonText(String name, boolean state) {
        return name + ": " + (state ? "\u00a7aENABLED" : "\u00a7cDISABLED");
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        // Play button press sound
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));

        if (button.id == 1) PvPClient.instance.toggleFullbright();
        if (button.id == 2) PvPClient.instance.toggleAnimations17();
        if (button.id == 3) PvPClient.instance.toggleSprint();
        if (button.id == 4) PvPClient.instance.pvp_fpsHud = !PvPClient.instance.pvp_fpsHud;
        if (button.id == 100) {
            mc.displayGuiScreen(new GuiHudEditor(this));
            return;
        }
        if (button.id == 101) {
            mc.displayGuiScreen(new GuiMusicPrompt(this));
            return;
        }
        if (button.id == 200) {
            mc.displayGuiScreen(null);
            return;
        }
        initGui(); // Refresh text
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableDepth(); // Ensure GUI renders on top of everything
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); // Reset color state

        // Background Overlay
        drawRect(width / 2 - 120, height / 6 - 15, width / 2 + 120, height / 6 + 195, 0xEE050505);
        drawHorizontalLine(width / 2 - 120, width / 2 + 120, height / 6 - 15, 0xFF00FFFF);

        drawCenteredString(fontRendererObj, "\u00a7b\u00a7lDARVY PvP \u00a7f| \u00a77v2.0", width / 2, height / 6, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "\u00a78Optimization Engine: \u00a7aActive", width / 2, height / 6 + 12, 0xFFFFFF);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.enableDepth(); // Re-enable depth for world rendering
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}