package net.thegrimsey.statues.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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

    static final VoxelShape statueCuboid = VoxelShapes.cuboid(0, 0, 0, 1, 2, 1);

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return statueCuboid;
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
                drops.addAll(Registry.BLOCK.get(statueBlockEntity.blockId).getDefaultState().getDroppedStacks(builder));
                drops.addAll(Registry.BLOCK.get(statueBlockEntity.blockId).getDefaultState().getDroppedStacks(builder));
            }
            drops.addAll(statueBlockEntity.getEquipment());
        }
        return drops;
    }
}
