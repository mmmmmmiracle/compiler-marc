package ast;

import java.io.*;
import java.util.*;
import lexer.*;
import symtable.*;

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
//     DeclListNode        linked list of DeclNode
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode
//       StructDeclNode    IdNode, DeclListNode
//
//     FormalsListNode     linked list of FormalDeclNode
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        linked list of StmtNode
//     ExpListNode         linked list of ExpNode
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
//       IntLitNode          -- none --
//       StringLitNode       -- none --
//       TrueNode            -- none --
//       FalseNode           -- none --
//       IdNode              -- none --
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

    public ProgramNode(DeclListNode L) {
        myDeclList = L;
    }
    
    /**
     * typeCheck
     */
    public void typeCheck() {
        myDeclList.typeCheck();
    }

    /**
     * nameAnalysis
     * Creates an empty symbol table for the outermost scope, then processes
     * all of the globals, struct defintions, and functions in the program.
     */
    public void nameAnalysis() {
        SymTable symTab = new SymTable();
        myDeclList.nameAnalysis(symTab);
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }

    // 1 kid
    private DeclListNode myDeclList;
}

public static class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;
    }
       
    /**
     * typeCheck
     */
    public void typeCheck() {
        for (DeclNode node : myDecls) {
            node.typeCheck();
        }
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, process all of the decls in the list.
     */
    public void nameAnalysis(SymTable symTab) {
        // nameAnalysis(symTab, symTab);
        for (DeclNode node : myDecls) {
            node.nameAnalysis(symTab);
        }
    }
    
    /**
     * nameAnalysis inside a struct definition
     * Given a symbol table structSymTab and a global symbol table globalTab
     * process all of the decls in the list.
     */    
    public void nameAnalysis(SymTable structSymTab, SymTable globalTab) {
        for (DeclNode node : myDecls) {
            if (node instanceof VarDeclNode) {
                ((VarDeclNode)node).nameAnalysis(structSymTab, globalTab);
            } else {
                // this should never happen
                node.nameAnalysis(globalTab);
            }
        }
    }    

    public void unparse(PrintWriter p, int indent) {
        Iterator<DeclNode> it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                (it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }

    // list of kids (DeclNodes)
    private List<DeclNode> myDecls;
}

public static class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * for each formal decl in the list
     *     process the formal decl
     *     if there was no error, add type of formal decl to list
     */
    public List<Type.AbstractType> nameAnalysis(SymTable symTab) {
        List<Type.AbstractType> typeList = new LinkedList<Type.AbstractType>();
        for (FormalDeclNode node : myFormals) {
            SymInfo info = node.nameAnalysis(symTab);
            if (info != null) {
                typeList.add(info.getType());
            }
        }
        return typeList;
    }    
    
    /**
     * Return the number of formals in this list.
     */
    public int length() {
        return myFormals.size();
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }

    // list of kids (FormalDeclNodes)
    private List<FormalDeclNode> myFormals;
}

public static class FnBodyNode extends ASTnode {

    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }
 
    /**
     * typeCheck
     */
    public void typeCheck(Type.AbstractType retType) {
        myStmtList.typeCheck(retType);
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the declaration list
     * - process the statement list
     */
    public void nameAnalysis(SymTable symTab) {
        myDeclList.nameAnalysis(symTab);
        myStmtList.nameAnalysis(symTab);
    }    
    
    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }

    // 2 kids
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

public static class StmtListNode extends ASTnode {

    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }
    
    /**
     * typeCheck
     */
    public void typeCheck(Type.AbstractType retType) {
        for(StmtNode node : myStmts) {
            node.typeCheck(retType);
        }
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, process each statement in the list.
     */
    public void nameAnalysis(SymTable symTab) {
        for (StmtNode node : myStmts) {
            node.nameAnalysis(symTab);
        }
    }    

    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        }
    }

    // list of kids (StmtNodes)
    private List<StmtNode> myStmts;
}

public static class ExpListNode extends ASTnode {

    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }
      
    /**
     * typeCheck
     */
    public void typeCheck(List<Type.AbstractType> typeList) {
        int k = 0;
        try {
            for (ExpNode node : myExps) {
                Type.AbstractType actualType = node.typeCheck();     // actual type of arg
                
                if (!actualType.isErrorType()) {        // if this is not an error
                    Type.AbstractType formalType = typeList.get(k);  // get the formal type
                    if ( ! formalType.equals(actualType)) {
                        ErrMsg.fatal(node.lineNum, node.charNum,
                                     "Type of actual does not match type of formal");
                    }
                }
                k++;
            }
        } catch (NoSuchElementException e) {
            System.err.println("unexpected NoSuchElementException in ExpListNode.typeCheck");
            System.exit(-1);
        }
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, process each exp in the list.
     */
    public void nameAnalysis(SymTable symTab) {
        for (ExpNode node : myExps) {
            node.nameAnalysis(symTab);
        }
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }

    public int size() {
        return myExps.size();
    }

    // list of kids (ExpNodes)
    private List<ExpNode> myExps;
}

// **********************************************************************
// DeclNode and its sub classes
// **********************************************************************

public static abstract class DeclNode extends ASTnode {

    /**
     * default version of typeCheck for non-function decls
     */
    public void typeCheck() { } 
    
    /**
     * Note: a formal decl needs to return an info
     */
    public abstract SymInfo nameAnalysis(SymTable symTab);
}

public static class VarDeclNode extends DeclNode {

    public VarDeclNode(TypeNode type, IdNode id, int size) {
        myType = type;
        myId = id;
        mySize = size;
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
     * globalTab is global symbol table (for struct type names)
     * symTab and globalTab can be the same
     */
    public SymInfo nameAnalysis(SymTable symTab) {
        // return nameAnalysis(symTab, symTab);
        boolean badDecl = false;
        String name = myId.name();
        SymInfo info = null;
        IdNode structId = null;

        if (myType instanceof VoidNode) {  // check for void type
            ErrMsg.fatal(myId.lineNum, myId.charNum, 
                         "Non-function declared void");
            badDecl = true;        
        }
        else if (myType instanceof StructNode) {
            structId = ((StructNode) myType).idNode();
            info = symTab.lookupGlobal(structId.name());
            // if the name for the struct type is not found, 
            // or is not a struct type
            if (info == null || !(info instanceof StructDefInfo)) {
                ErrMsg.fatal(structId.lineNum, structId.charNum, 
                             "Invalid name of struct type");
                badDecl = true;
            }
            else {
                structId.link(info);
            }
        }
        SymInfo dup = symTab.lookupLocal(name);
        
        if (dup != null) {
            ErrMsg.fatal(myId.lineNum, myId.charNum, 
                         "Multiple declaration of identifier");
            badDecl = true;            
        }
        if (!badDecl) {  // insert into symbol table
            if (myType instanceof StructNode) {
                info = new StructInfo(structId);
            }
            else {
                info = new SymInfo(myType.type());
            }
            symTab.addDecl(name, info);
            myId.link(info);
        }
        
        return info;
    }
    
    public SymInfo nameAnalysis(SymTable structSymTab, SymTable globalTab) {
        boolean badDecl = false;
        String name = myId.name();
        SymInfo info = null;
        IdNode structId = null;

        if (myType instanceof VoidNode) {  // check for void type
            ErrMsg.fatal(myId.lineNum, myId.charNum, 
                         "Non-function declared void");
            badDecl = true;        
        }
        else if (myType instanceof StructNode) {
            structId = ((StructNode) myType).idNode();
            info = globalTab.lookupGlobal(structId.name());
            // if the name for the struct type is not found, 
            // or is not a struct type
            if (info == null || !(info instanceof StructDefInfo)) {
                ErrMsg.fatal(structId.lineNum, structId.charNum, 
                             "Invalid name of struct type");
                badDecl = true;
            }
            else {
                structId.link(info);
            }
        }
        SymInfo dup = structSymTab.lookupLocal(name);
        
        if (dup != null) {
            ErrMsg.fatal(myId.lineNum, myId.charNum, 
                         "Multiple declaration of struct field");
            badDecl = true;            
        }
        if (!badDecl) {  // insert into symbol table
            if (myType instanceof StructNode) {
                info = new StructInfo(structId);
            }
            else {
                info = new SymInfo(myType.type());
            }
            structSymTab.addDecl(name, info);
            myId.link(info);
        }
        
        return info;
    }    

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.println(";");
    }

    // 3 kids
    private TypeNode myType;
    private IdNode myId;
    private int mySize;  // use value NOT_STRUCT if this is not a struct type

    public static int NOT_STRUCT = -1;
}

public static class FnDeclNode extends DeclNode {

    public FnDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FnBodyNode body) {
        myType = type;
        myId = id;
        myFormalsList = formalList;
        myBody = body;
    }
       
    /**
     * typeCheck
     */
    public void typeCheck() {
        myBody.typeCheck(myType.type());
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
        String name = myId.name();
        FnInfo info = null;
        
        SymInfo dup = symTab.lookupLocal(name);

        if (dup != null) {
            ErrMsg.fatal(myId.lineNum, myId.charNum,
                         "Multiply declared identifier");
        }
        
        else { // add function name to local symbol table
            info = new FnInfo(myType.type(), myFormalsList.length());
            symTab.addDecl(name, info);
            myId.link(info);
        }
        
        symTab.addScope();  // add a new scope for locals and params
        
        // process the formals
        List<Type.AbstractType> typeList = myFormalsList.nameAnalysis(symTab);
        if (info != null) {
            info.addFormals(typeList);
        }
        
        myBody.nameAnalysis(symTab); // process the function body
        symTab.removeScope();  // exit scope
        return null;
    }    

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.print("(");
        myFormalsList.unparse(p, 0);
        p.println(") {");
        myBody.unparse(p, indent+4);
        p.println("}\n");
    }

    // 4 kids
    private TypeNode myType;
    private IdNode myId;
    private FormalsListNode myFormalsList;
    private FnBodyNode myBody;
}

public static class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        myType = type;
        myId = id;
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
        String name = myId.name();
        boolean badDecl = false;
        SymInfo info = null;
        
        if (myType instanceof VoidNode) {
            ErrMsg.fatal(myId.lineNum, myId.charNum, 
                         "Non-function declared void");
            badDecl = true;        
        }

        if (symTab.lookupLocal(name) != null) {
            ErrMsg.fatal(myId.lineNum, myId.charNum, 
                         "Multiply declared identifier");
            badDecl = true;
        }
        
        if (!badDecl) {  // insert into symbol table
            info = new SymInfo(myType.type());
            symTab.addDecl(name, info);
            myId.link(info);
        }
        
        return info;
    }    

    public void unparse(PrintWriter p, int indent) {
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
    }

    // 2 kids
    private TypeNode myType;
    private IdNode myId;
}

public static class StructDeclNode extends DeclNode {

    public StructDeclNode(IdNode id, DeclListNode declList) {
        myId = id;
        myDeclList = declList;
    }

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
    public SymInfo nameAnalysis(SymTable symTab) {
        String name = myId.name();
        boolean badDecl = false;
        
        SymInfo dup = symTab.lookupLocal(name);

        if (dup != null) {
            ErrMsg.fatal(myId.lineNum, myId.charNum, 
                         "Multiply declared identifier");
            badDecl = true;            
        }

        SymTable structSymTab = new SymTable();
        
        // process the fields of the struct
        myDeclList.nameAnalysis(structSymTab, symTab);
        
        if (!badDecl) {
            StructDefInfo info = new StructDefInfo(structSymTab);
            symTab.addDecl(name, info);
            myId.link(info);
        }
        
        return null;
    }    
    
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("struct ");
		myId.unparse(p, 0);
		p.println("{");
        myDeclList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("};\n");

    }

    // 2 kids
    private IdNode myId;
	 private DeclListNode myDeclList;
}

// **********************************************************************
// TypeNode and its sub classes
// **********************************************************************

public static abstract class TypeNode extends ASTnode {

    /*
     * all subclasses must provide a type method
     */
    public abstract Type.AbstractType type();
}

public static class IntNode extends TypeNode {

    public IntNode() {
    }

    public Type.AbstractType type() {
        return new Type.IntType();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }
}

public static class BoolNode extends TypeNode {

    public BoolNode() {
    }
 
    public Type.AbstractType type() {
        return new Type.BoolType();
    }
   
    public void unparse(PrintWriter p, int indent) {
        p.print("bool");
    }
}

public static class VoidNode extends TypeNode {

    public VoidNode() {
    }
 
    public Type.AbstractType type() {
        return new Type.VoidType();
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }
}

public static class StructNode extends TypeNode {

    public StructNode(IdNode id) {
      myId = id;
    }

    public Type.AbstractType type() {
        return new Type.StructType(myId);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("struct ");
        myId.unparse(p, 0);
    }

    public IdNode idNode() {
      return myId;
    }
  
    // 1 kid
    private IdNode myId;
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

public static abstract class StmtNode extends ASTnode {

   /**
     * nameAnalysis
     */
    public void nameAnalysis(SymTable symTab) {
        
    }

    public abstract void typeCheck(Type.AbstractType retType);
}

public static class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        myAssign = assign;
    }
    
    /**
     * typeCheck
     */
    public void typeCheck(Type.AbstractType retType) {
        myAssign.typeCheck();
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myAssign.nameAnalysis(symTab);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(";");
    }

    // 1 kid
    private AssignNode myAssign;
}

public static class PostIncStmtNode extends StmtNode {

    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }
     
    /**
     * typeCheck
     */
    public void typeCheck(Type.AbstractType retType) {
        Type.AbstractType type = myExp.typeCheck();
        
        if ( ! type.isErrorType() && ! type.isIntType() ) {
            ErrMsg.fatal(myExp.lineNum, myExp.charNum,
                         "Arithmetic operator applied to non-numeric operand");
        }
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("++;");
    }

    // 1 kid
    private ExpNode myExp;
}

public static class PostDecStmtNode extends StmtNode {

    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }
    
    /**
     * typeCheck
     */
    public void typeCheck(Type.AbstractType retType) {
        Type.AbstractType type = myExp.typeCheck();
        
        if ( ! type.isErrorType() && ! type.isIntType() ) {
            ErrMsg.fatal(myExp.lineNum, myExp.charNum,
                         "Arithmetic operator applied to non-numeric operand");
        }
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("--;");
    }

    // 1 kid
    private ExpNode myExp;
}

public static class ReadStmtNode extends StmtNode {

    public ReadStmtNode(ExpNode e) {
        myExp = e;
    }
 
    /**
     * typeCheck
     */
    public void typeCheck(Type.AbstractType retType) {
        Type.AbstractType type = myExp.typeCheck();
        
        if ( type.isFnType() ) {
            ErrMsg.fatal(myExp.lineNum, myExp.charNum,
                         "Attempt to read a function");
        }
        
        if ( type.isStructDefType() ) {
            ErrMsg.fatal(myExp.lineNum, myExp.charNum,
                         "Attempt to read a struct name");
        }
        
        if ( type.isStructType() ) {
            ErrMsg.fatal(myExp.lineNum, myExp.charNum,
                         "Attempt to read a struct variable");
        }
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }    

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("cin >> ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // 1 kid (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;
}

public static class WriteStmtNode extends StmtNode {

    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
    }

    /**
     * typeCheck
     */
    public void typeCheck(Type.AbstractType retType) {
        Type.AbstractType type = myExp.typeCheck();
        
        if (type.isFnType()) {
            ErrMsg.fatal(myExp.lineNum, myExp.charNum,
                         "Attempt to write a function");
        }
        
        if (type.isStructDefType()) {
            ErrMsg.fatal(myExp.lineNum, myExp.charNum,
                         "Attempt to write a struct name");
        }
        
        if (type.isStructType()) {
            ErrMsg.fatal(myExp.lineNum, myExp.charNum,
                         "Attempt to write a struct variable");
        }
        
        if (type.isVoidType()) {
            ErrMsg.fatal(myExp.lineNum, myExp.charNum,
                         "Attempt to write void");
        }
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("cout << ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // 1 kid
    private ExpNode myExp;
}

public static class IfStmtNode extends StmtNode {

    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }
     
    /**
     * typeCheck
     */
    public void typeCheck(Type.AbstractType retType) {
        Type.AbstractType type = myExp.typeCheck();
        
        if ( ! type.isErrorType() && ! type.isBoolType() ) {
            ErrMsg.fatal(myExp.lineNum, myExp.charNum,
                         "Non-bool expression used as an if condition");        
        }
        
        myStmtList.typeCheck(retType);
    }
   
    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the condition
     * - enter a new scope
     * - process the decls and stmts
     * - exit the scope
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
        symTab.addScope();
        myDeclList.nameAnalysis(symTab);
        myStmtList.nameAnalysis(symTab);
        symTab.removeScope();
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
    }

    // e kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

public static class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
                          StmtListNode slist1, DeclListNode dlist2,
                          StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }
    
    /**
     * typeCheck
     */
    public void typeCheck(Type.AbstractType retType) {
        Type.AbstractType type = myExp.typeCheck();
        
        if ( ! type.isErrorType() && ! type.isBoolType() ) {
            ErrMsg.fatal(myExp.lineNum, myExp.charNum,
                         "Non-bool expression used as an if condition");        
        }
        
        myThenStmtList.typeCheck(retType);
        myElseStmtList.typeCheck(retType);
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
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
        symTab.addScope();
        myThenDeclList.nameAnalysis(symTab);
        myThenStmtList.nameAnalysis(symTab);
        symTab.removeScope();
        symTab.addScope();
        myElseDeclList.nameAnalysis(symTab);
        myElseStmtList.nameAnalysis(symTab);
        symTab.removeScope();
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myThenDeclList.unparse(p, indent+4);
        myThenStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
        doIndent(p, indent);
        p.println("else {");
        myElseDeclList.unparse(p, indent+4);
        myElseStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");        
    }

    // 5 kids
    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;
}

public static class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }
     
    /**
     * typeCheck
     */
    public void typeCheck(Type.AbstractType retType) {
        Type.AbstractType type = myExp.typeCheck();
        
        if ( ! type.isErrorType() && ! type.isBoolType() ) {
            ErrMsg.fatal(myExp.lineNum, myExp.charNum,
                         "Non-bool expression used as a while condition");        
        }
        
        myStmtList.typeCheck(retType);
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - process the condition
     * - enter a new scope
     * - process the decls and stmts
     * - exit the scope
     */
    public void nameAnalysis(SymTable symTab) {
        myExp.nameAnalysis(symTab);
        symTab.addScope();
        myDeclList.nameAnalysis(symTab);
        myStmtList.nameAnalysis(symTab);
        symTab.removeScope();
    }
	
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("while (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
    }

    // 3 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

public static class CallStmtNode extends StmtNode {

    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }
    
    /**
     * typeCheck
     */
    public void typeCheck(Type.AbstractType retType) {
        myCall.typeCheck();
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis
     * on this node's child
     */
    public void nameAnalysis(SymTable symTab) {
        myCall.nameAnalysis(symTab);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myCall.unparse(p, indent);
        p.println(";");
    }

    // 1 kid
    private CallExpNode myCall;
}

public static class ReturnStmtNode extends StmtNode {

    public ReturnStmtNode(ExpNode exp) {
        this(exp,0,0);
    }
  
    /**
     * typeCheck
     */
    public void typeCheck(Type.AbstractType retType) {
        if (myExp != null) {  // return value given
            Type.AbstractType type = myExp.typeCheck();
            
            if ( retType.isVoidType() ) {
                ErrMsg.fatal(myExp.lineNum, myExp.charNum,
                             "Return with a value in a void function");                
            }
            
            else if ( ! retType.isErrorType() && ! type.isErrorType() && ! retType.equals(type) ) {
                ErrMsg.fatal(myExp.lineNum, myExp.charNum,
                             "Bad return value");
            }
        }
        
        else {  // no return value given -- ok if this is a void function
            if ( ! retType.isVoidType() ) {
                ErrMsg.fatal(myLinenum, myCharnum, "Missing return value");                
            }
        }
        
    }
   
    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's child,
     * if it has one
     */
    public void nameAnalysis(SymTable symTab) {
        if (myExp != null) {
            myExp.nameAnalysis(symTab);
        }
    }
    public ReturnStmtNode(ExpNode exp, int charnum, int linenum) {
        myExp = exp;
        myCharnum = charnum;
        myLinenum = linenum;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.unparse(p, 0);
        }
        p.println(";");
    }

    // 1 kid
    private ExpNode myExp; // possibly null
    private int myCharnum;
    private int myLinenum;
}

// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

public static abstract class ExpNode extends ASTnode {

    public ExpNode() {
        lineNum = 0;
        charNum = 0;
    }

    public ExpNode(int lineNum, int charNum) {
        this.lineNum = lineNum;
        this.charNum = charNum;
    }
    
    public abstract Type.AbstractType typeCheck();
    
    /**
     * Default version for nodes with no names
     */
    public void nameAnalysis(SymTable symTab) { }

    protected int lineNum;
    protected int charNum;
}

public static class IntLitNode extends ExpNode {
  
    public IntLitNode(int lineNum, int charNum, int intVal) {
        super(lineNum,charNum);
        myIntVal = intVal;
    }
        
    /**
     * typeCheck
     */
    public Type.AbstractType typeCheck() {
        return new Type.IntType();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }

    private int myIntVal;
}

public static class StringLitNode extends ExpNode {

    public StringLitNode(int lineNum, int charNum, String strVal) {
        super(lineNum,charNum);
        myStrVal = strVal;
    }
    
    /**
     * typeCheck
     */
    public Type.AbstractType typeCheck() {
        return new Type.StringType();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }
 
    private String myStrVal;
}

public static class TrueNode extends ExpNode {

    public TrueNode(int lineNum, int charNum) {
        super(lineNum,charNum);
    }
    
    /**
     * typeCheck
     */
    public Type.AbstractType typeCheck() {
        return new Type.BoolType();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }
}

public static class FalseNode extends ExpNode {

    public FalseNode(int lineNum, int charNum) {
        super(lineNum,charNum);
    }
    
    /**
     * typeCheck
     */
    public Type.AbstractType typeCheck() {
        return new Type.BoolType();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }
}

public static class IdNode extends ExpNode {

    public IdNode(int lineNum, int charNum, String strVal) {
        super(lineNum,charNum);
        myStrVal = strVal;
    }
    
    /**
     * typeCheck
     */
    public Type.AbstractType typeCheck() {
        if ( myInfo != null ) {
            return myInfo.getType();
        } 
        else {
            System.err.println("ID with null info field in IdNode.typeCheck");
            System.exit(-1);
        }
        return null;
    }
    
    /**
     * nameAnalysis
     * Given a symbol table symTab, do:
     * - check for use of undeclared name
     * - if ok, link to symbol table entry
     */
    public void nameAnalysis(SymTable symTab) {
        SymInfo info = symTab.lookupGlobal(myStrVal);
        
        if (info == null) {
            ErrMsg.fatal(lineNum, charNum, "Undeclared identifier");
        } else {
            link(info);
        }
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }
    
    // Link the given symbol to this ID.
    public void link(SymInfo info) {
        myInfo = info;
    }
    
    // Return the name of this ID.
    public String name() {
        return myStrVal;
    }

    // Return the symbol associated with this ID.
    public SymInfo info() {
        return myInfo;
    }   

    private String myStrVal;
    private SymInfo myInfo;
}

public static class DotAccessExpNode extends ExpNode {

    public DotAccessExpNode(ExpNode lhs, IdNode id) {
        super(id.lineNum,id.charNum);
        myLhs = lhs;
        myId = id;
    }
  
    /**
     * typeCheck
     */
    public Type.AbstractType typeCheck() {
        return myId.typeCheck();
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
        badAccess = false;
        SymTable structSymTab = null; // to lookup RHS of dot-access
        SymInfo info = null;
        
        myLhs.nameAnalysis(symTab);  // do name analysis on LHS
        
        // if myLhs is really an ID, then info will be a link to the ID's symbol
        if (myLhs instanceof IdNode) {
            IdNode id = (IdNode)myLhs;
            info = id.info();
            
            // check ID has been declared to be of a struct type
            
            if (info == null) { // ID was undeclared
                badAccess = true;
            }
            else if (info instanceof StructInfo) { 
                // get symbol table for struct type
                SymInfo tempSym = ((StructInfo)info).getStructType().info();
                structSymTab = ((StructDefInfo)tempSym).getSymTable();
            } 
            else {  // LHS is not a struct type
                ErrMsg.fatal(id.lineNum, id.charNum, 
                             "Dot-access of non-struct type");
                badAccess = true;
            }
        }
        
        // if myLhs is really a dot-access (i.e., myLhs was of the form
        // LHSloc.RHSid), then info will either be
        // null - indicating RHSid is not of a struct type, or
        // a link to the Sym for the struct type RHSid was declared to be
        else if (myLhs instanceof DotAccessExpNode) {
            DotAccessExpNode lhs = (DotAccessExpNode) myLhs;
            
            if (lhs.badAccess) {  // if errors in processing myLhs
                badAccess = true; // don't continue proccessing this dot-access
            }
            else { //  no errors in processing myLhs
                info = lhs.info();

                if (info == null) {  // no struct in which to look up RHS
                    ErrMsg.fatal(lhs.lineNum, lhs.charNum, 
                                 "Dot-access of non-struct type");
                    badAccess = true;
                }
                else {  // get the struct's symbol table in which to lookup RHS
                    if (info instanceof StructDefInfo) {
                        structSymTab = ((StructDefInfo)info).getSymTable();
                    }
                    else {
                        System.err.println("Unexpected Sym type in DotAccessExpNode");
                        System.exit(-1);
                    }
                }
            }

        }
        
        else { // don't know what kind of thing myLhs is
            System.err.println("Unexpected node type in LHS of dot-access");
            System.exit(-1);
        }
        
        // do name analysis on RHS of dot-access in the struct's symbol table
        if (!badAccess) {
            info = structSymTab.lookupGlobal(myId.name()); // lookup
                
            if (info == null) { // not found - RHS is not a valid field name
                ErrMsg.fatal(myId.lineNum, myId.charNum, 
                             "Invalid struct field name");
                badAccess = true;
            }
            
            else {
                myId.link(info);  // link the symbol
                // if RHS is itself as struct type, link the symbol for its struct 
                // type to this dot-access node (to allow chained dot-access)
                if (info instanceof StructInfo) {
                    myInfo = ((StructInfo)info).getStructType().info();
                }
            }
        }
    }    

    /**
     * Return the info associated with this dot-access node.
     */
    public SymInfo info() {
        return myInfo;
    }    
    
    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myLhs.unparse(p, 0);
		p.print(").");
		myId.unparse(p, 0);
    }

    // 4  kids
    private ExpNode myLhs;	
    private IdNode myId;
    private SymInfo myInfo;    // link to SymInfo for struct type
    private boolean badAccess; // to prevent multiple, cascading errors
}

public static class AssignNode extends ExpNode {

    public AssignNode(ExpNode lhs, ExpNode rhs) {
        super();
        myLhs = lhs;
        myRhs = rhs;
    }
 
    /**
     * typeCheck
     */
    public Type.AbstractType typeCheck() {
        Type.AbstractType typeLhs = myLhs.typeCheck();
        Type.AbstractType typeExp = myRhs.typeCheck();
        Type.AbstractType retType = typeLhs;
        
        if (typeLhs.isFnType() && typeExp.isFnType()) {
            ErrMsg.fatal(myLhs.lineNum, charNum, "Function assignment");
            retType = new Type.ErrorType();
        }
        
        if (typeLhs.isStructDefType() && typeExp.isStructDefType()) {
            ErrMsg.fatal(myLhs.lineNum, charNum, "Struct name assignment");
            retType = new Type.ErrorType();
        }
        
        if (typeLhs.isStructType() && typeExp.isStructType()) {
            ErrMsg.fatal(myLhs.lineNum, charNum, "Struct variable assignment");
            retType = new Type.ErrorType();
        }        
        
        if (!typeLhs.equals(typeExp) && !typeLhs.isErrorType() && !typeExp.isErrorType()) {
            ErrMsg.fatal(myLhs.lineNum, charNum, "Type mismatch");
            retType = new Type.ErrorType();
        }
        
        if (typeLhs.isErrorType() || typeExp.isErrorType()) {
            retType = new Type.ErrorType();
        }
        
        return retType;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's 
     * two children
     */
    public void nameAnalysis(SymTable symTab) {
        myLhs.nameAnalysis(symTab);
        myRhs.nameAnalysis(symTab);
    }

    public void unparse(PrintWriter p, int indent) {
        if (indent != -1)  p.print("(");
        myLhs.unparse(p, 0);
        p.print(" = ");
        myRhs.unparse(p, 0);
        if (indent != -1)  p.print(")");
    }

    // 2 kids
    private ExpNode myLhs;
    private ExpNode myRhs;
}

public static class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        super();
        myId = name;
        myExpList = elist;
    }
      
    /**
     * typeCheck
     */
    public Type.AbstractType typeCheck() {
        if ( ! myId.typeCheck().isFnType() ) {  
            ErrMsg.fatal(myId.lineNum, myId.charNum, 
                         "Attempt to call a non-function");
            return new Type.ErrorType();
        }
        
        FnInfo fnInfo = (FnInfo)(myId.info());
        
        if ( fnInfo == null ) {
            System.err.println("null sym for Id in CallExpNode.typeCheck");
            System.exit(-1);
        }
        
        if ( myExpList.size() != fnInfo.getNumParams() ) {
            ErrMsg.fatal(myId.lineNum, myId.charNum, 
                         "Function call with wrong number of args");
            return fnInfo.getReturnType();
        }
        
        myExpList.typeCheck(fnInfo.getParamTypes());
        return fnInfo.getReturnType();
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's 
     * two children
     */
    public void nameAnalysis(SymTable symTab) {
        myId.nameAnalysis(symTab);
        myExpList.nameAnalysis(symTab);
    }    

    public CallExpNode(IdNode name) {
        myId = name;
        myExpList = new ExpListNode(new LinkedList<ExpNode>());
    }

    // ** unparse **
    public void unparse(PrintWriter p, int indent) {
	    myId.unparse(p, 0);
		  p.print("(");
		  if (myExpList != null) {
			 myExpList.unparse(p, 0);
		  }
		  p.print(")");
    }

    // 2 kids
    private IdNode myId;
    private ExpListNode myExpList;  // possibly null
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
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1.lineNum,exp1.charNum);
        myExp1 = exp1;
        myExp2 = exp2;
    }

    /**
     * nameAnalysis
     * Given a symbol table symTab, perform name analysis on this node's 
     * two children
     */
    public void nameAnalysis(SymTable symTab) {
        myExp1.nameAnalysis(symTab);
        myExp2.nameAnalysis(symTab);
    }

    // two kids
    protected ExpNode myExp1;
    protected ExpNode myExp2;
}

// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

public static class UnaryMinusNode extends UnaryExpNode {

    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    /**
     * typeCheck
     */
    public Type.AbstractType typeCheck() {
        Type.AbstractType type = myExp.typeCheck();
        Type.AbstractType retType = new Type.IntType();
        
        if ( ! type.isErrorType() && ! type.isIntType() ) {
            ErrMsg.fatal(lineNum, charNum,
                         "Arithmetic operator applied to non-numeric operand");
            retType = new Type.ErrorType();
        }
        
        if (type.isErrorType()) {
            retType = new Type.ErrorType();
        }
        
        return retType;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(-");
        myExp.unparse(p, 0);
        p.print(")");
    }
}

public static class NotNode extends UnaryExpNode {

    public NotNode(ExpNode exp) {
        super(exp);
    }

    /**
     * typeCheck
     */
    public Type.AbstractType typeCheck() {
        Type.AbstractType type = myExp.typeCheck();
        Type.AbstractType retType = new Type.BoolType();
        
        if ( ! type.isErrorType() && ! type.isBoolType() ) {
            ErrMsg.fatal(lineNum, charNum,
                         "Logical operator applied to non-bool operand");
            retType = new Type.ErrorType();
        }
        
        if ( type.isErrorType() ) {
            retType = new Type.ErrorType();
        }
        
        return retType;
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(!");
		  myExp.unparse(p, 0);
		  p.print(")");
    }
}

//////////

public static abstract class ArithmeticExpNode extends BinaryExpNode {

    public ArithmeticExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    /**
     * typeCheck
     */
    public Type.AbstractType typeCheck() {
        Type.AbstractType type1 = myExp1.typeCheck();
        Type.AbstractType type2 = myExp2.typeCheck();
        Type.AbstractType retType = new Type.IntType();
        
        if ( ! type1.isErrorType() && ! type1.isIntType() ) {
            ErrMsg.fatal(myExp1.lineNum, myExp1.charNum,
                         "Arithmetic operator applied to non-numeric operand");
            retType = new Type.ErrorType();
        }
        
        if ( ! type2.isErrorType() && ! type2.isIntType() ) {
            ErrMsg.fatal(myExp2.lineNum, myExp2.charNum,
                         "Arithmetic operator applied to non-numeric operand");
            retType = new Type.ErrorType();
        }
        
        if ( type1.isErrorType() || type2.isErrorType() ) {
            retType = new Type.ErrorType();
        }
        
        return retType;
    }

}

public static abstract class LogicalExpNode extends BinaryExpNode {

    public LogicalExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    /**
     * typeCheck
     */
    public Type.AbstractType typeCheck() {
        Type.AbstractType type1 = myExp1.typeCheck();
        Type.AbstractType type2 = myExp2.typeCheck();
        Type.AbstractType retType = new Type.BoolType();
        
        if ( ! type1.isErrorType() && ! type1.isBoolType() ) {
            ErrMsg.fatal(myExp1.lineNum, myExp1.charNum,
                         "Logical operator applied to non-bool operand");
            retType = new Type.ErrorType();
        }
        
        if ( ! type2.isErrorType() && ! type2.isBoolType() ) {
            ErrMsg.fatal(myExp2.lineNum, myExp2.charNum,
                         "Logical operator applied to non-bool operand");
            retType = new Type.ErrorType();
        }
        
        if ( type1.isErrorType() || type2.isErrorType() ) {
            retType = new Type.ErrorType();
        }
        
        return retType;
    }

}

public static abstract class EqualityExpNode extends BinaryExpNode {

    public EqualityExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    /**
     * typeCheck
     */
    public Type.AbstractType typeCheck() {
        Type.AbstractType type1 = myExp1.typeCheck();
        Type.AbstractType type2 = myExp2.typeCheck();
        Type.AbstractType retType = new Type.BoolType();
        
        if ( type1.isVoidType() && type2.isVoidType() ) {
            ErrMsg.fatal(lineNum, charNum,
                         "Equality operator applied to void functions");
            retType = new Type.ErrorType();
        }
        
        if ( type1.isFnType() && type2.isFnType() ) {
            ErrMsg.fatal(lineNum, charNum,
                         "Equality operator applied to functions");
            retType = new Type.ErrorType();
        }
        
        if ( type1.isStructDefType() && type2.isStructDefType() ) {
            ErrMsg.fatal(lineNum, charNum,
                         "Equality operator applied to struct names");
            retType = new Type.ErrorType();
        }
        
        if ( type1.isStructType() && type2.isStructType() ) {
            ErrMsg.fatal(lineNum, charNum,
                         "Equality operator applied to struct variables");
            retType = new Type.ErrorType();
        }        
        
        if ( ! type1.equals(type2) && ! type1.isErrorType() && ! type2.isErrorType() ) {
            ErrMsg.fatal(lineNum, charNum,
                         "Type mismatch");
            retType = new Type.ErrorType();
        }
        
        if ( type1.isErrorType() || type2.isErrorType() ) {
            retType = new Type.ErrorType();
        }
        
        return retType;
    }

}

public static abstract class RelationalExpNode extends BinaryExpNode {

    public RelationalExpNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    /**
     * typeCheck
     */
    public Type.AbstractType typeCheck() {
        Type.AbstractType type1 = myExp1.typeCheck();
        Type.AbstractType type2 = myExp2.typeCheck();
        Type.AbstractType retType = new Type.BoolType();
        
        if ( ! type1.isErrorType() && ! type1.isIntType() ) {
            ErrMsg.fatal(myExp1.lineNum, myExp1.charNum,
                         "Relational operator applied to non-numeric operand");
            retType = new Type.ErrorType();
        }
        
        if ( ! type2.isErrorType() && ! type2.isIntType() ) {
            ErrMsg.fatal(myExp2.lineNum, myExp2.charNum,
                         "Relational operator applied to non-numeric operand");
            retType = new Type.ErrorType();
        }
        
        if ( type1.isErrorType() || type2.isErrorType() ) {
            retType = new Type.ErrorType();
        }
        
        return retType;
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
		  myExp1.unparse(p, 0);
		  p.print(" + ");
		  myExp2.unparse(p, 0);
		  p.print(")");
    }
}

public static class MinusNode extends ArithmeticExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		  myExp1.unparse(p, 0);
		  p.print(" - ");
		  myExp2.unparse(p, 0);
		  p.print(")");
    }
}

public static class TimesNode extends ArithmeticExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		  myExp1.unparse(p, 0);
		  p.print(" * ");
		  myExp2.unparse(p, 0);
		  p.print(")");
    }
}

public static class DivideNode extends ArithmeticExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		  myExp1.unparse(p, 0);
		  p.print(" / ");
		  myExp2.unparse(p, 0);
		  p.print(")");
    }
}

public static class AndNode extends LogicalExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		  myExp1.unparse(p, 0);
		  p.print(" && ");
		  myExp2.unparse(p, 0);
		  p.print(")");
    }
}

public static class OrNode extends LogicalExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		  myExp1.unparse(p, 0);
		  p.print(" || ");
		  myExp2.unparse(p, 0);
		  p.print(")");
    }
}

public static class EqualsNode extends EqualityExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		  myExp1.unparse(p, 0);
		  p.print(" == ");
		  myExp2.unparse(p, 0);
		  p.print(")");
    }
}

public static class NotEqualsNode extends EqualityExpNode {
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		  myExp1.unparse(p, 0);
		  p.print(" != ");
		  myExp2.unparse(p, 0);
		  p.print(")");
    }
}

public static class LessNode extends RelationalExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		  myExp1.unparse(p, 0);
		  p.print(" < ");
		  myExp2.unparse(p, 0);
		  p.print(")");
    }
}

public static class GreaterNode extends RelationalExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		  myExp1.unparse(p, 0);
		  p.print(" > ");
		  myExp2.unparse(p, 0);
		  p.print(")");
    }
}

public static class LessEqNode extends RelationalExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		  myExp1.unparse(p, 0);
		  p.print(" <= ");
		  myExp2.unparse(p, 0);
		  p.print(")");
    }
}

public static class GreaterEqNode extends RelationalExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		  myExp1.unparse(p, 0);
		  p.print(" >= ");
	 	  myExp2.unparse(p, 0);
		  p.print(")");
    }
}
}
