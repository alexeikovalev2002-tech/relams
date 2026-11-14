package com.mogayt;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HudRenderer {

    // Цветовая схема (можно менять)
    private static final int ACCENT_COLOR = new Color(120, 80, 255).getRGB(); // Фиолетовый акцент
    private static final int BG_COLOR = new Color(15, 15, 20, 180).getRGB();   // Тёмный полупрозрачный фон
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;
            if (client.options.hudHidden) return; // Скрыто по F1

            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();
            int yOffset = 10;

            // 1. ВОДЯНОЙ ЗНАК (Watermark)
            String watermark = "GALOCHKA CLIENT";
            // Градиентный текст (условно)
            drawContext.drawTextWithShadow(client.textRenderer, Text.literal(watermark), 10, yOffset, 0xFFAA00);
            yOffset += 12;

            // 2. ИНФО-ПАНЕЛЬ (FPS, Ping, Coords)
            drawInfoPanel(drawContext, client, 10, yOffset);
            yOffset += 45;

            // 3. TARGET HUD (Информация о цели)
            if (client.crosshairTarget != null && client.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY) {
                if (client.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult entityHit) {
                    if (entityHit.getEntity() instanceof LivingEntity livingTarget && livingTarget != client.player) {
                        drawTargetHud(drawContext, client, livingTarget, 10, yOffset);
                    }
                }
            }

            // 4. ARRAYLIST (Список активных модулей)
            drawArrayList(drawContext, client, screenWidth, screenHeight);
        });
    }

    private static void drawInfoPanel(DrawContext context, MinecraftClient client, int x, int y) {
        int w = 100;
        int h = 40;
        // Фон панели с закруглением (имитация через несколько прямоугольников)
        context.fill(x, y, x + w, y + h, BG_COLOR);
        context.fill(x, y, x + 2, y + h, ACCENT_COLOR); // Акцентная полоска слева

        // FPS
        String fpsText = "FPS: " + client.getCurrentFps();
        context.drawTextWithShadow(client.textRenderer, Text.literal(fpsText), x + 6, y + 4, TEXT_COLOR);

        // Ping
        int ping = 0;
        if (client.getNetworkHandler() != null && client.player != null) {
            var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            if (entry != null) ping = entry.getLatency();
        }
        String pingText = "Ping: " + ping;
        context.drawTextWithShadow(client.textRenderer, Text.literal(pingText), x + 6, y + 15, TEXT_COLOR);

        // Координаты
        String coords = String.format("X: %d  Y: %d  Z: %d", 
            client.player.getBlockX(), client.player.getBlockY(), client.player.getBlockZ());
        context.drawTextWithShadow(client.textRenderer, Text.literal(coords), x + 6, y + 26, 0xFFAAAAAA);
    }

    private static void drawTargetHud(DrawContext context, MinecraftClient client, LivingEntity target, int x, int y) {
        int w = 120;
        int h = 35;
        context.fill(x, y, x + w, y + h, BG_COLOR);
        context.fill(x, y, x + 2, y + h, 0xFFFF5555); // Красная полоска для цели

        String name = target.getName().getString();
        context.drawTextWithShadow(client.textRenderer, Text.literal(name), x + 6, y + 4, TEXT_COLOR);

        float hp = target.getHealth();
        float maxHp = target.getMaxHealth();
        String hpText = String.format("%.1f HP", hp);
        context.drawTextWithShadow(client.textRenderer, Text.literal(hpText), x + 6, y + 15, 0xFFFF5555);

        // Полоска здоровья
        int barWidth = 100;
        int barHeight = 3;
        int barX = x + 6;
        int barY = y + 26;
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
        int fillWidth = (int) ((hp / maxHp) * barWidth);
        context.fill(barX, barY, barX + fillWidth, barY + barHeight, 0xFFFF5555);
    }

    private static void drawArrayList(DrawContext context, MinecraftClient client, int screenWidth, int screenHeight) {
        List<Module> activeModules = getActiveModules();
        // Сортируем по длине текста (чтобы было красиво, как в клиентах)
        activeModules.sort(Comparator.comparingInt(m -> -client.textRenderer.getWidth(m.name)));

        int startX = screenWidth - 4;
        int startY = 10;
        int rowHeight = 12;

        for (int i = 0; i < activeModules.size(); i++) {
            Module mod = activeModules.get(i);
            String name = mod.name;
            int textWidth = client.textRenderer.getWidth(name);
            
            // Позиция для выравнивания по правому краю
            int x = startX - textWidth - 4;
            int y = startY + (i * rowHeight);

            // Фон с прозрачностью
            context.fill(x - 2, y, startX, y + rowHeight - 1, new Color(20, 20, 25, 160).getRGB());
            
            // Акцентная полоска слева от модуля
            context.fill(x - 2, y, x, y + rowHeight - 1, mod.color);
            
            // Текст модуля
            context.drawTextWithShadow(client.textRenderer, Text.literal(name), x + 1, y + 1, TEXT_COLOR);
        }
    }

    // Собираем список активных модулей для ArrayList
    private static List<Module> getActiveModules() {
        List<Module> list = new ArrayList<>();
        if (BlockEspMod.espEnabled) list.add(new Module("BlockESP", 0xFF00FFFF));
        if (BlockEspMod.chestEspEnabled) list.add(new Module("ChestESP", 0xFFFFAA00));
        if (BlockEspMod.playerEspEnabled) list.add(new Module("PlayerESP", 0xFFFF5555));
        if (AimBot.enabled) list.add(new Module("AimBot", 0xFFFF00FF));
        if (AimMobs.enabled) list.add(new Module("AimMobs", 0xFFFF8800));
        if (MaceAura.enabled) list.add(new Module("MaceAura", 0xFFFFFF00));
        if (CrystalAura.enabled) list.add(new Module("CrystalAura", 0xFFAA00FF));
        if (AnchorAura.enabled) list.add(new Module("AnchorAura", 0xFF00FF88));
        if (TriggerBot.enabled) list.add(new Module("TriggerBot", 0xFFFF0055));
        if (Fullbright.enabled) list.add(new Module("Fullbright", 0xFFFFFF88));
        if (Freecam.enabled) list.add(new Module("Freecam", 0xFF88FF88));
        return list;
    }

    // Вспомогательный класс для модулей
    private record Module(String name, int color) {}
  }
