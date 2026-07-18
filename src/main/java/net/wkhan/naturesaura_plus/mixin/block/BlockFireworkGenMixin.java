package net.wkhan.naturesaura_plus.mixin.block;

import de.ellpeck.naturesaura.blocks.BlockFireworkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.wkhan.naturesaura_plus.data.config.AuraGenConfig.FIREWORK_GEN_RANGE;

@Mixin(BlockFireworkGenerator.class)
public abstract class BlockFireworkGenMixin extends BlockContainerImplMixin {
    protected BlockFireworkGenMixin(Properties p_49224_) {
        super(p_49224_);
    }

    @Inject(
            method = "getVisualizationBounds",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$visualizationBoundsMossGen(Level level, BlockPos pos, CallbackInfoReturnable<AABB> cir) {
        cir.cancel();
        cir.setReturnValue((new AABB(pos)).inflate(FIREWORK_GEN_RANGE.get()));
    }
}
