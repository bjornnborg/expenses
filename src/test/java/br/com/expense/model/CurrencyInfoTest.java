package br.com.expense.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class CurrencyInfoTest {

	@Test
	public void shouldConsiderRateWhenCalculatingValue() {
		CurrencyInfo currencyInfo = CurrencyInfo.withValues(new BigDecimal("10"), Currency.DOLLAR, new BigDecimal("2.5"));
		assertEquals(new BigDecimal("25.00"), currencyInfo.getTotalValue());
	}
	
}
