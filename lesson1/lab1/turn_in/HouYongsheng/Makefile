###
# A simple Makefile for Lab #1
###

# define the java compiler to be used and the flags
JC = javac
FLAGS = -g -cp $(CP)
CP = .

# define the rules
Main.class: Main.java
	$(JC) $(FLAGS) Main.java

#Info.class: ./scanner/Info.java
#	$(JC)  ./scanner/Info.java

#Word.class: ./scanner/Word.java
#	$(JC) ./scanner/Word.java

#WordCount.class: ./scanner/WordCount.java
#	$(JC) ./scanner/WordCount.java

#WordScanner.class: ./scanner/WordScanner.java
#	$(JC) ./scanner/WordScanner.java


# testing 
test:
	java -cp $(CP) Main

# clean up
clean:
	rm -f *~ *.class scanner/*.class
