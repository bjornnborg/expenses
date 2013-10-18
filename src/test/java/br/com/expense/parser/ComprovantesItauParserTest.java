package br.com.expense.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static br.com.expense.model.TransactionType.CREDIT;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;

import br.com.expense.model.Transaction;

public class ComprovantesItauParserTest extends BaseParserTest {
	
	@Test
	public void shouldAcceptIfMatches() throws FileNotFoundException, URISyntaxException {
		assertTrue(new ComprovantesItauParser().accept(this.loadFile("itau-comprovantes-personnalite.txt")));
	}
	
	@Test
	public void shouldAcceptIfMatchesCorporateReceipts() throws FileNotFoundException, URISyntaxException {
		assertTrue(new ComprovantesItauParser().accept(this.loadFile("itau-comprovantes-pj.txt")));
	}	
	
	@Test
	public void parseTransactions() throws FileNotFoundException {
		List<Transaction> transactions = new ComprovantesItauParser().parse(this.loadFile("itau-comprovantes-personnalite.txt"));
		assertNotNull(transactions);
		assertFalse(transactions.isEmpty());
		assertEquals(7, transactions.size());
	}
	
	@Test
	public void parseTransactionsFromCorporateReceipts() throws FileNotFoundException {
		List<Transaction> transactions = new ComprovantesItauParser().parse(this.loadFile("itau-comprovantes-pj.txt"));
		assertNotNull(transactions);
		assertFalse(transactions.isEmpty());
		assertEquals(7, transactions.size());
	}
	
	@Test
	public void mustUseTransactionTypeAsDescriptionWhenDescriptionIsNull() throws FileNotFoundException {
		List<Transaction> transactions = new ComprovantesItauParser().parse(this.loadFile("itau-comprovantes-personnalite.txt"));
		assertNotNull(transactions);
		assertEquals("Detran SP - IPVA", transactions.get(5).getDescription());
	}
	
	@Test
	public void mustIdentifyCreditTransactionsCorrectly() throws FileNotFoundException {
		List<Transaction> transactions = new ComprovantesItauParser().parse(this.loadFile("itau-comprovantes-personnalite.txt"));
		assertNotNull(transactions);
		assertEquals("CEI 000043 DINHEIRO", transactions.get(4).getDescription());
		assertEquals(CREDIT, transactions.get(4).getType());
	}
	
}
