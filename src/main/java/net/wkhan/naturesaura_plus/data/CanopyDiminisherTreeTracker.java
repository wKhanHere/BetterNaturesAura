package net.wkhan.naturesaura_plus.data;

import net.minecraft.core.BlockPos;

public class CanopyDiminisherTreeTracker {
    public static final ThreadLocal<BlockPos> OAK_GEN_POS = ThreadLocal.withInitial(() -> null);
}
