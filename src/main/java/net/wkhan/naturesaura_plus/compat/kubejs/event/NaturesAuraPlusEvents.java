package net.wkhan.naturesaura_plus.compat.kubejs.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public class NaturesAuraPlusEvents {

    public static final EventGroup NATURES_AURA_PLUS_GROUP = EventGroup.of("NaturesAuraPlus");

    public static final EventHandler HOPPER_UPGRADE_BASE_CHECK = NATURES_AURA_PLUS_GROUP
            .server("hopperUpgradeBaseCheck", () -> HopperUpgradeValidEvent.class);
    public static final EventHandler CALCULATE_AURA_GEN_BY_FIREWORK = NATURES_AURA_PLUS_GROUP
            .server("calculateAuragenByFirework", () -> CalculateAuraGenByFireworkEvent.class);
}
