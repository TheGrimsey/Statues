package net.thegrimsey.statues;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;
import net.thegrimsey.statues.networking.EditStatueChannelHandler;

public class StatueNetworking {
    private static final Identifier EDIT_STATUE = new Identifier(Statues.MODID, "edit_statue");
    private static final EditStatueChannelHandler EDIT_STATUE_CHANNEL_HANDLER = new EditStatueChannelHandler();

    public static void registerNetworking() {
        ServerPlayNetworking.registerGlobalReceiver(EDIT_STATUE, EDIT_STATUE_CHANNEL_HANDLER);
    }

    public static void sendEditStatuePacket(BlockPos statuePos, StatueBlockEntity blockEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeBlockPos(statuePos);

        // Left Arm.
        blockEntity.leftArm.writeToBuffer(buf);
        // Right Arm.
        blockEntity.rightArm.writeToBuffer(buf);
        // Left Leg.
        blockEntity.leftLeg.writeToBuffer(buf);
        // Right Leg.
        blockEntity.rightLeg.writeToBuffer(buf);
        // Head
        blockEntity.head.writeToBuffer(buf);
        // Body
        buf.writeFloat(blockEntity.yaw);

        ClientPlayNetworking.send(EDIT_STATUE, buf);
    }
}
