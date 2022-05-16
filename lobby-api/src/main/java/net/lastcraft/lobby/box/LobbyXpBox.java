package net.lastcraft.lobby.box;

import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.util.Rarity;
import net.lastcraft.box.type.XpBox;
import net.lastcraft.lobby.utils.LevelUtils;

public class LobbyXpBox extends XpBox {

    public LobbyXpBox(int exp, Rarity rarity) {
        super(exp, rarity);
    }

    @Override
    public void onMessage(BukkitGamer gamer) {
        LevelUtils.setExpData(gamer);
        super.onMessage(gamer);
    }
}
