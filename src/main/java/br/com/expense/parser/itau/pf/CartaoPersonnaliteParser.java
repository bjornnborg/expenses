package br.com.expense.parser.itau.pf;

import static br.com.expense.model.Currency.DOLLAR;
import static br.com.expense.model.Currency.REAL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.expense.model.Currency;
import br.com.expense.model.CurrencyInfo;
import br.com.expense.model.Transaction;
import br.com.expense.model.TransactionType;
import br.com.expense.parser.TransactionParser;
import br.com.expense.service.DateTimeService;
import br.com.expense.util.DateTimeUtil;

public class CartaoPersonnaliteParser implements TransactionParser {
	
	private static Pattern BREADCRUMB = Pattern.compile("Home.+Fatura.+Consultar");
	private static Pattern CARD_NUMBER = Pattern.compile("Meu.+final.+");
	private static Pattern FOOTER = Pattern.compile("Ita.+?\\|");
	
	private static Pattern PAYMENT_RECORD = Pattern.compile("Movimenta..es.+?^$", Pattern.DOTALL | Pattern.MULTILINE);
	private static Pattern LOCAL_TRANSACTIONS = Pattern.compile("Lan.amentos\\snacionais.+?^$", Pattern.DOTALL | Pattern.MULTILINE);
	private static Pattern FOREIGN_TRANSACTIONS = Pattern.compile("Lan.amentos\\sinternacionais.+?convers.+?$", Pattern.DOTALL | Pattern.MULTILINE);
	private static Pattern DOLLAR_RATE = Pattern.compile("utilizado.+((\\d+),\\d{2})");
	
	private static Pattern DISPOSABLE_TEXT = Pattern.compile(".+?(Movimenta..es.+)Op..es.+", Pattern.DOTALL | Pattern.MULTILINE);
	private static Pattern TRANSACTION_LINE = Pattern.compile("^(\\d{2}/\\d{2})\\s+(.+?)(-?((\\d+)\\.?)+,\\d{2})", Pattern.MULTILINE);
	private static Pattern TRANSACTION_LINE_WITH_CURRENCY = Pattern.compile("^(\\d{2}/\\d{2})\\s+(.+?)(-?((\\d+)\\.?)+,\\d{2}).+--(.+)$", Pattern.MULTILINE);
	private static Pattern HEADER_LINE = Pattern.compile("^DATA.+\\s(.+\\$)$", Pattern.MULTILINE);
	
	private DateTimeService dateTimeService;
	
	public CartaoPersonnaliteParser(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	@Override
	public List<Transaction> parse(String text) {
		List<Transaction> transactions = new ArrayList<Transaction>();
		
		BigDecimal dollarRate = getDollarRate(text);
		text = this.prepareToParse(text);

		
		Matcher transactionMatcher = TRANSACTION_LINE_WITH_CURRENCY.matcher(text);
		while(transactionMatcher.find()) {
				BigDecimal multiplier = new BigDecimal(1);
				Transaction transaction = new Transaction();
				
				transaction.setDate(DateTimeUtil.parse(transactionMatcher.group(1) + "/" + getCorrectYear(transactionMatcher.group(1))));
				transaction.setDescription(transactionMatcher.group(2).trim());
				String value = transactionMatcher.group(3);
				
				Currency currency = Currency.fromSymbol(transactionMatcher.group(6).trim());
				if (DOLLAR == currency) {
					multiplier = dollarRate;
				}
				
				if (value.trim().startsWith("-")) {
					transaction.setType(TransactionType.CREDIT);
					value = value.substring(1);
				} else {
					transaction.setType(TransactionType.DEBIT);
					value = "-" + value;
				}
				transaction.setCurrencyInfo(CurrencyInfo.withValues(new BigDecimal(value.replaceAll("\\.", "").replaceAll(",", ".")), currency, multiplier));
				transactions.add(transaction);
		}

		return transactions;
	}
	
	private BigDecimal getDollarRate(String text) {
		BigDecimal dollarRate = null;
		Matcher matcher = DOLLAR_RATE.matcher(text);
		if (matcher.find()) {
			dollarRate = new BigDecimal(matcher.group(1).replace(",", "."));
		}
		return dollarRate;
	}

	private String prepareToParse(String text) {
		String transactionBlocks = "";
		text = removeDisposableText(text);
		
		transactionBlocks += findLocalTransactionsBlocks(text);
		transactionBlocks += findForeignTransactionBlocks(text);
		
		return getTransactionRecords(transactionBlocks);
	}
	
	private String getTransactionRecords(String transactionBlocks) {
		BufferedReader reader = new BufferedReader(new StringReader(transactionBlocks));
		
		String currencySymbol = REAL.getSymbol();
		String transactionLines = "";
		
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				Matcher headerMatcher = HEADER_LINE.matcher(line);
				if (headerMatcher.matches()) {
					currencySymbol = headerMatcher.group(1);
				} else {
					Matcher transactionMatcher = TRANSACTION_LINE.matcher(line);
					if (transactionMatcher.matches()) {
						transactionLines += line + " -- " + currencySymbol + "\r\n";
					}
				}			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return transactionLines;
	}
	
	private String findLocalTransactionsBlocks(String text) {
		String records = "";
		
		Matcher paymentMatcher = PAYMENT_RECORD.matcher(text);
		while (paymentMatcher.find()) {
			records += paymentMatcher.group();
		}
		
		Matcher localTransactionsMatcher = LOCAL_TRANSACTIONS.matcher(text);
		while (localTransactionsMatcher.find()) {
			records += localTransactionsMatcher.group();
		}
		return records;
	}
	
	private String findForeignTransactionBlocks(String text) {
		String records = "";
		Matcher foreignTransactionsMatcher = FOREIGN_TRANSACTIONS.matcher(text);
		while (foreignTransactionsMatcher.find()) {
			records += foreignTransactionsMatcher.group();
		}
		return records;
	}
	
	private String removeDisposableText(String text){
		Matcher disposableTextMatcher = DISPOSABLE_TEXT.matcher(text);
		if (disposableTextMatcher.find()) {
			text = disposableTextMatcher.group(1);
		}
		return text;
	}
	
	private int getCorrectYear(String dayAndMonth) {
		int currentYear = dateTimeService.now().get(Calendar.YEAR);
		int currentMonthHumanReadable = dateTimeService.now().get(Calendar.MONTH) + 1;
		int correctYear = currentYear;
		String month = dayAndMonth.split("/")[1];
		if (Integer.valueOf(month) > currentMonthHumanReadable) {
			correctYear = --currentYear;
		}
		return correctYear;
	}
	
	@Override
	public boolean accept(String text) {
		return 
				hasBreadCrumb(text) &&
				hasCardNumber(text) &&
				hasCardFooter(text);
	}
	
	private boolean hasBreadCrumb(String text) {
		return BREADCRUMB.matcher(text).find();
	}
	
	private boolean hasCardNumber(String text) {
		return CARD_NUMBER.matcher(text).find();
	}
	
	private boolean hasCardFooter(String text) {
		return FOOTER.matcher(text).find();
	}

	@Override
	public String getName() {
		return "Cartão personnalité parser";
	}	

}
