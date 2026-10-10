package com.mogayt.mixin;

import com.mogayt.FogRemover;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderSystem.class)
public class FogRemoverMixin {

    @ModifyVariable(method = "setShaderFogStart", at = @At("HEAD"), argsOnly = true, require = 0)
    private static float modifyFogStart(float start) {
        return FogRemover.enabled ? Float.MAX_VALUE : start;
    }

    @ModifyVariable(method = "setShaderFogEnd", at = @At("HEAD"), argsOnly = true, require = 0)
    private static float modifyFogEnd(float end) {
        return FogRemover.enabled ? Float.MAX_VALUE : end;
    }
}
