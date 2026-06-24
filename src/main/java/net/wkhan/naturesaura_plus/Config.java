package net.wkhan.naturesaura_plus;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue MAX_RITUAL_STEMS = BUILDER
            .comment("Maximum number of logs/stems the Tree Ritual will map. (Default: 500)")
            .defineInRange("maxRitualLogs", 500, 10, 10000);

    private static final ForgeConfigSpec.IntValue MAX_RITUAL_LEAVES = BUILDER
            .comment("Maximum number of leaves the Tree Ritual will search for to destroy. (Default: 1,500)")
            .defineInRange("maxRitualLeaves", 1500, 10, 15000);

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

    private static final ForgeConfigSpec.IntValue FLOWER_GEN_RANGE = BUILDER
            .comment("Horizontal range in blocks of the flower generator. (Default: 3) \n(Note, does not affect vertical range)")
            .defineInRange("flowerGenRange", 3, 1, 10);


    private static final ForgeConfigSpec.IntValue FLOWER_GEN_MEMORY_SIZE = BUILDER
            .comment("Number of flowers the flower aura generator block remembers when calculating aura generated amount. (Default: 3)")
            .defineInRange("flowerGenMemorySize", 3, 1, 1000);

    private static final ForgeConfigSpec.IntValue FLOWER_GEN_VITALITY_FLOOR = BUILDER //clarify
            .comment("Defines the lowest value of vitality upto which the flower generator generates aura. (Default: 0)")
            .defineInRange("flowerGenVitalityFloor", 100, 0, 100);

    private static final ForgeConfigSpec.DoubleValue FLOWER_GEN_POW_FACTOR = BUILDER
            .comment("The value of the power factor in the aura generation formula for flower generator. Check wiki for more info. (Default: 0.5)")
            .defineInRange("flowerGenPowFactor", 0.5, -5, 10);

    //Make range config for moss gen
    private static final ForgeConfigSpec.IntValue MOSS_GEN_RANGE = BUILDER
            .comment("Horizontal range in blocks of the moss generator. (Default: 2) \n(Note, does not affect vertical range)")
            .defineInRange("mossGenRange", 2, 1, 10);

    private static final ForgeConfigSpec.IntValue MOSS_GEN_MEMORY_SIZE = BUILDER
            .comment("Number of mosses the moss aura generator block remembers when determining if moss block is recent (Hence skipping if recent). (Default: 3)")
            .defineInRange("mossGenMemorySize", 3, 1, 1000);

    private static final ForgeConfigSpec.IntValue ANIMAL_GEN_RANGE = BUILDER
            .comment("Cubical size radius (half-length) for range of the animal generator in blocks. (Default: 5)")
            .defineInRange("animalGenRange", 5, 1, 10);

    private static final ForgeConfigSpec.IntValue SLIME_GEN_RANGE = BUILDER
            .comment("Cubical size radius (half-length) for range of the slime generator in blocks. (Default: 8)")
            .defineInRange("slimeGenRange", 8, 1, 32);

    private static final ForgeConfigSpec.IntValue CHORUS_GEN_RANGE = BUILDER
            .comment("Cubical size radius (half-length) for range of the chorus generator to check for valid plants in blocks. (Default: 2)")
            .defineInRange("chorusGenRange", 2, 1, 10); //CHECK

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int maxRitualStems;
    public static int maxRitualLeaves;
    public static int lootFinderAuraCost;
    public static int lootFinderRange;
    public static int lootFinderUseCooldownInTicks;
    public static int lootFinderLightLifeInTicks;
    public static int flowerGenRange;
    public static int flowerGenMemorySize;
    public static int flowerGenVitalityFloor;
    public static double flowerGenPowFactor;
    public static int mossGenRange;
    public static int mossGenMemorySize;
    public static int animalGenRange;
    public static int slimeGenRange;
    public static int chorusGenRange;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        maxRitualStems = MAX_RITUAL_STEMS.get();
        maxRitualLeaves = MAX_RITUAL_LEAVES.get();
        lootFinderAuraCost = LOOT_FINDER_AURA_COST.get();
        lootFinderRange = LOOT_FINDER_RANGE.get();
        lootFinderUseCooldownInTicks = LOOT_FINDER_USE_COOLDOWN.get();
        lootFinderLightLifeInTicks = LOOT_FINDER_LIGHT_LIFE.get();
        flowerGenRange = FLOWER_GEN_RANGE.get();
        flowerGenMemorySize = FLOWER_GEN_MEMORY_SIZE.get();
        flowerGenVitalityFloor = FLOWER_GEN_VITALITY_FLOOR.get();
        flowerGenPowFactor = FLOWER_GEN_POW_FACTOR.get();
        mossGenRange = MOSS_GEN_RANGE.get();
        mossGenMemorySize = MOSS_GEN_MEMORY_SIZE.get();
        animalGenRange = ANIMAL_GEN_RANGE.get();
        slimeGenRange = SLIME_GEN_RANGE.get();
        chorusGenRange = CHORUS_GEN_RANGE.get();
    }
}
