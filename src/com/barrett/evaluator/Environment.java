package com.barrett.evaluator;

import java.util.HashMap;

import static com.barrett.evaluator.Value.Type.STRING;

public class Environment {

	private HashMap<String, Value> table;
	Environment parent;

	public Environment(Environment p) {
		table = new HashMap<>();
		parent = p;
	}

	public void insertEnv(Value v, Value w) {
		if (v.type != STRING) {
			System.err.println("Invalid variable name: " + v);
			System.exit(1);
		}
		table.put(v.string, w);
	}

	public void updateEnv(Value v, Value w) {
		if (v.type != STRING) {
			System.err.println("Invalid variable name: " + v);
			System.exit(1);
		}
		if (table.containsKey(v.string)) {
			table.put(v.string, w);
		} else if (parent != null) {
			parent.updateEnv(v, w);
		} else {
			System.err.println("Variable not found: " + v);
			System.exit(1);
		}
	}

	public Value lookupEnv(String v, int lineNum) {
		if (table.containsKey(v)) {
			return table.get(v);
		} else if (parent != null) {
			return parent.lookupEnv(v, lineNum);
		} else {
			if (!v.equals("Result")) {
				System.err.println("function or variable " + v + " not found in any enclosing scope" +
						"\n\tLine: " + lineNum);
				System.exit(1);
			}
			return null;
		}
	}

	public Environment extendEnv() {
		Environment env = new Environment(this);
		return env;
	}

}
