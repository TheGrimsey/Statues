package net.thegrimsey.statues.blocks.entity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.thegrimsey.statues.Statues;

public class StatueBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    // These should be stored in radians once we finish debugging.
    public float leftLegPitch = 0.f, leftLegYaw = 0.f, leftLegRoll = 0.f;
    public float rightLegPitch = 0.f, rightLegYaw = 0.f, rightLegRoll = 0.f;

    public float leftArmPitch = 0.f, leftArmYaw = 0.f, leftArmRoll = 0.f;
    public float rightArmPitch = 0.f, rightArmYaw = 0.f, rightArmRoll = 0.f;

    public float headPitch = 0.f, headYaw = 0.f, headRoll = 0.f;
    public float yaw = 0.f;

    public StatueBlockEntity(BlockPos pos, BlockState state) {
        super(Statues.STATUE_BLOCKENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        // LEGS
        leftLegPitch = nbt.getFloat("leftLegPitch");
        leftLegYaw = nbt.getFloat("leftLegYaw");
        leftLegRoll = nbt.getFloat("leftLegRoll");

        rightLegPitch = nbt.getFloat("rightLegPitch");
        rightLegYaw = nbt.getFloat("rightLegYaw");
        rightLegRoll = nbt.getFloat("rightLegRoll");

        // ARMS
        leftArmPitch = nbt.getFloat("leftArmPitch");
        leftArmYaw = nbt.getFloat("leftArmYaw");
        leftArmRoll = nbt.getFloat("leftArmRoll");

        rightArmPitch = nbt.getFloat("rightArmPitch");
        rightArmYaw = nbt.getFloat("rightArmYaw");
        rightArmRoll = nbt.getFloat("rightArmRoll");

        // HEAD
        headPitch = nbt.getFloat("headPitch");
        headYaw = nbt.getFloat("headYaw");
        headRoll = nbt.getFloat("headRoll");

        // FULL BODY
        yaw = nbt.getFloat("yaw");

        super.readNbt(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // LEGS
        nbt.putFloat("leftLegPitch", leftLegPitch);
        nbt.putFloat("leftLegYaw", leftLegYaw);
        nbt.putFloat("leftLegRoll", leftLegRoll);

        nbt.putFloat("rightLegPitch", rightLegPitch);
        nbt.putFloat("rightLegYaw", rightLegYaw);
        nbt.putFloat("rightLegRoll", rightLegRoll);

        // ARMS
        nbt.putFloat("leftArmPitch", leftArmPitch);
        nbt.putFloat("leftArmYaw", leftArmYaw);
        nbt.putFloat("leftArmRoll", leftArmRoll);

        nbt.putFloat("rightArmPitch", rightArmPitch);
        nbt.putFloat("rightArmYaw", rightArmYaw);
        nbt.putFloat("rightArmRoll", rightArmRoll);

        // HEAD
        nbt.putFloat("headPitch", headPitch);
        nbt.putFloat("headYaw", headYaw);
        nbt.putFloat("headRoll", headRoll);

        // FULL BODY
        nbt.putFloat("yaw", yaw);

        return super.writeNbt(nbt);
    }

    @Override
    public void fromClientTag(NbtCompound nbt) {
        // LEGS
        leftLegPitch = nbt.getFloat("leftLegPitch");
        leftLegYaw = nbt.getFloat("leftLegYaw");
        leftLegRoll = nbt.getFloat("leftLegRoll");

        rightLegPitch = nbt.getFloat("rightLegPitch");
        rightLegYaw = nbt.getFloat("rightLegYaw");
        rightLegRoll = nbt.getFloat("rightLegRoll");

        // ARMS
        leftArmPitch = nbt.getFloat("leftArmPitch");
        leftArmYaw = nbt.getFloat("leftArmYaw");
        leftArmRoll = nbt.getFloat("leftArmRoll");

        rightArmPitch = nbt.getFloat("rightArmPitch");
        rightArmYaw = nbt.getFloat("rightArmYaw");
        rightArmRoll = nbt.getFloat("rightArmRoll");

        // HEAD
        headPitch = nbt.getFloat("headPitch");
        headYaw = nbt.getFloat("headYaw");
        headRoll = nbt.getFloat("headRoll");

        // FULL BODY
        yaw = nbt.getFloat("yaw");
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt) {
        // LEGS
        nbt.putFloat("leftLegPitch", leftLegPitch);
        nbt.putFloat("leftLegYaw", leftLegYaw);
        nbt.putFloat("leftLegRoll", leftLegRoll);

        nbt.putFloat("rightLegPitch", rightLegPitch);
        nbt.putFloat("rightLegYaw", rightLegYaw);
        nbt.putFloat("rightLegRoll", rightLegRoll);

        // ARMS
        nbt.putFloat("leftArmPitch", leftArmPitch);
        nbt.putFloat("leftArmYaw", leftArmYaw);
        nbt.putFloat("leftArmRoll", leftArmRoll);

        nbt.putFloat("rightArmPitch", rightArmPitch);
        nbt.putFloat("rightArmYaw", rightArmYaw);
        nbt.putFloat("rightArmRoll", rightArmRoll);

        // HEAD
        nbt.putFloat("headPitch", headPitch);
        nbt.putFloat("headYaw", headYaw);
        nbt.putFloat("headRoll", headRoll);

        // FULL BODY
        nbt.putFloat("yaw", yaw);
        return nbt;
    }
}
