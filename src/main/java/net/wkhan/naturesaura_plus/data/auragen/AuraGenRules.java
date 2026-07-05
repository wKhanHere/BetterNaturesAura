package net.wkhan.naturesaura_plus.data.auragen;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.generateListFromEither;

public final class AuraGenRules {

    public record deMossedBlockAuraAmountPair(Block deMossedBlock, int auraAmount) {}
    public static final Map<Block, deMossedBlockAuraAmountPair> MOSS_GENERATIONS = new HashMap<>();

    public record flowerValues(int auraAmount, byte lucidity, byte obscurity, float obscurityScale) {}
    public static final Map<Block, flowerValues> FLOWER_GENERATIONS = new HashMap<>();

    public record slimeValues(int auraAmount, int slimeColor, int minSizeForSlime, int flatGenerationTimer,
                              float generationTimerModifier, float sizeModifier,
                              boolean doSlimeSizeScaling, boolean doEntityDropLoot, boolean isFlatGenerationTimer) {}
    public static final Map<EntityType<?>, slimeValues> SLIME_GENERATIONS = new HashMap<>();

    public record animalValues(int minimumTimeAliveForGenerationTime, int maximumGenerationTime, float timeAliveModifierForGenerationTime,
                               int minimumTimeAliveForAuraAmount, int maximumAuraAmount, float timeAliveModifierForAuraAmount,
                               boolean doEntityDropLoot, boolean isBabyValid, boolean isFlatAuraGain, boolean isFlatGenerationTimer) {}
    public static final Map<EntityType<?>, animalValues> ANIMAL_GENERATIONS = new HashMap<>();

    public record chorusValues(Block stemBlock, Block capBlock, int auraGainPerBlock, boolean isSizeScaled,
                               SoundEvent soundEvent, float soundVolume, float soundPitch) {}
    public static final Map<Block, chorusValues> CHORUS_GENERATIONS = new HashMap<>();

    public record oakValues(ResourceKey<ConfiguredFeature<?,?>> featureReplacement, int auraAmount) {}
    public static final Map<ResourceKey<ConfiguredFeature<?,?>>, oakValues> OAK_GENERATIONS = new HashMap<>();

    public static HashMap<String, Integer> auraRulesCount() {
        HashMap<String, Integer> rulesCount = new HashMap<>();
        rulesCount.put("Projectile Generations", NaturesAuraAPI.PROJECTILE_GENERATIONS.size());
        rulesCount.put("Moss Generations", MOSS_GENERATIONS.size());
        rulesCount.put("Flower Generations", FLOWER_GENERATIONS.size());
        rulesCount.put("Slime Generations", SLIME_GENERATIONS.size());
        rulesCount.put("Animal Generations", ANIMAL_GENERATIONS.size());
        rulesCount.put("Chorus Generations", CHORUS_GENERATIONS.size());
        rulesCount.put("Oak (Tree) Generations", OAK_GENERATIONS.size());
        return rulesCount;
    }

    public static void auraGenerationClear() {
        NaturesAuraAPI.PROJECTILE_GENERATIONS.clear();
        MOSS_GENERATIONS.clear();
        FLOWER_GENERATIONS.clear();
        SLIME_GENERATIONS.clear();
        ANIMAL_GENERATIONS.clear();
        CHORUS_GENERATIONS.clear();
        OAK_GENERATIONS.clear();
    }
    public static void addAuraGenerations() {
        addProjectileGenerations();
        addMossGenerations();
        addFlowerGenerations();
        addSlimeGenerations();
        addAnimalGenerations();
        addChorusGenerations();
        addOakGenerations();
    }

    public static final Queue<ProjectileGenRule> projectileRulesQueue = new ArrayDeque<>();
    public static void addProjectileGeneration(ProjectileGenRule rule) {
        int auraAmount = rule.auraAmount();
        EntityType<?> projectile = rule.getProjectile();

        if (projectile != null) {
                NaturesAuraAPI.PROJECTILE_GENERATIONS.put(projectile, auraAmount);
                return;
        }

        TagKey<EntityType<?>> projectileTag = rule.getProjectileTag();
        if (projectileTag != null) {
                ForgeRegistries.ENTITY_TYPES.getValues().stream()
                        .filter(e -> e.is(projectileTag))
                        .forEach(e -> NaturesAuraAPI.PROJECTILE_GENERATIONS.put(e, auraAmount));
        }
    }
    public static void addProjectileGenerations() {
        while(!projectileRulesQueue.isEmpty()) addProjectileGeneration(projectileRulesQueue.poll());
    }

    public static final Queue<MossGenRule> mossRulesQueue = new ArrayDeque<>();
    public static void addMossGeneration(MossGenRule rule) {
        int auraAmount = rule.auraAmount();
        Block mossBlock = rule.getBlockInput();
        TagKey<Block> mossBlockTag = rule.getBlockInputTag();
        Block deMossedBlock = rule.getBlockOutput();

        if (mossBlock == null && mossBlockTag == null) return;

        if (mossBlock != null) {
            MOSS_GENERATIONS.put(mossBlock, new deMossedBlockAuraAmountPair(deMossedBlock, auraAmount));
            return;
        }

        ForgeRegistries.BLOCKS.getValues().stream()
                .filter(b -> b.defaultBlockState().is(mossBlockTag))
                .forEach(b -> MOSS_GENERATIONS.put(b, new deMossedBlockAuraAmountPair(deMossedBlock, auraAmount)));
    }
    public static void addMossGenerations() {
        while(!mossRulesQueue.isEmpty()) addMossGeneration(mossRulesQueue.poll());
    }

    public static final Queue<FlowerGenRule> flowerRulesQueue = new ArrayDeque<>();
    public static void addFlowerGeneration(FlowerGenRule rule) {
        Block flowerBlock = rule.getBlockInput();
        TagKey<Block> flowerBlockTag = rule.getBlockInputTag();
        if (flowerBlock == null && flowerBlockTag == null) return;
        int auraAmount = rule.auraAmount();
        byte lucidity = rule.lucidity();
        byte obscurity = rule.obscurity();
        float obscurityScale = rule.obscurityScale();

        if(flowerBlock != null) {
            FLOWER_GENERATIONS.put(flowerBlock, new flowerValues(auraAmount, lucidity, obscurity, obscurityScale));
            return;
        }

        ForgeRegistries.BLOCKS.getValues().stream()
                .filter(b -> b.defaultBlockState().is(flowerBlockTag))
                .forEach(b -> FLOWER_GENERATIONS.put(b, new flowerValues(auraAmount, lucidity, obscurity, obscurityScale)));
    }
    public static void addFlowerGenerations() {
        while(!flowerRulesQueue.isEmpty()) addFlowerGeneration(flowerRulesQueue.poll());
    }

    public static final Queue<SlimeGenRule> slimeRulesQueue = new ArrayDeque<>();
    public static void addSlimeGeneration(SlimeGenRule rule) {
        int auraAmount = rule.auraAmount();
        int slimeColor = rule.slimeColor();
        int minSizeForSlime = rule.minSizeForSlime();
        int flatGenerationTimer = rule.flatGenerationTimer();
        float generationTimerModifier = rule.generationTimerModifier();
        float sizeModifier = rule.sizeModifier();
        boolean doSlimeSizeScaling = rule.doSlimeSizeScaling();
        boolean doEntityDropLoot = rule.doEntityDropLoot();
        boolean isFlatGenerationTimer = rule.isFlatGenerationTimer();
        EntityType<?> slime = rule.getEntity();

        if (slime != null) {
            SLIME_GENERATIONS.put(slime,
                    new slimeValues(auraAmount,slimeColor,minSizeForSlime,flatGenerationTimer,generationTimerModifier,
                            sizeModifier,doSlimeSizeScaling,doEntityDropLoot,isFlatGenerationTimer));
            return;
        }

        TagKey<EntityType<?>> slimeTag = rule.getEntityTag();
        if (slimeTag != null) {
            ForgeRegistries.ENTITY_TYPES.getValues().stream()
                    .filter(e -> e.is(slimeTag))
                    .forEach(e -> SLIME_GENERATIONS.put(e,
                            new slimeValues(auraAmount,slimeColor,minSizeForSlime,flatGenerationTimer,generationTimerModifier,
                                    sizeModifier,doSlimeSizeScaling,doEntityDropLoot,isFlatGenerationTimer))
            );
        }
    }
    public static void addSlimeGenerations() {
        while(!slimeRulesQueue.isEmpty()) addSlimeGeneration(slimeRulesQueue.poll());
    }

    public static final Queue<AnimalGenRule> animalRulesQueue = new ArrayDeque<>();
    public static void addAnimalGeneration(AnimalGenRule rule) {
        int minimumTimeAliveForGenerationTime = rule.minimumTimeAliveForGenerationTime();
        int maximumGenerationTime = rule.maximumGenerationTime();
        float timeAliveModifierForGenerationTime = rule.timeAliveModifierForGenerationTime();
        int minimumTimeAliveForAuraAmount = rule.minimumTimeAliveForAuraAmount();
        int maximumAuraAmount = rule.maximumAuraAmount();
        float timeAliveModifierForAuraAmount = rule.timeAliveModifierForAuraAmount();
        boolean doEntityDropLoot = rule.doEntityDropLoot();
        boolean isBabyValid = rule.isBabyValid();
        boolean isFlatAuraGain = rule.isFlatAuraGain();
        boolean isFlatGenerationTimer = rule.isFlatGenerationTimer();
        EntityType<?> animal = rule.getEntity();

        if (animal != null) {
            ANIMAL_GENERATIONS.put(animal,
                    new animalValues(minimumTimeAliveForGenerationTime, maximumGenerationTime, timeAliveModifierForGenerationTime,
                            minimumTimeAliveForAuraAmount, maximumAuraAmount, timeAliveModifierForAuraAmount , doEntityDropLoot,
                            isBabyValid, isFlatAuraGain, isFlatGenerationTimer));
            return;
        }

        TagKey<EntityType<?>> animalTag = rule.getEntityTag();
        if (animalTag != null) {
            ForgeRegistries.ENTITY_TYPES.getValues().stream()
                    .filter(e -> e.is(animalTag))
                    .forEach(e -> ANIMAL_GENERATIONS.put(e,
                            new animalValues(minimumTimeAliveForGenerationTime, maximumGenerationTime, timeAliveModifierForGenerationTime,
                                    minimumTimeAliveForAuraAmount, maximumAuraAmount, timeAliveModifierForAuraAmount , doEntityDropLoot,
                                    isBabyValid, isFlatAuraGain, isFlatGenerationTimer))
                    );
        }
    }
    public static void addAnimalGenerations() {
        while(!animalRulesQueue.isEmpty()) addAnimalGeneration(animalRulesQueue.poll());
    }

    public static final Queue<ChorusGenRule> chorusRulesQueue = new ArrayDeque<>();
    public static void addChorusGeneration(ChorusGenRule rule) { 
        Block soilBlock = rule.getBlockSoil();
        TagKey<Block> soilBlockTag = rule.getBlockSoilTag();
        if (soilBlock == null && soilBlockTag == null) return;
        int auraGainPerBlock = rule.auraGainPerBlock();
        boolean isSizeScaled = rule.isSizeScaled();
        SoundEvent soundEvent = rule.soundEvent();
        float soundVolume = rule.soundVolume();
        float soundPitch = rule.soundPitch();
        List<Block> listSoil = generateListFromEither(rule.soilBlockId(),ForgeRegistries.BLOCKS);
        Block stem = rule.stemBlock();
        Block cap = rule.capBlock();

        for (Block soil : listSoil) CHORUS_GENERATIONS.put(soil, new chorusValues
                (stem, cap, auraGainPerBlock, isSizeScaled, soundEvent, soundVolume, soundPitch));
    }
    public static void addChorusGenerations() {
        while(!chorusRulesQueue.isEmpty()) addChorusGeneration(chorusRulesQueue.poll());
    }

    public static final Queue<OakGenRule> oakRulesQueue = new ArrayDeque<>();
    public static void addOakGeneration(OakGenRule rule) {
        ResourceKey<ConfiguredFeature<?,?>> featureToReplace = rule.featureToReplace();
        ResourceKey<ConfiguredFeature<?,?>> featureReplacement = rule.featureReplacement();
        int auraAmount = rule.auraAmount();

        OAK_GENERATIONS.put(featureToReplace, new oakValues(featureReplacement, auraAmount));
    }
    public static void addOakGenerations() {
        while(!oakRulesQueue.isEmpty()) addOakGeneration(oakRulesQueue.poll());
    }
}

