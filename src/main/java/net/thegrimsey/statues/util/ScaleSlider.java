package net.thegrimsey.statues.util;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ScaleSlider extends SliderWidget {
    final String translationKey;
    final Consumer<Float> applyScale;

    final float min, max;

    public ScaleSlider(int x, int y, int width, int height, String translationKey, Consumer<Float> applyDegree, float max, float min) {
        super(x, y, width, height, Text.empty(), 1.0 - ((1.0D - min) / (max - min)));

        this.translationKey = translationKey;
        this.applyScale = applyDegree;
        this.min = min;
        this.max = max;

        this.updateMessage();
    }

    private float getRoundedValue() {
        double value = (1.0 - this.value) * (max - min) + min;

        return (float)((int)(value * 100.0)) / 100.0f;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Text.translatable(translationKey, getRoundedValue()));
    }

    @Override
    protected void applyValue() {
        applyScale.accept(getRoundedValue());
    }
}
