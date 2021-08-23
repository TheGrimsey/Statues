package net.thegrimsey.statues;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.thegrimsey.statues.client.renderer.StatueRenderer;
import net.thegrimsey.statues.client.screen.StatueScreen;

public class StatuesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(Statues.STATUE_BLOCKENTITY, StatueRenderer::new);

        ScreenRegistry.register(Statues.STATUE_SCREENHANDLER, StatueScreen::new);
    }
}
