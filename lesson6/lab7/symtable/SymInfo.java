package symtable;

import java.util.*;

/**
 * The SymInfo class defines a Symbol Table entry. 
 * Each SymInfo contains a type (a Type object).
 */
public class SymInfo {
    
    private Type.AbstractType type;
    private boolean isGlobal = false;
    private int offset = 0;
    private int size = 0;
    /**
     * Create a SymInfo object of type 'type'
     */
    public SymInfo(Type.AbstractType type) {
        this.type = type;
    }

    /**
     * Return the type of the SymInfo
     */
    public Type.AbstractType getType() {
        return type;
    }

    /**
     * Return a String representation of the SymInfo
     * (for debuggung purpose)
     */
    public String toString() {
        if(isGlobal)
            return "global " + type.toString() + " offset: " + offset +" size: " +size;
        return type.toString() + " offset: " + offset +" size: " +size;
    }

    public boolean getGlobal(){
        return isGlobal;
    }

    public void setGlobal(boolean flag) {
        this.isGlobal = flag;
    }

    public int getOffset() {
        return offset;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
}
