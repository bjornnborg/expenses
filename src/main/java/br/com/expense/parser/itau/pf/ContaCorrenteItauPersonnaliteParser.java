package br.com.expense.parser.itau.pf;


import static br.com.expense.model.Currency.REAL;
import static br.com.expense.model.TransactionType.CREDIT;
import static br.com.expense.model.TransactionType.DEBIT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
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

public class ContaCorrenteItauPersonnaliteParser implements TransactionParser {
	
	private static Pattern HEADER = Pattern.compile("Ag.ncia:.+Conta:");
	private static Pattern PERSONNALITE_LINE = Pattern.compile("^Ita.+Personnalite", Pattern.MULTILINE);
	private static Pattern CHECKING_ACCOUNT_LINE = Pattern.compile("^Saldo de Conta Corrente", Pattern.MULTILINE);
	private static Pattern FOOTER =  Pattern.compile("Ita.\\sUnibanco.+|.+site^");
	
	private static Pattern TRANSACTIONS_SNIPPET = Pattern.compile("(.+?)(Data.+)(Aviso.*)", Pattern.DOTALL);
	private static Pattern SCHEDULED_TRANSACTIONS_SNIPPET = Pattern.compile("(Lan.amentos.+futuros.+)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	
	private static Pattern TRANSACTION_RECORD = Pattern.compile("(\\d{2}/\\d{2})\\s+(D\\s|C\\s)?(.+?\\s+)((\\d{1,3}\\.?)+,(\\d{2}))(.*)", Pattern.MULTILINE);
	private static final Set<String> BALANCE_ENTRIES = new HashSet<String>();
	
	static {
		BALANCE_ENTRIES.add("SALDO ANTERIOR");
		BALANCE_ENTRIES.add("S A L D O");
		BALANCE_ENTRIES.add("SDO CTA/APL AUTOMATICAS");
	}
	
	
	private DateTimeService dateTimeService;

	public ContaCorrenteItauPersonnaliteParser(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	@Override
	public List<Transaction> parse(String text) {
		List<Transaction> transactions = new ArrayList<Transaction>();
		text = getTransactionsSnippet(text);
		Matcher transactionRecord = TRANSACTION_RECORD.matcher(text);
		while (transactionRecord.find()) {
			String description = transactionRecord.group(3).trim();
			if (isBalanceEntry(description)) {
				continue;
			}
			
			Transaction transaction = new Transaction();
			transaction.setDate(DateTimeUtil.parse(transactionRecord.group(1) + "/" + getCorrectYear(transactionRecord.group(1))));
			transaction.setDescription(description);
			String signal = transactionRecord.group(7).trim();
			String value = signal + transactionRecord.group(4).trim();
			
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
		return hasHeader(text) && isPersonnaliteAccount(text) && isCheckingAccount(text) && hasFooter(text);
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
		Matcher matcher = SCHEDULED_TRANSACTIONS_SNIPPET.matcher(text);
		if (matcher.find()) {
			snippet = snippet.replace(matcher.group(1), "");
		}
		return snippet;		
	}
	
	private boolean hasHeader(String text) {
		return HEADER.matcher(text).find();
	}
	
	private boolean hasFooter(String text) {
		return FOOTER.matcher(text).find();
	}
	
	private boolean isCheckingAccount(String text) {
		return CHECKING_ACCOUNT_LINE.matcher(text).find();
	}
	
	private boolean isPersonnaliteAccount(String text) {
		return PERSONNALITE_LINE.matcher(text).find();
	}

	@Override
	public String getName() {
		return "Itaú Personnalite accounts parser";
	}
	
}
