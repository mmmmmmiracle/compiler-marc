package lexer;

public class IdTokenVal extends TokenVal {
    public String idVal;
    // constructor
    public IdTokenVal(int line, int ch, String val) {
    	super(line, ch);
    	this.idVal = val;
    }
}
