package net.wkhan.naturesaura_plus.data.reload;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.data.auragen.*;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.wkhan.naturesaura_plus.data.auragen.AuraGenRules.addAuraGenerations;
import static net.wkhan.naturesaura_plus.data.config.MiscConfig.SHOW_AURA_GEN_RULES_IN_LOG;
import static org.apache.logging.log4j.LogManager.getLogger;

public class ReloadListener extends SimpleJsonResourceReloadListener {
    public ReloadListener() {
        super(new Gson(), "interactions");
    }

    private static final Logger LOGGER = getLogger();
    protected static final List<String> loadedAuraRules = new ArrayList<>();

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> data,
            @NotNull ResourceManager manager,
            @NotNull ProfilerFiller profiler
    ) {
        clearData();
        data.forEach((fileId, jsonElement) -> {
            try {
                JsonObject json = jsonElement.getAsJsonObject();
                if (!json.has("type")) {
                    LOGGER.error("Missing 'type' field in rule file: {}", fileId);
                    return;
                }
                String type = json.get("type").getAsString();
                switch (type) {
                    case "aura_gen:projectile_gen" -> {
                        DataResult<ProjectileGenRule> result = ProjectileGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(
                                errorMessage -> LOGGER.error("[NaturesAuraPlus] ProjectileGen JSON Error: {}", errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.projectileRulesQueue.add(rule);
                                });
                    } 
                    case "aura_gen:moss_gen" -> {
                        DataResult<MossGenRule> result = MossGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(
                                errorMessage -> LOGGER.error("[NaturesAuraPlus] MossGen JSON Error: {}", errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.mossRulesQueue.add(rule);
                                });
                    } 
                    case "aura_gen:flower_gen" -> {
                        DataResult<FlowerGenRule> result = FlowerGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(
                                errorMessage -> LOGGER.error("[NaturesAuraPlus] FlowerGen JSON Error: {}", errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.flowerRulesQueue.add(rule);
                                });
                    } 
                    case "aura_gen:slime_gen" -> { 
                        DataResult<SlimeGenRule> result = SlimeGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(
                                errorMessage -> LOGGER.error("[NaturesAuraPlus] SlimeGen JSON Error: {}", errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.slimeRulesQueue.add(rule);
                                });
                    }
                    case "aura_gen:animal_gen" -> { 
                        DataResult<AnimalGenRule> result = AnimalGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(
                                errorMessage -> LOGGER.error("[NaturesAuraPlus] AnimalGen JSON Error: {}", errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.animalRulesQueue.add(rule);
                                });
                    }
                    case "aura_gen:chorus_gen" -> { 
                        DataResult<ChorusGenRule> result = ChorusGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(
                                errorMessage -> LOGGER.error("[NaturesAuraPlus] ChorusGen JSON Error: {}", errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.chorusRulesQueue.add(rule);
                                });
                    }
                    case "aura_gen:oak_gen" -> { 
                        DataResult<OakGenRule> result = OakGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(
                                errorMessage -> LOGGER.error("[NaturesAuraPlus] OakGen JSON Error: {}", errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.oakRulesQueue.add(rule);
                                });
                    }
                    case "aura_gen:potion_gen" -> {
                        DataResult<PotionGenRule> result = PotionGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(
                                errorMessage -> LOGGER.error("[NaturesAuraPlus] PotionGen JSON Error: {}", errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.potionRulesQueue.add(rule);
                                });
                    }
                    case "aura_gen:firework_gen" -> {
                        DataResult<FireworkGenRule> result = FireworkGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(
                                errorMessage -> LOGGER.error("[NaturesAuraPlus] FireworkGen JSON Error: {}", errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.addFireworkGeneration(rule);
                                });
                    }

                    default -> LOGGER.error("Unknown rule type '{}' in file: {}", type, fileId);
                }
            }
            catch (Exception e) { // I know this is sucky.
                LOGGER.error("Failed to load interaction rule: {}", fileId);
            }
        });
    }

    private void clearData() {
        LogCleaner.init();
        loadedAuraRules.clear();
        AuraGenRules.auraGenerationClear();
    }

    @Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class onServerSetupEvents {

        @SubscribeEvent
        public static void onTagsUpdated(TagsUpdatedEvent event) {
            if (event.getPhase() != EventPriority.LOW)
                return;
            addAuraGenerations();
            System.out.println("Number of aura gen rules loaded: " + AuraGenRules.auraRulesCount());
            if (SHOW_AURA_GEN_RULES_IN_LOG.get())
                System.out.println("Aura generation rules loaded: " + loadedAuraRules);
        }
    }
}
