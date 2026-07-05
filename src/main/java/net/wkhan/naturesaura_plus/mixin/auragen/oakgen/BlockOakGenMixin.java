package net.wkhan.naturesaura_plus.mixin.auragen.oakgen;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.BlockContainerImpl;
import de.ellpeck.naturesaura.blocks.BlockOakGenerator;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityOakGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.wkhan.naturesaura_plus.data.auragen.AuraGenRules;
import net.wkhan.naturesaura_plus.data.duckfaces.OakGeneration;
import net.wkhan.naturesaura_plus.common.tag.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static net.wkhan.naturesaura_plus.data.config.AuraGenConfig.oakGenRange;
import static net.wkhan.naturesaura_plus.data.auragen.AuraGenRules.OAK_GENERATIONS;

@Mixin(BlockOakGenerator.class)
public abstract class BlockOakGenMixin extends BlockContainerImpl {
    public BlockOakGenMixin(String baseName, Class<? extends BlockEntity> tileClass, Properties properties) {
        super(baseName, tileClass, properties);
    }

    @Inject(
            method = "getVisualizationBounds",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$visualizationBoundsMossGen(Level level, BlockPos pos, CallbackInfoReturnable<AABB> cir) {
        cir.cancel();
        cir.setReturnValue((new AABB(pos)).inflate(oakGenRange));
    }

    @Inject(
            method = "onTreeGrow",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$onTreeGrowOakGen(SaplingGrowTreeEvent event, CallbackInfo ci) {
        ci.cancel();
        LevelAccessor level = event.getLevel();
        BlockPos pos = event.getPos();
        if (!(level instanceof Level lvl) || level.isClientSide() || !(IAuraType.forLevel(lvl).isSimilar(NaturesAuraAPI.TYPE_OVERWORLD)) //Make aura type check config driven
                || !(level.getBlockState(pos).is(ModTags.Blocks.OAK_GEN_SAPLING))) return;
        Helper.getBlockEntitiesInArea(level, pos, oakGenRange, (tile) -> {
            if (!(tile instanceof BlockEntityOakGenerator oakGen)) return false;
            Holder<ConfiguredFeature<?,?>> tree = event.getFeature();
            if (tree == null) return false;
            Optional<ResourceKey<ConfiguredFeature<?,?>>> optionalKey = tree.unwrapKey();
            if (optionalKey.isEmpty()) return false;
            AuraGenRules.oakValues oakValues = OAK_GENERATIONS.get(optionalKey.get());
            if (oakValues == null) return false;
            ResourceKey<ConfiguredFeature<?, ?>> replacement = oakValues.featureReplacement();
            if (replacement == null) return true;
            oakGen.scheduledBigTrees.add(pos);
            ((OakGeneration) oakGen).naturesaura_plus$scheduledBigTreesAuraGainAdd(oakValues.auraAmount());
            event.setFeature(replacement);
            return true;
        });
    }
}
