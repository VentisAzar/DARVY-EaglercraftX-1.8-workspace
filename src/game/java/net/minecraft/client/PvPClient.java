package net.minecraft.client;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
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
    public boolean pvp_clockHud = true;
    public boolean pvp_calendarHud = true;
    public boolean pvp_cooldownsHud = true;
    public boolean pvp_infoPills = true;
    public boolean pvp_customHotbar = true;
    public boolean pvp_chatMacros = true;
    public boolean pvp_audioCues = true;

    // Visuals & Customization Modules
    public boolean pvp_fullbright = false;
    public boolean pvp_blockOutline = true;

    // HUD Positions & Scaling
    public int fpsX = 5, fpsY = 5;
    public float fpsScale = 1.0F;

    public int cpsX = 5, cpsY = 22;
    public float cpsScale = 1.0F;

    public int pingX = 5, pingY = 39;
    public float pingScale = 1.0F;

    public int clockX = 5, clockY = 56;
    public float clockScale = 1.0F;

    public int coordsX = 5, coordsY = 73;
    public float coordsScale = 1.0F;
    
    public int keystrokesX = 5, keystrokesY = 95;
    public float keystrokesScale = 1.0F;
    
    public int armorX = 5, armorY = 180;
    public float armorScale = 1.0F;
    
    public int potionX = 5, potionY = 260;
    public float potionScale = 1.0F;

    public int calendarX = 400, calendarY = 5;
    public float calendarScale = 1.0F;

    public int cooldownsX = 200, cooldownsY = 200;
    public float cooldownsScale = 1.0F;

    public int scoreboardX = 0, scoreboardY = 0;
    public float scoreboardScale = 1.0F;

    // CPS Tracker State
    private final List<Long> leftClicks = new ArrayList<>();
    private final List<Long> rightClicks = new ArrayList<>();

    // Cooldown Timers
    public long lastPearlUseTime = 0L;
    public static final long PEARL_COOLDOWN_MS = 16000L;
    public boolean pearlReadySoundPlayed = true;

    public long lastGappleUseTime = 0L;
    public static final long GAPPLE_DURATION_MS = 120000L; // 2 minutes

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

        // Enderpearl Cooldown Audio Cue
        if (pvp_audioCues && !pearlReadySoundPlayed) {
            long elapsed = System.currentTimeMillis() - lastPearlUseTime;
            if (elapsed >= PEARL_COOLDOWN_MS) {
                pearlReadySoundPlayed = true;
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.orb"), 1.3F));
            }
        }
    }

    public void registerEnderPearlUse() {
        this.lastPearlUseTime = System.currentTimeMillis();
        this.pearlReadySoundPlayed = false;
    }

    public float getPearlCooldownProgress() {
        if (lastPearlUseTime == 0L) return 0.0F;
        long elapsed = System.currentTimeMillis() - lastPearlUseTime;
        if (elapsed >= PEARL_COOLDOWN_MS) return 0.0F;
        return 1.0F - ((float) elapsed / (float) PEARL_COOLDOWN_MS);
    }

    public float getPearlRemainingSeconds() {
        if (lastPearlUseTime == 0L) return 0.0F;
        long elapsed = System.currentTimeMillis() - lastPearlUseTime;
        if (elapsed >= PEARL_COOLDOWN_MS) return 0.0F;
        return (float) (PEARL_COOLDOWN_MS - elapsed) / 1000.0F;
    }

    public void registerGappleUse() {
        this.lastGappleUseTime = System.currentTimeMillis();
    }

    public float getGappleRemainingSeconds() {
        if (lastGappleUseTime == 0L) return 0.0F;
        long elapsed = System.currentTimeMillis() - lastGappleUseTime;
        if (elapsed >= GAPPLE_DURATION_MS) return 0.0F;
        return (float) (GAPPLE_DURATION_MS - elapsed) / 1000.0F;
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
            if (info == null) {
                info = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getName());
            }
            if (info != null) {
                return Math.max(0, info.getResponseTime());
            }
        }
        return 0;
    }

    public void resetDefaultPositions() {
        this.fpsX = 5; this.fpsY = 5; this.fpsScale = 1.0F;
        this.cpsX = 5; this.cpsY = 22; this.cpsScale = 1.0F;
        this.pingX = 5; this.pingY = 39; this.pingScale = 1.0F;
        this.clockX = 5; this.clockY = 56; this.clockScale = 1.0F;
        this.coordsX = 5; this.coordsY = 73; this.coordsScale = 1.0F;
        this.keystrokesX = 5; this.keystrokesY = 95; this.keystrokesScale = 1.0F;
        this.armorX = 5; this.armorY = 180; this.armorScale = 1.0F;
        this.potionX = 5; this.potionY = 260; this.potionScale = 1.0F;
        this.calendarX = 350; this.calendarY = 5; this.calendarScale = 1.0F;
        this.cooldownsX = 200; this.cooldownsY = 200; this.cooldownsScale = 1.0F;
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
}