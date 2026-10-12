package com.mogayt.mixin;

import com.mogayt.Freecam;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemRenderer.class)
public class HandRotationMixin {

    @Redirect(method = "renderFirstPersonItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getYaw()F"),
            require = 0)
    private float redirYaw(AbstractClientPlayerEntity player) {
        return Freecam.enabled ? Freecam.camYaw : player.getYaw();
    }

    @Redirect(method = "renderFirstPersonItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getPitch()F"),
            require = 0)
    private float redirPitch(AbstractClientPlayerEntity player) {
        return Freecam.enabled ? Freecam.camPitch : player.getPitch();
    }

    @Redirect(method = "renderFirstPersonItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getPitch(F)F"),
            require = 0)
    private float redirPitchDelta(AbstractClientPlayerEntity player, float tickDelta) {
        return Freecam.enabled ? Freecam.camPitch : player.getPitch(tickDelta);
    }

    @Redirect(method = "renderFirstPersonItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getYaw(F)F"),
            require = 0)
    private float redirYawDelta(AbstractClientPlayerEntity player, float tickDelta) {
        return Freecam.enabled ? Freecam.camYaw : player.getYaw(tickDelta);
    }
}
