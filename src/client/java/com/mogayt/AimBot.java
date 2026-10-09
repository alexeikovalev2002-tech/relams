package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class AimBot {

    public static boolean enabled = false;
    public static double delayMin = 0.5;
    public static double delayMax = 1.0;
    public static double distance = 4.5;
    public static double rotateSpeed = 8.0;

    private static long lastAttack = 0;
    private static long nextDelay = 500;
    private static final Random rand = new Random();

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;
        if (client.interactionManager == null) return;

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

        Vec3d targetEye = target.getEyePos();
        Vec3d eye = me.getEyePos();
        double dx = targetEye.x - eye.x;
        double dy = targetEye.y - eye.y;
        double dz = targetEye.z - eye.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, horiz));

        float curYaw = me.getYaw();
        float curPitch = me.getPitch();

        float yawDiff = targetYaw - curYaw;
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;
        float pitchDiff = targetPitch - curPitch;

        // Плавный lerp — как в AimMobs
        float factor = (float) (rotateSpeed / 100.0);
        if (factor > 1.0f) factor = 1.0f;
        if (factor < 0.01f) factor = 0.01f;

        float newYaw = curYaw + yawDiff * factor;
        float newPitch = curPitch + pitchDiff * factor;

        me.prevYaw = curYaw;
        me.prevPitch = curPitch;
        me.setYaw(newYaw);
        me.setPitch(newPitch);

        // Строгая проверка: смотрим ли точно на цель
        Vec3d look = me.getRotationVector();
        Vec3d toTarget = targetEye.subtract(eye).normalize();
        double dot = look.dotProduct(toTarget);
        if (dot < 0.995) return; // почти идеальное наведение

        long now = System.currentTimeMillis();
        if (now - lastAttack < nextDelay) return;

        client.interactionManager.attackEntity(me, target);
        me.swingHand(Hand.MAIN_HAND);
        lastAttack = now;

        // Генерируем следующую рандомную задержку
        long minMs = (long) (delayMin * 1000);
        long maxMs = (long) (delayMax * 1000);
        if (maxMs <= minMs) maxMs = minMs + 1;
        nextDelay = minMs + (long) (rand.nextDouble() * (maxMs - minMs));
    }
}
