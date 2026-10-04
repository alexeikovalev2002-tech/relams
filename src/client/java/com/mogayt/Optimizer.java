package com.mogayt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;

public class Optimizer {

    public static boolean enabled = false;
    private static boolean wasEnabled = false;

    private static boolean prevBobView = true;
    private static boolean prevEntityShadows = true;
    private static boolean prevAo = true;
    private static double prevEntityDistance = 1.0;
    private static CloudRenderMode prevClouds = CloudRenderMode.FANCY;
    private static GraphicsMode prevGraphics = GraphicsMode.FANCY;
    private static int prevBiomeBlend = 2;
    private static int prevMenuBlur = 5;

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;

        if (enabled && !wasEnabled) {
            prevBobView = client.options.getBobView().getValue();
            prevEntityShadows = client.options.getEntityShadows().getValue();
            prevAo = client.options.getAo().getValue();
            prevEntityDistance = client.options.getEntityDistanceScaling().getValue();
            prevClouds = client.options.getCloudRenderMode().getValue();
            prevGraphics = client.options.getGraphicsMode().getValue();
            prevBiomeBlend = client.options.getBiomeBlendRadius().getValue();
            prevMenuBlur = client.options.getMenuBackgroundBlurriness().getValue();

            client.options.getBobView().setValue(false);
            client.options.getEntityShadows().setValue(false);
            client.options.getAo().setValue(false);
            client.options.getEntityDistanceScaling().setValue(0.5);
            client.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
            client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
            client.options.getBiomeBlendRadius().setValue(0);
            client.options.getMenuBackgroundBlurriness().setValue(0);

            wasEnabled = true;
        } else if (!enabled && wasEnabled) {
            client.options.getBobView().setValue(prevBobView);
            client.options.getEntityShadows().setValue(prevEntityShadows);
            client.options.getAo().setValue(prevAo);
            client.options.getEntityDistanceScaling().setValue(prevEntityDistance);
            client.options.getCloudRenderMode().setValue(prevClouds);
            client.options.getGraphicsMode().setValue(prevGraphics);
            client.options.getBiomeBlendRadius().setValue(prevBiomeBlend);
            client.options.getMenuBackgroundBlurriness().setValue(prevMenuBlur);

            wasEnabled = false;
        }
    }
}
