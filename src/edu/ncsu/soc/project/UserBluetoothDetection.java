package edu.ncsu.soc.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class UserBluetoothDetection {
	private static UserBluetoothDetection instance;
	
	private BluetoothAdapter mBluetoothAdapter = null;
	private final String TAG = "UserBluetoothDetection";

	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;


	// Name for the SDP record when creating server socket
	private static final String NAME_SECURE = "BluetoothChatSecure";
	private static final String NAME_INSECURE = "BluetoothChatInsecure";

	// Unique UUID for this application
	private static final UUID MY_UUID_SECURE = UUID
			.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	private static final UUID MY_UUID_INSECURE = UUID
//			.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");			// I don't know why this doesn't work
			.fromString("00001105-0000-1000-8000-00805F9B34FB");			// I don't know why this does work
	
	private UserBluetoothDetection() {
		
	}
	
	private boolean isBluetoothAvailable() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Log.i(TAG, "Bluetooth unavailable.");
			return false;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Log.i(TAG, "Bluetooth not enabled.");
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @return		List of connected bluetooth devices if available, null otherwise
	 */
	public ArrayList<BluetoothDevice> getConnectedBluetoothDevices() {
		if(!isBluetoothAvailable()) {
			return null;
		}
		ArrayList<BluetoothDevice> connectedDevices = new ArrayList<BluetoothDevice>();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				if(device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.PHONE_SMART && canConnect(device)){
					connectedDevices.add(device);
				}
			}
		}
		return connectedDevices.size() > 0 ? connectedDevices : null;
	}
	
	public boolean canConnect(BluetoothDevice device) {
		BluetoothSocket socket;
		try {
			socket = device
					.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);

		} catch (IOException e) {
			Log.e(TAG, "Socket creation failed.", e);
			return false;
		}
		mBluetoothAdapter.cancelDiscovery();
		try {
			// This is a blocking call and will only return on a
			// successful connection or an exception
			socket.connect();
		} catch (IOException e) {
			// Close the socket
			try {
				socket.close();
			} catch (IOException e2) {
				Log.e(TAG, "Socket closure failed.", e2);				
			}
			return false;
		}
		return true;
	}
	
	public static UserBluetoothDetection getInstance() {
		if(instance == null) {
			return instance = new UserBluetoothDetection();
		}
		return instance;
	}
	

//	private ConnectThread mConnectThread;
//	private class ConnectThread extends Thread {
//		private final BluetoothSocket mmSocket;
//		private final BluetoothDevice mmDevice;
//		private String mSocketType;
//
//		public ConnectThread(BluetoothDevice device, boolean secure) {
//			mmDevice = device;
//			BluetoothSocket tmp = null;
//			mSocketType = secure ? "Secure" : "Insecure";
//
//			// Get a BluetoothSocket for a connection with the
//			// given BluetoothDevice
//			try {
//				if (secure) {
//					tmp = device
//							.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
//				} else {
//					tmp = device
//							.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
//				}
//			} catch (IOException e) {
//				Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
//			}
//			mmSocket = tmp;
//		}
//
//		public void run() {
//			Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
//			setName("ConnectThread" + mSocketType);
//
//			// Always cancel discovery because it will slow down a connection
//			mBluetoothAdapter.cancelDiscovery();
//
//			// Make a connection to the BluetoothSocket
//			try {
//				// This is a blocking call and will only return on a
//				// successful connection or an exception
//				mmSocket.connect();
//			} catch (IOException e) {
//				// Close the socket
//				try {
//					mmSocket.close();
//				} catch (IOException e2) {
//					Log.e(TAG, "unable to close() " + mSocketType
//							+ " socket during connection failure", e2);
//				}
//				// connectionFailed();
//				return;
//			}
//			Log.i(TAG, "Bluetooth device connected.");
//			// Reset the ConnectThread because we're done
//			synchronized (this) {
//				mConnectThread = null;
//			}
//		}
//
//		public void cancel() {
//			try {
//				mmSocket.close();
//			} catch (IOException e) {
//				Log.e(TAG, "close() of connect " + mSocketType
//						+ " socket failed", e);
//			}
//		}	
//	}

}
