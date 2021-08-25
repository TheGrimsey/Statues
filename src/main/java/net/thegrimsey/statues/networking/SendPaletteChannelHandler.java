package net.thegrimsey.statues.networking;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;

import java.util.UUID;

public class SendPaletteChannelHandler implements ServerPlayNetworking.PlayChannelHandler{
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        UUID id = buf.readUuid();

        server.execute(() -> {
            boolean canReach = player.squaredDistanceTo((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
            if(canReach && player.getServerWorld().getBlockEntity(pos) instanceof StatueBlockEntity blockEntity) {
                blockEntity.setProfileId(id);
                blockEntity.sync();
            }
        });
    }
}
