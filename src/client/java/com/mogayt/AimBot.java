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

    private static long lastAttack = 0;
    private static long nextDelay = 500;
    private static final Random rand = new Random();

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;
        if (client.interactionManager == null) return;

        PlayerEntity me = client.player;

        // Находим ближайшего игрока в радиусе
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

        // МГНОВЕННО ставим камеру на голову цели (без плавности)
        Vec3d targetEye = target.getEyePos();
        Vec3d eye = me.getEyePos();
        double dx = targetEye.x - eye.x;
        double dy = targetEye.y - eye.y;
        double dz = targetEye.z - eye.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, horiz));

        me.prevYaw = me.getYaw();
        me.prevPitch = me.getPitch();
        me.setYaw(targetYaw);
        me.setPitch(targetPitch);

        // АТАКА только при падении
        if (me.isOnGround()) return;
        if (me.getVelocity().y >= -0.01) return;

        long now = System.currentTimeMillis();
        if (now - lastAttack < nextDelay) return;

        client.interactionManager.attackEntity(me, target);
        me.swingHand(Hand.MAIN_HAND);
        lastAttack = now;

        long minMs = (long) (delayMin * 1000);
        long maxMs = (long) (delayMax * 1000);
        if (maxMs <= minMs) maxMs = minMs + 1;
        nextDelay = minMs + (long) (rand.nextDouble() * (maxMs - minMs));
    }
}
