package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class TrajectoryScreen extends Screen {

    public TrajectoryScreen() {
        super(Text.literal("Trajectory"));
    }

    @Override
    protected void init() {
        int cy = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(TrajectoryPredictor.enabled ? "Trajectory: ВКЛ" : "Trajectory: ВЫКЛ"),
                (b) -> {
                    TrajectoryPredictor.enabled = !TrajectoryPredictor.enabled;
                    b.setMessage(Text.literal(TrajectoryPredictor.enabled ? "Trajectory: ВКЛ" : "Trajectory: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, cy - 40, 200, 20).build());

        this.addDrawableChild(KeyBindHelper.create(this.textRenderer,
                this.width / 2 - 50, cy + 10, 100, 20,
                KeyBinds.trajectoryKey, k -> KeyBinds.trajectoryKey = k));

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (b) -> { if (this.client != null) this.client.setScreen(new EspMenuScreen()); }
        ).dimensions(this.width / 2 - 100, cy + 50, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Клавиша"),
                this.width / 2, this.height / 2 - 2, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
