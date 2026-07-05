package net.wkhan.naturesaura_plus.mixin.block;

import de.ellpeck.naturesaura.blocks.BlockContainerImpl;
import de.ellpeck.naturesaura.blocks.BlockFlowerGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.wkhan.naturesaura_plus.data.config.AuraGenConfig.flowerGenRange;

@Mixin(BlockFlowerGenerator.class)
public abstract class BlockFlowerGenMixin extends BlockContainerImpl {
    public BlockFlowerGenMixin(String baseName, Class<? extends BlockEntity> tileClass, Properties properties) {
        super(baseName, tileClass, properties);
    }

    @Inject(
            method = "getVisualizationBounds",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$visualizationBoundsFlowerGen(Level level, BlockPos pos, CallbackInfoReturnable<AABB> cir) {
        cir.cancel();
        cir.setReturnValue((new AABB(pos)).inflate(flowerGenRange,1,flowerGenRange));
    }
}
