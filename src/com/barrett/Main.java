package com.barrett;

import com.barrett.evaluator.Environment;
import com.barrett.evaluator.Evaluator;
import com.barrett.parser.Lexeme;
import com.barrett.parser.Parser;
import com.barrett.parser.PrettyPrinter;

public class Main {

    public static void main(String[] args) {
        Parser parser = new Parser();
        Lexeme parseTree = parser.parse(args[0]);
        //PrettyPrinter.print(parseTree);

	    Environment topLevel = new Environment(null);
        Evaluator.eval(parseTree, topLevel);
    }
}
