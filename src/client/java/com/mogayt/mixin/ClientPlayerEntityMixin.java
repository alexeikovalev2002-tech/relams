package com.mogayt.mixin;

import com.mogayt.Freecam;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void onTickMovement(CallbackInfo ci) {
        if (Freecam.enabled) {
            ci.cancel(); // отменяем движение — тело стоит на месте
        }
    }
}
