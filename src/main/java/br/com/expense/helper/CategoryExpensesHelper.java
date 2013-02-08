package br.com.expense.helper;

import static br.com.expense.model.TransactionType.CREDIT;
import static br.com.expense.model.TransactionType.DEBIT;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.expense.model.Category;
import br.com.expense.model.Transaction;
import br.com.expense.model.TransactionType;

public class CategoryExpensesHelper {

	private List<Transaction> transactions;
	private Map<TransactionType, Map<Category, BigDecimal>> expensesByCategory;

	public CategoryExpensesHelper(List<Transaction> transactions) {
		this.transactions = transactions;
		expensesByCategory = new HashMap<TransactionType, Map<Category,BigDecimal>>();
	}
	
	public CategoryExpenses getExpensesByCategory() {
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
				
				if (expensesByCategory.get(transaction.getType()) == null) {
					expensesByCategory.put(transaction.getType(), new HashMap<Category, BigDecimal>());
				}
				
				
				BigDecimal categorySum = expensesByCategory.get(transaction.getType()).get(category);
				if (categorySum == null) {
					categorySum = new BigDecimal(0);
				}
				expensesByCategory.get(transaction.getType()).put(category, categorySum.add(transaction.getValue()));
			}
		}
		return new CategoryExpenses(expensesByCategory);
	}
	
	public static class CategoryExpenses {
		
		private Map<TransactionType, Map<Category, BigDecimal>> expensesByCategory;
		
		public CategoryExpenses(Map<TransactionType, Map<Category, BigDecimal>> expensesByCategory) {
			this.expensesByCategory = expensesByCategory;
		}

		public BigDecimal getTotalDebitsAmountFor(Category category) {
			return this.expensesByCategory.get(DEBIT).get(category);
		}
		
		public BigDecimal getTotalCreditsAmountFor(Category category) {
			return this.expensesByCategory.get(CREDIT).get(category);
		}
		
		public int getDebitCount() {
			return this.expensesByCategory.get(DEBIT).keySet().size();
		}
		
		public int getCreditCount() {
			return this.expensesByCategory.get(CREDIT).keySet().size();
		}
		
		public boolean isEmpty() {
			return this.getDebitCount() == 0 && this.getCreditCount() == 0;
		}		
	}
	
}
