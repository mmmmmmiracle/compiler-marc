package symtable;
import ast.*;
import java.util.*;

public class StructInfo extends SymInfo {

    private AST.IdNode id;  // name of the struct type
    
    public StructInfo(AST.IdNode id) {
        super(new Type.StructType(id));
        this.id = id;
    }

    public AST.IdNode getId(){
        return this.id;
    }

    public Type.AbstractType getType(){
        return new Type.StructType(id);
    }
    
}