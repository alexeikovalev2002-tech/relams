package com.mogayt.mixin;

import com.mogayt.HudRenderer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
        // Проверяем, что текущий экран — это именно чат
        if ((Object) this instanceof ChatScreen) {
            if (HudRenderer.handleMouseDrag(mouseX, mouseY, button)) {
                cir.setReturnValue(true);
            }
        }
    }
}
