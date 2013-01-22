package br.com.expense.business;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.expense.config.Configuration;
import br.com.expense.model.Transaction;
import br.com.expense.parser.TransactionParser;
import br.com.expense.parser.TransactionParserEngine;
import br.com.expense.parser.rules.CategoryRulesEngine;
import br.com.expense.parser.rules.CategoryRulesParser;

public class TransactionBusinessImpl implements TransactionBusiness {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("###,###,##0.00");
	
	private TransactionParserEngine parserEngine;

	public TransactionBusinessImpl(TransactionParserEngine parserEngine) {
		this.parserEngine = parserEngine;
	}

	public TransactionBusinessImpl(Configuration configuration) {
		this.parserEngine = new TransactionParserEngine(configuration, new ArrayList<TransactionParser>(), new CategoryRulesEngine(Configuration.preset(), new CategoryRulesParser()));
	}	

	@Override
	public void process(String path) {
		File file = new File(path);
		
		if (!file.exists()) {
			throw new IllegalArgumentException("Path does not exists");
		}
		
		if (!file.isDirectory()) {
			throw new IllegalArgumentException("Path is not a directory");
		}
		
		List<Transaction> transactions = this.parserEngine.getTransactions(file);
		
		Map<String, BigDecimal> costsByCategory = new HashMap<String, BigDecimal>();
		
		StringBuilder expensesOutput = new StringBuilder();
		StringBuilder expensesByCategory = new StringBuilder();
		
		for (Transaction transaction : transactions) {
			// write file
			String line = DATE_FORMAT.format(transaction.getDate()) + ";" + transaction.getDescription() + ";" + transaction.getType() + ";" + NUMBER_FORMAT.format(transaction.getCurrencyInfo().getTotalValue());
			if (transaction.getCategory() != null) {
				line += ";" + transaction.getCategory().getName();
			}
			expensesOutput.append(line + "\r\n");
			
			String category = transaction.getCategory() != null ? transaction.getCategory().getName() : "unspecified";
			// just in case we have same category name for debit or credit
			category += "^" + transaction.getType();
			
			BigDecimal categorySum = costsByCategory.get(category);
			if (categorySum == null) {
				categorySum = new BigDecimal(0);
			}
			costsByCategory.put(category, categorySum.add(transaction.getCurrencyInfo().getTotalValue()));			
		}
		
		for (String category : costsByCategory.keySet()) {
			expensesByCategory.append(category.split("\\^")[0] + ";" + NUMBER_FORMAT.format(costsByCategory.get(category)) + "\r\n");
		}		
			
		BufferedWriter bw = null;
		try {
			 bw = new BufferedWriter(new FileWriter("expenses-report.csv"));
			 bw.write(expensesByCategory.toString() + "\r\n\r\n" + expensesOutput.toString()+"\r\n");
			 bw.write("=soma(d1:d50)");
			 bw.close();
		} catch (IOException e) {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
