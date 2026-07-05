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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.data.AnvilCostRules;
import net.wkhan.naturesaura_plus.data.auragen.*;
import net.wkhan.naturesaura_plus.data.block.BlockInteractionRule;
import net.wkhan.naturesaura_plus.data.block.BlockInteractionRules;
import net.wkhan.naturesaura_plus.data.entity.EntityInteractionRule;
import net.wkhan.naturesaura_plus.data.entity.EntityInteractionRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.wkhan.naturesaura_plus.data.auragen.AuraGenRules.addAuraGenerations;

public class ReloadListener
        extends SimpleJsonResourceReloadListener {

    public ReloadListener() {
        super(new Gson(), "interactions");
    }
    protected static final List<String> loadedAuraRules = new ArrayList<>();
    protected final List<String> loadedBlockRules = new ArrayList<>();
    protected final List<String> loadedEntityRules = new ArrayList<>();
    protected final List<String> loadedAnvilCosts = new ArrayList<>();

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> data,
            ResourceManager manager,
            ProfilerFiller profiler
    ) {
        clearData();
        data.forEach((fileId, jsonElement) -> {
            try {
                JsonObject json = jsonElement.getAsJsonObject();
                if (!json.has("type")) {
                    System.err.println("Missing 'type' field in rule file: " + fileId);
                    return;
                }
                String type = json.get("type").getAsString();
                switch (type) {
                    case "prevent_interact:entity" -> { //refactor
                        EntityInteractionRule rule = new Gson().fromJson(json, EntityInteractionRule.class);
                        rule.setSourceFile(fileId.toString());
                        loadedEntityRules.add(fileId.toString());
                        EntityInteractionRules.add(rule);
                    }
                    case "broken_prevent_interact:block" -> { //refactor
                        BlockInteractionRule rule = new Gson().fromJson(json, BlockInteractionRule.class);
                        rule.setSourceFile(fileId.toString());
                        loadedBlockRules.add(fileId.toString());
                        BlockInteractionRules.add(rule);
                    }
                    case "anvil_cost:apply_steel_token" -> { //refactor
                        if (!json.has("levels")) {
                            System.err.println("Missing 'levels' field in anvil cost file: " + fileId);
                            return;
                        }
                        loadedAnvilCosts.add(fileId.toString());
                        int cost = json.get("levels").getAsInt();
                        AnvilCostRules.add(fileId, cost);
                    }
                    case "aura_gen:projectile_gen" -> {
                        DataResult<ProjectileGenRule> result = ProjectileGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(errorMessage -> System.err.println("ProjectileGen JSON Error: " + errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.projectileRulesQueue.add(rule);
                                });
                    } 
                    case "aura_gen:moss_gen" -> {
                        DataResult<MossGenRule> result = MossGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(errorMessage -> System.err.println("MossGen JSON Error: " + errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.mossRulesQueue.add(rule);
                                });
                    } 
                    case "aura_gen:flower_gen" -> {
                        DataResult<FlowerGenRule> result = FlowerGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(errorMessage -> System.err.println("FlowerGen JSON Error: " + errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.flowerRulesQueue.add(rule);
                                });
                    } 
                    case "aura_gen:slime_gen" -> { 
                        DataResult<SlimeGenRule> result = SlimeGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(errorMessage -> System.err.println("SlimeGen JSON Error: " + errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.slimeRulesQueue.add(rule);
                                });
                    }
                    case "aura_gen:animal_gen" -> { 
                        DataResult<AnimalGenRule> result = AnimalGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(errorMessage -> System.err.println("AnimalGen JSON Error: " + errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.animalRulesQueue.add(rule);
                                });
                    }
                    case "aura_gen:chorus_gen" -> { 
                        DataResult<ChorusGenRule> result = ChorusGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(errorMessage -> System.err.println("ChorusGen JSON Error: " + errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.chorusRulesQueue.add(rule);
                                });
                    }
                    case "aura_gen:oak_gen" -> { 
                        DataResult<OakGenRule> result = OakGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(errorMessage -> System.err.println("OakGen JSON Error: " + errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.oakRulesQueue.add(rule);
                                });
                    }

                    default -> System.err.println("Unknown rule type '" + type + "' in file: " + fileId);
                }
            }
                catch (Exception e) { // I know this is sucky.
                System.err.println("Failed to load interaction rule: " + fileId);
                e.printStackTrace();
            }
        });

        System.out.println("Entity Rules Loaded: " + loadedEntityRules);
        System.out.println("Block Rules Loaded: " + loadedBlockRules);
        System.out.println("Anvil Costs Loaded: " + loadedAnvilCosts);
    }

    private void clearData() {
        loadedBlockRules.clear();
        loadedEntityRules.clear();
        loadedAnvilCosts.clear();
        loadedAuraRules.clear();
        EntityInteractionRules.clear();
        BlockInteractionRules.clear();
        AnvilCostRules.clear();
        AuraGenRules.auraGenerationClear();
    }

    @Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class onServerSetupEvents {

        @SubscribeEvent
        public static void onTagsUpdated(TagsUpdatedEvent event) {
            addAuraGenerations();
            System.out.println("Loaded " + EntityInteractionRules.size() + " entity rules, "
                    + BlockInteractionRules.size() + " block rules, "
                    + AnvilCostRules.size() + " anvil cost rules and \n"
                    + AuraGenRules.auraRulesCount() + " aura gen rules.");
            System.out.println("Aura generation rules loaded: " + loadedAuraRules);
        }
    }
}
