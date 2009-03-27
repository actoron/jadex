// $ANTLR 3.1.2 C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g 2009-03-27 16:55:31

package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.functions.IFunction;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class JavaJadexParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENTIFIER", "CharacterLiteral", "StringLiteral", "BooleanLiteral", "FloatingPointLiteral", "HexLiteral", "OctalLiteral", "DecimalLiteral", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "COMMENT", "LINE_COMMENT", "'?'", "':'", "'||'", "'&&'", "'=='", "'!='", "'<'", "'<='", "'>'", "'>='", "'+'", "'-'", "'*'", "'/'", "'%'", "'('", "')'", "'.'", "','", "'null'"
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
    public static final int LINE_COMMENT=23;
    public static final int IntegerTypeSuffix=13;
    public static final int DecimalLiteral=11;
    public static final int StringLiteral=6;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int WS=21;
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



    // $ANTLR start "lhs"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:35:1: lhs returns [Expression exp] : tmp= expression EOF ;
    public final Expression lhs() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:39:2: (tmp= expression EOF )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:39:4: tmp= expression EOF
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:42:1: expression returns [Expression exp] : tmp= conditionalExpression ;
    public final Expression expression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:47:2: (tmp= conditionalExpression )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:47:4: tmp= conditionalExpression
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:52:1: conditionalExpression returns [Expression exp] : tmp= logicalOrExpression ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )? ;
    public final Expression conditionalExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;

        Expression tmp3 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:56:2: (tmp= logicalOrExpression ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:56:4: tmp= logicalOrExpression ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )?
            {
            pushFollow(FOLLOW_logicalOrExpression_in_conditionalExpression98);
            tmp=logicalOrExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:57:9: ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==24) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:58:10: '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:65:1: logicalOrExpression returns [Expression exp] : tmp= logicalAndExpression ( '||' tmp2= logicalAndExpression )? ;
    public final Expression logicalOrExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:69:2: (tmp= logicalAndExpression ( '||' tmp2= logicalAndExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:69:4: tmp= logicalAndExpression ( '||' tmp2= logicalAndExpression )?
            {
            pushFollow(FOLLOW_logicalAndExpression_in_logicalOrExpression179);
            tmp=logicalAndExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:70:9: ( '||' tmp2= logicalAndExpression )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==26) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:71:10: '||' tmp2= logicalAndExpression
                    {
                    match(input,26,FOLLOW_26_in_logicalOrExpression202); 
                    pushFollow(FOLLOW_logicalAndExpression_in_logicalOrExpression208);
                    tmp2=logicalAndExpression();

                    state._fsp--;


                            		exp = new OperationExpression(tmp, tmp2, OperationExpression.OPERATOR_OR);
                            	

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
    // $ANTLR end "logicalOrExpression"


    // $ANTLR start "logicalAndExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:78:1: logicalAndExpression returns [Expression exp] : tmp= equalityExpression ( '&&' tmp2= equalityExpression )? ;
    public final Expression logicalAndExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:82:2: (tmp= equalityExpression ( '&&' tmp2= equalityExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:82:4: tmp= equalityExpression ( '&&' tmp2= equalityExpression )?
            {
            pushFollow(FOLLOW_equalityExpression_in_logicalAndExpression252);
            tmp=equalityExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:83:9: ( '&&' tmp2= equalityExpression )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==27) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:84:10: '&&' tmp2= equalityExpression
                    {
                    match(input,27,FOLLOW_27_in_logicalAndExpression275); 
                    pushFollow(FOLLOW_equalityExpression_in_logicalAndExpression281);
                    tmp2=equalityExpression();

                    state._fsp--;


                            		exp = new OperationExpression(tmp, tmp2, OperationExpression.OPERATOR_AND);
                            	

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
    // $ANTLR end "logicalAndExpression"


    // $ANTLR start "equalityExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:91:1: equalityExpression returns [Expression exp] : tmp= relationalExpression ( ( '==' | '!=' ) tmp2= relationalExpression )? ;
    public final Expression equalityExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:95:2: (tmp= relationalExpression ( ( '==' | '!=' ) tmp2= relationalExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:95:4: tmp= relationalExpression ( ( '==' | '!=' ) tmp2= relationalExpression )?
            {
            pushFollow(FOLLOW_relationalExpression_in_equalityExpression325);
            tmp=relationalExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:96:9: ( ( '==' | '!=' ) tmp2= relationalExpression )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( ((LA5_0>=28 && LA5_0<=29)) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:97:3: ( '==' | '!=' ) tmp2= relationalExpression
                    {

                    			IOperator	operator	= null;
                    		
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:100:10: ( '==' | '!=' )
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
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:100:11: '=='
                            {
                            match(input,28,FOLLOW_28_in_equalityExpression353); 
                            operator=IOperator.EQUAL;

                            }
                            break;
                        case 2 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:101:11: '!='
                            {
                            match(input,29,FOLLOW_29_in_equalityExpression367); 
                            operator=IOperator.NOTEQUAL;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_relationalExpression_in_equalityExpression386);
                    tmp2=relationalExpression();

                    state._fsp--;


                    	        	exp = new OperationExpression(tmp, tmp2, operator);
                    	        

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
    // $ANTLR end "equalityExpression"


    // $ANTLR start "relationalExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:109:1: relationalExpression returns [Expression exp] : tmp= additiveExpression ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )? ;
    public final Expression relationalExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:113:2: (tmp= additiveExpression ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:113:4: tmp= additiveExpression ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )?
            {
            pushFollow(FOLLOW_additiveExpression_in_relationalExpression422);
            tmp=additiveExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:114:9: ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( ((LA7_0>=30 && LA7_0<=33)) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:115:3: ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression
                    {

                    			IOperator	operator	= null;
                    		
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:118:10: ( '<' | '<=' | '>' | '>=' )
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
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:118:11: '<'
                            {
                            match(input,30,FOLLOW_30_in_relationalExpression450); 
                            operator=IOperator.LESS;

                            }
                            break;
                        case 2 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:119:11: '<='
                            {
                            match(input,31,FOLLOW_31_in_relationalExpression464); 
                            operator=IOperator.LESSOREQUAL;

                            }
                            break;
                        case 3 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:120:11: '>'
                            {
                            match(input,32,FOLLOW_32_in_relationalExpression478); 
                            operator=IOperator.GREATER;

                            }
                            break;
                        case 4 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:121:11: '>='
                            {
                            match(input,33,FOLLOW_33_in_relationalExpression492); 
                            operator=IOperator.GREATEROREQUAL;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_additiveExpression_in_relationalExpression511);
                    tmp2=additiveExpression();

                    state._fsp--;


                    	        	exp = new OperationExpression(tmp, tmp2, operator);
                    	        

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
    // $ANTLR end "relationalExpression"


    // $ANTLR start "additiveExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:129:1: additiveExpression returns [Expression exp] : tmp= multiplicativeExpression ( ( '+' | '-' ) tmp2= multiplicativeExpression )? ;
    public final Expression additiveExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:133:2: (tmp= multiplicativeExpression ( ( '+' | '-' ) tmp2= multiplicativeExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:133:4: tmp= multiplicativeExpression ( ( '+' | '-' ) tmp2= multiplicativeExpression )?
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression554);
            tmp=multiplicativeExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:134:9: ( ( '+' | '-' ) tmp2= multiplicativeExpression )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( ((LA9_0>=34 && LA9_0<=35)) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:135:3: ( '+' | '-' ) tmp2= multiplicativeExpression
                    {

                    			IFunction	operator	= null;
                    		
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:138:10: ( '+' | '-' )
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
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:138:11: '+'
                            {
                            match(input,34,FOLLOW_34_in_additiveExpression582); 
                            operator=IFunction.SUM;

                            }
                            break;
                        case 2 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:139:11: '-'
                            {
                            match(input,35,FOLLOW_35_in_additiveExpression596); 
                            operator=IFunction.SUB;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression615);
                    tmp2=multiplicativeExpression();

                    state._fsp--;


                    	        	exp = new OperationExpression(tmp, tmp2, operator);
                    	        

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
    // $ANTLR end "additiveExpression"


    // $ANTLR start "multiplicativeExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:147:1: multiplicativeExpression returns [Expression exp] : tmp= unaryExpression ( ( '*' | '/' | '%' ) tmp2= unaryExpression )? ;
    public final Expression multiplicativeExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:151:2: (tmp= unaryExpression ( ( '*' | '/' | '%' ) tmp2= unaryExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:151:4: tmp= unaryExpression ( ( '*' | '/' | '%' ) tmp2= unaryExpression )?
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression651);
            tmp=unaryExpression();

            state._fsp--;

            exp = tmp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:152:9: ( ( '*' | '/' | '%' ) tmp2= unaryExpression )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( ((LA11_0>=36 && LA11_0<=38)) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:153:3: ( '*' | '/' | '%' ) tmp2= unaryExpression
                    {

                    			IFunction	operator	= null;
                    		
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:156:10: ( '*' | '/' | '%' )
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
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:156:11: '*'
                            {
                            match(input,36,FOLLOW_36_in_multiplicativeExpression679); 
                            operator=IFunction.MULT;

                            }
                            break;
                        case 2 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:157:11: '/'
                            {
                            match(input,37,FOLLOW_37_in_multiplicativeExpression693); 
                            operator=IFunction.DIV;

                            }
                            break;
                        case 3 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:158:11: '%'
                            {
                            match(input,38,FOLLOW_38_in_multiplicativeExpression707); 
                            operator=IFunction.MOD;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression726);
                    tmp2=unaryExpression();

                    state._fsp--;


                    	        	exp = new OperationExpression(tmp, tmp2, operator);
                    	        

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
    // $ANTLR end "multiplicativeExpression"


    // $ANTLR start "unaryExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:166:1: unaryExpression returns [Expression exp] : tmp= primary (tmp2= suffix )* ;
    public final Expression unaryExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Suffix tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:170:2: (tmp= primary (tmp2= suffix )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:171:2: tmp= primary (tmp2= suffix )*
            {
            pushFollow(FOLLOW_primary_in_unaryExpression764);
            tmp=primary();

            state._fsp--;

            List suffs = null;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:172:2: (tmp2= suffix )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==41) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:172:3: tmp2= suffix
            	    {
            	    pushFollow(FOLLOW_suffix_in_unaryExpression774);
            	    tmp2=suffix();

            	    state._fsp--;


            	    			if(suffs==null)
            	    				suffs	= new ArrayList();
            	    			suffs.add(tmp2);
            	    		

            	    }
            	    break;

            	default :
            	    break loop12;
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
    // $ANTLR end "unaryExpression"


    // $ANTLR start "primary"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:187:1: primary returns [Expression exp] : ( '(' tmp= expression ')' | tmp= literal | {...}?tmp= pseudovariable | tmp= variable );
    public final Expression primary() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:191:2: ( '(' tmp= expression ')' | tmp= literal | {...}?tmp= pseudovariable | tmp= variable )
            int alt13=4;
            switch ( input.LA(1) ) {
            case 39:
                {
                alt13=1;
                }
                break;
            case CharacterLiteral:
            case StringLiteral:
            case BooleanLiteral:
            case FloatingPointLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 34:
            case 35:
            case 43:
                {
                alt13=2;
                }
                break;
            case IDENTIFIER:
                {
                int LA13_3 = input.LA(2);

                if ( (LA13_3==41) ) {
                    int LA13_4 = input.LA(3);

                    if ( (LA13_4==IDENTIFIER) ) {
                        int LA13_6 = input.LA(4);

                        if ( ((helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText()))) ) {
                            alt13=3;
                        }
                        else if ( (true) ) {
                            alt13=4;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 13, 6, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 13, 4, input);

                        throw nvae;
                    }
                }
                else if ( (LA13_3==EOF||(LA13_3>=24 && LA13_3<=38)||LA13_3==40||LA13_3==42) ) {
                    alt13=4;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:191:4: '(' tmp= expression ')'
                    {
                    match(input,39,FOLLOW_39_in_primary802); 
                    pushFollow(FOLLOW_expression_in_primary808);
                    tmp=expression();

                    state._fsp--;

                    match(input,40,FOLLOW_40_in_primary810); 
                    exp = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:192:4: tmp= literal
                    {
                    pushFollow(FOLLOW_literal_in_primary821);
                    tmp=literal();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:193:4: {...}?tmp= pseudovariable
                    {
                    if ( !((helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText()))) ) {
                        throw new FailedPredicateException(input, "primary", "helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText())");
                    }
                    pushFollow(FOLLOW_pseudovariable_in_primary834);
                    tmp=pseudovariable();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:194:4: tmp= variable
                    {
                    pushFollow(FOLLOW_variable_in_primary845);
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
    // $ANTLR end "primary"


    // $ANTLR start "suffix"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:197:1: suffix returns [Suffix suff] : (tmp= fieldAccess | tmp= methodAccess );
    public final Suffix suffix() throws RecognitionException {
        Suffix suff = null;

        Suffix tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:201:2: (tmp= fieldAccess | tmp= methodAccess )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==41) ) {
                int LA14_1 = input.LA(2);

                if ( (LA14_1==IDENTIFIER) ) {
                    int LA14_2 = input.LA(3);

                    if ( (LA14_2==39) ) {
                        alt14=2;
                    }
                    else if ( (LA14_2==EOF||(LA14_2>=24 && LA14_2<=38)||(LA14_2>=40 && LA14_2<=42)) ) {
                        alt14=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 14, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:201:4: tmp= fieldAccess
                    {
                    pushFollow(FOLLOW_fieldAccess_in_suffix868);
                    tmp=fieldAccess();

                    state._fsp--;

                    suff = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:202:4: tmp= methodAccess
                    {
                    pushFollow(FOLLOW_methodAccess_in_suffix879);
                    tmp=methodAccess();

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
    // $ANTLR end "suffix"


    // $ANTLR start "fieldAccess"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:205:1: fieldAccess returns [Suffix suff] : '.' tmp= IDENTIFIER ;
    public final Suffix fieldAccess() throws RecognitionException {
        Suffix suff = null;

        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:209:2: ( '.' tmp= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:209:4: '.' tmp= IDENTIFIER
            {
            match(input,41,FOLLOW_41_in_fieldAccess898); 
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_fieldAccess904); 
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:212:1: methodAccess returns [Suffix suff] : ( '.' tmp1= IDENTIFIER '(' ')' | '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')' );
    public final Suffix methodAccess() throws RecognitionException {
        Suffix suff = null;

        Token tmp1=null;
        Token tmp2=null;
        Expression p1 = null;

        Expression p2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:216:2: ( '.' tmp1= IDENTIFIER '(' ')' | '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')' )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==41) ) {
                int LA16_1 = input.LA(2);

                if ( (LA16_1==IDENTIFIER) ) {
                    int LA16_2 = input.LA(3);

                    if ( (LA16_2==39) ) {
                        int LA16_3 = input.LA(4);

                        if ( (LA16_3==40) ) {
                            alt16=1;
                        }
                        else if ( ((LA16_3>=IDENTIFIER && LA16_3<=DecimalLiteral)||(LA16_3>=34 && LA16_3<=35)||LA16_3==39||LA16_3==43) ) {
                            alt16=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 16, 3, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 16, 2, input);

                        throw nvae;
                    }
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:216:4: '.' tmp1= IDENTIFIER '(' ')'
                    {
                    match(input,41,FOLLOW_41_in_methodAccess923); 
                    tmp1=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodAccess929); 
                    match(input,39,FOLLOW_39_in_methodAccess931); 
                    match(input,40,FOLLOW_40_in_methodAccess933); 
                    suff = new MethodAccess(tmp1.getText(), null);

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:217:4: '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')'
                    {
                    match(input,41,FOLLOW_41_in_methodAccess940); 
                    tmp2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodAccess946); 
                    match(input,39,FOLLOW_39_in_methodAccess948); 
                    pushFollow(FOLLOW_expression_in_methodAccess954);
                    p1=expression();

                    state._fsp--;


                    		List params	= new ArrayList();
                    		params.add(p1);
                    	
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:222:2: ( ',' p2= expression )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==42) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:222:3: ',' p2= expression
                    	    {
                    	    match(input,42,FOLLOW_42_in_methodAccess961); 
                    	    pushFollow(FOLLOW_expression_in_methodAccess967);
                    	    p2=expression();

                    	    state._fsp--;

                    	    params.add(p2);

                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);

                    match(input,40,FOLLOW_40_in_methodAccess975); 

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


    // $ANTLR start "variable"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:229:1: variable returns [Expression exp] : tmp= IDENTIFIER ;
    public final Expression variable() throws RecognitionException {
        Expression exp = null;

        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:233:2: (tmp= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:233:4: tmp= IDENTIFIER
            {
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variable999); 

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:243:1: pseudovariable returns [Expression exp] : tmp= IDENTIFIER '.' tmp2= IDENTIFIER ;
    public final Expression pseudovariable() throws RecognitionException {
        Expression exp = null;

        Token tmp=null;
        Token tmp2=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:247:2: (tmp= IDENTIFIER '.' tmp2= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:247:4: tmp= IDENTIFIER '.' tmp2= IDENTIFIER
            {
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_pseudovariable1023); 
            match(input,41,FOLLOW_41_in_pseudovariable1025); 
            tmp2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_pseudovariable1029); 

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:257:1: literal returns [Expression exp] : (tmp= floatingPointLiteral | tmp= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );
    public final Expression literal() throws RecognitionException {
        Expression exp = null;

        Token CharacterLiteral1=null;
        Token StringLiteral2=null;
        Token BooleanLiteral3=null;
        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:258:2: (tmp= floatingPointLiteral | tmp= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' )
            int alt17=6;
            switch ( input.LA(1) ) {
            case 34:
            case 35:
                {
                int LA17_1 = input.LA(2);

                if ( ((LA17_1>=HexLiteral && LA17_1<=DecimalLiteral)) ) {
                    alt17=2;
                }
                else if ( (LA17_1==FloatingPointLiteral) ) {
                    alt17=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 17, 1, input);

                    throw nvae;
                }
                }
                break;
            case FloatingPointLiteral:
                {
                alt17=1;
                }
                break;
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
                {
                alt17=2;
                }
                break;
            case CharacterLiteral:
                {
                alt17=3;
                }
                break;
            case StringLiteral:
                {
                alt17=4;
                }
                break;
            case BooleanLiteral:
                {
                alt17=5;
                }
                break;
            case 43:
                {
                alt17=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:258:4: tmp= floatingPointLiteral
                    {
                    pushFollow(FOLLOW_floatingPointLiteral_in_literal1051);
                    tmp=floatingPointLiteral();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:259:4: tmp= integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal1062);
                    tmp=integerLiteral();

                    state._fsp--;

                    exp = tmp;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:260:4: CharacterLiteral
                    {
                    CharacterLiteral1=(Token)match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal1069); 
                    exp = new LiteralExpression(new Character((CharacterLiteral1!=null?CharacterLiteral1.getText():null).charAt(0)));

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:261:4: StringLiteral
                    {
                    StringLiteral2=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_literal1076); 
                    exp = new LiteralExpression((StringLiteral2!=null?StringLiteral2.getText():null).substring(1, (StringLiteral2!=null?StringLiteral2.getText():null).length()-1));

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:262:4: BooleanLiteral
                    {
                    BooleanLiteral3=(Token)match(input,BooleanLiteral,FOLLOW_BooleanLiteral_in_literal1083); 
                    exp = new LiteralExpression((BooleanLiteral3!=null?BooleanLiteral3.getText():null).equals("true")? Boolean.TRUE: Boolean.FALSE);

                    }
                    break;
                case 6 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:263:4: 'null'
                    {
                    match(input,43,FOLLOW_43_in_literal1090); 
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:266:1: floatingPointLiteral returns [Expression exp] : (sign= ( '+' | '-' ) )? FloatingPointLiteral ;
    public final Expression floatingPointLiteral() throws RecognitionException {
        Expression exp = null;

        Token sign=null;
        Token FloatingPointLiteral4=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:267:2: ( (sign= ( '+' | '-' ) )? FloatingPointLiteral )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:267:4: (sign= ( '+' | '-' ) )? FloatingPointLiteral
            {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:267:8: (sign= ( '+' | '-' ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( ((LA18_0>=34 && LA18_0<=35)) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:267:8: sign= ( '+' | '-' )
                    {
                    sign=(Token)input.LT(1);
                    if ( (input.LA(1)>=34 && input.LA(1)<=35) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;

            }

            FloatingPointLiteral4=(Token)match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1116); 
            exp = new LiteralExpression(sign!=null && "-".equals(sign.getText())? new Double("-"+(FloatingPointLiteral4!=null?FloatingPointLiteral4.getText():null)): new Double((FloatingPointLiteral4!=null?FloatingPointLiteral4.getText():null)));

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:270:1: integerLiteral returns [Expression exp] : (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral ) ;
    public final Expression integerLiteral() throws RecognitionException {
        Expression exp = null;

        Token sign=null;
        Token HexLiteral5=null;
        Token OctalLiteral6=null;
        Token DecimalLiteral7=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:271:2: ( (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral ) )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:271:4: (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral )
            {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:271:8: (sign= ( '+' | '-' ) )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( ((LA19_0>=34 && LA19_0<=35)) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:271:8: sign= ( '+' | '-' )
                    {
                    sign=(Token)input.LT(1);
                    if ( (input.LA(1)>=34 && input.LA(1)<=35) ) {
                        input.consume();
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;

            }

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:271:20: ( HexLiteral | OctalLiteral | DecimalLiteral )
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:271:21: HexLiteral
                    {
                    HexLiteral5=(Token)match(input,HexLiteral,FOLLOW_HexLiteral_in_integerLiteral1143); 
                    exp = new LiteralExpression(sign!=null && "-".equals(sign.getText())? new Integer("-"+(HexLiteral5!=null?HexLiteral5.getText():null)): new Integer((HexLiteral5!=null?HexLiteral5.getText():null)));

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:272:4: OctalLiteral
                    {
                    OctalLiteral6=(Token)match(input,OctalLiteral,FOLLOW_OctalLiteral_in_integerLiteral1150); 
                    exp = new LiteralExpression((sign!=null && "-".equals(sign.getText())? new Integer("-"+(OctalLiteral6!=null?OctalLiteral6.getText():null)): new Integer((OctalLiteral6!=null?OctalLiteral6.getText():null))));

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:273:4: DecimalLiteral
                    {
                    DecimalLiteral7=(Token)match(input,DecimalLiteral,FOLLOW_DecimalLiteral_in_integerLiteral1157); 
                    exp = new LiteralExpression(sign!=null && "-".equals(sign.getText())? new Integer("-"+(DecimalLiteral7!=null?DecimalLiteral7.getText():null)): new Integer((DecimalLiteral7!=null?DecimalLiteral7.getText():null)));

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
    // $ANTLR end "integerLiteral"

    // Delegated rules


 

    public static final BitSet FOLLOW_expression_in_lhs48 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_lhs50 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression73 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_logicalOrExpression_in_conditionalExpression98 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_24_in_conditionalExpression121 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression127 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_conditionalExpression129 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_logicalAndExpression_in_logicalOrExpression179 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_26_in_logicalOrExpression202 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_logicalAndExpression_in_logicalOrExpression208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_logicalAndExpression252 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_27_in_logicalAndExpression275 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_equalityExpression_in_logicalAndExpression281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression325 = new BitSet(new long[]{0x0000000030000002L});
    public static final BitSet FOLLOW_28_in_equalityExpression353 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_29_in_equalityExpression367 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression422 = new BitSet(new long[]{0x00000003C0000002L});
    public static final BitSet FOLLOW_30_in_relationalExpression450 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_31_in_relationalExpression464 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_32_in_relationalExpression478 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_33_in_relationalExpression492 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression554 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_34_in_additiveExpression582 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_35_in_additiveExpression596 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression651 = new BitSet(new long[]{0x0000007000000002L});
    public static final BitSet FOLLOW_36_in_multiplicativeExpression679 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_37_in_multiplicativeExpression693 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_38_in_multiplicativeExpression707 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpression764 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_suffix_in_unaryExpression774 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_39_in_primary802 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_expression_in_primary808 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_primary810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary821 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pseudovariable_in_primary834 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_primary845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldAccess_in_suffix868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodAccess_in_suffix879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_fieldAccess898 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_fieldAccess904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_methodAccess923 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodAccess929 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_methodAccess931 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_methodAccess933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_methodAccess940 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodAccess946 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_methodAccess948 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_expression_in_methodAccess954 = new BitSet(new long[]{0x0000050000000000L});
    public static final BitSet FOLLOW_42_in_methodAccess961 = new BitSet(new long[]{0x0000088C00000FF0L});
    public static final BitSet FOLLOW_expression_in_methodAccess967 = new BitSet(new long[]{0x0000050000000000L});
    public static final BitSet FOLLOW_40_in_methodAccess975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_pseudovariable1023 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_pseudovariable1025 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_pseudovariable1029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_floatingPointLiteral_in_literal1051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal1062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal1069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal1076 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BooleanLiteral_in_literal1083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_literal1090 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_floatingPointLiteral1109 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral1135 = new BitSet(new long[]{0x0000000000000E00L});
    public static final BitSet FOLLOW_HexLiteral_in_integerLiteral1143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OctalLiteral_in_integerLiteral1150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DecimalLiteral_in_integerLiteral1157 = new BitSet(new long[]{0x0000000000000002L});

}