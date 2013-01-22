package br.com.expense.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.com.expense.model.Transaction;
import br.com.expense.parser.CartaoPersonnaliteParser;
import br.com.expense.parser.Parser;
import br.com.expense.parser.rules.CategoryRulesParser;
import br.com.expense.service.DateTimeServiceImpl;

public class TransactionBusinessTest {

	@Test
	public void generateReport() {
		TransactionBusiness business = new TransactionBusinessImpl();
		business.process("src/test/resources/");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shoulBreakIfDirectoryPathDoesNotExists() {
		TransactionBusiness business = new TransactionBusinessImpl();
		business.process("j:/test");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shoulBreakIfIsNotDirectory() {
		TransactionBusiness business = new TransactionBusinessImpl();
		business.process("src/test/resources/itau-personnalite-visa.txt");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void onlyOneParserShouldAccept() {
		List<Parser> parsers = new ArrayList<Parser>();
		parsers.add(new CartaoPersonnaliteParser(new DateTimeServiceImpl()));
		parsers.add(new Parser() {
			
			@Override
			public List<Transaction> parse(String text) {
				return new ArrayList<Transaction>();
			}
			
			@Override
			public boolean accept(String text) {
				return true;
			}
		});
		
		TransactionBusiness business = new TransactionBusinessImpl(parsers, new CategoryRulesParser());
		business.process("src/test/resources/");
	}	
	
}
