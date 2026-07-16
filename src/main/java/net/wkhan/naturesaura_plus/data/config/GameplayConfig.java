package net.wkhan.naturesaura_plus.data.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GameplayConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue LOOT_FINDER_AURA_COST = BUILDER
            .comment("The aura cost of using the loot finder item. (Default: 100,000)")
            .defineInRange("lootFinderAuraCost", 100000, 0, 1200000);

    public static final ForgeConfigSpec.IntValue LOOT_FINDER_RANGE = BUILDER
            .comment("Maximum range (in blocks) upto which loot finder item can detect treasure. (Default: 64)")
            .defineInRange("lootFinderRange", 64, 0, 1024);

    public static final ForgeConfigSpec.IntValue LOOT_FINDER_USE_COOLDOWN = BUILDER
            .comment("Cooldown set on loot finder item upon use, in ticks. (Default: 1,200)")
            .defineInRange("lootFinderUseCooldownInTicks", 1200, 0, 72000);

    public static final ForgeConfigSpec.IntValue LOOT_FINDER_LIGHT_LIFE = BUILDER
            .comment("How long the loot finder particles stay, in ticks. (Default: 1,200)")
            .defineInRange("lootFinderLightLifeInTicks", 1200, 0, 72000);

    public static final ForgeConfigSpec.IntValue PET_RECALL_RANGE = BUILDER
            .comment("Maximum range upto which pets are selected to be teleported alongside owner upon using aura coffee. (Default: 4)")
            .defineInRange("petRecallRange", 4, 1, 64);

    public static final ForgeConfigSpec.IntValue SASH_AURA_CAPACITY = BUILDER
            .comment("Aura capacity for the Naturalist Sash. (Default: 1,200,000)")
            .defineInRange("sashAuraCapacity", 1200000, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue SASH_MANA_CAPACITY = BUILDER
            .comment("Mana capacity for the Naturalist Sash. (Default: 2,000,000)")
            .defineInRange("sashManaCapacity", 2000000, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if(event.getConfig().getSpec() != SPEC) return;

    }
}
