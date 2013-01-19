package br.com.expense.parser;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import br.com.expense.util.FileUtil;

public class BaseParserTest {

	public String loadFile(String path) throws FileNotFoundException {
		return FileUtil.loadFile(this.getResourceURI(path).getRawPath());
	}
	
	public String getPath(String path) throws FileNotFoundException {
		return this.getResourceURI(path).getRawPath();
	}	
	
	private URI getResourceURI(String name) {
		URI uri = null;
		try {
			uri = this.getClass().getClassLoader().getResource(name).toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;
	}
	
}
