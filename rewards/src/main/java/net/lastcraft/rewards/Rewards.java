package net.lastcraft.rewards;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.entity.npc.types.HumanNPC;
import net.lastcraft.api.util.LocationUtil;
import net.lastcraft.base.skin.Skin;
import net.lastcraft.dartaapi.utils.core.CoreUtil;
import net.lastcraft.rewards.managers.RewardListener;
import net.lastcraft.rewards.managers.RewardManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Rewards extends JavaPlugin {

    private HumanNPC humanNPC;
    private RewardManager rewardManager;

    @Override
    public void onEnable() {
        Location locationNPC = LocationUtil.stringToLocation(loadConfig().getString("NPCLocation"), true);
        humanNPC = LastCraft.getEntityAPI().createNPC(locationNPC, Skin.SKIN_REWARD);
        humanNPC.setPublic(true);

        rewardManager = new RewardManager();

        //Listeners
        new RewardListener(this);
    }

    private FileConfiguration loadConfig() {
        return YamlConfiguration.loadConfiguration(new File(CoreUtil.getConfigDirectory() + "/rewards.yml"));
    }

    public HumanNPC getHumanNPC() {
        return humanNPC;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }
}
