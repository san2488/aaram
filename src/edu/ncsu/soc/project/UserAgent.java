package edu.ncsu.soc.project;

import java.util.ArrayList;

import java.util.Date;

import javax.xml.datatype.Duration;

import edu.ncsu.soc.project.UserActivityService.UserActivityBinder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class UserAgent {
	
	private static UserAgent instance;
	
	private long idealSleepTime;														//Ideal sleep time for user

	private long getIdealSleepTime() {
		return idealSleepTime;
	}

	private static Date lastActivityTime = new Date();
	
	private void setIdealSleepTime(long sleepTime) {
		idealSleepTime = sleepTime;
	}
	
	private String contactNumber = "555-123-4567";
	
	private static Context context;

	private UserActivityService uaService;
	
	private boolean isUAServiceBound = false;
	
	private static final String TAG = "UserAgent";
	
	private UserAgent() {
		Date now = new Date();
		Date eightHrsFromNow = DateUtils.addHours(now, 8);								//Set default ideal sleep time to 8 hours
		idealSleepTime = eightHrsFromNow.getTime() - now.getTime();

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
		//TODO: Add bluetooth device detection
		sendSMSMessage(contactNumber, "Hey! Please wake me up! --Sent via aaram");
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
			Log.v(TAG, "Last Activity Time: " + DateUtils.toSimpleTime(lastActivityTime));
			return lastActivityTime;
		}
		else {
			return lastActivityTime = DateUtils.addHours(new Date(), 8);		//TODO: This is a hack. Works for now
			
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
        	Log.v(TAG, "Connected to User Activity Service.");
            // We've bound to UserActivityBinder, cast the IBinder and get UserActivityService instance
        	UserActivityBinder binder = (UserActivityBinder) service;
            uaService = binder.getService();            
            lastActivityTime = uaService.getLastActivityTime();
            isUAServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
        	Log.v(TAG, "Disconnected from User Activity Service.");
            isUAServiceBound = false;
        }
    };
    
    public void stopUserActivityService() {
    	Log.v(TAG, "Stopping User Activity Service.");
//    	if(isUAServiceBound) {
//    		context.unbindService(mConnection);
//    	}
    	context.stopService(new Intent(context, UserActivityService.class));
    }
    
    public boolean isSleepDeprived() {
    	return new Date().getTime() - getLastActivityTime().getTime() < idealSleepTime;
    }
}
