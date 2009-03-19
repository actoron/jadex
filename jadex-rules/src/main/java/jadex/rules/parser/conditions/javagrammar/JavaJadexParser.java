// $ANTLR 3.0.1 C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g 2009-03-19 18:19:46

package jadex.rules.parser.conditions.javagrammar;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class JavaJadexParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENTIFIER", "CharacterLiteral", "StringLiteral", "BooleanLiteral", "FloatingPointLiteral", "HexLiteral", "OctalLiteral", "DecimalLiteral", "IntegerNumber", "LongSuffix", "LONGLITERAL", "INTLITERAL", "HexPrefix", "HexDigit", "Exponent", "NonIntegerNumber", "FloatSuffix", "DoubleSuffix", "FLOATLITERAL", "DOUBLELITERAL", "EscapeSequence", "CHARLITERAL", "STRINGLITERAL", "ABSTRACT", "ASSERT", "BOOLEAN", "BREAK", "BYTE", "CASE", "CATCH", "CHAR", "CLASS", "CONST", "CONTINUE", "DEFAULT", "DO", "DOUBLE", "ELSE", "ENUM", "EXTENDS", "FINAL", "FINALLY", "FLOAT", "FOR", "GOTO", "IF", "IMPLEMENTS", "IMPORT", "INSTANCEOF", "INT", "INTERFACE", "LONG", "NATIVE", "NEW", "PACKAGE", "PRIVATE", "PROTECTED", "PUBLIC", "RETURN", "SHORT", "STATIC", "STRICTFP", "SUPER", "SWITCH", "SYNCHRONIZED", "THIS", "THROW", "THROWS", "TRANSIENT", "TRY", "VOID", "VOLATILE", "WHILE", "TRUE", "FALSE", "NULL", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET", "SEMI", "COMMA", "DOT", "ELLIPSIS", "EQ", "BANG", "TILDE", "QUES", "COLON", "EQEQ", "AMPAMP", "BARBAR", "PLUSPLUS", "SUBSUB", "PLUS", "SUB", "STAR", "SLASH", "AMP", "BAR", "CARET", "PERCENT", "PLUSEQ", "SUBEQ", "STAREQ", "SLASHEQ", "AMPEQ", "BAREQ", "CARETEQ", "PERCENTEQ", "MONKEYS_AT", "BANGEQ", "GT", "LT", "IntegerTypeSuffix", "FloatTypeSuffix", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "COMMENT", "LINE_COMMENT"
    };
    public static final int PACKAGE=58;
    public static final int LT=119;
    public static final int STAR=102;
    public static final int WHILE=76;
    public static final int FloatTypeSuffix=121;
    public static final int OctalLiteral=10;
    public static final int CONST=36;
    public static final int CASE=32;
    public static final int CHAR=34;
    public static final int NEW=57;
    public static final int DO=39;
    public static final int EOF=-1;
    public static final int BREAK=30;
    public static final int LBRACKET=84;
    public static final int FINAL=44;
    public static final int RPAREN=81;
    public static final int IMPORT=51;
    public static final int SUBSUB=99;
    public static final int STAREQ=110;
    public static final int FloatSuffix=20;
    public static final int NonIntegerNumber=19;
    public static final int CARET=106;
    public static final int RETURN=62;
    public static final int THIS=69;
    public static final int DOUBLE=40;
    public static final int MONKEYS_AT=116;
    public static final int BARBAR=97;
    public static final int VOID=74;
    public static final int SUPER=66;
    public static final int GOTO=48;
    public static final int EQ=90;
    public static final int COMMENT=127;
    public static final int AMPAMP=96;
    public static final int QUES=93;
    public static final int EQEQ=95;
    public static final int HexPrefix=16;
    public static final int RBRACE=83;
    public static final int LINE_COMMENT=128;
    public static final int IntegerTypeSuffix=120;
    public static final int PRIVATE=59;
    public static final int STATIC=64;
    public static final int SWITCH=67;
    public static final int NULL=79;
    public static final int ELSE=41;
    public static final int STRICTFP=65;
    public static final int DOUBLELITERAL=23;
    public static final int NATIVE=56;
    public static final int ELLIPSIS=89;
    public static final int THROWS=71;
    public static final int INT=53;
    public static final int SLASHEQ=111;
    public static final int INTLITERAL=15;
    public static final int ASSERT=28;
    public static final int TRY=73;
    public static final int LongSuffix=13;
    public static final int LONGLITERAL=14;
    public static final int WS=126;
    public static final int FloatingPointLiteral=8;
    public static final int CHARLITERAL=25;
    public static final int JavaIDDigit=125;
    public static final int GT=118;
    public static final int CATCH=33;
    public static final int FALSE=78;
    public static final int Letter=124;
    public static final int EscapeSequence=24;
    public static final int THROW=70;
    public static final int BooleanLiteral=7;
    public static final int PROTECTED=60;
    public static final int CLASS=35;
    public static final int BAREQ=113;
    public static final int IntegerNumber=12;
    public static final int AMP=104;
    public static final int PLUSPLUS=98;
    public static final int CharacterLiteral=5;
    public static final int LBRACE=82;
    public static final int SUBEQ=109;
    public static final int Exponent=18;
    public static final int FOR=47;
    public static final int SUB=101;
    public static final int FLOAT=46;
    public static final int ABSTRACT=27;
    public static final int HexDigit=17;
    public static final int PLUSEQ=108;
    public static final int LPAREN=80;
    public static final int IF=49;
    public static final int SLASH=103;
    public static final int BOOLEAN=29;
    public static final int SYNCHRONIZED=68;
    public static final int IMPLEMENTS=50;
    public static final int CONTINUE=37;
    public static final int COMMA=87;
    public static final int AMPEQ=112;
    public static final int IDENTIFIER=4;
    public static final int TRANSIENT=72;
    public static final int TILDE=92;
    public static final int BANGEQ=117;
    public static final int PLUS=100;
    public static final int RBRACKET=85;
    public static final int DOT=88;
    public static final int HexLiteral=9;
    public static final int BYTE=31;
    public static final int PERCENT=107;
    public static final int VOLATILE=75;
    public static final int DEFAULT=38;
    public static final int SHORT=63;
    public static final int BANG=91;
    public static final int INSTANCEOF=52;
    public static final int DecimalLiteral=11;
    public static final int TRUE=77;
    public static final int SEMI=86;
    public static final int COLON=94;
    public static final int StringLiteral=6;
    public static final int ENUM=42;
    public static final int PERCENTEQ=115;
    public static final int DoubleSuffix=21;
    public static final int FINALLY=45;
    public static final int UnicodeEscape=122;
    public static final int STRINGLITERAL=26;
    public static final int CARETEQ=114;
    public static final int INTERFACE=54;
    public static final int LONG=55;
    public static final int EXTENDS=43;
    public static final int FLOATLITERAL=22;
    public static final int PUBLIC=61;
    public static final int OctalEscape=123;
    public static final int BAR=105;

        public JavaJadexParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g"; }

    



    // $ANTLR start rhs
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:21:1: rhs : expression EOF ;
    public final void rhs() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:25:2: ( expression EOF )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:25:4: expression EOF
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:28:1: expression : relationalExpression ;
    public final void expression() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:33:2: ( relationalExpression )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:33:4: relationalExpression
            {
            pushFollow(FOLLOW_relationalExpression_in_expression55);
            relationalExpression();
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


    // $ANTLR start relationalExpression
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:36:1: relationalExpression returns [Constraint constraint] : left= unaryExpression ( ( '<' | '<' '=' | '>' | '>' '=' ) right= unaryExpression )? ;
    public final Constraint relationalExpression() throws RecognitionException {
        Constraint constraint = null;

        UnaryExpression left = null;

        UnaryExpression right = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:40:2: (left= unaryExpression ( ( '<' | '<' '=' | '>' | '>' '=' ) right= unaryExpression )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:40:4: left= unaryExpression ( ( '<' | '<' '=' | '>' | '>' '=' ) right= unaryExpression )?
            {
            pushFollow(FOLLOW_unaryExpression_in_relationalExpression76);
            left=unaryExpression();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:41:9: ( ( '<' | '<' '=' | '>' | '>' '=' ) right= unaryExpression )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( ((LA2_0>=GT && LA2_0<=LT)) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:41:10: ( '<' | '<' '=' | '>' | '>' '=' ) right= unaryExpression
                    {
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:41:10: ( '<' | '<' '=' | '>' | '>' '=' )
                    int alt1=4;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==LT) ) {
                        int LA1_1 = input.LA(2);

                        if ( (LA1_1==EQ) ) {
                            alt1=2;
                        }
                        else if ( ((LA1_1>=IDENTIFIER && LA1_1<=DecimalLiteral)||(LA1_1>=NULL && LA1_1<=LPAREN)||(LA1_1>=PLUS && LA1_1<=SUB)) ) {
                            alt1=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("41:10: ( '<' | '<' '=' | '>' | '>' '=' )", 1, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA1_0==GT) ) {
                        int LA1_2 = input.LA(2);

                        if ( (LA1_2==EQ) ) {
                            alt1=4;
                        }
                        else if ( ((LA1_2>=IDENTIFIER && LA1_2<=DecimalLiteral)||(LA1_2>=NULL && LA1_2<=LPAREN)||(LA1_2>=PLUS && LA1_2<=SUB)) ) {
                            alt1=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("41:10: ( '<' | '<' '=' | '>' | '>' '=' )", 1, 2, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("41:10: ( '<' | '<' '=' | '>' | '>' '=' )", 1, 0, input);

                        throw nvae;
                    }
                    switch (alt1) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:41:11: '<'
                            {
                            match(input,LT,FOLLOW_LT_in_relationalExpression88); 

                            }
                            break;
                        case 2 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:41:15: '<' '='
                            {
                            match(input,LT,FOLLOW_LT_in_relationalExpression90); 
                            match(input,EQ,FOLLOW_EQ_in_relationalExpression91); 

                            }
                            break;
                        case 3 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:41:22: '>'
                            {
                            match(input,GT,FOLLOW_GT_in_relationalExpression93); 

                            }
                            break;
                        case 4 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:41:26: '>' '='
                            {
                            match(input,GT,FOLLOW_GT_in_relationalExpression95); 
                            match(input,EQ,FOLLOW_EQ_in_relationalExpression96); 

                            }
                            break;

                    }

                    pushFollow(FOLLOW_unaryExpression_in_relationalExpression103);
                    right=unaryExpression();
                    _fsp--;

                    
                            	constraint = new Constraint(left, right, "blurps");
                            

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
        return constraint;
    }
    // $ANTLR end relationalExpression


    // $ANTLR start unaryExpression
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:48:1: unaryExpression returns [UnaryExpression exp] : prim= primary ( suffix )* ;
    public final UnaryExpression unaryExpression() throws RecognitionException {
        UnaryExpression exp = null;

        Primary prim = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:52:2: (prim= primary ( suffix )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:52:4: prim= primary ( suffix )*
            {
            pushFollow(FOLLOW_primary_in_unaryExpression146);
            prim=primary();
            _fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:52:19: ( suffix )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==DOT) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:52:20: suffix
            	    {
            	    pushFollow(FOLLOW_suffix_in_unaryExpression149);
            	    suffix();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            
            		exp = new UnaryExpression(prim, null);
            	

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
    // $ANTLR end unaryExpression


    // $ANTLR start primary
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:58:1: primary returns [Primary prim] : ( '(' expression ')' | lit= literal | var= variable );
    public final Primary primary() throws RecognitionException {
        Primary prim = null;

        Object lit = null;

        String var = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:62:2: ( '(' expression ')' | lit= literal | var= variable )
            int alt4=3;
            switch ( input.LA(1) ) {
            case LPAREN:
                {
                alt4=1;
                }
                break;
            case CharacterLiteral:
            case StringLiteral:
            case BooleanLiteral:
            case FloatingPointLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case NULL:
            case PLUS:
            case SUB:
                {
                alt4=2;
                }
                break;
            case IDENTIFIER:
                {
                alt4=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("58:1: primary returns [Primary prim] : ( '(' expression ')' | lit= literal | var= variable );", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:62:4: '(' expression ')'
                    {
                    match(input,LPAREN,FOLLOW_LPAREN_in_primary171); 
                    pushFollow(FOLLOW_expression_in_primary173);
                    expression();
                    _fsp--;

                    match(input,RPAREN,FOLLOW_RPAREN_in_primary175); 

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:63:4: lit= literal
                    {
                    pushFollow(FOLLOW_literal_in_primary184);
                    lit=literal();
                    _fsp--;

                    prim = new Literal(lit);

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:64:4: var= variable
                    {
                    pushFollow(FOLLOW_variable_in_primary194);
                    var=variable();
                    _fsp--;

                    prim = new Variable(var);

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
        return prim;
    }
    // $ANTLR end primary


    // $ANTLR start suffix
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:67:1: suffix : ( fieldAccess | methodAccess );
    public final void suffix() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:71:2: ( fieldAccess | methodAccess )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==DOT) ) {
                int LA5_1 = input.LA(2);

                if ( (LA5_1==IDENTIFIER) ) {
                    int LA5_2 = input.LA(3);

                    if ( (LA5_2==LPAREN) ) {
                        alt5=2;
                    }
                    else if ( (LA5_2==EOF||LA5_2==RPAREN||(LA5_2>=COMMA && LA5_2<=DOT)||(LA5_2>=GT && LA5_2<=LT)) ) {
                        alt5=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("67:1: suffix : ( fieldAccess | methodAccess );", 5, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("67:1: suffix : ( fieldAccess | methodAccess );", 5, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("67:1: suffix : ( fieldAccess | methodAccess );", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:71:4: fieldAccess
                    {
                    pushFollow(FOLLOW_fieldAccess_in_suffix209);
                    fieldAccess();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:72:4: methodAccess
                    {
                    pushFollow(FOLLOW_methodAccess_in_suffix214);
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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:75:1: fieldAccess : '.' IDENTIFIER ;
    public final void fieldAccess() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:79:2: ( '.' IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:79:4: '.' IDENTIFIER
            {
            match(input,DOT,FOLLOW_DOT_in_fieldAccess227); 
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_fieldAccess229); 

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:82:1: methodAccess : ( '.' IDENTIFIER '(' ')' | '.' IDENTIFIER '(' expression ( ',' expression )* ')' );
    public final void methodAccess() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:86:2: ( '.' IDENTIFIER '(' ')' | '.' IDENTIFIER '(' expression ( ',' expression )* ')' )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==DOT) ) {
                int LA7_1 = input.LA(2);

                if ( (LA7_1==IDENTIFIER) ) {
                    int LA7_2 = input.LA(3);

                    if ( (LA7_2==LPAREN) ) {
                        int LA7_3 = input.LA(4);

                        if ( (LA7_3==RPAREN) ) {
                            alt7=1;
                        }
                        else if ( ((LA7_3>=IDENTIFIER && LA7_3<=DecimalLiteral)||(LA7_3>=NULL && LA7_3<=LPAREN)||(LA7_3>=PLUS && LA7_3<=SUB)) ) {
                            alt7=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("82:1: methodAccess : ( '.' IDENTIFIER '(' ')' | '.' IDENTIFIER '(' expression ( ',' expression )* ')' );", 7, 3, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("82:1: methodAccess : ( '.' IDENTIFIER '(' ')' | '.' IDENTIFIER '(' expression ( ',' expression )* ')' );", 7, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("82:1: methodAccess : ( '.' IDENTIFIER '(' ')' | '.' IDENTIFIER '(' expression ( ',' expression )* ')' );", 7, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("82:1: methodAccess : ( '.' IDENTIFIER '(' ')' | '.' IDENTIFIER '(' expression ( ',' expression )* ')' );", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:86:4: '.' IDENTIFIER '(' ')'
                    {
                    match(input,DOT,FOLLOW_DOT_in_methodAccess242); 
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodAccess244); 
                    match(input,LPAREN,FOLLOW_LPAREN_in_methodAccess246); 
                    match(input,RPAREN,FOLLOW_RPAREN_in_methodAccess248); 

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:87:4: '.' IDENTIFIER '(' expression ( ',' expression )* ')'
                    {
                    match(input,DOT,FOLLOW_DOT_in_methodAccess253); 
                    match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_methodAccess255); 
                    match(input,LPAREN,FOLLOW_LPAREN_in_methodAccess257); 
                    pushFollow(FOLLOW_expression_in_methodAccess259);
                    expression();
                    _fsp--;

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:87:34: ( ',' expression )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==COMMA) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:87:35: ',' expression
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_methodAccess262); 
                    	    pushFollow(FOLLOW_expression_in_methodAccess264);
                    	    expression();
                    	    _fsp--;


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);

                    match(input,RPAREN,FOLLOW_RPAREN_in_methodAccess268); 

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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:90:1: variable returns [String name] : tmp= IDENTIFIER ;
    public final String variable() throws RecognitionException {
        String name = null;

        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:94:2: (tmp= IDENTIFIER )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:94:4: tmp= IDENTIFIER
            {
            tmp=(Token)input.LT(1);
            match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_variable289); 
            name = tmp.getText();

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return name;
    }
    // $ANTLR end variable


    // $ANTLR start literal
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:97:1: literal returns [Object val] : (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );
    public final Object literal() throws RecognitionException {
        Object val = null;

        Token CharacterLiteral1=null;
        Token StringLiteral2=null;
        Token BooleanLiteral3=null;
        Object lit = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:98:2: (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' )
            int alt8=6;
            switch ( input.LA(1) ) {
            case PLUS:
            case SUB:
                {
                int LA8_1 = input.LA(2);

                if ( ((LA8_1>=HexLiteral && LA8_1<=DecimalLiteral)) ) {
                    alt8=2;
                }
                else if ( (LA8_1==FloatingPointLiteral) ) {
                    alt8=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("97:1: literal returns [Object val] : (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );", 8, 1, input);

                    throw nvae;
                }
                }
                break;
            case FloatingPointLiteral:
                {
                alt8=1;
                }
                break;
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
                {
                alt8=2;
                }
                break;
            case CharacterLiteral:
                {
                alt8=3;
                }
                break;
            case StringLiteral:
                {
                alt8=4;
                }
                break;
            case BooleanLiteral:
                {
                alt8=5;
                }
                break;
            case NULL:
                {
                alt8=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("97:1: literal returns [Object val] : (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:98:4: lit= floatingPointLiteral
                    {
                    pushFollow(FOLLOW_floatingPointLiteral_in_literal308);
                    lit=floatingPointLiteral();
                    _fsp--;

                    val = lit;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:99:4: lit= integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal317);
                    lit=integerLiteral();
                    _fsp--;

                    val = lit;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:100:4: CharacterLiteral
                    {
                    CharacterLiteral1=(Token)input.LT(1);
                    match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal324); 
                    val = new Character(CharacterLiteral1.getText().charAt(0));

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:101:4: StringLiteral
                    {
                    StringLiteral2=(Token)input.LT(1);
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_literal331); 
                    val = StringLiteral2.getText().substring(1, StringLiteral2.getText().length()-1);

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:102:4: BooleanLiteral
                    {
                    BooleanLiteral3=(Token)input.LT(1);
                    match(input,BooleanLiteral,FOLLOW_BooleanLiteral_in_literal338); 
                    val = BooleanLiteral3.getText().equals("true")? Boolean.TRUE: Boolean.FALSE;

                    }
                    break;
                case 6 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:103:4: 'null'
                    {
                    match(input,NULL,FOLLOW_NULL_in_literal345); 
                    val = null;

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
        return val;
    }
    // $ANTLR end literal


    // $ANTLR start floatingPointLiteral
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:106:1: floatingPointLiteral returns [Object val] : (sign= ( '+' | '-' ) )? FloatingPointLiteral ;
    public final Object floatingPointLiteral() throws RecognitionException {
        Object val = null;

        Token sign=null;
        Token FloatingPointLiteral4=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:107:2: ( (sign= ( '+' | '-' ) )? FloatingPointLiteral )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:107:4: (sign= ( '+' | '-' ) )? FloatingPointLiteral
            {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:107:8: (sign= ( '+' | '-' ) )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( ((LA9_0>=PLUS && LA9_0<=SUB)) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:107:8: sign= ( '+' | '-' )
                    {
                    sign=(Token)input.LT(1);
                    if ( (input.LA(1)>=PLUS && input.LA(1)<=SUB) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_floatingPointLiteral364);    throw mse;
                    }


                    }
                    break;

            }

            FloatingPointLiteral4=(Token)input.LT(1);
            match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_floatingPointLiteral371); 
            val = sign!=null && "-".equals(sign.getText())? new Double("-"+FloatingPointLiteral4.getText()): new Double(FloatingPointLiteral4.getText());

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
    // $ANTLR end floatingPointLiteral


    // $ANTLR start integerLiteral
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:110:1: integerLiteral returns [Object val] : (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral ) ;
    public final Object integerLiteral() throws RecognitionException {
        Object val = null;

        Token sign=null;
        Token HexLiteral5=null;
        Token OctalLiteral6=null;
        Token DecimalLiteral7=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:111:2: ( (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral ) )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:111:4: (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral )
            {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:111:8: (sign= ( '+' | '-' ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( ((LA10_0>=PLUS && LA10_0<=SUB)) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:111:8: sign= ( '+' | '-' )
                    {
                    sign=(Token)input.LT(1);
                    if ( (input.LA(1)>=PLUS && input.LA(1)<=SUB) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_integerLiteral391);    throw mse;
                    }


                    }
                    break;

            }

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:111:20: ( HexLiteral | OctalLiteral | DecimalLiteral )
            int alt11=3;
            switch ( input.LA(1) ) {
            case HexLiteral:
                {
                alt11=1;
                }
                break;
            case OctalLiteral:
                {
                alt11=2;
                }
                break;
            case DecimalLiteral:
                {
                alt11=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("111:20: ( HexLiteral | OctalLiteral | DecimalLiteral )", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:111:21: HexLiteral
                    {
                    HexLiteral5=(Token)input.LT(1);
                    match(input,HexLiteral,FOLLOW_HexLiteral_in_integerLiteral399); 
                    val = sign!=null && "-".equals(sign.getText())? new Integer("-"+HexLiteral5.getText()): new Integer(HexLiteral5.getText());

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:112:4: OctalLiteral
                    {
                    OctalLiteral6=(Token)input.LT(1);
                    match(input,OctalLiteral,FOLLOW_OctalLiteral_in_integerLiteral406); 
                    val = sign!=null && "-".equals(sign.getText())? new Integer("-"+OctalLiteral6.getText()): new Integer(OctalLiteral6.getText());

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:113:4: DecimalLiteral
                    {
                    DecimalLiteral7=(Token)input.LT(1);
                    match(input,DecimalLiteral,FOLLOW_DecimalLiteral_in_integerLiteral413); 
                    val = sign!=null && "-".equals(sign.getText())? new Integer("-"+DecimalLiteral7.getText()): new Integer(DecimalLiteral7.getText());

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
    public static final BitSet FOLLOW_relationalExpression_in_expression55 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_relationalExpression76 = new BitSet(new long[]{0x0000000000000002L,0x00C0000000000000L});
    public static final BitSet FOLLOW_LT_in_relationalExpression88 = new BitSet(new long[]{0x0000000000000FF0L,0x0000003000018000L});
    public static final BitSet FOLLOW_LT_in_relationalExpression90 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_EQ_in_relationalExpression91 = new BitSet(new long[]{0x0000000000000FF0L,0x0000003000018000L});
    public static final BitSet FOLLOW_GT_in_relationalExpression93 = new BitSet(new long[]{0x0000000000000FF0L,0x0000003000018000L});
    public static final BitSet FOLLOW_GT_in_relationalExpression95 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_EQ_in_relationalExpression96 = new BitSet(new long[]{0x0000000000000FF0L,0x0000003000018000L});
    public static final BitSet FOLLOW_unaryExpression_in_relationalExpression103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpression146 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_suffix_in_unaryExpression149 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_LPAREN_in_primary171 = new BitSet(new long[]{0x0000000000000FF0L,0x0000003000018000L});
    public static final BitSet FOLLOW_expression_in_primary173 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RPAREN_in_primary175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_primary194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldAccess_in_suffix209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_methodAccess_in_suffix214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_fieldAccess227 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_fieldAccess229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_methodAccess242 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodAccess244 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_LPAREN_in_methodAccess246 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_RPAREN_in_methodAccess248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_methodAccess253 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_IDENTIFIER_in_methodAccess255 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_LPAREN_in_methodAccess257 = new BitSet(new long[]{0x0000000000000FF0L,0x0000003000018000L});
    public static final BitSet FOLLOW_expression_in_methodAccess259 = new BitSet(new long[]{0x0000000000000000L,0x0000000000820000L});
    public static final BitSet FOLLOW_COMMA_in_methodAccess262 = new BitSet(new long[]{0x0000000000000FF0L,0x0000003000018000L});
    public static final BitSet FOLLOW_expression_in_methodAccess264 = new BitSet(new long[]{0x0000000000000000L,0x0000000000820000L});
    public static final BitSet FOLLOW_RPAREN_in_methodAccess268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_floatingPointLiteral_in_literal308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BooleanLiteral_in_literal338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_floatingPointLiteral364 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_floatingPointLiteral371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral391 = new BitSet(new long[]{0x0000000000000E00L});
    public static final BitSet FOLLOW_HexLiteral_in_integerLiteral399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OctalLiteral_in_integerLiteral406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DecimalLiteral_in_integerLiteral413 = new BitSet(new long[]{0x0000000000000002L});

}