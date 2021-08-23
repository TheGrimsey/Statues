package net.thegrimsey.statues.client.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.thegrimsey.statues.Statues;

public class StatueScreenHandler extends ScreenHandler {
    final ScreenHandlerContext context;

    public StatueScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(Statues.STATUE_SCREENHANDLER, syncId);

        context = ScreenHandlerContext.create(playerInventory.player.world, buf.readBlockPos());
    }

    public StatueScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        super(Statues.STATUE_SCREENHANDLER, syncId);

        context = ScreenHandlerContext.create(playerInventory.player.world, pos);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(context, player, Statues.STATUE_BLOCK);
    }
}
