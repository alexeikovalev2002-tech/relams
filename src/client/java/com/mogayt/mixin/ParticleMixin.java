package com.mogayt.mixin;

import com.mogayt.Optimizer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public class ParticleMixin {

    // 1. Основной метод: частица с координатами
    @Inject(
        method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onAddParticle(ParticleEffect parameters, double x, double y, double z,
                                double vx, double vy, double vz,
                                CallbackInfoReturnable<Particle> cir) {
        if (Optimizer.enabled) cir.setReturnValue(null);
    }

    // 2. Метод с кастомным эффектом (например, от зачарований)
    @Inject(
        method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDDLnet/minecraft/particle/ParticleEffect;)Lnet/minecraft/client/particle/Particle;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onAddParticleCustom(ParticleEffect parameters, double x, double y, double z,
                                      double vx, double vy, double vz, ParticleEffect custom,
                                      CallbackInfoReturnable<Particle> cir) {
        if (Optimizer.enabled) cir.setReturnValue(null);
    }

    // 3. Emitter — большие эффекты (взрывы, дыхание дракона, порталы)
    @Inject(
        method = "addEmitter",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onAddEmitter(ParticleEffect parameters, double x, double y, double z,
                               double vx, double vy, double vz, ParticleEffect custom,
                               CallbackInfo ci) {
        if (Optimizer.enabled) ci.cancel();
    }
}
