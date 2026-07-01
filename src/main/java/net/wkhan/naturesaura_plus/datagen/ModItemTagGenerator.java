package net.wkhan.naturesaura_plus.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.common.item.ModItems;
import net.wkhan.naturesaura_plus.common.tag.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {
    public ModItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> p_275322_,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, p_275322_, NaturesAuraPlus.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(ModTags.Items.CANNOT_APPLY_BREAK_PREVENTION).add(Items.ELYTRA);

        this.tag(ModTags.Items.VALID_WOODEN_STAND_MATERIAL).addTag(ItemTags.LOGS);

        this.tag(ItemTags.LOGS).add(
                ModItems.STRIPPED_ANCIENT_LOG.get(),
                ModItems.STRIPPED_ANCIENT_BARK.get()
        );
    }
}
