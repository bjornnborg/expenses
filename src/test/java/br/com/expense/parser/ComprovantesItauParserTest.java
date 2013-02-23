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
		assertTrue(new ComprovantesItauParser().accept(this.loadFile("itau-comprovantes.txt")));
	}
	
	@Test
	public void parseTransactions() throws FileNotFoundException {
		List<Transaction> transactions = new ComprovantesItauParser().parse(this.loadFile("itau-comprovantes.txt"));
		assertNotNull(transactions);
		assertFalse(transactions.isEmpty());
		assertEquals(8, transactions.size());
	}
	
	@Test
	public void mustUseTransactionTypeAsDescriptionWhenDescriptionIsNull() throws FileNotFoundException {
		List<Transaction> transactions = new ComprovantesItauParser().parse(this.loadFile("itau-comprovantes.txt"));
		assertNotNull(transactions);
		assertEquals("Detran SP - DPVAT", transactions.get(4).getDescription());
		assertEquals("Detran SP - IPVA", transactions.get(5).getDescription());
	}
	
	@Test
	public void mustIdentifyCreditTransactionsCorrectly() throws FileNotFoundException {
		List<Transaction> transactions = new ComprovantesItauParser().parse(this.loadFile("itau-comprovantes.txt"));
		assertNotNull(transactions);
		assertEquals("CEI 000030 DINHEIRO", transactions.get(0).getDescription());
		assertEquals(CREDIT, transactions.get(0).getType());
	}
	
}
