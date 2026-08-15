package net.wkhan.naturesaura_plus.data.auragen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.data.PriorityRule;

import java.util.*;

import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.*;

public final class AuraGenRules {
    public record ProjectileValues(int auraAmount, Item item, int priority) implements PriorityRule {
        @Override public int getPriority() {
            return priority;
        }
    }
    public static final HashMap<EntityType<?>, ProjectileValues> PROJECTILE_GENERATIONS = new HashMap<>();
    public static final Queue<ProjectileGenRule> projectileRulesQueue = new ArrayDeque<>();

    public record MossValues(Block deMossedBlock, int auraAmount, int priority) implements PriorityRule {
        @Override
        public int getPriority() {
            return priority;
        }
    }
    public static final HashMap<Block, MossValues> MOSS_GENERATIONS = new HashMap<>();
    public static final Queue<MossGenRule> mossRulesQueue = new ArrayDeque<>();

    public record FlowerValues(int auraAmount, byte lucidity, byte obscurity,
                               float obscurityScale, int priority) implements PriorityRule {
        @Override
        public int getPriority() {
            return priority;
        }
    }
    public static final HashMap<Block, FlowerValues> FLOWER_GENERATIONS = new HashMap<>();
    public static final Queue<FlowerGenRule> flowerRulesQueue = new ArrayDeque<>();

    public record SlimeValues(int auraAmount, int slimeColor, int minSizeForSlime, int flatGenerationTimer,
                              float generationTimerModifier, float sizeModifier,
                              boolean doSlimeSizeScaling, boolean doEntityDropLoot, boolean isFlatGenerationTimer,
                              int priority) implements PriorityRule {
        @Override
        public int getPriority() {
            return priority;
        }
    }
    public static final HashMap<EntityType<?>, SlimeValues> SLIME_GENERATIONS = new HashMap<>();
    public static final Queue<SlimeGenRule> slimeRulesQueue = new ArrayDeque<>();

    public record AnimalValues(int minimumTimeAliveForGenerationTime, int maximumGenerationTime, float timeAliveModifierForGenerationTime,
                               int minimumTimeAliveForAuraAmount, int maximumAuraAmount, float timeAliveModifierForAuraAmount,
                               boolean doEntityDropLoot, boolean isBabyValid, boolean isFlatAuraGain, boolean isFlatGenerationTimer,
                               int priority) implements PriorityRule {
        @Override
        public int getPriority() {
            return priority;
        }
    }
    public static final HashMap<EntityType<?>, AnimalValues> ANIMAL_GENERATIONS = new HashMap<>();
    public static final Queue<AnimalGenRule> animalRulesQueue = new ArrayDeque<>();

    public record ChorusValues(Block stemBlock, Block capBlock, int auraGainPerBlock, boolean isSizeScaled,
                               SoundEvent soundEvent, float soundVolume, float soundPitch, int priority) implements PriorityRule {
        @Override
        public int getPriority() {
            return priority;
        }
    }
    public static final HashMap<Block, ChorusValues> CHORUS_GENERATIONS = new HashMap<>();
    public static final Queue<ChorusGenRule> chorusRulesQueue = new ArrayDeque<>();

    public record OakValues(ResourceKey<ConfiguredFeature<?,?>> featureReplacement, int auraAmount,
                            int priority) implements PriorityRule {
        @Override
        public int getPriority() {
            return priority;
        }
    }
    public static final HashMap<ResourceKey<ConfiguredFeature<?,?>>, OakValues> OAK_GENERATIONS = new HashMap<>();
    public static final Queue<OakGenRule> oakRulesQueue = new ArrayDeque<>();

    public record PotionValues(int flatAmplifier, int finalScale, int flatAmplifierScale,
                               Set<MobEffect> incompatibleEffects, boolean doAmplifierScaling, boolean doDurationScaling,
                               int priority) implements PriorityRule {
        @Override
        public int getPriority() {
            return priority;
        }
    }
    public static final HashMap<MobEffect, PotionValues> POTION_GENERATIONS = new HashMap<>();
    public static final Queue<PotionGenRule> potionRulesQueue = new ArrayDeque<>();

    public static final HashMap<Integer, Object> FIREWORK_GENERATION = new HashMap<>();

    public static HashMap<String, Integer> auraRulesCount() {
        HashMap<String, Integer> rulesCount = new HashMap<>();
        rulesCount.put("Projectile Generations", PROJECTILE_GENERATIONS.size());
        rulesCount.put("Moss Generations", MOSS_GENERATIONS.size());
        rulesCount.put("Flower Generations", FLOWER_GENERATIONS.size());
        rulesCount.put("Slime Generations", SLIME_GENERATIONS.size());
        rulesCount.put("Animal Generations", ANIMAL_GENERATIONS.size());
        rulesCount.put("Chorus Generations", CHORUS_GENERATIONS.size());
        rulesCount.put("Oak (Tree) Generations", OAK_GENERATIONS.size());
        rulesCount.put("Potion Generations", POTION_GENERATIONS.size());
        rulesCount.put("Firework Generations", FIREWORK_GENERATION.size());
        return rulesCount;
    }
    public static void auraGenerationClear() {
        PROJECTILE_GENERATIONS.clear();
        MOSS_GENERATIONS.clear();
        FLOWER_GENERATIONS.clear();
        SLIME_GENERATIONS.clear();
        ANIMAL_GENERATIONS.clear();
        CHORUS_GENERATIONS.clear();
        OAK_GENERATIONS.clear();
        POTION_GENERATIONS.clear();
        FIREWORK_GENERATION.clear();
    }
    public static void addAuraGenerations() {
        processRuleQueue(projectileRulesQueue, AuraGenRules::addProjectileGeneration);
        processRuleQueue(mossRulesQueue, AuraGenRules::addMossGeneration);
        processRuleQueue(flowerRulesQueue, AuraGenRules::addFlowerGeneration);
        processRuleQueue(slimeRulesQueue, AuraGenRules::addSlimeGeneration);
        processRuleQueue(animalRulesQueue, AuraGenRules::addAnimalGeneration);
        processRuleQueue(chorusRulesQueue, AuraGenRules::addChorusGeneration);
        processRuleQueue(oakRulesQueue, AuraGenRules::addOakGeneration);
        processRuleQueue(potionRulesQueue, AuraGenRules::addPotionGeneration);
    }

    public static void addProjectileGeneration(ProjectileGenRule rule) {
        EntityType<?> projectile = rule.getProjectile();
        ProjectileValues projectileValues = new ProjectileValues(rule.auraAmount(), rule.correspondingItem(), rule.priority());

        if (projectile != null) {
            computeAgainstPriorty(PROJECTILE_GENERATIONS, projectile, projectileValues, null);
            return;
        }

        TagKey<EntityType<?>> projectileTag = rule.getProjectileTag();
        if (projectileTag != null) {
            ForgeRegistries.ENTITY_TYPES.tags().getTag(projectileTag)
                        .forEach(e -> computeAgainstPriorty(PROJECTILE_GENERATIONS, e, projectileValues, null));
        }
    }
    public static void addMossGeneration(MossGenRule rule) {
        Block mossBlock = rule.getBlockInput();
        TagKey<Block> mossBlockTag = rule.getBlockInputTag();
        MossValues mossValues = new MossValues(rule.getBlockOutput(), rule.auraAmount(), rule.priority());

        if (mossBlock == null && mossBlockTag == null)
            return;

        if (mossBlock != null) {
            computeAgainstPriorty(MOSS_GENERATIONS, mossBlock, mossValues, null);
            return;
        }

        ForgeRegistries.BLOCKS.tags().getTag(mossBlockTag)
                .forEach(b -> computeAgainstPriorty(MOSS_GENERATIONS, b, mossValues, null));
    }
    public static void addFlowerGeneration(FlowerGenRule rule) {
        Block flowerBlock = rule.getBlockInput();
        TagKey<Block> flowerBlockTag = rule.getBlockInputTag();
        if (flowerBlock == null && flowerBlockTag == null)
            return;
        FlowerValues flowerValues = new FlowerValues(rule.auraAmount(), rule.lucidity(),
                rule.obscurity(), rule.obscurityScale(), rule.priority());

        if(flowerBlock != null) {
            computeAgainstPriorty(FLOWER_GENERATIONS, flowerBlock, flowerValues, null);
            return;
        }

        ForgeRegistries.BLOCKS.tags().getTag(flowerBlockTag)
                .forEach(b -> computeAgainstPriorty(FLOWER_GENERATIONS, b, flowerValues, null));
    }
    public static void addSlimeGeneration(SlimeGenRule rule) {
        SlimeValues slimeValues = new SlimeValues(rule.auraAmount(), rule.slimeColor(), rule.minSizeForSlime(),
                rule.flatGenerationTimer(), rule.generationTimerModifier(), rule.sizeModifier(),
                rule.doSlimeSizeScaling(), rule.doEntityDropLoot(), rule.isFlatGenerationTimer(), rule.priority());
        EntityType<?> slime = rule.getEntity();

        if (slime != null) {
            computeAgainstPriorty(SLIME_GENERATIONS, slime, slimeValues, null);
            return;
        }

        TagKey<EntityType<?>> slimeTag = rule.getEntityTag();
        if (slimeTag != null) {
            ForgeRegistries.ENTITY_TYPES.tags().getTag(slimeTag)
                    .forEach(e -> computeAgainstPriorty(SLIME_GENERATIONS, e, slimeValues, null)
            );
        }
    }
    public static void addAnimalGeneration(AnimalGenRule rule) {
        AnimalValues animalValues = new AnimalValues(rule.minimumTimeAliveForGenerationTime(), rule.maximumGenerationTime(),
                rule.timeAliveModifierForGenerationTime(), rule.minimumTimeAliveForAuraAmount(), rule.maximumAuraAmount(),
                rule.timeAliveModifierForAuraAmount(), rule.doEntityDropLoot(), rule.isBabyValid(), rule.isFlatAuraGain(),
                rule.isFlatGenerationTimer(), rule.priority());
        EntityType<?> animal = rule.getEntity();

        if (animal != null) {
            computeAgainstPriorty(ANIMAL_GENERATIONS, animal, animalValues, null);
            return;
        }

        TagKey<EntityType<?>> animalTag = rule.getEntityTag();
        if (animalTag != null) {
            ForgeRegistries.ENTITY_TYPES.tags().getTag(animalTag)
                    .forEach(e -> computeAgainstPriorty(ANIMAL_GENERATIONS, e, animalValues, null)
            );
        }
    }
    public static void addChorusGeneration(ChorusGenRule rule) { 
        Block soilBlock = rule.getBlockSoil();
        TagKey<Block> soilBlockTag = rule.getBlockSoilTag();
        if (soilBlock == null && soilBlockTag == null)
            return;
        List<Block> listSoil = generateListFromEither(rule.soilBlockId(),ForgeRegistries.BLOCKS);
        ChorusValues chorusValues = new ChorusValues(rule.stemBlock(), rule.capBlock(), rule.auraGainPerBlock(),
                rule.isSizeScaled(), rule.soundEvent(), rule.soundVolume(), rule.soundPitch(), rule.priority());

        for (Block soil : listSoil)
            computeAgainstPriorty(CHORUS_GENERATIONS, soil, chorusValues, null);
    } //todo: change chorus gen to instead take against a list for cap and stem since we know list codecs now
    public static void addOakGeneration(OakGenRule rule) {
        OakValues oakValues = new OakValues(rule.featureReplacement(), rule.auraAmount(), rule.priority());
        computeAgainstPriorty(OAK_GENERATIONS, rule.featureToReplace(), oakValues, null);
    }
    public static void addPotionGeneration(PotionGenRule rule) {
        PotionValues potionValues = new PotionValues(rule.flatAmplifier(), rule.finalScale(), rule.flatAmplifierScale(),
                new HashSet<>(rule.incompatibleEffects()), rule.doAmplifierScaling(), rule.doDurationScaling(), rule.priority());
        computeAgainstPriorty(POTION_GENERATIONS, rule.potion(), potionValues, (oldValue, newValue) -> {
            Set<MobEffect> incompatibleEffects = new HashSet<>(oldValue.incompatibleEffects());
            incompatibleEffects.addAll(newValue.incompatibleEffects());
            return new PotionValues(oldValue.flatAmplifier(), oldValue.finalScale(), oldValue.flatAmplifierScale(),
                    incompatibleEffects, oldValue.doAmplifierScaling(), oldValue.doDurationScaling(), oldValue.priority());
        });
    }
    public static void addFireworkGeneration(FireworkGenRule rule) {
        FIREWORK_GENERATION.put(0, rule.explosionFlickerFactor());
        FIREWORK_GENERATION.put(1, rule.explosionTrailFactor());
        FIREWORK_GENERATION.put(2, rule.explosionTypesListFactor());
        FIREWORK_GENERATION.put(3, rule.explosionColorFactor());
        FIREWORK_GENERATION.put(4, rule.flightTimeScale());
        FIREWORK_GENERATION.put(5, rule.flatReleaseTimer());
        FIREWORK_GENERATION.put(6, rule.finalScale());
        FIREWORK_GENERATION.put(7, rule.doFlightTimeScaling());
    }
}

