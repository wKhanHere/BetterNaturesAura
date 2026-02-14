package net.wkhan.naturesaura_plus.data;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

public final class EntityInteractionRules {

    // Cache: Map specific EntityType to a list of rules
    private static final Map<EntityType<?>, List<EntityInteractionRule>> RULE_CACHE = new HashMap<>();

    // Globals: Rules that apply to ALL entities (id was empty in JSON)
    private static final List<EntityInteractionRule> GLOBAL_RULES = new ArrayList<>();

    public static int size() {
        return RULE_CACHE.size() + GLOBAL_RULES.size();
    }

    public static void clear() {
        RULE_CACHE.clear();
        GLOBAL_RULES.clear();
    }

    public static void add(EntityInteractionRule rule) {
        // 1. Validate: If resolve fails, it logs the error internally.
        if (!rule.resolve()) return;

        EntityType<?> targetType = rule.getTargetEntityType();
        String rawId = rule.getTargetEntityId();

        // 2. Add to specific cache or global list
        if (targetType != null) {
            RULE_CACHE.computeIfAbsent(targetType, k -> new ArrayList<>()).add(rule);
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
    public static EntityInteractionRule match(ItemStack stack, Entity entity) {
        // 1. Check specific rules (Fast O(1) Lookup)
        List<EntityInteractionRule> specificRules = RULE_CACHE.get(entity.getType());

        if (specificRules != null) {
            for (EntityInteractionRule rule : specificRules) {
                if (rule.matches(stack, entity)) return rule;
            }
        }

        // 2. Check global rules (Iterative)
        for (EntityInteractionRule rule : GLOBAL_RULES) {
            if (rule.matches(stack, entity)) return rule;
        }

        return null;
    }
}