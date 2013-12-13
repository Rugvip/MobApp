package se.kth.anderslm.noninsensortest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements PollCallback {

	public static final int REQUEST_ENABLE_BT = 42;
	private PollDataTask pollDataThread;
	private BluetoothAdapter bluetoothAdapter = null;
	private BluetoothDevice noninDevice = null;

	private ListView deviceList;
	private DeviceAdapter deviceAdapter = new DeviceAdapter(this);

	private OnItemClickListener listClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int i,
				long l) {
			noninDevice = (BluetoothDevice) deviceAdapter.getItem(i);
			Toast.makeText(MainActivity.this, "Current device: " + noninDevice.getName(), Toast.LENGTH_SHORT).show();
		}
	};

	private TextView dataView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dataView = (TextView) findViewById(R.id.dataView);

		deviceList = (ListView) findViewById(R.id.deviceList);
		deviceList.setOnItemClickListener(listClick);

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			showToast("This device do not support Bluetooth");
			this.finish();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		dataView.setText(R.string.data);
		initBluetooth();
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			pollDataThread.closeSocket();
			Toast.makeText(this, "Thread interrupted", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		// TODO: stop ongoing BT communication
	}

	public void onPollButtonClicked(View view) {
		if (noninDevice != null) {
			Log.e("onPollbuttoncicked", "About to start thread");
			pollDataThread = new PollDataTask(this, noninDevice);
			pollDataThread.start();
			Log.e("Thread", pollDataThread.toString());
		} else {
			showToast("No Nonin sensor found");
		}
	}

	protected void displayData(CharSequence data) {
		dataView.setText(data);
	}

	private void initBluetooth() {
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			getNoninDevice();
		}
	}

	// callback for BluetoothAdapter.ACTION_REQUEST_ENABLE (called via
	// initBluetooth)
	protected void onActivityResult(int requestCode, int resultCode,
			Intent result) {
		super.onActivityResult(requestCode, resultCode, result);

		if (requestCode == REQUEST_ENABLE_BT) {
			if (bluetoothAdapter.isEnabled()) {
				getNoninDevice();
			} else {
				showToast("Bluetooth is turned off.");
			}
		}
	}

	private void getNoninDevice() {
		noninDevice = null;
		Set<BluetoothDevice> pairedBTDevices = bluetoothAdapter
				.getBondedDevices();

		if (pairedBTDevices.size() > 0) {

			ArrayList<BluetoothDevice> temp = new ArrayList<BluetoothDevice>();

			for (BluetoothDevice device : pairedBTDevices) {
				if (device.getName().contains("Nonin")) {
					temp.add(device);
				}
			}
			deviceAdapter.updateDevices(temp);
			deviceList.setAdapter(deviceAdapter);
		}
	}

	void showToast(final CharSequence msg) {
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		toast.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void resultCallback(final String results) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dataView.setText(results);
			}
		});
	}

	@Override
	public void saveDataCallback(String results) {
		FileOutputStream fos = null;
		try {
			fos = openFileOutput("data.txt", MODE_WORLD_READABLE ); 
			fos.write(results.getBytes());
			fos.close();
			Log.e("saved","saved: " + results.getBytes());
		} catch (Exception e) {
			Log.e("SaveToFile", "Could not save to file!\n" + e.getMessage());
			try {
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void abortButton(View view) {
		try {
			pollDataThread.closeSocket();
			Toast.makeText(this, "Thread interrupted", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
		}
	}

	public void sendDataButton(View view) {
		Toast.makeText(this, "Send ze datas!", Toast.LENGTH_SHORT).show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Log.e("sendDataButton","Sending data");
					FileInputStream in = openFileInput("data.txt");
					Socket sock = new Socket("130.229.184.54",6667);
					int val;
					while ((val = in.read()) >= 0) {
						sock.getOutputStream().write(val);
						Log.e("asd","asd: " + val);
					}
					sock.close();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
