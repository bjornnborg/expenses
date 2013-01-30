package br.com.expense.report;

import static br.com.expense.model.TransactionType.DEBIT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.com.expense.model.Category;
import br.com.expense.model.Currency;
import br.com.expense.model.CurrencyInfo;
import br.com.expense.model.Transaction;

public class CategoryExpensesReportTest {

	@Test
	public void generateGroupedReportForTransactionRecords() {
		List<Transaction> transactions = new ArrayList<Transaction>();
		Transaction t1 = new Transaction();
		t1.setType(DEBIT);
		t1.setCurrencyInfo(new CurrencyInfo(new BigDecimal("25"), Currency.REAL, new BigDecimal("1")));
		t1.setCategory(new Category("Clothes"));
		transactions.add(t1);
		
		Transaction t2 = new Transaction();
		t2.setType(DEBIT);
		t2.setCurrencyInfo(new CurrencyInfo(new BigDecimal("43.18"), Currency.REAL, new BigDecimal("1")));
		t2.setCategory(new Category("Clothes"));
		transactions.add(t2);
		
		CategoryExpensesReport report = new CategoryExpensesReport(transactions);
		String reportContent = report.getContent();
		assertNotNull(reportContent);
		assertEquals("Clothes;-68.18", reportContent);
	}
	
}
