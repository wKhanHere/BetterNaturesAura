package net.wkhan.naturesaura_plus.common.data.entity;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class EntityInteractionRule {

    @SerializedName("item")
    private String itemId;

    @SerializedName("entity")
    private String entityId;

    private transient Item cachedItem;
    private transient TagKey<Item> cachedItemTag;
    private transient EntityType<?> cachedEntity;
    private transient TagKey<EntityType<?>> cachedEntityTag;
    private transient boolean rulesResolved = false;
    private transient String sourceFile;

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public boolean resolve() {
        if (rulesResolved) return true;

        if (!itemResolve()) {
            logError("Failed to resolve Item ID: '" + itemId + "'");
            return false;
        }

        if (!entityResolve()) {
            logError("Failed to resolve Block ID: '" + entityId + "'");
            return false;
        }

        this.rulesResolved = true;
        return true;
    }

    private boolean itemResolve(){
        if (itemId == null || itemId.isEmpty()) {
            System.err.println("Block Rule Error: Missing Item ID'" + itemId + "'");
            return false;
        }
        if (itemId.equals("*")) {
            this.cachedItem = null;
            return true;
        }
        if (itemId.startsWith("#")) {
            String tagId = itemId.substring(1);
            this.cachedItemTag = TagKey.create(Registries.ITEM, new ResourceLocation(tagId));
            return true;
        }
        ResourceLocation loc = ResourceLocation.tryParse(itemId);
        if (loc != null && ForgeRegistries.ITEMS.containsKey(loc)) {
            this.cachedItem = ForgeRegistries.ITEMS.getValue(loc);
            return true;
        } else {
            System.err.println("Block Rule Error: Invalid Item ID '" + itemId + "'");
            return false;
        }
    }

    private boolean entityResolve() {
        if (entityId == null || entityId.isEmpty()) {
            System.err.println("Entity Rule Error: Missing Entity ID'" + entityId + "'");
            return false;
        }
        if (entityId.equals("*")) {
            this.cachedEntity = null;
            return true;
        }
        if (entityId.startsWith("#")) {
            String tagId = entityId.substring(1);
            this.cachedEntityTag = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(tagId));
            return true;
        }
        ResourceLocation loc = ResourceLocation.tryParse(entityId);
        if (loc != null && ForgeRegistries.ENTITY_TYPES.containsKey(loc)) {
            this.cachedEntity = ForgeRegistries.ENTITY_TYPES.getValue(loc);
            return true;
        } else {
            System.err.println("Entity Rule Error: Invalid Entity ID '" + entityId + "'");
            return false;
        }
    }

    public boolean matches(ItemStack stack, EntityType<?> entityType) {
        if (!rulesResolved) resolve();

        if (cachedEntity != null) {
            if (entityType == null) return true;
            if (entityType != cachedEntity) return false;
        }
        if (cachedEntityTag != null) {
            if (!entityType.is(cachedEntityTag)) return false;
        }
        if (cachedItem != null) {
            if (stack == null) return true;
            if(!stack.is(cachedItem)) return false;
        }
        if (cachedItemTag != null) {
            if (!Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(cachedItemTag).contains(stack.getItem())) return false;
        }

        return true;
    }

    private void logError(String message) {
        if (sourceFile != null) {
            System.err.println("Block Rule Error in " + sourceFile + ": " + message);
        } else {
            System.err.println("Block Rule Error: (Invalid SourceFile? <- Seen when sourceFile resolves to null) " + message);
        }
    }

    public Item getTargetItem() {
        return cachedItem;
    }
    public EntityType<?> getTargetEntity() {
        return cachedEntity;
    }
    public String getRawEntityId() {
        return entityId;
    }
    public String getRawItemId() {
        return itemId;
    }
    public TagKey<EntityType<?>> getTargetEntityTag() {
        return cachedEntityTag;
    }
    public TagKey<Item> getTargetItemTag() {
        return cachedItemTag;
    }
}
