###
# This Makefile can be used to make a scanner for the C-- language
# (Yylex.class) and to make a program that tests the scanner (Main.class).
#
# The default makes both the scanner and the test program.
#
# make clean removes all generated files.
#
# Note: Main.java will not compile unless Yylex.class exists.
#
###

# define the java compiler to be used and the flags
JC = javac
FLAGS = -g -cp $(CP)
CP = ../deps:.

Main.class: Main.java lexer/Yylex.class parser/sym.class
	$(JC) $(FLAGS) Main.java

lexer/Yylex.class: lexer/Yylex.java lexer/ErrMsg.class parser/sym.class
	$(JC) $(FLAGS) lexer/Yylex.java

lexer/Yylex.java: lexer/Yylex parser/sym.class
	java -cp $(CP) JLex.Main lexer/Yylex

parser/sym.class: parser/sym.java
	$(JC) $(FLAGS) parser/sym.java

lexer/ErrMsg.class: lexer/ErrMsg.java
	$(JC) $(FLAGS) lexer/ErrMsg.java

lexer/%.class: lexer/%.java
	$(JC) $(FLAGS) $^
	
###
# testing 
###

test:
	java -cp $(CP) Main

###
# clean up
###

clean:
	rm -f *~ *.class lexer/Yylex.java lexer/*.class parser/*.class
