package com.barrett.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Lexer {

	private BufferedReader reader;
	private int currentLineNumber;
	private boolean commentSection;

	Lexer(String fileName) {
		Charset charset = Charset.forName("US-ASCII");
		Path file = Paths.get(fileName);
		try {
			reader = Files.newBufferedReader(file, charset);
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			System.exit(1);
		}
		currentLineNumber = 1;
		commentSection = false;
	}

	Lexeme lex() {
		skipWhiteSpace();
		Lexeme lexeme = null;
		try {
			reader.mark(1);
			int i = reader.read();
			if (i == -1) {
				return new Lexeme(currentLineNumber,"ENDofINPUT");
			}
			char ch = (char) i;
			switch (ch) {
				case '(':
					lexeme = new Lexeme(currentLineNumber,"OPAREN");
					break;
				case ')':
					lexeme = new Lexeme(currentLineNumber,"CPAREN");
					break;
				case ',':
					lexeme = new Lexeme(currentLineNumber,"COMMA");
					break;
				case '+':
					lexeme = new Lexeme(currentLineNumber,"PLUS");
					break;
				case '*':
					lexeme = new Lexeme(currentLineNumber,"TIMES");
					break;
				case '-':
					lexeme = new Lexeme(currentLineNumber,"MINUS");
					break;
				case '/':
					lexeme = new Lexeme(currentLineNumber,"DIVBY");
					break;
				case '=':
					lexeme = new Lexeme(currentLineNumber,"EQUALS");
					break;
				case '<':
					reader.mark(1);
					if ((char) reader.read() == '>') {
						lexeme = new Lexeme(currentLineNumber, "NOTEQUALS");
					} else {
						lexeme = new Lexeme(currentLineNumber, "LESSTHAN");
						reader.reset();
					}
					break;
				case '>':
					lexeme = new Lexeme(currentLineNumber,"GREATERTHAN");
					break;
				case ':':
					reader.mark(1);
					if ((char) reader.read() == '=') {
						lexeme = new Lexeme(currentLineNumber,"ASSIGN");
					} else {
						reader.reset();
						lexeme = new Lexeme(currentLineNumber,"COLON");
					}
					break;
				case ';':
					lexeme = new Lexeme(currentLineNumber,"SEMI");
					break;
				case '[':
					lexeme = new Lexeme(currentLineNumber, "OBRACK");
					break;
				case ']':
					lexeme = new Lexeme(currentLineNumber, "CBRACK");
					break;
				case '.':
					lexeme = new Lexeme(currentLineNumber, "PERIOD");
					break;
				case '@':
					lexeme = new Lexeme(currentLineNumber, "AT");
					break;
				case '^':
					lexeme = new Lexeme(currentLineNumber, "CARET");
					break;
				case '%':
					lexeme = new Lexeme(currentLineNumber, "MOD");
					break;
				default:
					if (Character.isDigit(ch)) {
						reader.reset();
						lexeme = lexNumber();
					} else if (Character.isLetter(ch)) {
						reader.reset();
						lexeme = lexVariableOrKeyword();
					} else if (ch == '"') {
						lexeme = lexString();
					} else {
						System.err.println("Invalid character: " + ch + "; " + (int) ch);
						System.exit(1);
					}
					break;
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			System.exit(1);
		}
		//System.out.println(lexeme);
		return lexeme;
	}

	private void skipWhiteSpace() {
		try {
			reader.mark(1);
			while (isWhiteSpace(reader.read())) {
				reader.mark(1);
			}
			reader.reset();
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			System.exit(1);
		}
	}

	private boolean isWhiteSpace(int i) throws IOException {
		char c = (char) i;
		if (c == '\n') {
			currentLineNumber++;
		} else if (c == '{') {
			commentSection = true;
		} else if (c == '}') {
			commentSection = false;
			c = (char) reader.read();
			reader.mark(1);
		}
		return Character.isWhitespace(c) || commentSection;
	}

	private Lexeme lexNumber() {
		StringBuilder token = new StringBuilder();
		char ch;
		try {
			reader.mark(1);
			ch = (char) reader.read();
			while (Character.isDigit(ch)) {
				token.append(ch);
				reader.mark(1);
				ch = (char) reader.read();
			}
			reader.reset();
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			System.exit(1);
		}
		return new Lexeme(currentLineNumber,"NUMBER", Integer.valueOf(token.toString()));
	}

	private Lexeme lexVariableOrKeyword() {
		StringBuilder token = new StringBuilder();
		char ch;
		Lexeme lexeme;
		try {
			reader.mark(1);
			ch = (char) reader.read();
			while (Character.isLetter(ch) || Character.isDigit(ch)) {
				token.append(ch);
				reader.mark(1);
				ch = (char) reader.read();
			}
			reader.reset();
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			System.exit(1);
		}
		switch (token.toString().toLowerCase()) {
			case "if":
				lexeme = new Lexeme(currentLineNumber,"IF");
				break;
			case "else":
				lexeme = new Lexeme(currentLineNumber,"ELSE");
				break;
			case "while":
				lexeme = new Lexeme(currentLineNumber,"WHILE");
				break;
			case "function":
				lexeme = new Lexeme(currentLineNumber,"FUNCTION");
				break;
			case "begin":
				lexeme = new Lexeme(currentLineNumber,"BEGIN");
				break;
			case "end":
				lexeme = new Lexeme(currentLineNumber,"END");
				break;
			case "var":
				lexeme = new Lexeme(currentLineNumber,"VAR");
				break;
			case "then":
				lexeme = new Lexeme(currentLineNumber,"THEN");
				break;
			case "do":
				lexeme = new Lexeme(currentLineNumber,"DO");
				break;
			case "lambda":
				lexeme = new Lexeme(currentLineNumber, "LAMBDA");
				break;
			case "true":
				lexeme = new Lexeme(currentLineNumber, "TRUE");
				break;
			case "false":
				lexeme = new Lexeme(currentLineNumber, "FALSE");
				break;
			default:
				lexeme = new Lexeme(currentLineNumber,"ID", token.toString());
				break;
		}
		return lexeme;
	}

	private Lexeme lexString() {
		StringBuilder token = new StringBuilder();
		char ch;
		try {
			ch = (char) reader.read();
			while (ch != '"') {
				token.append(ch);
				ch = (char) reader.read();
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			System.exit(1);
		}
		return new Lexeme(currentLineNumber,"STRING", token.toString());
	}
}
