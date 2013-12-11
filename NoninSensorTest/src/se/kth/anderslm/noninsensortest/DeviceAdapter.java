package se.kth.anderslm.noninsensortest;

import java.util.Collections;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {

	private List<BluetoothDevice> btDevices = Collections.emptyList();
	private final Context context;

	public DeviceAdapter(Context context) {
		this.context = context;
	}

	public void updateDevices(List<BluetoothDevice> btDevices) {
		this.btDevices = btDevices;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return btDevices.size();
	}

	@Override
	public Object getItem(int position) {
		return btDevices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.device_list, parent, false);
		}
		
		TextView btDevice = (TextView) convertView.findViewById(R.id.btDevice);
		btDevice.setText(btDevices.get(position).getName());

		return convertView;
	}
}
