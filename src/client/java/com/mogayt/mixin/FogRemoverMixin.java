package com.mogayt.mixin;

import com.mogayt.FogRemover;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class FogRemoverMixin {

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void onApplyFog(Camera camera,
                                   BackgroundRenderer.FogType fogType,
                                   float viewDistance,
                                   boolean thickFog,
                                   CallbackInfo ci) {
        if (FogRemover.enabled) {
            ci.cancel();
        }
    }
}
