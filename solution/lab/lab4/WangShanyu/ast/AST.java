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
    DeclListNode L;
    public ProgramNode(DeclListNode L) {
        this.L = L;
    }

    public void unparse(PrintWriter p, int indent) {
        L.unparse(p, indent);
    }

}

public static class DeclListNode extends ASTnode {
    List<DeclNode> S;
    public DeclListNode(List<DeclNode> S) {
        this.S = S;
    }

    public void unparse(PrintWriter p, int indent) {
        for(DeclNode node : S){
            node.unparse(p, indent);
        }
    }

}

public static class FormalsListNode extends ASTnode {
    List<FormalDeclNode> S;
    public FormalsListNode(List<FormalDeclNode> S) {
        this.S = S;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("(");
        boolean flag = false;
        for(FormalDeclNode f: S){
            if(flag) p.print(", ");
            f.unparse(p, 0);
            if(!flag) flag = true;
        }
        p.print(")");
    }

}

public static class FnBodyNode extends ASTnode {
    DeclListNode declList;
    StmtListNode stmtList;
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        this.declList = declList;
        this.stmtList = stmtList;
    }

    public void unparse(PrintWriter p, int indent) {
       p.print(" {\n");
       declList.unparse(p, indent+4);
       stmtList.unparse(p, indent+4);
       doIndent(p, indent);
       p.print("}\n");
    }

}

public static class StmtListNode extends ASTnode {
    List<StmtNode> S;
    public StmtListNode(List<StmtNode> S) {
        this.S = S;
    }

    public void unparse(PrintWriter p, int indent) {
        for(StmtNode n: S){
            n.unparse(p, indent);
        }
    }

}

public static class ExpListNode extends ASTnode {
    List<ExpNode> S;
    public ExpListNode(List<ExpNode> S) {
        this.S = S;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        boolean flag = false;
        for(ExpNode n: S){
            if(flag) p.print(", ");
            n.unparse(p, 0);
            if(!flag) flag = true;
        }
    }

}

// **********************************************************************
// DeclNode and its sub classes
// **********************************************************************

public static abstract class DeclNode extends ASTnode {

}

public static class VarDeclNode extends DeclNode {
    TypeNode type;
    IdNode id;
    int size;
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        this.type = type;
        this.id = id;
        this.size = size;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        type.unparse(p, 0);
        id.unparse(p, 1);
        p.print(";\n");
    }

    // DO NOT CHANGE THIS (useful for the next labs)

    public static int NOT_STRUCT = -1;
}

public static class FnDeclNode extends DeclNode {
    TypeNode type;
    IdNode id;
    FormalsListNode formalList;
    FnBodyNode body;
    public FnDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FnBodyNode body) {
        this.type = type;
        this.id = id;
        this.formalList = formalList;
        this.body = body;
 
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        type.unparse(p, 0);
        id.unparse(p, 1);
        formalList.unparse(p,0);
        body.unparse(p, indent);
        p.print("\n");
    }

}

public static class FormalDeclNode extends DeclNode {
    TypeNode type;
    IdNode id;
    public FormalDeclNode(TypeNode type, IdNode id) {
        this.type = type;
        this.id = id;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        type.unparse(p, 0);
        id.unparse(p, 1);
    }

}

public static class StructDeclNode extends DeclNode {
    IdNode id;
    DeclListNode declList;
    public StructDeclNode(IdNode id, DeclListNode declList) {
        this.id = id;
        this.declList = declList;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("struct");
        id.unparse(p, 1);
        p.print("{\n");
        declList.unparse(p, indent+4);
        doIndent(p, indent);
        p.print("};\n\n");
    }

}

// **********************************************************************
// TypeNode and its sub classes
// **********************************************************************

public static abstract class TypeNode extends ASTnode {

}

public static class IntNode extends TypeNode {

    public IntNode() {
    
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("int");
    }
}

public static class BoolNode extends TypeNode {

    public BoolNode() {
    
    }
    
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("bool");
    }
}

public static class VoidNode extends TypeNode {

    public VoidNode() {
    
    }
    
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("void");
    }
}

public static class StructNode extends TypeNode {
    IdNode id;
    public StructNode(IdNode id) {
        this.id = id;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("struct");
        id.unparse(p, 1);
    }

    public IdNode idNode() {
        return id;
    }

}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

public static abstract class StmtNode extends ASTnode {
   
}

public static class AssignStmtNode extends StmtNode {
    AssignNode assign;
    public AssignStmtNode(AssignNode assign) {
        this.assign = assign;
    }

    public void unparse(PrintWriter p, int indent) {
        assign.unparse(p, indent);
        p.print(";\n");
    }

}

public static class PostIncStmtNode extends StmtNode {
    ExpNode exp;
    public PostIncStmtNode(ExpNode exp) {
        this.exp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        exp.unparse(p, indent);
        p.print("++;\n");
    }

}

public static class PostDecStmtNode extends StmtNode {
    ExpNode exp;
    public PostDecStmtNode(ExpNode exp) {
       this.exp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        exp.unparse(p, indent);
        p.print("--;\n");
    }

}

public static class ReadStmtNode extends StmtNode {
    ExpNode e;
    public ReadStmtNode(ExpNode e) {
        this.e = e;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("cin >>");
        e.unparse(p, 1);
        p.print(";\n");
    }

}

public static class WriteStmtNode extends StmtNode {
    ExpNode exp;
    public WriteStmtNode(ExpNode exp) {
        this.exp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("cout <<");
        exp.unparse(p, 1);
        p.print(";\n");
    }

}

public static class IfStmtNode extends StmtNode {
    ExpNode exp;
    DeclListNode dlist;
    StmtListNode slist;
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        this.exp = exp;
        this.dlist = dlist;
        this.slist = slist;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if (");
        exp.unparse(p,0);
        p.print(") {\n");
        dlist.unparse(p, indent+4);
        slist.unparse(p, indent+4);
        doIndent(p,indent);
        p.print("}\n");
    }

}

public static class IfElseStmtNode extends StmtNode {
    ExpNode exp;
    DeclListNode dlist1;
    StmtListNode slist1;
    DeclListNode dlist2;
    StmtListNode slist2;
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
        p.print("if (");
        exp.unparse(p,0);
        p.print(") {\n");
        dlist1.unparse(p, indent+4);
        slist1.unparse(p, indent+4);
        doIndent(p,indent);
        p.print("}\n");
        doIndent(p, indent);
        p.print("else {\n");
        dlist2.unparse(p, indent+4);
        slist2.unparse(p, indent+4);
        doIndent(p,indent);
        p.print("}\n");
    }

}

public static class WhileStmtNode extends StmtNode {
    ExpNode exp;
    DeclListNode dlist;
    StmtListNode slist;
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        this.exp = exp;
        this.dlist = dlist;
        this.slist = slist;
    }
	
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("while (");
        exp.unparse(p,0);
        p.print(") {\n");
        dlist.unparse(p, indent+4);
        slist.unparse(p, indent+4);
        doIndent(p,indent);
        p.print("}\n");
    }

}

public static class CallStmtNode extends StmtNode {
    CallExpNode call;
    public CallStmtNode(CallExpNode call) {
        this.call = call;
    }

    public void unparse(PrintWriter p, int indent) {
        call.unparse(p, indent);
        p.print(";\n");
    }

}

public static class ReturnStmtNode extends StmtNode {
    ExpNode exp;
    int charnum;
    int linenum;
    public ReturnStmtNode(ExpNode exp) {
        this.exp = exp;
    }

    public ReturnStmtNode(ExpNode exp, int charnum, int linenum) {
       this.exp = exp;
       this.charnum = charnum;
       this.linenum = linenum;
    }

    public void unparse(PrintWriter p, int indent) {
       doIndent(p, indent);
       p.print("return");
       if(exp!=null)
            exp.unparse(p,1);
       p.print(";\n");
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

    public ExpNode(int lineNum, int charNum) {
        this.lineNum = lineNum;
        this.charNum = charNum;
    }

    protected int lineNum;
    protected int charNum;
}

public static class IntLitNode extends ExpNode {
    int intVal;
    public IntLitNode(int lineNum, int charNum, int intVal) {
        super(lineNum, charNum);
        this.intVal = intVal;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print(intVal);
    }

}

public static class StringLitNode extends ExpNode {
    String strVal;
    public StringLitNode(int lineNum, int charNum, String strVal) {
        super(lineNum, charNum);
        this.strVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print(strVal);
    }

}

public static class TrueNode extends ExpNode {

    public TrueNode(int lineNum, int charNum) {
        super(lineNum, charNum);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("true");
    }
}

public static class FalseNode extends ExpNode {

    public FalseNode(int lineNum, int charNum) {
       super(lineNum, charNum);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("false");
    }
}

public static class IdNode extends ExpNode {
    String strVal;
    SymInfo info;
    public IdNode(int lineNum, int charNum, String strVal) {
        super(lineNum, charNum);
        this.strVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print(strVal);
    }
    public String name(){
        return strVal;
    }
    public void link(SymInfo info){
        this.info = info;
    }

}

public static class DotAccessExpNode extends ExpNode {
    ExpNode lhs;
    IdNode id;
    public DotAccessExpNode(ExpNode lhs, IdNode id) {
        this.lhs = lhs;
        this.id = id;
    }

    public void unparse(PrintWriter p, int indent) {
	   doIndent(p, indent);
       p.print("(");
       lhs.unparse(p, 0);
       p.print(").");
       id.unparse(p, 0);
    }

}

public static class AssignNode extends ExpNode {
    ExpNode lhs, rhs;
    public AssignNode(ExpNode lhs, ExpNode rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void unparse(PrintWriter p, int indent) {
        lhs.unparse(p, indent);
        p.print(" =");
        rhs.unparse(p, 1);
    }

}

public static class CallExpNode extends ExpNode {
    IdNode name;
    ExpListNode elist;
    public CallExpNode(IdNode name, ExpListNode elist) {
        this.name = name;
        this.elist = elist;
    }

    public CallExpNode(IdNode name) {
        this.name = name;
    }

    public void unparse(PrintWriter p, int indent) {
        name.unparse(p, indent);
        p.print("(");
        if(elist != null)
            elist.unparse(p, 0);
        p.print(")");
    }

}

public static abstract class UnaryExpNode extends ExpNode {

    public UnaryExpNode(ExpNode exp) {
        super(exp.lineNum,exp.charNum);
        myExp = exp;
    }

    // one child
    protected ExpNode myExp;
}

public static abstract class BinaryExpNode extends ExpNode {
    ExpNode exp1, exp2;
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        this.exp1 = exp1;
        this.exp2 = exp2;
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
        doIndent(p, indent);
        p.print("(-");
        myExp.unparse(p, 0);
        p.print(")");
    }

}

public static class NotNode extends UnaryExpNode {

    public NotNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
	    doIndent(p, indent);
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
        doIndent(p, indent);
        p.print("(");
        exp1.unparse(p, 0);
        p.print(" + ");
        exp2.unparse(p, 0);
        p.print(")");
    }
}

public static class MinusNode extends ArithmeticExpNode {

    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    doIndent(p, indent);
        p.print("(");
        exp1.unparse(p, 0);
        p.print(" - ");
        exp2.unparse(p, 0);
        p.print(")");
    }
}

public static class TimesNode extends ArithmeticExpNode {

    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("(");
        exp1.unparse(p, 0);
        p.print(" * ");
        exp2.unparse(p, 0);
        p.print(")");
    }
}

public static class DivideNode extends ArithmeticExpNode {

    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    doIndent(p, indent);
        p.print("(");
        exp1.unparse(p, 0);
        p.print(" / ");
        exp2.unparse(p, 0);
        p.print(")");
    }
}

public static class AndNode extends LogicalExpNode {

    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("(");
        exp1.unparse(p, 0);
        p.print(" && ");
        exp2.unparse(p, 0);
        p.print(")");
    }
}

public static class OrNode extends LogicalExpNode {

    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("(");
        exp1.unparse(p, 0);
        p.print(" || ");
        exp2.unparse(p, 0);
        p.print(")");
    }
}

public static class EqualsNode extends EqualityExpNode {

    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("(");
        exp1.unparse(p, 0);
        p.print(" == ");
        exp2.unparse(p, 0);
        p.print(")");
    }
}

public static class NotEqualsNode extends EqualityExpNode {

    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("(");
        exp1.unparse(p, 0);
        p.print(" != ");
        exp2.unparse(p, 0);
        p.print(")");
    }
}

public static class LessNode extends RelationalExpNode {

    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("(");
        exp1.unparse(p, 0);
        p.print(" < ");
        exp2.unparse(p, 0);
        p.print(")");
    }
}

public static class GreaterNode extends RelationalExpNode {

    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    doIndent(p, indent);
        p.print("(");
        exp1.unparse(p, 0);
        p.print(" > ");
        exp2.unparse(p, 0);
        p.print(")");
    }
}

public static class LessEqNode extends RelationalExpNode {

    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("(");
        exp1.unparse(p, 0);
        p.print(" <= ");
        exp2.unparse(p, 0);
        p.print(")");
    }
}

public static class GreaterEqNode extends RelationalExpNode {

    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("(");
        exp1.unparse(p, 0);
        p.print(" >= ");
        exp2.unparse(p, 0);
        p.print(")");
    }
}
}
