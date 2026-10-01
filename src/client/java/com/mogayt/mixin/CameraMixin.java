package com.mogayt.mixin;

import com.mogayt.Freecam;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {

    @Shadow private Vec3d pos;
    @Shadow private float yaw;
    @Shadow private float pitch;

    @Inject(method = "update", at = @At("TAIL"))
    private void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson,
                          boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (Freecam.enabled) {
            // Плавная интерполяция между прошлым и текущим тиком
            double x = Freecam.prevCamX + (Freecam.camX - Freecam.prevCamX) * tickDelta;
            double y = Freecam.prevCamY + (Freecam.camY - Freecam.prevCamY) * tickDelta;
            double z = Freecam.prevCamZ + (Freecam.camZ - Freecam.prevCamZ) * tickDelta;

            this.pos = new Vec3d(x, y, z);
            this.yaw = Freecam.prevCamYaw + (Freecam.camYaw - Freecam.prevCamYaw) * tickDelta;
            this.pitch = Freecam.prevCamPitch + (Freecam.camPitch - Freecam.prevCamPitch) * tickDelta;
        }
    }
}
