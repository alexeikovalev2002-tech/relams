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
    private static int prevParticles = 0; // 0=ALL, 1=DECREASED, 2=MINIMAL
    private static CloudRenderMode prevClouds = CloudRenderMode.FANCY;
    private static GraphicsMode prevGraphics = GraphicsMode.FANCY;

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;

        if (enabled && !wasEnabled) {
            prevBobView = client.options.getBobView().getValue();
            prevEntityShadows = client.options.getEntityShadows().getValue();
            prevAo = client.options.getAo().getValue();
            prevEntityDistance = client.options.getEntityDistanceScaling().getValue();
            prevParticles = client.options.getParticles().getValue();
            prevClouds = client.options.getCloudRenderMode().getValue();
            prevGraphics = client.options.getGraphicsMode().getValue();

            client.options.getBobView().setValue(false);
            client.options.getEntityShadows().setValue(false);
            client.options.getAo().setValue(false);
            client.options.getEntityDistanceScaling().setValue(0.5);
            client.options.getParticles().setValue(2); // MINIMAL
            client.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
            client.options.getGraphicsMode().setValue(GraphicsMode.FAST);

            wasEnabled = true;
        } else if (!enabled && wasEnabled) {
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
