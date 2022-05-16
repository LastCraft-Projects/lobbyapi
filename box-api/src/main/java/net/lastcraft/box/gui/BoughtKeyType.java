package net.lastcraft.box.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lastcraft.base.gamer.constans.KeyType;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum BoughtKeyType {
    DEFAULT_KEY(KeyType.DEFAULT_KEY, 200, 1),
    GAME_KEY(KeyType.GAME_KEY, 300, 2),
    DONATE_KEY(KeyType.GROUP_KEY, 0, 24),
    COSMETIC_KEY(KeyType.GAME_COSMETIC_KEY, 2000, 3),
    ;

    private final KeyType keyType;
    private final int priceMoney;
    private final int priceGold;

    public static BoughtKeyType getBoughtType(KeyType keyType) {
        return Arrays.stream(values())
                .filter(boughtKeyType -> boughtKeyType.keyType == keyType)
                .findFirst()
                .orElse(null);
    }
}
