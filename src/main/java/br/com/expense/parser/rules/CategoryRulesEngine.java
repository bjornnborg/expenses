package br.com.expense.parser.rules;

import java.io.File;
import java.io.FilenameFilter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.expense.config.Configuration;
import br.com.expense.model.Category;
import br.com.expense.util.FileUtil;

public class CategoryRulesEngine {

	private Map<String, Category> categories = new HashMap<String, Category>();
	private Map<String, Pattern> compiledPatterns = new HashMap<String, Pattern>();

	private CategoryRulesEngine(Map<String, Category> categories) {
		this.categories = categories;
	}

	public static CategoryRulesEngine fromConfiguration(Configuration configuration) {
		String[] ruleFiles = filterFiles(configuration);
		String rules = FileUtil.loadFiles(new File(configuration.getRulesLocation()), ruleFiles);
		return new CategoryRulesEngine(processRules(rules));
	}

	public Category getCategoryFor(String description) {
		Category category = null;
		for (String regex : categories.keySet()) {
			Pattern pattern = compiledPatterns.get(regex);
			if (pattern == null) {
				pattern = Pattern.compile(regex);
				compiledPatterns.put(regex, pattern);
			}
			
			Matcher matcher = pattern.matcher(description);
			if (matcher.matches()) {
				category = categories.get(regex);
				break;
			}
		}
		return category;
	}

	private static String[] filterFiles(Configuration config) {
		return new File(config.getRulesLocation()).list(new CategoriesFileFilter(config.getRulesExtension()));
	}

	private static Map<String, Category> processRules(String rules) {
		Map<String, Category> categories = new HashMap<String, Category>();
		
		Scanner scanner = new Scanner(new StringReader(rules));
		while (scanner.hasNextLine()) {
			String[] categorizationInformation = scanner.nextLine().split("=>");
			categories.put(categorizationInformation[0].trim(), new Category(categorizationInformation[1].trim()));			
		}

		return categories;
	}

	private static class CategoriesFileFilter implements FilenameFilter {

		private String extension;

		public CategoriesFileFilter(String extension) {
			this.extension = extension;
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(extension);
		}

	}

}
