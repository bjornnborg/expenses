package br.com.expense.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileUtil {
	
	public static String entryLocation() {
		return System.getProperty("user.dir");
	}

	public static String loadFile(String path) {
		System.out.println(">> Loading content from file: " + path);
		StringBuilder content = new StringBuilder();
		Scanner sc = null;
		try {
			sc = new Scanner(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (sc.hasNext()) {
			content.append(sc.nextLine() + "\r\n");
		}
		return content.toString();		
	}
	
	public static String loadFile(File baseDir, String file) {
		return loadFile(new File(baseDir, file).getPath());
	}
	
	public static String loadFiles(File baseDir, String... files) {
		StringBuilder content = new StringBuilder();
		for (String file : files) {
			content.append(loadFile(new File(baseDir, file).getPath()));
		}
		return content.toString();
	}	
}
