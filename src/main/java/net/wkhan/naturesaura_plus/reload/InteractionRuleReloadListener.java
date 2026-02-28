package net.wkhan.naturesaura_plus.reload;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.wkhan.naturesaura_plus.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InteractionRuleReloadListener
        extends SimpleJsonResourceReloadListener {

    public InteractionRuleReloadListener() {
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

        List<String> loadedBlockRules = new ArrayList<>();
        List<String> loadedEntityRules = new ArrayList<>();
        List<String> loadedAnvilCosts = new ArrayList<>();

        data.forEach((fileId, jsonElement) -> {
            try {
                JsonObject json = jsonElement.getAsJsonObject();

                // Check for a "type" discriminator
                if (json.has("type")) {
                    String type = json.get("type").getAsString();

                    if ("entity".equals(type)) {
                        EntityInteractionRule rule = new Gson().fromJson(json, EntityInteractionRule.class);
//                       rule.setSourceFile(fileId.toString()); //pls implement
                        loadedEntityRules.add(fileId.toString());
                        EntityInteractionRules.add(rule);

                    } else if ("block".equals(type)) {
                        // Deserialize to your BlockRule class
                        BlockInteractionRule rule = new Gson().fromJson(json, BlockInteractionRule.class);
                        rule.setSourceFile(fileId.toString());
                        loadedBlockRules.add(fileId.toString());
                        BlockInteractionRules.add(rule);
                    }
                    else if ("anvil_cost".equals(type)) {
                        if (json.has("levels")) {
                            int cost = json.get("levels").getAsInt();
                            AnvilCostRules.add(fileId, cost);
                            loadedAnvilCosts.add(fileId.toString());
                        } else {
                            System.err.println("Missing 'levels' field in anvil cost file: " + fileId);
                        }
                    }
                    else {
                        System.err.println("Unknown rule type '" + type + "' in file: " + fileId);
                    }
                } else {
                    System.err.println("Missing 'type' field in rule file: " + fileId);
                }

            } catch (Exception e) {
                System.err.println("Failed to load interaction rule: " + fileId);
                e.printStackTrace();
            }
        });

        System.out.println("Loaded " + EntityInteractionRules.size() + " entity rules and " + BlockInteractionRules.size() + " block rules and " + AnvilCostRules.size() + " anvil cost rules.");
        System.out.println("Entity Rules Loaded: " + loadedEntityRules);
        System.out.println("Block Rules Loaded: " + loadedBlockRules);
        System.out.println("Anvil Costs Loaded: " + loadedAnvilCosts);
        EntityInteractionRules.sortRules();
        BlockInteractionRules.sortRules();
        System.out.println("Rules loaded and sorted.");
    }
}
