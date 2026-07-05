package net.wkhan.naturesaura_plus.client.event;

import com.mojang.blaze3d.platform.Window;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityFlowerGenerator;
import de.ellpeck.naturesaura.events.ClientEvents;
import de.ellpeck.naturesaura.items.ItemEye;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;
import net.wkhan.naturesaura_plus.client.ClientDualBarTooltipComponent;
import net.wkhan.naturesaura_plus.client.render.DynamicWoodStandModel;
import net.wkhan.naturesaura_plus.data.duckfaces.FlowerGeneration;
import net.wkhan.naturesaura_plus.compat.botania.ItemAuraManaHolder;
import vazkii.patchouli.common.item.ItemModBook;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ClientCommonEvents {

    @Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID,
            bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class onSetupClientEvents {
        @SubscribeEvent
        public static void onRegisterTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(
                    ItemAuraManaHolder.DualAuraManaItemImpl.RecordDualAuraMana.class,
                    data -> new ClientDualBarTooltipComponent(data.aura(), data.max_aura(), data.mana(), data.max_mana()));
        }

        @SubscribeEvent
        public static void onModelBake(ModelEvent.ModifyBakingResult event) {
            ResourceLocation standId = ResourceLocation.fromNamespaceAndPath("naturesaura", "wood_stand");
            ModelResourceLocation modelLocWaterLogged = new ModelResourceLocation(standId, "waterlogged=true");
            ModelResourceLocation modelLoc = new ModelResourceLocation(standId, "waterlogged=false");

            BakedModel existingModelWaterLogged = event.getModels().get(modelLocWaterLogged);
            BakedModel existingModel = event.getModels().get(modelLoc);
            if (existingModelWaterLogged == null || existingModel == null)
                return;
            event.getModels().put(modelLocWaterLogged, new DynamicWoodStandModel(existingModelWaterLogged));
            event.getModels().put(modelLoc, new DynamicWoodStandModel(existingModel));
       }

        @SubscribeEvent
        public static void onAddPackFinders(AddPackFindersEvent event) {
            if (event.getPackType() != PackType.CLIENT_RESOURCES) return;
            IModFile modFile = ModList.get().getModFileById(NaturesAuraPlus.MODID).getFile();
            Path packPath = modFile.findResource("naturesaura_override");

            Pack pack = Pack.readMetaAndCreate(
                    NaturesAuraPlus.MODID, Component.literal("NA-Overrides"),
                    true, (name) -> new PathPackResources(name, true, packPath),
                    PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN
            );
            if (pack != null)
                event.addRepositorySource((consumer) -> consumer.accept(pack));
        }
    }

    private static final List<Field> auraItemFields = new ArrayList<>();

    static {
        try {
            for (Field field : ClientEvents.class.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) || field.getType() != ItemStack.class) continue;
                field.setAccessible(true);
                auraItemFields.add(field);
            }
        } catch (Exception e) {
            System.err.println("Failed to access de.ellpeck.naturesaura.events.ClientEvents classes' private variable (heldEye) via reflection.");
        }
    }

    private static final ResourceLocation FLOWER_BAR_GUI = ResourceLocation.fromNamespaceAndPath(NaturesAuraPlus.MODID, "textures/gui/flower_bar_sprite_sheet.png");

    @Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID,
            bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class duringGameplayClientEvents {

        @SubscribeEvent
        public static void onOverlayRender(RenderGuiOverlayEvent.Post event) {
            if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type()) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;
            HitResult hitResult = mc.hitResult;
            if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) return;
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();
            BlockEntity tile = mc.level.getBlockEntity(blockPos);
            if (!(tile instanceof BlockEntityFlowerGenerator flowerGenerator)) return;
            boolean hasEye = false;
            for (Field field : auraItemFields) {
                try {
                    ItemStack stack = (ItemStack) field.get(null);
                    if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemEye)) continue;
                    hasEye = true;
                    break;
                } catch (Exception ignored) {}
            }
            if (!hasEye) return;
            int yGuiOffset = 0;
            if (mc.player.getMainHandItem().getItem() instanceof ItemModBook && mc.player.getMainHandItem().hasTag() &&
                    (mc.player.getMainHandItem().getTag().getString("patchouli:book").equals("naturesaura:book"))) yGuiOffset = 20;
            GuiGraphics graphics = event.getGuiGraphics();
            Window eventWindow = event.getWindow();
            int vitality = ((FlowerGeneration) flowerGenerator).naturesaura_plus$flowerTileAuraGeneratorReadVitality();
            int centerX = eventWindow.getGuiScaledWidth() / 2;
            int centerY = eventWindow.getGuiScaledHeight() / 2;
            drawContainerInfo(graphics, vitality, 100, 16384063,
                    mc, centerX-40, centerY + yGuiOffset, 15, "Vitality", null);
            NaturesAuraPlusUtils.circularBuffer<Block> flowerBuffer =
                    ((FlowerGeneration) flowerGenerator).naturesaura_plus$flowerTileAuraGeneratorReadBuffer();
            if (flowerBuffer == null) return;
            int flowerCount = flowerBuffer.countObjectAny();
            if (flowerCount == 0) return;
            int itemSpacing = 20;
            int barWidth = flowerCount * itemSpacing;
            int barStartX = centerX - (barWidth / 2);
            int barStartY = centerY + 25 + yGuiOffset;
            int startEndWidth = 2;
            int slotSide = 20;
            int texSize = 64;
            int curX = barStartX + 2;
            for (Object b : flowerBuffer.getBuffer()) {
                if (b == null) continue;
                graphics.renderItem(((Block) b).asItem().getDefaultInstance(), curX, barStartY + 2);
                curX += itemSpacing;
            } //Flowers
            graphics.pose().pushPose();
            graphics.pose().translate(0,0,200); // may or may not need to deal with z-value issues
            graphics.blit(FLOWER_BAR_GUI,barStartX - startEndWidth, barStartY,
                    5, slotSide, 0, 0, 5, slotSide,texSize,texSize); //Start
            graphics.blit(FLOWER_BAR_GUI,barStartX + barWidth - 2, barStartY,
                    4, slotSide, 20, 0, 4, slotSide,texSize,texSize); //End
            for (int i = 0; i < flowerCount; i++) {
                if (flowerBuffer.getHead() == i) {
                    graphics.blit(FLOWER_BAR_GUI,barStartX + i*itemSpacing, barStartY,
                            slotSide, slotSide, 2, 32, slotSide, slotSide,texSize,texSize);
                    continue;
                }
                graphics.blit(FLOWER_BAR_GUI,barStartX + i*itemSpacing, barStartY,
                        slotSide, slotSide, 34, 0, slotSide, slotSide,texSize,texSize);
            } //Mid
            graphics.pose().popPose();
        }
    }

    //Ripped 99% Straight from Elpeck's ClientEvents class
    private static void drawContainerInfo(GuiGraphics graphics, int stored, int max, int color, Minecraft mc,
                                          int x, int y, int yOffset, String name, String textBelow) {
        graphics.setColor((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F, 1);

        y = y + yOffset;
        int width = Mth.ceil(stored / (float) max * 80);

        if (width < 80)
            graphics.blit(ClientEvents.OVERLAYS, x + width, y, width, 0, 80 - width, 6, 256, 256);
        if (width > 0)
            graphics.blit(ClientEvents.OVERLAYS, x, y, 0, 6, width, 6, 256, 256);

        graphics.drawString(mc.font, name, x + 40 - mc.font.width(name) / 2F, y - 9, color, true);

        if (textBelow != null)
            graphics.drawString(mc.font, textBelow, x + 40 - mc.font.width(textBelow) / 2F, y + 7, color, true);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
