package ast;

import java.io.*;
import java.util.*;
import lexer.*;
import sun.security.util.DerInputStream;

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
    
    public ProgramNode(DeclListNode L) {
        declListNode = L;
    }

    public void unparse(PrintWriter p, int indent) {
        declListNode.unparse(p, indent);
    }

}

public static class DeclListNode extends ASTnode {
    private List<DeclNode> declNodeList;
    
    public DeclListNode(List<DeclNode> S) {
        declNodeList = S;
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

}

public static class FormalsListNode extends ASTnode {

    private List<FormalDeclNode> formalDeclNodeList;

    public FormalsListNode(List<FormalDeclNode> S) {
       formalDeclNodeList = S;
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

    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        declListNode = declList;
        stmtListNode = stmtList;
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

    public ExpListNode(List<ExpNode> S) {
        expNodeList = S;
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

}

// **********************************************************************
// DeclNode and its sub classes
// **********************************************************************

public static abstract class DeclNode extends ASTnode {

}

public static class VarDeclNode extends DeclNode {

    private TypeNode typeNode;
    private IdNode idNode;
    private int size;      //if this is not a stuct type,use value NOT_STRUCT

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

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        typeNode.unparse(p, 0);
        p.print(" ");
        idNode.unparse(p, 0);
    }

}

public static class StructDeclNode extends DeclNode {

    private IdNode idNode;
    private DeclListNode declListNode;

    public StructDeclNode(IdNode id, DeclListNode declList) {
       idNode = id;
       declListNode = declList;
    }

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

}

public static class IntNode extends TypeNode {

    public IntNode() {
    
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }
}

public static class BoolNode extends TypeNode {

    public BoolNode() {
    
    }
    
    public void unparse(PrintWriter p, int indent) {
       p.print("bool");
    }
}

public static class VoidNode extends TypeNode {

    public VoidNode() {
    
    }
    
    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }
}

public static class StructNode extends TypeNode {

    private IdNode idNode;

    public StructNode(IdNode id) {
        idNode = id;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("struct ");
        idNode.unparse(p, indent);
    }

    public IdNode idNode() {
        return null;
    }

    
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

public static abstract class StmtNode extends ASTnode {
   
}

public static class AssignStmtNode extends StmtNode {

    private AssignNode assignNode;

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
  
    private int lineNum;
    private int charNum;
    private int intVal;

    public IntLitNode(int lineNum, int charNum, int intVal) {
        this.lineNum = lineNum;
        this.charNum = charNum;
        this.intVal = intVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(this.intVal);

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
}

public static class IdNode extends ExpNode {

    private int lineNum;
    private int charNum;
    private String strVal;

    public IdNode(int lineNum, int charNum, String strVal) {
        this.lineNum = lineNum;
        this.charNum = charNum;
        this.strVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(this.strVal);
    }

}

public static class DotAccessExpNode extends ExpNode {
    
    private ExpNode expNode;
    private IdNode idNode;

    public DotAccessExpNode(ExpNode lhs, IdNode id) {
        this.expNode = lhs;
        this.idNode = id;
    }

    public void unparse(PrintWriter p, int indent) {
        expNode.unparse(p, indent);
        p.print(".");
        idNode.unparse(p, 0);
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

}

public static class CallExpNode extends ExpNode {

    private IdNode idNode;
    private ExpListNode expListNode;

    public CallExpNode(IdNode name, ExpListNode elist) {
        idNode = name;
        expListNode = elist;
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
    
    protected ExpNode exp1;
    protected ExpNode exp2;

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
        p.print("(-");
        this.myExp.unparse(p, indent);
        p.print(")");
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
        p.print("(");
        this.exp1.unparse(p, indent);
        p.print(" + ");
        this.exp2.unparse(p, indent);
        p.print(")");
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
}
}
