package lexer;

public class IntLitTokenVal extends TokenVal {
    
    public int intVal;
    // constructor
    public IntLitTokenVal(int line, int ch, int val) {
        super(line,ch);
        this.intVal = val;
    }
}
