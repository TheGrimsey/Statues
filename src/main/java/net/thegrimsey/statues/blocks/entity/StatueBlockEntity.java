package net.thegrimsey.statues.blocks.entity;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.thegrimsey.statues.Statues;

import java.util.UUID;

public class StatueBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    // These should be stored in radians once we finish debugging.
    public float leftLegPitch = 0.f, leftLegYaw = 0.f, leftLegRoll = 0.f;
    public float rightLegPitch = 0.f, rightLegYaw = 0.f, rightLegRoll = 0.f;

    public float leftArmPitch = 0.f, leftArmYaw = 0.f, leftArmRoll = 0.f;
    public float rightArmPitch = 0.f, rightArmYaw = 0.f, rightArmRoll = 0.f;

    public float headPitch = 0.f, headYaw = 0.f, headRoll = 0.f;
    public float yaw = 0.f;

    final DefaultedList<ItemStack> heldItems;
    final DefaultedList<ItemStack> armorItems;

    UUID profileId;
    @Environment(EnvType.CLIENT)
    public GameProfile profile = null;

    public StatueBlockEntity(BlockPos pos, BlockState state) {
        super(Statues.STATUE_BLOCKENTITY, pos, state);
        heldItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
        armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);

        armorItems.set(3, Items.DIAMOND_HELMET.getDefaultStack());
        armorItems.set(2, Items.GOLDEN_CHESTPLATE.getDefaultStack());
        armorItems.set(1, Items.IRON_LEGGINGS.getDefaultStack());
        armorItems.set(0, Items.LEATHER_BOOTS.getDefaultStack());
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

        // INVENTORY
        readInventory(nbt);

        if(nbt.containsUuid("profileUUID"))
            profileId = nbt.getUuid("profileUUID");

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

        // INVENTORY
        writeInventory(nbt);

        if(profileId != null)
            nbt.putUuid("profileUUID", profileId);

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

        // INVENTORY
        readInventory(nbt);

        if(nbt.containsUuid("profileUUID")) {
            UUID newUUID = nbt.getUuid("profileUUID");

            if(newUUID != profile.getId()) {
                profile = new GameProfile(nbt.getUuid("profileUUID"), "");
                SkullBlockEntity.loadProperties(profile, gameProfile -> this.profile = gameProfile);
            }
        }

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

        // INVENTORY
        writeInventory(nbt);

        if(profileId != null)
            nbt.putUuid("profileUUID", profileId);

        return nbt;
    }

    void readInventory(NbtCompound nbt) {
        NbtList itemNbts;
        if (nbt.contains("ArmorItems", 9)) {
            itemNbts = nbt.getList("ArmorItems", 10);

            for(int i = 0; i < this.getArmorItems().size(); ++i) {
                this.getArmorItems().set(i, ItemStack.fromNbt(itemNbts.getCompound(i)));
            }
        }

        if (nbt.contains("HandItems", 9)) {
            itemNbts = nbt.getList("HandItems", 10);

            for(int i = 0; i < this.getHeldItems().size(); ++i) {
                this.getHeldItems().set(i, ItemStack.fromNbt(itemNbts.getCompound(i)));
            }
        }
    }

    void writeInventory(NbtCompound nbt) {
        NbtList armorItemsNbt = new NbtList();
        for(ItemStack stack : getArmorItems()) {
            NbtCompound nbtCompound = new NbtCompound();
            if (!stack.isEmpty()) {
                stack.writeNbt(nbtCompound);
            }
            armorItemsNbt.add(nbtCompound);
        }
        nbt.put("ArmorItems", armorItemsNbt);

        NbtList heldItemsNbt = new NbtList();
        for(ItemStack stack : getHeldItems()) {
            NbtCompound nbtCompound = new NbtCompound();
            if (!stack.isEmpty()) {
                stack.writeNbt(nbtCompound);
            }
            heldItemsNbt.add(nbtCompound);
        }
        nbt.put("HandItems", heldItemsNbt);
    }

    public DefaultedList<ItemStack> getHeldItems() {
        return heldItems;
    }
    public DefaultedList<ItemStack> getArmorItems() {
        return armorItems;
    }
}
