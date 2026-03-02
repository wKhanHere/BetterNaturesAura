package net.wkhan.naturesaura_plus.data;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public final class BlockInteractionRules {

    private record BlockItemPair(Block block, Item item) {}

    // Cache: Map specific Block to a list of rules
    private static final Map<BlockItemPair, BlockInteractionRule> RULE_CACHE = new HashMap<>();

    // Globals: Rules that apply to ALL blocks (id was empty in JSON)
    private static final Map<Item, List<BlockInteractionRule>> GLOBAL_RULES = new HashMap<>();

    public static int size() {
        return RULE_CACHE.size() + GLOBAL_RULES.size();
    }

    public static void clear() {
        RULE_CACHE.clear();
        GLOBAL_RULES.clear();
    }

    public static void add(BlockInteractionRule rule) {
        if (!rule.resolve()) return;

        String rawBlockId = rule.getRawBlockId();
        Item targetItem = rule.getTargetItem();
        if (Objects.equals(rawBlockId, "*")) {
            GLOBAL_RULES.computeIfAbsent(targetItem, k -> new ArrayList<>()).add(rule);
            return;
        }

        Block targetBlock = rule.getTargetBlock();
        if (targetBlock != null) {
            BlockItemPair key = new BlockItemPair(targetBlock, targetItem);
            RULE_CACHE.put(key, rule);
        }
    }

    public static void sortRules() {
        GLOBAL_RULES.values().forEach(Collections::sort);
    }

    public static boolean match(ItemStack stack, BlockState state) {

        BlockItemPair key = new BlockItemPair(state.getBlock(), stack.getItem());
        BlockInteractionRule specificRule = RULE_CACHE.get(key);
        List<BlockInteractionRule> globalRules = GLOBAL_RULES.getOrDefault(stack.getItem(), Collections.emptyList());

        for (BlockInteractionRule rule : globalRules) {
            if (rule.matches(stack, null)) return true;
        }

        if(specificRule == null) return false;
        return specificRule.matches(stack, state);
    }
}