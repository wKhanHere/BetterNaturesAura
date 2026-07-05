package net.wkhan.naturesaura_plus.data.duckfaces;

import net.minecraft.world.level.block.Block;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;

public interface FlowerGeneration {
    void naturesaura_plus$flowerTileAuraGeneratorSetVitality(byte vitality);
    void naturesaura_plus$flowerTileAuraGeneratorWriteFlower(Block flower);
    int naturesaura_plus$flowerTileAuraGeneratorReadVitality();
    NaturesAuraPlusUtils.circularBuffer<Block> naturesaura_plus$flowerTileAuraGeneratorReadBuffer();
}
