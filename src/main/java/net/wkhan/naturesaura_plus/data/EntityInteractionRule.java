package net.wkhan.naturesaura_plus.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityInteractionRule implements Comparable<EntityInteractionRule> {

    // --- JSON DATA FIELDS ---
    // These match the keys in your JSON file.

    @SerializedName("item") // Allows JSON key "item" to map to this field
    private String itemId;

    @SerializedName("entity") // Allows JSON key "entity" to map to this field
    private String entityId;

    @SerializedName("priority")
    private int priority = 0; // Default to 0 if missing


    // --- RUNTIME CACHE ---
    // We use 'transient' so GSON ignores these. We calculate them once to save performance.
    private transient Item cachedItem;
    private transient EntityType<?> cachedEntity;
    private transient boolean rulesResolved = false;



    // --- LOGIC ---

    /**
     * Call this ONCE after loading to validate the rule and cache the registry objects.
     * Returns false if the rule is invalid (e.g. typo in entity ID).
     */
    public boolean resolve() {
        if (rulesResolved) return true;

        // 1. Resolve Item (if specified)
        if (itemId != null && !itemId.isEmpty()) {
            ResourceLocation loc = ResourceLocation.tryParse(itemId);
            if (loc != null && ForgeRegistries.ITEMS.containsKey(loc)) {
                this.cachedItem = ForgeRegistries.ITEMS.getValue(loc);
            } else {
                System.err.println("Rule Error: Invalid Item ID '" + itemId + "'");
                return false; // Invalid rule
            }
        }

        // 2. Resolve Entity (if specified)
        if (entityId != null && !entityId.isEmpty()) {
            ResourceLocation loc = ResourceLocation.tryParse(entityId);
            if (loc != null && ForgeRegistries.ENTITY_TYPES.containsKey(loc)) {
                this.cachedEntity = ForgeRegistries.ENTITY_TYPES.getValue(loc);
            } else {
                System.err.println("Rule Error: Invalid Entity ID '" + entityId + "'");
                return false; // Invalid rule
            }
        }

        this.rulesResolved = true;
        return true;
    }

    public boolean matches(ItemStack stack, Entity entity) {
        // Ensure we resolved the strings to actual game objects
        if (!rulesResolved) resolve();

        // 1. Check Entity Type (If rule has a specific target)
        if (cachedEntity != null) {
            if (entity.getType() != cachedEntity) {
                return false; // Wrong entity
            }
        }

        // 2. Check Item (If rule requires a specific item)
        if (cachedItem != null) {
            return stack.is(cachedItem); // Wrong item
        }

        // If we passed all checks (or if fields were null/wildcards), it's a match!
        return true;
    }

    // Getters for your event handler to use
//    public int getDamage() { return damage; }

    // Helper to let your Manager know where to sort this rule
    public EntityType<?> getTargetEntityType() { return cachedEntity; }
    public String getItemId() { return itemId; }
    public String getTargetEntityId() { return entityId; }
    public int getPriority() { return priority;}

    @Override
    public int compareTo(EntityInteractionRule other) {
        // This sorts by priority (Higher numbers come FIRST)
        return Integer.compare(other.priority, this.priority);
    }
}
