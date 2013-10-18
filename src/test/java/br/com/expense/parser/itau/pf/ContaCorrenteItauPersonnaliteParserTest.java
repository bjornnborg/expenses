package br.com.expense.parser.itau.pf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import br.com.expense.model.Transaction;
import br.com.expense.parser.BaseParserTest;
import br.com.expense.service.DateTimeServiceImpl;

public class ContaCorrenteItauPersonnaliteParserTest extends BaseParserTest {

	@Test
	public void shouldNotConsiderBalanceEntries() {
		String snippet = "\r\n" +
				"Data	 	 	Lançamento	 	Valor (R$)	 	Saldo (R$)\r\n" +
				"20/12	 	 	SALDO ANTERIOR	 	 	 	1.826,33\r\n" +
				"02/01	 	 	TBI 0000.00000-5Contas	4175	350,00	-\r\n" +
				"Aviso";
		List<Transaction> transactions = new ContaCorrenteItauPersonnaliteParser(new DateTimeServiceImpl()).parse(snippet);
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
				"Aviso";
		List<Transaction> transactions = new ContaCorrenteItauPersonnaliteParser(new DateTimeServiceImpl()).parse(snippet);
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
				"Aviso";
		List<Transaction> transactions = new ContaCorrenteItauPersonnaliteParser(new DateTimeServiceImpl()).parse(snippet);
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
				"Aviso";
		List<Transaction> transactions = new ContaCorrenteItauPersonnaliteParser(new DateTimeServiceImpl()).parse(snippet);
		assertNotNull(transactions);
		assertEquals(2, transactions.size());
		assertFalse(transactions.get(0).getDescription().startsWith("D"));
		assertTrue(transactions.get(0).getDescription().startsWith("INT PAG"));
		assertTrue("Wrong indentification of non-confirmed transaction",transactions.get(1).getDescription().startsWith("DXE"));
	}
	
	@Test
	public void shouldAcceptIfMatches() throws FileNotFoundException {
		assertTrue(new ContaCorrenteItauPersonnaliteParser(new DateTimeServiceImpl()).accept(this.loadFile("itau-pf-personnalite.txt")));
	}	
	
	@Test
	public void parseItauPfPersonnaliteTransactions() throws FileNotFoundException {
		List<Transaction> transactions = new ContaCorrenteItauPersonnaliteParser(new DateTimeServiceImpl()).parse(this.loadFile("itau-pf-personnalite.txt"));
		assertEquals(17, transactions.size());
	}
	
	@Test
	public void parseItauPfPersonnaliteTransactionsByDate() throws FileNotFoundException {
		List<Transaction> transactions = new ContaCorrenteItauPersonnaliteParser(new DateTimeServiceImpl()).parse(this.loadFile("itau-pf-personnalite-periodo.txt"));
		assertEquals(2, transactions.size());
	}	
	
}
