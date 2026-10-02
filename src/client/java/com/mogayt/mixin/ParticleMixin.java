package com.mogayt.mixin;

import com.mogayt.Optimizer;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class ParticleMixin {

    @Inject(
        method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onAddParticle(ParticleEffect parameters, double x, double y, double z,
                                double velocityX, double velocityY, double velocityZ,
                                CallbackInfo ci) {
        if (Optimizer.enabled) {
            ci.cancel(); // блокируем создание ЛЮБОЙ частицы
        }
    }
}
