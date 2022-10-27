package net.thegrimsey.statues.networking;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.thegrimsey.statues.Statues;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;
import net.thegrimsey.statues.util.StatueRotation;

public class EditStatueChannelHandler implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos;
        StatueRotation leftArm;
        StatueRotation rightArm;
        StatueRotation leftLeg;
        StatueRotation rightLeg;
        StatueRotation head;
        float yaw;
        float scale;
        try {
            pos = buf.readBlockPos();

            leftArm = StatueRotation.fromBuffer(buf);
            rightArm = StatueRotation.fromBuffer(buf);

            leftLeg = StatueRotation.fromBuffer(buf);
            rightLeg = StatueRotation.fromBuffer(buf);

            head = StatueRotation.fromBuffer(buf);

            yaw = buf.readFloat();
            scale = buf.readFloat();
        } catch (IndexOutOfBoundsException exception) {
            exception.printStackTrace();
            return; // TODO log that this was an error in packet.
        }

        server.execute(() -> {
            boolean canReach = player.squaredDistanceTo((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
            if (canReach && player.getWorld().getBlockEntity(pos) instanceof StatueBlockEntity blockEntity) {
                if (!blockEntity.editingFinished()) {
                    blockEntity.leftArm = leftArm;
                    blockEntity.rightArm = rightArm;

                    blockEntity.leftLeg = leftLeg;
                    blockEntity.rightLeg = rightLeg;

                    blockEntity.head = head;

                    blockEntity.yaw = yaw;
                    blockEntity.scale = MathHelper.clamp(scale, Statues.MIN_STATUE_SCALE, Statues.MAX_STATUE_SCALE);

                    blockEntity.markEditingFinished();
                    blockEntity.sync();
                    blockEntity.markDirty();
                }
            }
        });
    }
}
