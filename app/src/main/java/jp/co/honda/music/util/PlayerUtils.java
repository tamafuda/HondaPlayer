package jp.co.honda.music.util;

public class PlayerUtils
{

	public static String getTimeHoursMinutesSecondsString(long elapsedTime)
	{
		String time;
	    String format = String.format("%%0%dd", 2);
	    elapsedTime /= 1000;
	    String seconds = String.format(format, elapsedTime % 60);
	    String minutes = String.format(format, (elapsedTime % 3600) / 60);
	    long hoursInt = elapsedTime / 3600;
	    if( hoursInt > 0 )
	    {
	    	String hours = String.format(format, hoursInt );
	    	time =  hours + ":" + minutes + ":" + seconds;
	    }
	    else
	    {
	    	time =  minutes + ":" + seconds;
	    }

	    return time;
	}
}
