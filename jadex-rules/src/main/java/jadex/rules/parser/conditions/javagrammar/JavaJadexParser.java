// $ANTLR 3.1.2 C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g 2009-05-25 10:10:49

package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.functions.IFunction;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVJavaType;

import jadex.commons.SReflect;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


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



    // $ANTLR start "lhs"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:53:1: lhs returns [Expression exp] : tmp= expression EOF ;
    public final Expression lhs() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:57:2: (tmp= expression EOF )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:57:4: tmp= expression EOF
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:60:1: expression returns [Expression exp] : tmp= conditionalExpression ;
    public final Expression expression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:65:2: (tmp= conditionalExpression )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:65:4: tmp= conditionalExpression
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:70:1: conditionalExpression returns [Expression exp] : tmp= logicalOrExpression ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )? ;
    public final Expression conditionalExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;

        Expression tmp3 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:74:2: (tmp= logicalOrExpression ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:74:4: tmp= logicalOrExpression ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )?
            {
            pushFollow(FOLLOW_logicalOrExpression_in_conditionalExpression98);
            tmp=logicalOrExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:75:9: ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==24) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:76:10: '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:83:1: logicalOrExpression returns [Expression exp] : tmp= logicalAndExpression ( '||' tmp2= logicalAndExpression )* ;
    public final Expression logicalOrExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:87:2: (tmp= logicalAndExpression ( '||' tmp2= logicalAndExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:87:4: tmp= logicalAndExpression ( '||' tmp2= logicalAndExpression )*
            {
            pushFollow(FOLLOW_logicalAndExpression_in_logicalOrExpression179);
            tmp=logicalAndExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:88:9: ( '||' tmp2= logicalAndExpression )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==26) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:89:10: '||' tmp2= logicalAndExpression
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:96:1: logicalAndExpression returns [Expression exp] : tmp= equalityExpression ( '&&' tmp2= equalityExpression )* ;
    public final Expression logicalAndExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:100:2: (tmp= equalityExpression ( '&&' tmp2= equalityExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:100:4: tmp= equalityExpression ( '&&' tmp2= equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_logicalAndExpression252);
            tmp=equalityExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:101:9: ( '&&' tmp2= equalityExpression )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==27) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:102:10: '&&' tmp2= equalityExpression
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:109:1: equalityExpression returns [Expression exp] : tmp= relationalExpression ( ( '==' | '!=' ) tmp2= relationalExpression )* ;
    public final Expression equalityExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        IOperator	operator = null;
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:114:2: (tmp= relationalExpression ( ( '==' | '!=' ) tmp2= relationalExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:114:4: tmp= relationalExpression ( ( '==' | '!=' ) tmp2= relationalExpression )*
            {
            pushFollow(FOLLOW_relationalExpression_in_equalityExpression330);
            tmp=relationalExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:115:9: ( ( '==' | '!=' ) tmp2= relationalExpression )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0>=28 && LA5_0<=29)) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:116:10: ( '==' | '!=' ) tmp2= relationalExpression
            	    {
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:116:10: ( '==' | '!=' )
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
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:116:11: '=='
            	            {
            	            match(input,28,FOLLOW_28_in_equalityExpression354); 
            	            operator=IOperator.EQUAL;

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:117:11: '!='
            	            {
            	            match(input,29,FOLLOW_29_in_equalityExpression368); 
            	            operator=IOperator.NOTEQUAL;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_relationalExpression_in_equalityExpression387);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:125:1: relationalExpression returns [Expression exp] : tmp= additiveExpression ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )* ;
    public final Expression relationalExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        IOperator	operator = null;
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:130:2: (tmp= additiveExpression ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:130:4: tmp= additiveExpression ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_relationalExpression428);
            tmp=additiveExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:131:9: ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>=30 && LA7_0<=33)) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:132:10: ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression
            	    {
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:132:10: ( '<' | '<=' | '>' | '>=' )
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
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:132:11: '<'
            	            {
            	            match(input,30,FOLLOW_30_in_relationalExpression452); 
            	            operator=IOperator.LESS;

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:133:11: '<='
            	            {
            	            match(input,31,FOLLOW_31_in_relationalExpression466); 
            	            operator=IOperator.LESSOREQUAL;

            	            }
            	            break;
            	        case 3 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:134:11: '>'
            	            {
            	            match(input,32,FOLLOW_32_in_relationalExpression480); 
            	            operator=IOperator.GREATER;

            	            }
            	            break;
            	        case 4 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:135:11: '>='
            	            {
            	            match(input,33,FOLLOW_33_in_relationalExpression494); 
            	            operator=IOperator.GREATEROREQUAL;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_additiveExpression_in_relationalExpression513);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:143:1: additiveExpression returns [Expression exp] : tmp= multiplicativeExpression ( ( '+' | '-' ) tmp2= multiplicativeExpression )* ;
    public final Expression additiveExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        IFunction	operator = null;
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:148:2: (tmp= multiplicativeExpression ( ( '+' | '-' ) tmp2= multiplicativeExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:148:4: tmp= multiplicativeExpression ( ( '+' | '-' ) tmp2= multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression561);
            tmp=multiplicativeExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:149:9: ( ( '+' | '-' ) tmp2= multiplicativeExpression )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>=34 && LA9_0<=35)) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:150:10: ( '+' | '-' ) tmp2= multiplicativeExpression
            	    {
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:150:10: ( '+' | '-' )
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
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:150:11: '+'
            	            {
            	            match(input,34,FOLLOW_34_in_additiveExpression585); 
            	            operator=IFunction.SUM;

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:151:11: '-'
            	            {
            	            match(input,35,FOLLOW_35_in_additiveExpression599); 
            	            operator=IFunction.SUB;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression618);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:159:1: multiplicativeExpression returns [Expression exp] : tmp= unaryExpression ( ( '*' | '/' | '%' ) tmp2= unaryExpression )* ;
    public final Expression multiplicativeExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        IFunction	operator = null;
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:164:2: (tmp= unaryExpression ( ( '*' | '/' | '%' ) tmp2= unaryExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:164:4: tmp= unaryExpression ( ( '*' | '/' | '%' ) tmp2= unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression659);
            tmp=unaryExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:165:9: ( ( '*' | '/' | '%' ) tmp2= unaryExpression )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>=36 && LA11_0<=38)) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:166:10: ( '*' | '/' | '%' ) tmp2= unaryExpression
            	    {
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:166:10: ( '*' | '/' | '%' )
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
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:166:11: '*'
            	            {
            	            match(input,36,FOLLOW_36_in_multiplicativeExpression683); 
            	            operator=IFunction.MULT;

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:167:11: '/'
            	            {
            	            match(input,37,FOLLOW_37_in_multiplicativeExpression697); 
            	            operator=IFunction.DIV;

            	            }
            	            break;
            	        case 3 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:168:11: '%'
            	            {
            	            match(input,38,FOLLOW_38_in_multiplicativeExpression711); 
            	            operator=IFunction.MOD;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression730);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:176:1: unaryExpression returns [Expression exp] : ( '+' tmp= unaryExpression | '-' tmp= unaryExpression | '!' tmp= unaryExpression | '~' tmp= unaryExpression | {...}? '(' tmp1= type ')' tmp2= unaryExpression | {...}?tmp= primaryExpression );
    public final Expression unaryExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        OAVObjectType tmp1 = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:180:2: ( '+' tmp= unaryExpression | '-' tmp= unaryExpression | '!' tmp= unaryExpression | '~' tmp= unaryExpression | {...}? '(' tmp1= type ')' tmp2= unaryExpression | {...}?tmp= primaryExpression )
            int alt12=6;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:180:4: '+' tmp= unaryExpression
                    {
                    match(input,34,FOLLOW_34_in_unaryExpression763); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression769);
                    tmp=unaryExpression();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:181:4: '-' tmp= unaryExpression
                    {
                    match(input,35,FOLLOW_35_in_unaryExpression776); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression782);
                    tmp=unaryExpression();

                    state._fsp--;

                    exp = new UnaryExpression(tmp, UnaryExpression.OPERATOR_MINUS);

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:182:4: '!' tmp= unaryExpression
                    {
                    match(input,39,FOLLOW_39_in_unaryExpression789); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression795);
                    tmp=unaryExpression();

                    state._fsp--;

                    exp = new UnaryExpression(tmp, UnaryExpression.OPERATOR_NOT);

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:183:4: '~' tmp= unaryExpression
                    {
                    match(input,40,FOLLOW_40_in_unaryExpression802); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression808);
                    tmp=unaryExpression();

                    state._fsp--;

                    exp = new UnaryExpression(tmp, UnaryExpression.OPERATOR_BNOT);

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:184:4: {...}? '(' tmp1= type ')' tmp2= unaryExpression
                    {
                    if ( !((SJavaParser.lookaheadCast(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports))) ) {
                        throw new FailedPredicateException(input, "unaryExpression", "SJavaParser.lookaheadCast(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)");
                    }
                    match(input,41,FOLLOW_41_in_unaryExpression817); 
                    pushFollow(FOLLOW_type_in_unaryExpression823);
                    tmp1=type();

                    state._fsp--;

                    match(input,42,FOLLOW_42_in_unaryExpression825); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression831);
                    tmp2=unaryExpression();

                    state._fsp--;

                    exp = new CastExpression(tmp1, tmp2);

                    }
                    break;
                case 6 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:185:4: {...}?tmp= primaryExpression
                    {
                    if ( !((!SJavaParser.lookaheadCast(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports))) ) {
                        throw new FailedPredicateException(input, "unaryExpression", "!SJavaParser.lookaheadCast(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)");
                    }
                    pushFollow(FOLLOW_primaryExpression_in_unaryExpression844);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:188:1: primaryExpression returns [Expression exp] : tmp= primaryPrefix (tmp2= primarySuffix )* ;
    public final Expression primaryExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Suffix tmp2 = null;


        List suffs = null;
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:193:2: (tmp= primaryPrefix (tmp2= primarySuffix )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:193:4: tmp= primaryPrefix (tmp2= primarySuffix )*
            {
            pushFollow(FOLLOW_primaryPrefix_in_primaryExpression873);
            tmp=primaryPrefix();

            state._fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:194:2: (tmp2= primarySuffix )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==43||LA13_0==45) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:194:3: tmp2= primarySuffix
            	    {
            	    pushFollow(FOLLOW_primarySuffix_in_primaryExpression881);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:209:1: primaryPrefix returns [Expression exp] : ( '(' tmp= expression ')' | tmp= literal | {...}?tmp= typePrimary | {...}?tmp= nontypePrimary );
    public final Expression primaryPrefix() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:213:2: ( '(' tmp= expression ')' | tmp= literal | {...}?tmp= typePrimary | {...}?tmp= nontypePrimary )
            int alt14=4;
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
                switch ( input.LA(2) ) {
                case 43:
                    {
                    int LA14_4 = input.LA(3);

                    if ( (LA14_4==IDENTIFIER) ) {
                        int LA14_7 = input.LA(4);

                        if ( ((((SJavaParser.lookaheadType(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)!=-1)&&(SJavaParser.lookaheadStaticMethod(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)))||((SJavaParser.lookaheadType(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)!=-1)&&(SJavaParser.lookaheadExistential(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)))||((SJavaParser.lookaheadType(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)!=-1)&&(SJavaParser.lookaheadStaticField(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports))))) ) {
                            alt14=3;
                        }
                        else if ( ((((SJavaParser.lookaheadType(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)==-1)&&(helper.getVariable(JavaJadexParser.this.input.LT(1).getText())!=null))||((SJavaParser.lookaheadType(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)==-1)&&(helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText()) && !"(".equals(JavaJadexParser.this.input.LT(4).getText()))))) ) {
                            alt14=4;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 14, 7, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 14, 4, input);

                        throw nvae;
                    }
                    }
                    break;
                case EOF:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 42:
                case 44:
                case 45:
                case 46:
                    {
                    alt14=4;
                    }
                    break;
                case IDENTIFIER:
                    {
                    alt14=3;
                    }
                    break;
                default:
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:213:4: '(' tmp= expression ')'
                    {
                    match(input,41,FOLLOW_41_in_primaryPrefix909); 
                    pushFollow(FOLLOW_expression_in_primaryPrefix915);
                    tmp=expression();

                    state._fsp--;

                    match(input,42,FOLLOW_42_in_primaryPrefix917); 
                    exp = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:214:4: tmp= literal
                    {
                    pushFollow(FOLLOW_literal_in_primaryPrefix928);
                    tmp=literal();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:215:4: {...}?tmp= typePrimary
                    {
                    if ( !((SJavaParser.lookaheadType(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)!=-1)) ) {
                        throw new FailedPredicateException(input, "primaryPrefix", "SJavaParser.lookaheadType(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)!=-1");
                    }
                    pushFollow(FOLLOW_typePrimary_in_primaryPrefix941);
                    tmp=typePrimary();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:216:4: {...}?tmp= nontypePrimary
                    {
                    if ( !((SJavaParser.lookaheadType(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)==-1)) ) {
                        throw new FailedPredicateException(input, "primaryPrefix", "SJavaParser.lookaheadType(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)==-1");
                    }
                    pushFollow(FOLLOW_nontypePrimary_in_primaryPrefix954);
                    tmp=nontypePrimary();

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


    // $ANTLR start "typePrimary"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:219:1: typePrimary returns [Expression exp] : ({...}?tmp= staticMethod | {...}?tmp= staticField | {...}?tmp= existentialDeclaration );
    public final Expression typePrimary() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:223:2: ({...}?tmp= staticMethod | {...}?tmp= staticField | {...}?tmp= existentialDeclaration )
            int alt15=3;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==IDENTIFIER) ) {
                int LA15_1 = input.LA(2);

                if ( ((SJavaParser.lookaheadStaticMethod(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports))) ) {
                    alt15=1;
                }
                else if ( ((SJavaParser.lookaheadStaticField(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports))) ) {
                    alt15=2;
                }
                else if ( ((SJavaParser.lookaheadExistential(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports))) ) {
                    alt15=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:223:4: {...}?tmp= staticMethod
                    {
                    if ( !((SJavaParser.lookaheadStaticMethod(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports))) ) {
                        throw new FailedPredicateException(input, "typePrimary", "SJavaParser.lookaheadStaticMethod(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)");
                    }
                    pushFollow(FOLLOW_staticMethod_in_typePrimary979);
                    tmp=staticMethod();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:224:4: {...}?tmp= staticField
                    {
                    if ( !((SJavaParser.lookaheadStaticField(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports))) ) {
                        throw new FailedPredicateException(input, "typePrimary", "SJavaParser.lookaheadStaticField(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)");
                    }
                    pushFollow(FOLLOW_staticField_in_typePrimary992);
                    tmp=staticField();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:225:4: {...}?tmp= existentialDeclaration
                    {
                    if ( !((SJavaParser.lookaheadExistential(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports))) ) {
                        throw new FailedPredicateException(input, "typePrimary", "SJavaParser.lookaheadExistential(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)");
                    }
                    pushFollow(FOLLOW_existentialDeclaration_in_typePrimary1005);
                    tmp=existentialDeclaration();

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
    // $ANTLR end "typePrimary"


    // $ANTLR start "nontypePrimary"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:228:1: nontypePrimary returns [Expression exp] : ({...}?tmp= pseudovariable | {...}?tmp= variable );
    public final Expression nontypePrimary() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:232:2: ({...}?tmp= pseudovariable | {...}?tmp= variable )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==IDENTIFIER) ) {
                int LA16_1 = input.LA(2);

                if ( (LA16_1==43) ) {
                    int LA16_2 = input.LA(3);

                    if ( (LA16_2==IDENTIFIER) ) {
                        int LA16_4 = input.LA(4);

                        if ( ((helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText()) && !"(".equals(JavaJadexParser.this.input.LT(4).getText()))) ) {
                            alt16=1;
                        }
                        else if ( ((helper.getVariable(JavaJadexParser.this.input.LT(1).getText())!=null)) ) {
                            alt16=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 16, 4, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 16, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA16_1==EOF||(LA16_1>=24 && LA16_1<=38)||LA16_1==42||(LA16_1>=44 && LA16_1<=46)) ) {
                    alt16=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:232:4: {...}?tmp= pseudovariable
                    {
                    if ( !((helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText()) && !"(".equals(JavaJadexParser.this.input.LT(4).getText()))) ) {
                        throw new FailedPredicateException(input, "nontypePrimary", "helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText()) && !\"(\".equals(JavaJadexParser.this.input.LT(4).getText())");
                    }
                    pushFollow(FOLLOW_pseudovariable_in_nontypePrimary1030);
                    tmp=pseudovariable();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:233:4: {...}?tmp= variable
                    {
                    if ( !((helper.getVariable(JavaJadexParser.this.input.LT(1).getText())!=null)) ) {
                        throw new FailedPredicateException(input, "nontypePrimary", "helper.getVariable(JavaJadexParser.this.input.LT(1).getText())!=null");
                    }
                    pushFollow(FOLLOW_variable_in_nontypePrimary1043);
                    tmp=variable();

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
    // $ANTLR end "nontypePrimary"


    // $ANTLR start "staticMethod"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:236:1: staticMethod returns [Expression exp] : (otype= type '.' tmp1= IDENTIFIER '(' ')' | otype= type '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')' );
    public final Expression staticMethod() throws RecognitionException {
        Expression exp = null;

        Token tmp1=null;
        Token tmp2=null;
        OAVObjectType otype = null;

        Expression p1 = null;

        Expression p2 = null;


        List params = new ArrayList();
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:241:2: (otype= type '.' tmp1= IDENTIFIER '(' ')' | otype= type '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')' )
            int alt18=2;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:241:4: otype= type '.' tmp1= IDENTIFIER '(' ')'
                    {
                    pushFollow(FOLLOW_type_in_staticMethod1071);
                    otype=type();

                    state._fsp--;

                    match(input,43,FOLLOW_43_in_staticMethod1073); 
                    tmp1=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_staticMethod1079); 
                    match(input,41,FOLLOW_41_in_staticMethod1081); 
                    match(input,42,FOLLOW_42_in_staticMethod1083); 
                    exp = new StaticMethodAccess((OAVJavaType)otype, tmp1.getText(), null);

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:242:4: otype= type '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')'
                    {
                    pushFollow(FOLLOW_type_in_staticMethod1094);
                    otype=type();

                    state._fsp--;

                    match(input,43,FOLLOW_43_in_staticMethod1096); 
                    tmp2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_staticMethod1102); 
                    match(input,41,FOLLOW_41_in_staticMethod1104); 
                    pushFollow(FOLLOW_expression_in_staticMethod1110);
                    p1=expression();

                    state._fsp--;

                    params.add(p1);
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:243:2: ( ',' p2= expression )*
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==44) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:243:3: ',' p2= expression
                    	    {
                    	    match(input,44,FOLLOW_44_in_staticMethod1116); 
                    	    pushFollow(FOLLOW_expression_in_staticMethod1122);
                    	    p2=expression();

                    	    state._fsp--;

                    	    params.add(p2);

                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);

                    match(input,42,FOLLOW_42_in_staticMethod1130); 

                    		exp = new StaticMethodAccess((OAVJavaType)otype, tmp2.getText(), (Expression[])params.toArray(new Expression[params.size()]));
                    	

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
    // $ANTLR end "staticMethod"


    // $ANTLR start "staticField"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:250:1: staticField returns [Expression exp] : otype= type '.' field= IDENTIFIER ;
    public final Expression staticField() throws RecognitionException {
        Expression exp = null;

        Token field=null;
        OAVObjectType otype = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:254:2: (otype= type '.' field= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:254:4: otype= type '.' field= IDENTIFIER
            {
            pushFollow(FOLLOW_type_in_staticField1154);
            otype=type();

            state._fsp--;

            match(input,43,FOLLOW_43_in_staticField1156); 
            field=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_staticField1162); 

            		try
            		{
            			Class	clazz	= ((OAVJavaType)otype).getClazz();
            			Field	f	= clazz.getField(field.getText());
            			exp = new LiteralExpression(f.get(null));
            			if((f.getModifiers()&Modifier.FINAL)==0)
            				System.out.println("Warning: static field should be final: "+clazz+", "+field.getText());
            		}
            		catch(Exception e)
            		{
            			throw new RuntimeException(e);
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
    // $ANTLR end "staticField"


    // $ANTLR start "existentialDeclaration"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:271:1: existentialDeclaration returns [Expression exp] : otype= type varname= IDENTIFIER ;
    public final Expression existentialDeclaration() throws RecognitionException {
        Expression exp = null;

        Token varname=null;
        OAVObjectType otype = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:275:2: (otype= type varname= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:275:4: otype= type varname= IDENTIFIER
            {
            pushFollow(FOLLOW_type_in_existentialDeclaration1186);
            otype=type();

            state._fsp--;

            varname=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_existentialDeclaration1192); 

            		Variable	var	= new Variable(varname.getText(), otype);
            		exp = new ExistentialDeclaration(otype, var);
            		helper.addVariable(var);
            	

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
    // $ANTLR end "existentialDeclaration"


    // $ANTLR start "type"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:283:1: type returns [OAVObjectType otype] : tmp= IDENTIFIER ({...}? '.' tmp2= IDENTIFIER )* ;
    public final OAVObjectType type() throws RecognitionException {
        OAVObjectType otype = null;

        Token tmp=null;
        Token tmp2=null;

        String name = null;
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:288:2: (tmp= IDENTIFIER ({...}? '.' tmp2= IDENTIFIER )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:288:4: tmp= IDENTIFIER ({...}? '.' tmp2= IDENTIFIER )*
            {
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_type1221); 

            		name	= tmp.getText();
            		try
            		{
            			otype = helper.getBuildContext().getTypeModel().getObjectType(name);
            		}
            		catch(Exception e)
            		{
            			Class	clazz	= SReflect.findClass0(name, imports, helper.getBuildContext().getTypeModel().getClassLoader());
            			if(clazz!=null)
            				otype = helper.getBuildContext().getTypeModel().getJavaType(clazz);
            		}
            	
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:302:2: ({...}? '.' tmp2= IDENTIFIER )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==43) ) {
                    int LA19_2 = input.LA(2);

                    if ( ((otype==null)) ) {
                        alt19=1;
                    }


                }


                switch (alt19) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:302:4: {...}? '.' tmp2= IDENTIFIER
            	    {
            	    if ( !((otype==null)) ) {
            	        throw new FailedPredicateException(input, "type", "$otype==null");
            	    }
            	    match(input,43,FOLLOW_43_in_type1233); 
            	    tmp2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_type1239); 

            	    			name += "."+tmp2.getText();
            	     			Class	clazz	= SReflect.findClass0(name, imports, helper.getBuildContext().getTypeModel().getClassLoader());
            	    			if(clazz!=null)
            	    				otype = helper.getBuildContext().getTypeModel().getJavaType(clazz);
            	     		

            	    }
            	    break;

            	default :
            	    break loop19;
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
        return otype;
    }
    // $ANTLR end "type"


    // $ANTLR start "primarySuffix"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:313:1: primarySuffix returns [Suffix suff] : (tmp= fieldAccess | tmp= methodAccess | tmp= arrayAccess );
    public final Suffix primarySuffix() throws RecognitionException {
        Suffix suff = null;

        Suffix tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:317:2: (tmp= fieldAccess | tmp= methodAccess | tmp= arrayAccess )
            int alt20=3;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==43) ) {
                int LA20_1 = input.LA(2);

                if ( (LA20_1==IDENTIFIER) ) {
                    int LA20_3 = input.LA(3);

                    if ( (LA20_3==41) ) {
                        alt20=2;
                    }
                    else if ( (LA20_3==EOF||(LA20_3>=24 && LA20_3<=38)||(LA20_3>=42 && LA20_3<=46)) ) {
                        alt20=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 20, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA20_0==45) ) {
                alt20=3;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:317:4: tmp= fieldAccess
                    {
                    pushFollow(FOLLOW_fieldAccess_in_primarySuffix1269);
                    tmp=fieldAccess();

                    state._fsp--;

                    suff = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:318:4: tmp= methodAccess
                    {
                    pushFollow(FOLLOW_methodAccess_in_primarySuffix1280);
                    tmp=methodAccess();

                    state._fsp--;

                    suff = tmp;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:319:4: tmp= arrayAccess
                    {
                    pushFollow(FOLLOW_arrayAccess_in_primarySuffix1291);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:322:1: fieldAccess returns [Suffix suff] : '.' tmp= IDENTIFIER ;
    public final Suffix fieldAccess() throws RecognitionException {
        Suffix suff = null;

        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:326:2: ( '.' tmp= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:326:4: '.' tmp= IDENTIFIER
            {
            match(input,43,FOLLOW_43_in_fieldAccess1310); 
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_fieldAccess1316); 
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:329:1: methodAccess returns [Suffix suff] : ( '.' tmp1= IDENTIFIER '(' ')' | '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')' );
    public final Suffix methodAccess() throws RecognitionException {
        Suffix suff = null;

        Token tmp1=null;
        Token tmp2=null;
        Expression p1 = null;

        Expression p2 = null;


        List params = new ArrayList();
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:334:2: ( '.' tmp1= IDENTIFIER '(' ')' | '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')' )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==43) ) {
                int LA22_1 = input.LA(2);

                if ( (LA22_1==IDENTIFIER) ) {
                    int LA22_2 = input.LA(3);

                    if ( (LA22_2==41) ) {
                        int LA22_3 = input.LA(4);

                        if ( (LA22_3==42) ) {
                            alt22=1;
                        }
                        else if ( ((LA22_3>=IDENTIFIER && LA22_3<=DecimalLiteral)||(LA22_3>=34 && LA22_3<=35)||(LA22_3>=39 && LA22_3<=41)||LA22_3==47) ) {
                            alt22=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 22, 3, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 22, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 22, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:334:4: '.' tmp1= IDENTIFIER '(' ')'
                    {
                    match(input,43,FOLLOW_43_in_methodAccess1340); 
                    tmp1=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodAccess1346); 
                    match(input,41,FOLLOW_41_in_methodAccess1348); 
                    match(input,42,FOLLOW_42_in_methodAccess1350); 
                    suff = new MethodAccess(tmp1.getText(), null);

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:335:4: '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')'
                    {
                    match(input,43,FOLLOW_43_in_methodAccess1357); 
                    tmp2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodAccess1363); 
                    match(input,41,FOLLOW_41_in_methodAccess1365); 
                    pushFollow(FOLLOW_expression_in_methodAccess1371);
                    p1=expression();

                    state._fsp--;

                    params.add(p1);
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:336:2: ( ',' p2= expression )*
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( (LA21_0==44) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:336:3: ',' p2= expression
                    	    {
                    	    match(input,44,FOLLOW_44_in_methodAccess1377); 
                    	    pushFollow(FOLLOW_expression_in_methodAccess1383);
                    	    p2=expression();

                    	    state._fsp--;

                    	    params.add(p2);

                    	    }
                    	    break;

                    	default :
                    	    break loop21;
                        }
                    } while (true);

                    match(input,42,FOLLOW_42_in_methodAccess1391); 

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:343:1: arrayAccess returns [Suffix suff] : '[' tmp= expression ']' ;
    public final Suffix arrayAccess() throws RecognitionException {
        Suffix suff = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:347:2: ( '[' tmp= expression ']' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:347:4: '[' tmp= expression ']'
            {
            match(input,45,FOLLOW_45_in_arrayAccess1411); 
            pushFollow(FOLLOW_expression_in_arrayAccess1417);
            tmp=expression();

            state._fsp--;

            match(input,46,FOLLOW_46_in_arrayAccess1419); 

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:353:1: variable returns [Expression exp] : tmp= IDENTIFIER ;
    public final Expression variable() throws RecognitionException {
        Expression exp = null;

        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:357:2: (tmp= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:357:4: tmp= IDENTIFIER
            {
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variable1443); 

            		String	name	= tmp.getText();
            		Variable	var	= helper.getVariable(name);
            		if(var==null)
            		{
            			throw new RuntimeException("No such variable: "+name);
            		}
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:369:1: pseudovariable returns [Expression exp] : tmp= IDENTIFIER '.' tmp2= IDENTIFIER ;
    public final Expression pseudovariable() throws RecognitionException {
        Expression exp = null;

        Token tmp=null;
        Token tmp2=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:373:2: (tmp= IDENTIFIER '.' tmp2= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:373:4: tmp= IDENTIFIER '.' tmp2= IDENTIFIER
            {
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_pseudovariable1467); 
            match(input,43,FOLLOW_43_in_pseudovariable1469); 
            tmp2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_pseudovariable1473); 

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:383:1: literal returns [Expression exp] : (tmp= floatingPointLiteral | tmp= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );
    public final Expression literal() throws RecognitionException {
        Expression exp = null;

        Token CharacterLiteral1=null;
        Token StringLiteral2=null;
        Token BooleanLiteral3=null;
        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:384:2: (tmp= floatingPointLiteral | tmp= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' )
            int alt23=6;
            switch ( input.LA(1) ) {
            case FloatingPointLiteral:
                {
                alt23=1;
                }
                break;
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
                {
                alt23=2;
                }
                break;
            case CharacterLiteral:
                {
                alt23=3;
                }
                break;
            case StringLiteral:
                {
                alt23=4;
                }
                break;
            case BooleanLiteral:
                {
                alt23=5;
                }
                break;
            case 47:
                {
                alt23=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }

            switch (alt23) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:384:4: tmp= floatingPointLiteral
                    {
                    pushFollow(FOLLOW_floatingPointLiteral_in_literal1495);
                    tmp=floatingPointLiteral();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:385:4: tmp= integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal1506);
                    tmp=integerLiteral();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:386:4: CharacterLiteral
                    {
                    CharacterLiteral1=(Token)match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal1513); 
                    exp = new LiteralExpression(new Character((CharacterLiteral1!=null?CharacterLiteral1.getText():null).charAt(0)));

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:387:4: StringLiteral
                    {
                    StringLiteral2=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_literal1520); 
                    exp = new LiteralExpression((StringLiteral2!=null?StringLiteral2.getText():null).substring(1, (StringLiteral2!=null?StringLiteral2.getText():null).length()-1));

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:388:4: BooleanLiteral
                    {
                    BooleanLiteral3=(Token)match(input,BooleanLiteral,FOLLOW_BooleanLiteral_in_literal1527); 
                    exp = new LiteralExpression((BooleanLiteral3!=null?BooleanLiteral3.getText():null).equals("true")? Boolean.TRUE: Boolean.FALSE);

                    }
                    break;
                case 6 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:389:4: 'null'
                    {
                    match(input,47,FOLLOW_47_in_literal1534); 
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:392:1: floatingPointLiteral returns [Expression exp] : FloatingPointLiteral ;
    public final Expression floatingPointLiteral() throws RecognitionException {
        Expression exp = null;

        Token FloatingPointLiteral4=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:393:2: ( FloatingPointLiteral )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:393:4: FloatingPointLiteral
            {
            FloatingPointLiteral4=(Token)match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1551); 
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:396:1: integerLiteral returns [Expression exp] : ( HexLiteral | OctalLiteral | DecimalLiteral );
    public final Expression integerLiteral() throws RecognitionException {
        Expression exp = null;

        Token HexLiteral5=null;
        Token OctalLiteral6=null;
        Token DecimalLiteral7=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:397:2: ( HexLiteral | OctalLiteral | DecimalLiteral )
            int alt24=3;
            switch ( input.LA(1) ) {
            case HexLiteral:
                {
                alt24=1;
                }
                break;
            case OctalLiteral:
                {
                alt24=2;
                }
                break;
            case DecimalLiteral:
                {
                alt24=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:397:4: HexLiteral
                    {
                    HexLiteral5=(Token)match(input,HexLiteral,FOLLOW_HexLiteral_in_integerLiteral1568); 
                    exp = new LiteralExpression(new Integer((HexLiteral5!=null?HexLiteral5.getText():null)));

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:398:4: OctalLiteral
                    {
                    OctalLiteral6=(Token)match(input,OctalLiteral,FOLLOW_OctalLiteral_in_integerLiteral1575); 
                    exp = new LiteralExpression(new Integer((OctalLiteral6!=null?OctalLiteral6.getText():null)));

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:399:4: DecimalLiteral
                    {
                    DecimalLiteral7=(Token)match(input,DecimalLiteral,FOLLOW_DecimalLiteral_in_integerLiteral1582); 
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


    protected DFA12 dfa12 = new DFA12(this);
    protected DFA18 dfa18 = new DFA18(this);
    static final String DFA12_eotS =
        "\20\uffff";
    static final String DFA12_eofS =
        "\20\uffff";
    static final String DFA12_minS =
        "\1\4\4\uffff\1\0\12\uffff";
    static final String DFA12_maxS =
        "\1\57\4\uffff\1\0\12\uffff";
    static final String DFA12_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\uffff\1\6\10\uffff\1\5";
    static final String DFA12_specialS =
        "\5\uffff\1\0\12\uffff}>";
    static final String[] DFA12_transitionS = {
            "\10\6\26\uffff\1\1\1\2\3\uffff\1\3\1\4\1\5\5\uffff\1\6",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "176:1: unaryExpression returns [Expression exp] : ( '+' tmp= unaryExpression | '-' tmp= unaryExpression | '!' tmp= unaryExpression | '~' tmp= unaryExpression | {...}? '(' tmp1= type ')' tmp2= unaryExpression | {...}?tmp= primaryExpression );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA12_5 = input.LA(1);

                         
                        int index12_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((SJavaParser.lookaheadCast(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports))) ) {s = 15;}

                        else if ( ((!SJavaParser.lookaheadCast(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports))) ) {s = 6;}

                         
                        input.seek(index12_5);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 12, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA18_eotS =
        "\7\uffff";
    static final String DFA18_eofS =
        "\7\uffff";
    static final String DFA18_minS =
        "\1\4\1\53\1\4\1\51\1\4\2\uffff";
    static final String DFA18_maxS =
        "\1\4\1\53\1\4\1\53\1\57\2\uffff";
    static final String DFA18_acceptS =
        "\5\uffff\1\1\1\2";
    static final String DFA18_specialS =
        "\7\uffff}>";
    static final String[] DFA18_transitionS = {
            "\1\1",
            "\1\2",
            "\1\3",
            "\1\4\1\uffff\1\2",
            "\10\6\26\uffff\2\6\3\uffff\3\6\1\5\4\uffff\1\6",
            "",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }
        public String getDescription() {
            return "236:1: staticMethod returns [Expression exp] : (otype= type '.' tmp1= IDENTIFIER '(' ')' | otype= type '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')' );";
        }
    }
 

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
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression330 = new BitSet(new long[]{0x0000000030000002L});
    public static final BitSet FOLLOW_28_in_equalityExpression354 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_29_in_equalityExpression368 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression387 = new BitSet(new long[]{0x0000000030000002L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression428 = new BitSet(new long[]{0x00000003C0000002L});
    public static final BitSet FOLLOW_30_in_relationalExpression452 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_31_in_relationalExpression466 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_32_in_relationalExpression480 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_33_in_relationalExpression494 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression513 = new BitSet(new long[]{0x00000003C0000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression561 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_34_in_additiveExpression585 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_35_in_additiveExpression599 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression618 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression659 = new BitSet(new long[]{0x0000007000000002L});
    public static final BitSet FOLLOW_36_in_multiplicativeExpression683 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_37_in_multiplicativeExpression697 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_38_in_multiplicativeExpression711 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression730 = new BitSet(new long[]{0x0000007000000002L});
    public static final BitSet FOLLOW_34_in_unaryExpression763 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_unaryExpression776 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_unaryExpression789 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_unaryExpression802 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_unaryExpression817 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_type_in_unaryExpression823 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_unaryExpression825 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression831 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression844 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primaryPrefix_in_primaryExpression873 = new BitSet(new long[]{0x0000280000000002L});
    public static final BitSet FOLLOW_primarySuffix_in_primaryExpression881 = new BitSet(new long[]{0x0000280000000002L});
    public static final BitSet FOLLOW_41_in_primaryPrefix909 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_expression_in_primaryPrefix915 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_primaryPrefix917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primaryPrefix928 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typePrimary_in_primaryPrefix941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nontypePrimary_in_primaryPrefix954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_staticMethod_in_typePrimary979 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_staticField_in_typePrimary992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existentialDeclaration_in_typePrimary1005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pseudovariable_in_nontypePrimary1030 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_nontypePrimary1043 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_staticMethod1071 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_staticMethod1073 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_staticMethod1079 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_staticMethod1081 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_staticMethod1083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_staticMethod1094 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_staticMethod1096 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_staticMethod1102 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_staticMethod1104 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_expression_in_staticMethod1110 = new BitSet(new long[]{0x0000140000000000L});
    public static final BitSet FOLLOW_44_in_staticMethod1116 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_expression_in_staticMethod1122 = new BitSet(new long[]{0x0000140000000000L});
    public static final BitSet FOLLOW_42_in_staticMethod1130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_staticField1154 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_staticField1156 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_staticField1162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_existentialDeclaration1186 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_existentialDeclaration1192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_type1221 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_type1233 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_type1239 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_fieldAccess_in_primarySuffix1269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodAccess_in_primarySuffix1280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arrayAccess_in_primarySuffix1291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_fieldAccess1310 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_fieldAccess1316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_methodAccess1340 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodAccess1346 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_methodAccess1348 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_methodAccess1350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_methodAccess1357 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodAccess1363 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_methodAccess1365 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_expression_in_methodAccess1371 = new BitSet(new long[]{0x0000140000000000L});
    public static final BitSet FOLLOW_44_in_methodAccess1377 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_expression_in_methodAccess1383 = new BitSet(new long[]{0x0000140000000000L});
    public static final BitSet FOLLOW_42_in_methodAccess1391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_arrayAccess1411 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_expression_in_arrayAccess1417 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_46_in_arrayAccess1419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable1443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_pseudovariable1467 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_pseudovariable1469 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_pseudovariable1473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_floatingPointLiteral_in_literal1495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal1506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal1513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal1520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BooleanLiteral_in_literal1527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_literal1534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HexLiteral_in_integerLiteral1568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OctalLiteral_in_integerLiteral1575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DecimalLiteral_in_integerLiteral1582 = new BitSet(new long[]{0x0000000000000002L});

}