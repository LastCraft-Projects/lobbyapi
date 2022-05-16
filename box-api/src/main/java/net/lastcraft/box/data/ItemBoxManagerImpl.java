package net.lastcraft.box.data;

import net.lastcraft.api.util.Rarity;
import net.lastcraft.base.gamer.constans.KeyType;
import net.lastcraft.box.api.ItemBox;
import net.lastcraft.box.api.ItemBoxManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemBoxManagerImpl implements ItemBoxManager {

    private final Map<KeyType, Map<Rarity, List<ItemBox>>> data = new ConcurrentHashMap<>();

    public ItemBoxManagerImpl() {
        for (KeyType keyType : KeyType.values()) {
            Map<Rarity, List<ItemBox>> rarityList = new ConcurrentHashMap<>();
            for (Rarity rarity : Rarity.values()) {
                rarityList.put(rarity, new ArrayList<>());
            }
            data.put(keyType, rarityList);
        }
    }

    @Override
    public List<ItemBox> getItems(KeyType keyType) {
        List<ItemBox> itemBoxes = new ArrayList<>();
        Map<Rarity, List<ItemBox>> rarityListMap = data.get(keyType);
        for (Rarity rarity : Rarity.values()) {
            itemBoxes.addAll(rarityListMap.get(rarity));
        }
        return itemBoxes;
    }

    @Override
    public List<ItemBox> getItems(KeyType keyType, Rarity rarity) {
        return data.get(keyType).get(rarity);
    }

    @Override
    public void addItemBox(KeyType keyType, ItemBox itemBox) {
        data.get(keyType).get(itemBox.getRarity()).add(itemBox);
    }

    @Override
    public void removeItemBoxes(KeyType keyType) {
        Map<Rarity, List<ItemBox>> rarityListMap = data.get(keyType);
        for (Rarity rarity : Rarity.values()) {
            rarityListMap.get(rarity).clear();
        }
    }

    @Override
    public void removeItemBox(ItemBox itemBox) {
        for (Map<Rarity, List<ItemBox>> listMap : data.values()) {
            for (Rarity rarity : Rarity.values()) {
                listMap.get(rarity).remove(itemBox);
            }
        }
    }
}
