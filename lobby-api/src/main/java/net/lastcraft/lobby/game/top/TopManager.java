package net.lastcraft.lobby.game.top;

import net.lastcraft.dartaapi.armorstandtop.ArmorStandTopManager;
import net.lastcraft.dartaapi.armorstandtop.StandTop;
import net.lastcraft.lobby.config.TopConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TopManager {

    private final TopConfig topConfig;

    private ArmorStandTopManager armorStandTopManager;
    private TopSql topSql;

    private final Map<TopTable, List<TopStandData>> data = new ConcurrentHashMap<>();

    public TopManager(TopConfig topConfig, String table) {
        this.topConfig = topConfig;

        armorStandTopManager = new ArmorStandTopManager(topConfig.getLobby(),
                "s3.huesos228.com", "SelectedTop", table);
        topSql = new TopSql(topConfig.getStandLocations().size());

        initDefault();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::update, 0,
                topConfig.getTime(), TimeUnit.MINUTES);
    }

    private void initDefault() {
        topConfig.getTopTables().forEach(topTable -> {
            List<TopStandData> standData = new ArrayList<>();
            List<StandTop> standTops = new ArrayList<>();

            AtomicInteger position = new AtomicInteger(1);

            topConfig.getStandLocations().forEach(location -> {
                TopStandData topStandData = new TopStandData(1, topTable.getColumnFormatLocaleKey());
                StandTop standTop = new StandTop(location, position.get(), topStandData);

                position.getAndIncrement();

                standData.add(topStandData);
                standTops.add(standTop);
            });

            data.put(topTable, standData);

            String topBy = topTable.getColumn().equals("Wins") ? "" : "_BY_" + topTable.getColumn().toUpperCase();

            if (topTable.getType() == 1) {
                armorStandTopManager.addArmorStandTop(standTops, "HOLO_TOP_MONTHLY" + topBy, topTable.getHoloName());
            } else {
                armorStandTopManager.addArmorStandTop(standTops, "HOLO_TOP_ALL" + topBy, topTable.getHoloName());
            }

            //armorStandTopManager.addArmorStandTop(standTops,
            //        topTable.getType() == 1 ? "HOLO_TOP_MONTLY" : "HOLO_TOP_ALL",
            //        topTable.getHoloName());
        });
    }

    private void update() {
        topSql.update(data);
        armorStandTopManager.updateTops();
    }
}
