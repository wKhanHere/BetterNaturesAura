package net.wkhan.naturesaura_plus.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.wkhan.naturesaura_plus.item.custom.ItemBreakPreventionAll.Events.isBroken;

@Mixin(DispenserBlock.class)
public abstract class BlockDispenserMixin extends net.minecraft.world.level.block.BaseEntityBlock {

    protected BlockDispenserMixin(Properties p_49224_) {
        super(p_49224_);
    }

    @Inject(
            method = "dispenseFrom(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/DispenserBlock;getDispenseMethod(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;",
                    ordinal = 0
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void naturesaura_plus$cancelBrokenDispense(ServerLevel p_52665_, BlockPos p_52666_, CallbackInfo ci, BlockSourceImpl blocksourceimpl, DispenserBlockEntity dispenserblockentity, int i, ItemStack itemstack) {
        if (isBroken(itemstack)) {
            p_52665_.playSound(
                    null,
                    p_52666_.getX() + 0.5D,
                    p_52666_.getY() + 0.5D,
                    p_52666_.getZ() + 0.5D,
                    SoundEvents.ITEM_BREAK,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
            );
            ci.cancel();
        }
    }
}
