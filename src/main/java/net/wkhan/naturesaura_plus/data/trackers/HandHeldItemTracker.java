package net.wkhan.naturesaura_plus.data.trackers;

public class HandHeldItemTracker {
    public static final ThreadLocal<Boolean> MASK_HAND_EMPTY = ThreadLocal.withInitial(() -> false);
}
