package edu.ncsu.soc.project;

import java.util.ArrayList;

import java.util.Date;

import javax.xml.datatype.Duration;

import edu.ncsu.soc.project.UserActivityService.UserActivityBinder;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

public class UserAgent {
	
	private static UserAgent instance;
	
	private final int defaultIdealSleepTime = 8;
	
	private int idealSleepTime;														//Ideal sleep time for user

	private int getIdealSleepTime() {
		return idealSleepTime;
	}

	private static Date lastActivityTime = new Date();
	
	private final String defaultContactNumber = "555-123-4567";
	
	private String contactNumber = defaultContactNumber;
	
	private static Context context;

	private UserActivityService uaService;
	
	private boolean isUAServiceBound = false;
	
	private static final String TAG = "UserAgent";
	
	private UserAgent() {

		SharedPreferences appPrefs = 
        		context.getSharedPreferences("edu.ncsu.soc.project_preferences", Context.MODE_PRIVATE);    	
        idealSleepTime = appPrefs.getInt("idealSleepTime", defaultIdealSleepTime);
        contactNumber = appPrefs.getString("predefinedContact", defaultContactNumber);
        
		Intent intent = new android.content.Intent(context, UserActivityService.class);	
		context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	public static void setContext(Context context) {
		UserAgent.context = context;
	}
	
	public static UserAgent getInstance() {
		if(instance == null) {
			return instance = new UserAgent();
		}
		return instance;
	}
	
	public void takeSnoozeLimitAction(){
		// Yuck! but need a quick fix to update time from preferences
		SharedPreferences appPrefs = 
        		context.getSharedPreferences("edu.ncsu.soc.project_preferences", Context.MODE_PRIVATE);
        contactNumber = appPrefs.getString("predefinedContact", defaultContactNumber);
		
		ArrayList<BluetoothDevice> devices = UserBluetoothDetection.getInstance().getConnectedBluetoothDevices();
		if(devices == null) {
			sendSMSMessage(contactNumber, "Hey! I need your help. Please wake me up! --Sent via aaram");
			return;
		}
		boolean sentOne = false;
		for(BluetoothDevice device : devices) {
			String phNum = getPhoneNumber(device.getName());
			if(phNum != null)  {
				sendSMSMessage(phNum, "Hey! Please wake me up! --Sent via aaram");
				sentOne = true;
			}
		}
		if(!sentOne) {
			sendSMSMessage(contactNumber, "Hey! I need your help. Please wake me up! --Sent via aaram");
		}
	}

	private void sendSMSMessage(String phoneNumber,String message)
	{                
	        SmsManager sms = SmsManager.getDefault();
	        sms.sendTextMessage(phoneNumber, null, message, null, null);  
	        Log.i(TAG, "Message Sent to " + phoneNumber);
	}

	public Date getLastActivityTime() {
		if(isUAServiceBound) {
			lastActivityTime = uaService.getLastActivityTime();
			Log.i(TAG, "Last Activity Time: " + DateUtils.toSimpleTime(lastActivityTime));
			return lastActivityTime;
		}
		else {
			return lastActivityTime = DateUtils.addHours(new Date(), -8);		//TODO: This is a hack. Works for now
			
//			Intent intent = new android.content.Intent(context, UserActivityService.class);	
//			context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//			return uaService.getLastActivityTime();
		}
	}
	
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
        	Log.i(TAG, "Connected to User Activity Service.");
            // We've bound to UserActivityBinder, cast the IBinder and get UserActivityService instance
        	UserActivityBinder binder = (UserActivityBinder) service;
            uaService = binder.getService();            
            lastActivityTime = uaService.getLastActivityTime();
            isUAServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
        	Log.i(TAG, "Disconnected from User Activity Service.");
            isUAServiceBound = false;
        }
    };
    
    public void stopUserActivityService() {
    	Log.i(TAG, "Stopping User Activity Service.");
//    	if(isUAServiceBound) {
//    		context.unbindService(mConnection);
//    	}
    	context.stopService(new Intent(context, UserActivityService.class));
    }
    
    public boolean isSleepDeprived() {
    	return DateUtils.addHours(getLastActivityTime(), getIdealSleepTime()).after(new Date()) ;
    }
    
    public String getPhoneNumber(String name) {
		ContentResolver contentResolver = context.getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER };
		String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?";
		String[] selectionArguments = { name };
		Cursor people = contentResolver.query(uri, projection, selection,
				selectionArguments, null);

		int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		if (people != null) {
			while (people.moveToNext()) {
				String number = people.getString(indexNumber);
				people.close();
				return number;
			}
		}
		people.close();
		return null;
	}
}
