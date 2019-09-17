package symtable;
import ast.*;

public class StructInfo extends SymInfo {

    private AST.IdNode id;  // name of the struct type
    
    public StructInfo(AST.IdNode id) {
        super(new Type.StructType(id));
        this.id = id;
    }

    public AST.IdNode getId(){
        return this.id;
    }
    
}