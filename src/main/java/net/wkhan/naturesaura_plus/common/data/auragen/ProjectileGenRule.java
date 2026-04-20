package net.wkhan.naturesaura_plus.common.data.auragen;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class ProjectileGenRule {

    @SerializedName("projectile")
    private String projectileId;

    @SerializedName("aura")
    private int auraAmount;

    private transient EntityType<?> cachedProjectileEntity;
    private transient TagKey<EntityType<?>> cachedProjectileEntityTag;
    private transient boolean rulesResolved = false;
    private transient String sourceFile;

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public boolean resolve() {
        if (rulesResolved) return true;

        if (!projectileResolve()) {
            logError("Failed to resolve projectile ID: '" + projectileId + "'");
            return false;
        }

        this.rulesResolved = true;
        return true;
    }

    private boolean projectileResolve(){
        if (projectileId == null || projectileId.isEmpty()) {
            System.err.println("ProjectileAuraGen Rule Error: Missing Projectile ID'" + projectileId + "'");
            return false;
        }
        if (projectileId.startsWith("#")) {
            String tagId = projectileId.substring(1);
            this.cachedProjectileEntityTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(tagId));
            return true;
        }
        ResourceLocation loc = ResourceLocation.tryParse(projectileId);
        if (loc != null && ForgeRegistries.ENTITY_TYPES.containsKey(loc)) {
            this.cachedProjectileEntity = ForgeRegistries.ENTITY_TYPES.getValue(loc);
            return true;
        } else {
            System.err.println("ProjectileAuraGen Rule Error: Invalid Entity ID '" + projectileId + "'");
            return false;
        }
    }


    private void logError(String message) {
        if (sourceFile != null) {
            System.err.println("ProjectileAuraGen Rule Error in " + sourceFile + ": " + message);
        } else {
            System.err.println("ProjectileAuraGen Rule Error: (Invalid SourceFile? <- Seen when sourceFile resolves to null) " + message);
        }
    }


    public EntityType<?> getProjectileEntity() {
        return this.cachedProjectileEntity;
    }
    public TagKey<EntityType<?>> getProjectileEntityTag() {
        return this.cachedProjectileEntityTag;
    }
    public int getAuraAmount() {
        return auraAmount;
    }
}
