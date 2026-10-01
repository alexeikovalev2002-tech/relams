package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class Freecam {

    public static boolean enabled = false;

    // Позиция камеры в свободном полёте
    public static double camX, camY, camZ;
    public static float camYaw, camPitch;

    // Скорость полёта
    private static final double SPEED = 0.5;

    public static void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        camX = client.player.getX();
        camY = client.player.getY() + client.player.getStandingEyeHeight();
        camZ = client.player.getZ();
        camYaw = client.player.getYaw();
        camPitch = client.player.getPitch();
    }

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.currentScreen != null) return;

        // Движение по WASD
        double forward = 0, strafe = 0, up = 0;
        if (client.options.forwardKey.isPressed()) forward += 1;
        if (client.options.backKey.isPressed()) forward -= 1;
        if (client.options.leftKey.isPressed()) strafe -= 1;
        if (client.options.rightKey.isPressed()) strafe += 1;
        if (client.options.jumpKey.isPressed()) up += 1;
        if (client.options.sneakKey.isPressed()) up -= 1;

        double yawRad = Math.toRadians(camYaw);
        double dx = (-Math.sin(yawRad) * forward + Math.cos(yawRad) * strafe) * SPEED;
        double dz = (Math.cos(yawRad) * forward + Math.sin(yawRad) * strafe) * SPEED;
        double dy = up * SPEED;

        camX += dx;
        camY += dy;
        camZ += dz;

        // Поворот камеры мышью
        camYaw = client.player.getYaw();
        camPitch = client.player.getPitch();
    }
}
