package net.wkhan.naturesaura_plus.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import net.wkhan.naturesaura_plus.kubejs.event.NaturesAuraPlusEvents;

public class KubeJSNaturesAuraPlusPlugin extends KubeJSPlugin {

    @Override
    public void registerEvents() {
        NaturesAuraPlusEvents.NATURES_AURA_PLUS_GROUP.register();
    }
}
