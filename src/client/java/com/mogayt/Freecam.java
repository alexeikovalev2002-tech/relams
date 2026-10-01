package com.mogayt;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class Freecam {

    public static boolean enabled = false;

    public static double camX, camY, camZ;
    public static float camYaw, camPitch;

    // Скорость полёта. Хочешь быстрее — увеличь.
    private static final double SPEED = 1.0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(Freecam::tick);
    }

    public static void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        camX = client.player.getX();
        camY = client.player.getY() + client.player.getStandingEyeHeight();
        camZ = client.player.getZ();
        camYaw = client.player.getYaw();
        camPitch = client.player.getPitch();
    }

    private static void tick(MinecraftClient client) {
        if (!enabled || client.player == null) return;

        // Читаем WASD для полёта камеры
        double forward = 0, strafe = 0, up = 0;
        if (client.options.forwardKey.isPressed()) forward += 1;
        if (client.options.backKey.isPressed()) forward -= 1;
        if (client.options.leftKey.isPressed()) strafe -= 1;
        if (client.options.rightKey.isPressed()) strafe += 1;
        if (client.options.jumpKey.isPressed()) up += 1;
        if (client.options.sneakKey.isPressed()) up -= 1;

        // Направление камеры = куда смотрит игрок
        camYaw = client.player.getYaw();
        camPitch = client.player.getPitch();

        double yawRad = Math.toRadians(camYaw);
        double dx = (-Math.sin(yawRad) * forward + Math.cos(yawRad) * strafe) * SPEED;
        double dz = (Math.cos(yawRad) * forward + Math.sin(yawRad) * strafe) * SPEED;
        double dy = up * SPEED;

        camX += dx;
        camY += dy;
        camZ += dz;
    }
}
