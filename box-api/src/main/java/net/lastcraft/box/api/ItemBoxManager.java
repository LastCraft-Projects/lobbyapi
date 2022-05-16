package net.lastcraft.box.api;

import net.lastcraft.api.util.Rarity;
import net.lastcraft.base.gamer.constans.KeyType;

import java.util.List;

public interface ItemBoxManager {

    List<ItemBox> getItems(KeyType keyType);

    List<ItemBox> getItems(KeyType keyType, Rarity rarity);

    void addItemBox(KeyType keyType, ItemBox itemBox);

    void removeItemBoxes(KeyType keyType);
    void removeItemBox(ItemBox itemBox);
}
