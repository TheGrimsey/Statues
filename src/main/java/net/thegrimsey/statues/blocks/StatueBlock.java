package net.thegrimsey.statues.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof StatueBlockEntity statueBlockEntity) {
            if (statueBlockEntity.editingFinished() && player.getMainHandStack().isEmpty()) {
                player.openHandledScreen(createScreenHandlerFactory(state, world, pos));

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    static final VoxelShape statueCuboid = VoxelShapes.cuboid(2/16d, 0, 2/16d, 14/16d, 2, 14/16d);

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return statueCuboid;
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return super.getCullingShape(state, world, pos);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        world.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();

        if (builder.get(LootContextParameters.BLOCK_ENTITY) instanceof StatueBlockEntity statueBlockEntity) {
            if(statueBlockEntity.blockId != null) {
                BlockState defaultState = Registry.BLOCK.get(statueBlockEntity.blockId).getDefaultState();
                // Check if we can break the block and if so drop it.
                if(builder.get(LootContextParameters.TOOL).isSuitableFor(defaultState) || !defaultState.isToolRequired()) {
                    drops.addAll(defaultState.getDroppedStacks(builder));
                    drops.addAll(defaultState.getDroppedStacks(builder));
                }
            }
            drops.addAll(statueBlockEntity.getEquipment());
        }
        return drops;
    }
}
