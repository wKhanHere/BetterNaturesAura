package net.wkhan.naturesaura_plus.mixin.event;

import com.simibubi.create.content.equipment.TreeFertilizerItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TreeFertilizerItem.class)
public class TreeFertilizerGrowFeatureEventPostMixin extends Item {
    public TreeFertilizerGrowFeatureEventPostMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Inject(
            method = "useOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/context/UseOnContext;getClickedPos()Lnet/minecraft/core/BlockPos;",
                    ordinal = 2
            ),
            remap = false

    )
    private void naturesaura_plus$callGrowFeatureEventWithTreeFertilizer (UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        SaplingGrowTreeEvent event = new SaplingGrowTreeEvent(level, level.getRandom(), context.getClickedPos(), null);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Event.Result.DENY) {
            cir.cancel();
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }
}
