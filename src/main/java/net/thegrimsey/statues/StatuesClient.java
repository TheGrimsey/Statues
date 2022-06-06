package net.thegrimsey.statues;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.thegrimsey.statues.client.renderer.StatueRenderer;
import net.thegrimsey.statues.client.screen.PaletteScreen;
import net.thegrimsey.statues.client.screen.StatueEditorScreen;
import net.thegrimsey.statues.client.screen.StatueEquipmentScreen;

public class StatuesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register block entity renderers.
        BlockEntityRendererRegistry.register(Statues.STATUE_BLOCKENTITY, StatueRenderer::new);

        // Register screens
        ScreenRegistry.register(Statues.STATUE_SCREENHANDLER, StatueEditorScreen::new);
        ScreenRegistry.register(Statues.STATUE_EQUIPMENT_SCREENHANDLER, StatueEquipmentScreen::new);
        ScreenRegistry.register(Statues.PALETTE_SCREENHANDLER, PaletteScreen::new);
    }
}
