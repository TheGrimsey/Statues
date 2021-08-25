package net.thegrimsey.statues.client.renderer;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;
import net.thegrimsey.statues.util.BipedModelWrapper;

import java.util.Map;

public class StatueRenderer implements BlockEntityRenderer<StatueBlockEntity> {
    public static final float LEG_LENGTH = 12.f;

    // Copied from ArmorFeatureRenderer.java
    private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();
    // Copied from EntityModels.java
    private static final Dilation ARMOR_DILATION = new Dilation(1.0F);
    private static final Dilation LEG_DILATION = new Dilation(0.5F);

    public final ModelPart blockModel;
    public final BipedModelWrapper blockModelWrapper;

    public final ModelPart playerModel;
    public final BipedModelWrapper playerModelWrapper;
    public final ModelPart slimPlayerModel;
    public final BipedModelWrapper slimPlayerModelWrapper;

    public final ModelPart armorModel;
    public final BipedModelWrapper armorModelWrapper;
    public final ModelPart legArmorModel;
    public final BipedModelWrapper legArmorModelWrapper;

    public StatueRenderer(BlockEntityRendererFactory.Context context) {
        // Block model
        TexturedModelData texturedModelData = TexturedModelData.of(BipedEntityModel.getModelData(Dilation.NONE, 0.0F), 16, 16);
        blockModel = texturedModelData.createModel();
        blockModelWrapper = new BipedModelWrapper(blockModel);

        // Player models
        texturedModelData = TexturedModelData.of(PlayerEntityModel.getTexturedModelData(Dilation.NONE, false), 64, 64);
        playerModel = texturedModelData.createModel();
        playerModelWrapper = new BipedModelWrapper(playerModel);

        texturedModelData = TexturedModelData.of(PlayerEntityModel.getTexturedModelData(Dilation.NONE, true), 64, 64);
        slimPlayerModel = texturedModelData.createModel();
        slimPlayerModelWrapper = new BipedModelWrapper(slimPlayerModel);

        // Armor models
        texturedModelData = TexturedModelData.of(BipedEntityModel.getModelData(ARMOR_DILATION, 0.0F), 64, 32);
        armorModel = texturedModelData.createModel();
        armorModelWrapper = new BipedModelWrapper(armorModel);

        texturedModelData = TexturedModelData.of(BipedEntityModel.getModelData(LEG_DILATION, 0.0F), 64, 32);
        legArmorModel = texturedModelData.createModel();
        legArmorModelWrapper = new BipedModelWrapper(legArmorModel);
    }

    @Override
    public void render(StatueBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        float legLength = entity.getLegLength();

        matrices.translate(0.5, 1.5 - (12.f/16f) + (legLength/16f), 0.5);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.f));
        matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(entity.yaw));

        // Set angles.
        updateAngles(armorModelWrapper, entity);
        updateAngles(legArmorModelWrapper, entity);

        // Render as player
        if(entity.getProfile() != null) {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = MinecraftClient.getInstance().getSkinProvider().getTextures(entity.getProfile());
            boolean slim = map.containsKey(MinecraftProfileTexture.Type.SKIN) && map.get(MinecraftProfileTexture.Type.SKIN).getMetadata("model") != null || DefaultSkinHelper.getModel(PlayerEntity.getUuidFromProfile(entity.getProfile())).equals("slim");

            ModelPart model = slim ? slimPlayerModel : playerModel;
            BipedModelWrapper wrapper = slim ? slimPlayerModelWrapper : playerModelWrapper;

            updateAngles(wrapper, entity);
            updatePlayerAngles(model, entity);

            // Render base model.
            model.render(matrices, vertexConsumers.getBuffer(getRenderLayer(entity)), light, overlay);
        } else { // Render as rock
            updateAngles(blockModelWrapper, entity);

            blockModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(new Identifier("minecraft", "textures/block/stone.png"))), light, overlay);
        }

        renderArmor(entity, matrices, vertexConsumers, light, EquipmentSlot.HEAD);
        renderArmor(entity, matrices, vertexConsumers, light, EquipmentSlot.CHEST);
        renderArmor(entity, matrices, vertexConsumers, light, EquipmentSlot.LEGS);
        renderArmor(entity, matrices, vertexConsumers, light, EquipmentSlot.FEET);

        renderHandItems(entity, matrices, vertexConsumers, light, overlay);
    }

    void updateAngles(BipedModelWrapper wrapper, StatueBlockEntity entity)
    {
        wrapper.leftLegModel.setAngles(entity.leftLeg.pitch, entity.leftLeg.yaw, entity.leftLeg.roll);
        wrapper.rightLegModel.setAngles(entity.rightLeg.pitch, entity.rightLeg.yaw, entity.rightLeg.roll);

        wrapper.leftArmModel.setAngles(entity.leftArm.pitch, entity.leftArm.yaw, entity.leftArm.roll);
        wrapper.rightArmModel.setAngles(entity.rightArm.pitch, entity.rightArm.yaw, entity.rightArm.roll);

        wrapper.headModel.setAngles(entity.head.pitch, entity.head.yaw, entity.head.roll);
        wrapper.hatModel.setAngles(entity.head.pitch, entity.head.yaw, entity.head.roll);
    }

    void updatePlayerAngles(ModelPart model, StatueBlockEntity entity) {
        model.getChild("left_pants").setAngles(entity.leftLeg.pitch, entity.leftLeg.yaw, entity.leftLeg.roll);
        model.getChild("right_pants").setAngles(entity.rightLeg.pitch, entity.rightLeg.yaw, entity.rightLeg.roll);

        model.getChild("left_sleeve").setAngles(entity.leftArm.pitch, entity.leftArm.yaw, entity.leftArm.roll);
        model.getChild("right_sleeve").setAngles(entity.rightArm.pitch, entity.rightArm.yaw, entity.rightArm.roll);
    }

    void renderHandItems(StatueBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        Quaternion quaternion = Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F);
        Quaternion quaternion1 = Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F);
        {
            matrices.push();
            legArmorModelWrapper.leftArmModel.rotate(matrices); // Rotating using legArmorModel because it is always updated.
            matrices.multiply(quaternion);
            matrices.multiply(quaternion1);
            matrices.translate(-1.0F / 16.0F, 0.125D, -0.625D);
            MinecraftClient.getInstance().getItemRenderer().renderItem(null, entity.getStack(4), ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, true, matrices, vertexConsumers, entity.getWorld(), light, overlay, 0);
            matrices.pop();
        }
        {
            matrices.push();
            legArmorModelWrapper.rightArmModel.rotate(matrices);
            matrices.multiply(quaternion);
            matrices.multiply(quaternion1);
            matrices.translate(1.0F / 16.0F, 0.125D, -0.625D);
            MinecraftClient.getInstance().getItemRenderer().renderItem(null, entity.getStack(5), ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, false, matrices, vertexConsumers, entity.getWorld(), light, overlay, 0);
            matrices.pop();
        }
    }

    // Copied from ArmorFeatureRenderer.java (though modified and made worse)
    void renderArmor(StatueBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EquipmentSlot slot)
    {
        ItemStack itemStack = entity.getEquipment().get(slot.getEntitySlotId());
        if (itemStack.getItem() instanceof ArmorItem armorItem) {
            if (armorItem.getSlotType() == slot) {
                ModelPart modelToRender = slot == EquipmentSlot.LEGS ? legArmorModel : armorModel;
                setArmorVisible(slot == EquipmentSlot.LEGS ? legArmorModelWrapper : armorModelWrapper, slot);

                boolean usesSecondLayer = slot == EquipmentSlot.LEGS;
                boolean hasGlint = itemStack.hasGlint();

                if (armorItem instanceof DyeableArmorItem) {
                    int i = ((DyeableArmorItem)armorItem).getColor(itemStack);

                    float r = (float)(i >> 16 & 255) / 255.0F;
                    float g = (float)(i >> 8 & 255) / 255.0F;
                    float b = (float)(i & 255) / 255.0F;

                    this.renderArmorParts(matrices, vertexConsumers, light, armorItem, hasGlint, modelToRender, usesSecondLayer, r, g, b, false);
                    this.renderArmorParts(matrices, vertexConsumers, light, armorItem, hasGlint, modelToRender, usesSecondLayer, 1.0F, 1.0F, 1.0F, true);
                } else {
                    this.renderArmorParts(matrices, vertexConsumers, light, armorItem, hasGlint, modelToRender, usesSecondLayer, 1.0F, 1.0F, 1.0F, false);
                }
            }
        }
    }

    void renderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem armorItem, boolean hasGlint, ModelPart model, boolean usesSecondLayer, float r, float g, float b, boolean overlay) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(armorItem, usesSecondLayer, overlay)), false, hasGlint);
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, r, g, b, 1.0F);
    }

    private Identifier getArmorTexture(ArmorItem item, boolean legs, boolean overlay) {
        String materialName = item.getMaterial().getName();
        String string = "textures/models/armor/" + materialName + "_layer_" + (legs ? 2 : 1) + (overlay ? "_overlay" : "") + ".png";
        return ARMOR_TEXTURE_CACHE.computeIfAbsent(string, Identifier::new);
    }

    protected void setArmorVisible(BipedModelWrapper modelToRender, EquipmentSlot slot) {
        modelToRender.hide();

        switch (slot) {
            case HEAD -> {
                modelToRender.headModel.visible = true;
                modelToRender.hatModel.visible = true;
            }
            case CHEST -> {
                modelToRender.bodyModel.visible = true;
                modelToRender.rightArmModel.visible = true;
                modelToRender.leftArmModel.visible = true;
            }
            case LEGS -> {
                modelToRender.bodyModel.visible = true;
                modelToRender.rightLegModel.visible = true;
                modelToRender.leftLegModel.visible = true;
            }
            case FEET -> {
                modelToRender.rightLegModel.visible = true;
                modelToRender.leftLegModel.visible = true;
            }
        }
    }
    // END copy

    RenderLayer getRenderLayer(StatueBlockEntity entity) {
        if(entity.getProfile() != null) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().getTextures(entity.getProfile());
            return map.containsKey(MinecraftProfileTexture.Type.SKIN) ? RenderLayer.getEntityTranslucent(minecraftClient.getSkinProvider().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN)) : RenderLayer.getEntityCutoutNoCull(DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(entity.getProfile())));
        }

        return RenderLayer.getEntityCutout(Registry.BLOCK.getId(Blocks.STONE));
    }
}
