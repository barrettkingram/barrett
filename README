# barrett

* The name of my programming language is barrett.
* barrett is loosely based on Pascal syntax.
* This document will cover the notable features of the language.

## Introduction
A sample barrett program:

```
var
    X, Y, Z;

function DoSomething(A, B, C);
    var
        Z;
    begin
        Z := A + B + C;
        WriteLn(Z);
    end;

begin
    X := 1;
    Y := 2;
    Z := 3;
    DoSomething(X, Y, Z);
end;
```

A program consists of three segments:
1. Variable declarations
* All variables must be declared in this segment before they can be used.
* Unlike Pascal, variables are not declared with a type.
2. Function definitions
* A function definition consists of the same three segments as a program
* Functions can be nested to arbitrary levels
3. Body
* Can only reference variables and functions that have previously been declared
* Starts with `begin` keyword and ends with `end` keyword followed by a semicolon
* Statements are terminated with semicolon

# Language Features

## Comments
* Comments are surrounded by the Curly Braces {}
* Comments can be single line or span multiple lines
```
var
    X, Y, Z;

{ This is a single line comment }
begin
    X := 1; { This comment spans multiple
    lines }

    Y := 2;
    Z := 3;
    WriteLn(X, Y, Z);
end;
```

## Integers and Strings
* barrett supports implicit integer and string types
```
var
    X, Y, Z;

begin
    X := 1;
    Y := -5;
    Z := "Hello World";
    WriteLn(X, Y, Z);
end;
```

## Arrays
* barrett supports arrays with constant access time
* Like Pascal, arrays in barrett are declared with the allowed range ahead of time
* Declared range is inclusive on both ends
```
var
    { Arrays declared with allowed range along with other variable declarations}
    a[0..5], i, b[2..3];

begin
    a[0] := 4;
    a[1] := 3;
    b[2] := 11;
    WriteLn(a[0] + a[1]);
    WriteLn(b[2]);
end;
```

## Conditionals
* barrett supports conditional statements similar to Pascal
* Unlike Pascal, the body of a conditional must be surrounded by `begin` and `end` even if it is only one statement
* This supports readability and maintainability and guards against careless errors
* Semicolon should follow the `end` keyword unless `end` is followed by an `else`
* Conditional statements can be nested
```
var
    A, B;

begin
    A := 1;
    B := 25;
    if A = 12 then
    begin
        WriteLn("Condition is true");
    end
    else if B = 3 then
    begin
        WriteLn("Second condition is true");
    end
    else
    begin
        WriteLn("Both conditions false");
    end;
end;
```

## Iteration
* barrett uses a `while do` loop similar to Pascal syntax
* Like conditionals, the `while do` loop requires the use of `begin` and `end` surrounding the body
```
var
    I;
begin
    I := 0;
    while I < 10 do
    begin
        WriteLn(I);
        I := I + 1;
    end;
end;
```


## Printing to Console
* barrett has two built-in methods for printing to the console:
1. `WriteLn` prints each of its arguments and finishes by printing a newline
2. `Write` leaves off the newline
* Multiple values can be printed at once by separating them with a comma
```
var
    A, B;
begin
    A := "Hello ";
    B := "World";
    WriteLn(A,B); { Prints "Hello World" }
end;
```


## Operators
* barrett comes built in with several operators:
* Arithmetic operators: `+, -, *, /`
* Boolean operators: `=, <>, <, >`
* Assignment: `:=`
* String concatenation: `+`
* Order of operations is undefined so parenthesis must be used to ensure proper ordering


## Functions
* Functions in barrett have first class status
* Functions can be passed as parameters and used as return values
* Functions can be nested as deeply as desired
* Functions return the last value assigned to the implicit variable "Result", if any
```
function AddThree(Num);
    begin
        WriteLn(Num + 3); { Print the value of the parameter plus 3 }
    end;

function ExecuteFunc(Func, Param);
    begin
        Func(Param);
    end;

begin
    ExecuteFunc(@AddThree, 22);
end;
```


## Anonymous Functions
* barrett has anonymous functions denoted by the `lambda` keyword
* Anonymous functions use `lambda` instead of a function identifier
* Otherwise, Syntax for lambda functions is same as normal function definitions
```
var
    F1, F2;

function StringAppender(str);
    begin
    Result :=
        lambda(x);
        begin
            WriteLn(x + str);
        end;
        ;
    end;

begin
    F1 := StringAppender(";");
    F2 := StringAppender(".");
    F1("Hello"); { prints "Hello;" }
    F2("Hello"); { prints "Hello." }
end;
```


