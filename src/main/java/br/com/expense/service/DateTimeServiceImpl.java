package br.com.expense.service;

import java.util.Calendar;
import java.util.Date;

public class DateTimeServiceImpl implements DateTimeService {

	@Override
	public Calendar now() {
		return Calendar.getInstance();
	}

	@Override
	public Date today() {
		return Calendar.getInstance().getTime();
	}

}
