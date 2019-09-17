package symtable;

public class StructDefInfo extends SymInfo {

    private SymTable symTable;
    
    public StructDefInfo(SymTable table) {
        super(new Type.StructDefType());
        symTable = table;
    }

    public SymTable getSymTable() {
        return symTable;
    }
    
}