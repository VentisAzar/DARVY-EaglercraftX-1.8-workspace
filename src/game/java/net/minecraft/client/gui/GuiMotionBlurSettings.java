package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.PvPClient;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

/**
 * Settings screen for Motion Blur strength (1-10).
 * Opened when the user right-clicks the Motion Blur module in the click GUI.
 */
public class GuiMotionBlurSettings extends GuiScreen {

    private final GuiScreen parentScreen;
    private int guiWidth = 280;
    private int guiHeight = 160;
    private int guiLeft;
    private int guiTop;

    private boolean dragging = false;

    public GuiMotionBlurSettings(GuiScreen parent) {
        this.parentScreen = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.guiLeft = (this.width - this.guiWidth) / 2;
        this.guiTop = (this.height - this.guiHeight) / 2;

        // Back button
        this.buttonList.add(new GuiButton(0, guiLeft + guiWidth / 2 - 50, guiTop + guiHeight - 30, 100, 20, "\u00a7fDone"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        // Dim backdrop
        drawRect(0, 0, width, height, 0x80000000);

        // Main panel
        RenderGuiUtils.drawBox(guiLeft, guiTop, guiWidth, guiHeight, 0xF20B0E15, 0xFF1E2433);

        // Title
        drawCenteredString(fontRendererObj, "\u00a7b\u00a7lMotion Blur Settings", guiLeft + guiWidth / 2, guiTop + 10, 0xFFFFFF);

        // Current value display
        int strength = PvPClient.instance.motionBlurStrength;
        drawCenteredString(fontRendererObj, "\u00a7fStrength: \u00a7b" + strength, guiLeft + guiWidth / 2, guiTop + 30, 0xFFFFFF);

        // Slider track
        int sliderX = guiLeft + 20;
        int sliderY = guiTop + 55;
        int sliderW = guiWidth - 40;
        int sliderH = 12;

        // Track background
        RenderGuiUtils.drawBox(sliderX, sliderY, sliderW, sliderH, 0xFF12151E, 0xFF1C212E);

        // Filled portion
        float fillRatio = (strength - 1) / 9.0f;
        int fillW = (int) (sliderW * fillRatio);
        if (fillW > 0) {
            RenderGuiUtils.drawBox(sliderX, sliderY, fillW, sliderH, 0xFF1C3A6E, 0xFF3B82F6);
        }

        // Slider handle
        int handleX = sliderX + fillW - 4;
        if (handleX < sliderX) handleX = sliderX;
        RenderGuiUtils.drawBox(handleX, sliderY - 2, 8, sliderH + 4, 0xFF3B82F6, 0xFF5B9CF6);

        // Labels: 1 and 10 at the ends
        drawString(fontRendererObj, "\u00a781", sliderX, sliderY + sliderH + 4, 0x888888);
        drawString(fontRendererObj, "\u00a7810", sliderX + sliderW - 10, sliderY + sliderH + 4, 0x888888);

        // Drag slider
        if (dragging) {
            updateSliderValue(mouseX);
        }

        // Preview description
        String desc;
        if (strength <= 2) desc = "Subtle trail effect";
        else if (strength <= 5) desc = "Moderate motion blur";
        else if (strength <= 8) desc = "Heavy blur trails";
        else desc = "Maximum smear effect";
        drawCenteredString(fontRendererObj, "\u00a78" + desc, guiLeft + guiWidth / 2, guiTop + sliderY - guiTop + sliderH + 22, 0x888888);

        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.enableDepth();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int sliderX = guiLeft + 20;
        int sliderY = guiTop + 55;
        int sliderW = guiWidth - 40;
        int sliderH = 12;

        // Check if clicking on slider area
        if (mouseButton == 0 && mouseX >= sliderX && mouseX <= sliderX + sliderW
                && mouseY >= sliderY - 4 && mouseY <= sliderY + sliderH + 4) {
            dragging = true;
            updateSliderValue(mouseX);
            playClickSound();
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        dragging = false;
    }

    private void updateSliderValue(int mouseX) {
        int sliderX = guiLeft + 20;
        int sliderW = guiWidth - 40;

        float ratio = (float) (mouseX - sliderX) / (float) sliderW;
        ratio = Math.max(0.0f, Math.min(1.0f, ratio));

        int newStrength = 1 + Math.round(ratio * 9.0f);
        newStrength = Math.max(1, Math.min(10, newStrength));

        PvPClient.instance.motionBlurStrength = newStrength;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            mc.displayGuiScreen(parentScreen);
        }
    }

    private void playClickSound() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
