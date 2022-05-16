package net.lastcraft.lobby.game.old.top.data;

import net.lastcraft.lobby.game.old.top.TopConfig;

import java.util.concurrent.TimeUnit;

@Deprecated
public class TopTask extends Thread {

    public TopTask() {
        start();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()){
            try {
                TopConfig.getTopLoader().updateTops();
                Thread.sleep(TimeUnit.HOURS.toMillis(1));
            } catch (InterruptedException ignored){}
        }
    }
}
