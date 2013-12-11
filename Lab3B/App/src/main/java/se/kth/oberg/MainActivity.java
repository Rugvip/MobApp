package se.kth.oberg;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;
import java.util.UUID;

public class MainActivity extends ActionBarActivity {
    private BluetoothSocket btSocket;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice btDevice;
    private int REQ_ENABLE_BT = 1;
    private BluetoothDevice[] pairedDevices;
    private ListView deviceList;
    private AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.e("onItemClick", "asd " + pairedDevices[i].getAddress());
            connectDevice();
        }
    };

    private void connectDevice() {
        btSocket = btDevice.createRfcommSocketToServiceRecord(new UUID())
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                break;
            case RESULT_CANCELED:
                break;
        }
        Log.e("onActivityResult", "requestCode: " + requestCode + " resultcode: " + resultCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceList = (ListView) findViewById(R.id.deviceList);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceList.setOnItemClickListener(listClick);


        Set<BluetoothDevice> deviceSet = btAdapter.getBondedDevices();

        if (btAdapter != null) {
            if (!btAdapter.isEnabled()) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQ_ENABLE_BT);
            }
            pairedDevices = deviceSet.toArray(new BluetoothDevice[deviceSet.size()]);
            deviceList.setAdapter(new ArrayAdapter<BluetoothDevice>(this,android.R.layout.simple_list_item_1,pairedDevices));

        } else {
            Toast.makeText(this, "Could not find BluetoothAdapter", Toast.LENGTH_LONG);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
