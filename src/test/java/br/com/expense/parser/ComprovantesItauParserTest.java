package br.com.expense.parser;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.junit.Test;

import br.com.expense.service.DateTimeServiceImpl;

public class ComprovantesItauParserTest extends BaseParserTest {

	@Test
	public void shouldAcceptIfMatches() throws FileNotFoundException, URISyntaxException {
		assertTrue(new ComprovantesItauParser(new DateTimeServiceImpl()).accept(this.loadFile("itau-comprovantes.txt")));
	}	
	
}
