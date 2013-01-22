package br.com.expense.parser;

import java.util.List;

import br.com.expense.model.Transaction;

public interface TransactionParser {

	List<Transaction> parse(String text);
	boolean accept(String text);
}
