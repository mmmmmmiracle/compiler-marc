

# define the java compiler to be used and the flags
JC = javac
FLAGS = -g -cp $(CP)
CP = ../deps:.

Main.class: Main.java Token.class Yylex.class
	$(JC) $(FLAGS) Main.java

Token.class: Token.java
	$(JC) $(FLAGS) Token.java

Yylex.class: Yylex.java Token.class
	$(JC) $(FLAGS) Yylex.java
	
Yylex.java: Yylex
	java -cp $(CP) JLex.Main Yylex
	
###
# testing 
###

test:
	java -cp $(CP) Main

###
# clean up
###

clean:
	rm -f *~ *.class Yylex.java
