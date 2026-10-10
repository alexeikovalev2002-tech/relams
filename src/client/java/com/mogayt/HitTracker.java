package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class HitTracker {

    public static class Entry {
        public LivingEntity entity;
        public long time;
        public Entry(LivingEntity e, long t) { entity = e; time = t; }
    }

    public static final List<Entry> hits = new ArrayList<>();
    public static final long DURATION_MS = 500;

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

        if (client.player == null || client.world == null) return;

        for (Entity proj : client.world.getEntities()) {
            if (!(proj instanceof ProjectileEntity)) continue;
            Vec3d pv = proj.getVelocity();
            if (pv.lengthSquared() < 0.01) continue;

            Vec3d pp = proj.getPos();
            Vec3d dir = pv.normalize();
            Vec3d end = pp.add(dir.multiply(12.0));

            Box scanBox = new Box(pp, end).expand(1.0);
            for (LivingEntity e : client.world.getEntitiesByClass(LivingEntity.class, scanBox, x -> x != client.player)) {
                Box eb = e.getBoundingBox().expand(0.3);
                if (eb.raycast(pp, end).isPresent()) {
                    add(e);
                }
            }
        }
    }
}
