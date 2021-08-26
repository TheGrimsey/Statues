package net.thegrimsey.statues.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.thegrimsey.statues.Statues;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;
import net.thegrimsey.statues.client.screen.StatueEditorScreenHandler;
import org.jetbrains.annotations.NotNull;

public class StatueHammerItem extends Item {
    record StatueEditorScreenFactory(BlockPos targetStatue, float startYaw) implements ExtendedScreenHandlerFactory {

        @Override
        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
            buf.writeBlockPos(targetStatue);
            buf.writeFloat(startYaw);
        }

        @Override
        public Text getDisplayName() {
            return new TranslatableText("statues.palette.title");
        }

        @Override
        public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            return new StatueEditorScreenHandler(syncId, inv, targetStatue);
        }
    }

    public StatueHammerItem() {
        super(new FabricItemSettings().maxCount(1).group(ItemGroup.DECORATIONS));
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        // Get world. Check if statue. If so pop up UI.
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        BlockState aboveState = context.getWorld().getBlockState(context.getBlockPos().up());

        if(state.isIn(Statues.STATUABLE_TAG) && aboveState.isOf(state.getBlock())) {
            World world = context.getWorld();

            // Set blocks.
            world.setBlockState(context.getBlockPos(), Statues.STATUE_BLOCK.getDefaultState(), Block.NOTIFY_ALL);
            world.setBlockState(context.getBlockPos().up(), Statues.STATUE_TOP_BLOCK.getDefaultState(), Block.NOTIFY_ALL);

            // Calculate yaw.
            float yaw = (float) Math.toRadians((int) ((context.getPlayer().getYaw() + 180 + 45) % 360) / 90 * 90);

            if (world.getBlockEntity(context.getBlockPos()) instanceof StatueBlockEntity blockEntity) {
                blockEntity.blockTexture = Registry.BLOCK.getId(state.getBlock());
                blockEntity.yaw = yaw;
            }

            context.getPlayer().openHandledScreen(new StatueEditorScreenFactory(context.getBlockPos(), yaw));
        }

        return super.useOnBlock(context);
    }
}
