package br.com.expense.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.expense.config.Configuration;
import br.com.expense.model.Transaction;
import br.com.expense.parser.TransactionParser;
import br.com.expense.parser.TransactionParserEngine;
import br.com.expense.parser.rules.CategoryRulesEngine;
import br.com.expense.parser.rules.CategoryRulesParser;
import br.com.expense.report.CategoryExpensesReport;
import br.com.expense.report.TransactionRecordReport;
import br.com.expense.util.FileUtil;

public class TransactionBusinessImpl implements TransactionBusiness {
	
	private TransactionParserEngine parserEngine;

	public TransactionBusinessImpl(TransactionParserEngine parserEngine) {
		this.parserEngine = parserEngine;
	}

	public TransactionBusinessImpl(Configuration configuration) {
		this.parserEngine = new TransactionParserEngine(configuration, new ArrayList<TransactionParser>(), new CategoryRulesEngine(Configuration.preset(), new CategoryRulesParser()));
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
		
		StringBuilder reportContent = new StringBuilder();
		
		CategoryExpensesReport categoryExpenses = new CategoryExpensesReport(transactions);
		reportContent.append(categoryExpenses.getContent());
		
		reportContent.append("\r\n");
		reportContent.append("\r\n");
		
		TransactionRecordReport transactionRecords = new TransactionRecordReport(transactions);
		reportContent.append(transactionRecords.getContent());
		
		FileUtil.writeFile(new File(basePath, "expenses-report.csv"), reportContent.toString());
	}
}
