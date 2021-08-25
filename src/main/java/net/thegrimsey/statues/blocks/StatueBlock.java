package net.thegrimsey.statues.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.thegrimsey.statues.Statues;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;
import org.jetbrains.annotations.Nullable;

public class StatueBlock extends BlockWithEntity {
    public StatueBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StatueBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if(world.isClient())
            return;
        world.setBlockState(pos.up(), Statues.STATUE_TOP_BLOCK.getDefaultState(), Block.NOTIFY_ALL);


        if(placer instanceof PlayerEntity playerEntity) {
            if(world.getBlockEntity(pos) instanceof StatueBlockEntity blockEntity)
                blockEntity.yaw = (float) Math.toRadians((int)((placer.getYaw() + 180 + 45) % 360) / 90 * 90); // Snap to closest 90 degrees.
            playerEntity.openHandledScreen(createScreenHandlerFactory(state, world, pos));
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.getBlockEntity(pos) instanceof StatueBlockEntity statueBlockEntity) {
            if(statueBlockEntity.editingFinished() && player.getMainHandStack().isEmpty()) {
                player.openHandledScreen(createScreenHandlerFactory(state, world, pos));

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        world.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
    }

    static final VoxelShape statueCuboid = VoxelShapes.cuboid(0,0,0,1,2,1);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return statueCuboid;
    }

}
