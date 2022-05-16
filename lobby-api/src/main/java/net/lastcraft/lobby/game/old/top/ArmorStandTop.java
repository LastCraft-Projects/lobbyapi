package net.lastcraft.lobby.game.old.top;

import net.lastcraft.api.LastCraft;
import net.lastcraft.api.depend.CraftVector;
import net.lastcraft.api.entity.EntityEquip;
import net.lastcraft.api.entity.stand.CustomStand;
import net.lastcraft.api.hologram.Hologram;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.player.GamerManager;
import net.lastcraft.base.locale.Localization;
import net.lastcraft.dartaapi.utils.inventory.ItemUtil;
import net.lastcraft.lobby.game.old.top.data.TopData;
import net.lastcraft.lobby.game.old.top.data.TopPlayer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class ArmorStandTop {

    private static final GamerManager GAMER_MANAGER = LastCraft.getGamerManager();

    private CustomStand customStand;
    private BukkitGamer gamer;
    private Hologram hologram;
    private Location location;
    private int place;

    ArmorStandTop(BukkitGamer gamer, int place, Location location, TopData topData) {
        this.gamer = gamer;
        this.location = location;
        this.place = place;

        customStand = LastCraft.getEntityAPI().createStand(location);
        customStand.setArms(true);
        customStand.setSmall(true);
        customStand.setBasePlate(false);
        setEquipment(place);
        customStand.showTo(gamer);

        create(topData);
    }

    public void remove() {
        if (customStand != null) {
            customStand.remove();
        }
        if (hologram != null) {
            hologram.remove();
        }
    }

    public void create(TopData topData) {
        if (gamer == null) {
            return;
        }
        int lang = gamer.getLanguage().getId();

        String displayName;
        ItemStack head;
        int statsTopPlayer;
        TopPlayer topPlayer = topData.getPlayersTop().get(place);
        if (topPlayer != null) {
            displayName = topPlayer.getDisplayName();
            head = topPlayer.getHead();
            statsTopPlayer = topPlayer.getStats();
        } else {
            displayName = "§cError!";
            head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            statsTopPlayer = 0;
        }

        customStand.getEntityEquip().setHelmet(new ItemStack(head));

        if (hologram != null) {
            hologram.remove();
        }

        hologram = LastCraft.getHologramAPI().createHologram(location.clone().add(0.0, 1.2, 0.0));
        hologram.addTextLine(String.format(Localization.getMessage(lang, "TOP_POSITION"), String.valueOf(place)));
        hologram.addTextLine(displayName);
        hologram.addTextLine(statsTopPlayer + " " + topData.getSecondLineHolo(statsTopPlayer, lang));
        hologram.showTo(gamer);
    }

    private void setEquipment(int place){
        Color color;
        ItemStack itemInHand;

        switch (place){
            case 1:
                color = Color.fromRGB(23, 164, 79);
                itemInHand = new ItemStack(Material.EMERALD);
                customStand.setLeftArmPose(new CraftVector(-20,0,-120));
                customStand.setRightArmPose(new CraftVector(-40,50,90));
                customStand.setRightLegPose(new CraftVector(-10,70,40));
                customStand.setLeftLegPose(new CraftVector(-10,-60,-40));
                customStand.setHeadPose(new CraftVector(15,0,0));
                break;
            case 2:
                color = Color.fromRGB(46, 210, 185);
                itemInHand = new ItemStack(Material.DIAMOND);
                customStand.setLeftArmPose(new CraftVector(-20,0,-140));
                customStand.setRightArmPose(new CraftVector(-50,20,10));
                customStand.setRightLegPose(new CraftVector(-5,-10,10));
                customStand.setLeftLegPose(new CraftVector(-10,-10,-6));
                customStand.setHeadPose(new CraftVector(5,0,5));
                break;
            case 3:
                color = Color.fromRGB(179, 132, 16);
                itemInHand = new ItemStack(Material.GOLD_INGOT);
                customStand.setLeftArmPose(new CraftVector(50,15,-7));
                customStand.setRightArmPose(new CraftVector(-50,10,5));
                customStand.setRightLegPose(new CraftVector(-20,0,5));
                customStand.setLeftLegPose(new CraftVector(20,0,-5));
                customStand.setHeadPose(new CraftVector(0,0,2));
                break;
            case 4:
                color = Color.fromRGB(190, 190, 194);
                itemInHand = new ItemStack(Material.IRON_INGOT);
                customStand.setLeftArmPose(new CraftVector( -10,0,-60));
                customStand.setRightArmPose(new CraftVector(-10,0,130));
                customStand.setRightLegPose(new CraftVector(0,0,60));
                customStand.setHeadPose(new CraftVector(0,0,10));
                break;
            case 5:
                color = Color.fromRGB(176, 86, 63);
                itemInHand = new ItemStack(Material.CLAY_BRICK);
                customStand.setLeftArmPose(new CraftVector(-90,-20,-30));
                customStand.setRightArmPose(new CraftVector(50,30,90));
                customStand.setRightLegPose(new CraftVector(50,0,15));
                customStand.setLeftLegPose(new CraftVector(-7,-6,-5));
                customStand.setHeadPose(new CraftVector(30,10,10));
                customStand.setBodyPose(new CraftVector(6,6,0));
                break;
            default:
                color = Color.fromRGB(0,0,0);
                itemInHand = new ItemStack(Material.BARRIER);
        }

        EntityEquip equip = customStand.getEntityEquip();
        equip.setChestplate(ItemUtil.getColorLeatherArmor(Material.LEATHER_CHESTPLATE, color));
        equip.setLeggings(ItemUtil.getColorLeatherArmor(Material.LEATHER_LEGGINGS, color));
        equip.setBoots(ItemUtil.getColorLeatherArmor(Material.LEATHER_BOOTS, color));
        equip.setItemInMainHand(itemInHand);
    }
}
