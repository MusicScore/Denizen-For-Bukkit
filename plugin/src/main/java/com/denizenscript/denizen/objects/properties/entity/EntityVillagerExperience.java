package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class EntityVillagerExperience implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && ((EntityTag) entity).getBukkitEntityType() == EntityType.VILLAGER;
    }

    public static EntityVillagerExperience getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityVillagerExperience((EntityTag) entity);
    }

    public static final String[] handledTags = new String[] {
            "villager_experience"
    };

    public static final String[] handledMechs = new String[] {
            "villager_experience"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityVillagerExperience(EntityTag entity) {
        this.entity = entity;
    }

    private EntityTag entity;

    private int getExperience() {
        return ((Villager) entity.getBukkitEntity()).getVillagerExperience();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return String.valueOf(getExperience());
    }

    @Override
    public String getPropertyId() {
        return "villager_experience";
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
        // @attribute <EntityTag.villager_experience>
        // @returns ElementTag(Number)
        // @mechanism EntityTag.villager_experience
        // @group properties
        // @description
        // Returns the trading experience of the villager entity.
        // -->
        if (attribute.startsWith("villager_experience")) {
            return new ElementTag(getExperience()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name villager_experience
        // @input ElementTag(Number)
        // @description
        // Sets the trading experience of the villager entity.
        // @tags
        // <EntityTag.villager_experience>
        // -->
        if (mechanism.matches("villager_experience") && mechanism.requireInteger()) {
            ((Villager) entity.getBukkitEntity()).setVillagerExperience(mechanism.getValue().asInt());
        }
    }
}
