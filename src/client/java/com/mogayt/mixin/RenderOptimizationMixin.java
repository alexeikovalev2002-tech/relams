package com.mogayt.mixin;

import com.mogayt.Optimizer;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class RenderOptimizationMixin {

    // Отключение неба
    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true, require = 0)
    private void onRenderSky(CallbackInfo ci) {
        if (Optimizer.enabled) ci.cancel();
    }

    // Отключение погоды (дождь, снег)
    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true, require = 0)
    private void onRenderWeather(CallbackInfo ci) {
        if (Optimizer.enabled) ci.cancel();
    }
}
