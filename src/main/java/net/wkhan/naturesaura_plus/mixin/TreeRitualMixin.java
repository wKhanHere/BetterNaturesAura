package net.wkhan.naturesaura_plus.mixin;

import de.ellpeck.naturesaura.api.multiblock.IMultiblock;
import de.ellpeck.naturesaura.api.multiblock.Matcher;
import de.ellpeck.naturesaura.blocks.multi.Multiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.wkhan.naturesaura_plus.common.tag.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiFunction;

@Mixin(Multiblock.class)
public abstract class TreeRitualMixin {

    @Shadow public abstract BlockPos getStart(BlockPos center);
    @Shadow public abstract char getChar(BlockPos offset);
    @Shadow public abstract boolean forEach(BlockPos center, char c, BiFunction<BlockPos, Matcher, Boolean> function);

    @Inject(
            method = "isComplete",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$treeRitualMultiCheck(Level level, BlockPos center, CallbackInfoReturnable<Boolean> cir) {
        ResourceLocation name = ((IMultiblock) this).getName();
        if (!(name.getNamespace().equals("naturesaura") && name.getPath().equals("tree_ritual"))) {
            return;
        }

        BlockPos start = this.getStart(center);
        boolean result = this.forEach(center, (char) 0, (pos, matcher) -> {
            var offset = pos.subtract(start);
            if (this.getChar(offset) == '0') {
                boolean isValidBlock = level.getBlockState(pos).is(ModTags.Blocks.TREE_RITUAL_SAPLINGS) || level.getBlockState(pos).is(ModTags.Blocks.TREE_RITUAL_STEMS);
                System.out.println("CENTER BLOCK FOUND: " + level.getBlockState(pos).getBlock() + " | IS IN TAG: " + isValidBlock);
                return isValidBlock;
            }
            return matcher.check().matches(level, start, offset, pos, level.getBlockState(pos), this.getChar(offset));
        });
        System.out.println("TREE RITUAL MIXIN FIRED! FINAL RESULT: " + result);
        cir.setReturnValue(result);
    }
}
