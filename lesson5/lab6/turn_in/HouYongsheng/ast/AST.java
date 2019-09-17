package ast;

import java.io.*;
import java.util.*;

import lexer.*;
import symtable.EmptySymTableException;
import symtable.FnInfo;
import symtable.StructDefInfo;
import symtable.StructInfo;
import symtable.SymInfo;
import symtable.SymTable;
import symtable.Type;

// **********************************************************************
// The AST class is a container with all the effective classes inside as
// public static inner classes. To use these classes, use the dot notation
// like:  AST.ASTnode
//
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a C-- program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of 
// children) or as a fixed set of fields.
//
// The nodes for literals and ids contain line and character number
// information; for string literals and identifiers, they also contain a
// string; for integer literals, they also contain an integer value.
//
// Here are all the different kinds of AST nodes and what kinds of children
// they have.  All of these kinds of AST nodes are subclasses of "ASTnode".
// Indentation indicates further subclassing:
//
//     Subclass            Kids
//     --------            ----
//     ProgramNode         DeclListNode
//     DeclListNode        List<DeclNode>
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode
//       StructDeclNode    IdNode, DeclListNode
//
//     FormalsListNode     List<FormalDeclNode>
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        List<StmtNode>
//     ExpListNode         List<ExpNode>
//
//     TypeNode:
//       IntNode           -- none --
//       BoolNode          -- none --
//       VoidNode          -- none --
//       StructNode        IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       IntLitNode          int
//       StringLitNode       String
//       TrueNode            -- none --
//       FalseNode           -- none --
//       IdNode              String
//       DotAccessNode       ExpNode, IdNode
//       AssignNode          ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         ArithmeticExpNode
//           PlusNode     
//           MinusNode
//           TimesNode
//           DivideNode
//         LogicalExpNode
//           AndNode
//           OrNode
//         EqualityExpNode
//           EqualsNode
//           NotEqualsNode
//         RelationalExpNode
//           LessNode
//           GreaterNode
//           LessEqNode
//           GreaterEqNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of kids, or
// internal nodes with a fixed number of kids:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StringLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of kids:
//        ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode   
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  CallStmtNode
//        ReturnStmtNode,  DotAccessNode,   AssignExpNode,  CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//        PlusNode,        MinusNode,       TimesNode,      DivideNode,
//        AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
// **********************************************************************

// **********************************************************************
// ASTnode class (container class for all other classes)
// **********************************************************************
public class AST {


// **********************************************************************
// ASTnode class (base class for all other kinds of nodes)
// **********************************************************************
public static abstract class ASTnode { 

    // every subclass must provide an unparse operation
    public void unparse(PrintWriter p, int indent) {
    }

    // this method can be used by the unparse methods to do indenting
    protected void doIndent(PrintWriter p, int indent) {
        for (int k=0; k<indent; k++) p.print(" ");
    }
}

// **********************************************************************
// ProgramNode,  DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode
// **********************************************************************

public static class ProgramNode extends ASTnode {
    private DeclListNode declListNode;
    // private SymTable symTable;
    
    public ProgramNode(DeclListNode L) {
        declListNode = L;
    }

    /**
     * nameAnalysis
     * Creates an empty symbol table for the outermost scope, then processes
     * all of the globals, struct defintions, and functions in the program.
     */
    public void nameAnalysis(){
        SymTable symTable = new SymTable();
        declListNode.nameAnalysis(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        declListNode.unparse(p, indent);
    }

    public void typeCheck(){
        this.declListNode.typeCheck();
    }

}

public static class DeclListNode extends ASTnode {
    private List<DeclNode> declNodeList;
    
    public DeclListNode(List<DeclNode> S) {
        declNodeList = S;
    }

    public void typeCheck(){
        for(DeclNode declNode : this.declNodeList){
            declNode.typeCheck();
        }
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = declNodeList.iterator();
        try {
            while(it.hasNext()){
                ((DeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException e) {
            //TODO: handle exception
            System.err.println("unexpected NoSuchElementException in DeclListNode.unparse");
            System.exit(-1); 
        }
    }

     /**
     * nameAnalysis
     * Given a symbol table symTab, process all of the decls in the list.
     */
    public void nameAnalysis(SymTable symTab) {
        // for(DeclNode declNode : declNodeList){
        //     declNode.nameAnalysis(symTab);
        // }
        this.nameAnalysis(symTab, symTab);
    }
    
    /**
     * nameAnalysis inside a struct definition
     * Given a symbol table structSymTab and a global symbol table globalTab
     * process all of the decls in the list.
     */    
    public void nameAnalysis(SymTable structSymTab, SymTable globalTab) {
        for(DeclNode declNode : declNodeList){
            if(declNode instanceof VarDeclNode){
                ((VarDeclNode)declNode).nameAnalysis(structSymTab,globalTab);
            }else{
                declNode.nameAnalysis(structSymTab);
            }
        }
    }    


}

public static class FormalsListNode extends ASTnode {

    private List<FormalDeclNode> formalDeclNodeList;

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * for each formal decl in the list
     *     process the formal decl
     *     if there was no error, add type of formal decl to list
     */
    public List<Type.AbstractType> nameAnalysis(SymTable symTab) {
        List<Type.AbstractType> typeList = new LinkedList<Type.AbstractType>();
        for(FormalDeclNode formalDeclNode : formalDeclNodeList){
            SymInfo symInfo = formalDeclNode.nameAnalysis(symTab);
            // if(symInfo == null)
            //     ErrMsg.fatal(lineNum, charNum, msg); // throw a exception
            // else
            if(symInfo != null)
                typeList.add(symInfo.getType());
        }
        return typeList;
    } 

    public FormalsListNode(List<FormalDeclNode> S) {
       formalDeclNodeList = S;
    }

    public int size(){
        return this.formalDeclNodeList.size();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");

        Iterator it = formalDeclNodeList.iterator();
        try {
            if(it.hasNext()){
                ((FormalDeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException e) {
            //TODO: handle exception
            System.err.println("unexpected NoSuchElementException in FormalsListNode.unparse");
            System.exit(-1); 
        }

        try {
            while(it.hasNext()){
                p.print(",");
                ((FormalDeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException e) {
            //TODO: handle exception
            System.err.println("unexpected NoSuchElementException in FormalsListNode.unparse");
            System.exit(-1); 
        }

        p.print(")");
    }

}

public static class FnBodyNode extends ASTnode {

    private DeclListNode declListNode;
    private StmtListNode stmtListNode;

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the declaration list
     * - process the statement list
     */
    public void nameAnalysis(SymTable symTab) {
        declListNode.nameAnalysis(symTab);
        stmtListNode.nameAnalysis(symTab);
    } 

    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        declListNode = declList;
        stmtListNode = stmtList;
    }

    public void typeCheck(Type.AbstractType retType){
        this.stmtListNode.typeCheck(retType);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.println("{");
        declListNode.unparse(p, indent + 4);
        stmtListNode.unparse(p, indent + 4);
        p.println("}");
    }

}

public static class StmtListNode extends ASTnode {

    private List<StmtNode> stmtNodeList;

    /**
     * nameAnalysis
     * Given a symbol table symTab, process each statement in the list.
     */
    public void nameAnalysis(SymTable symTab) {
        for(StmtNode stmtNode : stmtNodeList){
            stmtNode.nameAnalysis(symTab);
        }
    }  

    public void typeCheck(Type.AbstractType retType){
        for(StmtNode stmtNode : stmtNodeList){
            stmtNode.typeCheck(retType);
        }
    }

    public StmtListNode(List<StmtNode> S) {
        stmtNodeList = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = stmtNodeList.iterator();
        try {
            while(it.hasNext()){
                ((StmtNode)(it.next())).unparse(p, indent);
            }
        } catch (NoSuchElementException e) {
            //TODO: handle exception
            System.err.println("unexpected NoSuchElementException in StmtListNode.unparse");
            System.exit(-1); 
        }
    }

}

public static class ExpListNode extends ASTnode {

    private List<ExpNode> expNodeList;

    /**
     * nameAnalysis
     * Given a symbol table symTab, process each exp in the list.
     */
    public void nameAnalysis(SymTable symTab) {
        for(ExpNode expNode : expNodeList){
            // expNode.nameAnalysis(symTab);
        }
    }

    public ExpListNode(List<ExpNode> S) {
        expNodeList = S;
    }

    public void typeCheck(List<Type.AbstractType> fnParamTypes){
        // check if the params type are all matched
        if(fnParamTypes.size() == this.expNodeList.size()){
            for(int ithParam = 0; ithParam < this.expNodeList.size(); ithParam++){
                ExpNode tmpExpNode = this.expNodeList.get(ithParam);
                if(tmpExpNode != null && !tmpExpNode.typeCheck().equals(fnParamTypes.get(ithParam)) ){
                    ErrMsg.fatal(tmpExpNode.lineNum, tmpExpNode.charNum, "Type of actual does not match type of formal");
                }
            }
        }
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = expNodeList.iterator();
        try {
            if(it.hasNext()){
                ((ExpNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException e) {
            //TODO: handle exception
            System.err.println("unexpected NoSuchElementException in ExpListNode.unparse");
            System.exit(-1);
        }

        try {
            while(it.hasNext()){
                p.print(",");
                ((ExpNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException e) {
            //TODO: handle exception
            System.err.println("unexpected NoSuchElementException in ExpListNode.unparse");
            System.exit(-1);
        }
    }

    public List<ExpNode> getExpNodeList(){
        return this.expNodeList;
    }

}

// **********************************************************************
// DeclNode and its sub classes
// **********************************************************************

public static abstract class DeclNode extends ASTnode {
    abstract public SymInfo nameAnalysis(SymTable symTab);
    public abstract void typeCheck();
}

public static class VarDeclNode extends DeclNode {

    private TypeNode typeNode;
    private IdNode idNode;
    private int size;      


    public void typeCheck(){

    }

    /**
     * nameAnalysis (overloaded)
     * Given a symbol table symTab, do:
     * if this name is declared void, then error
     * else if the declaration is of a struct type, 
     *     lookup type name (globally)
     *     if type name doesn't exist, then error
     * if no errors so far,
     *     if name has already been declared in this scope, then error
     *     else add name to local symbol table     
     *
     * symTab is local symbol table (say, for struct field decls)
     */
    public SymInfo nameAnalysis(SymTable symTab) {
        return this.nameAnalysis(symTab, symTab);
    }
    
    /**
     * Same as before and:
     * globalTab is global symbol table (for struct type names)
     */
    public SymInfo nameAnalysis(SymTable structSymTab, SymTable globalTab) {
        boolean isExistErrors = false;
        IdNode structIdNode = null;
        SymInfo symInfo = null;
        if(typeNode instanceof VoidNode){
            ErrMsg.fatal(idNode.lineNum, idNode.charNum, "non-function declared void");
            isExistErrors = true;
        }else if(typeNode instanceof StructNode){
            structIdNode = ((StructNode)typeNode).idNode();
            // System.out.println(structIdNode == null);
            symInfo = globalTab.lookupGlobal(structIdNode.name());
            // System.out.println(symInfo == null);
            // System.out.println(symInfo instanceof StructDefInfo);
            if(symInfo == null || !(symInfo instanceof StructDefInfo)){
                ErrMsg.fatal(structIdNode.lineNum, structIdNode.charNum, "invalid struct type");
                isExistErrors = true;
            }else{
                this.idNode.link(symInfo);
            }
        }else{
            ;
        }

        if(structSymTab.lookupLocal(this.idNode.name()) != null){
            isExistErrors = true;
        }

        if( !isExistErrors ){
            if(structSymTab.lookupLocal(this.idNode.name()) != null){
                symInfo = new StructInfo(this.idNode);
                ErrMsg.fatal(idNode.lineNum, idNode.charNum, "multiply declared Id");
            }else{
                if(this.typeNode instanceof StructNode){
                    symInfo = new StructInfo(this.idNode);
                }else {
                    symInfo = new SymInfo(this.typeNode.type());
                }
                structSymTab.addDecl(this.idNode.name(), symInfo);
                this.idNode.link(symInfo);
            }
        }

        return symInfo;

    }

    public VarDeclNode(TypeNode type, IdNode id, int size) {
        typeNode = type;
        idNode = id;
        this.size = size;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        typeNode.unparse(p, 0);
        p.print(" ");
        idNode.unparse(p, 0);
        p.println(";");
    }

    // DO NOT CHANGE THIS (useful for the next labs)

    public static int NOT_STRUCT = -1;
}

public static class FnDeclNode extends DeclNode {

    private TypeNode typeNode;
    private IdNode idNode;
    private FormalsListNode formalsListNode;
    private FnBodyNode fnBodyNode;


    public FnDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FnBodyNode body) {
        typeNode = type;
        idNode = id;
        formalsListNode = formalList;
        fnBodyNode = body;
 
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * if this name has already been declared in this scope, then error
     * else add name to local symbol table
     * in any case, do the following:
     *     enter new scope
     *     process the formals
     *     if this function is not multiply declared,
     *         update symbol table entry with types of formals
     *     process the body of the function
     *     exit scope
     */
    public SymInfo nameAnalysis(SymTable symTab) {
        FnInfo fnInfo = null;
        String nameString = this.idNode.name();
        if( symTab.lookupLocal(nameString) != null){
            ErrMsg.fatal(idNode.lineNum, idNode.charNum, "multiply declared Id");
        }else{
            fnInfo = new FnInfo(typeNode.type(), formalsListNode.size());
            symTab.addDecl(nameString, fnInfo);
            idNode.link(fnInfo);
        }

        symTab.addScope();

        List<Type.AbstractType> typeList = this.formalsListNode.nameAnalysis(symTab);
        // symTab.lookupGlobal(nameString)
        if(fnInfo != null){
            fnInfo.addFormals(typeList);
        }
        
        fnBodyNode.nameAnalysis(symTab);

        symTab.removeScope();
        return null;   

    }    

    public void typeCheck(){
        this.fnBodyNode.typeCheck(this.typeNode.type());
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        typeNode.unparse(p, 0);
        p.print(" ");
        idNode.unparse(p, 0);
        formalsListNode.unparse(p, indent);
        fnBodyNode.unparse(p, indent);
    }

}

public static class FormalDeclNode extends DeclNode {

    private TypeNode typeNode;
    private IdNode idNode;

    public FormalDeclNode(TypeNode type, IdNode id) {
        typeNode = type;
        idNode = id;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * if this formal is declared void, then error
     * else if this formal is already in the local symble table,
     *     then issue multiply declared error message and return null
     * else add a new entry to the symbol table and return that Sym
     */
    public SymInfo nameAnalysis(SymTable symTab) {
        String nameString = idNode.name();
        // boolean isExistErrors = false;
        SymInfo symInfo = null;

        if(typeNode instanceof VoidNode){
            ErrMsg.fatal(idNode.lineNum, idNode.charNum, "non-function declared void");
            // isExistErrors = true;
        }else if(symTab.lookupLocal(nameString) != null){
            ErrMsg.fatal(idNode.lineNum, idNode.charNum, "multiply declared ");
            // isExistErrors = true;
            return null;
        }else{
            symInfo = new SymInfo(this.typeNode.type());
            symTab.addDecl(nameString, symInfo);
            this.idNode.link(symInfo);
        }  
        return null;
    } 

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        typeNode.unparse(p, 0);
        p.print(" ");
        idNode.unparse(p, 0);
    }

    public void typeCheck(){}
    
}

public static class StructDeclNode extends DeclNode {

    private IdNode idNode;
    private DeclListNode declListNode;

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * if this name is already in the symbol table,
     *     then multiply declared error (don't add to symbol table)
     * create a new symbol table for this struct definition
     * process the decl list
     * if no errors
     *     add a new entry to symbol table for this struct
     */
    public SymInfo nameAnalysis (SymTable symTab) 
        throws DuplicateFormatFlagsException,EmptySymTableException{
        String nameString = this.idNode.name();
        boolean isExistErrors = false;
        if(symTab.lookupLocal(nameString) != null){
            ErrMsg.fatal(this.idNode.lineNum, this.idNode.charNum, "multiply declared");
            isExistErrors = true;
        }
    
        SymTable structSymTab = new SymTable();

        this.declListNode.nameAnalysis(structSymTab, symTab);

        if( !isExistErrors ){
            StructDefInfo structDefInfo = new StructDefInfo(structSymTab);
            symTab.addDecl(nameString, structDefInfo);
            this.idNode.link(structDefInfo);
        }
        return null;
    }

    public StructDeclNode(IdNode id, DeclListNode declList) {
       idNode = id;
       declListNode = declList;
    }

    public void typeCheck(){}

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("struct ");
        idNode.unparse(p, indent);
        p.println(" {");
        declListNode.unparse(p, indent + 4);
        p.println("} ");
    }

}

// **********************************************************************
// TypeNode and its sub classes
// **********************************************************************

public static abstract class TypeNode extends ASTnode {
    abstract public Type.AbstractType type();
}

public static class IntNode extends TypeNode {

    public  Type.AbstractType type(){
        return new Type.IntType();
    }

    public IntNode() {
        
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }
}

public static class BoolNode extends TypeNode {

    public Type.AbstractType type(){
        return new Type.BoolType();
    }

    public BoolNode() {
    
    }
    
    public void unparse(PrintWriter p, int indent) {
       p.print("bool");
    }
}

public static class VoidNode extends TypeNode {

    public Type.AbstractType type(){
        return new Type.VoidType();
    }

    public VoidNode() {
    
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }
}

public static class StructNode extends TypeNode {

    private IdNode idNode;

    public Type.StructType type(){
        return new Type.StructType(this.idNode);
    }

    public StructNode(IdNode id) {
        idNode = id;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("struct ");
        idNode.unparse(p, indent);
    }

    public IdNode idNode() {
        return this.idNode;
    }

    
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

public static abstract class StmtNode extends ASTnode {
    /**
     * nameAnalysis
     */
   public abstract void nameAnalysis(SymTable symTab);
   public abstract void typeCheck(Type.AbstractType retType);
}

public static class AssignStmtNode extends StmtNode {
    
    private AssignNode assignNode;

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab){
        assignNode.nameAnalysis(symTab);
    }

    public void typeCheck(Type.AbstractType retType){
        this.assignNode.typeCheck();
    }

    public Type.AbstractType type(){
        return this.assignNode.type();
    }

    public AssignStmtNode(AssignNode assign) {
        assignNode = assign;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        assignNode.unparse(p, indent);
        p.println(";");
    }

}

public static class PostIncStmtNode extends StmtNode {

    private ExpNode expNode;

    public PostIncStmtNode(ExpNode exp) {
        expNode = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        expNode.unparse(p, indent);
        p.println(" ++;");
    }

    public void typeCheck(Type.AbstractType retType){
        Type.AbstractType type = this.expNode.typeCheck();
        if(!type.isErrorType() && !type.isIntType()){
            ErrMsg.fatal(this.expNode.lineNum, this.expNode.charNum, "Arithmetic operator applied to non-numeric operand");
        }
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        this.expNode.nameAnalysis(symTab);
    }

    // public Type type(){
    //     return null;
    // }
}

public static class PostDecStmtNode extends StmtNode {

    private ExpNode expNode;

    public PostDecStmtNode(ExpNode exp) {
        expNode = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        expNode.unparse(p, indent);
        p.println(" --;");
    }
    
    public void typeCheck(Type.AbstractType retType){
        Type.AbstractType type = this.expNode.typeCheck();
        if(!type.isErrorType() && !type.isIntType()){
            ErrMsg.fatal(this.expNode.lineNum, this.expNode.charNum, "Arithmetic operator applied to non-numeric operand");
        }
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        this.expNode.nameAnalysis(symTab);
    }
}

public static class ReadStmtNode extends StmtNode {

    private ExpNode expNode;

    public ReadStmtNode(ExpNode e) {
        expNode = e;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("cin >> ");
        expNode.unparse(p, indent);
        p.println(";");
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        this.expNode.nameAnalysis(symTab);
    }

    public void typeCheck(Type.AbstractType retType){
        Type.AbstractType type = this.expNode.typeCheck();
        if(type != null){
            if(type.isFnType()){
                ErrMsg.fatal(this.expNode.lineNum, this.expNode.charNum, "Attempt to read a function");
            }else if(type.isStructDefType()){
                ErrMsg.fatal(this.expNode.lineNum, this.expNode.charNum, "Attempt to read a struct name");
            }else if(type.isStructType()){
                ErrMsg.fatal(this.expNode.lineNum, this.expNode.charNum, "Attempt to read a struct variable");
            }else{
                ;
            }
        }
    }
}

public static class WriteStmtNode extends StmtNode {

    private ExpNode expNode;

    public WriteStmtNode(ExpNode exp) {
        expNode = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("cout << ");
        expNode.unparse(p, indent);
        p.println(";");
    }

    public void typeCheck(Type.AbstractType retType){
        Type.AbstractType type = this.expNode.type();
        if(type.isFnType()){
            ErrMsg.fatal(this.expNode.lineNum, this.expNode.charNum, "Attempt to write a function");
        }else if(type.isStructType()){
            ErrMsg.fatal(this.expNode.lineNum, this.expNode.charNum, "Attempt to write a struct name");
        }else if(type.isStructDefType()){
            ErrMsg.fatal(this.expNode.lineNum, this.expNode.charNum, "Attempt to write a struct name");
        }else if(type.isVoidType()){
            ErrMsg.fatal(this.expNode.lineNum, this.expNode.charNum, "Attempt to write void");
        }else if(type.isErrorType()){
            ErrMsg.fatal(this.expNode.lineNum, this.expNode.charNum, "Error");
        }else{
            ;
        }
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        this.expNode.nameAnalysis(symTab);
    }
}

public static class IfStmtNode extends StmtNode {

    private ExpNode expNode;
    private DeclListNode declListNode;
    private StmtListNode stmtListNode;

    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        expNode = exp;
        declListNode = dlist;
        stmtListNode = slist;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if(");
        expNode.unparse(p, indent);
        p.println(") {");
        declListNode.unparse(p, indent + 4);
        stmtListNode.unparse(p, indent + 4);
        doIndent(p, indent);
        p.println("}");
    }

    public void typeCheck(Type.AbstractType retType){
        Type.AbstractType type = this.expNode.typeCheck();
        if(!type.isErrorType() && !type.isBoolType()){
            ErrMsg.fatal(this.expNode.lineNum, this.expNode.charNum, "Non-bool expression used as an if condition");
        }
        this.stmtListNode.typeCheck(retType);
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the condition
     * - enter a new scope
     * - process the decls and stmts
     * - exit the scope
     */
    public void nameAnalysis(SymTable symTable){
        this.expNode.nameAnalysis(symTable);

        symTable.addScope();

        this.declListNode.nameAnalysis(symTable);
        this.stmtListNode.nameAnalysis(symTable);

        symTable.removeScope();

    }
}

public static class IfElseStmtNode extends StmtNode {

    private ExpNode exp;
    private DeclListNode dlist1;
    private StmtListNode slist1;
    private StmtListNode slist2;
    private DeclListNode dlist2;

    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
                          StmtListNode slist1, DeclListNode dlist2,
                          StmtListNode slist2) {
        this.exp = exp;
        this.dlist1 = dlist1;
        this.slist1 = slist1;
        this.dlist2 = dlist2;
        this.slist2 = slist2;         
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if(");
        exp.unparse(p, indent);
        p.println(") {");
        dlist1.unparse(p, indent + 4);
        slist1.unparse(p, indent + 4);
        doIndent(p, indent);
        p.println("}");
        doIndent(p, indent);
        p.println("else {");
        dlist2.unparse(p, indent + 4);
        slist2.unparse(p, indent + 4);
        doIndent(p, indent);
        p.println("}");
    }

    public void typeCheck(Type.AbstractType retType){
        Type.AbstractType type = this.exp.typeCheck();
        if(!type.isErrorType() && !type.isBoolType()){
            ErrMsg.fatal(this.exp.lineNum, this.exp.charNum, "Non-bool expression used as an if condition");
        }
        this.slist1.typeCheck(retType);
        this.slist2.typeCheck(retType);
    } 

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the condition
     * - enter a new scope
     * - process the decls and stmts of then
     * - exit the scope
     * - enter a new scope
     * - process the decls and stmts of else
     * - exit the scope
     */
    public void nameAnalysis(SymTable symTable){
        this.exp.nameAnalysis(symTable);

        symTable.addScope();

        this.dlist1.nameAnalysis(symTable);
        this.slist1.nameAnalysis(symTable);

        symTable.removeScope();

        symTable.addScope();

        this.dlist2.nameAnalysis(symTable);
        this.slist2.nameAnalysis(symTable);

        symTable.removeScope();
    }
}

public static class WhileStmtNode extends StmtNode {

    private ExpNode expNode;
    private DeclListNode declListNode;
    private StmtListNode stmtListNode;

    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        expNode = exp;
        declListNode = dlist;
        stmtListNode = slist;
    }
	
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("while(");
        expNode.unparse(p, indent);
        p.println(") {");
        declListNode.unparse(p, indent + 4);
        stmtListNode.unparse(p, indent + 4);
        doIndent(p, indent);
        p.println("}");
    }

    public void typeCheck(Type.AbstractType retType){
        Type.AbstractType type = this.expNode.typeCheck();
        if(!type.isErrorType() && !type.isBoolType()){
            ErrMsg.fatal(this.expNode.lineNum, this.expNode.charNum, "Non-bool expression used as an if condition");
        }
        this.stmtListNode.typeCheck(retType);
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the condition
     * - enter a new scope
     * - process the decls and stmts
     * - exit the scope
     */
    public void nameAnalysis(SymTable symTable){
        this.expNode.nameAnalysis(symTable);

        symTable.addScope();

        this.declListNode.nameAnalysis(symTable);
        this.stmtListNode.nameAnalysis(symTable);

        symTable.removeScope();
    }

}

public static class CallStmtNode extends StmtNode {

    private CallExpNode callExpNode;

    public CallStmtNode(CallExpNode call) {
        callExpNode = call;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        callExpNode.unparse(p, indent);
        p.println(";");
    }

    public void typeCheck(Type.AbstractType retType){
        this.callExpNode.typeCheck();
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        this.callExpNode.nameAnalysis(symTab);
    }
}

public static class ReturnStmtNode extends StmtNode {

    private ExpNode expNode;
    private int charNum;
    private int lineNum;

    public ReturnStmtNode(ExpNode exp) {
        expNode = exp;
    }

    public ReturnStmtNode(ExpNode exp, int charnum, int linenum) {
        expNode = exp;
        this.charNum = charNum;
        this.lineNum = linenum;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("return ");
        if(expNode != null){
            expNode.unparse(p, indent);
        }
        p.println(";");
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child,
     * if it has one
     */
    public void nameAnalysis(SymTable symTable){
        if(this.expNode != null){
            this.expNode.nameAnalysis(symTable);
        }
    }

    public void typeCheck(Type.AbstractType returnType){
        if(this.expNode == null){
            //it is correct that a void function has no return stmt,but not the non-void function
            if( !returnType.isVoidType() ){
                ErrMsg.fatal(0, 0, "Missing return value");
            }
        }else{
            Type.AbstractType type = this.expNode.typeCheck();
            //void func has no return stmt
            if( returnType.isVoidType() ){
                ErrMsg.fatal(this.expNode.lineNum, this.expNode.charNum, "Return with a value in a void function");
            }else if( (!type.isErrorType() && !returnType.isErrorType()) && (!type.equals(returnType))){
                ErrMsg.fatal(this.expNode.lineNum, this.expNode.charNum, "Bad return value");
            }
        }
    }
}

// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

public static abstract class ExpNode extends ASTnode {
    public ExpNode() {
        lineNum = 0;
        charNum = 0;
    }

    /**
     * Default version for nodes with no names
     */
    public void nameAnalysis(SymTable symTab) { }
    public Type.AbstractType type(){ return null;}
    public abstract Type.AbstractType typeCheck();
    public ExpNode(int lineNum, int charNum) {
        this.lineNum = lineNum;
        this.charNum = charNum;
    }

    protected int lineNum;
    protected int charNum;
}

public static class IntLitNode extends ExpNode {
  
    private int lineNum;
    private int charNum;
    private int intVal;

    public IntLitNode(int lineNum, int charNum, int intVal) {
        this.lineNum = lineNum;
        this.charNum = charNum;
        this.intVal = intVal;
    }

    public Type.AbstractType typeCheck(){
        return new Type.IntType();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(this.intVal);

    }

    public Type.AbstractType type(){
        return new Type.IntType();
    }
}

public static class StringLitNode extends ExpNode {

    private int lineNum;
    private int CharNum;
    private String strVal;

    public StringLitNode(int lineNum, int charNum, String strVal) {
        this.lineNum = lineNum;
        this.charNum = charNum;
        this.strVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(this.strVal);
    }

    public Type.AbstractType type(){
        return new Type.StringType();
    }

    public Type.AbstractType typeCheck(){
        return new Type.StringType();
    }
}

public static class TrueNode extends ExpNode {

    private int lineNum;
    private int charNum;

    public TrueNode(int lineNum, int charNum) {
        this.lineNum = lineNum;
        this.charNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }

    public Type.AbstractType type(){
        return new Type.BoolType();
    }

    public Type.AbstractType typeCheck(){
        return new Type.BoolType();
    }
}

public static class FalseNode extends ExpNode {

    private int lineNum;
    private int charNum;

    public FalseNode(int lineNum, int charNum) {
        this.lineNum = lineNum;
        this.charNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }

    public Type.AbstractType type(){
        return new Type.BoolType();
    }

    public Type.AbstractType typeCheck(){
        return new Type.BoolType();
    }
}

public static class IdNode extends ExpNode {

    public int lineNum;
    public int charNum;
    private String strVal;
    private SymInfo symInfo;

    public IdNode(int lineNum, int charNum, String strVal) {
        this.lineNum = lineNum;
        this.charNum = charNum;
        this.strVal = strVal;
    }

    public Type.AbstractType type(){
        return this.symInfo.getType();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(this.strVal);
        if(this.symInfo != null){
            p.print("["+this.symInfo+"]");
        }
    }

    public String name(){
        return this.strVal;
    }

    public Type.AbstractType typeCheck(){
        if(this.symInfo != null){
            return this.symInfo.getType();
        }
        return new Type.ErrorType();
    }

    public void link(SymInfo info){
        this.symInfo = info;
    }

    // Return the symbol associated with this ID.
    public SymInfo symInfo() {
        return this.symInfo;
    }   

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - check for use of undeclared name
     * - if ok, link to symbol table entry
     */
    public void nameAnalysis(SymTable symTab) {
        SymInfo symInfo = symTab.lookupGlobal(this.strVal);
        // System.out.println(symInfo == null);
        if(symInfo != null){
            this.link(symInfo);
        }else{
            ErrMsg.fatal(this.lineNum, this.charNum, "undeclared");
        }
    }
}

public static class DotAccessExpNode extends ExpNode {
    
    private ExpNode expNode;
    private IdNode idNode;

    private SymInfo myInfo;    // link to SymInfo for struct type
    private boolean badAccess; // to prevent multiple, cascading errors

    public DotAccessExpNode(ExpNode lhs, IdNode id) {
        super(id.lineNum,id.charNum);
        this.expNode = lhs;
        this.idNode = id;
        this.myInfo = null;
    }

    public Type.AbstractType type(){
        return this.idNode.type();
    }

    public void unparse(PrintWriter p, int indent) {
        expNode.unparse(p, indent);
        p.print(".");
        idNode.unparse(p, 0);
    }

    public Type.AbstractType typeCheck(){
        return this.idNode.typeCheck();
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the LHS of the dot-access
     * - process the RHS of the dot-access
     * - if the RHS is of a struct type, set the sym for this node so that
     *   a dot-access "higher up" in the AST can get access to the symbol
     *   table for the appropriate struct definition
     */
    public void nameAnalysis(SymTable symTab) {
        this.badAccess =false;
        SymTable structSymTab = null;
        SymInfo symInfo = null;

        this.expNode.nameAnalysis(symTab);

        if(this.expNode instanceof IdNode){
            IdNode id = (IdNode)this.expNode;
            symInfo = id.symInfo();
           
            if(symInfo == null){
                this.badAccess = true;
            }
            else if(symInfo instanceof StructInfo){
                // get symbol table for struct type
                SymInfo tempSym = ((StructInfo)symInfo).getId().symInfo();
                // System.out.println(tempSym instanceof StructInfo);
                structSymTab = ((StructDefInfo)tempSym).getSymTable();
            }else{
                // ErrMsg.fatal(id.lineNum, id.charNum, "");
                this.badAccess = true;
            }
        }else if(this.expNode instanceof DotAccessExpNode){
            DotAccessExpNode dotAccessExpNode = (DotAccessExpNode)this.expNode;
            if(dotAccessExpNode.badAccess == true){
                this.badAccess = true;
            }else{
                symInfo = dotAccessExpNode.myInfo;
                if(symInfo == null){
                    this.badAccess = true;
                }else{
                    if(this.myInfo instanceof StructDefInfo){
                        structSymTab = ((StructDefInfo)myInfo).getSymTable();

                    }else{
                        this.badAccess = true;
                    }
                }
            }
        }else{
            this.badAccess = true;
        }

        if(!this.badAccess){
            myInfo = structSymTab.lookupGlobal(idNode.name());
            if(myInfo == null){
                ErrMsg.fatal(this.idNode.lineNum, this.idNode.charNum, "invalid struct");
                this.badAccess = true;
            }else{
                this.idNode.link(this.myInfo);
                if(this.myInfo instanceof StructInfo){
                    this.myInfo = ((StructInfo)symInfo).getId().symInfo();
                }
            }
        }
    }    

    /**
     * Return the info associated with this dot-access node.
     */
    public SymInfo info() {
        return this.myInfo;
    } 
}

public static class AssignNode extends ExpNode {

    private ExpNode lhs;
    private ExpNode rhs;

    public AssignNode(ExpNode lhs, ExpNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void unparse(PrintWriter p, int indent) {
        lhs.unparse(p, indent);
        p.print(" = ");
        rhs.unparse(p, indent);
    }

    public Type.AbstractType type(){
        return this.rhs.type();
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's 
     * two children
     */
    public void nameAnalysis(SymTable symTable){
        lhs.nameAnalysis(symTable);
        rhs.nameAnalysis(symTable);
    }

    public Type.AbstractType typeCheck() {
        Type.AbstractType typeLhs = lhs.typeCheck();
        Type.AbstractType typeExp = rhs.typeCheck();
        Type.AbstractType retType = typeLhs;
        // System.out.println(typeLhs.toString());
        // System.out.println(typeExp.toString());
        if(typeLhs != null && typeExp != null){
            if (typeLhs.isFnType() && typeExp.isFnType()) {
                ErrMsg.fatal(lineNum, charNum, "Function assignment");
                retType = new Type.ErrorType();
            }
            
            if (typeLhs.isStructDefType() && typeExp.isStructDefType()) {
                ErrMsg.fatal(lineNum, charNum, "Struct name assignment");
                retType = new Type.ErrorType();
            }
            
            if (typeLhs.isStructType() && typeExp.isStructType()) {
                ErrMsg.fatal(lineNum, charNum, "Struct variable assignment");
                retType = new Type.ErrorType();
            }        
            
            if (!typeLhs.equals(typeExp) && !typeLhs.isErrorType() && !typeExp.isErrorType()) {
                ErrMsg.fatal(lineNum, charNum, "Type mismatch");
                retType = new Type.ErrorType();
            }
            
            if (typeLhs.isErrorType() || typeExp.isErrorType()) {
                retType = new Type.ErrorType();
            }
        }
        
        return retType;
    }
}

public static class CallExpNode extends ExpNode {

    private IdNode idNode;
    private ExpListNode expListNode;

    public CallExpNode(IdNode name, ExpListNode elist) {
        idNode = name;
        expListNode = elist;
    }

    public Type.AbstractType typeCheck(){

        Type.AbstractType retType = null;
        FnInfo fnInfo = null;
        SymInfo sym = this.idNode.symInfo();
        if(sym instanceof FnInfo){
            fnInfo = (FnInfo)sym;
        }
        
        Type.AbstractType type = this.idNode.typeCheck();
        if (type != null && !type.isFnType()) {  
            ErrMsg.fatal(this.idNode.lineNum, this.idNode.charNum, 
                         "Attempt to call a non-function");
            retType =  new Type.ErrorType();
        }
        //check if it is a function type
        if(fnInfo == null ){
            ErrMsg.fatal(this.idNode.lineNum, this.idNode.charNum, " Attempt to call a undefined-function");
            retType = new Type.ErrorType();
        }else{
            retType = fnInfo.getReturnType();
            int actualNumParams = fnInfo.getNumParams();
            int getNumParams = 0;
            if(this.expListNode != null){
                getNumParams = this.expListNode.getExpNodeList().size();
            }
            //check if we get the correct number of params
            if( getNumParams != actualNumParams ){
                ErrMsg.fatal(this.idNode.lineNum, this.idNode.charNum, "Function call with wrong number of args");
            }else{
                // List<Type.AbstractType> fnParamTypes = ( (FnInfo)(this.idNode.symInfo()) ).getParamTypes();
                // List<ExpNode> getFnExpNodes = this.expListNode.getExpNodeList();

                // // check if the params type are all matched
                // for(int ithParam = 0; ithParam < getFnExpNodes.size(); ithParam++){
                //     ExpNode tmpExpNode = getFnExpNodes.get(ithParam);
                //     if( !tmpExpNode.type().equals(fnParamTypes.get(ithParam)) ){
                //         ErrMsg.fatal(tmpExpNode.lineNum, tmpExpNode.charNum, "Type of actual does not match type of formal");
                //     }
                // }
                this.expListNode.typeCheck(fnInfo.getParamTypes());
            }
        }
        return retType;
    }

    public Type.AbstractType type(){
        return this.idNode.type();
    }

    public CallExpNode(IdNode name) {
        idNode = name;
    }

    public void unparse(PrintWriter p, int indent) {
        idNode.unparse(p, indent);
        p.print("(");
        if(expListNode != null){
            expListNode.unparse(p, indent);
        }
        p.print(")");
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's 
     * two children
    */
    public void nameAnalysis(SymTable symTable){
        this.idNode.nameAnalysis(symTable);
        // System.out.println(symTable == null);
        // System.out.println(this.expListNode == null);
        if(this.expListNode != null)
            this.expListNode.nameAnalysis(symTable);
        
    }

}

public static abstract class UnaryExpNode extends ExpNode {

    public UnaryExpNode(ExpNode exp) {
        super(exp.lineNum,exp.charNum);
        myExp = exp;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }

    // one child
    protected ExpNode myExp;
}

public static abstract class BinaryExpNode extends ExpNode {
    
    protected ExpNode exp1;
    protected ExpNode exp2;

    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        this.exp1 = exp1;
        this.exp2 = exp2;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
    */
    public void nameAnalysis(SymTable symTab) {
        this.exp1.nameAnalysis(symTab);
        this.exp2.nameAnalysis(symTab);
    }
}

// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

public static class UnaryMinusNode extends UnaryExpNode {


    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(-");
        this.myExp.unparse(p, indent);
        p.print(")");
    }
    
    public Type.AbstractType type(){
        return new Type.IntType();
    }

    public Type.AbstractType typeCheck(){
        Type.AbstractType type = this.myExp.typeCheck();
        Type.AbstractType retType = new Type.IntType();

        if(type.isErrorType()){
            retType = new Type.ErrorType();
        }else if(!type.isErrorType() && !type.isIntType()){
            retType = new Type.ErrorType();
            ErrMsg.fatal(lineNum, charNum, "Arithmetic operator applied to non-numeric operand");
        }else{
            ;
        }

        return retType;
    }
}

public static class NotNode extends UnaryExpNode {

    public NotNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("!");
        this.myExp.unparse(p, indent);
        p.print(")");
    }

    public Type.AbstractType type(){
        return new Type.BoolType();
    }

    public Type.AbstractType typeCheck(){
        Type.AbstractType type = this.myExp.typeCheck();
        Type.AbstractType retType = new Type.BoolType();

        if(type.isErrorType()){
            retType = new Type.ErrorType();
        }else if(!type.isErrorType() && type.isBoolType()){
            retType = new Type.ErrorType();
            ErrMsg.fatal(lineNum, charNum, "Logical operator applied to non-bool operand");
        }else{
            ;
        }
        return retType;
    }
}

//////////

public static abstract class ArithmeticExpNode extends BinaryExpNode {

    public ArithmeticExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

}

public static abstract class LogicalExpNode extends BinaryExpNode {

    public LogicalExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
}

public static abstract class EqualityExpNode extends BinaryExpNode {

    public EqualityExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public Type.AbstractType typeCheck(){
        Type.AbstractType expType1 = this.exp1.typeCheck();
        Type.AbstractType expType2 = this.exp2.typeCheck();
        Type.AbstractType retType = new Type.BoolType();
        //f() == g() is not allowed
        if(expType1.isVoidType() && expType2.isVoidType()){
            ErrMsg.fatal(lineNum, charNum, "Equality operator applied to void functions");
            retType = new Type.ErrorType();
        }
        // func1 == func2 is not allowed
        else if(expType1.isFnType() && expType2.isFnType()){
            ErrMsg.fatal(lineNum, charNum, "Equality operator applied to functions");
            retType = new Type.ErrorType();
        }
        // struct1 == struct2 is not allowed
        else if(expType1.isStructDefType() && expType2.isStructDefType()){
            ErrMsg.fatal(lineNum, charNum, "Equality operator applied to struct names");
            retType = new Type.ErrorType();
        }else if(expType1.isStructType() && expType2.isStructType()){
            ErrMsg.fatal(lineNum, charNum, "Equality operator applied to struct variables");
            retType = new Type.ErrorType();
        }
        // type missmatched
        else if(!expType1.isErrorType() && !expType2.isErrorType() && !expType1.equals(expType2)){
            ErrMsg.fatal(lineNum, charNum, "Type missmatch");
            retType = new Type.ErrorType();
        }else if(expType1.isErrorType() || expType2.isErrorType()){
            retType = new Type.ErrorType();
        }
        return retType;
    }
}

public static abstract class RelationalExpNode extends BinaryExpNode {

    public RelationalExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
}

// **********************************************************************
// Subp classes of BinaryExpNode
// **********************************************************************

public static class PlusNode extends ArithmeticExpNode {

    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        this.exp1.unparse(p, indent);
        p.print(" + ");
        this.exp2.unparse(p, indent);
        p.print(")");
    }

    public Type.AbstractType typeCheck(){
        Type.AbstractType retType = null;
        // check if they are both int 
        if(this.exp1.typeCheck().isIntType() && this.exp2.typeCheck().isIntType()){
            return new Type.IntType();
        }else{
            ErrMsg.fatal(lineNum, charNum, "Arithmetic operator applied to non-numeric operand");
        }
        return retType;
    }
}

public static class MinusNode extends ArithmeticExpNode {

    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        this.exp1.unparse(p, indent);
        p.print(" - ");
        this.exp2.unparse(p, indent);
        p.print(")");
    }

    public Type.AbstractType typeCheck(){
        Type.AbstractType retType = null;
        // check if they are both int 
        if(this.exp1.typeCheck().isIntType() && this.exp2.typeCheck().isIntType()){
            return new Type.IntType();
        }else{
            ErrMsg.fatal(lineNum, charNum, "Arithmetic operator applied to non-numeric operand");
        }
        return retType;
    }
}

public static class TimesNode extends ArithmeticExpNode {

    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        this.exp1.unparse(p, indent);
        p.print(" * ");
        this.exp2.unparse(p, indent);
        p.print(")");
    }

    public Type.AbstractType typeCheck(){
        Type.AbstractType retType = null;
        // check if they are both int 
        if(this.exp1.typeCheck().isIntType() && this.exp2.typeCheck().isIntType()){
            return new Type.IntType();
        }else{
            ErrMsg.fatal(lineNum, charNum, "Arithmetic operator applied to non-numeric operand");
        }
        return retType;
    }
}

public static class DivideNode extends ArithmeticExpNode {

    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
        this.exp1.unparse(p, indent);
        p.print(" / ");
        this.exp2.unparse(p, indent);
        p.print(")");
    }

    public Type.AbstractType typeCheck(){
        Type.AbstractType retType = null;
        // check if they are both int 
        if(this.exp1.typeCheck().isIntType() && this.exp2.typeCheck().isIntType()){
            return new Type.IntType();
        }else{
            ErrMsg.fatal(lineNum, charNum, "Arithmetic operator applied to non-numeric operand");
        }
        return retType;
    }
}

public static class AndNode extends LogicalExpNode {

    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
        this.exp1.unparse(p, indent);
        p.print(" && ");
        this.exp2.unparse(p, indent);
        p.print(")");
    }

    public Type.AbstractType typeCheck(){
        Type.AbstractType retType = null;
        // check if they are both int 
        if(this.exp1.typeCheck() != null && this.exp1.typeCheck().isBoolType() && this.exp2.typeCheck().isBoolType()){
            return new Type.BoolType();
        }else{
            ErrMsg.fatal(lineNum, charNum, "Logical operator applied to non-bool operand");
        }
        return retType;
    }
}

public static class OrNode extends LogicalExpNode {

    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        this.exp1.unparse(p, indent);
        p.print(" || ");
        this.exp2.unparse(p, indent);
        p.print(")");
    }

    public Type.AbstractType typeCheck(){
        Type.AbstractType retType = null;
        // check if they are both int 
        if(this.exp1.typeCheck().isBoolType() && this.exp2.typeCheck().isBoolType()){
            return new Type.BoolType();
        }else{
            ErrMsg.fatal(lineNum, charNum, "Logical operator applied to non-bool operand");
        }
        return retType;
    }

}

public static class EqualsNode extends EqualityExpNode {

    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
        this.exp1.unparse(p, indent);
        p.print(" == ");
        this.exp2.unparse(p, indent);
        p.print(")");
    }
}

public static class NotEqualsNode extends EqualityExpNode {

    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
        this.exp1.unparse(p, indent);
        p.print(" != ");
        this.exp2.unparse(p, indent);
        p.print(")");
    }
}

public static class LessNode extends RelationalExpNode {

    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        this.exp1.unparse(p, indent);
        p.print(" < ");
        this.exp2.unparse(p, indent);
        p.print(")");
    }

    public Type.AbstractType typeCheck(){
        Type.AbstractType retType = null;
        // check if they are both int 
        if(this.exp1.typeCheck().isIntType() && this.exp2.typeCheck().isIntType()){
            return new Type.IntType();
        }else{
            ErrMsg.fatal(lineNum, charNum, "Arithmetic operator applied to non-numeric operand");
        }
        return retType;
    }
}

public static class GreaterNode extends RelationalExpNode {

    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        this.exp1.unparse(p, indent);
        p.print(" > ");
        this.exp2.unparse(p, indent);
        p.print(")");
    }

    public Type.AbstractType typeCheck(){
        Type.AbstractType retType = null;
        // check if they are both int 
        if(this.exp1.typeCheck().isIntType() && this.exp2.typeCheck().isIntType()){
            return new Type.IntType();
        }else{
            ErrMsg.fatal(lineNum, charNum, "Arithmetic operator applied to non-numeric operand");
        }
        return retType;
    }
}

public static class LessEqNode extends RelationalExpNode {

    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        this.exp1.unparse(p, indent);
        p.print(" <= ");
        this.exp2.unparse(p, indent);
        p.print(")");
    }

    public Type.AbstractType typeCheck(){
        Type.AbstractType retType = null;
        // check if they are both int 
        if(this.exp1.typeCheck().isIntType() && this.exp2.typeCheck().isIntType()){
            return new Type.IntType();
        }else{
            ErrMsg.fatal(lineNum, charNum, "Arithmetic operator applied to non-numeric operand");
        }
        return retType;
    }
}

public static class GreaterEqNode extends RelationalExpNode {

    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        this.exp1.unparse(p, indent);
        p.print(" >= ");
        this.exp2.unparse(p, indent);
        p.print(")");
    }

    public Type.AbstractType typeCheck(){
        Type.AbstractType retType = null;
        // check if they are both int 
        if(this.exp1.typeCheck().isIntType() && this.exp2.typeCheck().isIntType()){
            return new Type.IntType();
        }else{
            ErrMsg.fatal(lineNum, charNum, "Arithmetic operator applied to non-numeric operand");
        }
        return retType;
    }
}
}
