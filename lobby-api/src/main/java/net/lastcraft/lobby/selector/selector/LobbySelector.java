package net.lastcraft.lobby.selector.selector;

import lombok.Getter;
import net.lastcraft.api.CoreAPI;
import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.type.DInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.api.player.Spigot;
import net.lastcraft.api.util.Head;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.base.gamer.sections.FriendsSection;
import net.lastcraft.base.locale.Language;
import net.lastcraft.core.io.info.InfoFieldNames;
import net.lastcraft.core.io.info.ServerInfo;
import net.lastcraft.lobby.selector.SelectorManager;

import java.util.ArrayList;
import java.util.List;

public class LobbySelector {
    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();
    private static final InventoryAPI API = LastCraft.getInventoryAPI();
    private static final CoreAPI CORE_API = LastCraft.getCoreAPI();
    private static final Spigot SPIGOT = LastCraft.getGamerManager().getSpigot();

    @Getter
    private final DInventory inventory;
    private final Language lang;
    private final BukkitGamer gamer;
    private final SelectorManager manager;

    public LobbySelector(BukkitGamer gamer, Language lang, SelectorManager manager) {
        this.gamer = gamer;
        this.lang = lang;
        this.inventory = API.createInventory(gamer.getPlayer(), lang.getMessage("GUI_LOBBY_SELECTOR"), 3);
        this.manager = manager;
    }

    public void update() {
        if (gamer == null || inventory == null) {
            return;
        }

        FriendsSection section = gamer.getSection(FriendsSection.class);
        inventory.clearInventory();

        if (LastCraft.isLobby()) {
            DItem hubButton = new DItem(ItemUtil.getBuilder(Head.MAINLOBBY.getHead())
                    .setName(lang.getMessage("GAMEMENU_LOBBY_NAME"))
                    .setLore(lang.getList("GAMEMENU_LOBBY_LORE"))
                    .build(),
                    (clicker, clickType, slot) -> CORE_API.sendToHub(clicker));
            inventory.setItem(16, hubButton);
        }

        int slot = 10;
        for (ServerInfo serverInfo : manager.getLobbyServers().values()) {
            DItem dItem;
            String name = serverInfo.value(InfoFieldNames.SERVER_NAME);

            if (name.equalsIgnoreCase(SPIGOT.getName())) {
                dItem = new DItem(ItemUtil.getBuilder(Head.LOBBY_ON.getHead())
                        .setName("§9" + name.toUpperCase())
                        .setLore(lang.getList("GUI_LOBBY_ON"))
                        .build());
            } else if (checkFriend(section, serverInfo)) {
                dItem = new DItem(ItemUtil.getBuilder(Head.LOBBY_FRIEND.getHead())
                        .setName("§e" + name.toUpperCase())
                        .setLore(getFriendsList(section, serverInfo))
                        .build(),
                        (clicker, clickType, slot1) -> CORE_API.sendToServer(clicker, name));
            } else {
                dItem = new DItem(ItemUtil.getBuilder(Head.LOBBY_ANOTHER.getHead())
                        .setName("§e" + name.toUpperCase())
                        .setLore(lang.getList("GUI_ANOTHER_LOBBY", serverInfo.<Integer[]>value("playerIds").length))
                        .build(), (clicker, clickType, slot1) -> CORE_API.sendToServer(clicker, name));
            }

            inventory.setItem(slot, dItem);

            if (slot == 14 && LastCraft.isLobby()) {
                slot = 19;
            } else if (slot == 16 && LastCraft.isHub()) {
                slot = 19;
            } else if (slot == 23 && LastCraft.isLobby()) {
                slot = 28;
            } else if (slot == 25 && LastCraft.isHub()) {
                slot = 28;
            } else if (slot == 34) {
                slot = 10;
            } else {
                ++slot;
            }

        }
    }

    private boolean checkFriend(FriendsSection section, ServerInfo serverData) {
        for (int id : serverData.<Integer[]>value("playerIds")) {
            if (section.getFriends().containsKey(id)) {
                return true;
            }
        }

        return false;
    }

    private List<String> getFriendsList(FriendsSection section, ServerInfo serverData) {
        Integer[] ids = serverData.value("playerIds");

        List<String> lore = new ArrayList<>(lang.getList("GUI_ANOTHER_LOBBY_FRIENDS", ids.length));
        String last = lore.remove(lore.size() - 1);

        List<String> list = new ArrayList<>();

        int count = 0;
        for (int id : ids) {
            if (section.getFriends().containsKey(id)) {
                IBaseGamer gamer = GAMER_MANAGER.getOrCreate(id);
                if (gamer == null) {
                    continue;
                }

                count++;
                list.add("§8• " + gamer.getDisplayName());
                if (count == 5) {
                    list.add(lang.getMessage("GUI_LOBBY_FRIEND_MAX"));
                    break;
                }
            }
        }

        lore.addAll(list);
        lore.add(" ");
        lore.add(last);

        return lore;
    }
}
