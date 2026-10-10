package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HitTracker {

    public static class Entry {
        public LivingEntity entity;
        public long time;
        public Entry(LivingEntity e, long t) { entity = e; time = t; }
    }

    public static final List<Entry> hits = new ArrayList<>();
    public static final long DURATION_MS = 300;

    public static void add(LivingEntity e) {
        for (Entry entry : hits) {
            if (entry.entity == e) {
                entry.time = System.currentTimeMillis();
                return;
            }
        }
        hits.add(new Entry(e, System.currentTimeMillis()));
    }

    public static void update(MinecraftClient client) {
        long now = System.currentTimeMillis();
        hits.removeIf(h -> now - h.time > DURATION_MS || !h.entity.isAlive());

        PlayerEntity player = client.player;
        if (player == null || client.world == null) return;

        // Проверяем, что игрок держит кидаемый предмет
        ItemStack stack = player.getMainHandStack();
        Item item = stack.getItem();
        boolean isThrowable = item instanceof BowItem
                || item instanceof CrossbowItem
                || item instanceof EnderPearlItem
                || item instanceof SnowballItem
                || item instanceof EggItem
                || item instanceof TridentItem
                || item instanceof SplashPotionItem
                || item instanceof LingeringPotionItem
                || item instanceof WindChargeItem
                || item instanceof ExperienceBottleItem
                || item instanceof FishingRodItem;
        if (!isThrowable) return;

        Vec3d eyePos = player.getEyePos();
        Vec3d look = player.getRotationVector();
        double reach = 64.0;
        Vec3d end = eyePos.add(look.multiply(reach));

        Box scan = new Box(eyePos, end).expand(1.0);

        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;

        for (LivingEntity e : client.world.getEntitiesByClass(LivingEntity.class, scan, x -> x != player)) {
            Optional<Vec3d> hit = e.getBoundingBox().expand(0.2).raycast(eyePos, end);
            if (hit.isPresent()) {
                double d = eyePos.squaredDistanceTo(hit.get());
                if (d < bestDist) {
                    bestDist = d;
                    best = e;
                }
            }
        }

        if (best != null) add(best);
    }
}
