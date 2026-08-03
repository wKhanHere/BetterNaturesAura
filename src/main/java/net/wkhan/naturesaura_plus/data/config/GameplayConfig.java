package net.wkhan.naturesaura_plus.data.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GameplayConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue BREAK_PREVENTION_APPLY_COST = BUILDER
            .comment("Levels cost required to apply Token of Fortitude to an item. (Default: 30)")
            .defineInRange("breakPreventionApplyCost", 30, 1, 1000);

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

    public static final ForgeConfigSpec.IntValue FURNACE_BOOSTER_AURA_COST = BUILDER
            .comment("The aura cost per recipe for the furnace booster block. (Default: 6,500)")
            .defineInRange("furnaceBoosterAuraCost", 6500, 0, 1000000);

    public static final ForgeConfigSpec.BooleanValue CHECK_TAG_FOR_FURNACE_BOOST = BUILDER
            .comment("Whether the furnace booster should check the input item in furnace against the tag #naturesaura_plus:valid_smeltable_to_boost,\n to decide whether to double the smelted output or not (Default: false)")
            .define("allowAllForFurnaceBoost", false);

    public static final ForgeConfigSpec.IntValue FURNACE_BOOSTER_CHANCE = BUILDER
            .comment("The aura cost per recipe for the furnace booster block. (Default: 45)")
            .defineInRange("furnaceBoosterChance", 45, 0, 100);

    public static final ForgeConfigSpec.BooleanValue CUBICAL_ORE_SPAWN = BUILDER
            .comment("Whether the ore spawning from Powder of the Bountiful Core applies to a cubical range (else spherical range). (Default: true)")
            .define("isOreEffectCubical", true);

    public static final ForgeConfigSpec.IntValue MIN_AURA_FOR_ORE_SPAWN = BUILDER
            .comment("The minimum environmental aura required for Powder of the Bountiful Core to function. (Default: 2,000,000)")
            .defineInRange("minAuraForOreSpawn", 2000000, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue MAX_ITERATION_FOR_ORE_SPAWN = BUILDER
            .comment("Maximum number of ores the Powder of the Bountiful Core attempts to place per operation. (Default: 20)")
            .defineInRange("maxIterationForOreSpawn", 20, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue ORE_SPAWN_ITER_SCALE = BUILDER
            .comment("Value of oreSpawnIterationCalcScale in the formula: AuraNearby / (AuraSpotsNearby * oreSpawnIterationCalcScale),\n to determine the number of ores the Powder of the Bountiful Core attempts to place per operation. (Default: 300,000)")
            .defineInRange("oreSpawnIterationCalcScale", 300000D, 0D, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue ORE_SPAWN_RANGE_SCALE = BUILDER
            .comment("Value of oreSpawnRangeScale in the formula: AuraNearby / oreSpawnRangeScale, to determine the range of the Powder of the Bountiful Core. (Default: 150,000)")
            .defineInRange("oreSpawnRangeScale", 150000D, 1D, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue MIN_ORE_SPAWN_RANGE = BUILDER
            .comment("Minimum radius for range of the of the Powder of the Bountiful Core. (Default: 5)")
            .defineInRange("minOreSpawnRange", 5, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue MAX_ORE_SPAWN_RANGE = BUILDER
            .comment("Maximum radius for range of the of the Powder of the Bountiful Core. (Default: 20)")
            .defineInRange("maxOreSpawnRange", 20, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue MAX_ORE_PLACE_ATTEMPTS = BUILDER
            .comment("Maximum attempts for the Powder of the Bountiful Core to place an ore, before failing and passing. (Default: 10)")
            .defineInRange("maxOrePlaceAttempts", 10, 1, 100);

    public static final ForgeConfigSpec.BooleanValue ENABLE_WHITELIST_AURA_BONEMEAL = BUILDER
            .comment("Whether the tag #naturesaura_plus:aura_bonemeal_whitelist is checked before allowing environmental bone-mealing due aura excess.\nThe tag #naturesaura_plus:aura_bonemeal_blacklist, however, still takes precedence. (Default: false)")
            .define("useAuraBoneMealWhiteList", false);

    public static final ForgeConfigSpec.IntValue AURA_BONEMEAL_COST = BUILDER
            .comment("The aura cost per environmental aura bonemeal effect occurrence. (Default: 3500)")
            .defineInRange("auraBoneMealCost", 3500, 1, Integer.MAX_VALUE);


    public static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if(event.getConfig().getSpec() != SPEC) return;

    }
}
