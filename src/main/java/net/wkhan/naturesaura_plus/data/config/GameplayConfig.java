package net.wkhan.naturesaura_plus.data.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GameplayConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue MAX_RITUAL_STEMS = BUILDER
            .comment("Maximum number of logs/stems the Tree Ritual will map. (Default: 500)")
            .defineInRange("maxRitualLogs", 500, 10, 10000);

    private static final ForgeConfigSpec.IntValue MAX_RITUAL_LEAVES = BUILDER
            .comment("Maximum number of leaves the Tree Ritual will search for to destroy. (Default: 2,500)")
            .defineInRange("maxRitualLeaves", 2500, 10, 15000);

    private static final ForgeConfigSpec.IntValue LOOT_FINDER_AURA_COST = BUILDER
            .comment("The aura cost of using the loot finder item. (Default: 100,000)")
            .defineInRange("lootFinderAuraCost", 100000, 0, 1200000);

    private static final ForgeConfigSpec.IntValue LOOT_FINDER_RANGE = BUILDER
            .comment("Maximum range (in blocks) upto which loot finder item can detect treasure. (Default: 64)")
            .defineInRange("lootFinderRange", 64, 0, 1024);

    private static final ForgeConfigSpec.IntValue LOOT_FINDER_USE_COOLDOWN = BUILDER
            .comment("Cooldown set on loot finder item upon use, in ticks. (Default: 1,200)")
            .defineInRange("lootFinderUseCooldownInTicks", 1200, 0, 72000);

    private static final ForgeConfigSpec.IntValue LOOT_FINDER_LIGHT_LIFE = BUILDER
            .comment("How long the loot finder particles stay, in ticks. (Default: 1,200)")
            .defineInRange("lootFinderLightLifeInTicks", 1200, 0, 72000);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int maxRitualStems;
    public static int maxRitualLeaves;
    public static int lootFinderAuraCost;
    public static int lootFinderRange;
    public static int lootFinderUseCooldownInTicks;
    public static int lootFinderLightLifeInTicks;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if(event.getConfig().getSpec() != SPEC) return;
        maxRitualStems = MAX_RITUAL_STEMS.get();
        maxRitualLeaves = MAX_RITUAL_LEAVES.get();
        lootFinderAuraCost = LOOT_FINDER_AURA_COST.get();
        lootFinderRange = LOOT_FINDER_RANGE.get();
        lootFinderUseCooldownInTicks = LOOT_FINDER_USE_COOLDOWN.get();
        lootFinderLightLifeInTicks = LOOT_FINDER_LIGHT_LIFE.get();
    }
}
