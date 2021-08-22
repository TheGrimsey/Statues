package net.thegrimsey.statues;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.thegrimsey.statues.client.renderer.StatueRenderer;

public class StatuesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(Statues.STATUE_BLOCKENTITY, StatueRenderer::new);
    }
}
