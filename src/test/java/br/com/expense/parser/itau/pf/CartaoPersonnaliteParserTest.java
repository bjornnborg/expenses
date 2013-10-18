package br.com.expense.parser.itau.pf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import br.com.expense.model.Transaction;
import br.com.expense.parser.BaseParserTest;
import br.com.expense.service.DateTimeService;
import br.com.expense.service.DateTimeServiceImpl;
import br.com.expense.util.DateTimeUtil;


public class CartaoPersonnaliteParserTest extends BaseParserTest {

	@Test
	public void shouldAcceptIfMatches() throws FileNotFoundException, URISyntaxException {
		assertTrue(new CartaoPersonnaliteParser(new DateTimeServiceImpl()).accept(this.loadFile("itau-personnalite-visa.txt")));
	}
	
	@Test
	public void parseTransactions() throws FileNotFoundException {
		List<Transaction> transactions = new CartaoPersonnaliteParser(new DateTimeServiceImpl()).parse(this.loadFile("itau-personnalite-visa.txt"));
		assertNotNull(transactions);
		assertFalse(transactions.isEmpty());
		assertEquals(8, transactions.size());
	}
	
	@Test
	public void detectPastYearWhenTransactionMonthBiggerThenCurrentMonth() {
		DateTimeService dateTimeService = new DateTimeService() {
			
			@Override
			public Date today() {
				return DateTimeUtil.parse("05/01/2013");
			}
			
			@Override
			public Calendar now() {
				Calendar now = Calendar.getInstance();
				now.setTime(this.today());
				return now;
			}
		};
		
		String snippet = 
				"Lançamentos nacionais\r\n" + 
				"08/12	PAO DE ACUCAR 1221	12,00\r\n\r\n"; 
				
		
		List<Transaction> transactions = new CartaoPersonnaliteParser(dateTimeService).parse(snippet);
		assertNotNull(transactions);
		assertFalse(transactions.isEmpty());
		assertEquals(1, transactions.size());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(transactions.get(0).getDate());
		assertEquals(2012, calendar.get(Calendar.YEAR));
	}
	
	@Test
	public void detectCurrentYearWhenTransactionMonthEqualsCurrentMonth() {
		DateTimeService dateTimeService = new DateTimeService() {
			
			@Override
			public Date today() {
				return DateTimeUtil.parse("04/02/2013");
			}
			
			@Override
			public Calendar now() {
				Calendar now = Calendar.getInstance();
				now.setTime(this.today());
				return now;
			}
		};
		
		String snippet = 
				"Lançamentos nacionais\r\n" + 
				"01/02	PAO DE ACUCAR 1221	12,00\r\n\r\n"; 
				
		
		List<Transaction> transactions = new CartaoPersonnaliteParser(dateTimeService).parse(snippet);
		assertNotNull(transactions);
		assertFalse(transactions.isEmpty());
		assertEquals(1, transactions.size());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(transactions.get(0).getDate());
		assertEquals(2013, calendar.get(Calendar.YEAR));
	}	
	
	@Test
	public void mustConvertDollarTransactionsToReais() throws FileNotFoundException {
		List<Transaction> transactions = new CartaoPersonnaliteParser(new DateTimeServiceImpl()).parse(this.loadFile("itau-personnalite-visa.txt"));
		assertNotNull(transactions);
		assertFalse(transactions.isEmpty());
		assertEquals(8, transactions.size());
		assertEquals(new BigDecimal("-25.44"), transactions.get(6).getCurrencyInfo().getTotalValue());
		
	}
}
