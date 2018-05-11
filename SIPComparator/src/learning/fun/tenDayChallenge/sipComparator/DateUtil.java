package learning.fun.tenDayChallenge.sipComparator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static Date getDate(String dateString, String dateFormat) throws ParseException {
		DateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.parse(dateString);
	}
	
	public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
	
	public static int getDateDiff(Date date1, Date date2) {
		long diff = date2.getTime() - date1.getTime();
		return (int) (diff/(24 * 60 * 60 * 1000));
	}
}
