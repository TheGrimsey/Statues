package net.thegrimsey.statues.util;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class RotationSlider extends SliderWidget {
    final String translationKey;
    final Consumer<Float> applyDegree;

    public RotationSlider(int x, int y, int width, int height, String translationKey, Consumer<Float> applyDegree) {
        super(x, y, width, height, Text.empty(), 0.0D);

        this.translationKey = translationKey;
        this.applyDegree = applyDegree;

        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Text.translatable(translationKey, MathHelper.floor(MathHelper.clampedLerp(0.0D, 360.0D, this.value))));
    }

    @Override
    protected void applyValue() {
        applyDegree.accept((float) MathHelper.clampedLerp(0.0D, 360.0D, this.value));
    }
}
