// $ANTLR 3.0.1 C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g 2009-03-20 17:22:56

package jadex.rules.parser.conditions.javagrammar;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class JavaJadexParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENTIFIER", "CharacterLiteral", "StringLiteral", "BooleanLiteral", "FloatingPointLiteral", "HexLiteral", "OctalLiteral", "DecimalLiteral", "ConstraintOperator", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "COMMENT", "LINE_COMMENT", "'&'", "'='", "'!'", "'<'", "'>'", "'.'", "'('", "')'", "','", "'null'", "'+'", "'-'"
    };
    public static final int HexLiteral=9;
    public static final int LINE_COMMENT=24;
    public static final int FloatTypeSuffix=16;
    public static final int OctalLiteral=10;
    public static final int IntegerTypeSuffix=14;
    public static final int CharacterLiteral=5;
    public static final int Exponent=15;
    public static final int EOF=-1;
    public static final int DecimalLiteral=11;
    public static final int HexDigit=13;
    public static final int StringLiteral=6;
    public static final int WS=22;
    public static final int IDENTIFIER=4;
    public static final int UnicodeEscape=18;
    public static final int FloatingPointLiteral=8;
    public static final int ConstraintOperator=12;
    public static final int JavaIDDigit=21;
    public static final int COMMENT=23;
    public static final int Letter=20;
    public static final int EscapeSequence=17;
    public static final int OctalEscape=19;
    public static final int BooleanLiteral=7;

        public JavaJadexParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g"; }

    
    	/** The stack of elements generated during parsing. */
    	protected List	stack	= new ArrayList();
    	
    	/**
    	 *  Get the elements from the stack.
    	 */
    	public List	getStack()
    	{
    		return stack;
    	}



    // $ANTLR start rhs
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:31:1: rhs : expression EOF ;
    public final void rhs() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:35:2: ( expression EOF )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:35:4: expression EOF
            {
            pushFollow(FOLLOW_expression_in_rhs40);
            expression();
            _fsp--;

            match(input,EOF,FOLLOW_EOF_in_rhs42); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end rhs


    // $ANTLR start expression
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:38:1: expression : logicalAndExpression ;
    public final void expression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:43:2: ( logicalAndExpression )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:43:4: logicalAndExpression
            {
            pushFollow(FOLLOW_logicalAndExpression_in_expression55);
            logicalAndExpression();
            _fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end expression


    // $ANTLR start logicalAndExpression
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:47:1: logicalAndExpression : equalityExpression ( '&' '&' equalityExpression )* ;
    public final void logicalAndExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:51:2: ( equalityExpression ( '&' '&' equalityExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:51:4: equalityExpression ( '&' '&' equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_logicalAndExpression69);
            equalityExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:52:9: ( '&' '&' equalityExpression )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==25) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:53:10: '&' '&' equalityExpression
            	    {
            	    match(input,25,FOLLOW_25_in_logicalAndExpression90); 
            	    match(input,25,FOLLOW_25_in_logicalAndExpression91); 
            	    pushFollow(FOLLOW_equalityExpression_in_logicalAndExpression93);
            	    equalityExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end logicalAndExpression


    // $ANTLR start equalityExpression
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:57:1: equalityExpression : relationalExpression ( ( '=' '=' | '!' '=' ) relationalExpression )? ;
    public final void equalityExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:61:2: ( relationalExpression ( ( '=' '=' | '!' '=' ) relationalExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:61:4: relationalExpression ( ( '=' '=' | '!' '=' ) relationalExpression )?
            {
            pushFollow(FOLLOW_relationalExpression_in_equalityExpression118);
            relationalExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:62:9: ( ( '=' '=' | '!' '=' ) relationalExpression )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( ((LA3_0>=26 && LA3_0<=27)) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:63:3: ( '=' '=' | '!' '=' ) relationalExpression
                    {
                    
                    			String	operator	= null;
                    		
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:66:10: ( '=' '=' | '!' '=' )
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==26) ) {
                        alt2=1;
                    }
                    else if ( (LA2_0==27) ) {
                        alt2=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("66:10: ( '=' '=' | '!' '=' )", 2, 0, input);

                        throw nvae;
                    }
                    switch (alt2) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:66:11: '=' '='
                            {
                            match(input,26,FOLLOW_26_in_equalityExpression144); 
                            match(input,26,FOLLOW_26_in_equalityExpression145); 
                            operator="==";

                            }
                            break;
                        case 2 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:67:11: '!' '='
                            {
                            match(input,27,FOLLOW_27_in_equalityExpression159); 
                            match(input,26,FOLLOW_26_in_equalityExpression160); 
                            operator="!=";

                            }
                            break;

                    }

                    pushFollow(FOLLOW_relationalExpression_in_equalityExpression175);
                    relationalExpression();
                    _fsp--;

                    
                    	        	// Pop values from stack and add constraint.
                    	        	UnaryExpression	right	= (UnaryExpression)stack.remove(stack.size()-1);
                    	        	UnaryExpression	left	= (UnaryExpression)stack.remove(stack.size()-1);
                    	        	stack.add(new Constraint(left, right, operator));
                    	        

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end equalityExpression


    // $ANTLR start relationalExpression
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:78:1: relationalExpression : unaryExpression ( ( '<' | '<' '=' | '>' | '>' '=' ) unaryExpression )? ;
    public final void relationalExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:82:2: ( unaryExpression ( ( '<' | '<' '=' | '>' | '>' '=' ) unaryExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:82:4: unaryExpression ( ( '<' | '<' '=' | '>' | '>' '=' ) unaryExpression )?
            {
            pushFollow(FOLLOW_unaryExpression_in_relationalExpression203);
            unaryExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:83:9: ( ( '<' | '<' '=' | '>' | '>' '=' ) unaryExpression )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( ((LA5_0>=28 && LA5_0<=29)) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:84:3: ( '<' | '<' '=' | '>' | '>' '=' ) unaryExpression
                    {
                    
                    			String	operator	= null;
                    		
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:87:10: ( '<' | '<' '=' | '>' | '>' '=' )
                    int alt4=4;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==28) ) {
                        int LA4_1 = input.LA(2);

                        if ( (LA4_1==26) ) {
                            alt4=2;
                        }
                        else if ( ((LA4_1>=IDENTIFIER && LA4_1<=DecimalLiteral)||(LA4_1>=34 && LA4_1<=36)) ) {
                            alt4=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("87:10: ( '<' | '<' '=' | '>' | '>' '=' )", 4, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA4_0==29) ) {
                        int LA4_2 = input.LA(2);

                        if ( (LA4_2==26) ) {
                            alt4=4;
                        }
                        else if ( ((LA4_2>=IDENTIFIER && LA4_2<=DecimalLiteral)||(LA4_2>=34 && LA4_2<=36)) ) {
                            alt4=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("87:10: ( '<' | '<' '=' | '>' | '>' '=' )", 4, 2, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("87:10: ( '<' | '<' '=' | '>' | '>' '=' )", 4, 0, input);

                        throw nvae;
                    }
                    switch (alt4) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:87:11: '<'
                            {
                            match(input,28,FOLLOW_28_in_relationalExpression229); 
                            operator="<";

                            }
                            break;
                        case 2 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:88:11: '<' '='
                            {
                            match(input,28,FOLLOW_28_in_relationalExpression243); 
                            match(input,26,FOLLOW_26_in_relationalExpression244); 
                            operator="<=";

                            }
                            break;
                        case 3 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:89:11: '>'
                            {
                            match(input,29,FOLLOW_29_in_relationalExpression258); 
                            operator=">";

                            }
                            break;
                        case 4 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:90:11: '>' '='
                            {
                            match(input,29,FOLLOW_29_in_relationalExpression272); 
                            match(input,26,FOLLOW_26_in_relationalExpression273); 
                            operator=">=";

                            }
                            break;

                    }

                    pushFollow(FOLLOW_unaryExpression_in_relationalExpression288);
                    unaryExpression();
                    _fsp--;

                    
                    	        	// Pop values from stack and add constraint.
                    	        	UnaryExpression	right	= (UnaryExpression)stack.remove(stack.size()-1);
                    	        	UnaryExpression	left	= (UnaryExpression)stack.remove(stack.size()-1);
                    	        	stack.add(new Constraint(left, right, operator));
                    	        

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end relationalExpression


    // $ANTLR start unaryExpression
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:101:1: unaryExpression : primary ( suffix )* ;
    public final void unaryExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:105:2: ( primary ( suffix )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:106:2: primary ( suffix )*
            {
            pushFollow(FOLLOW_primary_in_unaryExpression325);
            primary();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:107:2: ( suffix )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==30) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:107:3: suffix
            	    {
            	    pushFollow(FOLLOW_suffix_in_unaryExpression329);
            	    suffix();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            
            		List	suffs	= null;
            		while(stack.get(stack.size()-1) instanceof Suffix)
            		{
            			if(suffs==null)
            				suffs	= new ArrayList();
            			suffs.add(0, stack.remove(stack.size()-1));
            		}
            		Primary	prim	= (Primary)stack.remove(stack.size()-1);
            		stack.add(new UnaryExpression(prim, suffs==null ? null
            			: (Suffix[])suffs.toArray(new Suffix[suffs.size()])));
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end unaryExpression


    // $ANTLR start primary
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:123:1: primary : ( literal | variable );
    public final void primary() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:127:2: ( literal | variable )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( ((LA7_0>=CharacterLiteral && LA7_0<=DecimalLiteral)||(LA7_0>=34 && LA7_0<=36)) ) {
                alt7=1;
            }
            else if ( (LA7_0==IDENTIFIER) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("123:1: primary : ( literal | variable );", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:128:6: literal
                    {
                    pushFollow(FOLLOW_literal_in_primary351);
                    literal();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:129:4: variable
                    {
                    pushFollow(FOLLOW_variable_in_primary356);
                    variable();
                    _fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end primary


    // $ANTLR start suffix
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:132:1: suffix : ( fieldAccess | methodAccess );
    public final void suffix() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:136:2: ( fieldAccess | methodAccess )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==30) ) {
                int LA8_1 = input.LA(2);

                if ( (LA8_1==IDENTIFIER) ) {
                    int LA8_2 = input.LA(3);

                    if ( (LA8_2==31) ) {
                        alt8=2;
                    }
                    else if ( (LA8_2==EOF||(LA8_2>=25 && LA8_2<=30)||(LA8_2>=32 && LA8_2<=33)) ) {
                        alt8=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("132:1: suffix : ( fieldAccess | methodAccess );", 8, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("132:1: suffix : ( fieldAccess | methodAccess );", 8, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("132:1: suffix : ( fieldAccess | methodAccess );", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:136:4: fieldAccess
                    {
                    pushFollow(FOLLOW_fieldAccess_in_suffix369);
                    fieldAccess();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:137:4: methodAccess
                    {
                    pushFollow(FOLLOW_methodAccess_in_suffix374);
                    methodAccess();
                    _fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end suffix


    // $ANTLR start fieldAccess
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:140:1: fieldAccess : '.' tmp= IDENTIFIER ;
    public final void fieldAccess() throws RecognitionException {
        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:144:2: ( '.' tmp= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:144:4: '.' tmp= IDENTIFIER
            {
            match(input,30,FOLLOW_30_in_fieldAccess387); 
            tmp=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_fieldAccess393); 
            stack.add(new FieldAccess(tmp.getText()));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end fieldAccess


    // $ANTLR start methodAccess
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:147:1: methodAccess : ( '.' tmp= IDENTIFIER '(' ')' | '.' tmp= IDENTIFIER '(' unaryExpression ( ',' unaryExpression )* ')' );
    public final void methodAccess() throws RecognitionException {
        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:151:2: ( '.' tmp= IDENTIFIER '(' ')' | '.' tmp= IDENTIFIER '(' unaryExpression ( ',' unaryExpression )* ')' )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==30) ) {
                int LA10_1 = input.LA(2);

                if ( (LA10_1==IDENTIFIER) ) {
                    int LA10_2 = input.LA(3);

                    if ( (LA10_2==31) ) {
                        int LA10_3 = input.LA(4);

                        if ( (LA10_3==32) ) {
                            alt10=1;
                        }
                        else if ( ((LA10_3>=IDENTIFIER && LA10_3<=DecimalLiteral)||(LA10_3>=34 && LA10_3<=36)) ) {
                            alt10=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("147:1: methodAccess : ( '.' tmp= IDENTIFIER '(' ')' | '.' tmp= IDENTIFIER '(' unaryExpression ( ',' unaryExpression )* ')' );", 10, 3, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("147:1: methodAccess : ( '.' tmp= IDENTIFIER '(' ')' | '.' tmp= IDENTIFIER '(' unaryExpression ( ',' unaryExpression )* ')' );", 10, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("147:1: methodAccess : ( '.' tmp= IDENTIFIER '(' ')' | '.' tmp= IDENTIFIER '(' unaryExpression ( ',' unaryExpression )* ')' );", 10, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("147:1: methodAccess : ( '.' tmp= IDENTIFIER '(' ')' | '.' tmp= IDENTIFIER '(' unaryExpression ( ',' unaryExpression )* ')' );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:151:4: '.' tmp= IDENTIFIER '(' ')'
                    {
                    match(input,30,FOLLOW_30_in_methodAccess408); 
                    tmp=(Token)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodAccess414); 
                    match(input,31,FOLLOW_31_in_methodAccess416); 
                    match(input,32,FOLLOW_32_in_methodAccess418); 
                    stack.add(new MethodAccess(tmp.getText(), null));

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:152:4: '.' tmp= IDENTIFIER '(' unaryExpression ( ',' unaryExpression )* ')'
                    {
                    match(input,30,FOLLOW_30_in_methodAccess425); 
                    tmp=(Token)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodAccess431); 
                    match(input,31,FOLLOW_31_in_methodAccess433); 
                    pushFollow(FOLLOW_unaryExpression_in_methodAccess435);
                    unaryExpression();
                    _fsp--;

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:153:2: ( ',' unaryExpression )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==33) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:153:3: ',' unaryExpression
                    	    {
                    	    match(input,33,FOLLOW_33_in_methodAccess440); 
                    	    pushFollow(FOLLOW_unaryExpression_in_methodAccess442);
                    	    unaryExpression();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);

                    match(input,32,FOLLOW_32_in_methodAccess449); 
                    
                    		List	parexs	= null;
                    		while(stack.get(stack.size()-1) instanceof UnaryExpression)
                    		{
                    			if(parexs==null)
                    				parexs	= new ArrayList();
                    			parexs.add(0, stack.remove(stack.size()-1));
                    		}
                    		stack.add(new MethodAccess(tmp.getText(), (UnaryExpression[])parexs.toArray(new UnaryExpression[parexs.size()])));
                    	

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end methodAccess


    // $ANTLR start variable
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:167:1: variable : tmp= IDENTIFIER ;
    public final void variable() throws RecognitionException {
        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:171:2: (tmp= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:171:4: tmp= IDENTIFIER
            {
            tmp=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variable469); 
            stack.add(new Variable(tmp.getText()));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end variable


    // $ANTLR start literal
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:174:1: literal : (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );
    public final void literal() throws RecognitionException {
        Token CharacterLiteral1=null;
        Token StringLiteral2=null;
        Token BooleanLiteral3=null;
        Object lit = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:175:2: (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' )
            int alt11=6;
            switch ( input.LA(1) ) {
            case 35:
            case 36:
                {
                int LA11_1 = input.LA(2);

                if ( (LA11_1==FloatingPointLiteral) ) {
                    alt11=1;
                }
                else if ( ((LA11_1>=HexLiteral && LA11_1<=DecimalLiteral)) ) {
                    alt11=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("174:1: literal : (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );", 11, 1, input);

                    throw nvae;
                }
                }
                break;
            case FloatingPointLiteral:
                {
                alt11=1;
                }
                break;
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
                {
                alt11=2;
                }
                break;
            case CharacterLiteral:
                {
                alt11=3;
                }
                break;
            case StringLiteral:
                {
                alt11=4;
                }
                break;
            case BooleanLiteral:
                {
                alt11=5;
                }
                break;
            case 34:
                {
                alt11=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("174:1: literal : (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:175:4: lit= floatingPointLiteral
                    {
                    pushFollow(FOLLOW_floatingPointLiteral_in_literal484);
                    floatingPointLiteral();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:176:4: lit= integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal491);
                    lit=integerLiteral();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:177:4: CharacterLiteral
                    {
                    CharacterLiteral1=(Token)input.LT(1);
                    match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal496); 
                    stack.add(new Literal(new Character(CharacterLiteral1.getText().charAt(0))));

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:178:4: StringLiteral
                    {
                    StringLiteral2=(Token)input.LT(1);
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_literal503); 
                    stack.add(new Literal(StringLiteral2.getText().substring(1, StringLiteral2.getText().length()-1)));

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:179:4: BooleanLiteral
                    {
                    BooleanLiteral3=(Token)input.LT(1);
                    match(input,BooleanLiteral,FOLLOW_BooleanLiteral_in_literal510); 
                    stack.add(new Literal(BooleanLiteral3.getText().equals("true")? Boolean.TRUE: Boolean.FALSE));

                    }
                    break;
                case 6 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:180:4: 'null'
                    {
                    match(input,34,FOLLOW_34_in_literal517); 
                    stack.add(new Literal(null));

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end literal


    // $ANTLR start floatingPointLiteral
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:183:1: floatingPointLiteral : (sign= ( '+' | '-' ) )? FloatingPointLiteral ;
    public final void floatingPointLiteral() throws RecognitionException {
        Token sign=null;
        Token FloatingPointLiteral4=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:184:2: ( (sign= ( '+' | '-' ) )? FloatingPointLiteral )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:184:4: (sign= ( '+' | '-' ) )? FloatingPointLiteral
            {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:184:8: (sign= ( '+' | '-' ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( ((LA12_0>=35 && LA12_0<=36)) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:184:8: sign= ( '+' | '-' )
                    {
                    sign=(Token)input.LT(1);
                    if ( (input.LA(1)>=35 && input.LA(1)<=36) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_floatingPointLiteral532);    throw mse;
                    }


                    }
                    break;

            }

            FloatingPointLiteral4=(Token)input.LT(1);
            match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_floatingPointLiteral539); 
            stack.add(new Literal(sign!=null && "-".equals(sign.getText())? new Double("-"+FloatingPointLiteral4.getText()): new Double(FloatingPointLiteral4.getText())));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end floatingPointLiteral


    // $ANTLR start integerLiteral
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:187:1: integerLiteral returns [Object val] : (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral ) ;
    public final Object integerLiteral() throws RecognitionException {
        Object val = null;

        Token sign=null;
        Token HexLiteral5=null;
        Token OctalLiteral6=null;
        Token DecimalLiteral7=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:189:2: ( (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral ) )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:189:4: (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral )
            {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:189:8: (sign= ( '+' | '-' ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( ((LA13_0>=35 && LA13_0<=36)) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:189:8: sign= ( '+' | '-' )
                    {
                    sign=(Token)input.LT(1);
                    if ( (input.LA(1)>=35 && input.LA(1)<=36) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_integerLiteral560);    throw mse;
                    }


                    }
                    break;

            }

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:189:20: ( HexLiteral | OctalLiteral | DecimalLiteral )
            int alt14=3;
            switch ( input.LA(1) ) {
            case HexLiteral:
                {
                alt14=1;
                }
                break;
            case OctalLiteral:
                {
                alt14=2;
                }
                break;
            case DecimalLiteral:
                {
                alt14=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("189:20: ( HexLiteral | OctalLiteral | DecimalLiteral )", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:189:21: HexLiteral
                    {
                    HexLiteral5=(Token)input.LT(1);
                    match(input,HexLiteral,FOLLOW_HexLiteral_in_integerLiteral568); 
                    stack.add(new Literal(sign!=null && "-".equals(sign.getText())? new Integer("-"+HexLiteral5.getText()): new Integer(HexLiteral5.getText())));

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:190:4: OctalLiteral
                    {
                    OctalLiteral6=(Token)input.LT(1);
                    match(input,OctalLiteral,FOLLOW_OctalLiteral_in_integerLiteral575); 
                    stack.add(new Literal((sign!=null && "-".equals(sign.getText())? new Integer("-"+OctalLiteral6.getText()): new Integer(OctalLiteral6.getText()))));

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:191:4: DecimalLiteral
                    {
                    DecimalLiteral7=(Token)input.LT(1);
                    match(input,DecimalLiteral,FOLLOW_DecimalLiteral_in_integerLiteral582); 
                    stack.add(new Literal(sign!=null && "-".equals(sign.getText())? new Integer("-"+DecimalLiteral7.getText()): new Integer(DecimalLiteral7.getText())));

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return val;
    }
    // $ANTLR end integerLiteral


 

    public static final BitSet FOLLOW_expression_in_rhs40 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_rhs42 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_logicalAndExpression_in_expression55 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_logicalAndExpression69 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_logicalAndExpression90 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_logicalAndExpression91 = new BitSet(new long[]{0x0000001C00000FF0L});
    public static final BitSet FOLLOW_equalityExpression_in_logicalAndExpression93 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression118 = new BitSet(new long[]{0x000000000C000002L});
    public static final BitSet FOLLOW_26_in_equalityExpression144 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_equalityExpression145 = new BitSet(new long[]{0x0000001C00000FF0L});
    public static final BitSet FOLLOW_27_in_equalityExpression159 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_equalityExpression160 = new BitSet(new long[]{0x0000001C00000FF0L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_relationalExpression203 = new BitSet(new long[]{0x0000000030000002L});
    public static final BitSet FOLLOW_28_in_relationalExpression229 = new BitSet(new long[]{0x0000001C00000FF0L});
    public static final BitSet FOLLOW_28_in_relationalExpression243 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_relationalExpression244 = new BitSet(new long[]{0x0000001C00000FF0L});
    public static final BitSet FOLLOW_29_in_relationalExpression258 = new BitSet(new long[]{0x0000001C00000FF0L});
    public static final BitSet FOLLOW_29_in_relationalExpression272 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_relationalExpression273 = new BitSet(new long[]{0x0000001C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_relationalExpression288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpression325 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_suffix_in_unaryExpression329 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_literal_in_primary351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_primary356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldAccess_in_suffix369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodAccess_in_suffix374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_fieldAccess387 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_fieldAccess393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_methodAccess408 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodAccess414 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_methodAccess416 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_methodAccess418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_methodAccess425 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodAccess431 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_methodAccess433 = new BitSet(new long[]{0x0000001C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_methodAccess435 = new BitSet(new long[]{0x0000000300000000L});
    public static final BitSet FOLLOW_33_in_methodAccess440 = new BitSet(new long[]{0x0000001C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_methodAccess442 = new BitSet(new long[]{0x0000000300000000L});
    public static final BitSet FOLLOW_32_in_methodAccess449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_floatingPointLiteral_in_literal484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BooleanLiteral_in_literal510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_literal517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_floatingPointLiteral532 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_floatingPointLiteral539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral560 = new BitSet(new long[]{0x0000000000000E00L});
    public static final BitSet FOLLOW_HexLiteral_in_integerLiteral568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OctalLiteral_in_integerLiteral575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DecimalLiteral_in_integerLiteral582 = new BitSet(new long[]{0x0000000000000002L});

}