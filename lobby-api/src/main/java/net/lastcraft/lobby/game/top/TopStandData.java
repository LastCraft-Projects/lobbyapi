package net.lastcraft.lobby.game.top;

import net.lastcraft.dartaapi.armorstandtop.data.StandTopData;

import java.util.function.Supplier;

public class TopStandData extends StandTopData {

    private int statValue = 0;

    public TopStandData(int playerID, String key) {
        super(playerID, key, true);
    }

    @Override
    protected Supplier<Integer> getSupplier() {
        return () -> statValue;
    }

    public void update(int playerID, int statValue) {
        this.playerID = playerID;
        this.statValue = statValue;
    }
}
