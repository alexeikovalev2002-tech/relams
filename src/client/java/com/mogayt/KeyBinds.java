package com.mogayt;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {

    public static int espKey = -1;
    public static int chestKey = -1;
    public static int playerKey = -1;
    public static int itemKey = -1;
    public static int aimMobsKey = -1;
    public static int aimBotKey = -1;
    public static int fullbrightKey = -1;
    public static int freecamKey = -1;
    public static int optimizerKey = -1;
    public static int trajectoryKey = -1;

    private static final boolean[] wasPressed = new boolean[10];

    public static void tick(MinecraftClient client) {
        if (client.getWindow() == null) return;
        if (client.currentScreen != null) {
            for (int i = 0; i < 10; i++) wasPressed[i] = true;
            return;
        }
        long h = client.getWindow().getHandle();

        if (check(h, 0, espKey)) BlockEspMod.espEnabled = !BlockEspMod.espEnabled;
        if (check(h, 1, chestKey)) BlockEspMod.chestEspEnabled = !BlockEspMod.chestEspEnabled;
        if (check(h, 2, playerKey)) BlockEspMod.playerEspEnabled = !BlockEspMod.playerEspEnabled;
        if (check(h, 3, itemKey)) BlockEspMod.itemEspEnabled = !BlockEspMod.itemEspEnabled;
        if (check(h, 4, aimMobsKey)) AimMobs.enabled = !AimMobs.enabled;
        if (check(h, 5, aimBotKey)) AimBot.enabled = !AimBot.enabled;
        if (check(h, 6, fullbrightKey)) Fullbright.enabled = !Fullbright.enabled;
        if (check(h, 7, freecamKey)) {
            Freecam.enabled = !Freecam.enabled;
            if (Freecam.enabled) Freecam.onEnable();
        }
        if (check(h, 8, optimizerKey)) Optimizer.enabled = !Optimizer.enabled;
        if (check(h, 9, trajectoryKey)) TrajectoryPredictor.enabled = !TrajectoryPredictor.enabled;
    }

    private static boolean check(long h, int idx, int key) {
        if (key < 0) { wasPressed[idx] = false; return false; }
        boolean p = GLFW.glfwGetKey(h, key) == GLFW.GLFW_PRESS;
        boolean fire = p && !wasPressed[idx];
        wasPressed[idx] = p;
        return fire;
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
