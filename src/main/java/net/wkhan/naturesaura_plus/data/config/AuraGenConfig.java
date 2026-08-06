package net.wkhan.naturesaura_plus.data.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AuraGenConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // Flower Generator
    public static final ForgeConfigSpec.IntValue FLOWER_GEN_RANGE;
    public static final ForgeConfigSpec.IntValue FLOWER_GEN_MEMORY_SIZE;
    public static final ForgeConfigSpec.IntValue FLOWER_GEN_VITALITY_FLOOR;
    public static final ForgeConfigSpec.DoubleValue FLOWER_GEN_POW_FACTOR;

    // Moss Generator
    public static final ForgeConfigSpec.IntValue MOSS_GEN_RANGE;
    public static final ForgeConfigSpec.IntValue MOSS_GEN_MEMORY_SIZE;

    // Animal Generator
    public static final ForgeConfigSpec.IntValue ANIMAL_GEN_RANGE;

    // Slime Generator
    public static final ForgeConfigSpec.IntValue SLIME_GEN_RANGE;

    // Chorus Generator
    public static final ForgeConfigSpec.IntValue CHORUS_GEN_RANGE;

    // Oak Generator
    public static final ForgeConfigSpec.IntValue OAK_GEN_RANGE;

    // Potion Generator
    public static final ForgeConfigSpec.IntValue POTION_GEN_RANGE;
    public static final ForgeConfigSpec.IntValue POTION_CAP_PER_TICK;
    public static final ForgeConfigSpec.BooleanValue CHECK_MULTI_FOR_POTION_GEN;

    // Firework Generator
    public static final ForgeConfigSpec.IntValue FIREWORK_GEN_RANGE;
    static {
        BUILDER.comment("Settings for the Flower Aura Generator").push("FlowerGenerator");
        FLOWER_GEN_RANGE = BUILDER
                .comment("Horizontal range in blocks of the flower generator. (Default: 3) \n(Note, does not affect vertical range)")
                .defineInRange("flowerGenRange", 3, 1, 10);
        FLOWER_GEN_MEMORY_SIZE = BUILDER
                .comment("Number of flowers the flower aura generator block remembers when calculating aura generated amount. (Default: 3)")
                .defineInRange("flowerGenMemorySize", 3, 1, 1000);
        FLOWER_GEN_VITALITY_FLOOR = BUILDER
                .comment("Defines the lowest value of vitality upto which the flower generator generates aura. (Default: 100)")
                .defineInRange("flowerGenVitalityFloor", 100, 0, 100);
        FLOWER_GEN_POW_FACTOR = BUILDER
                .comment("The value of the power factor in the aura generation formula for flower generator. Check wiki for more info. (Default: 0.5)")
                .defineInRange("flowerGenPowFactor", 0.5, -5.0, 10.0);
        BUILDER.pop();


        BUILDER.comment("Settings for the Moss Aura Generator").push("MossGenerator");
        MOSS_GEN_RANGE = BUILDER
                .comment("Horizontal range in blocks of the moss generator. (Default: 2) \n(Note, does not affect vertical range)")
                .defineInRange("mossGenRange", 2, 1, 10);
        MOSS_GEN_MEMORY_SIZE = BUILDER
                .comment("Number of mosses the moss aura generator block remembers when determining if moss block is recent (Hence skipping if recent). (Default: 3)")
                .defineInRange("mossGenMemorySize", 3, 1, 1000);
        BUILDER.pop();


        BUILDER.comment("Settings for the Animal Aura Generator").push("AnimalGenerator");
        ANIMAL_GEN_RANGE = BUILDER
                .comment("Cubical size radius (half-length) for range of the animal generator in blocks. (Default: 5)")
                .defineInRange("animalGenRange", 5, 1, 10);
        BUILDER.pop();


        BUILDER.comment("Settings for the Slime Aura Generator").push("SlimeGenerator");
        SLIME_GEN_RANGE = BUILDER
                .comment("Cubical size radius (half-length) for range of the slime generator in blocks. (Default: 8)")
                .defineInRange("slimeGenRange", 8, 1, 32);
        BUILDER.pop();


        BUILDER.comment("Settings for the Chorus Aura Generator").push("ChorusGenerator");
        CHORUS_GEN_RANGE = BUILDER
                .comment("Cubical size radius (half-length) for range of the chorus generator to check for valid plants in blocks. (Default: 2)")
                .defineInRange("chorusGenRange", 2, 1, 10);
        BUILDER.pop();


        BUILDER.comment("Settings for the Oak Aura Generator").push("OakGenerator");
        OAK_GEN_RANGE = BUILDER
                .comment("Cubical size radius (half-length) for range of the oak generator to check for tree/feature placements. (Default: 10)")
                .defineInRange("oakGenRange", 10, 1, 32);
        BUILDER.pop();


        BUILDER.comment("Settings for the Potion Aura Generator").push("PotionGenerator");
        POTION_GEN_RANGE = BUILDER
                .comment("Cubical size radius (half-length) for range of the potion generator to check for lingering potions. (Default: 2)")
                .defineInRange("potionGenRange", 2, 1, 10);
        POTION_CAP_PER_TICK = BUILDER
                .comment("Number of lingering potions the potion generator will accept for aura generation per tick. \nSet to -1 to remove the cap. (Default: 2)")
                .defineInRange("potionCapForGenPerTick", -1, -1, 100);
        CHECK_MULTI_FOR_POTION_GEN = BUILDER
                .comment("Whether the potion generator needs its multiblock to function or not. (Default: true)")
                .define("checkMultiForPotionGen", true);
        BUILDER.pop();


        BUILDER.comment("Settings for the Firework Aura Generator").push("FireworkGenerator");
        FIREWORK_GEN_RANGE = BUILDER
                .comment("Cubical size radius (half-length) for range of the firework generator to check for dropped fireworks. (Default: 10)")
                .defineInRange("fireworkGenRange", 4, 1, 32);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) return;

    }
}