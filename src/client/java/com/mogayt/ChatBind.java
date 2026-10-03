package com.mogayt;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ChatBind {

    public static class Bind {
        public String command;
        public int keyCode;
        public boolean wasPressed;
        public Bind(String command, int keyCode) {
            this.command = command;
            this.keyCode = keyCode;
        }
    }

    public static final List<Bind> binds = new ArrayList<>();

    public static void register() {
        // 4 бинда по умолчанию
        binds.add(new Bind("/warp pvp",  GLFW.GLFW_KEY_R));
        binds.add(new Bind("/warp mine", GLFW.GLFW_KEY_T));
        binds.add(new Bind("/rtp",       GLFW.GLFW_KEY_Y));
        binds.add(new Bind("/spawn",     GLFW.GLFW_KEY_U));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            if (client.currentScreen != null) {
                // GUI открыт — сбрасываем состояния
                for (Bind b : binds) b.wasPressed = true;
                return;
            }
            if (client.getWindow() == null) return;

            long handle = client.getWindow().getHandle();

            for (Bind b : binds) {
                if (b.keyCode < 0) continue;
                boolean pressed = GLFW.glfwGetKey(handle, b.keyCode) == GLFW.GLFW_PRESS;
                if (pressed && !b.wasPressed) {
                    sendCommand(client, b.command);
                }
                b.wasPressed = pressed;
            }
        });
    }

    private static void sendCommand(MinecraftClient client, String command) {
        if (command == null || client.player == null || client.player.networkHandler == null) return;
        String cmd = command.trim();
        if (cmd.isEmpty()) return;
        if (cmd.startsWith("/")) cmd = cmd.substring(1);
        client.player.networkHandler.sendChatCommand(cmd);
    }

    public static int letterToKey(String s) {
        if (s == null || s.isEmpty()) return -1;
        char c = Character.toUpperCase(s.charAt(0));
        if (c >= 'A' && c <= 'Z') return GLFW.GLFW_KEY_A + (c - 'A');
        if (c >= '0' && c <= '9') return GLFW.GLFW_KEY_0 + (c - '0');
        return -1;
    }

    public static String keyToLetter(int key) {
        if (key >= GLFW.GLFW_KEY_A && key <= GLFW.GLFW_KEY_Z)
            return String.valueOf((char) ('A' + (key - GLFW.GLFW_KEY_A)));
        if (key >= GLFW.GLFW_KEY_0 && key <= GLFW.GLFW_KEY_9)
            return String.valueOf((char) ('0' + (key - GLFW.GLFW_KEY_0)));
        return "";
    }
}
