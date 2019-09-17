package lexer;
/**
 * This class is used to generate warning and fatal error messages.
 */
public class ErrMsg {
    /**
     * Generates a fatal error message.
     * @param lineNum line number for error location
     * @param charNum character number (i.e., column) for error location
     * @param msg associated message for error
     */

    static boolean hasFatal = false;

    public static void fatal(int lineNum, int charNum, String msg) {
        hasFatal = true;
        System.err.println(lineNum + ":" + charNum + " ***ERROR*** " + msg);
    }

    /**
     * Generates a warning message.
     * @param lineNum line number for warning location
     * @param charNum character number (i.e., column) for warning location
     * @param msg associated message for warning
     */
    public static void warn(int lineNum, int charNum, String msg) {
        System.err.println(lineNum + ":" + charNum + " ***WARNING*** " + msg);
    }

    public static boolean hasFatalError() {
        return hasFatal;
    }

    public static void reset() {
        hasFatal = false;
    }
}
