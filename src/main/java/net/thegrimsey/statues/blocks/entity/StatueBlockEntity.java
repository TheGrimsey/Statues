package net.thegrimsey.statues.blocks.entity;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
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
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.thegrimsey.statues.Statues;
import net.thegrimsey.statues.client.renderer.StatueRenderer;
import net.thegrimsey.statues.client.screen.StatueEquipmentScreenHandler;
import net.thegrimsey.statues.util.StatueRotation;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
import java.util.UUID;

public class StatueBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, Inventory {

    // Rotations of body parts.
    public StatueRotation leftLeg;
    public StatueRotation rightLeg;

    public StatueRotation leftArm;
    public StatueRotation rightArm;

    public StatueRotation head;
    // Full body rotation.
    public float yaw = 0.f;

    // Scale
    public float scale = 1.0f;

    // Inventory
    final DefaultedList<ItemStack> equipment;

    public Identifier blockId = new Identifier("minecraft", "stone");
    UUID profileId;

    @Environment(EnvType.CLIENT)
    GameProfile profile = null;
    @Environment(EnvType.CLIENT)
    float legLength;
    @Environment(EnvType.CLIENT)
    boolean hasEquipment = false;
    @Environment(EnvType.CLIENT)
    public Identifier blockTexture = new Identifier("minecraft", "textures/block/stone.png");

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

        // LEFT LEG leftLeg.pitch, leftLeg.yaw, leftLeg.roll, false
        Quaternionf leftLegRot = new Quaternionf();
        leftLegRot.rotateAxis(leftLeg.pitch, 0.0f, 0.0f, 1.0f);
        leftLegRot.rotateAxis(leftLeg.yaw, 0.0f, 1.0f, 0.0f);
        leftLegRot.rotateAxis(leftLeg.roll, 1.0f, 0.0f, 0.0f);
        Vector3f down = new Vector3f(0.0f, -1.0f, 0.0f);
        down.rotate(leftLegRot);
        float leftDot = down.dot(0.0f, -1.0f, 0.0f);

        // RIGHT LEG rightLeg.pitch, rightLeg.yaw, rightLeg.roll,
        Quaternionf rightLegRot = new Quaternionf();
        rightLegRot.rotateAxis(rightLeg.pitch, 0.0f, 0.0f, 1.0f);
        rightLegRot.rotateAxis(rightLeg.yaw, 0.0f, 1.0f, 0.0f);
        rightLegRot.rotateAxis(rightLeg.roll, 1.0f, 0.0f, 0.0f);
        down.set(0.0f, -1.0f, 0.0f);
        down.rotate(rightLegRot);
        float rightDot = down.dot(0.0f, -1.0f, 0.0f);

        // Straightest leg is base.
        legLength = Math.max(StatueRenderer.LEG_LENGTH * Math.max(leftDot, rightDot), 0.0F);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        // LEGS
        leftLeg = StatueRotation.readFromNbt(nbt, "leftLeg");
        rightLeg = StatueRotation.readFromNbt(nbt, "rightLeg");

        leftArm = StatueRotation.readFromNbt(nbt, "leftArm");
        rightArm = StatueRotation.readFromNbt(nbt, "rightArm");

        head = StatueRotation.readFromNbt(nbt, "head");

        // FULL BODY
        yaw = nbt.getFloat("yaw");
        if(nbt.contains("scale"))
            scale = nbt.getFloat("scale");

        // INVENTORY
        readEquipment(nbt);

        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            if (nbt.containsUuid("profileUUID")) {
                UUID newUUID = nbt.getUuid("profileUUID");

                if (profile == null || newUUID != getProfile().getId()) {
                    Util.getMainWorkerExecutor().execute(() -> {
                        GameProfile profile = MinecraftClient.getInstance().getSessionService().fillProfileProperties(new GameProfile(nbt.getUuid("profileUUID"), ""), true);
                        MinecraftClient.getInstance().execute(() -> SkullBlockEntity.loadProperties(profile, gameProfile -> this.profile = gameProfile));
                    });
                }
            } else {
                blockId = new Identifier(nbt.getString("textureNamespace"), nbt.getString("texturePath"));
                Identifier spriteId = MinecraftClient.getInstance().getBlockRenderManager().getModel(Registries.BLOCK.get(blockId).getDefaultState()).getParticleSprite().getContents().getId();

                blockTexture = new Identifier(spriteId.getNamespace(), "textures/" + spriteId.getPath() + ".png");
            }

            updateCache();
        }

        if (nbt.containsUuid("profileUUID"))
            setProfileId(nbt.getUuid("profileUUID"));

        String namespace = nbt.getString("textureNamespace");
        String path = nbt.getString("texturePath");
        blockId = new Identifier(namespace, path);

        // If it is loaded from disk it must have finished editing.
        finishedEditing = true;
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.put("leftLeg", leftLeg.toNbt());
        nbt.put("rightLeg", rightLeg.toNbt());

        nbt.put("leftArm", leftArm.toNbt());
        nbt.put("rightArm", rightArm.toNbt());

        nbt.put("head", head.toNbt());

        // FULL BODY
        nbt.putFloat("yaw", yaw);
        nbt.putFloat("scale", scale);

        // INVENTORY
        writeEquipment(nbt);

        if (profileId != null)
            nbt.putUuid("profileUUID", profileId);

        nbt.putString("textureNamespace", blockId.getNamespace());
        nbt.putString("texturePath", blockId.getPath());
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

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this, BlockEntity::createNbt);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
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
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
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
            sync();
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        sync();
        markDirty();

        return Inventories.removeStack(equipment, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        equipment.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack())
            stack.setCount(getMaxCountPerStack());

        sync();
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        equipment.clear();
    }

    public void sync() {
        ((ServerWorld)world).getChunkManager().markForUpdate(pos);
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    @Environment(EnvType.CLIENT)
    public boolean hasEquipment() {
        return hasEquipment;
    }

    @Environment(EnvType.CLIENT)
    public Identifier getBlockTexture() {
        return blockTexture;
    }
}
