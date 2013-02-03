package br.com.expense.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import br.com.expense.model.Transaction;
import br.com.expense.service.DateTimeServiceImpl;

public class ContaCorrenteItauParserTest extends BaseParserTest {

	@Test
	public void shouldAcceptIfMatches() throws FileNotFoundException {
		assertTrue(new ContaCorrenteItauParser(new DateTimeServiceImpl()).accept(this.loadFile("itau-pj.txt")));		
	}
	
	@Test
	public void shouldIgnoreFlagOfToBeProcessedDebits() {
		String snippet = 
				" Data	 	 	Lan�amento	 	Valor (R$)	 	Saldo (R$)\r\n" +
				"10/01	D	 	INT PAG TIT BANCO 033	4175	275,00	-\r\n" +
				"Posi��o da Conta Corrente";
		List<Transaction> transactions = new ContaCorrenteItauParser(new DateTimeServiceImpl()).parse(snippet);
		assertNotNull(transactions);
		assertEquals(1, transactions.size());
		assertFalse(transactions.get(0).getDescription().startsWith("D"));
		assertTrue(transactions.get(0).getDescription().startsWith("INT PAG"));
	}
	
	@Test
	public void shouldIgnoreFlagOfToBeProcessedCredits() {
		String snippet = 
				" Data	 	 	Lan�amento	 	Valor (R$)	 	Saldo (R$)\r\n" +
				"10/01	C	 	INT PAG TIT BANCO 033	4175	275,00\r\n" +
				"Posi��o da Conta Corrente";
		List<Transaction> transactions = new ContaCorrenteItauParser(new DateTimeServiceImpl()).parse(snippet);
		assertNotNull(transactions);
		assertEquals(1, transactions.size());
		assertFalse(transactions.get(0).getDescription().startsWith("C"));
		assertTrue(transactions.get(0).getDescription().startsWith("INT PAG"));
	}
	
	@Test
	public void shouldNotConsiderBalanceEntries() {
		String snippet = "\r\n" +
				"Data	 	 	Lan�amento	 	Valor (R$)	 	Saldo (R$)\r\n" +
				"20/12	 	 	SALDO ANTERIOR	 	 	 	1.826,33\r\n" +
				"02/01	 	 	TBI 0000.00000-5Contas	4175	350,00	-\r\n" +
				"Posi��o da Conta Corrente";
		List<Transaction> transactions = new ContaCorrenteItauParser(new DateTimeServiceImpl()).parse(snippet);
		assertNotNull(transactions);
		assertEquals(1, transactions.size());
		assertTrue(transactions.get(0).getDescription().startsWith("TBI"));
	}
	
	@Test
	public void parseTransactions() throws FileNotFoundException {
		List<Transaction> transactions = new ContaCorrenteItauParser(new DateTimeServiceImpl()).parse(this.loadFile("itau-pj.txt"));
		assertNotNull(transactions);
		assertFalse(transactions.isEmpty());
		assertEquals(11, transactions.size());
	}	
	
}
