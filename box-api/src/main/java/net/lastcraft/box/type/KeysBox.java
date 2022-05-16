package net.lastcraft.box.type;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.util.Head;
import net.lastcraft.api.util.Rarity;
import net.lastcraft.base.gamer.constans.KeyType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.box.api.ItemBox;

public class KeysBox extends ItemBox {

    private final int keys;
    private final KeyType keyType;

    public KeysBox(int keys, Rarity rarity, KeyType keyType) {
        super(Head.getHeadByValue(keyType.getHeadValue()), rarity);
        this.keys = keys;
        this.keyType = keyType;
    }

    @Override
    public String getName(Language lang) {
        return lang.getMessage("BOX_KEYS", StringUtil.getNumberFormat(keys));
    }

    @Override
    public void onApply(BukkitGamer gamer) {
        gamer.changeKeys(keyType, keys);
    }

    @Override
    public void onMessage(BukkitGamer gamer) {
        gamer.sendActionBar("§d+" + keys + " "
                + StringUtil.getCorrectWord(keys, "KEYS_1", gamer.getLanguage()));
    }
}
