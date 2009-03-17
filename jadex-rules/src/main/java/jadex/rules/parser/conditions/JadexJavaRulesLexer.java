// $ANTLR 3.0.1 C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g 2009-03-17 18:14:53

package jadex.rules.parser.conditions;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class JadexJavaRulesLexer extends Lexer {
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
    public static final int NonIntegerNumber=19;
    public static final int FloatSuffix=20;
    public static final int STAREQ=106;
    public static final int CARET=102;
    public static final int THIS=68;
    public static final int RETURN=61;
    public static final int DOUBLE=39;
    public static final int MONKEYS_AT=112;
    public static final int BARBAR=93;
    public static final int VOID=73;
    public static final int SUPER=65;
    public static final int EQ=86;
    public static final int GOTO=47;
    public static final int COMMENT=24;
    public static final int AMPAMP=92;
    public static final int QUES=89;
    public static final int EQEQ=91;
    public static final int HexPrefix=16;
    public static final int RBRACE=79;
    public static final int LINE_COMMENT=25;
    public static final int STATIC=63;
    public static final int PRIVATE=58;
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
    public static final int Tokens=119;
    public static final int SEMI=82;
    public static final int TRUE=11;
    public static final int COLON=90;
    public static final int ENUM=41;
    public static final int FINALLY=44;
    public static final int DoubleSuffix=21;
    public static final int PERCENTEQ=111;
    public static final int STRINGLITERAL=10;
    public static final int CARETEQ=110;
    public static final int INTERFACE=53;
    public static final int LONG=54;
    public static final int PUBLIC=60;
    public static final int FLOATLITERAL=7;
    public static final int EXTENDS=42;
    public static final int BAR=101;
    public JadexJavaRulesLexer() {;} 
    public JadexJavaRulesLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g"; }

    // $ANTLR start LONGLITERAL
    public final void mLONGLITERAL() throws RecognitionException {
        try {
            int _type = LONGLITERAL;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:448:5: ( IntegerNumber LongSuffix )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:448:9: IntegerNumber LongSuffix
            {
            mIntegerNumber(); 
            mLongSuffix(); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LONGLITERAL

    // $ANTLR start INTLITERAL
    public final void mINTLITERAL() throws RecognitionException {
        try {
            int _type = INTLITERAL;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:453:5: ( IntegerNumber )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:453:9: IntegerNumber
            {
            mIntegerNumber(); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INTLITERAL

    // $ANTLR start IntegerNumber
    public final void mIntegerNumber() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:458:5: ( '0' | '1' .. '9' ( '0' .. '9' )* | '0' ( '0' .. '7' )+ | HexPrefix ( HexDigit )+ )
            int alt4=4;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='0') ) {
                switch ( input.LA(2) ) {
                case 'X':
                case 'x':
                    {
                    alt4=4;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt4=3;
                    }
                    break;
                default:
                    alt4=1;}

            }
            else if ( ((LA4_0>='1' && LA4_0<='9')) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("456:1: fragment IntegerNumber : ( '0' | '1' .. '9' ( '0' .. '9' )* | '0' ( '0' .. '7' )+ | HexPrefix ( HexDigit )+ );", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:458:9: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:459:9: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:459:18: ( '0' .. '9' )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:459:19: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop1;
                        }
                    } while (true);


                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:460:9: '0' ( '0' .. '7' )+
                    {
                    match('0'); 
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:460:13: ( '0' .. '7' )+
                    int cnt2=0;
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( ((LA2_0>='0' && LA2_0<='7')) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:460:14: '0' .. '7'
                    	    {
                    	    matchRange('0','7'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt2 >= 1 ) break loop2;
                                EarlyExitException eee =
                                    new EarlyExitException(2, input);
                                throw eee;
                        }
                        cnt2++;
                    } while (true);


                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:461:9: HexPrefix ( HexDigit )+
                    {
                    mHexPrefix(); 
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:461:19: ( HexDigit )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>='0' && LA3_0<='9')||(LA3_0>='A' && LA3_0<='F')||(LA3_0>='a' && LA3_0<='f')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:461:19: HexDigit
                    	    {
                    	    mHexDigit(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt3 >= 1 ) break loop3;
                                EarlyExitException eee =
                                    new EarlyExitException(3, input);
                                throw eee;
                        }
                        cnt3++;
                    } while (true);


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end IntegerNumber

    // $ANTLR start HexPrefix
    public final void mHexPrefix() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:466:5: ( '0x' | '0X' )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='0') ) {
                int LA5_1 = input.LA(2);

                if ( (LA5_1=='X') ) {
                    alt5=2;
                }
                else if ( (LA5_1=='x') ) {
                    alt5=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("464:1: fragment HexPrefix : ( '0x' | '0X' );", 5, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("464:1: fragment HexPrefix : ( '0x' | '0X' );", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:466:9: '0x'
                    {
                    match("0x"); 


                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:466:16: '0X'
                    {
                    match("0X"); 


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end HexPrefix

    // $ANTLR start HexDigit
    public final void mHexDigit() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:471:5: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:471:9: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end HexDigit

    // $ANTLR start LongSuffix
    public final void mLongSuffix() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:476:5: ( 'l' | 'L' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:
            {
            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end LongSuffix

    // $ANTLR start NonIntegerNumber
    public final void mNonIntegerNumber() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:482:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? | '.' ( '0' .. '9' )+ ( Exponent )? | ( '0' .. '9' )+ Exponent | ( '0' .. '9' )+ | HexPrefix ( HexDigit )* ( () | ( '.' ( HexDigit )* ) ) ( 'p' | 'P' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            int alt18=5;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:482:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )?
                    {
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:482:9: ( '0' .. '9' )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:482:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);

                    match('.'); 
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:482:27: ( '0' .. '9' )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:482:28: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:482:41: ( Exponent )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0=='E'||LA8_0=='e') ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:482:41: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:483:9: '.' ( '0' .. '9' )+ ( Exponent )?
                    {
                    match('.'); 
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:483:13: ( '0' .. '9' )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>='0' && LA9_0<='9')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:483:15: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt9 >= 1 ) break loop9;
                                EarlyExitException eee =
                                    new EarlyExitException(9, input);
                                throw eee;
                        }
                        cnt9++;
                    } while (true);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:483:29: ( Exponent )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='E'||LA10_0=='e') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:483:29: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:484:9: ( '0' .. '9' )+ Exponent
                    {
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:484:9: ( '0' .. '9' )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( ((LA11_0>='0' && LA11_0<='9')) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:484:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt11 >= 1 ) break loop11;
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
                                throw eee;
                        }
                        cnt11++;
                    } while (true);

                    mExponent(); 

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:485:9: ( '0' .. '9' )+
                    {
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:485:9: ( '0' .. '9' )+
                    int cnt12=0;
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( ((LA12_0>='0' && LA12_0<='9')) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:485:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt12 >= 1 ) break loop12;
                                EarlyExitException eee =
                                    new EarlyExitException(12, input);
                                throw eee;
                        }
                        cnt12++;
                    } while (true);


                    }
                    break;
                case 5 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:487:9: HexPrefix ( HexDigit )* ( () | ( '.' ( HexDigit )* ) ) ( 'p' | 'P' ) ( '+' | '-' )? ( '0' .. '9' )+
                    {
                    mHexPrefix(); 
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:487:19: ( HexDigit )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( ((LA13_0>='0' && LA13_0<='9')||(LA13_0>='A' && LA13_0<='F')||(LA13_0>='a' && LA13_0<='f')) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:487:20: HexDigit
                    	    {
                    	    mHexDigit(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop13;
                        }
                    } while (true);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:488:9: ( () | ( '.' ( HexDigit )* ) )
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0=='P'||LA15_0=='p') ) {
                        alt15=1;
                    }
                    else if ( (LA15_0=='.') ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("488:9: ( () | ( '.' ( HexDigit )* ) )", 15, 0, input);

                        throw nvae;
                    }
                    switch (alt15) {
                        case 1 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:488:14: ()
                            {
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:488:14: ()
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:488:15: 
                            {
                            }


                            }
                            break;
                        case 2 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:489:14: ( '.' ( HexDigit )* )
                            {
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:489:14: ( '.' ( HexDigit )* )
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:489:15: '.' ( HexDigit )*
                            {
                            match('.'); 
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:489:19: ( HexDigit )*
                            loop14:
                            do {
                                int alt14=2;
                                int LA14_0 = input.LA(1);

                                if ( ((LA14_0>='0' && LA14_0<='9')||(LA14_0>='A' && LA14_0<='F')||(LA14_0>='a' && LA14_0<='f')) ) {
                                    alt14=1;
                                }


                                switch (alt14) {
                            	case 1 :
                            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:489:20: HexDigit
                            	    {
                            	    mHexDigit(); 

                            	    }
                            	    break;

                            	default :
                            	    break loop14;
                                }
                            } while (true);


                            }


                            }
                            break;

                    }

                    if ( input.LA(1)=='P'||input.LA(1)=='p' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:492:9: ( '+' | '-' )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0=='+'||LA16_0=='-') ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:
                            {
                            if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                                input.consume();

                            }
                            else {
                                MismatchedSetException mse =
                                    new MismatchedSetException(null,input);
                                recover(mse);    throw mse;
                            }


                            }
                            break;

                    }

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:493:9: ( '0' .. '9' )+
                    int cnt17=0;
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( ((LA17_0>='0' && LA17_0<='9')) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:493:11: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt17 >= 1 ) break loop17;
                                EarlyExitException eee =
                                    new EarlyExitException(17, input);
                                throw eee;
                        }
                        cnt17++;
                    } while (true);


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end NonIntegerNumber

    // $ANTLR start Exponent
    public final void mExponent() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:498:5: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:498:9: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:498:23: ( '+' | '-' )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0=='+'||LA19_0=='-') ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;

            }

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:498:38: ( '0' .. '9' )+
            int cnt20=0;
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( ((LA20_0>='0' && LA20_0<='9')) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:498:40: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt20 >= 1 ) break loop20;
                        EarlyExitException eee =
                            new EarlyExitException(20, input);
                        throw eee;
                }
                cnt20++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end Exponent

    // $ANTLR start FloatSuffix
    public final void mFloatSuffix() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:503:5: ( 'f' | 'F' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:
            {
            if ( input.LA(1)=='F'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end FloatSuffix

    // $ANTLR start DoubleSuffix
    public final void mDoubleSuffix() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:508:5: ( 'd' | 'D' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:
            {
            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end DoubleSuffix

    // $ANTLR start FLOATLITERAL
    public final void mFLOATLITERAL() throws RecognitionException {
        try {
            int _type = FLOATLITERAL;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:512:5: ( NonIntegerNumber FloatSuffix )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:512:9: NonIntegerNumber FloatSuffix
            {
            mNonIntegerNumber(); 
            mFloatSuffix(); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FLOATLITERAL

    // $ANTLR start DOUBLELITERAL
    public final void mDOUBLELITERAL() throws RecognitionException {
        try {
            int _type = DOUBLELITERAL;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:516:5: ( NonIntegerNumber ( DoubleSuffix )? )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:516:9: NonIntegerNumber ( DoubleSuffix )?
            {
            mNonIntegerNumber(); 
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:516:26: ( DoubleSuffix )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0=='D'||LA21_0=='d') ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:516:26: DoubleSuffix
                    {
                    mDoubleSuffix(); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOUBLELITERAL

    // $ANTLR start CHARLITERAL
    public final void mCHARLITERAL() throws RecognitionException {
        try {
            int _type = CHARLITERAL;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:520:5: ( '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) ) '\\'' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:520:9: '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) ) '\\''
            {
            match('\''); 
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:521:9: ( EscapeSequence | ~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0=='\\') ) {
                alt22=1;
            }
            else if ( ((LA22_0>='\u0000' && LA22_0<='\t')||(LA22_0>='\u000B' && LA22_0<='\f')||(LA22_0>='\u000E' && LA22_0<='&')||(LA22_0>='(' && LA22_0<='[')||(LA22_0>=']' && LA22_0<='\uFFFE')) ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("521:9: ( EscapeSequence | ~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) )", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:521:13: EscapeSequence
                    {
                    mEscapeSequence(); 

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:522:13: ~ ( '\\'' | '\\\\' | '\\r' | '\\n' )
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;

            }

            match('\''); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CHARLITERAL

    // $ANTLR start STRINGLITERAL
    public final void mSTRINGLITERAL() throws RecognitionException {
        try {
            int _type = STRINGLITERAL;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:528:5: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' | '\\r' | '\\n' ) )* '\"' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:528:9: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' | '\\r' | '\\n' ) )* '\"'
            {
            match('\"'); 
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:529:9: ( EscapeSequence | ~ ( '\\\\' | '\"' | '\\r' | '\\n' ) )*
            loop23:
            do {
                int alt23=3;
                int LA23_0 = input.LA(1);

                if ( (LA23_0=='\\') ) {
                    alt23=1;
                }
                else if ( ((LA23_0>='\u0000' && LA23_0<='\t')||(LA23_0>='\u000B' && LA23_0<='\f')||(LA23_0>='\u000E' && LA23_0<='!')||(LA23_0>='#' && LA23_0<='[')||(LA23_0>=']' && LA23_0<='\uFFFE')) ) {
                    alt23=2;
                }


                switch (alt23) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:529:13: EscapeSequence
            	    {
            	    mEscapeSequence(); 

            	    }
            	    break;
            	case 2 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:530:13: ~ ( '\\\\' | '\"' | '\\r' | '\\n' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);

            match('\"'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STRINGLITERAL

    // $ANTLR start EscapeSequence
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:537:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) ) )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:537:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) )
            {
            match('\\'); 
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:537:14: ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) )
            int alt24=11;
            switch ( input.LA(1) ) {
            case 'b':
                {
                alt24=1;
                }
                break;
            case 't':
                {
                alt24=2;
                }
                break;
            case 'n':
                {
                alt24=3;
                }
                break;
            case 'f':
                {
                alt24=4;
                }
                break;
            case 'r':
                {
                alt24=5;
                }
                break;
            case '\"':
                {
                alt24=6;
                }
                break;
            case '\'':
                {
                alt24=7;
                }
                break;
            case '\\':
                {
                alt24=8;
                }
                break;
            case '0':
            case '1':
            case '2':
            case '3':
                {
                int LA24_9 = input.LA(2);

                if ( ((LA24_9>='0' && LA24_9<='7')) ) {
                    int LA24_12 = input.LA(3);

                    if ( ((LA24_12>='0' && LA24_12<='7')) ) {
                        alt24=9;
                    }
                    else {
                        alt24=10;}
                }
                else {
                    alt24=11;}
                }
                break;
            case '4':
            case '5':
            case '6':
            case '7':
                {
                int LA24_10 = input.LA(2);

                if ( ((LA24_10>='0' && LA24_10<='7')) ) {
                    alt24=10;
                }
                else {
                    alt24=11;}
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("537:14: ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) ( '0' .. '7' ) | ( '0' .. '7' ) )", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:538:18: 'b'
                    {
                    match('b'); 

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:539:18: 't'
                    {
                    match('t'); 

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:540:18: 'n'
                    {
                    match('n'); 

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:541:18: 'f'
                    {
                    match('f'); 

                    }
                    break;
                case 5 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:542:18: 'r'
                    {
                    match('r'); 

                    }
                    break;
                case 6 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:543:18: '\\\"'
                    {
                    match('\"'); 

                    }
                    break;
                case 7 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:544:18: '\\''
                    {
                    match('\''); 

                    }
                    break;
                case 8 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:545:18: '\\\\'
                    {
                    match('\\'); 

                    }
                    break;
                case 9 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:547:18: ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:547:18: ( '0' .. '3' )
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:547:19: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:547:29: ( '0' .. '7' )
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:547:30: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:547:40: ( '0' .. '7' )
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:547:41: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 10 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:549:18: ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:549:18: ( '0' .. '7' )
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:549:19: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:549:29: ( '0' .. '7' )
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:549:30: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 11 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:551:18: ( '0' .. '7' )
                    {
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:551:18: ( '0' .. '7' )
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:551:19: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end EscapeSequence

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:556:5: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:556:9: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            
                            skip();
                        

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WS

    // $ANTLR start COMMENT
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            
                        boolean isJavaDoc = false;
                    
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:572:5: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:572:9: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            
                            if((char)input.LA(1) == '*'){
                                isJavaDoc = true;
                            }
                        
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:578:9: ( options {greedy=false; } : . )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0=='*') ) {
                    int LA25_1 = input.LA(2);

                    if ( (LA25_1=='/') ) {
                        alt25=2;
                    }
                    else if ( ((LA25_1>='\u0000' && LA25_1<='.')||(LA25_1>='0' && LA25_1<='\uFFFE')) ) {
                        alt25=1;
                    }


                }
                else if ( ((LA25_0>='\u0000' && LA25_0<=')')||(LA25_0>='+' && LA25_0<='\uFFFE')) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:578:36: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);

            match("*/"); 

            
                            if(isJavaDoc==true){
                                channel=HIDDEN;
                            }else{
                                skip();
                            }
                        

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMENT

    // $ANTLR start LINE_COMMENT
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:590:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r\\n' | '\\r' | '\\n' ) | '//' (~ ( '\\n' | '\\r' ) )* )
            int alt29=2;
            alt29 = dfa29.predict(input);
            switch (alt29) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:590:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r\\n' | '\\r' | '\\n' )
                    {
                    match("//"); 

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:590:14: (~ ( '\\n' | '\\r' ) )*
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( ((LA26_0>='\u0000' && LA26_0<='\t')||(LA26_0>='\u000B' && LA26_0<='\f')||(LA26_0>='\u000E' && LA26_0<='\uFFFE')) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:590:14: ~ ( '\\n' | '\\r' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop26;
                        }
                    } while (true);

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:590:29: ( '\\r\\n' | '\\r' | '\\n' )
                    int alt27=3;
                    int LA27_0 = input.LA(1);

                    if ( (LA27_0=='\r') ) {
                        int LA27_1 = input.LA(2);

                        if ( (LA27_1=='\n') ) {
                            alt27=1;
                        }
                        else {
                            alt27=2;}
                    }
                    else if ( (LA27_0=='\n') ) {
                        alt27=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("590:29: ( '\\r\\n' | '\\r' | '\\n' )", 27, 0, input);

                        throw nvae;
                    }
                    switch (alt27) {
                        case 1 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:590:30: '\\r\\n'
                            {
                            match("\r\n"); 


                            }
                            break;
                        case 2 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:590:39: '\\r'
                            {
                            match('\r'); 

                            }
                            break;
                        case 3 :
                            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:590:46: '\\n'
                            {
                            match('\n'); 

                            }
                            break;

                    }

                    
                                    skip();
                                

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:594:9: '//' (~ ( '\\n' | '\\r' ) )*
                    {
                    match("//"); 

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:594:14: (~ ( '\\n' | '\\r' ) )*
                    loop28:
                    do {
                        int alt28=2;
                        int LA28_0 = input.LA(1);

                        if ( ((LA28_0>='\u0000' && LA28_0<='\t')||(LA28_0>='\u000B' && LA28_0<='\f')||(LA28_0>='\u000E' && LA28_0<='\uFFFE')) ) {
                            alt28=1;
                        }


                        switch (alt28) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:594:14: ~ ( '\\n' | '\\r' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop28;
                        }
                    } while (true);

                    
                                    skip();
                                

                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LINE_COMMENT

    // $ANTLR start ABSTRACT
    public final void mABSTRACT() throws RecognitionException {
        try {
            int _type = ABSTRACT;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:601:5: ( 'abstract' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:601:9: 'abstract'
            {
            match("abstract"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ABSTRACT

    // $ANTLR start ASSERT
    public final void mASSERT() throws RecognitionException {
        try {
            int _type = ASSERT;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:605:5: ( 'assert' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:605:9: 'assert'
            {
            match("assert"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ASSERT

    // $ANTLR start BOOLEAN
    public final void mBOOLEAN() throws RecognitionException {
        try {
            int _type = BOOLEAN;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:609:5: ( 'boolean' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:609:9: 'boolean'
            {
            match("boolean"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BOOLEAN

    // $ANTLR start BREAK
    public final void mBREAK() throws RecognitionException {
        try {
            int _type = BREAK;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:613:5: ( 'break' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:613:9: 'break'
            {
            match("break"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BREAK

    // $ANTLR start BYTE
    public final void mBYTE() throws RecognitionException {
        try {
            int _type = BYTE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:617:5: ( 'byte' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:617:9: 'byte'
            {
            match("byte"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BYTE

    // $ANTLR start CASE
    public final void mCASE() throws RecognitionException {
        try {
            int _type = CASE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:621:5: ( 'case' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:621:9: 'case'
            {
            match("case"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CASE

    // $ANTLR start CATCH
    public final void mCATCH() throws RecognitionException {
        try {
            int _type = CATCH;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:625:5: ( 'catch' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:625:9: 'catch'
            {
            match("catch"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CATCH

    // $ANTLR start CHAR
    public final void mCHAR() throws RecognitionException {
        try {
            int _type = CHAR;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:629:5: ( 'char' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:629:9: 'char'
            {
            match("char"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CHAR

    // $ANTLR start CLASS
    public final void mCLASS() throws RecognitionException {
        try {
            int _type = CLASS;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:633:5: ( 'class' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:633:9: 'class'
            {
            match("class"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CLASS

    // $ANTLR start CONST
    public final void mCONST() throws RecognitionException {
        try {
            int _type = CONST;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:637:5: ( 'const' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:637:9: 'const'
            {
            match("const"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CONST

    // $ANTLR start CONTINUE
    public final void mCONTINUE() throws RecognitionException {
        try {
            int _type = CONTINUE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:641:5: ( 'continue' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:641:9: 'continue'
            {
            match("continue"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CONTINUE

    // $ANTLR start DEFAULT
    public final void mDEFAULT() throws RecognitionException {
        try {
            int _type = DEFAULT;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:645:5: ( 'default' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:645:9: 'default'
            {
            match("default"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DEFAULT

    // $ANTLR start DO
    public final void mDO() throws RecognitionException {
        try {
            int _type = DO;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:649:5: ( 'do' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:649:9: 'do'
            {
            match("do"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DO

    // $ANTLR start DOUBLE
    public final void mDOUBLE() throws RecognitionException {
        try {
            int _type = DOUBLE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:653:5: ( 'double' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:653:9: 'double'
            {
            match("double"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOUBLE

    // $ANTLR start ELSE
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:657:5: ( 'else' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:657:9: 'else'
            {
            match("else"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ELSE

    // $ANTLR start ENUM
    public final void mENUM() throws RecognitionException {
        try {
            int _type = ENUM;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:661:5: ( 'enum' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:661:9: 'enum'
            {
            match("enum"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ENUM

    // $ANTLR start EXTENDS
    public final void mEXTENDS() throws RecognitionException {
        try {
            int _type = EXTENDS;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:665:5: ( 'extends' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:665:9: 'extends'
            {
            match("extends"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EXTENDS

    // $ANTLR start FINAL
    public final void mFINAL() throws RecognitionException {
        try {
            int _type = FINAL;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:669:5: ( 'final' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:669:9: 'final'
            {
            match("final"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FINAL

    // $ANTLR start FINALLY
    public final void mFINALLY() throws RecognitionException {
        try {
            int _type = FINALLY;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:673:5: ( 'finally' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:673:9: 'finally'
            {
            match("finally"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FINALLY

    // $ANTLR start FLOAT
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:677:5: ( 'float' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:677:9: 'float'
            {
            match("float"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FLOAT

    // $ANTLR start FOR
    public final void mFOR() throws RecognitionException {
        try {
            int _type = FOR;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:681:5: ( 'for' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:681:9: 'for'
            {
            match("for"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FOR

    // $ANTLR start GOTO
    public final void mGOTO() throws RecognitionException {
        try {
            int _type = GOTO;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:685:5: ( 'goto' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:685:9: 'goto'
            {
            match("goto"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GOTO

    // $ANTLR start IF
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:689:5: ( 'if' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:689:9: 'if'
            {
            match("if"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IF

    // $ANTLR start IMPLEMENTS
    public final void mIMPLEMENTS() throws RecognitionException {
        try {
            int _type = IMPLEMENTS;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:693:5: ( 'implements' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:693:9: 'implements'
            {
            match("implements"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IMPLEMENTS

    // $ANTLR start IMPORT
    public final void mIMPORT() throws RecognitionException {
        try {
            int _type = IMPORT;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:697:5: ( 'import' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:697:9: 'import'
            {
            match("import"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IMPORT

    // $ANTLR start INSTANCEOF
    public final void mINSTANCEOF() throws RecognitionException {
        try {
            int _type = INSTANCEOF;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:701:5: ( 'instanceof' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:701:9: 'instanceof'
            {
            match("instanceof"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INSTANCEOF

    // $ANTLR start INT
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:705:5: ( 'int' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:705:9: 'int'
            {
            match("int"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INT

    // $ANTLR start INTERFACE
    public final void mINTERFACE() throws RecognitionException {
        try {
            int _type = INTERFACE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:709:5: ( 'interface' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:709:9: 'interface'
            {
            match("interface"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INTERFACE

    // $ANTLR start LONG
    public final void mLONG() throws RecognitionException {
        try {
            int _type = LONG;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:713:5: ( 'long' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:713:9: 'long'
            {
            match("long"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LONG

    // $ANTLR start NATIVE
    public final void mNATIVE() throws RecognitionException {
        try {
            int _type = NATIVE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:717:5: ( 'native' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:717:9: 'native'
            {
            match("native"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NATIVE

    // $ANTLR start NEW
    public final void mNEW() throws RecognitionException {
        try {
            int _type = NEW;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:721:5: ( 'new' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:721:9: 'new'
            {
            match("new"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NEW

    // $ANTLR start PACKAGE
    public final void mPACKAGE() throws RecognitionException {
        try {
            int _type = PACKAGE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:725:5: ( 'package' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:725:9: 'package'
            {
            match("package"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PACKAGE

    // $ANTLR start PRIVATE
    public final void mPRIVATE() throws RecognitionException {
        try {
            int _type = PRIVATE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:729:5: ( 'private' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:729:9: 'private'
            {
            match("private"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PRIVATE

    // $ANTLR start PROTECTED
    public final void mPROTECTED() throws RecognitionException {
        try {
            int _type = PROTECTED;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:733:5: ( 'protected' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:733:9: 'protected'
            {
            match("protected"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PROTECTED

    // $ANTLR start PUBLIC
    public final void mPUBLIC() throws RecognitionException {
        try {
            int _type = PUBLIC;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:737:5: ( 'public' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:737:9: 'public'
            {
            match("public"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PUBLIC

    // $ANTLR start RETURN
    public final void mRETURN() throws RecognitionException {
        try {
            int _type = RETURN;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:741:5: ( 'return' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:741:9: 'return'
            {
            match("return"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RETURN

    // $ANTLR start SHORT
    public final void mSHORT() throws RecognitionException {
        try {
            int _type = SHORT;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:745:5: ( 'short' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:745:9: 'short'
            {
            match("short"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SHORT

    // $ANTLR start STATIC
    public final void mSTATIC() throws RecognitionException {
        try {
            int _type = STATIC;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:749:5: ( 'static' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:749:9: 'static'
            {
            match("static"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STATIC

    // $ANTLR start STRICTFP
    public final void mSTRICTFP() throws RecognitionException {
        try {
            int _type = STRICTFP;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:753:5: ( 'strictfp' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:753:9: 'strictfp'
            {
            match("strictfp"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STRICTFP

    // $ANTLR start SUPER
    public final void mSUPER() throws RecognitionException {
        try {
            int _type = SUPER;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:757:5: ( 'super' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:757:9: 'super'
            {
            match("super"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SUPER

    // $ANTLR start SWITCH
    public final void mSWITCH() throws RecognitionException {
        try {
            int _type = SWITCH;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:761:5: ( 'switch' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:761:9: 'switch'
            {
            match("switch"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SWITCH

    // $ANTLR start SYNCHRONIZED
    public final void mSYNCHRONIZED() throws RecognitionException {
        try {
            int _type = SYNCHRONIZED;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:765:5: ( 'synchronized' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:765:9: 'synchronized'
            {
            match("synchronized"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SYNCHRONIZED

    // $ANTLR start THIS
    public final void mTHIS() throws RecognitionException {
        try {
            int _type = THIS;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:769:5: ( 'this' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:769:9: 'this'
            {
            match("this"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end THIS

    // $ANTLR start THROW
    public final void mTHROW() throws RecognitionException {
        try {
            int _type = THROW;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:773:5: ( 'throw' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:773:9: 'throw'
            {
            match("throw"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end THROW

    // $ANTLR start THROWS
    public final void mTHROWS() throws RecognitionException {
        try {
            int _type = THROWS;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:777:5: ( 'throws' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:777:9: 'throws'
            {
            match("throws"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end THROWS

    // $ANTLR start TRANSIENT
    public final void mTRANSIENT() throws RecognitionException {
        try {
            int _type = TRANSIENT;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:781:5: ( 'transient' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:781:9: 'transient'
            {
            match("transient"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TRANSIENT

    // $ANTLR start TRY
    public final void mTRY() throws RecognitionException {
        try {
            int _type = TRY;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:785:5: ( 'try' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:785:9: 'try'
            {
            match("try"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TRY

    // $ANTLR start VOID
    public final void mVOID() throws RecognitionException {
        try {
            int _type = VOID;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:789:5: ( 'void' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:789:9: 'void'
            {
            match("void"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end VOID

    // $ANTLR start VOLATILE
    public final void mVOLATILE() throws RecognitionException {
        try {
            int _type = VOLATILE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:793:5: ( 'volatile' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:793:9: 'volatile'
            {
            match("volatile"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end VOLATILE

    // $ANTLR start WHILE
    public final void mWHILE() throws RecognitionException {
        try {
            int _type = WHILE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:797:5: ( 'while' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:797:9: 'while'
            {
            match("while"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WHILE

    // $ANTLR start TRUE
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:801:5: ( 'true' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:801:9: 'true'
            {
            match("true"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TRUE

    // $ANTLR start FALSE
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:805:5: ( 'false' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:805:9: 'false'
            {
            match("false"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FALSE

    // $ANTLR start NULL
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:809:5: ( 'null' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:809:9: 'null'
            {
            match("null"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NULL

    // $ANTLR start LPAREN
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:813:5: ( '(' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:813:9: '('
            {
            match('('); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LPAREN

    // $ANTLR start RPAREN
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:817:5: ( ')' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:817:9: ')'
            {
            match(')'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RPAREN

    // $ANTLR start LBRACE
    public final void mLBRACE() throws RecognitionException {
        try {
            int _type = LBRACE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:821:5: ( '{' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:821:9: '{'
            {
            match('{'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LBRACE

    // $ANTLR start RBRACE
    public final void mRBRACE() throws RecognitionException {
        try {
            int _type = RBRACE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:825:5: ( '}' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:825:9: '}'
            {
            match('}'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RBRACE

    // $ANTLR start LBRACKET
    public final void mLBRACKET() throws RecognitionException {
        try {
            int _type = LBRACKET;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:829:5: ( '[' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:829:9: '['
            {
            match('['); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LBRACKET

    // $ANTLR start RBRACKET
    public final void mRBRACKET() throws RecognitionException {
        try {
            int _type = RBRACKET;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:833:5: ( ']' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:833:9: ']'
            {
            match(']'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RBRACKET

    // $ANTLR start SEMI
    public final void mSEMI() throws RecognitionException {
        try {
            int _type = SEMI;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:837:5: ( ';' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:837:9: ';'
            {
            match(';'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SEMI

    // $ANTLR start COMMA
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:841:5: ( ',' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:841:9: ','
            {
            match(','); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMA

    // $ANTLR start DOT
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:845:5: ( '.' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:845:9: '.'
            {
            match('.'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOT

    // $ANTLR start ELLIPSIS
    public final void mELLIPSIS() throws RecognitionException {
        try {
            int _type = ELLIPSIS;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:849:5: ( '...' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:849:9: '...'
            {
            match("..."); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ELLIPSIS

    // $ANTLR start EQ
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:853:5: ( '=' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:853:9: '='
            {
            match('='); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQ

    // $ANTLR start BANG
    public final void mBANG() throws RecognitionException {
        try {
            int _type = BANG;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:857:5: ( '!' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:857:9: '!'
            {
            match('!'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BANG

    // $ANTLR start TILDE
    public final void mTILDE() throws RecognitionException {
        try {
            int _type = TILDE;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:861:5: ( '~' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:861:9: '~'
            {
            match('~'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TILDE

    // $ANTLR start QUES
    public final void mQUES() throws RecognitionException {
        try {
            int _type = QUES;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:865:5: ( '?' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:865:9: '?'
            {
            match('?'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end QUES

    // $ANTLR start COLON
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:869:5: ( ':' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:869:9: ':'
            {
            match(':'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COLON

    // $ANTLR start EQEQ
    public final void mEQEQ() throws RecognitionException {
        try {
            int _type = EQEQ;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:873:5: ( '==' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:873:9: '=='
            {
            match("=="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQEQ

    // $ANTLR start AMPAMP
    public final void mAMPAMP() throws RecognitionException {
        try {
            int _type = AMPAMP;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:877:5: ( '&&' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:877:9: '&&'
            {
            match("&&"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AMPAMP

    // $ANTLR start BARBAR
    public final void mBARBAR() throws RecognitionException {
        try {
            int _type = BARBAR;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:881:5: ( '||' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:881:9: '||'
            {
            match("||"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BARBAR

    // $ANTLR start PLUSPLUS
    public final void mPLUSPLUS() throws RecognitionException {
        try {
            int _type = PLUSPLUS;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:885:5: ( '++' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:885:9: '++'
            {
            match("++"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PLUSPLUS

    // $ANTLR start SUBSUB
    public final void mSUBSUB() throws RecognitionException {
        try {
            int _type = SUBSUB;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:889:5: ( '--' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:889:9: '--'
            {
            match("--"); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SUBSUB

    // $ANTLR start PLUS
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:893:5: ( '+' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:893:9: '+'
            {
            match('+'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PLUS

    // $ANTLR start SUB
    public final void mSUB() throws RecognitionException {
        try {
            int _type = SUB;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:897:5: ( '-' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:897:9: '-'
            {
            match('-'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SUB

    // $ANTLR start STAR
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:901:5: ( '*' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:901:9: '*'
            {
            match('*'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STAR

    // $ANTLR start SLASH
    public final void mSLASH() throws RecognitionException {
        try {
            int _type = SLASH;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:905:5: ( '/' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:905:9: '/'
            {
            match('/'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SLASH

    // $ANTLR start AMP
    public final void mAMP() throws RecognitionException {
        try {
            int _type = AMP;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:909:5: ( '&' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:909:9: '&'
            {
            match('&'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AMP

    // $ANTLR start BAR
    public final void mBAR() throws RecognitionException {
        try {
            int _type = BAR;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:913:5: ( '|' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:913:9: '|'
            {
            match('|'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BAR

    // $ANTLR start CARET
    public final void mCARET() throws RecognitionException {
        try {
            int _type = CARET;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:917:5: ( '^' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:917:9: '^'
            {
            match('^'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CARET

    // $ANTLR start PERCENT
    public final void mPERCENT() throws RecognitionException {
        try {
            int _type = PERCENT;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:921:5: ( '%' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:921:9: '%'
            {
            match('%'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PERCENT

    // $ANTLR start PLUSEQ
    public final void mPLUSEQ() throws RecognitionException {
        try {
            int _type = PLUSEQ;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:925:5: ( '+=' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:925:9: '+='
            {
            match("+="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PLUSEQ

    // $ANTLR start SUBEQ
    public final void mSUBEQ() throws RecognitionException {
        try {
            int _type = SUBEQ;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:929:5: ( '-=' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:929:9: '-='
            {
            match("-="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SUBEQ

    // $ANTLR start STAREQ
    public final void mSTAREQ() throws RecognitionException {
        try {
            int _type = STAREQ;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:933:5: ( '*=' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:933:9: '*='
            {
            match("*="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STAREQ

    // $ANTLR start SLASHEQ
    public final void mSLASHEQ() throws RecognitionException {
        try {
            int _type = SLASHEQ;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:937:5: ( '/=' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:937:9: '/='
            {
            match("/="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SLASHEQ

    // $ANTLR start AMPEQ
    public final void mAMPEQ() throws RecognitionException {
        try {
            int _type = AMPEQ;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:941:5: ( '&=' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:941:9: '&='
            {
            match("&="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AMPEQ

    // $ANTLR start BAREQ
    public final void mBAREQ() throws RecognitionException {
        try {
            int _type = BAREQ;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:945:5: ( '|=' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:945:9: '|='
            {
            match("|="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BAREQ

    // $ANTLR start CARETEQ
    public final void mCARETEQ() throws RecognitionException {
        try {
            int _type = CARETEQ;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:949:5: ( '^=' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:949:9: '^='
            {
            match("^="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CARETEQ

    // $ANTLR start PERCENTEQ
    public final void mPERCENTEQ() throws RecognitionException {
        try {
            int _type = PERCENTEQ;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:953:5: ( '%=' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:953:9: '%='
            {
            match("%="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PERCENTEQ

    // $ANTLR start MONKEYS_AT
    public final void mMONKEYS_AT() throws RecognitionException {
        try {
            int _type = MONKEYS_AT;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:957:5: ( '@' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:957:9: '@'
            {
            match('@'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MONKEYS_AT

    // $ANTLR start BANGEQ
    public final void mBANGEQ() throws RecognitionException {
        try {
            int _type = BANGEQ;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:961:5: ( '!=' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:961:9: '!='
            {
            match("!="); 


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BANGEQ

    // $ANTLR start GT
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:965:5: ( '>' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:965:9: '>'
            {
            match('>'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GT

    // $ANTLR start LT
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:969:5: ( '<' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:969:9: '<'
            {
            match('<'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LT

    // $ANTLR start IDENTIFIER
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:973:5: ( IdentifierStart ( IdentifierPart )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:973:9: IdentifierStart ( IdentifierPart )*
            {
            mIdentifierStart(); 
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:973:25: ( IdentifierPart )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( ((LA30_0>='\u0000' && LA30_0<='\b')||(LA30_0>='\u000E' && LA30_0<='\u001B')||LA30_0=='$'||(LA30_0>='0' && LA30_0<='9')||(LA30_0>='A' && LA30_0<='Z')||LA30_0=='_'||(LA30_0>='a' && LA30_0<='z')||(LA30_0>='\u007F' && LA30_0<='\u009F')||(LA30_0>='\u00A2' && LA30_0<='\u00A5')||LA30_0=='\u00AA'||LA30_0=='\u00AD'||LA30_0=='\u00B5'||LA30_0=='\u00BA'||(LA30_0>='\u00C0' && LA30_0<='\u00D6')||(LA30_0>='\u00D8' && LA30_0<='\u00F6')||(LA30_0>='\u00F8' && LA30_0<='\u0236')||(LA30_0>='\u0250' && LA30_0<='\u02C1')||(LA30_0>='\u02C6' && LA30_0<='\u02D1')||(LA30_0>='\u02E0' && LA30_0<='\u02E4')||LA30_0=='\u02EE'||(LA30_0>='\u0300' && LA30_0<='\u0357')||(LA30_0>='\u035D' && LA30_0<='\u036F')||LA30_0=='\u037A'||LA30_0=='\u0386'||(LA30_0>='\u0388' && LA30_0<='\u038A')||LA30_0=='\u038C'||(LA30_0>='\u038E' && LA30_0<='\u03A1')||(LA30_0>='\u03A3' && LA30_0<='\u03CE')||(LA30_0>='\u03D0' && LA30_0<='\u03F5')||(LA30_0>='\u03F7' && LA30_0<='\u03FB')||(LA30_0>='\u0400' && LA30_0<='\u0481')||(LA30_0>='\u0483' && LA30_0<='\u0486')||(LA30_0>='\u048A' && LA30_0<='\u04CE')||(LA30_0>='\u04D0' && LA30_0<='\u04F5')||(LA30_0>='\u04F8' && LA30_0<='\u04F9')||(LA30_0>='\u0500' && LA30_0<='\u050F')||(LA30_0>='\u0531' && LA30_0<='\u0556')||LA30_0=='\u0559'||(LA30_0>='\u0561' && LA30_0<='\u0587')||(LA30_0>='\u0591' && LA30_0<='\u05A1')||(LA30_0>='\u05A3' && LA30_0<='\u05B9')||(LA30_0>='\u05BB' && LA30_0<='\u05BD')||LA30_0=='\u05BF'||(LA30_0>='\u05C1' && LA30_0<='\u05C2')||LA30_0=='\u05C4'||(LA30_0>='\u05D0' && LA30_0<='\u05EA')||(LA30_0>='\u05F0' && LA30_0<='\u05F2')||(LA30_0>='\u0600' && LA30_0<='\u0603')||(LA30_0>='\u0610' && LA30_0<='\u0615')||(LA30_0>='\u0621' && LA30_0<='\u063A')||(LA30_0>='\u0640' && LA30_0<='\u0658')||(LA30_0>='\u0660' && LA30_0<='\u0669')||(LA30_0>='\u066E' && LA30_0<='\u06D3')||(LA30_0>='\u06D5' && LA30_0<='\u06DD')||(LA30_0>='\u06DF' && LA30_0<='\u06E8')||(LA30_0>='\u06EA' && LA30_0<='\u06FC')||LA30_0=='\u06FF'||(LA30_0>='\u070F' && LA30_0<='\u074A')||(LA30_0>='\u074D' && LA30_0<='\u074F')||(LA30_0>='\u0780' && LA30_0<='\u07B1')||(LA30_0>='\u0901' && LA30_0<='\u0939')||(LA30_0>='\u093C' && LA30_0<='\u094D')||(LA30_0>='\u0950' && LA30_0<='\u0954')||(LA30_0>='\u0958' && LA30_0<='\u0963')||(LA30_0>='\u0966' && LA30_0<='\u096F')||(LA30_0>='\u0981' && LA30_0<='\u0983')||(LA30_0>='\u0985' && LA30_0<='\u098C')||(LA30_0>='\u098F' && LA30_0<='\u0990')||(LA30_0>='\u0993' && LA30_0<='\u09A8')||(LA30_0>='\u09AA' && LA30_0<='\u09B0')||LA30_0=='\u09B2'||(LA30_0>='\u09B6' && LA30_0<='\u09B9')||(LA30_0>='\u09BC' && LA30_0<='\u09C4')||(LA30_0>='\u09C7' && LA30_0<='\u09C8')||(LA30_0>='\u09CB' && LA30_0<='\u09CD')||LA30_0=='\u09D7'||(LA30_0>='\u09DC' && LA30_0<='\u09DD')||(LA30_0>='\u09DF' && LA30_0<='\u09E3')||(LA30_0>='\u09E6' && LA30_0<='\u09F3')||(LA30_0>='\u0A01' && LA30_0<='\u0A03')||(LA30_0>='\u0A05' && LA30_0<='\u0A0A')||(LA30_0>='\u0A0F' && LA30_0<='\u0A10')||(LA30_0>='\u0A13' && LA30_0<='\u0A28')||(LA30_0>='\u0A2A' && LA30_0<='\u0A30')||(LA30_0>='\u0A32' && LA30_0<='\u0A33')||(LA30_0>='\u0A35' && LA30_0<='\u0A36')||(LA30_0>='\u0A38' && LA30_0<='\u0A39')||LA30_0=='\u0A3C'||(LA30_0>='\u0A3E' && LA30_0<='\u0A42')||(LA30_0>='\u0A47' && LA30_0<='\u0A48')||(LA30_0>='\u0A4B' && LA30_0<='\u0A4D')||(LA30_0>='\u0A59' && LA30_0<='\u0A5C')||LA30_0=='\u0A5E'||(LA30_0>='\u0A66' && LA30_0<='\u0A74')||(LA30_0>='\u0A81' && LA30_0<='\u0A83')||(LA30_0>='\u0A85' && LA30_0<='\u0A8D')||(LA30_0>='\u0A8F' && LA30_0<='\u0A91')||(LA30_0>='\u0A93' && LA30_0<='\u0AA8')||(LA30_0>='\u0AAA' && LA30_0<='\u0AB0')||(LA30_0>='\u0AB2' && LA30_0<='\u0AB3')||(LA30_0>='\u0AB5' && LA30_0<='\u0AB9')||(LA30_0>='\u0ABC' && LA30_0<='\u0AC5')||(LA30_0>='\u0AC7' && LA30_0<='\u0AC9')||(LA30_0>='\u0ACB' && LA30_0<='\u0ACD')||LA30_0=='\u0AD0'||(LA30_0>='\u0AE0' && LA30_0<='\u0AE3')||(LA30_0>='\u0AE6' && LA30_0<='\u0AEF')||LA30_0=='\u0AF1'||(LA30_0>='\u0B01' && LA30_0<='\u0B03')||(LA30_0>='\u0B05' && LA30_0<='\u0B0C')||(LA30_0>='\u0B0F' && LA30_0<='\u0B10')||(LA30_0>='\u0B13' && LA30_0<='\u0B28')||(LA30_0>='\u0B2A' && LA30_0<='\u0B30')||(LA30_0>='\u0B32' && LA30_0<='\u0B33')||(LA30_0>='\u0B35' && LA30_0<='\u0B39')||(LA30_0>='\u0B3C' && LA30_0<='\u0B43')||(LA30_0>='\u0B47' && LA30_0<='\u0B48')||(LA30_0>='\u0B4B' && LA30_0<='\u0B4D')||(LA30_0>='\u0B56' && LA30_0<='\u0B57')||(LA30_0>='\u0B5C' && LA30_0<='\u0B5D')||(LA30_0>='\u0B5F' && LA30_0<='\u0B61')||(LA30_0>='\u0B66' && LA30_0<='\u0B6F')||LA30_0=='\u0B71'||(LA30_0>='\u0B82' && LA30_0<='\u0B83')||(LA30_0>='\u0B85' && LA30_0<='\u0B8A')||(LA30_0>='\u0B8E' && LA30_0<='\u0B90')||(LA30_0>='\u0B92' && LA30_0<='\u0B95')||(LA30_0>='\u0B99' && LA30_0<='\u0B9A')||LA30_0=='\u0B9C'||(LA30_0>='\u0B9E' && LA30_0<='\u0B9F')||(LA30_0>='\u0BA3' && LA30_0<='\u0BA4')||(LA30_0>='\u0BA8' && LA30_0<='\u0BAA')||(LA30_0>='\u0BAE' && LA30_0<='\u0BB5')||(LA30_0>='\u0BB7' && LA30_0<='\u0BB9')||(LA30_0>='\u0BBE' && LA30_0<='\u0BC2')||(LA30_0>='\u0BC6' && LA30_0<='\u0BC8')||(LA30_0>='\u0BCA' && LA30_0<='\u0BCD')||LA30_0=='\u0BD7'||(LA30_0>='\u0BE7' && LA30_0<='\u0BEF')||LA30_0=='\u0BF9'||(LA30_0>='\u0C01' && LA30_0<='\u0C03')||(LA30_0>='\u0C05' && LA30_0<='\u0C0C')||(LA30_0>='\u0C0E' && LA30_0<='\u0C10')||(LA30_0>='\u0C12' && LA30_0<='\u0C28')||(LA30_0>='\u0C2A' && LA30_0<='\u0C33')||(LA30_0>='\u0C35' && LA30_0<='\u0C39')||(LA30_0>='\u0C3E' && LA30_0<='\u0C44')||(LA30_0>='\u0C46' && LA30_0<='\u0C48')||(LA30_0>='\u0C4A' && LA30_0<='\u0C4D')||(LA30_0>='\u0C55' && LA30_0<='\u0C56')||(LA30_0>='\u0C60' && LA30_0<='\u0C61')||(LA30_0>='\u0C66' && LA30_0<='\u0C6F')||(LA30_0>='\u0C82' && LA30_0<='\u0C83')||(LA30_0>='\u0C85' && LA30_0<='\u0C8C')||(LA30_0>='\u0C8E' && LA30_0<='\u0C90')||(LA30_0>='\u0C92' && LA30_0<='\u0CA8')||(LA30_0>='\u0CAA' && LA30_0<='\u0CB3')||(LA30_0>='\u0CB5' && LA30_0<='\u0CB9')||(LA30_0>='\u0CBC' && LA30_0<='\u0CC4')||(LA30_0>='\u0CC6' && LA30_0<='\u0CC8')||(LA30_0>='\u0CCA' && LA30_0<='\u0CCD')||(LA30_0>='\u0CD5' && LA30_0<='\u0CD6')||LA30_0=='\u0CDE'||(LA30_0>='\u0CE0' && LA30_0<='\u0CE1')||(LA30_0>='\u0CE6' && LA30_0<='\u0CEF')||(LA30_0>='\u0D02' && LA30_0<='\u0D03')||(LA30_0>='\u0D05' && LA30_0<='\u0D0C')||(LA30_0>='\u0D0E' && LA30_0<='\u0D10')||(LA30_0>='\u0D12' && LA30_0<='\u0D28')||(LA30_0>='\u0D2A' && LA30_0<='\u0D39')||(LA30_0>='\u0D3E' && LA30_0<='\u0D43')||(LA30_0>='\u0D46' && LA30_0<='\u0D48')||(LA30_0>='\u0D4A' && LA30_0<='\u0D4D')||LA30_0=='\u0D57'||(LA30_0>='\u0D60' && LA30_0<='\u0D61')||(LA30_0>='\u0D66' && LA30_0<='\u0D6F')||(LA30_0>='\u0D82' && LA30_0<='\u0D83')||(LA30_0>='\u0D85' && LA30_0<='\u0D96')||(LA30_0>='\u0D9A' && LA30_0<='\u0DB1')||(LA30_0>='\u0DB3' && LA30_0<='\u0DBB')||LA30_0=='\u0DBD'||(LA30_0>='\u0DC0' && LA30_0<='\u0DC6')||LA30_0=='\u0DCA'||(LA30_0>='\u0DCF' && LA30_0<='\u0DD4')||LA30_0=='\u0DD6'||(LA30_0>='\u0DD8' && LA30_0<='\u0DDF')||(LA30_0>='\u0DF2' && LA30_0<='\u0DF3')||(LA30_0>='\u0E01' && LA30_0<='\u0E3A')||(LA30_0>='\u0E3F' && LA30_0<='\u0E4E')||(LA30_0>='\u0E50' && LA30_0<='\u0E59')||(LA30_0>='\u0E81' && LA30_0<='\u0E82')||LA30_0=='\u0E84'||(LA30_0>='\u0E87' && LA30_0<='\u0E88')||LA30_0=='\u0E8A'||LA30_0=='\u0E8D'||(LA30_0>='\u0E94' && LA30_0<='\u0E97')||(LA30_0>='\u0E99' && LA30_0<='\u0E9F')||(LA30_0>='\u0EA1' && LA30_0<='\u0EA3')||LA30_0=='\u0EA5'||LA30_0=='\u0EA7'||(LA30_0>='\u0EAA' && LA30_0<='\u0EAB')||(LA30_0>='\u0EAD' && LA30_0<='\u0EB9')||(LA30_0>='\u0EBB' && LA30_0<='\u0EBD')||(LA30_0>='\u0EC0' && LA30_0<='\u0EC4')||LA30_0=='\u0EC6'||(LA30_0>='\u0EC8' && LA30_0<='\u0ECD')||(LA30_0>='\u0ED0' && LA30_0<='\u0ED9')||(LA30_0>='\u0EDC' && LA30_0<='\u0EDD')||LA30_0=='\u0F00'||(LA30_0>='\u0F18' && LA30_0<='\u0F19')||(LA30_0>='\u0F20' && LA30_0<='\u0F29')||LA30_0=='\u0F35'||LA30_0=='\u0F37'||LA30_0=='\u0F39'||(LA30_0>='\u0F3E' && LA30_0<='\u0F47')||(LA30_0>='\u0F49' && LA30_0<='\u0F6A')||(LA30_0>='\u0F71' && LA30_0<='\u0F84')||(LA30_0>='\u0F86' && LA30_0<='\u0F8B')||(LA30_0>='\u0F90' && LA30_0<='\u0F97')||(LA30_0>='\u0F99' && LA30_0<='\u0FBC')||LA30_0=='\u0FC6'||(LA30_0>='\u1000' && LA30_0<='\u1021')||(LA30_0>='\u1023' && LA30_0<='\u1027')||(LA30_0>='\u1029' && LA30_0<='\u102A')||(LA30_0>='\u102C' && LA30_0<='\u1032')||(LA30_0>='\u1036' && LA30_0<='\u1039')||(LA30_0>='\u1040' && LA30_0<='\u1049')||(LA30_0>='\u1050' && LA30_0<='\u1059')||(LA30_0>='\u10A0' && LA30_0<='\u10C5')||(LA30_0>='\u10D0' && LA30_0<='\u10F8')||(LA30_0>='\u1100' && LA30_0<='\u1159')||(LA30_0>='\u115F' && LA30_0<='\u11A2')||(LA30_0>='\u11A8' && LA30_0<='\u11F9')||(LA30_0>='\u1200' && LA30_0<='\u1206')||(LA30_0>='\u1208' && LA30_0<='\u1246')||LA30_0=='\u1248'||(LA30_0>='\u124A' && LA30_0<='\u124D')||(LA30_0>='\u1250' && LA30_0<='\u1256')||LA30_0=='\u1258'||(LA30_0>='\u125A' && LA30_0<='\u125D')||(LA30_0>='\u1260' && LA30_0<='\u1286')||LA30_0=='\u1288'||(LA30_0>='\u128A' && LA30_0<='\u128D')||(LA30_0>='\u1290' && LA30_0<='\u12AE')||LA30_0=='\u12B0'||(LA30_0>='\u12B2' && LA30_0<='\u12B5')||(LA30_0>='\u12B8' && LA30_0<='\u12BE')||LA30_0=='\u12C0'||(LA30_0>='\u12C2' && LA30_0<='\u12C5')||(LA30_0>='\u12C8' && LA30_0<='\u12CE')||(LA30_0>='\u12D0' && LA30_0<='\u12D6')||(LA30_0>='\u12D8' && LA30_0<='\u12EE')||(LA30_0>='\u12F0' && LA30_0<='\u130E')||LA30_0=='\u1310'||(LA30_0>='\u1312' && LA30_0<='\u1315')||(LA30_0>='\u1318' && LA30_0<='\u131E')||(LA30_0>='\u1320' && LA30_0<='\u1346')||(LA30_0>='\u1348' && LA30_0<='\u135A')||(LA30_0>='\u1369' && LA30_0<='\u1371')||(LA30_0>='\u13A0' && LA30_0<='\u13F4')||(LA30_0>='\u1401' && LA30_0<='\u166C')||(LA30_0>='\u166F' && LA30_0<='\u1676')||(LA30_0>='\u1681' && LA30_0<='\u169A')||(LA30_0>='\u16A0' && LA30_0<='\u16EA')||(LA30_0>='\u16EE' && LA30_0<='\u16F0')||(LA30_0>='\u1700' && LA30_0<='\u170C')||(LA30_0>='\u170E' && LA30_0<='\u1714')||(LA30_0>='\u1720' && LA30_0<='\u1734')||(LA30_0>='\u1740' && LA30_0<='\u1753')||(LA30_0>='\u1760' && LA30_0<='\u176C')||(LA30_0>='\u176E' && LA30_0<='\u1770')||(LA30_0>='\u1772' && LA30_0<='\u1773')||(LA30_0>='\u1780' && LA30_0<='\u17D3')||LA30_0=='\u17D7'||(LA30_0>='\u17DB' && LA30_0<='\u17DD')||(LA30_0>='\u17E0' && LA30_0<='\u17E9')||(LA30_0>='\u180B' && LA30_0<='\u180D')||(LA30_0>='\u1810' && LA30_0<='\u1819')||(LA30_0>='\u1820' && LA30_0<='\u1877')||(LA30_0>='\u1880' && LA30_0<='\u18A9')||(LA30_0>='\u1900' && LA30_0<='\u191C')||(LA30_0>='\u1920' && LA30_0<='\u192B')||(LA30_0>='\u1930' && LA30_0<='\u193B')||(LA30_0>='\u1946' && LA30_0<='\u196D')||(LA30_0>='\u1970' && LA30_0<='\u1974')||(LA30_0>='\u1D00' && LA30_0<='\u1D6B')||(LA30_0>='\u1E00' && LA30_0<='\u1E9B')||(LA30_0>='\u1EA0' && LA30_0<='\u1EF9')||(LA30_0>='\u1F00' && LA30_0<='\u1F15')||(LA30_0>='\u1F18' && LA30_0<='\u1F1D')||(LA30_0>='\u1F20' && LA30_0<='\u1F45')||(LA30_0>='\u1F48' && LA30_0<='\u1F4D')||(LA30_0>='\u1F50' && LA30_0<='\u1F57')||LA30_0=='\u1F59'||LA30_0=='\u1F5B'||LA30_0=='\u1F5D'||(LA30_0>='\u1F5F' && LA30_0<='\u1F7D')||(LA30_0>='\u1F80' && LA30_0<='\u1FB4')||(LA30_0>='\u1FB6' && LA30_0<='\u1FBC')||LA30_0=='\u1FBE'||(LA30_0>='\u1FC2' && LA30_0<='\u1FC4')||(LA30_0>='\u1FC6' && LA30_0<='\u1FCC')||(LA30_0>='\u1FD0' && LA30_0<='\u1FD3')||(LA30_0>='\u1FD6' && LA30_0<='\u1FDB')||(LA30_0>='\u1FE0' && LA30_0<='\u1FEC')||(LA30_0>='\u1FF2' && LA30_0<='\u1FF4')||(LA30_0>='\u1FF6' && LA30_0<='\u1FFC')||(LA30_0>='\u200C' && LA30_0<='\u200F')||(LA30_0>='\u202A' && LA30_0<='\u202E')||(LA30_0>='\u203F' && LA30_0<='\u2040')||LA30_0=='\u2054'||(LA30_0>='\u2060' && LA30_0<='\u2063')||(LA30_0>='\u206A' && LA30_0<='\u206F')||LA30_0=='\u2071'||LA30_0=='\u207F'||(LA30_0>='\u20A0' && LA30_0<='\u20B1')||(LA30_0>='\u20D0' && LA30_0<='\u20DC')||LA30_0=='\u20E1'||(LA30_0>='\u20E5' && LA30_0<='\u20EA')||LA30_0=='\u2102'||LA30_0=='\u2107'||(LA30_0>='\u210A' && LA30_0<='\u2113')||LA30_0=='\u2115'||(LA30_0>='\u2119' && LA30_0<='\u211D')||LA30_0=='\u2124'||LA30_0=='\u2126'||LA30_0=='\u2128'||(LA30_0>='\u212A' && LA30_0<='\u212D')||(LA30_0>='\u212F' && LA30_0<='\u2131')||(LA30_0>='\u2133' && LA30_0<='\u2139')||(LA30_0>='\u213D' && LA30_0<='\u213F')||(LA30_0>='\u2145' && LA30_0<='\u2149')||(LA30_0>='\u2160' && LA30_0<='\u2183')||(LA30_0>='\u3005' && LA30_0<='\u3007')||(LA30_0>='\u3021' && LA30_0<='\u302F')||(LA30_0>='\u3031' && LA30_0<='\u3035')||(LA30_0>='\u3038' && LA30_0<='\u303C')||(LA30_0>='\u3041' && LA30_0<='\u3096')||(LA30_0>='\u3099' && LA30_0<='\u309A')||(LA30_0>='\u309D' && LA30_0<='\u309F')||(LA30_0>='\u30A1' && LA30_0<='\u30FF')||(LA30_0>='\u3105' && LA30_0<='\u312C')||(LA30_0>='\u3131' && LA30_0<='\u318E')||(LA30_0>='\u31A0' && LA30_0<='\u31B7')||(LA30_0>='\u31F0' && LA30_0<='\u31FF')||(LA30_0>='\u3400' && LA30_0<='\u4DB5')||(LA30_0>='\u4E00' && LA30_0<='\u9FA5')||(LA30_0>='\uA000' && LA30_0<='\uA48C')||(LA30_0>='\uAC00' && LA30_0<='\uD7A3')||(LA30_0>='\uD800' && LA30_0<='\uDBFF')||(LA30_0>='\uF900' && LA30_0<='\uFA2D')||(LA30_0>='\uFA30' && LA30_0<='\uFA6A')||(LA30_0>='\uFB00' && LA30_0<='\uFB06')||(LA30_0>='\uFB13' && LA30_0<='\uFB17')||(LA30_0>='\uFB1D' && LA30_0<='\uFB28')||(LA30_0>='\uFB2A' && LA30_0<='\uFB36')||(LA30_0>='\uFB38' && LA30_0<='\uFB3C')||LA30_0=='\uFB3E'||(LA30_0>='\uFB40' && LA30_0<='\uFB41')||(LA30_0>='\uFB43' && LA30_0<='\uFB44')||(LA30_0>='\uFB46' && LA30_0<='\uFBB1')||(LA30_0>='\uFBD3' && LA30_0<='\uFD3D')||(LA30_0>='\uFD50' && LA30_0<='\uFD8F')||(LA30_0>='\uFD92' && LA30_0<='\uFDC7')||(LA30_0>='\uFDF0' && LA30_0<='\uFDFC')||(LA30_0>='\uFE00' && LA30_0<='\uFE0F')||(LA30_0>='\uFE20' && LA30_0<='\uFE23')||(LA30_0>='\uFE33' && LA30_0<='\uFE34')||(LA30_0>='\uFE4D' && LA30_0<='\uFE4F')||LA30_0=='\uFE69'||(LA30_0>='\uFE70' && LA30_0<='\uFE74')||(LA30_0>='\uFE76' && LA30_0<='\uFEFC')||LA30_0=='\uFEFF'||LA30_0=='\uFF04'||(LA30_0>='\uFF10' && LA30_0<='\uFF19')||(LA30_0>='\uFF21' && LA30_0<='\uFF3A')||LA30_0=='\uFF3F'||(LA30_0>='\uFF41' && LA30_0<='\uFF5A')||(LA30_0>='\uFF65' && LA30_0<='\uFFBE')||(LA30_0>='\uFFC2' && LA30_0<='\uFFC7')||(LA30_0>='\uFFCA' && LA30_0<='\uFFCF')||(LA30_0>='\uFFD2' && LA30_0<='\uFFD7')||(LA30_0>='\uFFDA' && LA30_0<='\uFFDC')||(LA30_0>='\uFFE0' && LA30_0<='\uFFE1')||(LA30_0>='\uFFE5' && LA30_0<='\uFFE6')||(LA30_0>='\uFFF9' && LA30_0<='\uFFFB')) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:973:25: IdentifierPart
            	    {
            	    mIdentifierPart(); 

            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IDENTIFIER

    // $ANTLR start SurrogateIdentifer
    public final void mSurrogateIdentifer() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:978:5: ( ( '\\ud800' .. '\\udbff' ) ( '\\udc00' .. '\\udfff' ) )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:978:9: ( '\\ud800' .. '\\udbff' ) ( '\\udc00' .. '\\udfff' )
            {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:978:9: ( '\\ud800' .. '\\udbff' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:978:10: '\\ud800' .. '\\udbff'
            {
            matchRange('\uD800','\uDBFF'); 

            }

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:978:30: ( '\\udc00' .. '\\udfff' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:978:31: '\\udc00' .. '\\udfff'
            {
            matchRange('\uDC00','\uDFFF'); 

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end SurrogateIdentifer

    // $ANTLR start IdentifierStart
    public final void mIdentifierStart() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:983:5: ( '\\u0024' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u00a2' .. '\\u00a5' | '\\u00aa' | '\\u00b5' | '\\u00ba' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u0236' | '\\u0250' .. '\\u02c1' | '\\u02c6' .. '\\u02d1' | '\\u02e0' .. '\\u02e4' | '\\u02ee' | '\\u037a' | '\\u0386' | '\\u0388' .. '\\u038a' | '\\u038c' | '\\u038e' .. '\\u03a1' | '\\u03a3' .. '\\u03ce' | '\\u03d0' .. '\\u03f5' | '\\u03f7' .. '\\u03fb' | '\\u0400' .. '\\u0481' | '\\u048a' .. '\\u04ce' | '\\u04d0' .. '\\u04f5' | '\\u04f8' .. '\\u04f9' | '\\u0500' .. '\\u050f' | '\\u0531' .. '\\u0556' | '\\u0559' | '\\u0561' .. '\\u0587' | '\\u05d0' .. '\\u05ea' | '\\u05f0' .. '\\u05f2' | '\\u0621' .. '\\u063a' | '\\u0640' .. '\\u064a' | '\\u066e' .. '\\u066f' | '\\u0671' .. '\\u06d3' | '\\u06d5' | '\\u06e5' .. '\\u06e6' | '\\u06ee' .. '\\u06ef' | '\\u06fa' .. '\\u06fc' | '\\u06ff' | '\\u0710' | '\\u0712' .. '\\u072f' | '\\u074d' .. '\\u074f' | '\\u0780' .. '\\u07a5' | '\\u07b1' | '\\u0904' .. '\\u0939' | '\\u093d' | '\\u0950' | '\\u0958' .. '\\u0961' | '\\u0985' .. '\\u098c' | '\\u098f' .. '\\u0990' | '\\u0993' .. '\\u09a8' | '\\u09aa' .. '\\u09b0' | '\\u09b2' | '\\u09b6' .. '\\u09b9' | '\\u09bd' | '\\u09dc' .. '\\u09dd' | '\\u09df' .. '\\u09e1' | '\\u09f0' .. '\\u09f3' | '\\u0a05' .. '\\u0a0a' | '\\u0a0f' .. '\\u0a10' | '\\u0a13' .. '\\u0a28' | '\\u0a2a' .. '\\u0a30' | '\\u0a32' .. '\\u0a33' | '\\u0a35' .. '\\u0a36' | '\\u0a38' .. '\\u0a39' | '\\u0a59' .. '\\u0a5c' | '\\u0a5e' | '\\u0a72' .. '\\u0a74' | '\\u0a85' .. '\\u0a8d' | '\\u0a8f' .. '\\u0a91' | '\\u0a93' .. '\\u0aa8' | '\\u0aaa' .. '\\u0ab0' | '\\u0ab2' .. '\\u0ab3' | '\\u0ab5' .. '\\u0ab9' | '\\u0abd' | '\\u0ad0' | '\\u0ae0' .. '\\u0ae1' | '\\u0af1' | '\\u0b05' .. '\\u0b0c' | '\\u0b0f' .. '\\u0b10' | '\\u0b13' .. '\\u0b28' | '\\u0b2a' .. '\\u0b30' | '\\u0b32' .. '\\u0b33' | '\\u0b35' .. '\\u0b39' | '\\u0b3d' | '\\u0b5c' .. '\\u0b5d' | '\\u0b5f' .. '\\u0b61' | '\\u0b71' | '\\u0b83' | '\\u0b85' .. '\\u0b8a' | '\\u0b8e' .. '\\u0b90' | '\\u0b92' .. '\\u0b95' | '\\u0b99' .. '\\u0b9a' | '\\u0b9c' | '\\u0b9e' .. '\\u0b9f' | '\\u0ba3' .. '\\u0ba4' | '\\u0ba8' .. '\\u0baa' | '\\u0bae' .. '\\u0bb5' | '\\u0bb7' .. '\\u0bb9' | '\\u0bf9' | '\\u0c05' .. '\\u0c0c' | '\\u0c0e' .. '\\u0c10' | '\\u0c12' .. '\\u0c28' | '\\u0c2a' .. '\\u0c33' | '\\u0c35' .. '\\u0c39' | '\\u0c60' .. '\\u0c61' | '\\u0c85' .. '\\u0c8c' | '\\u0c8e' .. '\\u0c90' | '\\u0c92' .. '\\u0ca8' | '\\u0caa' .. '\\u0cb3' | '\\u0cb5' .. '\\u0cb9' | '\\u0cbd' | '\\u0cde' | '\\u0ce0' .. '\\u0ce1' | '\\u0d05' .. '\\u0d0c' | '\\u0d0e' .. '\\u0d10' | '\\u0d12' .. '\\u0d28' | '\\u0d2a' .. '\\u0d39' | '\\u0d60' .. '\\u0d61' | '\\u0d85' .. '\\u0d96' | '\\u0d9a' .. '\\u0db1' | '\\u0db3' .. '\\u0dbb' | '\\u0dbd' | '\\u0dc0' .. '\\u0dc6' | '\\u0e01' .. '\\u0e30' | '\\u0e32' .. '\\u0e33' | '\\u0e3f' .. '\\u0e46' | '\\u0e81' .. '\\u0e82' | '\\u0e84' | '\\u0e87' .. '\\u0e88' | '\\u0e8a' | '\\u0e8d' | '\\u0e94' .. '\\u0e97' | '\\u0e99' .. '\\u0e9f' | '\\u0ea1' .. '\\u0ea3' | '\\u0ea5' | '\\u0ea7' | '\\u0eaa' .. '\\u0eab' | '\\u0ead' .. '\\u0eb0' | '\\u0eb2' .. '\\u0eb3' | '\\u0ebd' | '\\u0ec0' .. '\\u0ec4' | '\\u0ec6' | '\\u0edc' .. '\\u0edd' | '\\u0f00' | '\\u0f40' .. '\\u0f47' | '\\u0f49' .. '\\u0f6a' | '\\u0f88' .. '\\u0f8b' | '\\u1000' .. '\\u1021' | '\\u1023' .. '\\u1027' | '\\u1029' .. '\\u102a' | '\\u1050' .. '\\u1055' | '\\u10a0' .. '\\u10c5' | '\\u10d0' .. '\\u10f8' | '\\u1100' .. '\\u1159' | '\\u115f' .. '\\u11a2' | '\\u11a8' .. '\\u11f9' | '\\u1200' .. '\\u1206' | '\\u1208' .. '\\u1246' | '\\u1248' | '\\u124a' .. '\\u124d' | '\\u1250' .. '\\u1256' | '\\u1258' | '\\u125a' .. '\\u125d' | '\\u1260' .. '\\u1286' | '\\u1288' | '\\u128a' .. '\\u128d' | '\\u1290' .. '\\u12ae' | '\\u12b0' | '\\u12b2' .. '\\u12b5' | '\\u12b8' .. '\\u12be' | '\\u12c0' | '\\u12c2' .. '\\u12c5' | '\\u12c8' .. '\\u12ce' | '\\u12d0' .. '\\u12d6' | '\\u12d8' .. '\\u12ee' | '\\u12f0' .. '\\u130e' | '\\u1310' | '\\u1312' .. '\\u1315' | '\\u1318' .. '\\u131e' | '\\u1320' .. '\\u1346' | '\\u1348' .. '\\u135a' | '\\u13a0' .. '\\u13f4' | '\\u1401' .. '\\u166c' | '\\u166f' .. '\\u1676' | '\\u1681' .. '\\u169a' | '\\u16a0' .. '\\u16ea' | '\\u16ee' .. '\\u16f0' | '\\u1700' .. '\\u170c' | '\\u170e' .. '\\u1711' | '\\u1720' .. '\\u1731' | '\\u1740' .. '\\u1751' | '\\u1760' .. '\\u176c' | '\\u176e' .. '\\u1770' | '\\u1780' .. '\\u17b3' | '\\u17d7' | '\\u17db' .. '\\u17dc' | '\\u1820' .. '\\u1877' | '\\u1880' .. '\\u18a8' | '\\u1900' .. '\\u191c' | '\\u1950' .. '\\u196d' | '\\u1970' .. '\\u1974' | '\\u1d00' .. '\\u1d6b' | '\\u1e00' .. '\\u1e9b' | '\\u1ea0' .. '\\u1ef9' | '\\u1f00' .. '\\u1f15' | '\\u1f18' .. '\\u1f1d' | '\\u1f20' .. '\\u1f45' | '\\u1f48' .. '\\u1f4d' | '\\u1f50' .. '\\u1f57' | '\\u1f59' | '\\u1f5b' | '\\u1f5d' | '\\u1f5f' .. '\\u1f7d' | '\\u1f80' .. '\\u1fb4' | '\\u1fb6' .. '\\u1fbc' | '\\u1fbe' | '\\u1fc2' .. '\\u1fc4' | '\\u1fc6' .. '\\u1fcc' | '\\u1fd0' .. '\\u1fd3' | '\\u1fd6' .. '\\u1fdb' | '\\u1fe0' .. '\\u1fec' | '\\u1ff2' .. '\\u1ff4' | '\\u1ff6' .. '\\u1ffc' | '\\u203f' .. '\\u2040' | '\\u2054' | '\\u2071' | '\\u207f' | '\\u20a0' .. '\\u20b1' | '\\u2102' | '\\u2107' | '\\u210a' .. '\\u2113' | '\\u2115' | '\\u2119' .. '\\u211d' | '\\u2124' | '\\u2126' | '\\u2128' | '\\u212a' .. '\\u212d' | '\\u212f' .. '\\u2131' | '\\u2133' .. '\\u2139' | '\\u213d' .. '\\u213f' | '\\u2145' .. '\\u2149' | '\\u2160' .. '\\u2183' | '\\u3005' .. '\\u3007' | '\\u3021' .. '\\u3029' | '\\u3031' .. '\\u3035' | '\\u3038' .. '\\u303c' | '\\u3041' .. '\\u3096' | '\\u309d' .. '\\u309f' | '\\u30a1' .. '\\u30ff' | '\\u3105' .. '\\u312c' | '\\u3131' .. '\\u318e' | '\\u31a0' .. '\\u31b7' | '\\u31f0' .. '\\u31ff' | '\\u3400' .. '\\u4db5' | '\\u4e00' .. '\\u9fa5' | '\\ua000' .. '\\ua48c' | '\\uac00' .. '\\ud7a3' | '\\uf900' .. '\\ufa2d' | '\\ufa30' .. '\\ufa6a' | '\\ufb00' .. '\\ufb06' | '\\ufb13' .. '\\ufb17' | '\\ufb1d' | '\\ufb1f' .. '\\ufb28' | '\\ufb2a' .. '\\ufb36' | '\\ufb38' .. '\\ufb3c' | '\\ufb3e' | '\\ufb40' .. '\\ufb41' | '\\ufb43' .. '\\ufb44' | '\\ufb46' .. '\\ufbb1' | '\\ufbd3' .. '\\ufd3d' | '\\ufd50' .. '\\ufd8f' | '\\ufd92' .. '\\ufdc7' | '\\ufdf0' .. '\\ufdfc' | '\\ufe33' .. '\\ufe34' | '\\ufe4d' .. '\\ufe4f' | '\\ufe69' | '\\ufe70' .. '\\ufe74' | '\\ufe76' .. '\\ufefc' | '\\uff04' | '\\uff21' .. '\\uff3a' | '\\uff3f' | '\\uff41' .. '\\uff5a' | '\\uff65' .. '\\uffbe' | '\\uffc2' .. '\\uffc7' | '\\uffca' .. '\\uffcf' | '\\uffd2' .. '\\uffd7' | '\\uffda' .. '\\uffdc' | '\\uffe0' .. '\\uffe1' | '\\uffe5' .. '\\uffe6' | ( '\\ud800' .. '\\udbff' ) ( '\\udc00' .. '\\udfff' ) )
            int alt31=294;
            int LA31_0 = input.LA(1);

            if ( (LA31_0=='$') ) {
                alt31=1;
            }
            else if ( ((LA31_0>='A' && LA31_0<='Z')) ) {
                alt31=2;
            }
            else if ( (LA31_0=='_') ) {
                alt31=3;
            }
            else if ( ((LA31_0>='a' && LA31_0<='z')) ) {
                alt31=4;
            }
            else if ( ((LA31_0>='\u00A2' && LA31_0<='\u00A5')) ) {
                alt31=5;
            }
            else if ( (LA31_0=='\u00AA') ) {
                alt31=6;
            }
            else if ( (LA31_0=='\u00B5') ) {
                alt31=7;
            }
            else if ( (LA31_0=='\u00BA') ) {
                alt31=8;
            }
            else if ( ((LA31_0>='\u00C0' && LA31_0<='\u00D6')) ) {
                alt31=9;
            }
            else if ( ((LA31_0>='\u00D8' && LA31_0<='\u00F6')) ) {
                alt31=10;
            }
            else if ( ((LA31_0>='\u00F8' && LA31_0<='\u0236')) ) {
                alt31=11;
            }
            else if ( ((LA31_0>='\u0250' && LA31_0<='\u02C1')) ) {
                alt31=12;
            }
            else if ( ((LA31_0>='\u02C6' && LA31_0<='\u02D1')) ) {
                alt31=13;
            }
            else if ( ((LA31_0>='\u02E0' && LA31_0<='\u02E4')) ) {
                alt31=14;
            }
            else if ( (LA31_0=='\u02EE') ) {
                alt31=15;
            }
            else if ( (LA31_0=='\u037A') ) {
                alt31=16;
            }
            else if ( (LA31_0=='\u0386') ) {
                alt31=17;
            }
            else if ( ((LA31_0>='\u0388' && LA31_0<='\u038A')) ) {
                alt31=18;
            }
            else if ( (LA31_0=='\u038C') ) {
                alt31=19;
            }
            else if ( ((LA31_0>='\u038E' && LA31_0<='\u03A1')) ) {
                alt31=20;
            }
            else if ( ((LA31_0>='\u03A3' && LA31_0<='\u03CE')) ) {
                alt31=21;
            }
            else if ( ((LA31_0>='\u03D0' && LA31_0<='\u03F5')) ) {
                alt31=22;
            }
            else if ( ((LA31_0>='\u03F7' && LA31_0<='\u03FB')) ) {
                alt31=23;
            }
            else if ( ((LA31_0>='\u0400' && LA31_0<='\u0481')) ) {
                alt31=24;
            }
            else if ( ((LA31_0>='\u048A' && LA31_0<='\u04CE')) ) {
                alt31=25;
            }
            else if ( ((LA31_0>='\u04D0' && LA31_0<='\u04F5')) ) {
                alt31=26;
            }
            else if ( ((LA31_0>='\u04F8' && LA31_0<='\u04F9')) ) {
                alt31=27;
            }
            else if ( ((LA31_0>='\u0500' && LA31_0<='\u050F')) ) {
                alt31=28;
            }
            else if ( ((LA31_0>='\u0531' && LA31_0<='\u0556')) ) {
                alt31=29;
            }
            else if ( (LA31_0=='\u0559') ) {
                alt31=30;
            }
            else if ( ((LA31_0>='\u0561' && LA31_0<='\u0587')) ) {
                alt31=31;
            }
            else if ( ((LA31_0>='\u05D0' && LA31_0<='\u05EA')) ) {
                alt31=32;
            }
            else if ( ((LA31_0>='\u05F0' && LA31_0<='\u05F2')) ) {
                alt31=33;
            }
            else if ( ((LA31_0>='\u0621' && LA31_0<='\u063A')) ) {
                alt31=34;
            }
            else if ( ((LA31_0>='\u0640' && LA31_0<='\u064A')) ) {
                alt31=35;
            }
            else if ( ((LA31_0>='\u066E' && LA31_0<='\u066F')) ) {
                alt31=36;
            }
            else if ( ((LA31_0>='\u0671' && LA31_0<='\u06D3')) ) {
                alt31=37;
            }
            else if ( (LA31_0=='\u06D5') ) {
                alt31=38;
            }
            else if ( ((LA31_0>='\u06E5' && LA31_0<='\u06E6')) ) {
                alt31=39;
            }
            else if ( ((LA31_0>='\u06EE' && LA31_0<='\u06EF')) ) {
                alt31=40;
            }
            else if ( ((LA31_0>='\u06FA' && LA31_0<='\u06FC')) ) {
                alt31=41;
            }
            else if ( (LA31_0=='\u06FF') ) {
                alt31=42;
            }
            else if ( (LA31_0=='\u0710') ) {
                alt31=43;
            }
            else if ( ((LA31_0>='\u0712' && LA31_0<='\u072F')) ) {
                alt31=44;
            }
            else if ( ((LA31_0>='\u074D' && LA31_0<='\u074F')) ) {
                alt31=45;
            }
            else if ( ((LA31_0>='\u0780' && LA31_0<='\u07A5')) ) {
                alt31=46;
            }
            else if ( (LA31_0=='\u07B1') ) {
                alt31=47;
            }
            else if ( ((LA31_0>='\u0904' && LA31_0<='\u0939')) ) {
                alt31=48;
            }
            else if ( (LA31_0=='\u093D') ) {
                alt31=49;
            }
            else if ( (LA31_0=='\u0950') ) {
                alt31=50;
            }
            else if ( ((LA31_0>='\u0958' && LA31_0<='\u0961')) ) {
                alt31=51;
            }
            else if ( ((LA31_0>='\u0985' && LA31_0<='\u098C')) ) {
                alt31=52;
            }
            else if ( ((LA31_0>='\u098F' && LA31_0<='\u0990')) ) {
                alt31=53;
            }
            else if ( ((LA31_0>='\u0993' && LA31_0<='\u09A8')) ) {
                alt31=54;
            }
            else if ( ((LA31_0>='\u09AA' && LA31_0<='\u09B0')) ) {
                alt31=55;
            }
            else if ( (LA31_0=='\u09B2') ) {
                alt31=56;
            }
            else if ( ((LA31_0>='\u09B6' && LA31_0<='\u09B9')) ) {
                alt31=57;
            }
            else if ( (LA31_0=='\u09BD') ) {
                alt31=58;
            }
            else if ( ((LA31_0>='\u09DC' && LA31_0<='\u09DD')) ) {
                alt31=59;
            }
            else if ( ((LA31_0>='\u09DF' && LA31_0<='\u09E1')) ) {
                alt31=60;
            }
            else if ( ((LA31_0>='\u09F0' && LA31_0<='\u09F3')) ) {
                alt31=61;
            }
            else if ( ((LA31_0>='\u0A05' && LA31_0<='\u0A0A')) ) {
                alt31=62;
            }
            else if ( ((LA31_0>='\u0A0F' && LA31_0<='\u0A10')) ) {
                alt31=63;
            }
            else if ( ((LA31_0>='\u0A13' && LA31_0<='\u0A28')) ) {
                alt31=64;
            }
            else if ( ((LA31_0>='\u0A2A' && LA31_0<='\u0A30')) ) {
                alt31=65;
            }
            else if ( ((LA31_0>='\u0A32' && LA31_0<='\u0A33')) ) {
                alt31=66;
            }
            else if ( ((LA31_0>='\u0A35' && LA31_0<='\u0A36')) ) {
                alt31=67;
            }
            else if ( ((LA31_0>='\u0A38' && LA31_0<='\u0A39')) ) {
                alt31=68;
            }
            else if ( ((LA31_0>='\u0A59' && LA31_0<='\u0A5C')) ) {
                alt31=69;
            }
            else if ( (LA31_0=='\u0A5E') ) {
                alt31=70;
            }
            else if ( ((LA31_0>='\u0A72' && LA31_0<='\u0A74')) ) {
                alt31=71;
            }
            else if ( ((LA31_0>='\u0A85' && LA31_0<='\u0A8D')) ) {
                alt31=72;
            }
            else if ( ((LA31_0>='\u0A8F' && LA31_0<='\u0A91')) ) {
                alt31=73;
            }
            else if ( ((LA31_0>='\u0A93' && LA31_0<='\u0AA8')) ) {
                alt31=74;
            }
            else if ( ((LA31_0>='\u0AAA' && LA31_0<='\u0AB0')) ) {
                alt31=75;
            }
            else if ( ((LA31_0>='\u0AB2' && LA31_0<='\u0AB3')) ) {
                alt31=76;
            }
            else if ( ((LA31_0>='\u0AB5' && LA31_0<='\u0AB9')) ) {
                alt31=77;
            }
            else if ( (LA31_0=='\u0ABD') ) {
                alt31=78;
            }
            else if ( (LA31_0=='\u0AD0') ) {
                alt31=79;
            }
            else if ( ((LA31_0>='\u0AE0' && LA31_0<='\u0AE1')) ) {
                alt31=80;
            }
            else if ( (LA31_0=='\u0AF1') ) {
                alt31=81;
            }
            else if ( ((LA31_0>='\u0B05' && LA31_0<='\u0B0C')) ) {
                alt31=82;
            }
            else if ( ((LA31_0>='\u0B0F' && LA31_0<='\u0B10')) ) {
                alt31=83;
            }
            else if ( ((LA31_0>='\u0B13' && LA31_0<='\u0B28')) ) {
                alt31=84;
            }
            else if ( ((LA31_0>='\u0B2A' && LA31_0<='\u0B30')) ) {
                alt31=85;
            }
            else if ( ((LA31_0>='\u0B32' && LA31_0<='\u0B33')) ) {
                alt31=86;
            }
            else if ( ((LA31_0>='\u0B35' && LA31_0<='\u0B39')) ) {
                alt31=87;
            }
            else if ( (LA31_0=='\u0B3D') ) {
                alt31=88;
            }
            else if ( ((LA31_0>='\u0B5C' && LA31_0<='\u0B5D')) ) {
                alt31=89;
            }
            else if ( ((LA31_0>='\u0B5F' && LA31_0<='\u0B61')) ) {
                alt31=90;
            }
            else if ( (LA31_0=='\u0B71') ) {
                alt31=91;
            }
            else if ( (LA31_0=='\u0B83') ) {
                alt31=92;
            }
            else if ( ((LA31_0>='\u0B85' && LA31_0<='\u0B8A')) ) {
                alt31=93;
            }
            else if ( ((LA31_0>='\u0B8E' && LA31_0<='\u0B90')) ) {
                alt31=94;
            }
            else if ( ((LA31_0>='\u0B92' && LA31_0<='\u0B95')) ) {
                alt31=95;
            }
            else if ( ((LA31_0>='\u0B99' && LA31_0<='\u0B9A')) ) {
                alt31=96;
            }
            else if ( (LA31_0=='\u0B9C') ) {
                alt31=97;
            }
            else if ( ((LA31_0>='\u0B9E' && LA31_0<='\u0B9F')) ) {
                alt31=98;
            }
            else if ( ((LA31_0>='\u0BA3' && LA31_0<='\u0BA4')) ) {
                alt31=99;
            }
            else if ( ((LA31_0>='\u0BA8' && LA31_0<='\u0BAA')) ) {
                alt31=100;
            }
            else if ( ((LA31_0>='\u0BAE' && LA31_0<='\u0BB5')) ) {
                alt31=101;
            }
            else if ( ((LA31_0>='\u0BB7' && LA31_0<='\u0BB9')) ) {
                alt31=102;
            }
            else if ( (LA31_0=='\u0BF9') ) {
                alt31=103;
            }
            else if ( ((LA31_0>='\u0C05' && LA31_0<='\u0C0C')) ) {
                alt31=104;
            }
            else if ( ((LA31_0>='\u0C0E' && LA31_0<='\u0C10')) ) {
                alt31=105;
            }
            else if ( ((LA31_0>='\u0C12' && LA31_0<='\u0C28')) ) {
                alt31=106;
            }
            else if ( ((LA31_0>='\u0C2A' && LA31_0<='\u0C33')) ) {
                alt31=107;
            }
            else if ( ((LA31_0>='\u0C35' && LA31_0<='\u0C39')) ) {
                alt31=108;
            }
            else if ( ((LA31_0>='\u0C60' && LA31_0<='\u0C61')) ) {
                alt31=109;
            }
            else if ( ((LA31_0>='\u0C85' && LA31_0<='\u0C8C')) ) {
                alt31=110;
            }
            else if ( ((LA31_0>='\u0C8E' && LA31_0<='\u0C90')) ) {
                alt31=111;
            }
            else if ( ((LA31_0>='\u0C92' && LA31_0<='\u0CA8')) ) {
                alt31=112;
            }
            else if ( ((LA31_0>='\u0CAA' && LA31_0<='\u0CB3')) ) {
                alt31=113;
            }
            else if ( ((LA31_0>='\u0CB5' && LA31_0<='\u0CB9')) ) {
                alt31=114;
            }
            else if ( (LA31_0=='\u0CBD') ) {
                alt31=115;
            }
            else if ( (LA31_0=='\u0CDE') ) {
                alt31=116;
            }
            else if ( ((LA31_0>='\u0CE0' && LA31_0<='\u0CE1')) ) {
                alt31=117;
            }
            else if ( ((LA31_0>='\u0D05' && LA31_0<='\u0D0C')) ) {
                alt31=118;
            }
            else if ( ((LA31_0>='\u0D0E' && LA31_0<='\u0D10')) ) {
                alt31=119;
            }
            else if ( ((LA31_0>='\u0D12' && LA31_0<='\u0D28')) ) {
                alt31=120;
            }
            else if ( ((LA31_0>='\u0D2A' && LA31_0<='\u0D39')) ) {
                alt31=121;
            }
            else if ( ((LA31_0>='\u0D60' && LA31_0<='\u0D61')) ) {
                alt31=122;
            }
            else if ( ((LA31_0>='\u0D85' && LA31_0<='\u0D96')) ) {
                alt31=123;
            }
            else if ( ((LA31_0>='\u0D9A' && LA31_0<='\u0DB1')) ) {
                alt31=124;
            }
            else if ( ((LA31_0>='\u0DB3' && LA31_0<='\u0DBB')) ) {
                alt31=125;
            }
            else if ( (LA31_0=='\u0DBD') ) {
                alt31=126;
            }
            else if ( ((LA31_0>='\u0DC0' && LA31_0<='\u0DC6')) ) {
                alt31=127;
            }
            else if ( ((LA31_0>='\u0E01' && LA31_0<='\u0E30')) ) {
                alt31=128;
            }
            else if ( ((LA31_0>='\u0E32' && LA31_0<='\u0E33')) ) {
                alt31=129;
            }
            else if ( ((LA31_0>='\u0E3F' && LA31_0<='\u0E46')) ) {
                alt31=130;
            }
            else if ( ((LA31_0>='\u0E81' && LA31_0<='\u0E82')) ) {
                alt31=131;
            }
            else if ( (LA31_0=='\u0E84') ) {
                alt31=132;
            }
            else if ( ((LA31_0>='\u0E87' && LA31_0<='\u0E88')) ) {
                alt31=133;
            }
            else if ( (LA31_0=='\u0E8A') ) {
                alt31=134;
            }
            else if ( (LA31_0=='\u0E8D') ) {
                alt31=135;
            }
            else if ( ((LA31_0>='\u0E94' && LA31_0<='\u0E97')) ) {
                alt31=136;
            }
            else if ( ((LA31_0>='\u0E99' && LA31_0<='\u0E9F')) ) {
                alt31=137;
            }
            else if ( ((LA31_0>='\u0EA1' && LA31_0<='\u0EA3')) ) {
                alt31=138;
            }
            else if ( (LA31_0=='\u0EA5') ) {
                alt31=139;
            }
            else if ( (LA31_0=='\u0EA7') ) {
                alt31=140;
            }
            else if ( ((LA31_0>='\u0EAA' && LA31_0<='\u0EAB')) ) {
                alt31=141;
            }
            else if ( ((LA31_0>='\u0EAD' && LA31_0<='\u0EB0')) ) {
                alt31=142;
            }
            else if ( ((LA31_0>='\u0EB2' && LA31_0<='\u0EB3')) ) {
                alt31=143;
            }
            else if ( (LA31_0=='\u0EBD') ) {
                alt31=144;
            }
            else if ( ((LA31_0>='\u0EC0' && LA31_0<='\u0EC4')) ) {
                alt31=145;
            }
            else if ( (LA31_0=='\u0EC6') ) {
                alt31=146;
            }
            else if ( ((LA31_0>='\u0EDC' && LA31_0<='\u0EDD')) ) {
                alt31=147;
            }
            else if ( (LA31_0=='\u0F00') ) {
                alt31=148;
            }
            else if ( ((LA31_0>='\u0F40' && LA31_0<='\u0F47')) ) {
                alt31=149;
            }
            else if ( ((LA31_0>='\u0F49' && LA31_0<='\u0F6A')) ) {
                alt31=150;
            }
            else if ( ((LA31_0>='\u0F88' && LA31_0<='\u0F8B')) ) {
                alt31=151;
            }
            else if ( ((LA31_0>='\u1000' && LA31_0<='\u1021')) ) {
                alt31=152;
            }
            else if ( ((LA31_0>='\u1023' && LA31_0<='\u1027')) ) {
                alt31=153;
            }
            else if ( ((LA31_0>='\u1029' && LA31_0<='\u102A')) ) {
                alt31=154;
            }
            else if ( ((LA31_0>='\u1050' && LA31_0<='\u1055')) ) {
                alt31=155;
            }
            else if ( ((LA31_0>='\u10A0' && LA31_0<='\u10C5')) ) {
                alt31=156;
            }
            else if ( ((LA31_0>='\u10D0' && LA31_0<='\u10F8')) ) {
                alt31=157;
            }
            else if ( ((LA31_0>='\u1100' && LA31_0<='\u1159')) ) {
                alt31=158;
            }
            else if ( ((LA31_0>='\u115F' && LA31_0<='\u11A2')) ) {
                alt31=159;
            }
            else if ( ((LA31_0>='\u11A8' && LA31_0<='\u11F9')) ) {
                alt31=160;
            }
            else if ( ((LA31_0>='\u1200' && LA31_0<='\u1206')) ) {
                alt31=161;
            }
            else if ( ((LA31_0>='\u1208' && LA31_0<='\u1246')) ) {
                alt31=162;
            }
            else if ( (LA31_0=='\u1248') ) {
                alt31=163;
            }
            else if ( ((LA31_0>='\u124A' && LA31_0<='\u124D')) ) {
                alt31=164;
            }
            else if ( ((LA31_0>='\u1250' && LA31_0<='\u1256')) ) {
                alt31=165;
            }
            else if ( (LA31_0=='\u1258') ) {
                alt31=166;
            }
            else if ( ((LA31_0>='\u125A' && LA31_0<='\u125D')) ) {
                alt31=167;
            }
            else if ( ((LA31_0>='\u1260' && LA31_0<='\u1286')) ) {
                alt31=168;
            }
            else if ( (LA31_0=='\u1288') ) {
                alt31=169;
            }
            else if ( ((LA31_0>='\u128A' && LA31_0<='\u128D')) ) {
                alt31=170;
            }
            else if ( ((LA31_0>='\u1290' && LA31_0<='\u12AE')) ) {
                alt31=171;
            }
            else if ( (LA31_0=='\u12B0') ) {
                alt31=172;
            }
            else if ( ((LA31_0>='\u12B2' && LA31_0<='\u12B5')) ) {
                alt31=173;
            }
            else if ( ((LA31_0>='\u12B8' && LA31_0<='\u12BE')) ) {
                alt31=174;
            }
            else if ( (LA31_0=='\u12C0') ) {
                alt31=175;
            }
            else if ( ((LA31_0>='\u12C2' && LA31_0<='\u12C5')) ) {
                alt31=176;
            }
            else if ( ((LA31_0>='\u12C8' && LA31_0<='\u12CE')) ) {
                alt31=177;
            }
            else if ( ((LA31_0>='\u12D0' && LA31_0<='\u12D6')) ) {
                alt31=178;
            }
            else if ( ((LA31_0>='\u12D8' && LA31_0<='\u12EE')) ) {
                alt31=179;
            }
            else if ( ((LA31_0>='\u12F0' && LA31_0<='\u130E')) ) {
                alt31=180;
            }
            else if ( (LA31_0=='\u1310') ) {
                alt31=181;
            }
            else if ( ((LA31_0>='\u1312' && LA31_0<='\u1315')) ) {
                alt31=182;
            }
            else if ( ((LA31_0>='\u1318' && LA31_0<='\u131E')) ) {
                alt31=183;
            }
            else if ( ((LA31_0>='\u1320' && LA31_0<='\u1346')) ) {
                alt31=184;
            }
            else if ( ((LA31_0>='\u1348' && LA31_0<='\u135A')) ) {
                alt31=185;
            }
            else if ( ((LA31_0>='\u13A0' && LA31_0<='\u13F4')) ) {
                alt31=186;
            }
            else if ( ((LA31_0>='\u1401' && LA31_0<='\u166C')) ) {
                alt31=187;
            }
            else if ( ((LA31_0>='\u166F' && LA31_0<='\u1676')) ) {
                alt31=188;
            }
            else if ( ((LA31_0>='\u1681' && LA31_0<='\u169A')) ) {
                alt31=189;
            }
            else if ( ((LA31_0>='\u16A0' && LA31_0<='\u16EA')) ) {
                alt31=190;
            }
            else if ( ((LA31_0>='\u16EE' && LA31_0<='\u16F0')) ) {
                alt31=191;
            }
            else if ( ((LA31_0>='\u1700' && LA31_0<='\u170C')) ) {
                alt31=192;
            }
            else if ( ((LA31_0>='\u170E' && LA31_0<='\u1711')) ) {
                alt31=193;
            }
            else if ( ((LA31_0>='\u1720' && LA31_0<='\u1731')) ) {
                alt31=194;
            }
            else if ( ((LA31_0>='\u1740' && LA31_0<='\u1751')) ) {
                alt31=195;
            }
            else if ( ((LA31_0>='\u1760' && LA31_0<='\u176C')) ) {
                alt31=196;
            }
            else if ( ((LA31_0>='\u176E' && LA31_0<='\u1770')) ) {
                alt31=197;
            }
            else if ( ((LA31_0>='\u1780' && LA31_0<='\u17B3')) ) {
                alt31=198;
            }
            else if ( (LA31_0=='\u17D7') ) {
                alt31=199;
            }
            else if ( ((LA31_0>='\u17DB' && LA31_0<='\u17DC')) ) {
                alt31=200;
            }
            else if ( ((LA31_0>='\u1820' && LA31_0<='\u1877')) ) {
                alt31=201;
            }
            else if ( ((LA31_0>='\u1880' && LA31_0<='\u18A8')) ) {
                alt31=202;
            }
            else if ( ((LA31_0>='\u1900' && LA31_0<='\u191C')) ) {
                alt31=203;
            }
            else if ( ((LA31_0>='\u1950' && LA31_0<='\u196D')) ) {
                alt31=204;
            }
            else if ( ((LA31_0>='\u1970' && LA31_0<='\u1974')) ) {
                alt31=205;
            }
            else if ( ((LA31_0>='\u1D00' && LA31_0<='\u1D6B')) ) {
                alt31=206;
            }
            else if ( ((LA31_0>='\u1E00' && LA31_0<='\u1E9B')) ) {
                alt31=207;
            }
            else if ( ((LA31_0>='\u1EA0' && LA31_0<='\u1EF9')) ) {
                alt31=208;
            }
            else if ( ((LA31_0>='\u1F00' && LA31_0<='\u1F15')) ) {
                alt31=209;
            }
            else if ( ((LA31_0>='\u1F18' && LA31_0<='\u1F1D')) ) {
                alt31=210;
            }
            else if ( ((LA31_0>='\u1F20' && LA31_0<='\u1F45')) ) {
                alt31=211;
            }
            else if ( ((LA31_0>='\u1F48' && LA31_0<='\u1F4D')) ) {
                alt31=212;
            }
            else if ( ((LA31_0>='\u1F50' && LA31_0<='\u1F57')) ) {
                alt31=213;
            }
            else if ( (LA31_0=='\u1F59') ) {
                alt31=214;
            }
            else if ( (LA31_0=='\u1F5B') ) {
                alt31=215;
            }
            else if ( (LA31_0=='\u1F5D') ) {
                alt31=216;
            }
            else if ( ((LA31_0>='\u1F5F' && LA31_0<='\u1F7D')) ) {
                alt31=217;
            }
            else if ( ((LA31_0>='\u1F80' && LA31_0<='\u1FB4')) ) {
                alt31=218;
            }
            else if ( ((LA31_0>='\u1FB6' && LA31_0<='\u1FBC')) ) {
                alt31=219;
            }
            else if ( (LA31_0=='\u1FBE') ) {
                alt31=220;
            }
            else if ( ((LA31_0>='\u1FC2' && LA31_0<='\u1FC4')) ) {
                alt31=221;
            }
            else if ( ((LA31_0>='\u1FC6' && LA31_0<='\u1FCC')) ) {
                alt31=222;
            }
            else if ( ((LA31_0>='\u1FD0' && LA31_0<='\u1FD3')) ) {
                alt31=223;
            }
            else if ( ((LA31_0>='\u1FD6' && LA31_0<='\u1FDB')) ) {
                alt31=224;
            }
            else if ( ((LA31_0>='\u1FE0' && LA31_0<='\u1FEC')) ) {
                alt31=225;
            }
            else if ( ((LA31_0>='\u1FF2' && LA31_0<='\u1FF4')) ) {
                alt31=226;
            }
            else if ( ((LA31_0>='\u1FF6' && LA31_0<='\u1FFC')) ) {
                alt31=227;
            }
            else if ( ((LA31_0>='\u203F' && LA31_0<='\u2040')) ) {
                alt31=228;
            }
            else if ( (LA31_0=='\u2054') ) {
                alt31=229;
            }
            else if ( (LA31_0=='\u2071') ) {
                alt31=230;
            }
            else if ( (LA31_0=='\u207F') ) {
                alt31=231;
            }
            else if ( ((LA31_0>='\u20A0' && LA31_0<='\u20B1')) ) {
                alt31=232;
            }
            else if ( (LA31_0=='\u2102') ) {
                alt31=233;
            }
            else if ( (LA31_0=='\u2107') ) {
                alt31=234;
            }
            else if ( ((LA31_0>='\u210A' && LA31_0<='\u2113')) ) {
                alt31=235;
            }
            else if ( (LA31_0=='\u2115') ) {
                alt31=236;
            }
            else if ( ((LA31_0>='\u2119' && LA31_0<='\u211D')) ) {
                alt31=237;
            }
            else if ( (LA31_0=='\u2124') ) {
                alt31=238;
            }
            else if ( (LA31_0=='\u2126') ) {
                alt31=239;
            }
            else if ( (LA31_0=='\u2128') ) {
                alt31=240;
            }
            else if ( ((LA31_0>='\u212A' && LA31_0<='\u212D')) ) {
                alt31=241;
            }
            else if ( ((LA31_0>='\u212F' && LA31_0<='\u2131')) ) {
                alt31=242;
            }
            else if ( ((LA31_0>='\u2133' && LA31_0<='\u2139')) ) {
                alt31=243;
            }
            else if ( ((LA31_0>='\u213D' && LA31_0<='\u213F')) ) {
                alt31=244;
            }
            else if ( ((LA31_0>='\u2145' && LA31_0<='\u2149')) ) {
                alt31=245;
            }
            else if ( ((LA31_0>='\u2160' && LA31_0<='\u2183')) ) {
                alt31=246;
            }
            else if ( ((LA31_0>='\u3005' && LA31_0<='\u3007')) ) {
                alt31=247;
            }
            else if ( ((LA31_0>='\u3021' && LA31_0<='\u3029')) ) {
                alt31=248;
            }
            else if ( ((LA31_0>='\u3031' && LA31_0<='\u3035')) ) {
                alt31=249;
            }
            else if ( ((LA31_0>='\u3038' && LA31_0<='\u303C')) ) {
                alt31=250;
            }
            else if ( ((LA31_0>='\u3041' && LA31_0<='\u3096')) ) {
                alt31=251;
            }
            else if ( ((LA31_0>='\u309D' && LA31_0<='\u309F')) ) {
                alt31=252;
            }
            else if ( ((LA31_0>='\u30A1' && LA31_0<='\u30FF')) ) {
                alt31=253;
            }
            else if ( ((LA31_0>='\u3105' && LA31_0<='\u312C')) ) {
                alt31=254;
            }
            else if ( ((LA31_0>='\u3131' && LA31_0<='\u318E')) ) {
                alt31=255;
            }
            else if ( ((LA31_0>='\u31A0' && LA31_0<='\u31B7')) ) {
                alt31=256;
            }
            else if ( ((LA31_0>='\u31F0' && LA31_0<='\u31FF')) ) {
                alt31=257;
            }
            else if ( ((LA31_0>='\u3400' && LA31_0<='\u4DB5')) ) {
                alt31=258;
            }
            else if ( ((LA31_0>='\u4E00' && LA31_0<='\u9FA5')) ) {
                alt31=259;
            }
            else if ( ((LA31_0>='\uA000' && LA31_0<='\uA48C')) ) {
                alt31=260;
            }
            else if ( ((LA31_0>='\uAC00' && LA31_0<='\uD7A3')) ) {
                alt31=261;
            }
            else if ( ((LA31_0>='\uF900' && LA31_0<='\uFA2D')) ) {
                alt31=262;
            }
            else if ( ((LA31_0>='\uFA30' && LA31_0<='\uFA6A')) ) {
                alt31=263;
            }
            else if ( ((LA31_0>='\uFB00' && LA31_0<='\uFB06')) ) {
                alt31=264;
            }
            else if ( ((LA31_0>='\uFB13' && LA31_0<='\uFB17')) ) {
                alt31=265;
            }
            else if ( (LA31_0=='\uFB1D') ) {
                alt31=266;
            }
            else if ( ((LA31_0>='\uFB1F' && LA31_0<='\uFB28')) ) {
                alt31=267;
            }
            else if ( ((LA31_0>='\uFB2A' && LA31_0<='\uFB36')) ) {
                alt31=268;
            }
            else if ( ((LA31_0>='\uFB38' && LA31_0<='\uFB3C')) ) {
                alt31=269;
            }
            else if ( (LA31_0=='\uFB3E') ) {
                alt31=270;
            }
            else if ( ((LA31_0>='\uFB40' && LA31_0<='\uFB41')) ) {
                alt31=271;
            }
            else if ( ((LA31_0>='\uFB43' && LA31_0<='\uFB44')) ) {
                alt31=272;
            }
            else if ( ((LA31_0>='\uFB46' && LA31_0<='\uFBB1')) ) {
                alt31=273;
            }
            else if ( ((LA31_0>='\uFBD3' && LA31_0<='\uFD3D')) ) {
                alt31=274;
            }
            else if ( ((LA31_0>='\uFD50' && LA31_0<='\uFD8F')) ) {
                alt31=275;
            }
            else if ( ((LA31_0>='\uFD92' && LA31_0<='\uFDC7')) ) {
                alt31=276;
            }
            else if ( ((LA31_0>='\uFDF0' && LA31_0<='\uFDFC')) ) {
                alt31=277;
            }
            else if ( ((LA31_0>='\uFE33' && LA31_0<='\uFE34')) ) {
                alt31=278;
            }
            else if ( ((LA31_0>='\uFE4D' && LA31_0<='\uFE4F')) ) {
                alt31=279;
            }
            else if ( (LA31_0=='\uFE69') ) {
                alt31=280;
            }
            else if ( ((LA31_0>='\uFE70' && LA31_0<='\uFE74')) ) {
                alt31=281;
            }
            else if ( ((LA31_0>='\uFE76' && LA31_0<='\uFEFC')) ) {
                alt31=282;
            }
            else if ( (LA31_0=='\uFF04') ) {
                alt31=283;
            }
            else if ( ((LA31_0>='\uFF21' && LA31_0<='\uFF3A')) ) {
                alt31=284;
            }
            else if ( (LA31_0=='\uFF3F') ) {
                alt31=285;
            }
            else if ( ((LA31_0>='\uFF41' && LA31_0<='\uFF5A')) ) {
                alt31=286;
            }
            else if ( ((LA31_0>='\uFF65' && LA31_0<='\uFFBE')) ) {
                alt31=287;
            }
            else if ( ((LA31_0>='\uFFC2' && LA31_0<='\uFFC7')) ) {
                alt31=288;
            }
            else if ( ((LA31_0>='\uFFCA' && LA31_0<='\uFFCF')) ) {
                alt31=289;
            }
            else if ( ((LA31_0>='\uFFD2' && LA31_0<='\uFFD7')) ) {
                alt31=290;
            }
            else if ( ((LA31_0>='\uFFDA' && LA31_0<='\uFFDC')) ) {
                alt31=291;
            }
            else if ( ((LA31_0>='\uFFE0' && LA31_0<='\uFFE1')) ) {
                alt31=292;
            }
            else if ( ((LA31_0>='\uFFE5' && LA31_0<='\uFFE6')) ) {
                alt31=293;
            }
            else if ( ((LA31_0>='\uD800' && LA31_0<='\uDBFF')) ) {
                alt31=294;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("981:1: fragment IdentifierStart : ( '\\u0024' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u00a2' .. '\\u00a5' | '\\u00aa' | '\\u00b5' | '\\u00ba' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u0236' | '\\u0250' .. '\\u02c1' | '\\u02c6' .. '\\u02d1' | '\\u02e0' .. '\\u02e4' | '\\u02ee' | '\\u037a' | '\\u0386' | '\\u0388' .. '\\u038a' | '\\u038c' | '\\u038e' .. '\\u03a1' | '\\u03a3' .. '\\u03ce' | '\\u03d0' .. '\\u03f5' | '\\u03f7' .. '\\u03fb' | '\\u0400' .. '\\u0481' | '\\u048a' .. '\\u04ce' | '\\u04d0' .. '\\u04f5' | '\\u04f8' .. '\\u04f9' | '\\u0500' .. '\\u050f' | '\\u0531' .. '\\u0556' | '\\u0559' | '\\u0561' .. '\\u0587' | '\\u05d0' .. '\\u05ea' | '\\u05f0' .. '\\u05f2' | '\\u0621' .. '\\u063a' | '\\u0640' .. '\\u064a' | '\\u066e' .. '\\u066f' | '\\u0671' .. '\\u06d3' | '\\u06d5' | '\\u06e5' .. '\\u06e6' | '\\u06ee' .. '\\u06ef' | '\\u06fa' .. '\\u06fc' | '\\u06ff' | '\\u0710' | '\\u0712' .. '\\u072f' | '\\u074d' .. '\\u074f' | '\\u0780' .. '\\u07a5' | '\\u07b1' | '\\u0904' .. '\\u0939' | '\\u093d' | '\\u0950' | '\\u0958' .. '\\u0961' | '\\u0985' .. '\\u098c' | '\\u098f' .. '\\u0990' | '\\u0993' .. '\\u09a8' | '\\u09aa' .. '\\u09b0' | '\\u09b2' | '\\u09b6' .. '\\u09b9' | '\\u09bd' | '\\u09dc' .. '\\u09dd' | '\\u09df' .. '\\u09e1' | '\\u09f0' .. '\\u09f3' | '\\u0a05' .. '\\u0a0a' | '\\u0a0f' .. '\\u0a10' | '\\u0a13' .. '\\u0a28' | '\\u0a2a' .. '\\u0a30' | '\\u0a32' .. '\\u0a33' | '\\u0a35' .. '\\u0a36' | '\\u0a38' .. '\\u0a39' | '\\u0a59' .. '\\u0a5c' | '\\u0a5e' | '\\u0a72' .. '\\u0a74' | '\\u0a85' .. '\\u0a8d' | '\\u0a8f' .. '\\u0a91' | '\\u0a93' .. '\\u0aa8' | '\\u0aaa' .. '\\u0ab0' | '\\u0ab2' .. '\\u0ab3' | '\\u0ab5' .. '\\u0ab9' | '\\u0abd' | '\\u0ad0' | '\\u0ae0' .. '\\u0ae1' | '\\u0af1' | '\\u0b05' .. '\\u0b0c' | '\\u0b0f' .. '\\u0b10' | '\\u0b13' .. '\\u0b28' | '\\u0b2a' .. '\\u0b30' | '\\u0b32' .. '\\u0b33' | '\\u0b35' .. '\\u0b39' | '\\u0b3d' | '\\u0b5c' .. '\\u0b5d' | '\\u0b5f' .. '\\u0b61' | '\\u0b71' | '\\u0b83' | '\\u0b85' .. '\\u0b8a' | '\\u0b8e' .. '\\u0b90' | '\\u0b92' .. '\\u0b95' | '\\u0b99' .. '\\u0b9a' | '\\u0b9c' | '\\u0b9e' .. '\\u0b9f' | '\\u0ba3' .. '\\u0ba4' | '\\u0ba8' .. '\\u0baa' | '\\u0bae' .. '\\u0bb5' | '\\u0bb7' .. '\\u0bb9' | '\\u0bf9' | '\\u0c05' .. '\\u0c0c' | '\\u0c0e' .. '\\u0c10' | '\\u0c12' .. '\\u0c28' | '\\u0c2a' .. '\\u0c33' | '\\u0c35' .. '\\u0c39' | '\\u0c60' .. '\\u0c61' | '\\u0c85' .. '\\u0c8c' | '\\u0c8e' .. '\\u0c90' | '\\u0c92' .. '\\u0ca8' | '\\u0caa' .. '\\u0cb3' | '\\u0cb5' .. '\\u0cb9' | '\\u0cbd' | '\\u0cde' | '\\u0ce0' .. '\\u0ce1' | '\\u0d05' .. '\\u0d0c' | '\\u0d0e' .. '\\u0d10' | '\\u0d12' .. '\\u0d28' | '\\u0d2a' .. '\\u0d39' | '\\u0d60' .. '\\u0d61' | '\\u0d85' .. '\\u0d96' | '\\u0d9a' .. '\\u0db1' | '\\u0db3' .. '\\u0dbb' | '\\u0dbd' | '\\u0dc0' .. '\\u0dc6' | '\\u0e01' .. '\\u0e30' | '\\u0e32' .. '\\u0e33' | '\\u0e3f' .. '\\u0e46' | '\\u0e81' .. '\\u0e82' | '\\u0e84' | '\\u0e87' .. '\\u0e88' | '\\u0e8a' | '\\u0e8d' | '\\u0e94' .. '\\u0e97' | '\\u0e99' .. '\\u0e9f' | '\\u0ea1' .. '\\u0ea3' | '\\u0ea5' | '\\u0ea7' | '\\u0eaa' .. '\\u0eab' | '\\u0ead' .. '\\u0eb0' | '\\u0eb2' .. '\\u0eb3' | '\\u0ebd' | '\\u0ec0' .. '\\u0ec4' | '\\u0ec6' | '\\u0edc' .. '\\u0edd' | '\\u0f00' | '\\u0f40' .. '\\u0f47' | '\\u0f49' .. '\\u0f6a' | '\\u0f88' .. '\\u0f8b' | '\\u1000' .. '\\u1021' | '\\u1023' .. '\\u1027' | '\\u1029' .. '\\u102a' | '\\u1050' .. '\\u1055' | '\\u10a0' .. '\\u10c5' | '\\u10d0' .. '\\u10f8' | '\\u1100' .. '\\u1159' | '\\u115f' .. '\\u11a2' | '\\u11a8' .. '\\u11f9' | '\\u1200' .. '\\u1206' | '\\u1208' .. '\\u1246' | '\\u1248' | '\\u124a' .. '\\u124d' | '\\u1250' .. '\\u1256' | '\\u1258' | '\\u125a' .. '\\u125d' | '\\u1260' .. '\\u1286' | '\\u1288' | '\\u128a' .. '\\u128d' | '\\u1290' .. '\\u12ae' | '\\u12b0' | '\\u12b2' .. '\\u12b5' | '\\u12b8' .. '\\u12be' | '\\u12c0' | '\\u12c2' .. '\\u12c5' | '\\u12c8' .. '\\u12ce' | '\\u12d0' .. '\\u12d6' | '\\u12d8' .. '\\u12ee' | '\\u12f0' .. '\\u130e' | '\\u1310' | '\\u1312' .. '\\u1315' | '\\u1318' .. '\\u131e' | '\\u1320' .. '\\u1346' | '\\u1348' .. '\\u135a' | '\\u13a0' .. '\\u13f4' | '\\u1401' .. '\\u166c' | '\\u166f' .. '\\u1676' | '\\u1681' .. '\\u169a' | '\\u16a0' .. '\\u16ea' | '\\u16ee' .. '\\u16f0' | '\\u1700' .. '\\u170c' | '\\u170e' .. '\\u1711' | '\\u1720' .. '\\u1731' | '\\u1740' .. '\\u1751' | '\\u1760' .. '\\u176c' | '\\u176e' .. '\\u1770' | '\\u1780' .. '\\u17b3' | '\\u17d7' | '\\u17db' .. '\\u17dc' | '\\u1820' .. '\\u1877' | '\\u1880' .. '\\u18a8' | '\\u1900' .. '\\u191c' | '\\u1950' .. '\\u196d' | '\\u1970' .. '\\u1974' | '\\u1d00' .. '\\u1d6b' | '\\u1e00' .. '\\u1e9b' | '\\u1ea0' .. '\\u1ef9' | '\\u1f00' .. '\\u1f15' | '\\u1f18' .. '\\u1f1d' | '\\u1f20' .. '\\u1f45' | '\\u1f48' .. '\\u1f4d' | '\\u1f50' .. '\\u1f57' | '\\u1f59' | '\\u1f5b' | '\\u1f5d' | '\\u1f5f' .. '\\u1f7d' | '\\u1f80' .. '\\u1fb4' | '\\u1fb6' .. '\\u1fbc' | '\\u1fbe' | '\\u1fc2' .. '\\u1fc4' | '\\u1fc6' .. '\\u1fcc' | '\\u1fd0' .. '\\u1fd3' | '\\u1fd6' .. '\\u1fdb' | '\\u1fe0' .. '\\u1fec' | '\\u1ff2' .. '\\u1ff4' | '\\u1ff6' .. '\\u1ffc' | '\\u203f' .. '\\u2040' | '\\u2054' | '\\u2071' | '\\u207f' | '\\u20a0' .. '\\u20b1' | '\\u2102' | '\\u2107' | '\\u210a' .. '\\u2113' | '\\u2115' | '\\u2119' .. '\\u211d' | '\\u2124' | '\\u2126' | '\\u2128' | '\\u212a' .. '\\u212d' | '\\u212f' .. '\\u2131' | '\\u2133' .. '\\u2139' | '\\u213d' .. '\\u213f' | '\\u2145' .. '\\u2149' | '\\u2160' .. '\\u2183' | '\\u3005' .. '\\u3007' | '\\u3021' .. '\\u3029' | '\\u3031' .. '\\u3035' | '\\u3038' .. '\\u303c' | '\\u3041' .. '\\u3096' | '\\u309d' .. '\\u309f' | '\\u30a1' .. '\\u30ff' | '\\u3105' .. '\\u312c' | '\\u3131' .. '\\u318e' | '\\u31a0' .. '\\u31b7' | '\\u31f0' .. '\\u31ff' | '\\u3400' .. '\\u4db5' | '\\u4e00' .. '\\u9fa5' | '\\ua000' .. '\\ua48c' | '\\uac00' .. '\\ud7a3' | '\\uf900' .. '\\ufa2d' | '\\ufa30' .. '\\ufa6a' | '\\ufb00' .. '\\ufb06' | '\\ufb13' .. '\\ufb17' | '\\ufb1d' | '\\ufb1f' .. '\\ufb28' | '\\ufb2a' .. '\\ufb36' | '\\ufb38' .. '\\ufb3c' | '\\ufb3e' | '\\ufb40' .. '\\ufb41' | '\\ufb43' .. '\\ufb44' | '\\ufb46' .. '\\ufbb1' | '\\ufbd3' .. '\\ufd3d' | '\\ufd50' .. '\\ufd8f' | '\\ufd92' .. '\\ufdc7' | '\\ufdf0' .. '\\ufdfc' | '\\ufe33' .. '\\ufe34' | '\\ufe4d' .. '\\ufe4f' | '\\ufe69' | '\\ufe70' .. '\\ufe74' | '\\ufe76' .. '\\ufefc' | '\\uff04' | '\\uff21' .. '\\uff3a' | '\\uff3f' | '\\uff41' .. '\\uff5a' | '\\uff65' .. '\\uffbe' | '\\uffc2' .. '\\uffc7' | '\\uffca' .. '\\uffcf' | '\\uffd2' .. '\\uffd7' | '\\uffda' .. '\\uffdc' | '\\uffe0' .. '\\uffe1' | '\\uffe5' .. '\\uffe6' | ( '\\ud800' .. '\\udbff' ) ( '\\udc00' .. '\\udfff' ) );", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:983:9: '\\u0024'
                    {
                    match('$'); 

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:984:9: '\\u0041' .. '\\u005a'
                    {
                    matchRange('A','Z'); 

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:985:9: '\\u005f'
                    {
                    match('_'); 

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:986:9: '\\u0061' .. '\\u007a'
                    {
                    matchRange('a','z'); 

                    }
                    break;
                case 5 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:987:9: '\\u00a2' .. '\\u00a5'
                    {
                    matchRange('\u00A2','\u00A5'); 

                    }
                    break;
                case 6 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:988:9: '\\u00aa'
                    {
                    match('\u00AA'); 

                    }
                    break;
                case 7 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:989:9: '\\u00b5'
                    {
                    match('\u00B5'); 

                    }
                    break;
                case 8 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:990:9: '\\u00ba'
                    {
                    match('\u00BA'); 

                    }
                    break;
                case 9 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:991:9: '\\u00c0' .. '\\u00d6'
                    {
                    matchRange('\u00C0','\u00D6'); 

                    }
                    break;
                case 10 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:992:9: '\\u00d8' .. '\\u00f6'
                    {
                    matchRange('\u00D8','\u00F6'); 

                    }
                    break;
                case 11 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:993:9: '\\u00f8' .. '\\u0236'
                    {
                    matchRange('\u00F8','\u0236'); 

                    }
                    break;
                case 12 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:994:9: '\\u0250' .. '\\u02c1'
                    {
                    matchRange('\u0250','\u02C1'); 

                    }
                    break;
                case 13 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:995:9: '\\u02c6' .. '\\u02d1'
                    {
                    matchRange('\u02C6','\u02D1'); 

                    }
                    break;
                case 14 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:996:9: '\\u02e0' .. '\\u02e4'
                    {
                    matchRange('\u02E0','\u02E4'); 

                    }
                    break;
                case 15 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:997:9: '\\u02ee'
                    {
                    match('\u02EE'); 

                    }
                    break;
                case 16 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:998:9: '\\u037a'
                    {
                    match('\u037A'); 

                    }
                    break;
                case 17 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:999:9: '\\u0386'
                    {
                    match('\u0386'); 

                    }
                    break;
                case 18 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1000:9: '\\u0388' .. '\\u038a'
                    {
                    matchRange('\u0388','\u038A'); 

                    }
                    break;
                case 19 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1001:9: '\\u038c'
                    {
                    match('\u038C'); 

                    }
                    break;
                case 20 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1002:9: '\\u038e' .. '\\u03a1'
                    {
                    matchRange('\u038E','\u03A1'); 

                    }
                    break;
                case 21 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1003:9: '\\u03a3' .. '\\u03ce'
                    {
                    matchRange('\u03A3','\u03CE'); 

                    }
                    break;
                case 22 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1004:9: '\\u03d0' .. '\\u03f5'
                    {
                    matchRange('\u03D0','\u03F5'); 

                    }
                    break;
                case 23 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1005:9: '\\u03f7' .. '\\u03fb'
                    {
                    matchRange('\u03F7','\u03FB'); 

                    }
                    break;
                case 24 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1006:9: '\\u0400' .. '\\u0481'
                    {
                    matchRange('\u0400','\u0481'); 

                    }
                    break;
                case 25 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1007:9: '\\u048a' .. '\\u04ce'
                    {
                    matchRange('\u048A','\u04CE'); 

                    }
                    break;
                case 26 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1008:9: '\\u04d0' .. '\\u04f5'
                    {
                    matchRange('\u04D0','\u04F5'); 

                    }
                    break;
                case 27 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1009:9: '\\u04f8' .. '\\u04f9'
                    {
                    matchRange('\u04F8','\u04F9'); 

                    }
                    break;
                case 28 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1010:9: '\\u0500' .. '\\u050f'
                    {
                    matchRange('\u0500','\u050F'); 

                    }
                    break;
                case 29 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1011:9: '\\u0531' .. '\\u0556'
                    {
                    matchRange('\u0531','\u0556'); 

                    }
                    break;
                case 30 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1012:9: '\\u0559'
                    {
                    match('\u0559'); 

                    }
                    break;
                case 31 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1013:9: '\\u0561' .. '\\u0587'
                    {
                    matchRange('\u0561','\u0587'); 

                    }
                    break;
                case 32 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1014:9: '\\u05d0' .. '\\u05ea'
                    {
                    matchRange('\u05D0','\u05EA'); 

                    }
                    break;
                case 33 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1015:9: '\\u05f0' .. '\\u05f2'
                    {
                    matchRange('\u05F0','\u05F2'); 

                    }
                    break;
                case 34 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1016:9: '\\u0621' .. '\\u063a'
                    {
                    matchRange('\u0621','\u063A'); 

                    }
                    break;
                case 35 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1017:9: '\\u0640' .. '\\u064a'
                    {
                    matchRange('\u0640','\u064A'); 

                    }
                    break;
                case 36 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1018:9: '\\u066e' .. '\\u066f'
                    {
                    matchRange('\u066E','\u066F'); 

                    }
                    break;
                case 37 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1019:9: '\\u0671' .. '\\u06d3'
                    {
                    matchRange('\u0671','\u06D3'); 

                    }
                    break;
                case 38 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1020:9: '\\u06d5'
                    {
                    match('\u06D5'); 

                    }
                    break;
                case 39 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1021:9: '\\u06e5' .. '\\u06e6'
                    {
                    matchRange('\u06E5','\u06E6'); 

                    }
                    break;
                case 40 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1022:9: '\\u06ee' .. '\\u06ef'
                    {
                    matchRange('\u06EE','\u06EF'); 

                    }
                    break;
                case 41 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1023:9: '\\u06fa' .. '\\u06fc'
                    {
                    matchRange('\u06FA','\u06FC'); 

                    }
                    break;
                case 42 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1024:9: '\\u06ff'
                    {
                    match('\u06FF'); 

                    }
                    break;
                case 43 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1025:9: '\\u0710'
                    {
                    match('\u0710'); 

                    }
                    break;
                case 44 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1026:9: '\\u0712' .. '\\u072f'
                    {
                    matchRange('\u0712','\u072F'); 

                    }
                    break;
                case 45 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1027:9: '\\u074d' .. '\\u074f'
                    {
                    matchRange('\u074D','\u074F'); 

                    }
                    break;
                case 46 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1028:9: '\\u0780' .. '\\u07a5'
                    {
                    matchRange('\u0780','\u07A5'); 

                    }
                    break;
                case 47 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1029:9: '\\u07b1'
                    {
                    match('\u07B1'); 

                    }
                    break;
                case 48 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1030:9: '\\u0904' .. '\\u0939'
                    {
                    matchRange('\u0904','\u0939'); 

                    }
                    break;
                case 49 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1031:9: '\\u093d'
                    {
                    match('\u093D'); 

                    }
                    break;
                case 50 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1032:9: '\\u0950'
                    {
                    match('\u0950'); 

                    }
                    break;
                case 51 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1033:9: '\\u0958' .. '\\u0961'
                    {
                    matchRange('\u0958','\u0961'); 

                    }
                    break;
                case 52 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1034:9: '\\u0985' .. '\\u098c'
                    {
                    matchRange('\u0985','\u098C'); 

                    }
                    break;
                case 53 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1035:9: '\\u098f' .. '\\u0990'
                    {
                    matchRange('\u098F','\u0990'); 

                    }
                    break;
                case 54 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1036:9: '\\u0993' .. '\\u09a8'
                    {
                    matchRange('\u0993','\u09A8'); 

                    }
                    break;
                case 55 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1037:9: '\\u09aa' .. '\\u09b0'
                    {
                    matchRange('\u09AA','\u09B0'); 

                    }
                    break;
                case 56 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1038:9: '\\u09b2'
                    {
                    match('\u09B2'); 

                    }
                    break;
                case 57 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1039:9: '\\u09b6' .. '\\u09b9'
                    {
                    matchRange('\u09B6','\u09B9'); 

                    }
                    break;
                case 58 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1040:9: '\\u09bd'
                    {
                    match('\u09BD'); 

                    }
                    break;
                case 59 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1041:9: '\\u09dc' .. '\\u09dd'
                    {
                    matchRange('\u09DC','\u09DD'); 

                    }
                    break;
                case 60 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1042:9: '\\u09df' .. '\\u09e1'
                    {
                    matchRange('\u09DF','\u09E1'); 

                    }
                    break;
                case 61 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1043:9: '\\u09f0' .. '\\u09f3'
                    {
                    matchRange('\u09F0','\u09F3'); 

                    }
                    break;
                case 62 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1044:9: '\\u0a05' .. '\\u0a0a'
                    {
                    matchRange('\u0A05','\u0A0A'); 

                    }
                    break;
                case 63 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1045:9: '\\u0a0f' .. '\\u0a10'
                    {
                    matchRange('\u0A0F','\u0A10'); 

                    }
                    break;
                case 64 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1046:9: '\\u0a13' .. '\\u0a28'
                    {
                    matchRange('\u0A13','\u0A28'); 

                    }
                    break;
                case 65 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1047:9: '\\u0a2a' .. '\\u0a30'
                    {
                    matchRange('\u0A2A','\u0A30'); 

                    }
                    break;
                case 66 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1048:9: '\\u0a32' .. '\\u0a33'
                    {
                    matchRange('\u0A32','\u0A33'); 

                    }
                    break;
                case 67 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1049:9: '\\u0a35' .. '\\u0a36'
                    {
                    matchRange('\u0A35','\u0A36'); 

                    }
                    break;
                case 68 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1050:9: '\\u0a38' .. '\\u0a39'
                    {
                    matchRange('\u0A38','\u0A39'); 

                    }
                    break;
                case 69 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1051:9: '\\u0a59' .. '\\u0a5c'
                    {
                    matchRange('\u0A59','\u0A5C'); 

                    }
                    break;
                case 70 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1052:9: '\\u0a5e'
                    {
                    match('\u0A5E'); 

                    }
                    break;
                case 71 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1053:9: '\\u0a72' .. '\\u0a74'
                    {
                    matchRange('\u0A72','\u0A74'); 

                    }
                    break;
                case 72 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1054:9: '\\u0a85' .. '\\u0a8d'
                    {
                    matchRange('\u0A85','\u0A8D'); 

                    }
                    break;
                case 73 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1055:9: '\\u0a8f' .. '\\u0a91'
                    {
                    matchRange('\u0A8F','\u0A91'); 

                    }
                    break;
                case 74 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1056:9: '\\u0a93' .. '\\u0aa8'
                    {
                    matchRange('\u0A93','\u0AA8'); 

                    }
                    break;
                case 75 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1057:9: '\\u0aaa' .. '\\u0ab0'
                    {
                    matchRange('\u0AAA','\u0AB0'); 

                    }
                    break;
                case 76 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1058:9: '\\u0ab2' .. '\\u0ab3'
                    {
                    matchRange('\u0AB2','\u0AB3'); 

                    }
                    break;
                case 77 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1059:9: '\\u0ab5' .. '\\u0ab9'
                    {
                    matchRange('\u0AB5','\u0AB9'); 

                    }
                    break;
                case 78 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1060:9: '\\u0abd'
                    {
                    match('\u0ABD'); 

                    }
                    break;
                case 79 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1061:9: '\\u0ad0'
                    {
                    match('\u0AD0'); 

                    }
                    break;
                case 80 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1062:9: '\\u0ae0' .. '\\u0ae1'
                    {
                    matchRange('\u0AE0','\u0AE1'); 

                    }
                    break;
                case 81 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1063:9: '\\u0af1'
                    {
                    match('\u0AF1'); 

                    }
                    break;
                case 82 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1064:9: '\\u0b05' .. '\\u0b0c'
                    {
                    matchRange('\u0B05','\u0B0C'); 

                    }
                    break;
                case 83 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1065:9: '\\u0b0f' .. '\\u0b10'
                    {
                    matchRange('\u0B0F','\u0B10'); 

                    }
                    break;
                case 84 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1066:9: '\\u0b13' .. '\\u0b28'
                    {
                    matchRange('\u0B13','\u0B28'); 

                    }
                    break;
                case 85 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1067:9: '\\u0b2a' .. '\\u0b30'
                    {
                    matchRange('\u0B2A','\u0B30'); 

                    }
                    break;
                case 86 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1068:9: '\\u0b32' .. '\\u0b33'
                    {
                    matchRange('\u0B32','\u0B33'); 

                    }
                    break;
                case 87 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1069:9: '\\u0b35' .. '\\u0b39'
                    {
                    matchRange('\u0B35','\u0B39'); 

                    }
                    break;
                case 88 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1070:9: '\\u0b3d'
                    {
                    match('\u0B3D'); 

                    }
                    break;
                case 89 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1071:9: '\\u0b5c' .. '\\u0b5d'
                    {
                    matchRange('\u0B5C','\u0B5D'); 

                    }
                    break;
                case 90 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1072:9: '\\u0b5f' .. '\\u0b61'
                    {
                    matchRange('\u0B5F','\u0B61'); 

                    }
                    break;
                case 91 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1073:9: '\\u0b71'
                    {
                    match('\u0B71'); 

                    }
                    break;
                case 92 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1074:9: '\\u0b83'
                    {
                    match('\u0B83'); 

                    }
                    break;
                case 93 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1075:9: '\\u0b85' .. '\\u0b8a'
                    {
                    matchRange('\u0B85','\u0B8A'); 

                    }
                    break;
                case 94 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1076:9: '\\u0b8e' .. '\\u0b90'
                    {
                    matchRange('\u0B8E','\u0B90'); 

                    }
                    break;
                case 95 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1077:9: '\\u0b92' .. '\\u0b95'
                    {
                    matchRange('\u0B92','\u0B95'); 

                    }
                    break;
                case 96 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1078:9: '\\u0b99' .. '\\u0b9a'
                    {
                    matchRange('\u0B99','\u0B9A'); 

                    }
                    break;
                case 97 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1079:9: '\\u0b9c'
                    {
                    match('\u0B9C'); 

                    }
                    break;
                case 98 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1080:9: '\\u0b9e' .. '\\u0b9f'
                    {
                    matchRange('\u0B9E','\u0B9F'); 

                    }
                    break;
                case 99 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1081:9: '\\u0ba3' .. '\\u0ba4'
                    {
                    matchRange('\u0BA3','\u0BA4'); 

                    }
                    break;
                case 100 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1082:9: '\\u0ba8' .. '\\u0baa'
                    {
                    matchRange('\u0BA8','\u0BAA'); 

                    }
                    break;
                case 101 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1083:9: '\\u0bae' .. '\\u0bb5'
                    {
                    matchRange('\u0BAE','\u0BB5'); 

                    }
                    break;
                case 102 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1084:9: '\\u0bb7' .. '\\u0bb9'
                    {
                    matchRange('\u0BB7','\u0BB9'); 

                    }
                    break;
                case 103 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1085:9: '\\u0bf9'
                    {
                    match('\u0BF9'); 

                    }
                    break;
                case 104 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1086:9: '\\u0c05' .. '\\u0c0c'
                    {
                    matchRange('\u0C05','\u0C0C'); 

                    }
                    break;
                case 105 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1087:9: '\\u0c0e' .. '\\u0c10'
                    {
                    matchRange('\u0C0E','\u0C10'); 

                    }
                    break;
                case 106 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1088:9: '\\u0c12' .. '\\u0c28'
                    {
                    matchRange('\u0C12','\u0C28'); 

                    }
                    break;
                case 107 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1089:9: '\\u0c2a' .. '\\u0c33'
                    {
                    matchRange('\u0C2A','\u0C33'); 

                    }
                    break;
                case 108 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1090:9: '\\u0c35' .. '\\u0c39'
                    {
                    matchRange('\u0C35','\u0C39'); 

                    }
                    break;
                case 109 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1091:9: '\\u0c60' .. '\\u0c61'
                    {
                    matchRange('\u0C60','\u0C61'); 

                    }
                    break;
                case 110 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1092:9: '\\u0c85' .. '\\u0c8c'
                    {
                    matchRange('\u0C85','\u0C8C'); 

                    }
                    break;
                case 111 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1093:9: '\\u0c8e' .. '\\u0c90'
                    {
                    matchRange('\u0C8E','\u0C90'); 

                    }
                    break;
                case 112 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1094:9: '\\u0c92' .. '\\u0ca8'
                    {
                    matchRange('\u0C92','\u0CA8'); 

                    }
                    break;
                case 113 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1095:9: '\\u0caa' .. '\\u0cb3'
                    {
                    matchRange('\u0CAA','\u0CB3'); 

                    }
                    break;
                case 114 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1096:9: '\\u0cb5' .. '\\u0cb9'
                    {
                    matchRange('\u0CB5','\u0CB9'); 

                    }
                    break;
                case 115 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1097:9: '\\u0cbd'
                    {
                    match('\u0CBD'); 

                    }
                    break;
                case 116 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1098:9: '\\u0cde'
                    {
                    match('\u0CDE'); 

                    }
                    break;
                case 117 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1099:9: '\\u0ce0' .. '\\u0ce1'
                    {
                    matchRange('\u0CE0','\u0CE1'); 

                    }
                    break;
                case 118 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1100:9: '\\u0d05' .. '\\u0d0c'
                    {
                    matchRange('\u0D05','\u0D0C'); 

                    }
                    break;
                case 119 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1101:9: '\\u0d0e' .. '\\u0d10'
                    {
                    matchRange('\u0D0E','\u0D10'); 

                    }
                    break;
                case 120 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1102:9: '\\u0d12' .. '\\u0d28'
                    {
                    matchRange('\u0D12','\u0D28'); 

                    }
                    break;
                case 121 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1103:9: '\\u0d2a' .. '\\u0d39'
                    {
                    matchRange('\u0D2A','\u0D39'); 

                    }
                    break;
                case 122 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1104:9: '\\u0d60' .. '\\u0d61'
                    {
                    matchRange('\u0D60','\u0D61'); 

                    }
                    break;
                case 123 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1105:9: '\\u0d85' .. '\\u0d96'
                    {
                    matchRange('\u0D85','\u0D96'); 

                    }
                    break;
                case 124 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1106:9: '\\u0d9a' .. '\\u0db1'
                    {
                    matchRange('\u0D9A','\u0DB1'); 

                    }
                    break;
                case 125 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1107:9: '\\u0db3' .. '\\u0dbb'
                    {
                    matchRange('\u0DB3','\u0DBB'); 

                    }
                    break;
                case 126 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1108:9: '\\u0dbd'
                    {
                    match('\u0DBD'); 

                    }
                    break;
                case 127 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1109:9: '\\u0dc0' .. '\\u0dc6'
                    {
                    matchRange('\u0DC0','\u0DC6'); 

                    }
                    break;
                case 128 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1110:9: '\\u0e01' .. '\\u0e30'
                    {
                    matchRange('\u0E01','\u0E30'); 

                    }
                    break;
                case 129 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1111:9: '\\u0e32' .. '\\u0e33'
                    {
                    matchRange('\u0E32','\u0E33'); 

                    }
                    break;
                case 130 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1112:9: '\\u0e3f' .. '\\u0e46'
                    {
                    matchRange('\u0E3F','\u0E46'); 

                    }
                    break;
                case 131 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1113:9: '\\u0e81' .. '\\u0e82'
                    {
                    matchRange('\u0E81','\u0E82'); 

                    }
                    break;
                case 132 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1114:9: '\\u0e84'
                    {
                    match('\u0E84'); 

                    }
                    break;
                case 133 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1115:9: '\\u0e87' .. '\\u0e88'
                    {
                    matchRange('\u0E87','\u0E88'); 

                    }
                    break;
                case 134 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1116:9: '\\u0e8a'
                    {
                    match('\u0E8A'); 

                    }
                    break;
                case 135 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1117:9: '\\u0e8d'
                    {
                    match('\u0E8D'); 

                    }
                    break;
                case 136 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1118:9: '\\u0e94' .. '\\u0e97'
                    {
                    matchRange('\u0E94','\u0E97'); 

                    }
                    break;
                case 137 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1119:9: '\\u0e99' .. '\\u0e9f'
                    {
                    matchRange('\u0E99','\u0E9F'); 

                    }
                    break;
                case 138 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1120:9: '\\u0ea1' .. '\\u0ea3'
                    {
                    matchRange('\u0EA1','\u0EA3'); 

                    }
                    break;
                case 139 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1121:9: '\\u0ea5'
                    {
                    match('\u0EA5'); 

                    }
                    break;
                case 140 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1122:9: '\\u0ea7'
                    {
                    match('\u0EA7'); 

                    }
                    break;
                case 141 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1123:9: '\\u0eaa' .. '\\u0eab'
                    {
                    matchRange('\u0EAA','\u0EAB'); 

                    }
                    break;
                case 142 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1124:9: '\\u0ead' .. '\\u0eb0'
                    {
                    matchRange('\u0EAD','\u0EB0'); 

                    }
                    break;
                case 143 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1125:9: '\\u0eb2' .. '\\u0eb3'
                    {
                    matchRange('\u0EB2','\u0EB3'); 

                    }
                    break;
                case 144 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1126:9: '\\u0ebd'
                    {
                    match('\u0EBD'); 

                    }
                    break;
                case 145 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1127:9: '\\u0ec0' .. '\\u0ec4'
                    {
                    matchRange('\u0EC0','\u0EC4'); 

                    }
                    break;
                case 146 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1128:9: '\\u0ec6'
                    {
                    match('\u0EC6'); 

                    }
                    break;
                case 147 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1129:9: '\\u0edc' .. '\\u0edd'
                    {
                    matchRange('\u0EDC','\u0EDD'); 

                    }
                    break;
                case 148 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1130:9: '\\u0f00'
                    {
                    match('\u0F00'); 

                    }
                    break;
                case 149 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1131:9: '\\u0f40' .. '\\u0f47'
                    {
                    matchRange('\u0F40','\u0F47'); 

                    }
                    break;
                case 150 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1132:9: '\\u0f49' .. '\\u0f6a'
                    {
                    matchRange('\u0F49','\u0F6A'); 

                    }
                    break;
                case 151 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1133:9: '\\u0f88' .. '\\u0f8b'
                    {
                    matchRange('\u0F88','\u0F8B'); 

                    }
                    break;
                case 152 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1134:9: '\\u1000' .. '\\u1021'
                    {
                    matchRange('\u1000','\u1021'); 

                    }
                    break;
                case 153 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1135:9: '\\u1023' .. '\\u1027'
                    {
                    matchRange('\u1023','\u1027'); 

                    }
                    break;
                case 154 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1136:9: '\\u1029' .. '\\u102a'
                    {
                    matchRange('\u1029','\u102A'); 

                    }
                    break;
                case 155 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1137:9: '\\u1050' .. '\\u1055'
                    {
                    matchRange('\u1050','\u1055'); 

                    }
                    break;
                case 156 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1138:9: '\\u10a0' .. '\\u10c5'
                    {
                    matchRange('\u10A0','\u10C5'); 

                    }
                    break;
                case 157 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1139:9: '\\u10d0' .. '\\u10f8'
                    {
                    matchRange('\u10D0','\u10F8'); 

                    }
                    break;
                case 158 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1140:9: '\\u1100' .. '\\u1159'
                    {
                    matchRange('\u1100','\u1159'); 

                    }
                    break;
                case 159 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1141:9: '\\u115f' .. '\\u11a2'
                    {
                    matchRange('\u115F','\u11A2'); 

                    }
                    break;
                case 160 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1142:9: '\\u11a8' .. '\\u11f9'
                    {
                    matchRange('\u11A8','\u11F9'); 

                    }
                    break;
                case 161 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1143:9: '\\u1200' .. '\\u1206'
                    {
                    matchRange('\u1200','\u1206'); 

                    }
                    break;
                case 162 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1144:9: '\\u1208' .. '\\u1246'
                    {
                    matchRange('\u1208','\u1246'); 

                    }
                    break;
                case 163 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1145:9: '\\u1248'
                    {
                    match('\u1248'); 

                    }
                    break;
                case 164 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1146:9: '\\u124a' .. '\\u124d'
                    {
                    matchRange('\u124A','\u124D'); 

                    }
                    break;
                case 165 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1147:9: '\\u1250' .. '\\u1256'
                    {
                    matchRange('\u1250','\u1256'); 

                    }
                    break;
                case 166 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1148:9: '\\u1258'
                    {
                    match('\u1258'); 

                    }
                    break;
                case 167 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1149:9: '\\u125a' .. '\\u125d'
                    {
                    matchRange('\u125A','\u125D'); 

                    }
                    break;
                case 168 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1150:9: '\\u1260' .. '\\u1286'
                    {
                    matchRange('\u1260','\u1286'); 

                    }
                    break;
                case 169 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1151:9: '\\u1288'
                    {
                    match('\u1288'); 

                    }
                    break;
                case 170 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1152:9: '\\u128a' .. '\\u128d'
                    {
                    matchRange('\u128A','\u128D'); 

                    }
                    break;
                case 171 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1153:9: '\\u1290' .. '\\u12ae'
                    {
                    matchRange('\u1290','\u12AE'); 

                    }
                    break;
                case 172 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1154:9: '\\u12b0'
                    {
                    match('\u12B0'); 

                    }
                    break;
                case 173 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1155:9: '\\u12b2' .. '\\u12b5'
                    {
                    matchRange('\u12B2','\u12B5'); 

                    }
                    break;
                case 174 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1156:9: '\\u12b8' .. '\\u12be'
                    {
                    matchRange('\u12B8','\u12BE'); 

                    }
                    break;
                case 175 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1157:9: '\\u12c0'
                    {
                    match('\u12C0'); 

                    }
                    break;
                case 176 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1158:9: '\\u12c2' .. '\\u12c5'
                    {
                    matchRange('\u12C2','\u12C5'); 

                    }
                    break;
                case 177 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1159:9: '\\u12c8' .. '\\u12ce'
                    {
                    matchRange('\u12C8','\u12CE'); 

                    }
                    break;
                case 178 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1160:9: '\\u12d0' .. '\\u12d6'
                    {
                    matchRange('\u12D0','\u12D6'); 

                    }
                    break;
                case 179 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1161:9: '\\u12d8' .. '\\u12ee'
                    {
                    matchRange('\u12D8','\u12EE'); 

                    }
                    break;
                case 180 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1162:9: '\\u12f0' .. '\\u130e'
                    {
                    matchRange('\u12F0','\u130E'); 

                    }
                    break;
                case 181 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1163:9: '\\u1310'
                    {
                    match('\u1310'); 

                    }
                    break;
                case 182 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1164:9: '\\u1312' .. '\\u1315'
                    {
                    matchRange('\u1312','\u1315'); 

                    }
                    break;
                case 183 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1165:9: '\\u1318' .. '\\u131e'
                    {
                    matchRange('\u1318','\u131E'); 

                    }
                    break;
                case 184 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1166:9: '\\u1320' .. '\\u1346'
                    {
                    matchRange('\u1320','\u1346'); 

                    }
                    break;
                case 185 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1167:9: '\\u1348' .. '\\u135a'
                    {
                    matchRange('\u1348','\u135A'); 

                    }
                    break;
                case 186 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1168:9: '\\u13a0' .. '\\u13f4'
                    {
                    matchRange('\u13A0','\u13F4'); 

                    }
                    break;
                case 187 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1169:9: '\\u1401' .. '\\u166c'
                    {
                    matchRange('\u1401','\u166C'); 

                    }
                    break;
                case 188 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1170:9: '\\u166f' .. '\\u1676'
                    {
                    matchRange('\u166F','\u1676'); 

                    }
                    break;
                case 189 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1171:9: '\\u1681' .. '\\u169a'
                    {
                    matchRange('\u1681','\u169A'); 

                    }
                    break;
                case 190 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1172:9: '\\u16a0' .. '\\u16ea'
                    {
                    matchRange('\u16A0','\u16EA'); 

                    }
                    break;
                case 191 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1173:9: '\\u16ee' .. '\\u16f0'
                    {
                    matchRange('\u16EE','\u16F0'); 

                    }
                    break;
                case 192 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1174:9: '\\u1700' .. '\\u170c'
                    {
                    matchRange('\u1700','\u170C'); 

                    }
                    break;
                case 193 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1175:9: '\\u170e' .. '\\u1711'
                    {
                    matchRange('\u170E','\u1711'); 

                    }
                    break;
                case 194 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1176:9: '\\u1720' .. '\\u1731'
                    {
                    matchRange('\u1720','\u1731'); 

                    }
                    break;
                case 195 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1177:9: '\\u1740' .. '\\u1751'
                    {
                    matchRange('\u1740','\u1751'); 

                    }
                    break;
                case 196 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1178:9: '\\u1760' .. '\\u176c'
                    {
                    matchRange('\u1760','\u176C'); 

                    }
                    break;
                case 197 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1179:9: '\\u176e' .. '\\u1770'
                    {
                    matchRange('\u176E','\u1770'); 

                    }
                    break;
                case 198 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1180:9: '\\u1780' .. '\\u17b3'
                    {
                    matchRange('\u1780','\u17B3'); 

                    }
                    break;
                case 199 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1181:9: '\\u17d7'
                    {
                    match('\u17D7'); 

                    }
                    break;
                case 200 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1182:9: '\\u17db' .. '\\u17dc'
                    {
                    matchRange('\u17DB','\u17DC'); 

                    }
                    break;
                case 201 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1183:9: '\\u1820' .. '\\u1877'
                    {
                    matchRange('\u1820','\u1877'); 

                    }
                    break;
                case 202 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1184:9: '\\u1880' .. '\\u18a8'
                    {
                    matchRange('\u1880','\u18A8'); 

                    }
                    break;
                case 203 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1185:9: '\\u1900' .. '\\u191c'
                    {
                    matchRange('\u1900','\u191C'); 

                    }
                    break;
                case 204 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1186:9: '\\u1950' .. '\\u196d'
                    {
                    matchRange('\u1950','\u196D'); 

                    }
                    break;
                case 205 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1187:9: '\\u1970' .. '\\u1974'
                    {
                    matchRange('\u1970','\u1974'); 

                    }
                    break;
                case 206 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1188:9: '\\u1d00' .. '\\u1d6b'
                    {
                    matchRange('\u1D00','\u1D6B'); 

                    }
                    break;
                case 207 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1189:9: '\\u1e00' .. '\\u1e9b'
                    {
                    matchRange('\u1E00','\u1E9B'); 

                    }
                    break;
                case 208 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1190:9: '\\u1ea0' .. '\\u1ef9'
                    {
                    matchRange('\u1EA0','\u1EF9'); 

                    }
                    break;
                case 209 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1191:9: '\\u1f00' .. '\\u1f15'
                    {
                    matchRange('\u1F00','\u1F15'); 

                    }
                    break;
                case 210 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1192:9: '\\u1f18' .. '\\u1f1d'
                    {
                    matchRange('\u1F18','\u1F1D'); 

                    }
                    break;
                case 211 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1193:9: '\\u1f20' .. '\\u1f45'
                    {
                    matchRange('\u1F20','\u1F45'); 

                    }
                    break;
                case 212 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1194:9: '\\u1f48' .. '\\u1f4d'
                    {
                    matchRange('\u1F48','\u1F4D'); 

                    }
                    break;
                case 213 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1195:9: '\\u1f50' .. '\\u1f57'
                    {
                    matchRange('\u1F50','\u1F57'); 

                    }
                    break;
                case 214 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1196:9: '\\u1f59'
                    {
                    match('\u1F59'); 

                    }
                    break;
                case 215 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1197:9: '\\u1f5b'
                    {
                    match('\u1F5B'); 

                    }
                    break;
                case 216 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1198:9: '\\u1f5d'
                    {
                    match('\u1F5D'); 

                    }
                    break;
                case 217 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1199:9: '\\u1f5f' .. '\\u1f7d'
                    {
                    matchRange('\u1F5F','\u1F7D'); 

                    }
                    break;
                case 218 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1200:9: '\\u1f80' .. '\\u1fb4'
                    {
                    matchRange('\u1F80','\u1FB4'); 

                    }
                    break;
                case 219 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1201:9: '\\u1fb6' .. '\\u1fbc'
                    {
                    matchRange('\u1FB6','\u1FBC'); 

                    }
                    break;
                case 220 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1202:9: '\\u1fbe'
                    {
                    match('\u1FBE'); 

                    }
                    break;
                case 221 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1203:9: '\\u1fc2' .. '\\u1fc4'
                    {
                    matchRange('\u1FC2','\u1FC4'); 

                    }
                    break;
                case 222 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1204:9: '\\u1fc6' .. '\\u1fcc'
                    {
                    matchRange('\u1FC6','\u1FCC'); 

                    }
                    break;
                case 223 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1205:9: '\\u1fd0' .. '\\u1fd3'
                    {
                    matchRange('\u1FD0','\u1FD3'); 

                    }
                    break;
                case 224 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1206:9: '\\u1fd6' .. '\\u1fdb'
                    {
                    matchRange('\u1FD6','\u1FDB'); 

                    }
                    break;
                case 225 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1207:9: '\\u1fe0' .. '\\u1fec'
                    {
                    matchRange('\u1FE0','\u1FEC'); 

                    }
                    break;
                case 226 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1208:9: '\\u1ff2' .. '\\u1ff4'
                    {
                    matchRange('\u1FF2','\u1FF4'); 

                    }
                    break;
                case 227 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1209:9: '\\u1ff6' .. '\\u1ffc'
                    {
                    matchRange('\u1FF6','\u1FFC'); 

                    }
                    break;
                case 228 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1210:9: '\\u203f' .. '\\u2040'
                    {
                    matchRange('\u203F','\u2040'); 

                    }
                    break;
                case 229 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1211:9: '\\u2054'
                    {
                    match('\u2054'); 

                    }
                    break;
                case 230 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1212:9: '\\u2071'
                    {
                    match('\u2071'); 

                    }
                    break;
                case 231 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1213:9: '\\u207f'
                    {
                    match('\u207F'); 

                    }
                    break;
                case 232 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1214:9: '\\u20a0' .. '\\u20b1'
                    {
                    matchRange('\u20A0','\u20B1'); 

                    }
                    break;
                case 233 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1215:9: '\\u2102'
                    {
                    match('\u2102'); 

                    }
                    break;
                case 234 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1216:9: '\\u2107'
                    {
                    match('\u2107'); 

                    }
                    break;
                case 235 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1217:9: '\\u210a' .. '\\u2113'
                    {
                    matchRange('\u210A','\u2113'); 

                    }
                    break;
                case 236 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1218:9: '\\u2115'
                    {
                    match('\u2115'); 

                    }
                    break;
                case 237 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1219:9: '\\u2119' .. '\\u211d'
                    {
                    matchRange('\u2119','\u211D'); 

                    }
                    break;
                case 238 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1220:9: '\\u2124'
                    {
                    match('\u2124'); 

                    }
                    break;
                case 239 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1221:9: '\\u2126'
                    {
                    match('\u2126'); 

                    }
                    break;
                case 240 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1222:9: '\\u2128'
                    {
                    match('\u2128'); 

                    }
                    break;
                case 241 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1223:9: '\\u212a' .. '\\u212d'
                    {
                    matchRange('\u212A','\u212D'); 

                    }
                    break;
                case 242 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1224:9: '\\u212f' .. '\\u2131'
                    {
                    matchRange('\u212F','\u2131'); 

                    }
                    break;
                case 243 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1225:9: '\\u2133' .. '\\u2139'
                    {
                    matchRange('\u2133','\u2139'); 

                    }
                    break;
                case 244 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1226:9: '\\u213d' .. '\\u213f'
                    {
                    matchRange('\u213D','\u213F'); 

                    }
                    break;
                case 245 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1227:9: '\\u2145' .. '\\u2149'
                    {
                    matchRange('\u2145','\u2149'); 

                    }
                    break;
                case 246 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1228:9: '\\u2160' .. '\\u2183'
                    {
                    matchRange('\u2160','\u2183'); 

                    }
                    break;
                case 247 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1229:9: '\\u3005' .. '\\u3007'
                    {
                    matchRange('\u3005','\u3007'); 

                    }
                    break;
                case 248 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1230:9: '\\u3021' .. '\\u3029'
                    {
                    matchRange('\u3021','\u3029'); 

                    }
                    break;
                case 249 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1231:9: '\\u3031' .. '\\u3035'
                    {
                    matchRange('\u3031','\u3035'); 

                    }
                    break;
                case 250 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1232:9: '\\u3038' .. '\\u303c'
                    {
                    matchRange('\u3038','\u303C'); 

                    }
                    break;
                case 251 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1233:9: '\\u3041' .. '\\u3096'
                    {
                    matchRange('\u3041','\u3096'); 

                    }
                    break;
                case 252 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1234:9: '\\u309d' .. '\\u309f'
                    {
                    matchRange('\u309D','\u309F'); 

                    }
                    break;
                case 253 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1235:9: '\\u30a1' .. '\\u30ff'
                    {
                    matchRange('\u30A1','\u30FF'); 

                    }
                    break;
                case 254 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1236:9: '\\u3105' .. '\\u312c'
                    {
                    matchRange('\u3105','\u312C'); 

                    }
                    break;
                case 255 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1237:9: '\\u3131' .. '\\u318e'
                    {
                    matchRange('\u3131','\u318E'); 

                    }
                    break;
                case 256 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1238:9: '\\u31a0' .. '\\u31b7'
                    {
                    matchRange('\u31A0','\u31B7'); 

                    }
                    break;
                case 257 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1239:9: '\\u31f0' .. '\\u31ff'
                    {
                    matchRange('\u31F0','\u31FF'); 

                    }
                    break;
                case 258 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1240:9: '\\u3400' .. '\\u4db5'
                    {
                    matchRange('\u3400','\u4DB5'); 

                    }
                    break;
                case 259 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1241:9: '\\u4e00' .. '\\u9fa5'
                    {
                    matchRange('\u4E00','\u9FA5'); 

                    }
                    break;
                case 260 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1242:9: '\\ua000' .. '\\ua48c'
                    {
                    matchRange('\uA000','\uA48C'); 

                    }
                    break;
                case 261 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1243:9: '\\uac00' .. '\\ud7a3'
                    {
                    matchRange('\uAC00','\uD7A3'); 

                    }
                    break;
                case 262 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1244:9: '\\uf900' .. '\\ufa2d'
                    {
                    matchRange('\uF900','\uFA2D'); 

                    }
                    break;
                case 263 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1245:9: '\\ufa30' .. '\\ufa6a'
                    {
                    matchRange('\uFA30','\uFA6A'); 

                    }
                    break;
                case 264 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1246:9: '\\ufb00' .. '\\ufb06'
                    {
                    matchRange('\uFB00','\uFB06'); 

                    }
                    break;
                case 265 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1247:9: '\\ufb13' .. '\\ufb17'
                    {
                    matchRange('\uFB13','\uFB17'); 

                    }
                    break;
                case 266 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1248:9: '\\ufb1d'
                    {
                    match('\uFB1D'); 

                    }
                    break;
                case 267 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1249:9: '\\ufb1f' .. '\\ufb28'
                    {
                    matchRange('\uFB1F','\uFB28'); 

                    }
                    break;
                case 268 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1250:9: '\\ufb2a' .. '\\ufb36'
                    {
                    matchRange('\uFB2A','\uFB36'); 

                    }
                    break;
                case 269 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1251:9: '\\ufb38' .. '\\ufb3c'
                    {
                    matchRange('\uFB38','\uFB3C'); 

                    }
                    break;
                case 270 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1252:9: '\\ufb3e'
                    {
                    match('\uFB3E'); 

                    }
                    break;
                case 271 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1253:9: '\\ufb40' .. '\\ufb41'
                    {
                    matchRange('\uFB40','\uFB41'); 

                    }
                    break;
                case 272 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1254:9: '\\ufb43' .. '\\ufb44'
                    {
                    matchRange('\uFB43','\uFB44'); 

                    }
                    break;
                case 273 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1255:9: '\\ufb46' .. '\\ufbb1'
                    {
                    matchRange('\uFB46','\uFBB1'); 

                    }
                    break;
                case 274 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1256:9: '\\ufbd3' .. '\\ufd3d'
                    {
                    matchRange('\uFBD3','\uFD3D'); 

                    }
                    break;
                case 275 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1257:9: '\\ufd50' .. '\\ufd8f'
                    {
                    matchRange('\uFD50','\uFD8F'); 

                    }
                    break;
                case 276 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1258:9: '\\ufd92' .. '\\ufdc7'
                    {
                    matchRange('\uFD92','\uFDC7'); 

                    }
                    break;
                case 277 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1259:9: '\\ufdf0' .. '\\ufdfc'
                    {
                    matchRange('\uFDF0','\uFDFC'); 

                    }
                    break;
                case 278 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1260:9: '\\ufe33' .. '\\ufe34'
                    {
                    matchRange('\uFE33','\uFE34'); 

                    }
                    break;
                case 279 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1261:9: '\\ufe4d' .. '\\ufe4f'
                    {
                    matchRange('\uFE4D','\uFE4F'); 

                    }
                    break;
                case 280 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1262:9: '\\ufe69'
                    {
                    match('\uFE69'); 

                    }
                    break;
                case 281 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1263:9: '\\ufe70' .. '\\ufe74'
                    {
                    matchRange('\uFE70','\uFE74'); 

                    }
                    break;
                case 282 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1264:9: '\\ufe76' .. '\\ufefc'
                    {
                    matchRange('\uFE76','\uFEFC'); 

                    }
                    break;
                case 283 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1265:9: '\\uff04'
                    {
                    match('\uFF04'); 

                    }
                    break;
                case 284 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1266:9: '\\uff21' .. '\\uff3a'
                    {
                    matchRange('\uFF21','\uFF3A'); 

                    }
                    break;
                case 285 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1267:9: '\\uff3f'
                    {
                    match('\uFF3F'); 

                    }
                    break;
                case 286 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1268:9: '\\uff41' .. '\\uff5a'
                    {
                    matchRange('\uFF41','\uFF5A'); 

                    }
                    break;
                case 287 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1269:9: '\\uff65' .. '\\uffbe'
                    {
                    matchRange('\uFF65','\uFFBE'); 

                    }
                    break;
                case 288 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1270:9: '\\uffc2' .. '\\uffc7'
                    {
                    matchRange('\uFFC2','\uFFC7'); 

                    }
                    break;
                case 289 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1271:9: '\\uffca' .. '\\uffcf'
                    {
                    matchRange('\uFFCA','\uFFCF'); 

                    }
                    break;
                case 290 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1272:9: '\\uffd2' .. '\\uffd7'
                    {
                    matchRange('\uFFD2','\uFFD7'); 

                    }
                    break;
                case 291 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1273:9: '\\uffda' .. '\\uffdc'
                    {
                    matchRange('\uFFDA','\uFFDC'); 

                    }
                    break;
                case 292 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1274:9: '\\uffe0' .. '\\uffe1'
                    {
                    matchRange('\uFFE0','\uFFE1'); 

                    }
                    break;
                case 293 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1275:9: '\\uffe5' .. '\\uffe6'
                    {
                    matchRange('\uFFE5','\uFFE6'); 

                    }
                    break;
                case 294 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1276:9: ( '\\ud800' .. '\\udbff' ) ( '\\udc00' .. '\\udfff' )
                    {
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1276:9: ( '\\ud800' .. '\\udbff' )
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1276:10: '\\ud800' .. '\\udbff'
                    {
                    matchRange('\uD800','\uDBFF'); 

                    }

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1276:30: ( '\\udc00' .. '\\udfff' )
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1276:31: '\\udc00' .. '\\udfff'
                    {
                    matchRange('\uDC00','\uDFFF'); 

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end IdentifierStart

    // $ANTLR start IdentifierPart
    public final void mIdentifierPart() throws RecognitionException {
        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1281:5: ( '\\u0000' .. '\\u0008' | '\\u000e' .. '\\u001b' | '\\u0024' | '\\u0030' .. '\\u0039' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u007f' .. '\\u009f' | '\\u00a2' .. '\\u00a5' | '\\u00aa' | '\\u00ad' | '\\u00b5' | '\\u00ba' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u0236' | '\\u0250' .. '\\u02c1' | '\\u02c6' .. '\\u02d1' | '\\u02e0' .. '\\u02e4' | '\\u02ee' | '\\u0300' .. '\\u0357' | '\\u035d' .. '\\u036f' | '\\u037a' | '\\u0386' | '\\u0388' .. '\\u038a' | '\\u038c' | '\\u038e' .. '\\u03a1' | '\\u03a3' .. '\\u03ce' | '\\u03d0' .. '\\u03f5' | '\\u03f7' .. '\\u03fb' | '\\u0400' .. '\\u0481' | '\\u0483' .. '\\u0486' | '\\u048a' .. '\\u04ce' | '\\u04d0' .. '\\u04f5' | '\\u04f8' .. '\\u04f9' | '\\u0500' .. '\\u050f' | '\\u0531' .. '\\u0556' | '\\u0559' | '\\u0561' .. '\\u0587' | '\\u0591' .. '\\u05a1' | '\\u05a3' .. '\\u05b9' | '\\u05bb' .. '\\u05bd' | '\\u05bf' | '\\u05c1' .. '\\u05c2' | '\\u05c4' | '\\u05d0' .. '\\u05ea' | '\\u05f0' .. '\\u05f2' | '\\u0600' .. '\\u0603' | '\\u0610' .. '\\u0615' | '\\u0621' .. '\\u063a' | '\\u0640' .. '\\u0658' | '\\u0660' .. '\\u0669' | '\\u066e' .. '\\u06d3' | '\\u06d5' .. '\\u06dd' | '\\u06df' .. '\\u06e8' | '\\u06ea' .. '\\u06fc' | '\\u06ff' | '\\u070f' .. '\\u074a' | '\\u074d' .. '\\u074f' | '\\u0780' .. '\\u07b1' | '\\u0901' .. '\\u0939' | '\\u093c' .. '\\u094d' | '\\u0950' .. '\\u0954' | '\\u0958' .. '\\u0963' | '\\u0966' .. '\\u096f' | '\\u0981' .. '\\u0983' | '\\u0985' .. '\\u098c' | '\\u098f' .. '\\u0990' | '\\u0993' .. '\\u09a8' | '\\u09aa' .. '\\u09b0' | '\\u09b2' | '\\u09b6' .. '\\u09b9' | '\\u09bc' .. '\\u09c4' | '\\u09c7' .. '\\u09c8' | '\\u09cb' .. '\\u09cd' | '\\u09d7' | '\\u09dc' .. '\\u09dd' | '\\u09df' .. '\\u09e3' | '\\u09e6' .. '\\u09f3' | '\\u0a01' .. '\\u0a03' | '\\u0a05' .. '\\u0a0a' | '\\u0a0f' .. '\\u0a10' | '\\u0a13' .. '\\u0a28' | '\\u0a2a' .. '\\u0a30' | '\\u0a32' .. '\\u0a33' | '\\u0a35' .. '\\u0a36' | '\\u0a38' .. '\\u0a39' | '\\u0a3c' | '\\u0a3e' .. '\\u0a42' | '\\u0a47' .. '\\u0a48' | '\\u0a4b' .. '\\u0a4d' | '\\u0a59' .. '\\u0a5c' | '\\u0a5e' | '\\u0a66' .. '\\u0a74' | '\\u0a81' .. '\\u0a83' | '\\u0a85' .. '\\u0a8d' | '\\u0a8f' .. '\\u0a91' | '\\u0a93' .. '\\u0aa8' | '\\u0aaa' .. '\\u0ab0' | '\\u0ab2' .. '\\u0ab3' | '\\u0ab5' .. '\\u0ab9' | '\\u0abc' .. '\\u0ac5' | '\\u0ac7' .. '\\u0ac9' | '\\u0acb' .. '\\u0acd' | '\\u0ad0' | '\\u0ae0' .. '\\u0ae3' | '\\u0ae6' .. '\\u0aef' | '\\u0af1' | '\\u0b01' .. '\\u0b03' | '\\u0b05' .. '\\u0b0c' | '\\u0b0f' .. '\\u0b10' | '\\u0b13' .. '\\u0b28' | '\\u0b2a' .. '\\u0b30' | '\\u0b32' .. '\\u0b33' | '\\u0b35' .. '\\u0b39' | '\\u0b3c' .. '\\u0b43' | '\\u0b47' .. '\\u0b48' | '\\u0b4b' .. '\\u0b4d' | '\\u0b56' .. '\\u0b57' | '\\u0b5c' .. '\\u0b5d' | '\\u0b5f' .. '\\u0b61' | '\\u0b66' .. '\\u0b6f' | '\\u0b71' | '\\u0b82' .. '\\u0b83' | '\\u0b85' .. '\\u0b8a' | '\\u0b8e' .. '\\u0b90' | '\\u0b92' .. '\\u0b95' | '\\u0b99' .. '\\u0b9a' | '\\u0b9c' | '\\u0b9e' .. '\\u0b9f' | '\\u0ba3' .. '\\u0ba4' | '\\u0ba8' .. '\\u0baa' | '\\u0bae' .. '\\u0bb5' | '\\u0bb7' .. '\\u0bb9' | '\\u0bbe' .. '\\u0bc2' | '\\u0bc6' .. '\\u0bc8' | '\\u0bca' .. '\\u0bcd' | '\\u0bd7' | '\\u0be7' .. '\\u0bef' | '\\u0bf9' | '\\u0c01' .. '\\u0c03' | '\\u0c05' .. '\\u0c0c' | '\\u0c0e' .. '\\u0c10' | '\\u0c12' .. '\\u0c28' | '\\u0c2a' .. '\\u0c33' | '\\u0c35' .. '\\u0c39' | '\\u0c3e' .. '\\u0c44' | '\\u0c46' .. '\\u0c48' | '\\u0c4a' .. '\\u0c4d' | '\\u0c55' .. '\\u0c56' | '\\u0c60' .. '\\u0c61' | '\\u0c66' .. '\\u0c6f' | '\\u0c82' .. '\\u0c83' | '\\u0c85' .. '\\u0c8c' | '\\u0c8e' .. '\\u0c90' | '\\u0c92' .. '\\u0ca8' | '\\u0caa' .. '\\u0cb3' | '\\u0cb5' .. '\\u0cb9' | '\\u0cbc' .. '\\u0cc4' | '\\u0cc6' .. '\\u0cc8' | '\\u0cca' .. '\\u0ccd' | '\\u0cd5' .. '\\u0cd6' | '\\u0cde' | '\\u0ce0' .. '\\u0ce1' | '\\u0ce6' .. '\\u0cef' | '\\u0d02' .. '\\u0d03' | '\\u0d05' .. '\\u0d0c' | '\\u0d0e' .. '\\u0d10' | '\\u0d12' .. '\\u0d28' | '\\u0d2a' .. '\\u0d39' | '\\u0d3e' .. '\\u0d43' | '\\u0d46' .. '\\u0d48' | '\\u0d4a' .. '\\u0d4d' | '\\u0d57' | '\\u0d60' .. '\\u0d61' | '\\u0d66' .. '\\u0d6f' | '\\u0d82' .. '\\u0d83' | '\\u0d85' .. '\\u0d96' | '\\u0d9a' .. '\\u0db1' | '\\u0db3' .. '\\u0dbb' | '\\u0dbd' | '\\u0dc0' .. '\\u0dc6' | '\\u0dca' | '\\u0dcf' .. '\\u0dd4' | '\\u0dd6' | '\\u0dd8' .. '\\u0ddf' | '\\u0df2' .. '\\u0df3' | '\\u0e01' .. '\\u0e3a' | '\\u0e3f' .. '\\u0e4e' | '\\u0e50' .. '\\u0e59' | '\\u0e81' .. '\\u0e82' | '\\u0e84' | '\\u0e87' .. '\\u0e88' | '\\u0e8a' | '\\u0e8d' | '\\u0e94' .. '\\u0e97' | '\\u0e99' .. '\\u0e9f' | '\\u0ea1' .. '\\u0ea3' | '\\u0ea5' | '\\u0ea7' | '\\u0eaa' .. '\\u0eab' | '\\u0ead' .. '\\u0eb9' | '\\u0ebb' .. '\\u0ebd' | '\\u0ec0' .. '\\u0ec4' | '\\u0ec6' | '\\u0ec8' .. '\\u0ecd' | '\\u0ed0' .. '\\u0ed9' | '\\u0edc' .. '\\u0edd' | '\\u0f00' | '\\u0f18' .. '\\u0f19' | '\\u0f20' .. '\\u0f29' | '\\u0f35' | '\\u0f37' | '\\u0f39' | '\\u0f3e' .. '\\u0f47' | '\\u0f49' .. '\\u0f6a' | '\\u0f71' .. '\\u0f84' | '\\u0f86' .. '\\u0f8b' | '\\u0f90' .. '\\u0f97' | '\\u0f99' .. '\\u0fbc' | '\\u0fc6' | '\\u1000' .. '\\u1021' | '\\u1023' .. '\\u1027' | '\\u1029' .. '\\u102a' | '\\u102c' .. '\\u1032' | '\\u1036' .. '\\u1039' | '\\u1040' .. '\\u1049' | '\\u1050' .. '\\u1059' | '\\u10a0' .. '\\u10c5' | '\\u10d0' .. '\\u10f8' | '\\u1100' .. '\\u1159' | '\\u115f' .. '\\u11a2' | '\\u11a8' .. '\\u11f9' | '\\u1200' .. '\\u1206' | '\\u1208' .. '\\u1246' | '\\u1248' | '\\u124a' .. '\\u124d' | '\\u1250' .. '\\u1256' | '\\u1258' | '\\u125a' .. '\\u125d' | '\\u1260' .. '\\u1286' | '\\u1288' | '\\u128a' .. '\\u128d' | '\\u1290' .. '\\u12ae' | '\\u12b0' | '\\u12b2' .. '\\u12b5' | '\\u12b8' .. '\\u12be' | '\\u12c0' | '\\u12c2' .. '\\u12c5' | '\\u12c8' .. '\\u12ce' | '\\u12d0' .. '\\u12d6' | '\\u12d8' .. '\\u12ee' | '\\u12f0' .. '\\u130e' | '\\u1310' | '\\u1312' .. '\\u1315' | '\\u1318' .. '\\u131e' | '\\u1320' .. '\\u1346' | '\\u1348' .. '\\u135a' | '\\u1369' .. '\\u1371' | '\\u13a0' .. '\\u13f4' | '\\u1401' .. '\\u166c' | '\\u166f' .. '\\u1676' | '\\u1681' .. '\\u169a' | '\\u16a0' .. '\\u16ea' | '\\u16ee' .. '\\u16f0' | '\\u1700' .. '\\u170c' | '\\u170e' .. '\\u1714' | '\\u1720' .. '\\u1734' | '\\u1740' .. '\\u1753' | '\\u1760' .. '\\u176c' | '\\u176e' .. '\\u1770' | '\\u1772' .. '\\u1773' | '\\u1780' .. '\\u17d3' | '\\u17d7' | '\\u17db' .. '\\u17dd' | '\\u17e0' .. '\\u17e9' | '\\u180b' .. '\\u180d' | '\\u1810' .. '\\u1819' | '\\u1820' .. '\\u1877' | '\\u1880' .. '\\u18a9' | '\\u1900' .. '\\u191c' | '\\u1920' .. '\\u192b' | '\\u1930' .. '\\u193b' | '\\u1946' .. '\\u196d' | '\\u1970' .. '\\u1974' | '\\u1d00' .. '\\u1d6b' | '\\u1e00' .. '\\u1e9b' | '\\u1ea0' .. '\\u1ef9' | '\\u1f00' .. '\\u1f15' | '\\u1f18' .. '\\u1f1d' | '\\u1f20' .. '\\u1f45' | '\\u1f48' .. '\\u1f4d' | '\\u1f50' .. '\\u1f57' | '\\u1f59' | '\\u1f5b' | '\\u1f5d' | '\\u1f5f' .. '\\u1f7d' | '\\u1f80' .. '\\u1fb4' | '\\u1fb6' .. '\\u1fbc' | '\\u1fbe' | '\\u1fc2' .. '\\u1fc4' | '\\u1fc6' .. '\\u1fcc' | '\\u1fd0' .. '\\u1fd3' | '\\u1fd6' .. '\\u1fdb' | '\\u1fe0' .. '\\u1fec' | '\\u1ff2' .. '\\u1ff4' | '\\u1ff6' .. '\\u1ffc' | '\\u200c' .. '\\u200f' | '\\u202a' .. '\\u202e' | '\\u203f' .. '\\u2040' | '\\u2054' | '\\u2060' .. '\\u2063' | '\\u206a' .. '\\u206f' | '\\u2071' | '\\u207f' | '\\u20a0' .. '\\u20b1' | '\\u20d0' .. '\\u20dc' | '\\u20e1' | '\\u20e5' .. '\\u20ea' | '\\u2102' | '\\u2107' | '\\u210a' .. '\\u2113' | '\\u2115' | '\\u2119' .. '\\u211d' | '\\u2124' | '\\u2126' | '\\u2128' | '\\u212a' .. '\\u212d' | '\\u212f' .. '\\u2131' | '\\u2133' .. '\\u2139' | '\\u213d' .. '\\u213f' | '\\u2145' .. '\\u2149' | '\\u2160' .. '\\u2183' | '\\u3005' .. '\\u3007' | '\\u3021' .. '\\u302f' | '\\u3031' .. '\\u3035' | '\\u3038' .. '\\u303c' | '\\u3041' .. '\\u3096' | '\\u3099' .. '\\u309a' | '\\u309d' .. '\\u309f' | '\\u30a1' .. '\\u30ff' | '\\u3105' .. '\\u312c' | '\\u3131' .. '\\u318e' | '\\u31a0' .. '\\u31b7' | '\\u31f0' .. '\\u31ff' | '\\u3400' .. '\\u4db5' | '\\u4e00' .. '\\u9fa5' | '\\ua000' .. '\\ua48c' | '\\uac00' .. '\\ud7a3' | '\\uf900' .. '\\ufa2d' | '\\ufa30' .. '\\ufa6a' | '\\ufb00' .. '\\ufb06' | '\\ufb13' .. '\\ufb17' | '\\ufb1d' .. '\\ufb28' | '\\ufb2a' .. '\\ufb36' | '\\ufb38' .. '\\ufb3c' | '\\ufb3e' | '\\ufb40' .. '\\ufb41' | '\\ufb43' .. '\\ufb44' | '\\ufb46' .. '\\ufbb1' | '\\ufbd3' .. '\\ufd3d' | '\\ufd50' .. '\\ufd8f' | '\\ufd92' .. '\\ufdc7' | '\\ufdf0' .. '\\ufdfc' | '\\ufe00' .. '\\ufe0f' | '\\ufe20' .. '\\ufe23' | '\\ufe33' .. '\\ufe34' | '\\ufe4d' .. '\\ufe4f' | '\\ufe69' | '\\ufe70' .. '\\ufe74' | '\\ufe76' .. '\\ufefc' | '\\ufeff' | '\\uff04' | '\\uff10' .. '\\uff19' | '\\uff21' .. '\\uff3a' | '\\uff3f' | '\\uff41' .. '\\uff5a' | '\\uff65' .. '\\uffbe' | '\\uffc2' .. '\\uffc7' | '\\uffca' .. '\\uffcf' | '\\uffd2' .. '\\uffd7' | '\\uffda' .. '\\uffdc' | '\\uffe0' .. '\\uffe1' | '\\uffe5' .. '\\uffe6' | '\\ufff9' .. '\\ufffb' | ( '\\ud800' .. '\\udbff' ) ( '\\udc00' .. '\\udfff' ) )
            int alt32=386;
            int LA32_0 = input.LA(1);

            if ( ((LA32_0>='\u0000' && LA32_0<='\b')) ) {
                alt32=1;
            }
            else if ( ((LA32_0>='\u000E' && LA32_0<='\u001B')) ) {
                alt32=2;
            }
            else if ( (LA32_0=='$') ) {
                alt32=3;
            }
            else if ( ((LA32_0>='0' && LA32_0<='9')) ) {
                alt32=4;
            }
            else if ( ((LA32_0>='A' && LA32_0<='Z')) ) {
                alt32=5;
            }
            else if ( (LA32_0=='_') ) {
                alt32=6;
            }
            else if ( ((LA32_0>='a' && LA32_0<='z')) ) {
                alt32=7;
            }
            else if ( ((LA32_0>='\u007F' && LA32_0<='\u009F')) ) {
                alt32=8;
            }
            else if ( ((LA32_0>='\u00A2' && LA32_0<='\u00A5')) ) {
                alt32=9;
            }
            else if ( (LA32_0=='\u00AA') ) {
                alt32=10;
            }
            else if ( (LA32_0=='\u00AD') ) {
                alt32=11;
            }
            else if ( (LA32_0=='\u00B5') ) {
                alt32=12;
            }
            else if ( (LA32_0=='\u00BA') ) {
                alt32=13;
            }
            else if ( ((LA32_0>='\u00C0' && LA32_0<='\u00D6')) ) {
                alt32=14;
            }
            else if ( ((LA32_0>='\u00D8' && LA32_0<='\u00F6')) ) {
                alt32=15;
            }
            else if ( ((LA32_0>='\u00F8' && LA32_0<='\u0236')) ) {
                alt32=16;
            }
            else if ( ((LA32_0>='\u0250' && LA32_0<='\u02C1')) ) {
                alt32=17;
            }
            else if ( ((LA32_0>='\u02C6' && LA32_0<='\u02D1')) ) {
                alt32=18;
            }
            else if ( ((LA32_0>='\u02E0' && LA32_0<='\u02E4')) ) {
                alt32=19;
            }
            else if ( (LA32_0=='\u02EE') ) {
                alt32=20;
            }
            else if ( ((LA32_0>='\u0300' && LA32_0<='\u0357')) ) {
                alt32=21;
            }
            else if ( ((LA32_0>='\u035D' && LA32_0<='\u036F')) ) {
                alt32=22;
            }
            else if ( (LA32_0=='\u037A') ) {
                alt32=23;
            }
            else if ( (LA32_0=='\u0386') ) {
                alt32=24;
            }
            else if ( ((LA32_0>='\u0388' && LA32_0<='\u038A')) ) {
                alt32=25;
            }
            else if ( (LA32_0=='\u038C') ) {
                alt32=26;
            }
            else if ( ((LA32_0>='\u038E' && LA32_0<='\u03A1')) ) {
                alt32=27;
            }
            else if ( ((LA32_0>='\u03A3' && LA32_0<='\u03CE')) ) {
                alt32=28;
            }
            else if ( ((LA32_0>='\u03D0' && LA32_0<='\u03F5')) ) {
                alt32=29;
            }
            else if ( ((LA32_0>='\u03F7' && LA32_0<='\u03FB')) ) {
                alt32=30;
            }
            else if ( ((LA32_0>='\u0400' && LA32_0<='\u0481')) ) {
                alt32=31;
            }
            else if ( ((LA32_0>='\u0483' && LA32_0<='\u0486')) ) {
                alt32=32;
            }
            else if ( ((LA32_0>='\u048A' && LA32_0<='\u04CE')) ) {
                alt32=33;
            }
            else if ( ((LA32_0>='\u04D0' && LA32_0<='\u04F5')) ) {
                alt32=34;
            }
            else if ( ((LA32_0>='\u04F8' && LA32_0<='\u04F9')) ) {
                alt32=35;
            }
            else if ( ((LA32_0>='\u0500' && LA32_0<='\u050F')) ) {
                alt32=36;
            }
            else if ( ((LA32_0>='\u0531' && LA32_0<='\u0556')) ) {
                alt32=37;
            }
            else if ( (LA32_0=='\u0559') ) {
                alt32=38;
            }
            else if ( ((LA32_0>='\u0561' && LA32_0<='\u0587')) ) {
                alt32=39;
            }
            else if ( ((LA32_0>='\u0591' && LA32_0<='\u05A1')) ) {
                alt32=40;
            }
            else if ( ((LA32_0>='\u05A3' && LA32_0<='\u05B9')) ) {
                alt32=41;
            }
            else if ( ((LA32_0>='\u05BB' && LA32_0<='\u05BD')) ) {
                alt32=42;
            }
            else if ( (LA32_0=='\u05BF') ) {
                alt32=43;
            }
            else if ( ((LA32_0>='\u05C1' && LA32_0<='\u05C2')) ) {
                alt32=44;
            }
            else if ( (LA32_0=='\u05C4') ) {
                alt32=45;
            }
            else if ( ((LA32_0>='\u05D0' && LA32_0<='\u05EA')) ) {
                alt32=46;
            }
            else if ( ((LA32_0>='\u05F0' && LA32_0<='\u05F2')) ) {
                alt32=47;
            }
            else if ( ((LA32_0>='\u0600' && LA32_0<='\u0603')) ) {
                alt32=48;
            }
            else if ( ((LA32_0>='\u0610' && LA32_0<='\u0615')) ) {
                alt32=49;
            }
            else if ( ((LA32_0>='\u0621' && LA32_0<='\u063A')) ) {
                alt32=50;
            }
            else if ( ((LA32_0>='\u0640' && LA32_0<='\u0658')) ) {
                alt32=51;
            }
            else if ( ((LA32_0>='\u0660' && LA32_0<='\u0669')) ) {
                alt32=52;
            }
            else if ( ((LA32_0>='\u066E' && LA32_0<='\u06D3')) ) {
                alt32=53;
            }
            else if ( ((LA32_0>='\u06D5' && LA32_0<='\u06DD')) ) {
                alt32=54;
            }
            else if ( ((LA32_0>='\u06DF' && LA32_0<='\u06E8')) ) {
                alt32=55;
            }
            else if ( ((LA32_0>='\u06EA' && LA32_0<='\u06FC')) ) {
                alt32=56;
            }
            else if ( (LA32_0=='\u06FF') ) {
                alt32=57;
            }
            else if ( ((LA32_0>='\u070F' && LA32_0<='\u074A')) ) {
                alt32=58;
            }
            else if ( ((LA32_0>='\u074D' && LA32_0<='\u074F')) ) {
                alt32=59;
            }
            else if ( ((LA32_0>='\u0780' && LA32_0<='\u07B1')) ) {
                alt32=60;
            }
            else if ( ((LA32_0>='\u0901' && LA32_0<='\u0939')) ) {
                alt32=61;
            }
            else if ( ((LA32_0>='\u093C' && LA32_0<='\u094D')) ) {
                alt32=62;
            }
            else if ( ((LA32_0>='\u0950' && LA32_0<='\u0954')) ) {
                alt32=63;
            }
            else if ( ((LA32_0>='\u0958' && LA32_0<='\u0963')) ) {
                alt32=64;
            }
            else if ( ((LA32_0>='\u0966' && LA32_0<='\u096F')) ) {
                alt32=65;
            }
            else if ( ((LA32_0>='\u0981' && LA32_0<='\u0983')) ) {
                alt32=66;
            }
            else if ( ((LA32_0>='\u0985' && LA32_0<='\u098C')) ) {
                alt32=67;
            }
            else if ( ((LA32_0>='\u098F' && LA32_0<='\u0990')) ) {
                alt32=68;
            }
            else if ( ((LA32_0>='\u0993' && LA32_0<='\u09A8')) ) {
                alt32=69;
            }
            else if ( ((LA32_0>='\u09AA' && LA32_0<='\u09B0')) ) {
                alt32=70;
            }
            else if ( (LA32_0=='\u09B2') ) {
                alt32=71;
            }
            else if ( ((LA32_0>='\u09B6' && LA32_0<='\u09B9')) ) {
                alt32=72;
            }
            else if ( ((LA32_0>='\u09BC' && LA32_0<='\u09C4')) ) {
                alt32=73;
            }
            else if ( ((LA32_0>='\u09C7' && LA32_0<='\u09C8')) ) {
                alt32=74;
            }
            else if ( ((LA32_0>='\u09CB' && LA32_0<='\u09CD')) ) {
                alt32=75;
            }
            else if ( (LA32_0=='\u09D7') ) {
                alt32=76;
            }
            else if ( ((LA32_0>='\u09DC' && LA32_0<='\u09DD')) ) {
                alt32=77;
            }
            else if ( ((LA32_0>='\u09DF' && LA32_0<='\u09E3')) ) {
                alt32=78;
            }
            else if ( ((LA32_0>='\u09E6' && LA32_0<='\u09F3')) ) {
                alt32=79;
            }
            else if ( ((LA32_0>='\u0A01' && LA32_0<='\u0A03')) ) {
                alt32=80;
            }
            else if ( ((LA32_0>='\u0A05' && LA32_0<='\u0A0A')) ) {
                alt32=81;
            }
            else if ( ((LA32_0>='\u0A0F' && LA32_0<='\u0A10')) ) {
                alt32=82;
            }
            else if ( ((LA32_0>='\u0A13' && LA32_0<='\u0A28')) ) {
                alt32=83;
            }
            else if ( ((LA32_0>='\u0A2A' && LA32_0<='\u0A30')) ) {
                alt32=84;
            }
            else if ( ((LA32_0>='\u0A32' && LA32_0<='\u0A33')) ) {
                alt32=85;
            }
            else if ( ((LA32_0>='\u0A35' && LA32_0<='\u0A36')) ) {
                alt32=86;
            }
            else if ( ((LA32_0>='\u0A38' && LA32_0<='\u0A39')) ) {
                alt32=87;
            }
            else if ( (LA32_0=='\u0A3C') ) {
                alt32=88;
            }
            else if ( ((LA32_0>='\u0A3E' && LA32_0<='\u0A42')) ) {
                alt32=89;
            }
            else if ( ((LA32_0>='\u0A47' && LA32_0<='\u0A48')) ) {
                alt32=90;
            }
            else if ( ((LA32_0>='\u0A4B' && LA32_0<='\u0A4D')) ) {
                alt32=91;
            }
            else if ( ((LA32_0>='\u0A59' && LA32_0<='\u0A5C')) ) {
                alt32=92;
            }
            else if ( (LA32_0=='\u0A5E') ) {
                alt32=93;
            }
            else if ( ((LA32_0>='\u0A66' && LA32_0<='\u0A74')) ) {
                alt32=94;
            }
            else if ( ((LA32_0>='\u0A81' && LA32_0<='\u0A83')) ) {
                alt32=95;
            }
            else if ( ((LA32_0>='\u0A85' && LA32_0<='\u0A8D')) ) {
                alt32=96;
            }
            else if ( ((LA32_0>='\u0A8F' && LA32_0<='\u0A91')) ) {
                alt32=97;
            }
            else if ( ((LA32_0>='\u0A93' && LA32_0<='\u0AA8')) ) {
                alt32=98;
            }
            else if ( ((LA32_0>='\u0AAA' && LA32_0<='\u0AB0')) ) {
                alt32=99;
            }
            else if ( ((LA32_0>='\u0AB2' && LA32_0<='\u0AB3')) ) {
                alt32=100;
            }
            else if ( ((LA32_0>='\u0AB5' && LA32_0<='\u0AB9')) ) {
                alt32=101;
            }
            else if ( ((LA32_0>='\u0ABC' && LA32_0<='\u0AC5')) ) {
                alt32=102;
            }
            else if ( ((LA32_0>='\u0AC7' && LA32_0<='\u0AC9')) ) {
                alt32=103;
            }
            else if ( ((LA32_0>='\u0ACB' && LA32_0<='\u0ACD')) ) {
                alt32=104;
            }
            else if ( (LA32_0=='\u0AD0') ) {
                alt32=105;
            }
            else if ( ((LA32_0>='\u0AE0' && LA32_0<='\u0AE3')) ) {
                alt32=106;
            }
            else if ( ((LA32_0>='\u0AE6' && LA32_0<='\u0AEF')) ) {
                alt32=107;
            }
            else if ( (LA32_0=='\u0AF1') ) {
                alt32=108;
            }
            else if ( ((LA32_0>='\u0B01' && LA32_0<='\u0B03')) ) {
                alt32=109;
            }
            else if ( ((LA32_0>='\u0B05' && LA32_0<='\u0B0C')) ) {
                alt32=110;
            }
            else if ( ((LA32_0>='\u0B0F' && LA32_0<='\u0B10')) ) {
                alt32=111;
            }
            else if ( ((LA32_0>='\u0B13' && LA32_0<='\u0B28')) ) {
                alt32=112;
            }
            else if ( ((LA32_0>='\u0B2A' && LA32_0<='\u0B30')) ) {
                alt32=113;
            }
            else if ( ((LA32_0>='\u0B32' && LA32_0<='\u0B33')) ) {
                alt32=114;
            }
            else if ( ((LA32_0>='\u0B35' && LA32_0<='\u0B39')) ) {
                alt32=115;
            }
            else if ( ((LA32_0>='\u0B3C' && LA32_0<='\u0B43')) ) {
                alt32=116;
            }
            else if ( ((LA32_0>='\u0B47' && LA32_0<='\u0B48')) ) {
                alt32=117;
            }
            else if ( ((LA32_0>='\u0B4B' && LA32_0<='\u0B4D')) ) {
                alt32=118;
            }
            else if ( ((LA32_0>='\u0B56' && LA32_0<='\u0B57')) ) {
                alt32=119;
            }
            else if ( ((LA32_0>='\u0B5C' && LA32_0<='\u0B5D')) ) {
                alt32=120;
            }
            else if ( ((LA32_0>='\u0B5F' && LA32_0<='\u0B61')) ) {
                alt32=121;
            }
            else if ( ((LA32_0>='\u0B66' && LA32_0<='\u0B6F')) ) {
                alt32=122;
            }
            else if ( (LA32_0=='\u0B71') ) {
                alt32=123;
            }
            else if ( ((LA32_0>='\u0B82' && LA32_0<='\u0B83')) ) {
                alt32=124;
            }
            else if ( ((LA32_0>='\u0B85' && LA32_0<='\u0B8A')) ) {
                alt32=125;
            }
            else if ( ((LA32_0>='\u0B8E' && LA32_0<='\u0B90')) ) {
                alt32=126;
            }
            else if ( ((LA32_0>='\u0B92' && LA32_0<='\u0B95')) ) {
                alt32=127;
            }
            else if ( ((LA32_0>='\u0B99' && LA32_0<='\u0B9A')) ) {
                alt32=128;
            }
            else if ( (LA32_0=='\u0B9C') ) {
                alt32=129;
            }
            else if ( ((LA32_0>='\u0B9E' && LA32_0<='\u0B9F')) ) {
                alt32=130;
            }
            else if ( ((LA32_0>='\u0BA3' && LA32_0<='\u0BA4')) ) {
                alt32=131;
            }
            else if ( ((LA32_0>='\u0BA8' && LA32_0<='\u0BAA')) ) {
                alt32=132;
            }
            else if ( ((LA32_0>='\u0BAE' && LA32_0<='\u0BB5')) ) {
                alt32=133;
            }
            else if ( ((LA32_0>='\u0BB7' && LA32_0<='\u0BB9')) ) {
                alt32=134;
            }
            else if ( ((LA32_0>='\u0BBE' && LA32_0<='\u0BC2')) ) {
                alt32=135;
            }
            else if ( ((LA32_0>='\u0BC6' && LA32_0<='\u0BC8')) ) {
                alt32=136;
            }
            else if ( ((LA32_0>='\u0BCA' && LA32_0<='\u0BCD')) ) {
                alt32=137;
            }
            else if ( (LA32_0=='\u0BD7') ) {
                alt32=138;
            }
            else if ( ((LA32_0>='\u0BE7' && LA32_0<='\u0BEF')) ) {
                alt32=139;
            }
            else if ( (LA32_0=='\u0BF9') ) {
                alt32=140;
            }
            else if ( ((LA32_0>='\u0C01' && LA32_0<='\u0C03')) ) {
                alt32=141;
            }
            else if ( ((LA32_0>='\u0C05' && LA32_0<='\u0C0C')) ) {
                alt32=142;
            }
            else if ( ((LA32_0>='\u0C0E' && LA32_0<='\u0C10')) ) {
                alt32=143;
            }
            else if ( ((LA32_0>='\u0C12' && LA32_0<='\u0C28')) ) {
                alt32=144;
            }
            else if ( ((LA32_0>='\u0C2A' && LA32_0<='\u0C33')) ) {
                alt32=145;
            }
            else if ( ((LA32_0>='\u0C35' && LA32_0<='\u0C39')) ) {
                alt32=146;
            }
            else if ( ((LA32_0>='\u0C3E' && LA32_0<='\u0C44')) ) {
                alt32=147;
            }
            else if ( ((LA32_0>='\u0C46' && LA32_0<='\u0C48')) ) {
                alt32=148;
            }
            else if ( ((LA32_0>='\u0C4A' && LA32_0<='\u0C4D')) ) {
                alt32=149;
            }
            else if ( ((LA32_0>='\u0C55' && LA32_0<='\u0C56')) ) {
                alt32=150;
            }
            else if ( ((LA32_0>='\u0C60' && LA32_0<='\u0C61')) ) {
                alt32=151;
            }
            else if ( ((LA32_0>='\u0C66' && LA32_0<='\u0C6F')) ) {
                alt32=152;
            }
            else if ( ((LA32_0>='\u0C82' && LA32_0<='\u0C83')) ) {
                alt32=153;
            }
            else if ( ((LA32_0>='\u0C85' && LA32_0<='\u0C8C')) ) {
                alt32=154;
            }
            else if ( ((LA32_0>='\u0C8E' && LA32_0<='\u0C90')) ) {
                alt32=155;
            }
            else if ( ((LA32_0>='\u0C92' && LA32_0<='\u0CA8')) ) {
                alt32=156;
            }
            else if ( ((LA32_0>='\u0CAA' && LA32_0<='\u0CB3')) ) {
                alt32=157;
            }
            else if ( ((LA32_0>='\u0CB5' && LA32_0<='\u0CB9')) ) {
                alt32=158;
            }
            else if ( ((LA32_0>='\u0CBC' && LA32_0<='\u0CC4')) ) {
                alt32=159;
            }
            else if ( ((LA32_0>='\u0CC6' && LA32_0<='\u0CC8')) ) {
                alt32=160;
            }
            else if ( ((LA32_0>='\u0CCA' && LA32_0<='\u0CCD')) ) {
                alt32=161;
            }
            else if ( ((LA32_0>='\u0CD5' && LA32_0<='\u0CD6')) ) {
                alt32=162;
            }
            else if ( (LA32_0=='\u0CDE') ) {
                alt32=163;
            }
            else if ( ((LA32_0>='\u0CE0' && LA32_0<='\u0CE1')) ) {
                alt32=164;
            }
            else if ( ((LA32_0>='\u0CE6' && LA32_0<='\u0CEF')) ) {
                alt32=165;
            }
            else if ( ((LA32_0>='\u0D02' && LA32_0<='\u0D03')) ) {
                alt32=166;
            }
            else if ( ((LA32_0>='\u0D05' && LA32_0<='\u0D0C')) ) {
                alt32=167;
            }
            else if ( ((LA32_0>='\u0D0E' && LA32_0<='\u0D10')) ) {
                alt32=168;
            }
            else if ( ((LA32_0>='\u0D12' && LA32_0<='\u0D28')) ) {
                alt32=169;
            }
            else if ( ((LA32_0>='\u0D2A' && LA32_0<='\u0D39')) ) {
                alt32=170;
            }
            else if ( ((LA32_0>='\u0D3E' && LA32_0<='\u0D43')) ) {
                alt32=171;
            }
            else if ( ((LA32_0>='\u0D46' && LA32_0<='\u0D48')) ) {
                alt32=172;
            }
            else if ( ((LA32_0>='\u0D4A' && LA32_0<='\u0D4D')) ) {
                alt32=173;
            }
            else if ( (LA32_0=='\u0D57') ) {
                alt32=174;
            }
            else if ( ((LA32_0>='\u0D60' && LA32_0<='\u0D61')) ) {
                alt32=175;
            }
            else if ( ((LA32_0>='\u0D66' && LA32_0<='\u0D6F')) ) {
                alt32=176;
            }
            else if ( ((LA32_0>='\u0D82' && LA32_0<='\u0D83')) ) {
                alt32=177;
            }
            else if ( ((LA32_0>='\u0D85' && LA32_0<='\u0D96')) ) {
                alt32=178;
            }
            else if ( ((LA32_0>='\u0D9A' && LA32_0<='\u0DB1')) ) {
                alt32=179;
            }
            else if ( ((LA32_0>='\u0DB3' && LA32_0<='\u0DBB')) ) {
                alt32=180;
            }
            else if ( (LA32_0=='\u0DBD') ) {
                alt32=181;
            }
            else if ( ((LA32_0>='\u0DC0' && LA32_0<='\u0DC6')) ) {
                alt32=182;
            }
            else if ( (LA32_0=='\u0DCA') ) {
                alt32=183;
            }
            else if ( ((LA32_0>='\u0DCF' && LA32_0<='\u0DD4')) ) {
                alt32=184;
            }
            else if ( (LA32_0=='\u0DD6') ) {
                alt32=185;
            }
            else if ( ((LA32_0>='\u0DD8' && LA32_0<='\u0DDF')) ) {
                alt32=186;
            }
            else if ( ((LA32_0>='\u0DF2' && LA32_0<='\u0DF3')) ) {
                alt32=187;
            }
            else if ( ((LA32_0>='\u0E01' && LA32_0<='\u0E3A')) ) {
                alt32=188;
            }
            else if ( ((LA32_0>='\u0E3F' && LA32_0<='\u0E4E')) ) {
                alt32=189;
            }
            else if ( ((LA32_0>='\u0E50' && LA32_0<='\u0E59')) ) {
                alt32=190;
            }
            else if ( ((LA32_0>='\u0E81' && LA32_0<='\u0E82')) ) {
                alt32=191;
            }
            else if ( (LA32_0=='\u0E84') ) {
                alt32=192;
            }
            else if ( ((LA32_0>='\u0E87' && LA32_0<='\u0E88')) ) {
                alt32=193;
            }
            else if ( (LA32_0=='\u0E8A') ) {
                alt32=194;
            }
            else if ( (LA32_0=='\u0E8D') ) {
                alt32=195;
            }
            else if ( ((LA32_0>='\u0E94' && LA32_0<='\u0E97')) ) {
                alt32=196;
            }
            else if ( ((LA32_0>='\u0E99' && LA32_0<='\u0E9F')) ) {
                alt32=197;
            }
            else if ( ((LA32_0>='\u0EA1' && LA32_0<='\u0EA3')) ) {
                alt32=198;
            }
            else if ( (LA32_0=='\u0EA5') ) {
                alt32=199;
            }
            else if ( (LA32_0=='\u0EA7') ) {
                alt32=200;
            }
            else if ( ((LA32_0>='\u0EAA' && LA32_0<='\u0EAB')) ) {
                alt32=201;
            }
            else if ( ((LA32_0>='\u0EAD' && LA32_0<='\u0EB9')) ) {
                alt32=202;
            }
            else if ( ((LA32_0>='\u0EBB' && LA32_0<='\u0EBD')) ) {
                alt32=203;
            }
            else if ( ((LA32_0>='\u0EC0' && LA32_0<='\u0EC4')) ) {
                alt32=204;
            }
            else if ( (LA32_0=='\u0EC6') ) {
                alt32=205;
            }
            else if ( ((LA32_0>='\u0EC8' && LA32_0<='\u0ECD')) ) {
                alt32=206;
            }
            else if ( ((LA32_0>='\u0ED0' && LA32_0<='\u0ED9')) ) {
                alt32=207;
            }
            else if ( ((LA32_0>='\u0EDC' && LA32_0<='\u0EDD')) ) {
                alt32=208;
            }
            else if ( (LA32_0=='\u0F00') ) {
                alt32=209;
            }
            else if ( ((LA32_0>='\u0F18' && LA32_0<='\u0F19')) ) {
                alt32=210;
            }
            else if ( ((LA32_0>='\u0F20' && LA32_0<='\u0F29')) ) {
                alt32=211;
            }
            else if ( (LA32_0=='\u0F35') ) {
                alt32=212;
            }
            else if ( (LA32_0=='\u0F37') ) {
                alt32=213;
            }
            else if ( (LA32_0=='\u0F39') ) {
                alt32=214;
            }
            else if ( ((LA32_0>='\u0F3E' && LA32_0<='\u0F47')) ) {
                alt32=215;
            }
            else if ( ((LA32_0>='\u0F49' && LA32_0<='\u0F6A')) ) {
                alt32=216;
            }
            else if ( ((LA32_0>='\u0F71' && LA32_0<='\u0F84')) ) {
                alt32=217;
            }
            else if ( ((LA32_0>='\u0F86' && LA32_0<='\u0F8B')) ) {
                alt32=218;
            }
            else if ( ((LA32_0>='\u0F90' && LA32_0<='\u0F97')) ) {
                alt32=219;
            }
            else if ( ((LA32_0>='\u0F99' && LA32_0<='\u0FBC')) ) {
                alt32=220;
            }
            else if ( (LA32_0=='\u0FC6') ) {
                alt32=221;
            }
            else if ( ((LA32_0>='\u1000' && LA32_0<='\u1021')) ) {
                alt32=222;
            }
            else if ( ((LA32_0>='\u1023' && LA32_0<='\u1027')) ) {
                alt32=223;
            }
            else if ( ((LA32_0>='\u1029' && LA32_0<='\u102A')) ) {
                alt32=224;
            }
            else if ( ((LA32_0>='\u102C' && LA32_0<='\u1032')) ) {
                alt32=225;
            }
            else if ( ((LA32_0>='\u1036' && LA32_0<='\u1039')) ) {
                alt32=226;
            }
            else if ( ((LA32_0>='\u1040' && LA32_0<='\u1049')) ) {
                alt32=227;
            }
            else if ( ((LA32_0>='\u1050' && LA32_0<='\u1059')) ) {
                alt32=228;
            }
            else if ( ((LA32_0>='\u10A0' && LA32_0<='\u10C5')) ) {
                alt32=229;
            }
            else if ( ((LA32_0>='\u10D0' && LA32_0<='\u10F8')) ) {
                alt32=230;
            }
            else if ( ((LA32_0>='\u1100' && LA32_0<='\u1159')) ) {
                alt32=231;
            }
            else if ( ((LA32_0>='\u115F' && LA32_0<='\u11A2')) ) {
                alt32=232;
            }
            else if ( ((LA32_0>='\u11A8' && LA32_0<='\u11F9')) ) {
                alt32=233;
            }
            else if ( ((LA32_0>='\u1200' && LA32_0<='\u1206')) ) {
                alt32=234;
            }
            else if ( ((LA32_0>='\u1208' && LA32_0<='\u1246')) ) {
                alt32=235;
            }
            else if ( (LA32_0=='\u1248') ) {
                alt32=236;
            }
            else if ( ((LA32_0>='\u124A' && LA32_0<='\u124D')) ) {
                alt32=237;
            }
            else if ( ((LA32_0>='\u1250' && LA32_0<='\u1256')) ) {
                alt32=238;
            }
            else if ( (LA32_0=='\u1258') ) {
                alt32=239;
            }
            else if ( ((LA32_0>='\u125A' && LA32_0<='\u125D')) ) {
                alt32=240;
            }
            else if ( ((LA32_0>='\u1260' && LA32_0<='\u1286')) ) {
                alt32=241;
            }
            else if ( (LA32_0=='\u1288') ) {
                alt32=242;
            }
            else if ( ((LA32_0>='\u128A' && LA32_0<='\u128D')) ) {
                alt32=243;
            }
            else if ( ((LA32_0>='\u1290' && LA32_0<='\u12AE')) ) {
                alt32=244;
            }
            else if ( (LA32_0=='\u12B0') ) {
                alt32=245;
            }
            else if ( ((LA32_0>='\u12B2' && LA32_0<='\u12B5')) ) {
                alt32=246;
            }
            else if ( ((LA32_0>='\u12B8' && LA32_0<='\u12BE')) ) {
                alt32=247;
            }
            else if ( (LA32_0=='\u12C0') ) {
                alt32=248;
            }
            else if ( ((LA32_0>='\u12C2' && LA32_0<='\u12C5')) ) {
                alt32=249;
            }
            else if ( ((LA32_0>='\u12C8' && LA32_0<='\u12CE')) ) {
                alt32=250;
            }
            else if ( ((LA32_0>='\u12D0' && LA32_0<='\u12D6')) ) {
                alt32=251;
            }
            else if ( ((LA32_0>='\u12D8' && LA32_0<='\u12EE')) ) {
                alt32=252;
            }
            else if ( ((LA32_0>='\u12F0' && LA32_0<='\u130E')) ) {
                alt32=253;
            }
            else if ( (LA32_0=='\u1310') ) {
                alt32=254;
            }
            else if ( ((LA32_0>='\u1312' && LA32_0<='\u1315')) ) {
                alt32=255;
            }
            else if ( ((LA32_0>='\u1318' && LA32_0<='\u131E')) ) {
                alt32=256;
            }
            else if ( ((LA32_0>='\u1320' && LA32_0<='\u1346')) ) {
                alt32=257;
            }
            else if ( ((LA32_0>='\u1348' && LA32_0<='\u135A')) ) {
                alt32=258;
            }
            else if ( ((LA32_0>='\u1369' && LA32_0<='\u1371')) ) {
                alt32=259;
            }
            else if ( ((LA32_0>='\u13A0' && LA32_0<='\u13F4')) ) {
                alt32=260;
            }
            else if ( ((LA32_0>='\u1401' && LA32_0<='\u166C')) ) {
                alt32=261;
            }
            else if ( ((LA32_0>='\u166F' && LA32_0<='\u1676')) ) {
                alt32=262;
            }
            else if ( ((LA32_0>='\u1681' && LA32_0<='\u169A')) ) {
                alt32=263;
            }
            else if ( ((LA32_0>='\u16A0' && LA32_0<='\u16EA')) ) {
                alt32=264;
            }
            else if ( ((LA32_0>='\u16EE' && LA32_0<='\u16F0')) ) {
                alt32=265;
            }
            else if ( ((LA32_0>='\u1700' && LA32_0<='\u170C')) ) {
                alt32=266;
            }
            else if ( ((LA32_0>='\u170E' && LA32_0<='\u1714')) ) {
                alt32=267;
            }
            else if ( ((LA32_0>='\u1720' && LA32_0<='\u1734')) ) {
                alt32=268;
            }
            else if ( ((LA32_0>='\u1740' && LA32_0<='\u1753')) ) {
                alt32=269;
            }
            else if ( ((LA32_0>='\u1760' && LA32_0<='\u176C')) ) {
                alt32=270;
            }
            else if ( ((LA32_0>='\u176E' && LA32_0<='\u1770')) ) {
                alt32=271;
            }
            else if ( ((LA32_0>='\u1772' && LA32_0<='\u1773')) ) {
                alt32=272;
            }
            else if ( ((LA32_0>='\u1780' && LA32_0<='\u17D3')) ) {
                alt32=273;
            }
            else if ( (LA32_0=='\u17D7') ) {
                alt32=274;
            }
            else if ( ((LA32_0>='\u17DB' && LA32_0<='\u17DD')) ) {
                alt32=275;
            }
            else if ( ((LA32_0>='\u17E0' && LA32_0<='\u17E9')) ) {
                alt32=276;
            }
            else if ( ((LA32_0>='\u180B' && LA32_0<='\u180D')) ) {
                alt32=277;
            }
            else if ( ((LA32_0>='\u1810' && LA32_0<='\u1819')) ) {
                alt32=278;
            }
            else if ( ((LA32_0>='\u1820' && LA32_0<='\u1877')) ) {
                alt32=279;
            }
            else if ( ((LA32_0>='\u1880' && LA32_0<='\u18A9')) ) {
                alt32=280;
            }
            else if ( ((LA32_0>='\u1900' && LA32_0<='\u191C')) ) {
                alt32=281;
            }
            else if ( ((LA32_0>='\u1920' && LA32_0<='\u192B')) ) {
                alt32=282;
            }
            else if ( ((LA32_0>='\u1930' && LA32_0<='\u193B')) ) {
                alt32=283;
            }
            else if ( ((LA32_0>='\u1946' && LA32_0<='\u196D')) ) {
                alt32=284;
            }
            else if ( ((LA32_0>='\u1970' && LA32_0<='\u1974')) ) {
                alt32=285;
            }
            else if ( ((LA32_0>='\u1D00' && LA32_0<='\u1D6B')) ) {
                alt32=286;
            }
            else if ( ((LA32_0>='\u1E00' && LA32_0<='\u1E9B')) ) {
                alt32=287;
            }
            else if ( ((LA32_0>='\u1EA0' && LA32_0<='\u1EF9')) ) {
                alt32=288;
            }
            else if ( ((LA32_0>='\u1F00' && LA32_0<='\u1F15')) ) {
                alt32=289;
            }
            else if ( ((LA32_0>='\u1F18' && LA32_0<='\u1F1D')) ) {
                alt32=290;
            }
            else if ( ((LA32_0>='\u1F20' && LA32_0<='\u1F45')) ) {
                alt32=291;
            }
            else if ( ((LA32_0>='\u1F48' && LA32_0<='\u1F4D')) ) {
                alt32=292;
            }
            else if ( ((LA32_0>='\u1F50' && LA32_0<='\u1F57')) ) {
                alt32=293;
            }
            else if ( (LA32_0=='\u1F59') ) {
                alt32=294;
            }
            else if ( (LA32_0=='\u1F5B') ) {
                alt32=295;
            }
            else if ( (LA32_0=='\u1F5D') ) {
                alt32=296;
            }
            else if ( ((LA32_0>='\u1F5F' && LA32_0<='\u1F7D')) ) {
                alt32=297;
            }
            else if ( ((LA32_0>='\u1F80' && LA32_0<='\u1FB4')) ) {
                alt32=298;
            }
            else if ( ((LA32_0>='\u1FB6' && LA32_0<='\u1FBC')) ) {
                alt32=299;
            }
            else if ( (LA32_0=='\u1FBE') ) {
                alt32=300;
            }
            else if ( ((LA32_0>='\u1FC2' && LA32_0<='\u1FC4')) ) {
                alt32=301;
            }
            else if ( ((LA32_0>='\u1FC6' && LA32_0<='\u1FCC')) ) {
                alt32=302;
            }
            else if ( ((LA32_0>='\u1FD0' && LA32_0<='\u1FD3')) ) {
                alt32=303;
            }
            else if ( ((LA32_0>='\u1FD6' && LA32_0<='\u1FDB')) ) {
                alt32=304;
            }
            else if ( ((LA32_0>='\u1FE0' && LA32_0<='\u1FEC')) ) {
                alt32=305;
            }
            else if ( ((LA32_0>='\u1FF2' && LA32_0<='\u1FF4')) ) {
                alt32=306;
            }
            else if ( ((LA32_0>='\u1FF6' && LA32_0<='\u1FFC')) ) {
                alt32=307;
            }
            else if ( ((LA32_0>='\u200C' && LA32_0<='\u200F')) ) {
                alt32=308;
            }
            else if ( ((LA32_0>='\u202A' && LA32_0<='\u202E')) ) {
                alt32=309;
            }
            else if ( ((LA32_0>='\u203F' && LA32_0<='\u2040')) ) {
                alt32=310;
            }
            else if ( (LA32_0=='\u2054') ) {
                alt32=311;
            }
            else if ( ((LA32_0>='\u2060' && LA32_0<='\u2063')) ) {
                alt32=312;
            }
            else if ( ((LA32_0>='\u206A' && LA32_0<='\u206F')) ) {
                alt32=313;
            }
            else if ( (LA32_0=='\u2071') ) {
                alt32=314;
            }
            else if ( (LA32_0=='\u207F') ) {
                alt32=315;
            }
            else if ( ((LA32_0>='\u20A0' && LA32_0<='\u20B1')) ) {
                alt32=316;
            }
            else if ( ((LA32_0>='\u20D0' && LA32_0<='\u20DC')) ) {
                alt32=317;
            }
            else if ( (LA32_0=='\u20E1') ) {
                alt32=318;
            }
            else if ( ((LA32_0>='\u20E5' && LA32_0<='\u20EA')) ) {
                alt32=319;
            }
            else if ( (LA32_0=='\u2102') ) {
                alt32=320;
            }
            else if ( (LA32_0=='\u2107') ) {
                alt32=321;
            }
            else if ( ((LA32_0>='\u210A' && LA32_0<='\u2113')) ) {
                alt32=322;
            }
            else if ( (LA32_0=='\u2115') ) {
                alt32=323;
            }
            else if ( ((LA32_0>='\u2119' && LA32_0<='\u211D')) ) {
                alt32=324;
            }
            else if ( (LA32_0=='\u2124') ) {
                alt32=325;
            }
            else if ( (LA32_0=='\u2126') ) {
                alt32=326;
            }
            else if ( (LA32_0=='\u2128') ) {
                alt32=327;
            }
            else if ( ((LA32_0>='\u212A' && LA32_0<='\u212D')) ) {
                alt32=328;
            }
            else if ( ((LA32_0>='\u212F' && LA32_0<='\u2131')) ) {
                alt32=329;
            }
            else if ( ((LA32_0>='\u2133' && LA32_0<='\u2139')) ) {
                alt32=330;
            }
            else if ( ((LA32_0>='\u213D' && LA32_0<='\u213F')) ) {
                alt32=331;
            }
            else if ( ((LA32_0>='\u2145' && LA32_0<='\u2149')) ) {
                alt32=332;
            }
            else if ( ((LA32_0>='\u2160' && LA32_0<='\u2183')) ) {
                alt32=333;
            }
            else if ( ((LA32_0>='\u3005' && LA32_0<='\u3007')) ) {
                alt32=334;
            }
            else if ( ((LA32_0>='\u3021' && LA32_0<='\u302F')) ) {
                alt32=335;
            }
            else if ( ((LA32_0>='\u3031' && LA32_0<='\u3035')) ) {
                alt32=336;
            }
            else if ( ((LA32_0>='\u3038' && LA32_0<='\u303C')) ) {
                alt32=337;
            }
            else if ( ((LA32_0>='\u3041' && LA32_0<='\u3096')) ) {
                alt32=338;
            }
            else if ( ((LA32_0>='\u3099' && LA32_0<='\u309A')) ) {
                alt32=339;
            }
            else if ( ((LA32_0>='\u309D' && LA32_0<='\u309F')) ) {
                alt32=340;
            }
            else if ( ((LA32_0>='\u30A1' && LA32_0<='\u30FF')) ) {
                alt32=341;
            }
            else if ( ((LA32_0>='\u3105' && LA32_0<='\u312C')) ) {
                alt32=342;
            }
            else if ( ((LA32_0>='\u3131' && LA32_0<='\u318E')) ) {
                alt32=343;
            }
            else if ( ((LA32_0>='\u31A0' && LA32_0<='\u31B7')) ) {
                alt32=344;
            }
            else if ( ((LA32_0>='\u31F0' && LA32_0<='\u31FF')) ) {
                alt32=345;
            }
            else if ( ((LA32_0>='\u3400' && LA32_0<='\u4DB5')) ) {
                alt32=346;
            }
            else if ( ((LA32_0>='\u4E00' && LA32_0<='\u9FA5')) ) {
                alt32=347;
            }
            else if ( ((LA32_0>='\uA000' && LA32_0<='\uA48C')) ) {
                alt32=348;
            }
            else if ( ((LA32_0>='\uAC00' && LA32_0<='\uD7A3')) ) {
                alt32=349;
            }
            else if ( ((LA32_0>='\uF900' && LA32_0<='\uFA2D')) ) {
                alt32=350;
            }
            else if ( ((LA32_0>='\uFA30' && LA32_0<='\uFA6A')) ) {
                alt32=351;
            }
            else if ( ((LA32_0>='\uFB00' && LA32_0<='\uFB06')) ) {
                alt32=352;
            }
            else if ( ((LA32_0>='\uFB13' && LA32_0<='\uFB17')) ) {
                alt32=353;
            }
            else if ( ((LA32_0>='\uFB1D' && LA32_0<='\uFB28')) ) {
                alt32=354;
            }
            else if ( ((LA32_0>='\uFB2A' && LA32_0<='\uFB36')) ) {
                alt32=355;
            }
            else if ( ((LA32_0>='\uFB38' && LA32_0<='\uFB3C')) ) {
                alt32=356;
            }
            else if ( (LA32_0=='\uFB3E') ) {
                alt32=357;
            }
            else if ( ((LA32_0>='\uFB40' && LA32_0<='\uFB41')) ) {
                alt32=358;
            }
            else if ( ((LA32_0>='\uFB43' && LA32_0<='\uFB44')) ) {
                alt32=359;
            }
            else if ( ((LA32_0>='\uFB46' && LA32_0<='\uFBB1')) ) {
                alt32=360;
            }
            else if ( ((LA32_0>='\uFBD3' && LA32_0<='\uFD3D')) ) {
                alt32=361;
            }
            else if ( ((LA32_0>='\uFD50' && LA32_0<='\uFD8F')) ) {
                alt32=362;
            }
            else if ( ((LA32_0>='\uFD92' && LA32_0<='\uFDC7')) ) {
                alt32=363;
            }
            else if ( ((LA32_0>='\uFDF0' && LA32_0<='\uFDFC')) ) {
                alt32=364;
            }
            else if ( ((LA32_0>='\uFE00' && LA32_0<='\uFE0F')) ) {
                alt32=365;
            }
            else if ( ((LA32_0>='\uFE20' && LA32_0<='\uFE23')) ) {
                alt32=366;
            }
            else if ( ((LA32_0>='\uFE33' && LA32_0<='\uFE34')) ) {
                alt32=367;
            }
            else if ( ((LA32_0>='\uFE4D' && LA32_0<='\uFE4F')) ) {
                alt32=368;
            }
            else if ( (LA32_0=='\uFE69') ) {
                alt32=369;
            }
            else if ( ((LA32_0>='\uFE70' && LA32_0<='\uFE74')) ) {
                alt32=370;
            }
            else if ( ((LA32_0>='\uFE76' && LA32_0<='\uFEFC')) ) {
                alt32=371;
            }
            else if ( (LA32_0=='\uFEFF') ) {
                alt32=372;
            }
            else if ( (LA32_0=='\uFF04') ) {
                alt32=373;
            }
            else if ( ((LA32_0>='\uFF10' && LA32_0<='\uFF19')) ) {
                alt32=374;
            }
            else if ( ((LA32_0>='\uFF21' && LA32_0<='\uFF3A')) ) {
                alt32=375;
            }
            else if ( (LA32_0=='\uFF3F') ) {
                alt32=376;
            }
            else if ( ((LA32_0>='\uFF41' && LA32_0<='\uFF5A')) ) {
                alt32=377;
            }
            else if ( ((LA32_0>='\uFF65' && LA32_0<='\uFFBE')) ) {
                alt32=378;
            }
            else if ( ((LA32_0>='\uFFC2' && LA32_0<='\uFFC7')) ) {
                alt32=379;
            }
            else if ( ((LA32_0>='\uFFCA' && LA32_0<='\uFFCF')) ) {
                alt32=380;
            }
            else if ( ((LA32_0>='\uFFD2' && LA32_0<='\uFFD7')) ) {
                alt32=381;
            }
            else if ( ((LA32_0>='\uFFDA' && LA32_0<='\uFFDC')) ) {
                alt32=382;
            }
            else if ( ((LA32_0>='\uFFE0' && LA32_0<='\uFFE1')) ) {
                alt32=383;
            }
            else if ( ((LA32_0>='\uFFE5' && LA32_0<='\uFFE6')) ) {
                alt32=384;
            }
            else if ( ((LA32_0>='\uFFF9' && LA32_0<='\uFFFB')) ) {
                alt32=385;
            }
            else if ( ((LA32_0>='\uD800' && LA32_0<='\uDBFF')) ) {
                alt32=386;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1279:1: fragment IdentifierPart : ( '\\u0000' .. '\\u0008' | '\\u000e' .. '\\u001b' | '\\u0024' | '\\u0030' .. '\\u0039' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u007f' .. '\\u009f' | '\\u00a2' .. '\\u00a5' | '\\u00aa' | '\\u00ad' | '\\u00b5' | '\\u00ba' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u0236' | '\\u0250' .. '\\u02c1' | '\\u02c6' .. '\\u02d1' | '\\u02e0' .. '\\u02e4' | '\\u02ee' | '\\u0300' .. '\\u0357' | '\\u035d' .. '\\u036f' | '\\u037a' | '\\u0386' | '\\u0388' .. '\\u038a' | '\\u038c' | '\\u038e' .. '\\u03a1' | '\\u03a3' .. '\\u03ce' | '\\u03d0' .. '\\u03f5' | '\\u03f7' .. '\\u03fb' | '\\u0400' .. '\\u0481' | '\\u0483' .. '\\u0486' | '\\u048a' .. '\\u04ce' | '\\u04d0' .. '\\u04f5' | '\\u04f8' .. '\\u04f9' | '\\u0500' .. '\\u050f' | '\\u0531' .. '\\u0556' | '\\u0559' | '\\u0561' .. '\\u0587' | '\\u0591' .. '\\u05a1' | '\\u05a3' .. '\\u05b9' | '\\u05bb' .. '\\u05bd' | '\\u05bf' | '\\u05c1' .. '\\u05c2' | '\\u05c4' | '\\u05d0' .. '\\u05ea' | '\\u05f0' .. '\\u05f2' | '\\u0600' .. '\\u0603' | '\\u0610' .. '\\u0615' | '\\u0621' .. '\\u063a' | '\\u0640' .. '\\u0658' | '\\u0660' .. '\\u0669' | '\\u066e' .. '\\u06d3' | '\\u06d5' .. '\\u06dd' | '\\u06df' .. '\\u06e8' | '\\u06ea' .. '\\u06fc' | '\\u06ff' | '\\u070f' .. '\\u074a' | '\\u074d' .. '\\u074f' | '\\u0780' .. '\\u07b1' | '\\u0901' .. '\\u0939' | '\\u093c' .. '\\u094d' | '\\u0950' .. '\\u0954' | '\\u0958' .. '\\u0963' | '\\u0966' .. '\\u096f' | '\\u0981' .. '\\u0983' | '\\u0985' .. '\\u098c' | '\\u098f' .. '\\u0990' | '\\u0993' .. '\\u09a8' | '\\u09aa' .. '\\u09b0' | '\\u09b2' | '\\u09b6' .. '\\u09b9' | '\\u09bc' .. '\\u09c4' | '\\u09c7' .. '\\u09c8' | '\\u09cb' .. '\\u09cd' | '\\u09d7' | '\\u09dc' .. '\\u09dd' | '\\u09df' .. '\\u09e3' | '\\u09e6' .. '\\u09f3' | '\\u0a01' .. '\\u0a03' | '\\u0a05' .. '\\u0a0a' | '\\u0a0f' .. '\\u0a10' | '\\u0a13' .. '\\u0a28' | '\\u0a2a' .. '\\u0a30' | '\\u0a32' .. '\\u0a33' | '\\u0a35' .. '\\u0a36' | '\\u0a38' .. '\\u0a39' | '\\u0a3c' | '\\u0a3e' .. '\\u0a42' | '\\u0a47' .. '\\u0a48' | '\\u0a4b' .. '\\u0a4d' | '\\u0a59' .. '\\u0a5c' | '\\u0a5e' | '\\u0a66' .. '\\u0a74' | '\\u0a81' .. '\\u0a83' | '\\u0a85' .. '\\u0a8d' | '\\u0a8f' .. '\\u0a91' | '\\u0a93' .. '\\u0aa8' | '\\u0aaa' .. '\\u0ab0' | '\\u0ab2' .. '\\u0ab3' | '\\u0ab5' .. '\\u0ab9' | '\\u0abc' .. '\\u0ac5' | '\\u0ac7' .. '\\u0ac9' | '\\u0acb' .. '\\u0acd' | '\\u0ad0' | '\\u0ae0' .. '\\u0ae3' | '\\u0ae6' .. '\\u0aef' | '\\u0af1' | '\\u0b01' .. '\\u0b03' | '\\u0b05' .. '\\u0b0c' | '\\u0b0f' .. '\\u0b10' | '\\u0b13' .. '\\u0b28' | '\\u0b2a' .. '\\u0b30' | '\\u0b32' .. '\\u0b33' | '\\u0b35' .. '\\u0b39' | '\\u0b3c' .. '\\u0b43' | '\\u0b47' .. '\\u0b48' | '\\u0b4b' .. '\\u0b4d' | '\\u0b56' .. '\\u0b57' | '\\u0b5c' .. '\\u0b5d' | '\\u0b5f' .. '\\u0b61' | '\\u0b66' .. '\\u0b6f' | '\\u0b71' | '\\u0b82' .. '\\u0b83' | '\\u0b85' .. '\\u0b8a' | '\\u0b8e' .. '\\u0b90' | '\\u0b92' .. '\\u0b95' | '\\u0b99' .. '\\u0b9a' | '\\u0b9c' | '\\u0b9e' .. '\\u0b9f' | '\\u0ba3' .. '\\u0ba4' | '\\u0ba8' .. '\\u0baa' | '\\u0bae' .. '\\u0bb5' | '\\u0bb7' .. '\\u0bb9' | '\\u0bbe' .. '\\u0bc2' | '\\u0bc6' .. '\\u0bc8' | '\\u0bca' .. '\\u0bcd' | '\\u0bd7' | '\\u0be7' .. '\\u0bef' | '\\u0bf9' | '\\u0c01' .. '\\u0c03' | '\\u0c05' .. '\\u0c0c' | '\\u0c0e' .. '\\u0c10' | '\\u0c12' .. '\\u0c28' | '\\u0c2a' .. '\\u0c33' | '\\u0c35' .. '\\u0c39' | '\\u0c3e' .. '\\u0c44' | '\\u0c46' .. '\\u0c48' | '\\u0c4a' .. '\\u0c4d' | '\\u0c55' .. '\\u0c56' | '\\u0c60' .. '\\u0c61' | '\\u0c66' .. '\\u0c6f' | '\\u0c82' .. '\\u0c83' | '\\u0c85' .. '\\u0c8c' | '\\u0c8e' .. '\\u0c90' | '\\u0c92' .. '\\u0ca8' | '\\u0caa' .. '\\u0cb3' | '\\u0cb5' .. '\\u0cb9' | '\\u0cbc' .. '\\u0cc4' | '\\u0cc6' .. '\\u0cc8' | '\\u0cca' .. '\\u0ccd' | '\\u0cd5' .. '\\u0cd6' | '\\u0cde' | '\\u0ce0' .. '\\u0ce1' | '\\u0ce6' .. '\\u0cef' | '\\u0d02' .. '\\u0d03' | '\\u0d05' .. '\\u0d0c' | '\\u0d0e' .. '\\u0d10' | '\\u0d12' .. '\\u0d28' | '\\u0d2a' .. '\\u0d39' | '\\u0d3e' .. '\\u0d43' | '\\u0d46' .. '\\u0d48' | '\\u0d4a' .. '\\u0d4d' | '\\u0d57' | '\\u0d60' .. '\\u0d61' | '\\u0d66' .. '\\u0d6f' | '\\u0d82' .. '\\u0d83' | '\\u0d85' .. '\\u0d96' | '\\u0d9a' .. '\\u0db1' | '\\u0db3' .. '\\u0dbb' | '\\u0dbd' | '\\u0dc0' .. '\\u0dc6' | '\\u0dca' | '\\u0dcf' .. '\\u0dd4' | '\\u0dd6' | '\\u0dd8' .. '\\u0ddf' | '\\u0df2' .. '\\u0df3' | '\\u0e01' .. '\\u0e3a' | '\\u0e3f' .. '\\u0e4e' | '\\u0e50' .. '\\u0e59' | '\\u0e81' .. '\\u0e82' | '\\u0e84' | '\\u0e87' .. '\\u0e88' | '\\u0e8a' | '\\u0e8d' | '\\u0e94' .. '\\u0e97' | '\\u0e99' .. '\\u0e9f' | '\\u0ea1' .. '\\u0ea3' | '\\u0ea5' | '\\u0ea7' | '\\u0eaa' .. '\\u0eab' | '\\u0ead' .. '\\u0eb9' | '\\u0ebb' .. '\\u0ebd' | '\\u0ec0' .. '\\u0ec4' | '\\u0ec6' | '\\u0ec8' .. '\\u0ecd' | '\\u0ed0' .. '\\u0ed9' | '\\u0edc' .. '\\u0edd' | '\\u0f00' | '\\u0f18' .. '\\u0f19' | '\\u0f20' .. '\\u0f29' | '\\u0f35' | '\\u0f37' | '\\u0f39' | '\\u0f3e' .. '\\u0f47' | '\\u0f49' .. '\\u0f6a' | '\\u0f71' .. '\\u0f84' | '\\u0f86' .. '\\u0f8b' | '\\u0f90' .. '\\u0f97' | '\\u0f99' .. '\\u0fbc' | '\\u0fc6' | '\\u1000' .. '\\u1021' | '\\u1023' .. '\\u1027' | '\\u1029' .. '\\u102a' | '\\u102c' .. '\\u1032' | '\\u1036' .. '\\u1039' | '\\u1040' .. '\\u1049' | '\\u1050' .. '\\u1059' | '\\u10a0' .. '\\u10c5' | '\\u10d0' .. '\\u10f8' | '\\u1100' .. '\\u1159' | '\\u115f' .. '\\u11a2' | '\\u11a8' .. '\\u11f9' | '\\u1200' .. '\\u1206' | '\\u1208' .. '\\u1246' | '\\u1248' | '\\u124a' .. '\\u124d' | '\\u1250' .. '\\u1256' | '\\u1258' | '\\u125a' .. '\\u125d' | '\\u1260' .. '\\u1286' | '\\u1288' | '\\u128a' .. '\\u128d' | '\\u1290' .. '\\u12ae' | '\\u12b0' | '\\u12b2' .. '\\u12b5' | '\\u12b8' .. '\\u12be' | '\\u12c0' | '\\u12c2' .. '\\u12c5' | '\\u12c8' .. '\\u12ce' | '\\u12d0' .. '\\u12d6' | '\\u12d8' .. '\\u12ee' | '\\u12f0' .. '\\u130e' | '\\u1310' | '\\u1312' .. '\\u1315' | '\\u1318' .. '\\u131e' | '\\u1320' .. '\\u1346' | '\\u1348' .. '\\u135a' | '\\u1369' .. '\\u1371' | '\\u13a0' .. '\\u13f4' | '\\u1401' .. '\\u166c' | '\\u166f' .. '\\u1676' | '\\u1681' .. '\\u169a' | '\\u16a0' .. '\\u16ea' | '\\u16ee' .. '\\u16f0' | '\\u1700' .. '\\u170c' | '\\u170e' .. '\\u1714' | '\\u1720' .. '\\u1734' | '\\u1740' .. '\\u1753' | '\\u1760' .. '\\u176c' | '\\u176e' .. '\\u1770' | '\\u1772' .. '\\u1773' | '\\u1780' .. '\\u17d3' | '\\u17d7' | '\\u17db' .. '\\u17dd' | '\\u17e0' .. '\\u17e9' | '\\u180b' .. '\\u180d' | '\\u1810' .. '\\u1819' | '\\u1820' .. '\\u1877' | '\\u1880' .. '\\u18a9' | '\\u1900' .. '\\u191c' | '\\u1920' .. '\\u192b' | '\\u1930' .. '\\u193b' | '\\u1946' .. '\\u196d' | '\\u1970' .. '\\u1974' | '\\u1d00' .. '\\u1d6b' | '\\u1e00' .. '\\u1e9b' | '\\u1ea0' .. '\\u1ef9' | '\\u1f00' .. '\\u1f15' | '\\u1f18' .. '\\u1f1d' | '\\u1f20' .. '\\u1f45' | '\\u1f48' .. '\\u1f4d' | '\\u1f50' .. '\\u1f57' | '\\u1f59' | '\\u1f5b' | '\\u1f5d' | '\\u1f5f' .. '\\u1f7d' | '\\u1f80' .. '\\u1fb4' | '\\u1fb6' .. '\\u1fbc' | '\\u1fbe' | '\\u1fc2' .. '\\u1fc4' | '\\u1fc6' .. '\\u1fcc' | '\\u1fd0' .. '\\u1fd3' | '\\u1fd6' .. '\\u1fdb' | '\\u1fe0' .. '\\u1fec' | '\\u1ff2' .. '\\u1ff4' | '\\u1ff6' .. '\\u1ffc' | '\\u200c' .. '\\u200f' | '\\u202a' .. '\\u202e' | '\\u203f' .. '\\u2040' | '\\u2054' | '\\u2060' .. '\\u2063' | '\\u206a' .. '\\u206f' | '\\u2071' | '\\u207f' | '\\u20a0' .. '\\u20b1' | '\\u20d0' .. '\\u20dc' | '\\u20e1' | '\\u20e5' .. '\\u20ea' | '\\u2102' | '\\u2107' | '\\u210a' .. '\\u2113' | '\\u2115' | '\\u2119' .. '\\u211d' | '\\u2124' | '\\u2126' | '\\u2128' | '\\u212a' .. '\\u212d' | '\\u212f' .. '\\u2131' | '\\u2133' .. '\\u2139' | '\\u213d' .. '\\u213f' | '\\u2145' .. '\\u2149' | '\\u2160' .. '\\u2183' | '\\u3005' .. '\\u3007' | '\\u3021' .. '\\u302f' | '\\u3031' .. '\\u3035' | '\\u3038' .. '\\u303c' | '\\u3041' .. '\\u3096' | '\\u3099' .. '\\u309a' | '\\u309d' .. '\\u309f' | '\\u30a1' .. '\\u30ff' | '\\u3105' .. '\\u312c' | '\\u3131' .. '\\u318e' | '\\u31a0' .. '\\u31b7' | '\\u31f0' .. '\\u31ff' | '\\u3400' .. '\\u4db5' | '\\u4e00' .. '\\u9fa5' | '\\ua000' .. '\\ua48c' | '\\uac00' .. '\\ud7a3' | '\\uf900' .. '\\ufa2d' | '\\ufa30' .. '\\ufa6a' | '\\ufb00' .. '\\ufb06' | '\\ufb13' .. '\\ufb17' | '\\ufb1d' .. '\\ufb28' | '\\ufb2a' .. '\\ufb36' | '\\ufb38' .. '\\ufb3c' | '\\ufb3e' | '\\ufb40' .. '\\ufb41' | '\\ufb43' .. '\\ufb44' | '\\ufb46' .. '\\ufbb1' | '\\ufbd3' .. '\\ufd3d' | '\\ufd50' .. '\\ufd8f' | '\\ufd92' .. '\\ufdc7' | '\\ufdf0' .. '\\ufdfc' | '\\ufe00' .. '\\ufe0f' | '\\ufe20' .. '\\ufe23' | '\\ufe33' .. '\\ufe34' | '\\ufe4d' .. '\\ufe4f' | '\\ufe69' | '\\ufe70' .. '\\ufe74' | '\\ufe76' .. '\\ufefc' | '\\ufeff' | '\\uff04' | '\\uff10' .. '\\uff19' | '\\uff21' .. '\\uff3a' | '\\uff3f' | '\\uff41' .. '\\uff5a' | '\\uff65' .. '\\uffbe' | '\\uffc2' .. '\\uffc7' | '\\uffca' .. '\\uffcf' | '\\uffd2' .. '\\uffd7' | '\\uffda' .. '\\uffdc' | '\\uffe0' .. '\\uffe1' | '\\uffe5' .. '\\uffe6' | '\\ufff9' .. '\\ufffb' | ( '\\ud800' .. '\\udbff' ) ( '\\udc00' .. '\\udfff' ) );", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1281:9: '\\u0000' .. '\\u0008'
                    {
                    matchRange('\u0000','\b'); 

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1282:9: '\\u000e' .. '\\u001b'
                    {
                    matchRange('\u000E','\u001B'); 

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1283:9: '\\u0024'
                    {
                    match('$'); 

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1284:9: '\\u0030' .. '\\u0039'
                    {
                    matchRange('0','9'); 

                    }
                    break;
                case 5 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1285:9: '\\u0041' .. '\\u005a'
                    {
                    matchRange('A','Z'); 

                    }
                    break;
                case 6 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1286:9: '\\u005f'
                    {
                    match('_'); 

                    }
                    break;
                case 7 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1287:9: '\\u0061' .. '\\u007a'
                    {
                    matchRange('a','z'); 

                    }
                    break;
                case 8 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1288:9: '\\u007f' .. '\\u009f'
                    {
                    matchRange('\u007F','\u009F'); 

                    }
                    break;
                case 9 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1289:9: '\\u00a2' .. '\\u00a5'
                    {
                    matchRange('\u00A2','\u00A5'); 

                    }
                    break;
                case 10 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1290:9: '\\u00aa'
                    {
                    match('\u00AA'); 

                    }
                    break;
                case 11 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1291:9: '\\u00ad'
                    {
                    match('\u00AD'); 

                    }
                    break;
                case 12 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1292:9: '\\u00b5'
                    {
                    match('\u00B5'); 

                    }
                    break;
                case 13 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1293:9: '\\u00ba'
                    {
                    match('\u00BA'); 

                    }
                    break;
                case 14 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1294:9: '\\u00c0' .. '\\u00d6'
                    {
                    matchRange('\u00C0','\u00D6'); 

                    }
                    break;
                case 15 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1295:9: '\\u00d8' .. '\\u00f6'
                    {
                    matchRange('\u00D8','\u00F6'); 

                    }
                    break;
                case 16 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1296:9: '\\u00f8' .. '\\u0236'
                    {
                    matchRange('\u00F8','\u0236'); 

                    }
                    break;
                case 17 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1297:9: '\\u0250' .. '\\u02c1'
                    {
                    matchRange('\u0250','\u02C1'); 

                    }
                    break;
                case 18 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1298:9: '\\u02c6' .. '\\u02d1'
                    {
                    matchRange('\u02C6','\u02D1'); 

                    }
                    break;
                case 19 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1299:9: '\\u02e0' .. '\\u02e4'
                    {
                    matchRange('\u02E0','\u02E4'); 

                    }
                    break;
                case 20 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1300:9: '\\u02ee'
                    {
                    match('\u02EE'); 

                    }
                    break;
                case 21 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1301:9: '\\u0300' .. '\\u0357'
                    {
                    matchRange('\u0300','\u0357'); 

                    }
                    break;
                case 22 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1302:9: '\\u035d' .. '\\u036f'
                    {
                    matchRange('\u035D','\u036F'); 

                    }
                    break;
                case 23 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1303:9: '\\u037a'
                    {
                    match('\u037A'); 

                    }
                    break;
                case 24 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1304:9: '\\u0386'
                    {
                    match('\u0386'); 

                    }
                    break;
                case 25 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1305:9: '\\u0388' .. '\\u038a'
                    {
                    matchRange('\u0388','\u038A'); 

                    }
                    break;
                case 26 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1306:9: '\\u038c'
                    {
                    match('\u038C'); 

                    }
                    break;
                case 27 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1307:9: '\\u038e' .. '\\u03a1'
                    {
                    matchRange('\u038E','\u03A1'); 

                    }
                    break;
                case 28 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1308:9: '\\u03a3' .. '\\u03ce'
                    {
                    matchRange('\u03A3','\u03CE'); 

                    }
                    break;
                case 29 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1309:9: '\\u03d0' .. '\\u03f5'
                    {
                    matchRange('\u03D0','\u03F5'); 

                    }
                    break;
                case 30 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1310:9: '\\u03f7' .. '\\u03fb'
                    {
                    matchRange('\u03F7','\u03FB'); 

                    }
                    break;
                case 31 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1311:9: '\\u0400' .. '\\u0481'
                    {
                    matchRange('\u0400','\u0481'); 

                    }
                    break;
                case 32 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1312:9: '\\u0483' .. '\\u0486'
                    {
                    matchRange('\u0483','\u0486'); 

                    }
                    break;
                case 33 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1313:9: '\\u048a' .. '\\u04ce'
                    {
                    matchRange('\u048A','\u04CE'); 

                    }
                    break;
                case 34 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1314:9: '\\u04d0' .. '\\u04f5'
                    {
                    matchRange('\u04D0','\u04F5'); 

                    }
                    break;
                case 35 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1315:9: '\\u04f8' .. '\\u04f9'
                    {
                    matchRange('\u04F8','\u04F9'); 

                    }
                    break;
                case 36 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1316:9: '\\u0500' .. '\\u050f'
                    {
                    matchRange('\u0500','\u050F'); 

                    }
                    break;
                case 37 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1317:9: '\\u0531' .. '\\u0556'
                    {
                    matchRange('\u0531','\u0556'); 

                    }
                    break;
                case 38 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1318:9: '\\u0559'
                    {
                    match('\u0559'); 

                    }
                    break;
                case 39 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1319:9: '\\u0561' .. '\\u0587'
                    {
                    matchRange('\u0561','\u0587'); 

                    }
                    break;
                case 40 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1320:9: '\\u0591' .. '\\u05a1'
                    {
                    matchRange('\u0591','\u05A1'); 

                    }
                    break;
                case 41 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1321:9: '\\u05a3' .. '\\u05b9'
                    {
                    matchRange('\u05A3','\u05B9'); 

                    }
                    break;
                case 42 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1322:9: '\\u05bb' .. '\\u05bd'
                    {
                    matchRange('\u05BB','\u05BD'); 

                    }
                    break;
                case 43 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1323:9: '\\u05bf'
                    {
                    match('\u05BF'); 

                    }
                    break;
                case 44 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1324:9: '\\u05c1' .. '\\u05c2'
                    {
                    matchRange('\u05C1','\u05C2'); 

                    }
                    break;
                case 45 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1325:9: '\\u05c4'
                    {
                    match('\u05C4'); 

                    }
                    break;
                case 46 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1326:9: '\\u05d0' .. '\\u05ea'
                    {
                    matchRange('\u05D0','\u05EA'); 

                    }
                    break;
                case 47 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1327:9: '\\u05f0' .. '\\u05f2'
                    {
                    matchRange('\u05F0','\u05F2'); 

                    }
                    break;
                case 48 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1328:9: '\\u0600' .. '\\u0603'
                    {
                    matchRange('\u0600','\u0603'); 

                    }
                    break;
                case 49 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1329:9: '\\u0610' .. '\\u0615'
                    {
                    matchRange('\u0610','\u0615'); 

                    }
                    break;
                case 50 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1330:9: '\\u0621' .. '\\u063a'
                    {
                    matchRange('\u0621','\u063A'); 

                    }
                    break;
                case 51 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1331:9: '\\u0640' .. '\\u0658'
                    {
                    matchRange('\u0640','\u0658'); 

                    }
                    break;
                case 52 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1332:9: '\\u0660' .. '\\u0669'
                    {
                    matchRange('\u0660','\u0669'); 

                    }
                    break;
                case 53 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1333:9: '\\u066e' .. '\\u06d3'
                    {
                    matchRange('\u066E','\u06D3'); 

                    }
                    break;
                case 54 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1334:9: '\\u06d5' .. '\\u06dd'
                    {
                    matchRange('\u06D5','\u06DD'); 

                    }
                    break;
                case 55 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1335:9: '\\u06df' .. '\\u06e8'
                    {
                    matchRange('\u06DF','\u06E8'); 

                    }
                    break;
                case 56 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1336:9: '\\u06ea' .. '\\u06fc'
                    {
                    matchRange('\u06EA','\u06FC'); 

                    }
                    break;
                case 57 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1337:9: '\\u06ff'
                    {
                    match('\u06FF'); 

                    }
                    break;
                case 58 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1338:9: '\\u070f' .. '\\u074a'
                    {
                    matchRange('\u070F','\u074A'); 

                    }
                    break;
                case 59 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1339:9: '\\u074d' .. '\\u074f'
                    {
                    matchRange('\u074D','\u074F'); 

                    }
                    break;
                case 60 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1340:9: '\\u0780' .. '\\u07b1'
                    {
                    matchRange('\u0780','\u07B1'); 

                    }
                    break;
                case 61 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1341:9: '\\u0901' .. '\\u0939'
                    {
                    matchRange('\u0901','\u0939'); 

                    }
                    break;
                case 62 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1342:9: '\\u093c' .. '\\u094d'
                    {
                    matchRange('\u093C','\u094D'); 

                    }
                    break;
                case 63 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1343:9: '\\u0950' .. '\\u0954'
                    {
                    matchRange('\u0950','\u0954'); 

                    }
                    break;
                case 64 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1344:9: '\\u0958' .. '\\u0963'
                    {
                    matchRange('\u0958','\u0963'); 

                    }
                    break;
                case 65 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1345:9: '\\u0966' .. '\\u096f'
                    {
                    matchRange('\u0966','\u096F'); 

                    }
                    break;
                case 66 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1346:9: '\\u0981' .. '\\u0983'
                    {
                    matchRange('\u0981','\u0983'); 

                    }
                    break;
                case 67 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1347:9: '\\u0985' .. '\\u098c'
                    {
                    matchRange('\u0985','\u098C'); 

                    }
                    break;
                case 68 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1348:9: '\\u098f' .. '\\u0990'
                    {
                    matchRange('\u098F','\u0990'); 

                    }
                    break;
                case 69 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1349:9: '\\u0993' .. '\\u09a8'
                    {
                    matchRange('\u0993','\u09A8'); 

                    }
                    break;
                case 70 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1350:9: '\\u09aa' .. '\\u09b0'
                    {
                    matchRange('\u09AA','\u09B0'); 

                    }
                    break;
                case 71 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1351:9: '\\u09b2'
                    {
                    match('\u09B2'); 

                    }
                    break;
                case 72 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1352:9: '\\u09b6' .. '\\u09b9'
                    {
                    matchRange('\u09B6','\u09B9'); 

                    }
                    break;
                case 73 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1353:9: '\\u09bc' .. '\\u09c4'
                    {
                    matchRange('\u09BC','\u09C4'); 

                    }
                    break;
                case 74 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1354:9: '\\u09c7' .. '\\u09c8'
                    {
                    matchRange('\u09C7','\u09C8'); 

                    }
                    break;
                case 75 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1355:9: '\\u09cb' .. '\\u09cd'
                    {
                    matchRange('\u09CB','\u09CD'); 

                    }
                    break;
                case 76 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1356:9: '\\u09d7'
                    {
                    match('\u09D7'); 

                    }
                    break;
                case 77 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1357:9: '\\u09dc' .. '\\u09dd'
                    {
                    matchRange('\u09DC','\u09DD'); 

                    }
                    break;
                case 78 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1358:9: '\\u09df' .. '\\u09e3'
                    {
                    matchRange('\u09DF','\u09E3'); 

                    }
                    break;
                case 79 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1359:9: '\\u09e6' .. '\\u09f3'
                    {
                    matchRange('\u09E6','\u09F3'); 

                    }
                    break;
                case 80 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1360:9: '\\u0a01' .. '\\u0a03'
                    {
                    matchRange('\u0A01','\u0A03'); 

                    }
                    break;
                case 81 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1361:9: '\\u0a05' .. '\\u0a0a'
                    {
                    matchRange('\u0A05','\u0A0A'); 

                    }
                    break;
                case 82 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1362:9: '\\u0a0f' .. '\\u0a10'
                    {
                    matchRange('\u0A0F','\u0A10'); 

                    }
                    break;
                case 83 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1363:9: '\\u0a13' .. '\\u0a28'
                    {
                    matchRange('\u0A13','\u0A28'); 

                    }
                    break;
                case 84 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1364:9: '\\u0a2a' .. '\\u0a30'
                    {
                    matchRange('\u0A2A','\u0A30'); 

                    }
                    break;
                case 85 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1365:9: '\\u0a32' .. '\\u0a33'
                    {
                    matchRange('\u0A32','\u0A33'); 

                    }
                    break;
                case 86 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1366:9: '\\u0a35' .. '\\u0a36'
                    {
                    matchRange('\u0A35','\u0A36'); 

                    }
                    break;
                case 87 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1367:9: '\\u0a38' .. '\\u0a39'
                    {
                    matchRange('\u0A38','\u0A39'); 

                    }
                    break;
                case 88 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1368:9: '\\u0a3c'
                    {
                    match('\u0A3C'); 

                    }
                    break;
                case 89 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1369:9: '\\u0a3e' .. '\\u0a42'
                    {
                    matchRange('\u0A3E','\u0A42'); 

                    }
                    break;
                case 90 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1370:9: '\\u0a47' .. '\\u0a48'
                    {
                    matchRange('\u0A47','\u0A48'); 

                    }
                    break;
                case 91 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1371:9: '\\u0a4b' .. '\\u0a4d'
                    {
                    matchRange('\u0A4B','\u0A4D'); 

                    }
                    break;
                case 92 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1372:9: '\\u0a59' .. '\\u0a5c'
                    {
                    matchRange('\u0A59','\u0A5C'); 

                    }
                    break;
                case 93 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1373:9: '\\u0a5e'
                    {
                    match('\u0A5E'); 

                    }
                    break;
                case 94 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1374:9: '\\u0a66' .. '\\u0a74'
                    {
                    matchRange('\u0A66','\u0A74'); 

                    }
                    break;
                case 95 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1375:9: '\\u0a81' .. '\\u0a83'
                    {
                    matchRange('\u0A81','\u0A83'); 

                    }
                    break;
                case 96 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1376:9: '\\u0a85' .. '\\u0a8d'
                    {
                    matchRange('\u0A85','\u0A8D'); 

                    }
                    break;
                case 97 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1377:9: '\\u0a8f' .. '\\u0a91'
                    {
                    matchRange('\u0A8F','\u0A91'); 

                    }
                    break;
                case 98 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1378:9: '\\u0a93' .. '\\u0aa8'
                    {
                    matchRange('\u0A93','\u0AA8'); 

                    }
                    break;
                case 99 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1379:9: '\\u0aaa' .. '\\u0ab0'
                    {
                    matchRange('\u0AAA','\u0AB0'); 

                    }
                    break;
                case 100 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1380:9: '\\u0ab2' .. '\\u0ab3'
                    {
                    matchRange('\u0AB2','\u0AB3'); 

                    }
                    break;
                case 101 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1381:9: '\\u0ab5' .. '\\u0ab9'
                    {
                    matchRange('\u0AB5','\u0AB9'); 

                    }
                    break;
                case 102 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1382:9: '\\u0abc' .. '\\u0ac5'
                    {
                    matchRange('\u0ABC','\u0AC5'); 

                    }
                    break;
                case 103 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1383:9: '\\u0ac7' .. '\\u0ac9'
                    {
                    matchRange('\u0AC7','\u0AC9'); 

                    }
                    break;
                case 104 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1384:9: '\\u0acb' .. '\\u0acd'
                    {
                    matchRange('\u0ACB','\u0ACD'); 

                    }
                    break;
                case 105 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1385:9: '\\u0ad0'
                    {
                    match('\u0AD0'); 

                    }
                    break;
                case 106 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1386:9: '\\u0ae0' .. '\\u0ae3'
                    {
                    matchRange('\u0AE0','\u0AE3'); 

                    }
                    break;
                case 107 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1387:9: '\\u0ae6' .. '\\u0aef'
                    {
                    matchRange('\u0AE6','\u0AEF'); 

                    }
                    break;
                case 108 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1388:9: '\\u0af1'
                    {
                    match('\u0AF1'); 

                    }
                    break;
                case 109 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1389:9: '\\u0b01' .. '\\u0b03'
                    {
                    matchRange('\u0B01','\u0B03'); 

                    }
                    break;
                case 110 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1390:9: '\\u0b05' .. '\\u0b0c'
                    {
                    matchRange('\u0B05','\u0B0C'); 

                    }
                    break;
                case 111 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1391:9: '\\u0b0f' .. '\\u0b10'
                    {
                    matchRange('\u0B0F','\u0B10'); 

                    }
                    break;
                case 112 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1392:9: '\\u0b13' .. '\\u0b28'
                    {
                    matchRange('\u0B13','\u0B28'); 

                    }
                    break;
                case 113 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1393:9: '\\u0b2a' .. '\\u0b30'
                    {
                    matchRange('\u0B2A','\u0B30'); 

                    }
                    break;
                case 114 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1394:9: '\\u0b32' .. '\\u0b33'
                    {
                    matchRange('\u0B32','\u0B33'); 

                    }
                    break;
                case 115 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1395:9: '\\u0b35' .. '\\u0b39'
                    {
                    matchRange('\u0B35','\u0B39'); 

                    }
                    break;
                case 116 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1396:9: '\\u0b3c' .. '\\u0b43'
                    {
                    matchRange('\u0B3C','\u0B43'); 

                    }
                    break;
                case 117 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1397:9: '\\u0b47' .. '\\u0b48'
                    {
                    matchRange('\u0B47','\u0B48'); 

                    }
                    break;
                case 118 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1398:9: '\\u0b4b' .. '\\u0b4d'
                    {
                    matchRange('\u0B4B','\u0B4D'); 

                    }
                    break;
                case 119 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1399:9: '\\u0b56' .. '\\u0b57'
                    {
                    matchRange('\u0B56','\u0B57'); 

                    }
                    break;
                case 120 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1400:9: '\\u0b5c' .. '\\u0b5d'
                    {
                    matchRange('\u0B5C','\u0B5D'); 

                    }
                    break;
                case 121 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1401:9: '\\u0b5f' .. '\\u0b61'
                    {
                    matchRange('\u0B5F','\u0B61'); 

                    }
                    break;
                case 122 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1402:9: '\\u0b66' .. '\\u0b6f'
                    {
                    matchRange('\u0B66','\u0B6F'); 

                    }
                    break;
                case 123 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1403:9: '\\u0b71'
                    {
                    match('\u0B71'); 

                    }
                    break;
                case 124 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1404:9: '\\u0b82' .. '\\u0b83'
                    {
                    matchRange('\u0B82','\u0B83'); 

                    }
                    break;
                case 125 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1405:9: '\\u0b85' .. '\\u0b8a'
                    {
                    matchRange('\u0B85','\u0B8A'); 

                    }
                    break;
                case 126 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1406:9: '\\u0b8e' .. '\\u0b90'
                    {
                    matchRange('\u0B8E','\u0B90'); 

                    }
                    break;
                case 127 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1407:9: '\\u0b92' .. '\\u0b95'
                    {
                    matchRange('\u0B92','\u0B95'); 

                    }
                    break;
                case 128 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1408:9: '\\u0b99' .. '\\u0b9a'
                    {
                    matchRange('\u0B99','\u0B9A'); 

                    }
                    break;
                case 129 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1409:9: '\\u0b9c'
                    {
                    match('\u0B9C'); 

                    }
                    break;
                case 130 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1410:9: '\\u0b9e' .. '\\u0b9f'
                    {
                    matchRange('\u0B9E','\u0B9F'); 

                    }
                    break;
                case 131 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1411:9: '\\u0ba3' .. '\\u0ba4'
                    {
                    matchRange('\u0BA3','\u0BA4'); 

                    }
                    break;
                case 132 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1412:9: '\\u0ba8' .. '\\u0baa'
                    {
                    matchRange('\u0BA8','\u0BAA'); 

                    }
                    break;
                case 133 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1413:9: '\\u0bae' .. '\\u0bb5'
                    {
                    matchRange('\u0BAE','\u0BB5'); 

                    }
                    break;
                case 134 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1414:9: '\\u0bb7' .. '\\u0bb9'
                    {
                    matchRange('\u0BB7','\u0BB9'); 

                    }
                    break;
                case 135 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1415:9: '\\u0bbe' .. '\\u0bc2'
                    {
                    matchRange('\u0BBE','\u0BC2'); 

                    }
                    break;
                case 136 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1416:9: '\\u0bc6' .. '\\u0bc8'
                    {
                    matchRange('\u0BC6','\u0BC8'); 

                    }
                    break;
                case 137 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1417:9: '\\u0bca' .. '\\u0bcd'
                    {
                    matchRange('\u0BCA','\u0BCD'); 

                    }
                    break;
                case 138 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1418:9: '\\u0bd7'
                    {
                    match('\u0BD7'); 

                    }
                    break;
                case 139 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1419:9: '\\u0be7' .. '\\u0bef'
                    {
                    matchRange('\u0BE7','\u0BEF'); 

                    }
                    break;
                case 140 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1420:9: '\\u0bf9'
                    {
                    match('\u0BF9'); 

                    }
                    break;
                case 141 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1421:9: '\\u0c01' .. '\\u0c03'
                    {
                    matchRange('\u0C01','\u0C03'); 

                    }
                    break;
                case 142 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1422:9: '\\u0c05' .. '\\u0c0c'
                    {
                    matchRange('\u0C05','\u0C0C'); 

                    }
                    break;
                case 143 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1423:9: '\\u0c0e' .. '\\u0c10'
                    {
                    matchRange('\u0C0E','\u0C10'); 

                    }
                    break;
                case 144 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1424:9: '\\u0c12' .. '\\u0c28'
                    {
                    matchRange('\u0C12','\u0C28'); 

                    }
                    break;
                case 145 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1425:9: '\\u0c2a' .. '\\u0c33'
                    {
                    matchRange('\u0C2A','\u0C33'); 

                    }
                    break;
                case 146 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1426:9: '\\u0c35' .. '\\u0c39'
                    {
                    matchRange('\u0C35','\u0C39'); 

                    }
                    break;
                case 147 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1427:9: '\\u0c3e' .. '\\u0c44'
                    {
                    matchRange('\u0C3E','\u0C44'); 

                    }
                    break;
                case 148 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1428:9: '\\u0c46' .. '\\u0c48'
                    {
                    matchRange('\u0C46','\u0C48'); 

                    }
                    break;
                case 149 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1429:9: '\\u0c4a' .. '\\u0c4d'
                    {
                    matchRange('\u0C4A','\u0C4D'); 

                    }
                    break;
                case 150 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1430:9: '\\u0c55' .. '\\u0c56'
                    {
                    matchRange('\u0C55','\u0C56'); 

                    }
                    break;
                case 151 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1431:9: '\\u0c60' .. '\\u0c61'
                    {
                    matchRange('\u0C60','\u0C61'); 

                    }
                    break;
                case 152 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1432:9: '\\u0c66' .. '\\u0c6f'
                    {
                    matchRange('\u0C66','\u0C6F'); 

                    }
                    break;
                case 153 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1433:9: '\\u0c82' .. '\\u0c83'
                    {
                    matchRange('\u0C82','\u0C83'); 

                    }
                    break;
                case 154 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1434:9: '\\u0c85' .. '\\u0c8c'
                    {
                    matchRange('\u0C85','\u0C8C'); 

                    }
                    break;
                case 155 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1435:9: '\\u0c8e' .. '\\u0c90'
                    {
                    matchRange('\u0C8E','\u0C90'); 

                    }
                    break;
                case 156 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1436:9: '\\u0c92' .. '\\u0ca8'
                    {
                    matchRange('\u0C92','\u0CA8'); 

                    }
                    break;
                case 157 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1437:9: '\\u0caa' .. '\\u0cb3'
                    {
                    matchRange('\u0CAA','\u0CB3'); 

                    }
                    break;
                case 158 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1438:9: '\\u0cb5' .. '\\u0cb9'
                    {
                    matchRange('\u0CB5','\u0CB9'); 

                    }
                    break;
                case 159 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1439:9: '\\u0cbc' .. '\\u0cc4'
                    {
                    matchRange('\u0CBC','\u0CC4'); 

                    }
                    break;
                case 160 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1440:9: '\\u0cc6' .. '\\u0cc8'
                    {
                    matchRange('\u0CC6','\u0CC8'); 

                    }
                    break;
                case 161 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1441:9: '\\u0cca' .. '\\u0ccd'
                    {
                    matchRange('\u0CCA','\u0CCD'); 

                    }
                    break;
                case 162 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1442:9: '\\u0cd5' .. '\\u0cd6'
                    {
                    matchRange('\u0CD5','\u0CD6'); 

                    }
                    break;
                case 163 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1443:9: '\\u0cde'
                    {
                    match('\u0CDE'); 

                    }
                    break;
                case 164 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1444:9: '\\u0ce0' .. '\\u0ce1'
                    {
                    matchRange('\u0CE0','\u0CE1'); 

                    }
                    break;
                case 165 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1445:9: '\\u0ce6' .. '\\u0cef'
                    {
                    matchRange('\u0CE6','\u0CEF'); 

                    }
                    break;
                case 166 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1446:9: '\\u0d02' .. '\\u0d03'
                    {
                    matchRange('\u0D02','\u0D03'); 

                    }
                    break;
                case 167 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1447:9: '\\u0d05' .. '\\u0d0c'
                    {
                    matchRange('\u0D05','\u0D0C'); 

                    }
                    break;
                case 168 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1448:9: '\\u0d0e' .. '\\u0d10'
                    {
                    matchRange('\u0D0E','\u0D10'); 

                    }
                    break;
                case 169 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1449:9: '\\u0d12' .. '\\u0d28'
                    {
                    matchRange('\u0D12','\u0D28'); 

                    }
                    break;
                case 170 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1450:9: '\\u0d2a' .. '\\u0d39'
                    {
                    matchRange('\u0D2A','\u0D39'); 

                    }
                    break;
                case 171 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1451:9: '\\u0d3e' .. '\\u0d43'
                    {
                    matchRange('\u0D3E','\u0D43'); 

                    }
                    break;
                case 172 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1452:9: '\\u0d46' .. '\\u0d48'
                    {
                    matchRange('\u0D46','\u0D48'); 

                    }
                    break;
                case 173 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1453:9: '\\u0d4a' .. '\\u0d4d'
                    {
                    matchRange('\u0D4A','\u0D4D'); 

                    }
                    break;
                case 174 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1454:9: '\\u0d57'
                    {
                    match('\u0D57'); 

                    }
                    break;
                case 175 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1455:9: '\\u0d60' .. '\\u0d61'
                    {
                    matchRange('\u0D60','\u0D61'); 

                    }
                    break;
                case 176 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1456:9: '\\u0d66' .. '\\u0d6f'
                    {
                    matchRange('\u0D66','\u0D6F'); 

                    }
                    break;
                case 177 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1457:9: '\\u0d82' .. '\\u0d83'
                    {
                    matchRange('\u0D82','\u0D83'); 

                    }
                    break;
                case 178 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1458:9: '\\u0d85' .. '\\u0d96'
                    {
                    matchRange('\u0D85','\u0D96'); 

                    }
                    break;
                case 179 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1459:9: '\\u0d9a' .. '\\u0db1'
                    {
                    matchRange('\u0D9A','\u0DB1'); 

                    }
                    break;
                case 180 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1460:9: '\\u0db3' .. '\\u0dbb'
                    {
                    matchRange('\u0DB3','\u0DBB'); 

                    }
                    break;
                case 181 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1461:9: '\\u0dbd'
                    {
                    match('\u0DBD'); 

                    }
                    break;
                case 182 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1462:9: '\\u0dc0' .. '\\u0dc6'
                    {
                    matchRange('\u0DC0','\u0DC6'); 

                    }
                    break;
                case 183 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1463:9: '\\u0dca'
                    {
                    match('\u0DCA'); 

                    }
                    break;
                case 184 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1464:9: '\\u0dcf' .. '\\u0dd4'
                    {
                    matchRange('\u0DCF','\u0DD4'); 

                    }
                    break;
                case 185 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1465:9: '\\u0dd6'
                    {
                    match('\u0DD6'); 

                    }
                    break;
                case 186 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1466:9: '\\u0dd8' .. '\\u0ddf'
                    {
                    matchRange('\u0DD8','\u0DDF'); 

                    }
                    break;
                case 187 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1467:9: '\\u0df2' .. '\\u0df3'
                    {
                    matchRange('\u0DF2','\u0DF3'); 

                    }
                    break;
                case 188 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1468:9: '\\u0e01' .. '\\u0e3a'
                    {
                    matchRange('\u0E01','\u0E3A'); 

                    }
                    break;
                case 189 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1469:9: '\\u0e3f' .. '\\u0e4e'
                    {
                    matchRange('\u0E3F','\u0E4E'); 

                    }
                    break;
                case 190 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1470:9: '\\u0e50' .. '\\u0e59'
                    {
                    matchRange('\u0E50','\u0E59'); 

                    }
                    break;
                case 191 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1471:9: '\\u0e81' .. '\\u0e82'
                    {
                    matchRange('\u0E81','\u0E82'); 

                    }
                    break;
                case 192 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1472:9: '\\u0e84'
                    {
                    match('\u0E84'); 

                    }
                    break;
                case 193 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1473:9: '\\u0e87' .. '\\u0e88'
                    {
                    matchRange('\u0E87','\u0E88'); 

                    }
                    break;
                case 194 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1474:9: '\\u0e8a'
                    {
                    match('\u0E8A'); 

                    }
                    break;
                case 195 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1475:9: '\\u0e8d'
                    {
                    match('\u0E8D'); 

                    }
                    break;
                case 196 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1476:9: '\\u0e94' .. '\\u0e97'
                    {
                    matchRange('\u0E94','\u0E97'); 

                    }
                    break;
                case 197 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1477:9: '\\u0e99' .. '\\u0e9f'
                    {
                    matchRange('\u0E99','\u0E9F'); 

                    }
                    break;
                case 198 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1478:9: '\\u0ea1' .. '\\u0ea3'
                    {
                    matchRange('\u0EA1','\u0EA3'); 

                    }
                    break;
                case 199 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1479:9: '\\u0ea5'
                    {
                    match('\u0EA5'); 

                    }
                    break;
                case 200 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1480:9: '\\u0ea7'
                    {
                    match('\u0EA7'); 

                    }
                    break;
                case 201 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1481:9: '\\u0eaa' .. '\\u0eab'
                    {
                    matchRange('\u0EAA','\u0EAB'); 

                    }
                    break;
                case 202 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1482:9: '\\u0ead' .. '\\u0eb9'
                    {
                    matchRange('\u0EAD','\u0EB9'); 

                    }
                    break;
                case 203 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1483:9: '\\u0ebb' .. '\\u0ebd'
                    {
                    matchRange('\u0EBB','\u0EBD'); 

                    }
                    break;
                case 204 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1484:9: '\\u0ec0' .. '\\u0ec4'
                    {
                    matchRange('\u0EC0','\u0EC4'); 

                    }
                    break;
                case 205 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1485:9: '\\u0ec6'
                    {
                    match('\u0EC6'); 

                    }
                    break;
                case 206 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1486:9: '\\u0ec8' .. '\\u0ecd'
                    {
                    matchRange('\u0EC8','\u0ECD'); 

                    }
                    break;
                case 207 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1487:9: '\\u0ed0' .. '\\u0ed9'
                    {
                    matchRange('\u0ED0','\u0ED9'); 

                    }
                    break;
                case 208 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1488:9: '\\u0edc' .. '\\u0edd'
                    {
                    matchRange('\u0EDC','\u0EDD'); 

                    }
                    break;
                case 209 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1489:9: '\\u0f00'
                    {
                    match('\u0F00'); 

                    }
                    break;
                case 210 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1490:9: '\\u0f18' .. '\\u0f19'
                    {
                    matchRange('\u0F18','\u0F19'); 

                    }
                    break;
                case 211 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1491:9: '\\u0f20' .. '\\u0f29'
                    {
                    matchRange('\u0F20','\u0F29'); 

                    }
                    break;
                case 212 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1492:9: '\\u0f35'
                    {
                    match('\u0F35'); 

                    }
                    break;
                case 213 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1493:9: '\\u0f37'
                    {
                    match('\u0F37'); 

                    }
                    break;
                case 214 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1494:9: '\\u0f39'
                    {
                    match('\u0F39'); 

                    }
                    break;
                case 215 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1495:9: '\\u0f3e' .. '\\u0f47'
                    {
                    matchRange('\u0F3E','\u0F47'); 

                    }
                    break;
                case 216 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1496:9: '\\u0f49' .. '\\u0f6a'
                    {
                    matchRange('\u0F49','\u0F6A'); 

                    }
                    break;
                case 217 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1497:9: '\\u0f71' .. '\\u0f84'
                    {
                    matchRange('\u0F71','\u0F84'); 

                    }
                    break;
                case 218 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1498:9: '\\u0f86' .. '\\u0f8b'
                    {
                    matchRange('\u0F86','\u0F8B'); 

                    }
                    break;
                case 219 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1499:9: '\\u0f90' .. '\\u0f97'
                    {
                    matchRange('\u0F90','\u0F97'); 

                    }
                    break;
                case 220 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1500:9: '\\u0f99' .. '\\u0fbc'
                    {
                    matchRange('\u0F99','\u0FBC'); 

                    }
                    break;
                case 221 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1501:9: '\\u0fc6'
                    {
                    match('\u0FC6'); 

                    }
                    break;
                case 222 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1502:9: '\\u1000' .. '\\u1021'
                    {
                    matchRange('\u1000','\u1021'); 

                    }
                    break;
                case 223 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1503:9: '\\u1023' .. '\\u1027'
                    {
                    matchRange('\u1023','\u1027'); 

                    }
                    break;
                case 224 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1504:9: '\\u1029' .. '\\u102a'
                    {
                    matchRange('\u1029','\u102A'); 

                    }
                    break;
                case 225 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1505:9: '\\u102c' .. '\\u1032'
                    {
                    matchRange('\u102C','\u1032'); 

                    }
                    break;
                case 226 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1506:9: '\\u1036' .. '\\u1039'
                    {
                    matchRange('\u1036','\u1039'); 

                    }
                    break;
                case 227 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1507:9: '\\u1040' .. '\\u1049'
                    {
                    matchRange('\u1040','\u1049'); 

                    }
                    break;
                case 228 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1508:9: '\\u1050' .. '\\u1059'
                    {
                    matchRange('\u1050','\u1059'); 

                    }
                    break;
                case 229 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1509:9: '\\u10a0' .. '\\u10c5'
                    {
                    matchRange('\u10A0','\u10C5'); 

                    }
                    break;
                case 230 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1510:9: '\\u10d0' .. '\\u10f8'
                    {
                    matchRange('\u10D0','\u10F8'); 

                    }
                    break;
                case 231 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1511:9: '\\u1100' .. '\\u1159'
                    {
                    matchRange('\u1100','\u1159'); 

                    }
                    break;
                case 232 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1512:9: '\\u115f' .. '\\u11a2'
                    {
                    matchRange('\u115F','\u11A2'); 

                    }
                    break;
                case 233 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1513:9: '\\u11a8' .. '\\u11f9'
                    {
                    matchRange('\u11A8','\u11F9'); 

                    }
                    break;
                case 234 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1514:9: '\\u1200' .. '\\u1206'
                    {
                    matchRange('\u1200','\u1206'); 

                    }
                    break;
                case 235 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1515:9: '\\u1208' .. '\\u1246'
                    {
                    matchRange('\u1208','\u1246'); 

                    }
                    break;
                case 236 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1516:9: '\\u1248'
                    {
                    match('\u1248'); 

                    }
                    break;
                case 237 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1517:9: '\\u124a' .. '\\u124d'
                    {
                    matchRange('\u124A','\u124D'); 

                    }
                    break;
                case 238 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1518:9: '\\u1250' .. '\\u1256'
                    {
                    matchRange('\u1250','\u1256'); 

                    }
                    break;
                case 239 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1519:9: '\\u1258'
                    {
                    match('\u1258'); 

                    }
                    break;
                case 240 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1520:9: '\\u125a' .. '\\u125d'
                    {
                    matchRange('\u125A','\u125D'); 

                    }
                    break;
                case 241 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1521:9: '\\u1260' .. '\\u1286'
                    {
                    matchRange('\u1260','\u1286'); 

                    }
                    break;
                case 242 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1522:9: '\\u1288'
                    {
                    match('\u1288'); 

                    }
                    break;
                case 243 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1523:9: '\\u128a' .. '\\u128d'
                    {
                    matchRange('\u128A','\u128D'); 

                    }
                    break;
                case 244 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1524:9: '\\u1290' .. '\\u12ae'
                    {
                    matchRange('\u1290','\u12AE'); 

                    }
                    break;
                case 245 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1525:9: '\\u12b0'
                    {
                    match('\u12B0'); 

                    }
                    break;
                case 246 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1526:9: '\\u12b2' .. '\\u12b5'
                    {
                    matchRange('\u12B2','\u12B5'); 

                    }
                    break;
                case 247 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1527:9: '\\u12b8' .. '\\u12be'
                    {
                    matchRange('\u12B8','\u12BE'); 

                    }
                    break;
                case 248 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1528:9: '\\u12c0'
                    {
                    match('\u12C0'); 

                    }
                    break;
                case 249 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1529:9: '\\u12c2' .. '\\u12c5'
                    {
                    matchRange('\u12C2','\u12C5'); 

                    }
                    break;
                case 250 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1530:9: '\\u12c8' .. '\\u12ce'
                    {
                    matchRange('\u12C8','\u12CE'); 

                    }
                    break;
                case 251 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1531:9: '\\u12d0' .. '\\u12d6'
                    {
                    matchRange('\u12D0','\u12D6'); 

                    }
                    break;
                case 252 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1532:9: '\\u12d8' .. '\\u12ee'
                    {
                    matchRange('\u12D8','\u12EE'); 

                    }
                    break;
                case 253 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1533:9: '\\u12f0' .. '\\u130e'
                    {
                    matchRange('\u12F0','\u130E'); 

                    }
                    break;
                case 254 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1534:9: '\\u1310'
                    {
                    match('\u1310'); 

                    }
                    break;
                case 255 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1535:9: '\\u1312' .. '\\u1315'
                    {
                    matchRange('\u1312','\u1315'); 

                    }
                    break;
                case 256 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1536:9: '\\u1318' .. '\\u131e'
                    {
                    matchRange('\u1318','\u131E'); 

                    }
                    break;
                case 257 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1537:9: '\\u1320' .. '\\u1346'
                    {
                    matchRange('\u1320','\u1346'); 

                    }
                    break;
                case 258 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1538:9: '\\u1348' .. '\\u135a'
                    {
                    matchRange('\u1348','\u135A'); 

                    }
                    break;
                case 259 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1539:9: '\\u1369' .. '\\u1371'
                    {
                    matchRange('\u1369','\u1371'); 

                    }
                    break;
                case 260 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1540:9: '\\u13a0' .. '\\u13f4'
                    {
                    matchRange('\u13A0','\u13F4'); 

                    }
                    break;
                case 261 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1541:9: '\\u1401' .. '\\u166c'
                    {
                    matchRange('\u1401','\u166C'); 

                    }
                    break;
                case 262 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1542:9: '\\u166f' .. '\\u1676'
                    {
                    matchRange('\u166F','\u1676'); 

                    }
                    break;
                case 263 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1543:9: '\\u1681' .. '\\u169a'
                    {
                    matchRange('\u1681','\u169A'); 

                    }
                    break;
                case 264 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1544:9: '\\u16a0' .. '\\u16ea'
                    {
                    matchRange('\u16A0','\u16EA'); 

                    }
                    break;
                case 265 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1545:9: '\\u16ee' .. '\\u16f0'
                    {
                    matchRange('\u16EE','\u16F0'); 

                    }
                    break;
                case 266 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1546:9: '\\u1700' .. '\\u170c'
                    {
                    matchRange('\u1700','\u170C'); 

                    }
                    break;
                case 267 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1547:9: '\\u170e' .. '\\u1714'
                    {
                    matchRange('\u170E','\u1714'); 

                    }
                    break;
                case 268 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1548:9: '\\u1720' .. '\\u1734'
                    {
                    matchRange('\u1720','\u1734'); 

                    }
                    break;
                case 269 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1549:9: '\\u1740' .. '\\u1753'
                    {
                    matchRange('\u1740','\u1753'); 

                    }
                    break;
                case 270 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1550:9: '\\u1760' .. '\\u176c'
                    {
                    matchRange('\u1760','\u176C'); 

                    }
                    break;
                case 271 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1551:9: '\\u176e' .. '\\u1770'
                    {
                    matchRange('\u176E','\u1770'); 

                    }
                    break;
                case 272 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1552:9: '\\u1772' .. '\\u1773'
                    {
                    matchRange('\u1772','\u1773'); 

                    }
                    break;
                case 273 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1553:9: '\\u1780' .. '\\u17d3'
                    {
                    matchRange('\u1780','\u17D3'); 

                    }
                    break;
                case 274 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1554:9: '\\u17d7'
                    {
                    match('\u17D7'); 

                    }
                    break;
                case 275 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1555:9: '\\u17db' .. '\\u17dd'
                    {
                    matchRange('\u17DB','\u17DD'); 

                    }
                    break;
                case 276 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1556:9: '\\u17e0' .. '\\u17e9'
                    {
                    matchRange('\u17E0','\u17E9'); 

                    }
                    break;
                case 277 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1557:9: '\\u180b' .. '\\u180d'
                    {
                    matchRange('\u180B','\u180D'); 

                    }
                    break;
                case 278 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1558:9: '\\u1810' .. '\\u1819'
                    {
                    matchRange('\u1810','\u1819'); 

                    }
                    break;
                case 279 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1559:9: '\\u1820' .. '\\u1877'
                    {
                    matchRange('\u1820','\u1877'); 

                    }
                    break;
                case 280 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1560:9: '\\u1880' .. '\\u18a9'
                    {
                    matchRange('\u1880','\u18A9'); 

                    }
                    break;
                case 281 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1561:9: '\\u1900' .. '\\u191c'
                    {
                    matchRange('\u1900','\u191C'); 

                    }
                    break;
                case 282 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1562:9: '\\u1920' .. '\\u192b'
                    {
                    matchRange('\u1920','\u192B'); 

                    }
                    break;
                case 283 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1563:9: '\\u1930' .. '\\u193b'
                    {
                    matchRange('\u1930','\u193B'); 

                    }
                    break;
                case 284 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1564:9: '\\u1946' .. '\\u196d'
                    {
                    matchRange('\u1946','\u196D'); 

                    }
                    break;
                case 285 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1565:9: '\\u1970' .. '\\u1974'
                    {
                    matchRange('\u1970','\u1974'); 

                    }
                    break;
                case 286 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1566:9: '\\u1d00' .. '\\u1d6b'
                    {
                    matchRange('\u1D00','\u1D6B'); 

                    }
                    break;
                case 287 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1567:9: '\\u1e00' .. '\\u1e9b'
                    {
                    matchRange('\u1E00','\u1E9B'); 

                    }
                    break;
                case 288 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1568:9: '\\u1ea0' .. '\\u1ef9'
                    {
                    matchRange('\u1EA0','\u1EF9'); 

                    }
                    break;
                case 289 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1569:9: '\\u1f00' .. '\\u1f15'
                    {
                    matchRange('\u1F00','\u1F15'); 

                    }
                    break;
                case 290 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1570:9: '\\u1f18' .. '\\u1f1d'
                    {
                    matchRange('\u1F18','\u1F1D'); 

                    }
                    break;
                case 291 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1571:9: '\\u1f20' .. '\\u1f45'
                    {
                    matchRange('\u1F20','\u1F45'); 

                    }
                    break;
                case 292 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1572:9: '\\u1f48' .. '\\u1f4d'
                    {
                    matchRange('\u1F48','\u1F4D'); 

                    }
                    break;
                case 293 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1573:9: '\\u1f50' .. '\\u1f57'
                    {
                    matchRange('\u1F50','\u1F57'); 

                    }
                    break;
                case 294 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1574:9: '\\u1f59'
                    {
                    match('\u1F59'); 

                    }
                    break;
                case 295 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1575:9: '\\u1f5b'
                    {
                    match('\u1F5B'); 

                    }
                    break;
                case 296 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1576:9: '\\u1f5d'
                    {
                    match('\u1F5D'); 

                    }
                    break;
                case 297 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1577:9: '\\u1f5f' .. '\\u1f7d'
                    {
                    matchRange('\u1F5F','\u1F7D'); 

                    }
                    break;
                case 298 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1578:9: '\\u1f80' .. '\\u1fb4'
                    {
                    matchRange('\u1F80','\u1FB4'); 

                    }
                    break;
                case 299 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1579:9: '\\u1fb6' .. '\\u1fbc'
                    {
                    matchRange('\u1FB6','\u1FBC'); 

                    }
                    break;
                case 300 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1580:9: '\\u1fbe'
                    {
                    match('\u1FBE'); 

                    }
                    break;
                case 301 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1581:9: '\\u1fc2' .. '\\u1fc4'
                    {
                    matchRange('\u1FC2','\u1FC4'); 

                    }
                    break;
                case 302 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1582:9: '\\u1fc6' .. '\\u1fcc'
                    {
                    matchRange('\u1FC6','\u1FCC'); 

                    }
                    break;
                case 303 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1583:9: '\\u1fd0' .. '\\u1fd3'
                    {
                    matchRange('\u1FD0','\u1FD3'); 

                    }
                    break;
                case 304 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1584:9: '\\u1fd6' .. '\\u1fdb'
                    {
                    matchRange('\u1FD6','\u1FDB'); 

                    }
                    break;
                case 305 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1585:9: '\\u1fe0' .. '\\u1fec'
                    {
                    matchRange('\u1FE0','\u1FEC'); 

                    }
                    break;
                case 306 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1586:9: '\\u1ff2' .. '\\u1ff4'
                    {
                    matchRange('\u1FF2','\u1FF4'); 

                    }
                    break;
                case 307 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1587:9: '\\u1ff6' .. '\\u1ffc'
                    {
                    matchRange('\u1FF6','\u1FFC'); 

                    }
                    break;
                case 308 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1588:9: '\\u200c' .. '\\u200f'
                    {
                    matchRange('\u200C','\u200F'); 

                    }
                    break;
                case 309 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1589:9: '\\u202a' .. '\\u202e'
                    {
                    matchRange('\u202A','\u202E'); 

                    }
                    break;
                case 310 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1590:9: '\\u203f' .. '\\u2040'
                    {
                    matchRange('\u203F','\u2040'); 

                    }
                    break;
                case 311 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1591:9: '\\u2054'
                    {
                    match('\u2054'); 

                    }
                    break;
                case 312 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1592:9: '\\u2060' .. '\\u2063'
                    {
                    matchRange('\u2060','\u2063'); 

                    }
                    break;
                case 313 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1593:9: '\\u206a' .. '\\u206f'
                    {
                    matchRange('\u206A','\u206F'); 

                    }
                    break;
                case 314 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1594:9: '\\u2071'
                    {
                    match('\u2071'); 

                    }
                    break;
                case 315 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1595:9: '\\u207f'
                    {
                    match('\u207F'); 

                    }
                    break;
                case 316 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1596:9: '\\u20a0' .. '\\u20b1'
                    {
                    matchRange('\u20A0','\u20B1'); 

                    }
                    break;
                case 317 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1597:9: '\\u20d0' .. '\\u20dc'
                    {
                    matchRange('\u20D0','\u20DC'); 

                    }
                    break;
                case 318 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1598:9: '\\u20e1'
                    {
                    match('\u20E1'); 

                    }
                    break;
                case 319 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1599:9: '\\u20e5' .. '\\u20ea'
                    {
                    matchRange('\u20E5','\u20EA'); 

                    }
                    break;
                case 320 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1600:9: '\\u2102'
                    {
                    match('\u2102'); 

                    }
                    break;
                case 321 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1601:9: '\\u2107'
                    {
                    match('\u2107'); 

                    }
                    break;
                case 322 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1602:9: '\\u210a' .. '\\u2113'
                    {
                    matchRange('\u210A','\u2113'); 

                    }
                    break;
                case 323 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1603:9: '\\u2115'
                    {
                    match('\u2115'); 

                    }
                    break;
                case 324 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1604:9: '\\u2119' .. '\\u211d'
                    {
                    matchRange('\u2119','\u211D'); 

                    }
                    break;
                case 325 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1605:9: '\\u2124'
                    {
                    match('\u2124'); 

                    }
                    break;
                case 326 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1606:9: '\\u2126'
                    {
                    match('\u2126'); 

                    }
                    break;
                case 327 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1607:9: '\\u2128'
                    {
                    match('\u2128'); 

                    }
                    break;
                case 328 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1608:9: '\\u212a' .. '\\u212d'
                    {
                    matchRange('\u212A','\u212D'); 

                    }
                    break;
                case 329 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1609:9: '\\u212f' .. '\\u2131'
                    {
                    matchRange('\u212F','\u2131'); 

                    }
                    break;
                case 330 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1610:9: '\\u2133' .. '\\u2139'
                    {
                    matchRange('\u2133','\u2139'); 

                    }
                    break;
                case 331 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1611:9: '\\u213d' .. '\\u213f'
                    {
                    matchRange('\u213D','\u213F'); 

                    }
                    break;
                case 332 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1612:9: '\\u2145' .. '\\u2149'
                    {
                    matchRange('\u2145','\u2149'); 

                    }
                    break;
                case 333 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1613:9: '\\u2160' .. '\\u2183'
                    {
                    matchRange('\u2160','\u2183'); 

                    }
                    break;
                case 334 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1614:9: '\\u3005' .. '\\u3007'
                    {
                    matchRange('\u3005','\u3007'); 

                    }
                    break;
                case 335 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1615:9: '\\u3021' .. '\\u302f'
                    {
                    matchRange('\u3021','\u302F'); 

                    }
                    break;
                case 336 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1616:9: '\\u3031' .. '\\u3035'
                    {
                    matchRange('\u3031','\u3035'); 

                    }
                    break;
                case 337 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1617:9: '\\u3038' .. '\\u303c'
                    {
                    matchRange('\u3038','\u303C'); 

                    }
                    break;
                case 338 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1618:9: '\\u3041' .. '\\u3096'
                    {
                    matchRange('\u3041','\u3096'); 

                    }
                    break;
                case 339 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1619:9: '\\u3099' .. '\\u309a'
                    {
                    matchRange('\u3099','\u309A'); 

                    }
                    break;
                case 340 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1620:9: '\\u309d' .. '\\u309f'
                    {
                    matchRange('\u309D','\u309F'); 

                    }
                    break;
                case 341 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1621:9: '\\u30a1' .. '\\u30ff'
                    {
                    matchRange('\u30A1','\u30FF'); 

                    }
                    break;
                case 342 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1622:9: '\\u3105' .. '\\u312c'
                    {
                    matchRange('\u3105','\u312C'); 

                    }
                    break;
                case 343 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1623:9: '\\u3131' .. '\\u318e'
                    {
                    matchRange('\u3131','\u318E'); 

                    }
                    break;
                case 344 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1624:9: '\\u31a0' .. '\\u31b7'
                    {
                    matchRange('\u31A0','\u31B7'); 

                    }
                    break;
                case 345 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1625:9: '\\u31f0' .. '\\u31ff'
                    {
                    matchRange('\u31F0','\u31FF'); 

                    }
                    break;
                case 346 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1626:9: '\\u3400' .. '\\u4db5'
                    {
                    matchRange('\u3400','\u4DB5'); 

                    }
                    break;
                case 347 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1627:9: '\\u4e00' .. '\\u9fa5'
                    {
                    matchRange('\u4E00','\u9FA5'); 

                    }
                    break;
                case 348 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1628:9: '\\ua000' .. '\\ua48c'
                    {
                    matchRange('\uA000','\uA48C'); 

                    }
                    break;
                case 349 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1629:9: '\\uac00' .. '\\ud7a3'
                    {
                    matchRange('\uAC00','\uD7A3'); 

                    }
                    break;
                case 350 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1630:9: '\\uf900' .. '\\ufa2d'
                    {
                    matchRange('\uF900','\uFA2D'); 

                    }
                    break;
                case 351 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1631:9: '\\ufa30' .. '\\ufa6a'
                    {
                    matchRange('\uFA30','\uFA6A'); 

                    }
                    break;
                case 352 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1632:9: '\\ufb00' .. '\\ufb06'
                    {
                    matchRange('\uFB00','\uFB06'); 

                    }
                    break;
                case 353 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1633:9: '\\ufb13' .. '\\ufb17'
                    {
                    matchRange('\uFB13','\uFB17'); 

                    }
                    break;
                case 354 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1634:9: '\\ufb1d' .. '\\ufb28'
                    {
                    matchRange('\uFB1D','\uFB28'); 

                    }
                    break;
                case 355 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1635:9: '\\ufb2a' .. '\\ufb36'
                    {
                    matchRange('\uFB2A','\uFB36'); 

                    }
                    break;
                case 356 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1636:9: '\\ufb38' .. '\\ufb3c'
                    {
                    matchRange('\uFB38','\uFB3C'); 

                    }
                    break;
                case 357 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1637:9: '\\ufb3e'
                    {
                    match('\uFB3E'); 

                    }
                    break;
                case 358 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1638:9: '\\ufb40' .. '\\ufb41'
                    {
                    matchRange('\uFB40','\uFB41'); 

                    }
                    break;
                case 359 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1639:9: '\\ufb43' .. '\\ufb44'
                    {
                    matchRange('\uFB43','\uFB44'); 

                    }
                    break;
                case 360 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1640:9: '\\ufb46' .. '\\ufbb1'
                    {
                    matchRange('\uFB46','\uFBB1'); 

                    }
                    break;
                case 361 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1641:9: '\\ufbd3' .. '\\ufd3d'
                    {
                    matchRange('\uFBD3','\uFD3D'); 

                    }
                    break;
                case 362 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1642:9: '\\ufd50' .. '\\ufd8f'
                    {
                    matchRange('\uFD50','\uFD8F'); 

                    }
                    break;
                case 363 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1643:9: '\\ufd92' .. '\\ufdc7'
                    {
                    matchRange('\uFD92','\uFDC7'); 

                    }
                    break;
                case 364 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1644:9: '\\ufdf0' .. '\\ufdfc'
                    {
                    matchRange('\uFDF0','\uFDFC'); 

                    }
                    break;
                case 365 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1645:9: '\\ufe00' .. '\\ufe0f'
                    {
                    matchRange('\uFE00','\uFE0F'); 

                    }
                    break;
                case 366 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1646:9: '\\ufe20' .. '\\ufe23'
                    {
                    matchRange('\uFE20','\uFE23'); 

                    }
                    break;
                case 367 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1647:9: '\\ufe33' .. '\\ufe34'
                    {
                    matchRange('\uFE33','\uFE34'); 

                    }
                    break;
                case 368 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1648:9: '\\ufe4d' .. '\\ufe4f'
                    {
                    matchRange('\uFE4D','\uFE4F'); 

                    }
                    break;
                case 369 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1649:9: '\\ufe69'
                    {
                    match('\uFE69'); 

                    }
                    break;
                case 370 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1650:9: '\\ufe70' .. '\\ufe74'
                    {
                    matchRange('\uFE70','\uFE74'); 

                    }
                    break;
                case 371 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1651:9: '\\ufe76' .. '\\ufefc'
                    {
                    matchRange('\uFE76','\uFEFC'); 

                    }
                    break;
                case 372 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1652:9: '\\ufeff'
                    {
                    match('\uFEFF'); 

                    }
                    break;
                case 373 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1653:9: '\\uff04'
                    {
                    match('\uFF04'); 

                    }
                    break;
                case 374 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1654:9: '\\uff10' .. '\\uff19'
                    {
                    matchRange('\uFF10','\uFF19'); 

                    }
                    break;
                case 375 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1655:9: '\\uff21' .. '\\uff3a'
                    {
                    matchRange('\uFF21','\uFF3A'); 

                    }
                    break;
                case 376 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1656:9: '\\uff3f'
                    {
                    match('\uFF3F'); 

                    }
                    break;
                case 377 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1657:9: '\\uff41' .. '\\uff5a'
                    {
                    matchRange('\uFF41','\uFF5A'); 

                    }
                    break;
                case 378 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1658:9: '\\uff65' .. '\\uffbe'
                    {
                    matchRange('\uFF65','\uFFBE'); 

                    }
                    break;
                case 379 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1659:9: '\\uffc2' .. '\\uffc7'
                    {
                    matchRange('\uFFC2','\uFFC7'); 

                    }
                    break;
                case 380 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1660:9: '\\uffca' .. '\\uffcf'
                    {
                    matchRange('\uFFCA','\uFFCF'); 

                    }
                    break;
                case 381 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1661:9: '\\uffd2' .. '\\uffd7'
                    {
                    matchRange('\uFFD2','\uFFD7'); 

                    }
                    break;
                case 382 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1662:9: '\\uffda' .. '\\uffdc'
                    {
                    matchRange('\uFFDA','\uFFDC'); 

                    }
                    break;
                case 383 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1663:9: '\\uffe0' .. '\\uffe1'
                    {
                    matchRange('\uFFE0','\uFFE1'); 

                    }
                    break;
                case 384 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1664:9: '\\uffe5' .. '\\uffe6'
                    {
                    matchRange('\uFFE5','\uFFE6'); 

                    }
                    break;
                case 385 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1665:9: '\\ufff9' .. '\\ufffb'
                    {
                    matchRange('\uFFF9','\uFFFB'); 

                    }
                    break;
                case 386 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1666:9: ( '\\ud800' .. '\\udbff' ) ( '\\udc00' .. '\\udfff' )
                    {
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1666:9: ( '\\ud800' .. '\\udbff' )
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1666:10: '\\ud800' .. '\\udbff'
                    {
                    matchRange('\uD800','\uDBFF'); 

                    }

                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1666:30: ( '\\udc00' .. '\\udfff' )
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1666:31: '\\udc00' .. '\\udfff'
                    {
                    matchRange('\uDC00','\uDFFF'); 

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end IdentifierPart

    public void mTokens() throws RecognitionException {
        // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:8: ( LONGLITERAL | INTLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | WS | COMMENT | LINE_COMMENT | ABSTRACT | ASSERT | BOOLEAN | BREAK | BYTE | CASE | CATCH | CHAR | CLASS | CONST | CONTINUE | DEFAULT | DO | DOUBLE | ELSE | ENUM | EXTENDS | FINAL | FINALLY | FLOAT | FOR | GOTO | IF | IMPLEMENTS | IMPORT | INSTANCEOF | INT | INTERFACE | LONG | NATIVE | NEW | PACKAGE | PRIVATE | PROTECTED | PUBLIC | RETURN | SHORT | STATIC | STRICTFP | SUPER | SWITCH | SYNCHRONIZED | THIS | THROW | THROWS | TRANSIENT | TRY | VOID | VOLATILE | WHILE | TRUE | FALSE | NULL | LPAREN | RPAREN | LBRACE | RBRACE | LBRACKET | RBRACKET | SEMI | COMMA | DOT | ELLIPSIS | EQ | BANG | TILDE | QUES | COLON | EQEQ | AMPAMP | BARBAR | PLUSPLUS | SUBSUB | PLUS | SUB | STAR | SLASH | AMP | BAR | CARET | PERCENT | PLUSEQ | SUBEQ | STAREQ | SLASHEQ | AMPEQ | BAREQ | CARETEQ | PERCENTEQ | MONKEYS_AT | BANGEQ | GT | LT | IDENTIFIER )
        int alt33=103;
        int LA33_0 = input.LA(1);

        if ( (LA33_0=='0') ) {
            alt33=1;
        }
        else if ( ((LA33_0>='1' && LA33_0<='9')) ) {
            alt33=1;
        }
        else if ( (LA33_0=='.') ) {
            alt33=3;
        }
        else if ( (LA33_0=='\'') ) {
            alt33=5;
        }
        else if ( (LA33_0=='\"') ) {
            alt33=6;
        }
        else if ( ((LA33_0>='\t' && LA33_0<='\n')||(LA33_0>='\f' && LA33_0<='\r')||LA33_0==' ') ) {
            alt33=7;
        }
        else if ( (LA33_0=='/') ) {
            alt33=8;
        }
        else if ( (LA33_0=='a') ) {
            alt33=10;
        }
        else if ( (LA33_0=='b') ) {
            alt33=12;
        }
        else if ( (LA33_0=='c') ) {
            alt33=15;
        }
        else if ( (LA33_0=='d') ) {
            alt33=21;
        }
        else if ( (LA33_0=='e') ) {
            alt33=24;
        }
        else if ( (LA33_0=='f') ) {
            alt33=27;
        }
        else if ( (LA33_0=='g') ) {
            alt33=31;
        }
        else if ( (LA33_0=='i') ) {
            alt33=32;
        }
        else if ( (LA33_0=='l') ) {
            alt33=38;
        }
        else if ( (LA33_0=='n') ) {
            alt33=39;
        }
        else if ( (LA33_0=='p') ) {
            alt33=41;
        }
        else if ( (LA33_0=='r') ) {
            alt33=45;
        }
        else if ( (LA33_0=='s') ) {
            alt33=46;
        }
        else if ( (LA33_0=='t') ) {
            alt33=52;
        }
        else if ( (LA33_0=='v') ) {
            alt33=57;
        }
        else if ( (LA33_0=='w') ) {
            alt33=59;
        }
        else if ( (LA33_0=='(') ) {
            alt33=63;
        }
        else if ( (LA33_0==')') ) {
            alt33=64;
        }
        else if ( (LA33_0=='{') ) {
            alt33=65;
        }
        else if ( (LA33_0=='}') ) {
            alt33=66;
        }
        else if ( (LA33_0=='[') ) {
            alt33=67;
        }
        else if ( (LA33_0==']') ) {
            alt33=68;
        }
        else if ( (LA33_0==';') ) {
            alt33=69;
        }
        else if ( (LA33_0==',') ) {
            alt33=70;
        }
        else if ( (LA33_0=='=') ) {
            alt33=73;
        }
        else if ( (LA33_0=='!') ) {
            alt33=74;
        }
        else if ( (LA33_0=='~') ) {
            alt33=75;
        }
        else if ( (LA33_0=='?') ) {
            alt33=76;
        }
        else if ( (LA33_0==':') ) {
            alt33=77;
        }
        else if ( (LA33_0=='&') ) {
            alt33=79;
        }
        else if ( (LA33_0=='|') ) {
            alt33=80;
        }
        else if ( (LA33_0=='+') ) {
            alt33=81;
        }
        else if ( (LA33_0=='-') ) {
            alt33=82;
        }
        else if ( (LA33_0=='*') ) {
            alt33=85;
        }
        else if ( (LA33_0=='^') ) {
            alt33=89;
        }
        else if ( (LA33_0=='%') ) {
            alt33=90;
        }
        else if ( (LA33_0=='@') ) {
            alt33=99;
        }
        else if ( (LA33_0=='>') ) {
            alt33=101;
        }
        else if ( (LA33_0=='<') ) {
            alt33=102;
        }
        else if ( (LA33_0=='$'||(LA33_0>='A' && LA33_0<='Z')||LA33_0=='_'||LA33_0=='h'||(LA33_0>='j' && LA33_0<='k')||LA33_0=='m'||LA33_0=='o'||LA33_0=='q'||LA33_0=='u'||(LA33_0>='x' && LA33_0<='z')||(LA33_0>='\u00A2' && LA33_0<='\u00A5')||LA33_0=='\u00AA'||LA33_0=='\u00B5'||LA33_0=='\u00BA'||(LA33_0>='\u00C0' && LA33_0<='\u00D6')||(LA33_0>='\u00D8' && LA33_0<='\u00F6')||(LA33_0>='\u00F8' && LA33_0<='\u0236')||(LA33_0>='\u0250' && LA33_0<='\u02C1')||(LA33_0>='\u02C6' && LA33_0<='\u02D1')||(LA33_0>='\u02E0' && LA33_0<='\u02E4')||LA33_0=='\u02EE'||LA33_0=='\u037A'||LA33_0=='\u0386'||(LA33_0>='\u0388' && LA33_0<='\u038A')||LA33_0=='\u038C'||(LA33_0>='\u038E' && LA33_0<='\u03A1')||(LA33_0>='\u03A3' && LA33_0<='\u03CE')||(LA33_0>='\u03D0' && LA33_0<='\u03F5')||(LA33_0>='\u03F7' && LA33_0<='\u03FB')||(LA33_0>='\u0400' && LA33_0<='\u0481')||(LA33_0>='\u048A' && LA33_0<='\u04CE')||(LA33_0>='\u04D0' && LA33_0<='\u04F5')||(LA33_0>='\u04F8' && LA33_0<='\u04F9')||(LA33_0>='\u0500' && LA33_0<='\u050F')||(LA33_0>='\u0531' && LA33_0<='\u0556')||LA33_0=='\u0559'||(LA33_0>='\u0561' && LA33_0<='\u0587')||(LA33_0>='\u05D0' && LA33_0<='\u05EA')||(LA33_0>='\u05F0' && LA33_0<='\u05F2')||(LA33_0>='\u0621' && LA33_0<='\u063A')||(LA33_0>='\u0640' && LA33_0<='\u064A')||(LA33_0>='\u066E' && LA33_0<='\u066F')||(LA33_0>='\u0671' && LA33_0<='\u06D3')||LA33_0=='\u06D5'||(LA33_0>='\u06E5' && LA33_0<='\u06E6')||(LA33_0>='\u06EE' && LA33_0<='\u06EF')||(LA33_0>='\u06FA' && LA33_0<='\u06FC')||LA33_0=='\u06FF'||LA33_0=='\u0710'||(LA33_0>='\u0712' && LA33_0<='\u072F')||(LA33_0>='\u074D' && LA33_0<='\u074F')||(LA33_0>='\u0780' && LA33_0<='\u07A5')||LA33_0=='\u07B1'||(LA33_0>='\u0904' && LA33_0<='\u0939')||LA33_0=='\u093D'||LA33_0=='\u0950'||(LA33_0>='\u0958' && LA33_0<='\u0961')||(LA33_0>='\u0985' && LA33_0<='\u098C')||(LA33_0>='\u098F' && LA33_0<='\u0990')||(LA33_0>='\u0993' && LA33_0<='\u09A8')||(LA33_0>='\u09AA' && LA33_0<='\u09B0')||LA33_0=='\u09B2'||(LA33_0>='\u09B6' && LA33_0<='\u09B9')||LA33_0=='\u09BD'||(LA33_0>='\u09DC' && LA33_0<='\u09DD')||(LA33_0>='\u09DF' && LA33_0<='\u09E1')||(LA33_0>='\u09F0' && LA33_0<='\u09F3')||(LA33_0>='\u0A05' && LA33_0<='\u0A0A')||(LA33_0>='\u0A0F' && LA33_0<='\u0A10')||(LA33_0>='\u0A13' && LA33_0<='\u0A28')||(LA33_0>='\u0A2A' && LA33_0<='\u0A30')||(LA33_0>='\u0A32' && LA33_0<='\u0A33')||(LA33_0>='\u0A35' && LA33_0<='\u0A36')||(LA33_0>='\u0A38' && LA33_0<='\u0A39')||(LA33_0>='\u0A59' && LA33_0<='\u0A5C')||LA33_0=='\u0A5E'||(LA33_0>='\u0A72' && LA33_0<='\u0A74')||(LA33_0>='\u0A85' && LA33_0<='\u0A8D')||(LA33_0>='\u0A8F' && LA33_0<='\u0A91')||(LA33_0>='\u0A93' && LA33_0<='\u0AA8')||(LA33_0>='\u0AAA' && LA33_0<='\u0AB0')||(LA33_0>='\u0AB2' && LA33_0<='\u0AB3')||(LA33_0>='\u0AB5' && LA33_0<='\u0AB9')||LA33_0=='\u0ABD'||LA33_0=='\u0AD0'||(LA33_0>='\u0AE0' && LA33_0<='\u0AE1')||LA33_0=='\u0AF1'||(LA33_0>='\u0B05' && LA33_0<='\u0B0C')||(LA33_0>='\u0B0F' && LA33_0<='\u0B10')||(LA33_0>='\u0B13' && LA33_0<='\u0B28')||(LA33_0>='\u0B2A' && LA33_0<='\u0B30')||(LA33_0>='\u0B32' && LA33_0<='\u0B33')||(LA33_0>='\u0B35' && LA33_0<='\u0B39')||LA33_0=='\u0B3D'||(LA33_0>='\u0B5C' && LA33_0<='\u0B5D')||(LA33_0>='\u0B5F' && LA33_0<='\u0B61')||LA33_0=='\u0B71'||LA33_0=='\u0B83'||(LA33_0>='\u0B85' && LA33_0<='\u0B8A')||(LA33_0>='\u0B8E' && LA33_0<='\u0B90')||(LA33_0>='\u0B92' && LA33_0<='\u0B95')||(LA33_0>='\u0B99' && LA33_0<='\u0B9A')||LA33_0=='\u0B9C'||(LA33_0>='\u0B9E' && LA33_0<='\u0B9F')||(LA33_0>='\u0BA3' && LA33_0<='\u0BA4')||(LA33_0>='\u0BA8' && LA33_0<='\u0BAA')||(LA33_0>='\u0BAE' && LA33_0<='\u0BB5')||(LA33_0>='\u0BB7' && LA33_0<='\u0BB9')||LA33_0=='\u0BF9'||(LA33_0>='\u0C05' && LA33_0<='\u0C0C')||(LA33_0>='\u0C0E' && LA33_0<='\u0C10')||(LA33_0>='\u0C12' && LA33_0<='\u0C28')||(LA33_0>='\u0C2A' && LA33_0<='\u0C33')||(LA33_0>='\u0C35' && LA33_0<='\u0C39')||(LA33_0>='\u0C60' && LA33_0<='\u0C61')||(LA33_0>='\u0C85' && LA33_0<='\u0C8C')||(LA33_0>='\u0C8E' && LA33_0<='\u0C90')||(LA33_0>='\u0C92' && LA33_0<='\u0CA8')||(LA33_0>='\u0CAA' && LA33_0<='\u0CB3')||(LA33_0>='\u0CB5' && LA33_0<='\u0CB9')||LA33_0=='\u0CBD'||LA33_0=='\u0CDE'||(LA33_0>='\u0CE0' && LA33_0<='\u0CE1')||(LA33_0>='\u0D05' && LA33_0<='\u0D0C')||(LA33_0>='\u0D0E' && LA33_0<='\u0D10')||(LA33_0>='\u0D12' && LA33_0<='\u0D28')||(LA33_0>='\u0D2A' && LA33_0<='\u0D39')||(LA33_0>='\u0D60' && LA33_0<='\u0D61')||(LA33_0>='\u0D85' && LA33_0<='\u0D96')||(LA33_0>='\u0D9A' && LA33_0<='\u0DB1')||(LA33_0>='\u0DB3' && LA33_0<='\u0DBB')||LA33_0=='\u0DBD'||(LA33_0>='\u0DC0' && LA33_0<='\u0DC6')||(LA33_0>='\u0E01' && LA33_0<='\u0E30')||(LA33_0>='\u0E32' && LA33_0<='\u0E33')||(LA33_0>='\u0E3F' && LA33_0<='\u0E46')||(LA33_0>='\u0E81' && LA33_0<='\u0E82')||LA33_0=='\u0E84'||(LA33_0>='\u0E87' && LA33_0<='\u0E88')||LA33_0=='\u0E8A'||LA33_0=='\u0E8D'||(LA33_0>='\u0E94' && LA33_0<='\u0E97')||(LA33_0>='\u0E99' && LA33_0<='\u0E9F')||(LA33_0>='\u0EA1' && LA33_0<='\u0EA3')||LA33_0=='\u0EA5'||LA33_0=='\u0EA7'||(LA33_0>='\u0EAA' && LA33_0<='\u0EAB')||(LA33_0>='\u0EAD' && LA33_0<='\u0EB0')||(LA33_0>='\u0EB2' && LA33_0<='\u0EB3')||LA33_0=='\u0EBD'||(LA33_0>='\u0EC0' && LA33_0<='\u0EC4')||LA33_0=='\u0EC6'||(LA33_0>='\u0EDC' && LA33_0<='\u0EDD')||LA33_0=='\u0F00'||(LA33_0>='\u0F40' && LA33_0<='\u0F47')||(LA33_0>='\u0F49' && LA33_0<='\u0F6A')||(LA33_0>='\u0F88' && LA33_0<='\u0F8B')||(LA33_0>='\u1000' && LA33_0<='\u1021')||(LA33_0>='\u1023' && LA33_0<='\u1027')||(LA33_0>='\u1029' && LA33_0<='\u102A')||(LA33_0>='\u1050' && LA33_0<='\u1055')||(LA33_0>='\u10A0' && LA33_0<='\u10C5')||(LA33_0>='\u10D0' && LA33_0<='\u10F8')||(LA33_0>='\u1100' && LA33_0<='\u1159')||(LA33_0>='\u115F' && LA33_0<='\u11A2')||(LA33_0>='\u11A8' && LA33_0<='\u11F9')||(LA33_0>='\u1200' && LA33_0<='\u1206')||(LA33_0>='\u1208' && LA33_0<='\u1246')||LA33_0=='\u1248'||(LA33_0>='\u124A' && LA33_0<='\u124D')||(LA33_0>='\u1250' && LA33_0<='\u1256')||LA33_0=='\u1258'||(LA33_0>='\u125A' && LA33_0<='\u125D')||(LA33_0>='\u1260' && LA33_0<='\u1286')||LA33_0=='\u1288'||(LA33_0>='\u128A' && LA33_0<='\u128D')||(LA33_0>='\u1290' && LA33_0<='\u12AE')||LA33_0=='\u12B0'||(LA33_0>='\u12B2' && LA33_0<='\u12B5')||(LA33_0>='\u12B8' && LA33_0<='\u12BE')||LA33_0=='\u12C0'||(LA33_0>='\u12C2' && LA33_0<='\u12C5')||(LA33_0>='\u12C8' && LA33_0<='\u12CE')||(LA33_0>='\u12D0' && LA33_0<='\u12D6')||(LA33_0>='\u12D8' && LA33_0<='\u12EE')||(LA33_0>='\u12F0' && LA33_0<='\u130E')||LA33_0=='\u1310'||(LA33_0>='\u1312' && LA33_0<='\u1315')||(LA33_0>='\u1318' && LA33_0<='\u131E')||(LA33_0>='\u1320' && LA33_0<='\u1346')||(LA33_0>='\u1348' && LA33_0<='\u135A')||(LA33_0>='\u13A0' && LA33_0<='\u13F4')||(LA33_0>='\u1401' && LA33_0<='\u166C')||(LA33_0>='\u166F' && LA33_0<='\u1676')||(LA33_0>='\u1681' && LA33_0<='\u169A')||(LA33_0>='\u16A0' && LA33_0<='\u16EA')||(LA33_0>='\u16EE' && LA33_0<='\u16F0')||(LA33_0>='\u1700' && LA33_0<='\u170C')||(LA33_0>='\u170E' && LA33_0<='\u1711')||(LA33_0>='\u1720' && LA33_0<='\u1731')||(LA33_0>='\u1740' && LA33_0<='\u1751')||(LA33_0>='\u1760' && LA33_0<='\u176C')||(LA33_0>='\u176E' && LA33_0<='\u1770')||(LA33_0>='\u1780' && LA33_0<='\u17B3')||LA33_0=='\u17D7'||(LA33_0>='\u17DB' && LA33_0<='\u17DC')||(LA33_0>='\u1820' && LA33_0<='\u1877')||(LA33_0>='\u1880' && LA33_0<='\u18A8')||(LA33_0>='\u1900' && LA33_0<='\u191C')||(LA33_0>='\u1950' && LA33_0<='\u196D')||(LA33_0>='\u1970' && LA33_0<='\u1974')||(LA33_0>='\u1D00' && LA33_0<='\u1D6B')||(LA33_0>='\u1E00' && LA33_0<='\u1E9B')||(LA33_0>='\u1EA0' && LA33_0<='\u1EF9')||(LA33_0>='\u1F00' && LA33_0<='\u1F15')||(LA33_0>='\u1F18' && LA33_0<='\u1F1D')||(LA33_0>='\u1F20' && LA33_0<='\u1F45')||(LA33_0>='\u1F48' && LA33_0<='\u1F4D')||(LA33_0>='\u1F50' && LA33_0<='\u1F57')||LA33_0=='\u1F59'||LA33_0=='\u1F5B'||LA33_0=='\u1F5D'||(LA33_0>='\u1F5F' && LA33_0<='\u1F7D')||(LA33_0>='\u1F80' && LA33_0<='\u1FB4')||(LA33_0>='\u1FB6' && LA33_0<='\u1FBC')||LA33_0=='\u1FBE'||(LA33_0>='\u1FC2' && LA33_0<='\u1FC4')||(LA33_0>='\u1FC6' && LA33_0<='\u1FCC')||(LA33_0>='\u1FD0' && LA33_0<='\u1FD3')||(LA33_0>='\u1FD6' && LA33_0<='\u1FDB')||(LA33_0>='\u1FE0' && LA33_0<='\u1FEC')||(LA33_0>='\u1FF2' && LA33_0<='\u1FF4')||(LA33_0>='\u1FF6' && LA33_0<='\u1FFC')||(LA33_0>='\u203F' && LA33_0<='\u2040')||LA33_0=='\u2054'||LA33_0=='\u2071'||LA33_0=='\u207F'||(LA33_0>='\u20A0' && LA33_0<='\u20B1')||LA33_0=='\u2102'||LA33_0=='\u2107'||(LA33_0>='\u210A' && LA33_0<='\u2113')||LA33_0=='\u2115'||(LA33_0>='\u2119' && LA33_0<='\u211D')||LA33_0=='\u2124'||LA33_0=='\u2126'||LA33_0=='\u2128'||(LA33_0>='\u212A' && LA33_0<='\u212D')||(LA33_0>='\u212F' && LA33_0<='\u2131')||(LA33_0>='\u2133' && LA33_0<='\u2139')||(LA33_0>='\u213D' && LA33_0<='\u213F')||(LA33_0>='\u2145' && LA33_0<='\u2149')||(LA33_0>='\u2160' && LA33_0<='\u2183')||(LA33_0>='\u3005' && LA33_0<='\u3007')||(LA33_0>='\u3021' && LA33_0<='\u3029')||(LA33_0>='\u3031' && LA33_0<='\u3035')||(LA33_0>='\u3038' && LA33_0<='\u303C')||(LA33_0>='\u3041' && LA33_0<='\u3096')||(LA33_0>='\u309D' && LA33_0<='\u309F')||(LA33_0>='\u30A1' && LA33_0<='\u30FF')||(LA33_0>='\u3105' && LA33_0<='\u312C')||(LA33_0>='\u3131' && LA33_0<='\u318E')||(LA33_0>='\u31A0' && LA33_0<='\u31B7')||(LA33_0>='\u31F0' && LA33_0<='\u31FF')||(LA33_0>='\u3400' && LA33_0<='\u4DB5')||(LA33_0>='\u4E00' && LA33_0<='\u9FA5')||(LA33_0>='\uA000' && LA33_0<='\uA48C')||(LA33_0>='\uAC00' && LA33_0<='\uD7A3')||(LA33_0>='\uD800' && LA33_0<='\uDBFF')||(LA33_0>='\uF900' && LA33_0<='\uFA2D')||(LA33_0>='\uFA30' && LA33_0<='\uFA6A')||(LA33_0>='\uFB00' && LA33_0<='\uFB06')||(LA33_0>='\uFB13' && LA33_0<='\uFB17')||LA33_0=='\uFB1D'||(LA33_0>='\uFB1F' && LA33_0<='\uFB28')||(LA33_0>='\uFB2A' && LA33_0<='\uFB36')||(LA33_0>='\uFB38' && LA33_0<='\uFB3C')||LA33_0=='\uFB3E'||(LA33_0>='\uFB40' && LA33_0<='\uFB41')||(LA33_0>='\uFB43' && LA33_0<='\uFB44')||(LA33_0>='\uFB46' && LA33_0<='\uFBB1')||(LA33_0>='\uFBD3' && LA33_0<='\uFD3D')||(LA33_0>='\uFD50' && LA33_0<='\uFD8F')||(LA33_0>='\uFD92' && LA33_0<='\uFDC7')||(LA33_0>='\uFDF0' && LA33_0<='\uFDFC')||(LA33_0>='\uFE33' && LA33_0<='\uFE34')||(LA33_0>='\uFE4D' && LA33_0<='\uFE4F')||LA33_0=='\uFE69'||(LA33_0>='\uFE70' && LA33_0<='\uFE74')||(LA33_0>='\uFE76' && LA33_0<='\uFEFC')||LA33_0=='\uFF04'||(LA33_0>='\uFF21' && LA33_0<='\uFF3A')||LA33_0=='\uFF3F'||(LA33_0>='\uFF41' && LA33_0<='\uFF5A')||(LA33_0>='\uFF65' && LA33_0<='\uFFBE')||(LA33_0>='\uFFC2' && LA33_0<='\uFFC7')||(LA33_0>='\uFFCA' && LA33_0<='\uFFCF')||(LA33_0>='\uFFD2' && LA33_0<='\uFFD7')||(LA33_0>='\uFFDA' && LA33_0<='\uFFDC')||(LA33_0>='\uFFE0' && LA33_0<='\uFFE1')||(LA33_0>='\uFFE5' && LA33_0<='\uFFE6')) ) {
            alt33=103;
        }
        else {
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( LONGLITERAL | INTLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | WS | COMMENT | LINE_COMMENT | ABSTRACT | ASSERT | BOOLEAN | BREAK | BYTE | CASE | CATCH | CHAR | CLASS | CONST | CONTINUE | DEFAULT | DO | DOUBLE | ELSE | ENUM | EXTENDS | FINAL | FINALLY | FLOAT | FOR | GOTO | IF | IMPLEMENTS | IMPORT | INSTANCEOF | INT | INTERFACE | LONG | NATIVE | NEW | PACKAGE | PRIVATE | PROTECTED | PUBLIC | RETURN | SHORT | STATIC | STRICTFP | SUPER | SWITCH | SYNCHRONIZED | THIS | THROW | THROWS | TRANSIENT | TRY | VOID | VOLATILE | WHILE | TRUE | FALSE | NULL | LPAREN | RPAREN | LBRACE | RBRACE | LBRACKET | RBRACKET | SEMI | COMMA | DOT | ELLIPSIS | EQ | BANG | TILDE | QUES | COLON | EQEQ | AMPAMP | BARBAR | PLUSPLUS | SUBSUB | PLUS | SUB | STAR | SLASH | AMP | BAR | CARET | PERCENT | PLUSEQ | SUBEQ | STAREQ | SLASHEQ | AMPEQ | BAREQ | CARETEQ | PERCENTEQ | MONKEYS_AT | BANGEQ | GT | LT | IDENTIFIER );", 33, 0, input);

            throw nvae;
        }
        switch (alt33) {
            case 1 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:10: LONGLITERAL
                {
                mLONGLITERAL(); 

                }
                break;
            case 2 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:22: INTLITERAL
                {
                mINTLITERAL(); 

                }
                break;
            case 3 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:33: FLOATLITERAL
                {
                mFLOATLITERAL(); 

                }
                break;
            case 4 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:46: DOUBLELITERAL
                {
                mDOUBLELITERAL(); 

                }
                break;
            case 5 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:60: CHARLITERAL
                {
                mCHARLITERAL(); 

                }
                break;
            case 6 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:72: STRINGLITERAL
                {
                mSTRINGLITERAL(); 

                }
                break;
            case 7 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:86: WS
                {
                mWS(); 

                }
                break;
            case 8 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:89: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 9 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:97: LINE_COMMENT
                {
                mLINE_COMMENT(); 

                }
                break;
            case 10 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:110: ABSTRACT
                {
                mABSTRACT(); 

                }
                break;
            case 11 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:119: ASSERT
                {
                mASSERT(); 

                }
                break;
            case 12 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:126: BOOLEAN
                {
                mBOOLEAN(); 

                }
                break;
            case 13 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:134: BREAK
                {
                mBREAK(); 

                }
                break;
            case 14 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:140: BYTE
                {
                mBYTE(); 

                }
                break;
            case 15 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:145: CASE
                {
                mCASE(); 

                }
                break;
            case 16 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:150: CATCH
                {
                mCATCH(); 

                }
                break;
            case 17 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:156: CHAR
                {
                mCHAR(); 

                }
                break;
            case 18 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:161: CLASS
                {
                mCLASS(); 

                }
                break;
            case 19 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:167: CONST
                {
                mCONST(); 

                }
                break;
            case 20 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:173: CONTINUE
                {
                mCONTINUE(); 

                }
                break;
            case 21 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:182: DEFAULT
                {
                mDEFAULT(); 

                }
                break;
            case 22 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:190: DO
                {
                mDO(); 

                }
                break;
            case 23 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:193: DOUBLE
                {
                mDOUBLE(); 

                }
                break;
            case 24 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:200: ELSE
                {
                mELSE(); 

                }
                break;
            case 25 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:205: ENUM
                {
                mENUM(); 

                }
                break;
            case 26 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:210: EXTENDS
                {
                mEXTENDS(); 

                }
                break;
            case 27 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:218: FINAL
                {
                mFINAL(); 

                }
                break;
            case 28 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:224: FINALLY
                {
                mFINALLY(); 

                }
                break;
            case 29 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:232: FLOAT
                {
                mFLOAT(); 

                }
                break;
            case 30 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:238: FOR
                {
                mFOR(); 

                }
                break;
            case 31 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:242: GOTO
                {
                mGOTO(); 

                }
                break;
            case 32 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:247: IF
                {
                mIF(); 

                }
                break;
            case 33 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:250: IMPLEMENTS
                {
                mIMPLEMENTS(); 

                }
                break;
            case 34 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:261: IMPORT
                {
                mIMPORT(); 

                }
                break;
            case 35 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:268: INSTANCEOF
                {
                mINSTANCEOF(); 

                }
                break;
            case 36 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:279: INT
                {
                mINT(); 

                }
                break;
            case 37 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:283: INTERFACE
                {
                mINTERFACE(); 

                }
                break;
            case 38 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:293: LONG
                {
                mLONG(); 

                }
                break;
            case 39 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:298: NATIVE
                {
                mNATIVE(); 

                }
                break;
            case 40 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:305: NEW
                {
                mNEW(); 

                }
                break;
            case 41 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:309: PACKAGE
                {
                mPACKAGE(); 

                }
                break;
            case 42 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:317: PRIVATE
                {
                mPRIVATE(); 

                }
                break;
            case 43 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:325: PROTECTED
                {
                mPROTECTED(); 

                }
                break;
            case 44 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:335: PUBLIC
                {
                mPUBLIC(); 

                }
                break;
            case 45 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:342: RETURN
                {
                mRETURN(); 

                }
                break;
            case 46 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:349: SHORT
                {
                mSHORT(); 

                }
                break;
            case 47 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:355: STATIC
                {
                mSTATIC(); 

                }
                break;
            case 48 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:362: STRICTFP
                {
                mSTRICTFP(); 

                }
                break;
            case 49 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:371: SUPER
                {
                mSUPER(); 

                }
                break;
            case 50 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:377: SWITCH
                {
                mSWITCH(); 

                }
                break;
            case 51 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:384: SYNCHRONIZED
                {
                mSYNCHRONIZED(); 

                }
                break;
            case 52 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:397: THIS
                {
                mTHIS(); 

                }
                break;
            case 53 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:402: THROW
                {
                mTHROW(); 

                }
                break;
            case 54 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:408: THROWS
                {
                mTHROWS(); 

                }
                break;
            case 55 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:415: TRANSIENT
                {
                mTRANSIENT(); 

                }
                break;
            case 56 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:425: TRY
                {
                mTRY(); 

                }
                break;
            case 57 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:429: VOID
                {
                mVOID(); 

                }
                break;
            case 58 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:434: VOLATILE
                {
                mVOLATILE(); 

                }
                break;
            case 59 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:443: WHILE
                {
                mWHILE(); 

                }
                break;
            case 60 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:449: TRUE
                {
                mTRUE(); 

                }
                break;
            case 61 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:454: FALSE
                {
                mFALSE(); 

                }
                break;
            case 62 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:460: NULL
                {
                mNULL(); 

                }
                break;
            case 63 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:465: LPAREN
                {
                mLPAREN(); 

                }
                break;
            case 64 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:472: RPAREN
                {
                mRPAREN(); 

                }
                break;
            case 65 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:479: LBRACE
                {
                mLBRACE(); 

                }
                break;
            case 66 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:486: RBRACE
                {
                mRBRACE(); 

                }
                break;
            case 67 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:493: LBRACKET
                {
                mLBRACKET(); 

                }
                break;
            case 68 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:502: RBRACKET
                {
                mRBRACKET(); 

                }
                break;
            case 69 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:511: SEMI
                {
                mSEMI(); 

                }
                break;
            case 70 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:516: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 71 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:522: DOT
                {
                mDOT(); 

                }
                break;
            case 72 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:526: ELLIPSIS
                {
                mELLIPSIS(); 

                }
                break;
            case 73 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:535: EQ
                {
                mEQ(); 

                }
                break;
            case 74 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:538: BANG
                {
                mBANG(); 

                }
                break;
            case 75 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:543: TILDE
                {
                mTILDE(); 

                }
                break;
            case 76 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:549: QUES
                {
                mQUES(); 

                }
                break;
            case 77 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:554: COLON
                {
                mCOLON(); 

                }
                break;
            case 78 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:560: EQEQ
                {
                mEQEQ(); 

                }
                break;
            case 79 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:565: AMPAMP
                {
                mAMPAMP(); 

                }
                break;
            case 80 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:572: BARBAR
                {
                mBARBAR(); 

                }
                break;
            case 81 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:579: PLUSPLUS
                {
                mPLUSPLUS(); 

                }
                break;
            case 82 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:588: SUBSUB
                {
                mSUBSUB(); 

                }
                break;
            case 83 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:595: PLUS
                {
                mPLUS(); 

                }
                break;
            case 84 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:600: SUB
                {
                mSUB(); 

                }
                break;
            case 85 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:604: STAR
                {
                mSTAR(); 

                }
                break;
            case 86 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:609: SLASH
                {
                mSLASH(); 

                }
                break;
            case 87 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:615: AMP
                {
                mAMP(); 

                }
                break;
            case 88 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:619: BAR
                {
                mBAR(); 

                }
                break;
            case 89 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:623: CARET
                {
                mCARET(); 

                }
                break;
            case 90 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:629: PERCENT
                {
                mPERCENT(); 

                }
                break;
            case 91 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:637: PLUSEQ
                {
                mPLUSEQ(); 

                }
                break;
            case 92 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:644: SUBEQ
                {
                mSUBEQ(); 

                }
                break;
            case 93 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:650: STAREQ
                {
                mSTAREQ(); 

                }
                break;
            case 94 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:657: SLASHEQ
                {
                mSLASHEQ(); 

                }
                break;
            case 95 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:665: AMPEQ
                {
                mAMPEQ(); 

                }
                break;
            case 96 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:671: BAREQ
                {
                mBAREQ(); 

                }
                break;
            case 97 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:677: CARETEQ
                {
                mCARETEQ(); 

                }
                break;
            case 98 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:685: PERCENTEQ
                {
                mPERCENTEQ(); 

                }
                break;
            case 99 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:695: MONKEYS_AT
                {
                mMONKEYS_AT(); 

                }
                break;
            case 100 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:706: BANGEQ
                {
                mBANGEQ(); 

                }
                break;
            case 101 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:713: GT
                {
                mGT(); 

                }
                break;
            case 102 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:716: LT
                {
                mLT(); 

                }
                break;
            case 103 :
                // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\JadexJavaRules.g:1:719: IDENTIFIER
                {
                mIDENTIFIER(); 

                }
                break;

        }

    }


    protected DFA18 dfa18 = new DFA18(this);
    protected DFA29 dfa29 = new DFA29(this);
    static final String DFA18_eotS =
        "\1\uffff\1\5\1\uffff\1\5\4\uffff";
    static final String DFA18_eofS =
        "\10\uffff";
    static final String DFA18_minS =
        "\2\56\1\uffff\1\56\4\uffff";
    static final String DFA18_maxS =
        "\1\71\1\170\1\uffff\1\145\4\uffff";
    static final String DFA18_acceptS =
        "\2\uffff\1\2\1\uffff\1\5\1\4\1\1\1\3";
    static final String DFA18_specialS =
        "\10\uffff}>";
    static final String[] DFA18_transitionS = {
            "\1\2\1\uffff\1\1\11\3",
            "\1\6\1\uffff\12\3\13\uffff\1\7\22\uffff\1\4\14\uffff\1\7\22"+
            "\uffff\1\4",
            "",
            "\1\6\1\uffff\12\3\13\uffff\1\7\37\uffff\1\7",
            "",
            "",
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
            return "480:1: fragment NonIntegerNumber : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? | '.' ( '0' .. '9' )+ ( Exponent )? | ( '0' .. '9' )+ Exponent | ( '0' .. '9' )+ | HexPrefix ( HexDigit )* ( () | ( '.' ( HexDigit )* ) ) ( 'p' | 'P' ) ( '+' | '-' )? ( '0' .. '9' )+ );";
        }
    }
    static final String DFA29_eotS =
        "\2\uffff\2\4\2\uffff";
    static final String DFA29_eofS =
        "\6\uffff";
    static final String DFA29_minS =
        "\2\57\2\0\2\uffff";
    static final String DFA29_maxS =
        "\2\57\2\ufffe\2\uffff";
    static final String DFA29_acceptS =
        "\4\uffff\1\2\1\1";
    static final String DFA29_specialS =
        "\6\uffff}>";
    static final String[] DFA29_transitionS = {
            "\1\1",
            "\1\2",
            "\12\3\1\5\2\3\1\5\ufff1\3",
            "\12\3\1\5\2\3\1\5\ufff1\3",
            "",
            ""
    };

    static final short[] DFA29_eot = DFA.unpackEncodedString(DFA29_eotS);
    static final short[] DFA29_eof = DFA.unpackEncodedString(DFA29_eofS);
    static final char[] DFA29_min = DFA.unpackEncodedStringToUnsignedChars(DFA29_minS);
    static final char[] DFA29_max = DFA.unpackEncodedStringToUnsignedChars(DFA29_maxS);
    static final short[] DFA29_accept = DFA.unpackEncodedString(DFA29_acceptS);
    static final short[] DFA29_special = DFA.unpackEncodedString(DFA29_specialS);
    static final short[][] DFA29_transition;

    static {
        int numStates = DFA29_transitionS.length;
        DFA29_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA29_transition[i] = DFA.unpackEncodedString(DFA29_transitionS[i]);
        }
    }

    class DFA29 extends DFA {

        public DFA29(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 29;
            this.eot = DFA29_eot;
            this.eof = DFA29_eof;
            this.min = DFA29_min;
            this.max = DFA29_max;
            this.accept = DFA29_accept;
            this.special = DFA29_special;
            this.transition = DFA29_transition;
        }
        public String getDescription() {
            return "589:1: LINE_COMMENT : ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r\\n' | '\\r' | '\\n' ) | '//' (~ ( '\\n' | '\\r' ) )* );";
        }
    }
 

}