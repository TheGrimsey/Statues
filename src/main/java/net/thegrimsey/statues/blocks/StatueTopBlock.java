package net.thegrimsey.statues.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
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

import java.util.Optional;

@SuppressWarnings("deprecation")
public class StatueTopBlock extends Block {
    public StatueTopBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        world.breakBlock(pos.down(), true, player);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    static final VoxelShape statueCuboid = VoxelShapes.cuboid(2/16d, -1, 2/16d, 14/16d, 1, 14/16d);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return statueCuboid;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockPos downPos = pos.down();
        if (world.getBlockEntity(downPos) instanceof StatueBlockEntity statueBlockEntity) {
            if (statueBlockEntity.editingFinished() && player.getMainHandStack().isEmpty()) {
                player.openHandledScreen(Statues.STATUE_BLOCK.createScreenHandlerFactory(state, world, downPos));

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos.down());

        if(blockEntity instanceof StatueBlockEntity statueBlockEntity) {
            Optional<Block> statueBlock = Registries.BLOCK.getOrEmpty(statueBlockEntity.blockId);
            if(statueBlock.isPresent()) {
                return statueBlock.get().calcBlockBreakingDelta(statueBlock.get().getDefaultState(), player, world, pos);
            }
        }

        return super.calcBlockBreakingDelta(state, player, world, pos);
    }
}
