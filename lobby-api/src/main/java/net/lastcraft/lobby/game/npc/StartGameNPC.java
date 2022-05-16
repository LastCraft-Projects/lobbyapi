package net.lastcraft.lobby.game.npc;

import lombok.Getter;
import net.lastcraft.api.entity.npc.types.HumanNPC;
import net.lastcraft.api.hologram.Hologram;
import net.lastcraft.base.locale.Language;
import net.lastcraft.lobby.api.game.GameUpdateType;
import net.lastcraft.lobby.game.data.Channel;
import org.bukkit.Location;

public final class StartGameNPC extends LobbyNPC {

    @Getter
    private final Channel channel;

    public StartGameNPC(String name, Location location, Channel channel, HumanNPC humanNPC, GameUpdateType gameUpdateType) {
        super(humanNPC);
        this.channel = channel;

        for (Language language : Language.values()) {
            String dependString = (gameUpdateType == GameUpdateType.DEFAULT ? ""
                    : " " + gameUpdateType.getChatColor() + language.getMessage(gameUpdateType.getKey()));

            Hologram hologram = HOLOGRAM_API.createHologram(location.clone().add(0.0, 1.9, 0.0));
            hologram.addTextLine("§b§l" + name + dependString);
            hologram.addAnimationLine(20 * 3, new OnlineReplacer(channel, language));
            hologram.addTextLine(language.getMessage("HOLO_SELECTOR_GAME_CHANNEL"));
            holograms.put(language.getId(), hologram);
        }
    }

}
