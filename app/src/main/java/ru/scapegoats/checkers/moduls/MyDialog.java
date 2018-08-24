package ru.scapegoats.checkers.moduls;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Dialog;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import ru.scapegoats.checkers.activity.bluetooth.BluetoothMenu;
import ru.scapegoats.checkers.activity.game.Game;
import ru.scapegoats.checkers.activity.internet.InternetMenu;
import ru.scapegoats.checkers.R;

/**
 * Created by Андрей on 06.12.2017.
 */

public class MyDialog extends DialogFragment implements DialogInterface.OnClickListener
{

	final String LOG_TAG = "ALOOOOOOOOOOOOOO";

	public int usertoken;


	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
		switch(BaseActivity.idDialog)
		{
			case 1: adb.setTitle(getString(R.string.s9)).setItems(R.array.modes, this);break;
			case 2: adb.setTitle(getString(R.string.s10)).setItems(R.array.con, this);break;
		}
		return adb.create();
	}

	public void onClick(DialogInterface dialog, int which)
	{
		switch(BaseActivity.idDialog)
		{
			case 1: {
				Intent intent = new Intent(getActivity(), Game.class);
				intent.putExtra(getString(R.string.key1), which);
				startActivity(intent);
				break;
			}
			case 2: {
				Intent intent=null;
				boolean start=false;
				switch(which)
				{
					case 0:
						Log.e("click","internet");
						if(usertoken!=-1) {
							intent = new Intent(getActivity(), InternetMenu.class);
							start = true;
						} else {
							Toast.makeText(getActivity(),"Авторизуйтесь",Toast.LENGTH_LONG).show();
						}
						break;
					case 1:
						intent=new Intent(getActivity(),BluetoothMenu.class);
						start=true;
						break;
				}
				if(start)
					startActivity(intent);
				break;
			}
		}
	}

	public void onDismiss(DialogInterface dialog)
	{
		super.onDismiss(dialog);
		Log.d(LOG_TAG, "Dialog 2: onDismiss");
	}

	public void onCancel(DialogInterface dialog)
	{
		super.onCancel(dialog);
		Log.d(LOG_TAG, "Dialog 2: onCancel");
	}
}