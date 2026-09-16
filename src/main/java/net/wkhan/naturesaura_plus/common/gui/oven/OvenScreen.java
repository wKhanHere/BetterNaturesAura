package net.wkhan.naturesaura_plus.common.gui.oven;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.buildFluidStackToolTip;
import static net.wkhan.naturesaura_plus.data.config.GameplayConfig.OVEN_FLUID_TANK_CAPACITY;

public class OvenScreen extends AbstractContainerScreen<OvenMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NaturesAuraPlus.MODID, "textures/gui/oven_menu.png");

    public OvenScreen(OvenMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 77;
        this.titleLabelY = 0;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int mainInvCentreX = leftPos + 1;
        int mainInvCentreY = topPos + 1;

        guiGraphics.blit(TEXTURE, mainInvCentreX, mainInvCentreY, 1, 1, 174, 164); //Main Inventory
        renderFluid(guiGraphics, menu.blockEntity.multiblockData.fluidTank.getFluid(),
                menu.blockEntity.multiblockData.fluidTank.getCapacity(),
                mainInvCentreX + 126, mainInvCentreY + 16, 16, 47); //Fluid
        guiGraphics.blit(TEXTURE, mainInvCentreX + 124, mainInvCentreY + 13,
                176, 25, 20, 51); //Tank Overlay

        if (!menu.isCrafting())
            return;
        guiGraphics.blit(TEXTURE, mainInvCentreX + 39,
                mainInvCentreY + 31 + (int) Math.ceil(14 * menu.getFractionalProgress()),
                176, 1 + (int) Math.ceil(14 * menu.getFractionalProgress()),
                13, 14 - (int) Math.floor(14 * menu.getFractionalProgress())); //Progress Flame
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderFluidTooltip(guiGraphics, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public void renderFluidTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!isHovering(126, 15, 16, 47, mouseX, mouseY))
            return;
        guiGraphics.renderComponentTooltip(
                this.font, buildFluidStackToolTip(menu.blockEntity.multiblockData.fluidTank.getFluid(), OVEN_FLUID_TANK_CAPACITY.get()), mouseX, mouseY
        );
    }

    public static void renderFluid(GuiGraphics guiGraphics, FluidStack fluidStack, int capacity, int x, int y, int width, int height) {
        if (fluidStack.isEmpty() || height <= 0 || width <= 0 || capacity <= 0)
            return;

        int fluidHeight = (int) Math.ceil(height * Math.min((float) fluidStack.getAmount(), capacity) / capacity);
        if (fluidHeight <= 0)
            return;

        IClientFluidTypeExtensions fluidExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation stillTexture = fluidExtensions.getStillTexture(fluidStack);
        if (stillTexture == null)
            return;
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
        int color = fluidExtensions.getTintColor(fluidStack);
        float a = ((color >> 24) & 0xFF) / 255.0F;
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(r, g, b, a);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Matrix4f matrix = guiGraphics.pose().last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        int bottom = y + height - 1;
        for (int i = 0; i < width; i += 16) {
            int drawWidth = Math.min(width - i, 16);

            for (int j = 0; j < fluidHeight; j += 16) {
                int drawHeight = Math.min(fluidHeight - j, 16);
                int drawX = x + i;
                int drawY = bottom - j - drawHeight;

                float u0 = sprite.getU0();
                float v0 = sprite.getV0();
                float u1 = u0 + (sprite.getU1() - u0) * (drawWidth / 16.0F);
                float v1 = sprite.getV1();
                float vTop = v1 - (v1 - v0) * (drawHeight / 16.0F);

                buffer.vertex(matrix, drawX, drawY + drawHeight, 0).uv(u0, v1).endVertex();
                buffer.vertex(matrix, drawX + drawWidth, drawY + drawHeight, 0).uv(u1, v1).endVertex();
                buffer.vertex(matrix, drawX + drawWidth, drawY, 0).uv(u1, vTop).endVertex();
                buffer.vertex(matrix, drawX, drawY, 0).uv(u0, vTop).endVertex();
            }
        }

        tesselator.end();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

}