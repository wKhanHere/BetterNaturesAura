package net.wkhan.naturesaura_plus.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.common.block.ModBlocks;
import net.wkhan.naturesaura_plus.common.tag.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {

        public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, NaturesAuraPlus.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
                this.tag(ModTags.Blocks.TREE_RITUAL_SAPLINGS)
                        .addTag(BlockTags.SAPLINGS)
                        .add(
                                Blocks.BROWN_MUSHROOM,
                                Blocks.RED_MUSHROOM
                        );

                this.tag(ModTags.Blocks.TREE_RITUAL_STEMS)
                        .addTag(BlockTags.LOGS)
                        .add(Blocks.MUSHROOM_STEM);

                this.tag(ModTags.Blocks.TREE_RITUAL_LEAVES)
                        .addTag(BlockTags.LEAVES)
                        .add(
                                Blocks.BROWN_MUSHROOM_BLOCK,
                                Blocks.RED_MUSHROOM_BLOCK,
                                Blocks.WARPED_WART_BLOCK,
                                Blocks.NETHER_WART_BLOCK
                        );

                this.tag(ModTags.Blocks.HOPPER_UPGRADE_AFFECTED)
                        .add(
                                Blocks.HOPPER,
                                de.ellpeck.naturesaura.blocks.ModBlocks.GRATED_CHUTE
                        );

                this.tag(ModTags.Blocks.LOOT_FINDER_TREASURE_CHEST)
                        .add(
                                Blocks.CHEST,
                                Blocks.BARREL,
                                Blocks.SPAWNER
                        );

                this.tag(ModTags.Blocks.LOOT_FINDER_TREASURE)
                        .add(
                                Blocks.COPPER_BLOCK,
                                Blocks.IRON_BLOCK,
                                Blocks.GOLD_BLOCK,
                                Blocks.DIAMOND_BLOCK,
                                Blocks.NETHERITE_BLOCK
                        );

                this.tag(ModTags.Blocks.TOWERING_PLANT_SOIL)
                        .add(
                                Blocks.END_STONE
                        );

                this.tag(ModTags.Blocks.TOWERING_PLANT_STEM)
                        .add(
                                Blocks.CHORUS_PLANT
                        );

                this.tag(ModTags.Blocks.TOWERING_PLANT_CAP)
                        .add(
                                Blocks.CHORUS_FLOWER
                        );

                this.tag(ModTags.Blocks.OAK_GEN_SAPLING)
                        .addTag(BlockTags.SAPLINGS);

                this.tag(BlockTags.LOGS)
                        .add(
                                ModBlocks.STRIPPED_ANCIENT_LOG.get(),
                                ModBlocks.STRIPPED_ANCIENT_BARK.get()
                        );
        }
}
