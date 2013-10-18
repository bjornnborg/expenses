package br.com.expense.business;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.com.expense.config.Configuration;
import br.com.expense.model.Transaction;
import br.com.expense.parser.TransactionParser;
import br.com.expense.parser.TransactionParserEngine;
import br.com.expense.parser.itau.pf.CartaoPersonnaliteParser;
import br.com.expense.parser.rules.CategoryRulesEngine;
import br.com.expense.parser.rules.CategoryRulesParser;
import br.com.expense.service.DateTimeServiceImpl;

public class TransactionBusinessTest {

	@Test
	public void generateReport() {
		List<TransactionParser> parsers = new ArrayList<TransactionParser>();
		parsers.add(new CartaoPersonnaliteParser(new DateTimeServiceImpl()));
		TransactionBusiness business = new TransactionBusinessImpl(new TransactionParserEngine(Configuration.preset(), parsers, new CategoryRulesEngine(Configuration.preset("src/test/resources/"), new CategoryRulesParser())));
		business.process("src/test/resources/");
		assertTrue(new File("src/test/resources/expenses-report.csv").exists());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shoulBreakIfDirectoryPathDoesNotExists() {
		TransactionBusiness business = new TransactionBusinessImpl(Configuration.preset());
		business.process("j:/test");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shoulBreakIfIsNotDirectory() {
		TransactionBusiness business = new TransactionBusinessImpl(Configuration.preset());
		business.process("src/test/resources/itau-personnalite-visa.txt");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void onlyOneParserShouldAccept() {
		List<TransactionParser> transactionParsers = new ArrayList<TransactionParser>();
		transactionParsers.add(new CartaoPersonnaliteParser(new DateTimeServiceImpl()));
		transactionParsers.add(new TransactionParser() {
			
			@Override
			public List<Transaction> parse(String text) {
				return new ArrayList<Transaction>();
			}
			
			@Override
			public boolean accept(String text) {
				return true;
			}

			@Override
			public String getName() {
				return "annonymous";
			}
		});
		
		TransactionBusiness business = new TransactionBusinessImpl(new TransactionParserEngine(Configuration.preset(), transactionParsers, new CategoryRulesEngine(Configuration.preset(), new CategoryRulesParser())));
		business.process("src/test/resources/");
	}	
	
}
