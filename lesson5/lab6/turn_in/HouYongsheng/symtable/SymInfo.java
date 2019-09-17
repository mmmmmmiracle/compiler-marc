package symtable;

import java.util.*;

/**
 * The SymInfo class defines a Symbol Table entry. 
 * Each SymInfo contains a type (a Type object).
 */
public class SymInfo {


    private Type.AbstractType  type;

    public static int counter = 0;
    public int num = 0;
    /**
     * Create a SymInfo object of type 'type'
     */
    public SymInfo(Type.AbstractType type) {
        this.type = type;
        this.num = this.counter++;
    }

    /**
     * Return the type of the SymInfo
     */
    public Type.AbstractType getType() {
        return this.type;
    }

    /**
     * Return a String representation of the SymInfo
     * (for debuggung purpose)
     */
    public String toString() {
        return this.type.toString()+this.num;
    }
}
