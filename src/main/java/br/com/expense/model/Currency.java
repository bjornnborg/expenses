package br.com.expense.model;

public enum Currency {
	REAL("R$"),
	DOLLAR("US$");
	
	private String symbol;

	private Currency(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}
	
	public static Currency fromSymbol(String symbol) {
		Currency currency = null;
		symbol = symbol.trim();
		if (DOLLAR.symbol.equals(symbol)) {
			currency = DOLLAR;
		} else if (REAL.symbol.equals(symbol)) {
			currency = REAL;
		}
		return currency;
	}
	
}
