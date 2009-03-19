// $ANTLR 3.0.1 C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g 2009-03-19 13:07:56

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
    public String getGrammarFileName() { return "C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g"; }

    
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:65:1: rhs : expression EOF ;
    public final void rhs() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:66:2: ( expression EOF )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:66:4: expression EOF
            {
            pushFollow(FOLLOW_expression_in_rhs38);
            expression();
            _fsp--;

            match(input,EOF,FOLLOW_EOF_in_rhs40); 

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:69:1: expression : conditionalExpression ;
    public final void expression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:70:2: ( conditionalExpression )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:70:4: conditionalExpression
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression52);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:75:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' conditionalExpression )? ;
    public final void conditionalExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:76:2: ( conditionalOrExpression ( '?' expression ':' conditionalExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:76:4: conditionalOrExpression ( '?' expression ':' conditionalExpression )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression81);
            conditionalOrExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:77:6: ( '?' expression ':' conditionalExpression )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==QUES) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:77:7: '?' expression ':' conditionalExpression
                    {
                    match(input,QUES,FOLLOW_QUES_in_conditionalExpression89); 
                    pushFollow(FOLLOW_expression_in_conditionalExpression91);
                    expression();
                    _fsp--;

                    match(input,COLON,FOLLOW_COLON_in_conditionalExpression93); 
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression95);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:80:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final void conditionalOrExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:81:2: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:81:4: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression108);
            conditionalAndExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:82:9: ( '||' conditionalAndExpression )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==BARBAR) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:82:10: '||' conditionalAndExpression
            	    {
            	    match(input,BARBAR,FOLLOW_BARBAR_in_conditionalOrExpression119); 
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression121);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:85:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final void conditionalAndExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:86:2: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:86:4: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression135);
            inclusiveOrExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:87:9: ( '&&' inclusiveOrExpression )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==AMPAMP) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:87:10: '&&' inclusiveOrExpression
            	    {
            	    match(input,AMPAMP,FOLLOW_AMPAMP_in_conditionalAndExpression146); 
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression148);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:90:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final void inclusiveOrExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:91:6: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:91:8: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression166);
            exclusiveOrExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:92:9: ( '|' exclusiveOrExpression )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==BAR) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:92:10: '|' exclusiveOrExpression
            	    {
            	    match(input,BAR,FOLLOW_BAR_in_inclusiveOrExpression177); 
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression179);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:95:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final void exclusiveOrExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:96:2: ( andExpression ( '^' andExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:96:4: andExpression ( '^' andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression193);
            andExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:97:9: ( '^' andExpression )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==CARET) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:97:10: '^' andExpression
            	    {
            	    match(input,CARET,FOLLOW_CARET_in_exclusiveOrExpression204); 
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression206);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:100:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final void andExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:101:2: ( equalityExpression ( '&' equalityExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:101:4: equalityExpression ( '&' equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_andExpression219);
            equalityExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:102:9: ( '&' equalityExpression )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==AMP) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:102:10: '&' equalityExpression
            	    {
            	    match(input,AMP,FOLLOW_AMP_in_andExpression230); 
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression232);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:105:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
    public final void equalityExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:106:6: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:106:8: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
            {
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression250);
            instanceOfExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:107:9: ( ( '==' | '!=' ) instanceOfExpression )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==EQEQ||LA8_0==BANGEQ) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:107:11: ( '==' | '!=' ) instanceOfExpression
            	    {
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:107:11: ( '==' | '!=' )
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
            	            new NoViableAltException("107:11: ( '==' | '!=' )", 7, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt7) {
            	        case 1 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:107:12: '=='
            	            {
            	            match(input,EQEQ,FOLLOW_EQEQ_in_equalityExpression263); 
            	            
            	            		System.out.println("Found: ==");
            	                    

            	            }
            	            break;
            	        case 2 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:111:11: '!='
            	            {
            	            match(input,BANGEQ,FOLLOW_BANGEQ_in_equalityExpression279); 

            	            }
            	            break;

            	    }

            	    
            	    		System.out.println("Found: !=");
            	    	
            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression299);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:119:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
    public final void instanceOfExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:120:2: ( relationalExpression ( 'instanceof' type )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:120:4: relationalExpression ( 'instanceof' type )?
            {
            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression321);
            relationalExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:121:9: ( 'instanceof' type )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==INSTANCEOF) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:121:10: 'instanceof' type
                    {
                    match(input,INSTANCEOF,FOLLOW_INSTANCEOF_in_instanceOfExpression332); 
                    pushFollow(FOLLOW_type_in_instanceOfExpression334);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:124:1: relationalExpression : additiveExpression ( relationalOp additiveExpression )* ;
    public final void relationalExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:125:2: ( additiveExpression ( relationalOp additiveExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:125:4: additiveExpression ( relationalOp additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_relationalExpression347);
            additiveExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:126:9: ( relationalOp additiveExpression )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0>=GT && LA10_0<=LT)) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:126:10: relationalOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression359);
            	    relationalOp();
            	    _fsp--;

            	    pushFollow(FOLLOW_additiveExpression_in_relationalExpression361);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:130:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );
    public final void relationalOp() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:131:2: ( '<' '=' | '>' '=' | '<' | '>' )
            int alt11=4;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==LT) ) {
                int LA11_1 = input.LA(2);

                if ( (LA11_1==EQ) ) {
                    alt11=1;
                }
                else if ( ((LA11_1>=IDENTIFIER && LA11_1<=NULL)||LA11_1==BOOLEAN||LA11_1==BYTE||LA11_1==CHAR||LA11_1==DOUBLE||LA11_1==FLOAT||LA11_1==INT||LA11_1==LONG||LA11_1==NEW||LA11_1==SHORT||LA11_1==SUPER||LA11_1==THIS||LA11_1==VOID||(LA11_1>=PLUSPLUS && LA11_1<=SUB)) ) {
                    alt11=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("130:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );", 11, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA11_0==GT) ) {
                int LA11_2 = input.LA(2);

                if ( (LA11_2==EQ) ) {
                    alt11=2;
                }
                else if ( ((LA11_2>=IDENTIFIER && LA11_2<=NULL)||LA11_2==BOOLEAN||LA11_2==BYTE||LA11_2==CHAR||LA11_2==DOUBLE||LA11_2==FLOAT||LA11_2==INT||LA11_2==LONG||LA11_2==NEW||LA11_2==SHORT||LA11_2==SUPER||LA11_2==THIS||LA11_2==VOID||(LA11_2>=PLUSPLUS && LA11_2<=SUB)) ) {
                    alt11=4;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("130:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );", 11, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("130:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:131:7: '<' '='
                    {
                    match(input,LT,FOLLOW_LT_in_relationalOp388); 
                    match(input,EQ,FOLLOW_EQ_in_relationalOp390); 
                    
                    		System.out.println("Found: <=");
                    	

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:135:7: '>' '='
                    {
                    match(input,GT,FOLLOW_GT_in_relationalOp401); 
                    match(input,EQ,FOLLOW_EQ_in_relationalOp403); 
                    
                    		System.out.println("Found: >=");
                    	

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:139:6: '<'
                    {
                    match(input,LT,FOLLOW_LT_in_relationalOp413); 
                    
                    		System.out.println("Found: <");
                    	

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:143:6: '>'
                    {
                    match(input,GT,FOLLOW_GT_in_relationalOp423); 
                    
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:161:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final void additiveExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:162:2: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:162:4: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression440);
            multiplicativeExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:163:9: ( ( '+' | '-' ) multiplicativeExpression )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>=PLUS && LA12_0<=SUB)) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:164:13: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    if ( (input.LA(1)>=PLUS && input.LA(1)<=SUB) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_additiveExpression467);    throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression517);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:171:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final void multiplicativeExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:172:2: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:172:4: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression546);
            unaryExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:173:9: ( ( '*' | '/' | '%' ) unaryExpression )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0>=STAR && LA13_0<=SLASH)||LA13_0==PERCENT) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:174:13: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    if ( (input.LA(1)>=STAR && input.LA(1)<=SLASH)||input.LA(1)==PERCENT ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_multiplicativeExpression573);    throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression641);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:186:1: expressionList : expression ( ',' expression )* ;
    public final void expressionList() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:187:2: ( expression ( ',' expression )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:187:4: expression ( ',' expression )*
            {
            pushFollow(FOLLOW_expression_in_expressionList671);
            expression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:188:9: ( ',' expression )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==COMMA) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:188:10: ',' expression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_expressionList682); 
            	    pushFollow(FOLLOW_expression_in_expressionList684);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:191:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );
    public final void unaryExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:196:2: ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus )
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
                {
                alt15=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("191:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:196:6: '+' unaryExpression
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_unaryExpression701); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression703);
                    unaryExpression();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:197:6: '-' unaryExpression
                    {
                    match(input,SUB,FOLLOW_SUB_in_unaryExpression710); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression712);
                    unaryExpression();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:198:6: '++' unaryExpression
                    {
                    match(input,PLUSPLUS,FOLLOW_PLUSPLUS_in_unaryExpression719); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression721);
                    unaryExpression();
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:199:6: '--' unaryExpression
                    {
                    match(input,SUBSUB,FOLLOW_SUBSUB_in_unaryExpression728); 
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression730);
                    unaryExpression();
                    _fsp--;


                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:200:6: unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression737);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:203:1: unaryExpressionNotPlusMinus : primary ( selector )* ( '++' | '--' )? ;
    public final void unaryExpressionNotPlusMinus() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:204:2: ( primary ( selector )* ( '++' | '--' )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:209:2: primary ( selector )* ( '++' | '--' )?
            {
            pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus761);
            primary();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:210:9: ( selector )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==LBRACKET||LA16_0==DOT) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:210:10: selector
            	    {
            	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus772);
            	    selector();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:211:9: ( '++' | '--' )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( ((LA17_0>=PLUSPLUS && LA17_0<=SUBSUB)) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:
                    {
                    if ( (input.LA(1)>=PLUSPLUS && input.LA(1)<=SUBSUB) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_unaryExpressionNotPlusMinus784);    throw mse;
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
        return ;
    }
    // $ANTLR end unaryExpressionNotPlusMinus


    // $ANTLR start castExpression
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:216:1: castExpression : ( '(' primitiveType | type ')' unaryExpression );
    public final void castExpression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:217:2: ( '(' primitiveType | type ')' unaryExpression )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==LPAREN) ) {
                alt18=1;
            }
            else if ( (LA18_0==IDENTIFIER||LA18_0==BOOLEAN||LA18_0==BYTE||LA18_0==CHAR||LA18_0==DOUBLE||LA18_0==FLOAT||LA18_0==INT||LA18_0==LONG||LA18_0==SHORT) ) {
                alt18=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("216:1: castExpression : ( '(' primitiveType | type ')' unaryExpression );", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:218:3: '(' primitiveType
                    {
                    match(input,LPAREN,FOLLOW_LPAREN_in_castExpression826); 
                    pushFollow(FOLLOW_primitiveType_in_castExpression828);
                    primitiveType();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:218:23: type ')' unaryExpression
                    {
                    pushFollow(FOLLOW_type_in_castExpression832);
                    type();
                    _fsp--;

                    match(input,RPAREN,FOLLOW_RPAREN_in_castExpression834); 
                    pushFollow(FOLLOW_unaryExpression_in_castExpression836);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:223:1: primary : ( 'this' ( identifierSuffix )? | IDENTIFIER ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
    public final void primary() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:227:2: ( 'this' ( identifierSuffix )? | IDENTIFIER ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
            int alt22=7;
            switch ( input.LA(1) ) {
            case THIS:
                {
                alt22=1;
                }
                break;
            case IDENTIFIER:
                {
                alt22=2;
                }
                break;
            case SUPER:
                {
                alt22=3;
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
                alt22=4;
                }
                break;
            case NEW:
                {
                alt22=5;
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
                alt22=6;
                }
                break;
            case VOID:
                {
                alt22=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("223:1: primary : ( 'this' ( identifierSuffix )? | IDENTIFIER ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:229:6: 'this' ( identifierSuffix )?
                    {
                    match(input,THIS,FOLLOW_THIS_in_primary873); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:229:37: ( identifierSuffix )?
                    int alt19=2;
                    switch ( input.LA(1) ) {
                        case LBRACKET:
                            {
                            int LA19_1 = input.LA(2);

                            if ( (LA19_1==RBRACKET) ) {
                                alt19=1;
                            }
                            }
                            break;
                        case LPAREN:
                            {
                            alt19=1;
                            }
                            break;
                        case DOT:
                            {
                            int LA19_3 = input.LA(2);

                            if ( (LA19_3==CLASS||LA19_3==THIS) ) {
                                alt19=1;
                            }
                            }
                            break;
                    }

                    switch (alt19) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:229:38: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary878);
                            identifierSuffix();
                            _fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:230:4: IDENTIFIER ( identifierSuffix )?
                    {
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary885); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:230:39: ( identifierSuffix )?
                    int alt20=2;
                    switch ( input.LA(1) ) {
                        case LBRACKET:
                            {
                            int LA20_1 = input.LA(2);

                            if ( (LA20_1==RBRACKET) ) {
                                alt20=1;
                            }
                            }
                            break;
                        case LPAREN:
                            {
                            alt20=1;
                            }
                            break;
                        case DOT:
                            {
                            int LA20_3 = input.LA(2);

                            if ( (LA20_3==CLASS) ) {
                                alt20=1;
                            }
                            else if ( (LA20_3==THIS) ) {
                                alt20=1;
                            }
                            }
                            break;
                    }

                    switch (alt20) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:230:40: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary890);
                            identifierSuffix();
                            _fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:231:4: 'super' superSuffix
                    {
                    match(input,SUPER,FOLLOW_SUPER_in_primary897); 
                    pushFollow(FOLLOW_superSuffix_in_primary899);
                    superSuffix();
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:232:4: literal
                    {
                    pushFollow(FOLLOW_literal_in_primary904);
                    literal();
                    _fsp--;


                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:233:4: creator
                    {
                    pushFollow(FOLLOW_creator_in_primary909);
                    creator();
                    _fsp--;


                    }
                    break;
                case 6 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:234:4: primitiveType ( '[' ']' )* '.' 'class'
                    {
                    pushFollow(FOLLOW_primitiveType_in_primary914);
                    primitiveType();
                    _fsp--;

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:234:18: ( '[' ']' )*
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( (LA21_0==LBRACKET) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:234:19: '[' ']'
                    	    {
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_primary917); 
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_primary919); 

                    	    }
                    	    break;

                    	default :
                    	    break loop21;
                        }
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_primary923); 
                    match(input,CLASS,FOLLOW_CLASS_in_primary925); 

                    }
                    break;
                case 7 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:235:4: 'void' '.' 'class'
                    {
                    match(input,VOID,FOLLOW_VOID_in_primary930); 
                    match(input,DOT,FOLLOW_DOT_in_primary932); 
                    match(input,CLASS,FOLLOW_CLASS_in_primary934); 

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:239:1: superSuffix : ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? );
    public final void superSuffix() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:240:2: ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==LPAREN) ) {
                alt25=1;
            }
            else if ( (LA25_0==DOT) ) {
                alt25=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("239:1: superSuffix : ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? );", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:240:4: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_superSuffix951);
                    arguments();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:241:4: '.' ( typeArguments )? IDENTIFIER ( arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_superSuffix956); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:241:8: ( typeArguments )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);

                    if ( (LA23_0==LT) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:241:9: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_superSuffix959);
                            typeArguments();
                            _fsp--;


                            }
                            break;

                    }

                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_superSuffix963); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:241:36: ( arguments )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);

                    if ( (LA24_0==LPAREN) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:241:37: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix966);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:244:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | arguments | '.' 'class' | '.' 'this' );
    public final void identifierSuffix() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:245:2: ( ( '[' ']' )+ '.' 'class' | arguments | '.' 'class' | '.' 'this' )
            int alt27=4;
            switch ( input.LA(1) ) {
            case LBRACKET:
                {
                alt27=1;
                }
                break;
            case LPAREN:
                {
                alt27=2;
                }
                break;
            case DOT:
                {
                int LA27_3 = input.LA(2);

                if ( (LA27_3==THIS) ) {
                    alt27=4;
                }
                else if ( (LA27_3==CLASS) ) {
                    alt27=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("244:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | arguments | '.' 'class' | '.' 'this' );", 27, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("244:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | arguments | '.' 'class' | '.' 'this' );", 27, 0, input);

                throw nvae;
            }

            switch (alt27) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:245:4: ( '[' ']' )+ '.' 'class'
                    {
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:245:4: ( '[' ']' )+
                    int cnt26=0;
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( (LA26_0==LBRACKET) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:245:5: '[' ']'
                    	    {
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_identifierSuffix980); 
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_identifierSuffix982); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt26 >= 1 ) break loop26;
                                EarlyExitException eee =
                                    new EarlyExitException(26, input);
                                throw eee;
                        }
                        cnt26++;
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix994); 
                    match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix996); 

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:248:4: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_identifierSuffix1003);
                    arguments();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:249:4: '.' 'class'
                    {
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix1008); 
                    match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix1010); 

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:251:4: '.' 'this'
                    {
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix1017); 
                    match(input,THIS,FOLLOW_THIS_in_identifierSuffix1019); 

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:257:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '[' expression ']' );
    public final void selector() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:258:2: ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '[' expression ']' )
            int alt29=4;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==DOT) ) {
                switch ( input.LA(2) ) {
                case THIS:
                    {
                    alt29=2;
                    }
                    break;
                case IDENTIFIER:
                    {
                    alt29=1;
                    }
                    break;
                case SUPER:
                    {
                    alt29=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("257:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '[' expression ']' );", 29, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA29_0==LBRACKET) ) {
                alt29=4;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("257:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '[' expression ']' );", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:258:4: '.' IDENTIFIER ( arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector1035); 
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_selector1037); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:258:19: ( arguments )?
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==LPAREN) ) {
                        alt28=1;
                    }
                    switch (alt28) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:258:20: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector1040);
                            arguments();
                            _fsp--;


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:259:4: '.' 'this'
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector1047); 
                    match(input,THIS,FOLLOW_THIS_in_selector1049); 

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:260:4: '.' 'super' superSuffix
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector1054); 
                    match(input,SUPER,FOLLOW_SUPER_in_selector1056); 
                    pushFollow(FOLLOW_superSuffix_in_selector1058);
                    superSuffix();
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:262:4: '[' expression ']'
                    {
                    match(input,LBRACKET,FOLLOW_LBRACKET_in_selector1065); 
                    pushFollow(FOLLOW_expression_in_selector1067);
                    expression();
                    _fsp--;

                    match(input,RBRACKET,FOLLOW_RBRACKET_in_selector1069); 

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:265:1: creator : 'new' classOrInterfaceType classCreatorRest ;
    public final void creator() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:266:2: ( 'new' classOrInterfaceType classCreatorRest )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:268:2: 'new' classOrInterfaceType classCreatorRest
            {
            match(input,NEW,FOLLOW_NEW_in_creator1087); 
            pushFollow(FOLLOW_classOrInterfaceType_in_creator1089);
            classOrInterfaceType();
            _fsp--;

            pushFollow(FOLLOW_classCreatorRest_in_creator1091);
            classCreatorRest();
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
    // $ANTLR end creator


    // $ANTLR start typeList
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:273:1: typeList : type ( ',' type )* ;
    public final void typeList() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:274:2: ( type ( ',' type )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:274:4: type ( ',' type )*
            {
            pushFollow(FOLLOW_type_in_typeList1106);
            type();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:274:9: ( ',' type )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==COMMA) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:274:10: ',' type
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeList1109); 
            	    pushFollow(FOLLOW_type_in_typeList1111);
            	    type();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop30;
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:277:1: classCreatorRest : arguments ;
    public final void classCreatorRest() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:278:2: ( arguments )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:278:4: arguments
            {
            pushFollow(FOLLOW_arguments_in_classCreatorRest1126);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:300:1: variableInitializer : expression ;
    public final void variableInitializer() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:301:2: ( expression )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:303:2: expression
            {
            pushFollow(FOLLOW_expression_in_variableInitializer1165);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:317:1: createdName : ( classOrInterfaceType | primitiveType );
    public final void createdName() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:318:2: ( classOrInterfaceType | primitiveType )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==IDENTIFIER) ) {
                alt31=1;
            }
            else if ( (LA31_0==BOOLEAN||LA31_0==BYTE||LA31_0==CHAR||LA31_0==DOUBLE||LA31_0==FLOAT||LA31_0==INT||LA31_0==LONG||LA31_0==SHORT) ) {
                alt31=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("317:1: createdName : ( classOrInterfaceType | primitiveType );", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:318:4: classOrInterfaceType
                    {
                    pushFollow(FOLLOW_classOrInterfaceType_in_createdName1181);
                    classOrInterfaceType();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:319:4: primitiveType
                    {
                    pushFollow(FOLLOW_primitiveType_in_createdName1186);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:345:1: arguments : '(' ( expressionList )? ')' ;
    public final void arguments() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:346:2: ( '(' ( expressionList )? ')' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:346:4: '(' ( expressionList )? ')'
            {
            match(input,LPAREN,FOLLOW_LPAREN_in_arguments1209); 
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:346:8: ( expressionList )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( ((LA32_0>=IDENTIFIER && LA32_0<=NULL)||LA32_0==BOOLEAN||LA32_0==BYTE||LA32_0==CHAR||LA32_0==DOUBLE||LA32_0==FLOAT||LA32_0==INT||LA32_0==LONG||LA32_0==NEW||LA32_0==SHORT||LA32_0==SUPER||LA32_0==THIS||LA32_0==VOID||(LA32_0>=PLUSPLUS && LA32_0<=SUB)) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:346:9: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments1212);
                    expressionList();
                    _fsp--;


                    }
                    break;

            }

            match(input,RPAREN,FOLLOW_RPAREN_in_arguments1216); 

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:349:1: type : ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* );
    public final void type() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:350:2: ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* )
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
                    new NoViableAltException("349:1: type : ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* );", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:350:4: classOrInterfaceType ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_classOrInterfaceType_in_type1228);
                    classOrInterfaceType();
                    _fsp--;

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:350:25: ( '[' ']' )*
                    loop33:
                    do {
                        int alt33=2;
                        int LA33_0 = input.LA(1);

                        if ( (LA33_0==LBRACKET) ) {
                            alt33=1;
                        }


                        switch (alt33) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:350:26: '[' ']'
                    	    {
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_type1231); 
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_type1233); 

                    	    }
                    	    break;

                    	default :
                    	    break loop33;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:351:6: primitiveType ( '[' ']' )*
                    {
                    pushFollow(FOLLOW_primitiveType_in_type1243);
                    primitiveType();
                    _fsp--;

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:351:20: ( '[' ']' )*
                    loop34:
                    do {
                        int alt34=2;
                        int LA34_0 = input.LA(1);

                        if ( (LA34_0==LBRACKET) ) {
                            alt34=1;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:351:21: '[' ']'
                    	    {
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_type1246); 
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_type1248); 

                    	    }
                    	    break;

                    	default :
                    	    break loop34;
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:355:1: classOrInterfaceType : IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )* ;
    public final void classOrInterfaceType() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:356:2: ( IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:356:4: IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )*
            {
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classOrInterfaceType1263); 
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:356:15: ( typeArguments )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==LT) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:356:16: typeArguments
                    {
                    pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType1266);
                    typeArguments();
                    _fsp--;


                    }
                    break;

            }

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:356:32: ( '.' IDENTIFIER ( typeArguments )? )*
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);

                if ( (LA38_0==DOT) ) {
                    alt38=1;
                }


                switch (alt38) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:356:33: '.' IDENTIFIER ( typeArguments )?
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_classOrInterfaceType1271); 
            	    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classOrInterfaceType1273); 
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:356:48: ( typeArguments )?
            	    int alt37=2;
            	    int LA37_0 = input.LA(1);

            	    if ( (LA37_0==LT) ) {
            	        alt37=1;
            	    }
            	    switch (alt37) {
            	        case 1 :
            	            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:356:49: typeArguments
            	            {
            	            pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType1276);
            	            typeArguments();
            	            _fsp--;


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop38;
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:359:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
    public final void typeArguments() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:360:2: ( '<' typeArgument ( ',' typeArgument )* '>' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:360:4: '<' typeArgument ( ',' typeArgument )* '>'
            {
            match(input,LT,FOLLOW_LT_in_typeArguments1292); 
            pushFollow(FOLLOW_typeArgument_in_typeArguments1294);
            typeArgument();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:360:21: ( ',' typeArgument )*
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);

                if ( (LA39_0==COMMA) ) {
                    alt39=1;
                }


                switch (alt39) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:360:22: ',' typeArgument
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeArguments1297); 
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments1299);
            	    typeArgument();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop39;
                }
            } while (true);

            match(input,GT,FOLLOW_GT_in_typeArguments1303); 

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:363:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
    public final void typeArgument() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:364:2: ( type | '?' ( ( 'extends' | 'super' ) type )? )
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==IDENTIFIER||LA41_0==BOOLEAN||LA41_0==BYTE||LA41_0==CHAR||LA41_0==DOUBLE||LA41_0==FLOAT||LA41_0==INT||LA41_0==LONG||LA41_0==SHORT) ) {
                alt41=1;
            }
            else if ( (LA41_0==QUES) ) {
                alt41=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("363:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:364:6: type
                    {
                    pushFollow(FOLLOW_type_in_typeArgument1317);
                    type();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:365:6: '?' ( ( 'extends' | 'super' ) type )?
                    {
                    match(input,QUES,FOLLOW_QUES_in_typeArgument1324); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:366:9: ( ( 'extends' | 'super' ) type )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==EXTENDS||LA40_0==SUPER) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:367:13: ( 'extends' | 'super' ) type
                            {
                            if ( input.LA(1)==EXTENDS||input.LA(1)==SUPER ) {
                                input.consume();
                                errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse =
                                    new MismatchedSetException(null,input);
                                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_typeArgument1348);    throw mse;
                            }

                            pushFollow(FOLLOW_type_in_typeArgument1392);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:375:1: primitiveType : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
    public final void primitiveType() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:376:2: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:386:1: literal : ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL );
    public final void literal() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:387:2: ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:
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


 

    public static final BitSet FOLLOW_expression_in_rhs38 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_rhs40 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression52 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression81 = new BitSet(new long[]{0x0000000000000002L,0x0000000000400000L});
    public static final BitSet FOLLOW_QUES_in_conditionalExpression89 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression91 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression93 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression95 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression108 = new BitSet(new long[]{0x0000000000000002L,0x0000000004000000L});
    public static final BitSet FOLLOW_BARBAR_in_conditionalOrExpression119 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression121 = new BitSet(new long[]{0x0000000000000002L,0x0000000004000000L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression135 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_AMPAMP_in_conditionalAndExpression146 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression148 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression166 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_BAR_in_inclusiveOrExpression177 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression179 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression193 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_CARET_in_exclusiveOrExpression204 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression206 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression219 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_AMP_in_andExpression230 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression232 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression250 = new BitSet(new long[]{0x0000000000000002L,0x0000400001000000L});
    public static final BitSet FOLLOW_EQEQ_in_equalityExpression263 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_BANGEQ_in_equalityExpression279 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression299 = new BitSet(new long[]{0x0000000000000002L,0x0000400001000000L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression321 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_INSTANCEOF_in_instanceOfExpression332 = new BitSet(new long[]{0x080A04104A000010L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression347 = new BitSet(new long[]{0x0000000000000002L,0x0001800000000000L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression359 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression361 = new BitSet(new long[]{0x0000000000000002L,0x0001800000000000L});
    public static final BitSet FOLLOW_LT_in_relationalOp388 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_EQ_in_relationalOp390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_relationalOp401 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_EQ_in_relationalOp403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_relationalOp413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_relationalOp423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression440 = new BitSet(new long[]{0x0000000000000002L,0x0000000060000000L});
    public static final BitSet FOLLOW_set_in_additiveExpression467 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression517 = new BitSet(new long[]{0x0000000000000002L,0x0000000060000000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression546 = new BitSet(new long[]{0x0000000000000002L,0x0000001180000000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression573 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression641 = new BitSet(new long[]{0x0000000000000002L,0x0000001180000000L});
    public static final BitSet FOLLOW_expression_in_expressionList671 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_expressionList682 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_expression_in_expressionList684 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression701 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUB_in_unaryExpression710 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSPLUS_in_unaryExpression719 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSUB_in_unaryExpression728 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus761 = new BitSet(new long[]{0x0000000000000002L,0x0000000018022000L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus772 = new BitSet(new long[]{0x0000000000000002L,0x0000000018022000L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus784 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_castExpression826 = new BitSet(new long[]{0x080A04104A000000L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_castExpression832 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RPAREN_in_castExpression834 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression836 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THIS_in_primary873 = new BitSet(new long[]{0x0000000000000002L,0x0000000000022200L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary885 = new BitSet(new long[]{0x0000000000000002L,0x0000000000022200L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary890 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_in_primary897 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020200L});
    public static final BitSet FOLLOW_superSuffix_in_primary899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_creator_in_primary909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary914 = new BitSet(new long[]{0x0000000000000000L,0x0000000000022000L});
    public static final BitSet FOLLOW_LBRACKET_in_primary917 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_RBRACKET_in_primary919 = new BitSet(new long[]{0x0000000000000000L,0x0000000000022000L});
    public static final BitSet FOLLOW_DOT_in_primary923 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_CLASS_in_primary925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VOID_in_primary930 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_DOT_in_primary932 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_CLASS_in_primary934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix956 = new BitSet(new long[]{0x0000000000000010L,0x0001000000000000L});
    public static final BitSet FOLLOW_typeArguments_in_superSuffix959 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_superSuffix963 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_arguments_in_superSuffix966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_identifierSuffix980 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_RBRACKET_in_identifierSuffix982 = new BitSet(new long[]{0x0000000000000000L,0x0000000000022000L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix994 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_CLASS_in_identifierSuffix996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix1003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix1008 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_CLASS_in_identifierSuffix1010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix1017 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_THIS_in_identifierSuffix1019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector1035 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_selector1037 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_arguments_in_selector1040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector1047 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_THIS_in_selector1049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector1054 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_SUPER_in_selector1056 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020200L});
    public static final BitSet FOLLOW_superSuffix_in_selector1058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_selector1065 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000042L});
    public static final BitSet FOLLOW_expression_in_selector1067 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_RBRACKET_in_selector1069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_creator1087 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_creator1089 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator1091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList1106 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_typeList1109 = new BitSet(new long[]{0x080A04104A000010L});
    public static final BitSet FOLLOW_type_in_typeList1111 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest1126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer1165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_createdName1181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName1186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_arguments1209 = new BitSet(new long[]{0x482A04104A003FF0L,0x0000000078000442L});
    public static final BitSet FOLLOW_expressionList_in_arguments1212 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RPAREN_in_arguments1216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_type1228 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_LBRACKET_in_type1231 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_RBRACKET_in_type1233 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_primitiveType_in_type1243 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_LBRACKET_in_type1246 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_RBRACKET_in_type1248 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classOrInterfaceType1263 = new BitSet(new long[]{0x0000000000000002L,0x0001000000020000L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType1266 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_DOT_in_classOrInterfaceType1271 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classOrInterfaceType1273 = new BitSet(new long[]{0x0000000000000002L,0x0001000000020000L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType1276 = new BitSet(new long[]{0x0000000000000002L,0x0000000000020000L});
    public static final BitSet FOLLOW_LT_in_typeArguments1292 = new BitSet(new long[]{0x080A04104A000010L,0x0000000000400000L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments1294 = new BitSet(new long[]{0x0000000000000000L,0x0000800000010000L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments1297 = new BitSet(new long[]{0x080A04104A000010L,0x0000000000400000L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments1299 = new BitSet(new long[]{0x0000000000000000L,0x0000800000010000L});
    public static final BitSet FOLLOW_GT_in_typeArguments1303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument1317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUES_in_typeArgument1324 = new BitSet(new long[]{0x4000008000000002L});
    public static final BitSet FOLLOW_set_in_typeArgument1348 = new BitSet(new long[]{0x080A04104A000010L});
    public static final BitSet FOLLOW_type_in_typeArgument1392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_primitiveType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(new long[]{0x0000000000000002L});

}