package net.wkhan.naturesaura_plus.data;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;

public final class BlockInteractionRules {

    // Cache: Map specific Block to a list of rules
    private static final Map<Block, List<BlockInteractionRule>> RULE_CACHE = new HashMap<>();

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
            RULE_CACHE.computeIfAbsent(targetBlock, k -> new ArrayList<>()).add(rule);
        }
    }

    public static void sortRules() {
        RULE_CACHE.values().forEach(Collections::sort);
        GLOBAL_RULES.values().forEach(Collections::sort);
    }

    @Nullable
    public static BlockInteractionRule match(ItemStack stack, BlockState state) {

        List<BlockInteractionRule> specificRules = RULE_CACHE.getOrDefault(state.getBlock(), Collections.emptyList());
        List<BlockInteractionRule> globalRules = GLOBAL_RULES.getOrDefault(stack.getItem(), Collections.emptyList());

        for (BlockInteractionRule rule : specificRules) {
                if (rule.matches(stack, state)) return rule;
        }

        for (BlockInteractionRule rule : globalRules) {
            if (rule.matches(stack, null)) return rule;
        }


        return null;
    }
}