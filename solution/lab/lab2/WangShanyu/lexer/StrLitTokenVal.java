package lexer;

public class StrLitTokenVal extends TokenVal {
	public String strVal;
    // constructor
    public StrLitTokenVal(int line, int ch, String val) {
        super(line, ch);
        this.strVal = val;
    }
}
