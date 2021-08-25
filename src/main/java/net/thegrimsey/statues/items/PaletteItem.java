package net.thegrimsey.statues.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.thegrimsey.statues.Statues;
import net.thegrimsey.statues.client.screen.PaletteScreenHandler;
import org.jetbrains.annotations.Nullable;

public class PaletteItem extends Item {
    // Is this a bit cursed? I have no opinion on this.
    static class PaletteScreenFactory implements ExtendedScreenHandlerFactory {
        BlockPos targetStatue;

        public PaletteScreenFactory(BlockPos targetStatue) {
            this.targetStatue = targetStatue;
        }

        @Override
        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
            buf.writeBlockPos(targetStatue);
        }

        @Override
        public Text getDisplayName() {
            return new TranslatableText("statues.palette.title");
        }

        @Nullable
        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            return new PaletteScreenHandler(syncId, inv, targetStatue);
        }
    }

    public PaletteItem() {
        super(new FabricItemSettings().maxCount(1).group(ItemGroup.DECORATIONS));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        // Get world. Check if statue. If so pop up UI.
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        if(state.isOf(Statues.STATUE_BLOCK)) {
            context.getPlayer().openHandledScreen(new PaletteScreenFactory(context.getBlockPos()));
        }

        return super.useOnBlock(context);
    }
}
