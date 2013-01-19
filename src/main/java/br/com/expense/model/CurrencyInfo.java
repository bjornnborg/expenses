package br.com.expense.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyInfo {
	private BigDecimal amount;
	private Currency currency;
	private BigDecimal currencyRate = new BigDecimal(1);
	
	public CurrencyInfo(BigDecimal amount, Currency currency, BigDecimal currencyRate) {
		this.amount = amount;
		this.currency = currency;
		this.currencyRate = currencyRate;
	}
	
	public static CurrencyInfo withValues(BigDecimal amount, Currency currency, BigDecimal currencyRate) {
		return new CurrencyInfo(amount, currency, currencyRate);
	}

	public BigDecimal getTotalValue() {
		return amount.multiply(currencyRate).setScale(2, RoundingMode.HALF_EVEN);
	}
	
	public Currency getCurrency() {
		return currency;
	}
	
	public BigDecimal getCurrencyRate() {
		return currencyRate;
	}
}
