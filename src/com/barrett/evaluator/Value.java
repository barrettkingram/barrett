package com.barrett.evaluator;

import static com.barrett.evaluator.Value.Type.*;

public class Value {

	public enum Type {STRING, INT, REAL, BOOL, CLOSURE, ARRAY}

	public Type type;
	public Integer integer;
	public String string;
	public Closure closure;
	public Boolean bool;
	public Array array;
	public int lineNumber;

	public Value(Integer i) {
		type = INT;
		integer = i;
	}

	public Value(Boolean b) {
		type = BOOL;
		bool = b;
	}


	public Value(String s) {
		type = STRING;
		string = s;
	}

	public Value(Closure c) {
		type = CLOSURE;
		closure = c;
	}

	public Value(Array a) {
		type = ARRAY;
		array = a;
	}

	public String toString() {
		if (type == INT) {
			return integer.toString();
		} else if (type == STRING) {
			return string;
		} else {
			return "closure";
		}
	}

	public String getType() {
		if (type == INT) {
			return "INT";
		} else if (type == REAL) {
			return "REAL";
		} else if (type == STRING) {
			return "STRING";
		} else {
			return "CLOSURE";
		}
	}
}
