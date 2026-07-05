package net.wkhan.naturesaura_plus.data.auragen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;

public record AnimalGenRule(
        Either<EntityType<?>, TagKey<EntityType<?>>> entityId,
        int minimumTimeAliveForGenerationTime,
        int maximumGenerationTime,
        float timeAliveModifierForGenerationTime,
        int minimumTimeAliveForAuraAmount,
        int maximumAuraAmount,
        float timeAliveModifierForAuraAmount ,
        boolean doEntityDropLoot,
        boolean isBabyValid,
        boolean isFlatAuraGain,
        boolean isFlatGenerationTimer
) {

    public static final Codec<AnimalGenRule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    NaturesAuraPlusUtils.elementOrTagCodec(ForgeRegistries.ENTITY_TYPES, Registries.ENTITY_TYPE)
                            .fieldOf("entity").forGetter(AnimalGenRule::entityId),
                    Codec.INT.optionalFieldOf("min_time_alive_for_generation", 15000).forGetter(AnimalGenRule::minimumTimeAliveForGenerationTime),
                    Codec.INT.optionalFieldOf("max_time_for_timer", 200).forGetter(AnimalGenRule::maximumGenerationTime),
                    Codec.FLOAT.optionalFieldOf("time_mod_for_timer",0.002F).forGetter(AnimalGenRule::timeAliveModifierForGenerationTime),
                    Codec.INT.optionalFieldOf("min_time_alive_for_aura", 8000).forGetter(AnimalGenRule::minimumTimeAliveForAuraAmount),
                    Codec.INT.optionalFieldOf("max_aura_gain", 25000).forGetter(AnimalGenRule::maximumAuraAmount),
                    Codec.FLOAT.optionalFieldOf("time_mod_for_aura", 0.5F).forGetter(AnimalGenRule::timeAliveModifierForAuraAmount),
                    Codec.BOOL.optionalFieldOf("do_entity_drop_loot", false).forGetter(AnimalGenRule::doEntityDropLoot),
                    Codec.BOOL.optionalFieldOf("is_baby_valid", false).forGetter(AnimalGenRule::isBabyValid),
                    Codec.BOOL.optionalFieldOf("is_flat_aura_gain", false).forGetter(AnimalGenRule::isFlatAuraGain),
                    Codec.BOOL.optionalFieldOf("is_flat_generation_timer", false).forGetter(AnimalGenRule::isFlatGenerationTimer)
            ).apply(instance, AnimalGenRule::new)
    );

    public boolean isTag() {
        return this.entityId.right().isPresent();
    }

    public EntityType<?> getEntity() {
        return this.entityId.left().orElse(null);
    }

    public TagKey<EntityType<?>> getEntityTag() {
        return this.entityId.right().orElse(null);
    }

}
