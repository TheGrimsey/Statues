package net.thegrimsey.statues.networking;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;
import net.thegrimsey.statues.util.StatueRotation;

public class EditStatueChannelHandler implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();

        StatueRotation leftArm = StatueRotation.fromBuffer(buf);
        StatueRotation rightArm = StatueRotation.fromBuffer(buf);

        StatueRotation leftLeg = StatueRotation.fromBuffer(buf);
        StatueRotation rightLeg = StatueRotation.fromBuffer(buf);

        StatueRotation head = StatueRotation.fromBuffer(buf);

        float yaw = buf.readFloat();

        server.execute(() -> {
            boolean canReach = player.squaredDistanceTo((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D;
            if(canReach && player.getServerWorld().getBlockEntity(pos) instanceof StatueBlockEntity blockEntity) {
                if(!blockEntity.editingFinished()) {
                    blockEntity.leftArm = leftArm;
                    blockEntity.rightArm = rightArm;

                    blockEntity.leftLeg = leftLeg;
                    blockEntity.rightLeg = rightLeg;

                    blockEntity.head = head;

                    blockEntity.yaw = yaw;

                    blockEntity.sync();
                }
            }
        });
    }
}
