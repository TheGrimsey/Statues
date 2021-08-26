package net.thegrimsey.statues.blocks.entity;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.thegrimsey.statues.Statues;
import net.thegrimsey.statues.client.renderer.StatueRenderer;
import net.thegrimsey.statues.client.screen.StatueEquipmentScreenHandler;
import net.thegrimsey.statues.util.StatueRotation;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StatueBlockEntity extends BlockEntity implements BlockEntityClientSerializable, ExtendedScreenHandlerFactory, Inventory {

    // Rotations of body parts.
    public StatueRotation leftLeg;
    public StatueRotation rightLeg;

    public StatueRotation leftArm;
    public StatueRotation rightArm;

    public StatueRotation head;
    // Full body rotation.
    public float yaw = 0.f;

    // Inventory
    final DefaultedList<ItemStack> equipment;

    public Identifier blockTexture = new Identifier("minecraft", "stone");
    UUID profileId;
    @Environment(EnvType.CLIENT)
    GameProfile profile = null;
    @Environment(EnvType.CLIENT)
    float legLength;
    @Environment(EnvType.CLIENT)
    boolean hasEquipment = false;

    boolean finishedEditing = false;

    public StatueBlockEntity(BlockPos pos, BlockState state) {
        super(Statues.STATUE_BLOCKENTITY, pos, state);

        leftLeg = new StatueRotation();
        rightLeg = new StatueRotation();

        leftArm = new StatueRotation();
        rightArm = new StatueRotation();

        head = new StatueRotation();

        equipment = DefaultedList.ofSize(6, ItemStack.EMPTY);
    }

    public boolean editingFinished() {
        return finishedEditing;
    }

    public void markEditingFinished() {
        finishedEditing = true;
    }

    public void updateCache() {
        hasEquipment = equipment.stream().anyMatch(itemStack -> !itemStack.isEmpty());

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
        legLength = Math.max(StatueRenderer.LEG_LENGTH * Math.max(leftDot, rightDot), 0.0F);
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
        readEquipment(nbt);

        if (nbt.containsUuid("profileUUID"))
            setProfileId(nbt.getUuid("profileUUID"));
        else {
            String namespace = nbt.getString("textureNamespace");
            String path = nbt.getString("texturePath");
            blockTexture = new Identifier(namespace, path);
        }

        // If it is loaded from disk it must have finished editing.
        finishedEditing = true;

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
        writeEquipment(nbt);

        if (profileId != null)
            nbt.putUuid("profileUUID", profileId);
        else {
            nbt.putString("textureNamespace", getBlockTexture().getNamespace());
            nbt.putString("texturePath", getBlockTexture().getPath());
        }

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
        readEquipment(nbt);

        if (nbt.containsUuid("profileUUID")) {
            UUID newUUID = nbt.getUuid("profileUUID");

            if (profile == null || newUUID != getProfile().getId()) {
                Util.getMainWorkerExecutor().execute(() -> {
                    GameProfile profile = MinecraftClient.getInstance().getSessionService().fillProfileProperties(new GameProfile(nbt.getUuid("profileUUID"), ""), true);
                    MinecraftClient.getInstance().execute(() -> SkullBlockEntity.loadProperties(profile, gameProfile -> this.profile = gameProfile));
                });
            }
        } else {
            String namespace = nbt.getString("textureNamespace");
            String path = "textures/block/" + nbt.getString("texturePath") + ".png";
            blockTexture = new Identifier(namespace, path);
        }

        updateCache();
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
        writeEquipment(nbt);

        if (profileId != null)
            nbt.putUuid("profileUUID", profileId);
        else {
            nbt.putString("textureNamespace", getBlockTexture().getNamespace());
            nbt.putString("texturePath", getBlockTexture().getPath());
        }

        return nbt;
    }

    void readEquipment(NbtCompound nbt) {
        NbtList equipmentNbt;
        if (nbt.contains("Equipment", 9)) {
            equipmentNbt = nbt.getList("Equipment", 10);

            for (int i = 0; i < equipment.size(); ++i) {
                equipment.set(i, ItemStack.fromNbt(equipmentNbt.getCompound(i)));
            }
        }
    }

    void writeEquipment(NbtCompound nbt) {
        NbtList equipmentNbt = new NbtList();
        for (ItemStack stack : equipment) {
            NbtCompound nbtCompound = new NbtCompound();
            stack.writeNbt(nbtCompound);

            equipmentNbt.add(nbtCompound);
        }
        nbt.put("Equipment", equipmentNbt);
    }

    public DefaultedList<ItemStack> getEquipment() {
        return equipment;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(getPos());
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new StatueEquipmentScreenHandler(syncId, inv, this);
    }

    @Environment(EnvType.CLIENT)
    public GameProfile getProfile() {
        return profile;
    }

    @Environment(EnvType.CLIENT)
    public void setProfile(GameProfile profile) {
        this.profile = profile;
    }

    @Environment(EnvType.CLIENT)
    public float getLegLength() {
        return legLength;
    }

    // INVENTORY
    @Override
    public int size() {
        return equipment.size();
    }

    @Override
    public boolean isEmpty() {
        return equipment.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return equipment.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(equipment, slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(equipment, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        equipment.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack())
            stack.setCount(getMaxCountPerStack());
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        equipment.clear();
    }

    @Override
    public void markDirty() {
        super.markDirty();

        sync();
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public boolean hasEquipment() {
        return hasEquipment;
    }

    public Identifier getBlockTexture() {
        return blockTexture;
    }
}
