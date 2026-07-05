package net.wkhan.naturesaura_plus.data.auragen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;

public record SlimeGenRule(
        Either<EntityType<?>,TagKey<EntityType<?>>> entityId,
        int auraAmount,
        int slimeColor,
        int minSizeForSlime,
        int flatGenerationTimer,
        float generationTimerModifier,
        float sizeModifier,
        boolean doSlimeSizeScaling,
        boolean doEntityDropLoot,
        boolean isFlatGenerationTimer
) {

    public static final Codec<SlimeGenRule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    NaturesAuraPlusUtils.elementOrTagCodec(ForgeRegistries.ENTITY_TYPES, Registries.ENTITY_TYPE)
                            .fieldOf("entity").forGetter(SlimeGenRule::entityId),
                    Codec.INT.fieldOf("aura_gain").forGetter(SlimeGenRule::auraAmount),
                    Codec.INT.optionalFieldOf("slime_color", 5089359).forGetter(SlimeGenRule::slimeColor),
                    Codec.INT.optionalFieldOf("min_size_for_slime", 2).forGetter(SlimeGenRule::minSizeForSlime),
                    Codec.INT.optionalFieldOf("flat_generation_timer", 10).forGetter(SlimeGenRule::flatGenerationTimer),
                    Codec.FLOAT.optionalFieldOf("generation_timer_modifier", 30F).forGetter(SlimeGenRule::generationTimerModifier),
                    Codec.FLOAT.optionalFieldOf("size_modifier", 1.0F).forGetter(SlimeGenRule::sizeModifier),
                    Codec.BOOL.optionalFieldOf("use_slime_size", false).forGetter(SlimeGenRule::doSlimeSizeScaling),
                    Codec.BOOL.optionalFieldOf("entity_drop_loot", true).forGetter(SlimeGenRule::doEntityDropLoot),
                    Codec.BOOL.optionalFieldOf("is_flat_generation_timer", false).forGetter(SlimeGenRule::isFlatGenerationTimer)
            ).apply(instance, SlimeGenRule::new)
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
