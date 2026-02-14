package net.wkhan.naturesaura_plus.reload;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.wkhan.naturesaura_plus.data.BlockInteractionRule;
import net.wkhan.naturesaura_plus.data.BlockInteractionRules;
import net.wkhan.naturesaura_plus.data.EntityInteractionRule;
import net.wkhan.naturesaura_plus.data.EntityInteractionRules;

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

        data.forEach((fileId, jsonElement) -> {
            try {
                JsonObject json = jsonElement.getAsJsonObject();

                // 3. Check for a "type" discriminator
                // This assumes your JSON has a field like: "type": "entity" or "type": "block"
                if (json.has("type")) {
                    String type = json.get("type").getAsString();

                    if ("entity".equals(type)) {
                        // Deserialize to your EntityRule class
                        EntityInteractionRule rule = new Gson().fromJson(json, EntityInteractionRule.class);
                        EntityInteractionRules.add(rule);

                    } else if ("block".equals(type)) {
                        // Deserialize to your BlockRule class
                        BlockInteractionRule rule = new Gson().fromJson(json, BlockInteractionRule.class);
                        BlockInteractionRules.add(rule);
                    }
                } else {
                    // Fallback: If no type is specified, you might try to guess based on fields
                    // or log a warning.
                    System.err.println("Missing 'type' field in rule file: " + fileId);
                }

            } catch (Exception e) {
                // 4. Robust Error Logging
                // This ensures one bad JSON file doesn't crash the whole game or stop other rules from loading
                System.err.println("Failed to load interaction rule: " + fileId);
                e.printStackTrace();
            }
        });

        System.out.println("Loaded " + EntityInteractionRules.size() + " entity rules and " + BlockInteractionRules.size() + " block rules.");
        EntityInteractionRules.sortRules();
        BlockInteractionRules.sortRules();
        System.out.println("Rules loaded and sorted.");
    }
}
