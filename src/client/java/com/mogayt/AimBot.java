package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class AimBot {

    public static boolean enabled = false;
    public static double distance = 4.5;

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;

        PlayerEntity me = client.player;
        double maxSq = distance * distance;
        PlayerEntity target = null;
        double bestSq = Double.MAX_VALUE;
        for (PlayerEntity p : client.world.getPlayers()) {
            if (p == me || !p.isAlive()) continue;
            double dSq = me.squaredDistanceTo(p);
            if (dSq > maxSq) continue;
            if (dSq < bestSq) { bestSq = dSq; target = p; }
        }
        if (target == null) return;

        Vec3d eye = me.getEyePos();
        Vec3d targetEye = target.getEyePos();
        double dx = targetEye.x - eye.x;
        double dz = targetEye.z - eye.z;

        // Yaw (только по оси X — право/лево)
        float targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));

        me.prevYaw = me.getYaw();
        me.setYaw(targetYaw);
        // Pitch (ось Y — вверх/вниз) НЕ ТРОГАЕМ
    }
}
