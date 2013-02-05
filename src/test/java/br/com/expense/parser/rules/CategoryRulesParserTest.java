package br.com.expense.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import br.com.expense.model.Category;

public class CategoryRulesParserTest {
	
	@Test
	public void shouldIgnoreBlankAndComments() {
		String rulesLines = 
				"# regras para alimentação e supermercado\r\n" +
				".*PAO DE ACUCAR.* => supermercado\r\n" +
				".*JARDIM SOPHIE.* => alimentação\r\n" +
				"\r\n" +
				"#regras outros gastos\r\n" +
				".*NETMOVIES.* => casa\r\n";
				
		Map<String, Category> rules = new CategoryRulesParser().processRules(rulesLines);
		assertNotNull(rules);
		assertEquals(3, rules.keySet().size());
		assertTrue(rules.keySet().contains(".*PAO DE ACUCAR.*"));
	}
	
	@Test
	public void shouldUseNoCategoryWithInvalidCategoryToken() {
		String rulesLines = ".*PAO DE ACUCAR.* = > supermercado";
		Map<String, Category> categoriesByRegex = new CategoryRulesParser().processRules(rulesLines);
		assertNotNull(categoriesByRegex);
		assertEquals(1, categoriesByRegex.keySet().size());
		assertEquals(null, categoriesByRegex.get(".*PAO DE ACUCAR.*"));
	}
	
	@Test
	public void shouldAllowInlineRuleComments() {
		String rulesLines = ".*PAO DE ACUCAR.* => supermercado -- comments";
		Map<String, Category> categoriesByRegex = new CategoryRulesParser().processRules(rulesLines);
		assertNotNull(categoriesByRegex);
		assertEquals(1, categoriesByRegex.keySet().size());
		assertEquals("supermercado", categoriesByRegex.get(".*PAO DE ACUCAR.*").getName());
	}	

}
