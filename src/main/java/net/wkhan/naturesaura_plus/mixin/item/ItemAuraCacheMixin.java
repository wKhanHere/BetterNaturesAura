package net.wkhan.naturesaura_plus.mixin.item;

import de.ellpeck.naturesaura.items.ItemAuraCache;
import de.ellpeck.naturesaura.items.ItemImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.inventoryAuraContainerTick;

@Mixin(ItemAuraCache.class)
public class ItemAuraCacheMixin extends ItemImpl {
    public ItemAuraCacheMixin(String baseName) {
        super(baseName);
    }

    @Inject(
            method = "inventoryTick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void naturesaura_plus$invalidateItemAuraCacheTick(ItemStack stackIn, Level levelIn, Entity entityIn, int itemSlot, boolean isSelected, CallbackInfo ci) {
        ci.cancel();
        inventoryAuraContainerTick(stackIn, levelIn, entityIn, itemSlot);
    }
}
