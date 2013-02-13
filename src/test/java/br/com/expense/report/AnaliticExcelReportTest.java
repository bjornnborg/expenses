package br.com.expense.report;

import static br.com.expense.model.TransactionType.DEBIT;
import static br.com.expense.model.TransactionType.CREDIT;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
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


public class AnaliticExcelReportTest {

	@Test
	public void mustGenerateDetailedReportForTransactions() {
		List<Transaction> transactions = new ArrayList<Transaction>();
		Transaction t1 = new Transaction();
		t1.setType(DEBIT);
		t1.setDate(DateTimeUtil.parse("13/02/2013"));
		t1.setDescription("TNG");
		t1.setCurrencyInfo(new CurrencyInfo(new BigDecimal("25"), Currency.REAL, new BigDecimal("1")));
		t1.setCategory(new Category("Clothes"));
		transactions.add(t1);
		
		Transaction t2 = new Transaction();
		t2.setType(DEBIT);
		t2.setDate(DateTimeUtil.parse("13/02/2013"));
		t2.setDescription("CINEMARK");
		t2.setCurrencyInfo(new CurrencyInfo(new BigDecimal("43.18"), Currency.REAL, new BigDecimal("1")));
		t2.setCategory(new Category("Entertainment"));
		transactions.add(t2);
		
		Transaction t3 = new Transaction();
		t3.setType(CREDIT);
		t3.setDate(DateTimeUtil.parse("13/02/2013"));
		t3.setDescription("WAGE");
		t3.setCurrencyInfo(new CurrencyInfo(new BigDecimal("1000.00"), Currency.REAL, new BigDecimal("1")));
		t3.setCategory(new Category("REVENUE"));
		transactions.add(t3);
		
		String content = new AnaliticExcelReport(transactions).getContent();
		assertNotNull(content);
		assertEquals(8, content.split("\r\n").length);
		assertTrue(content.contains("D6:D8"));
		assertTrue(content.contains("C6:C8"));
	}
	
}
