
import java.io.*;

public class Main {

    public static void main (String[] args) throws IOException {
	
	Yylex myscanner = new Yylex(System.in);
	Token nextToken = myscanner.yylex();

	while (nextToken != null) {
	    System.out.print(nextToken.getToken());

	    if (nextToken.getToken().equals("NUMBER"))
		System.out.println(" : " + nextToken.getValue());
	    else {
		if (nextToken.getToken().equals("ID"))
			System.out.println(" : " + nextToken.getName());
		else
			System.out.println();
	    }
	    nextToken = myscanner.yylex();
	}


    }

}
