package br.com.expense.parser;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.junit.Test;

import br.com.expense.service.DateTimeServiceImpl;

public class ContaCorrenteItauParserTest extends BaseParserTest {

	@Test
	public void shouldAcceptIfMatches() throws FileNotFoundException {
		assertTrue(new ContaCorrenteItauParser(new DateTimeServiceImpl()).accept(this.loadFile("itau-pj.txt")));		
	}
	
}
