package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class EntityVillagerType implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && ((EntityTag) entity).getBukkitEntityType() == EntityType.VILLAGER;
    }

    public static EntityVillagerType getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityVillagerType((EntityTag) entity);
    }

    public static final String[] handledTags = new String[] {
            "villager_type"
    };

    public static final String[] handledMechs = new String[] {
            "villager_type"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityVillagerType(EntityTag entity) {
        this.entity = entity;
    }

    private EntityTag entity;

    private String getVillagerType() {
        return ((Villager) entity.getBukkitEntity()).getVillagerType().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getVillagerType();
    }

    @Override
    public String getPropertyId() {
        return "villager_type";
    }

    ///////////
    // ObjectTag Attributes
    ////////

    @Override
    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <EntityTag.villager_type>
        // @returns ElementTag
        // @mechanism EntityTag.villager_type
        // @group properties
        // @description
        // Returns the villager type of the villager entity.
        // Can be any of: <@link url https://hub.spigotmc.org/javadocs/spigot/index.html?org/bukkit/Material.html>
        // -->
        if (attribute.startsWith("villager_type")) {
            return new ElementTag(getVillagerType()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name villager_type
        // @input ElementTag
        // @description
        // Sets the villager type of the villager entity.
        // Can be any of: <@link url https://hub.spigotmc.org/javadocs/spigot/index.html?org/bukkit/Material.html>
        // @tags
        // <EntityTag.villager_type>
        // -->
        if (mechanism.matches("villager_type") && mechanism.requireEnum(false, Villager.Type.values())) {
            ((Villager) entity.getBukkitEntity()).setVillagerType(Villager.Type.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}
