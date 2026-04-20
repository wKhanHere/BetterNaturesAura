package net.wkhan.naturesaura_plus.common.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> CANNOT_APPLY_BREAK_PREVENTION = tag("cannot_apply_break_prevention");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(NaturesAuraPlus.MODID, name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> TREE_RITUAL_SAPLINGS = tag("tree_ritual_saplings");
        public static final TagKey<Block> TREE_RITUAL_STEMS = tag("tree_ritual_stems");
        public static final TagKey<Block> TREE_RITUAL_LEAVES = tag("tree_ritual_leaves");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(NaturesAuraPlus.MODID, name));
        }
    }
}
