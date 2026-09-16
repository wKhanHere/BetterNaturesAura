package net.wkhan.naturesaura_plus.datagen;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.wkhan.naturesaura_plus.common.block.ModBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModBlockLootSubProvider extends BlockLootSubProvider {
    protected ModBlockLootSubProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.AURIC_OVEN_BRICK.get());
        this.dropSelf(ModBlocks.STRIPPED_ANCIENT_LOG.get());
        this.dropSelf(ModBlocks.STRIPPED_ANCIENT_BARK.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return Stream.concat(
                ModBlocks.BLOCKS.getEntries().stream(),
                ModBlocks.NATURES_AURA_BLOCKS.getEntries().stream()
        ).map(Supplier::get).collect(Collectors.toList());
    }
}
