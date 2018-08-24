package ru.scapegoats.checkers.activity.main;

import android.annotation.TargetApi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ru.scapegoats.checkers.activity.autorization.AutorizationActivity;
import ru.scapegoats.checkers.activity.properties.Properties;
import ru.scapegoats.checkers.R;
import ru.scapegoats.checkers.moduls.BaseActivity;
import ru.scapegoats.checkers.moduls.MyDialog;

/**
 * Created by Андрей on 02.10.2017.
 */

public class Main extends BaseActivity
{
    TextView b;
    LinearLayout ll;
    int start = 0;
    int w = 140;
    Button b1, b2, b3, b4;
    int go = 0;
    Typeface typeface;
    Context context;
    float density;
    LinearLayout.LayoutParams param;


    @Override
    @TargetApi(16)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        density = getApplicationContext().getResources().getDisplayMetrics().density;
        //bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        setContentView(R.layout.main_act);
        getSupportActionBar().hide();
        ll = (LinearLayout) findViewById(R.id.fon);
        loadFon(ll);

        context = this;
        typeface = Typeface.createFromAsset(getAssets(), "fonts/Rosamunda Two.ttf");
        ll = (LinearLayout) findViewById(R.id.ll);
        b1 = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.but2);
        b3 = (Button) findViewById(R.id.but3);
        b4 = (Button) findViewById(R.id.but1);
        ((Button)findViewById(R.id.auth)).setTypeface(typeface);


        b1.setTypeface(typeface);
        b2.setTypeface(typeface);
        b3.setTypeface(typeface);
        b4.setTypeface(typeface);
    }

    DialogFragment a;

    public void net(View view)
    {
        idDialog=2;
        a = new MyDialog();
        ((MyDialog) a).usertoken=usertoken;
        a.show(getSupportFragmentManager(), "2");
    }

    public void play(View view)
    {
        idDialog = 1;
        a = new MyDialog();
        a.show(getSupportFragmentManager(), "1");

    }

    public void prop(View view)
    {
        Intent intent = new Intent(this, Properties.class);
        startActivity(intent);
    }

    public void exit(View view)
    {
        this.finish();

    }
    public void auth(View view){
        startActivity(new Intent(this,AutorizationActivity.class));
    }

}

