package net.wkhan.naturesaura_plus.data.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AuraGenConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue FLOWER_GEN_RANGE = BUILDER
            .comment("Horizontal range in blocks of the flower generator. (Default: 3) \n(Note, does not affect vertical range)")
            .defineInRange("flowerGenRange", 3, 1, 10);


    private static final ForgeConfigSpec.IntValue FLOWER_GEN_MEMORY_SIZE = BUILDER
            .comment("Number of flowers the flower aura generator block remembers when calculating aura generated amount. (Default: 3)")
            .defineInRange("flowerGenMemorySize", 3, 1, 1000);

    private static final ForgeConfigSpec.IntValue FLOWER_GEN_VITALITY_FLOOR = BUILDER //clarify
            .comment("Defines the lowest value of vitality upto which the flower generator generates aura. (Default: 100)")
            .defineInRange("flowerGenVitalityFloor", 100, 0, 100);

    private static final ForgeConfigSpec.DoubleValue FLOWER_GEN_POW_FACTOR = BUILDER
            .comment("The value of the power factor in the aura generation formula for flower generator. Check wiki for more info. (Default: 0.5)")
            .defineInRange("flowerGenPowFactor", 0.5, -5, 10);

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
            .defineInRange("chorusGenRange", 2, 1, 10);

    private static final ForgeConfigSpec.IntValue OAK_GEN_RANGE = BUILDER
            .comment("Cubical size radius (half-length) for range of the oak generator to check for tree/feature placements. (Default: 10)")
            .defineInRange("oakGenRange", 10, 1, 32);

    private static final ForgeConfigSpec.IntValue POTION_GEN_RANGE = BUILDER
            .comment("Cubical size radius (half-length) for range of the potion generator to check for lingering potions. (Default: 2)")
            .defineInRange("potionGenRange", 2, 1, 10);

    private static final ForgeConfigSpec.IntValue POTION_CAP_PER_TICK = BUILDER
            .comment("Number of lingering potions the potion generator will accept for aura generation per tick. \nSet to -1 to remove the cap. (Default: 2)")
            .defineInRange("potionCapForGenPerTick", 2, -1, 100);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int flowerGenRange;
    public static int flowerGenMemorySize;
    public static int flowerGenVitalityFloor;
    public static double flowerGenPowFactor;
    public static int mossGenRange;
    public static int mossGenMemorySize;
    public static int animalGenRange;
    public static int slimeGenRange;
    public static int chorusGenRange;
    public static int oakGenRange;
    public static int potionGenRange;
    public static int potionCapForGenPerTick;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if(event.getConfig().getSpec() != SPEC) return;
        flowerGenRange = FLOWER_GEN_RANGE.get();
        flowerGenMemorySize = FLOWER_GEN_MEMORY_SIZE.get();
        flowerGenVitalityFloor = FLOWER_GEN_VITALITY_FLOOR.get();
        flowerGenPowFactor = FLOWER_GEN_POW_FACTOR.get();
        mossGenRange = MOSS_GEN_RANGE.get();
        mossGenMemorySize = MOSS_GEN_MEMORY_SIZE.get();
        animalGenRange = ANIMAL_GEN_RANGE.get();
        slimeGenRange = SLIME_GEN_RANGE.get();
        chorusGenRange = CHORUS_GEN_RANGE.get();
        oakGenRange = OAK_GEN_RANGE.get();
        potionGenRange = POTION_GEN_RANGE.get();
        potionCapForGenPerTick = POTION_CAP_PER_TICK.get();
    }
}
