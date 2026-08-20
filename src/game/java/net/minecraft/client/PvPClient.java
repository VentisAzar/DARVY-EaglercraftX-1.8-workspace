package net.minecraft.client;

import net.minecraft.client.settings.KeyBinding;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import java.util.ArrayList;
import java.util.List;

public class PvPClient {
    public static final PvPClient instance = new PvPClient();

    // Optimizations Toggles (Delegated to SodiumOptimizer)
    public boolean sodiumChunkRebuilding = true;
    public boolean entityFrustumCulling = true;
    public boolean fastMath = true;

    // PvP Core Modules
    public boolean pvp_fullbright = false;
    public boolean pvp_animations17 = true;
    public boolean pvp_toggleSprint = false;
    public boolean pvp_noHurtCam = false;
    public boolean pvp_lowFire = true;

    // HUD Display Modules
    public boolean pvp_fpsHud = true;
    public boolean pvp_cpsHud = true;
    public boolean pvp_keystrokesHud = true;
    public boolean pvp_armorStatus = true;
    public boolean pvp_potionHud = true;
    public boolean pvp_customCrosshair = false;
    public boolean pvp_blockOutline = true;
    public boolean pvp_customFont = false;

    // HUD Positions & Scaling
    public int fpsX = 5, fpsY = 5;
    public float fpsScale = 1.0F;
    
    public int keystrokesX = 5, keystrokesY = 40;
    public float keystrokesScale = 1.0F;
    
    public int armorX = 5, armorY = 130;
    public float armorScale = 1.0F;
    
    public int potionX = 5, potionY = 195;
    public float potionScale = 1.0F;

    public int scoreboardX = 0, scoreboardY = 0;
    public float scoreboardScale = 1.0F;

    // CPS Tracker State
    private final List<Long> leftClicks = new ArrayList<>();
    private final List<Long> rightClicks = new ArrayList<>();

    // Music System
    public String currentTrack = "None";

    public void onTick() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        // Fullbright Logic
        if (this.pvp_fullbright) {
            mc.gameSettings.gammaSetting = 100.0F;
        } else if (mc.gameSettings.gammaSetting > 1.0F) {
            mc.gameSettings.gammaSetting = 1.0F;
        }

        // Toggle Sprint Logic
        if (this.pvp_toggleSprint) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        }
    }

    public void registerLeftClick() {
        this.leftClicks.add(System.currentTimeMillis());
    }

    public void registerRightClick() {
        this.rightClicks.add(System.currentTimeMillis());
    }

    public int getLeftCPS() {
        long time = System.currentTimeMillis();
        this.leftClicks.removeIf(clickTime -> clickTime < time - 1000L);
        return this.leftClicks.size();
    }

    public int getRightCPS() {
        long time = System.currentTimeMillis();
        this.rightClicks.removeIf(clickTime -> clickTime < time - 1000L);
        return this.rightClicks.size();
    }

    public void playMusic(String url) {
        this.currentTrack = url;
        EagRuntime.openLink(url);
    }

    public void toggleFullbright() { this.pvp_fullbright = !this.pvp_fullbright; }
    public void toggleAnimations17() { this.pvp_animations17 = !this.pvp_animations17; }
    public void toggleSprint() { this.pvp_toggleSprint = !this.pvp_toggleSprint; }
    public void toggleNoHurtCam() { this.pvp_noHurtCam = !this.pvp_noHurtCam; }
    public void toggleLowFire() { this.pvp_lowFire = !this.pvp_lowFire; }
    public void toggleCustomFont() { this.pvp_customFont = !this.pvp_customFont; }
}