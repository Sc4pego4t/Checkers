package ru.scapegoats.checkers.activity.session;

import ru.scapegoats.checkers.util.ApiFactory;
import ru.scapegoats.checkers.util.CreatingObservers;
import ru.scapegoats.checkers.util.Keywords;

public class ThreadGameStarted extends Thread {
    SessionActivity activity;
    String sesid;
    Boolean running=true;

    ThreadGameStarted(SessionActivity activity,String sesid){
        this.activity=activity;
        this.sesid=sesid;
    }
    @Override
    public void run() {
        while (running) {
            ApiFactory.checkGameStarted(sesid).subscribe(
                    CreatingObservers.startGame(activity,Keywords.Guest)
            );
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopRun(){
        running=false;
    }
}