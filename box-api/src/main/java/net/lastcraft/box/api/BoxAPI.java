package net.lastcraft.box.api;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.lastcraft.box.data.Box;
import net.lastcraft.box.data.ItemBoxManagerImpl;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class BoxAPI {

    public static final ImmutableList<Integer> EMPTY_SLOTS = ImmutableList.of(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 49, 51, 52, 53
    );

    private ItemBoxManager itemBoxManager;

    @Getter
    private final int amountItems = 5; //сколько айтемов будет вылетать

    @Getter
    private final List<Box> boxes = new ArrayList<>();

    public ItemBoxManager getItemBoxManager() {
        if (itemBoxManager == null) {
            itemBoxManager = new ItemBoxManagerImpl();
        }

        return itemBoxManager;
    }


}
