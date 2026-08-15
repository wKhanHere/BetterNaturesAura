package net.wkhan.naturesaura_plus.data.auragen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.wkhan.naturesaura_plus.data.PriorityRule;

public record OakGenRule(
        ResourceKey<ConfiguredFeature<?,?>> featureToReplace,
        ResourceKey<ConfiguredFeature<?,?>> featureReplacement,
        int auraAmount,
        int priority
) implements PriorityRule {

    public static final Codec<OakGenRule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceKey.codec(Registries.CONFIGURED_FEATURE).fieldOf("feature_to_replace")
                            .forGetter(OakGenRule::featureToReplace),
                    ResourceKey.codec(Registries.CONFIGURED_FEATURE)
                            .fieldOf("feature_replacement").forGetter(OakGenRule::featureReplacement),
                    Codec.INT.fieldOf("aura_gain").forGetter(OakGenRule::auraAmount),
                    Codec.INT.optionalFieldOf("priority", 1).forGetter(OakGenRule::priority)
            ).apply(instance, OakGenRule::new)
    );

    @Override
    public int getPriority() {
        return priority;
    }
}
