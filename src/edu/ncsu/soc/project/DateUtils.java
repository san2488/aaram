package edu.ncsu.soc.project;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/** date utility methods
 * 
 * @author snellenbach
 *
 */
public class DateUtils {
	
	private static String location = "America/New_York";  // TODO - timezone currently defaults to east coast

	/**
	 * add specified number of days to a date
	 * @return
	 */
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

	/**
	 * add specified number of hours to a date
	 * @return
	 */
    public static Date addHours(Date date, int hours)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hours); //minus number would decrement the hours
        return cal.getTime();
    }
    
	/**
	 * add specified number of minutes to a date
	 * @return
	 */
    public static Date addMinutes(Date date, int mins)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, mins); //minus number would decrement the minutes
        return cal.getTime();
    }
    
	/**
	 * add specified number of seconds to a date
	 * @return
	 */
    public static Date addSeconds(Date date, int secs)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, secs); //minus number would decrement the minutes
        return cal.getTime();
    }
    
	/**
	 * convert a Date into a date/time string in appropriate timezone
	 * @return
	 */
    public static String toDateTime1(Date date)
    {
    	SimpleDateFormat sdf = new SimpleDateFormat("MMM d, hh:mm a z");
    	sdf.setTimeZone(TimeZone.getTimeZone(location));
        return sdf.format(date);
    }
    
	/**
	 * convert a Date into a simple time string in appropriate timezone
	 * @return
	 */
    public static String toSimpleTime(Date date)
    {
    	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
    	sdf.setTimeZone(TimeZone.getTimeZone(location));
        return sdf.format(date);
    }

    /** return a date within the next 24 hrs given an hour and minute in 24h format for current timezone
     * 
     * @param alarmTimeHour
     * @param alarmTimeMinutes
     * @return
     */
	public static Date getNewDateFromTime(int alarmTimeHour, int alarmTimeMinutes) {
	      Calendar cal = Calendar.getInstance();
	      cal.clear(); 
	      cal.setTimeZone(TimeZone.getTimeZone(location));
	      Date currentDate = new Date();
	      cal.setTime( currentDate );  // start with current date
	      cal.set(Calendar.HOUR_OF_DAY, alarmTimeHour);
	      cal.set(Calendar.MINUTE, alarmTimeMinutes);
	      cal.set(Calendar.SECOND, 0);
	      cal.set(Calendar.MILLISECOND, 0);
	      // add a day if the time created isn't in the future
	      Date retDate = cal.getTime();
	      if (retDate.before(currentDate)) retDate = DateUtils.addDays(retDate, 1);
	      return retDate;
	}

	/** compare 2 dates and return true if within specified minutes
	 * 
	 * @param oldAlarmTime
	 * @param currentAlarmTime
	 * @param mins - max separation for true result in minutes
	 * @return
	 */
	public static boolean datesRoughlyEqual(Date oldAlarmTime,
			Date currentAlarmTime, int mins) {
		if ((oldAlarmTime==null) || (currentAlarmTime==null)) return true;
		Date lowBound = DateUtils.addMinutes(oldAlarmTime, mins * -1);
		Date highBound = DateUtils.addMinutes(oldAlarmTime, mins);
		return (currentAlarmTime.after(lowBound) && currentAlarmTime.before(highBound));
	}
 
}
