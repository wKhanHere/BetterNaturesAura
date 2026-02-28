package net.wkhan.naturesaura_plus.data;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class AnvilCostRules {

    private static final Map<ResourceLocation, Integer> COSTS = new HashMap<>();

    private AnvilCostRules() {
        // Private constructor to prevent instantiation
    }

    public static void clear() {
        COSTS.clear();
    }

    public static void add(ResourceLocation id, int cost) {
        COSTS.put(id, cost);
    }

    public static Integer getCost(ResourceLocation id) {
        return COSTS.get(id);
    }

    public static Integer size() {
        return COSTS.size();
    }
}
