package net.wkhan.naturesaura_plus.compat.kubejs;

import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.wkhan.naturesaura_plus.compat.kubejs.event.HopperUpgradeValidEvent;
import net.wkhan.naturesaura_plus.compat.kubejs.event.NaturesAuraPlusEvents;

public class KubeJSNaturesAuraPlus {

    public static boolean HopperUpgradeBaseCheck(BlockEntity tile, boolean defaultValid) {
        HopperUpgradeValidEvent event = new HopperUpgradeValidEvent(tile, defaultValid);
        NaturesAuraPlusEvents.HOPPER_UPGRADE_BASE_CHECK.post(ScriptType.SERVER, event);
        return event.isValid();
    }
}
