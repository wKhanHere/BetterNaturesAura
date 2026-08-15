package net.wkhan.naturesaura_plus.data;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.computeAgainstPriorty;

public final class OreSpawnRules {
    public record OreSpawnValues(@Nullable List<ResourceKey<Biome>> biomes, Reference2IntOpenHashMap<Block> baseBlockAndAuraDrain,
                                 SimpleWeightedRandomList<Block> outputOres, int priority) implements PriorityRule{
        @Override
        public int getPriority() {
            return priority;
        }
    }
    public static final HashMap<ResourceKey<DimensionType>, OreSpawnValues> ORE_SPAWNS = new HashMap<>();

    public static final Queue<OreSpawnRule> oreRulesQueue = new ArrayDeque<>();
    public static void addOreSpawn(OreSpawnRule rule) {
        Reference2IntOpenHashMap<Block> baseBlockAndAuraDrain = new Reference2IntOpenHashMap<>(rule.baseBlockAndAuraDrain().size());
        baseBlockAndAuraDrain.defaultReturnValue(0);
        SimpleWeightedRandomList.Builder<Block> outputOres = new SimpleWeightedRandomList.Builder<>();

        for (Either<Block, TagKey<Block>> eitherBorBT : rule.baseBlockAndAuraDrain().keySet()) {
            int auraDrain = rule.baseBlockAndAuraDrain().get(eitherBorBT);
            eitherBorBT.ifLeft(block -> baseBlockAndAuraDrain.put(block, auraDrain))
                    .ifRight(blockTagKey -> ForgeRegistries.BLOCKS.tags().getTag(blockTagKey)
                            .forEach(block -> baseBlockAndAuraDrain.put(block, auraDrain)));
        }

        for (WeightedEntry.Wrapper<Either<Block, TagKey<Block>>> eitherBorBT : rule.outputOres().unwrap()) {
            int weight = eitherBorBT.getWeight().asInt();
            eitherBorBT.getData().ifLeft(block -> outputOres.add(block, weight))
                    .ifRight(blockTagKey -> ForgeRegistries.BLOCKS.tags().getTag(blockTagKey)
                                .forEach(block -> outputOres.add(block, weight)));
        }

        List<ResourceKey<Biome>> biomes;
        if (rule.biomes() != null)
            biomes = new ArrayList<>(rule.biomes());
        else
            biomes = null;

        OreSpawnValues oreSpawnValues = new OreSpawnValues(biomes, baseBlockAndAuraDrain, outputOres.build(), rule.priority());

        computeAgainstPriorty(ORE_SPAWNS, rule.dimensionType(), oreSpawnValues, (oldValue, newValue) -> {
            List<ResourceKey<Biome>> finalBiomes = new ArrayList<>();
            if (oldValue.biomes() != null)
                finalBiomes.addAll(oldValue.biomes());
            if (newValue.biomes() != null)
                finalBiomes.addAll(newValue.biomes());
            Reference2IntOpenHashMap<Block> newBaseBlockAndAuraDrain = new Reference2IntOpenHashMap<>(oldValue.baseBlockAndAuraDrain());
            newBaseBlockAndAuraDrain.putAll(newValue.baseBlockAndAuraDrain());

            SimpleWeightedRandomList.Builder<Block> newOutputOres = new SimpleWeightedRandomList.Builder<>();
            oldValue.outputOres().unwrap().forEach(entry -> newOutputOres.add(entry.getData(), entry.getWeight().asInt()));
            newValue.outputOres().unwrap().forEach(entry -> newOutputOres.add(entry.getData(), entry.getWeight().asInt()));
            return new OreSpawnValues(finalBiomes, newBaseBlockAndAuraDrain, newOutputOres.build(), oldValue.priority());
        });
    }
}
