###
# A simple Makefile for Lab #1
###

# define the java compiler to be used and the flags
JC = javac
FLAGS = -g -cp $(CP)
CP = .

# define the rules
Main.class: Main.java scanner/Word.class scanner/WordCount.class scanner/Info.class
	$(JC) $(FLAGS) Main.java

scanner/WordScanner.class: scanner/WordScanner.java scanner/Word.class scanner/Info.class
	$(JC) $(FLAGS) scanner/WordScanner.java

scanner/WordCount.class: scanner/WordCount.java scanner/Info.class
	$(JC) $(FLAGS) scanner/WordCount.java

scanner/Info.class: scanner/Info.java
	$(JC) $(FLAGS) scanner/Info.java

scanner/Word.class: scanner/Word.java scanner/Info.class
	$(JC) $(FLAGS) scanner/Word.java

# testing 
test:
	java -cp $(CP) Main

# clean up
clean:
	rm -f *~ *.class scanner/*.class
