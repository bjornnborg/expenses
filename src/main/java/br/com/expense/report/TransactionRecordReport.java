package br.com.expense.report;

import java.util.Collections;
import java.util.List;

import br.com.expense.model.Transaction;
import br.com.expense.util.DateTimeUtil;

public class TransactionRecordReport {
	
	private List<Transaction> transactions;

	public TransactionRecordReport(List<Transaction> transactions) {
		this.transactions = transactions;
		Collections.sort(this.transactions);
	}

	public String getContent() {
		StringBuilder builder = new StringBuilder();
		for (Transaction transaction : transactions) {
			builder.append(getReportLineFor(transaction) + "\r\n");
		}
		return builder.toString().replaceAll("\r\n$", "");
	}
	
	private String getReportLineFor(Transaction transaction) {
		String line = 
				DateTimeUtil.format(transaction.getDate()) + ";" +
				transaction.getDescription() + ";" + 
				transaction.getValue() + ";";
		
		if (transaction.getCategory() != null) {
			line += transaction.getCategory().getName();
		}
		return line;
		
	}

}
