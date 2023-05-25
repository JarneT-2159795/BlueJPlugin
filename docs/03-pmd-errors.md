---
layout: home
title: PMD suggestions
permalink: /pmd-errors
---

The explanation of all errors in more detail can be found in the <a href="https://docs.pmd-code.org/latest/pmd_rules_java_bestpractices.html" target="_blank">PMD documentation</a>. This page contains the checks for the IIW students, adapted from the PMD documentation.

* AbstractClassWithoutAbstractMethod: your abstract class has no abstract methods. Make sure your class needs to be abstract or add an abstract method.
* AvoidPrintStackTrace: printing the stacktrace when something goes wrong is not useful for the user. Consider handling the error yourself by creating an appropriate response.
* ConstantsInInterface: you should not use a constant in your interface. Interfaces are designed to define types. Constant are implementation details which are better placed classes or enums.
* DefaultLabelNotLastInSwitchStmt: the convention is to place the default statement as the last statement.
* ForLoopCanBeForeach: you can replace this `for` loop with a `forEach` loop.
* SwitchStmtsShouldHaveDefault: the switch statement does not have a default statement. Consider adding a default statement as this can avoid creating errors in runtime when an unhandled case does reach the switch statement.
* UnusedAssignment: a value is assigned to the variable but it is never read.
* UnusedFormalParameter: the parameter of the method is never used.
* UnusedLocalVariable: the variable is never used.
* UnusedPrivateField: the private variable in the class is never used.
* UnusedPrivateMethod: the private method in the class is never used.
* WhileLoopWithLiteralBoolean: a mistake has been made with the `while` loop. It might run forever or never run at all. Look <a href="https://docs.pmd-code.org/latest/pmd_rules_java_bestpractices.html#whileloopwithliteralboolean" target="_blank">here</a> for some examples as to what may have gone wrong.
* ClassNamingConventions: class names should written in Pascal Case. This means that the first letter of every word in the name should be written with a capital letter.
* FieldNamingConventions: this rule checks a couple of different variables types.
    * Constant variables: constant are written in all capital letters and use an underscore (_) as a space between words.
    * Final and static variables: these variables should be written in camelcase. This means that all words in the name should start with a capital letter, except the first word.
    * Other variables: all other variables also use camelcase meaning that all words in the name should start with a capital letter, except the first word.
* LinguisticNaming: this checks if the names of methods and variables are confusing. This does not mean that it is wrong like the previous suggestions about different case styles but might give the wrong impression. Say for example you have a variable `isImportant`. By just reading it you automatically assume this will be a boolean. This check makes sure that the names of variables and methods always reflect their type.
* MethodNamingConventions: the method is not written in camelcase. Camelcase means that all words in the name should start with a capital letter, except the first word.
* AtLeastOneConstructor: always create at least one constructor for the class. Even when it is empty it shows clear intention and can avoid confusing behavior when Java creates an automatic constructor.
* AvoidDollarSigns: do not use dollar signs ($) in names.
* BooleanGetMethodName: methods with a boolean return value should reflect this in their name. This can be done by adding a prefix like "get", "has" or "is". For example `getValue()` or `isReady()`.
* CallSuperInConstructor: you should always call the `super()` method in the constructor of a derived class.
* ConfusingTernary: avoid using a negation in an `if-else` expression.
* EmptyMethodInAbstractClassShouldBeAbstract: when a method in an abstract class is empty you should make it an abstract method. This way the class cannot be used in derived classes, causing unexpected behavior.
* ExtendsObject: all classes automatically extend from the `Object` class. It does not have to be explicitly stated.
* FieldDeclarationsShouldBeAtStartOfClass: all variables should be declared at the top of the class, before all methods.
* ForLoopShouldBeWhileLoop: this `for` loop can be simplified to a `while` loop making the code more readable.
* LogicInversion: you should use the opposite comparator (e.g., `a != b` instead of `!(a == b)` or `a > b` instead of `!(a <= b)`).
* SimplifiedTernary: the ternary can be simplified to a more readable form. Look <a href="https://docs.pmd-code.org/latest/pmd_rules_java_design.html#simplifiedternary" target="_blank">here</a> for some examples.
* SimplifyBooleanReturns: the `if-else` expression can be replaced by simply returning the variable itself.
* SingularField: the variable can be converted to a local variable instead of a class variable.
* UselessOverridingMethod: the overridden method only calls the super method. This makes the method unnecessary.
* AssignmentInOperand: a variable is assigned in another operand. This makes the code harder to read.
* AvoidMultipleUnaryOperators: combining unary operators (e.g., `i = - -1`) is often a typo and hard to read none the less.
* BrokenNullCheck: when you check for `null` in this way, the check itself will throw a `NullPointerException`.
* CompareObjectsWithEquals: you should always use the `.equals()` method instead of using the `==` operator, except when checking for `null`.
* EqualsNull: testing for `null` should not be done using the `.equals()` method. Instead, use the `==` operator.
* ImplicitSwitchFallThrough: you should use a `break` statement at the end of a `switch` clause. Otherwise, the next clauses will also be executed.
* JumbledIncrementer: you might be incrementing the wrong variable in this loop.
* MisplacedNullCheck: the `null` check is put in the wrong place and will not be useful.
* NonCaseLabelInSwitchStatement: every clause should start with 'case'.
* UnconditionalIfStatement: this `if` statement is always `true` or `false`.
* UnnecessaryConversionTemporary: intermediate objects should not be used in type conversions.
* UnusedNullCheckInEquals: when you check if an object is not `null`, you should call the `.equals()` method on this object rather than the object you want to compare with.
* EmptyControlStatement: this means that you have a control structure with an empty body. This checks for: 
    * bodies of try statements
    * finally clauses of try statements
    * switch statements
    * synchronized statements
    * if statements
    * loop statements: while, for, do .. while
    * initializers
    * blocks used as statements (for scoping)
* UnnecessarySemicolon: a semicolon is written which is not necessary.
