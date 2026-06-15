package net.minecraft.client;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class PvPClient {
    public static final PvPClient instance = new PvPClient();

    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        // Fullbright Logic
        if (mc.gameSettings.pvp_fullbright) { // Read from GameSettings
            mc.gameSettings.gammaSetting = 100.0F;
        } else if (mc.gameSettings.gammaSetting > 1.0F) {
            mc.gameSettings.gammaSetting = 1.0F;
        }

        // Toggle Sprint Logic (Forced)
        if (mc.gameSettings.pvp_toggleSprint) { // Read from GameSettings
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        }
        // 1.7 Animations logic will be applied where animations are rendered (e.g., ItemRenderer)
    }

    /**
     * Sets the fullbright state and saves it to GameSettings.
     */
    public void setFullbright(boolean state) {
        Minecraft.getMinecraft().gameSettings.pvp_fullbright = state;
        Minecraft.getMinecraft().gameSettings.saveOptions();
    }

    /**
     * Sets the 1.7 animations state and saves it to GameSettings.
     */
    public void setAnimations17(boolean state) {
        Minecraft.getMinecraft().gameSettings.pvp_animations17 = state;
        Minecraft.getMinecraft().gameSettings.saveOptions();
    }

    /**
     * Sets the toggle sprint state and saves it to GameSettings.
     */
    public void setToggleSprint(boolean state) {
        Minecraft.getMinecraft().gameSettings.pvp_toggleSprint = state;
        Minecraft.getMinecraft().gameSettings.saveOptions();
    }
}