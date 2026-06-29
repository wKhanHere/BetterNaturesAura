package net.wkhan.naturesaura_plus.mixin.misc;

import de.ellpeck.naturesaura.blocks.BlockContainerImpl;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BlockContainerImpl.class)
public abstract class BlockContainerImplMixin extends BaseEntityBlock {
    protected BlockContainerImplMixin(Properties p_49224_) {
        super(p_49224_);
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/BaseEntityBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"
            ),
            index = 0
    )
    private static BlockBehaviour.Properties naturesaura_plus$blockContainerImplNoOcclusion(BlockBehaviour.Properties properties) {
        return properties.noOcclusion();
    }
}
