package net.thegrimsey.statues.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class StatueRotation {
    // Rotation stored in radians.
    public float pitch, yaw, roll;

    public void writeToBuffer(PacketByteBuf buf) {
        buf.writeFloat(pitch);
        buf.writeFloat(yaw);
        buf.writeFloat(roll);
    }

    public static StatueRotation fromBuffer(PacketByteBuf buf) {
        StatueRotation rotation = new StatueRotation();
        rotation.pitch = buf.readFloat();
        rotation.yaw = buf.readFloat();
        rotation.roll = buf.readFloat();
        return rotation;
    }

    public NbtCompound toNbt() {
        NbtCompound nbtCompound = new NbtCompound();

        nbtCompound.putFloat("Pitch", pitch);
        nbtCompound.putFloat("Yaw", yaw);
        nbtCompound.putFloat("Roll", roll);

        return nbtCompound;
    }

    public static StatueRotation readFromNbt(NbtCompound nbt, String name) {
        StatueRotation statueRotation = new StatueRotation();

        if (nbt.get(name) instanceof NbtCompound nbtCompound) {
            statueRotation.pitch = nbtCompound.getFloat("Pitch");
            statueRotation.yaw = nbtCompound.getFloat("Yaw");
            statueRotation.roll = nbtCompound.getFloat("Roll");
        }

        return statueRotation;
    }
}
