package net.wkhan.naturesaura_plus.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import net.wkhan.naturesaura_plus.compat.kubejs.event.NaturesAuraPlusEvents;

public class KubeJSNaturesAuraPlusPlugin extends KubeJSPlugin {

    @Override
    public void registerEvents() {
        NaturesAuraPlusEvents.NATURES_AURA_PLUS_GROUP.register();
    }
}
