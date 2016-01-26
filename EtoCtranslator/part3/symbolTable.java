import java.util.*;

public class symbolTable {

  private ArrayList<ArrayList<String>> symbolTables;
  
  symbolTable() {
    // create stack of lists 
    symbolTables = new ArrayList<ArrayList<String>>(); 
  }
  
  public int sizeOfStack () {
    return symbolTables.size();
  }
  public void push() {
    // add new table to stack 
    symbolTables.add(0, new ArrayList<String>());
  }
  
  public void pop() {
    symbolTables.remove(0); 
  }

  // search for variable in stack of lists 
  public boolean search(Token variable, int scope) {
   if(scope < 0){ // if scope is not defined 
        return false; 
    }
    else if (symbolTables.size() < (scope + 1)) // scope is larger than stack size 
    {
      return false; 
    }
    // retrieve list via scope and search for variable 
    ArrayList<String> currentTable = symbolTables.get(scope);
    for (int j = 0; j < currentTable.size(); j++) {
          if (currentTable.get(j).equals(variable.string)) 
              return true;
    } 
    return false;
  }
   // add variable to current scope, if not it has been redeclared 
  public void addVar(Token variable, int tablenum)
  {
      if (!search(variable, tablenum)){
         symbolTables.get(tablenum).add(variable.string);
      } else {
        System.err.println("redeclaration of variable " + variable.string);
      }
  }
  
  
}
