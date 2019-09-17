package symtable;

import java.util.*;

import symtable.Type.StructDefType;

public class StructDefInfo extends SymInfo {

    private SymTable symTable;
    
    public StructDefInfo(SymTable table) {
        super(new Type.StructDefType());
        symTable = table;
    }

    public SymTable getSymTable() {
        return symTable;
    }
    
    public Type.AbstractType getType(){
        return new Type.StructDefType();
    }
}