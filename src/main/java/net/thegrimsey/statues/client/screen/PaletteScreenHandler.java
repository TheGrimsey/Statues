package net.thegrimsey.statues.client.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.thegrimsey.statues.Statues;

public class PaletteScreenHandler extends ScreenHandler {
    final ScreenHandlerContext context;
    BlockPos statuePos;

    public PaletteScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(Statues.PALETTE_SCREENHANDLER, syncId);

        statuePos = buf.readBlockPos();

        context = null;
    }

    public PaletteScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        super(Statues.PALETTE_SCREENHANDLER, syncId);

        context = ScreenHandlerContext.create(playerInventory.player.world, pos);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(context, player, Statues.STATUE_BLOCK);
    }
}
