package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;

public class Optimizer {

    public static boolean enabled = false;
    private static boolean wasEnabled = false;

    // Сохранённые оригинальные значения
    private static boolean prevBobView = true;
    private static boolean prevEntityShadows = true;
    private static boolean prevAo = true;
    private static double prevEntityDistance = 1.0;
    private static ParticlesMode prevParticles = ParticlesMode.ALL;
    private static CloudRenderMode prevClouds = CloudRenderMode.FANCY;
    private static GraphicsMode prevGraphics = GraphicsMode.FANCY;

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;

        if (enabled && !wasEnabled) {
            // Запоминаем оригинальные значения
            prevBobView = client.options.getBobView().getValue();
            prevEntityShadows = client.options.getEntityShadows().getValue();
            prevAo = client.options.getAo().getValue();
            prevEntityDistance = client.options.getEntityDistanceScaling().getValue();
            prevParticles = client.options.getParticles().getValue();
            prevClouds = client.options.getCloudRenderMode().getValue();
            prevGraphics = client.options.getGraphicsMode().getValue();

            // Применяем оптимизации
            client.options.getBobView().setValue(false);
            client.options.getEntityShadows().setValue(false);
            client.options.getAo().setValue(false);
            client.options.getEntityDistanceScaling().setValue(0.5);
            client.options.getParticles().setValue(ParticlesMode.MINIMAL);
            client.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
            client.options.getGraphicsMode().setValue(GraphicsMode.FAST);

            wasEnabled = true;
        } else if (!enabled && wasEnabled) {
            // Возвращаем всё как было
            client.options.getBobView().setValue(prevBobView);
            client.options.getEntityShadows().setValue(prevEntityShadows);
            client.options.getAo().setValue(prevAo);
            client.options.getEntityDistanceScaling().setValue(prevEntityDistance);
            client.options.getParticles().setValue(prevParticles);
            client.options.getCloudRenderMode().setValue(prevClouds);
            client.options.getGraphicsMode().setValue(prevGraphics);

            wasEnabled = false;
        }
    }
}
