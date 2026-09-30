package com.mogayt.mixin;

import com.mogayt.Fullbright;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightmapTextureManager.class)
public class LightmapMixin {

    @ModifyVariable(method = "update", at = @At("STORE"), ordinal = 0)
    private float modifyGamma(float originalGamma) {
        if (Fullbright.enabled) {
            return 15.0f;
        }
        return originalGamma;
    }
}
