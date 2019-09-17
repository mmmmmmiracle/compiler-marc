package symtable;

import java.util.*;

/**
 * A class for Symbol Table
 */
public class SymTable {

    private List<HashMap<String,SymInfo>> list;

    /**
     * Create a Symbol Table with one empty scope
     */
    public SymTable() {
        list = new LinkedList<HashMap<String,SymInfo>>();
        list.add(new HashMap<String,SymInfo>());
    }

    /**
     * Add a declaration (i.e. a pair [name,sym]) in the inner scope
     */
    public void addDecl(String name, SymInfo sym) throws DuplicateSymException, EmptySymTableException {
        if(list.isEmpty()){
            throw new EmptySymTableException();
        }
        if(name == null || sym == null){
            throw new NullPointerException();
        }
        HashMap<String, SymInfo> symTable = list.get(0);
        if (symTable.containsKey(name)){
            throw new DuplicateSymException();
        }
        symTable.put(name, sym);
    }

    /**
     * Add a new inner scope
     */
    public void addScope() {
        list.add(0,new HashMap<String,SymInfo>());
    }

    /**
     * Lookup for 'name' in the inner scope
     */
    public SymInfo lookupLocal(String name) throws EmptySymTableException {
        if(list.isEmpty()){
            throw new EmptySymTableException();
        }
        HashMap<String,SymInfo> symTable = list.get(0);
        return symTable.get(name);

    }

    /**
     * Lookup for 'name' sequentially in all scopes from inner to outer
     */
    public SymInfo lookupGlobal(String name)  throws EmptySymTableException {
        if(list.isEmpty()){
            throw new EmptySymTableException();
        }
        SymInfo symInfo = null;
        for(HashMap<String,SymInfo> symTable : list){
            symInfo = symTable.get(name);
            if(symInfo != null){
                break;
            }
        }
        return symInfo;
    }

    /**
     * Remove the inner scope
     */
    public void removeScope() throws EmptySymTableException {
        if(list.isEmpty()){
            throw new EmptySymTableException();
        }
        list.remove(0);
    }

    /**
     * Print the Symbol Table on System.out
     */
    public void print() {
        System.out.println("Sym Table");
        for(HashMap<String,SymInfo> symTable : list){
            System.out.println(symTable.toString());
        }
        System.out.println();
    }
}
