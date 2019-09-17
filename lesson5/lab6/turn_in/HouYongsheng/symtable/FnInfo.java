package symtable;

import java.util.*;

public class FnInfo extends SymInfo{
    
    private int numParams;
    private List<Type.AbstractType> paramTypes;
    private Type.AbstractType returnType;
    
    public FnInfo(Type.AbstractType type, int numparams) {
        super(new Type.FnType());
        this.returnType = type;
        this.numParams = numparams;
    }

    public void addFormals(List<Type.AbstractType> L) {
        this.paramTypes = L;
    }
    
    public Type.AbstractType getReturnType() {
        return this.returnType;
    }

    public int getNumParams() {
        return this.numParams;
    }

    public List<Type.AbstractType> getParamTypes() {
        return this.paramTypes;
    }

    public String toString() {
        String str = "";
        boolean isFirstFlag = true;
        for (Type.AbstractType absType : this.paramTypes) {
            if(isFirstFlag){
                str += absType.toString();
            }else{
                str = str + "," + absType.toString();
            }
        }

        str += "->" + this.returnType.toString();
        return str;
    }
}