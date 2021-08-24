package net.thegrimsey.statues.util;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelPartNames;

public class BipedModelWrapper {
    public final ModelPart headModel;
    public final ModelPart hatModel;
    public final ModelPart bodyModel;
    public final ModelPart leftArmModel;
    public final ModelPart rightArmModel;
    public final ModelPart leftLegModel;
    public final ModelPart rightLegModel;

    public BipedModelWrapper(ModelPart root) {
        this.headModel = root.getChild(EntityModelPartNames.HEAD);
        this.hatModel = root.getChild(EntityModelPartNames.HAT);
        this.bodyModel = root.getChild(EntityModelPartNames.BODY);
        this.leftArmModel = root.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightArmModel = root.getChild(EntityModelPartNames.RIGHT_ARM);
        this.leftLegModel = root.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightLegModel = root.getChild(EntityModelPartNames.RIGHT_LEG);
    }

    public void hide() {
        headModel.visible = false;
        hatModel.visible = false;
        bodyModel.visible = false;
        leftArmModel.visible = false;
        rightArmModel.visible = false;
        leftLegModel.visible = false;
        rightLegModel.visible = false;
    }
}
