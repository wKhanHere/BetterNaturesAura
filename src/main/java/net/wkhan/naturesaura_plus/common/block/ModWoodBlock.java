package net.wkhan.naturesaura_plus.common.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

public class ModWoodBlock extends RotatedPillarBlock {
    public ModWoodBlock(Properties p_55926_) {
        super(p_55926_);
    }
//    Ancient wood in Nature's Aura isn't flammable.
//    I'm almost certain that's an oversight, but I will keep consistency and let the stripped variants be unflammable.
//    @Override
//    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
//        return true;
//    }
//    @Override
//    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
//        return 5;
//    }
//    @Override
//    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
//        return 5;
//    }

    @Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID)
    public static class ModWoodBlockEventListener {
        @SubscribeEvent
        public static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
            if (event.getToolAction() != ToolActions.AXE_STRIP) return;
            Block ancientLog = ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath("naturesaura","ancient_log"));
            Block ancientBark = ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath("naturesaura","ancient_bark"));
            if (ancientLog == null || ancientBark == null) return;
            BlockState state = event.getState();
            if (state.is(ancientLog))
                event.setFinalState(ModBlocks.STRIPPED_ANCIENT_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS)));
            else if (state.is(ancientBark))
                event.setFinalState(ModBlocks.STRIPPED_ANCIENT_BARK.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS)));
        }
    }
}
