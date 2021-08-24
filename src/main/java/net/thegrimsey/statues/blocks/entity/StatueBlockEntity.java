package net.thegrimsey.statues.blocks.entity;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.thegrimsey.statues.Statues;
import net.thegrimsey.statues.client.renderer.StatueRenderer;
import net.thegrimsey.statues.client.screen.StatueScreenHandler;
import net.thegrimsey.statues.util.StatueRotation;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StatueBlockEntity extends BlockEntity implements BlockEntityClientSerializable, ExtendedScreenHandlerFactory {

    // Rotations of body parts.
    public StatueRotation leftLeg;
    public StatueRotation rightLeg;

    public StatueRotation leftArm;
    public StatueRotation rightArm;

    public StatueRotation head;
    // Full body rotation.
    public float yaw = 0.f;

    // Inventory
    final DefaultedList<ItemStack> heldItems;
    final DefaultedList<ItemStack> armorItems;

    UUID profileId;
    @Environment(EnvType.CLIENT)
    GameProfile profile = null;
    @Environment(EnvType.CLIENT)
    float legLength;

    public StatueBlockEntity(BlockPos pos, BlockState state) {
        super(Statues.STATUE_BLOCKENTITY, pos, state);

        leftLeg = new StatueRotation();
        rightLeg = new StatueRotation();

        leftArm = new StatueRotation();
        rightArm = new StatueRotation();

        head = new StatueRotation();

        heldItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
        armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);

        armorItems.set(3, Items.DIAMOND_HELMET.getDefaultStack());
        armorItems.set(2, Items.GOLDEN_CHESTPLATE.getDefaultStack());
        armorItems.set(1, Items.IRON_LEGGINGS.getDefaultStack());
        armorItems.set(0, Items.LEATHER_BOOTS.getDefaultStack());
    }

    public boolean editingFinished() {
        return false;
    }

    public void recalculateLegLength() {
        // LEFT LEG
        Quaternion leftLegRot = new Quaternion(leftLeg.pitch, leftLeg.yaw, leftLeg.roll, false);
        Vec3f down = Vec3f.NEGATIVE_Y.copy();
        down.rotate(leftLegRot);
        float leftDot = down.dot(Vec3f.NEGATIVE_Y);

        // RIGHT LEG
        Quaternion rightLegRot = new Quaternion(rightLeg.pitch, rightLeg.yaw, rightLeg.roll, false);
        down.set(Vec3f.NEGATIVE_Y);
        down.rotate(rightLegRot);
        float rightDot = down.dot(Vec3f.NEGATIVE_Y);

        // Straightest leg is base.
        legLength = StatueRenderer.LEG_LENGTH * Math.max(leftDot, rightDot);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        // LEGS
        leftLeg = StatueRotation.readFromNbt(nbt, "leftLeg");
        rightLeg = StatueRotation.readFromNbt(nbt, "rightLeg");

        leftArm = StatueRotation.readFromNbt(nbt, "leftArm");
        rightArm = StatueRotation.readFromNbt(nbt, "rightArm");

        head = StatueRotation.readFromNbt(nbt, "head");

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
        nbt.put("leftLeg", leftLeg.toNbt());
        nbt.put("rightLeg", rightLeg.toNbt());

        nbt.put("leftArm", leftArm.toNbt());
        nbt.put("rightArm", rightArm.toNbt());

        nbt.put("head", head.toNbt());

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
        leftLeg = StatueRotation.readFromNbt(nbt, "leftLeg");
        rightLeg = StatueRotation.readFromNbt(nbt, "rightLeg");

        leftArm = StatueRotation.readFromNbt(nbt, "leftArm");
        rightArm = StatueRotation.readFromNbt(nbt, "rightArm");

        head = StatueRotation.readFromNbt(nbt, "head");

        // FULL BODY
        yaw = nbt.getFloat("yaw");

        // INVENTORY
        readInventory(nbt);

        if(nbt.containsUuid("profileUUID")) {
            UUID newUUID = nbt.getUuid("profileUUID");

            if(newUUID != getProfile().getId()) {
                profile = new GameProfile(nbt.getUuid("profileUUID"), "");
                SkullBlockEntity.loadProperties(getProfile(), gameProfile -> this.profile = gameProfile);
            }
        }

        recalculateLegLength();
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt) {
        nbt.put("leftLeg", leftLeg.toNbt());
        nbt.put("rightLeg", rightLeg.toNbt());

        nbt.put("leftArm", leftArm.toNbt());
        nbt.put("rightArm", rightArm.toNbt());

        nbt.put("head", head.toNbt());

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

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(getPos());
        buf.writeFloat(yaw);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new StatueScreenHandler(syncId, inv, getPos());
    }

    public GameProfile getProfile() {
        return profile;
    }

    public float getLegLength() {
        return legLength;
    }
}
