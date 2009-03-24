// $ANTLR 3.0.1 C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g 2009-03-24 15:36:06

package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class JavaJadexParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENTIFIER", "CharacterLiteral", "StringLiteral", "BooleanLiteral", "FloatingPointLiteral", "HexLiteral", "OctalLiteral", "DecimalLiteral", "ConstraintOperator", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "COMMENT", "LINE_COMMENT", "'&'", "'='", "'!'", "'<'", "'>'", "'+'", "'-'", "'('", "')'", "'.'", "','", "'null'"
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
    	
    	/** The parser helper provides additional information (e.g. local variables). */
    	protected IParserHelper	helper;
    	
    	/**
    	 *  Get the elements from the stack.
    	 */
    	public List	getStack()
    	{
    		return stack;
    	}
    
    	/**
    	 *  Set the predefined conditions.
    	 */
    	public void	setParserHelper(IParserHelper helper)
    	{
    		this.helper	= helper;
    	}



    // $ANTLR start lhs
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:44:1: lhs : expression EOF ;
    public final void lhs() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:48:2: ( expression EOF )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:48:4: expression EOF
            {
            pushFollow(FOLLOW_expression_in_lhs40);
            expression();
            _fsp--;

            match(input,EOF,FOLLOW_EOF_in_lhs42); 

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
    // $ANTLR end lhs


    // $ANTLR start expression
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:51:1: expression : logicalAndExpression ;
    public final void expression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:56:2: ( logicalAndExpression )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:56:4: logicalAndExpression
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:60:1: logicalAndExpression : equalityExpression ( '&' '&' equalityExpression )* ;
    public final void logicalAndExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:64:2: ( equalityExpression ( '&' '&' equalityExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:64:4: equalityExpression ( '&' '&' equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_logicalAndExpression69);
            equalityExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:65:9: ( '&' '&' equalityExpression )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==25) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:66:10: '&' '&' equalityExpression
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:70:1: equalityExpression : relationalExpression ( ( '=' '=' | '!' '=' ) relationalExpression )? ;
    public final void equalityExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:74:2: ( relationalExpression ( ( '=' '=' | '!' '=' ) relationalExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:74:4: relationalExpression ( ( '=' '=' | '!' '=' ) relationalExpression )?
            {
            pushFollow(FOLLOW_relationalExpression_in_equalityExpression118);
            relationalExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:75:9: ( ( '=' '=' | '!' '=' ) relationalExpression )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( ((LA3_0>=26 && LA3_0<=27)) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:76:3: ( '=' '=' | '!' '=' ) relationalExpression
                    {
                    
                    			String	operator	= null;
                    		
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:79:10: ( '=' '=' | '!' '=' )
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
                            new NoViableAltException("79:10: ( '=' '=' | '!' '=' )", 2, 0, input);

                        throw nvae;
                    }
                    switch (alt2) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:79:11: '=' '='
                            {
                            match(input,26,FOLLOW_26_in_equalityExpression144); 
                            match(input,26,FOLLOW_26_in_equalityExpression145); 
                            operator="==";

                            }
                            break;
                        case 2 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:80:11: '!' '='
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:91:1: relationalExpression : additiveExpression ( ( '<' | '<' '=' | '>' | '>' '=' ) additiveExpression )? ;
    public final void relationalExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:95:2: ( additiveExpression ( ( '<' | '<' '=' | '>' | '>' '=' ) additiveExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:95:4: additiveExpression ( ( '<' | '<' '=' | '>' | '>' '=' ) additiveExpression )?
            {
            pushFollow(FOLLOW_additiveExpression_in_relationalExpression203);
            additiveExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:96:9: ( ( '<' | '<' '=' | '>' | '>' '=' ) additiveExpression )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( ((LA5_0>=28 && LA5_0<=29)) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:97:3: ( '<' | '<' '=' | '>' | '>' '=' ) additiveExpression
                    {
                    
                    			String	operator	= null;
                    		
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:100:10: ( '<' | '<' '=' | '>' | '>' '=' )
                    int alt4=4;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==28) ) {
                        int LA4_1 = input.LA(2);

                        if ( (LA4_1==26) ) {
                            alt4=2;
                        }
                        else if ( ((LA4_1>=IDENTIFIER && LA4_1<=DecimalLiteral)||(LA4_1>=30 && LA4_1<=32)||LA4_1==36) ) {
                            alt4=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("100:10: ( '<' | '<' '=' | '>' | '>' '=' )", 4, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA4_0==29) ) {
                        int LA4_2 = input.LA(2);

                        if ( (LA4_2==26) ) {
                            alt4=4;
                        }
                        else if ( ((LA4_2>=IDENTIFIER && LA4_2<=DecimalLiteral)||(LA4_2>=30 && LA4_2<=32)||LA4_2==36) ) {
                            alt4=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("100:10: ( '<' | '<' '=' | '>' | '>' '=' )", 4, 2, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("100:10: ( '<' | '<' '=' | '>' | '>' '=' )", 4, 0, input);

                        throw nvae;
                    }
                    switch (alt4) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:100:11: '<'
                            {
                            match(input,28,FOLLOW_28_in_relationalExpression229); 
                            operator="<";

                            }
                            break;
                        case 2 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:101:11: '<' '='
                            {
                            match(input,28,FOLLOW_28_in_relationalExpression243); 
                            match(input,26,FOLLOW_26_in_relationalExpression244); 
                            operator="<=";

                            }
                            break;
                        case 3 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:102:11: '>'
                            {
                            match(input,29,FOLLOW_29_in_relationalExpression258); 
                            operator=">";

                            }
                            break;
                        case 4 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:103:11: '>' '='
                            {
                            match(input,29,FOLLOW_29_in_relationalExpression272); 
                            match(input,26,FOLLOW_26_in_relationalExpression273); 
                            operator=">=";

                            }
                            break;

                    }

                    pushFollow(FOLLOW_additiveExpression_in_relationalExpression288);
                    additiveExpression();
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


    // $ANTLR start additiveExpression
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:114:1: additiveExpression : unaryExpression ( ( '+' | '-' ) unaryExpression )? ;
    public final void additiveExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:118:2: ( unaryExpression ( ( '+' | '-' ) unaryExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:118:4: unaryExpression ( ( '+' | '-' ) unaryExpression )?
            {
            pushFollow(FOLLOW_unaryExpression_in_additiveExpression323);
            unaryExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:119:9: ( ( '+' | '-' ) unaryExpression )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( ((LA7_0>=30 && LA7_0<=31)) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:120:3: ( '+' | '-' ) unaryExpression
                    {
                    
                    			String	operator	= null;
                    		
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:123:10: ( '+' | '-' )
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==30) ) {
                        alt6=1;
                    }
                    else if ( (LA6_0==31) ) {
                        alt6=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("123:10: ( '+' | '-' )", 6, 0, input);

                        throw nvae;
                    }
                    switch (alt6) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:123:11: '+'
                            {
                            match(input,30,FOLLOW_30_in_additiveExpression349); 
                            operator="+";

                            }
                            break;
                        case 2 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:124:11: '-'
                            {
                            match(input,31,FOLLOW_31_in_additiveExpression363); 
                            operator="-";

                            }
                            break;

                    }

                    pushFollow(FOLLOW_unaryExpression_in_additiveExpression378);
                    unaryExpression();
                    _fsp--;

                    
                    	        	// Pop values from stack and add constraint.
                    	        	UnaryExpression	right	= (UnaryExpression)stack.remove(stack.size()-1);
                    	        	UnaryExpression	left	= (UnaryExpression)stack.remove(stack.size()-1);
                    	        	stack.add(new Operation(left, right, operator));
                    	        

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
    // $ANTLR end additiveExpression


    // $ANTLR start unaryExpression
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:135:1: unaryExpression : primary ( suffix )* ;
    public final void unaryExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:139:2: ( primary ( suffix )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:140:2: primary ( suffix )*
            {
            pushFollow(FOLLOW_primary_in_unaryExpression408);
            primary();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:141:2: ( suffix )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==34) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:141:3: suffix
            	    {
            	    pushFollow(FOLLOW_suffix_in_unaryExpression412);
            	    suffix();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            
            		List	suffs	= null;
            		while(stack.get(stack.size()-1) instanceof Suffix)
            		{
            			if(suffs==null)
            				suffs	= new ArrayList();
            			suffs.add(0, stack.remove(stack.size()-1));
            		}
            		Object	prim	= (Object)stack.remove(stack.size()-1);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:157:1: primary : ( '(' expression ')' | literal | {...}? pseudovariable | variable );
    public final void primary() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:161:2: ( '(' expression ')' | literal | {...}? pseudovariable | variable )
            int alt9=4;
            switch ( input.LA(1) ) {
            case 32:
                {
                alt9=1;
                }
                break;
            case CharacterLiteral:
            case StringLiteral:
            case BooleanLiteral:
            case FloatingPointLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 30:
            case 31:
            case 36:
                {
                alt9=2;
                }
                break;
            case IDENTIFIER:
                {
                int LA9_3 = input.LA(2);

                if ( (LA9_3==34) ) {
                    int LA9_4 = input.LA(3);

                    if ( (LA9_4==IDENTIFIER) ) {
                        int LA9_6 = input.LA(4);

                        if ( (helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText())) ) {
                            alt9=3;
                        }
                        else if ( (true) ) {
                            alt9=4;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("157:1: primary : ( '(' expression ')' | literal | {...}? pseudovariable | variable );", 9, 6, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("157:1: primary : ( '(' expression ')' | literal | {...}? pseudovariable | variable );", 9, 4, input);

                        throw nvae;
                    }
                }
                else if ( (LA9_3==EOF||(LA9_3>=25 && LA9_3<=31)||LA9_3==33||LA9_3==35) ) {
                    alt9=4;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("157:1: primary : ( '(' expression ')' | literal | {...}? pseudovariable | variable );", 9, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("157:1: primary : ( '(' expression ')' | literal | {...}? pseudovariable | variable );", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:161:4: '(' expression ')'
                    {
                    match(input,32,FOLLOW_32_in_primary432); 
                    pushFollow(FOLLOW_expression_in_primary434);
                    expression();
                    _fsp--;

                    match(input,33,FOLLOW_33_in_primary436); 

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:162:4: literal
                    {
                    pushFollow(FOLLOW_literal_in_primary441);
                    literal();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:163:4: {...}? pseudovariable
                    {
                    if ( !(helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText())) ) {
                        throw new FailedPredicateException(input, "primary", "helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText())");
                    }
                    pushFollow(FOLLOW_pseudovariable_in_primary448);
                    pseudovariable();
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:164:4: variable
                    {
                    pushFollow(FOLLOW_variable_in_primary453);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:167:1: suffix : ( fieldAccess | methodAccess );
    public final void suffix() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:171:2: ( fieldAccess | methodAccess )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==34) ) {
                int LA10_1 = input.LA(2);

                if ( (LA10_1==IDENTIFIER) ) {
                    int LA10_2 = input.LA(3);

                    if ( (LA10_2==32) ) {
                        alt10=2;
                    }
                    else if ( (LA10_2==EOF||(LA10_2>=25 && LA10_2<=31)||(LA10_2>=33 && LA10_2<=35)) ) {
                        alt10=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("167:1: suffix : ( fieldAccess | methodAccess );", 10, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("167:1: suffix : ( fieldAccess | methodAccess );", 10, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("167:1: suffix : ( fieldAccess | methodAccess );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:171:4: fieldAccess
                    {
                    pushFollow(FOLLOW_fieldAccess_in_suffix466);
                    fieldAccess();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:172:4: methodAccess
                    {
                    pushFollow(FOLLOW_methodAccess_in_suffix471);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:175:1: fieldAccess : '.' tmp= IDENTIFIER ;
    public final void fieldAccess() throws RecognitionException {
        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:179:2: ( '.' tmp= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:179:4: '.' tmp= IDENTIFIER
            {
            match(input,34,FOLLOW_34_in_fieldAccess484); 
            tmp=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_fieldAccess490); 
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:182:1: methodAccess : ( '.' tmp= IDENTIFIER '(' ')' | '.' tmp= IDENTIFIER '(' unaryExpression ( ',' unaryExpression )* ')' );
    public final void methodAccess() throws RecognitionException {
        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:186:2: ( '.' tmp= IDENTIFIER '(' ')' | '.' tmp= IDENTIFIER '(' unaryExpression ( ',' unaryExpression )* ')' )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==34) ) {
                int LA12_1 = input.LA(2);

                if ( (LA12_1==IDENTIFIER) ) {
                    int LA12_2 = input.LA(3);

                    if ( (LA12_2==32) ) {
                        int LA12_3 = input.LA(4);

                        if ( (LA12_3==33) ) {
                            alt12=1;
                        }
                        else if ( ((LA12_3>=IDENTIFIER && LA12_3<=DecimalLiteral)||(LA12_3>=30 && LA12_3<=32)||LA12_3==36) ) {
                            alt12=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("182:1: methodAccess : ( '.' tmp= IDENTIFIER '(' ')' | '.' tmp= IDENTIFIER '(' unaryExpression ( ',' unaryExpression )* ')' );", 12, 3, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("182:1: methodAccess : ( '.' tmp= IDENTIFIER '(' ')' | '.' tmp= IDENTIFIER '(' unaryExpression ( ',' unaryExpression )* ')' );", 12, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("182:1: methodAccess : ( '.' tmp= IDENTIFIER '(' ')' | '.' tmp= IDENTIFIER '(' unaryExpression ( ',' unaryExpression )* ')' );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("182:1: methodAccess : ( '.' tmp= IDENTIFIER '(' ')' | '.' tmp= IDENTIFIER '(' unaryExpression ( ',' unaryExpression )* ')' );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:186:4: '.' tmp= IDENTIFIER '(' ')'
                    {
                    match(input,34,FOLLOW_34_in_methodAccess505); 
                    tmp=(Token)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodAccess511); 
                    match(input,32,FOLLOW_32_in_methodAccess513); 
                    match(input,33,FOLLOW_33_in_methodAccess515); 
                    stack.add(new MethodAccess(tmp.getText(), null));

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:187:4: '.' tmp= IDENTIFIER '(' unaryExpression ( ',' unaryExpression )* ')'
                    {
                    match(input,34,FOLLOW_34_in_methodAccess522); 
                    tmp=(Token)input.LT(1);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodAccess528); 
                    match(input,32,FOLLOW_32_in_methodAccess530); 
                    pushFollow(FOLLOW_unaryExpression_in_methodAccess532);
                    unaryExpression();
                    _fsp--;

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:188:2: ( ',' unaryExpression )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==35) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:188:3: ',' unaryExpression
                    	    {
                    	    match(input,35,FOLLOW_35_in_methodAccess537); 
                    	    pushFollow(FOLLOW_unaryExpression_in_methodAccess539);
                    	    unaryExpression();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop11;
                        }
                    } while (true);

                    match(input,33,FOLLOW_33_in_methodAccess546); 
                    
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:202:1: variable : tmp= IDENTIFIER ;
    public final void variable() throws RecognitionException {
        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:206:2: (tmp= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:206:4: tmp= IDENTIFIER
            {
            tmp=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variable566); 
            
            		Variable	var	= helper.getVariable(tmp.getText());
            		if(var!=null)
            			stack.add(var);
            		else
            			throw new RuntimeException("No such variable: "+tmp.getText());
            	

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


    // $ANTLR start pseudovariable
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:216:1: pseudovariable : tmp= IDENTIFIER '.' tmp2= IDENTIFIER ;
    public final void pseudovariable() throws RecognitionException {
        Token tmp=null;
        Token tmp2=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:220:2: (tmp= IDENTIFIER '.' tmp2= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:220:4: tmp= IDENTIFIER '.' tmp2= IDENTIFIER
            {
            tmp=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_pseudovariable586); 
            match(input,34,FOLLOW_34_in_pseudovariable588); 
            tmp2=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_pseudovariable592); 
            
            		String name	= tmp.getText()+"."+tmp2.getText();
            		Variable	var	= helper.getVariable(name);
            		if(var!=null)
            			stack.add(var);
            		else
            			throw new RuntimeException("No such variable: "+name);
            	

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
    // $ANTLR end pseudovariable


    // $ANTLR start literal
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:231:1: literal : ( floatingPointLiteral | integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );
    public final void literal() throws RecognitionException {
        Token CharacterLiteral1=null;
        Token StringLiteral2=null;
        Token BooleanLiteral3=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:232:2: ( floatingPointLiteral | integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' )
            int alt13=6;
            switch ( input.LA(1) ) {
            case 30:
            case 31:
                {
                int LA13_1 = input.LA(2);

                if ( ((LA13_1>=HexLiteral && LA13_1<=DecimalLiteral)) ) {
                    alt13=2;
                }
                else if ( (LA13_1==FloatingPointLiteral) ) {
                    alt13=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("231:1: literal : ( floatingPointLiteral | integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );", 13, 1, input);

                    throw nvae;
                }
                }
                break;
            case FloatingPointLiteral:
                {
                alt13=1;
                }
                break;
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
                {
                alt13=2;
                }
                break;
            case CharacterLiteral:
                {
                alt13=3;
                }
                break;
            case StringLiteral:
                {
                alt13=4;
                }
                break;
            case BooleanLiteral:
                {
                alt13=5;
                }
                break;
            case 36:
                {
                alt13=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("231:1: literal : ( floatingPointLiteral | integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:232:4: floatingPointLiteral
                    {
                    pushFollow(FOLLOW_floatingPointLiteral_in_literal606);
                    floatingPointLiteral();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:233:4: integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal611);
                    integerLiteral();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:234:4: CharacterLiteral
                    {
                    CharacterLiteral1=(Token)input.LT(1);
                    match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal616); 
                    stack.add(new Literal(new Character(CharacterLiteral1.getText().charAt(0))));

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:235:4: StringLiteral
                    {
                    StringLiteral2=(Token)input.LT(1);
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_literal623); 
                    stack.add(new Literal(StringLiteral2.getText().substring(1, StringLiteral2.getText().length()-1)));

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:236:4: BooleanLiteral
                    {
                    BooleanLiteral3=(Token)input.LT(1);
                    match(input,BooleanLiteral,FOLLOW_BooleanLiteral_in_literal630); 
                    stack.add(new Literal(BooleanLiteral3.getText().equals("true")? Boolean.TRUE: Boolean.FALSE));

                    }
                    break;
                case 6 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:237:4: 'null'
                    {
                    match(input,36,FOLLOW_36_in_literal637); 
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:240:1: floatingPointLiteral : (sign= ( '+' | '-' ) )? FloatingPointLiteral ;
    public final void floatingPointLiteral() throws RecognitionException {
        Token sign=null;
        Token FloatingPointLiteral4=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:241:2: ( (sign= ( '+' | '-' ) )? FloatingPointLiteral )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:241:4: (sign= ( '+' | '-' ) )? FloatingPointLiteral
            {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:241:8: (sign= ( '+' | '-' ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( ((LA14_0>=30 && LA14_0<=31)) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:241:8: sign= ( '+' | '-' )
                    {
                    sign=(Token)input.LT(1);
                    if ( (input.LA(1)>=30 && input.LA(1)<=31) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_floatingPointLiteral652);    throw mse;
                    }


                    }
                    break;

            }

            FloatingPointLiteral4=(Token)input.LT(1);
            match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_floatingPointLiteral659); 
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:244:1: integerLiteral returns [Object val] : (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral ) ;
    public final Object integerLiteral() throws RecognitionException {
        Object val = null;

        Token sign=null;
        Token HexLiteral5=null;
        Token OctalLiteral6=null;
        Token DecimalLiteral7=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:246:2: ( (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral ) )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:246:4: (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral )
            {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:246:8: (sign= ( '+' | '-' ) )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( ((LA15_0>=30 && LA15_0<=31)) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:246:8: sign= ( '+' | '-' )
                    {
                    sign=(Token)input.LT(1);
                    if ( (input.LA(1)>=30 && input.LA(1)<=31) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_integerLiteral680);    throw mse;
                    }


                    }
                    break;

            }

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:246:20: ( HexLiteral | OctalLiteral | DecimalLiteral )
            int alt16=3;
            switch ( input.LA(1) ) {
            case HexLiteral:
                {
                alt16=1;
                }
                break;
            case OctalLiteral:
                {
                alt16=2;
                }
                break;
            case DecimalLiteral:
                {
                alt16=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("246:20: ( HexLiteral | OctalLiteral | DecimalLiteral )", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:246:21: HexLiteral
                    {
                    HexLiteral5=(Token)input.LT(1);
                    match(input,HexLiteral,FOLLOW_HexLiteral_in_integerLiteral688); 
                    stack.add(new Literal(sign!=null && "-".equals(sign.getText())? new Integer("-"+HexLiteral5.getText()): new Integer(HexLiteral5.getText())));

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:247:4: OctalLiteral
                    {
                    OctalLiteral6=(Token)input.LT(1);
                    match(input,OctalLiteral,FOLLOW_OctalLiteral_in_integerLiteral695); 
                    stack.add(new Literal((sign!=null && "-".equals(sign.getText())? new Integer("-"+OctalLiteral6.getText()): new Integer(OctalLiteral6.getText()))));

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:248:4: DecimalLiteral
                    {
                    DecimalLiteral7=(Token)input.LT(1);
                    match(input,DecimalLiteral,FOLLOW_DecimalLiteral_in_integerLiteral702); 
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


 

    public static final BitSet FOLLOW_expression_in_lhs40 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_lhs42 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_logicalAndExpression_in_expression55 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_logicalAndExpression69 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_logicalAndExpression90 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_logicalAndExpression91 = new BitSet(new long[]{0x00000011C0000FF0L});
    public static final BitSet FOLLOW_equalityExpression_in_logicalAndExpression93 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression118 = new BitSet(new long[]{0x000000000C000002L});
    public static final BitSet FOLLOW_26_in_equalityExpression144 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_equalityExpression145 = new BitSet(new long[]{0x00000011C0000FF0L});
    public static final BitSet FOLLOW_27_in_equalityExpression159 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_equalityExpression160 = new BitSet(new long[]{0x00000011C0000FF0L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression203 = new BitSet(new long[]{0x0000000030000002L});
    public static final BitSet FOLLOW_28_in_relationalExpression229 = new BitSet(new long[]{0x00000011C0000FF0L});
    public static final BitSet FOLLOW_28_in_relationalExpression243 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_relationalExpression244 = new BitSet(new long[]{0x00000011C0000FF0L});
    public static final BitSet FOLLOW_29_in_relationalExpression258 = new BitSet(new long[]{0x00000011C0000FF0L});
    public static final BitSet FOLLOW_29_in_relationalExpression272 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_relationalExpression273 = new BitSet(new long[]{0x00000011C0000FF0L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_additiveExpression323 = new BitSet(new long[]{0x00000000C0000002L});
    public static final BitSet FOLLOW_30_in_additiveExpression349 = new BitSet(new long[]{0x00000011C0000FF0L});
    public static final BitSet FOLLOW_31_in_additiveExpression363 = new BitSet(new long[]{0x00000011C0000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_additiveExpression378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpression408 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_suffix_in_unaryExpression412 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_32_in_primary432 = new BitSet(new long[]{0x00000011C0000FF0L});
    public static final BitSet FOLLOW_expression_in_primary434 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_primary436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pseudovariable_in_primary448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_primary453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldAccess_in_suffix466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodAccess_in_suffix471 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_fieldAccess484 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_fieldAccess490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_methodAccess505 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodAccess511 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_methodAccess513 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_methodAccess515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_methodAccess522 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodAccess528 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_methodAccess530 = new BitSet(new long[]{0x00000011C0000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_methodAccess532 = new BitSet(new long[]{0x0000000A00000000L});
    public static final BitSet FOLLOW_35_in_methodAccess537 = new BitSet(new long[]{0x00000011C0000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_methodAccess539 = new BitSet(new long[]{0x0000000A00000000L});
    public static final BitSet FOLLOW_33_in_methodAccess546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_pseudovariable586 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_pseudovariable588 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_pseudovariable592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_floatingPointLiteral_in_literal606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BooleanLiteral_in_literal630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_literal637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_floatingPointLiteral652 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_floatingPointLiteral659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral680 = new BitSet(new long[]{0x0000000000000E00L});
    public static final BitSet FOLLOW_HexLiteral_in_integerLiteral688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OctalLiteral_in_integerLiteral695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DecimalLiteral_in_integerLiteral702 = new BitSet(new long[]{0x0000000000000002L});

}