package com.mogayt;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ChatBind {

    public static String command = "/warp pvp";
    private static KeyBinding sendKey;

    public static void register() {
        sendKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mog-mod.chatbind",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.mog-mod.keys"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (sendKey.wasPressed()) {
                sendCommand(client);
            }
        });
    }

    private static void sendCommand(MinecraftClient client) {
        if (client.player == null || client.player.networkHandler == null) return;
        if (command == null) return;
        String cmd = command.trim();
        if (cmd.isEmpty()) return;
        if (cmd.startsWith("/")) cmd = cmd.substring(1);
        client.player.networkHandler.sendChatCommand(cmd);
    }
}
