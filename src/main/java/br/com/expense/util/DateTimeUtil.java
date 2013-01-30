package br.com.expense.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

	public static Date parse(String dateString, String pattern) {
		Date date = null;
		try {
			date = new SimpleDateFormat(pattern).parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date parse(String date) {
		return parse(date, "dd/MM/yyyy");
	}
	
	public static String format(Date date) {
		return format(date, "dd/MM/yyyy");
	}
	
	public static String format(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}	
}
