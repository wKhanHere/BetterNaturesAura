package net.wkhan.naturesaura_plus.mixin.auragen.oakgen;


import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityOakGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.wkhan.naturesaura_plus.data.duckfaces.OakGeneration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayDeque;
import java.util.Queue;

@Mixin(BlockEntityOakGenerator.class)
public abstract class OakGenMixin extends BlockEntityImpl implements OakGeneration {
    public OakGenMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Unique private final Queue<Integer> naturesaura_plus$scheduledBigTreesAuraGain = new ArrayDeque<>();

    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$oakGenTick(CallbackInfo ci) {
        ci.cancel();
    }

    @Override
    public void naturesaura_plus$scheduledBigTreesAuraGainAdd(Integer integer) {
        naturesaura_plus$scheduledBigTreesAuraGain.add(integer);
    }

    @Override
    public int naturesaura_plus$scheduledBigTreesAuraGainRemove() {
        if (naturesaura_plus$scheduledBigTreesAuraGain.isEmpty())
            return 0;
        return naturesaura_plus$scheduledBigTreesAuraGain.remove();
    }
}
