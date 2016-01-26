/* *** This file is given as part of the programming assignment. *** */

public class Parser {


    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    private void scan() {
	tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
	this.scanner = scanner;
	scan();
	program();
	if( tok.kind != TK.EOF )
	    parse_error("junk after logical end of program");
    }
  
  private void program() {
	  block();
  }

  private void block(){
	  declaration_list();
	  statement_list();
  }

    private void declaration_list() {
	// below checks whether tok is in first set of declaration.
	// here, that's easy since there's only one token kind in the set.
	// in other places, though, there might be more.
	// so, you might want to write a general function to handle that.
	while( is(TK.DECLARE) ) {
	    declaration();
  	}
  }
 
    private void declaration() {
	    mustbe(TK.DECLARE);
	    mustbe(TK.ID);
	    while( is(TK.COMMA) ) {
	      scan();
	      mustbe(TK.ID);
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
   	  else{ // cannot find tok, call error 
   	  	parse_error("this statement doesn't not exist"); 
   	  }
    }
    // parses assignment non-terminal
    private void assignment() {
      ref_id();
      mustbe(TK.ASSIGN); // '='
      expr();
    }
    // parses ref_id non-terminal
    private void ref_id (){
    	if (is(TK.TILDE))
    	{
    		scan();

    		if(is(TK.NUM))
    		{
    			scan();
    		}
    	}
    	mustbe(TK.ID);
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
      while (is(TK.PLUS) || is(TK.MINUS)) 
      {
        addop();
        term();
      }
    }
    // parses term non-terminal
    private void term() {
      factor();
      while(is(TK.TIMES) || is(TK.DIVIDE)) {
        multop();
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
    // parses addop non-terminal
    private void addop() {
      if (is(TK.PLUS)){
      	mustbe(TK.PLUS);
      }
      else{
      	mustbe(TK.MINUS); 
      }
    }
    // parses multop non-terminal
    private void multop() {
      if (is(TK.TIMES)) {
        mustbe(TK.TIMES);
      }
      else {
        mustbe(TK.DIVIDE);
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
