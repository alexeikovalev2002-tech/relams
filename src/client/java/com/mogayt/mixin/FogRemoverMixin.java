package com.mogayt.mixin;

import com.mogayt.FogRemover;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BackgroundRenderer.class)
public class FogRemoverMixin {

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void onApplyFog(Camera camera,
                                   BackgroundRenderer.FogType fogType,
                                   Vector4f color,
                                   float viewDistance,
                                   boolean thickFog,
                                   float tickDelta,
                                   CallbackInfoReturnable<Vector4f> cir) {
        if (FogRemover.enabled) {
            // Возвращаем белый цвет без изменений — туман не применяется
            cir.setReturnValue(color);
        }
    }
}
