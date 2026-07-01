package net.wkhan.naturesaura_plus.datagen;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.wkhan.naturesaura_plus.common.block.ModBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootSubProvider extends BlockLootSubProvider {
    protected ModBlockLootSubProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.STRIPPED_ANCIENT_LOG.get());
        this.dropSelf(ModBlocks.STRIPPED_ANCIENT_BARK.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(java.util.function.Supplier::get)::iterator;
    }
}
