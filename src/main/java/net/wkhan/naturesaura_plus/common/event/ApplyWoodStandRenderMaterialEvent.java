package net.wkhan.naturesaura_plus.common.event;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityWoodStand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.data.duckfaces.AbstractWoodStand;
import net.wkhan.naturesaura_plus.common.tag.ModTags;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID)
public class ApplyWoodStandRenderMaterialEvent {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player))
            return;
        if (!player.isShiftKeyDown())
            return;
        if (!(event.getLevel().getBlockEntity(pos) instanceof BlockEntityWoodStand woodStand))
            return;
        ItemStack stack = event.getItemStack();
        BlockState material;
        if (stack.is(ModTags.Items.VALID_WOODEN_STAND_MATERIAL))
            material = Block.byItem(stack.getItem()).defaultBlockState();
        else if (stack == ItemStack.EMPTY)
            material = Blocks.AIR.defaultBlockState();
        else
            return;
        if (((AbstractWoodStand) woodStand).naturesaura_plus$getWoodStandMaterialBlockState() == material)
            return;
        ((AbstractWoodStand) woodStand).naturesaura_plus$setWoodStandMaterialBlockState(material);
        woodStand.setChanged();
        woodStand.requestModelDataUpdate();
        BlockState state = woodStand.getBlockState();
        event.getLevel().sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
