package net.wkhan.naturesaura_plus.mixin.block;

import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.blocks.BlockChorusGenerator;
import de.ellpeck.naturesaura.blocks.BlockContainerImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;

import static net.wkhan.naturesaura_plus.data.config.AuraGenConfig.chorusGenRange;

@Mixin(BlockChorusGenerator.class)
public abstract class BlockChorusGenMixin extends BlockContainerImpl implements IVisualizable {

    public BlockChorusGenMixin(String baseName, Class<? extends BlockEntity> tileClass, Properties properties) {
        super(baseName, tileClass, properties);
    }

    @OnlyIn(Dist.CLIENT)
    public AABB getVisualizationBounds(Level level, BlockPos pos) {
        return (new AABB(pos)).inflate(chorusGenRange);
    }

    @OnlyIn(Dist.CLIENT)
    public int getVisualizationColor(Level level, BlockPos pos) {
        return 13148872;
    }
}
