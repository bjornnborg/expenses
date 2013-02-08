package br.com.expense.helper;

import static br.com.expense.model.TransactionType.CREDIT;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.expense.model.Category;
import br.com.expense.model.Transaction;

public class CategoryExpensesHelper {

	private List<Transaction> transactions;
	private Map<Category, BigDecimal> expensesByCategory;

	public CategoryExpensesHelper(List<Transaction> transactions) {
		this.transactions = transactions;
		expensesByCategory = new HashMap<Category, BigDecimal>();
	}
	
	public Map<Category, BigDecimal> getExpensesByCategory() {
		if (expensesByCategory.isEmpty()) {
			for (Transaction transaction : transactions) {
				Category category = transaction.getCategory();
				if (category == null) {
					if (CREDIT == transaction.getType()) {
						category = new Category("unspecified credit");
					} else {
						category = new Category("unspecified debit");
					}
				}
				BigDecimal categorySum = expensesByCategory.get(category);
				if (categorySum == null) {
					categorySum = new BigDecimal(0);
				}
				expensesByCategory.put(category, categorySum.add(transaction.getValue()));
			}
		}
		return expensesByCategory;
	}
	
}
