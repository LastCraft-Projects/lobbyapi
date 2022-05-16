package net.lastcraft.lobby.bossbar;

import com.google.common.collect.Iterators;
import lombok.Getter;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BossBarReplaced implements Runnable {

    private static final Random RANDOM = new Random();

    @Getter
    private final BossBar bar;
    private final Iterator<String> text;

    private String oldText;

    private int time = 20;

    BossBarReplaced(BossBar bossBar, List<String> text) {
        this.bar = bossBar;
        this.text = Iterators.cycle(text);
        this.oldText = this.text.hasNext() ? this.text.next() : "";
    }

    @Override
    public void run() {
        time--;

        if (time != 0 || this.bar == null) {
            return;
        }

        time = 20;

        this.bar.setColor(BarColor.values()[BossBarReplaced.RANDOM.nextInt(BarColor.values().length - 1)]);

        String text = this.text.hasNext() ? this.text.next() : "";
        if (!oldText.equals(text)) {
            oldText = text;
            this.bar.setTitle(text);
        }
    }
}
