package net.lastcraft.lobby.game.npc;

import lombok.AllArgsConstructor;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.lobby.game.data.Channel;

import java.util.function.Supplier;

@AllArgsConstructor
public final class OnlineReplacer implements Supplier<String> {

    private final Channel channel;
    private final Language lang;

    @Override
    public String get() {
        int online = channel.getOnline();

        return online >= 0 ? lang.getMessage( "HOLO_REPLACER_CHANNEL",
                StringUtil.getNumberFormat(online),
                StringUtil.getCorrectWord(online, "PLAYERS_1", lang)) :
                lang.getMessage( "HOLO_REPLACER_CHANNEL_ERROR");
    }
}
