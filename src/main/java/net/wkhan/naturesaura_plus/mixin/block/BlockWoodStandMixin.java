package net.wkhan.naturesaura_plus.mixin.block;

import de.ellpeck.naturesaura.blocks.BlockContainerImpl;
import de.ellpeck.naturesaura.blocks.BlockWoodStand;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockWoodStand.class)
public abstract class BlockWoodStandMixin extends BlockContainerImpl implements SimpleWaterloggedBlock {
    public BlockWoodStandMixin(String baseName, Class<? extends BlockEntity> tileClass, Properties properties) {
        super(baseName, tileClass, properties);
    }
}
