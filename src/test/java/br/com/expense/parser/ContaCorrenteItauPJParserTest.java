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

public class ContaCorrenteItauPJParserTest extends BaseParserTest {
	
	@Test
	public void shouldAcceptIfMatches() throws FileNotFoundException {
		assertTrue(new ContaCorrenteItauPjParser(new DateTimeServiceImpl()).accept(this.loadFile("itau-pj.txt")));
	}

	@Test
	public void shouldNotConsiderBalanceEntries() {
		String snippet = "\r\n" +
				"Data	 	 	Lançamento	 	Valor (R$)	 	Saldo (R$)\r\n" +
				"01/10	 	 	S A L D O	 	 	 	99,99\r\n" +
				"02/01	 	 	TBI 0000.00000-5Contas	4175	350,00	-\r\n" +
				"Os Saldos...";
		List<Transaction> transactions = new ContaCorrenteItauPjParser(new DateTimeServiceImpl()).parse(snippet);
		assertNotNull(transactions);
		assertEquals(1, transactions.size());
		assertTrue(transactions.get(0).getDescription().startsWith("TBI"));
	}
	
	@Test
	public void shouldIgnoreFlagOfToBeProcessedCredits() {
		String snippet = 
				" Data	 	 	Lançamento	 	Valor (R$)	 	Saldo (R$)\r\n" +
				"10/01	C	 	INT PAG TIT BANCO 033	4175	275,00\r\n" +
				"28/01			CXE 001842 SAQUE	7619	50,00	-\r\n" +
				"Os Saldos...";
		List<Transaction> transactions = new ContaCorrenteItauPjParser(new DateTimeServiceImpl()).parse(snippet);
		assertNotNull(transactions);
		assertEquals(2, transactions.size());
		assertFalse(transactions.get(0).getDescription().startsWith("C"));
		assertTrue(transactions.get(0).getDescription().startsWith("INT PAG"));
		assertTrue("Wrong indentification of non-confirmed transaction", transactions.get(1).getDescription().startsWith("CXE"));
	}
	
	@Test
	public void shouldIgnoreFlagOfToBeProcessedDebits() {
		String snippet = 
				" Data	 	 	Lançamento	 	Valor (R$)	 	Saldo (R$)\r\n" +
				"10/01	D	 	INT PAG TIT BANCO 033	4175	275,00	-\r\n" +
				"28/01			DXE 001842 SAQUE	7619	50,00	-\r\n" +
				"Os Saldos...";
		List<Transaction> transactions = new ContaCorrenteItauPjParser(new DateTimeServiceImpl()).parse(snippet);
		assertNotNull(transactions);
		assertEquals(2, transactions.size());
		assertFalse(transactions.get(0).getDescription().startsWith("D"));
		assertTrue(transactions.get(0).getDescription().startsWith("INT PAG"));
		assertTrue("Wrong indentification of non-confirmed transaction",transactions.get(1).getDescription().startsWith("DXE"));
	}
	
	@Test
	public void shouldIgnoreScheduledTransactions() {
		String snippet = 
				" Data	 	 	Lançamento	 	Valor (R$)	 	Saldo (R$)\r\n" +
				"10/01		 	INT PAG TIT BANCO 033	4175	275,00	-\r\n" +
				"28/01			DXE 001842 SAQUE	7619	50,00	-\r\n" +
				"Lançamentos futuros\r\n" +
				"Data			Lançamentos				Valor (R$)	\r\n"+
				"10/10			SISDEB SEM PARAR	 0				999,99-\r\n"+
				" \r\n" +
				"Os Saldos...";
		List<Transaction> transactions = new ContaCorrenteItauPjParser(new DateTimeServiceImpl()).parse(snippet);
		assertNotNull(transactions);
		assertEquals(2, transactions.size());
		assertFalse(transactions.get(0).getDescription().startsWith("D"));
		assertTrue(transactions.get(0).getDescription().startsWith("INT PAG"));
		assertTrue("Wrong indentification of non-confirmed transaction",transactions.get(1).getDescription().startsWith("DXE"));
	}
	

	
	@Test
	public void parseItauPJTransactions() throws FileNotFoundException {
		List<Transaction> transactions = new ContaCorrenteItauPjParser(new DateTimeServiceImpl()).parse(this.loadFile("itau-pj.txt"));
		assertEquals(6, transactions.size());
	}

	@Test
	public void parseItauPJTransactionsByDate() throws FileNotFoundException {
		List<Transaction> transactions = new ContaCorrenteItauPjParser(new DateTimeServiceImpl()).parse(this.loadFile("itau-pj-periodo.txt"));
		assertEquals(8, transactions.size());
	}
	
}
