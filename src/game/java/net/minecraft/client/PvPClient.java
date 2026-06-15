package net.minecraft.client;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class PvPClient {
    public static final PvPClient instance = new PvPClient();

    public boolean pvp_fullbright = false;
    public boolean pvp_animations17 = true;
    public boolean pvp_toggleSprint = false;
    public boolean pvp_fpsHud = true;

    // HUD Positions & Scaling
    public int fpsX = 5, fpsY = 5;
    public float fpsScale = 1.0F;
    public int scoreboardX = 0, scoreboardY = 0; // Relative to default
    public float scoreboardScale = 1.0F;

    // Music System
    public String currentTrack = "None";
    private boolean isMusicPlaying = false;

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

    public void playMusic(String url) {
        this.currentTrack = url;
        EagRuntime.openLink(url); // Redirects to the music source for browser playback
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