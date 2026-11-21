package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CrystalAura {

    public static boolean enabled = false;
    public static double range = 4.5;
    public static double aimSpeed = 15.0;

    private static long lastAttack = 0;

    public static void tick(MinecraftClient client) {
        if (client == null || !enabled || client.player == null || client.world == null) return;
        if (client.interactionManager == null || client.currentScreen != null) return;

        PlayerEntity me = client.player;
        EndCrystalEntity target = null;
        double bestDistSq = range * range;

        Box searchBox = me.getBoundingBox().expand(range);
        for (EndCrystalEntity crystal : client.world.getEntitiesByClass(EndCrystalEntity.class, searchBox, e -> e != null && e.isAlive() && !e.isRemoved())) {
            if (me.getY() > crystal.getY() + 0.5) continue;

            double distSq = me.squaredDistanceTo(crystal);
            if (distSq <= bestDistSq) {
                bestDistSq = distSq;
                target = crystal;
            }
        }

        if (target == null) return;

        Vec3d eyePos = me.getEyePos();
        Vec3d targetPos = target.getPos().add(0.0, target.getHeight() * 0.5, 0.0);

        double dx = targetPos.x - eyePos.x;
        double dy = targetPos.y - eyePos.y;
        double dz = targetPos.z - eyePos.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float targetPitch = MathHelper.clamp((float) -Math.toDegrees(Math.atan2(dy, horizontal)), -90.0f, 90.0f);

        float yawDiff = MathHelper.wrapDegrees(targetYaw - me.getYaw());
        float pitchDiff = MathHelper.wrapDegrees(targetPitch - me.getPitch());

        float factor = MathHelper.clamp((float) (aimSpeed / 100.0), 0.01f, 1.0f);

        me.prevYaw = me.getYaw();
        me.prevPitch = me.getPitch();
        me.setYaw(me.getYaw() + yawDiff * factor);
        me.setPitch(MathHelper.clamp(me.getPitch() + pitchDiff * factor, -90.0f, 90.0f));

        Vec3d lookVec = me.getRotationVector();
        Vec3d endPos = eyePos.add(lookVec.multiply(range + 1.0));

        if (target.getBoundingBox().raycast(eyePos, endPos).isPresent()) {
            long now = System.currentTimeMillis();
            if (now - lastAttack >= 100L) {
                client.interactionManager.attackEntity(me, target);
                me.swingHand(Hand.MAIN_HAND);
                lastAttack = now;
            }
        }
    }
}
