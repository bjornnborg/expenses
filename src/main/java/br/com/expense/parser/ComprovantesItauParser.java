package br.com.expense.parser;

import java.util.List;
import java.util.regex.Pattern;

import br.com.expense.model.Transaction;
import br.com.expense.service.DateTimeService;
import br.com.expense.service.DateTimeServiceImpl;

public class ComprovantesItauParser implements TransactionParser {
	
	private static Pattern HEADER = Pattern.compile("Home.+>.+Comprovantes");
	private static Pattern KEY_LINE = Pattern.compile("^Comprovantes$", Pattern.MULTILINE);
	private static Pattern FOOTER =  Pattern.compile("Ita.\\sUnibanco.+|.+mapa.+site");
	
	private DateTimeService dateTimeService;

	public ComprovantesItauParser(DateTimeServiceImpl dateTimeServiceImpl) {
		this.dateTimeService = dateTimeServiceImpl;
	}

	@Override
	public List<Transaction> parse(String text) {
		return null;
	}

	@Override
	public boolean accept(String text) {
		return hasHeader(text) && hasKeyLine(text) && hasFooter(text);
	}
	
	private boolean hasHeader(String text) {
		return HEADER.matcher(text).find();
	}
	
	private boolean hasFooter(String text) {
		return FOOTER.matcher(text).find();
	}
	
	private boolean hasKeyLine(String text) {
		return KEY_LINE.matcher(text).find();
	}

	@Override
	public String getName() {
		return "Itaú receipts parser";
	}

}
