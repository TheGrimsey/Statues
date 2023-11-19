package net.thegrimsey.statues.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.thegrimsey.statues.StatueNetworking;
import net.thegrimsey.statues.Statues;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;
import net.thegrimsey.statues.util.RotationSlider;
import net.thegrimsey.statues.util.ScaleSlider;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class StatueEditorScreen extends HandledScreen<StatueEditorScreenHandler> {
    static final Identifier TEXTURE = new Identifier(Statues.MODID, "textures/gui/statue_editor.png");

    List<SliderWidget> sliders;
    final StatueBlockEntity blockEntity;

    SliderWidget draggedSlider = null;

    public StatueEditorScreen(StatueEditorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 220;
        backgroundHeight = 207;

        blockEntity = new StatueBlockEntity(BlockPos.ORIGIN, null);
        blockEntity.setProfile(inventory.player.getGameProfile());
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        super.renderBackground(context);

        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);

        drawStatue(this.width / 2 - 18, this.height / 2 + 24);

    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {}

    // Based on InventoryScreen::DrawEntity
    void drawStatue(int x, int y) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 1050.0D);
        matrixStack.scale(1.0F, 1.0F, -1.0F);

        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0D, 0.0D, 1000.0D);
        matrixStack2.scale((float) 35, (float) 35, (float) 35);
        Quaternionf quaternion = new Quaternionf().rotateZ((float) Math.toRadians(180.0F));
        Quaternionf quaternion2 = new Quaternionf().rotateY((float) Math.toRadians(180.0F));
        matrixStack2.multiply(quaternion);
        matrixStack2.multiply(quaternion2);

        DiffuseLighting.method_34742();
        BlockEntityRenderDispatcher blockEntityRenderDispatcher = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        blockEntityRenderDispatcher.renderEntity(blockEntity, matrixStack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, 0);
        immediate.draw();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    @Override
    protected void init() {
        super.init();

        x = (width - backgroundWidth) / 2;
        y = (height - backgroundHeight) / 2;

        sliders = new ArrayList<>();

        // Left Arm Properties
        int sliderX = this.width / 2 - 70 - 35;
        int sliderY = (this.height - this.backgroundHeight) / 2 + 81 - 23;

        // LEFT arm PANELS. (Modifies right arm)
        // Left Arm Pitch
        sliders.add(addDrawableChild(new RotationSlider(sliderX, sliderY, 70, 20, "statues.pitch", aFloat -> blockEntity.rightArm.pitch = (float) Math.toRadians(aFloat))));
        // Left Arm Yaw
        sliders.add(addDrawableChild(new RotationSlider(sliderX, sliderY + 22, 70, 20, "statues.yaw", aFloat -> blockEntity.rightArm.yaw = (float) Math.toRadians(aFloat))));
        // Left Arm Roll
        sliders.add(addDrawableChild(new RotationSlider(sliderX, sliderY + 44, 70, 20, "statues.roll", aFloat -> blockEntity.rightArm.roll = (float) Math.toRadians(aFloat))));

        // RIGHT arm PANELS. (Modifies left arm)
        // Right Arm Properties
        sliderX = this.width / 2 + 35;

        // Right Arm Pitch
        sliders.add(addDrawableChild(new RotationSlider(sliderX, sliderY, 70, 20, "statues.pitch", aFloat -> blockEntity.leftArm.pitch = (float) Math.toRadians(aFloat))));
        // Right Arm Yaw
        sliders.add(addDrawableChild(new RotationSlider(sliderX, sliderY + 22, 70, 20, "statues.yaw", aFloat -> blockEntity.leftArm.yaw = (float) Math.toRadians(aFloat))));
        // Right Arm Roll
        sliders.add(addDrawableChild(new RotationSlider(sliderX, sliderY + 44, 70, 20, "statues.roll", aFloat -> blockEntity.leftArm.roll = (float) Math.toRadians(aFloat))));

        // LEGS
        sliderX = this.width / 2 - 70 - 35;
        sliderY = (this.height - backgroundHeight) / 2 + 137;

        // LEFT leg PANELS. (Modifies right leg)
        // Left Leg Pitch
        sliders.add(addDrawableChild(new RotationSlider(sliderX, sliderY, 70, 20, "statues.pitch", aFloat -> blockEntity.rightLeg.pitch = (float) Math.toRadians(aFloat))));
        // Left Leg Yaw
        sliders.add(addDrawableChild(new RotationSlider(sliderX, sliderY + 22, 70, 20, "statues.yaw", aFloat -> blockEntity.rightLeg.yaw = (float) Math.toRadians(aFloat))));
        // Left Leg Roll
        sliders.add(addDrawableChild(new RotationSlider(sliderX, sliderY + 44, 70, 20, "statues.roll", aFloat -> blockEntity.rightLeg.roll = (float) Math.toRadians(aFloat))));

        sliderX = this.width / 2 + 35;

        // RIGHT leg PANELS. (Modifies left leg)
        // Right Leg Pitch
        sliders.add(addDrawableChild(new RotationSlider(sliderX, sliderY, 70, 20, "statues.pitch", aFloat -> blockEntity.leftLeg.pitch = (float) Math.toRadians(aFloat))));
        // Right Leg Yaw
        sliders.add(addDrawableChild(new RotationSlider(sliderX, sliderY + 22, 70, 20, "statues.yaw", aFloat -> blockEntity.leftLeg.yaw = (float) Math.toRadians(aFloat))));
        // Right Leg Roll
        sliders.add(addDrawableChild(new RotationSlider(sliderX, sliderY + 44, 70, 20, "statues.roll", aFloat -> blockEntity.leftLeg.roll = (float) Math.toRadians(aFloat))));

        // HEAD
        sliderX = this.width / 2;
        sliderY = (this.height - backgroundHeight) / 2 + 27;

        // Head Pitch
        sliders.add(addDrawableChild(new RotationSlider(sliderX - 1 - 70, sliderY, 70, 20, "statues.pitch", aFloat -> blockEntity.head.pitch = (float) Math.toRadians(aFloat))));
        // Head Yaw
        sliders.add(addDrawableChild(new RotationSlider(sliderX - 30, sliderY - 22, 60, 20, "statues.yaw", aFloat -> blockEntity.head.yaw = (float) Math.toRadians(aFloat))));
        // Head Roll
        sliders.add(addDrawableChild(new RotationSlider(sliderX + 1, sliderY, 70, 20, "statues.roll", aFloat -> blockEntity.head.roll = (float) Math.toRadians(aFloat))));

        // FULL BODY
        sliderX = this.width / 2 - 30;
        sliderY = (this.height - backgroundHeight) / 2 + 137;

        // Body Yaw
        sliders.add(addDrawableChild(new RotationSlider(sliderX, sliderY, 60, 20, "statues.yaw", aFloat -> blockEntity.yaw = (float) Math.toRadians(360 - aFloat))));

        sliders.add(addDrawableChild(new ScaleSlider(sliderX, sliderY + 22, 60, 20, "statues.scale", aFloat -> blockEntity.scale = aFloat, Statues.MIN_STATUE_SCALE, Statues.MAX_STATUE_SCALE)));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        draggedSlider = null;

        for (SliderWidget sliderWidget : sliders) {
            if (sliderWidget.isMouseOver(mouseX, mouseY)) {
                draggedSlider = sliderWidget;
                break;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggedSlider = null;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggedSlider != null) {
            draggedSlider.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void close() {
        super.close();

        blockEntity.yaw += handler.startYaw;
        StatueNetworking.sendEditStatuePacket(handler.statuePos, blockEntity);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();

        // We shouldn't be doing this every frame.
        blockEntity.updateCache();
    }
}
