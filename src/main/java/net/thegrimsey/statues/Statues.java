package net.thegrimsey.statues;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.thegrimsey.statues.blocks.StatueBlock;
import net.thegrimsey.statues.blocks.StatueTopBlock;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;
import net.thegrimsey.statues.client.screen.PaletteScreenHandler;
import net.thegrimsey.statues.client.screen.StatueEquipmentScreenHandler;
import net.thegrimsey.statues.client.screen.StatueEditorScreenHandler;
import net.thegrimsey.statues.items.PaletteItem;

public class Statues implements ModInitializer {
	public static final String MODID = "statues";

	// Blocks
	static final Material statueMaterial = new FabricMaterialBuilder(MapColor.STONE_GRAY).blocksPistons().lightPassesThrough().build();
	static final AbstractBlock.Settings statueBlockSettings = FabricBlockSettings.of(statueMaterial).dropsNothing().requiresTool().breakByTool(FabricToolTags.PICKAXES, 0).strength(1.0f).nonOpaque();

	static final Identifier STATUE_ID = new Identifier(MODID, "statue");
	public static final StatueBlock STATUE_BLOCK = new StatueBlock(statueBlockSettings);
	public static BlockEntityType<StatueBlockEntity> STATUE_BLOCKENTITY;

	static final Identifier STATUE_TOP_ID = new Identifier(MODID, "statue_top");
	public static final StatueTopBlock STATUE_TOP_BLOCK = new StatueTopBlock(statueBlockSettings);

	// Items
	public static final PaletteItem PALETTE_ITEM = new PaletteItem();

	// Screens
	public static final ScreenHandlerType<StatueEditorScreenHandler> STATUE_SCREENHANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "statue_editor_screenhandler"), StatueEditorScreenHandler::new);
	public static final ScreenHandlerType<StatueEquipmentScreenHandler> STATUE_EQUIPMENT_SCREENHANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "statue_equipment_screenhandler"), StatueEquipmentScreenHandler::new);
	public static final ScreenHandlerType<PaletteScreenHandler> PALETTE_SCREENHANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "palette_screenhandler"), PaletteScreenHandler::new);

	@Override
	public void onInitialize() {
		// Register Statue block.
		Registry.register(Registry.BLOCK, STATUE_ID, STATUE_BLOCK);
		Registry.register(Registry.ITEM, STATUE_ID, new BlockItem(STATUE_BLOCK, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		STATUE_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "statue_blockentity"), FabricBlockEntityTypeBuilder.create(StatueBlockEntity::new, STATUE_BLOCK).build(null));

		Registry.register(Registry.BLOCK, STATUE_TOP_ID, STATUE_TOP_BLOCK);

		// Register palette item.
		Registry.register(Registry.ITEM, new Identifier(MODID, "palette"), PALETTE_ITEM);

		StatueNetworking.registerNetworking();
	}
}
