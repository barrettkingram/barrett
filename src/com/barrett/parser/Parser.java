package com.barrett.parser;

public class Parser {
	private Lexeme currentLexeme;
	private Lexer lexer;
	private String fileName;

	public Parser() {
		currentLexeme = null;
	}

	public Lexeme parse(String fileName) {
		lexer = new Lexer(fileName);
		this.fileName = fileName;

		currentLexeme = lexer.lex();
		Lexeme parseTree = functionBlock();
		if (!check("ENDofINPUT")) {
			error("Failed to find any further definitions and haven't reached end of input");
		}
		return parseTree;
	}

	private boolean check(String type) {
		return type.equals(currentLexeme.type);
	}

	private Lexeme advance() {
		Lexeme curr = currentLexeme;
		currentLexeme = lexer.lex();
		return curr;
	}

	private Lexeme match(String type) {
		matchNoAdvance(type);
		//System.out.println("Matched " + type);
		return advance();
	}

	private void error(String msg) {
		System.err.println(msg + "\n\tCurrent Lexeme is " + currentLexeme.type + "\n\tLine "
				+ currentLexeme.lineNumber);
		currentLexeme.printContext(fileName);
		System.exit(1);
	}

	private void matchNoAdvance(String type) {
		if (!check(type)) {
			switch(type) {
				case "SEMI":
					error("Parse error; Expected semicolon");
					break;
				case "BEGIN":
					error("Parse error; Expected \"begin\" keyword");
					break;
				case "CPAREN":
					error("Parse error; Expected closing parenthesis");
					break;
				default:
					error("Lexeme match failure: expected " + type);
					break;
			}
		}
	}

	private Lexeme functionBlock() {
		Lexeme tree = new Lexeme("FUNCBLOCK");
		tree.left = optVarDefs();
		tree.right = new Lexeme("GLUE");
		tree.right.left = optFuncDefs();
		tree.right.right = block();
		match("SEMI");
		return tree;
	}

	private Lexeme optVarDefs() {
		Lexeme tree = null;
		if (check("VAR")) {
			tree = match("VAR");
			tree.right = variables();
			match("SEMI");
		}
		return tree;
	}

	private Lexeme optFuncDefs() {
		Lexeme tree = null;
		if (funcDefPending()) {
			tree = new Lexeme("FUNCDEFS");
			tree.left = funcDef();
			tree.right = optFuncDefs();
		}
		return tree;
	}

	private Lexeme variables() {
		Lexeme tree = variable();
		if (check("COMMA")) {
			match("COMMA");
			tree.right = variables();
		}
		return tree;
	}

	private Lexeme variable() {
		Lexeme tree = match("ID");
		if (check("OBRACK")) {
			match("OBRACK");
			tree.left = match("NUMBER");
			match("PERIOD");
			match("PERIOD");
			tree.left.right = match("NUMBER");
			match("CBRACK");
		}
		return tree;
	}

	private Lexeme funcDef() {
		Lexeme tree = match("FUNCTION");
		tree.left = match("ID");
		tree.right = new Lexeme("GLUE");
		tree.right.left = optParamList();
		match("SEMI");
		tree.right.right = functionBlock();
		return tree;
	}

	private boolean funcDefPending() {
		return check("FUNCTION");
	}

	private Lexeme optParamList() {
		Lexeme tree = null;
		if (check("OPAREN")) {
			match("OPAREN");
			tree = paramList();
			match("CPAREN");
		}
		return tree;
	}

	private Lexeme optArgList() {
		Lexeme tree = null;
		if (check("OPAREN")) {
			match("OPAREN");
			tree = new Lexeme("ARGLIST");
			tree.right = argList();
			match("CPAREN");
		}
		return tree;
	}

	private Lexeme argList() {
		Lexeme tree = new Lexeme("ARG");
		tree.left = expression();
		if (check("COMMA")) {
			match("COMMA");
			tree.right = argList();
		}
		return tree;
	}

	private Lexeme paramList() {
		Lexeme tree = match("ID");
		if (check("COMMA")) {
			match("COMMA");
			tree.right = paramList();
		}
		return tree;
	}

	private Lexeme block() {
		match("BEGIN");
		Lexeme tree = new Lexeme("BLOCK");
		tree.right = statements();
		match("END");
		return tree;
	}

	private boolean blockPending() {
		return check("BEGIN");
	}

	private Lexeme statements() {
		Lexeme tree = new Lexeme("STATEMENTS");
		tree.left = statement();
		if (statementsPending()) {
			tree.right = statements();
		}
		return tree;
	}

	private boolean statementsPending() {
		return statementPending();
	}

	private Lexeme statement() {
		Lexeme tree = new Lexeme("STATEMENT");
		if (expressionPending()) {
			tree.right = expression();
			match("SEMI");
		} else if (ifStatementPending()) {
			tree.right = ifStatement();
		} else if (whileLoopPending()) {
			tree.right = whileLoop();
		} else {
			error("Invalid statement. Statement must be expression, conditional, or loop");
		}
		return tree;
	}

	private boolean statementPending() {
		return expressionPending() || ifStatementPending() || whileLoopPending();
	}

	private Lexeme expression() {
		Lexeme tree = new Lexeme("EXPRESSION");
		tree.left = unary();
		if (operatorPending()) {
			tree.right = new Lexeme("GLUE");
			tree.right.left = operator();
			tree.right.right = expression();
		}
		return tree;
	}

	private boolean expressionPending() {
		return unaryPending();
	}

	private Lexeme ifStatement() {
		Lexeme tree = new Lexeme("IFSTATEMENT");
		match("IF");
		tree.left = expression();
		match("THEN");
		tree.right = new Lexeme("GLUE");
		tree.right.left = block();
		tree.right.right = optElse();
		return tree;
	}

	private boolean ifStatementPending() {
		return check("IF");
	}

	private Lexeme whileLoop() {
		Lexeme tree = match("WHILE");
		tree.left = expression();
		match("DO");
		tree.right = block();
		match("SEMI");
		return tree;
	}

	private boolean whileLoopPending() {
		return check("WHILE");
	}

	private Lexeme unary() {
		Lexeme tree = null;
		if (check("NUMBER")) {
			tree = match("NUMBER");
		} else if (check("STRING")) {
			tree = match("STRING");
		} else if (check("TRUE")) {
			tree = match("TRUE");
		} else if (check("FALSE")) {
			tree = match("FALSE");
		} else if (check("OPAREN")) {
			match("OPAREN");
			tree = expression();
			match("CPAREN");
		} else if (check("AT")) {
			match("AT");
			tree = new Lexeme("FUNCREF");
			tree.right = match("ID");
		} else if (check("LAMBDA")) {
			tree = match("LAMBDA");
			tree.left = optParamList();
			match("SEMI");
			tree.right = functionBlock();
		} else if (check("ID")) {
			Lexeme node = match("ID");
			if (check("OBRACK")) {
				match("OBRACK");
				tree = new Lexeme("ARRAY");
				tree.left = node;
				tree.left.left = expression();
				match("CBRACK");
			} else {
				tree = new Lexeme("FUNCCALL");
				tree.left = node;
				tree.right = optArgList();
			}
		} else if (check("MINUS")) {
			tree = new Lexeme("NEGNUM");
			match("MINUS");
			tree.right = unary();
		} else {
			error("Expected unary");
		}
		return tree;
	}

	private boolean unaryPending() {
		return check("NUMBER") || check("OPAREN") || check("ID") || check("MINUS")
				|| check("TRUE") || check("FALSE");
	}

	private Lexeme operator() {
		Lexeme tree = null;
		if (check("PLUS")) {
			tree = match("PLUS");
		} else if (check("TIMES")) {
			tree = match("TIMES");
		} else if (check("MINUS")) {
			tree = match("MINUS");
		} else if (check("DIVBY")) {
			tree = match("DIVBY");
		} else if (check("ASSIGN")) {
			tree = match("ASSIGN");
		} else if (check("GREATERTHAN")) {
			tree = match("GREATERTHAN");
		} else if (check("LESSTHAN")) {
			tree = match("LESSTHAN");
		} else if (check("EQUALS")) {
			tree = match("EQUALS");
		} else if (check("NOTEQUALS")) {
			tree = match("NOTEQUALS");
		} else if (check("MOD")) {
			tree = match("MOD");
		} else {
			error("Expected operator");
		}
		return tree;
	}

	private boolean operatorPending() {
		return check("PLUS") || check("TIMES") || check("MINUS") || check("DIVBY")
				|| check("ASSIGN") || check("GREATERTHAN") || check("LESSTHAN")
				|| check("EQUALS") || check("NOTEQUALS") || check("MOD");
	}

	private Lexeme optElse() {
		Lexeme tree = null;
		if (check("ELSE")) {
			tree = match("ELSE");
			if (blockPending()) {
				tree.right = block();
				match("SEMI");
			} else if (ifStatementPending()) {
				tree.right = ifStatement();
			} else {
				error("Expected block or if conditional after else keyword");
			}
		} else if (check("SEMI")) {
			match("SEMI");
		} else {
			error("Must end begin-else block with semicolon");
		}
		return tree;
	}

}
