package br.com.expense;

import br.com.expense.business.TransactionBusinessImpl;
import br.com.expense.config.Configuration;

public class Finance {
	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			throw new IllegalStateException("Type a directory to scan");
		}
		new TransactionBusinessImpl(Configuration.preset()).process(args[0]);
	}
}
