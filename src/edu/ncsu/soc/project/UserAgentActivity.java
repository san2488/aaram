package edu.ncsu.soc.project;

import java.io.Console;
import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class UserAgentActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1;
	
	private String contactNumber = "000-000-0000";
	
	private ArrayList<String> nearByDevices = new ArrayList<String>();

	@Override
	protected void onStart() {
		super.onStart();
		Intent bluetoothIntent = new Intent(this, UserBluetoothService.class);
		startService(bluetoothIntent);
		
//		Intent serviceIntent = new Intent();
//		serviceIntent.setAction("edu.ncsu.soc.project.UserBluetoothService");
//		startService(serviceIntent);
	};
	

	private void sendSms(String phoneNumber,String message, boolean isBinary)
	{
		PendingIntent pi = PendingIntent.getActivity(this, 0,
	            new Intent(this, UserBluetoothService.class), 0);                
	        SmsManager sms = SmsManager.getDefault();
	        sms.sendTextMessage(phoneNumber, null, message, pi, null);  
	}
	
}
