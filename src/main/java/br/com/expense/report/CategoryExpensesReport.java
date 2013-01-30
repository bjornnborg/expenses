package br.com.expense.report;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.expense.model.Transaction;
import br.com.expense.util.NumberUtil;

public class CategoryExpensesReport {

	private List<Transaction> transactions;
	
	private Map<String, BigDecimal> expensesByCategory = new HashMap<String, BigDecimal>();
	
	public CategoryExpensesReport(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public String getContent() {
		StringBuilder builder = new StringBuilder();
		if (expensesByCategory.isEmpty()) {
			for (Transaction transaction : transactions) {
				String category = transaction.getCategory() != null ? transaction.getCategory().getName() : "unspecified";
				// just in case we have same category name for debit or credit
				category += "^" + transaction.getType();	
				BigDecimal categorySum = expensesByCategory.get(category);
				if (categorySum == null) {
					categorySum = new BigDecimal(0);
				}
				expensesByCategory.put(category, categorySum.add(transaction.getValue()));
			}
		}
		
		for (String category : expensesByCategory.keySet()) {
			builder.append(category.split("\\^")[0] + ";" + NumberUtil.format(expensesByCategory.get(category)) + "\r\n");
		}		

		return builder.toString().replaceAll("\r\n$", "");		
	}
	
}
