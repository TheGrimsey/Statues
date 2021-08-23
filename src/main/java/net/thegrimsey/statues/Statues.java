package net.thegrimsey.statues;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.thegrimsey.statues.blocks.StatueBlock;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;
import net.thegrimsey.statues.client.screen.StatueScreenHandler;

public class Statues implements ModInitializer {
	public static final String MODID = "statues";

	Identifier STATUE_ID = new Identifier(MODID, "statue");
	public static final StatueBlock STATUE_BLOCK = new StatueBlock();
	public static BlockEntityType<StatueBlockEntity> STATUE_BLOCKENTITY;

	public static final ScreenHandlerType<StatueScreenHandler> STATUE_SCREENHANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "statue_screenhandler"), StatueScreenHandler::new);

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, STATUE_ID, STATUE_BLOCK);
		Registry.register(Registry.ITEM, STATUE_ID, new BlockItem(STATUE_BLOCK, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		STATUE_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "statue_blockentity"), FabricBlockEntityTypeBuilder.create(StatueBlockEntity::new, STATUE_BLOCK).build(null));
	}
}
