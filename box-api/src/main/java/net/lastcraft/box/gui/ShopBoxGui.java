package net.lastcraft.box.gui;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.inventory.InventoryAPI;
import net.lastcraft.api.inventory.type.DInventory;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.sound.SoundAPI;
import net.lastcraft.api.util.Head;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.SoundType;
import net.lastcraft.base.gamer.constans.KeyType;
import net.lastcraft.base.gamer.constans.PurchaseType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.box.api.BoxAPI;
import net.lastcraft.dartaapi.guis.CustomItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopBoxGui {

    private static final SoundAPI SOUND_API = LastCraft.getSoundAPI();
    private static final InventoryAPI INVENTORY_API = LastCraft.getInventoryAPI();

    private final BukkitGamer gamer;
    private final DInventory inventory;
    private final BoughtKeyType boughtKeyType;
    private final ItemStack keyTypeItem;

    public ShopBoxGui(PlayerBoxGui boxGui, BukkitGamer gamer, BoughtKeyType boughtKeyType) {
        this.gamer = gamer;
        this.boughtKeyType = boughtKeyType;

        Language language = gamer.getLanguage();
        KeyType keyType = boughtKeyType.getKeyType();

        keyTypeItem = Head.getHeadByValue(keyType.getHeadValue());

        this.inventory = INVENTORY_API.createInventory(gamer.getPlayer(), language.getMessage("BOX_SHOP_GUI_NAME"),6);

        for (int slot : BoxAPI.EMPTY_SLOTS) {
            inventory.setItem(slot, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setName(" ")
                    .setDurability((short) 7)
                    .build()));
        }

        inventory.setItem(4, new DItem(ItemUtil.getBuilder(keyTypeItem.clone())
                .setName(keyType.getName(language))
                .setLore(keyType.getLore(language))
                .build()));

        inventory.setItem(49, new DItem(CustomItems.getBack(language),
                (player, clickType, i) -> boxGui.open()));

        initItems(11, 1);
        initItems(13, 3);
        initItems(15, 5);
    }

    public void open() {
        inventory.openInventory(gamer.getPlayer());
    }

    private void initItems(int slot, int amount) {
        Language language = gamer.getLanguage();

        KeyType keyType = boughtKeyType.getKeyType();

        inventory.setItem(slot, new DItem(ItemUtil.getBuilder(keyTypeItem.clone())
                .setName(keyType.getName(language))
                .setLore(language.getMessage("BOX_BUY_LORE_MAIN",
                        amount,
                        StringUtil.getCorrectWord(amount, "KEYS_1", language)))
                .setAmount(amount)
                .build()));

        //money
        boolean money = boughtKeyType.getPriceMoney() > 0;
        inventory.setItem(slot + 9, new DItem(ItemUtil.getBuilder(money ? new ItemStack(Material.IRON_INGOT, 1) :
                ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                        .setDurability((short) 14)
                        .build())
                .setAmount(money ? boughtKeyType.getPriceMoney() * amount : 1)
                .setName(language.getMessage("BOX_BUY_MONEY"))
                .setLore(money ? language.getList("BOX_BUY_LORE",
                        amount,
                        StringUtil.getCorrectWord(amount, "KEYS_1", language),
                        "§6" + boughtKeyType.getPriceMoney() * amount,
                        StringUtil.getCorrectWord(boughtKeyType.getPriceMoney() * amount, "MONEY_1",language)
                ) : language.getList("BOX_NO_BUY_LORE")).build(), (player, clickType, i) -> {
                    if (!money) {
                        SOUND_API.play(player, SoundType.NO);
                        return;
                    }

                    if (gamer.changeMoney(PurchaseType.MYSTERY_DUST, -boughtKeyType.getPriceMoney() * amount)) {
                        gamer.changeKeys(keyType, amount);
                        SOUND_API.play(player, SoundType.SELECTED);
                    }
                }));

        //gold
        boolean gold = boughtKeyType.getPriceGold() > 0;
        inventory.setItem(slot + 18, new DItem(ItemUtil.getBuilder(gold ? new ItemStack(Material.GOLD_INGOT, 1) :
                ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                        .setDurability((short) 14)
                        .build())
                .setName(language.getMessage("BOX_BUY_GOLD"))
                .setAmount(gold ? boughtKeyType.getPriceGold() * amount : 1)
                .setLore(gold ? language.getList("BOX_BUY_LORE",
                        amount,
                        StringUtil.getCorrectWord(amount, "KEYS_1", language),
                        "§e" + boughtKeyType.getPriceGold() * amount,
                        StringUtil.getCorrectWord(boughtKeyType.getPriceGold() * amount, "GOLD_1", language)
                ) : language.getList("BOX_NO_BUY_LORE")).build(), (player, clickType, i) -> {
                    if (!gold) {
                        SOUND_API.play(player, SoundType.NO);
                        return;
                    }

                    if (gamer.changeMoney(PurchaseType.GOLD, -boughtKeyType.getPriceGold() * amount)) {
                        gamer.changeKeys(keyType, amount);
                        SOUND_API.play(player, SoundType.SELECTED);
                    }
                }));

        //todo сделать получение ключей через конвертацию

    }
}
