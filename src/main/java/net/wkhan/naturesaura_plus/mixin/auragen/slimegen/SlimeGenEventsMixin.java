package net.wkhan.naturesaura_plus.mixin.auragen.slimegen;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntitySlimeSplitGenerator;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.wkhan.naturesaura_plus.data.auragen.AuraGenRules;
import net.wkhan.naturesaura_plus.data.duckfaces.SlimeGeneration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.wkhan.naturesaura_plus.data.config.AuraGenConfig.slimeGenRange;
import static net.wkhan.naturesaura_plus.data.auragen.AuraGenRules.SLIME_GENERATIONS;

@Mixin(targets = "de.ellpeck.naturesaura.blocks.BlockSlimeSplitGenerator$Events")
public abstract class SlimeGenEventsMixin {

    @Inject(
            method = "onLivingDeath",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$slimeAuraGeneratorEvent(LivingDeathEvent event, CallbackInfo ci) {
        ci.cancel();
        LivingEntity entity = event.getEntity();
        AuraGenRules.slimeValues slimeValues = SLIME_GENERATIONS.get(entity.getType());
        if (slimeValues == null) return;
        if (entity.level().isClientSide()) return;
        if (entity.getPersistentData().getBoolean("naturesaura:pet_reviver")) return;
        if (entity instanceof Slime slime && slime.getSize() < slimeValues.minSizeForSlime()) return;
        Helper.getBlockEntitiesInArea(entity.level(), entity.blockPosition(), slimeGenRange, (tile) -> {
            if (!(tile instanceof BlockEntitySlimeSplitGenerator gen)) return false;
            if (gen.isBusy()) return false;
            if (!(slimeValues.doEntityDropLoot())) entity.getPersistentData().putBoolean("naturesaura:no_drops", true);
            ((SlimeGeneration) gen).naturesaura_plus$slimeTileAuraGeneratorStart(entity);
            return true;
        });
    }
}
