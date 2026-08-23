package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.internal.KeyboardConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

/**
 * Chat Macro System for DARVY Client.
 * Allows binding hotkeys to frequently used server commands or messages.
 */
public class ChatMacroManager {

    public static class MacroEntry {
        public int keyCode;
        public String keyName;
        public String command;
        public String label;

        public MacroEntry(int keyCode, String keyName, String label, String command) {
            this.keyCode = keyCode;
            this.keyName = keyName;
            this.label = label;
            this.command = command;
        }
    }

    public static final ChatMacroManager instance = new ChatMacroManager();
    public final List<MacroEntry> macros = new ArrayList<>();
    public boolean enabled = true;

    private ChatMacroManager() {
        initDefaultMacros();
    }

    public void initDefaultMacros() {
        macros.clear();
        // Customizable defaults (user can modify or add their own)
        macros.add(new MacroEntry(KeyboardConstants.KEY_H, "H", "Spawn / Hub", "/spawn"));
        macros.add(new MacroEntry(KeyboardConstants.KEY_J, "J", "Lobby Quick Warp", "/lobby"));
        macros.add(new MacroEntry(KeyboardConstants.KEY_K, "K", "Party Chat Toggle", "/p chat"));
        macros.add(new MacroEntry(KeyboardConstants.KEY_L, "L", "Toggle Fly / Flight", "/fly"));
    }

    public boolean handleKeyPress(int keyCode) {
        if (!enabled) return false;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.currentScreen != null) return false;

        for (MacroEntry macro : macros) {
            if (macro.keyCode == keyCode && macro.command != null && !macro.command.isEmpty()) {
                mc.thePlayer.sendChatMessage(macro.command);
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.2F));
                return true;
            }
        }
        return false;
    }

    public void setMacro(int index, int keyCode, String keyName, String label, String command) {
        if (index >= 0 && index < macros.size()) {
            macros.set(index, new MacroEntry(keyCode, keyName, label, command));
        } else {
            macros.add(new MacroEntry(keyCode, keyName, label, command));
        }
    }
}
