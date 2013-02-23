package br.com.expense.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import br.com.expense.model.Transaction;
import br.com.expense.service.DateTimeServiceImpl;

public class ComprovantesItauParserTest extends BaseParserTest {
	
	@Test
	public void shouldAcceptIfMatches() throws FileNotFoundException, URISyntaxException {
		assertTrue(new ComprovantesItauParser(new DateTimeServiceImpl()).accept(this.loadFile("itau-comprovantes.txt")));
	}
	
	@Test
	public void parseTransactions() throws FileNotFoundException {
		List<Transaction> transactions = new ComprovantesItauParser(new DateTimeServiceImpl()).parse(this.loadFile("itau-comprovantes.txt"));
		assertNotNull(transactions);
		assertFalse(transactions.isEmpty());
		assertEquals(8, transactions.size());
	}
	
}
