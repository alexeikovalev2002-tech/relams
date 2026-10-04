package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class AimMobs {

    public static boolean enabled = false;
    public static double aimSpeed = 5.0;
    public static double aimDistance = 16.0;
    public static double fovAngle = 15.0;

    public static void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) return;

        PlayerEntity player = client.player;
        Box box = player.getBoundingBox().expand(aimDistance);

        LivingEntity target = null;
        double minDist = Double.MAX_VALUE;

        for (LivingEntity living : client.world.getEntitiesByClass(LivingEntity.class, box, e -> e != player)) {
            if (!living.isAlive()) continue;

            double dist = player.distanceTo(living);
            if (dist > aimDistance) continue;

            Vec3d look = player.getRotationVector();
            Vec3d toTarget = living.getPos()
                    .add(0, living.getHeight() / 2.0, 0)
                    .subtract(player.getEyePos())
                    .normalize();
            double dot = look.dotProduct(toTarget);
            dot = Math.max(-1.0, Math.min(1.0, dot));
            double angle = Math.toDegrees(Math.acos(dot));

            if (angle > fovAngle) continue;

            if (dist < minDist) {
                minDist = dist;
                target = living;
            }
        }

        if (target == null) return;

        Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2.0, 0);
        Vec3d eyePos = player.getEyePos();
        double dx = targetPos.x - eyePos.x;
        double dy = targetPos.y - eyePos.y;
        double dz = targetPos.z - eyePos.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));

        float curYaw = player.getYaw();
        float curPitch = player.getPitch();

        float yawDiff = targetYaw - curYaw;
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;

        float pitchDiff = targetPitch - curPitch;
        float step = (float) aimSpeed;
        float yawStep = Math.max(-step, Math.min(step, yawDiff));
        float pitchStep = Math.max(-step, Math.min(step, pitchDiff));

        player.setYaw(curYaw + yawStep);
        player.setPitch(curPitch + pitchStep);
    }
}
