package net.wkhan.naturesaura_plus.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record OreSpawnRule(
        ResourceKey<DimensionType> dimensionType,
        @Nullable List<ResourceKey<Biome>> biomes,
        Map<Either<Block, TagKey<Block>>, Integer> baseBlockAndAuraDrain,
        SimpleWeightedRandomList<Either<Block, TagKey<Block>>> outputOres,
        int priority
) implements PriorityRule {
    public static final Codec<OreSpawnRule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceKey.codec(Registries.DIMENSION_TYPE).fieldOf("dimension").forGetter(OreSpawnRule::dimensionType),
                    ResourceKey.codec(Registries.BIOME).listOf()
                            .optionalFieldOf("biomes",List.of()).forGetter(OreSpawnRule::biomes),
                    Codec.unboundedMap(NaturesAuraPlusUtils.elementOrTagCodec(ForgeRegistries.BLOCKS, Registries.BLOCK), Codec.INT)
                            .fieldOf("base_block_to_aura_drain").forGetter(OreSpawnRule::baseBlockAndAuraDrain),
                    SimpleWeightedRandomList.wrappedCodecAllowingEmpty(NaturesAuraPlusUtils
                            .elementOrTagCodec(ForgeRegistries.BLOCKS, Registries.BLOCK)).fieldOf("ores").forGetter(OreSpawnRule::outputOres),
                    Codec.INT.optionalFieldOf("priority", 1).forGetter(OreSpawnRule::priority)
            ).apply(instance, OreSpawnRule::new)
    );

    @Override
    public int getPriority() {
        return priority;
    }
}
