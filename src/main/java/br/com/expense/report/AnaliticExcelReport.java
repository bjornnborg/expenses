package br.com.expense.report;

import java.util.List;
import java.util.Locale;

import br.com.expense.helper.CategoryExpensesHelper;
import br.com.expense.helper.CategoryExpensesHelper.CategoryExpenses;
import br.com.expense.model.Category;
import br.com.expense.model.Transaction;
import br.com.expense.util.DateTimeUtil;
import br.com.expense.util.NumberUtil;

public class AnaliticExcelReport {
	
	private static final String SEPARATOR = ";";
	private static final int BLANK_LINES = 2;
	private static final int CATEGORY_FIELD_INDEX = 3;
	private static final int AMOUNT_FIELD_INDEX = 2;
	
	private static final String[] FIELDS = {"A", "B", "C", "D"};
	
	private List<Transaction> transactions;
	private CategoryExpenses expensesByCategory;
	private String content;

	public AnaliticExcelReport(List<Transaction> transactions) {
		this.transactions = transactions;
		expensesByCategory = new CategoryExpensesHelper(transactions).getExpensesByCategory();
	}

	public String getContent() {
		if (content == null) {
			content = "";
			int totalCategories = expensesByCategory.getCreditCount() + expensesByCategory.getDebitCount();
			int totalTransactions = transactions.size();
			Locale locale = Locale.getDefault();
			int start = totalCategories + BLANK_LINES + 1;
			int end = start + totalTransactions - 1;
			for(Category category : expensesByCategory.allDebits()) {
				content += category.getName() + SEPARATOR;
				String formula = "";
				if (new Locale("pt", "BR").equals(locale)) {
					formula = "=somase";
				} else {
					formula = "=sumif";
				}
				content += formula + "(" + FIELDS[CATEGORY_FIELD_INDEX] + start + ":" + FIELDS[CATEGORY_FIELD_INDEX] + end + "," + "\"" + category.getName() + "\"" + "," + FIELDS[AMOUNT_FIELD_INDEX] + start + ":" + FIELDS[AMOUNT_FIELD_INDEX] + end + ")";
				content += "\r\n";
			}
			for(Category category : expensesByCategory.allCredits()) {
				content += category.getName() + SEPARATOR;
				String formula = "";
				if (new Locale("pt", "BR").equals(locale)) {
					formula = "=somase";
				} else {
					formula = "=sumif";
				}
			content += formula + "(" + FIELDS[CATEGORY_FIELD_INDEX] + start + ":" + FIELDS[CATEGORY_FIELD_INDEX] + end + "," + "\"" + category.getName() + "\"" + "," + FIELDS[AMOUNT_FIELD_INDEX] + start + ":" + FIELDS[AMOUNT_FIELD_INDEX] + end + ")";
				content += "\r\n";
			}			
			
			for (int i=0; i < BLANK_LINES; i ++) {
				content += "\r\n";
			}
			
			for(Transaction transaction : transactions) {
				Category category = transaction.getCategory();
				content += DateTimeUtil.format(transaction.getDate()) + SEPARATOR + transaction.getDescription() + SEPARATOR + NumberUtil.format(transaction.getValue()) + SEPARATOR;
				if (category != null) {
					content += category.getName();
				}
				content += "\r\n";
			}
		}
		return content;
	}

}
