package com.mon_per.my_first_bluetooth_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


public class ledControl extends AppCompatActivity {

    private Button btnStop, btnW, btnA, btnS, btnD, btnDis, btnDbg;
    private SeekBar brightness;
    private String address = null;
    private TextView lumn, debug_text;
    private ProgressDialog progress;
    private int carSpeed = 50;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBTConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private void msg(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void turnCounterClockwise(){
        if(btSocket != null){
            try{
                String sendString = "R|" + (carSpeed) +":L|" + (-1*carSpeed) + ":";
                btSocket.getOutputStream().write(sendString.getBytes());
            }catch(IOException e){
                msg("Error");
            }
        }
    }

    private void debug(){
        if(btSocket != null){
            try{
                String sendString = "D";
                btSocket.getOutputStream().write(sendString.getBytes());
            }catch(IOException e){
                msg("Error");
            }
        }
    }

    private void turnClockwise(){
        if(btSocket != null){
            try{
                String sendString = "R|" + (-1*carSpeed) +":L|" + (carSpeed) + ":";
                btSocket.getOutputStream().write(sendString.getBytes());
            }catch(IOException e){
                msg("Error");
            }
        }
    }

    private void moveForwards(){
        if(btSocket != null){
            try{
                String sendString = "R|" + (carSpeed) +":L|" + (carSpeed) + ":";
                btSocket.getOutputStream().write(sendString.getBytes());
            }catch(IOException e){
                msg("Error");
            }
        }
    }

    private void moveBackwards(){
        if(btSocket != null){
            try{
                String sendString = "R|" + (-1*carSpeed) +":L|" + (-1*carSpeed) + ":";
                btSocket.getOutputStream().write(sendString.getBytes());
            }catch(IOException e){
                msg("Error");
            }
        }
    }

    private void stopMoving(){
        if(btSocket != null){
            try{
                String sendString = "R|" + (0) +":L|" + (0) + ":";
                btSocket.getOutputStream().write(sendString.getBytes());
            }catch(IOException e){
                msg("Error");
            }
        }
    }

    private void setSpeed(boolean fromUser, int progress){
        if(fromUser == true){
            lumn.setText(String.valueOf(progress));
            carSpeed = progress;
        }
    }

    private void disconnect(){
        if(btSocket != null){
            try{
                btSocket.close();
            }catch(IOException e){
                msg("Error");
            }
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control);
        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);
        //Set the widgets
        btnStop = (Button) findViewById(R.id.button_turn_off);
        btnW = (Button) findViewById(R.id.button_w);
        btnA = (Button) findViewById(R.id.button_a);
        btnS = (Button) findViewById(R.id.button_s);
        btnD = (Button) findViewById(R.id.button_d);
        btnDis = (Button) findViewById(R.id.button_disconnect);
        btnDbg = (Button) findViewById(R.id.button_debug);
        lumn = (TextView) findViewById(R.id.textView_led_control);
        debug_text = (TextView) findViewById(R.id.textView_debug);
        brightness = (SeekBar) findViewById(R.id.seekBar_brightness);

        btnW.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                moveForwards();
            }
        });
        btnS.setOnClickListener(new OnClickListener(){
           @Override
           public void onClick(View v){
               moveBackwards();
           }
        });
        btnA.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                turnCounterClockwise();
            }
        });
        btnD.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                turnClockwise();
            }
        });

        btnStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMoving();
            }
        });
        btnDis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                disconnect();
            }
        });
        btnDbg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                debug();
            }
        });

        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                setSpeed(fromUser, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){

            }

        });

        new ConnectBT().execute();
    }


    //Private class that handles the bluetooth connection
    //UI thread
    private class ConnectBT extends AsyncTask<Void, Void, Void>{
        private boolean connectSuccess = true; //If its in here its almost connected

        @Override
        protected void onPreExecute(){
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");
        }

        @Override
        //While the process dialog is shown, connect in the background
        protected Void doInBackground(Void... devices){
            try{
                if(btSocket == null || !isBTConnected){
                    //Get the bluetooth device
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    //Connects to a devices address and checks if its available
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    //Create a RFComm connection
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            }catch(IOException e){
                connectSuccess = false;
            }
            return null;
        }


        @Override
        //After the doInBackground checks if everything is fine
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            if(!connectSuccess){
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }else{
                msg("Connected.");
                isBTConnected = true;
                progress.dismiss();
                new ConnectedThread(btSocket).start();
            }
            progress.dismiss();
        }
    }


    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[80];  // buffer store for the stream
            int bytes; // bytes returned from read()
            int i,j;
            String debug_content = "";
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    debug_content = (new String(buffer));
                    i = debug_content.indexOf("Right");
                    j = debug_content.indexOf("Comm", i);
                    if(i > 0 && j > i) {
                        debug_content = debug_content.substring(i, j-1);
                        debug_text.setText(debug_content);
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
}
