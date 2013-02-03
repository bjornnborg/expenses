package br.com.expense.parser;

import java.util.List;
import java.util.regex.Pattern;

import br.com.expense.model.Transaction;
import br.com.expense.service.DateTimeService;

public class ContaCorrenteItauParser implements TransactionParser {
	
	private static Pattern HEADER = Pattern.compile("Ag.ncia.+|\\s+Conta\\sCorrente.+|.+Investimentos");
	private static Pattern FOOTER =  Pattern.compile("Ita.\\sUnibanco.+|.+site^");

	public ContaCorrenteItauParser(DateTimeService dateTimeServiceImpl) {
		
	}

	@Override
	public List<Transaction> parse(String text) {
		return null;
	}

	@Override
	public boolean accept(String text) {
		return hasHeader(text) && hasFooter(text);
	}
	
	private boolean hasHeader(String text) {
		return HEADER.matcher(text).find();
	}
	
	private boolean hasFooter(String text) {
		return FOOTER.matcher(text).find();
	}

}
