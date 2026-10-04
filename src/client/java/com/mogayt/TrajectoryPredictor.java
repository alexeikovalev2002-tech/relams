package com.mogayt;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class TrajectoryPredictor {

    public static boolean enabled = false;
    private static final List<Vec3d> trajectory = new ArrayList<>();
    public static Vec3d impactPoint = null;
    public static Vec3d impactNormal = new Vec3d(0, 1, 0);

    public static final float[] COLOR = {0.2f, 1.0f, 0.2f, 1.0f};

    public static void update(MinecraftClient client) {
        trajectory.clear();
        impactPoint = null;
        impactNormal = new Vec3d(0, 1, 0);
        if (!enabled || client.player == null || client.world == null) return;

        PlayerEntity player = client.player;
        ItemStack stack = player.getMainHandStack();
        Item item = stack.getItem();

        double velocity;
        double gravity;
        double drag = 0.99;

        if (item instanceof EnderPearlItem) { velocity = 1.5; gravity = 0.03; }
        else if (item instanceof BowItem) {
            if (!player.isUsingItem()) return;
            float pull = Math.min(player.getItemUseTime() / 20.0f, 1.0f);
            if (pull < 0.1f) return;
            velocity = pull * 3.0; gravity = 0.05;
        }
        else if (item instanceof CrossbowItem) {
            if (!CrossbowItem.isCharged(stack)) return;
            velocity = 3.15; gravity = 0.05;
        }
        else if (item instanceof SnowballItem || item instanceof EggItem) { velocity = 1.5; gravity = 0.03; }
        else if (item instanceof FishingRodItem) { velocity = 1.5; gravity = 0.03; drag = 0.92; }
        else if (item instanceof TridentItem) {
            if (player.getItemUseTime() < 10) return;
            velocity = 2.5; gravity = 0.05;
        }
        else if (item instanceof WindChargeItem) { velocity = 1.5; gravity = 0.0; }
        else if (item instanceof SplashPotionItem || item instanceof LingeringPotionItem) { velocity = 0.5; gravity = 0.05; }
        else if (item instanceof ExperienceBottleItem) { velocity = 0.7; gravity = 0.07; }
        else return;

        Vec3d pos = new Vec3d(player.getX(), player.getEyeY() - 0.1, player.getZ());
        float yawRad = (float) Math.toRadians(player.getYaw());
        float pitchRad = (float) Math.toRadians(player.getPitch());
        double dx = -Math.sin(yawRad) * Math.cos(pitchRad);
        double dy = -Math.sin(pitchRad);
        double dz = Math.cos(yawRad) * Math.cos(pitchRad);
        Vec3d vel = new Vec3d(dx * velocity, dy * velocity, dz * velocity);

        for (int i = 0; i < 300; i++) {
            trajectory.add(pos);
            Vec3d prevPos = pos;
            pos = pos.add(vel);
            vel = new Vec3d(vel.x * drag, vel.y * drag - gravity, vel.z * drag);

            if (pos.y < client.world.getBottomY() - 5 || pos.y > 400) break;

            BlockPos bp = BlockPos.ofFloored(pos);
            BlockState state = client.world.getBlockState(bp);
            if (!state.isAir()) {
                double px = prevPos.x, py = prevPos.y, pz = prevPos.z;
                double bx1 = bp.getX(), by1 = bp.getY(), bz1 = bp.getZ();
                double bx2 = bx1 + 1, by2 = by1 + 1, bz2 = bz1 + 1;

                double ddx = Math.abs(pos.x - prevPos.x);
                double ddy = Math.abs(pos.y - prevPos.y);
                double ddz = Math.abs(pos.z - prevPos.z);

                if (ddx >= ddy && ddx >= ddz) {
                    if (px < bx1) impactNormal = new Vec3d(-1, 0, 0);
                    else impactNormal = new Vec3d(1, 0, 0);
                } else if (ddy >= ddx && ddy >= ddz) {
                    if (py < by1) impactNormal = new Vec3d(0, -1, 0);
                    else impactNormal = new Vec3d(0, 1, 0);
                } else {
                    if (pz < bz1) impactNormal = new Vec3d(0, 0, -1);
                    else impactNormal = new Vec3d(0, 0, 1);
                }

                double sx, sy, sz;
                if (impactNormal.x != 0) {
                    sx = impactNormal.x > 0 ? bx2 : bx1;
                    sy = pos.y; sz = pos.z;
                } else if (impactNormal.y != 0) {
                    sy = impactNormal.y > 0 ? by2 : by1;
                    sx = pos.x; sz = pos.z;
                } else {
                    sz = impactNormal.z > 0 ? bz2 : bz1;
                    sx = pos.x; sy = pos.y;
                }
                impactPoint = new Vec3d(sx, sy, sz);
                trajectory.add(impactPoint);
                break;
            }
        }
    }

    public static List<Vec3d> getTrajectory() { return trajectory; }
}
