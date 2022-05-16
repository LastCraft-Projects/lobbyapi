package net.lastcraft.lobby.profile.gui.guis;

import net.lastcraft.api.inventory.DItem;
import net.lastcraft.api.util.Head;
import net.lastcraft.api.util.ItemUtil;
import net.lastcraft.base.SoundType;
import net.lastcraft.base.locale.Language;
import net.lastcraft.lobby.api.profile.ProfileGui;
import org.bukkit.entity.Player;

public class LangPage extends ProfileGui {

    public LangPage(Player player) {
        super(player, "PROFILE_MAIN_ITEM_LANG_NAME");
    }

    @Override
    protected void setItems() {
        setBackItem();

        int slot = 11;
        for (Language language : Language.values()) {
            boolean enable = language == lang;
            Head head = Head.valueOf(language.getHeadName());
            inventory.setItem(slot++, new DItem(ItemUtil.getBuilder(head)
                    .setName((enable ? "§a" : "§c") + language.getName())
                    .setLore(lang.getMessage("PROFILE_LANG_ITEM_LORE1", language.getName()))
                    .addLore("")
                    .addLore(enable ? lang.getMessage( "PROFILE_LANG_ITEM_LORE3")
                            : lang.getMessage( "PROFILE_LANG_ITEM_LORE2"))
                    .build(), (clicker, clickType, i) -> {
                if (enable) {
                    SOUND_API.play(clicker, SoundType.NO);
                    return;
                }

                SOUND_API.play(clicker, SoundType.DESTROY);
                gamer.setLanguage(language);
                clicker.closeInventory();

                this.update();
            }));
        }
    }
}
