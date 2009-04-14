// $ANTLR 3.1.2 C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g 2009-04-14 11:41:33

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
import java.util.Map;
import java.util.HashMap;
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

    	/** The type model. */
    	protected OAVTypeModel	tmodel;
    	
    	/**
    	 *  Set the type model.
    	 */
    	public void	setTypeModel(OAVTypeModel tmodel)
    	{
    		this.tmodel	= tmodel;
    	}



    // $ANTLR start "lhs"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:64:1: lhs returns [Expression exp] : tmp= expression EOF ;
    public final Expression lhs() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:68:2: (tmp= expression EOF )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:68:4: tmp= expression EOF
            {
            pushFollow(FOLLOW_expression_in_lhs48);
            tmp=expression();

            state._fsp--;
            if (state.failed) return exp;
            match(input,EOF,FOLLOW_EOF_in_lhs50); if (state.failed) return exp;
            if ( state.backtracking==0 ) {
              exp = tmp;
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
    // $ANTLR end "lhs"


    // $ANTLR start "expression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:71:1: expression returns [Expression exp] : tmp= conditionalExpression ;
    public final Expression expression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:76:2: (tmp= conditionalExpression )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:76:4: tmp= conditionalExpression
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression73);
            tmp=conditionalExpression();

            state._fsp--;
            if (state.failed) return exp;
            if ( state.backtracking==0 ) {
              exp = tmp;
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
    // $ANTLR end "expression"


    // $ANTLR start "conditionalExpression"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:81:1: conditionalExpression returns [Expression exp] : tmp= logicalOrExpression ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )? ;
    public final Expression conditionalExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;

        Expression tmp3 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:85:2: (tmp= logicalOrExpression ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:85:4: tmp= logicalOrExpression ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )?
            {
            pushFollow(FOLLOW_logicalOrExpression_in_conditionalExpression98);
            tmp=logicalOrExpression();

            state._fsp--;
            if (state.failed) return exp;
            if ( state.backtracking==0 ) {
              exp = tmp;
            }
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:86:9: ( '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==24) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:87:10: '?' tmp2= conditionalExpression ':' tmp3= conditionalExpression
                    {
                    match(input,24,FOLLOW_24_in_conditionalExpression121); if (state.failed) return exp;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression127);
                    tmp2=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return exp;
                    match(input,25,FOLLOW_25_in_conditionalExpression129); if (state.failed) return exp;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression135);
                    tmp3=conditionalExpression();

                    state._fsp--;
                    if (state.failed) return exp;
                    if ( state.backtracking==0 ) {

                              		exp = new ConditionalExpression(tmp, tmp2, tmp3);
                              	
                    }

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:94:1: logicalOrExpression returns [Expression exp] : tmp= logicalAndExpression ( '||' tmp2= logicalAndExpression )* ;
    public final Expression logicalOrExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:98:2: (tmp= logicalAndExpression ( '||' tmp2= logicalAndExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:98:4: tmp= logicalAndExpression ( '||' tmp2= logicalAndExpression )*
            {
            pushFollow(FOLLOW_logicalAndExpression_in_logicalOrExpression179);
            tmp=logicalAndExpression();

            state._fsp--;
            if (state.failed) return exp;
            if ( state.backtracking==0 ) {
              exp = tmp;
            }
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:99:9: ( '||' tmp2= logicalAndExpression )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==26) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:100:10: '||' tmp2= logicalAndExpression
            	    {
            	    match(input,26,FOLLOW_26_in_logicalOrExpression202); if (state.failed) return exp;
            	    pushFollow(FOLLOW_logicalAndExpression_in_logicalOrExpression208);
            	    tmp2=logicalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return exp;
            	    if ( state.backtracking==0 ) {

            	              		exp = new OperationExpression(exp, tmp2, OperationExpression.OPERATOR_OR);
            	              	
            	    }

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:107:1: logicalAndExpression returns [Expression exp] : tmp= equalityExpression ( '&&' tmp2= equalityExpression )* ;
    public final Expression logicalAndExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:111:2: (tmp= equalityExpression ( '&&' tmp2= equalityExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:111:4: tmp= equalityExpression ( '&&' tmp2= equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_logicalAndExpression252);
            tmp=equalityExpression();

            state._fsp--;
            if (state.failed) return exp;
            if ( state.backtracking==0 ) {
              exp = tmp;
            }
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:112:9: ( '&&' tmp2= equalityExpression )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==27) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:113:10: '&&' tmp2= equalityExpression
            	    {
            	    match(input,27,FOLLOW_27_in_logicalAndExpression275); if (state.failed) return exp;
            	    pushFollow(FOLLOW_equalityExpression_in_logicalAndExpression281);
            	    tmp2=equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return exp;
            	    if ( state.backtracking==0 ) {

            	              		exp = new OperationExpression(exp, tmp2, OperationExpression.OPERATOR_AND);
            	              	
            	    }

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:120:1: equalityExpression returns [Expression exp] : tmp= relationalExpression ( ( '==' | '!=' ) tmp2= relationalExpression )* ;
    public final Expression equalityExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        IOperator	operator = null;
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:125:2: (tmp= relationalExpression ( ( '==' | '!=' ) tmp2= relationalExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:125:4: tmp= relationalExpression ( ( '==' | '!=' ) tmp2= relationalExpression )*
            {
            pushFollow(FOLLOW_relationalExpression_in_equalityExpression330);
            tmp=relationalExpression();

            state._fsp--;
            if (state.failed) return exp;
            if ( state.backtracking==0 ) {
              exp = tmp;
            }
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:126:9: ( ( '==' | '!=' ) tmp2= relationalExpression )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0>=28 && LA5_0<=29)) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:127:10: ( '==' | '!=' ) tmp2= relationalExpression
            	    {
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:127:10: ( '==' | '!=' )
            	    int alt4=2;
            	    int LA4_0 = input.LA(1);

            	    if ( (LA4_0==28) ) {
            	        alt4=1;
            	    }
            	    else if ( (LA4_0==29) ) {
            	        alt4=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return exp;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 4, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt4) {
            	        case 1 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:127:11: '=='
            	            {
            	            match(input,28,FOLLOW_28_in_equalityExpression354); if (state.failed) return exp;
            	            if ( state.backtracking==0 ) {
            	              operator=IOperator.EQUAL;
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:128:11: '!='
            	            {
            	            match(input,29,FOLLOW_29_in_equalityExpression368); if (state.failed) return exp;
            	            if ( state.backtracking==0 ) {
            	              operator=IOperator.NOTEQUAL;
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_relationalExpression_in_equalityExpression387);
            	    tmp2=relationalExpression();

            	    state._fsp--;
            	    if (state.failed) return exp;
            	    if ( state.backtracking==0 ) {

            	      	        	exp = new OperationExpression(exp, tmp2, operator);
            	      	        
            	    }

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:136:1: relationalExpression returns [Expression exp] : tmp= additiveExpression ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )* ;
    public final Expression relationalExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        IOperator	operator = null;
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:141:2: (tmp= additiveExpression ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:141:4: tmp= additiveExpression ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_relationalExpression428);
            tmp=additiveExpression();

            state._fsp--;
            if (state.failed) return exp;
            if ( state.backtracking==0 ) {
              exp = tmp;
            }
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:142:9: ( ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>=30 && LA7_0<=33)) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:143:10: ( '<' | '<=' | '>' | '>=' ) tmp2= additiveExpression
            	    {
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:143:10: ( '<' | '<=' | '>' | '>=' )
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
            	        if (state.backtracking>0) {state.failed=true; return exp;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 6, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt6) {
            	        case 1 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:143:11: '<'
            	            {
            	            match(input,30,FOLLOW_30_in_relationalExpression452); if (state.failed) return exp;
            	            if ( state.backtracking==0 ) {
            	              operator=IOperator.LESS;
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:144:11: '<='
            	            {
            	            match(input,31,FOLLOW_31_in_relationalExpression466); if (state.failed) return exp;
            	            if ( state.backtracking==0 ) {
            	              operator=IOperator.LESSOREQUAL;
            	            }

            	            }
            	            break;
            	        case 3 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:145:11: '>'
            	            {
            	            match(input,32,FOLLOW_32_in_relationalExpression480); if (state.failed) return exp;
            	            if ( state.backtracking==0 ) {
            	              operator=IOperator.GREATER;
            	            }

            	            }
            	            break;
            	        case 4 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:146:11: '>='
            	            {
            	            match(input,33,FOLLOW_33_in_relationalExpression494); if (state.failed) return exp;
            	            if ( state.backtracking==0 ) {
            	              operator=IOperator.GREATEROREQUAL;
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_additiveExpression_in_relationalExpression513);
            	    tmp2=additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return exp;
            	    if ( state.backtracking==0 ) {

            	      	        	exp = new OperationExpression(exp, tmp2, operator);
            	      	        
            	    }

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:154:1: additiveExpression returns [Expression exp] : tmp= multiplicativeExpression ( ( '+' | '-' ) tmp2= multiplicativeExpression )* ;
    public final Expression additiveExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        IFunction	operator = null;
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:159:2: (tmp= multiplicativeExpression ( ( '+' | '-' ) tmp2= multiplicativeExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:159:4: tmp= multiplicativeExpression ( ( '+' | '-' ) tmp2= multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression561);
            tmp=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return exp;
            if ( state.backtracking==0 ) {
              exp = tmp;
            }
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:160:9: ( ( '+' | '-' ) tmp2= multiplicativeExpression )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>=34 && LA9_0<=35)) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:161:10: ( '+' | '-' ) tmp2= multiplicativeExpression
            	    {
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:161:10: ( '+' | '-' )
            	    int alt8=2;
            	    int LA8_0 = input.LA(1);

            	    if ( (LA8_0==34) ) {
            	        alt8=1;
            	    }
            	    else if ( (LA8_0==35) ) {
            	        alt8=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return exp;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 8, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt8) {
            	        case 1 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:161:11: '+'
            	            {
            	            match(input,34,FOLLOW_34_in_additiveExpression585); if (state.failed) return exp;
            	            if ( state.backtracking==0 ) {
            	              operator=IFunction.SUM;
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:162:11: '-'
            	            {
            	            match(input,35,FOLLOW_35_in_additiveExpression599); if (state.failed) return exp;
            	            if ( state.backtracking==0 ) {
            	              operator=IFunction.SUB;
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression618);
            	    tmp2=multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return exp;
            	    if ( state.backtracking==0 ) {

            	      	        	exp = new OperationExpression(exp, tmp2, operator);
            	      	        
            	    }

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:170:1: multiplicativeExpression returns [Expression exp] : tmp= unaryExpression ( ( '*' | '/' | '%' ) tmp2= unaryExpression )* ;
    public final Expression multiplicativeExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Expression tmp2 = null;


        IFunction	operator = null;
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:175:2: (tmp= unaryExpression ( ( '*' | '/' | '%' ) tmp2= unaryExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:175:4: tmp= unaryExpression ( ( '*' | '/' | '%' ) tmp2= unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression659);
            tmp=unaryExpression();

            state._fsp--;
            if (state.failed) return exp;
            if ( state.backtracking==0 ) {
              exp = tmp;
            }
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
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:177:10: ( '*' | '/' | '%' ) tmp2= unaryExpression
            	    {
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:177:10: ( '*' | '/' | '%' )
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
            	        if (state.backtracking>0) {state.failed=true; return exp;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 10, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt10) {
            	        case 1 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:177:11: '*'
            	            {
            	            match(input,36,FOLLOW_36_in_multiplicativeExpression683); if (state.failed) return exp;
            	            if ( state.backtracking==0 ) {
            	              operator=IFunction.MULT;
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:178:11: '/'
            	            {
            	            match(input,37,FOLLOW_37_in_multiplicativeExpression697); if (state.failed) return exp;
            	            if ( state.backtracking==0 ) {
            	              operator=IFunction.DIV;
            	            }

            	            }
            	            break;
            	        case 3 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:179:11: '%'
            	            {
            	            match(input,38,FOLLOW_38_in_multiplicativeExpression711); if (state.failed) return exp;
            	            if ( state.backtracking==0 ) {
            	              operator=IFunction.MOD;
            	            }

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression730);
            	    tmp2=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return exp;
            	    if ( state.backtracking==0 ) {

            	      	        	exp = new OperationExpression(exp, tmp2, operator);
            	      	        
            	    }

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:187:1: unaryExpression returns [Expression exp] : ( '+' tmp= unaryExpression | '-' tmp= unaryExpression | '!' tmp= unaryExpression | '~' tmp= unaryExpression | tmp= primaryExpression );
    public final Expression unaryExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:191:2: ( '+' tmp= unaryExpression | '-' tmp= unaryExpression | '!' tmp= unaryExpression | '~' tmp= unaryExpression | tmp= primaryExpression )
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
                if (state.backtracking>0) {state.failed=true; return exp;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:191:4: '+' tmp= unaryExpression
                    {
                    match(input,34,FOLLOW_34_in_unaryExpression763); if (state.failed) return exp;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression769);
                    tmp=unaryExpression();

                    state._fsp--;
                    if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = tmp;
                    }

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:192:4: '-' tmp= unaryExpression
                    {
                    match(input,35,FOLLOW_35_in_unaryExpression776); if (state.failed) return exp;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression782);
                    tmp=unaryExpression();

                    state._fsp--;
                    if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = new UnaryExpression(tmp, UnaryExpression.OPERATOR_MINUS);
                    }

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:193:4: '!' tmp= unaryExpression
                    {
                    match(input,39,FOLLOW_39_in_unaryExpression789); if (state.failed) return exp;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression795);
                    tmp=unaryExpression();

                    state._fsp--;
                    if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = new UnaryExpression(tmp, UnaryExpression.OPERATOR_NOT);
                    }

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:194:4: '~' tmp= unaryExpression
                    {
                    match(input,40,FOLLOW_40_in_unaryExpression802); if (state.failed) return exp;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression808);
                    tmp=unaryExpression();

                    state._fsp--;
                    if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = new UnaryExpression(tmp, UnaryExpression.OPERATOR_BNOT);
                    }

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:195:4: tmp= primaryExpression
                    {
                    pushFollow(FOLLOW_primaryExpression_in_unaryExpression819);
                    tmp=primaryExpression();

                    state._fsp--;
                    if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = tmp;
                    }

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:198:1: primaryExpression returns [Expression exp] : tmp= primaryPrefix (tmp2= primarySuffix )* ;
    public final Expression primaryExpression() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;

        Suffix tmp2 = null;


        List suffs = null;
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:203:2: (tmp= primaryPrefix (tmp2= primarySuffix )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:203:4: tmp= primaryPrefix (tmp2= primarySuffix )*
            {
            pushFollow(FOLLOW_primaryPrefix_in_primaryExpression848);
            tmp=primaryPrefix();

            state._fsp--;
            if (state.failed) return exp;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:204:2: (tmp2= primarySuffix )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==43||LA13_0==45) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:204:3: tmp2= primarySuffix
            	    {
            	    pushFollow(FOLLOW_primarySuffix_in_primaryExpression856);
            	    tmp2=primarySuffix();

            	    state._fsp--;
            	    if (state.failed) return exp;
            	    if ( state.backtracking==0 ) {

            	      			if(suffs==null)
            	      				suffs	= new ArrayList();
            	      			suffs.add(tmp2);
            	      		
            	    }

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            if ( state.backtracking==0 ) {

              		if(suffs==null)
              			exp = tmp;
              		else
              			exp = new PrimaryExpression(tmp, (Suffix[])suffs.toArray(new Suffix[suffs.size()]));
              	
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
    // $ANTLR end "primaryExpression"


    // $ANTLR start "primaryPrefix"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:219:1: primaryPrefix returns [Expression exp] : ( '(' tmp= expression ')' | tmp= literal | {...}?tmp= pseudovariable | {...}?tmp= variable | ( staticField )=>{...}?tmp= staticField | ( existentialDeclaration )=>tmp= existentialDeclaration );
    public final Expression primaryPrefix() throws RecognitionException {
        Expression exp = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:223:2: ( '(' tmp= expression ')' | tmp= literal | {...}?tmp= pseudovariable | {...}?tmp= variable | ( staticField )=>{...}?tmp= staticField | ( existentialDeclaration )=>tmp= existentialDeclaration )
            int alt14=6;
            alt14 = dfa14.predict(input);
            switch (alt14) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:223:4: '(' tmp= expression ')'
                    {
                    match(input,41,FOLLOW_41_in_primaryPrefix884); if (state.failed) return exp;
                    pushFollow(FOLLOW_expression_in_primaryPrefix890);
                    tmp=expression();

                    state._fsp--;
                    if (state.failed) return exp;
                    match(input,42,FOLLOW_42_in_primaryPrefix892); if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = tmp;
                    }

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:224:4: tmp= literal
                    {
                    pushFollow(FOLLOW_literal_in_primaryPrefix903);
                    tmp=literal();

                    state._fsp--;
                    if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = tmp;
                    }

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:225:4: {...}?tmp= pseudovariable
                    {
                    if ( !((helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText()))) ) {
                        if (state.backtracking>0) {state.failed=true; return exp;}
                        throw new FailedPredicateException(input, "primaryPrefix", "helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText())");
                    }
                    pushFollow(FOLLOW_pseudovariable_in_primaryPrefix916);
                    tmp=pseudovariable();

                    state._fsp--;
                    if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = tmp;
                    }

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:226:4: {...}?tmp= variable
                    {
                    if ( !((helper.getVariable(JavaJadexParser.this.input.LT(1).getText())!=null)) ) {
                        if (state.backtracking>0) {state.failed=true; return exp;}
                        throw new FailedPredicateException(input, "primaryPrefix", "helper.getVariable(JavaJadexParser.this.input.LT(1).getText())!=null");
                    }
                    pushFollow(FOLLOW_variable_in_primaryPrefix929);
                    tmp=variable();

                    state._fsp--;
                    if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = tmp;
                    }

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:227:4: ( staticField )=>{...}?tmp= staticField
                    {
                    if ( !((SJavaParser.lookaheadStaticField(JavaJadexParser.this.input, tmodel, imports))) ) {
                        if (state.backtracking>0) {state.failed=true; return exp;}
                        throw new FailedPredicateException(input, "primaryPrefix", "SJavaParser.lookaheadStaticField(JavaJadexParser.this.input, tmodel, imports)");
                    }
                    pushFollow(FOLLOW_staticField_in_primaryPrefix948);
                    tmp=staticField();

                    state._fsp--;
                    if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = tmp;
                    }

                    }
                    break;
                case 6 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:228:4: ( existentialDeclaration )=>tmp= existentialDeclaration
                    {
                    pushFollow(FOLLOW_existentialDeclaration_in_primaryPrefix965);
                    tmp=existentialDeclaration();

                    state._fsp--;
                    if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = tmp;
                    }

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:232:1: staticField returns [Expression exp] : otype= type '.' field= IDENTIFIER ;
    public final Expression staticField() throws RecognitionException {
        Expression exp = null;

        Token field=null;
        OAVObjectType otype = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:236:2: (otype= type '.' field= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:236:4: otype= type '.' field= IDENTIFIER
            {
            pushFollow(FOLLOW_type_in_staticField989);
            otype=type();

            state._fsp--;
            if (state.failed) return exp;
            match(input,43,FOLLOW_43_in_staticField991); if (state.failed) return exp;
            field=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_staticField997); if (state.failed) return exp;
            if ( state.backtracking==0 ) {

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:253:1: existentialDeclaration returns [Expression exp] : otype= type varname= IDENTIFIER ;
    public final Expression existentialDeclaration() throws RecognitionException {
        Expression exp = null;

        Token varname=null;
        OAVObjectType otype = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:257:2: (otype= type varname= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:257:4: otype= type varname= IDENTIFIER
            {
            pushFollow(FOLLOW_type_in_existentialDeclaration1021);
            otype=type();

            state._fsp--;
            if (state.failed) return exp;
            varname=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_existentialDeclaration1027); if (state.failed) return exp;
            if ( state.backtracking==0 ) {

              		Variable	var	= new Variable(varname.getText(), otype);
              		exp = new ExistentialDeclaration(otype, var);
              		helper.addVariable(var);
              	
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
    // $ANTLR end "existentialDeclaration"


    // $ANTLR start "type"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:265:1: type returns [OAVObjectType otype] : tmp= IDENTIFIER ({...}? '.' tmp2= IDENTIFIER )* ;
    public final OAVObjectType type() throws RecognitionException {
        OAVObjectType otype = null;

        Token tmp=null;
        Token tmp2=null;

        String name = null;
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:270:2: (tmp= IDENTIFIER ({...}? '.' tmp2= IDENTIFIER )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:270:4: tmp= IDENTIFIER ({...}? '.' tmp2= IDENTIFIER )*
            {
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_type1056); if (state.failed) return otype;
            if ( state.backtracking==0 ) {

              		name	= tmp.getText();
              		try
              		{
              			otype = tmodel.getObjectType(name);
              		}
              		catch(Exception e)
              		{
              			Class	clazz	= SReflect.findClass0(name, imports, tmodel.getClassLoader());
              			if(clazz!=null)
              				otype = tmodel.getJavaType(clazz);
              		}
              	
            }
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:284:2: ({...}? '.' tmp2= IDENTIFIER )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==43) ) {
                    int LA15_1 = input.LA(2);

                    if ( (LA15_1==IDENTIFIER) ) {
                        int LA15_3 = input.LA(3);

                        if ( (LA15_3==43) ) {
                            int LA15_4 = input.LA(4);

                            if ( (LA15_4==IDENTIFIER) ) {
                                int LA15_6 = input.LA(5);

                                if ( ((otype==null)) ) {
                                    alt15=1;
                                }


                            }


                        }
                        else if ( (LA15_3==IDENTIFIER) ) {
                            alt15=1;
                        }


                    }


                }


                switch (alt15) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:284:4: {...}? '.' tmp2= IDENTIFIER
            	    {
            	    if ( !((otype==null)) ) {
            	        if (state.backtracking>0) {state.failed=true; return otype;}
            	        throw new FailedPredicateException(input, "type", "$otype==null");
            	    }
            	    match(input,43,FOLLOW_43_in_type1068); if (state.failed) return otype;
            	    tmp2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_type1074); if (state.failed) return otype;
            	    if ( state.backtracking==0 ) {

            	      			name += "."+tmp2.getText();
            	       			Class	clazz	= SReflect.findClass0(name, imports, tmodel.getClassLoader());
            	      			if(clazz!=null)
            	      				otype = tmodel.getJavaType(clazz);
            	       		
            	    }

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
        return otype;
    }
    // $ANTLR end "type"


    // $ANTLR start "primarySuffix"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:295:1: primarySuffix returns [Suffix suff] : (tmp= fieldAccess | tmp= methodAccess | tmp= arrayAccess );
    public final Suffix primarySuffix() throws RecognitionException {
        Suffix suff = null;

        Suffix tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:299:2: (tmp= fieldAccess | tmp= methodAccess | tmp= arrayAccess )
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
                        if (state.backtracking>0) {state.failed=true; return suff;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 16, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return suff;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA16_0==45) ) {
                alt16=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return suff;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:299:4: tmp= fieldAccess
                    {
                    pushFollow(FOLLOW_fieldAccess_in_primarySuffix1104);
                    tmp=fieldAccess();

                    state._fsp--;
                    if (state.failed) return suff;
                    if ( state.backtracking==0 ) {
                      suff = tmp;
                    }

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:300:4: tmp= methodAccess
                    {
                    pushFollow(FOLLOW_methodAccess_in_primarySuffix1115);
                    tmp=methodAccess();

                    state._fsp--;
                    if (state.failed) return suff;
                    if ( state.backtracking==0 ) {
                      suff = tmp;
                    }

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:301:4: tmp= arrayAccess
                    {
                    pushFollow(FOLLOW_arrayAccess_in_primarySuffix1126);
                    tmp=arrayAccess();

                    state._fsp--;
                    if (state.failed) return suff;
                    if ( state.backtracking==0 ) {
                      suff = tmp;
                    }

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:304:1: fieldAccess returns [Suffix suff] : '.' tmp= IDENTIFIER ;
    public final Suffix fieldAccess() throws RecognitionException {
        Suffix suff = null;

        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:308:2: ( '.' tmp= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:308:4: '.' tmp= IDENTIFIER
            {
            match(input,43,FOLLOW_43_in_fieldAccess1145); if (state.failed) return suff;
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_fieldAccess1151); if (state.failed) return suff;
            if ( state.backtracking==0 ) {
              suff = new FieldAccess(tmp.getText());
            }

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:311:1: methodAccess returns [Suffix suff] : ( '.' tmp1= IDENTIFIER '(' ')' | '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')' );
    public final Suffix methodAccess() throws RecognitionException {
        Suffix suff = null;

        Token tmp1=null;
        Token tmp2=null;
        Expression p1 = null;

        Expression p2 = null;


        List params = new ArrayList();
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:316:2: ( '.' tmp1= IDENTIFIER '(' ')' | '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')' )
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
                            if (state.backtracking>0) {state.failed=true; return suff;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 18, 3, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return suff;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 18, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return suff;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return suff;}
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:316:4: '.' tmp1= IDENTIFIER '(' ')'
                    {
                    match(input,43,FOLLOW_43_in_methodAccess1175); if (state.failed) return suff;
                    tmp1=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodAccess1181); if (state.failed) return suff;
                    match(input,41,FOLLOW_41_in_methodAccess1183); if (state.failed) return suff;
                    match(input,42,FOLLOW_42_in_methodAccess1185); if (state.failed) return suff;
                    if ( state.backtracking==0 ) {
                      suff = new MethodAccess(tmp1.getText(), null);
                    }

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:317:4: '.' tmp2= IDENTIFIER '(' p1= expression ( ',' p2= expression )* ')'
                    {
                    match(input,43,FOLLOW_43_in_methodAccess1192); if (state.failed) return suff;
                    tmp2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodAccess1198); if (state.failed) return suff;
                    match(input,41,FOLLOW_41_in_methodAccess1200); if (state.failed) return suff;
                    pushFollow(FOLLOW_expression_in_methodAccess1206);
                    p1=expression();

                    state._fsp--;
                    if (state.failed) return suff;
                    if ( state.backtracking==0 ) {
                      params.add(p1);
                    }
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:318:2: ( ',' p2= expression )*
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==44) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:318:3: ',' p2= expression
                    	    {
                    	    match(input,44,FOLLOW_44_in_methodAccess1212); if (state.failed) return suff;
                    	    pushFollow(FOLLOW_expression_in_methodAccess1218);
                    	    p2=expression();

                    	    state._fsp--;
                    	    if (state.failed) return suff;
                    	    if ( state.backtracking==0 ) {
                    	      params.add(p2);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);

                    match(input,42,FOLLOW_42_in_methodAccess1226); if (state.failed) return suff;
                    if ( state.backtracking==0 ) {

                      		suff = new MethodAccess(tmp2.getText(), (Expression[])params.toArray(new Expression[params.size()]));
                      	
                    }

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:325:1: arrayAccess returns [Suffix suff] : '[' tmp= expression ']' ;
    public final Suffix arrayAccess() throws RecognitionException {
        Suffix suff = null;

        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:329:2: ( '[' tmp= expression ']' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:329:4: '[' tmp= expression ']'
            {
            match(input,45,FOLLOW_45_in_arrayAccess1246); if (state.failed) return suff;
            pushFollow(FOLLOW_expression_in_arrayAccess1252);
            tmp=expression();

            state._fsp--;
            if (state.failed) return suff;
            match(input,46,FOLLOW_46_in_arrayAccess1254); if (state.failed) return suff;
            if ( state.backtracking==0 ) {

              		suff = new ArrayAccess(tmp);
              	
            }

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:335:1: variable returns [Expression exp] : tmp= IDENTIFIER ;
    public final Expression variable() throws RecognitionException {
        Expression exp = null;

        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:339:2: (tmp= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:339:4: tmp= IDENTIFIER
            {
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variable1278); if (state.failed) return exp;
            if ( state.backtracking==0 ) {

              		String	name	= tmp.getText();
              		Variable	var	= helper.getVariable(name);
              		if(var==null)
              			throw new RuntimeException("No such variable: "+name);
              		exp = new VariableExpression(var);
              	
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
    // $ANTLR end "variable"


    // $ANTLR start "pseudovariable"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:349:1: pseudovariable returns [Expression exp] : tmp= IDENTIFIER '.' tmp2= IDENTIFIER ;
    public final Expression pseudovariable() throws RecognitionException {
        Expression exp = null;

        Token tmp=null;
        Token tmp2=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:353:2: (tmp= IDENTIFIER '.' tmp2= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:353:4: tmp= IDENTIFIER '.' tmp2= IDENTIFIER
            {
            tmp=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_pseudovariable1302); if (state.failed) return exp;
            match(input,43,FOLLOW_43_in_pseudovariable1304); if (state.failed) return exp;
            tmp2=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_pseudovariable1308); if (state.failed) return exp;
            if ( state.backtracking==0 ) {

              		String name	= tmp.getText()+"."+tmp2.getText();
              		Variable	var	= helper.getVariable(name);
              		if(var==null)
              			throw new RuntimeException("No such variable: "+name);
              		exp = new VariableExpression(var);
              	
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
    // $ANTLR end "pseudovariable"


    // $ANTLR start "literal"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:363:1: literal returns [Expression exp] : (tmp= floatingPointLiteral | tmp= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );
    public final Expression literal() throws RecognitionException {
        Expression exp = null;

        Token CharacterLiteral1=null;
        Token StringLiteral2=null;
        Token BooleanLiteral3=null;
        Expression tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:364:2: (tmp= floatingPointLiteral | tmp= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' )
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
                if (state.backtracking>0) {state.failed=true; return exp;}
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:364:4: tmp= floatingPointLiteral
                    {
                    pushFollow(FOLLOW_floatingPointLiteral_in_literal1330);
                    tmp=floatingPointLiteral();

                    state._fsp--;
                    if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = tmp;
                    }

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:365:4: tmp= integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal1341);
                    tmp=integerLiteral();

                    state._fsp--;
                    if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = tmp;
                    }

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:366:4: CharacterLiteral
                    {
                    CharacterLiteral1=(Token)match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal1348); if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = new LiteralExpression(new Character((CharacterLiteral1!=null?CharacterLiteral1.getText():null).charAt(0)));
                    }

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:367:4: StringLiteral
                    {
                    StringLiteral2=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_literal1355); if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = new LiteralExpression((StringLiteral2!=null?StringLiteral2.getText():null).substring(1, (StringLiteral2!=null?StringLiteral2.getText():null).length()-1));
                    }

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:368:4: BooleanLiteral
                    {
                    BooleanLiteral3=(Token)match(input,BooleanLiteral,FOLLOW_BooleanLiteral_in_literal1362); if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = new LiteralExpression((BooleanLiteral3!=null?BooleanLiteral3.getText():null).equals("true")? Boolean.TRUE: Boolean.FALSE);
                    }

                    }
                    break;
                case 6 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:369:4: 'null'
                    {
                    match(input,47,FOLLOW_47_in_literal1369); if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = new LiteralExpression(null);
                    }

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:372:1: floatingPointLiteral returns [Expression exp] : FloatingPointLiteral ;
    public final Expression floatingPointLiteral() throws RecognitionException {
        Expression exp = null;

        Token FloatingPointLiteral4=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:373:2: ( FloatingPointLiteral )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:373:4: FloatingPointLiteral
            {
            FloatingPointLiteral4=(Token)match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1386); if (state.failed) return exp;
            if ( state.backtracking==0 ) {
              exp = new LiteralExpression(new Double((FloatingPointLiteral4!=null?FloatingPointLiteral4.getText():null)));
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
    // $ANTLR end "floatingPointLiteral"


    // $ANTLR start "integerLiteral"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:376:1: integerLiteral returns [Expression exp] : ( HexLiteral | OctalLiteral | DecimalLiteral );
    public final Expression integerLiteral() throws RecognitionException {
        Expression exp = null;

        Token HexLiteral5=null;
        Token OctalLiteral6=null;
        Token DecimalLiteral7=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:377:2: ( HexLiteral | OctalLiteral | DecimalLiteral )
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
                if (state.backtracking>0) {state.failed=true; return exp;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:377:4: HexLiteral
                    {
                    HexLiteral5=(Token)match(input,HexLiteral,FOLLOW_HexLiteral_in_integerLiteral1403); if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = new LiteralExpression(new Integer((HexLiteral5!=null?HexLiteral5.getText():null)));
                    }

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:378:4: OctalLiteral
                    {
                    OctalLiteral6=(Token)match(input,OctalLiteral,FOLLOW_OctalLiteral_in_integerLiteral1410); if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = new LiteralExpression(new Integer((OctalLiteral6!=null?OctalLiteral6.getText():null)));
                    }

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:379:4: DecimalLiteral
                    {
                    DecimalLiteral7=(Token)match(input,DecimalLiteral,FOLLOW_DecimalLiteral_in_integerLiteral1417); if (state.failed) return exp;
                    if ( state.backtracking==0 ) {
                      exp = new LiteralExpression(new Integer((DecimalLiteral7!=null?DecimalLiteral7.getText():null)));
                    }

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

    // $ANTLR start synpred1_JavaJadex
    public final void synpred1_JavaJadex_fragment() throws RecognitionException {   
        // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:227:4: ( staticField )
        // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:227:5: staticField
        {
        pushFollow(FOLLOW_staticField_in_synpred1_JavaJadex937);
        staticField();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_JavaJadex

    // $ANTLR start synpred2_JavaJadex
    public final void synpred2_JavaJadex_fragment() throws RecognitionException {   
        // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:228:4: ( existentialDeclaration )
        // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:228:5: existentialDeclaration
        {
        pushFollow(FOLLOW_existentialDeclaration_in_synpred2_JavaJadex956);
        existentialDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_JavaJadex

    // Delegated rules

    public final boolean synpred1_JavaJadex() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_JavaJadex_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_JavaJadex() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_JavaJadex_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA14 dfa14 = new DFA14(this);
    static final String DFA14_eotS =
        "\13\uffff";
    static final String DFA14_eofS =
        "\3\uffff\1\6\7\uffff";
    static final String DFA14_minS =
        "\1\4\2\uffff\2\4\2\uffff\1\4\3\uffff";
    static final String DFA14_maxS =
        "\1\57\2\uffff\1\56\1\4\2\uffff\1\53\3\uffff";
    static final String DFA14_acceptS =
        "\1\uffff\1\1\1\2\2\uffff\1\6\1\4\1\uffff\1\6\1\3\1\5";
    static final String DFA14_specialS =
        "\3\uffff\1\1\3\uffff\1\0\3\uffff}>";
    static final String[] DFA14_transitionS = {
            "\1\3\7\2\35\uffff\1\1\5\uffff\1\2",
            "",
            "",
            "\1\5\23\uffff\17\6\3\uffff\1\6\1\4\3\6",
            "\1\7",
            "",
            "",
            "\1\5\46\uffff\1\10",
            "",
            "",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "219:1: primaryPrefix returns [Expression exp] : ( '(' tmp= expression ')' | tmp= literal | {...}?tmp= pseudovariable | {...}?tmp= variable | ( staticField )=>{...}?tmp= staticField | ( existentialDeclaration )=>tmp= existentialDeclaration );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA14_7 = input.LA(1);

                         
                        int index14_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA14_7==43) && (synpred2_JavaJadex())) {s = 8;}

                        else if ( (LA14_7==IDENTIFIER) && (synpred2_JavaJadex())) {s = 5;}

                        else if ( ((helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText()))) ) {s = 9;}

                        else if ( ((helper.getVariable(JavaJadexParser.this.input.LT(1).getText())!=null)) ) {s = 6;}

                        else if ( ((synpred1_JavaJadex()&&(SJavaParser.lookaheadStaticField(JavaJadexParser.this.input, tmodel, imports)))) ) {s = 10;}

                         
                        input.seek(index14_7);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA14_3 = input.LA(1);

                         
                        int index14_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA14_3==43) ) {s = 4;}

                        else if ( (LA14_3==IDENTIFIER) && (synpred2_JavaJadex())) {s = 5;}

                        else if ( (LA14_3==EOF||(LA14_3>=24 && LA14_3<=38)||LA14_3==42||(LA14_3>=44 && LA14_3<=46)) ) {s = 6;}

                         
                        input.seek(index14_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 14, _s, input);
            error(nvae);
            throw nvae;
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
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primaryPrefix_in_primaryExpression848 = new BitSet(new long[]{0x0000280000000002L});
    public static final BitSet FOLLOW_primarySuffix_in_primaryExpression856 = new BitSet(new long[]{0x0000280000000002L});
    public static final BitSet FOLLOW_41_in_primaryPrefix884 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_expression_in_primaryPrefix890 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_primaryPrefix892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primaryPrefix903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pseudovariable_in_primaryPrefix916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_primaryPrefix929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_staticField_in_primaryPrefix948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existentialDeclaration_in_primaryPrefix965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_staticField989 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_staticField991 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_staticField997 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_existentialDeclaration1021 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_existentialDeclaration1027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_type1056 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_type1068 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_type1074 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_fieldAccess_in_primarySuffix1104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodAccess_in_primarySuffix1115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arrayAccess_in_primarySuffix1126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_fieldAccess1145 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_fieldAccess1151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_methodAccess1175 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodAccess1181 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_methodAccess1183 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_methodAccess1185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_methodAccess1192 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodAccess1198 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_methodAccess1200 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_expression_in_methodAccess1206 = new BitSet(new long[]{0x0000140000000000L});
    public static final BitSet FOLLOW_44_in_methodAccess1212 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_expression_in_methodAccess1218 = new BitSet(new long[]{0x0000140000000000L});
    public static final BitSet FOLLOW_42_in_methodAccess1226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_arrayAccess1246 = new BitSet(new long[]{0x0000838C00000FF0L});
    public static final BitSet FOLLOW_expression_in_arrayAccess1252 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_46_in_arrayAccess1254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable1278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_pseudovariable1302 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_pseudovariable1304 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_pseudovariable1308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_floatingPointLiteral_in_literal1330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal1341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal1348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal1355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BooleanLiteral_in_literal1362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_literal1369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HexLiteral_in_integerLiteral1403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OctalLiteral_in_integerLiteral1410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DecimalLiteral_in_integerLiteral1417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_staticField_in_synpred1_JavaJadex937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existentialDeclaration_in_synpred2_JavaJadex956 = new BitSet(new long[]{0x0000000000000002L});

}