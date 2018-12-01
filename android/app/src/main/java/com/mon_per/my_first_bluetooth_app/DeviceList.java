package com.mon_per.my_first_bluetooth_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.Set;
import java.util.ArrayList;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;


public class DeviceList extends AppCompatActivity {

    private Button buttonPaired;
    private ListView deviceList;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener(){

        public void onItemClick (AdapterView av, View v, int arg2, long arg3){
            //Get the device MAC Address, the last 17 chars in the view
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            //Make intent to start activity
            Intent i = new Intent(DeviceList.this, ledControl.class);
            //Change Activity
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl class activity
            startActivity(i);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        buttonPaired = (Button) findViewById(R.id.button_1);
        deviceList = (ListView) findViewById(R.id.list_1);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null){
            //Show error message showing that you do not have a bluetooth adapter available
            Toast.makeText(getApplicationContext(), "Bluetooth Device not available", Toast.LENGTH_LONG).show();
            //Finish app
            finish();
        }else{
            if(myBluetooth.isEnabled()){

            }else{
                //ask the user to turn on bluetooth
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }
        }
        //Create a listener for the bluetooth device
        buttonPaired.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                pairedDeviceList();
            }
        });
    }

    private void pairedDeviceList(){
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if(pairedDevices.size() > 0 ) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        }else{
            Toast.makeText(getApplicationContext(), "No paired bluetooth device found", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(myListClickListener);
    }

}
