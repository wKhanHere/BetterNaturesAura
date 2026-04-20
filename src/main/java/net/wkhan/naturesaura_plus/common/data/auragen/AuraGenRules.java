package net.wkhan.naturesaura_plus.common.data.auragen;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public final class AuraGenRules {

    public record deMossedBlockAuraAmountPair(Block deMossedBlock, int auraAmount) {}
    public static final Map<Block, deMossedBlockAuraAmountPair> MOSS_GENERATIONS = new HashMap<>();

    public static void projectileGenerationClear() {
        NaturesAuraAPI.PROJECTILE_GENERATIONS.clear();
    }

    public static void addProjectileGeneration(ProjectileGenRule rule) {
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


    public static void mossGenerationClear() {
        MOSS_GENERATIONS.clear();
    }

    public static void addMossGeneration(MossGenRule rule) {
        if (!rule.resolve()) return;

        int auraAmount = rule.getAuraAmount();
        Block mossBlock = rule.getBlockInput();
        TagKey<Block> mossBlockTag = rule.getBlockInputTag();
        Block deMossedBlock = rule.getBlockResult();

        if (mossBlock != null) {
            MOSS_GENERATIONS.put(mossBlock, new deMossedBlockAuraAmountPair(deMossedBlock, auraAmount));
            return;
        }

        ForgeRegistries.BLOCKS.getValues().stream()
                .filter(b -> b.defaultBlockState().is(mossBlockTag))
                .forEach(b -> MOSS_GENERATIONS.put(b, new deMossedBlockAuraAmountPair(deMossedBlock, auraAmount)));
    }
}

