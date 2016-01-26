/* *** This file is given as part of the programming assignment. *** */


public class Parser {
   // tok is global to all these parsing methods;
   // scan just calls the scanner's scan method and saves the result in tok.
   private Token tok; // the current token
   private symbolTable scopeTables; // the symbol table

   private void scan(){
      tok = scanner.scan();
   }

   private Scan scanner;
   Parser(Scan scanner){
      this.scanner = scanner;
      scan();
      scopeTables = new symbolTable(); // initialize the symbol table
      program();
      if (tok.kind != TK.EOF)
         parse_error("junk after logical end of program");
   }

   private void program(){
     // add library and main program identifier 
     System.out.print("#include <stdio.h>\n" + "int main()\n");
      block();
   }

   private void block(){
      scopeTables.push(); // push new block onto stack
      System.out.println("{");
      declaration_list();
      statement_list();
      scopeTables.pop(); // pop off last scopetable
      System.out.println("}");
   }

   private void declaration_list()
   {
      // below checks whether tok is in first set of declaration.
      // here, that's easy since there's only one token kind in the set.
      // in other places, though, there might be more.
      // so, you might want to write a general function to handle that.
      while (is(TK.DECLARE))
      {
         declaration();
      }
   }

   private void declaration()
   {
      mustbe(TK.DECLARE);
      Token storetok = tok;
      String appendPrefix = "x_" + scopeTables.sizeOfStack();
      boolean validVariable = false;
      mustbe(TK.ID);
      
      
      validVariable = scopeTables.addVar(storetok,0);
      if(validVariable) // add first variable to current table on top of stack 
      {
        System.out.print("int " + appendPrefix + storetok.string);
      }
      
      // add all variables seperated by commas 
      while (is(TK.COMMA))
      {
         scan();
         storetok = tok; 
         mustbe(TK.ID);
         
         if(scopeTables.addVar(storetok,0)) {
           if (validVariable == false) {
             System.out.print("int " + appendPrefix + storetok.string);
             validVariable = true;
           }
           else {
             System.out.print(", " + appendPrefix + storetok.string);
           }
         } 
      }
      System.out.println(";");
   }

  // check for tok in first set of statement
  private void statement_list() {
      while((is(TK.ID) || is(TK.TILDE) || is(TK.PRINT) || is(TK.DO) || is(TK.IF)) || is(TK.FOR)){
        statement();
      } 
  }
   
    // check for type of statement
    private void statement() {
      
      if(is(TK.TILDE) || is(TK.ID)) {
        assignment();
      }
      else if (is(TK.PRINT)){
        print(); 
      }
      else if (is(TK.DO)){
        DO();
      }
      else if (is(TK.IF)){
        IF();
      }
      else if (is(TK.FOR)){
        FOR();
      }
    }
   
   private void assignment(){
      ref_id();
      mustbe(TK.ASSIGN); // '='
      System.out.print(" = ");
      expr();
      System.out.println(";");
   }
   
  // parses ref_id non-terminal and checks for variables in stack 
 private void ref_id (){
      boolean globalscope = true; // set to false when there is number 
      boolean checktilde = false; // to check if tilde exists in current tok 
      boolean foundvar = false; // if var found, set to true 
      int tablescope = 0;
      int tablesize = scopeTables.sizeOfStack(); 
      
      if (is(TK.TILDE))
      {
        scan();
        checktilde = true;
        if(is(TK.NUM))
        {
          tablescope = Integer.parseInt(tok.string);
          globalscope = false;
          scan();
        }
      }
      Token variable = tok; // store current variable
      mustbe(TK.ID);
      
      if (checktilde && !globalscope) 
             { // search for specific tablescope
              if (!scopeTables.search(variable, tablescope)) {
                  System.err.println("no such variable ~" + tablescope +
                  variable.string + " on line " + variable.lineNumber);
                  System.exit(1);
              }
        }
        // check last table for global scope
      else if (checktilde && globalscope){
          //System.out.println("In tilde and global" + variable.string);
          tablescope = tablesize - 1;
          if(!scopeTables.search(variable,  tablescope)){  
          System.err.println("no such variable ~" +
                     variable.string + " on line " + variable.lineNumber);
               System.exit(1);  
          }
        }
        // check all tables for var
      else if(!checktilde){
         for (; tablescope < tablesize; tablescope++){ 
            if (scopeTables.search(variable, tablescope)){
              foundvar = true;
              break;
            }
         }
         if (!foundvar) {
            System.err.println(variable.string + " is an undeclared variable " +
                "on line " + variable.lineNumber);
            System.exit(1);
         } 
      }
      String appendPrefix = "x_" + (tablesize - tablescope);
    /*  if (tablescope != 0)
        appendPrefix = "int x_" + tablescope;
      else 
        appendPrefix = "x_";*/
      System.out.println(appendPrefix + variable.string); // print validVariable
}
   
   // parses print non-terminal
    private void print() {
      mustbe(TK.PRINT); // '!'
      System.out.println("printf(\"%d\\n\",");
      expr();
      System.out.println(");");
    } 
    // parses do non-terminal
    private void DO() {
      mustbe(TK.DO);
      System.out.println("while");
      guarded_command();
      mustbe(TK.ENDDO);
    }

    // add iteration for part 5, for loop 
    private void FOR() {
      mustbe(TK.FOR); // $
      declaration();
      // print for loop in C, call respective functions for E
      System.out.print("for ("); 
      assignment();
      System.out.print("(");
      expr();
      System.out.print("<= 0);");
      assignmentfor();
      System.out.println(")");
      block();
      mustbe(TK.EXITFOR);
    }
    
    private void assignmentfor() {
      ref_id();
      mustbe(TK.ASSIGN); // '='
      System.out.print(" = ");
      expr();
    }
    // parses if non terminal
    private void IF() {
      mustbe(TK.IF);
      System.out.println("if");
      guarded_command();
      while (is(TK.ELSEIF)) {
         scan();
         System.out.println("else if");
         guarded_command();
      }
      if (is(TK.ELSE)) {
         scan(); 
         System.out.println("else");
         block();
      }
      mustbe(TK.ENDIF);
    } 
    // parses guarded_command non-terminal
    private void guarded_command() {
      System.out.print("(");
      expr();
      System.out.print("<= 0)");
      mustbe(TK.THEN); // ':'
      block();
    }
   
     // parses expr non-terminal
    private void expr() {
      term();
      while (is(TK.PLUS) || is(TK.MINUS)) {
        // print operators 
        System.out.println(tok.string);
        scan();
        term();
      }
    }
    // parses term non-terminal
    private void term() {
      factor();
      while(is(TK.TIMES) || is(TK.DIVIDE)) {
        // print operators 
        System.out.println(tok.string);
        scan(); 
        factor();
      }
    }
    // parses factor non-terminal
    private void factor() {
      if(is(TK.LPAREN)){
        System.out.println("(");
        scan(); 
        expr();
        mustbe(TK.RPAREN);
        System.out.println(")");
      }
      else if (is(TK.NUM)){
        // print possible number tok
        System.out.println(tok.string);
        scan(); 
      }
      else {
        ref_id(); 
      }
    }
   
    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
    if( tok.kind != tk ) {
      System.err.println( "mustbe: want " + tk + ", got " +
            tok);
      parse_error( "missing token (mustbe)" );
    }
      scan();
    }

    private void parse_error(String msg) {
  System.err.println( "can't parse: line "
          + tok.lineNumber + " " + msg );
  System.exit(1);
    }
}
