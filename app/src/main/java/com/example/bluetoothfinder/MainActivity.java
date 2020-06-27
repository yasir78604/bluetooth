package com.example.bluetoothfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    TextView searchTextView;
    Button searchButton;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> addresses = new ArrayList<>();
    ArrayAdapter adapter;

    BluetoothAdapter bluetoothAdapter;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                searchTextView.setText("Finished");
                searchButton.setEnabled(true);
            }else if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress();
                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
                Log.i("Device Found","Name : " + name + " Address : " + address + " rssi : " + rssi);

                if (!addresses.contains(address)){
                    addresses.add(address);
                    String deviceString = "";
                    if (name == null || name.equals("")){
                        deviceString = address + " -rssi " + rssi + "dBm";
                    }else {
                        deviceString = name + " -rssi " + rssi + "dBm";
                    }
                    arrayList.add(deviceString);
                    adapter.notifyDataSetChanged();
                }


            }

        }
    };

    public void searchClicked(View view){
        searchTextView.setText("Searching....");
        searchButton.setEnabled(false);
        arrayList.clear();
        addresses.clear();
        bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        searchTextView = findViewById(R.id.searchTextView);
        searchButton = findViewById(R.id.searchButton);

        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(adapter);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver,intentFilter);


    }
}