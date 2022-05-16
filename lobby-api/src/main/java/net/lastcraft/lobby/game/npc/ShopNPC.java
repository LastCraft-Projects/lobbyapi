package net.lastcraft.lobby.game.npc;

import net.lastcraft.api.entity.npc.types.HumanNPC;
import net.lastcraft.api.hologram.Hologram;
import net.lastcraft.base.locale.Language;
import org.bukkit.Location;

public final class ShopNPC extends LobbyNPC {

    public ShopNPC(HumanNPC humanNPC, Location location) {
        super(humanNPC);

        for (Language lang : Language.values()) {
            Hologram hologram = HOLOGRAM_API.createHologram(location.clone().add(0.0, 1.9, 0.0));
            hologram.addTextLine(lang.getList("HOLO_SHOP_NPC"));
            holograms.put(lang.getId(), hologram);
        }
    }
}
