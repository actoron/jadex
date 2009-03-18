// $ANTLR 3.0.1 C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g 2009-03-18 14:42:53

package jadex.rules.parser.conditions;

import jadex.rules.rulesystem.rules.*;
import jadex.rules.rulesystem.rules.functions.*;
import jadex.rules.rulesystem.*;
import jadex.rules.state.*;
import jadex.commons.SReflect;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class JadexJavaRulesParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENTIFIER", "INTLITERAL", "LONGLITERAL", "FLOATLITERAL", "DOUBLELITERAL", "CHARLITERAL", "STRINGLITERAL", "TRUE", "FALSE", "NULL", "IntegerNumber", "LongSuffix", "HexPrefix", "HexDigit", "Exponent", "NonIntegerNumber", "FloatSuffix", "DoubleSuffix", "EscapeSequence", "ABSTRACT", "ASSERT", "BOOLEAN", "BREAK", "BYTE", "CASE", "CATCH", "CHAR", "CLASS", "CONST", "CONTINUE", "DEFAULT", "DO", "DOUBLE", "ELSE", "ENUM", "EXTENDS", "FINAL", "FINALLY", "FLOAT", "FOR", "GOTO", "IF", "IMPLEMENTS", "IMPORT", "INSTANCEOF", "INT", "INTERFACE", "LONG", "NATIVE", "NEW", "PACKAGE", "PRIVATE", "PROTECTED", "PUBLIC", "RETURN", "SHORT", "STATIC", "STRICTFP", "SUPER", "SWITCH", "SYNCHRONIZED", "THIS", "THROW", "THROWS", "TRANSIENT", "TRY", "VOID", "VOLATILE", "WHILE", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET", "SEMI", "COMMA", "DOT", "ELLIPSIS", "EQ", "BANG", "TILDE", "QUES", "COLON", "EQEQ", "AMPAMP", "BARBAR", "PLUSPLUS", "SUBSUB", "PLUS", "SUB", "STAR", "SLASH", "AMP", "BAR", "CARET", "PERCENT", "PLUSEQ", "SUBEQ", "STAREQ", "SLASHEQ", "AMPEQ", "BAREQ", "CARETEQ", "PERCENTEQ", "MONKEYS_AT", "BANGEQ", "GT", "LT", "BooleanLiteral", "IntegerTypeSuffix", "HexLiteral", "DecimalLiteral", "OctalLiteral", "FloatTypeSuffix", "FloatingPointLiteral", "CharacterLiteral", "StringLiteral", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "COMMENT", "LINE_COMMENT"
    };
    public static final int PACKAGE=54;
    public static final int LT=112;
    public static final int STAR=95;
    public static final int WHILE=72;
    public static final int FloatTypeSuffix=118;
    public static final int OctalLiteral=117;
    public static final int CONST=32;
    public static final int CASE=28;
    public static final int CHAR=30;
    public static final int NEW=53;
    public static final int DO=35;
    public static final int EOF=-1;
    public static final int BREAK=26;
    public static final int LBRACKET=77;
    public static final int FINAL=40;
    public static final int RPAREN=74;
    public static final int IMPORT=47;
    public static final int SUBSUB=92;
    public static final int STAREQ=103;
    public static final int FloatSuffix=20;
    public static final int NonIntegerNumber=19;
    public static final int CARET=99;
    public static final int RETURN=58;
    public static final int THIS=65;
    public static final int DOUBLE=36;
    public static final int MONKEYS_AT=109;
    public static final int BARBAR=90;
    public static final int VOID=70;
    public static final int SUPER=62;
    public static final int GOTO=44;
    public static final int EQ=83;
    public static final int COMMENT=127;
    public static final int AMPAMP=89;
    public static final int QUES=86;
    public static final int EQEQ=88;
    public static final int HexPrefix=16;
    public static final int RBRACE=76;
    public static final int LINE_COMMENT=128;
    public static final int IntegerTypeSuffix=114;
    public static final int PRIVATE=55;
    public static final int STATIC=60;
    public static final int SWITCH=63;
    public static final int NULL=13;
    public static final int ELSE=37;
    public static final int STRICTFP=61;
    public static final int DOUBLELITERAL=8;
    public static final int NATIVE=52;
    public static final int ELLIPSIS=82;
    public static final int THROWS=67;
    public static final int INT=49;
    public static final int SLASHEQ=104;
    public static final int INTLITERAL=5;
    public static final int ASSERT=24;
    public static final int TRY=69;
    public static final int LONGLITERAL=6;
    public static final int LongSuffix=15;
    public static final int WS=126;
    public static final int FloatingPointLiteral=119;
    public static final int CHARLITERAL=9;
    public static final int JavaIDDigit=125;
    public static final int GT=111;
    public static final int CATCH=29;
    public static final int FALSE=12;
    public static final int Letter=124;
    public static final int EscapeSequence=22;
    public static final int THROW=66;
    public static final int BooleanLiteral=113;
    public static final int PROTECTED=56;
    public static final int CLASS=31;
    public static final int BAREQ=106;
    public static final int IntegerNumber=14;
    public static final int AMP=97;
    public static final int CharacterLiteral=120;
    public static final int PLUSPLUS=91;
    public static final int LBRACE=75;
    public static final int SUBEQ=102;
    public static final int FOR=43;
    public static final int Exponent=18;
    public static final int SUB=94;
    public static final int FLOAT=42;
    public static final int ABSTRACT=23;
    public static final int HexDigit=17;
    public static final int PLUSEQ=101;
    public static final int LPAREN=73;
    public static final int IF=45;
    public static final int SLASH=96;
    public static final int BOOLEAN=25;
    public static final int SYNCHRONIZED=64;
    public static final int IMPLEMENTS=46;
    public static final int CONTINUE=33;
    public static final int COMMA=80;
    public static final int AMPEQ=105;
    public static final int IDENTIFIER=4;
    public static final int TRANSIENT=68;
    public static final int TILDE=85;
    public static final int BANGEQ=110;
    public static final int PLUS=93;
    public static final int RBRACKET=78;
    public static final int DOT=81;
    public static final int HexLiteral=115;
    public static final int BYTE=27;
    public static final int PERCENT=100;
    public static final int VOLATILE=71;
    public static final int DEFAULT=34;
    public static final int SHORT=59;
    public static final int BANG=84;
    public static final int INSTANCEOF=48;
    public static final int DecimalLiteral=116;
    public static final int TRUE=11;
    public static final int SEMI=79;
    public static final int StringLiteral=121;
    public static final int COLON=87;
    public static final int ENUM=38;
    public static final int PERCENTEQ=108;
    public static final int FINALLY=41;
    public static final int DoubleSuffix=21;
    public static final int UnicodeEscape=122;
    public static final int STRINGLITERAL=10;
    public static final int CARETEQ=107;
    public static final int INTERFACE=50;
    public static final int LONG=51;
    public static final int EXTENDS=39;
    public static final int FLOATLITERAL=7;
    public static final int PUBLIC=57;
    public static final int OctalEscape=123;
    public static final int BAR=98;

        public JadexJavaRulesParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g"; }

    
        protected List	errors;
        public void displayRecognitionError(String[] tokenNames, RecognitionException e)
        {
            if(errors!=null)
            {
                String hdr = getErrorHeader(e);
                String msg = getErrorMessage(e, tokenNames);
            	errors.add(hdr + " " + msg);
            }
            else
            {
            	super.displayRecognitionError(tokenNames, e);
            }
        }
        public void setErrorList(List errors)
        {
            this.errors	= errors;
        }
        public List getErrorList()
        {
            return errors;
        }
        
        protected String[]	imports;
        public void	setImports(String[] imports)
        {
        	this.imports	= imports;
        }
        
        protected JavaRulesContext context;
        public void	setContext(JavaRulesContext context)
        {
        	this.context	= context;
        }



    // $ANTLR start rhs
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:65:1: rhs : expression EOF ;
    public final void rhs() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:66:2: ( expression EOF )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:66:4: expression EOF
            {
            pushFollow(FOLLOW_expression_in_rhs38);
            expression();
            _fsp--;

            match(input,EOF,FOLLOW_EOF_in_rhs40); 
            
            		System.out.println("rhs");
            	

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
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:72:1: expression : conditionalExpression ;
    public final void expression() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:73:2: ( conditionalExpression )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:73:4: conditionalExpression
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression55);
            conditionalExpression();
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


    // $ANTLR start conditionalExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:78:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' conditionalExpression )? ;
    public final void conditionalExpression() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:79:2: ( conditionalOrExpression ( '?' expression ':' conditionalExpression )? )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:79:4: conditionalOrExpression ( '?' expression ':' conditionalExpression )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression84);
            conditionalOrExpression();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:80:6: ( '?' expression ':' conditionalExpression )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==QUES) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:80:7: '?' expression ':' conditionalExpression
                    {
                    match(input,QUES,FOLLOW_QUES_in_conditionalExpression92); 
                    pushFollow(FOLLOW_expression_in_conditionalExpression94);
                    expression();
                    _fsp--;

                    match(input,COLON,FOLLOW_COLON_in_conditionalExpression96); 
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression98);
                    conditionalExpression();
                    _fsp--;


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
    // $ANTLR end conditionalExpression


    // $ANTLR start conditionalOrExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:83:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final void conditionalOrExpression() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:84:2: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:84:4: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression111);
            conditionalAndExpression();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:85:9: ( '||' conditionalAndExpression )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==BARBAR) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:85:10: '||' conditionalAndExpression
            	    {
            	    match(input,BARBAR,FOLLOW_BARBAR_in_conditionalOrExpression122); 
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression124);
            	    conditionalAndExpression();
            	    _fsp--;


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
        return ;
    }
    // $ANTLR end conditionalOrExpression


    // $ANTLR start conditionalAndExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:88:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final void conditionalAndExpression() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:89:2: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:89:4: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression138);
            inclusiveOrExpression();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:90:9: ( '&&' inclusiveOrExpression )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==AMPAMP) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:90:10: '&&' inclusiveOrExpression
            	    {
            	    match(input,AMPAMP,FOLLOW_AMPAMP_in_conditionalAndExpression149); 
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression151);
            	    inclusiveOrExpression();
            	    _fsp--;


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
        return ;
    }
    // $ANTLR end conditionalAndExpression


    // $ANTLR start inclusiveOrExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:93:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final void inclusiveOrExpression() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:94:6: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:94:8: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression169);
            exclusiveOrExpression();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:95:9: ( '|' exclusiveOrExpression )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==BAR) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:95:10: '|' exclusiveOrExpression
            	    {
            	    match(input,BAR,FOLLOW_BAR_in_inclusiveOrExpression180); 
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression182);
            	    exclusiveOrExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop4;
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
    // $ANTLR end inclusiveOrExpression


    // $ANTLR start exclusiveOrExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:98:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final void exclusiveOrExpression() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:99:2: ( andExpression ( '^' andExpression )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:99:4: andExpression ( '^' andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression196);
            andExpression();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:100:9: ( '^' andExpression )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==CARET) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:100:10: '^' andExpression
            	    {
            	    match(input,CARET,FOLLOW_CARET_in_exclusiveOrExpression207); 
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression209);
            	    andExpression();
            	    _fsp--;


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
        return ;
    }
    // $ANTLR end exclusiveOrExpression


    // $ANTLR start andExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:103:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final void andExpression() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:104:2: ( equalityExpression ( '&' equalityExpression )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:104:4: equalityExpression ( '&' equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_andExpression222);
            equalityExpression();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:105:9: ( '&' equalityExpression )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==AMP) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:105:10: '&' equalityExpression
            	    {
            	    match(input,AMP,FOLLOW_AMP_in_andExpression233); 
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression235);
            	    equalityExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop6;
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
    // $ANTLR end andExpression


    // $ANTLR start equalityExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:108:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
    public final void equalityExpression() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:109:6: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:109:8: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
            {
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression253);
            instanceOfExpression();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:110:9: ( ( '==' | '!=' ) instanceOfExpression )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==EQEQ||LA8_0==BANGEQ) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:110:11: ( '==' | '!=' ) instanceOfExpression
            	    {
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:110:11: ( '==' | '!=' )
            	    int alt7=2;
            	    int LA7_0 = input.LA(1);

            	    if ( (LA7_0==EQEQ) ) {
            	        alt7=1;
            	    }
            	    else if ( (LA7_0==BANGEQ) ) {
            	        alt7=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("110:11: ( '==' | '!=' )", 7, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt7) {
            	        case 1 :
            	            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:110:12: '=='
            	            {
            	            match(input,EQEQ,FOLLOW_EQEQ_in_equalityExpression266); 
            	            
            	            		System.out.println("Found: ==");
            	                    

            	            }
            	            break;
            	        case 2 :
            	            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:114:11: '!='
            	            {
            	            match(input,BANGEQ,FOLLOW_BANGEQ_in_equalityExpression282); 

            	            }
            	            break;

            	    }

            	    
            	    		System.out.println("Found: !=");
            	    	
            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression302);
            	    instanceOfExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop8;
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
    // $ANTLR end equalityExpression


    // $ANTLR start instanceOfExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:122:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
    public final void instanceOfExpression() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:123:2: ( relationalExpression ( 'instanceof' type )? )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:123:4: relationalExpression ( 'instanceof' type )?
            {
            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression324);
            relationalExpression();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:124:9: ( 'instanceof' type )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==INSTANCEOF) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:124:10: 'instanceof' type
                    {
                    match(input,INSTANCEOF,FOLLOW_INSTANCEOF_in_instanceOfExpression335); 
                    pushFollow(FOLLOW_type_in_instanceOfExpression337);
                    type();
                    _fsp--;


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
    // $ANTLR end instanceOfExpression


    // $ANTLR start relationalExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:127:1: relationalExpression : additiveExpression ( relationalOp additiveExpression )* ;
    public final void relationalExpression() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:128:2: ( additiveExpression ( relationalOp additiveExpression )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:128:4: additiveExpression ( relationalOp additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_relationalExpression350);
            additiveExpression();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:129:9: ( relationalOp additiveExpression )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0>=GT && LA10_0<=LT)) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:129:10: relationalOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression362);
            	    relationalOp();
            	    _fsp--;

            	    pushFollow(FOLLOW_additiveExpression_in_relationalExpression364);
            	    additiveExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop10;
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
    // $ANTLR end relationalExpression


    // $ANTLR start relationalOp
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:133:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );
    public final void relationalOp() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:134:2: ( '<' '=' | '>' '=' | '<' | '>' )
            int alt11=4;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==LT) ) {
                int LA11_1 = input.LA(2);

                if ( (LA11_1==EQ) ) {
                    alt11=1;
                }
                else if ( (LA11_1==EOF||(LA11_1>=IDENTIFIER && LA11_1<=NULL)||LA11_1==BOOLEAN||LA11_1==BYTE||LA11_1==CHAR||LA11_1==DOUBLE||LA11_1==FLOAT||(LA11_1>=INSTANCEOF && LA11_1<=INT)||LA11_1==LONG||LA11_1==NEW||LA11_1==SHORT||LA11_1==SUPER||LA11_1==THIS||LA11_1==VOID||LA11_1==RPAREN||(LA11_1>=LBRACKET && LA11_1<=RBRACKET)||(LA11_1>=COMMA && LA11_1<=DOT)||(LA11_1>=QUES && LA11_1<=PERCENT)||(LA11_1>=BANGEQ && LA11_1<=LT)) ) {
                    alt11=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("133:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );", 11, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA11_0==GT) ) {
                int LA11_2 = input.LA(2);

                if ( (LA11_2==EQ) ) {
                    alt11=2;
                }
                else if ( (LA11_2==EOF||(LA11_2>=IDENTIFIER && LA11_2<=NULL)||LA11_2==BOOLEAN||LA11_2==BYTE||LA11_2==CHAR||LA11_2==DOUBLE||LA11_2==FLOAT||(LA11_2>=INSTANCEOF && LA11_2<=INT)||LA11_2==LONG||LA11_2==NEW||LA11_2==SHORT||LA11_2==SUPER||LA11_2==THIS||LA11_2==VOID||LA11_2==RPAREN||(LA11_2>=LBRACKET && LA11_2<=RBRACKET)||(LA11_2>=COMMA && LA11_2<=DOT)||(LA11_2>=QUES && LA11_2<=PERCENT)||(LA11_2>=BANGEQ && LA11_2<=LT)) ) {
                    alt11=4;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("133:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );", 11, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("133:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:134:7: '<' '='
                    {
                    match(input,LT,FOLLOW_LT_in_relationalOp391); 
                    match(input,EQ,FOLLOW_EQ_in_relationalOp393); 
                    
                    		System.out.println("Found: <=");
                    	

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:138:7: '>' '='
                    {
                    match(input,GT,FOLLOW_GT_in_relationalOp404); 
                    match(input,EQ,FOLLOW_EQ_in_relationalOp406); 
                    
                    		System.out.println("Found: >=");
                    	

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:142:6: '<'
                    {
                    match(input,LT,FOLLOW_LT_in_relationalOp416); 
                    
                    		System.out.println("Found: <");
                    	

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:146:6: '>'
                    {
                    match(input,GT,FOLLOW_GT_in_relationalOp426); 
                    
                    		System.out.println("Found: >");
                    	

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
    // $ANTLR end relationalOp


    // $ANTLR start additiveExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:164:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final void additiveExpression() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:165:2: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:165:4: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression443);
            multiplicativeExpression();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:166:9: ( ( '+' | '-' ) multiplicativeExpression )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>=PLUS && LA12_0<=SUB)) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:167:13: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    if ( (input.LA(1)>=PLUS && input.LA(1)<=SUB) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_additiveExpression470);    throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression520);
            	    multiplicativeExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop12;
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
    // $ANTLR end additiveExpression


    // $ANTLR start multiplicativeExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:174:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final void multiplicativeExpression() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:175:2: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:175:4: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression549);
            unaryExpression();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:176:9: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0>=STAR && LA13_0<=SLASH)||LA13_0==PERCENT) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:177:13: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    if ( (input.LA(1)>=STAR && input.LA(1)<=SLASH)||input.LA(1)==PERCENT ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_multiplicativeExpression576);    throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression644);
            	    unaryExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop13;
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
    // $ANTLR end multiplicativeExpression


    // $ANTLR start expressionList
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:189:1: expressionList : expression ( ',' expression )* ;
    public final void expressionList() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:190:2: ( expression ( ',' expression )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:190:4: expression ( ',' expression )*
            {
            pushFollow(FOLLOW_expression_in_expressionList674);
            expression();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:191:9: ( ',' expression )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==COMMA) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:191:10: ',' expression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_expressionList685); 
            	    pushFollow(FOLLOW_expression_in_expressionList687);
            	    expression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop14;
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
    // $ANTLR end expressionList


    // $ANTLR start unaryExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:194:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );
    public final void unaryExpression() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:199:2: ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus )
            int alt15=5;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt15=1;
                }
                break;
            case SUB:
                {
                alt15=2;
                }
                break;
            case PLUSPLUS:
                {
                alt15=3;
                }
                break;
            case SUBSUB:
                {
                alt15=4;
                }
                break;
            case EOF:
            case IDENTIFIER:
            case INTLITERAL:
            case LONGLITERAL:
            case FLOATLITERAL:
            case DOUBLELITERAL:
            case CHARLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INSTANCEOF:
            case INT:
            case LONG:
            case NEW:
            case SHORT:
            case SUPER:
            case THIS:
            case VOID:
            case RPAREN:
            case LBRACKET:
            case RBRACKET:
            case COMMA:
            case DOT:
            case QUES:
            case COLON:
            case EQEQ:
            case AMPAMP:
            case BARBAR:
            case STAR:
            case SLASH:
            case AMP:
            case BAR:
            case CARET:
            case PERCENT:
            case BANGEQ:
            case GT:
            case LT:
                {
                alt15=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("194:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:199:6: '+' unaryExpression
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_unaryExpression704); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression706);
                    unaryExpression();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:200:6: '-' unaryExpression
                    {
                    match(input,SUB,FOLLOW_SUB_in_unaryExpression713); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression715);
                    unaryExpression();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:201:6: '++' unaryExpression
                    {
                    match(input,PLUSPLUS,FOLLOW_PLUSPLUS_in_unaryExpression722); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression724);
                    unaryExpression();
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:202:6: '--' unaryExpression
                    {
                    match(input,SUBSUB,FOLLOW_SUBSUB_in_unaryExpression731); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression733);
                    unaryExpression();
                    _fsp--;


                    }
                    break;
                case 5 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:203:6: unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression740);
                    unaryExpressionNotPlusMinus();
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
    // $ANTLR end unaryExpression


    // $ANTLR start unaryExpressionNotPlusMinus
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:206:1: unaryExpressionNotPlusMinus : ( | primary ( selector )* ( '++' | '--' )? );
    public final void unaryExpressionNotPlusMinus() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:207:2: ( | primary ( selector )* ( '++' | '--' )? )
            int alt18=2;
            switch ( input.LA(1) ) {
            case STAR:
            case SLASH:
            case PERCENT:
                {
                alt18=1;
                }
                break;
            case PLUS:
            case SUB:
                {
                alt18=1;
                }
                break;
            case LT:
                {
                alt18=1;
                }
                break;
            case GT:
                {
                alt18=1;
                }
                break;
            case INSTANCEOF:
                {
                alt18=1;
                }
                break;
            case EQEQ:
                {
                alt18=1;
                }
                break;
            case BANGEQ:
                {
                alt18=1;
                }
                break;
            case AMP:
                {
                alt18=1;
                }
                break;
            case CARET:
                {
                alt18=1;
                }
                break;
            case BAR:
                {
                alt18=1;
                }
                break;
            case AMPAMP:
                {
                alt18=1;
                }
                break;
            case BARBAR:
                {
                alt18=1;
                }
                break;
            case QUES:
                {
                alt18=1;
                }
                break;
            case EOF:
                {
                alt18=1;
                }
                break;
            case COLON:
                {
                alt18=1;
                }
                break;
            case COMMA:
                {
                alt18=1;
                }
                break;
            case RPAREN:
                {
                alt18=1;
                }
                break;
            case RBRACKET:
                {
                alt18=1;
                }
                break;
            case IDENTIFIER:
            case INTLITERAL:
            case LONGLITERAL:
            case FLOATLITERAL:
            case DOUBLELITERAL:
            case CHARLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case NEW:
            case SHORT:
            case SUPER:
            case THIS:
            case VOID:
            case LBRACKET:
            case DOT:
            case PLUSPLUS:
            case SUBSUB:
                {
                alt18=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("206:1: unaryExpressionNotPlusMinus : ( | primary ( selector )* ( '++' | '--' )? );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:211:2: 
                    {
                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:211:4: primary ( selector )* ( '++' | '--' )?
                    {
                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus764);
                    primary();
                    _fsp--;

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:212:9: ( selector )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( (LA16_0==LBRACKET||LA16_0==DOT) ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:212:10: selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus775);
                    	    selector();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:213:9: ( '++' | '--' )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( ((LA17_0>=PLUSPLUS && LA17_0<=SUBSUB)) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:
                            {
                            if ( (input.LA(1)>=PLUSPLUS && input.LA(1)<=SUBSUB) ) {
                                input.consume();
                                errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse =
                                    new MismatchedSetException(null,input);
                                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_unaryExpressionNotPlusMinus787);    throw mse;
                            }


                            }
                            break;

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
        return ;
    }
    // $ANTLR end unaryExpressionNotPlusMinus


    // $ANTLR start castExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:218:1: castExpression : ( '(' primitiveType | type ')' unaryExpression );
    public final void castExpression() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:219:2: ( '(' primitiveType | type ')' unaryExpression )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==LPAREN) ) {
                alt19=1;
            }
            else if ( (LA19_0==IDENTIFIER||LA19_0==BOOLEAN||LA19_0==BYTE||LA19_0==CHAR||LA19_0==DOUBLE||LA19_0==FLOAT||LA19_0==INT||LA19_0==LONG||LA19_0==SHORT) ) {
                alt19=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("218:1: castExpression : ( '(' primitiveType | type ')' unaryExpression );", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:220:3: '(' primitiveType
                    {
                    match(input,LPAREN,FOLLOW_LPAREN_in_castExpression829); 
                    pushFollow(FOLLOW_primitiveType_in_castExpression831);
                    primitiveType();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:220:23: type ')' unaryExpression
                    {
                    pushFollow(FOLLOW_type_in_castExpression835);
                    type();
                    _fsp--;

                    match(input,RPAREN,FOLLOW_RPAREN_in_castExpression837); 
                    pushFollow(FOLLOW_unaryExpression_in_castExpression839);
                    unaryExpression();
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
    // $ANTLR end castExpression


    // $ANTLR start primary
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:225:1: primary : ( 'this' ( '.' IDENTIFIER )* ( identifierSuffix )? | IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
    public final void primary() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:229:2: ( 'this' ( '.' IDENTIFIER )* ( identifierSuffix )? | IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
            int alt25=7;
            switch ( input.LA(1) ) {
            case THIS:
                {
                alt25=1;
                }
                break;
            case IDENTIFIER:
                {
                alt25=2;
                }
                break;
            case SUPER:
                {
                alt25=3;
                }
                break;
            case INTLITERAL:
            case LONGLITERAL:
            case FLOATLITERAL:
            case DOUBLELITERAL:
            case CHARLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
                {
                alt25=4;
                }
                break;
            case EOF:
            case INSTANCEOF:
            case NEW:
            case RPAREN:
            case LBRACKET:
            case RBRACKET:
            case COMMA:
            case DOT:
            case QUES:
            case COLON:
            case EQEQ:
            case AMPAMP:
            case BARBAR:
            case PLUSPLUS:
            case SUBSUB:
            case PLUS:
            case SUB:
            case STAR:
            case SLASH:
            case AMP:
            case BAR:
            case CARET:
            case PERCENT:
            case BANGEQ:
            case GT:
            case LT:
                {
                alt25=5;
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                alt25=6;
                }
                break;
            case VOID:
                {
                alt25=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("225:1: primary : ( 'this' ( '.' IDENTIFIER )* ( identifierSuffix )? | IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );", 25, 0, input);

                throw nvae;
            }

            switch (alt25) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:231:6: 'this' ( '.' IDENTIFIER )* ( identifierSuffix )?
                    {
                    match(input,THIS,FOLLOW_THIS_in_primary876); 
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:231:13: ( '.' IDENTIFIER )*
                    loop20:
                    do {
                        int alt20=2;
                        int LA20_0 = input.LA(1);

                        if ( (LA20_0==DOT) ) {
                            int LA20_2 = input.LA(2);

                            if ( (LA20_2==IDENTIFIER) ) {
                                alt20=1;
                            }


                        }


                        switch (alt20) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:231:14: '.' IDENTIFIER
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_primary879); 
                    	    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary881); 

                    	    }
                    	    break;

                    	default :
                    	    break loop20;
                        }
                    } while (true);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:231:31: ( identifierSuffix )?
                    int alt21=2;
                    alt21 = dfa21.predict(input);
                    switch (alt21) {
                        case 1 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:231:32: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary886);
                            identifierSuffix();
                            _fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:232:4: IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )?
                    {
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary893); 
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:232:15: ( '.' IDENTIFIER )*
                    loop22:
                    do {
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==DOT) ) {
                            int LA22_2 = input.LA(2);

                            if ( (LA22_2==IDENTIFIER) ) {
                                alt22=1;
                            }


                        }


                        switch (alt22) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:232:16: '.' IDENTIFIER
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_primary896); 
                    	    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary898); 

                    	    }
                    	    break;

                    	default :
                    	    break loop22;
                        }
                    } while (true);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:232:33: ( identifierSuffix )?
                    int alt23=2;
                    alt23 = dfa23.predict(input);
                    switch (alt23) {
                        case 1 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:232:34: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary903);
                            identifierSuffix();
                            _fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:233:4: 'super' superSuffix
                    {
                    match(input,SUPER,FOLLOW_SUPER_in_primary910); 
                    pushFollow(FOLLOW_superSuffix_in_primary912);
                    superSuffix();
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:234:4: literal
                    {
                    pushFollow(FOLLOW_literal_in_primary917);
                    literal();
                    _fsp--;


                    }
                    break;
                case 5 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:235:4: creator
                    {
                    pushFollow(FOLLOW_creator_in_primary922);
                    creator();
                    _fsp--;


                    }
                    break;
                case 6 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:236:4: primitiveType ( '[' ']' )* '.' 'class'
                    {
                    pushFollow(FOLLOW_primitiveType_in_primary927);
                    primitiveType();
                    _fsp--;

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:236:18: ( '[' ']' )*
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( (LA24_0==LBRACKET) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:236:19: '[' ']'
                    	    {
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_primary930); 
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_primary932); 

                    	    }
                    	    break;

                    	default :
                    	    break loop24;
                        }
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_primary936); 
                    match(input,CLASS,FOLLOW_CLASS_in_primary938); 

                    }
                    break;
                case 7 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:237:4: 'void' '.' 'class'
                    {
                    match(input,VOID,FOLLOW_VOID_in_primary943); 
                    match(input,DOT,FOLLOW_DOT_in_primary945); 
                    match(input,CLASS,FOLLOW_CLASS_in_primary947); 

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


    // $ANTLR start superSuffix
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:241:1: superSuffix : ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? );
    public final void superSuffix() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:242:2: ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? )
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==LPAREN) ) {
                alt28=1;
            }
            else if ( (LA28_0==DOT) ) {
                alt28=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("241:1: superSuffix : ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? );", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:242:4: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_superSuffix964);
                    arguments();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:243:4: '.' ( typeArguments )? IDENTIFIER ( arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_superSuffix969); 
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:243:8: ( typeArguments )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);

                    if ( (LA26_0==LT) ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:243:9: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_superSuffix972);
                            typeArguments();
                            _fsp--;


                            }
                            break;

                    }

                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_superSuffix976); 
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:243:36: ( arguments )?
                    int alt27=2;
                    int LA27_0 = input.LA(1);

                    if ( (LA27_0==LPAREN) ) {
                        alt27=1;
                    }
                    switch (alt27) {
                        case 1 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:243:37: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix979);
                            arguments();
                            _fsp--;


                            }
                            break;

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
        return ;
    }
    // $ANTLR end superSuffix


    // $ANTLR start identifierSuffix
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:246:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | arguments | '.' 'class' | '.' 'this' );
    public final void identifierSuffix() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:247:2: ( ( '[' ']' )+ '.' 'class' | arguments | '.' 'class' | '.' 'this' )
            int alt30=4;
            switch ( input.LA(1) ) {
            case LBRACKET:
                {
                alt30=1;
                }
                break;
            case LPAREN:
                {
                alt30=2;
                }
                break;
            case DOT:
                {
                int LA30_3 = input.LA(2);

                if ( (LA30_3==CLASS) ) {
                    alt30=3;
                }
                else if ( (LA30_3==THIS) ) {
                    alt30=4;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("246:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | arguments | '.' 'class' | '.' 'this' );", 30, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("246:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | arguments | '.' 'class' | '.' 'this' );", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:247:4: ( '[' ']' )+ '.' 'class'
                    {
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:247:4: ( '[' ']' )+
                    int cnt29=0;
                    loop29:
                    do {
                        int alt29=2;
                        int LA29_0 = input.LA(1);

                        if ( (LA29_0==LBRACKET) ) {
                            alt29=1;
                        }


                        switch (alt29) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:247:5: '[' ']'
                    	    {
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_identifierSuffix993); 
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_identifierSuffix995); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt29 >= 1 ) break loop29;
                                EarlyExitException eee =
                                    new EarlyExitException(29, input);
                                throw eee;
                        }
                        cnt29++;
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix1007); 
                    match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix1009); 

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:250:4: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_identifierSuffix1016);
                    arguments();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:251:4: '.' 'class'
                    {
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix1021); 
                    match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix1023); 

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:253:4: '.' 'this'
                    {
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix1030); 
                    match(input,THIS,FOLLOW_THIS_in_identifierSuffix1032); 

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
    // $ANTLR end identifierSuffix


    // $ANTLR start selector
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:259:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '[' expression ']' );
    public final void selector() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:260:2: ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '[' expression ']' )
            int alt32=4;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==DOT) ) {
                switch ( input.LA(2) ) {
                case THIS:
                    {
                    alt32=2;
                    }
                    break;
                case SUPER:
                    {
                    alt32=3;
                    }
                    break;
                case IDENTIFIER:
                    {
                    alt32=1;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("259:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '[' expression ']' );", 32, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA32_0==LBRACKET) ) {
                alt32=4;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("259:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '[' expression ']' );", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:260:4: '.' IDENTIFIER ( arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector1048); 
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_selector1050); 
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:260:19: ( arguments )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==LPAREN) ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:260:20: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector1053);
                            arguments();
                            _fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:261:4: '.' 'this'
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector1060); 
                    match(input,THIS,FOLLOW_THIS_in_selector1062); 

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:262:4: '.' 'super' superSuffix
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector1067); 
                    match(input,SUPER,FOLLOW_SUPER_in_selector1069); 
                    pushFollow(FOLLOW_superSuffix_in_selector1071);
                    superSuffix();
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:264:4: '[' expression ']'
                    {
                    match(input,LBRACKET,FOLLOW_LBRACKET_in_selector1078); 
                    pushFollow(FOLLOW_expression_in_selector1080);
                    expression();
                    _fsp--;

                    match(input,RBRACKET,FOLLOW_RBRACKET_in_selector1082); 

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
    // $ANTLR end selector


    // $ANTLR start creator
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:267:1: creator : ( | 'new' classOrInterfaceType classCreatorRest );
    public final void creator() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:268:2: ( | 'new' classOrInterfaceType classCreatorRest )
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==EOF||LA33_0==INSTANCEOF||LA33_0==RPAREN||(LA33_0>=LBRACKET && LA33_0<=RBRACKET)||(LA33_0>=COMMA && LA33_0<=DOT)||(LA33_0>=QUES && LA33_0<=PERCENT)||(LA33_0>=BANGEQ && LA33_0<=LT)) ) {
                alt33=1;
            }
            else if ( (LA33_0==NEW) ) {
                alt33=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("267:1: creator : ( | 'new' classOrInterfaceType classCreatorRest );", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:269:2: 
                    {
                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:269:6: 'new' classOrInterfaceType classCreatorRest
                    {
                    match(input,NEW,FOLLOW_NEW_in_creator1102); 
                    pushFollow(FOLLOW_classOrInterfaceType_in_creator1104);
                    classOrInterfaceType();
                    _fsp--;

                    pushFollow(FOLLOW_classCreatorRest_in_creator1106);
                    classCreatorRest();
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
    // $ANTLR end creator


    // $ANTLR start typeList
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:274:1: typeList : type ( ',' type )* ;
    public final void typeList() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:275:2: ( type ( ',' type )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:275:4: type ( ',' type )*
            {
            pushFollow(FOLLOW_type_in_typeList1121);
            type();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:275:9: ( ',' type )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==COMMA) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:275:10: ',' type
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeList1124); 
            	    pushFollow(FOLLOW_type_in_typeList1126);
            	    type();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop34;
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
    // $ANTLR end typeList


    // $ANTLR start classCreatorRest
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:278:1: classCreatorRest : arguments ;
    public final void classCreatorRest() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:279:2: ( arguments )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:279:4: arguments
            {
            pushFollow(FOLLOW_arguments_in_classCreatorRest1141);
            arguments();
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
    // $ANTLR end classCreatorRest


    // $ANTLR start variableInitializer
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:301:1: variableInitializer : expression ;
    public final void variableInitializer() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:302:2: ( expression )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:304:2: expression
            {
            pushFollow(FOLLOW_expression_in_variableInitializer1180);
            expression();
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
    // $ANTLR end variableInitializer


    // $ANTLR start createdName
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:318:1: createdName : ( classOrInterfaceType | primitiveType );
    public final void createdName() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:319:2: ( classOrInterfaceType | primitiveType )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==IDENTIFIER) ) {
                alt35=1;
            }
            else if ( (LA35_0==BOOLEAN||LA35_0==BYTE||LA35_0==CHAR||LA35_0==DOUBLE||LA35_0==FLOAT||LA35_0==INT||LA35_0==LONG||LA35_0==SHORT) ) {
                alt35=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("318:1: createdName : ( classOrInterfaceType | primitiveType );", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:319:4: classOrInterfaceType
                    {
                    pushFollow(FOLLOW_classOrInterfaceType_in_createdName1196);
                    classOrInterfaceType();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:320:4: primitiveType
                    {
                    pushFollow(FOLLOW_primitiveType_in_createdName1201);
                    primitiveType();
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
    // $ANTLR end createdName


    // $ANTLR start arguments
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:346:1: arguments : '(' ( expressionList )? ')' ;
    public final void arguments() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:347:2: ( '(' ( expressionList )? ')' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:347:4: '(' ( expressionList )? ')'
            {
            match(input,LPAREN,FOLLOW_LPAREN_in_arguments1224); 
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:347:8: ( expressionList )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==INSTANCEOF||LA36_0==COMMA||LA36_0==QUES||(LA36_0>=EQEQ && LA36_0<=PERCENT)||(LA36_0>=BANGEQ && LA36_0<=LT)) ) {
                alt36=1;
            }
            else if ( ((LA36_0>=IDENTIFIER && LA36_0<=NULL)||LA36_0==BOOLEAN||LA36_0==BYTE||LA36_0==CHAR||LA36_0==DOUBLE||LA36_0==FLOAT||LA36_0==INT||LA36_0==LONG||LA36_0==NEW||LA36_0==SHORT||LA36_0==SUPER||LA36_0==THIS||LA36_0==VOID||LA36_0==RPAREN||LA36_0==LBRACKET||LA36_0==DOT) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:347:9: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments1227);
                    expressionList();
                    _fsp--;


                    }
                    break;

            }

            match(input,RPAREN,FOLLOW_RPAREN_in_arguments1231); 

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
    // $ANTLR end arguments


    // $ANTLR start type
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:350:1: type : ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* );
    public final void type() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:351:2: ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* )
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==IDENTIFIER) ) {
                alt39=1;
            }
            else if ( (LA39_0==BOOLEAN||LA39_0==BYTE||LA39_0==CHAR||LA39_0==DOUBLE||LA39_0==FLOAT||LA39_0==INT||LA39_0==LONG||LA39_0==SHORT) ) {
                alt39=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("350:1: type : ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* );", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:351:4: classOrInterfaceType ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_classOrInterfaceType_in_type1243);
                    classOrInterfaceType();
                    _fsp--;

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:351:25: ( '[' ']' )*
                    loop37:
                    do {
                        int alt37=2;
                        int LA37_0 = input.LA(1);

                        if ( (LA37_0==LBRACKET) ) {
                            alt37=1;
                        }


                        switch (alt37) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:351:26: '[' ']'
                    	    {
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_type1246); 
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_type1248); 

                    	    }
                    	    break;

                    	default :
                    	    break loop37;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:352:6: primitiveType ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_primitiveType_in_type1258);
                    primitiveType();
                    _fsp--;

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:352:20: ( '[' ']' )*
                    loop38:
                    do {
                        int alt38=2;
                        int LA38_0 = input.LA(1);

                        if ( (LA38_0==LBRACKET) ) {
                            alt38=1;
                        }


                        switch (alt38) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:352:21: '[' ']'
                    	    {
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_type1261); 
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_type1263); 

                    	    }
                    	    break;

                    	default :
                    	    break loop38;
                        }
                    } while (true);


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
    // $ANTLR end type


    // $ANTLR start classOrInterfaceType
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:356:1: classOrInterfaceType : IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )* ;
    public final void classOrInterfaceType() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:357:2: ( IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:357:4: IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )*
            {
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classOrInterfaceType1278); 
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:357:15: ( typeArguments )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==LT) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:357:16: typeArguments
                    {
                    pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType1281);
                    typeArguments();
                    _fsp--;


                    }
                    break;

            }

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:357:32: ( '.' IDENTIFIER ( typeArguments )? )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( (LA42_0==DOT) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:357:33: '.' IDENTIFIER ( typeArguments )?
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_classOrInterfaceType1286); 
            	    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classOrInterfaceType1288); 
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:357:48: ( typeArguments )?
            	    int alt41=2;
            	    int LA41_0 = input.LA(1);

            	    if ( (LA41_0==LT) ) {
            	        alt41=1;
            	    }
            	    switch (alt41) {
            	        case 1 :
            	            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:357:49: typeArguments
            	            {
            	            pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType1291);
            	            typeArguments();
            	            _fsp--;


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop42;
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
    // $ANTLR end classOrInterfaceType


    // $ANTLR start typeArguments
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:360:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
    public final void typeArguments() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:361:2: ( '<' typeArgument ( ',' typeArgument )* '>' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:361:4: '<' typeArgument ( ',' typeArgument )* '>'
            {
            match(input,LT,FOLLOW_LT_in_typeArguments1307); 
            pushFollow(FOLLOW_typeArgument_in_typeArguments1309);
            typeArgument();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:361:21: ( ',' typeArgument )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==COMMA) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:361:22: ',' typeArgument
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeArguments1312); 
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments1314);
            	    typeArgument();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop43;
                }
            } while (true);

            match(input,GT,FOLLOW_GT_in_typeArguments1318); 

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
    // $ANTLR end typeArguments


    // $ANTLR start typeArgument
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:364:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
    public final void typeArgument() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:365:2: ( type | '?' ( ( 'extends' | 'super' ) type )? )
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==IDENTIFIER||LA45_0==BOOLEAN||LA45_0==BYTE||LA45_0==CHAR||LA45_0==DOUBLE||LA45_0==FLOAT||LA45_0==INT||LA45_0==LONG||LA45_0==SHORT) ) {
                alt45=1;
            }
            else if ( (LA45_0==QUES) ) {
                alt45=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("364:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:365:6: type
                    {
                    pushFollow(FOLLOW_type_in_typeArgument1332);
                    type();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:366:6: '?' ( ( 'extends' | 'super' ) type )?
                    {
                    match(input,QUES,FOLLOW_QUES_in_typeArgument1339); 
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:367:9: ( ( 'extends' | 'super' ) type )?
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( (LA44_0==EXTENDS||LA44_0==SUPER) ) {
                        alt44=1;
                    }
                    switch (alt44) {
                        case 1 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:368:13: ( 'extends' | 'super' ) type
                            {
                            if ( input.LA(1)==EXTENDS||input.LA(1)==SUPER ) {
                                input.consume();
                                errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse =
                                    new MismatchedSetException(null,input);
                                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_typeArgument1363);    throw mse;
                            }

                            pushFollow(FOLLOW_type_in_typeArgument1407);
                            type();
                            _fsp--;


                            }
                            break;

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
        return ;
    }
    // $ANTLR end typeArgument


    // $ANTLR start primitiveType
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:376:1: primitiveType : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
    public final void primitiveType() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:377:2: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:
            {
            if ( input.LA(1)==BOOLEAN||input.LA(1)==BYTE||input.LA(1)==CHAR||input.LA(1)==DOUBLE||input.LA(1)==FLOAT||input.LA(1)==INT||input.LA(1)==LONG||input.LA(1)==SHORT ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_primitiveType0);    throw mse;
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
    // $ANTLR end primitiveType


    // $ANTLR start literal
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:387:1: literal : ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL );
    public final void literal() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:388:2: ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:
            {
            if ( (input.LA(1)>=INTLITERAL && input.LA(1)<=NULL) ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_literal0);    throw mse;
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
    // $ANTLR end literal


    protected DFA21 dfa21 = new DFA21(this);
    protected DFA23 dfa23 = new DFA23(this);
    static final String DFA21_eotS =
        "\10\uffff";
    static final String DFA21_eofS =
        "\1\4\4\uffff\1\4\2\uffff";
    static final String DFA21_minS =
        "\1\60\1\4\1\uffff\1\4\1\uffff\1\60\1\uffff\1\4";
    static final String DFA21_maxS =
        "\2\160\1\uffff\1\101\1\uffff\1\160\1\uffff\1\101";
    static final String DFA21_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff\1\1\1\uffff";
    static final String DFA21_specialS =
        "\10\uffff}>";
    static final String[] DFA21_transitionS = {
            "\1\4\30\uffff\1\2\1\4\2\uffff\1\1\1\4\1\uffff\1\4\1\3\4\uffff"+
            "\17\4\11\uffff\3\4",
            "\12\4\13\uffff\1\4\1\uffff\1\4\2\uffff\1\4\5\uffff\1\4\5\uffff"+
            "\1\4\5\uffff\2\4\1\uffff\1\4\1\uffff\1\4\5\uffff\1\4\2\uffff"+
            "\1\4\2\uffff\1\4\4\uffff\1\4\6\uffff\1\4\1\5\2\uffff\1\4\4\uffff"+
            "\1\4\1\uffff\15\4\11\uffff\3\4",
            "",
            "\1\4\32\uffff\1\6\36\uffff\1\4\2\uffff\1\6",
            "",
            "\1\4\31\uffff\1\4\2\uffff\1\1\1\4\1\uffff\1\4\1\7\4\uffff\17"+
            "\4\11\uffff\3\4",
            "",
            "\1\4\32\uffff\1\6\36\uffff\1\4\2\uffff\1\4"
    };

    static final short[] DFA21_eot = DFA.unpackEncodedString(DFA21_eotS);
    static final short[] DFA21_eof = DFA.unpackEncodedString(DFA21_eofS);
    static final char[] DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
    static final char[] DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
    static final short[] DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS);
    static final short[] DFA21_special = DFA.unpackEncodedString(DFA21_specialS);
    static final short[][] DFA21_transition;

    static {
        int numStates = DFA21_transitionS.length;
        DFA21_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA21_transition[i] = DFA.unpackEncodedString(DFA21_transitionS[i]);
        }
    }

    class DFA21 extends DFA {

        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA21_eot;
            this.eof = DFA21_eof;
            this.min = DFA21_min;
            this.max = DFA21_max;
            this.accept = DFA21_accept;
            this.special = DFA21_special;
            this.transition = DFA21_transition;
        }
        public String getDescription() {
            return "231:31: ( identifierSuffix )?";
        }
    }
    static final String DFA23_eotS =
        "\10\uffff";
    static final String DFA23_eofS =
        "\1\4\4\uffff\1\4\2\uffff";
    static final String DFA23_minS =
        "\1\60\1\4\1\uffff\1\4\1\uffff\1\60\1\uffff\1\4";
    static final String DFA23_maxS =
        "\2\160\1\uffff\1\101\1\uffff\1\160\1\uffff\1\101";
    static final String DFA23_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff\1\1\1\uffff";
    static final String DFA23_specialS =
        "\10\uffff}>";
    static final String[] DFA23_transitionS = {
            "\1\4\30\uffff\1\2\1\4\2\uffff\1\1\1\4\1\uffff\1\4\1\3\4\uffff"+
            "\17\4\11\uffff\3\4",
            "\12\4\13\uffff\1\4\1\uffff\1\4\2\uffff\1\4\5\uffff\1\4\5\uffff"+
            "\1\4\5\uffff\2\4\1\uffff\1\4\1\uffff\1\4\5\uffff\1\4\2\uffff"+
            "\1\4\2\uffff\1\4\4\uffff\1\4\6\uffff\1\4\1\5\2\uffff\1\4\4\uffff"+
            "\1\4\1\uffff\15\4\11\uffff\3\4",
            "",
            "\1\4\32\uffff\1\6\36\uffff\1\4\2\uffff\1\6",
            "",
            "\1\4\31\uffff\1\4\2\uffff\1\1\1\4\1\uffff\1\4\1\7\4\uffff\17"+
            "\4\11\uffff\3\4",
            "",
            "\1\4\32\uffff\1\6\36\uffff\1\4\2\uffff\1\4"
    };

    static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
    static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
    static final char[] DFA23_min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
    static final char[] DFA23_max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
    static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
    static final short[] DFA23_special = DFA.unpackEncodedString(DFA23_specialS);
    static final short[][] DFA23_transition;

    static {
        int numStates = DFA23_transitionS.length;
        DFA23_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
        }
    }

    class DFA23 extends DFA {

        public DFA23(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 23;
            this.eot = DFA23_eot;
            this.eof = DFA23_eof;
            this.min = DFA23_min;
            this.max = DFA23_max;
            this.accept = DFA23_accept;
            this.special = DFA23_special;
            this.transition = DFA23_transition;
        }
        public String getDescription() {
            return "232:33: ( identifierSuffix )?";
        }
    }
 

    public static final BitSet FOLLOW_expression_in_rhs38 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_rhs40 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression55 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression84 = new BitSet(new long[]{0x0000000000000002L,0x0000000000400000L});
    public static final BitSet FOLLOW_QUES_in_conditionalExpression92 = new BitSet(new long[]{0x482B04104A003FF0L,0x0001C01FFFC22042L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression94 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression96 = new BitSet(new long[]{0x482B04104A003FF0L,0x0001C01FFF422042L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression98 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression111 = new BitSet(new long[]{0x0000000000000002L,0x0000000004000000L});
    public static final BitSet FOLLOW_BARBAR_in_conditionalOrExpression122 = new BitSet(new long[]{0x482B04104A003FF2L,0x0001C01FFF022042L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression124 = new BitSet(new long[]{0x0000000000000002L,0x0000000004000000L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression138 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_AMPAMP_in_conditionalAndExpression149 = new BitSet(new long[]{0x482B04104A003FF2L,0x0001C01FFB022042L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression151 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression169 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_BAR_in_inclusiveOrExpression180 = new BitSet(new long[]{0x482B04104A003FF2L,0x0001C01FF9022042L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression182 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression196 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_CARET_in_exclusiveOrExpression207 = new BitSet(new long[]{0x482B04104A003FF2L,0x0001C01BF9022042L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression209 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression222 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_AMP_in_andExpression233 = new BitSet(new long[]{0x482B04104A003FF2L,0x0001C013F9022042L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression235 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression253 = new BitSet(new long[]{0x0000000000000002L,0x0000400001000000L});
    public static final BitSet FOLLOW_EQEQ_in_equalityExpression266 = new BitSet(new long[]{0x482B04104A003FF2L,0x0001C011F9022042L});
    public static final BitSet FOLLOW_BANGEQ_in_equalityExpression282 = new BitSet(new long[]{0x482B04104A003FF2L,0x0001C011F9022042L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression302 = new BitSet(new long[]{0x0000000000000002L,0x0000400001000000L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression324 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_INSTANCEOF_in_instanceOfExpression335 = new BitSet(new long[]{0x080A04104A000010L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression350 = new BitSet(new long[]{0x0000000000000002L,0x0001800000000000L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression362 = new BitSet(new long[]{0x482A04104A003FF2L,0x00018011F8022042L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression364 = new BitSet(new long[]{0x0000000000000002L,0x0001800000000000L});
    public static final BitSet FOLLOW_LT_in_relationalOp391 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_EQ_in_relationalOp393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_relationalOp404 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_EQ_in_relationalOp406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_relationalOp416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_relationalOp426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression443 = new BitSet(new long[]{0x0000000000000002L,0x0000000060000000L});
    public static final BitSet FOLLOW_set_in_additiveExpression470 = new BitSet(new long[]{0x482A04104A003FF2L,0x00000011F8022042L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression520 = new BitSet(new long[]{0x0000000000000002L,0x0000000060000000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression549 = new BitSet(new long[]{0x0000000000000002L,0x0000001180000000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression576 = new BitSet(new long[]{0x482A04104A003FF2L,0x00000011F8022042L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression644 = new BitSet(new long[]{0x0000000000000002L,0x0000001180000000L});
    public static final BitSet FOLLOW_expression_in_expressionList674 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_expressionList685 = new BitSet(new long[]{0x482B04104A003FF2L,0x0001C01FFF432042L});
    public static final BitSet FOLLOW_expression_in_expressionList687 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression704 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078022042L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUB_in_unaryExpression713 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078022042L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSPLUS_in_unaryExpression722 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078022042L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSUB_in_unaryExpression731 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078022042L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus764 = new BitSet(new long[]{0x0000000000000002L,0x0000000018022000L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus775 = new BitSet(new long[]{0x0000000000000002L,0x0000000018022000L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_castExpression829 = new BitSet(new long[]{0x080A04104A000000L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression831 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_castExpression835 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RPAREN_in_castExpression837 = new BitSet(new long[]{0x482A04104A003FF2L,0x0000000078022042L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THIS_in_primary876 = new BitSet(new long[]{0x0000000000000002L,0x0000000000022200L});
    public static final BitSet FOLLOW_DOT_in_primary879 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary881 = new BitSet(new long[]{0x0000000000000002L,0x0000000000022200L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary893 = new BitSet(new long[]{0x0000000000000002L,0x0000000000022200L});
    public static final BitSet FOLLOW_DOT_in_primary896 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary898 = new BitSet(new long[]{0x0000000000000002L,0x0000000000022200L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_in_primary910 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020200L});
    public static final BitSet FOLLOW_superSuffix_in_primary912 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_creator_in_primary922 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary927 = new BitSet(new long[]{0x0000000000000000L,0x0000000000022000L});
    public static final BitSet FOLLOW_LBRACKET_in_primary930 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_RBRACKET_in_primary932 = new BitSet(new long[]{0x0000000000000000L,0x0000000000022000L});
    public static final BitSet FOLLOW_DOT_in_primary936 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_CLASS_in_primary938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VOID_in_primary943 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_DOT_in_primary945 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_CLASS_in_primary947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix969 = new BitSet(new long[]{0x0000000000000010L,0x0001000000000000L});
    public static final BitSet FOLLOW_typeArguments_in_superSuffix972 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_superSuffix976 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_arguments_in_superSuffix979 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_identifierSuffix993 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_RBRACKET_in_identifierSuffix995 = new BitSet(new long[]{0x0000000000000000L,0x0000000000022000L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix1007 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_CLASS_in_identifierSuffix1009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix1016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix1021 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_CLASS_in_identifierSuffix1023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix1030 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_THIS_in_identifierSuffix1032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector1048 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_selector1050 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_arguments_in_selector1053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector1060 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_THIS_in_selector1062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector1067 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_SUPER_in_selector1069 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020200L});
    public static final BitSet FOLLOW_superSuffix_in_selector1071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_selector1078 = new BitSet(new long[]{0x482B04104A003FF0L,0x0001C01FFF426042L});
    public static final BitSet FOLLOW_expression_in_selector1080 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_RBRACKET_in_selector1082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_creator1102 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_creator1104 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator1106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList1121 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_typeList1124 = new BitSet(new long[]{0x080A04104A000010L});
    public static final BitSet FOLLOW_type_in_typeList1126 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest1141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer1180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_createdName1196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName1201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_arguments1224 = new BitSet(new long[]{0x482B04104A003FF0L,0x0001C01FFF432442L});
    public static final BitSet FOLLOW_expressionList_in_arguments1227 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RPAREN_in_arguments1231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_type1243 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_LBRACKET_in_type1246 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_RBRACKET_in_type1248 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_primitiveType_in_type1258 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_LBRACKET_in_type1261 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_RBRACKET_in_type1263 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classOrInterfaceType1278 = new BitSet(new long[]{0x0000000000000002L,0x0001000000020000L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType1281 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_DOT_in_classOrInterfaceType1286 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classOrInterfaceType1288 = new BitSet(new long[]{0x0000000000000002L,0x0001000000020000L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType1291 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_LT_in_typeArguments1307 = new BitSet(new long[]{0x080A04104A000010L,0x0000000000400000L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments1309 = new BitSet(new long[]{0x0000000000000000L,0x0000800000010000L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments1312 = new BitSet(new long[]{0x080A04104A000010L,0x0000000000400000L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments1314 = new BitSet(new long[]{0x0000000000000000L,0x0000800000010000L});
    public static final BitSet FOLLOW_GT_in_typeArguments1318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument1332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUES_in_typeArgument1339 = new BitSet(new long[]{0x4000008000000002L});
    public static final BitSet FOLLOW_set_in_typeArgument1363 = new BitSet(new long[]{0x080A04104A000010L});
    public static final BitSet FOLLOW_type_in_typeArgument1407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_primitiveType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(new long[]{0x0000000000000002L});

}