package com.barrett.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Lexeme {

	public String type;
	public int lineNumber;
	public String string;
	public Integer integer;
	public Lexeme left;
	public Lexeme right;

	Lexeme(int line, String t) {
		lineNumber = line;
		type = t;
		string = null;
		integer = null;
		left = null;
		right = null;
	}

	Lexeme(String t) {
		lineNumber = -1;
		type = t;
		string = null;
		integer = null;
		left = null;
		right = null;
	}

	Lexeme(int line, String t, String s) {
		lineNumber = line;
		type = t;
		string = s;
		integer = null;
		left = null;
		right = null;
	}

	Lexeme(int line, String t, Integer i) {
		lineNumber = line;
		type = t;
		string = null;
		integer = i;
		left = null;
		right = null;
	}

	public String toString() {
		String str = type;
		if (string != null) {
			str = str + ": " + string;
		} else if (integer != null) {
			str = str + ": " + integer;
		}
		return str;
	}

	void printContext(String fileName) {
		Charset charset = Charset.forName("US-ASCII");
		BufferedReader reader;
		Path file = Paths.get(fileName);
		String lineValue = "";
		try {
			reader = Files.newBufferedReader(file, charset);
			int currentLine = 0;
			while (currentLine < lineNumber - 1) {
				lineValue = reader.readLine();
				currentLine++;
			}
			if (currentLine > 0) {
				System.err.println(currentLine + ": " + lineValue);
			}
			lineValue = reader.readLine();
			currentLine++;
			System.err.println(currentLine + ": " + lineValue);
			lineValue = reader.readLine();
			currentLine++;
			if (lineValue != null) {
				System.err.println(currentLine + ": " + lineValue);
			}

		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			System.exit(1);
		}
	}
}
