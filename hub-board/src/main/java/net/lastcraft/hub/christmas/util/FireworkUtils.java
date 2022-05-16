package net.lastcraft.hub.christmas.util;

import com.google.common.collect.ImmutableList;
import lombok.experimental.UtilityClass;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;

@UtilityClass
public class FireworkUtils {

    private final ImmutableList<Color> COLORS = ImmutableList.of(
            Color.GREEN,
            Color.WHITE,
            Color.RED,
            Color.ORANGE
    );

    public FireworkEffect createDefaultFireworkEffect() {
        FireworkEffect.Type type = FireworkEffect.Type.BALL_LARGE;

        return FireworkEffect.builder()
                .flicker(false)
                .trail(false)
                .with(type)
                .withColor(COLORS)
                .build();
    }

}
