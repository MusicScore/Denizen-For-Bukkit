package net.aufdemrand.denizen.nms.helpers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.aufdemrand.denizen.nms.NMSHandler;
import net.aufdemrand.denizen.nms.impl.entities.CraftFakePlayer_v1_8_R3;
import net.aufdemrand.denizen.nms.impl.entities.EntityFakeArrow_v1_8_R3;
import net.aufdemrand.denizen.nms.impl.entities.EntityFakePlayer_v1_8_R3;
import net.aufdemrand.denizen.nms.impl.entities.EntityItemProjectile_v1_8_R3;
import net.aufdemrand.denizen.nms.interfaces.CustomEntityHelper;
import net.aufdemrand.denizen.nms.interfaces.FakeArrow;
import net.aufdemrand.denizen.nms.interfaces.FakePlayer;
import net.aufdemrand.denizen.nms.interfaces.ItemProjectile;
import net.aufdemrand.denizen.nms.util.PlayerProfile;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;

public class CustomEntityHelper_v1_8_R3 implements CustomEntityHelper {

    @Override
    public FakeArrow spawnFakeArrow(Location location) {
        CraftWorld world = (CraftWorld) location.getWorld();
        EntityFakeArrow_v1_8_R3 arrow = new EntityFakeArrow_v1_8_R3(world, location);
        return arrow.getBukkitEntity();
    }

    @Override
    public ItemProjectile spawnItemProjectile(Location location, ItemStack itemStack) {
        CraftWorld world = (CraftWorld) location.getWorld();
        EntityItemProjectile_v1_8_R3 entity = new EntityItemProjectile_v1_8_R3(world, location, itemStack);
        return entity.getBukkitEntity();
    }

    @Override
    public FakePlayer spawnFakePlayer(Location location, String name, String skin) throws IllegalArgumentException {
        String fullName = name;
        String prefix = null;
        String suffix = null;
        if (name == null) {
            return null;
        } else if (fullName.length() > 16) {
            prefix = fullName.substring(0, 16);
            if (fullName.length() > 30) {
                int len = 30;
                name = fullName.substring(16, 30);
                if (name.matches(".*[^A-Za-z0-9_].*")) {
                    if (fullName.length() >= 32) {
                        len = 32;
                        name = fullName.substring(16, 32);
                    } else if (fullName.length() == 31) {
                        len = 31;
                        name = fullName.substring(16, 31);
                    }
                } else if (name.length() > 46) {
                    throw new IllegalArgumentException("You must specify a name with no more than 46 characters for FAKE_PLAYER entities!");
                } else {
                    name = ChatColor.RESET + name;
                }
                suffix = fullName.substring(len);
            } else {
                name = fullName.substring(16);
                if (!name.matches(".*[^A-Za-z0-9_].*")) {
                    name = ChatColor.RESET + name;
                }
                if (name.length() > 16) {
                    suffix = name.substring(16);
                    name = name.substring(0, 16);
                }
            }
        }
        if (skin != null && skin.length() > 16) {
            throw new IllegalArgumentException("You must specify a name with no more than 16 characters for FAKE_PLAYER entity skins!");
        }
        CraftWorld world = (CraftWorld) location.getWorld();
        WorldServer worldServer = world.getHandle();
        PlayerProfile playerProfile = new PlayerProfile(name, null);
        if (skin == null && !name.matches(".*[^A-Za-z0-9_].*")) {
            playerProfile = NMSHandler.getInstance().fillPlayerProfile(playerProfile);
        }
        if (skin != null) {
            PlayerProfile skinProfile = new PlayerProfile(skin, null);
            skinProfile = NMSHandler.getInstance().fillPlayerProfile(skinProfile);
            playerProfile.setTexture(skinProfile.getTexture());
            playerProfile.setTextureSignature(skinProfile.getTextureSignature());
        }
        UUID uuid = UUID.randomUUID();
        if (uuid.version() == 4) {
            long msb = uuid.getMostSignificantBits();
            msb &= ~0x0000000000004000L;
            msb |= 0x0000000000002000L;
            uuid = new UUID(msb, uuid.getLeastSignificantBits());
        }
        playerProfile.setUniqueId(uuid);

        GameProfile gameProfile = new GameProfile(playerProfile.getUniqueId(), playerProfile.getName());
        gameProfile.getProperties().put("textures",
                new Property("value", playerProfile.getTexture(), playerProfile.getTextureSignature()));

        final EntityFakePlayer_v1_8_R3 fakePlayer = new EntityFakePlayer_v1_8_R3(worldServer.getMinecraftServer(), worldServer,
                gameProfile, new PlayerInteractManager(worldServer));

        fakePlayer.setPositionRotation(location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
        CraftFakePlayer_v1_8_R3 craftFakePlayer = fakePlayer.getBukkitEntity();
        craftFakePlayer.fullName = fullName;
        if (prefix != null) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            String teamName = "FAKE_PLAYER_TEAM_" + fullName;
            String hash = null;
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] bytes = teamName.getBytes("UTF-8");
                md.update(bytes, 0, bytes.length);
                hash = new BigInteger(1, md.digest()).toString(16).substring(0, 16);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (hash != null) {
                Team team = scoreboard.getTeam(hash);
                if (team == null) {
                    team = scoreboard.registerNewTeam(hash);
                    team.setPrefix(prefix);
                    if (suffix != null) {
                        team.setSuffix(suffix);
                    }
                }
                team.addPlayer(craftFakePlayer);
            }
        }
        return craftFakePlayer;
    }
}
