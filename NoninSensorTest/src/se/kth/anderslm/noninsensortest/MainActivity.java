package se.kth.anderslm.noninsensortest;

import java.util.ArrayList;
import java.util.Set;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
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
			pollDataThread.interrupt();
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
		noninDevice = (BluetoothDevice) deviceList.getSelectedItem();
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
	public void displayResults(final CharSequence results) {
		// Log.e("displayResults", "Callback! " + results);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dataView.setText(results);
			}
		});
	}
}
