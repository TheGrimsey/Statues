package net.thegrimsey.statues.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.thegrimsey.statues.Statues;

public class StatueEditorScreenHandler extends ScreenHandler {
    final ScreenHandlerContext context;

    @Environment(EnvType.CLIENT)
    float startYaw;
    @Environment(EnvType.CLIENT)
    BlockPos statuePos;

    public StatueEditorScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(Statues.STATUE_SCREENHANDLER, syncId);

        statuePos = buf.readBlockPos();
        startYaw = buf.readFloat();
        context = null;
    }

    public StatueEditorScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        super(Statues.STATUE_SCREENHANDLER, syncId);

        context = ScreenHandlerContext.create(playerInventory.player.world, pos);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(context, player, Statues.STATUE_BLOCK);
    }
}
