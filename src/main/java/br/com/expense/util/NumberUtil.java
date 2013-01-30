package br.com.expense.util;

import java.text.DecimalFormat;

public class NumberUtil {
	
	public static String format(Object o, String mask) {
		return new DecimalFormat(mask).format(o);
	}
	
	public static String format(Object o) {
		return format(o, "###,###,##0.00");
	}	

}
