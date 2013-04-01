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

	private void setIdealSleepTime(long sleepTime) {
		idealSleepTime = sleepTime;
	}
	
	private String contactNumber = "000-000-0000";
	
	private static Context context;

	private UserActivityService uaService;
	
	private boolean isUAServiceBound = false;
	
	private static final String TAG = "edu.ncsu.soc.project.UserAgent";
	
	private UserAgent() {
		Date now = new Date();
		Date eightHrsFromNow = DateUtils.addHours(now, 8);								//Set ideal sleep time to 8 hours
		setIdealSleepTime(eightHrsFromNow.getTime() - now.getTime());

		Intent intent = new android.content.Intent(context, UserActivityService.class);	
		context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	public void setContext(Context context) {
		UserAgent.context = context;
	}
	
	public static UserAgent getInstance() {
		if(instance == null) {
			return instance = new UserAgent();
		}
		return instance;
	}
	
	public void takeSnoozeLimitAction(){
		sendSMS(contactNumber, "Hey! Please wake me up! --Sent via aaram");
	}

	private void sendSMS(String phoneNumber,String message)
	{                
	        SmsManager sms = SmsManager.getDefault();
	        sms.sendTextMessage(phoneNumber, null, message, null, null);  
	}

	public Date getLastActivityTime() {
		if(isUAServiceBound)
			return uaService.getLastActivityTime();
		else {
			Intent intent = new android.content.Intent(context, UserActivityService.class);	
			context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
			return uaService.getLastActivityTime();
		}
	}
	
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
        	Log.v(TAG, "Connect User Activity Service.");
            // We've bound to UserActivityBinder, cast the IBinder and get UserActivityService instance
        	UserActivityBinder binder = (UserActivityBinder) service;
            uaService = binder.getService();
            isUAServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            isUAServiceBound = false;
        }
    };
    
    public static void stopUserActivityService() {
    	Log.v(TAG, "Stopping User Activity Service.");
    	context.stopService(new Intent(context, UserActivityService.class));
    }
    
    public boolean isSleepDeprived() {
    	return new Date().getTime() - getLastActivityTime().getTime() < idealSleepTime;
    }
}
