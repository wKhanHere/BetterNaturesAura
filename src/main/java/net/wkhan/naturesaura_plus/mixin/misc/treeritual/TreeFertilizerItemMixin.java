package net.wkhan.naturesaura_plus.mixin.misc.treeritual;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.equipment.TreeFertilizerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.wkhan.naturesaura_plus.data.TreeRitualTreeTracker;
import net.wkhan.naturesaura_plus.common.tag.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.updateWoodStandMemoryIfRitual;

@Mixin(TreeFertilizerItem.class)
public class TreeFertilizerItemMixin extends Item {
    public TreeFertilizerItemMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @WrapMethod(
            method = "useOn"
    )
    private InteractionResult naturesaura_plus$initializeRitualData(UseOnContext context, Operation<InteractionResult> original) {
        return updateWoodStandMemoryIfRitual(
                context.getLevel(), context.getClickedPos(),
                () -> original.call(context),
                InteractionResult::consumesAction
        );
    }

    @WrapOperation(
            method = "useOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
            )
    )
    private boolean naturesaura_plus$grabSyntheticTreeCache(Level level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        Set<BlockPos> stems = TreeRitualTreeTracker.STEM_CACHE.get();
        Set<BlockPos> leaves = TreeRitualTreeTracker.LEAF_CACHE.get();
        Set<BlockPos> decorators = TreeRitualTreeTracker.DECORATOR_CACHE.get();

        if (stems == null || leaves == null) return original.call(level, pos, state);

        if (level.getBlockState(pos).is(ModTags.Blocks.TREE_FERTILIZER_SAFE_IN_RITUAL))
            return false;

        if (state.is(ModTags.Blocks.TREE_RITUAL_STEMS))
            stems.add(pos.immutable());
        else if (state.is(ModTags.Blocks.TREE_RITUAL_LEAVES))
            leaves.add(pos.immutable());
        else
            decorators.add(pos.immutable());
        return original.call(level, pos, state);
    }

    @Inject(
            method = "useOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/context/UseOnContext;getClickedPos()Lnet/minecraft/core/BlockPos;",
                    ordinal = 2
            ),
            cancellable = true
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
