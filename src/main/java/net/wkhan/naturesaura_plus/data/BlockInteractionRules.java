package net.wkhan.naturesaura_plus.data;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;

public final class BlockInteractionRules {

    // Cache: Map specific Block to a list of rules
    private static final Map<Block, List<BlockInteractionRule>> RULE_CACHE = new HashMap<>();

    // Globals: Rules that apply to ALL blocks (id was empty in JSON)
    private static final List<BlockInteractionRule> GLOBAL_RULES = new ArrayList<>();

    public static int size() {
        return RULE_CACHE.size() + GLOBAL_RULES.size();
    }

    public static void clear() {
        RULE_CACHE.clear();
        GLOBAL_RULES.clear();
    }

    public static void add(BlockInteractionRule rule) {
        // 1. Validate: If resolve fails, it logs the error internally.
        if (!rule.resolve()) return;

        Block targetBlock = rule.getTargetBlock();
        String rawId = rule.getRawBlockId();

        // 2. Add to specific cache or global list
        if (targetBlock != null) {
            RULE_CACHE.computeIfAbsent(targetBlock, k -> new ArrayList<>()).add(rule);
        }
        else if (rawId == null || rawId.isEmpty()) {
            GLOBAL_RULES.add(rule);
        }
    }

    public static void sortRules() {
        RULE_CACHE.values().forEach(Collections::sort);
        Collections.sort(GLOBAL_RULES);
    }

    @Nullable
    public static BlockInteractionRule match(ItemStack stack, BlockState state) {
        // 1. Check specific rules (Fast O(1) Lookup)
        List<BlockInteractionRule> specificRules = RULE_CACHE.get(state.getBlock());

        if (specificRules != null) {
            for (BlockInteractionRule rule : specificRules) {
                if (rule.matches(stack, state)) return rule;
            }
        }

        // 2. Check global rules (Iterative)
        for (BlockInteractionRule rule : GLOBAL_RULES) {
            if (rule.matches(stack, state)) return rule;
        }

        return null;
    }
}