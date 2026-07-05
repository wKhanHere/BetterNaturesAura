package net.wkhan.naturesaura_plus.client.render;

import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class DynamicWoodStandModel implements IDynamicBakedModel {

    public static final ModelProperty<BlockState> STAND_MATERIAL = new ModelProperty<>();
    private final BakedModel originalModel;

    public DynamicWoodStandModel(BakedModel originalModel) {
        this.originalModel = originalModel;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        BlockState material = extraData.get(STAND_MATERIAL);
        if (material == null || material.isAir())
            return originalModel.getQuads(state, side, rand, extraData, renderType);

        BakedModel materialModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(material);
        List<BakedQuad> woodenQuads = materialModel.getQuads(material, side, rand, ModelData.EMPTY, renderType);
        Transformation transform = new Transformation(
                new Matrix4f().translate(0.1875F, 0.0F, 0.1875F).scale(0.625F, 0.8125F, 0.625F)
        );
        IQuadTransformer transformer = QuadTransformers.applying(transform);
        List<BakedQuad> materialQuads = transformer.process(woodenQuads);

        return new ArrayList<>(materialQuads);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }
    @Override
    public boolean isGui3d() {
        return originalModel.isGui3d();
    }
    @Override
    public boolean usesBlockLight() {
        return originalModel.usesBlockLight();
    }
    @Override
    public boolean isCustomRenderer() {
        return originalModel.isCustomRenderer();
    }
    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return originalModel.getParticleIcon();
    }
    @Override
    public @NotNull ItemOverrides getOverrides() {
        return originalModel.getOverrides();
    }
}
