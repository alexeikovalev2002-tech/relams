package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;

public class TrajectoryPredictor {

    public static boolean enabled = false;
    public static float diamondSize = 0.4f;
    public static boolean filled = false;

    public static Vec3d impactPoint = null;
    public static Vec3d impactNormal = new Vec3d(0, 1, 0);

    public static void update(MinecraftClient client) {
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

        for (int i = 0; i < 150; i++) {
            Vec3d nextPos = pos.add(vel);

            BlockHitResult hit = client.world.raycast(new RaycastContext(
                    pos, nextPos,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    player
            ));

            if (hit.getType() == HitResult.Type.BLOCK) {
                impactPoint = hit.getPos();
                Vec3i side = hit.getSide().getVector();
                impactNormal = new Vec3d(side.getX(), side.getY(), side.getZ());
                return;
            }

            pos = nextPos;
            vel = new Vec3d(vel.x * drag, vel.y * drag - gravity, vel.z * drag);

            if (pos.y < client.world.getBottomY() - 5 || pos.y > 400) return;
        }
    }
}
