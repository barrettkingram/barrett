package com.barrett.evaluator;

import com.barrett.parser.Lexeme;

public class Closure {

	public Lexeme params;
	public Lexeme body;
	public Environment env;

	public Closure(Lexeme p, Lexeme b, Environment e) {
		params = p;
		body = b;
		env = e;
	}
}
