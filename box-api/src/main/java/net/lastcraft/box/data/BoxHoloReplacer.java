package net.lastcraft.box.data;

import com.google.common.collect.Iterators;
import lombok.RequiredArgsConstructor;
import net.lastcraft.api.player.BukkitGamer;
import net.lastcraft.base.locale.Language;
import net.lastcraft.base.util.StringUtil;
import net.lastcraft.box.util.BoxUtil;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Supplier;

@RequiredArgsConstructor
public final class BoxHoloReplacer implements Supplier<String> {

    private final BukkitGamer gamer;
    private final Iterator<Character> colors = Iterators.cycle(Arrays.asList('d', '6', 'e', 'a', 'b'));

    @Override
    public String get() {
        Language lang = gamer.getLanguage();
        int keys = BoxUtil.getKeys(gamer);
        return lang.getMessage("KEY_BOX",
                "§" + colors.next(),
                StringUtil.getNumberFormat(keys),
                StringUtil.getCorrectWord(keys, "KEYS_1", lang));
    }
}