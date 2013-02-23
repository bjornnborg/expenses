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
					formula = "=SOMARPRODUTO";
				} else {
					formula = "=SUMPRODUCT";
				}
				
				String textToSum = getTextToSum(category.getName());
				
				//=SUMPRODUCT((B6:B7<0)*(C6:C7="roupas"),B6:B7)
				content += formula +
						"(" +
							"(" + FIELDS[AMOUNT_FIELD_INDEX] + start + ":" + FIELDS[AMOUNT_FIELD_INDEX] + end + " < 0" + ") * " +
							"(" + FIELDS[CATEGORY_FIELD_INDEX] + start + ":" + FIELDS[CATEGORY_FIELD_INDEX] + end + " = \"" + textToSum +"\"), " +
							FIELDS[AMOUNT_FIELD_INDEX] + start + ":" + FIELDS[AMOUNT_FIELD_INDEX] + end + 
						")";
				content += "\r\n";
			}
			for(Category category : expensesByCategory.allCredits()) {
				content += category.getName() + SEPARATOR;
				String formula = "";
				if (new Locale("pt", "BR").equals(locale)) {
					formula = "=SOMARPRODUTO";
				} else {
					formula = "=SUMPRODUCT";
				}
				
				String textToSum = getTextToSum(category.getName());
				
				//=SUMPRODUCT((B6:B7<0)*(C6:C7="roupas"),B6:B7)
				content += formula +
						"(" +
							"(" + FIELDS[AMOUNT_FIELD_INDEX] + start + ":" + FIELDS[AMOUNT_FIELD_INDEX] + end + " >= 0" + ") * " +
							"(" + FIELDS[CATEGORY_FIELD_INDEX] + start + ":" + FIELDS[CATEGORY_FIELD_INDEX] + end + " = \"" + textToSum +"\"), " +
							FIELDS[AMOUNT_FIELD_INDEX] + start + ":" + FIELDS[AMOUNT_FIELD_INDEX] + end + 
						")";
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

	private String getTextToSum(String name) {
		return "unspecified credit".equals(name) || "unspecified debit".equals(name) ? "" : name;
	}
	
	//=SUMPRODUCT(B5:B6,C5:C6="roupas",B5:B6>=0)
	//{=SUM(($B$5:$B$6)*($B$5:$B$6<0)*($C$5:$C$6="roupas"))}
	//=SUMPRODUCT((B6:B7<0)*(C6:C7="roupas"),B6:B7)
	//SUM - SOMA
	//SUMIF - SOMA.SE
	//SUMPRODUCT - SOMARPRODUTO	

}
