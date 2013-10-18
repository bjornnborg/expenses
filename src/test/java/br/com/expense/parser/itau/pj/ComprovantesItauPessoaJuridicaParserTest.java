package br.com.expense.parser.itau.pj;

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

public class ComprovantesItauPessoaJuridicaParserTest extends BaseParserTest {
	
	@Test
	public void shouldAcceptIfMatchesCorporateReceipts() throws FileNotFoundException, URISyntaxException {
		assertTrue(new ComprovantesItauPessoaJuridicaParser().accept(this.loadFile("itau-comprovantes-pj.txt")));
	}	
	
	@Test
	public void parseTransactions() throws FileNotFoundException {
		List<Transaction> transactions = new ComprovantesItauPessoaJuridicaParser().parse(this.loadFile("itau-comprovantes-pj.txt"));
		assertNotNull(transactions);
		assertFalse(transactions.isEmpty());
		assertEquals(4, transactions.size());
	}
	
}
