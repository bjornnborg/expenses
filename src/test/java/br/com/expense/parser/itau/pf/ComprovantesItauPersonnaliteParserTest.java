package br.com.expense.parser.itau.pf;

import static br.com.expense.model.TransactionType.CREDIT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;

import br.com.expense.model.Transaction;
import br.com.expense.parser.BaseParserTest;

public class ComprovantesItauPersonnaliteParserTest extends BaseParserTest {
	
	@Test
	public void shouldAcceptIfMatches() throws FileNotFoundException, URISyntaxException {
		assertTrue(new ComprovantesItauPersonnaliteParser().accept(this.loadFile("itau-comprovantes-personnalite.txt")));
	}
	
	@Test
	public void parseTransactions() throws FileNotFoundException {
		List<Transaction> transactions = new ComprovantesItauPersonnaliteParser().parse(this.loadFile("itau-comprovantes-personnalite.txt"));
		assertNotNull(transactions);
		assertFalse(transactions.isEmpty());
		assertEquals(7, transactions.size());
	}
	
	@Test
	public void mustUseTransactionTypeAsDescriptionWhenDescriptionIsNull() throws FileNotFoundException {
		List<Transaction> transactions = new ComprovantesItauPersonnaliteParser().parse(this.loadFile("itau-comprovantes-personnalite.txt"));
		assertNotNull(transactions);
		assertEquals("Detran SP - IPVA", transactions.get(5).getDescription());
	}
	
	@Test
	public void mustIdentifyCreditTransactionsCorrectly() throws FileNotFoundException {
		List<Transaction> transactions = new ComprovantesItauPersonnaliteParser().parse(this.loadFile("itau-comprovantes-personnalite.txt"));
		assertNotNull(transactions);
		assertEquals("CEI 000043 DINHEIRO", transactions.get(4).getDescription());
		assertEquals(CREDIT, transactions.get(4).getType());
	}
	
}
