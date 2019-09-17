###
# This Makefile can be used to make a parser for the moo language
# (parser.class) and to make a program (P3.class) that tests the parser and
# the unparse methods in ast.java.
#
# make clean removes all generated files.
#
###

JC = javac
FLAGS = -g -cp $(CP)
CP = ../deps:.

Main.class: Main.java parser/CmmParser.class lexer/Yylex.class ast/AST.class
	$(JC) $(FLAGS) Main.java

parser/CmmParser.class: parser/CmmParser.java ast/AST.class lexer/Yylex.class lexer/ErrMsg.class
	$(JC) $(FLAGS) parser/CmmParser.java

parser/CmmParser.java: parser/cmm.cup
	java -cp $(CP) java_cup.Main -package parser -parser CmmParser < parser/cmm.cup
	mv CmmParser.java sym.java parser/

lexer/Yylex.class: lexer/Yylex.java lexer/ErrMsg.class parser/sym.class
	$(JC) $(FLAGS) lexer/Yylex.java 

ast/AST.class: ast/AST.java
	$(JC) $(FLAGS)  ast/AST.java

lexer/Yylex.java: lexer/Yylex parser/sym.class
	java -cp $(CP) JLex.Main lexer/Yylex

parser/sym.class: parser/sym.java
	$(JC) $(FLAGS) parser/sym.java

parser/sym.java: parser/cmm.cup
	java -cp $(CP) java_cup.Main -package parser < parser/cmm.cup
	mv sym.java parser/

lexer/%.class: lexer/%.java
	$(JC) $(FLAGS) $^

# test
test:
	java -cp $(CP) Main

# clean
clean:
	rm -f *~ *.class parser/CmmParser.java parser/sym.java lexer/Yylex.java lexer/*.class ast/*.class parser/*.class
