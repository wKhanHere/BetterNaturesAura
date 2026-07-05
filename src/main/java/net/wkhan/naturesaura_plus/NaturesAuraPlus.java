package net.wkhan.naturesaura_plus;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.wkhan.naturesaura_plus.common.block.ModBlocks;
import net.wkhan.naturesaura_plus.network.ModNetwork;
import net.wkhan.naturesaura_plus.compat.botania.BotaniaModItems;
import net.wkhan.naturesaura_plus.common.item.ModItems;
import net.wkhan.naturesaura_plus.data.reload.ReloadListener;
import net.wkhan.naturesaura_plus.data.config.AuraGenConfig;
import net.wkhan.naturesaura_plus.data.config.GameplayConfig;
import net.wkhan.naturesaura_plus.data.config.MiscConfig;

@Mod(NaturesAuraPlus.MODID)
public class NaturesAuraPlus
{
    public static final String MODID = "naturesaura_plus";

    public static boolean isKubeJsLoaded;
    public static boolean isCuriosLoaded;
    public static boolean isBotaniaLoaded;

    public NaturesAuraPlus(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        isKubeJsLoaded = ModList.get().isLoaded("kubejs");
        isCuriosLoaded = ModList.get().isLoaded("curios");
        isBotaniaLoaded = ModList.get().isLoaded("botania");

        ModItems.register(modEventBus);
        if (isBotaniaLoaded) BotaniaModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onReload);
        ModNetwork.init();

        context.registerConfig(ModConfig.Type.COMMON, AuraGenConfig.SPEC, "naturesaura_plus/aura_gen_config.toml");
        context.registerConfig(ModConfig.Type.COMMON, GameplayConfig.SPEC, "naturesaura_plus/gameplay_config.toml");
        context.registerConfig(ModConfig.Type.COMMON, MiscConfig.SPEC, "naturesaura_plus/misc_config.toml");
    }

    private void onReload(AddReloadListenerEvent event) {
        event.addListener(new ReloadListener());
    }


}
