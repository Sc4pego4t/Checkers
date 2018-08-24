package ru.scapegoats.checkers.activity.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import ru.scapegoats.checkers.activity.main.Main;
import ru.scapegoats.checkers.R;
import ru.scapegoats.checkers.moduls.BaseActivity;

/**
 * Created by Андрей on 06.11.2017.
 */

public class BluetoothMenu extends BaseActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "MYAPP" ;
    private static final String NAME = "GG" ;
    int REQUEST_ENABLE_BT=1;
    int resID = R.id.lv2;
    UUID MY_UUID=UUID.fromString("deea44c7-a180-4898-9527-58db0ed34683");
    ArrayAdapter listAd;
    ArrayAdapter arad2;
    Context context=this;
    BluetoothAll ba;
    ListView lv,lv2;
    Button b;
    ArrayList<BluetoothDevice> devices=new ArrayList<BluetoothDevice>();
    ArrayList<BluetoothDevice> connect=new ArrayList<BluetoothDevice>();
    BluetoothAdapter mBluetoothAdapter;
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.bar, menu);
        mi=menu.findItem(R.id.item1);
        mi.setIcon(R.drawable.refr);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Activity activity=this;
        switch (item.getItemId())
        {
            case R.id.item1: refresh();return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            loadLocale();
            super.onCreate(savedInstanceState);
            setContentView(R.layout.bluetooth_search);
            ba=new BluetoothAll(this);
            lv=(ListView)findViewById(R.id.lv);
            lv2=(ListView)findViewById(R.id.lv2);
            listAd = new ArrayAdapter<>(this, R.layout.fill);
            arad2 = new ArrayAdapter<>(this, R.layout.fill);
            lv.setAdapter(listAd);
            lv2.setAdapter(arad2);
            lv.setOnItemClickListener(this);
            lv2.setOnItemClickListener(this);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(this,"Ваше устройство не поддерживает Bluetooth",Toast.LENGTH_LONG).show();
                startActivity(new Intent(this,Main.class));
            }
            else {
                new StartThread().start();
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent discoverableIntent=new
                            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,0);
                    startActivity(discoverableIntent);
                }
            }

        }
        catch (Exception e) {
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    private class StartThread extends Thread
    {
        boolean iterrupt=true;
        public void run() {
            while(iterrupt)
            {
                if (mBluetoothAdapter.isEnabled())
                {
                    ba.startAccept();
                    iterrupt=false;
                }
            }
        }

    }
    public void back(View view)
    {
        Intent intent=new Intent(this,Main.class);
        startActivity(intent);
    }
    public void refresh()
    {
        Set<BluetoothDevice> pairedDevices= mBluetoothAdapter.getBondedDevices();
        arad2.clear();
        if(pairedDevices.size()>0){
            for(BluetoothDevice device: pairedDevices)
            {

                arad2.add(device.getName()+"\n"+ device.getAddress());
                devices.add(device);
            }
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        mBluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver mReceiver=new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action= intent.getAction();
            // Когда найдено новое устройство
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Получаем объект BluetoothDevice из интента
                BluetoothDevice device= intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //Добавляем имя и адрес в array adapter, чтобы показвать в ListView
                listAd.add(device.getName()+"\n"+ device.getAddress());
                connect.add(device);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mReceiver);
        }
        catch (Exception e)
        {
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Intent intent=new Intent(this,Main.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
       // unregisterReceiver(mReceiver);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        try
        {
            if(parent.getId()==resID)
            {
                //Toast.makeText(this, parent.getId() + "//" + resID, Toast.LENGTH_LONG).show();
                //new ConnectThread(devices.get(position)).start();
                ba.startConnect(devices.get(position));
            }
            else
            {
                ba.startConnect(connect.get(position));
                //new ConnectThread(connect.get(position)).start();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
        }

    }
}
