package net.wkhan.naturesaura_plus.common.data.auragen;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public final class AuraGenRules {

    public static void projectileGenerationClear() {
        NaturesAuraAPI.PROJECTILE_GENERATIONS.clear();
    }

    public static void addProjectileGeneration(ShootingMarkRule rule) {
        if (!rule.resolve()) return;

        int auraAmount = rule.getAuraAmount();
        EntityType<?> projectileEntityType = rule.getProjectileEntity();

        if (projectileEntityType != null) {
                NaturesAuraAPI.PROJECTILE_GENERATIONS.put(projectileEntityType, auraAmount);
                return;
        }

        TagKey<EntityType<?>> projectileEntityTypeTag = rule.getProjectileEntityTag();
        if (projectileEntityTypeTag != null) {
                ForgeRegistries.ENTITY_TYPES.getValues().stream()
                        .filter(e -> e.is(projectileEntityTypeTag))
                        .forEach(e -> NaturesAuraAPI.PROJECTILE_GENERATIONS.put(e, auraAmount));
        }
    }
}
