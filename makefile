compile:
	javac -sourcepath src -classpath out/production/barrett src/com/barrett/Main.java -d out/production/barrett

clean:
	rm -r -f ./out/production/barrett/*

error1:
	cat error1.barrett

error1x:
	java -classpath out/production/barrett com.barrett.Main error1.barrett

error2:
	cat error2.barrett

error2x:
	java -classpath out/production/barrett com.barrett.Main error2.barrett

error3:
	cat error3.barrett

error3x:
	java -classpath out/production/barrett com.barrett.Main error3.barrett

error4:
	cat error4.barrett

error4x:
	java -classpath out/production/barrett com.barrett.Main error4.barrett

error5:
	cat error5.barrett

error5x:
	java -classpath out/production/barrett com.barrett.Main error5.barrett

arrays:
	cat arrays.barrett

arraysx:
	java -classpath out/production/barrett com.barrett.Main arrays.barrett

conditionals:
	cat conditionals.barrett

conditionalsx:
	java -classpath out/production/barrett com.barrett.Main conditionals.barrett

recursion:
	cat recursion.barrett

recursionx:
	java -classpath out/production/barrett com.barrett.Main recursion.barrett

iteration:
	cat iteration.barrett

iterationx:
	java -classpath out/production/barrett com.barrett.Main iteration.barrett

functions:
	cat functions.barrett

functionsx:
	java -classpath out/production/barrett com.barrett.Main functions.barrett

lambda:
	cat lambda.barrett

lambdax:
	java -classpath out/production/barrett com.barrett.Main lambda.barrett

dictionary:
	cat dictionary.barrett

dictionaryx:
	java -classpath out/production/barrett com.barrett.Main dictionary.barrett

problem:
	cat problem.barrett

problemx:
	java -classpath out/production/barrett com.barrett.Main problem.barrett

