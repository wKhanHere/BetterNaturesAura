package net.wkhan.naturesaura_plus.data.duckfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public interface AbstractWoodStand {
    BlockState naturesaura_plus$getWoodStandMaterialBlockState();
    void naturesaura_plus$setWoodStandMaterialBlockState(BlockState material);
    void naturesaura_plus$setTreeStemCache(Set<BlockPos> treeCache);
    void naturesaura_plus$setTreeLeafCache(Set<BlockPos> treeCache);
    void naturesaura_plus$setTreeDecoratorCache(Set<BlockPos> treeCache);
}
