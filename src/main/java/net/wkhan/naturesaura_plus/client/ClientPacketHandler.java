package net.wkhan.naturesaura_plus.client;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityFlowerGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.wkhan.naturesaura_plus.data.duckfaces.FlowerGeneration;
import net.wkhan.naturesaura_plus.network.packets.S2CPacketFlowerGenUpdate;

public class ClientPacketHandler {

    public static void handleFlowerGenUpdate(S2CPacketFlowerGenUpdate msg) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;
        BlockEntity tile = level.getBlockEntity(msg.blockpos());
        if (!(tile instanceof BlockEntityFlowerGenerator flowerGen)) return;
        ((FlowerGeneration) flowerGen).naturesaura_plus$flowerTileAuraGeneratorSetVitality(msg.vitality());
        ((FlowerGeneration) flowerGen).naturesaura_plus$flowerTileAuraGeneratorWriteFlower(msg.flower());
    }
}
