package net.wkhan.naturesaura_plus.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> CANNOT_APPLY_BREAK_PREVENTION = tag("cannot_apply_break_prevention");
        public static final TagKey<Item> VALID_WOODEN_STAND_MATERIAL = tag("valid_wooden_stand_material");
        public static final TagKey<Item> VALID_SMELTABLE_TO_BOOST = tag("valid_smeltable_to_boost");

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(NaturesAuraPlus.MODID, name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> TREE_RITUAL_SAPLINGS = tag("tree_ritual_saplings");
        public static final TagKey<Block> TREE_RITUAL_STEMS = tag("tree_ritual_stems");
        public static final TagKey<Block> TREE_RITUAL_LEAVES = tag("tree_ritual_leaves");
        public static final TagKey<Block> HOPPER_UPGRADE_AFFECTED = tag("hopper_upgrade_affected");
        public static final TagKey<Block> LOOT_FINDER_TREASURE = tag("loot_finder_treasure");
        public static final TagKey<Block> LOOT_FINDER_TREASURE_CHEST = tag("loot_finder_treasure_chest");
        public static final TagKey<Block> TOWERING_PLANT_SOIL = tag("towering_plant_soil");
        public static final TagKey<Block> TOWERING_PLANT_STEM = tag("towering_plant_stem");
        public static final TagKey<Block> TOWERING_PLANT_CAP = tag("towering_plant_cap");
        public static final TagKey<Block> OAK_GEN_SAPLING = tag("oak_gen_sapling");
        public static final TagKey<Block> TREE_FERTILIZER_SAFE_IN_RITUAL = tag("tree_fertilizer_safe_in_ritual");
        public static final TagKey<Block> EXCLUDE_IN_TREE_RITUAL_CLEANUP = tag("exclude_in_tree_ritual_cleanup");
        public static final TagKey<Block> STRIPPED_LOGS = TagKey.create(Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath("forge", "stripped_logs"));
        public static final TagKey<Block> STRIPPED_WOOD = TagKey.create(Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath("forge", "stripped_wood"));
        public static final TagKey<Block> FURNACE_FOR_BOOSTER = tag("furnace_for_booster");


        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(NaturesAuraPlus.MODID, name));
        }
    }

    public static class Entities {
        public static final TagKey<EntityType<?>> ANIMAL = tag("animal");

        private static TagKey<EntityType<?>> tag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(NaturesAuraPlus.MODID, name));
        }
    }
}
