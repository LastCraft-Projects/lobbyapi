package net.lastcraft.hub.command;

import net.lastcraft.api.command.CommandInterface;
import net.lastcraft.api.command.SpigotCommand;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerEntity;
import net.lastcraft.base.gamer.IBaseGamer;
import net.lastcraft.dartaapi.utils.bukkit.BukkitUtil;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class HorseCommand implements CommandInterface {

    private final Random random = new Random();
    private final JavaPlugin plugin;

    public HorseCommand(JavaPlugin plugin) {
        this.plugin = plugin;

        SpigotCommand spigotCommand = COMMANDS_API.register("horse", this);
        spigotCommand.setOnlyPlayers(true);
    }

    @Override
    public void execute(GamerEntity gamerEntity, String cmd, String[] args) {
        BukkitGamer gamer = (BukkitGamer) gamerEntity;
        Player player = gamer.getPlayer();

        if (args.length < 1 || !args[0].equalsIgnoreCase("sosay")) {
            gamer.sendMessageLocale("NO_PERMS");
            return;
        }

        Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
        horse.setMetadata("owner", new FixedMetadataValue(plugin, gamerEntity.getName().toLowerCase()));
        horse.teleport(player.getLocation());
        horse.setOwner(player);

        horse.setAI(false);
        horse.setAdult();

        horse.setMaxHealth(15D);
        horse.setHealth(15D);
        horse.setFireTicks(0);

        horse.setColor(Horse.Color.values()[random.nextInt(Horse.Color.values().length)]);
        horse.setStyle(Horse.Style.values()[random.nextInt(Horse.Style.values().length)]);

        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        ItemStack armor = getArmor(gamer);
        if (armor != null)
            horse.getInventory().setArmor(armor);

        BukkitUtil.runTaskLater(2L, ()-> horse.setPassenger(player));
    }

    private static ItemStack getArmor(IBaseGamer gamer) {
        switch (gamer.getGroup()) {
            case DEFAULT:
                return null;
            case GOLD:
                return new ItemStack(Material.IRON_BARDING);
            case DIAMOND:
                return new ItemStack(Material.GOLD_BARDING);
            default:
                return new ItemStack(Material.DIAMOND_BARDING);
        }
    }
}
