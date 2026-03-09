package net.wkhan.naturesaura_plus.mixin;


import de.ellpeck.naturesaura.items.ItemAuraCache;
import de.ellpeck.naturesaura.items.ItemImpl;
import net.minecraft.core.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemAuraCache.class)
public abstract class AuraRepairMixin extends ItemImpl {
    public AuraRepairMixin(String baseName, Properties properties) {
        super(baseName, properties);
    }



    @Redirect(
            method = "inventoryTick", // (Or whatever the method is named)
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/NonNullList;size()I",
                    ordinal = 1
            ))
    private int naturesaura_plus$expandArmorToOffhand(NonNullList instance) {
        return instance.size() + 1; // Tricks the math into including slot 40
    }
}
