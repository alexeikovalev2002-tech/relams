package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AimBot {

    public static boolean enabled = false;
    public static double distance = 4.5;
    public static double smoothSpeed = 25.0;

    public static boolean targetPlayers = true;
    public static boolean targetMobs = false;

    public static void tick(MinecraftClient client) {
        if (client == null || !enabled || client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;

        PlayerEntity me = client.player;
        double maxSq = distance * distance;
        LivingEntity target = null;
        double bestSq = maxSq;

        Box searchBox = me.getBoundingBox().expand(distance);

        for (LivingEntity entity : client.world.getEntitiesByClass(LivingEntity.class, searchBox, e -> e != null && e != me && e.isAlive() && !e.isRemoved())) {
            if (entity instanceof PlayerEntity && !targetPlayers) continue;
            if (entity instanceof MobEntity && !targetMobs) continue;
            if (!(entity instanceof PlayerEntity) && !(entity instanceof MobEntity)) continue;

            double dSq = me.squaredDistanceTo(entity);
            if (dSq <= bestSq) {
                bestSq = dSq;
                target = entity;
            }
        }

        if (target == null) return;

        Vec3d eye = me.getEyePos();
        Vec3d targetPos = target.getBoundingBox().getCenter();
        double dx = targetPos.x - eye.x;
        double dy = targetPos.y - eye.y;
        double dz = targetPos.z - eye.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float targetPitch = MathHelper.clamp((float) -Math.toDegrees(Math.atan2(dy, horizontal)), -90.0f, 90.0f);

        float yawDiff = MathHelper.wrapDegrees(targetYaw - me.getYaw());
        float pitchDiff = MathHelper.wrapDegrees(targetPitch - me.getPitch());
        float factor = MathHelper.clamp((float) (smoothSpeed / 100.0), 0.01f, 1.0f);

        me.prevYaw = me.getYaw();
        me.prevPitch = me.getPitch();
        me.setYaw(me.getYaw() + yawDiff * factor);
        me.setPitch(MathHelper.clamp(me.getPitch() + pitchDiff * factor, -90.0f, 90.0f));
    }
}
