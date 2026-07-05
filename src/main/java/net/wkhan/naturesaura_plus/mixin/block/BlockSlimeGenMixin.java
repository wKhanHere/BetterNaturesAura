package net.wkhan.naturesaura_plus.mixin.block;

import de.ellpeck.naturesaura.blocks.BlockContainerImpl;
import de.ellpeck.naturesaura.blocks.BlockSlimeSplitGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.wkhan.naturesaura_plus.data.config.AuraGenConfig.slimeGenRange;

@Mixin(BlockSlimeSplitGenerator.class)
public abstract class BlockSlimeGenMixin extends BlockContainerImpl {
    public BlockSlimeGenMixin(String baseName, Class<? extends BlockEntity> tileClass, Properties properties) {
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
        cir.setReturnValue((new AABB(pos)).inflate(slimeGenRange));
    }
}
