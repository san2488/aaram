package edu.ncsu.soc.project;

import java.util.Calendar;
import java.util.Date;

/** date utility methods
 * 
 * @author snellenbach
 *
 */
public class DateUtils {
	
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
}
