package br.com.expense.report;

import static br.com.expense.model.TransactionType.DEBIT;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.com.expense.model.Category;
import br.com.expense.model.Currency;
import br.com.expense.model.CurrencyInfo;
import br.com.expense.model.Transaction;
import br.com.expense.util.DateTimeUtil;

public class TransactionRecordReportTest {
	
	@Test
	public void generateReportForEachTransactionRecord() {
		List<Transaction> transactions = new ArrayList<Transaction>();
		Transaction t1 = new Transaction();
		t1.setDate(DateTimeUtil.parse("15/01/2013"));
		t1.setDescription("Some transaction");
		t1.setType(DEBIT);
		t1.setCurrencyInfo(new CurrencyInfo(new BigDecimal("25"), Currency.REAL, new BigDecimal("1")));
		t1.setCategory(new Category("Some category"));
		transactions.add(t1);
		
		Transaction t2 = new Transaction();
		t2.setDate(DateTimeUtil.parse("12/01/2013"));
		t2.setDescription("Another transaction");
		t2.setType(DEBIT);
		t2.setCurrencyInfo(new CurrencyInfo(new BigDecimal("43.18"), Currency.REAL, new BigDecimal("1")));
		t2.setCategory(new Category("Another category"));
		transactions.add(t2);
		
		TransactionRecordReport report = new TransactionRecordReport(transactions);
		String reportContent = report.getContent();
		assertNotNull(reportContent);
		assertTrue("transactions must be in ascending date order", reportContent.startsWith("12/01/2013"));
		
	}

}
