package net.minecraft.client;

import net.minecraft.client.network.NetworkPlayerInfo;
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

    // PvP Combat Modules
    public boolean pvp_toggleSprint = false;
    public boolean pvp_animations17 = true;
    public boolean pvp_noHurtCam = false;
    public boolean pvp_lowFire = true;

    // HUD Display Modules
    public boolean pvp_fpsHud = true;
    public boolean pvp_cpsHud = true;
    public boolean pvp_pingHud = true;
    public boolean pvp_keystrokesHud = true;
    public boolean pvp_armorStatus = true;
    public boolean pvp_potionHud = true;

    // Visuals & Customization Modules
    public boolean pvp_fullbright = false;
    public boolean pvp_customCrosshair = false;
    public boolean pvp_blockOutline = true;
    public boolean pvp_customFont = false;

    // HUD Positions & Scaling
    public int fpsX = 5, fpsY = 5;
    public float fpsScale = 1.0F;

    public int cpsX = 5, cpsY = 20;
    public float cpsScale = 1.0F;

    public int pingX = 5, pingY = 35;
    public float pingScale = 1.0F;
    
    public int keystrokesX = 5, keystrokesY = 55;
    public float keystrokesScale = 1.0F;
    
    public int armorX = 5, armorY = 145;
    public float armorScale = 1.0F;
    
    public int potionX = 5, potionY = 210;
    public float potionScale = 1.0F;

    public int scoreboardX = 0, scoreboardY = 0;
    public float scoreboardScale = 1.0F;

    // CPS Tracker State
    private final List<Long> leftClicks = new ArrayList<>();
    private final List<Long> rightClicks = new ArrayList<>();

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

    public int getPing() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.getNetHandler() != null) {
            NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
            if (info != null) {
                return Math.max(0, info.getResponseTime());
            }
        }
        return 0;
    }

    public void resetDefaultPositions() {
        this.fpsX = 5; this.fpsY = 5; this.fpsScale = 1.0F;
        this.cpsX = 5; this.cpsY = 20; this.cpsScale = 1.0F;
        this.pingX = 5; this.pingY = 35; this.pingScale = 1.0F;
        this.keystrokesX = 5; this.keystrokesY = 55; this.keystrokesScale = 1.0F;
        this.armorX = 5; this.armorY = 145; this.armorScale = 1.0F;
        this.potionX = 5; this.potionY = 210; this.potionScale = 1.0F;
        this.scoreboardX = 0; this.scoreboardY = 0; this.scoreboardScale = 1.0F;
    }

    public void playMusic(String url) {
        if (url != null && !url.isEmpty()) {
            EagRuntime.openLink(url);
        }
    }

    public void toggleFullbright() { this.pvp_fullbright = !this.pvp_fullbright; }
    public void toggleAnimations17() { this.pvp_animations17 = !this.pvp_animations17; }
    public void toggleSprint() { this.pvp_toggleSprint = !this.pvp_toggleSprint; }
    public void toggleNoHurtCam() { this.pvp_noHurtCam = !this.pvp_noHurtCam; }
    public void toggleLowFire() { this.pvp_lowFire = !this.pvp_lowFire; }
    public void toggleCustomFont() { this.pvp_customFont = !this.pvp_customFont; }
}