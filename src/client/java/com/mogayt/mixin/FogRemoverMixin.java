package com.mogayt.mixin;

import com.mogayt.FogRemover;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderSystem.class)
public class FogRemoverMixin {

    @ModifyVariable(method = "setShaderFogStart", at = @At("HEAD"), argsOnly = true, require = 0)
    private static float modFogStart(float v) {
        return FogRemover.enabled ? Float.MAX_VALUE : v;
    }

    @ModifyVariable(method = "setShaderFogEnd", at = @At("HEAD"), argsOnly = true, require = 0)
    private static float modFogEnd(float v) {
        return FogRemover.enabled ? Float.MAX_VALUE : v;
    }
}
