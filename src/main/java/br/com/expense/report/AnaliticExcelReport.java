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
			FormulaHelper formulaHelper = new FormulaHelper(expensesByCategory, transactions);
			for(Category category : expensesByCategory.allDebits()) {
				content += category.getName() + SEPARATOR;
				content += formulaHelper.getNegativeFormula(category);
				content += "\r\n";
			}
			for(Category category : expensesByCategory.allCredits()) {
				content += category.getName() + SEPARATOR;
				content += formulaHelper.getPositiveFormula(category);
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

	private static class FormulaHelper {
		
		private static final int CATEGORY_FIELD_INDEX = 3;
		private static final int AMOUNT_FIELD_INDEX = 2;
		private static final String[] FIELDS = {"A", "B", "C", "D"};
		
		private int start;
		private int end;
		
		private FormulaHelper(CategoryExpenses expensesByCategory, List<Transaction> transactions) {
			int transactionsCount = transactions.size(); 
			int categoriesCount = expensesByCategory.getCreditCount() + expensesByCategory.getDebitCount();
			start = categoriesCount + BLANK_LINES + 1;
			end = start + transactionsCount - 1;
		}
		
		public String getPositiveFormula(Category category) {
			return getLine(category, false);
		}
		
		public String getNegativeFormula(Category category) {
			return getLine(category, true);
		}
		
		private String getLine(Category category, boolean negativeValue) {
			String expressionLine = getFunction();
			String value = " < 0";
			if (!negativeValue) {
				value = " >= 0";
			}
			expressionLine +=
			"(" +
				"(" + FIELDS[AMOUNT_FIELD_INDEX] + start + ":" + FIELDS[AMOUNT_FIELD_INDEX] + end + value + ") * " +
				"(" + FIELDS[CATEGORY_FIELD_INDEX] + start + ":" + FIELDS[CATEGORY_FIELD_INDEX] + end + " = \"" + getTextToSum(category.getName()) +"\") * " +
				"(" + FIELDS[AMOUNT_FIELD_INDEX] + start + ":" + FIELDS[AMOUNT_FIELD_INDEX] + end + ")" + 
			")";
			return expressionLine;
		}
		
		private String getFunction() {
			Locale locale = Locale.getDefault();
			String formula="=SUMPRODUCT";
			if (new Locale("pt", "BR").equals(locale)) {
				formula = "=SOMARPRODUTO";
			}
			return formula;
		}
		
		private String getTextToSum(String name) {
			return "unspecified credit".equals(name) || "unspecified debit".equals(name) ? "" : name;
		}
	}
	
}
