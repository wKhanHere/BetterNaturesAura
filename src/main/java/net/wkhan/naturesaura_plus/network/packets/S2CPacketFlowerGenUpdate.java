package net.wkhan.naturesaura_plus.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkEvent;
import net.wkhan.naturesaura_plus.client.ClientPacketHandler;

import java.util.function.Supplier;

public record S2CPacketFlowerGenUpdate(byte vitality, Block flower, BlockPos blockpos) {

    public S2CPacketFlowerGenUpdate(FriendlyByteBuf buf) {
        this(
                buf.readByte(),
                BuiltInRegistries.BLOCK.byId(buf.readVarInt()),
                buf.readBlockPos());
    }

    public void encodePacket(FriendlyByteBuf buf) {
        buf.writeByte(vitality);
        buf.writeVarInt(BuiltInRegistries.BLOCK.getId(flower));
        buf.writeBlockPos(blockpos);
    }

    public void handlePacket(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> ClientPacketHandler.handleFlowerGenUpdate(this));
        context.setPacketHandled(true);
    }

}
