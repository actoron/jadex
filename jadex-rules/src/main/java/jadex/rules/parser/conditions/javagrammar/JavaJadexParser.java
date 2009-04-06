// $ANTLR 3.1.2 C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g 2009-04-06 16:14:04

package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.functions.IFunction;

import jadex.commons.SReflect;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class JavaJadexParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENTIFIER", "CharacterLiteral", "StringLiteral", "BooleanLiteral", "FloatingPointLiteral", "HexLiteral", "OctalLiteral", "DecimalLiteral", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "COMMENT", "LINE_COMMENT", "'?'", "':'", "'||'", "'&&'", "'=='", "'!='", "'<'", "'<='", "'>'", "'>='", "'+'", "'-'", "'*'", "'/'", "'%'", "'!'", "'~'", "'('", "')'", "'.'", "','", "'['", "']'", "'null'"
    };
    public static final int T__29=29;
    public static final int T__28=28;
    public static final int T__27=27;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int FloatTypeSuffix=15;
    public static final int T__24=24;
    public static final int OctalLiteral=10;
    public static final int CharacterLiteral=5;
    public static final int Exponent=14;
    public static final int EOF=-1;
    public static final int HexDigit=12;
    public static final int IDENTIFIER=4;
    public static final int COMMENT=22;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int HexLiteral=9;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int LINE_COMMENT=23;
    public static final int IntegerTypeSuffix=13;
    public static final int DecimalLiteral=11;
    public static final int StringLiteral=6;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int WS=21;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int UnicodeEscape=17;
    public static final int FloatingPointLiteral=8;
    public static final int JavaIDDigit=20;
    public static final int EscapeSequence=16;
    public static final int OctalEscape=18;
    public static final int Letter=19;
    public static final int BooleanLiteral=7;

    // delegates
    // delegators


        public JavaJadexParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public JavaJadexParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return JavaJadexParser.tokenNames; }
    public String getGrammarFileName() { return "C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g"; }


    	/** The parser helper provides additional information (e.g. local variables). */
    	protected IParserHelper	helper;
    	
    	/**
    	 *  Set the parser helper.
    	 */
    	public void	setParserHelper(IParserHelper helper)
    	{
    		this.helper	= helper;
    	}

    	/** The imports (if any). */
    	protected String[]	imports;
    	
    	/**
    	 *  Set the imports.
    	 */
    	public void	setImports(String[] imports)
    	{
    		this.imports	= imports;
    	}

    	/** The classloader. */
    	protected ClassLoader	cloader;
    	
    	/**
    	 *  Set the class loader.
    	 */
    	public void	setClassLoader(ClassLoader cloader)
    	{
    		this.cloader	= cloader;
    	}



    // $ANTLR start "lhs"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:59:1: lhs returns [Expression exp] : tmp= expression EOF ;
    public final Expression lhs() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:63:2: (tmp= expression EOF )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:63:4: tmp= expression EOF
            {
            pushFollow(FOLLOW_expression_in_lhs48);
            tmp=expression();

            state._fsp--;

            match(input,EOF,FOLLOW_EOF_in_lhs50); 
            exp = tmp;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return exp;
    }
    // $ANTLR end "lhs"


    // $ANTLR start "expression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:66:1: expression returns [Expression exp] : tmp= conditionalExpression ;
    public final Expression expression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:71:2: (tmp= conditionalExpression )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:71:4: tmp= conditionalExpression
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression73);
            tmp=conditionalExpression();

            state._fsp--;

            exp = tmp;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return exp;
    }
    // $ANTLR end "expression"


    // $ANTLR start "conditionalExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:76:1: conditionalExpression returns [Expression exp] : tmp= logicalOrExpression ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )? ;
    public final Expression conditionalExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;

        Expression tmp3 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:80:2: (tmp= logicalOrExpression ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:80:4: tmp= logicalOrExpression ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )?
            {
            pushFollow(FOLLOW_logicalOrExpression_in_conditionalExpression98);
            tmp=logicalOrExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:81:9: ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==24) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:82:10: '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression
                    {
                    match(input,24,FOLLOW_24_in_conditionalExpression121); 
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression127);
                    tmp2=conditionalExpression();

                    state._fsp--;

                    match(input,25,FOLLOW_25_in_conditionalExpression129); 
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression135);
                    tmp3=conditionalExpression();

                    state._fsp--;


                            		exp = new ConditionalExpression(tmp, tmp2, tmp3);
                            	

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
        return exp;
    }
    // $ANTLR end "conditionalExpression"


    // $ANTLR start "logicalOrExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:89:1: logicalOrExpression returns [Expression exp] : tmp= logicalAndExpression ( '||' tmp2= logicalAndExpression )* ;
    public final Expression logicalOrExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:93:2: (tmp= logicalAndExpression ( '||' tmp2= logicalAndExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:93:4: tmp= logicalAndExpression ( '||' tmp2= logicalAndExpression )*
            {
            pushFollow(FOLLOW_logicalAndExpression_in_logicalOrExpression179);
            tmp=logicalAndExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:94:9: ( '||' tmp2= logicalAndExpression )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==26) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:95:10: '||' tmp2= logicalAndExpression
            	    {
            	    match(input,26,FOLLOW_26_in_logicalOrExpression202); 
            	    pushFollow(FOLLOW_logicalAndExpression_in_logicalOrExpression208);
            	    tmp2=logicalAndExpression();

            	    state._fsp--;


            	            		exp = new OperationExpression(exp, tmp2, OperationExpression.OPERATOR_OR);
            	            	

            	    }
            	    break;

            	default :
            	    break loop2;
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
        return exp;
    }
    // $ANTLR end "logicalOrExpression"


    // $ANTLR start "logicalAndExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:102:1: logicalAndExpression returns [Expression exp] : tmp= equalityExpression ( '&&' tmp2= equalityExpression )* ;
    public final Expression logicalAndExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:106:2: (tmp= equalityExpression ( '&&' tmp2= equalityExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:106:4: tmp= equalityExpression ( '&&' tmp2= equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_logicalAndExpression252);
            tmp=equalityExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:107:9: ( '&&' tmp2= equalityExpression )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==27) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:108:10: '&&' tmp2= equalityExpression
            	    {
            	    match(input,27,FOLLOW_27_in_logicalAndExpression275); 
            	    pushFollow(FOLLOW_equalityExpression_in_logicalAndExpression281);
            	    tmp2=equalityExpression();

            	    state._fsp--;


            	            		exp = new OperationExpression(exp, tmp2, OperationExpression.OPERATOR_AND);
            	            	

            	    }
            	    break;

            	default :
            	    break loop3;
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
        return exp;
    }
    // $ANTLR end "logicalAndExpression"


    // $ANTLR start "equalityExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:115:1: equalityExpression returns [Expression exp] : tmp= relationalExpression ( ( '==' | '!=' ) tmp2= relationalExpression )* ;
    public final Expression equalityExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:119:2: (tmp= relationalExpression ( ( '==' | '!=' ) tmp2= relationalExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:119:4: tmp= relationalExpression ( ( '==' | '!=' ) tmp2= relationalExpression )*
            {
            pushFollow(FOLLOW_relationalExpression_in_equalityExpression325);
            tmp=relationalExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:120:9: ( ( '==' | '!=' ) tmp2= relationalExpression )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0>=28 && LA5_0<=29)) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:121:3: ( '==' | '!=' ) tmp2= relationalExpression
            	    {

            	    			IOperator	operator	= null;
            	    		
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:124:10: ( '==' | '!=' )
            	    int alt4=2;
            	    int LA4_0 = input.LA(1);

            	    if ( (LA4_0==28) ) {
            	        alt4=1;
            	    }
            	    else if ( (LA4_0==29) ) {
            	        alt4=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 4, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt4) {
            	        case 1 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:124:11: '=='
            	            {
            	            match(input,28,FOLLOW_28_in_equalityExpression353); 
            	            operator=IOperator.EQUAL;

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:125:11: '!='
            	            {
            	            match(input,29,FOLLOW_29_in_equalityExpression367); 
            	            operator=IOperator.NOTEQUAL;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_relationalExpression_in_equalityExpression386);
            	    tmp2=relationalExpression();

            	    state._fsp--;


            	    	        	exp = new OperationExpression(exp, tmp2, operator);
            	    	        

            	    }
            	    break;

            	default :
            	    break loop5;
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
        return exp;
    }
    // $ANTLR end "equalityExpression"


    // $ANTLR start "relationalExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:133:1: relationalExpression returns [Expression exp] : tmp= additiveExpression ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )* ;
    public final Expression relationalExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:137:2: (tmp= additiveExpression ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:137:4: tmp= additiveExpression ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_relationalExpression422);
            tmp=additiveExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:138:9: ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>=30 && LA7_0<=33)) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:139:3: ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression
            	    {

            	    			IOperator	operator	= null;
            	    		
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:142:10: ( '<' | '<=' | '>' | '>=' )
            	    int alt6=4;
            	    switch ( input.LA(1) ) {
            	    case 30:
            	        {
            	        alt6=1;
            	        }
            	        break;
            	    case 31:
            	        {
            	        alt6=2;
            	        }
            	        break;
            	    case 32:
            	        {
            	        alt6=3;
            	        }
            	        break;
            	    case 33:
            	        {
            	        alt6=4;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 6, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt6) {
            	        case 1 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:142:11: '<'
            	            {
            	            match(input,30,FOLLOW_30_in_relationalExpression450); 
            	            operator=IOperator.LESS;

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:143:11: '<='
            	            {
            	            match(input,31,FOLLOW_31_in_relationalExpression464); 
            	            operator=IOperator.LESSOREQUAL;

            	            }
            	            break;
            	        case 3 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:144:11: '>'
            	            {
            	            match(input,32,FOLLOW_32_in_relationalExpression478); 
            	            operator=IOperator.GREATER;

            	            }
            	            break;
            	        case 4 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:145:11: '>='
            	            {
            	            match(input,33,FOLLOW_33_in_relationalExpression492); 
            	            operator=IOperator.GREATEROREQUAL;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_additiveExpression_in_relationalExpression511);
            	    tmp2=additiveExpression();

            	    state._fsp--;


            	    	        	exp = new OperationExpression(exp, tmp2, operator);
            	    	        

            	    }
            	    break;

            	default :
            	    break loop7;
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
        return exp;
    }
    // $ANTLR end "relationalExpression"


    // $ANTLR start "additiveExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:153:1: additiveExpression returns [Expression exp] : tmp= multiplicativeExpression ( ( '+' | '-' ) tmp2= multiplicativeExpression )* ;
    public final Expression additiveExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:157:2: (tmp= multiplicativeExpression ( ( '+' | '-' ) tmp2= multiplicativeExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:157:4: tmp= multiplicativeExpression ( ( '+' | '-' ) tmp2= multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression554);
            tmp=multiplicativeExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:158:9: ( ( '+' | '-' ) tmp2= multiplicativeExpression )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>=34 && LA9_0<=35)) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:159:3: ( '+' | '-' ) tmp2= multiplicativeExpression
            	    {

            	    			IFunction	operator	= null;
            	    		
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:162:10: ( '+' | '-' )
            	    int alt8=2;
            	    int LA8_0 = input.LA(1);

            	    if ( (LA8_0==34) ) {
            	        alt8=1;
            	    }
            	    else if ( (LA8_0==35) ) {
            	        alt8=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 8, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt8) {
            	        case 1 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:162:11: '+'
            	            {
            	            match(input,34,FOLLOW_34_in_additiveExpression582); 
            	            operator=IFunction.SUM;

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:163:11: '-'
            	            {
            	            match(input,35,FOLLOW_35_in_additiveExpression596); 
            	            operator=IFunction.SUB;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression615);
            	    tmp2=multiplicativeExpression();

            	    state._fsp--;


            	    	        	exp = new OperationExpression(exp, tmp2, operator);
            	    	        

            	    }
            	    break;

            	default :
            	    break loop9;
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
        return exp;
    }
    // $ANTLR end "additiveExpression"


    // $ANTLR start "multiplicativeExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:171:1: multiplicativeExpression returns [Expression exp] : tmp= unaryExpression ( ( '*' | '/' | '%' ) tmp2= unaryExpression )* ;
    public final Expression multiplicativeExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:175:2: (tmp= unaryExpression ( ( '*' | '/' | '%' ) tmp2= unaryExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:175:4: tmp= unaryExpression ( ( '*' | '/' | '%' ) tmp2= unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression651);
            tmp=unaryExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:176:9: ( ( '*' | '/' | '%' ) tmp2= unaryExpression )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>=36 && LA11_0<=38)) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:177:3: ( '*' | '/' | '%' ) tmp2= unaryExpression
            	    {

            	    			IFunction	operator	= null;
            	    		
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:180:10: ( '*' | '/' | '%' )
            	    int alt10=3;
            	    switch ( input.LA(1) ) {
            	    case 36:
            	        {
            	        alt10=1;
            	        }
            	        break;
            	    case 37:
            	        {
            	        alt10=2;
            	        }
            	        break;
            	    case 38:
            	        {
            	        alt10=3;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 10, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt10) {
            	        case 1 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:180:11: '*'
            	            {
            	            match(input,36,FOLLOW_36_in_multiplicativeExpression679); 
            	            operator=IFunction.MULT;

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:181:11: '/'
            	            {
            	            match(input,37,FOLLOW_37_in_multiplicativeExpression693); 
            	            operator=IFunction.DIV;

            	            }
            	            break;
            	        case 3 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:182:11: '%'
            	            {
            	            match(input,38,FOLLOW_38_in_multiplicativeExpression707); 
            	            operator=IFunction.MOD;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression726);
            	    tmp2=unaryExpression();

            	    state._fsp--;


            	    	        	exp = new OperationExpression(exp, tmp2, operator);
            	    	        

            	    }
            	    break;

            	default :
            	    break loop11;
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
        return exp;
    }
    // $ANTLR end "multiplicativeExpression"


    // $ANTLR start "unaryExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:190:1: unaryExpression returns [Expression exp] : ( '+' tmp= unaryExpression | '-' tmp= unaryExpression | '!' tmp= unaryExpression | '~' tmp= unaryExpression | tmp= primaryExpression );
    public final Expression unaryExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:194:2: ( '+' tmp= unaryExpression | '-' tmp= unaryExpression | '!' tmp= unaryExpression | '~' tmp= unaryExpression | tmp= primaryExpression )
            int alt12=5;
            switch ( input.LA(1) ) {
            case 34:
                {
                alt12=1;
                }
                break;
            case 35:
                {
                alt12=2;
                }
                break;
            case 39:
                {
                alt12=3;
                }
                break;
            case 40:
                {
                alt12=4;
                }
                break;
            case IDENTIFIER:
            case CharacterLiteral:
            case StringLiteral:
            case BooleanLiteral:
            case FloatingPointLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 41:
            case 47:
                {
                alt12=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:194:4: '+' tmp= unaryExpression
                    {
                    match(input,34,FOLLOW_34_in_unaryExpression759); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression765);
                    tmp=unaryExpression();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:195:4: '-' tmp= unaryExpression
                    {
                    match(input,35,FOLLOW_35_in_unaryExpression772); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression778);
                    tmp=unaryExpression();

                    state._fsp--;

                    exp = new UnaryExpression(tmp, UnaryExpression.OPERATOR_MINUS);

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:196:4: '!' tmp= unaryExpression
                    {
                    match(input,39,FOLLOW_39_in_unaryExpression785); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression791);
                    tmp=unaryExpression();

                    state._fsp--;

                    exp = new UnaryExpression(tmp, UnaryExpression.OPERATOR_NOT);

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:197:4: '~' tmp= unaryExpression
                    {
                    match(input,40,FOLLOW_40_in_unaryExpression798); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression804);
                    tmp=unaryExpression();

                    state._fsp--;

                    exp = new UnaryExpression(tmp, UnaryExpression.OPERATOR_BNOT);

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:198:4: tmp= primaryExpression
                    {
                    pushFollow(FOLLOW_primaryExpression_in_unaryExpression815);
                    tmp=primaryExpression();

                    state._fsp--;

                    exp = tmp;

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
        return exp;
    }
    // $ANTLR end "unaryExpression"


    // $ANTLR start "primaryExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:201:1: primaryExpression returns [Expression exp] : tmp= primaryPrefix (tmp2= primarySuffix )* ;
    public final Expression primaryExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Suffix tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:205:2: (tmp= primaryPrefix (tmp2= primarySuffix )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:206:2: tmp= primaryPrefix (tmp2= primarySuffix )*
            {
            pushFollow(FOLLOW_primaryPrefix_in_primaryExpression840);
            tmp=primaryPrefix();

            state._fsp--;

            List suffs = null;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:207:2: (tmp2= primarySuffix )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==43||LA13_0==45) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:207:3: tmp2= primarySuffix
            	    {
            	    pushFollow(FOLLOW_primarySuffix_in_primaryExpression850);
            	    tmp2=primarySuffix();

            	    state._fsp--;


            	    			if(suffs==null)
            	    				suffs	= new ArrayList();
            	    			suffs.add(tmp2);
            	    		

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            		if(suffs==null)
            			exp = tmp;
            		else
            			exp = new PrimaryExpression(tmp, (Suffix[])suffs.toArray(new Suffix[suffs.size()]));
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return exp;
    }
    // $ANTLR end "primaryExpression"


    // $ANTLR start "primaryPrefix"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:222:1: primaryPrefix returns [Expression exp] : ( '(' tmp= expression ')' | tmp= literal | {...}?tmp= pseudovariable | {...}?tmp= variable | tmp= staticField );
    public final Expression primaryPrefix() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:226:2: ( '(' tmp= expression ')' | tmp= literal | {...}?tmp= pseudovariable | {...}?tmp= variable | tmp= staticField )
            int alt14=5;
            switch ( input.LA(1) ) {
            case 41:
                {
                alt14=1;
                }
                break;
            case CharacterLiteral:
            case StringLiteral:
            case BooleanLiteral:
            case FloatingPointLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 47:
                {
                alt14=2;
                }
                break;
            case IDENTIFIER:
                {
                int LA14_3 = input.LA(2);

                if ( (LA14_3==43) ) {
                    int LA14_4 = input.LA(3);

                    if ( (LA14_4==IDENTIFIER) ) {
                        int LA14_6 = input.LA(4);

                        if ( ((helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText()))) ) {
                            alt14=3;
                        }
                        else if ( ((helper.getVariable(JavaJadexParser.this.input.LT(1).getText())!=null)) ) {
                            alt14=4;
                        }
                        else if ( (true) ) {
                            alt14=5;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 14, 6, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 14, 4, input);

                        throw nvae;
                    }
                }
                else if ( (LA14_3==EOF||(LA14_3>=24 && LA14_3<=38)||LA14_3==42||(LA14_3>=44 && LA14_3<=46)) ) {
                    alt14=4;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:226:4: '(' tmp= expression ')'
                    {
                    match(input,41,FOLLOW_41_in_primaryPrefix878); 
                    pushFollow(FOLLOW_expression_in_primaryPrefix884);
                    tmp=expression();

                    state._fsp--;

                    match(input,42,FOLLOW_42_in_primaryPrefix886); 
                    exp = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:227:4: tmp= literal
                    {
                    pushFollow(FOLLOW_literal_in_primaryPrefix897);
                    tmp=literal();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:228:4: {...}?tmp= pseudovariable
                    {
                    if ( !((helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText()))) ) {
                        throw new FailedPredicateException(input, "primaryPrefix", "helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText())");
                    }
                    pushFollow(FOLLOW_pseudovariable_in_primaryPrefix910);
                    tmp=pseudovariable();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:229:4: {...}?tmp= variable
                    {
                    if ( !((helper.getVariable(JavaJadexParser.this.input.LT(1).getText())!=null)) ) {
                        throw new FailedPredicateException(input, "primaryPrefix", "helper.getVariable(JavaJadexParser.this.input.LT(1).getText())!=null");
                    }
                    pushFollow(FOLLOW_variable_in_primaryPrefix923);
                    tmp=variable();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:230:4: tmp= staticField
                    {
                    pushFollow(FOLLOW_staticField_in_primaryPrefix934);
                    tmp=staticField();

                    state._fsp--;

                    exp = tmp;

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
        return exp;
    }
    // $ANTLR end "primaryPrefix"


    // $ANTLR start "staticField"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:234:1: staticField returns [Expression exp] : tmp= type '.' tmp2= IDENTIFIER ;
    public final Expression staticField() throws RecognitionException {
        Expression exp = null;

        Token tmp2=null;
        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:238:2: (tmp= type '.' tmp2= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:238:4: tmp= type '.' tmp2= IDENTIFIER
            {
            pushFollow(FOLLOW_type_in_staticField958);
            tmp=type();

            state._fsp--;

            match(input,43,FOLLOW_43_in_staticField960); 
            tmp2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_staticField966); 
            /*exp = new FieldAccess(tmp.getText());*/

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return exp;
    }
    // $ANTLR end "staticField"


    // $ANTLR start "type"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:241:1: type returns [Expression exp] : tmp= IDENTIFIER ({...}? '.' tmp2= IDENTIFIER )* ;
    public final Expression type() throws RecognitionException {
        Expression exp = null;

        Token tmp=null;
        Token tmp2=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:245:2: (tmp= IDENTIFIER ({...}? '.' tmp2= IDENTIFIER )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:245:4: tmp= IDENTIFIER ({...}? '.' tmp2= IDENTIFIER )*
            {
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_type989); 
            String classname = tmp.getText();
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:246:2: ({...}? '.' tmp2= IDENTIFIER )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==43) ) {
                    int LA15_1 = input.LA(2);

                    if ( (LA15_1==IDENTIFIER) ) {
                        int LA15_2 = input.LA(3);

                        if ( (LA15_2==43) ) {
                            int LA15_3 = input.LA(4);

                            if ( (LA15_3==IDENTIFIER) ) {
                                int LA15_5 = input.LA(5);

                                if ( ((SReflect.findClass0(classname, imports, cloader)==null)) ) {
                                    alt15=1;
                                }


                            }


                        }


                    }


                }


                switch (alt15) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:246:4: {...}? '.' tmp2= IDENTIFIER
            	    {
            	    if ( !((SReflect.findClass0(classname, imports, cloader)==null)) ) {
            	        throw new FailedPredicateException(input, "type", "SReflect.findClass0(classname, imports, cloader)==null");
            	    }
            	    match(input,43,FOLLOW_43_in_type1000); 
            	    tmp2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_type1006); 
            	    classname += "."+tmp.getText();

            	    }
            	    break;

            	default :
            	    break loop15;
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
        return exp;
    }
    // $ANTLR end "type"


    // $ANTLR start "primarySuffix"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:251:1: primarySuffix returns [Suffix suff] : (tmp= fieldAccess | tmp= methodAccess | tmp= arrayAccess );
    public final Suffix primarySuffix() throws RecognitionException {
        Suffix suff = null;

        Suffix tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:255:2: (tmp= fieldAccess | tmp= methodAccess | tmp= arrayAccess )
            int alt16=3;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==43) ) {
                int LA16_1 = input.LA(2);

                if ( (LA16_1==IDENTIFIER) ) {
                    int LA16_3 = input.LA(3);

                    if ( (LA16_3==41) ) {
                        alt16=2;
                    }
                    else if ( (LA16_3==EOF||(LA16_3>=24 && LA16_3<=38)||(LA16_3>=42 && LA16_3<=46)) ) {
                        alt16=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 16, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA16_0==45) ) {
                alt16=3;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:255:4: tmp= fieldAccess
                    {
                    pushFollow(FOLLOW_fieldAccess_in_primarySuffix1034);
                    tmp=fieldAccess();

                    state._fsp--;

                    suff = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:256:4: tmp= methodAccess
                    {
                    pushFollow(FOLLOW_methodAccess_in_primarySuffix1045);
                    tmp=methodAccess();

                    state._fsp--;

                    suff = tmp;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:257:4: tmp= arrayAccess
                    {
                    pushFollow(FOLLOW_arrayAccess_in_primarySuffix1056);
                    tmp=arrayAccess();

                    state._fsp--;

                    suff = tmp;

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
        return suff;
    }
    // $ANTLR end "primarySuffix"


    // $ANTLR start "fieldAccess"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:260:1: fieldAccess returns [Suffix suff] : '.' tmp= IDENTIFIER ;
    public final Suffix fieldAccess() throws RecognitionException {
        Suffix suff = null;

        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:264:2: ( '.' tmp= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:264:4: '.' tmp= IDENTIFIER
            {
            match(input,43,FOLLOW_43_in_fieldAccess1075); 
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_fieldAccess1081); 
            suff = new FieldAccess(tmp.getText());

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return suff;
    }
    // $ANTLR end "fieldAccess"


    // $ANTLR start "methodAccess"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:267:1: methodAccess returns [Suffix suff] : ( '.' tmp1= IDENTIFIER '(' ')' | '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')' );
    public final Suffix methodAccess() throws RecognitionException {
        Suffix suff = null;

        Token tmp1=null;
        Token tmp2=null;
        Expression p1 = null;

        Expression p2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:271:2: ( '.' tmp1= IDENTIFIER '(' ')' | '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')' )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==43) ) {
                int LA18_1 = input.LA(2);

                if ( (LA18_1==IDENTIFIER) ) {
                    int LA18_2 = input.LA(3);

                    if ( (LA18_2==41) ) {
                        int LA18_3 = input.LA(4);

                        if ( (LA18_3==42) ) {
                            alt18=1;
                        }
                        else if ( ((LA18_3>=IDENTIFIER && LA18_3<=DecimalLiteral)||(LA18_3>=34 && LA18_3<=35)||(LA18_3>=39 && LA18_3<=41)||LA18_3==47) ) {
                            alt18=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 18, 3, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 18, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:271:4: '.' tmp1= IDENTIFIER '(' ')'
                    {
                    match(input,43,FOLLOW_43_in_methodAccess1100); 
                    tmp1=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodAccess1106); 
                    match(input,41,FOLLOW_41_in_methodAccess1108); 
                    match(input,42,FOLLOW_42_in_methodAccess1110); 
                    suff = new MethodAccess(tmp1.getText(), null);

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:272:4: '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')'
                    {
                    match(input,43,FOLLOW_43_in_methodAccess1117); 
                    tmp2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodAccess1123); 
                    match(input,41,FOLLOW_41_in_methodAccess1125); 
                    pushFollow(FOLLOW_expression_in_methodAccess1131);
                    p1=expression();

                    state._fsp--;


                    		List params	= new ArrayList();
                    		params.add(p1);
                    	
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:277:2: ( ',' p2= expression )*
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==44) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:277:3: ',' p2= expression
                    	    {
                    	    match(input,44,FOLLOW_44_in_methodAccess1138); 
                    	    pushFollow(FOLLOW_expression_in_methodAccess1144);
                    	    p2=expression();

                    	    state._fsp--;

                    	    params.add(p2);

                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);

                    match(input,42,FOLLOW_42_in_methodAccess1152); 

                    		suff = new MethodAccess(tmp2.getText(), (Expression[])params.toArray(new Expression[params.size()]));
                    	

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
        return suff;
    }
    // $ANTLR end "methodAccess"


    // $ANTLR start "arrayAccess"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:284:1: arrayAccess returns [Suffix suff] : '[' tmp= expression ']' ;
    public final Suffix arrayAccess() throws RecognitionException {
        Suffix suff = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:288:2: ( '[' tmp= expression ']' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:288:4: '[' tmp= expression ']'
            {
            match(input,45,FOLLOW_45_in_arrayAccess1172); 
            pushFollow(FOLLOW_expression_in_arrayAccess1178);
            tmp=expression();

            state._fsp--;

            match(input,46,FOLLOW_46_in_arrayAccess1180); 

            		suff = new ArrayAccess(tmp);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return suff;
    }
    // $ANTLR end "arrayAccess"


    // $ANTLR start "variable"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:294:1: variable returns [Expression exp] : tmp= IDENTIFIER ;
    public final Expression variable() throws RecognitionException {
        Expression exp = null;

        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:298:2: (tmp= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:298:4: tmp= IDENTIFIER
            {
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variable1204); 

            		String	name	= tmp.getText();
            		Variable	var	= helper.getVariable(name);
            		if(var==null)
            			throw new RuntimeException("No such variable: "+name);
            		exp = new VariableExpression(var);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return exp;
    }
    // $ANTLR end "variable"


    // $ANTLR start "pseudovariable"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:308:1: pseudovariable returns [Expression exp] : tmp= IDENTIFIER '.' tmp2= IDENTIFIER ;
    public final Expression pseudovariable() throws RecognitionException {
        Expression exp = null;

        Token tmp=null;
        Token tmp2=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:312:2: (tmp= IDENTIFIER '.' tmp2= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:312:4: tmp= IDENTIFIER '.' tmp2= IDENTIFIER
            {
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_pseudovariable1228); 
            match(input,43,FOLLOW_43_in_pseudovariable1230); 
            tmp2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_pseudovariable1234); 

            		String name	= tmp.getText()+"."+tmp2.getText();
            		Variable	var	= helper.getVariable(name);
            		if(var==null)
            			throw new RuntimeException("No such variable: "+name);
            		exp = new VariableExpression(var);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return exp;
    }
    // $ANTLR end "pseudovariable"


    // $ANTLR start "literal"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:322:1: literal returns [Expression exp] : (tmp= floatingPointLiteral | tmp= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );
    public final Expression literal() throws RecognitionException {
        Expression exp = null;

        Token CharacterLiteral1=null;
        Token StringLiteral2=null;
        Token BooleanLiteral3=null;
        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:323:2: (tmp= floatingPointLiteral | tmp= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' )
            int alt19=6;
            switch ( input.LA(1) ) {
            case FloatingPointLiteral:
                {
                alt19=1;
                }
                break;
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
                {
                alt19=2;
                }
                break;
            case CharacterLiteral:
                {
                alt19=3;
                }
                break;
            case StringLiteral:
                {
                alt19=4;
                }
                break;
            case BooleanLiteral:
                {
                alt19=5;
                }
                break;
            case 47:
                {
                alt19=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:323:4: tmp= floatingPointLiteral
                    {
                    pushFollow(FOLLOW_floatingPointLiteral_in_literal1256);
                    tmp=floatingPointLiteral();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:324:4: tmp= integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal1267);
                    tmp=integerLiteral();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:325:4: CharacterLiteral
                    {
                    CharacterLiteral1=(Token)match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal1274); 
                    exp = new LiteralExpression(new Character((CharacterLiteral1!=null?CharacterLiteral1.getText():null).charAt(0)));

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:326:4: StringLiteral
                    {
                    StringLiteral2=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_literal1281); 
                    exp = new LiteralExpression((StringLiteral2!=null?StringLiteral2.getText():null).substring(1, (StringLiteral2!=null?StringLiteral2.getText():null).length()-1));

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:327:4: BooleanLiteral
                    {
                    BooleanLiteral3=(Token)match(input,BooleanLiteral,FOLLOW_BooleanLiteral_in_literal1288); 
                    exp = new LiteralExpression((BooleanLiteral3!=null?BooleanLiteral3.getText():null).equals("true")? Boolean.TRUE: Boolean.FALSE);

                    }
                    break;
                case 6 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:328:4: 'null'
                    {
                    match(input,47,FOLLOW_47_in_literal1295); 
                    exp = new LiteralExpression(null);

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
        return exp;
    }
    // $ANTLR end "literal"


    // $ANTLR start "floatingPointLiteral"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:331:1: floatingPointLiteral returns [Expression exp] : FloatingPointLiteral ;
    public final Expression floatingPointLiteral() throws RecognitionException {
        Expression exp = null;

        Token FloatingPointLiteral4=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:332:2: ( FloatingPointLiteral )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:332:4: FloatingPointLiteral
            {
            FloatingPointLiteral4=(Token)match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1312); 
            exp = new LiteralExpression(new Double((FloatingPointLiteral4!=null?FloatingPointLiteral4.getText():null)));

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return exp;
    }
    // $ANTLR end "floatingPointLiteral"


    // $ANTLR start "integerLiteral"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:335:1: integerLiteral returns [Expression exp] : ( HexLiteral | OctalLiteral | DecimalLiteral );
    public final Expression integerLiteral() throws RecognitionException {
        Expression exp = null;

        Token HexLiteral5=null;
        Token OctalLiteral6=null;
        Token DecimalLiteral7=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:336:2: ( HexLiteral | OctalLiteral | DecimalLiteral )
            int alt20=3;
            switch ( input.LA(1) ) {
            case HexLiteral:
                {
                alt20=1;
                }
                break;
            case OctalLiteral:
                {
                alt20=2;
                }
                break;
            case DecimalLiteral:
                {
                alt20=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:336:4: HexLiteral
                    {
                    HexLiteral5=(Token)match(input,HexLiteral,FOLLOW_HexLiteral_in_integerLiteral1329); 
                    exp = new LiteralExpression(new Integer((HexLiteral5!=null?HexLiteral5.getText():null)));

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:337:4: OctalLiteral
                    {
                    OctalLiteral6=(Token)match(input,OctalLiteral,FOLLOW_OctalLiteral_in_integerLiteral1336); 
                    exp = new LiteralExpression(new Integer((OctalLiteral6!=null?OctalLiteral6.getText():null)));

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:338:4: DecimalLiteral
                    {
                    DecimalLiteral7=(Token)match(input,DecimalLiteral,FOLLOW_DecimalLiteral_in_integerLiteral1343); 
                    exp = new LiteralExpression(new Integer((DecimalLiteral7!=null?DecimalLiteral7.getText():null)));

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
        return exp;
    }
    // $ANTLR end "integerLiteral"

    // Delegated rules


 

    public static final BitSet FOLLOW_expression_in_lhs48 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_lhs50 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression73 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_logicalOrExpression_in_conditionalExpression98 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_24_in_conditionalExpression121 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression127 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_conditionalExpression129 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_logicalAndExpression_in_logicalOrExpression179 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_logicalOrExpression202 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_logicalAndExpression_in_logicalOrExpression208 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_equalityExpression_in_logicalAndExpression252 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_27_in_logicalAndExpression275 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_equalityExpression_in_logicalAndExpression281 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression325 = new BitSet(new long[]{0x0000000030000002L});
    public static final BitSet FOLLOW_28_in_equalityExpression353 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_29_in_equalityExpression367 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression386 = new BitSet(new long[]{0x0000000030000002L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression422 = new BitSet(new long[]{0x00000003C0000002L});
    public static final BitSet FOLLOW_30_in_relationalExpression450 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_31_in_relationalExpression464 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_32_in_relationalExpression478 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_33_in_relationalExpression492 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression511 = new BitSet(new long[]{0x00000003C0000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression554 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_34_in_additiveExpression582 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_35_in_additiveExpression596 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression615 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression651 = new BitSet(new long[]{0x0000007000000002L});
    public static final BitSet FOLLOW_36_in_multiplicativeExpression679 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_37_in_multiplicativeExpression693 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_38_in_multiplicativeExpression707 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression726 = new BitSet(new long[]{0x0000007000000002L});
    public static final BitSet FOLLOW_34_in_unaryExpression759 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression765 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_unaryExpression772 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_unaryExpression785 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_unaryExpression798 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primaryPrefix_in_primaryExpression840 = new BitSet(new long[]{0x0000280000000002L});
    public static final BitSet FOLLOW_primarySuffix_in_primaryExpression850 = new BitSet(new long[]{0x0000280000000002L});
    public static final BitSet FOLLOW_41_in_primaryPrefix878 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_expression_in_primaryPrefix884 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_primaryPrefix886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primaryPrefix897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pseudovariable_in_primaryPrefix910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_primaryPrefix923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_staticField_in_primaryPrefix934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_staticField958 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_staticField960 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_staticField966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_type989 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_type1000 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_type1006 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_fieldAccess_in_primarySuffix1034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodAccess_in_primarySuffix1045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arrayAccess_in_primarySuffix1056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_fieldAccess1075 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_fieldAccess1081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_methodAccess1100 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodAccess1106 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_methodAccess1108 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_methodAccess1110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_methodAccess1117 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodAccess1123 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_methodAccess1125 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_expression_in_methodAccess1131 = new BitSet(new long[]{0x0000140000000000L});
    public static final BitSet FOLLOW_44_in_methodAccess1138 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_expression_in_methodAccess1144 = new BitSet(new long[]{0x0000140000000000L});
    public static final BitSet FOLLOW_42_in_methodAccess1152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_arrayAccess1172 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_expression_in_arrayAccess1178 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_46_in_arrayAccess1180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable1204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_pseudovariable1228 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_pseudovariable1230 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_pseudovariable1234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_floatingPointLiteral_in_literal1256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal1267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal1274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal1281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BooleanLiteral_in_literal1288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_literal1295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HexLiteral_in_integerLiteral1329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OctalLiteral_in_integerLiteral1336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DecimalLiteral_in_integerLiteral1343 = new BitSet(new long[]{0x0000000000000002L});

}