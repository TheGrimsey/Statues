package net.thegrimsey.statues.client.screen;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.thegrimsey.statues.StatueNetworking;

public class PaletteScreen extends HandledScreen<PaletteScreenHandler> {
    static final Text renderText = Text.translatable("statues.palette.name");

    TextFieldWidget textFieldWidget;
    float textX, textY;

    public PaletteScreen(PaletteScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        super.renderBackground(context);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, renderText, (int)textX, (int)textY, 16777215, true);
    }
    @Override
    protected void init() {
        super.init();

        textX = (this.backgroundWidth - textRenderer.getWidth(renderText)) / 2.f;
        textY = this.backgroundHeight / 2.f - 15 - textRenderer.fontHeight;


        textFieldWidget = addDrawableChild(new TextFieldWidget(textRenderer, this.width / 2 - 75, this.height / 2 - 10, 150, 20, Text.of("")));
        textFieldWidget.setMaxLength(16);

        // Done button this.width / 2 - 75, this.height / 2 + 15, 150, 20,
        addDrawableChild(ButtonWidget.builder(Text.translatable("statues.palette.button_done"), button -> {
            // Send.
            if(textFieldWidget.getText().isBlank() || textFieldWidget.getText().isEmpty()) {
                close();
                return;
            }

            SkullBlockEntity.loadProperties(new GameProfile(null, textFieldWidget.getText()), gameProfile -> {
                if(gameProfile.isComplete())
                    StatueNetworking.sendSendPalette(handler.statuePos, gameProfile.getId());

                close();
            });
        }).dimensions(this.width / 2 - 75, this.height / 2 + 15, 150, 20).build());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (textFieldWidget.isActive() && (keyCode >= 'A' && keyCode <= 'z' || keyCode >= '0' && keyCode <= '9'))
            return true;

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
