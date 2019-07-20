package com.denizenscript.denizen.events.player;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerStandsOnScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player stands on material
    // player stands on (<material>)
    //
    // @Regex ^on player stands on [^\s]+$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when a player stands on a pressure plate, tripwire, or redstone ore.
    // @Context
    // <context.location> returns the LocationTag the player is interacting with.
    // <context.material> returns the MaterialTag the player is interacting with.
    //
    // @Determine
    //
    // -->

    public PlayerStandsOnScriptEvent() {
        instance = this;
    }

    PlayerStandsOnScriptEvent instance;
    PlayerInteractEvent event;
    LocationTag location;
    MaterialTag material;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        return lower.startsWith("player stands on");
    }

    @Override
    public boolean matches(ScriptPath path) {

        String mat = path.eventArgLowerAt(3);
        if (mat.length() > 0
                && !mat.equals("in")
                && !tryMaterial(material, mat)) {
            return false;
        }

        if (!runInCheck(path, event.getPlayer().getLocation())) {
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "PlayerStandsOn";
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(EntityTag.getPlayerFrom(event.getPlayer()), null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("location")) {
            return location;
        }
        else if (name.equals("material")) {
            return material;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void playerStandsOn(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }
        material = new MaterialTag(event.getClickedBlock());
        location = new LocationTag(event.getClickedBlock().getLocation());
        this.event = event;
        fire(event);
    }
}