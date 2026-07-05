package net.wkhan.naturesaura_plus.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.wkhan.naturesaura_plus.network.packets.S2CPacketFlowerGenUpdate;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath("naturesaura_plus", "main_channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        int id = 0;
        CHANNEL.registerMessage(id++, S2CPacketFlowerGenUpdate.class,
                S2CPacketFlowerGenUpdate::encodePacket, S2CPacketFlowerGenUpdate::new, S2CPacketFlowerGenUpdate::handlePacket);
    }
}
