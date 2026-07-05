package net.wkhan.naturesaura_plus.data;

import net.minecraft.core.BlockPos;

import java.util.Set;

public class TreeRitualTreeTracker {
    public static final ThreadLocal<Set<BlockPos>> STEM_CACHE = new ThreadLocal<>();
    public static final ThreadLocal<Set<BlockPos>> LEAF_CACHE = new ThreadLocal<>();
    public static final ThreadLocal<Set<BlockPos>> DECORATOR_CACHE = new ThreadLocal<>();
}
