package se.kth.anderslm.noninsensortest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
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
	private Graph graph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dataView = (TextView) findViewById(R.id.dataView);
		graph = (Graph) findViewById(R.id.graph);

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
			closeFile();
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
			openFile();
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

	private FileOutputStream fos = null;
	
	private void openFile() {
		try {
			fos = openFileOutput("data.txt", MODE_WORLD_READABLE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
	
	private void closeFile() {
		try {
			fos.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void saveDataCallback(int data) {
		try {
			graph.addValue(data);
			assert fos != null;
			fos.write(("" + data + "\r\n").getBytes());
			Log.e("saved","saved: " + data);
		} catch (Exception e) {
			Log.e("SaveToFile", "Could not save to file!\n" + e.getMessage());
		}
	}

	public void abortButton(View view) {
		try {
			closeFile();
			pollDataThread.closeSocket();
			Toast.makeText(this, "Thread interrupted", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
		}
	}

	public void sendDataButton(View view) {
		Toast.makeText(this, "Send ze datas!", Toast.LENGTH_SHORT).show();
		new AsyncTask<Void,Void,Void>() {
			@Override
			public Void doInBackground(Void...voids) {
				try {
					Log.e("sendDataButton","Sending data");
					FileInputStream in = openFileInput("data.txt");
					Log.e("Open", "File");
					Socket sock = new Socket("130.237.84.12",6667);
					int val;
					Log.e("EEEE", "EEE");
					Log.e("Val", "" + in.read());
					Log.e("len", "" + in.available());
					while ((val = in.read()) >= 0) {
						Log.e("asd","asd: " + val);
						sock.getOutputStream().write(val);
					}
					sock.close();
				} catch (Exception e) {
					Log.e("Error", "" + e.getMessage());
				}
				return null;
			}
		}.execute();
	}
}
