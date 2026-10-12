package com.mogayt;

import net.minecraft.client.MinecraftClient;

public class Freecam {

    public static boolean enabled = false;

    public static double camX, camY, camZ;
    public static float camYaw, camPitch;

    public static double prevCamX, prevCamY, prevCamZ;
    public static float prevCamYaw, prevCamPitch;

    private static final double SPEED = 1.0;

    public static void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        camX = prevCamX = client.player.getX();
        camY = prevCamY = client.player.getY() + client.player.getStandingEyeHeight();
        camZ = prevCamZ = client.player.getZ();
        camYaw = prevCamYaw = client.player.getYaw();
        camPitch = prevCamPitch = client.player.getPitch();
    }

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null) return;

        prevCamX = camX;
        prevCamY = camY;
        prevCamZ = camZ;
        prevCamYaw = camYaw;
        prevCamPitch = camPitch;

        double forward = 0, strafe = 0, up = 0;
        if (client.options.forwardKey.isPressed()) forward += 1;
        if (client.options.backKey.isPressed()) forward -= 1;
        if (client.options.leftKey.isPressed()) strafe -= 1;
        if (client.options.rightKey.isPressed()) strafe += 1;
        if (client.options.jumpKey.isPressed()) up += 1;
        if (client.options.sneakKey.isPressed()) up -= 1;

        camYaw = client.player.getYaw();
        camPitch = client.player.getPitch();

        double yawRad = Math.toRadians(camYaw);
        double dx = (-Math.sin(yawRad) * forward - Math.cos(yawRad) * strafe) * SPEED;
        double dz = ( Math.cos(yawRad) * forward - Math.sin(yawRad) * strafe) * SPEED;
        double dy = up * SPEED;

        camX += dx;
        camY += dy;
        camZ += dz;

        // НЕ трогаем игрока — он стоит на месте
    }
}
