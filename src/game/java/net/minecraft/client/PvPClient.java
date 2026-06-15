package net.minecraft.client;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class PvPClient {
    public static final PvPClient instance = new PvPClient();

    public boolean pvp_fullbright = false;
    public boolean pvp_animations17 = true;
    public boolean pvp_toggleSprint = false;

    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        // Fullbright Logic
        if (this.pvp_fullbright) {
            mc.gameSettings.gammaSetting = 100.0F;
        } else if (mc.gameSettings.gammaSetting > 1.0F) {
            mc.gameSettings.gammaSetting = 1.0F;
        }

        // Toggle Sprint Logic (Forced)
        if (this.pvp_toggleSprint) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        }
    }

    /**
     * Toggles the fullbright state.
     */
    public void toggleFullbright() {
        this.pvp_fullbright = !this.pvp_fullbright;
    }

    public void toggleAnimations17() {
        this.pvp_animations17 = !this.pvp_animations17;
    }

    public void toggleSprint() {
        this.pvp_toggleSprint = !this.pvp_toggleSprint;
    }
}