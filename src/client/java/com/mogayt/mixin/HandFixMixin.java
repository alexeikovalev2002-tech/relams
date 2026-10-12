package com.mogayt.mixin;

import com.mogayt.Freecam;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HandFixMixin {

    @Inject(method = "render", at = @At("HEAD"), require = 0)
    private void onRenderHead(float tickDelta, MatrixStack matrices,
                              VertexConsumerProvider vertexConsumers, int light,
                              CallbackInfo ci) {
        if (!Freecam.enabled) return;

        ClientPlayerEntity p = MinecraftClient.getInstance().player;
        if (p == null) return;

        // Сдвигаем всю руку туда, где камера, а не игрок
        double dx = Freecam.camX - p.getX();
        double dy = Freecam.camY - (p.getY() + p.getStandingEyeHeight());
        double dz = Freecam.camZ - p.getZ();

        matrices.translate(dx, dy, dz);
    }
}
