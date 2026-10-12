package com.mogayt.mixin;

import com.mogayt.Freecam;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemRenderer.class)
public class HandRotationMixin {

    @Redirect(method = "renderFirstPersonItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getYaw(F)F"),
            require = 0)
    private float redirYaw(LivingEntity entity, float tickDelta) {
        return Freecam.enabled ? Freecam.camYaw : entity.getYaw(tickDelta);
    }

    @Redirect(method = "renderFirstPersonItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getPitch(F)F"),
            require = 0)
    private float redirPitch(LivingEntity entity, float tickDelta) {
        return Freecam.enabled ? Freecam.camPitch : entity.getPitch(tickDelta);
    }
}
