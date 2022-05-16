package net.lastcraft.lobby.game.commands;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.game.GameState;
import net.lastcraft.base.gamer.constans.Group;
import net.lastcraft.lobby.config.GameConfig;
import net.lastcraft.lobby.game.data.Channel;
import net.lastcraft.lobby.game.data.Server;

import java.util.Map;

public class GameInfoCommand implements CommandInterface {

    private final GameConfig gameConfig;

    public GameInfoCommand(GameConfig gameConfig){
        SpigotCommand spigotCommand = COMMANDS_API.register("gameinfo", this, "gi");
        spigotCommand.setMinimalGroup(Group.ADMIN);

        this.gameConfig = gameConfig;
    }

    @Override
    public void execute(GamerEntity gamerEntity, String s, String[] args) {
        if (args.length > 0) {
            String nameChannel = args[0];
            Channel channel = gameConfig.getChannels().get(nameChannel.toLowerCase());
            if (channel == null) {
                gamerEntity.sendMessageLocale( "CHANNEL_NOT_FOUND", nameChannel);
                return;
            }

            send(channel, gamerEntity);
            return;
        }

        for (Channel channel : gameConfig.getChannels().values())
            send(channel, gamerEntity);
    }

    private static void send(Channel channel, GamerEntity gamerEntity) {
        Map<String, Server> servers = channel.getServers();
        int free = 0;
        int busy = 0;
        int online = 0;

        StringBuilder onRestart = new StringBuilder();
        for (Server server : servers.values()) {
            if (!server.isAlive()) {
                if (onRestart.toString().isEmpty()) {
                    onRestart.append(server.getName());
                } else {
                    onRestart.append(", ").append(server.getName());
                }
                continue;
            }

            online += server.getOnline();
            if (server.getGameState() == GameState.WAITING) {
                free++;
            } else {
                busy++;
            }

        }

        gamerEntity.sendMessage(" ");
        gamerEntity.sendMessage("§6Канал §c" + channel.getNameChannel() + "§6:");
        gamerEntity.sendMessage(" §c▪ §6Всего серверов - §7" + servers.size());
        gamerEntity.sendMessage(" §c▪ §6Свободных - §a" + free);
        gamerEntity.sendMessage(" §c▪ §6Идет игра - §e" + busy);
        gamerEntity.sendMessage(" §c▪ §6На перезагрузке - " + (onRestart.toString().isEmpty() ? "§aВсе арены работают" : "§c" + onRestart));
        gamerEntity.sendMessage(" §c▪ §6Общий онлайн §e" + online);
        gamerEntity.sendMessage(" ");
    }
}
