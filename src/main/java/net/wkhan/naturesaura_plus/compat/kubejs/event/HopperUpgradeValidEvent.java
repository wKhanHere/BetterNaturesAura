package net.wkhan.naturesaura_plus.compat.kubejs.event;


import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.level.block.entity.BlockEntity;

public class HopperUpgradeValidEvent extends EventJS {
    public final BlockEntity blockEntity;
    private boolean valid;

    public HopperUpgradeValidEvent(BlockEntity blockEntity, boolean defaultValid) {
        this.blockEntity = blockEntity;
        this.valid = defaultValid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return this.valid;
    }
}
