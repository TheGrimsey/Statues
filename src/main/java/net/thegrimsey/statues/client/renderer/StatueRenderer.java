package net.thegrimsey.statues.client.renderer;

import com.google.common.collect.Maps;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class StatueRenderer implements BlockEntityRenderer<StatueBlockEntity> {
    // Copied from ArmorFeatureRenderer.java
    private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();
    // Copied from EntityModels.java
    private static final Dilation ARMOR_DILATION = new Dilation(1.0F);
    private static final Dilation HAT_DILATION = new Dilation(0.5F);

    public final ModelPart model;
    public final ModelPart armorModel; // TODO Let's just get all the children directly to save the getChild thing every frame.

    public StatueRenderer(BlockEntityRendererFactory.Context context) {
        TexturedModelData texturedModelData = TexturedModelData.of(BipedEntityModel.getModelData(Dilation.NONE, 0.0F), 16, 16);
        model = texturedModelData.createModel();

        texturedModelData = TexturedModelData.of(BipedEntityModel.getModelData(ARMOR_DILATION, 0.0F), 16, 16);
        armorModel = texturedModelData.createModel();
    }

    @Override
    public void render(StatueBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        float legLength = 12.f;

        // Calculate leg length.
        {
            // LEFT LEG
            Quaternion leftLegRot = new Quaternion(entity.leftLegPitch, entity.leftLegYaw, entity.leftLegRoll, true);
            Vec3f down = Vec3f.NEGATIVE_Y.copy();
            down.rotate(leftLegRot);
            float leftDot = down.dot(Vec3f.NEGATIVE_Y);

            // RIGHT LEG
            Quaternion rightLegRot = new Quaternion(entity.rightLegPitch, entity.rightLegYaw, entity.rightLegRoll, true);
            down.set(Vec3f.NEGATIVE_Y);
            down.rotate(rightLegRot);
            float rightDot = down.dot(Vec3f.NEGATIVE_Y);

            // Straightest leg is base.
            legLength *= Math.max(leftDot, rightDot);
        }

        matrices.translate(0.5, 1.5 - (12.f/16f) + (legLength/16f), 0.5);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.f));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.yaw));

        // Set angles.
        updateAngles(model, entity);
        updateAngles(armorModel, entity);

        // Render base model.
        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(new Identifier("minecraft", "textures/block/stone.png"))), light, overlay);

        renderArmor(entity, matrices, vertexConsumers, light, overlay);
    }

    void updateAngles(ModelPart model, StatueBlockEntity entity)
    {
        model.getChild(EntityModelPartNames.LEFT_LEG).setAngles((float)Math.toRadians(entity.leftLegPitch), (float)Math.toRadians(entity.leftLegYaw), (float)Math.toRadians(entity.leftLegRoll));
        model.getChild(EntityModelPartNames.RIGHT_LEG).setAngles((float)Math.toRadians(entity.rightLegPitch), (float)Math.toRadians(entity.rightLegYaw), (float)Math.toRadians(entity.rightLegRoll));

        model.getChild(EntityModelPartNames.LEFT_ARM).setAngles((float)Math.toRadians(entity.leftArmPitch), (float)Math.toRadians(entity.leftArmYaw), (float)Math.toRadians(entity.leftArmRoll));
        model.getChild(EntityModelPartNames.RIGHT_ARM).setAngles((float)Math.toRadians(entity.rightArmPitch), (float)Math.toRadians(entity.rightArmYaw), (float)Math.toRadians(entity.rightArmRoll));

        model.getChild(EntityModelPartNames.HEAD).setAngles((float)Math.toRadians(entity.headPitch), (float)Math.toRadians(entity.headYaw), (float)Math.toRadians(entity.headRoll));
    }

    void renderArmor(StatueBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {

    }

    // Copied from ArmorFeatureRenderer.java (though modified and made worse)
    private Identifier getArmorTexture(ArmorItem item, boolean legs, @Nullable String overlay) {
        String var10000 = item.getMaterial().getName();
        String string = "textures/models/armor/" + var10000 + "_layer_" + (legs ? 2 : 1) + (overlay == null ? "" : "_" + overlay) + ".png";
        return (Identifier)ARMOR_TEXTURE_CACHE.computeIfAbsent(string, Identifier::new);
    }

    protected void setVisible(EquipmentSlot slot) {
        switch(slot) {
            case HEAD:
                armorModel.getChild(EntityModelPartNames.HEAD).visible = true;
                armorModel.getChild(EntityModelPartNames.HAT).visible = true;
                break;
            case CHEST:
                armorModel.getChild(EntityModelPartNames.BODY).visible = true;
                armorModel.getChild(EntityModelPartNames.RIGHT_ARM).visible = true;
                armorModel.getChild(EntityModelPartNames.LEFT_ARM).visible = true;
                break;
            case LEGS:
                armorModel.getChild(EntityModelPartNames.BODY).visible = true;
                armorModel.getChild(EntityModelPartNames.RIGHT_LEG).visible = true;
                armorModel.getChild(EntityModelPartNames.LEFT_LEG).visible = true;
                break;
            case FEET:
                armorModel.getChild(EntityModelPartNames.RIGHT_LEG).visible = true;
                armorModel.getChild(EntityModelPartNames.LEFT_LEG).visible = true;
        }

    }
    // END copy
}
