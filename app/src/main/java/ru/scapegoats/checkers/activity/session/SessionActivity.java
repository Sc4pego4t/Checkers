package ru.scapegoats.checkers.activity.session;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import ru.scapegoats.checkers.R;
import ru.scapegoats.checkers.moduls.BaseActivity;
import ru.scapegoats.checkers.util.ApiFactory;
import ru.scapegoats.checkers.util.CreatingObservers;
import ru.scapegoats.checkers.util.Keywords;

public class SessionActivity extends BaseActivity {
    private final static String GUEST_READY = "1";
    private final static String GUEST_UNREADY = "0";
    public TextView rate1,nick1,rate2,nick2;
    public boolean guestReady=false;
    public ThreadCheckGuests thread;
    public ThreadGameStarted threadGameStarted;
    public CardView cardView;
    public Button ready,start;
    String hostToken;
    //id комнаты
    String sesid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        rate1=findViewById(R.id.rate1);
        rate2=findViewById(R.id.rate2);
        nick1=findViewById(R.id.nick1);
        nick2=findViewById(R.id.nick2);
        ready=findViewById(R.id.ready);
        start=findViewById(R.id.start);
        butEnabled(start,getResources().getDrawable(R.color.grey),false);
        cardView=findViewById(R.id.guest);

        sesid=getIntent().getExtras().getString(Keywords.session);
            hostToken=getIntent().getExtras().getString(Keywords.Host);
            if(hostToken!=null) {
                //TODO: сделай возможность выкидывать гостя если вышел создатель
                //Guest actions

                Log.e("my number", usertoken + "");
                ApiFactory.getPlayerInfo(usertoken + "", false).subscribe(
                        CreatingObservers.displayUserInfo(this, Keywords.Guest)
                );

                Log.e("host number", hostToken);
                ApiFactory.getPlayerInfo(hostToken, false).subscribe(
                        CreatingObservers.displayUserInfo(this, Keywords.Host)
                );
                ApiFactory.updateSession(sesid, usertoken + "").subscribe(
                        CreatingObservers.updatedResult(this)
                );
                threadGameStarted=new ThreadGameStarted(this,sesid);
                threadGameStarted.start();
                ready.setOnClickListener(e->{
                    if(!guestReady) {
                        ApiFactory.guestReady(sesid, GUEST_READY, true).subscribe(
                                CreatingObservers.guestReady(this)
                        );
                    } else {
                        ApiFactory.guestReady(sesid,GUEST_UNREADY,true).subscribe(
                                CreatingObservers.guestReady(this)
                        );
                    }
                });
            } else {
                butEnabled(ready,getResources().getDrawable(R.color.grey),false);
                //Host actions
                Log.e("gg", "host");
                ApiFactory.getPlayerInfo(usertoken + "", true).subscribe(
                        CreatingObservers.displayUserInfo(this, Keywords.Host)
                );
                //Thread for checking guests approval
                thread = new ThreadCheckGuests(this, sesid);
                thread.start();
            }

            setClickListners();
    }
    void setClickListners(){
        start.setOnClickListener(e->
            ApiFactory.gameStart(sesid).subscribe(
                    CreatingObservers.startGame(this,Keywords.Host)
            ));
    }

    @TargetApi(16)
    public void butEnabled(Button b, Drawable dr, boolean bool){
        b.setEnabled(bool);
        b.setBackground(dr);
    }

    @Override
    protected void onDestroy() {
        Log.e("Destroyed", "Destroyed");
        if(hostToken!=null){
            //если гость ливнет
            ApiFactory.guestLeave(sesid);
        } else{
            //if host leaves
            thread.stopRun();
            ApiFactory.deleteSesssion(sesid);
        }
        super.onDestroy();
    }
}
