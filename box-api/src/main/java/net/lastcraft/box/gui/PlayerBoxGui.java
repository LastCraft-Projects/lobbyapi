package net.lastcraft.box.gui;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.type.DInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.api.types.GameType;
import net.lastcraft.api.types.SubType;
import net.lastcraft.api.util.Head;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.api.util.Rarity;
import net.lastcraft.base.SoundType;
import net.lastcraft.base.gamer.constans.KeyType;
import net.lastcraft.base.gamer.sections.NetworkingSection;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.box.api.BoxAPI;
import net.lastcraft.box.api.ItemBox;
import net.lastcraft.box.api.ItemBoxManager;
import net.lastcraft.box.data.Box;
import net.lastcraft.connector.bukkit.BukkitConnector;
import net.lastcraft.core.io.packet.bukkit.BukkitBalancePacket;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerBoxGui {

    private static final ItemBoxManager ITEM_BOX_MANAGER = BoxAPI.getItemBoxManager();
    private static final Random RANDOM = new Random();
    private static final SoundAPI SOUND_API = LastCraft.getSoundAPI();
    private static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();

    private final DInventory inventory;
    private final BukkitGamer gamer;
    private final Box box;

    public PlayerBoxGui(BukkitGamer gamer, Box box) {
        this.gamer = gamer;
        this.box = box;

        Language language = gamer.getLanguage();

        inventory = INVENTORY_API.createInventory(gamer.getPlayer(), language.getMessage("BOX_GUI_NAME"), 6);
        for (int slot : BoxAPI.EMPTY_SLOTS) {
            inventory.setItem(slot, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setName(" ")
                    .setDurability((short) 7)
                    .build()));
        }

        inventory.setItem(48, new DItem(ItemUtil.getBuilder(Material.BOOK)
                .setName(language.getMessage("BOX_GUI_HELP_NAME"))
                .setLore(language.getList("BOX_GUI_HELP_LORE"))
                .build()));

        inventory.setItem(50, new DItem(ItemUtil.getBuilder(Material.PAPER)
                .setName(language.getMessage("BOX_GUI_HELP2_NAME"))
                .setLore(language.getList("BOX_GUI_HELP2_LORE"))
                .build()));
    }

    public void open() {
        if (box.isWork()) {
            gamer.getPlayer().closeInventory();
            return;
        }
        box.getPlayersOpenGui().add(gamer.getName()); //чтобы потом если что закрыть ему гуи

        inventory.openInventory(gamer.getPlayer());

        Language language = gamer.getLanguage();
        for (KeyType keyType : KeyType.values()) {
            int amount = gamer.getKeys(keyType);
            boolean enable = amount > 0;
            ItemStack item = enable ? Head.getHeadByValue(keyType.getHeadValue()) : ItemUtil.getBuilder(Material.STAINED_GLASS)
                    .setDurability((short) 14)
                    .build();
            inventory.setItem(keyType.getSlotGui(), new DItem(ItemUtil.getBuilder(item)
                    .setName(keyType.getName(language))
                    .setLore(keyType.getLore(language))
                    .addLore(language.getList("BOX_GUI_LORE", StringUtil.getNumberFormat(amount)))
                    .setAmount(enable ? (amount > 64 ? 64 : amount) : 1)
                    .build(), (player, clickType, i) -> {
                box.getPlayersOpenGui().remove(gamer.getName());

                if (clickType.isLeftClick()) {
                    PlayerBoxGui.this.onClick(keyType);
                    return;
                }

                BoughtKeyType boughtKeyType = BoughtKeyType.getBoughtType(keyType);
                if (boughtKeyType == null) {
                    gamer.sendMessageLocale("BOX_KEY_NOT_BUY");
                    player.closeInventory();
                    return;
                }

                ShopBoxGui shopBoxGui = new ShopBoxGui(this, gamer, boughtKeyType);
                shopBoxGui.open();
            }));
        }
    }

    private void onClick(KeyType keyType) {
        if (!isEnableOpen(keyType)) {
            gamer.sendMessageLocale("BOX_NO_OPEN");
            gamer.getPlayer().closeInventory();
            return;
        }

        NetworkingSection section = gamer.getSection(NetworkingSection.class);
        if (section == null) {
            gamer.sendMessage("§cОшибка, сообщите админам в vk.com/lastcraft");
            return;
        }

        if (gamer.getKeys(keyType) < 1 || ITEM_BOX_MANAGER.getItems(keyType).isEmpty()) {
            SOUND_API.play(gamer.getPlayer(), SoundType.NOTE_BASS);
            gamer.getPlayer().closeInventory();
            return;
        }

        Language lang = gamer.getLanguage();

        int randomNumber = section.getRandom().getOrDefault(keyType, 0);

        List<ItemBox> items = new ArrayList<>();
        for (int i = 1; i <= BoxAPI.getAmountItems(); i++) {
            items.add(getRandomItemByRarity(keyType, getRandomRarity(randomNumber)));
        }

        gamer.sendMessage(box.getPrefix(lang) + lang.getMessage("BOX_OPEN_YOU"));
        ItemBox winItem = items.get(BoxAPI.getAmountItems() - 1);
        winItem.onApply(gamer);

        BukkitBalancePacket packet = new BukkitBalancePacket(gamer.getPlayerID(),
                BukkitBalancePacket.Type.KEYS,
                keyType.getId(),
                -1,
                true);
        BukkitConnector.getInstance().sendPacket(packet);

        section.openKeys(keyType, winItem.getRarity().getRarity() > 0 ? 0 : randomNumber + 1);

        box.onStart(gamer, items, winItem, Head.getHeadByValue(keyType.getHeadValue()));

        gamer.getPlayer().closeInventory();
    }

    private static Rarity getRandomRarity(int pseudoRandom) {
        List<Rarity> rarities = Arrays.asList(Rarity.values());
        rarities.sort(Comparator.comparingDouble(Rarity::getChance));
        double randChance = Math.random();
        for (Rarity rarity : rarities) {
            if (rarity.getChance() + rarity.getChangeChance() * pseudoRandom > randChance) {
                return rarity;
            }
        }

        return rarities.get(rarities.size() - 1);
    }

    private static ItemBox getRandomItemByRarity(KeyType keyType, Rarity rarity) {
        List<ItemBox> itemBoxes = ITEM_BOX_MANAGER.getItems(keyType, rarity);
        return itemBoxes.get(RANDOM.nextInt(itemBoxes.size()));
    }

    private static boolean isEnableOpen(KeyType keyType) {
        if (GameType.current == GameType.UNKNOWN) {
            if (SubType.current == SubType.LOBBY) {
                return true;
            }
            return keyType != KeyType.GAME_KEY && keyType != KeyType.DEFAULT_KEY;
        } else {
            return keyType != KeyType.SURVIVAL_KEY;
        }
    }
}
