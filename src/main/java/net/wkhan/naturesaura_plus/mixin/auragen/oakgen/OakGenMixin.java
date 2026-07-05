package net.wkhan.naturesaura_plus.mixin.auragen.oakgen;


import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityOakGenerator;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.wkhan.naturesaura_plus.data.duckfaces.OakGeneration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow(remap = false) public Queue<BlockPos> scheduledBigTrees;
    @Unique public Queue<Integer> naturesaura_plus$scheduledBigTreesAuraGain = new ArrayDeque<>();

    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$oakGenTick(CallbackInfo ci) {
        ci.cancel();
        if (this.level.isClientSide()) return;
        while(!this.scheduledBigTrees.isEmpty()) {
            BlockPos pos = this.scheduledBigTrees.remove();
            int toAdd = naturesaura_plus$scheduledBigTreesAuraGain.remove();
            boolean canGen = this.canGenerateRightNow(toAdd);
            if (canGen) this.generateAura(toAdd);
            PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles((float)this.worldPosition.getX(),
                    (float)this.worldPosition.getY(), (float)this.worldPosition.getZ(), PacketParticles.Type.OAK_GENERATOR,
                    pos.getX(), pos.getY(), pos.getZ(), canGen ? 1 : 0));
        }
    }

    @Override
    public void naturesaura_plus$scheduledBigTreesAuraGainAdd(Integer integer) {
        naturesaura_plus$scheduledBigTreesAuraGain.add(integer);
    }
}
