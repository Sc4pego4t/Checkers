package ru.scapegoats.checkers.activity.session;

import ru.scapegoats.checkers.util.ApiFactory;
import ru.scapegoats.checkers.util.CreatingObservers;

public class ThreadCheckGuests extends Thread {
    SessionActivity activity;
    String sesid;
    Boolean running=true;

    ThreadCheckGuests(SessionActivity activity,String sesid){
        this.activity=activity;
        this.sesid=sesid;
    }
    @Override
    public void run() {
        while (running) {
            ApiFactory.checkGuest(sesid).subscribe(
                    CreatingObservers.checkGuest(activity)
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
