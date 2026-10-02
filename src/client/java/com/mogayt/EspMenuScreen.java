package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EspMenuScreen extends Screen {

    // Координаты кнопки ESP (нужны для отслеживания правого клика)
    private int espX, espY, espW, espH;

    public EspMenuScreen() {
        super(Text.literal("Mog Mod Menu"));
    }

    @Override
    protected void init() {
        int centerY = this.height / 2;

        espW = 200;
        espH = 20;
        espX = this.width / 2 - 100;
        espY = centerY - 60;

        // ESP (левый клик — вкл/выкл, правый — выбор руды)
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(getEspLabel()),
                (button) -> {
                    BlockEspMod.espEnabled = !BlockEspMod.espEnabled;
                    button.setMessage(Text.literal(getEspLabel()));
                }
        ).dimensions(espX, espY, espW, espH).build());

        // Fullbright
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Fullbright.enabled ? "Fullbright: ВКЛ" : "Fullbright: ВЫКЛ"),
                (button) -> {
                    Fullbright.enabled = !Fullbright.enabled;
                    button.setMessage(Text.literal(Fullbright.enabled ? "Fullbright: ВКЛ" : "Fullbright: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, centerY - 30, 200, 20).build());

        // Freecam
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(Freecam.enabled ? "Freecam: ВКЛ" : "Freecam: ВЫКЛ"),
                (button) -> {
                    Freecam.enabled = !Freecam.enabled;
                    if (Freecam.enabled) Freecam.onEnable();
                    button.setMessage(Text.literal(Freecam.enabled ? "Freecam: ВКЛ" : "Freecam: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, centerY, 200, 20).build());

        // Distant Horizons (дальняя прорисовка)
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(BlockEspMod.distantEnabled ? "Distant: ВКЛ" : "Distant: ВЫКЛ"),
                (button) -> {
                    BlockEspMod.distantEnabled = !BlockEspMod.distantEnabled;
                    button.setMessage(Text.literal(BlockEspMod.distantEnabled ? "Distant: ВКЛ" : "Distant: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, centerY + 30, 200, 20).build());

        // Закрыть
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Закрыть"),
                (button) -> this.close()
        ).dimensions(this.width / 2 - 100, centerY + 70, 200, 20).build());
    }

    private String getEspLabel() {
        String state = BlockEspMod.espEnabled ? "ВКЛ" : "ВЫКЛ";
        return "ESP [" + BlockEspMod.getOreName() + "]: " + state;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1
                && mouseX >= espX && mouseX <= espX + espW
                && mouseY >= espY && mouseY <= espY + espH) {
            if (this.client != null) {
                this.client.setScreen(new OreSelectScreen());
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("ПКМ по кнопке ESP — выбор руды"),
                this.width / 2, 40, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
