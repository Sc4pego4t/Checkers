package ru.scapegoats.checkers.activity.internet;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.scapegoats.checkers.R;
import ru.scapegoats.checkers.activity.createsession.CreateSessionActivity;
import ru.scapegoats.checkers.moduls.BaseActivity;
import ru.scapegoats.checkers.util.ApiFactory;
import ru.scapegoats.checkers.util.CreatingObservers;

/**
 * Created by Андрей on 18.11.2017.
 */

public class InternetMenu extends BaseActivity
{

	//TODO сделай вход с ограниченным рейтингом
	public RecyclerView recyclerView;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		loadLocale();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.internet_menu);
		recyclerView=findViewById(R.id.recyclerView);

		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		ApiFactory.getSessions().subscribe(
				CreatingObservers.getSessionList(this)
		);

		findViewById(R.id.accept).setOnClickListener(e->{
			startActivity(new Intent(this,CreateSessionActivity.class));
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.bar, menu);
		mi=menu.findItem(R.id.item1);
		mi.setIcon(R.drawable.refr);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.item1){
			ApiFactory.getSessions().subscribe(
					CreatingObservers.getSessionList(this)
			);
		}
		return true;
	}
}
