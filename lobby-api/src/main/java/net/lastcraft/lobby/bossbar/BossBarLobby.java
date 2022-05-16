package net.lastcraft.lobby.bossbar;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.lastcraft.base.locale.Language;
import net.lastcraft.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import javax.annotation.Nullable;

public class BossBarLobby {

    private final TIntObjectMap<BossBarReplaced> bossBars = TCollections.synchronizedMap(new TIntObjectHashMap<>());

    BossBarLobby(Lobby lobby) {
        for (Language language : Language.values()) {
            BossBar bossBar = Bukkit.createBossBar(language.name(), BarColor.GREEN, BarStyle.SEGMENTED_12);
            bossBar.setProgress(0.0f);
            bossBars.put(language.getId(), new BossBarReplaced(bossBar, language.getList("BOSS_BAR_LOBBY")));
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(lobby, ()-> {
            for (BossBarReplaced bossBarReplaced : bossBars.valueCollection()) {
                bossBarReplaced.run();
            }
        }, 0, 15);
    }

    @Nullable
    public BossBar get(Language language) {
        BossBarReplaced bossBarReplaced = bossBars.get(language.getId());
        if (bossBarReplaced != null) {
            return bossBarReplaced.getBar();
        }
        return null;
    }
}
