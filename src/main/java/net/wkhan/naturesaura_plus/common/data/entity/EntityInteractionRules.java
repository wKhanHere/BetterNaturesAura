package net.wkhan.naturesaura_plus.common.data.entity;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class EntityInteractionRules {

    private record EntityItemPair(Object entityType, Object item) {}
    private static final Map<EntityItemPair, EntityInteractionRule> RULE_CACHE = new HashMap<>();
    private static final Map<Object, List<EntityInteractionRule>> GLOBAL_RULES = new HashMap<>();

    private static final Set<TagKey<EntityType<?>>> EXPECTED_ENTITY_TAGS = new HashSet<>();
    private static final Set<TagKey<Item>> EXPECTED_ITEM_TAGS = new HashSet<>();

    public static int size() {
        return RULE_CACHE.size() + GLOBAL_RULES.size();
    }

    public static void clear() {
        RULE_CACHE.clear();
        GLOBAL_RULES.clear();
    }

    public static void add(EntityInteractionRule rule) {
        if (!rule.resolve()) return;

        String rawEntityId = rule.getRawEntityId();
        String rawItemId = rule.getRawItemId(); //Global item rules dont exist yet
        Object targetItem = rule.getTargetItem();
        if (targetItem == null) {
            TagKey<Item> itemTag = rule.getTargetItemTag();
            targetItem = itemTag;
            EXPECTED_ITEM_TAGS.add(itemTag);
        }
        Object targetEntity = rule.getTargetEntity();
        if (targetEntity == null) {
            TagKey<EntityType<?>> entityTag = rule.getTargetEntityTag();
            targetEntity = entityTag;
            EXPECTED_ENTITY_TAGS.add(entityTag);
        }

        if (Objects.equals(rawEntityId, "*")) {
            GLOBAL_RULES.computeIfAbsent(targetItem, k -> new ArrayList<>()).add(rule);
            return;
        }

        if (targetEntity != null && targetItem != null) {
            EntityItemPair key = new EntityItemPair(targetEntity, targetItem);
            RULE_CACHE.put(key, rule);
        } //May wanna add an error log here.
    }

    public static boolean match(ItemStack stack, Entity entity) {

        Item item = stack.getItem();
        EntityType<?> entityType = entity.getType();
        EntityItemPair key = new EntityItemPair(entityType, item);
        EntityInteractionRule specificRule = RULE_CACHE.get(key);

        if (specificRule != null) {
            return specificRule.matches(stack, entityType);
        }
        List<TagKey<Item>> validItemTags = stack.getTags().filter(EXPECTED_ITEM_TAGS::contains).toList();
        List<TagKey<EntityType<?>>> validEntityTags = entity.getType().getTags().filter(EXPECTED_ENTITY_TAGS::contains).toList();

        for (TagKey<Item> itemTag: validItemTags) {
            key = new EntityItemPair(entityType, itemTag);
            specificRule = RULE_CACHE.get(key);
            if (specificRule != null) return specificRule.matches(stack, entityType);
        }

        for (TagKey<EntityType<?>> entityTag: validEntityTags) {
            key = new EntityItemPair(entityTag, item);
            specificRule = RULE_CACHE.get(key);
            if (specificRule != null) return specificRule.matches(stack, entityType);
        }

        for (TagKey<EntityType<?>> entityTag: validEntityTags) {
            for (TagKey<Item> itemTag: validItemTags) {
                key = new EntityItemPair(entityTag, itemTag);
                specificRule = RULE_CACHE.get(key);
                if (specificRule != null) return specificRule.matches(stack, entityType);
            }
        }

        List<EntityInteractionRule> globalRules = GLOBAL_RULES.getOrDefault(stack.getItem(), Collections.emptyList());
        for (EntityInteractionRule rule : globalRules) {
            if (rule.matches(stack, null)) return true;
        }
        return false;
    }
}