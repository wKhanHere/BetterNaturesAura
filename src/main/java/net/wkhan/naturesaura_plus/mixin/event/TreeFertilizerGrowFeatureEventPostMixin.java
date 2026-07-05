package net.wkhan.naturesaura_plus.mixin.event;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.equipment.TreeFertilizerItem;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.wkhan.naturesaura_plus.data.TreeRitualTreeTracker;
import net.wkhan.naturesaura_plus.data.duckfaces.AbstractWoodStand;
import net.wkhan.naturesaura_plus.data.duckfaces.MultiBlockUtil;
import net.wkhan.naturesaura_plus.common.tag.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(TreeFertilizerItem.class)
public class TreeFertilizerGrowFeatureEventPostMixin extends Item {
    public TreeFertilizerGrowFeatureEventPostMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @WrapMethod(
            method = "useOn"
    )
    private InteractionResult naturesaura_plus$initializeRitualData(UseOnContext context, Operation<InteractionResult> original) {
        Level level = context.getLevel();
        BlockPos saplingPos = context.getClickedPos();
        ((MultiBlockUtil) Multiblocks.TREE_RITUAL).naturesaura_plus$allowAirInRitual();
        boolean isRitual = Multiblocks.TREE_RITUAL.isComplete(level, saplingPos);
        if (!isRitual)
            return original.call(context);

        Set<BlockPos> capturedStems = new HashSet<>();
        Set<BlockPos> capturedLeaves = new HashSet<>();
        Set<BlockPos> capturedDecorators = new HashSet<>();
        TreeRitualTreeTracker.STEM_CACHE.set(capturedStems);
        TreeRitualTreeTracker.LEAF_CACHE.set(capturedLeaves);
        TreeRitualTreeTracker.DECORATOR_CACHE.set(capturedDecorators);

        InteractionResult result;
        try {
            result = original.call(context);
        } finally {
            TreeRitualTreeTracker.STEM_CACHE.remove();
            TreeRitualTreeTracker.LEAF_CACHE.remove();
            TreeRitualTreeTracker.DECORATOR_CACHE.remove();
        }

        if (!result.consumesAction() || capturedStems.isEmpty())
            return result;

        Multiblocks.TREE_RITUAL.forEach(saplingPos, 'W', (standPos, matcher) -> {
            BlockEntity tile = level.getBlockEntity(standPos);
            if (tile instanceof AbstractWoodStand woodStand) {
                woodStand.naturesaura_plus$setTreeStemCache(capturedStems.isEmpty() ? null : capturedStems);
                woodStand.naturesaura_plus$setTreeLeafCache(capturedLeaves.isEmpty() ? null : capturedLeaves);
                woodStand.naturesaura_plus$setTreeDecoratorCache(capturedDecorators.isEmpty() ? null : capturedDecorators);
            }
            return true;
        });
        return result;
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
