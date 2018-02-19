package com.barrett.evaluator;

import com.barrett.parser.Lexeme;

import static com.barrett.evaluator.Value.Type.BOOL;
import static com.barrett.evaluator.Value.Type.INT;
import static com.barrett.evaluator.Value.Type.STRING;

public class Evaluator {

	public static Value eval(Lexeme tree, Environment env) {
		Value val = null;
		switch(tree.type) {
			case "FUNCTION":
				val = evalFuncDef(tree, env);
				break;
			case "FUNCCALL":
				if (isBuiltIn(tree.left.string)) {
					val = evalBuiltIn(tree, env);
				} else {
					val = evalFuncCall(tree, env);
				}
				break;
			case "EXPRESSION":
				val = evalExpression(tree, env);
				break;
			case "BLOCK":
				val = evalBlock(tree, env);
				break;
			case "IFSTATEMENT":
				val = evalIfStatement(tree, env);
				break;
			case "FUNCBLOCK":
				val = evalFuncBlock(tree, env);
				break;
			default:
				System.err.println("Unhandled Lexeme in Evaluator dispatcher: " + tree.type);
				System.exit(1);
		}
		return val;
	}

	private static Value evalFuncBlock(Lexeme tree, Environment env) {
		if (tree.left != null) {
			Lexeme varDefs = tree.left.right;
			while (varDefs != null) {
				if (varDefs.left != null) {
					int iMin = varDefs.left.integer;
					int iMax = varDefs.left.right.integer;
					Array array = new Array(iMin,iMax);
					env.insertEnv(new Value(varDefs.string), new Value(array));
				} else {
					env.insertEnv(new Value(varDefs.string), new Value("NULL"));
				}
				varDefs = varDefs.right;
			}
		}
		if (tree.right.left != null) {
			Lexeme funcDef = tree.right.left;
			while (funcDef != null) {
				evalFuncDef(funcDef.left, env);
				funcDef = funcDef.right;
			}
		}
		return evalBlock(tree.right.right, env);
	}

	private static Value evalFuncDef(Lexeme tree, Environment env) {
		Closure closure = new Closure(tree.right.left, tree.right.right, env);
		Value cValue = new Value(closure);
		env.insertEnv(new Value(tree.left.string), cValue);
		return cValue;
	}

	private static Value evalFuncCall(Lexeme tree, Environment env) {
		Closure closure = env.lookupEnv(tree.left.string, tree.left.lineNumber).closure;
		if (closure != null) {
			Environment cEnv = closure.env;
			Environment xEnv = cEnv.extendEnv();
			Lexeme paramList = closure.params;
			Lexeme argList = null;
			if (tree.right != null) {
				argList = tree.right.right;
			}
			while (paramList != null) {
				if (argList == null) {
					System.err.println("Function: " + tree.left.string
							+ " called with incorrect number of arguments\n\tLine: " + tree.left.lineNumber);
					System.exit(1);
				}
				Value varName = new Value(paramList.string);
				Value varValue = evalExpression(argList.left, env);
				xEnv.insertEnv(varName, varValue);
				paramList = paramList.right;
				argList = argList.right;
			}
			if (argList != null) {
				System.err.println("Function: " + tree.left.string + " called with incorrect number of "
						+ "arguments\n\tLine: " + tree.left.lineNumber);
				System.exit(1);
			}
			xEnv.insertEnv(new Value("Result"), new Value("NULL"));
			return evalFuncBlock(closure.body, xEnv);
		} else { // Variable is not closure, simply return value
			return env.lookupEnv(tree.left.string, tree.left.lineNumber);
		}
	}

	private static Value evalBlock(Lexeme tree, Environment env) {
		Lexeme statement = tree.right;
		while (statement != null) {
			evalStatement(statement.left, env);
			statement = statement.right;
		}
		return env.lookupEnv("Result", 0);
	}

	private static Value evalStatement(Lexeme tree, Environment env) {
		Value result = null;
		switch(tree.right.type) {
			case "EXPRESSION":
				result = evalExpression(tree.right, env);
				break;
			case "IFSTATEMENT":
				result = evalIfStatement(tree.right, env);
				break;
			case "WHILE":
				result = evalWhileLoop(tree.right, env);
				break;
			default:
				System.err.println("Invalid statement type: " + tree.right.type);
				System.exit(1);
				break;
		}
		return result;
	}

	private static Value evalWhileLoop(Lexeme tree, Environment env) {
		Value boolExpr = evalExpression(tree.left, env);
		Value result = null;
		if (boolExpr.type != BOOL) {
			System.out.println("Non-boolean expression used in if statement");
			System.exit(1);
		}
		while (evalExpression(tree.left, env).bool) {
			result = evalBlock(tree.right, env);
		}
		return result;
	}

	private static Value evalIfStatement(Lexeme tree, Environment env) {
		Value boolExpr = evalExpression(tree.left, env);
		Value result = null;
		if (boolExpr.type != BOOL) {
			System.out.println("Non-boolean expression used in if statement");
			System.exit(1);
		}
		if (boolExpr.bool) {
			result = eval(tree.right.left, env);
		} else if (tree.right.right != null) {
			result = eval(tree.right.right.right, env);
		}
		return result;
	}

	private static Value evalExpression(Lexeme tree, Environment env) {
		Value val = null;
		if (tree.right == null) {
			val = evalUnary(tree.left, env);
		} else {
			switch(tree.right.left.type) {
				case "PLUS":
					val = evalPlus(tree, env);
					break;
				case "TIMES":
					val = evalTimes(tree, env);
					break;
				case "MINUS":
					val = evalMinus(tree, env);
					break;
				case "DIVBY":
					val = evalDivision(tree, env);
					break;
				case "ASSIGN":
					val = evalAssignment(tree, env);
					break;
				case "EQUALS":
					val = evalEquals(tree, env);
					break;
				case "LESSTHAN":
					val = evalLessThan(tree, env);
					break;
				case "GREATERTHAN":
					val = evalGreaterThan(tree, env);
					break;
				case "NOTEQUALS":
					val = evalNotEquals(tree, env);
					break;
				case "MOD":
					val = evalMod(tree, env);
					break;
				default:
					System.err.println("Unsupported operator: " + tree.right.left.type);
					break;
			}
		}
		return val;
	}

	private static Value evalUnary(Lexeme lexeme, Environment env) {
		Value val = null;
		switch (lexeme.type) {
			case "NUMBER":
				val = new Value(lexeme.integer);
				break;
			case "STRING":
				val = new Value(lexeme.string);
				break;
			case "TRUE":
				val = new Value(true);
				break;
			case "FALSE":
				val = new Value(false);
				break;
			case "EXPRESSION":
				val = evalExpression(lexeme, env);
				break;
			case "FUNCCALL":
				val = eval(lexeme, env);
				break;
			case "NEGNUM":
				val = new Value(evalUnary(lexeme.right, env).integer * -1);
				break;
			case "ARRAY":
				val = evalArrayAccess(lexeme, env);
				break;
			case "FUNCREF":
				val = new Value(env.lookupEnv(lexeme.right.string, lexeme.right.lineNumber).closure);
				break;
			case "LAMBDA":
				Closure closure = new Closure(lexeme.left, lexeme.right, env);
				val = new Value(closure);
				break;
		}
		return val;
	}

	private static Value evalArrayAccess(Lexeme tree, Environment env) {
		Value varName = new Value(tree.left.string);
		Array arr = env.lookupEnv(varName.string, tree.left.lineNumber).array;
		Value index = evalExpression(tree.left.left, env);
		Value result = null;
		if (index.integer == null) {
			System.err.println("Array error: index expression fails to evaluate to integer.\n\tEvaluates to "
			+ index.type);
			System.exit(1);
		} else {
			result = arr.get(evalExpression(tree.left.left, env).integer);
		}
		return result;
	}

	private static Value evalPlus(Lexeme tree, Environment env) {
		Value result = null;
		Value val1 = evalUnary(tree.left, env);
		Value val2 = evalExpression(tree.right.right, env);
		if (val1.type == INT && val2.type == INT) {
			result = new Value(val1.integer + val2.integer);
		} else if (val1.type == STRING && val2.type == STRING) {
			result = new Value(val1.string + val2.string);
		} else {
			System.err.println("Incompatible types for '+' operator:\n\t"  + val1 + ": " + val1.getType()
					+ "\n\t" + val2 + ": " + val2.getType() + "\n\tLine: " + tree.right.left.lineNumber);
			System.exit(1);
		}
		return result;
	}

	private static Value evalTimes(Lexeme tree, Environment env) {
		Value result = null;
		Value val1 = evalUnary(tree.left, env);
		Value val2 = evalExpression(tree.right.right, env);
		if (val1.type == INT && val2.type == INT) {
			result = new Value(val1.integer * val2.integer);
		} else {
			System.err.println("Incompatible types for '*' operator:\n\t"  + val1 + ": " + val1.getType()
					+ "\n\t" + val2 + ": " + val2.getType());
			System.exit(1);
		}
		return result;
	}

	private static Value evalMinus(Lexeme tree, Environment env) {
		Value result = null;
		Value val1 = evalUnary(tree.left, env);
		Value val2 = evalExpression(tree.right.right, env);
		if (val1.type == INT && val2.type == INT) {
			result = new Value(val1.integer - val2.integer);
		} else {
			System.err.println("Incompatible types for '-' operator:\n\t" + val1 + ": " + val1.getType()
					+ "\n\t" + val2 + ": " + val2.getType());
			System.exit(1);
		}
		return result;
	}

	private static Value evalDivision(Lexeme tree, Environment env) {
		Value result = null;
		Value val1 = evalUnary(tree.left, env);
		Value val2 = evalExpression(tree.right.right, env);
		if (val1.type == INT && val2.type == INT) {
			result = new Value(val1.integer / val2.integer);
		} else {
			System.err.println("Incompatible types for '/' operator:\n\t" + val1 + ": " + val1.getType()
					+ "\n\t" + val2 + ": " + val2.getType());
			System.exit(1);
		}
		return result;
	}

	private static Value evalMod(Lexeme tree, Environment env) {
		Value result = null;
		Value val1 = evalUnary(tree.left, env);
		Value val2 = evalExpression(tree.right.right, env);
		if (val1.type == INT && val2.type == INT) {
			result = new Value(val1.integer % val2.integer);
		} else {
			System.err.println("Incompatible types for '%' operator:\n\t" + val1 + ": " + val1.getType()
					+ "\n\t" + val2 + ": " + val2.getType());
			System.exit(1);
		}
		return result;
	}

	private static Value evalAssignment(Lexeme tree, Environment env) {
		Value varName = new Value(tree.left.left.string);
		Value result = evalExpression(tree.right.right, env);
		if (tree.left.type.equals("ARRAY")) {
			Array arr = env.lookupEnv(varName.string, tree.left.left.lineNumber).array;
			int index = evalExpression(tree.left.left, env).integer;
			if (arr.indexInRange(index)) {
				arr.update(index, result);
			} else {
				System.err.println("Attempted to access out of range index");
				System.exit(1);
			}
		} else {
			env.updateEnv(varName, result);
		}
		return result;
	}

	private static Value evalEquals(Lexeme tree, Environment env) {
		Value result;
		Value val1 = evalUnary(tree.left, env);
		Value val2 = evalExpression(tree.right.right, env);
		if (val1.type == INT && val2.type == INT) {
			result = new Value(val1.integer.equals(val2.integer));
		} else if (val1.type == STRING && val2.type == STRING) {
			result = new Value(val1.string.equals(val2.string));
		} else if (val1.type == BOOL && val2.type == BOOL) {
			result = new Value(val1.bool == val2.bool);
		} else {
			result = new Value(false);
		}
		return result;
	}
	private static Value evalNotEquals(Lexeme tree, Environment env) {
		Value result;
		Value val1 = evalUnary(tree.left, env);
		Value val2 = evalExpression(tree.right.right, env);
		if (val1.type == INT && val2.type == INT) {
			result = new Value(!val1.integer.equals(val2.integer));
		} else if (val1.type == STRING && val2.type == STRING) {
			result = new Value(!val1.string.equals(val2.string));
		} else if (val1.type == BOOL && val2.type == BOOL) {
			result = new Value(val1.bool != val2.bool);
		} else {
			result = new Value(true);
		}
		return result;
	}

	private static Value evalLessThan(Lexeme tree, Environment env) {
		Value result = null;
		Value val1 = evalUnary(tree.left, env);
		Value val2 = evalExpression(tree.right.right, env);
		if (val1.type == INT && val2.type == INT) {
			result = new Value(val1.integer < val2.integer);
		} else if (val1.type == STRING && val2.type == STRING) {
			result = new Value(val1.string.compareTo(val2.string) < 0);
		} else {
			System.err.println("Incompatible types for '<' operator:\n\t"  + val1 + ": " + val1.getType()
					+ "\n\t" + val2 + ": " + val2.getType());
			System.exit(1);
		}
		return result;
	}

	private static Value evalGreaterThan(Lexeme tree, Environment env) {
		Value result = null;
		Value val1 = evalUnary(tree.left, env);
		Value val2 = evalExpression(tree.right.right, env);
		if (val1.type == INT && val2.type == INT) {
			result = new Value(val1.integer > val2.integer);
		} else if (val1.type == STRING && val2.type == STRING) {
			result = new Value(val1.string.compareTo(val2.string) > 0);
		} else {
			System.err.println("Incompatible types for '>' operator:\n\t"  + val1 + ": " + val1.getType()
					+ "\n\t" + val2 + ": " + val2.getType());
			System.exit(1);
		}
		return result;
	}

	private static boolean isBuiltIn(String f) {
		String funcName = f.toUpperCase();
		return funcName.equals("WRITELN") || funcName.equals("WRITE");
	}

	private static Value evalBuiltIn(Lexeme tree, Environment env) {
		switch(tree.left.string.toUpperCase()) {
			case "WRITELN":
				if (tree.right != null) {
					Lexeme arg = tree.right.right;
					while (arg != null) {
						System.out.print(evalExpression(arg.left, env));
						arg = arg.right;
					}
				}
				System.out.println();
				break;
			case "WRITE":
				if (tree.right != null) {
					Lexeme arg = tree.right.right;
					while (arg != null) {
						System.out.print(evalExpression(arg.left, env));
						arg = arg.right;
					}
				}
				break;
			default:
				System.err.println("Error: no function defined for built-in: " + tree.left.string);
				System.exit(1);
				break;
		}
		return new Value(0);
	}
}
