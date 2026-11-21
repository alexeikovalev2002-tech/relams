package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

import java.util.concurrent.ThreadLocalRandom;

public class TriggerBot {

    public static boolean enabled = false;

    // Настройки целей
    public static boolean targetPlayers = true;
    public static boolean targetMobs = false;

    // Настройки атаки
    public static boolean onlyCrits = false;
    public static double delayMin = 0.1;
    public static double delayMax = 0.3;

    private static long lastAttackTime = 0;
    private static long nextAttackDelay = 0;
    private static boolean hasCritThisJump = false;

    public static void tick(MinecraftClient client) {
        if (client == null || !enabled || client.player == null || client.world == null) return;
        if (client.interactionManager == null || client.currentScreen != null) return;

        if (client.player.isOnGround() || client.player.isTouchingWater() || client.player.isClimbing()) {
            hasCritThisJump = false;
        }

        if (onlyCrits) {
            if (hasCritThisJump) return;
            if (client.player.getVelocity().y >= -0.05) return;
            if (client.player.isTouchingWater() || client.player.isClimbing() || client.player.hasVehicle()) return;
            if (client.player.getAttackCooldownProgress(0.5f) < 0.9f) return;
        } else {
            if (client.player.getAttackCooldownProgress(0.5f) < 0.95f) return;
        }

        if (!(client.crosshairTarget instanceof EntityHitResult hitResult)) return;
        if (!(hitResult.getEntity() instanceof LivingEntity target)) return;
        if (target == client.player || !target.isAlive() || target.isRemoved()) return;

        boolean isPlayer = target instanceof PlayerEntity;
        boolean isMob = target instanceof MobEntity;

        if (isPlayer && !targetPlayers) return;
        if (isMob && !targetMobs) return;
        if (!isPlayer && !isMob) return;

        long now = System.currentTimeMillis();
        long currentDelay = onlyCrits ? 0L : nextAttackDelay;

        if (now - lastAttackTime >= currentDelay) {
            boolean wasSprinting = client.player.isSprinting();
            if (onlyCrits && wasSprinting) {
                client.player.setSprinting(false);
            }

            client.interactionManager.attackEntity(client.player, target);
            client.player.swingHand(Hand.MAIN_HAND);

            if (onlyCrits && wasSprinting) {
                client.player.setSprinting(true);
            }

            lastAttackTime = now;

            if (onlyCrits) {
                hasCritThisJump = true;
            } else {
                double min = Math.max(0.0, Math.min(delayMin, delayMax));
                double max = Math.max(min, Math.max(delayMin, delayMax));
                double randomSec = min + (ThreadLocalRandom.current().nextDouble() * (max - min));
                nextAttackDelay = (long) (randomSec * 1000.0);
            }
        }
    }
}
