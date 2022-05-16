package net.lastcraft.lobby.config;

import lombok.Getter;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.entity.EntityAPI;
import net.lastcraft.api.entity.npc.NPC;
import net.lastcraft.api.entity.npc.types.HumanNPC;
import net.lastcraft.api.types.SubType;
import net.lastcraft.api.util.LocationUtil;
import net.lastcraft.base.skin.Skin;
import net.lastcraft.lobby.Lobby;
import net.lastcraft.lobby.api.game.GameUpdateType;
import net.lastcraft.lobby.game.commands.GameInfoCommand;
import net.lastcraft.lobby.game.data.Channel;
import net.lastcraft.lobby.game.npc.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class GameConfig extends LobbyConfig {

    private static final EntityAPI ENTITY_API = LastCraft.getEntityAPI();

    private final Map<NPC, StartGameNPC> startGameNPCs = new HashMap<>();
    private final Map<String, Channel> channels = new ConcurrentHashMap<>();

    private ShopNPC shopNPC;
    private SpectatorNpc spectatorNpc;

    public GameConfig(Lobby lobby) {
        super(lobby, "game");
    }

    @Override
    public void load() {
        FileConfiguration config = getConfig();

        for (String nameChannel : config.getConfigurationSection("Channels").getKeys(false)) {
            String path = "Channels." + nameChannel + ".";

            GameUpdateType gameUpdateType = GameUpdateType.DEFAULT;

            SubType type = SubType.getByName(config.getString(path + "Type"));
            Channel channel = new Channel(nameChannel.toLowerCase(), type);
            this.channels.put(nameChannel.toLowerCase(), channel);

            String name = config.getString(path + "Name");
            Location location = LocationUtil.stringToLocation(config.getString(path + "Location"), true);
            String value = config.getString(path + "Value");
            String signature = config.getString(path + "Signature");

            if (config.getBoolean(path + "New"))
                gameUpdateType = GameUpdateType.NEW;

            if (config.getBoolean(path + "Update"))
                gameUpdateType = GameUpdateType.UPDATE;

            createStartNPC(name, location, channel, value, signature, gameUpdateType);
        }


        if (config.contains("LocationShop")) {
            Location location = LocationUtil.stringToLocation(config.getString("LocationShop"), true);
            HumanNPC humanNPC = ENTITY_API.createNPC(location, Skin.SKIN_SHOP);
            humanNPC.getEntityEquip().setItemInMainHand(new ItemStack(Material.EMERALD));
            humanNPC.setPublic(true);
            shopNPC = new ShopNPC(humanNPC, location);
        }

        if (config.contains("LocationSpectator")) {
            Location location = LocationUtil.stringToLocation(config.getString("LocationSpectator"), true);
            HumanNPC humanNPC = ENTITY_API.createNPC(location, Skin.SKIN_SPECTATOR);
            humanNPC.getEntityEquip().setItemInMainHand(new ItemStack(Material.COMPASS));
            humanNPC.setPublic(true);
            spectatorNpc = new SpectatorNpc(humanNPC, location);
        }
    }

    @Override
    public void init() {
        if (getAllNpc().isEmpty()) {
            return;
        }

        new NPCListener(lobby, this);
        new GameInfoCommand(this);
    }

    private void createStartNPC(String name, Location location, Channel channel,
                                String value, String signature, GameUpdateType gameUpdateType) {
        HumanNPC humanNPC = ENTITY_API.createNPC(location, value, signature);
        humanNPC.getEntityEquip().setItemInMainHand(channel.getSubType().getItemStack());
        humanNPC.setGlowing(gameUpdateType.getChatColor());
        humanNPC.setPublic(true);

        StartGameNPC startGameNPC = new StartGameNPC(name, location, channel, humanNPC, gameUpdateType);
        startGameNPCs.put(humanNPC, startGameNPC);
    }

    public Map<NPC, LobbyNPC> getAllNpc() {
        Map<NPC, LobbyNPC> npcs = new HashMap<>(startGameNPCs);
        if (shopNPC != null) {
            npcs.put(shopNPC.getHumanNPC(), shopNPC);
        }
        if (spectatorNpc != null) {
            npcs.put(spectatorNpc.getHumanNPC(), spectatorNpc);
        }

        return npcs;
    }
}
