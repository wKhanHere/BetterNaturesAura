package net.wkhan.naturesaura_plus.common.reload;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.wkhan.naturesaura_plus.common.data.*;
import net.wkhan.naturesaura_plus.common.data.auragen.*;
import net.wkhan.naturesaura_plus.common.data.block.BlockInteractionRule;
import net.wkhan.naturesaura_plus.common.data.block.BlockInteractionRules;
import net.wkhan.naturesaura_plus.common.data.entity.EntityInteractionRule;
import net.wkhan.naturesaura_plus.common.data.entity.EntityInteractionRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReloadListener
        extends SimpleJsonResourceReloadListener {

    public ReloadListener() {
        super(new Gson(), "interactions");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> data,
            ResourceManager manager,
            ProfilerFiller profiler
    ) {
        EntityInteractionRules.clear();
        BlockInteractionRules.clear();
        AnvilCostRules.clear();
        AuraGenRules.auraGenerationClear();

        List<String> loadedBlockRules = new ArrayList<>();
        List<String> loadedEntityRules = new ArrayList<>();
        List<String> loadedAnvilCosts = new ArrayList<>();
        List<String> loadedAuraRules = new ArrayList<>();

        data.forEach((fileId, jsonElement) -> {
            try {
                JsonObject json = jsonElement.getAsJsonObject();
                if (!json.has("type")) {
                    System.err.println("Missing 'type' field in rule file: " + fileId);
                    return;
                }
                String type = json.get("type").getAsString();
                switch (type) {
                    case "prevent_interact:entity" -> {
                        EntityInteractionRule rule = new Gson().fromJson(json, EntityInteractionRule.class);
                        rule.setSourceFile(fileId.toString());
                        loadedEntityRules.add(fileId.toString());
                        EntityInteractionRules.add(rule);
                    }
                    case "broken_prevent_interact:block" -> {
                        BlockInteractionRule rule = new Gson().fromJson(json, BlockInteractionRule.class);
                        rule.setSourceFile(fileId.toString());
                        loadedBlockRules.add(fileId.toString());
                        BlockInteractionRules.add(rule);
                    }
                    case "anvil_cost:apply_steel_token" -> {
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
                                    AuraGenRules.addProjectileGeneration(rule);
                                });
                    } //refactored
                    case "aura_gen:moss_gen" -> {
                        DataResult<MossGenRule> result = MossGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(errorMessage -> System.err.println("MossGen JSON Error: " + errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.addMossGeneration(rule);
                                });
                    } //refactored
                    case "aura_gen:flower_gen" -> {
                        DataResult<FlowerGenRule> result = FlowerGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(errorMessage -> System.err.println("FlowerGen JSON Error: " + errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.addFlowerGeneration(rule);
                                });
                    } //refactored
                    case "aura_gen:slime_gen" -> { //refactored
                        DataResult<SlimeGenRule> result = SlimeGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(errorMessage -> System.err.println("SlimeGen JSON Error: " + errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.addSlimeGeneration(rule);
                                });
                    }
                    case "aura_gen:animal_gen" -> { //refactored
                        DataResult<AnimalGenRule> result = AnimalGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(errorMessage -> System.err.println("AnimalGen JSON Error: " + errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.addAnimalGeneration(rule);
                                });
                    }
                    case "aura_gen:chorus_gen" -> { //refactored
                        DataResult<ChorusGenRule> result = ChorusGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(errorMessage -> System.err.println("ChorusGen JSON Error: " + errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.addChorusGeneration(rule);
                                });
                    }
                    case "aura_gen:oak_gen" -> { //refactored
                        DataResult<OakGenRule> result = OakGenRule.CODEC.parse(JsonOps.INSTANCE, json)
                                .mapError(originalError -> "Error in file '" + fileId + "': " + originalError);
                        result.resultOrPartial(errorMessage -> System.err.println("OakGen JSON Error: " + errorMessage))
                                .ifPresent(rule -> {
                                    loadedAuraRules.add(fileId.toString());
                                    AuraGenRules.addOakGeneration(rule);
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

        System.out.println("Loaded " + EntityInteractionRules.size() + " entity rules, "
                + BlockInteractionRules.size() + " block rules, "
                + AnvilCostRules.size() + " anvil cost rules and \n"
                + AuraGenRules.auraRulesCount() + " aura gen rules."); //add aura gen rules count
        System.out.println("Entity Rules Loaded: " + loadedEntityRules);
        System.out.println("Block Rules Loaded: " + loadedBlockRules);
        System.out.println("Anvil Costs Loaded: " + loadedAnvilCosts);
        System.out.println("Aura generation rules loaded: " + loadedAuraRules);
    }
}
