package br.com.expense.config;

import static br.com.expense.util.FileUtil.entryLocation;
import br.com.expense.model.TransactionType;

public class Configuration {

	private String rulesLocation;
	private String rulesExtension;
	
	private String transactionExtension;
	private String reportExtension;
	
	private TransactionType transactionTypeFilter;
	
	private boolean enableLogging;
	
	public Configuration(String rulesLocation, String rulesExtension, String transactionExtension, String reportExtension, TransactionType transactionTypeFilter, boolean enableLogging) {
		this.rulesLocation = rulesLocation;
		this.rulesExtension = rulesExtension;
		this.transactionExtension = transactionExtension;
		this.reportExtension = reportExtension;
		this.transactionTypeFilter = transactionTypeFilter;
		this.enableLogging = enableLogging;
	}

	public String getRulesLocation() {
		return rulesLocation;
	}

	public String getRulesExtension() {
		return rulesExtension;
	}

	public String getTransactionExtension() {
		return transactionExtension;
	}
	
	public TransactionType getTransactionTypeFilter() {
		return transactionTypeFilter;
	}
	
	public String getReportExtension() {
		return reportExtension;
	}

	public boolean isEnableLogging() {
		return enableLogging;
	}

	public static Configuration preset() {
		return new Configuration(entryLocation(), ".rules", ".txt", ".csv", null, true);
	}
	
	public static Configuration preset(String rulesLocation) {
		return new Configuration(rulesLocation, ".rules", ".txt", ".csv", null, true);
	}	
	
}
