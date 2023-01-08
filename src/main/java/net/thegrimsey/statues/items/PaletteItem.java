package net.thegrimsey.statues.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thegrimsey.statues.Statues;
import net.thegrimsey.statues.client.screen.PaletteScreenHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PaletteItem extends Item {
    // Is this a bit cursed? I have no opinion on this.
    record PaletteScreenFactory(BlockPos targetStatue) implements ExtendedScreenHandlerFactory {

        @Override
        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
            buf.writeBlockPos(targetStatue);
        }

        @Override
        public Text getDisplayName() {
            return Text.translatable("statues.palette.title");
        }

        @Override
        public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            return new PaletteScreenHandler(syncId, inv, targetStatue);
        }
    }

    public PaletteItem() {
        super(new FabricItemSettings().maxCount(1));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(this));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        // Get world. Check if statue. If so pop up UI.
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        if(context.getPlayer() != null) {
            if (state.isOf(Statues.STATUE_BLOCK)) {
                context.getPlayer().openHandledScreen(new PaletteScreenFactory(context.getBlockPos()));
            } else if (state.isOf(Statues.STATUE_TOP_BLOCK))
                context.getPlayer().openHandledScreen(new PaletteScreenFactory(context.getBlockPos().down()));
        }

        return super.useOnBlock(context);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.statues.palette.lore"));
    }
}
