package com.barrett.parser;

public class PrettyPrinter {
	public static void print(Lexeme tree) {
		if (tree != null) {
			switch (tree.type) {
				case "NUMBER":
					System.out.print(tree.integer);
					break;
				case "STRING":
					System.out.print("\"" + tree.string + "\"");
					break;
				case "ID":
					System.out.print(tree.string);
					break;
				case "EQUALS":
					System.out.print("=");
					break;
				case "ASSIGN":
					System.out.print(":=");
					break;
				case "PLUS":
					System.out.print("+");
					break;
				case "MINUS":
					System.out.print("-");
					break;
				case "TIMES":
					System.out.print("*");
					break;
				case "DIVBY":
					System.out.print("/");
					break;
				case "GREATERTHAN":
					System.out.print(">");
					break;
				case "LESSTHAN":
					System.out.print("<");
					break;
				case "BLOCK":
					System.out.println("begin");
					print(tree.right);
					System.out.print("end");
					break;
				case "STATEMENTS":
					print(tree.left);
					print(tree.right);
					break;
				case "STATEMENT":
					print(tree.right);
					if (tree.right.type.equals("EXPRESSION")) {
						System.out.print(";\n");
					}
					break;
				case "EXPRESSION":
					print(tree.left);
					if (tree.right != null) {
						System.out.print(" ");
						print(tree.right.left);
						System.out.print(" ");
						print(tree.right.right);
					}
					break;
				case "FUNCCALL":
					print(tree.left);
					print(tree.right);
					break;
				case "ARGLIST":
					System.out.print("(");
					print(tree.right);
					System.out.print(")");
					break;
				case "ARG":
					print(tree.left);
					if (tree.right != null) {
						System.out.println(",");
						print(tree.right);
					}
					break;
				case "FUNCBLOCK":
					print(tree.left);
					print(tree.right.left);
					print(tree.right.right);
					System.out.println(";");
					break;
				case "VAR":
					System.out.println("var");
					while (tree.right != null) {
						print(tree.right);
						tree = tree.right;
						if (tree.right != null) {
							System.out.print(",");
						}
					}
					System.out.println(";");
					break;
				case "FUNCDEFS":
					print(tree.left);
					print(tree.right);
					break;
				case "FUNCTION":
					System.out.print("function " + tree.left.string);
					if (tree.right != null) {
						print(tree.right.left);
					}
					System.out.println(";");
					print(tree.right.right);
					break;
				case "PARAMLIST":
					System.out.print("(");
					while (tree.right != null) {
						print(tree.right);
						tree = tree.right;
						if (tree.right != null) {
							System.out.print(", ");
						}
					}
					System.out.print(")");
					break;
				case "IFSTATEMENT":
					System.out.print("if ");
					print(tree.left);
					System.out.println(" then");
					print(tree.right.left);
					if (tree.right.right != null) {
						System.out.println();
						print(tree.right.right);
					} else {
						System.out.println(";");
					}
					break;
				case "ELSE":
					System.out.print("else");
					if (tree.right.type.equals("BLOCK")) {
						System.out.println();
						print(tree.right);
						System.out.println(";");
					} else {
						System.out.print(" ");
						print(tree.right);
					}
					break;
				case "WHILE":
					System.out.print("while ");
					print(tree.left);
					System.out.println(" do");
					print(tree.right);
					System.out.println(";");
					break;
				default:
					System.err.println("Pretty print failure. Unhandled Lexeme: " + tree.type);
			}
		}
	}
}
