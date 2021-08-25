package net.thegrimsey.statues.client.screen;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.thegrimsey.statues.StatueNetworking;

public class PaletteScreen extends HandledScreen<PaletteScreenHandler> {
    TextFieldWidget textFieldWidget;
    TranslatableText renderText = new TranslatableText("statues.palette.name");
    float textX, textY;

    public PaletteScreen(PaletteScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        renderBackground(matrices, 0);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        textRenderer.drawWithShadow(matrices, renderText, textX, textY, 16777215);
    }

    @Override
    protected void init() {
        super.init();

        textX = (this.backgroundWidth - textRenderer.getWidth(renderText)) / 2.f;
        textY = this.backgroundHeight/2.f - 15 - textRenderer.fontHeight;

        client.keyboard.setRepeatEvents(true);

        textFieldWidget = addDrawableChild(new TextFieldWidget(textRenderer, this.width/2 - 75, this.height/2 - 10, 150, 20, Text.of("")));
        textFieldWidget.setMaxLength(16);

        // Done button
        addDrawableChild(new ButtonWidget(this.width/2 - 75, this.height/2 + 15, 150, 20, new TranslatableText("statues.palette.button_done"), button -> {
            // Send.
            SkullBlockEntity.loadProperties(new GameProfile(null, textFieldWidget.getText()), gameProfile -> {
                StatueNetworking.sendSendPalette(handler.statuePos, gameProfile.getId());
                onClose();
            });
        }));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(textFieldWidget.isActive() && (keyCode >= 'A' && keyCode <= 'z' || keyCode >= '0' && keyCode <= '9'))
            return true;

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
