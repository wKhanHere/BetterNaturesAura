package net.wkhan.naturesaura_plus.data.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MiscConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue SHOW_AURA_GEN_RULES_IN_LOG;
    public static final ForgeConfigSpec.BooleanValue SHOW_ORE_SPAWN_RULES_IN_LOG;
    public static final ForgeConfigSpec.BooleanValue SHOW_AURA_QUANTITY_IN_JEI;

    static {
        SHOW_AURA_QUANTITY_IN_JEI = BUILDER
                .comment("Whether the aura cost/gain for aura generations/usages will be displayed in jei next to displayed aura bar or not. (Default: false)")
                .define("showAuraGenRulesInLog", false);
        SHOW_AURA_GEN_RULES_IN_LOG = BUILDER
                .comment("Whether the rules for aura generations will be displayed in log or not. (Default: false)")
                .define("showAuraGenRulesInLog", false);
        SHOW_ORE_SPAWN_RULES_IN_LOG = BUILDER
                .comment("Whether the rules for Powder of the Bountiful Core will be displayed in log or not. (Default: false)")
                .define("showOreSpawnRulesInLog", false);

        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if(event.getConfig().getSpec() != SPEC) return;

    }
}
