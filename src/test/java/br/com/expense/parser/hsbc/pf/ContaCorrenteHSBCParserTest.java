package br.com.expense.parser.hsbc.pf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import br.com.expense.model.Transaction;
import br.com.expense.parser.BaseParserTest;
import br.com.expense.service.DateTimeServiceImpl;

public class ContaCorrenteHSBCParserTest extends BaseParserTest {
	
	@Test
	public void shouldAcceptIfMatches() throws FileNotFoundException {
		assertTrue(new ContaCorrenteHSBCParser(new DateTimeServiceImpl()).accept(this.loadFile("hsbc-conta-corrente-pf.txt")));
	}	

	@Test
	public void shouldNotConsiderBalanceEntries() {
		String snippet = "\r\n" +
				"Data	Conta corrente	Valor\r\n" +
				"25/09	COMP VISA ELECTRON	1163	0063276	5,50  D\r\n" +
				"Saldo disponível			1.170.147,25  D\r\n" +
				"Saldo em...";
		List<Transaction> transactions = new ContaCorrenteHSBCParser(new DateTimeServiceImpl()).parse(snippet);
		assertNotNull(transactions);
		assertEquals(1, transactions.size());
		assertTrue(transactions.get(0).getDescription().startsWith("COMP VISA"));
	}
	
	@Test
	public void shouldKnowCreditAndDebitTransactions() {
		String snippet = "\r\n" +
				"Data	Conta corrente	Valor\r\n" +
				"25/09	COMP VISA ELECTRON	1163	0063276	5,50  D\r\n" +
				"Algum Depósito			70,25  C\r\n" +
				"Saldo em...";
		List<Transaction> transactions = new ContaCorrenteHSBCParser(new DateTimeServiceImpl()).parse(snippet);
		assertNotNull(transactions);
		assertEquals(2, transactions.size());
		assertEquals(new BigDecimal("-5.50"), transactions.get(0).getValue());
		assertEquals(new BigDecimal("70.25"), transactions.get(1).getValue());
	}	

	@Test
	public void shouldStickWithLastDateWhenNoDateFoundInTransactionLine() {
		String snippet = "\r\n" +
				"Data	Conta corrente	Valor\r\n" +
				"10/10	LUZ/ENERGIA	1163	0080789	102,26  D\r\n" +
				"PAGAMENTO TITULO-IB	1163	0278217	453,00  D\r\n" +
				"PAGAMENTO ELETRONICO	1163	0661137	509,00  D\r\n" +
				"Saldo em...";
		List<Transaction> transactions = new ContaCorrenteHSBCParser(new DateTimeServiceImpl()).parse(snippet);
		assertNotNull(transactions);
		assertEquals(3, transactions.size());
		assertEquals(transactions.get(0).getDate(), transactions.get(1).getDate());
		assertEquals(transactions.get(0).getDate(), transactions.get(2).getDate());
	}

}
