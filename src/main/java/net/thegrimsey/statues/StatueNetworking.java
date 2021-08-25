package net.thegrimsey.statues;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.thegrimsey.statues.blocks.entity.StatueBlockEntity;
import net.thegrimsey.statues.networking.EditStatueChannelHandler;
import net.thegrimsey.statues.networking.SendPaletteChannelHandler;

import java.util.UUID;

public class StatueNetworking {
    private static final Identifier EDIT_STATUE = new Identifier(Statues.MODID, "edit_statue");
    private static final EditStatueChannelHandler EDIT_STATUE_CHANNEL_HANDLER = new EditStatueChannelHandler();

    private static final Identifier SEND_PALETTE = new Identifier(Statues.MODID, "send_palette");
    private static final SendPaletteChannelHandler SEND_PALETTE_CHANNEL_HANDLER = new SendPaletteChannelHandler();

    public static void registerNetworking() {
        ServerPlayNetworking.registerGlobalReceiver(EDIT_STATUE, EDIT_STATUE_CHANNEL_HANDLER);
        ServerPlayNetworking.registerGlobalReceiver(SEND_PALETTE, SEND_PALETTE_CHANNEL_HANDLER);
    }

    @Environment(EnvType.CLIENT)
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

    @Environment(EnvType.CLIENT)
    public static void sendSendPalette(BlockPos statuePos, UUID id) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeBlockPos(statuePos);
        buf.writeUuid(id);

        ClientPlayNetworking.send(SEND_PALETTE, buf);
    }
}
