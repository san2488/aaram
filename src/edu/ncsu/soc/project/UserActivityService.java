package edu.ncsu.soc.project;

import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

//http://developer.android.com/guide/components/bound-services.html
public class UserActivityService extends Service implements SensorEventListener{

    private final IBinder mBinder = new UserActivityBinder();			// Binder given to clients

    private final int ACCEL_THRESHOLD = 1;								//Limit at which the device is determined to have 'moved'
    
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	
	private static Date lastActivityTime;								//Time at which user last interacted with device
	
	private final static String TAG = "UserActivityService";

	@Override
	public void onCreate() {
		super.onCreate();
		lastActivityTime = new Date();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
	}
	
    @Override
    public IBinder onBind(Intent intent) {
    	mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    	mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    	boolean hasAccelerometer = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    	if(!hasAccelerometer) {
    		Log.i(TAG, "Accelerometer unavailable. Relying on Screen lock alone for user activity.");
    	}
        return mBinder;
    }

    public Date getLastActivityTime() {
    	return lastActivityTime;
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Don't need this
		
	}

	//http://developer.android.com/guide/topics/sensors/sensors_motion.html#sensors-motion-accel
	@Override
	public void onSensorChanged(SensorEvent event) {
		  final float alpha = (float) 0.8;
		  float[] gravity = new float[3];
		  float[] linear_acceleration = new float[3];
		  // Isolate the force of gravity with the low-pass filter.
		  gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
		  gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
		  gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

		  // Remove the gravity contribution with the high-pass filter.
		  linear_acceleration[0] = event.values[0] - gravity[0];
		  linear_acceleration[1] = event.values[1] - gravity[1];
		  linear_acceleration[2] = event.values[2] - gravity[2];

		if(Math.abs(linear_acceleration[0]) > ACCEL_THRESHOLD || 
				Math.abs(linear_acceleration[1]) > ACCEL_THRESHOLD|| Math.abs(linear_acceleration[2]) > ACCEL_THRESHOLD) {
			lastActivityTime = new Date();
		}
		
	}
	
	//http://thinkandroid.wordpress.com/2010/01/24/handling-screen-off-and-screen-on-intents/
	public class ScreenReceiver extends BroadcastReceiver {
		 
	    private boolean screenOff;
	 
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
	        	if(!screenOff) lastActivityTime = new Date();
	            screenOff = true;
	        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
	        	if(screenOff) lastActivityTime = new Date();
	            screenOff = false;
	        }
//	        Intent i = new Intent(context, UserActivityService.class);
//	        i.putExtra("screen_state", screenOff);
//	        context.startService(i);
	    }
	 
	}

	public class UserActivityBinder extends Binder {
		UserActivityService getService() {
            // Return this instance of LocalService so clients can call public methods
            return UserActivityService.this;
        }
    }
}
