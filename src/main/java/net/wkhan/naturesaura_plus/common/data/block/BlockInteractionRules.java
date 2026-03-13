package net.wkhan.naturesaura_plus.common.data.block;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public final class BlockInteractionRules {

    private record BlockItemPair(Object block, Object item) {}
    private static final Map<BlockItemPair, BlockInteractionRule> RULE_CACHE = new HashMap<>();

    private static final Set<TagKey<Block>> EXPECTED_BLOCK_TAGS = new HashSet<>();
    private static final Set<TagKey<Item>> EXPECTED_ITEM_TAGS = new HashSet<>();

    public static int size() {
        return RULE_CACHE.size();
    }

    public static void clear() {
        RULE_CACHE.clear();
    }

    public static void add(BlockInteractionRule rule) {
        if (!rule.resolve()) return;

        String rawBlockId = rule.getRawBlockId();
        String rawItemId = rule.getRawItemId(); //Global item rules dont exist yet
        Object targetItem = rule.getTargetItem();
        if (targetItem == null) {
            TagKey<Item> itemTag = rule.getTargetItemTag();
            targetItem = itemTag;
            EXPECTED_ITEM_TAGS.add(itemTag);
        }
        Object targetBlock = rule.getTargetBlock();
        if (targetBlock == null) {
            TagKey<Block> blockTag = rule.getTargetBlockTag();
            targetBlock = blockTag;
            EXPECTED_BLOCK_TAGS.add(blockTag);
        }

        if (Objects.equals(rawBlockId, "*")) {
            BlockItemPair key = new BlockItemPair("*", targetItem);
            RULE_CACHE.put(key, rule);
            return;
        }

        if (Objects.equals(rawItemId, "*")) {
            BlockItemPair key = new BlockItemPair(targetBlock, "*");
            RULE_CACHE.put(key, rule);
            return;
        }

        if (targetBlock != null && targetItem != null) {
            BlockItemPair key = new BlockItemPair(targetBlock, targetItem);
            RULE_CACHE.put(key, rule);
        } //May wanna add an error log here.
    }

    public static boolean match(ItemStack stack, BlockState state) {

        Item item = stack.getItem();
        Block block = state.getBlock();
        BlockItemPair key = new BlockItemPair(block, item);
        BlockInteractionRule specificRule = RULE_CACHE.get(key);

        if (specificRule != null) {
            return specificRule.matches(stack, state);
        }

        key = new BlockItemPair("*", item);
        specificRule = RULE_CACHE.get(key);
        if (specificRule != null) return specificRule.matches(stack, null);

        key = new BlockItemPair(block, "*");
        specificRule = RULE_CACHE.get(key);
        if (specificRule != null) return specificRule.matches(null, state);

        List<TagKey<Item>> validItemTags = stack.getTags().filter(EXPECTED_ITEM_TAGS::contains).toList();
        List<TagKey<Block>> validBlockTags = state.getTags().filter(EXPECTED_BLOCK_TAGS::contains).toList();

        for (TagKey<Item> itemTag: validItemTags) {
            key = new BlockItemPair(block, itemTag);
            specificRule = RULE_CACHE.get(key);
            if (specificRule != null) return specificRule.matches(stack, state);

            key = new BlockItemPair("*", itemTag);
            specificRule = RULE_CACHE.get(key);
            if (specificRule != null) return specificRule.matches(stack, null);
        }

        for (TagKey<Block> blockTag: validBlockTags) {
            key = new BlockItemPair(blockTag, item);
            specificRule = RULE_CACHE.get(key);
            if (specificRule != null) return specificRule.matches(stack, state);

            key = new BlockItemPair(blockTag, "*");
            specificRule = RULE_CACHE.get(key);
            if (specificRule != null) return specificRule.matches(null, state);
        }

        for (TagKey<Block> blockTag: validBlockTags) {
            for (TagKey<Item> itemTag: validItemTags) {
                key = new BlockItemPair(blockTag, itemTag);
                specificRule = RULE_CACHE.get(key);
                if (specificRule != null) return specificRule.matches(stack, state);
            }
        }

        return false;
    }
}