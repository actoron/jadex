// $ANTLR 3.0.1 C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g 2009-03-17 18:14:50

package jadex.rules.parser.conditions;

/*import jadex.rules.rulesystem.rules.*;
import jadex.rules.rulesystem.rules.functions.*;
import jadex.rules.rulesystem.*;
import jadex.rules.state.*;
import jadex.commons.SReflect;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.HashMap;*/


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.debug.*;
import java.io.IOException;
public class JadexJavaRulesParser extends DebugParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENTIFIER", "INTLITERAL", "LONGLITERAL", "FLOATLITERAL", "DOUBLELITERAL", "CHARLITERAL", "STRINGLITERAL", "TRUE", "FALSE", "NULL", "IntegerNumber", "LongSuffix", "HexPrefix", "HexDigit", "Exponent", "NonIntegerNumber", "FloatSuffix", "DoubleSuffix", "EscapeSequence", "WS", "COMMENT", "LINE_COMMENT", "ABSTRACT", "ASSERT", "BOOLEAN", "BREAK", "BYTE", "CASE", "CATCH", "CHAR", "CLASS", "CONST", "CONTINUE", "DEFAULT", "DO", "DOUBLE", "ELSE", "ENUM", "EXTENDS", "FINAL", "FINALLY", "FLOAT", "FOR", "GOTO", "IF", "IMPLEMENTS", "IMPORT", "INSTANCEOF", "INT", "INTERFACE", "LONG", "NATIVE", "NEW", "PACKAGE", "PRIVATE", "PROTECTED", "PUBLIC", "RETURN", "SHORT", "STATIC", "STRICTFP", "SUPER", "SWITCH", "SYNCHRONIZED", "THIS", "THROW", "THROWS", "TRANSIENT", "TRY", "VOID", "VOLATILE", "WHILE", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET", "SEMI", "COMMA", "DOT", "ELLIPSIS", "EQ", "BANG", "TILDE", "QUES", "COLON", "EQEQ", "AMPAMP", "BARBAR", "PLUSPLUS", "SUBSUB", "PLUS", "SUB", "STAR", "SLASH", "AMP", "BAR", "CARET", "PERCENT", "PLUSEQ", "SUBEQ", "STAREQ", "SLASHEQ", "AMPEQ", "BAREQ", "CARETEQ", "PERCENTEQ", "MONKEYS_AT", "BANGEQ", "GT", "LT", "IdentifierStart", "IdentifierPart", "SurrogateIdentifer"
    };
    public static final int PACKAGE=57;
    public static final int LT=115;
    public static final int STAR=98;
    public static final int WHILE=75;
    public static final int CONST=35;
    public static final int CASE=31;
    public static final int CHAR=33;
    public static final int NEW=56;
    public static final int DO=38;
    public static final int EOF=-1;
    public static final int BREAK=29;
    public static final int LBRACKET=80;
    public static final int FINAL=43;
    public static final int RPAREN=77;
    public static final int IMPORT=50;
    public static final int SUBSUB=95;
    public static final int STAREQ=106;
    public static final int FloatSuffix=20;
    public static final int NonIntegerNumber=19;
    public static final int CARET=102;
    public static final int RETURN=61;
    public static final int THIS=68;
    public static final int DOUBLE=39;
    public static final int MONKEYS_AT=112;
    public static final int BARBAR=93;
    public static final int VOID=73;
    public static final int SUPER=65;
    public static final int GOTO=47;
    public static final int EQ=86;
    public static final int AMPAMP=92;
    public static final int COMMENT=24;
    public static final int QUES=89;
    public static final int EQEQ=91;
    public static final int HexPrefix=16;
    public static final int RBRACE=79;
    public static final int LINE_COMMENT=25;
    public static final int PRIVATE=58;
    public static final int STATIC=63;
    public static final int SWITCH=66;
    public static final int NULL=13;
    public static final int ELSE=40;
    public static final int STRICTFP=64;
    public static final int DOUBLELITERAL=8;
    public static final int IdentifierStart=116;
    public static final int NATIVE=55;
    public static final int ELLIPSIS=85;
    public static final int THROWS=70;
    public static final int INT=52;
    public static final int SLASHEQ=107;
    public static final int INTLITERAL=5;
    public static final int ASSERT=27;
    public static final int TRY=72;
    public static final int LONGLITERAL=6;
    public static final int LongSuffix=15;
    public static final int WS=23;
    public static final int SurrogateIdentifer=118;
    public static final int CHARLITERAL=9;
    public static final int GT=114;
    public static final int CATCH=32;
    public static final int FALSE=12;
    public static final int EscapeSequence=22;
    public static final int THROW=69;
    public static final int PROTECTED=59;
    public static final int CLASS=34;
    public static final int BAREQ=109;
    public static final int IntegerNumber=14;
    public static final int AMP=100;
    public static final int PLUSPLUS=94;
    public static final int LBRACE=78;
    public static final int SUBEQ=105;
    public static final int Exponent=18;
    public static final int FOR=46;
    public static final int SUB=97;
    public static final int FLOAT=45;
    public static final int ABSTRACT=26;
    public static final int HexDigit=17;
    public static final int PLUSEQ=104;
    public static final int LPAREN=76;
    public static final int IF=48;
    public static final int SLASH=99;
    public static final int BOOLEAN=28;
    public static final int SYNCHRONIZED=67;
    public static final int IMPLEMENTS=49;
    public static final int CONTINUE=36;
    public static final int COMMA=83;
    public static final int AMPEQ=108;
    public static final int IDENTIFIER=4;
    public static final int TRANSIENT=71;
    public static final int TILDE=88;
    public static final int BANGEQ=113;
    public static final int PLUS=96;
    public static final int RBRACKET=81;
    public static final int DOT=84;
    public static final int IdentifierPart=117;
    public static final int BYTE=30;
    public static final int PERCENT=103;
    public static final int VOLATILE=74;
    public static final int DEFAULT=37;
    public static final int SHORT=62;
    public static final int BANG=87;
    public static final int INSTANCEOF=51;
    public static final int TRUE=11;
    public static final int SEMI=82;
    public static final int COLON=90;
    public static final int ENUM=41;
    public static final int PERCENTEQ=111;
    public static final int DoubleSuffix=21;
    public static final int FINALLY=44;
    public static final int STRINGLITERAL=10;
    public static final int CARETEQ=110;
    public static final int INTERFACE=53;
    public static final int LONG=54;
    public static final int EXTENDS=42;
    public static final int FLOATLITERAL=7;
    public static final int PUBLIC=60;
    public static final int BAR=101;
    public static final String[] ruleNames = new String[] {
        "invalidRule", "rhs", "expression", "expressionList", "conditionalExpression", 
        "conditionalOrExpression", "conditionalAndExpression", "inclusiveOrExpression", 
        "exclusiveOrExpression", "andExpression", "equalityExpression", 
        "instanceOfExpression", "relationalExpression", "relationalOp", 
        "additiveExpression", "multiplicativeExpression", "unaryExpression", 
        "unaryExpressionNotPlusMinus", "castExpression", "primary", "superSuffix", 
        "identifierSuffix", "selector", "creator", "typeList", "classCreatorRest", 
        "variableInitializer", "createdName", "arguments", "type", "classOrInterfaceType", 
        "typeArguments", "typeArgument", "primitiveType", "literal"
    };

    public int ruleLevel = 0;
    public JadexJavaRulesParser(TokenStream input, int port) {
            super(input, port);
            DebugEventSocketProxy proxy =
                new DebugEventSocketProxy(this, port, null);setDebugListener(proxy);
            try {
                proxy.handshake();
            }
            catch (IOException ioe) {
                reportError(ioe);
            }

    }
    public JadexJavaRulesParser(TokenStream input) {
        this(input, DebugEventSocketProxy.DEFAULT_DEBUGGER_PORT);
    }
    public JadexJavaRulesParser(TokenStream input, DebugEventListener dbg) {
        super(input, dbg);
    }

    protected boolean evalPredicate(boolean result, String predicate) {
        dbg.semanticPredicate(result, predicate);
        return result;
    }


    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g"; }



    // $ANTLR start rhs
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:59:1: rhs : expression EOF ;
    public final void rhs() throws RecognitionException {
        try { dbg.enterRule("rhs");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(59, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:60:2: ( expression EOF )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:61:2: expression EOF
            {
            dbg.location(61,2);
            pushFollow(FOLLOW_expression_in_rhs38);
            expression();
            _fsp--;

            dbg.location(66,2);
            match(input,EOF,FOLLOW_EOF_in_rhs49); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(66, 5);

        }
        finally {
            dbg.exitRule("rhs");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end rhs


    // $ANTLR start expression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:68:1: expression : conditionalExpression ;
    public final void expression() throws RecognitionException {
        try { dbg.enterRule("expression");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(68, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:69:5: ( conditionalExpression )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:69:9: conditionalExpression
            {
            dbg.location(69,9);
            pushFollow(FOLLOW_conditionalExpression_in_expression65);
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
        dbg.location(72, 5);

        }
        finally {
            dbg.exitRule("expression");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end expression


    // $ANTLR start expressionList
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:94:1: expressionList : expression ( ',' expression )* ;
    public final void expressionList() throws RecognitionException {
        try { dbg.enterRule("expressionList");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(94, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:95:5: ( expression ( ',' expression )* )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:95:9: expression ( ',' expression )*
            {
            dbg.location(95,9);
            pushFollow(FOLLOW_expression_in_expressionList113);
            expression();
            _fsp--;

            dbg.location(96,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:96:9: ( ',' expression )*
            try { dbg.enterSubRule(1);

            loop1:
            do {
                int alt1=2;
                try { dbg.enterDecision(1);

                int LA1_0 = input.LA(1);

                if ( (LA1_0==COMMA) ) {
                    alt1=1;
                }


                } finally {dbg.exitDecision(1);}

                switch (alt1) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:96:10: ',' expression
            	    {
            	    dbg.location(96,10);
            	    match(input,COMMA,FOLLOW_COMMA_in_expressionList124); 
            	    dbg.location(96,14);
            	    pushFollow(FOLLOW_expression_in_expressionList126);
            	    expression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);
            } finally {dbg.exitSubRule(1);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(98, 5);

        }
        finally {
            dbg.exitRule("expressionList");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end expressionList


    // $ANTLR start conditionalExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:101:1: conditionalExpression : conditionalOrExpression ( '?' expression ':' conditionalExpression )? ;
    public final void conditionalExpression() throws RecognitionException {
        try { dbg.enterRule("conditionalExpression");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(101, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:102:5: ( conditionalOrExpression ( '?' expression ':' conditionalExpression )? )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:102:9: conditionalOrExpression ( '?' expression ':' conditionalExpression )?
            {
            dbg.location(102,9);
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression158);
            conditionalOrExpression();
            _fsp--;

            dbg.location(103,6);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:103:6: ( '?' expression ':' conditionalExpression )?
            int alt2=2;
            try { dbg.enterSubRule(2);
            try { dbg.enterDecision(2);

            int LA2_0 = input.LA(1);

            if ( (LA2_0==QUES) ) {
                alt2=1;
            }
            } finally {dbg.exitDecision(2);}

            switch (alt2) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:103:7: '?' expression ':' conditionalExpression
                    {
                    dbg.location(103,7);
                    match(input,QUES,FOLLOW_QUES_in_conditionalExpression166); 
                    dbg.location(103,11);
                    pushFollow(FOLLOW_expression_in_conditionalExpression168);
                    expression();
                    _fsp--;

                    dbg.location(103,22);
                    match(input,COLON,FOLLOW_COLON_in_conditionalExpression170); 
                    dbg.location(103,26);
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalExpression172);
                    conditionalExpression();
                    _fsp--;


                    }
                    break;

            }
            } finally {dbg.exitSubRule(2);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(105, 5);

        }
        finally {
            dbg.exitRule("conditionalExpression");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end conditionalExpression


    // $ANTLR start conditionalOrExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:107:1: conditionalOrExpression : conditionalAndExpression ( '||' conditionalAndExpression )* ;
    public final void conditionalOrExpression() throws RecognitionException {
        try { dbg.enterRule("conditionalOrExpression");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(107, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:108:5: ( conditionalAndExpression ( '||' conditionalAndExpression )* )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:108:9: conditionalAndExpression ( '||' conditionalAndExpression )*
            {
            dbg.location(108,9);
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression203);
            conditionalAndExpression();
            _fsp--;

            dbg.location(109,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:109:9: ( '||' conditionalAndExpression )*
            try { dbg.enterSubRule(3);

            loop3:
            do {
                int alt3=2;
                try { dbg.enterDecision(3);

                int LA3_0 = input.LA(1);

                if ( (LA3_0==BARBAR) ) {
                    alt3=1;
                }


                } finally {dbg.exitDecision(3);}

                switch (alt3) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:109:10: '||' conditionalAndExpression
            	    {
            	    dbg.location(109,10);
            	    match(input,BARBAR,FOLLOW_BARBAR_in_conditionalOrExpression214); 
            	    dbg.location(109,15);
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression216);
            	    conditionalAndExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);
            } finally {dbg.exitSubRule(3);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(111, 5);

        }
        finally {
            dbg.exitRule("conditionalOrExpression");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end conditionalOrExpression


    // $ANTLR start conditionalAndExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:113:1: conditionalAndExpression : inclusiveOrExpression ( '&&' inclusiveOrExpression )* ;
    public final void conditionalAndExpression() throws RecognitionException {
        try { dbg.enterRule("conditionalAndExpression");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(113, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:114:5: ( inclusiveOrExpression ( '&&' inclusiveOrExpression )* )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:114:9: inclusiveOrExpression ( '&&' inclusiveOrExpression )*
            {
            dbg.location(114,9);
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression247);
            inclusiveOrExpression();
            _fsp--;

            dbg.location(115,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:115:9: ( '&&' inclusiveOrExpression )*
            try { dbg.enterSubRule(4);

            loop4:
            do {
                int alt4=2;
                try { dbg.enterDecision(4);

                int LA4_0 = input.LA(1);

                if ( (LA4_0==AMPAMP) ) {
                    alt4=1;
                }


                } finally {dbg.exitDecision(4);}

                switch (alt4) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:115:10: '&&' inclusiveOrExpression
            	    {
            	    dbg.location(115,10);
            	    match(input,AMPAMP,FOLLOW_AMPAMP_in_conditionalAndExpression258); 
            	    dbg.location(115,15);
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression260);
            	    inclusiveOrExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);
            } finally {dbg.exitSubRule(4);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(117, 5);

        }
        finally {
            dbg.exitRule("conditionalAndExpression");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end conditionalAndExpression


    // $ANTLR start inclusiveOrExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:119:1: inclusiveOrExpression : exclusiveOrExpression ( '|' exclusiveOrExpression )* ;
    public final void inclusiveOrExpression() throws RecognitionException {
        try { dbg.enterRule("inclusiveOrExpression");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(119, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:120:5: ( exclusiveOrExpression ( '|' exclusiveOrExpression )* )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:120:9: exclusiveOrExpression ( '|' exclusiveOrExpression )*
            {
            dbg.location(120,9);
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression291);
            exclusiveOrExpression();
            _fsp--;

            dbg.location(121,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:121:9: ( '|' exclusiveOrExpression )*
            try { dbg.enterSubRule(5);

            loop5:
            do {
                int alt5=2;
                try { dbg.enterDecision(5);

                int LA5_0 = input.LA(1);

                if ( (LA5_0==BAR) ) {
                    alt5=1;
                }


                } finally {dbg.exitDecision(5);}

                switch (alt5) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:121:10: '|' exclusiveOrExpression
            	    {
            	    dbg.location(121,10);
            	    match(input,BAR,FOLLOW_BAR_in_inclusiveOrExpression302); 
            	    dbg.location(121,14);
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression304);
            	    exclusiveOrExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);
            } finally {dbg.exitSubRule(5);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(123, 5);

        }
        finally {
            dbg.exitRule("inclusiveOrExpression");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end inclusiveOrExpression


    // $ANTLR start exclusiveOrExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:125:1: exclusiveOrExpression : andExpression ( '^' andExpression )* ;
    public final void exclusiveOrExpression() throws RecognitionException {
        try { dbg.enterRule("exclusiveOrExpression");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(125, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:126:5: ( andExpression ( '^' andExpression )* )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:126:9: andExpression ( '^' andExpression )*
            {
            dbg.location(126,9);
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression335);
            andExpression();
            _fsp--;

            dbg.location(127,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:127:9: ( '^' andExpression )*
            try { dbg.enterSubRule(6);

            loop6:
            do {
                int alt6=2;
                try { dbg.enterDecision(6);

                int LA6_0 = input.LA(1);

                if ( (LA6_0==CARET) ) {
                    alt6=1;
                }


                } finally {dbg.exitDecision(6);}

                switch (alt6) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:127:10: '^' andExpression
            	    {
            	    dbg.location(127,10);
            	    match(input,CARET,FOLLOW_CARET_in_exclusiveOrExpression346); 
            	    dbg.location(127,14);
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression348);
            	    andExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);
            } finally {dbg.exitSubRule(6);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(129, 5);

        }
        finally {
            dbg.exitRule("exclusiveOrExpression");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end exclusiveOrExpression


    // $ANTLR start andExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:131:1: andExpression : equalityExpression ( '&' equalityExpression )* ;
    public final void andExpression() throws RecognitionException {
        try { dbg.enterRule("andExpression");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(131, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:132:5: ( equalityExpression ( '&' equalityExpression )* )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:132:9: equalityExpression ( '&' equalityExpression )*
            {
            dbg.location(132,9);
            pushFollow(FOLLOW_equalityExpression_in_andExpression379);
            equalityExpression();
            _fsp--;

            dbg.location(133,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:133:9: ( '&' equalityExpression )*
            try { dbg.enterSubRule(7);

            loop7:
            do {
                int alt7=2;
                try { dbg.enterDecision(7);

                int LA7_0 = input.LA(1);

                if ( (LA7_0==AMP) ) {
                    alt7=1;
                }


                } finally {dbg.exitDecision(7);}

                switch (alt7) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:133:10: '&' equalityExpression
            	    {
            	    dbg.location(133,10);
            	    match(input,AMP,FOLLOW_AMP_in_andExpression390); 
            	    dbg.location(133,14);
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression392);
            	    equalityExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);
            } finally {dbg.exitSubRule(7);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(135, 5);

        }
        finally {
            dbg.exitRule("andExpression");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end andExpression


    // $ANTLR start equalityExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:137:1: equalityExpression : instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* ;
    public final void equalityExpression() throws RecognitionException {
        try { dbg.enterRule("equalityExpression");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(137, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:138:5: ( instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )* )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:138:9: instanceOfExpression ( ( '==' | '!=' ) instanceOfExpression )*
            {
            dbg.location(138,9);
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression423);
            instanceOfExpression();
            _fsp--;

            dbg.location(139,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:139:9: ( ( '==' | '!=' ) instanceOfExpression )*
            try { dbg.enterSubRule(8);

            loop8:
            do {
                int alt8=2;
                try { dbg.enterDecision(8);

                int LA8_0 = input.LA(1);

                if ( (LA8_0==EQEQ||LA8_0==BANGEQ) ) {
                    alt8=1;
                }


                } finally {dbg.exitDecision(8);}

                switch (alt8) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:140:13: ( '==' | '!=' ) instanceOfExpression
            	    {
            	    dbg.location(140,13);
            	    if ( input.LA(1)==EQEQ||input.LA(1)==BANGEQ ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        dbg.recognitionException(mse);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_equalityExpression450);    throw mse;
            	    }

            	    dbg.location(143,13);
            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression500);
            	    instanceOfExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);
            } finally {dbg.exitSubRule(8);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(145, 5);

        }
        finally {
            dbg.exitRule("equalityExpression");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end equalityExpression


    // $ANTLR start instanceOfExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:147:1: instanceOfExpression : relationalExpression ( 'instanceof' type )? ;
    public final void instanceOfExpression() throws RecognitionException {
        try { dbg.enterRule("instanceOfExpression");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(147, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:148:5: ( relationalExpression ( 'instanceof' type )? )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:148:9: relationalExpression ( 'instanceof' type )?
            {
            dbg.location(148,9);
            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression531);
            relationalExpression();
            _fsp--;

            dbg.location(149,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:149:9: ( 'instanceof' type )?
            int alt9=2;
            try { dbg.enterSubRule(9);
            try { dbg.enterDecision(9);

            int LA9_0 = input.LA(1);

            if ( (LA9_0==INSTANCEOF) ) {
                alt9=1;
            }
            } finally {dbg.exitDecision(9);}

            switch (alt9) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:149:10: 'instanceof' type
                    {
                    dbg.location(149,10);
                    match(input,INSTANCEOF,FOLLOW_INSTANCEOF_in_instanceOfExpression542); 
                    dbg.location(149,23);
                    pushFollow(FOLLOW_type_in_instanceOfExpression544);
                    type();
                    _fsp--;


                    }
                    break;

            }
            } finally {dbg.exitSubRule(9);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(151, 5);

        }
        finally {
            dbg.exitRule("instanceOfExpression");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end instanceOfExpression


    // $ANTLR start relationalExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:153:1: relationalExpression : additiveExpression ( relationalOp additiveExpression )* ;
    public final void relationalExpression() throws RecognitionException {
        try { dbg.enterRule("relationalExpression");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(153, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:154:5: ( additiveExpression ( relationalOp additiveExpression )* )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:154:9: additiveExpression ( relationalOp additiveExpression )*
            {
            dbg.location(154,9);
            pushFollow(FOLLOW_additiveExpression_in_relationalExpression575);
            additiveExpression();
            _fsp--;

            dbg.location(155,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:155:9: ( relationalOp additiveExpression )*
            try { dbg.enterSubRule(10);

            loop10:
            do {
                int alt10=2;
                try { dbg.enterDecision(10);

                int LA10_0 = input.LA(1);

                if ( ((LA10_0>=GT && LA10_0<=LT)) ) {
                    alt10=1;
                }


                } finally {dbg.exitDecision(10);}

                switch (alt10) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:155:10: relationalOp additiveExpression
            	    {
            	    dbg.location(155,10);
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression587);
            	    relationalOp();
            	    _fsp--;

            	    dbg.location(155,23);
            	    pushFollow(FOLLOW_additiveExpression_in_relationalExpression589);
            	    additiveExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);
            } finally {dbg.exitSubRule(10);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(157, 5);

        }
        finally {
            dbg.exitRule("relationalExpression");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end relationalExpression


    // $ANTLR start relationalOp
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:159:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );
    public final void relationalOp() throws RecognitionException {
        try { dbg.enterRule("relationalOp");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(159, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:160:5: ( '<' '=' | '>' '=' | '<' | '>' )
            int alt11=4;
            try { dbg.enterDecision(11);

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
                        new NoViableAltException("159:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );", 11, 1, input);

                    dbg.recognitionException(nvae);
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
                        new NoViableAltException("159:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );", 11, 2, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("159:1: relationalOp : ( '<' '=' | '>' '=' | '<' | '>' );", 11, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(11);}

            switch (alt11) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:160:10: '<' '='
                    {
                    dbg.location(160,10);
                    match(input,LT,FOLLOW_LT_in_relationalOp622); 
                    dbg.location(160,14);
                    match(input,EQ,FOLLOW_EQ_in_relationalOp624); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:161:10: '>' '='
                    {
                    dbg.location(161,10);
                    match(input,GT,FOLLOW_GT_in_relationalOp635); 
                    dbg.location(161,14);
                    match(input,EQ,FOLLOW_EQ_in_relationalOp637); 

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:162:9: '<'
                    {
                    dbg.location(162,9);
                    match(input,LT,FOLLOW_LT_in_relationalOp647); 

                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:163:9: '>'
                    {
                    dbg.location(163,9);
                    match(input,GT,FOLLOW_GT_in_relationalOp657); 

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
        dbg.location(164, 5);

        }
        finally {
            dbg.exitRule("relationalOp");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end relationalOp


    // $ANTLR start additiveExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:178:1: additiveExpression : multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* ;
    public final void additiveExpression() throws RecognitionException {
        try { dbg.enterRule("additiveExpression");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(178, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:179:5: ( multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )* )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:179:9: multiplicativeExpression ( ( '+' | '-' ) multiplicativeExpression )*
            {
            dbg.location(179,9);
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression680);
            multiplicativeExpression();
            _fsp--;

            dbg.location(180,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:180:9: ( ( '+' | '-' ) multiplicativeExpression )*
            try { dbg.enterSubRule(12);

            loop12:
            do {
                int alt12=2;
                try { dbg.enterDecision(12);

                int LA12_0 = input.LA(1);

                if ( ((LA12_0>=PLUS && LA12_0<=SUB)) ) {
                    alt12=1;
                }


                } finally {dbg.exitDecision(12);}

                switch (alt12) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:181:13: ( '+' | '-' ) multiplicativeExpression
            	    {
            	    dbg.location(181,13);
            	    if ( (input.LA(1)>=PLUS && input.LA(1)<=SUB) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        dbg.recognitionException(mse);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_additiveExpression707);    throw mse;
            	    }

            	    dbg.location(184,13);
            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression757);
            	    multiplicativeExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);
            } finally {dbg.exitSubRule(12);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(186, 5);

        }
        finally {
            dbg.exitRule("additiveExpression");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end additiveExpression


    // $ANTLR start multiplicativeExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:188:1: multiplicativeExpression : unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* ;
    public final void multiplicativeExpression() throws RecognitionException {
        try { dbg.enterRule("multiplicativeExpression");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(188, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:189:5: ( unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )* )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:190:9: unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
            {
            dbg.location(190,9);
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression795);
            unaryExpression();
            _fsp--;

            dbg.location(191,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:191:9: ( ( '*' | '/' | '%' ) unaryExpression )*
            try { dbg.enterSubRule(13);

            loop13:
            do {
                int alt13=2;
                try { dbg.enterDecision(13);

                int LA13_0 = input.LA(1);

                if ( ((LA13_0>=STAR && LA13_0<=SLASH)||LA13_0==PERCENT) ) {
                    alt13=1;
                }


                } finally {dbg.exitDecision(13);}

                switch (alt13) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:192:13: ( '*' | '/' | '%' ) unaryExpression
            	    {
            	    dbg.location(192,13);
            	    if ( (input.LA(1)>=STAR && input.LA(1)<=SLASH)||input.LA(1)==PERCENT ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        dbg.recognitionException(mse);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_multiplicativeExpression822);    throw mse;
            	    }

            	    dbg.location(196,13);
            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression890);
            	    unaryExpression();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);
            } finally {dbg.exitSubRule(13);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(198, 5);

        }
        finally {
            dbg.exitRule("multiplicativeExpression");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end multiplicativeExpression


    // $ANTLR start unaryExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:200:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );
    public final void unaryExpression() throws RecognitionException {
        try { dbg.enterRule("unaryExpression");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(200, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:205:5: ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus )
            int alt14=5;
            try { dbg.enterDecision(14);

            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt14=1;
                }
                break;
            case SUB:
                {
                alt14=2;
                }
                break;
            case PLUSPLUS:
                {
                alt14=3;
                }
                break;
            case SUBSUB:
                {
                alt14=4;
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
                alt14=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("200:1: unaryExpression : ( '+' unaryExpression | '-' unaryExpression | '++' unaryExpression | '--' unaryExpression | unaryExpressionNotPlusMinus );", 14, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }

            } finally {dbg.exitDecision(14);}

            switch (alt14) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:205:9: '+' unaryExpression
                    {
                    dbg.location(205,9);
                    match(input,PLUS,FOLLOW_PLUS_in_unaryExpression923); 
                    dbg.location(205,13);
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression925);
                    unaryExpression();
                    _fsp--;


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:206:9: '-' unaryExpression
                    {
                    dbg.location(206,9);
                    match(input,SUB,FOLLOW_SUB_in_unaryExpression935); 
                    dbg.location(206,13);
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression937);
                    unaryExpression();
                    _fsp--;


                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:207:9: '++' unaryExpression
                    {
                    dbg.location(207,9);
                    match(input,PLUSPLUS,FOLLOW_PLUSPLUS_in_unaryExpression947); 
                    dbg.location(207,14);
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression949);
                    unaryExpression();
                    _fsp--;


                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:208:9: '--' unaryExpression
                    {
                    dbg.location(208,9);
                    match(input,SUBSUB,FOLLOW_SUBSUB_in_unaryExpression959); 
                    dbg.location(208,14);
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression961);
                    unaryExpression();
                    _fsp--;


                    }
                    break;
                case 5 :
                    dbg.enterAlt(5);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:209:9: unaryExpressionNotPlusMinus
                    {
                    dbg.location(209,9);
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression971);
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
        dbg.location(210, 5);

        }
        finally {
            dbg.exitRule("unaryExpression");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end unaryExpression


    // $ANTLR start unaryExpressionNotPlusMinus
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:212:1: unaryExpressionNotPlusMinus : primary ( selector )* ( '++' | '--' )? ;
    public final void unaryExpressionNotPlusMinus() throws RecognitionException {
        try { dbg.enterRule("unaryExpressionNotPlusMinus");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(212, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:213:5: ( primary ( selector )* ( '++' | '--' )? )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:218:5: primary ( selector )* ( '++' | '--' )?
            {
            dbg.location(218,5);
            pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus1016);
            primary();
            _fsp--;

            dbg.location(219,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:219:9: ( selector )*
            try { dbg.enterSubRule(15);

            loop15:
            do {
                int alt15=2;
                try { dbg.enterDecision(15);

                int LA15_0 = input.LA(1);

                if ( (LA15_0==LBRACKET||LA15_0==DOT) ) {
                    alt15=1;
                }


                } finally {dbg.exitDecision(15);}

                switch (alt15) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:219:10: selector
            	    {
            	    dbg.location(219,10);
            	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus1027);
            	    selector();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);
            } finally {dbg.exitSubRule(15);}

            dbg.location(221,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:221:9: ( '++' | '--' )?
            int alt16=2;
            try { dbg.enterSubRule(16);
            try { dbg.enterDecision(16);

            int LA16_0 = input.LA(1);

            if ( ((LA16_0>=PLUSPLUS && LA16_0<=SUBSUB)) ) {
                alt16=1;
            }
            } finally {dbg.exitDecision(16);}

            switch (alt16) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:
                    {
                    dbg.location(221,9);
                    if ( (input.LA(1)>=PLUSPLUS && input.LA(1)<=SUBSUB) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        dbg.recognitionException(mse);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_unaryExpressionNotPlusMinus1048);    throw mse;
                    }


                    }
                    break;

            }
            } finally {dbg.exitSubRule(16);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(224, 5);

        }
        finally {
            dbg.exitRule("unaryExpressionNotPlusMinus");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end unaryExpressionNotPlusMinus


    // $ANTLR start castExpression
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:226:1: castExpression : ( '(' primitiveType | type ')' unaryExpression );
    public final void castExpression() throws RecognitionException {
        try { dbg.enterRule("castExpression");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(226, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:227:2: ( '(' primitiveType | type ')' unaryExpression )
            int alt17=2;
            try { dbg.enterDecision(17);

            int LA17_0 = input.LA(1);

            if ( (LA17_0==LPAREN) ) {
                alt17=1;
            }
            else if ( (LA17_0==IDENTIFIER||LA17_0==BOOLEAN||LA17_0==BYTE||LA17_0==CHAR||LA17_0==DOUBLE||LA17_0==FLOAT||LA17_0==INT||LA17_0==LONG||LA17_0==SHORT) ) {
                alt17=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("226:1: castExpression : ( '(' primitiveType | type ')' unaryExpression );", 17, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(17);}

            switch (alt17) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:228:3: '(' primitiveType
                    {
                    dbg.location(228,3);
                    match(input,LPAREN,FOLLOW_LPAREN_in_castExpression1094); 
                    dbg.location(228,7);
                    pushFollow(FOLLOW_primitiveType_in_castExpression1096);
                    primitiveType();
                    _fsp--;


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:228:23: type ')' unaryExpression
                    {
                    dbg.location(228,23);
                    pushFollow(FOLLOW_type_in_castExpression1100);
                    type();
                    _fsp--;

                    dbg.location(228,28);
                    match(input,RPAREN,FOLLOW_RPAREN_in_castExpression1102); 
                    dbg.location(228,32);
                    pushFollow(FOLLOW_unaryExpression_in_castExpression1104);
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
        dbg.location(231, 5);

        }
        finally {
            dbg.exitRule("castExpression");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end castExpression


    // $ANTLR start primary
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:233:1: primary : ( 'this' ( '.' IDENTIFIER )* ( identifierSuffix )? | IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );
    public final void primary() throws RecognitionException {
        try { dbg.enterRule("primary");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(233, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:237:5: ( 'this' ( '.' IDENTIFIER )* ( identifierSuffix )? | IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' )
            int alt23=7;
            try { dbg.enterDecision(23);

            switch ( input.LA(1) ) {
            case THIS:
                {
                alt23=1;
                }
                break;
            case IDENTIFIER:
                {
                alt23=2;
                }
                break;
            case SUPER:
                {
                alt23=3;
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
                alt23=4;
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
                alt23=5;
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
                alt23=6;
                }
                break;
            case VOID:
                {
                alt23=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("233:1: primary : ( 'this' ( '.' IDENTIFIER )* ( identifierSuffix )? | IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )? | 'super' superSuffix | literal | creator | primitiveType ( '[' ']' )* '.' 'class' | 'void' '.' 'class' );", 23, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }

            } finally {dbg.exitDecision(23);}

            switch (alt23) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:239:6: 'this' ( '.' IDENTIFIER )* ( identifierSuffix )?
                    {
                    dbg.location(239,6);
                    match(input,THIS,FOLLOW_THIS_in_primary1149); 
                    dbg.location(240,9);
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:240:9: ( '.' IDENTIFIER )*
                    try { dbg.enterSubRule(18);

                    loop18:
                    do {
                        int alt18=2;
                        try { dbg.enterDecision(18);

                        int LA18_0 = input.LA(1);

                        if ( (LA18_0==DOT) ) {
                            int LA18_2 = input.LA(2);

                            if ( (LA18_2==IDENTIFIER) ) {
                                alt18=1;
                            }


                        }


                        } finally {dbg.exitDecision(18);}

                        switch (alt18) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:240:10: '.' IDENTIFIER
                    	    {
                    	    dbg.location(240,10);
                    	    match(input,DOT,FOLLOW_DOT_in_primary1160); 
                    	    dbg.location(240,14);
                    	    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary1162); 

                    	    }
                    	    break;

                    	default :
                    	    break loop18;
                        }
                    } while (true);
                    } finally {dbg.exitSubRule(18);}

                    dbg.location(242,9);
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:242:9: ( identifierSuffix )?
                    int alt19=2;
                    try { dbg.enterSubRule(19);
                    try { dbg.enterDecision(19);

                    try {
                        isCyclicDecision = true;
                        alt19 = dfa19.predict(input);
                    }
                    catch (NoViableAltException nvae) {
                        dbg.recognitionException(nvae);
                        throw nvae;
                    }
                    } finally {dbg.exitDecision(19);}

                    switch (alt19) {
                        case 1 :
                            dbg.enterAlt(1);

                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:242:10: identifierSuffix
                            {
                            dbg.location(242,10);
                            pushFollow(FOLLOW_identifierSuffix_in_primary1184);
                            identifierSuffix();
                            _fsp--;


                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(19);}


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:244:9: IDENTIFIER ( '.' IDENTIFIER )* ( identifierSuffix )?
                    {
                    dbg.location(244,9);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary1205); 
                    dbg.location(245,9);
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:245:9: ( '.' IDENTIFIER )*
                    try { dbg.enterSubRule(20);

                    loop20:
                    do {
                        int alt20=2;
                        try { dbg.enterDecision(20);

                        int LA20_0 = input.LA(1);

                        if ( (LA20_0==DOT) ) {
                            int LA20_2 = input.LA(2);

                            if ( (LA20_2==IDENTIFIER) ) {
                                alt20=1;
                            }


                        }


                        } finally {dbg.exitDecision(20);}

                        switch (alt20) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:245:10: '.' IDENTIFIER
                    	    {
                    	    dbg.location(245,10);
                    	    match(input,DOT,FOLLOW_DOT_in_primary1216); 
                    	    dbg.location(245,14);
                    	    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_primary1218); 

                    	    }
                    	    break;

                    	default :
                    	    break loop20;
                        }
                    } while (true);
                    } finally {dbg.exitSubRule(20);}

                    dbg.location(247,9);
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:247:9: ( identifierSuffix )?
                    int alt21=2;
                    try { dbg.enterSubRule(21);
                    try { dbg.enterDecision(21);

                    try {
                        isCyclicDecision = true;
                        alt21 = dfa21.predict(input);
                    }
                    catch (NoViableAltException nvae) {
                        dbg.recognitionException(nvae);
                        throw nvae;
                    }
                    } finally {dbg.exitDecision(21);}

                    switch (alt21) {
                        case 1 :
                            dbg.enterAlt(1);

                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:247:10: identifierSuffix
                            {
                            dbg.location(247,10);
                            pushFollow(FOLLOW_identifierSuffix_in_primary1240);
                            identifierSuffix();
                            _fsp--;


                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(21);}


                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:249:9: 'super' superSuffix
                    {
                    dbg.location(249,9);
                    match(input,SUPER,FOLLOW_SUPER_in_primary1261); 
                    dbg.location(250,9);
                    pushFollow(FOLLOW_superSuffix_in_primary1271);
                    superSuffix();
                    _fsp--;


                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:251:9: literal
                    {
                    dbg.location(251,9);
                    pushFollow(FOLLOW_literal_in_primary1281);
                    literal();
                    _fsp--;


                    }
                    break;
                case 5 :
                    dbg.enterAlt(5);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:252:9: creator
                    {
                    dbg.location(252,9);
                    pushFollow(FOLLOW_creator_in_primary1291);
                    creator();
                    _fsp--;


                    }
                    break;
                case 6 :
                    dbg.enterAlt(6);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:253:9: primitiveType ( '[' ']' )* '.' 'class'
                    {
                    dbg.location(253,9);
                    pushFollow(FOLLOW_primitiveType_in_primary1301);
                    primitiveType();
                    _fsp--;

                    dbg.location(254,9);
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:254:9: ( '[' ']' )*
                    try { dbg.enterSubRule(22);

                    loop22:
                    do {
                        int alt22=2;
                        try { dbg.enterDecision(22);

                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==LBRACKET) ) {
                            alt22=1;
                        }


                        } finally {dbg.exitDecision(22);}

                        switch (alt22) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:254:10: '[' ']'
                    	    {
                    	    dbg.location(254,10);
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_primary1312); 
                    	    dbg.location(254,14);
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_primary1314); 

                    	    }
                    	    break;

                    	default :
                    	    break loop22;
                        }
                    } while (true);
                    } finally {dbg.exitSubRule(22);}

                    dbg.location(256,9);
                    match(input,DOT,FOLLOW_DOT_in_primary1335); 
                    dbg.location(256,13);
                    match(input,CLASS,FOLLOW_CLASS_in_primary1337); 

                    }
                    break;
                case 7 :
                    dbg.enterAlt(7);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:257:9: 'void' '.' 'class'
                    {
                    dbg.location(257,9);
                    match(input,VOID,FOLLOW_VOID_in_primary1347); 
                    dbg.location(257,16);
                    match(input,DOT,FOLLOW_DOT_in_primary1349); 
                    dbg.location(257,20);
                    match(input,CLASS,FOLLOW_CLASS_in_primary1351); 

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
        dbg.location(258, 5);

        }
        finally {
            dbg.exitRule("primary");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end primary


    // $ANTLR start superSuffix
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:261:1: superSuffix : ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? );
    public final void superSuffix() throws RecognitionException {
        try { dbg.enterRule("superSuffix");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(261, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:262:5: ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? )
            int alt26=2;
            try { dbg.enterDecision(26);

            int LA26_0 = input.LA(1);

            if ( (LA26_0==LPAREN) ) {
                alt26=1;
            }
            else if ( (LA26_0==DOT) ) {
                alt26=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("261:1: superSuffix : ( arguments | '.' ( typeArguments )? IDENTIFIER ( arguments )? );", 26, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(26);}

            switch (alt26) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:262:9: arguments
                    {
                    dbg.location(262,9);
                    pushFollow(FOLLOW_arguments_in_superSuffix1377);
                    arguments();
                    _fsp--;


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:263:9: '.' ( typeArguments )? IDENTIFIER ( arguments )?
                    {
                    dbg.location(263,9);
                    match(input,DOT,FOLLOW_DOT_in_superSuffix1387); 
                    dbg.location(263,13);
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:263:13: ( typeArguments )?
                    int alt24=2;
                    try { dbg.enterSubRule(24);
                    try { dbg.enterDecision(24);

                    int LA24_0 = input.LA(1);

                    if ( (LA24_0==LT) ) {
                        alt24=1;
                    }
                    } finally {dbg.exitDecision(24);}

                    switch (alt24) {
                        case 1 :
                            dbg.enterAlt(1);

                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:263:14: typeArguments
                            {
                            dbg.location(263,14);
                            pushFollow(FOLLOW_typeArguments_in_superSuffix1390);
                            typeArguments();
                            _fsp--;


                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(24);}

                    dbg.location(265,9);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_superSuffix1411); 
                    dbg.location(266,9);
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:266:9: ( arguments )?
                    int alt25=2;
                    try { dbg.enterSubRule(25);
                    try { dbg.enterDecision(25);

                    int LA25_0 = input.LA(1);

                    if ( (LA25_0==LPAREN) ) {
                        alt25=1;
                    }
                    } finally {dbg.exitDecision(25);}

                    switch (alt25) {
                        case 1 :
                            dbg.enterAlt(1);

                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:266:10: arguments
                            {
                            dbg.location(266,10);
                            pushFollow(FOLLOW_arguments_in_superSuffix1422);
                            arguments();
                            _fsp--;


                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(25);}


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
        dbg.location(268, 5);

        }
        finally {
            dbg.exitRule("superSuffix");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end superSuffix


    // $ANTLR start identifierSuffix
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:270:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | '.' 'class' | '.' 'this' );
    public final void identifierSuffix() throws RecognitionException {
        try { dbg.enterRule("identifierSuffix");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(270, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:271:5: ( ( '[' ']' )+ '.' 'class' | '.' 'class' | '.' 'this' )
            int alt28=3;
            try { dbg.enterDecision(28);

            int LA28_0 = input.LA(1);

            if ( (LA28_0==LBRACKET) ) {
                alt28=1;
            }
            else if ( (LA28_0==DOT) ) {
                int LA28_2 = input.LA(2);

                if ( (LA28_2==CLASS) ) {
                    alt28=2;
                }
                else if ( (LA28_2==THIS) ) {
                    alt28=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("270:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | '.' 'class' | '.' 'this' );", 28, 2, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("270:1: identifierSuffix : ( ( '[' ']' )+ '.' 'class' | '.' 'class' | '.' 'this' );", 28, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(28);}

            switch (alt28) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:271:9: ( '[' ']' )+ '.' 'class'
                    {
                    dbg.location(271,9);
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:271:9: ( '[' ']' )+
                    int cnt27=0;
                    try { dbg.enterSubRule(27);

                    loop27:
                    do {
                        int alt27=2;
                        try { dbg.enterDecision(27);

                        int LA27_0 = input.LA(1);

                        if ( (LA27_0==LBRACKET) ) {
                            alt27=1;
                        }


                        } finally {dbg.exitDecision(27);}

                        switch (alt27) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:271:10: '[' ']'
                    	    {
                    	    dbg.location(271,10);
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_identifierSuffix1454); 
                    	    dbg.location(271,14);
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_identifierSuffix1456); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt27 >= 1 ) break loop27;
                                EarlyExitException eee =
                                    new EarlyExitException(27, input);
                                dbg.recognitionException(eee);

                                throw eee;
                        }
                        cnt27++;
                    } while (true);
                    } finally {dbg.exitSubRule(27);}

                    dbg.location(272,9);
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix1468); 
                    dbg.location(272,13);
                    match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix1470); 

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:275:9: '.' 'class'
                    {
                    dbg.location(275,9);
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix1490); 
                    dbg.location(275,13);
                    match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix1492); 

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:277:9: '.' 'this'
                    {
                    dbg.location(277,9);
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix1506); 
                    dbg.location(277,13);
                    match(input,THIS,FOLLOW_THIS_in_identifierSuffix1508); 

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
        dbg.location(280, 5);

        }
        finally {
            dbg.exitRule("identifierSuffix");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end identifierSuffix


    // $ANTLR start selector
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:283:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '[' expression ']' );
    public final void selector() throws RecognitionException {
        try { dbg.enterRule("selector");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(283, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:284:5: ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '[' expression ']' )
            int alt30=4;
            try { dbg.enterDecision(30);

            int LA30_0 = input.LA(1);

            if ( (LA30_0==DOT) ) {
                switch ( input.LA(2) ) {
                case SUPER:
                    {
                    alt30=3;
                    }
                    break;
                case IDENTIFIER:
                    {
                    alt30=1;
                    }
                    break;
                case THIS:
                    {
                    alt30=2;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("283:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '[' expression ']' );", 30, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }

            }
            else if ( (LA30_0==LBRACKET) ) {
                alt30=4;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("283:1: selector : ( '.' IDENTIFIER ( arguments )? | '.' 'this' | '.' 'super' superSuffix | '[' expression ']' );", 30, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(30);}

            switch (alt30) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:284:9: '.' IDENTIFIER ( arguments )?
                    {
                    dbg.location(284,9);
                    match(input,DOT,FOLLOW_DOT_in_selector1539); 
                    dbg.location(284,13);
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_selector1541); 
                    dbg.location(285,9);
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:285:9: ( arguments )?
                    int alt29=2;
                    try { dbg.enterSubRule(29);
                    try { dbg.enterDecision(29);

                    int LA29_0 = input.LA(1);

                    if ( (LA29_0==LPAREN) ) {
                        alt29=1;
                    }
                    } finally {dbg.exitDecision(29);}

                    switch (alt29) {
                        case 1 :
                            dbg.enterAlt(1);

                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:285:10: arguments
                            {
                            dbg.location(285,10);
                            pushFollow(FOLLOW_arguments_in_selector1552);
                            arguments();
                            _fsp--;


                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(29);}


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:287:9: '.' 'this'
                    {
                    dbg.location(287,9);
                    match(input,DOT,FOLLOW_DOT_in_selector1573); 
                    dbg.location(287,13);
                    match(input,THIS,FOLLOW_THIS_in_selector1575); 

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:288:9: '.' 'super' superSuffix
                    {
                    dbg.location(288,9);
                    match(input,DOT,FOLLOW_DOT_in_selector1585); 
                    dbg.location(288,13);
                    match(input,SUPER,FOLLOW_SUPER_in_selector1587); 
                    dbg.location(289,9);
                    pushFollow(FOLLOW_superSuffix_in_selector1597);
                    superSuffix();
                    _fsp--;


                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:291:9: '[' expression ']'
                    {
                    dbg.location(291,9);
                    match(input,LBRACKET,FOLLOW_LBRACKET_in_selector1611); 
                    dbg.location(291,13);
                    pushFollow(FOLLOW_expression_in_selector1613);
                    expression();
                    _fsp--;

                    dbg.location(291,24);
                    match(input,RBRACKET,FOLLOW_RBRACKET_in_selector1615); 

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
        dbg.location(292, 5);

        }
        finally {
            dbg.exitRule("selector");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end selector


    // $ANTLR start creator
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:294:1: creator : ( | 'new' classOrInterfaceType classCreatorRest );
    public final void creator() throws RecognitionException {
        try { dbg.enterRule("creator");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(294, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:295:5: ( | 'new' classOrInterfaceType classCreatorRest )
            int alt31=2;
            try { dbg.enterDecision(31);

            int LA31_0 = input.LA(1);

            if ( (LA31_0==EOF||LA31_0==INSTANCEOF||LA31_0==RPAREN||(LA31_0>=LBRACKET && LA31_0<=RBRACKET)||(LA31_0>=COMMA && LA31_0<=DOT)||(LA31_0>=QUES && LA31_0<=PERCENT)||(LA31_0>=BANGEQ && LA31_0<=LT)) ) {
                alt31=1;
            }
            else if ( (LA31_0==NEW) ) {
                alt31=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("294:1: creator : ( | 'new' classOrInterfaceType classCreatorRest );", 31, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(31);}

            switch (alt31) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:296:5: 
                    {
                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:296:9: 'new' classOrInterfaceType classCreatorRest
                    {
                    dbg.location(296,9);
                    match(input,NEW,FOLLOW_NEW_in_creator1644); 
                    dbg.location(296,15);
                    pushFollow(FOLLOW_classOrInterfaceType_in_creator1646);
                    classOrInterfaceType();
                    _fsp--;

                    dbg.location(296,36);
                    pushFollow(FOLLOW_classCreatorRest_in_creator1648);
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
        dbg.location(298, 5);

        }
        finally {
            dbg.exitRule("creator");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end creator


    // $ANTLR start typeList
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:301:1: typeList : type ( ',' type )* ;
    public final void typeList() throws RecognitionException {
        try { dbg.enterRule("typeList");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(301, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:302:5: ( type ( ',' type )* )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:302:9: type ( ',' type )*
            {
            dbg.location(302,9);
            pushFollow(FOLLOW_type_in_typeList1674);
            type();
            _fsp--;

            dbg.location(303,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:303:9: ( ',' type )*
            try { dbg.enterSubRule(32);

            loop32:
            do {
                int alt32=2;
                try { dbg.enterDecision(32);

                int LA32_0 = input.LA(1);

                if ( (LA32_0==COMMA) ) {
                    alt32=1;
                }


                } finally {dbg.exitDecision(32);}

                switch (alt32) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:303:10: ',' type
            	    {
            	    dbg.location(303,10);
            	    match(input,COMMA,FOLLOW_COMMA_in_typeList1685); 
            	    dbg.location(303,14);
            	    pushFollow(FOLLOW_type_in_typeList1687);
            	    type();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop32;
                }
            } while (true);
            } finally {dbg.exitSubRule(32);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(305, 5);

        }
        finally {
            dbg.exitRule("typeList");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end typeList


    // $ANTLR start classCreatorRest
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:307:1: classCreatorRest : arguments ;
    public final void classCreatorRest() throws RecognitionException {
        try { dbg.enterRule("classCreatorRest");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(307, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:308:5: ( arguments )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:308:9: arguments
            {
            dbg.location(308,9);
            pushFollow(FOLLOW_arguments_in_classCreatorRest1718);
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
        dbg.location(311, 5);

        }
        finally {
            dbg.exitRule("classCreatorRest");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end classCreatorRest


    // $ANTLR start variableInitializer
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:330:1: variableInitializer : expression ;
    public final void variableInitializer() throws RecognitionException {
        try { dbg.enterRule("variableInitializer");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(330, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:331:5: ( expression )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:333:6: expression
            {
            dbg.location(333,6);
            pushFollow(FOLLOW_expression_in_variableInitializer1770);
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
        dbg.location(334, 5);

        }
        finally {
            dbg.exitRule("variableInitializer");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end variableInitializer


    // $ANTLR start createdName
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:347:1: createdName : ( classOrInterfaceType | primitiveType );
    public final void createdName() throws RecognitionException {
        try { dbg.enterRule("createdName");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(347, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:348:5: ( classOrInterfaceType | primitiveType )
            int alt33=2;
            try { dbg.enterDecision(33);

            int LA33_0 = input.LA(1);

            if ( (LA33_0==IDENTIFIER) ) {
                alt33=1;
            }
            else if ( (LA33_0==BOOLEAN||LA33_0==BYTE||LA33_0==CHAR||LA33_0==DOUBLE||LA33_0==FLOAT||LA33_0==INT||LA33_0==LONG||LA33_0==SHORT) ) {
                alt33=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("347:1: createdName : ( classOrInterfaceType | primitiveType );", 33, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(33);}

            switch (alt33) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:348:9: classOrInterfaceType
                    {
                    dbg.location(348,9);
                    pushFollow(FOLLOW_classOrInterfaceType_in_createdName1794);
                    classOrInterfaceType();
                    _fsp--;


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:349:9: primitiveType
                    {
                    dbg.location(349,9);
                    pushFollow(FOLLOW_primitiveType_in_createdName1804);
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
        dbg.location(350, 5);

        }
        finally {
            dbg.exitRule("createdName");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end createdName


    // $ANTLR start arguments
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:375:1: arguments : '(' ( expressionList )? ')' ;
    public final void arguments() throws RecognitionException {
        try { dbg.enterRule("arguments");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(375, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:376:5: ( '(' ( expressionList )? ')' )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:376:9: '(' ( expressionList )? ')'
            {
            dbg.location(376,9);
            match(input,LPAREN,FOLLOW_LPAREN_in_arguments1835); 
            dbg.location(376,13);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:376:13: ( expressionList )?
            int alt34=2;
            try { dbg.enterSubRule(34);
            try { dbg.enterDecision(34);

            int LA34_0 = input.LA(1);

            if ( ((LA34_0>=IDENTIFIER && LA34_0<=NULL)||LA34_0==INSTANCEOF||LA34_0==SUPER||LA34_0==THIS||LA34_0==LBRACKET||(LA34_0>=COMMA && LA34_0<=DOT)||LA34_0==QUES||(LA34_0>=EQEQ && LA34_0<=PERCENT)||(LA34_0>=BANGEQ && LA34_0<=LT)) ) {
                alt34=1;
            }
            else if ( (LA34_0==BOOLEAN||LA34_0==BYTE||LA34_0==CHAR||LA34_0==DOUBLE||LA34_0==FLOAT||LA34_0==INT||LA34_0==LONG||LA34_0==NEW||LA34_0==SHORT||LA34_0==VOID||LA34_0==RPAREN) ) {
                alt34=1;
            }
            } finally {dbg.exitDecision(34);}

            switch (alt34) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:376:14: expressionList
                    {
                    dbg.location(376,14);
                    pushFollow(FOLLOW_expressionList_in_arguments1838);
                    expressionList();
                    _fsp--;


                    }
                    break;

            }
            } finally {dbg.exitSubRule(34);}

            dbg.location(377,12);
            match(input,RPAREN,FOLLOW_RPAREN_in_arguments1851); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(378, 5);

        }
        finally {
            dbg.exitRule("arguments");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end arguments


    // $ANTLR start type
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:380:1: type : ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* );
    public final void type() throws RecognitionException {
        try { dbg.enterRule("type");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(380, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:381:5: ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* )
            int alt37=2;
            try { dbg.enterDecision(37);

            int LA37_0 = input.LA(1);

            if ( (LA37_0==IDENTIFIER) ) {
                alt37=1;
            }
            else if ( (LA37_0==BOOLEAN||LA37_0==BYTE||LA37_0==CHAR||LA37_0==DOUBLE||LA37_0==FLOAT||LA37_0==INT||LA37_0==LONG||LA37_0==SHORT) ) {
                alt37=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("380:1: type : ( classOrInterfaceType ( '[' ']' )* | primitiveType ( '[' ']' )* );", 37, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(37);}

            switch (alt37) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:381:9: classOrInterfaceType ( '[' ']' )*
                    {
                    dbg.location(381,9);
                    pushFollow(FOLLOW_classOrInterfaceType_in_type1871);
                    classOrInterfaceType();
                    _fsp--;

                    dbg.location(382,9);
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:382:9: ( '[' ']' )*
                    try { dbg.enterSubRule(35);

                    loop35:
                    do {
                        int alt35=2;
                        try { dbg.enterDecision(35);

                        int LA35_0 = input.LA(1);

                        if ( (LA35_0==LBRACKET) ) {
                            alt35=1;
                        }


                        } finally {dbg.exitDecision(35);}

                        switch (alt35) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:382:10: '[' ']'
                    	    {
                    	    dbg.location(382,10);
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_type1882); 
                    	    dbg.location(382,14);
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_type1884); 

                    	    }
                    	    break;

                    	default :
                    	    break loop35;
                        }
                    } while (true);
                    } finally {dbg.exitSubRule(35);}


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:384:9: primitiveType ( '[' ']' )*
                    {
                    dbg.location(384,9);
                    pushFollow(FOLLOW_primitiveType_in_type1905);
                    primitiveType();
                    _fsp--;

                    dbg.location(385,9);
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:385:9: ( '[' ']' )*
                    try { dbg.enterSubRule(36);

                    loop36:
                    do {
                        int alt36=2;
                        try { dbg.enterDecision(36);

                        int LA36_0 = input.LA(1);

                        if ( (LA36_0==LBRACKET) ) {
                            alt36=1;
                        }


                        } finally {dbg.exitDecision(36);}

                        switch (alt36) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:385:10: '[' ']'
                    	    {
                    	    dbg.location(385,10);
                    	    match(input,LBRACKET,FOLLOW_LBRACKET_in_type1916); 
                    	    dbg.location(385,14);
                    	    match(input,RBRACKET,FOLLOW_RBRACKET_in_type1918); 

                    	    }
                    	    break;

                    	default :
                    	    break loop36;
                        }
                    } while (true);
                    } finally {dbg.exitSubRule(36);}


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
        dbg.location(387, 5);

        }
        finally {
            dbg.exitRule("type");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end type


    // $ANTLR start classOrInterfaceType
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:390:1: classOrInterfaceType : IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )* ;
    public final void classOrInterfaceType() throws RecognitionException {
        try { dbg.enterRule("classOrInterfaceType");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(390, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:391:5: ( IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )* )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:391:9: IDENTIFIER ( typeArguments )? ( '.' IDENTIFIER ( typeArguments )? )*
            {
            dbg.location(391,9);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classOrInterfaceType1950); 
            dbg.location(392,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:392:9: ( typeArguments )?
            int alt38=2;
            try { dbg.enterSubRule(38);
            try { dbg.enterDecision(38);

            int LA38_0 = input.LA(1);

            if ( (LA38_0==LT) ) {
                alt38=1;
            }
            } finally {dbg.exitDecision(38);}

            switch (alt38) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:392:10: typeArguments
                    {
                    dbg.location(392,10);
                    pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType1961);
                    typeArguments();
                    _fsp--;


                    }
                    break;

            }
            } finally {dbg.exitSubRule(38);}

            dbg.location(394,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:394:9: ( '.' IDENTIFIER ( typeArguments )? )*
            try { dbg.enterSubRule(40);

            loop40:
            do {
                int alt40=2;
                try { dbg.enterDecision(40);

                int LA40_0 = input.LA(1);

                if ( (LA40_0==DOT) ) {
                    alt40=1;
                }


                } finally {dbg.exitDecision(40);}

                switch (alt40) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:394:10: '.' IDENTIFIER ( typeArguments )?
            	    {
            	    dbg.location(394,10);
            	    match(input,DOT,FOLLOW_DOT_in_classOrInterfaceType1983); 
            	    dbg.location(394,14);
            	    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_classOrInterfaceType1985); 
            	    dbg.location(395,13);
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:395:13: ( typeArguments )?
            	    int alt39=2;
            	    try { dbg.enterSubRule(39);
            	    try { dbg.enterDecision(39);

            	    int LA39_0 = input.LA(1);

            	    if ( (LA39_0==LT) ) {
            	        alt39=1;
            	    }
            	    } finally {dbg.exitDecision(39);}

            	    switch (alt39) {
            	        case 1 :
            	            dbg.enterAlt(1);

            	            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:395:14: typeArguments
            	            {
            	            dbg.location(395,14);
            	            pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType2000);
            	            typeArguments();
            	            _fsp--;


            	            }
            	            break;

            	    }
            	    } finally {dbg.exitSubRule(39);}


            	    }
            	    break;

            	default :
            	    break loop40;
                }
            } while (true);
            } finally {dbg.exitSubRule(40);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(398, 5);

        }
        finally {
            dbg.exitRule("classOrInterfaceType");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end classOrInterfaceType


    // $ANTLR start typeArguments
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:400:1: typeArguments : '<' typeArgument ( ',' typeArgument )* '>' ;
    public final void typeArguments() throws RecognitionException {
        try { dbg.enterRule("typeArguments");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(400, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:401:5: ( '<' typeArgument ( ',' typeArgument )* '>' )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:401:9: '<' typeArgument ( ',' typeArgument )* '>'
            {
            dbg.location(401,9);
            match(input,LT,FOLLOW_LT_in_typeArguments2046); 
            dbg.location(401,13);
            pushFollow(FOLLOW_typeArgument_in_typeArguments2048);
            typeArgument();
            _fsp--;

            dbg.location(402,9);
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:402:9: ( ',' typeArgument )*
            try { dbg.enterSubRule(41);

            loop41:
            do {
                int alt41=2;
                try { dbg.enterDecision(41);

                int LA41_0 = input.LA(1);

                if ( (LA41_0==COMMA) ) {
                    alt41=1;
                }


                } finally {dbg.exitDecision(41);}

                switch (alt41) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:402:10: ',' typeArgument
            	    {
            	    dbg.location(402,10);
            	    match(input,COMMA,FOLLOW_COMMA_in_typeArguments2059); 
            	    dbg.location(402,14);
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments2061);
            	    typeArgument();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop41;
                }
            } while (true);
            } finally {dbg.exitSubRule(41);}

            dbg.location(404,9);
            match(input,GT,FOLLOW_GT_in_typeArguments2083); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(405, 5);

        }
        finally {
            dbg.exitRule("typeArguments");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end typeArguments


    // $ANTLR start typeArgument
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:407:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );
    public final void typeArgument() throws RecognitionException {
        try { dbg.enterRule("typeArgument");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(407, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:408:5: ( type | '?' ( ( 'extends' | 'super' ) type )? )
            int alt43=2;
            try { dbg.enterDecision(43);

            int LA43_0 = input.LA(1);

            if ( (LA43_0==IDENTIFIER||LA43_0==BOOLEAN||LA43_0==BYTE||LA43_0==CHAR||LA43_0==DOUBLE||LA43_0==FLOAT||LA43_0==INT||LA43_0==LONG||LA43_0==SHORT) ) {
                alt43=1;
            }
            else if ( (LA43_0==QUES) ) {
                alt43=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("407:1: typeArgument : ( type | '?' ( ( 'extends' | 'super' ) type )? );", 43, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(43);}

            switch (alt43) {
                case 1 :
                    dbg.enterAlt(1);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:408:9: type
                    {
                    dbg.location(408,9);
                    pushFollow(FOLLOW_type_in_typeArgument2103);
                    type();
                    _fsp--;


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:409:9: '?' ( ( 'extends' | 'super' ) type )?
                    {
                    dbg.location(409,9);
                    match(input,QUES,FOLLOW_QUES_in_typeArgument2113); 
                    dbg.location(410,9);
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:410:9: ( ( 'extends' | 'super' ) type )?
                    int alt42=2;
                    try { dbg.enterSubRule(42);
                    try { dbg.enterDecision(42);

                    int LA42_0 = input.LA(1);

                    if ( (LA42_0==EXTENDS||LA42_0==SUPER) ) {
                        alt42=1;
                    }
                    } finally {dbg.exitDecision(42);}

                    switch (alt42) {
                        case 1 :
                            dbg.enterAlt(1);

                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:411:13: ( 'extends' | 'super' ) type
                            {
                            dbg.location(411,13);
                            if ( input.LA(1)==EXTENDS||input.LA(1)==SUPER ) {
                                input.consume();
                                errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse =
                                    new MismatchedSetException(null,input);
                                dbg.recognitionException(mse);
                                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_typeArgument2137);    throw mse;
                            }

                            dbg.location(414,13);
                            pushFollow(FOLLOW_type_in_typeArgument2181);
                            type();
                            _fsp--;


                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(42);}


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
        dbg.location(416, 5);

        }
        finally {
            dbg.exitRule("typeArgument");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end typeArgument


    // $ANTLR start primitiveType
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:419:1: primitiveType : ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' );
    public final void primitiveType() throws RecognitionException {
        try { dbg.enterRule("primitiveType");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(419, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:420:5: ( 'boolean' | 'char' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:
            {
            dbg.location(420,5);
            if ( input.LA(1)==BOOLEAN||input.LA(1)==BYTE||input.LA(1)==CHAR||input.LA(1)==DOUBLE||input.LA(1)==FLOAT||input.LA(1)==INT||input.LA(1)==LONG||input.LA(1)==SHORT ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                dbg.recognitionException(mse);
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
        dbg.location(428, 5);

        }
        finally {
            dbg.exitRule("primitiveType");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end primitiveType


    // $ANTLR start literal
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:430:1: literal : ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL );
    public final void literal() throws RecognitionException {
        try { dbg.enterRule("literal");
        if ( ruleLevel==0 ) {dbg.commence();}
        ruleLevel++;
        dbg.location(430, 1);

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:431:5: ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL )
            dbg.enterAlt(1);

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:
            {
            dbg.location(431,5);
            if ( (input.LA(1)>=INTLITERAL && input.LA(1)<=NULL) ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                dbg.recognitionException(mse);
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
        dbg.location(440, 5);

        }
        finally {
            dbg.exitRule("literal");
            ruleLevel--;
            if ( ruleLevel==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end literal


    protected DFA19 dfa19 = new DFA19(this);
    protected DFA21 dfa21 = new DFA21(this);
    static final String DFA19_eotS =
        "\7\uffff";
    static final String DFA19_eofS =
        "\1\3\3\uffff\1\3\2\uffff";
    static final String DFA19_minS =
        "\1\63\2\4\1\uffff\1\63\1\uffff\1\4";
    static final String DFA19_maxS =
        "\2\163\1\104\1\uffff\1\163\1\uffff\1\104";
    static final String DFA19_acceptS =
        "\3\uffff\1\2\1\uffff\1\1\1\uffff";
    static final String DFA19_specialS =
        "\7\uffff}>";
    static final String[] DFA19_transitionS = {
            "\1\3\31\uffff\1\3\2\uffff\1\1\1\3\1\uffff\1\3\1\2\4\uffff\17"+
            "\3\11\uffff\3\3",
            "\12\3\16\uffff\1\3\1\uffff\1\3\2\uffff\1\3\5\uffff\1\3\5\uffff"+
            "\1\3\5\uffff\2\3\1\uffff\1\3\1\uffff\1\3\5\uffff\1\3\2\uffff"+
            "\1\3\2\uffff\1\3\4\uffff\1\3\6\uffff\1\3\1\4\2\uffff\1\3\4\uffff"+
            "\1\3\1\uffff\15\3\11\uffff\3\3",
            "\1\3\35\uffff\1\5\36\uffff\1\3\2\uffff\1\5",
            "",
            "\1\3\31\uffff\1\3\2\uffff\1\1\1\3\1\uffff\1\3\1\6\4\uffff\17"+
            "\3\11\uffff\3\3",
            "",
            "\1\3\35\uffff\1\5\36\uffff\1\3\2\uffff\1\3"
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "242:9: ( identifierSuffix )?";
        }
        public void error(NoViableAltException nvae) {
            dbg.recognitionException(nvae);
        }
    }
    static final String DFA21_eotS =
        "\7\uffff";
    static final String DFA21_eofS =
        "\1\3\3\uffff\1\3\2\uffff";
    static final String DFA21_minS =
        "\1\63\2\4\1\uffff\1\63\1\uffff\1\4";
    static final String DFA21_maxS =
        "\2\163\1\104\1\uffff\1\163\1\uffff\1\104";
    static final String DFA21_acceptS =
        "\3\uffff\1\2\1\uffff\1\1\1\uffff";
    static final String DFA21_specialS =
        "\7\uffff}>";
    static final String[] DFA21_transitionS = {
            "\1\3\31\uffff\1\3\2\uffff\1\1\1\3\1\uffff\1\3\1\2\4\uffff\17"+
            "\3\11\uffff\3\3",
            "\12\3\16\uffff\1\3\1\uffff\1\3\2\uffff\1\3\5\uffff\1\3\5\uffff"+
            "\1\3\5\uffff\2\3\1\uffff\1\3\1\uffff\1\3\5\uffff\1\3\2\uffff"+
            "\1\3\2\uffff\1\3\4\uffff\1\3\6\uffff\1\3\1\4\2\uffff\1\3\4\uffff"+
            "\1\3\1\uffff\15\3\11\uffff\3\3",
            "\1\3\35\uffff\1\5\36\uffff\1\3\2\uffff\1\5",
            "",
            "\1\3\31\uffff\1\3\2\uffff\1\1\1\3\1\uffff\1\3\1\6\4\uffff\17"+
            "\3\11\uffff\3\3",
            "",
            "\1\3\35\uffff\1\5\36\uffff\1\3\2\uffff\1\3"
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
            return "247:9: ( identifierSuffix )?";
        }
        public void error(NoViableAltException nvae) {
            dbg.recognitionException(nvae);
        }
    }
 

    public static final BitSet FOLLOW_expression_in_rhs38 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_rhs49 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression65 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList113 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_COMMA_in_expressionList124 = new BitSet(new long[]{0x4158208250003FF2L,0x000E00FFFA190212L});
    public static final BitSet FOLLOW_expression_in_expressionList126 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression158 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_QUES_in_conditionalExpression166 = new BitSet(new long[]{0x4158208250003FF0L,0x000E00FFFE110212L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression168 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression170 = new BitSet(new long[]{0x4158208250003FF0L,0x000E00FFFA110212L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalExpression172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression203 = new BitSet(new long[]{0x0000000000000002L,0x0000000020000000L});
    public static final BitSet FOLLOW_BARBAR_in_conditionalOrExpression214 = new BitSet(new long[]{0x4158208250003FF2L,0x000E00FFF8110212L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression216 = new BitSet(new long[]{0x0000000000000002L,0x0000000020000000L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression247 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
    public static final BitSet FOLLOW_AMPAMP_in_conditionalAndExpression258 = new BitSet(new long[]{0x4158208250003FF2L,0x000E00FFD8110212L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression260 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression291 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_BAR_in_inclusiveOrExpression302 = new BitSet(new long[]{0x4158208250003FF2L,0x000E00FFC8110212L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression304 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression335 = new BitSet(new long[]{0x0000000000000002L,0x0000004000000000L});
    public static final BitSet FOLLOW_CARET_in_exclusiveOrExpression346 = new BitSet(new long[]{0x4158208250003FF2L,0x000E00DFC8110212L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression348 = new BitSet(new long[]{0x0000000000000002L,0x0000004000000000L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression379 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_AMP_in_andExpression390 = new BitSet(new long[]{0x4158208250003FF2L,0x000E009FC8110212L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression392 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression423 = new BitSet(new long[]{0x0000000000000002L,0x0002000008000000L});
    public static final BitSet FOLLOW_set_in_equalityExpression450 = new BitSet(new long[]{0x4158208250003FF2L,0x000E008FC8110212L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression500 = new BitSet(new long[]{0x0000000000000002L,0x0002000008000000L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression531 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_INSTANCEOF_in_instanceOfExpression542 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression575 = new BitSet(new long[]{0x0000000000000002L,0x000C000000000000L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression587 = new BitSet(new long[]{0x4150208250003FF2L,0x000C008FC0110212L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression589 = new BitSet(new long[]{0x0000000000000002L,0x000C000000000000L});
    public static final BitSet FOLLOW_LT_in_relationalOp622 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_EQ_in_relationalOp624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_relationalOp635 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_EQ_in_relationalOp637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_relationalOp647 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_relationalOp657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression680 = new BitSet(new long[]{0x0000000000000002L,0x0000000300000000L});
    public static final BitSet FOLLOW_set_in_additiveExpression707 = new BitSet(new long[]{0x4150208250003FF2L,0x0000008FC0110212L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression757 = new BitSet(new long[]{0x0000000000000002L,0x0000000300000000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression795 = new BitSet(new long[]{0x0000000000000002L,0x0000008C00000000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression822 = new BitSet(new long[]{0x4150208250003FF2L,0x0000008FC0110212L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression890 = new BitSet(new long[]{0x0000000000000002L,0x0000008C00000000L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression923 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C0110212L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUB_in_unaryExpression935 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C0110212L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSPLUS_in_unaryExpression947 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C0110212L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSUB_in_unaryExpression959 = new BitSet(new long[]{0x4150208250003FF0L,0x00000003C0110212L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression971 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus1016 = new BitSet(new long[]{0x0000000000000002L,0x00000000C0110000L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus1027 = new BitSet(new long[]{0x0000000000000002L,0x00000000C0110000L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus1048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_castExpression1094 = new BitSet(new long[]{0x4050208250000000L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression1096 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_castExpression1100 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_RPAREN_in_castExpression1102 = new BitSet(new long[]{0x4150208250003FF2L,0x00000003C0110212L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression1104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THIS_in_primary1149 = new BitSet(new long[]{0x0000000000000002L,0x0000000000110000L});
    public static final BitSet FOLLOW_DOT_in_primary1160 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary1162 = new BitSet(new long[]{0x0000000000000002L,0x0000000000110000L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary1184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary1205 = new BitSet(new long[]{0x0000000000000002L,0x0000000000110000L});
    public static final BitSet FOLLOW_DOT_in_primary1216 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_primary1218 = new BitSet(new long[]{0x0000000000000002L,0x0000000000110000L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary1240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_in_primary1261 = new BitSet(new long[]{0x0000000000000000L,0x0000000000101000L});
    public static final BitSet FOLLOW_superSuffix_in_primary1271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary1281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_creator_in_primary1291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary1301 = new BitSet(new long[]{0x0000000000000000L,0x0000000000110000L});
    public static final BitSet FOLLOW_LBRACKET_in_primary1312 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_primary1314 = new BitSet(new long[]{0x0000000000000000L,0x0000000000110000L});
    public static final BitSet FOLLOW_DOT_in_primary1335 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_CLASS_in_primary1337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VOID_in_primary1347 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_primary1349 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_CLASS_in_primary1351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix1377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix1387 = new BitSet(new long[]{0x0000000000000010L,0x0008000000000000L});
    public static final BitSet FOLLOW_typeArguments_in_superSuffix1390 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_superSuffix1411 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_arguments_in_superSuffix1422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_identifierSuffix1454 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_identifierSuffix1456 = new BitSet(new long[]{0x0000000000000000L,0x0000000000110000L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix1468 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_CLASS_in_identifierSuffix1470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix1490 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_CLASS_in_identifierSuffix1492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix1506 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_THIS_in_identifierSuffix1508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector1539 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_selector1541 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_arguments_in_selector1552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector1573 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_THIS_in_selector1575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector1585 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_in_selector1587 = new BitSet(new long[]{0x0000000000000000L,0x0000000000101000L});
    public static final BitSet FOLLOW_superSuffix_in_selector1597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_selector1611 = new BitSet(new long[]{0x4158208250003FF0L,0x000E00FFFA130212L});
    public static final BitSet FOLLOW_expression_in_selector1613 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_selector1615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_creator1644 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_creator1646 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator1648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList1674 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_COMMA_in_typeList1685 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_typeList1687 = new BitSet(new long[]{0x0000000000000002L,0x0000000000080000L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest1718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer1770 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_createdName1794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName1804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_arguments1835 = new BitSet(new long[]{0x4158208250003FF0L,0x000E00FFFA192212L});
    public static final BitSet FOLLOW_expressionList_in_arguments1838 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_RPAREN_in_arguments1851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_type1871 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_LBRACKET_in_type1882 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_type1884 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_primitiveType_in_type1905 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_LBRACKET_in_type1916 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RBRACKET_in_type1918 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classOrInterfaceType1950 = new BitSet(new long[]{0x0000000000000002L,0x0008000000100000L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType1961 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_classOrInterfaceType1983 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_classOrInterfaceType1985 = new BitSet(new long[]{0x0000000000000002L,0x0008000000100000L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType2000 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_LT_in_typeArguments2046 = new BitSet(new long[]{0x4050208250000010L,0x0000000002000000L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments2048 = new BitSet(new long[]{0x0000000000000000L,0x0004000000080000L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments2059 = new BitSet(new long[]{0x4050208250000010L,0x0000000002000000L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments2061 = new BitSet(new long[]{0x0000000000000000L,0x0004000000080000L});
    public static final BitSet FOLLOW_GT_in_typeArguments2083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument2103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUES_in_typeArgument2113 = new BitSet(new long[]{0x0000040000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_typeArgument2137 = new BitSet(new long[]{0x4050208250000010L});
    public static final BitSet FOLLOW_type_in_typeArgument2181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_primitiveType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(new long[]{0x0000000000000002L});

}