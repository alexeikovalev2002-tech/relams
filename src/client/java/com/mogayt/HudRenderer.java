package com.mogayt;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
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

    private static boolean dragging = false;
    private static double dragOffsetX = 0;
    private static double dragOffsetY = 0;

    public static void register() {
        // Регистрация отрисовки HUD
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;
            if (client.options.hudHidden) return;

            int screenWidth = client.getWindow().getScaledWidth();
            boolean isChatOpen = client.currentScreen instanceof ChatScreen;

            Notification.tick();
            if (!Notification.entries.isEmpty()) {
                int ny = (int) hudY + 45;
                for (Notification.Entry e : Notification.entries) {
                    drawContext.drawTextWithShadow(client.textRenderer, Text.literal(e.text), (int) hudX, ny, 0xFFFFFFFF);
                    ny += 10;
                }
            }

            drawInfoPanel(drawContext, client, (int) hudX, (int) hudY, isChatOpen);

            if (client.crosshairTarget instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target && target != client.player) {
                drawTargetHud(drawContext, client, target, (int) hudX, (int) hudY + 45);
            }

            drawArrayList(drawContext, client, screenWidth);
        });

        // Регистрация событий мыши для экрана чата
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof ChatScreen)) return;

            // Перетаскивание
            ScreenMouseEvents.beforeMouseDrag(screen).register((mouseX, mouseY, button, deltaX, deltaY) -> {
                if (button == 0 && dragging) {
                    hudX = mouseX - dragOffsetX;
                    hudY = mouseY - dragOffsetY;
                    
                    int sw = client.getWindow().getScaledWidth();
                    int sh = client.getWindow().getScaledHeight();
                    if (hudX < 0) hudX = 0;
                    if (hudY < 0) hudY = 0;
                    if (hudX > sw - 110) hudX = sw - 110;
                    if (hudY > sh - 40) hudY = sh - 40;
                    
                    return true; // Отменяем стандартную обработку
                }
                return false;
            });

            // Клик (чтобы начать перетаскивание)
            ScreenMouseEvents.beforeMouseClick(screen).register((mouseX, mouseY, button) -> {
                if (button == 0 && mouseX >= hudX && mouseX <= hudX + 110 && mouseY >= hudY && mouseY <= hudY + 40) {
                    dragging = true;
                    dragOffsetX = mouseX - hudX;
                    dragOffsetY = mouseY - hudY;
                    return true;
                }
                return false;
            });

            // Отпускание мыши
            ScreenMouseEvents.beforeMouseRelease(screen).register((mouseX, mouseY, button) -> {
                if (button == 0) dragging = false;
                return false;
            });
        });
    }

    // ... (остальные методы drawInfoPanel, drawTargetHud, drawArrayList остаются без изменений)
}
