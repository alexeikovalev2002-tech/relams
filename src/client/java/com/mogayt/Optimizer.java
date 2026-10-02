package com.mogayt;

import net.minecraft.client.MinecraftClient;

public class Optimizer {

    public static boolean enabled = false;
    private static boolean wasEnabled = false;

    private static boolean prevBobView = true;
    private static boolean prevEntityShadows = true;
    private static boolean prevAo = true;
    private static double prevEntityDistance = 1.0;

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;

        if (enabled && !wasEnabled) {
            prevBobView = client.options.getBobView().getValue();
            prevEntityShadows = client.options.getEntityShadows().getValue();
            prevAo = client.options.getAo().getValue();
            prevEntityDistance = client.options.getEntityDistanceScaling().getValue();

            client.options.getBobView().setValue(false);
            client.options.getEntityShadows().setValue(false);
            client.options.getAo().setValue(false);
            client.options.getEntityDistanceScaling().setValue(0.5);

            wasEnabled = true;
        } else if (!enabled && wasEnabled) {
            client.options.getBobView().setValue(prevBobView);
            client.options.getEntityShadows().setValue(prevEntityShadows);
            client.options.getAo().setValue(prevAo);
            client.options.getEntityDistanceScaling().setValue(prevEntityDistance);

            wasEnabled = false;
        }
    }
}
