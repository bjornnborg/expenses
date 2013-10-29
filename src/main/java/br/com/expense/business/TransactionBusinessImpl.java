package br.com.expense.business;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.expense.config.Configuration;
import br.com.expense.model.Transaction;
import br.com.expense.parser.TransactionParser;
import br.com.expense.parser.TransactionParserEngine;
import br.com.expense.parser.hsbc.pf.ContaCorrenteHSBCParser;
import br.com.expense.parser.itau.pf.CartaoPersonnaliteParser;
import br.com.expense.parser.itau.pf.ComprovantesItauPersonnaliteParser;
import br.com.expense.parser.itau.pf.ContaCorrenteItauPersonnaliteParser;
import br.com.expense.parser.itau.pj.ComprovantesItauPessoaJuridicaParser;
import br.com.expense.parser.itau.pj.ContaCorrenteItauPjParser;
import br.com.expense.parser.rules.CategoryRulesEngine;
import br.com.expense.parser.rules.CategoryRulesParser;
import br.com.expense.report.AnaliticExcelReport;
import br.com.expense.service.DateTimeServiceImpl;
import br.com.expense.util.DateTimeUtil;
import br.com.expense.util.FileUtil;

public class TransactionBusinessImpl implements TransactionBusiness {
	
	private TransactionParserEngine parserEngine;

	public TransactionBusinessImpl(TransactionParserEngine parserEngine) {
		this.parserEngine = parserEngine;
	}

	public TransactionBusinessImpl(Configuration configuration) {
		this.parserEngine = new TransactionParserEngine(configuration, getDefaultParsers(), new CategoryRulesEngine(Configuration.preset(), new CategoryRulesParser()));
	}
	
	private List<TransactionParser> getDefaultParsers() {
		List<TransactionParser> parsers = new ArrayList<TransactionParser>();
		parsers.add(new CartaoPersonnaliteParser(new DateTimeServiceImpl()));
		parsers.add(new ComprovantesItauPersonnaliteParser());
		parsers.add(new ComprovantesItauPessoaJuridicaParser());
		parsers.add(new ContaCorrenteItauPjParser(new DateTimeServiceImpl()));
		parsers.add(new ContaCorrenteItauPersonnaliteParser(new DateTimeServiceImpl()));
		parsers.add(new ContaCorrenteHSBCParser(new DateTimeServiceImpl()));
		System.out.println(">> Utilizando parsers: ");
		for (TransactionParser transactionParser : parsers) {
			System.out.println("  >> " + transactionParser.getName());
		}
		return parsers;
	}

	@Override
	public void process(String path) {
		File basePath = new File(path);
		
		if (!basePath.exists()) {
			throw new IllegalArgumentException("Path does not exists");
		}
		
		if (!basePath.isDirectory()) {
			throw new IllegalArgumentException("Path is not a directory");
		}
		
		List<Transaction> transactions = this.parserEngine.getTransactions(basePath);
		String dateTime = DateTimeUtil.format(new Date(), "yyyyMMdd HHmmss");
		String fileName = String.format("expenses-report-%s.csv", dateTime);
		FileUtil.writeFile(new File(basePath, fileName), new AnaliticExcelReport(transactions).getContent());
		System.out.println(String.format(">> %d transactions", transactions.size()));
	}
}
