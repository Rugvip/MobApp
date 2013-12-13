package se.kth.anderslm.noninsensortest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

class PollDataTask extends Thread {
	private PollCallback callback;
	// The byte sequence to set sensor to a basic, and obsolete, format
	// private static final byte[] FORMAT = { 0x44, 0x31 };
	private static final byte[] FORMAT = { 0x02, 0x70, 0x04, 0x02, 0x02, 0x01, 0x79, 0x03 };
	private static final byte ACK = 0x06; // ACK from Nonin sensor

	private static final UUID STANDARD_SPP_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private BluetoothDevice noninDevice;
	private BluetoothAdapter adapter;
	private BluetoothSocket socket = null;

	protected PollDataTask(PollCallback activity, BluetoothDevice noninDevice) {
		callback = (PollCallback) activity;
		this.noninDevice = noninDevice;
		this.adapter = BluetoothAdapter.getDefaultAdapter();
	}

	private static int FRAME_SIZE = 5;
	private static int PACKET_SIZE = 25 * FRAME_SIZE;

	public void run() {
		String output = "";

		// an ongoing discovery will slow down the connection
		adapter.cancelDiscovery();

		try {
			socket = noninDevice
					.createRfcommSocketToServiceRecord(STANDARD_SPP_UUID);
			socket.connect();

			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();

			os.write(FORMAT);
			os.flush();
			Log.e("PollDataTask", "format!");
			byte[] reply = new byte[1];
			is.read(reply);
			Log.e("PollDataTask", "reply: " + Integer.toHexString(reply[0]));
			if (reply[0] == ACK) {
				Log.e("PollDataTask", "Ack ACKccepted");
				byte[] frame = new byte[FRAME_SIZE];
				while (!isInterrupted()) {
					output = "";
					is.read(frame);
					int pleth = unsignedByteToInt(frame[2]) & 0x7F;
					if ((frame[1] & 1) != 0) {
						int PR_MSB = unsignedByteToInt(frame[3]) & 3;
						is.read(frame);
						int PR_LSB = unsignedByteToInt(frame[3]) & 0x7F;
						is.read(frame);
						int SP_O2 = unsignedByteToInt(frame[3]) & 0x7F;

						Log.e("PollDataTask", "Datas: " + PR_MSB + " " + PR_LSB);
						output = ((PR_MSB << 7) + PR_LSB) + ":" + SP_O2;
						callback.resultCallback(output);
					}
					callback.saveDataCallback(pleth + ":" + output + "\r\n");
				}
			}
		} catch (Exception e) {
		} finally {
			try {
				Log.e("PollDataTask", "Finally thread.");
				if (socket != null) {
					socket.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public void closeSocket() throws IOException {
		socket.close();
	}

	private int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}
}
