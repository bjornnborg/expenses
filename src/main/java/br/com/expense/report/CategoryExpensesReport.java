package br.com.expense.report;

import java.util.List;

import br.com.expense.helper.CategoryExpensesHelper;
import br.com.expense.helper.CategoryExpensesHelper.CategoryExpenses;
import br.com.expense.model.Category;
import br.com.expense.model.Transaction;
import br.com.expense.util.NumberUtil;

public class CategoryExpensesReport {

	private CategoryExpenses expensesByCategory;
	
	public CategoryExpensesReport(List<Transaction> transactions) {
		this.expensesByCategory = new CategoryExpensesHelper(transactions).getExpensesByCategory();
	}

	public String getContent() {
		StringBuilder builder = new StringBuilder();
		for (Category category : expensesByCategory.allCredits()) {
			builder.append(category.getName() + ";" + NumberUtil.format(expensesByCategory.getTotalCreditAmountFor(category)) + "\r\n");
		}
		for (Category category : expensesByCategory.allDebits()) {
			builder.append(category.getName() + ";" + NumberUtil.format(expensesByCategory.getTotalDebitAmountFor(category)) + "\r\n");
		}			

		return builder.toString().replaceAll("\r\n$", "");		
	}
	
}
