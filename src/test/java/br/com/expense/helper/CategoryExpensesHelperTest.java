package br.com.expense.helper;

import static br.com.expense.model.TransactionType.CREDIT;
import static br.com.expense.model.TransactionType.DEBIT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.com.expense.helper.CategoryExpensesHelper.CategoryExpenses;
import br.com.expense.model.Category;
import br.com.expense.model.Currency;
import br.com.expense.model.CurrencyInfo;
import br.com.expense.model.Transaction;

public class CategoryExpensesHelperTest {
	
	@Test
	public void mustGroupExpensesOfTheSameTypeAndCategory() {
		List<Transaction> transactions = new ArrayList<Transaction>();
		Transaction t1 = new Transaction();
		t1.setType(DEBIT);
		t1.setCurrencyInfo(new CurrencyInfo(new BigDecimal("25"), Currency.REAL, new BigDecimal("1")));
		t1.setCategory(new Category("Clothes"));
		transactions.add(t1);
		
		Transaction t2 = new Transaction();
		t2.setType(DEBIT);
		t2.setCurrencyInfo(new CurrencyInfo(new BigDecimal("12"), Currency.REAL, new BigDecimal("1")));
		t2.setCategory(new Category("Clothes"));
		transactions.add(t2);
		
		CategoryExpenses expensesByCategory = new CategoryExpensesHelper(transactions).getExpensesByCategory();
		assertNotNull(expensesByCategory);
		assertFalse(expensesByCategory.isEmpty());
		assertEquals(1, expensesByCategory.getDebitCount());
		assertEquals(new BigDecimal("-37.00"), expensesByCategory.getTotalDebitAmountFor(new Category("Clothes")));
	}
	
	@Test
	public void mustConsiderTransactionTypeWhenGrouping() {
		List<Transaction> transactions = new ArrayList<Transaction>();
		Transaction t1 = new Transaction();
		t1.setType(DEBIT);
		t1.setCurrencyInfo(new CurrencyInfo(new BigDecimal("25"), Currency.REAL, new BigDecimal("1")));
		t1.setCategory(new Category("Clothes"));
		transactions.add(t1);
		
		Transaction t2 = new Transaction();
		t2.setType(CREDIT);
		t2.setCurrencyInfo(new CurrencyInfo(new BigDecimal("12"), Currency.REAL, new BigDecimal("1")));
		t2.setCategory(new Category("Clothes"));
		transactions.add(t2);
		
		CategoryExpenses expensesByCategory = new CategoryExpensesHelper(transactions).getExpensesByCategory();
		assertNotNull(expensesByCategory);
		assertFalse(expensesByCategory.isEmpty());
		assertEquals(1, expensesByCategory.getDebitCount());
		assertEquals(1, expensesByCategory.getCreditCount());
		assertEquals(new BigDecimal("-25.00"), expensesByCategory.getTotalDebitAmountFor(new Category("Clothes")));
		assertEquals(new BigDecimal("12.00"), expensesByCategory.getTotalCreditAmountFor(new Category("Clothes")));
	}	
	
	@Test
	public void mustGroupUncategorizedExpensesCorrectly() {
		List<Transaction> transactions = new ArrayList<Transaction>();
		Transaction t1 = new Transaction();
		t1.setType(CREDIT);
		t1.setCurrencyInfo(new CurrencyInfo(new BigDecimal("100"), Currency.REAL, new BigDecimal("1")));
		transactions.add(t1);
		
		Transaction t2 = new Transaction();
		t2.setType(DEBIT);
		t2.setCurrencyInfo(new CurrencyInfo(new BigDecimal("100"), Currency.REAL, new BigDecimal("1")));
		transactions.add(t2);
		
		CategoryExpenses expensesByCategory = new CategoryExpensesHelper(transactions).getExpensesByCategory();
		assertNotNull(expensesByCategory);
		assertFalse(expensesByCategory.isEmpty());
		assertEquals(1, expensesByCategory.getDebitCount());
		assertEquals(1, expensesByCategory.getCreditCount());
		assertEquals(new BigDecimal("100.00"), expensesByCategory.getTotalCreditAmountFor(new Category("unspecified credit")));
		assertEquals(new BigDecimal("-100.00"), expensesByCategory.getTotalDebitAmountFor(new Category("unspecified debit")));
	}

	@Test
	public void mustGroupExpensesCorrectly() {
		List<Transaction> transactions = new ArrayList<Transaction>();
		Transaction t1 = new Transaction();
		t1.setType(DEBIT);
		t1.setCurrencyInfo(new CurrencyInfo(new BigDecimal("25"), Currency.REAL, new BigDecimal("1")));
		t1.setCategory(new Category("Clothes"));
		transactions.add(t1);
		
		Transaction t2 = new Transaction();
		t2.setType(CREDIT);
		t2.setCurrencyInfo(new CurrencyInfo(new BigDecimal("12"), Currency.REAL, new BigDecimal("1")));
		t2.setCategory(new Category("Wage"));
		transactions.add(t2);
		
		Transaction t3 = new Transaction();
		t3.setType(CREDIT);
		t3.setCurrencyInfo(new CurrencyInfo(new BigDecimal("100"), Currency.REAL, new BigDecimal("1")));
		transactions.add(t3);
		
		Transaction t4 = new Transaction();
		t4.setType(DEBIT);
		t4.setCurrencyInfo(new CurrencyInfo(new BigDecimal("100"), Currency.REAL, new BigDecimal("1")));
		transactions.add(t4);
		
		CategoryExpenses expensesByCategory = new CategoryExpensesHelper(transactions).getExpensesByCategory();
		assertNotNull(expensesByCategory);
		assertFalse(expensesByCategory.isEmpty());
		assertEquals(2, expensesByCategory.getDebitCount());
		assertEquals(2, expensesByCategory.getCreditCount());
		assertEquals(new BigDecimal("-25.00"), expensesByCategory.getTotalDebitAmountFor(new Category("Clothes")));
		assertEquals(new BigDecimal("12.00"), expensesByCategory.getTotalCreditAmountFor(new Category("Wage")));
		assertEquals(new BigDecimal("100.00"), expensesByCategory.getTotalCreditAmountFor(new Category("unspecified credit")));
		assertEquals(new BigDecimal("-100.00"), expensesByCategory.getTotalDebitAmountFor(new Category("unspecified debit")));
	}
	
}
