package net.wkhan.naturesaura_plus.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
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
                        .add(Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM);

                this.tag(ModTags.Blocks.TREE_RITUAL_STEMS)
                        .addTag(BlockTags.LOGS)
                        .add(
                                Blocks.BROWN_MUSHROOM_BLOCK, //Trust this makes sense in-game
                                Blocks.RED_MUSHROOM_BLOCK, //And this too
                                Blocks.MUSHROOM_STEM
                        );

                this.tag(ModTags.Blocks.TREE_RITUAL_LEAVES)
                        .addTag(BlockTags.LEAVES)
                        .add(
                                Blocks.MANGROVE_ROOTS,
                                Blocks.MUDDY_MANGROVE_ROOTS,
                                Blocks.WARPED_WART_BLOCK,
                                Blocks.NETHER_WART_BLOCK
                        );
        }
}
