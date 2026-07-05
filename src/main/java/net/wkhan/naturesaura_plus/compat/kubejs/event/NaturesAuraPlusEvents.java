package net.wkhan.naturesaura_plus.compat.kubejs.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public class NaturesAuraPlusEvents {

    public static final EventGroup NATURES_AURA_PLUS_GROUP = EventGroup.of("NaturesAuraPlus");

    public static final EventHandler HOPPER_UPGRADE_BASE_CHECK = NATURES_AURA_PLUS_GROUP.server("hopperUpgradeBaseCheck", () -> HopperUpgradeValidEvent.class);
}
