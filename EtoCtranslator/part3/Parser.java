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
      scopeTables = new symbolTable(); // initialize the symbol table
      scan();
      program();
      if (tok.kind != TK.EOF)
         parse_error("junk after logical end of program");
   }

   private void program(){
      block();
   }

   private void block(){
      scopeTables.push(); // push new block onto stack 
      declaration_list();
      statement_list();
      scopeTables.pop(); 
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
      mustbe(TK.ID);
      scopeTables.addVar(storetok,0); // add first variable to current table on top of stack 

      // add all variables seperated by commas 
      while (is(TK.COMMA))
      {
         scan();
         storetok = tok; 
         mustbe(TK.ID);
         scopeTables.addVar(storetok,0);
      }
   }

  // check for tok in first set of statement
  private void statement_list() {
      while(is(TK.ID) || is(TK.TILDE) || is(TK.PRINT) || is(TK.DO) || is(TK.IF)){
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
    }
   
   private void assignment(){
      ref_id();
      mustbe(TK.ASSIGN); // '='
      expr();
   }
   
  // parses ref_id non-terminal and checks for variables in stack 
 private void ref_id (){
      
      boolean globalscope = true; // set to false when there is number 
      boolean checktilde = false; // check if tilde exists in current tok
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
      
      if (checktilde && globalscope == false) {
              foundvar = true; 
              if (!scopeTables.search(variable, tablescope)) {
                 System.err.println("no such variable ~" + tablescope +
                     variable.string + " on line " + variable.lineNumber);
                   System.exit(1);
              }
        }
        // check last table for global var 
      else if (checktilde && globalscope){
          foundvar = true; 
          if(!scopeTables.search(variable,  tablesize - 1)){  
          System.err.println("no such variable ~" +
                     variable.string + " on line " + variable.lineNumber);
               System.exit(1);  
          }
        }
      else if(checktilde == false){
          // check all tables for var
         for (int i = tablescope; i < tablesize; i++){ 
            if (scopeTables.search(variable, i)){
               foundvar = true; 
               i = tablesize; // if var found, exit loop 
            }
         }
         // in case var not found 
          if (foundvar == false) {
            System.err.println(variable.string + " is an undeclared variable " +
                "on line " + variable.lineNumber);
            System.exit(1);
         }
    
      }
     

}
   
   // parses print non-terminal
    private void print() {
      mustbe(TK.PRINT); // '!'
      expr();
    }
    // parses do non-terminal
    private void DO() {
      mustbe(TK.DO);
      guarded_command();
      mustbe(TK.ENDDO);
    }
    // parses if non terminal
    private void IF() {
      mustbe(TK.IF);
      guarded_command(); 
      while (is(TK.ELSEIF)) {
         scan();
         guarded_command();
      }
      if (is(TK.ELSE)) {
         scan(); 
         block();
      }
      mustbe(TK.ENDIF);
    } 
    // parses guarded_command non-terminal
    private void guarded_command() {
      expr();
      mustbe(TK.THEN); // ':'
      block();
    }
   
     // parses expr non-terminal
    private void expr() {
      term();
      while (is(TK.PLUS) || is(TK.MINUS)) {
        scan();
        term();
      }
    }
    // parses term non-terminal
    private void term() {
      factor();
      while(is(TK.TIMES) || is(TK.DIVIDE)) {
        scan(); 
        factor();
      }
    }
    // parses factor non-terminal
    private void factor() {
      if(is(TK.LPAREN)){
        scan(); 
        expr();
        mustbe(TK.RPAREN);
      }
      else if (is(TK.NUM)){
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
