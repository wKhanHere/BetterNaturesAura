package net.wkhan.naturesaura_plus.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> CANNOT_APPLY_STEEL_TOKEN = tag("cannot_apply_steel_token");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(NaturesAuraPlus.MODID, name));
        }
    }


}
