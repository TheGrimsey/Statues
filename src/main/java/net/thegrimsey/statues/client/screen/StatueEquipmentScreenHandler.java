package net.thegrimsey.statues.client.screen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.thegrimsey.statues.Statues;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;
import org.jetbrains.annotations.Nullable;

public class StatueEquipmentScreenHandler extends ScreenHandler {
    static class ArmorSlot extends Slot {
        final EquipmentSlot slot;

        public ArmorSlot(Inventory inventory, int index, int x, int y, EquipmentSlot slot) {
            super(inventory, index, x, y);
            this.slot = slot;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() instanceof ArmorItem armorItem && armorItem.getSlotType() == slot;
        }

        @Override
        protected void onTake(int amount) {
            super.onTake(amount);

            ((StatueBlockEntity) inventory).sync();
        }

        @Nullable
        @Override
        public Pair<Identifier, Identifier> getBackgroundSprite() {
            Identifier slotTexture = switch (slot) {
                case HEAD -> PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE;
                case CHEST -> PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE;
                case LEGS -> PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE;
                case FEET -> PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE;
                default -> null;
            };

            return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, slotTexture);
        }
    }

    private final Inventory inventory;

    public StatueEquipmentScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(6));
    }

    public StatueEquipmentScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(Statues.STATUE_EQUIPMENT_SCREENHANDLER, syncId);
        this.inventory = inventory;

        inventory.onOpen(playerInventory.player);

        int column;
        int row;
        // Armor Slots
        for (column = 0; column < 4; ++column) {
            this.addSlot(new ArmorSlot(inventory, column, 80, 61 - column * 18, EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, column)));
        }
        // Held Items
        for (column = 0; column < 2; ++column) {
            this.addSlot(new Slot(inventory, 4 + column, 115 - 70 * column, 34));
        }

        // Player inventory
        for (column = 0; column < 3; ++column) {
            for (row = 0; row < 9; ++row) {
                this.addSlot(new Slot(playerInventory, row + column * 9 + 9, 8 + row * 18, 94 + column * 18));
            }
        }
        // Hot-bar
        for (column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 152));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }
}
