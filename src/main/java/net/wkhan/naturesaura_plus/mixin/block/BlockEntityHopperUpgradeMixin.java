package net.wkhan.naturesaura_plus.mixin.block;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityHopperUpgrade;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.wkhan.naturesaura_plus.compat.kubejs.KubeJSNaturesAuraPlus.HopperUpgradeBaseCheck;
import static net.wkhan.naturesaura_plus.common.tag.ModTags.Blocks.HOPPER_UPGRADE_AFFECTED;

@Mixin(BlockEntityHopperUpgrade.class)
public abstract class BlockEntityHopperUpgradeMixin extends BlockEntityImpl {
    public BlockEntityHopperUpgradeMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = "isValidHopper",
            at = @At("HEAD"),
            cancellable = true,
            remap = false

    )
    private static void naturesaura_plus$allowVarietyHopper(
            BlockEntity tile, CallbackInfoReturnable<Boolean> cir
    ) {
        cir.cancel();
        if (tile == null) {
            cir.setReturnValue(false);
            return;
        }
        boolean pass;
        BlockState state = tile.getBlockState();

        pass = state.is(HOPPER_UPGRADE_AFFECTED);

        if (!NaturesAuraPlus.isKubeJsLoaded) {
            cir.setReturnValue(pass);
            return;
        }

        cir.setReturnValue(HopperUpgradeBaseCheck(tile, pass));
    }
}
