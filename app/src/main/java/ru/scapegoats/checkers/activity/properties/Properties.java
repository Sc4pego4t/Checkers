package ru.scapegoats.checkers.activity.properties;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import ru.scapegoats.checkers.R;
import ru.scapegoats.checkers.activity.main.Main;
import ru.scapegoats.checkers.moduls.BaseActivity;

/**
 * Created by Андрей on 18.11.2017.
 */

public class Properties extends BaseActivity
{
	Spinner sp;
	boolean podsvChange=false;
	boolean fonChange=false;
	String podsv,fon;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		loadLocale();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.properties);
		ArrayAdapter<String> aa=new ArrayAdapter<>(this,R.layout.fill);
		sp=(Spinner)findViewById(R.id.spin);
		sp.setAdapter(aa);
		aa.addAll(getResources().getStringArray(R.array.lang));
	}
	public void apply(View view)
	{
		int id=(int)sp.getSelectedItemId();
		String lang="";
		switch (id)
		{
			case 0:lang="en";break;
			case 1:lang="ru";break;
		}
		changeLang(lang);
		if(podsvChange)
			savePref(podsv,"Podsv");
		if(fonChange)
			savePref(fon,"Fon");
		Intent i = new Intent( this , Main.class );
		this.startActivity(i);
	}
	@TargetApi(16)
	public void podsv(View view)
	{
		podsvChange=true;
		LinearLayout ll;
		ll=(LinearLayout)findViewById(R.id.blue);
		ll.setBackground(null);
		ll=(LinearLayout)findViewById(R.id.red);
		ll.setBackground(null);
		ll=(LinearLayout)findViewById(R.id.green);
		ll.setBackground(null);
		ll=(LinearLayout)findViewById(R.id.yellow);
		ll.setBackground(null);
		ll=(LinearLayout)findViewById(R.id.purple);
		ll.setBackground(null);

		ll=(LinearLayout)view;
		ll.setBackground(getResources().getDrawable(R.drawable.square2));
		podsv=ll.getTag().toString();
	}

	@TargetApi(16)
	public void fon(View view)
	{
		fonChange=true;
		LinearLayout ll;
		ll=(LinearLayout)findViewById(R.id.back1);
		ll.setBackground(null);
		ll=(LinearLayout)findViewById(R.id.back2);
		ll.setBackground(null);
		ll=(LinearLayout)findViewById(R.id.back3);
		ll.setBackground(null);

		ll=(LinearLayout)view;
		ll.setBackground(getResources().getDrawable(R.drawable.square2));
		fon=ll.getTag().toString();
	}



}
