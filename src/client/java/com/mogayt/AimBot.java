package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class AimBot {

    public static boolean enabled = false;
    public static double delay = 0.5;
    public static double distance = 4.5;
    public static double rotateSpeed = 25.0;

    private static long lastAttack = 0;

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

        float step = (float) rotateSpeed;
        float yawStep = Math.max(-step, Math.min(step, yawDiff));
        float pitchStep = Math.max(-step, Math.min(step, pitchDiff));

        me.prevYaw = curYaw;
        me.prevPitch = curPitch;
        me.setYaw(curYaw + yawStep);
        me.setPitch(curPitch + pitchStep);

        Vec3d look = me.getRotationVector();
        Vec3d toTarget = targetEye.subtract(eye).normalize();
        if (look.dotProduct(toTarget) < 0.95) return;

        long now = System.currentTimeMillis();
        if (now - lastAttack < (long)(delay * 1000)) return;

        client.interactionManager.attackEntity(me, target);
        me.swingHand(Hand.MAIN_HAND);
        lastAttack = now;
    }
}
