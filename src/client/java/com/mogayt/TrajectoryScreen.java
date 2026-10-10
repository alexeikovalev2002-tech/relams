package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
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
        ).dimensions(this.width / 2 - 100, cy - 70, 200, 20).build());

        TextFieldWidget widthField = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 50, cy - 20, 100, 20, Text.literal("Толщина"));
        widthField.setText(String.valueOf(TrajectoryPredictor.lineWidth));
        widthField.setChangedListener(t -> {
            try {
                float v = Float.parseFloat(t.trim());
                if (v >= 0.5f && v <= 5.0f) TrajectoryPredictor.lineWidth = v;
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(widthField);

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(TrajectoryPredictor.filled ? "Заполнение: ВКЛ" : "Заполнение: ВЫКЛ"),
                (b) -> {
                    TrajectoryPredictor.filled = !TrajectoryPredictor.filled;
                    b.setMessage(Text.literal(TrajectoryPredictor.filled ? "Заполнение: ВКЛ" : "Заполнение: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, cy + 30, 200, 20).build());

        this.addDrawableChild(KeyBindHelper.create(this.textRenderer,
                this.width / 2 - 50, cy + 70, 100, 20,
                KeyBinds.trajectoryKey, k -> KeyBinds.trajectoryKey = k));

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Назад"),
                (b) -> { if (this.client != null) this.client.setScreen(new EspMenuScreen()); }
        ).dimensions(this.width / 2 - 100, cy + 110, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        int cy = this.height / 2;
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Толщина линии (0.5-5.0)"),
                this.width / 2, cy - 32, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Клавиша"),
                this.width / 2, cy + 58, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
