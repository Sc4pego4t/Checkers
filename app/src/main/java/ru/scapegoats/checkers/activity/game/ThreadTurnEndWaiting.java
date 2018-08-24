package ru.scapegoats.checkers.activity.game;

import ru.scapegoats.checkers.activity.session.SessionActivity;
import ru.scapegoats.checkers.util.ApiFactory;
import ru.scapegoats.checkers.util.CreatingObservers;

public class ThreadTurnEndWaiting extends Thread {
    Game activity;
    String gameid;
    String role;
    Boolean running=true;

    public ThreadTurnEndWaiting(Game activity,String gameid){
        this.activity=activity;
        this.gameid=gameid;
    }
    @Override
    public void run() {
        while (running) {
            ApiFactory.waitTurn(gameid).subscribe(
                    CreatingObservers.waitTurn(activity));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopRun(){
        running=false;
    }
}
