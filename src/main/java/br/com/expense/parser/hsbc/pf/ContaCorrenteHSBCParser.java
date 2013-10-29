package br.com.expense.parser.hsbc.pf;


import static br.com.expense.model.Currency.REAL;
import static br.com.expense.model.TransactionType.CREDIT;
import static br.com.expense.model.TransactionType.DEBIT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.expense.model.CurrencyInfo;
import br.com.expense.model.Transaction;
import br.com.expense.parser.TransactionParser;
import br.com.expense.service.DateTimeService;
import br.com.expense.util.DateTimeUtil;

public class ContaCorrenteHSBCParser implements TransactionParser {
	
	private static Pattern HEADER = Pattern.compile("Skip page header and navigation.+");
	private static Pattern FOOTER =  Pattern.compile(".+HSBC\\sBank.+");
	
	private static Pattern TRANSACTIONS_SNIPPET = Pattern.compile("(.+?)(Data.+)(Saldo\\sem.+)", Pattern.DOTALL);
	// ITAU: ((\\d{1,3}\\.?)+,(\\d{2}))
	// HSBC: (((\\d{1,3}\\.?)+),(\\d{2}))
	private static Pattern TRANSACTION_RECORD = Pattern.compile("(\\d{2}/\\d{2})?(.+?)((\\d{1,3}\\.?)+,(\\d{2}))\\s+(C|D)", Pattern.MULTILINE);
	private static final Set<String> BALANCE_ENTRIES = new HashSet<String>();
	
	static {
		BALANCE_ENTRIES.add("Saldo anterior");
		BALANCE_ENTRIES.add("Saldo disponível");
	}
	
	
	private DateTimeService dateTimeService;

	public ContaCorrenteHSBCParser(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	@Override
	public List<Transaction> parse(String text) {
		List<Transaction> transactions = new ArrayList<Transaction>();
		text = getTransactionsSnippet(text);
		Matcher transactionRecord = TRANSACTION_RECORD.matcher(text);
		Date dateForTransactions = null;
		while (transactionRecord.find()) {
			String description = transactionRecord.group(2).trim();
			if (isBalanceEntry(description)) {
				continue;
			}
			
			Transaction transaction = new Transaction();
			if (isDatePresent(transactionRecord)) {
				dateForTransactions = DateTimeUtil.parse(transactionRecord.group(1) + "/" + getCorrectYear(transactionRecord.group(1)));
			}
			transaction.setDate(dateForTransactions);
			transaction.setDescription(description);
			String signal = "D".equals(transactionRecord.group(6).trim()) ? "-" : "";
			String value = signal + transactionRecord.group(3).trim();
			
			if (value.startsWith("-")) {
				transaction.setType(DEBIT);
			} else {
				transaction.setType(CREDIT);
			}
			
			transaction.setCurrencyInfo(new CurrencyInfo(new BigDecimal(value.replaceAll("\\.", "").replaceAll(",", ".")), REAL, new BigDecimal("1")));
			transactions.add(transaction);
			
		}
		return transactions;
	}
	
	private boolean isDatePresent(Matcher transactionRecord) {
		return transactionRecord.group(1) != null;
	}

	private int getCorrectYear(String dayAndMonth) {
		int currentYear = dateTimeService.now().get(Calendar.YEAR);
		int currentMonth = dateTimeService.now().get(Calendar.MONTH);
		int correctYear = currentYear;
		String month = dayAndMonth.split("/")[1];
		if (Integer.valueOf(month) > currentMonth) {
			correctYear = --currentYear;
		}
		return correctYear;
	}
	
	private boolean isBalanceEntry(String transactionDescription) {
		return BALANCE_ENTRIES.contains(transactionDescription.trim());
	}
	
	@Override
	public boolean accept(String text) {
		return hasHeader(text) && hasFooter(text);
	}
	
	private String getTransactionsSnippet(String text) {
		String snippet = "";
		Matcher matcher = TRANSACTIONS_SNIPPET.matcher(text);
		if (matcher.matches()) {
			snippet = matcher.group(2);
		}
		return removeScheduledTransactions(snippet);
	}
	
	private String removeScheduledTransactions(String text) {
		String snippet = text;
		return snippet;		
	}
	
	private boolean hasHeader(String text) {
		return HEADER.matcher(text).find();
	}
	
	private boolean hasFooter(String text) {
		return FOOTER.matcher(text).find();
	}


	@Override
	public String getName() {
		return "HSBC PF parser";
	}
	
}
