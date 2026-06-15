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
        this.buttonList.add(new GuiButton(1, x, y + 40, 200, 20, getButtonText("Fullbright", PvPClient.instance.pvp_fullbright)));
        this.buttonList.add(new GuiButton(2, x, y + 65, 200, 20, getButtonText("1.7 Animations", PvPClient.instance.pvp_animations17)));
        this.buttonList.add(new GuiButton(3, x, y + 90, 200, 20, getButtonText("Toggle Sprint", PvPClient.instance.pvp_toggleSprint)));
        this.buttonList.add(new GuiButton(200, x, y + 130, 200, 20, I18n.format("gui.done")));
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
        drawRect(width / 2 - 110, height / 6 - 10, width / 2 + 110, height / 6 + 160, 0xCC000000);
        drawHorizontalLine(width / 2 - 110, width / 2 + 110, height / 6 - 10, 0xFF00FFFF);
        
        drawCenteredString(fontRendererObj, "\u00a7b\u00a7lEAGLER PvP", width / 2, height / 6, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "\u00a77Modules", width / 2, height / 6 + 12, 0xFFFFFF);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.enableDepth(); // Re-enable depth for world rendering
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }
}