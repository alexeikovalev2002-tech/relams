package com.mogayt;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HudRenderer {

    public static double hudX = 5;
    public static double hudY = 5;

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;
            if (client.options.hudHidden) return;

            int screenWidth = client.getWindow().getScaledWidth();
            boolean inMenu = client.currentScreen instanceof EspMenuScreen; // Показываем рамку в нашем меню

            Notification.tick();
            if (!Notification.entries.isEmpty()) {
                int ny = (int) hudY + 45;
                for (Notification.Entry e : Notification.entries) {
                    drawContext.drawTextWithShadow(client.textRenderer, Text.literal(e.text), (int) hudX, ny, 0xFFFFFFFF);
                    ny += 10;
                }
            }

            drawInfoPanel(drawContext, client, (int) hudX, (int) hudY, inMenu);

            if (client.crosshairTarget instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target && target != client.player) {
                drawTargetHud(drawContext, client, target, (int) hudX, (int) hudY + 45);
            }

            drawArrayList(drawContext, client, screenWidth);
        });
    }

    private static void drawInfoPanel(DrawContext context, MinecraftClient client, int x, int y, boolean inMenu) {
        int w = 110;
        int h = 40;
        context.fill(x, y, x + w, y + h, 0x90000000);
        context.fill(x, y, x + 2, y + h, 0xFFAA00FF);

        context.drawTextWithShadow(client.textRenderer, Text.literal("FPS: " + client.getCurrentFps()), x + 6, y + 4, 0xFFFFFFFF);

        int ping = 0;
        if (client.getNetworkHandler() != null) {
            var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            if (entry != null) ping = entry.getLatency();
        }
        context.drawTextWithShadow(client.textRenderer, Text.literal("Ping: " + ping), x + 6, y + 15, 0xFFFFFFFF);

        String coords = String.format("X:%d Y:%d Z:%d", client.player.getBlockX(), client.player.getBlockY(), client.player.getBlockZ());
        context.drawTextWithShadow(client.textRenderer, Text.literal(coords), x + 6, y + 26, 0xFFAAAAAA);

        // Если открыто меню чита, показываем желтую рамку
        if (inMenu) {
            context.drawBorder(x - 1, y - 1, w + 2, h + 2, 0xFFFFFF00);
        }
    }

    private static void drawTargetHud(DrawContext context, MinecraftClient client, LivingEntity target, int x, int y) {
        int w = 120;
        int h = 35;
        context.fill(x, y, x + w, y + h, 0x90000000);
        context.fill(x, y, x + 2, y + h, 0xFFFF0000);
        context.drawTextWithShadow(client.textRenderer, Text.literal(target.getName().getString()), x + 6, y + 4, 0xFFFFFFFF);

        float hp = target.getHealth();
        float maxHp = target.getMaxHealth();
        context.drawTextWithShadow(client.textRenderer, Text.literal(String.format("%.1f HP", hp)), x + 6, y + 15, 0xFFFF5555);

        int barW = 108;
        context.fill(x + 6, y + 26, x + 6 + barW, y + 29, 0xFF333333);
        context.fill(x + 6, y + 26, x + 6 + (int) ((hp / maxHp) * barW), y + 29, 0xFFFF0000);
    }

    private static void drawArrayList(DrawContext context, MinecraftClient client, int screenWidth) {
        List<Module> active = new ArrayList<>();
        if (BlockEspMod.espEnabled) active.add(new Module("BlockESP", 0xFF00FFFF));
        if (BlockEspMod.chestEspEnabled) active.add(new Module("ChestESP", 0xFFFFAA00));
        if (BlockEspMod.playerEspEnabled) active.add(new Module("PlayerESP", 0xFFFF5555));
        if (AimBot.enabled) active.add(new Module("AimBot", 0xFFFF00FF));
        if (AimMobs.enabled) active.add(new Module("AimMobs", 0xFFFF8800));
        if (MaceAura.enabled) active.add(new Module("MaceAura", 0xFFFFFF00));
        if (CrystalAura.enabled) active.add(new Module("CrystalAura", 0xFFAA00FF));
        if (AnchorAura.enabled) active.add(new Module("AnchorAura", 0xFF00FF88));
        if (TriggerBot.enabled) active.add(new Module("TriggerBot", 0xFFFF0055));
        if (Fullbright.enabled) active.add(new Module("Fullbright", 0xFFFFFF88));
        if (Freecam.enabled) active.add(new Module("Freecam", 0xFF88FF88));

        active.sort(Comparator.comparingInt(m -> -client.textRenderer.getWidth(m.name)));

        int startX = screenWidth - 5;
        int startY = 5;
        int rowH = 12;

        for (int i = 0; i < active.size(); i++) {
            Module m = active.get(i);
            int textWidth = client.textRenderer.getWidth(m.name);
            int x = startX - textWidth - 5;
            int y = startY + (i * rowH);

            context.fill(x - 2, y, startX, y + rowH - 1, 0x90000000);
            context.fill(x - 2, y, x, y + rowH - 1, m.color);
            context.drawTextWithShadow(client.textRenderer, Text.literal(m.name), x + 1, y + 1, 0xFFFFFFFF);
        }
    }

    private record Module(String name, int color) {}
}
