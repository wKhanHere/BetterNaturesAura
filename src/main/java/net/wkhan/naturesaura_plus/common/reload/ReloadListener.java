package net.wkhan.naturesaura_plus.common.reload;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.wkhan.naturesaura_plus.common.data.*;
import net.wkhan.naturesaura_plus.common.data.auragen.AuraGenRules;
import net.wkhan.naturesaura_plus.common.data.auragen.MossGenRule;
import net.wkhan.naturesaura_plus.common.data.auragen.ProjectileGenRule;
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
        AuraGenRules.projectileGenerationClear();
        AuraGenRules.mossGenerationClear();

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

                if ("brokenPreventInteract:entity".equals(type)) {
                    EntityInteractionRule rule = new Gson().fromJson(json, EntityInteractionRule.class);
                    rule.setSourceFile(fileId.toString());
                    loadedEntityRules.add(fileId.toString());
                    EntityInteractionRules.add(rule);
                    return;
                }
                if ("brokenPreventInteract:block".equals(type)) {
                    BlockInteractionRule rule = new Gson().fromJson(json, BlockInteractionRule.class);
                    rule.setSourceFile(fileId.toString());
                    loadedBlockRules.add(fileId.toString());
                    BlockInteractionRules.add(rule);
                    return;
                }
                if ("anvilCost:applySteelToken".equals(type)) {
                    if(!json.has("levels")) {
                        System.err.println("Missing 'levels' field in anvil cost file: " + fileId);
                        return;
                    }
                    if (json.has("levels")) {
                        loadedAnvilCosts.add(fileId.toString());
                        int cost = json.get("levels").getAsInt();
                        AnvilCostRules.add(fileId, cost);
                        return;
                    }
                }
                if ("auraGen:projectileGen".equals(type)) {
                    ProjectileGenRule rule = new Gson().fromJson(json, ProjectileGenRule.class);
                    rule.setSourceFile(fileId.toString());
                    loadedAuraRules.add(fileId.toString());
                    AuraGenRules.addProjectileGeneration(rule);
                    return;
                }
                if ("auraGen:mossGen".equals(type)) {
                    MossGenRule rule = new Gson().fromJson(json, MossGenRule.class);
                    rule.setSourceFile(fileId.toString());
                    loadedAuraRules.add(fileId.toString());
                    AuraGenRules.addMossGeneration(rule);
                    return;
                }
                System.err.println("Unknown rule type '" + type + "' in file: " + fileId);
            }
            catch (Exception e) { // I know this is sucky.
                System.err.println("Failed to load interaction rule: " + fileId);
                e.printStackTrace();
            }
        });

        System.out.println("Loaded " + EntityInteractionRules.size() + " entity rules and " + BlockInteractionRules.size() + " block rules and " + AnvilCostRules.size() + " anvil cost rules.");
        System.out.println("Entity Rules Loaded: " + loadedEntityRules);
        System.out.println("Block Rules Loaded: " + loadedBlockRules);
        System.out.println("Anvil Costs Loaded: " + loadedAnvilCosts);
        System.out.println("Aura generation rules loaded: " + loadedAuraRules);
    }
}
