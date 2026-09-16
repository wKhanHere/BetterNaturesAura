package net.wkhan.naturesaura_plus.datagen;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.wkhan.naturesaura_plus.common.block.ModBlocks;
import net.wkhan.naturesaura_plus.common.block.OvenPartBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, String MODID, ExistingFileHelper exFileHelper) {
        super(output, MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile brickModel = models().cubeAll("auric_oven_brick", modLoc("block/auric_oven_brick"));
        simpleBlockItem(ModBlocks.AURIC_OVEN_BRICK.get(), brickModel);

        getVariantBuilder(ModBlocks.AURIC_OVEN_BRICK.get()).forAllStates(state -> {
            OvenPartBlock.OvenPart part = state.getValue(OvenPartBlock.OVEN_PART);
            Direction dir = state.getValue(OvenPartBlock.FACING_DIRECTION);

            if (part == OvenPartBlock.OvenPart.BASE || part == OvenPartBlock.OvenPart.EDGE || part == OvenPartBlock.OvenPart.CORE)
                return ConfiguredModel.builder().modelFile(brickModel).build();
            String index = part.name().split("_")[1];
            String modelName = "auric_oven_front_" + index + "_" + dir.getName();

            return ConfiguredModel.builder()
                    .modelFile(models().withExistingParent(modelName, "minecraft:block/cube")
                            .texture(dir.getName(), modLoc("block/auric_oven_front_" + index))
                            .texture(dir.getOpposite().getName(), modLoc("block/auric_oven_brick"))
                            .texture(Direction.UP.getName(), modLoc("block/auric_oven_brick"))
                            .texture(Direction.DOWN.getName(), modLoc("block/auric_oven_brick"))
                            .texture(dir.getClockWise().getName(), modLoc("block/auric_oven_brick"))
                            .texture(dir.getCounterClockWise().getName(), modLoc("block/auric_oven_brick"))
                            .texture("particle", modLoc("block/auric_oven_brick"))).build();
        });
    }
}
