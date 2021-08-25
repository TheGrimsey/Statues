package net.thegrimsey.statues.blocks;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
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

public class StatueTopBlock extends Block {
    public StatueTopBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        world.setBlockState(pos.down(), Blocks.AIR.getDefaultState());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    static final VoxelShape statueCuboid = VoxelShapes.cuboid(0,-1,0,1,1,1);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return statueCuboid;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockPos downPos = pos.down();
        if(world.getBlockEntity(downPos) instanceof StatueBlockEntity statueBlockEntity) {
            if(statueBlockEntity.editingFinished() && player.getMainHandStack().isEmpty()) {
                player.openHandledScreen(Statues.STATUE_BLOCK.createScreenHandlerFactory(state, world, downPos));

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }
}
