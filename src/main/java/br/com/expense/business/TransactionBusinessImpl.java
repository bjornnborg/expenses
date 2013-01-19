package br.com.expense.business;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.expense.model.Category;
import br.com.expense.model.Transaction;
import br.com.expense.parser.CartaoPersonnaliteParser;
import br.com.expense.parser.Parser;
import br.com.expense.service.DateTimeServiceImpl;
import br.com.expense.util.FileUtil;

public class TransactionBusinessImpl implements TransactionBusiness {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("###,###,##0.00");

	private List<Parser> parsers = new ArrayList<Parser>();
	
	public TransactionBusinessImpl(List<Parser> parsers) {
		this.parsers = parsers;
	}
	
	public TransactionBusinessImpl() {
		this.parsers = new ArrayList<Parser>();
		parsers.add(new CartaoPersonnaliteParser(new DateTimeServiceImpl()));
	}	

	@Override
	public void process(String path) {
		File file = new File(path);
		if (file.exists() && file.isDirectory()) {
			String[] transactionFiles = file.list(new TransactionFileFilter());
			Map<String, Category> categories = new HashMap<String, Category>(); 
			if (transactionFiles.length > 0) {
				//load categorization files
				String[] categoryFiles = file.list(new CategoriesFileFilter());
				for (String fileName : categoryFiles) {
					String fileContent = FileUtil.loadFile(file, fileName);
					String[] ruleLines = fileContent.split("\n");
					for (String ruleLine : ruleLines) {
						String[] categorizationInformation = ruleLine.split("=>");
						categories.put(categorizationInformation[0].trim(), new Category(categorizationInformation[1].trim()));
					}
				}
			}
			
			for (String fileName : transactionFiles) {
				String fileContent = FileUtil.loadFile(file, fileName);
				List<Transaction> transactions = new ArrayList<Transaction>();
				for (Parser parser : parsers) {
					if (parser.accept(fileContent)) {
						List<Transaction> parserTransactions = parser.parse(fileContent);
						outter: for (Transaction transaction : parserTransactions) {
							for(String regex : categories.keySet()) {
								Pattern pattern = Pattern.compile(regex);
								Matcher matcher = pattern.matcher(transaction.getDescription());
								if (matcher.matches()) {
									transaction.setCategory(categories.get(regex));
									continue outter;
								}
							}
						}
						if (!parserTransactions.isEmpty()) {
							transactions.addAll(parserTransactions);
						}
					}
				}
				
				Collections.sort(transactions);
				Map<String, BigDecimal> costsByCategory = new HashMap<String, BigDecimal>();
				
				StringBuilder expensesOutput = new StringBuilder();
				StringBuilder expensesByCategory = new StringBuilder();
				if (!transactions.isEmpty()) {
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
		
	}
	
	private static class TransactionFileFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			boolean accept = name.endsWith(".txt");
			if (accept) {
				System.out.println(">> Accepting " + name);
			}
			return accept;
		}
		
	}
	
	private static class CategoriesFileFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".rules");
		}
		
	}	
}
