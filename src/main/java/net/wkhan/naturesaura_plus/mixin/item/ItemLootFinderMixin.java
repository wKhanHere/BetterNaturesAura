package net.wkhan.naturesaura_plus.mixin.item;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.items.ItemImpl;
import de.ellpeck.naturesaura.items.ItemLootFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.wkhan.naturesaura_plus.common.tag.ModTags.Blocks.LOOT_FINDER_TREASURE;
import static net.wkhan.naturesaura_plus.common.tag.ModTags.Blocks.LOOT_FINDER_TREASURE_CHEST;
import static net.wkhan.naturesaura_plus.data.config.GameplayConfig.*;

@Mixin(ItemLootFinder.class)
public abstract class ItemLootFinderMixin extends ItemImpl {
    public ItemLootFinderMixin(String baseName) {
        super(baseName);
    }

    //Code is from Elpeck's ItemLootFinder class, only configs and extra block compatibility have been added.
    @Inject(
            method = "use",
            at = @At("HEAD"),
            cancellable = true
    )
    private void naturesaura_plus$allowVarietyForLootFinder(
            Level levelIn, Player playerIn, InteractionHand handIn,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        cir.cancel();
        ItemStack stack = playerIn.getItemInHand(handIn);
        NaturesAuraAPI.IInternalHooks inst = NaturesAuraAPI.instance();
        if (!inst.extractAuraFromPlayer(playerIn, lootFinderAuraCost, false)) {
            cir.setReturnValue(new InteractionResultHolder<>(InteractionResult.FAIL, stack));
            return;
        }
        if (!levelIn.isClientSide) return;
        inst.setParticleDepth(false);
        inst.setParticleSpawnRange(lootFinderRange);
        inst.setParticleCulling(false);
        BlockPos centre = playerIn.blockPosition();
        Helper.getBlockEntitiesInArea(levelIn, centre, lootFinderRange, (tile) -> {
            if (tile.getBlockState().is(LOOT_FINDER_TREASURE_CHEST)) {
                inst.spawnMagicParticle((float) tile.getBlockPos().getX() + 0.5F, (float) tile.getBlockPos().getY() + 0.5F,
                        (float) tile.getBlockPos().getZ() + 0.5F, 0.0F, 0.0F, 0.0F,
                        16761095, 6.0F, lootFinderLightLifeInTicks, 0.0F, false, true);
            }
            return false;
        });

        List<BlockPos> foundBlocks = NaturesAuraPlusUtils.scanSphereAgainstTag(levelIn, centre, lootFinderRange, LOOT_FINDER_TREASURE);
        foundBlocks.forEach(pos ->
                inst.spawnMagicParticle((float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F,
                        (float) pos.getZ() + 0.5F, 0.0F, 0.0F, 0.0F,
                        12434877, 6.0F, lootFinderLightLifeInTicks, 0.0F, false, true)
                );

        inst.setParticleDepth(true);
        inst.setParticleSpawnRange(32);
        inst.setParticleCulling(true);
        playerIn.swing(handIn);

        playerIn.getCooldowns().addCooldown(this, lootFinderUseCooldownInTicks);
        cir.setReturnValue(new InteractionResultHolder<>(InteractionResult.SUCCESS, stack));
    }
}