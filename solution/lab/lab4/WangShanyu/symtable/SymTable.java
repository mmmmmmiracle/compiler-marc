package symtable;

import java.util.*;

/**
 * A class for Symbol Table
 */
public class SymTable {

    /**
     * Create a Symbol Table with one empty scope
     */
    LinkedList<HashMap<String, SymInfo> > list;
    public SymTable() {
        list = new LinkedList<HashMap<String, SymInfo> >();
        list.addLast(new HashMap<String, SymInfo>());
    }

    /**
     * Add a declaration (i.e. a pair [name,sym]) in the inner scope
     */
    public void addDecl(String name, SymInfo sym) throws DuplicateSymException, EmptySymTableException {
        if(list.isEmpty())
            throw new EmptySymTableException();
        if(name == null || sym == null)
            throw new NullPointerException();
        if(list.getFirst().containsKey(name))
            throw new DuplicateSymException();
        list.getFirst().put(name, sym);
    }

    /**
     * Add a new inner scope
     */
    public void addScope() {
       list.addFirst(new HashMap<String, SymInfo>());
    }

    /**
     * Lookup for 'name' in the inner scope
     */
    public SymInfo lookupLocal(String name) throws EmptySymTableException {
        if(list.isEmpty())
            throw new EmptySymTableException();
        if(list.getFirst().containsKey(name))
            return list.getFirst().get(name);
        else
            return null;
    }

    /**
     * Lookup for 'name' sequentially in all scopes from inner to outer
     */
    public SymInfo lookupGlobal(String name)  throws EmptySymTableException {
        if(list.isEmpty())
            throw new EmptySymTableException();
        for(Iterator<HashMap<String, SymInfo> > iter = list.iterator(); iter.hasNext();){
            HashMap<String, SymInfo> map = iter.next();
            if(map.containsKey(name))
                return map.get(name);
        }
        return null;
    }

    /**
     * Remove the inner scope
     */
    public void removeScope() throws EmptySymTableException {
        if(list.isEmpty())
            throw new EmptySymTableException();
        list.pop();
    }

    /**
     * Print the Symbol Table on System.out
     */
    public void print() {
        System.out.print("\nSym Table\n");
        for(Iterator<HashMap<String, SymInfo> > iter = list.iterator(); iter.hasNext();){
            System.out.println(iter.next());
        }
        System.out.println();
    }
}
