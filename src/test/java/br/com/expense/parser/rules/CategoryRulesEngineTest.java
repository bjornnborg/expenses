package br.com.expense.parser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;

import org.junit.Test;

import br.com.expense.config.Configuration;
import br.com.expense.model.Category;
import br.com.expense.model.Transaction;
import br.com.expense.parser.BaseParserTest;

public class CategoryRulesEngineTest extends BaseParserTest{
	
	@Test
	public void categorize() throws FileNotFoundException {
		CategoryRulesEngine rulesEngine = CategoryRulesEngine.fromConfiguration(Configuration.preset(getPath(".")));
		Transaction transaction = new Transaction();
		transaction.setDescription("PAO DE ACUCAR");
		Category category = rulesEngine.getCategoryFor(transaction.getDescription());
		assertNotNull(category);
		assertEquals("supermercado", category.getName());
	}

}
