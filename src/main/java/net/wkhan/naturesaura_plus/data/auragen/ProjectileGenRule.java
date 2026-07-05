package net.wkhan.naturesaura_plus.data.auragen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;

public record ProjectileGenRule(
        Either<EntityType<?>, TagKey<EntityType<?>>> projectileId,
        int auraAmount
) {

    public static final Codec<ProjectileGenRule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    NaturesAuraPlusUtils.elementOrTagCodec(ForgeRegistries.ENTITY_TYPES, Registries.ENTITY_TYPE)
                            .fieldOf("projectile").forGetter(ProjectileGenRule::projectileId),
                    Codec.INT.fieldOf("aura_gain").forGetter(ProjectileGenRule::auraAmount)
            ).apply(instance, ProjectileGenRule::new)
    );

    public boolean isTag() {
        return this.projectileId.right().isPresent();
    }

    public EntityType<?> getProjectile() {
        return this.projectileId.left().orElse(null);
    }

    public TagKey<EntityType<?>> getProjectileTag() {
        return this.projectileId.right().orElse(null);
    }
}
