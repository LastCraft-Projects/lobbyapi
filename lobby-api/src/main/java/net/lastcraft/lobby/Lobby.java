package net.lastcraft.lobby;

import lombok.Getter;
import net.lastcraft.api.LastCraft;
import net.lastcraft.base.game.GameState;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.base.gamer.constans.KeyType;
import net.lastcraft.connector.bukkit.Core;
import net.lastcraft.core.io.info.InfoFieldNames;
import net.lastcraft.core.io.info.InfoFields;
import net.lastcraft.core.io.info.ServerInfo;
import net.lastcraft.core.io.info.mapper.ServerInfoMapper;
import net.lastcraft.core.io.info.mapper.ServerInfoMapping;
import net.lastcraft.dartaapi.commands.MoneyCommand;
import net.lastcraft.dartaapi.listeners.JoinListener;
import net.lastcraft.dartaapi.utils.ArmorStandUtil;
import net.lastcraft.dartaapi.utils.WorldTime;
import net.lastcraft.lobby.api.LobbyAPI;
import net.lastcraft.lobby.api.leveling.Leveling;
import net.lastcraft.lobby.api.leveling.type.GroupLevelReward;
import net.lastcraft.lobby.api.leveling.type.KeysLevelReward;
import net.lastcraft.lobby.api.leveling.type.MessageLevelReward;
import net.lastcraft.lobby.api.leveling.type.MoneyLevelReward;
import net.lastcraft.lobby.bossbar.BossBarListener;
import net.lastcraft.lobby.commands.ReloadConfigCommand;
import net.lastcraft.lobby.config.GameConfig;
import net.lastcraft.lobby.config.LobbyConfig;
import net.lastcraft.lobby.config.SettingConfig;
import net.lastcraft.lobby.config.TopConfig;
import net.lastcraft.lobby.customitems.CustomItemListener;
import net.lastcraft.lobby.game.old.OldGameListener;
import net.lastcraft.lobby.game.old.stats.StatsCommand;
import net.lastcraft.lobby.profile.JumpListener;
import net.lastcraft.lobby.profile.PlayerListener;
import net.lastcraft.lobby.profile.gui.ProfileGuiListener;
import net.lastcraft.lobby.profile.hider.HiderListener;
import net.lastcraft.lobby.selector.SelectorManager;
import net.lastcraft.serverstream.client.ProvidingServerInfoReader;
import net.lastcraft.serverstream.client.ServerStreamer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class Lobby extends JavaPlugin {

    private SelectorManager selectorManager;
    private ServerInfoMapper serverInfoMapper;
    private ServerStreamer serverStreamer;

    private final Map<String, LobbyConfig> configs = new HashMap<>();

    @Override
    public void onEnable() {
        serverInfoMapper = new ServerInfoMapper();
        ServerInfoMapping gameMapping = new ServerInfoMapping();

        gameMapping.addStandardFields();
        gameMapping.addFieldLast(ServerInfo.GLOBAL_VERSION, "gameState", InfoFields.newEnumField(GameState.class));
        gameMapping.addFieldLast(ServerInfo.GLOBAL_VERSION, "mapName", InfoFields.STRING);
        //gameMapping.addFieldLast(ServerInfo.GLOBAL_VERSION, "teamPlayers", InfoFields.STRING);
        //gameMapping.addFieldLast(ServerInfo.GLOBAL_VERSION, "typeGame", InfoFields.newEnumField(TypeGame.class));

        ServerInfoMapping lobbyMapping = new ServerInfoMapping();

        lobbyMapping.addFieldLast(ServerInfo.GLOBAL_VERSION, InfoFieldNames.SERVER_NAME, InfoFields.STRING);
        lobbyMapping.addFieldLast(ServerInfo.GLOBAL_VERSION, "playerIds", InfoFields.INTEGER_ARRAY);

        serverInfoMapper.setMapping("(?i)hub-[1-9]|\\w+lobby-[1-9]", lobbyMapping);
        serverInfoMapper.setMapping("(?i)[p|s|e|b][g|w|r|b][n|t|i|s|d|r|c|p]{1,2}-\\w+", gameMapping);

        serverStreamer = new ServerStreamer("default", new ProvidingServerInfoReader(() -> serverInfoMapper), null);

        Core.registerServerStreamer(serverStreamer);

        SettingConfig settingConfig = initConfig(SettingConfig.class);
        GameConfig gameConfig = initConfig(GameConfig.class);
        initConfig(TopConfig.class);

        selectorManager = new SelectorManager(this, lobbyMapping);

        Leveling leveling = LobbyAPI.getLeveling();
        leveling.addReward(1, new MoneyLevelReward(500));
        leveling.addReward(2,
                new MoneyLevelReward(500),
                new KeysLevelReward(KeyType.DEFAULT_KEY, 1));
        leveling.addReward(3, new MoneyLevelReward(600));
        leveling.addReward(4,
                new MoneyLevelReward(650),
                new KeysLevelReward(KeyType.DEFAULT_KEY, 2));
        leveling.addReward(5,
                new MoneyLevelReward(700),
                new KeysLevelReward(KeyType.DEFAULT_KEY, 2),
                new KeysLevelReward(KeyType.GAME_KEY, 1));
        leveling.addReward(6, new MoneyLevelReward(800));
        leveling.addReward(7,
                new MoneyLevelReward(850),
                new KeysLevelReward(KeyType.DEFAULT_KEY, 3));
        leveling.addReward(8, new MoneyLevelReward(900));
        leveling.addReward(9,
                new MoneyLevelReward(950),
                new KeysLevelReward(KeyType.DEFAULT_KEY, 2),
                new KeysLevelReward(KeyType.GAME_KEY, 1));
        leveling.addReward(10,
                new MoneyLevelReward(1000),
                new KeysLevelReward(KeyType.DEFAULT_KEY, 3),
                new KeysLevelReward(KeyType.GAME_KEY, 3));
        leveling.addReward(11, new MoneyLevelReward(1100));
        leveling.addReward(12,
                new MoneyLevelReward(1200),
                new KeysLevelReward(KeyType.DEFAULT_KEY, 3));
        leveling.addReward(13, new MoneyLevelReward(1300));
        leveling.addReward(14,
                new MoneyLevelReward(1400),
                new KeysLevelReward(KeyType.DEFAULT_KEY, 3),
                new KeysLevelReward(KeyType.GAME_KEY, 3));
        leveling.addReward(15,
                new MoneyLevelReward(1500),
                new KeysLevelReward(KeyType.DEFAULT_KEY, 5),
                new KeysLevelReward(KeyType.GAME_KEY, 3),
                new KeysLevelReward(KeyType.GAME_COSMETIC_KEY, 1));
        leveling.addReward(20, new MessageLevelReward("LEVEL_REWARD_ANTICHEAT"));

        leveling.addReward(110, new GroupLevelReward(Group.GOLD));
        leveling.addReward(200, new GroupLevelReward(Group.DIAMOND));

        new ReloadConfigCommand(this);
        new MoneyCommand();

        new LobbyGuardListener(this, settingConfig);
        new JumpListener(this);
        new PlayerListener(this, settingConfig);
        new ProfileGuiListener(this);
        new BossBarListener(this);
        new JoinListener(this);
        new CustomItemListener(this, gameConfig);
        new HiderListener(this);

        if (LobbyAPI.isOldGame()) {
            new OldGameListener(this, gameConfig);
            net.lastcraft.lobby.game.old.top.TopConfig.loadTOP(this);
            new StatsCommand();
        }

        ArmorStandUtil.fixArmorStand(); //фикс стендов обычных

        if (!LastCraft.isHub()) {
            WorldTime.freezeTime("lobby", 6000, false);
        }
        //WorldTime.freezeTime("lobby", 22200, false);
    }

    private <T extends LobbyConfig> T initConfig(Class<T> configClass) {
        String name = configClass.getSimpleName().toLowerCase();
        T config = null;
        try {
            config = configClass.getConstructor(Lobby.class).newInstance(this);
            config.load();
            config.init();
            configs.put(name, config);
        } catch (Exception ignored) {
        }
        return config;
    }

    public <T extends LobbyConfig> T getConfig(Class<T> configClass) {
        String name = configClass.getSimpleName().toLowerCase();
        return (T) configs.get(name);
    }

    @Override
    public void reloadConfig() {
        //configs.values().forEach(LobbyConfig::reload); //todo сделать метод reload во всех конфигах
    }
}
