package net.lastcraft.box.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.api.util.Rarity;
import net.lastcraft.base.locale.Language;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Getter
public abstract class ItemBox {

    private final ItemStack icon;
    private final Rarity rarity;

    public final ItemStack getIcon() {
        return icon.clone();
    }

    /**
     * имя которое будет
     * @param lang - язык
     * @return - имя
     */
    public abstract String getName(Language lang);

    /**
     * выполняется до анимации(выдает приз)
     * @param gamer - победитель
     */
    public abstract void onApply(BukkitGamer gamer);

    /**
     * выполняется после анимации(пишет сообщение о том, что ты выиграл)
     * @param gamer - победитель
     */
    public abstract void onMessage(BukkitGamer gamer);

}
