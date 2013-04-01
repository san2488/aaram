package edu.ncsu.soc.project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.IntentService;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsManager;
import android.util.Log;

public class BluetoothService extends IntentService {
	public BluetoothService(String name) {
		super(name);
	}

	public BluetoothService() {
		this("aaram");
	}

	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	private final String TAG = "UserBluetooth";
	private static final UUID MY_UUID_INSECURE = 
	        UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

	@Override
	public void onCreate() {
		super.onCreate();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Log.w(TAG, "This device does not support Bluetooth.");
			stopSelf();
			return;
		}
//		android.telephony.TelephonyManager tManager = (android.telephony.TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		if (!mBluetoothAdapter.isEnabled()) {
			Log.w(TAG, "Bluetooth is off.");
			stopSelf();
			return START_NOT_STICKY;
		}
		broadCastToPairedDevices();
		return START_STICKY;
	}

	public boolean broadCastToPairedDevices() {
		boolean sentOne = false;
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				String phNum = findContact(device.getName());
				if(isConnected(device)){
//					String phNum = findContact("SoC Dummy");
					sendSms(phNum, "Hey! Please wake me up! --Sent via aaram 'Never be late again'", false);
				}
			}
		}
		return sentOne;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		broadCastToPairedDevices();
		stopSelf();

	}

	private File getTempFile(String filename) {
		return new File(getFilesDir(), filename);
		// String string = "Hello world!";
		// FileOutputStream outputStream;
		//
		// try {
		// outputStream = new FileOutputStream(file);
		// outputStream.write(string.getBytes());
		// outputStream.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	public String findContact(String name) {
		ContentResolver contentResolver = getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER };
		String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?";
		String[] selectionArguments = { name };
		Cursor people = contentResolver.query(uri, projection, selection,
				selectionArguments, null);

		int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		if (people != null) {
			while (people.moveToNext()) {
//				 System.out.println(people.getString(indexNumber));
//				 System.out.println(people.getString(indexName));
				String number = people.getString(indexNumber);
				people.close();
				return number;
			}
		}
		people.close();
		return "0000000000";
	}

	private boolean isConnected(BluetoothDevice device){
		//http://stackoverflow.com/questions/9282867/android-bluetooth-accept-connect-with-already-paired-devices
		//http://stackoverflow.com/questions/6760102/connecting-to-a-already-paired-bluetooth-device
		BluetoothDevice actualDevice = mBluetoothAdapter.getRemoteDevice(device.getAddress());
		mBluetoothAdapter.cancelDiscovery();
		BluetoothSocket socket = null;
		try {
			socket = actualDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
			socket.connect();
//			Method m = actualDevice.getClass().getMethod("createRfcommSocket",
//	                new Class[] { int.class });
//	        BluetoothSocket mySocket = (BluetoothSocket) m.invoke(actualDevice, Integer.valueOf(1));
		} 
		/* Connection failed */
//		catch (IllegalArgumentException e) {
////			e.printStackTrace();
//			closeSocket(socket);
//			return false;
//		}
//		catch(NoSuchMethodException e) {
//			closeSocket(socket);
//			return false;
//		}
//		catch(InvocationTargetException e) {
//			closeSocket(socket);
//			return false;
//		}
//		catch(IllegalAccessException e) {
//			closeSocket(socket);
//			return false;
//		}
		catch(IOException e) {
			closeSocket(socket);
			return false;
		}
		return true;
	}
	private void closeSocket(BluetoothSocket socket) {
		try {
			socket.close();
		}
		catch(IOException e1) {
			
		}
	}
	private void sendSms(String phoneNumber,String message, boolean isBinary)
	{
		PendingIntent pi = PendingIntent.getActivity(this, 0,
	            new Intent(this, BluetoothService.class), 0);                
	        SmsManager sms = SmsManager.getDefault();
	        sms.sendTextMessage(phoneNumber, null, message, pi, null);  
	}
	// public ArrayList<String> getNearbyBluetoothUserList() {
	// Set<BluetoothDevice> pairedDevices =
	// mBluetoothAdapter.getBondedDevices();
	// ArrayList<String> availableDevices = new ArrayList<String>();
	// // If there are paired devices
	// if (pairedDevices.size() > 0) {
	// // Loop through paired devices
	// for (BluetoothDevice device : pairedDevices) {
	// availableDevices.add(device.getName());
	// }
	// }
	// return availableDevices;
	// }
}
