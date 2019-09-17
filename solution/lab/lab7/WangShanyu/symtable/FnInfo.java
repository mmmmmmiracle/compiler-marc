package symtable;

import java.util.*;

/**
 * The FnInfo class is a subclass of the Sym class just for functions.
 * The returnType field holds the return type and there are fields to hold
 * information about the parameters.
 */
public class FnInfo extends SymInfo {
    // new fields
    private Type.AbstractType returnType;
    private int numParams;
    private List<Type.AbstractType> paramTypes;
    private int localSize = 0;
    private int paramSize = 0;
    
    public FnInfo(Type.AbstractType type, int numparams) {
        super(new Type.FnType());
        returnType = type;
        numParams = numparams;
    }

    public void addFormals(List<Type.AbstractType> L) {
        paramTypes = L;
    }
    
    public Type.AbstractType getReturnType() {
        return returnType;
    }

    public int getNumParams() {
        return numParams;
    }

    public List<Type.AbstractType> getParamTypes() {
        return paramTypes;
    }

    public void setLocalSize(int size){
        this.localSize = size;
    }
    public int getLocalSize(){
        return localSize;
    }
    public void setParamSize(int size){
        this.paramSize = size;
    }
    public int getParamSize(){
        return paramSize;
    }

    public String toString() {
        // make list of formals
        String str = "";
        boolean notfirst = false;
        for (Type.AbstractType type : paramTypes) {
            if (notfirst)
                str += ",";
            else
                notfirst = true;
            str += type.toString();
        }

        str += "->" + returnType.toString();

        str += " localSize: " + localSize +" paramSize: " +paramSize;
        return str;
    }
}
