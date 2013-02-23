package br.com.expense.parser.rules;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.expense.config.Configuration;
import br.com.expense.model.Category;
import br.com.expense.util.FileUtil;

public class CategoryRulesEngine {

	private Map<String, Category> categories = new HashMap<String, Category>();
	private Map<String, Pattern> compiledPatterns = new HashMap<String, Pattern>();
	
	public CategoryRulesEngine(Configuration configuration, CategoryRulesParser rulesParser) {
		String[] ruleFiles = filterFiles(configuration);
		categories = rulesParser.processRules(FileUtil.loadFiles(new File(configuration.getRulesLocation()), ruleFiles));		
	}	

	public Category getCategoryFor(String description) {
		Category category = null;
		for (String regex : categories.keySet()) {
			Pattern pattern = compiledPatterns.get(regex);
			if (pattern == null) {
				pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
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
