package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

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
            double dot = Math.max(-1.0, Math.min(1.0, look.dotProduct(toTarget)));
            double angle = Math.toDegrees(Math.acos(dot));
            if (angle > fovAngle) continue;

            Vec3d eyePos = player.getEyePos();
            Vec3d targetEye = living.getPos().add(0, living.getHeight() / 2.0, 0);
            BlockHitResult hit = client.world.raycast(new RaycastContext(
                    eyePos, targetEye,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    player
            ));
            if (hit.getType() == HitResult.Type.BLOCK) continue;

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

        float factor = (float) (aimSpeed / 100.0);
        if (factor > 1.0f) factor = 1.0f;
        if (factor < 0.01f) factor = 0.01f;

        float newYaw = curYaw + yawDiff * factor;
        float newPitch = curPitch + pitchDiff * factor;

        // ВАЖНО: сохраняем старые значения как prev — тогда Minecraft плавно интерполирует
        player.prevYaw = curYaw;
        player.prevPitch = curPitch;
        player.setYaw(newYaw);
        player.setPitch(newPitch);
    }
}
