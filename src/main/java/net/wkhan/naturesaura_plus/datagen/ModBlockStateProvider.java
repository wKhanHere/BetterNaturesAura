package net.wkhan.naturesaura_plus.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.wkhan.naturesaura_plus.common.block.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, String MODID, ExistingFileHelper exFileHelper) {
        super(output, MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        logBlock((RotatedPillarBlock) ModBlocks.STRIPPED_ANCIENT_LOG.get());
        axisBlock((RotatedPillarBlock) ModBlocks.STRIPPED_ANCIENT_BARK.get(),
                modLoc("block/stripped_ancient_log"), modLoc("block/stripped_ancient_log"));

        simpleBlockItem(ModBlocks.STRIPPED_ANCIENT_LOG.get(), models().getExistingFile(modLoc("block/stripped_ancient_log")));
        simpleBlockItem(ModBlocks.STRIPPED_ANCIENT_BARK.get(), models().getExistingFile(modLoc("block/stripped_ancient_bark")));
    }
}
