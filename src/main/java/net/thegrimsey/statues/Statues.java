package net.thegrimsey.statues;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.thegrimsey.statues.blocks.StatueBlock;
import net.thegrimsey.statues.blocks.StatueTopBlock;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;
import net.thegrimsey.statues.client.screen.PaletteScreenHandler;
import net.thegrimsey.statues.client.screen.StatueEditorScreenHandler;
import net.thegrimsey.statues.client.screen.StatueEquipmentScreenHandler;
import net.thegrimsey.statues.items.PaletteItem;
import net.thegrimsey.statues.items.StatueHammerItem;

public class Statues implements ModInitializer {
    public static final String MODID = "statues";

    // Tags
    public static final TagKey<Block> NOT_STATUABLE_TAG = TagKey.of(Registries.BLOCK.getKey(), new Identifier(MODID, "not_statuable")); // Blocks you can't turn into a statue.

    // Blocks
    static final Material statueMaterial = new FabricMaterialBuilder(MapColor.STONE_GRAY).blocksPistons().lightPassesThrough().build();
    static final AbstractBlock.Settings statueBlockSettings = FabricBlockSettings.of(statueMaterial).dropsNothing().requiresTool().strength(8.0f).nonOpaque();

    public static final StatueBlock STATUE_BLOCK = new StatueBlock(statueBlockSettings);
    public static final StatueTopBlock STATUE_TOP_BLOCK = new StatueTopBlock(statueBlockSettings);
    public static BlockEntityType<StatueBlockEntity> STATUE_BLOCKENTITY;

    // Items
    public static final PaletteItem PALETTE_ITEM = new PaletteItem();
    public static final StatueHammerItem HAMMER_ITEM = new StatueHammerItem();

    // Screens
    public static final ScreenHandlerType<StatueEditorScreenHandler> STATUE_SCREENHANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "statue_editor_screenhandler"), StatueEditorScreenHandler::new);
    public static final ScreenHandlerType<StatueEquipmentScreenHandler> STATUE_EQUIPMENT_SCREENHANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "statue_equipment_screenhandler"), StatueEquipmentScreenHandler::new);
    public static final ScreenHandlerType<PaletteScreenHandler> PALETTE_SCREENHANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "palette_screenhandler"), PaletteScreenHandler::new);

    // Bounds
    public static float MIN_STATUE_SCALE = 0.25f, MAX_STATUE_SCALE = 1.3f;

    @Override
    public void onInitialize() {
        // Register Statue block.
        Registry.register(Registries.BLOCK, new Identifier(MODID, "statue"), STATUE_BLOCK);
        STATUE_BLOCKENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MODID, "statue_blockentity"), FabricBlockEntityTypeBuilder.create(StatueBlockEntity::new, STATUE_BLOCK).build(null));

        Registry.register(Registries.BLOCK, new Identifier(MODID, "statue_top"), STATUE_TOP_BLOCK);

        // Register palette item.
        Registry.register(Registries.ITEM, new Identifier(MODID, "palette"), PALETTE_ITEM);
        Registry.register(Registries.ITEM, new Identifier(MODID, "hammer"), HAMMER_ITEM);

        StatueNetworking.registerNetworking();
    }
}
