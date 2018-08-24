package ru.scapegoats.checkers.moduls;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Locale;

import ru.scapegoats.checkers.R;
import ru.scapegoats.checkers.util.Keywords;

/**
 * Created by Андрей on 18.11.2017.
 */

public class BaseActivity extends AppCompatActivity
{
	static Context context;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		loadLocale();
		usertoken=getSharedPreferences(Keywords.User,0).getInt(Keywords.Token,-1);
		super.onCreate(savedInstanceState);
		context=this;
	}

	public static int idDialog;
	private Locale myLocale;
	public int usertoken;
	public void changeLang(String lang)
	{
		if (lang.equalsIgnoreCase(""))
			return;
		myLocale = new Locale(lang);
		savePref(lang,"Language");
		Locale.setDefault(myLocale);
		android.content.res.Configuration config = new android.content.res.Configuration();
		config.locale = myLocale;
		getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

	}
	protected MenuItem mi=null;

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu, menu);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}

	protected void miInvis()
	{
		mi.setVisible(false);
	}
	protected void miEnabT()
	{
		mi.setEnabled(true);
	}
	protected void miEnabF()
	{
		mi.setEnabled(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);return true;

		}

		return super.onOptionsItemSelected(item);
	}
	public void savePref(String str,String id)
	{
		SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(id, str);
		editor.apply();
	}
	public String loadPref(String id)
	{
		SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
		String load = prefs.getString(id, "");
		return  load;
	}
	@TargetApi(16)
	public void loadFon(LinearLayout ll)
	{
		String fon=loadPref("Fon");
		switch (fon)
		{
			case "back1":ll.setBackground(getResources().getDrawable(R.drawable.back1));break;
			case "back2":ll.setBackground(getResources().getDrawable(R.drawable.back2));break;
			case "back3":ll.setBackground(getResources().getDrawable(R.drawable.back3));break;
		}
	}

	public void loadLocale()
	{
		try
		{
			String langPref = "Language";
			SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
			String language = prefs.getString(langPref, "");
			Log.e("SHO?",language);
			changeLang(language);
		}
		catch (Exception e)
		{
			Log.e("SHO?",e.toString());
		}
	}
	private static ProgressDialog pd;

	public static void showProgress() {
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Загрузка...");
		pd.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
	}
	public static void hideProgress() {
		pd.dismiss();
	}

}
