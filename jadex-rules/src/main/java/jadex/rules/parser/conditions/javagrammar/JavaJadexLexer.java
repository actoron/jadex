// $ANTLR 3.0.1 C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g 2009-03-19 18:19:47

package jadex.rules.parser.conditions.javagrammar;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class JavaJadexLexer extends Lexer {
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
    public static final int NonIntegerNumber=19;
    public static final int FloatSuffix=20;
    public static final int STAREQ=110;
    public static final int CARET=106;
    public static final int THIS=69;
    public static final int RETURN=62;
    public static final int DOUBLE=40;
    public static final int MONKEYS_AT=116;
    public static final int BARBAR=97;
    public static final int VOID=74;
    public static final int SUPER=66;
    public static final int EQ=90;
    public static final int GOTO=48;
    public static final int AMPAMP=96;
    public static final int COMMENT=127;
    public static final int QUES=93;
    public static final int EQEQ=95;
    public static final int HexPrefix=16;
    public static final int RBRACE=83;
    public static final int LINE_COMMENT=128;
    public static final int IntegerTypeSuffix=120;
    public static final int STATIC=64;
    public static final int PRIVATE=59;
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
    public static final int TRANSIENT=72;
    public static final int IDENTIFIER=4;
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
    public static final int Tokens=129;
    public static final int DecimalLiteral=11;
    public static final int SEMI=86;
    public static final int TRUE=77;
    public static final int StringLiteral=6;
    public static final int COLON=94;
    public static final int ENUM=42;
    public static final int FINALLY=45;
    public static final int DoubleSuffix=21;
    public static final int PERCENTEQ=115;
    public static final int STRINGLITERAL=26;
    public static final int UnicodeEscape=122;
    public static final int CARETEQ=114;
    public static final int INTERFACE=54;
    public static final int LONG=55;
    public static final int PUBLIC=61;
    public static final int FLOATLITERAL=22;
    public static final int EXTENDS=43;
    public static final int OctalEscape=123;
    public static final int BAR=105;
    public JavaJadexLexer() {;} 
    public JavaJadexLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g"; }

    // $ANTLR start LONGLITERAL
    public final void mLONGLITERAL() throws RecognitionException {
        try {
            int _type = LONGLITERAL;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:123:5: ( IntegerNumber LongSuffix )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:123:9: IntegerNumber LongSuffix
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:128:5: ( IntegerNumber )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:128:9: IntegerNumber
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:133:5: ( '0' | '1' .. '9' ( '0' .. '9' )* | '0' ( '0' .. '7' )+ | HexPrefix ( HexDigit )+ )
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
                    new NoViableAltException("131:1: fragment IntegerNumber : ( '0' | '1' .. '9' ( '0' .. '9' )* | '0' ( '0' .. '7' )+ | HexPrefix ( HexDigit )+ );", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:133:9: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:134:9: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:134:18: ( '0' .. '9' )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:134:19: '0' .. '9'
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:135:9: '0' ( '0' .. '7' )+
                    {
                    match('0'); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:135:13: ( '0' .. '7' )+
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
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:135:14: '0' .. '7'
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:136:9: HexPrefix ( HexDigit )+
                    {
                    mHexPrefix(); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:136:19: ( HexDigit )+
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
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:136:19: HexDigit
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:141:5: ( '0x' | '0X' )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='0') ) {
                int LA5_1 = input.LA(2);

                if ( (LA5_1=='x') ) {
                    alt5=1;
                }
                else if ( (LA5_1=='X') ) {
                    alt5=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("139:1: fragment HexPrefix : ( '0x' | '0X' );", 5, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("139:1: fragment HexPrefix : ( '0x' | '0X' );", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:141:9: '0x'
                    {
                    match("0x"); 


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:141:16: '0X'
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

    // $ANTLR start LongSuffix
    public final void mLongSuffix() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:146:5: ( 'l' | 'L' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:152:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? | '.' ( '0' .. '9' )+ ( Exponent )? | ( '0' .. '9' )+ Exponent | ( '0' .. '9' )+ | HexPrefix ( HexDigit )* ( () | ( '.' ( HexDigit )* ) ) ( 'p' | 'P' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            int alt18=5;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:152:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )?
                    {
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:152:9: ( '0' .. '9' )+
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
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:152:10: '0' .. '9'
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:152:27: ( '0' .. '9' )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:152:28: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:152:41: ( Exponent )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0=='E'||LA8_0=='e') ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:152:41: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:153:9: '.' ( '0' .. '9' )+ ( Exponent )?
                    {
                    match('.'); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:153:13: ( '0' .. '9' )+
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
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:153:15: '0' .. '9'
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

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:153:29: ( Exponent )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='E'||LA10_0=='e') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:153:29: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:154:9: ( '0' .. '9' )+ Exponent
                    {
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:154:9: ( '0' .. '9' )+
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
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:154:10: '0' .. '9'
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:155:9: ( '0' .. '9' )+
                    {
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:155:9: ( '0' .. '9' )+
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
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:155:10: '0' .. '9'
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:157:9: HexPrefix ( HexDigit )* ( () | ( '.' ( HexDigit )* ) ) ( 'p' | 'P' ) ( '+' | '-' )? ( '0' .. '9' )+
                    {
                    mHexPrefix(); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:157:19: ( HexDigit )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( ((LA13_0>='0' && LA13_0<='9')||(LA13_0>='A' && LA13_0<='F')||(LA13_0>='a' && LA13_0<='f')) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:157:20: HexDigit
                    	    {
                    	    mHexDigit(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop13;
                        }
                    } while (true);

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:158:9: ( () | ( '.' ( HexDigit )* ) )
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
                            new NoViableAltException("158:9: ( () | ( '.' ( HexDigit )* ) )", 15, 0, input);

                        throw nvae;
                    }
                    switch (alt15) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:158:14: ()
                            {
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:158:14: ()
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:158:15: 
                            {
                            }


                            }
                            break;
                        case 2 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:159:14: ( '.' ( HexDigit )* )
                            {
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:159:14: ( '.' ( HexDigit )* )
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:159:15: '.' ( HexDigit )*
                            {
                            match('.'); 
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:159:19: ( HexDigit )*
                            loop14:
                            do {
                                int alt14=2;
                                int LA14_0 = input.LA(1);

                                if ( ((LA14_0>='0' && LA14_0<='9')||(LA14_0>='A' && LA14_0<='F')||(LA14_0>='a' && LA14_0<='f')) ) {
                                    alt14=1;
                                }


                                switch (alt14) {
                            	case 1 :
                            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:159:20: HexDigit
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

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:162:9: ( '+' | '-' )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0=='+'||LA16_0=='-') ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
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

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:163:9: ( '0' .. '9' )+
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
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:163:11: '0' .. '9'
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

    // $ANTLR start FloatSuffix
    public final void mFloatSuffix() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:168:5: ( 'f' | 'F' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:173:5: ( 'd' | 'D' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:177:5: ( NonIntegerNumber FloatSuffix )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:177:9: NonIntegerNumber FloatSuffix
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:181:5: ( NonIntegerNumber ( DoubleSuffix )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:181:9: NonIntegerNumber ( DoubleSuffix )?
            {
            mNonIntegerNumber(); 
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:181:26: ( DoubleSuffix )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0=='D'||LA19_0=='d') ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:181:26: DoubleSuffix
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:185:5: ( '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) ) '\\'' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:185:9: '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) ) '\\''
            {
            match('\''); 
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:186:9: ( EscapeSequence | ~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0=='\\') ) {
                alt20=1;
            }
            else if ( ((LA20_0>='\u0000' && LA20_0<='\t')||(LA20_0>='\u000B' && LA20_0<='\f')||(LA20_0>='\u000E' && LA20_0<='&')||(LA20_0>='(' && LA20_0<='[')||(LA20_0>=']' && LA20_0<='\uFFFE')) ) {
                alt20=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("186:9: ( EscapeSequence | ~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) )", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:186:13: EscapeSequence
                    {
                    mEscapeSequence(); 

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:187:13: ~ ( '\\'' | '\\\\' | '\\r' | '\\n' )
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:193:5: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' | '\\r' | '\\n' ) )* '\"' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:193:9: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' | '\\r' | '\\n' ) )* '\"'
            {
            match('\"'); 
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:194:9: ( EscapeSequence | ~ ( '\\\\' | '\"' | '\\r' | '\\n' ) )*
            loop21:
            do {
                int alt21=3;
                int LA21_0 = input.LA(1);

                if ( (LA21_0=='\\') ) {
                    alt21=1;
                }
                else if ( ((LA21_0>='\u0000' && LA21_0<='\t')||(LA21_0>='\u000B' && LA21_0<='\f')||(LA21_0>='\u000E' && LA21_0<='!')||(LA21_0>='#' && LA21_0<='[')||(LA21_0>=']' && LA21_0<='\uFFFE')) ) {
                    alt21=2;
                }


                switch (alt21) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:194:13: EscapeSequence
            	    {
            	    mEscapeSequence(); 

            	    }
            	    break;
            	case 2 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:195:13: ~ ( '\\\\' | '\"' | '\\r' | '\\n' )
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
            	    break loop21;
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

    // $ANTLR start ABSTRACT
    public final void mABSTRACT() throws RecognitionException {
        try {
            int _type = ABSTRACT;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:201:5: ( 'abstract' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:201:9: 'abstract'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:205:5: ( 'assert' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:205:9: 'assert'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:209:5: ( 'boolean' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:209:9: 'boolean'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:213:5: ( 'break' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:213:9: 'break'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:217:5: ( 'byte' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:217:9: 'byte'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:221:5: ( 'case' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:221:9: 'case'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:225:5: ( 'catch' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:225:9: 'catch'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:229:5: ( 'char' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:229:9: 'char'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:233:5: ( 'class' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:233:9: 'class'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:237:5: ( 'const' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:237:9: 'const'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:241:5: ( 'continue' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:241:9: 'continue'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:245:5: ( 'default' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:245:9: 'default'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:249:5: ( 'do' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:249:9: 'do'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:253:5: ( 'double' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:253:9: 'double'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:257:5: ( 'else' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:257:9: 'else'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:261:5: ( 'enum' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:261:9: 'enum'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:265:5: ( 'extends' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:265:9: 'extends'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:269:5: ( 'final' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:269:9: 'final'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:273:5: ( 'finally' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:273:9: 'finally'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:277:5: ( 'float' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:277:9: 'float'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:281:5: ( 'for' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:281:9: 'for'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:285:5: ( 'goto' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:285:9: 'goto'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:289:5: ( 'if' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:289:9: 'if'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:293:5: ( 'implements' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:293:9: 'implements'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:297:5: ( 'import' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:297:9: 'import'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:301:5: ( 'instanceof' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:301:9: 'instanceof'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:305:5: ( 'int' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:305:9: 'int'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:309:5: ( 'interface' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:309:9: 'interface'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:313:5: ( 'long' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:313:9: 'long'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:317:5: ( 'native' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:317:9: 'native'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:321:5: ( 'new' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:321:9: 'new'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:325:5: ( 'package' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:325:9: 'package'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:329:5: ( 'private' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:329:9: 'private'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:333:5: ( 'protected' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:333:9: 'protected'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:337:5: ( 'public' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:337:9: 'public'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:341:5: ( 'return' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:341:9: 'return'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:345:5: ( 'short' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:345:9: 'short'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:349:5: ( 'static' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:349:9: 'static'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:353:5: ( 'strictfp' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:353:9: 'strictfp'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:357:5: ( 'super' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:357:9: 'super'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:361:5: ( 'switch' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:361:9: 'switch'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:365:5: ( 'synchronized' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:365:9: 'synchronized'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:369:5: ( 'this' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:369:9: 'this'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:373:5: ( 'throw' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:373:9: 'throw'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:377:5: ( 'throws' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:377:9: 'throws'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:381:5: ( 'transient' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:381:9: 'transient'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:385:5: ( 'try' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:385:9: 'try'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:389:5: ( 'void' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:389:9: 'void'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:393:5: ( 'volatile' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:393:9: 'volatile'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:397:5: ( 'while' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:397:9: 'while'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:401:5: ( 'true' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:401:9: 'true'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:405:5: ( 'false' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:405:9: 'false'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:409:5: ( 'null' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:409:9: 'null'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:413:5: ( '(' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:413:9: '('
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:417:5: ( ')' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:417:9: ')'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:421:5: ( '{' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:421:9: '{'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:425:5: ( '}' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:425:9: '}'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:429:5: ( '[' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:429:9: '['
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:433:5: ( ']' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:433:9: ']'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:437:5: ( ';' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:437:9: ';'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:441:5: ( ',' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:441:9: ','
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:445:5: ( '.' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:445:9: '.'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:449:5: ( '...' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:449:9: '...'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:453:5: ( '=' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:453:9: '='
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:457:5: ( '!' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:457:9: '!'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:461:5: ( '~' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:461:9: '~'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:465:5: ( '?' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:465:9: '?'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:469:5: ( ':' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:469:9: ':'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:473:5: ( '==' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:473:9: '=='
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:477:5: ( '&&' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:477:9: '&&'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:481:5: ( '||' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:481:9: '||'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:485:5: ( '++' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:485:9: '++'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:489:5: ( '--' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:489:9: '--'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:493:5: ( '+' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:493:9: '+'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:497:5: ( '-' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:497:9: '-'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:501:5: ( '*' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:501:9: '*'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:505:5: ( '/' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:505:9: '/'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:509:5: ( '&' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:509:9: '&'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:513:5: ( '|' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:513:9: '|'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:517:5: ( '^' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:517:9: '^'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:521:5: ( '%' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:521:9: '%'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:525:5: ( '+=' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:525:9: '+='
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:529:5: ( '-=' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:529:9: '-='
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:533:5: ( '*=' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:533:9: '*='
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:537:5: ( '/=' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:537:9: '/='
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:541:5: ( '&=' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:541:9: '&='
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:545:5: ( '|=' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:545:9: '|='
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:549:5: ( '^=' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:549:9: '^='
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:553:5: ( '%=' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:553:9: '%='
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:557:5: ( '@' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:557:9: '@'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:561:5: ( '!=' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:561:9: '!='
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:565:5: ( '>' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:565:9: '>'
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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:569:5: ( '<' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:569:9: '<'
            {
            match('<'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LT

    // $ANTLR start BooleanLiteral
    public final void mBooleanLiteral() throws RecognitionException {
        try {
            int _type = BooleanLiteral;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:573:2: ( 'true' | 'false' )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0=='t') ) {
                alt22=1;
            }
            else if ( (LA22_0=='f') ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("572:1: BooleanLiteral : ( 'true' | 'false' );", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:573:6: 'true'
                    {
                    match("true"); 


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:573:15: 'false'
                    {
                    match("false"); 


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BooleanLiteral

    // $ANTLR start HexLiteral
    public final void mHexLiteral() throws RecognitionException {
        try {
            int _type = HexLiteral;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:577:2: ( '0' ( 'x' | 'X' ) ( HexDigit )+ ( IntegerTypeSuffix )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:577:4: '0' ( 'x' | 'X' ) ( HexDigit )+ ( IntegerTypeSuffix )?
            {
            match('0'); 
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:577:18: ( HexDigit )+
            int cnt23=0;
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( ((LA23_0>='0' && LA23_0<='9')||(LA23_0>='A' && LA23_0<='F')||(LA23_0>='a' && LA23_0<='f')) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:577:18: HexDigit
            	    {
            	    mHexDigit(); 

            	    }
            	    break;

            	default :
            	    if ( cnt23 >= 1 ) break loop23;
                        EarlyExitException eee =
                            new EarlyExitException(23, input);
                        throw eee;
                }
                cnt23++;
            } while (true);

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:577:28: ( IntegerTypeSuffix )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0=='L'||LA24_0=='l') ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:577:28: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end HexLiteral

    // $ANTLR start DecimalLiteral
    public final void mDecimalLiteral() throws RecognitionException {
        try {
            int _type = DecimalLiteral;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:581:2: ( ( '0' | '1' .. '9' ( '0' .. '9' )* ) ( IntegerTypeSuffix )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:581:4: ( '0' | '1' .. '9' ( '0' .. '9' )* ) ( IntegerTypeSuffix )?
            {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:581:4: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0=='0') ) {
                alt26=1;
            }
            else if ( ((LA26_0>='1' && LA26_0<='9')) ) {
                alt26=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("581:4: ( '0' | '1' .. '9' ( '0' .. '9' )* )", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:581:5: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:581:11: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:581:20: ( '0' .. '9' )*
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( ((LA25_0>='0' && LA25_0<='9')) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:581:20: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop25;
                        }
                    } while (true);


                    }
                    break;

            }

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:581:31: ( IntegerTypeSuffix )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0=='L'||LA27_0=='l') ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:581:31: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DecimalLiteral

    // $ANTLR start OctalLiteral
    public final void mOctalLiteral() throws RecognitionException {
        try {
            int _type = OctalLiteral;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:585:2: ( '0' ( '0' .. '7' )+ ( IntegerTypeSuffix )? )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:585:4: '0' ( '0' .. '7' )+ ( IntegerTypeSuffix )?
            {
            match('0'); 
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:585:8: ( '0' .. '7' )+
            int cnt28=0;
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( ((LA28_0>='0' && LA28_0<='7')) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:585:9: '0' .. '7'
            	    {
            	    matchRange('0','7'); 

            	    }
            	    break;

            	default :
            	    if ( cnt28 >= 1 ) break loop28;
                        EarlyExitException eee =
                            new EarlyExitException(28, input);
                        throw eee;
                }
                cnt28++;
            } while (true);

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:585:20: ( IntegerTypeSuffix )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0=='L'||LA29_0=='l') ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:585:20: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OctalLiteral

    // $ANTLR start HexDigit
    public final void mHexDigit() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:589:9: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:589:11: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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

    // $ANTLR start IntegerTypeSuffix
    public final void mIntegerTypeSuffix() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:594:2: ( ( 'l' | 'L' ) )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:594:4: ( 'l' | 'L' )
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
    // $ANTLR end IntegerTypeSuffix

    // $ANTLR start FloatingPointLiteral
    public final void mFloatingPointLiteral() throws RecognitionException {
        try {
            int _type = FloatingPointLiteral;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:598:6: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )? | '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )? | ( '0' .. '9' )+ Exponent | ( '0' .. '9' )+ FloatTypeSuffix | ( '0' .. '9' )+ Exponent FloatTypeSuffix )
            int alt40=5;
            alt40 = dfa40.predict(input);
            switch (alt40) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:598:10: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )?
                    {
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:598:10: ( '0' .. '9' )+
                    int cnt30=0;
                    loop30:
                    do {
                        int alt30=2;
                        int LA30_0 = input.LA(1);

                        if ( ((LA30_0>='0' && LA30_0<='9')) ) {
                            alt30=1;
                        }


                        switch (alt30) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:598:11: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt30 >= 1 ) break loop30;
                                EarlyExitException eee =
                                    new EarlyExitException(30, input);
                                throw eee;
                        }
                        cnt30++;
                    } while (true);

                    match('.'); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:598:26: ( '0' .. '9' )*
                    loop31:
                    do {
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( ((LA31_0>='0' && LA31_0<='9')) ) {
                            alt31=1;
                        }


                        switch (alt31) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:598:27: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop31;
                        }
                    } while (true);

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:598:38: ( Exponent )?
                    int alt32=2;
                    int LA32_0 = input.LA(1);

                    if ( (LA32_0=='E'||LA32_0=='e') ) {
                        alt32=1;
                    }
                    switch (alt32) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:598:38: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:598:48: ( FloatTypeSuffix )?
                    int alt33=2;
                    int LA33_0 = input.LA(1);

                    if ( (LA33_0=='D'||LA33_0=='F'||LA33_0=='d'||LA33_0=='f') ) {
                        alt33=1;
                    }
                    switch (alt33) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:598:48: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:599:7: '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )?
                    {
                    match('.'); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:599:11: ( '0' .. '9' )+
                    int cnt34=0;
                    loop34:
                    do {
                        int alt34=2;
                        int LA34_0 = input.LA(1);

                        if ( ((LA34_0>='0' && LA34_0<='9')) ) {
                            alt34=1;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:599:12: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt34 >= 1 ) break loop34;
                                EarlyExitException eee =
                                    new EarlyExitException(34, input);
                                throw eee;
                        }
                        cnt34++;
                    } while (true);

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:599:23: ( Exponent )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);

                    if ( (LA35_0=='E'||LA35_0=='e') ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:599:23: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:599:33: ( FloatTypeSuffix )?
                    int alt36=2;
                    int LA36_0 = input.LA(1);

                    if ( (LA36_0=='D'||LA36_0=='F'||LA36_0=='d'||LA36_0=='f') ) {
                        alt36=1;
                    }
                    switch (alt36) {
                        case 1 :
                            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:599:33: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:600:7: ( '0' .. '9' )+ Exponent
                    {
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:600:7: ( '0' .. '9' )+
                    int cnt37=0;
                    loop37:
                    do {
                        int alt37=2;
                        int LA37_0 = input.LA(1);

                        if ( ((LA37_0>='0' && LA37_0<='9')) ) {
                            alt37=1;
                        }


                        switch (alt37) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:600:8: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt37 >= 1 ) break loop37;
                                EarlyExitException eee =
                                    new EarlyExitException(37, input);
                                throw eee;
                        }
                        cnt37++;
                    } while (true);

                    mExponent(); 

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:601:6: ( '0' .. '9' )+ FloatTypeSuffix
                    {
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:601:6: ( '0' .. '9' )+
                    int cnt38=0;
                    loop38:
                    do {
                        int alt38=2;
                        int LA38_0 = input.LA(1);

                        if ( ((LA38_0>='0' && LA38_0<='9')) ) {
                            alt38=1;
                        }


                        switch (alt38) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:601:7: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt38 >= 1 ) break loop38;
                                EarlyExitException eee =
                                    new EarlyExitException(38, input);
                                throw eee;
                        }
                        cnt38++;
                    } while (true);

                    mFloatTypeSuffix(); 

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:602:6: ( '0' .. '9' )+ Exponent FloatTypeSuffix
                    {
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:602:6: ( '0' .. '9' )+
                    int cnt39=0;
                    loop39:
                    do {
                        int alt39=2;
                        int LA39_0 = input.LA(1);

                        if ( ((LA39_0>='0' && LA39_0<='9')) ) {
                            alt39=1;
                        }


                        switch (alt39) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:602:7: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt39 >= 1 ) break loop39;
                                EarlyExitException eee =
                                    new EarlyExitException(39, input);
                                throw eee;
                        }
                        cnt39++;
                    } while (true);

                    mExponent(); 
                    mFloatTypeSuffix(); 

                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FloatingPointLiteral

    // $ANTLR start Exponent
    public final void mExponent() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:606:9: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:606:11: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:606:21: ( '+' | '-' )?
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0=='+'||LA41_0=='-') ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
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

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:606:32: ( '0' .. '9' )+
            int cnt42=0;
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( ((LA42_0>='0' && LA42_0<='9')) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:606:33: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt42 >= 1 ) break loop42;
                        EarlyExitException eee =
                            new EarlyExitException(42, input);
                        throw eee;
                }
                cnt42++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end Exponent

    // $ANTLR start FloatTypeSuffix
    public final void mFloatTypeSuffix() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:611:2: ( ( 'f' | 'F' | 'd' | 'D' ) )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:611:4: ( 'f' | 'F' | 'd' | 'D' )
            {
            if ( input.LA(1)=='D'||input.LA(1)=='F'||input.LA(1)=='d'||input.LA(1)=='f' ) {
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
    // $ANTLR end FloatTypeSuffix

    // $ANTLR start CharacterLiteral
    public final void mCharacterLiteral() throws RecognitionException {
        try {
            int _type = CharacterLiteral;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:615:2: ( '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' ) ) '\\'' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:615:6: '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' ) ) '\\''
            {
            match('\''); 
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:615:11: ( EscapeSequence | ~ ( '\\'' | '\\\\' ) )
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0=='\\') ) {
                alt43=1;
            }
            else if ( ((LA43_0>='\u0000' && LA43_0<='&')||(LA43_0>='(' && LA43_0<='[')||(LA43_0>=']' && LA43_0<='\uFFFE')) ) {
                alt43=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("615:11: ( EscapeSequence | ~ ( '\\'' | '\\\\' ) )", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:615:13: EscapeSequence
                    {
                    mEscapeSequence(); 

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:615:30: ~ ( '\\'' | '\\\\' )
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
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
    // $ANTLR end CharacterLiteral

    // $ANTLR start StringLiteral
    public final void mStringLiteral() throws RecognitionException {
        try {
            int _type = StringLiteral;
            Token text=null;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:619:2: ( '\"' (text= EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:619:5: '\"' (text= EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:619:9: (text= EscapeSequence | ~ ( '\\\\' | '\"' ) )*
            loop44:
            do {
                int alt44=3;
                int LA44_0 = input.LA(1);

                if ( (LA44_0=='\\') ) {
                    alt44=1;
                }
                else if ( ((LA44_0>='\u0000' && LA44_0<='!')||(LA44_0>='#' && LA44_0<='[')||(LA44_0>=']' && LA44_0<='\uFFFE')) ) {
                    alt44=2;
                }


                switch (alt44) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:619:11: text= EscapeSequence
            	    {
            	    int textStart3043 = getCharIndex();
            	    mEscapeSequence(); 
            	    text = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, textStart3043, getCharIndex()-1);

            	    }
            	    break;
            	case 2 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:619:33: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
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
            	    break loop44;
                }
            } while (true);

            match('\"'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end StringLiteral

    // $ANTLR start EscapeSequence
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:624:2: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape )
            int alt45=3;
            int LA45_0 = input.LA(1);

            if ( (LA45_0=='\\') ) {
                switch ( input.LA(2) ) {
                case 'u':
                    {
                    alt45=2;
                    }
                    break;
                case '\"':
                case '\'':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    {
                    alt45=1;
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
                    alt45=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("622:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape );", 45, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("622:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape );", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:624:6: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
                    {
                    match('\\'); 
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:625:6: UnicodeEscape
                    {
                    mUnicodeEscape(); 

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:626:6: OctalEscape
                    {
                    mOctalEscape(); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end EscapeSequence

    // $ANTLR start OctalEscape
    public final void mOctalEscape() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:631:2: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt46=3;
            int LA46_0 = input.LA(1);

            if ( (LA46_0=='\\') ) {
                int LA46_1 = input.LA(2);

                if ( ((LA46_1>='0' && LA46_1<='3')) ) {
                    int LA46_2 = input.LA(3);

                    if ( ((LA46_2>='0' && LA46_2<='7')) ) {
                        int LA46_4 = input.LA(4);

                        if ( ((LA46_4>='0' && LA46_4<='7')) ) {
                            alt46=1;
                        }
                        else {
                            alt46=2;}
                    }
                    else {
                        alt46=3;}
                }
                else if ( ((LA46_1>='4' && LA46_1<='7')) ) {
                    int LA46_3 = input.LA(3);

                    if ( ((LA46_3>='0' && LA46_3<='7')) ) {
                        alt46=2;
                    }
                    else {
                        alt46=3;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("629:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 46, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("629:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:631:6: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:631:11: ( '0' .. '3' )
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:631:12: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:631:22: ( '0' .. '7' )
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:631:23: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:631:33: ( '0' .. '7' )
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:631:34: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:632:6: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:632:11: ( '0' .. '7' )
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:632:12: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:632:22: ( '0' .. '7' )
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:632:23: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:633:6: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:633:11: ( '0' .. '7' )
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:633:12: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end OctalEscape

    // $ANTLR start UnicodeEscape
    public final void mUnicodeEscape() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:638:2: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:638:6: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
            {
            match('\\'); 
            match('u'); 
            mHexDigit(); 
            mHexDigit(); 
            mHexDigit(); 
            mHexDigit(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end UnicodeEscape

    // $ANTLR start IDENTIFIER
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:642:2: ( Letter ( Letter | JavaIDDigit )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:642:6: Letter ( Letter | JavaIDDigit )*
            {
            mLetter(); 
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:642:13: ( Letter | JavaIDDigit )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0=='$'||(LA47_0>='0' && LA47_0<='9')||(LA47_0>='A' && LA47_0<='Z')||LA47_0=='_'||(LA47_0>='a' && LA47_0<='z')||(LA47_0>='\u00C0' && LA47_0<='\u00D6')||(LA47_0>='\u00D8' && LA47_0<='\u00F6')||(LA47_0>='\u00F8' && LA47_0<='\u1FFF')||(LA47_0>='\u3040' && LA47_0<='\u318F')||(LA47_0>='\u3300' && LA47_0<='\u337F')||(LA47_0>='\u3400' && LA47_0<='\u3D2D')||(LA47_0>='\u4E00' && LA47_0<='\u9FFF')||(LA47_0>='\uF900' && LA47_0<='\uFAFF')) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
            	    {
            	    if ( input.LA(1)=='$'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u3040' && input.LA(1)<='\u318F')||(input.LA(1)>='\u3300' && input.LA(1)<='\u337F')||(input.LA(1)>='\u3400' && input.LA(1)<='\u3D2D')||(input.LA(1)>='\u4E00' && input.LA(1)<='\u9FFF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFAFF') ) {
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
            	    break loop47;
                }
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IDENTIFIER

    // $ANTLR start Letter
    public final void mLetter() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:647:2: ( '\\u0024' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u00ff' | '\\u0100' .. '\\u1fff' | '\\u3040' .. '\\u318f' | '\\u3300' .. '\\u337f' | '\\u3400' .. '\\u3d2d' | '\\u4e00' .. '\\u9fff' | '\\uf900' .. '\\ufaff' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
            {
            if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u3040' && input.LA(1)<='\u318F')||(input.LA(1)>='\u3300' && input.LA(1)<='\u337F')||(input.LA(1)>='\u3400' && input.LA(1)<='\u3D2D')||(input.LA(1)>='\u4E00' && input.LA(1)<='\u9FFF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFAFF') ) {
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
    // $ANTLR end Letter

    // $ANTLR start JavaIDDigit
    public final void mJavaIDDigit() throws RecognitionException {
        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:664:2: ( '\\u0030' .. '\\u0039' | '\\u0660' .. '\\u0669' | '\\u06f0' .. '\\u06f9' | '\\u0966' .. '\\u096f' | '\\u09e6' .. '\\u09ef' | '\\u0a66' .. '\\u0a6f' | '\\u0ae6' .. '\\u0aef' | '\\u0b66' .. '\\u0b6f' | '\\u0be7' .. '\\u0bef' | '\\u0c66' .. '\\u0c6f' | '\\u0ce6' .. '\\u0cef' | '\\u0d66' .. '\\u0d6f' | '\\u0e50' .. '\\u0e59' | '\\u0ed0' .. '\\u0ed9' | '\\u1040' .. '\\u1049' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='\u0660' && input.LA(1)<='\u0669')||(input.LA(1)>='\u06F0' && input.LA(1)<='\u06F9')||(input.LA(1)>='\u0966' && input.LA(1)<='\u096F')||(input.LA(1)>='\u09E6' && input.LA(1)<='\u09EF')||(input.LA(1)>='\u0A66' && input.LA(1)<='\u0A6F')||(input.LA(1)>='\u0AE6' && input.LA(1)<='\u0AEF')||(input.LA(1)>='\u0B66' && input.LA(1)<='\u0B6F')||(input.LA(1)>='\u0BE7' && input.LA(1)<='\u0BEF')||(input.LA(1)>='\u0C66' && input.LA(1)<='\u0C6F')||(input.LA(1)>='\u0CE6' && input.LA(1)<='\u0CEF')||(input.LA(1)>='\u0D66' && input.LA(1)<='\u0D6F')||(input.LA(1)>='\u0E50' && input.LA(1)<='\u0E59')||(input.LA(1)>='\u0ED0' && input.LA(1)<='\u0ED9')||(input.LA(1)>='\u1040' && input.LA(1)<='\u1049') ) {
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
    // $ANTLR end JavaIDDigit

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:681:5: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:681:8: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            channel=HIDDEN;

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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:685:2: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:685:6: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:685:11: ( options {greedy=false; } : . )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0=='*') ) {
                    int LA48_1 = input.LA(2);

                    if ( (LA48_1=='/') ) {
                        alt48=2;
                    }
                    else if ( ((LA48_1>='\u0000' && LA48_1<='.')||(LA48_1>='0' && LA48_1<='\uFFFE')) ) {
                        alt48=1;
                    }


                }
                else if ( ((LA48_0>='\u0000' && LA48_0<=')')||(LA48_0>='+' && LA48_0<='\uFFFE')) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:685:39: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop48;
                }
            } while (true);

            match("*/"); 

            channel=HIDDEN;

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
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:689:2: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:689:4: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match("//"); 

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:689:9: (~ ( '\\n' | '\\r' ) )*
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( ((LA49_0>='\u0000' && LA49_0<='\t')||(LA49_0>='\u000B' && LA49_0<='\f')||(LA49_0>='\u000E' && LA49_0<='\uFFFE')) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:689:9: ~ ( '\\n' | '\\r' )
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
            	    break loop49;
                }
            } while (true);

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:689:23: ( '\\r' )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0=='\r') ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:689:23: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 
            channel=HIDDEN;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LINE_COMMENT

    public void mTokens() throws RecognitionException {
        // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:8: ( LONGLITERAL | INTLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | ABSTRACT | ASSERT | BOOLEAN | BREAK | BYTE | CASE | CATCH | CHAR | CLASS | CONST | CONTINUE | DEFAULT | DO | DOUBLE | ELSE | ENUM | EXTENDS | FINAL | FINALLY | FLOAT | FOR | GOTO | IF | IMPLEMENTS | IMPORT | INSTANCEOF | INT | INTERFACE | LONG | NATIVE | NEW | PACKAGE | PRIVATE | PROTECTED | PUBLIC | RETURN | SHORT | STATIC | STRICTFP | SUPER | SWITCH | SYNCHRONIZED | THIS | THROW | THROWS | TRANSIENT | TRY | VOID | VOLATILE | WHILE | TRUE | FALSE | NULL | LPAREN | RPAREN | LBRACE | RBRACE | LBRACKET | RBRACKET | SEMI | COMMA | DOT | ELLIPSIS | EQ | BANG | TILDE | QUES | COLON | EQEQ | AMPAMP | BARBAR | PLUSPLUS | SUBSUB | PLUS | SUB | STAR | SLASH | AMP | BAR | CARET | PERCENT | PLUSEQ | SUBEQ | STAREQ | SLASHEQ | AMPEQ | BAREQ | CARETEQ | PERCENTEQ | MONKEYS_AT | BANGEQ | GT | LT | BooleanLiteral | HexLiteral | DecimalLiteral | OctalLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | IDENTIFIER | WS | COMMENT | LINE_COMMENT )
        int alt51=110;
        alt51 = dfa51.predict(input);
        switch (alt51) {
            case 1 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:10: LONGLITERAL
                {
                mLONGLITERAL(); 

                }
                break;
            case 2 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:22: INTLITERAL
                {
                mINTLITERAL(); 

                }
                break;
            case 3 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:33: FLOATLITERAL
                {
                mFLOATLITERAL(); 

                }
                break;
            case 4 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:46: DOUBLELITERAL
                {
                mDOUBLELITERAL(); 

                }
                break;
            case 5 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:60: CHARLITERAL
                {
                mCHARLITERAL(); 

                }
                break;
            case 6 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:72: STRINGLITERAL
                {
                mSTRINGLITERAL(); 

                }
                break;
            case 7 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:86: ABSTRACT
                {
                mABSTRACT(); 

                }
                break;
            case 8 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:95: ASSERT
                {
                mASSERT(); 

                }
                break;
            case 9 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:102: BOOLEAN
                {
                mBOOLEAN(); 

                }
                break;
            case 10 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:110: BREAK
                {
                mBREAK(); 

                }
                break;
            case 11 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:116: BYTE
                {
                mBYTE(); 

                }
                break;
            case 12 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:121: CASE
                {
                mCASE(); 

                }
                break;
            case 13 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:126: CATCH
                {
                mCATCH(); 

                }
                break;
            case 14 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:132: CHAR
                {
                mCHAR(); 

                }
                break;
            case 15 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:137: CLASS
                {
                mCLASS(); 

                }
                break;
            case 16 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:143: CONST
                {
                mCONST(); 

                }
                break;
            case 17 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:149: CONTINUE
                {
                mCONTINUE(); 

                }
                break;
            case 18 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:158: DEFAULT
                {
                mDEFAULT(); 

                }
                break;
            case 19 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:166: DO
                {
                mDO(); 

                }
                break;
            case 20 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:169: DOUBLE
                {
                mDOUBLE(); 

                }
                break;
            case 21 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:176: ELSE
                {
                mELSE(); 

                }
                break;
            case 22 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:181: ENUM
                {
                mENUM(); 

                }
                break;
            case 23 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:186: EXTENDS
                {
                mEXTENDS(); 

                }
                break;
            case 24 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:194: FINAL
                {
                mFINAL(); 

                }
                break;
            case 25 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:200: FINALLY
                {
                mFINALLY(); 

                }
                break;
            case 26 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:208: FLOAT
                {
                mFLOAT(); 

                }
                break;
            case 27 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:214: FOR
                {
                mFOR(); 

                }
                break;
            case 28 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:218: GOTO
                {
                mGOTO(); 

                }
                break;
            case 29 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:223: IF
                {
                mIF(); 

                }
                break;
            case 30 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:226: IMPLEMENTS
                {
                mIMPLEMENTS(); 

                }
                break;
            case 31 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:237: IMPORT
                {
                mIMPORT(); 

                }
                break;
            case 32 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:244: INSTANCEOF
                {
                mINSTANCEOF(); 

                }
                break;
            case 33 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:255: INT
                {
                mINT(); 

                }
                break;
            case 34 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:259: INTERFACE
                {
                mINTERFACE(); 

                }
                break;
            case 35 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:269: LONG
                {
                mLONG(); 

                }
                break;
            case 36 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:274: NATIVE
                {
                mNATIVE(); 

                }
                break;
            case 37 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:281: NEW
                {
                mNEW(); 

                }
                break;
            case 38 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:285: PACKAGE
                {
                mPACKAGE(); 

                }
                break;
            case 39 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:293: PRIVATE
                {
                mPRIVATE(); 

                }
                break;
            case 40 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:301: PROTECTED
                {
                mPROTECTED(); 

                }
                break;
            case 41 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:311: PUBLIC
                {
                mPUBLIC(); 

                }
                break;
            case 42 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:318: RETURN
                {
                mRETURN(); 

                }
                break;
            case 43 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:325: SHORT
                {
                mSHORT(); 

                }
                break;
            case 44 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:331: STATIC
                {
                mSTATIC(); 

                }
                break;
            case 45 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:338: STRICTFP
                {
                mSTRICTFP(); 

                }
                break;
            case 46 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:347: SUPER
                {
                mSUPER(); 

                }
                break;
            case 47 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:353: SWITCH
                {
                mSWITCH(); 

                }
                break;
            case 48 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:360: SYNCHRONIZED
                {
                mSYNCHRONIZED(); 

                }
                break;
            case 49 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:373: THIS
                {
                mTHIS(); 

                }
                break;
            case 50 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:378: THROW
                {
                mTHROW(); 

                }
                break;
            case 51 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:384: THROWS
                {
                mTHROWS(); 

                }
                break;
            case 52 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:391: TRANSIENT
                {
                mTRANSIENT(); 

                }
                break;
            case 53 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:401: TRY
                {
                mTRY(); 

                }
                break;
            case 54 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:405: VOID
                {
                mVOID(); 

                }
                break;
            case 55 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:410: VOLATILE
                {
                mVOLATILE(); 

                }
                break;
            case 56 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:419: WHILE
                {
                mWHILE(); 

                }
                break;
            case 57 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:425: TRUE
                {
                mTRUE(); 

                }
                break;
            case 58 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:430: FALSE
                {
                mFALSE(); 

                }
                break;
            case 59 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:436: NULL
                {
                mNULL(); 

                }
                break;
            case 60 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:441: LPAREN
                {
                mLPAREN(); 

                }
                break;
            case 61 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:448: RPAREN
                {
                mRPAREN(); 

                }
                break;
            case 62 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:455: LBRACE
                {
                mLBRACE(); 

                }
                break;
            case 63 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:462: RBRACE
                {
                mRBRACE(); 

                }
                break;
            case 64 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:469: LBRACKET
                {
                mLBRACKET(); 

                }
                break;
            case 65 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:478: RBRACKET
                {
                mRBRACKET(); 

                }
                break;
            case 66 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:487: SEMI
                {
                mSEMI(); 

                }
                break;
            case 67 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:492: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 68 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:498: DOT
                {
                mDOT(); 

                }
                break;
            case 69 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:502: ELLIPSIS
                {
                mELLIPSIS(); 

                }
                break;
            case 70 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:511: EQ
                {
                mEQ(); 

                }
                break;
            case 71 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:514: BANG
                {
                mBANG(); 

                }
                break;
            case 72 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:519: TILDE
                {
                mTILDE(); 

                }
                break;
            case 73 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:525: QUES
                {
                mQUES(); 

                }
                break;
            case 74 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:530: COLON
                {
                mCOLON(); 

                }
                break;
            case 75 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:536: EQEQ
                {
                mEQEQ(); 

                }
                break;
            case 76 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:541: AMPAMP
                {
                mAMPAMP(); 

                }
                break;
            case 77 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:548: BARBAR
                {
                mBARBAR(); 

                }
                break;
            case 78 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:555: PLUSPLUS
                {
                mPLUSPLUS(); 

                }
                break;
            case 79 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:564: SUBSUB
                {
                mSUBSUB(); 

                }
                break;
            case 80 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:571: PLUS
                {
                mPLUS(); 

                }
                break;
            case 81 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:576: SUB
                {
                mSUB(); 

                }
                break;
            case 82 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:580: STAR
                {
                mSTAR(); 

                }
                break;
            case 83 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:585: SLASH
                {
                mSLASH(); 

                }
                break;
            case 84 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:591: AMP
                {
                mAMP(); 

                }
                break;
            case 85 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:595: BAR
                {
                mBAR(); 

                }
                break;
            case 86 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:599: CARET
                {
                mCARET(); 

                }
                break;
            case 87 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:605: PERCENT
                {
                mPERCENT(); 

                }
                break;
            case 88 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:613: PLUSEQ
                {
                mPLUSEQ(); 

                }
                break;
            case 89 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:620: SUBEQ
                {
                mSUBEQ(); 

                }
                break;
            case 90 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:626: STAREQ
                {
                mSTAREQ(); 

                }
                break;
            case 91 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:633: SLASHEQ
                {
                mSLASHEQ(); 

                }
                break;
            case 92 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:641: AMPEQ
                {
                mAMPEQ(); 

                }
                break;
            case 93 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:647: BAREQ
                {
                mBAREQ(); 

                }
                break;
            case 94 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:653: CARETEQ
                {
                mCARETEQ(); 

                }
                break;
            case 95 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:661: PERCENTEQ
                {
                mPERCENTEQ(); 

                }
                break;
            case 96 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:671: MONKEYS_AT
                {
                mMONKEYS_AT(); 

                }
                break;
            case 97 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:682: BANGEQ
                {
                mBANGEQ(); 

                }
                break;
            case 98 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:689: GT
                {
                mGT(); 

                }
                break;
            case 99 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:692: LT
                {
                mLT(); 

                }
                break;
            case 100 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:695: BooleanLiteral
                {
                mBooleanLiteral(); 

                }
                break;
            case 101 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:710: HexLiteral
                {
                mHexLiteral(); 

                }
                break;
            case 102 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:721: DecimalLiteral
                {
                mDecimalLiteral(); 

                }
                break;
            case 103 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:736: OctalLiteral
                {
                mOctalLiteral(); 

                }
                break;
            case 104 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:749: FloatingPointLiteral
                {
                mFloatingPointLiteral(); 

                }
                break;
            case 105 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:770: CharacterLiteral
                {
                mCharacterLiteral(); 

                }
                break;
            case 106 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:787: StringLiteral
                {
                mStringLiteral(); 

                }
                break;
            case 107 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:801: IDENTIFIER
                {
                mIDENTIFIER(); 

                }
                break;
            case 108 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:812: WS
                {
                mWS(); 

                }
                break;
            case 109 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:815: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 110 :
                // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:823: LINE_COMMENT
                {
                mLINE_COMMENT(); 

                }
                break;

        }

    }


    protected DFA18 dfa18 = new DFA18(this);
    protected DFA40 dfa40 = new DFA40(this);
    protected DFA51 dfa51 = new DFA51(this);
    static final String DFA18_eotS =
        "\1\uffff\1\5\1\uffff\1\5\4\uffff";
    static final String DFA18_eofS =
        "\10\uffff";
    static final String DFA18_minS =
        "\2\56\1\uffff\1\56\4\uffff";
    static final String DFA18_maxS =
        "\1\71\1\170\1\uffff\1\145\4\uffff";
    static final String DFA18_acceptS =
        "\2\uffff\1\2\1\uffff\1\5\1\4\1\3\1\1";
    static final String DFA18_specialS =
        "\10\uffff}>";
    static final String[] DFA18_transitionS = {
            "\1\2\1\uffff\1\1\11\3",
            "\1\7\1\uffff\12\3\13\uffff\1\6\22\uffff\1\4\14\uffff\1\6\22"+
            "\uffff\1\4",
            "",
            "\1\7\1\uffff\12\3\13\uffff\1\6\37\uffff\1\6",
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
            return "150:1: fragment NonIntegerNumber : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? | '.' ( '0' .. '9' )+ ( Exponent )? | ( '0' .. '9' )+ Exponent | ( '0' .. '9' )+ | HexPrefix ( HexDigit )* ( () | ( '.' ( HexDigit )* ) ) ( 'p' | 'P' ) ( '+' | '-' )? ( '0' .. '9' )+ );";
        }
    }
    static final String DFA40_eotS =
        "\7\uffff\1\10\2\uffff";
    static final String DFA40_eofS =
        "\12\uffff";
    static final String DFA40_minS =
        "\2\56\2\uffff\1\53\1\uffff\2\60\2\uffff";
    static final String DFA40_maxS =
        "\1\71\1\146\2\uffff\1\71\1\uffff\1\71\1\146\2\uffff";
    static final String DFA40_acceptS =
        "\2\uffff\1\2\1\4\1\uffff\1\1\2\uffff\1\3\1\5";
    static final String DFA40_specialS =
        "\12\uffff}>";
    static final String[] DFA40_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\5\1\uffff\12\1\12\uffff\1\3\1\4\1\3\35\uffff\1\3\1\4\1\3",
            "",
            "",
            "\1\6\1\uffff\1\6\2\uffff\12\7",
            "",
            "\12\7",
            "\12\7\12\uffff\1\11\1\uffff\1\11\35\uffff\1\11\1\uffff\1\11",
            "",
            ""
    };

    static final short[] DFA40_eot = DFA.unpackEncodedString(DFA40_eotS);
    static final short[] DFA40_eof = DFA.unpackEncodedString(DFA40_eofS);
    static final char[] DFA40_min = DFA.unpackEncodedStringToUnsignedChars(DFA40_minS);
    static final char[] DFA40_max = DFA.unpackEncodedStringToUnsignedChars(DFA40_maxS);
    static final short[] DFA40_accept = DFA.unpackEncodedString(DFA40_acceptS);
    static final short[] DFA40_special = DFA.unpackEncodedString(DFA40_specialS);
    static final short[][] DFA40_transition;

    static {
        int numStates = DFA40_transitionS.length;
        DFA40_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA40_transition[i] = DFA.unpackEncodedString(DFA40_transitionS[i]);
        }
    }

    class DFA40 extends DFA {

        public DFA40(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 40;
            this.eot = DFA40_eot;
            this.eof = DFA40_eof;
            this.min = DFA40_min;
            this.max = DFA40_max;
            this.accept = DFA40_accept;
            this.special = DFA40_special;
            this.transition = DFA40_transition;
        }
        public String getDescription() {
            return "597:1: FloatingPointLiteral : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )? | '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )? | ( '0' .. '9' )+ Exponent | ( '0' .. '9' )+ FloatTypeSuffix | ( '0' .. '9' )+ Exponent FloatTypeSuffix );";
        }
    }
    static final String DFA51_eotS =
        "\1\uffff\2\66\1\75\2\uffff\20\56\10\uffff\1\155\1\157\3\uffff\1"+
        "\162\1\165\1\170\1\173\1\175\1\u0081\1\u0083\1\u0085\10\uffff\1"+
        "\66\2\u008f\4\uffff\1\66\1\uffff\1\u008f\10\uffff\11\56\1\u00ab"+
        "\13\56\1\u00b8\21\56\32\uffff\1\66\3\uffff\1\u008f\1\uffff\1\u008f"+
        "\23\uffff\13\56\1\uffff\7\56\1\u00f4\2\56\1\u00f8\1\56\1\uffff\2"+
        "\56\1\u00fd\16\56\1\u010c\5\56\2\uffff\1\u008f\4\uffff\1\u008f\1"+
        "\uffff\1\u008f\7\uffff\2\56\1\u0118\2\56\1\u011b\1\56\1\u011d\6"+
        "\56\1\u0124\1\u0125\3\56\1\uffff\1\u0129\2\56\1\uffff\2\56\1\u012e"+
        "\1\u012f\1\uffff\14\56\1\u013c\1\56\1\uffff\1\56\1\u013f\1\56\1"+
        "\u0141\1\56\4\uffff\2\56\1\uffff\1\56\1\u0148\1\uffff\1\u0149\1"+
        "\uffff\1\56\1\u014b\1\u014c\3\56\2\uffff\1\u0150\1\u0151\1\u0153"+
        "\1\uffff\4\56\2\uffff\6\56\1\u015e\2\56\1\u0161\2\56\1\uffff\1\56"+
        "\1\u0166\1\uffff\1\56\1\uffff\1\u0168\2\uffff\1\u016b\2\56\2\uffff"+
        "\1\56\2\uffff\1\u016f\2\56\2\uffff\1\56\1\uffff\3\56\1\u0176\1\u0177"+
        "\2\56\1\u017a\1\56\1\u017c\1\uffff\1\u017d\1\56\1\uffff\1\u017f"+
        "\2\56\1\u0182\1\uffff\1\56\4\uffff\1\56\1\u0185\1\56\1\uffff\1\u0187"+
        "\1\u0188\1\u0189\3\56\2\uffff\1\56\1\u018e\1\uffff\1\u018f\2\uffff"+
        "\1\56\1\uffff\2\56\1\uffff\1\56\1\u0194\1\uffff\1\u0195\3\uffff"+
        "\4\56\2\uffff\1\u019a\2\56\1\u019d\2\uffff\1\56\1\u019f\1\56\1\u01a1"+
        "\1\uffff\1\56\1\u01a3\1\uffff\1\u01a4\1\uffff\1\u01a5\1\uffff\1"+
        "\56\3\uffff\1\56\1\u01a8\1\uffff";
    static final String DFA51_eofS =
        "\u01a9\uffff";
    static final String DFA51_minS =
        "\1\11\3\56\2\0\1\142\1\157\1\141\1\145\1\154\1\141\1\157\1\146\1"+
        "\157\2\141\1\145\2\150\1\157\1\150\10\uffff\2\75\3\uffff\1\46\1"+
        "\75\1\53\1\55\1\75\1\52\2\75\5\uffff\2\56\1\53\1\56\1\60\1\56\4"+
        "\uffff\1\56\1\uffff\1\60\1\uffff\1\42\1\47\1\uffff\1\42\1\0\2\uffff"+
        "\2\163\1\164\1\157\1\145\1\163\1\141\1\156\1\141\1\44\1\146\1\164"+
        "\1\165\1\163\1\154\1\157\1\156\1\162\1\164\1\163\1\160\1\44\1\156"+
        "\1\154\1\167\1\164\1\151\1\142\1\143\1\164\1\157\1\141\1\160\1\151"+
        "\1\156\1\141\3\151\32\uffff\1\56\1\53\3\60\1\uffff\1\60\1\53\5\uffff"+
        "\1\53\2\uffff\1\60\3\47\1\uffff\1\60\3\0\1\uffff\1\145\1\164\1\145"+
        "\1\154\1\141\1\145\1\143\1\162\2\163\1\142\1\uffff\1\141\1\145\1"+
        "\155\1\145\1\163\2\141\1\44\1\157\1\164\1\44\1\154\1\uffff\1\147"+
        "\1\154\1\44\1\151\1\164\1\166\1\154\1\153\1\165\1\162\1\164\1\151"+
        "\1\145\1\164\1\143\1\145\1\156\1\44\1\157\1\163\1\141\1\144\1\154"+
        "\1\uffff\3\60\2\uffff\5\60\2\47\1\uffff\1\60\2\0\2\162\1\44\1\145"+
        "\1\153\1\44\1\150\1\44\1\151\1\164\1\163\1\154\1\165\1\156\2\44"+
        "\1\145\1\164\1\154\1\uffff\1\44\1\141\1\162\1\uffff\1\145\1\162"+
        "\2\44\1\uffff\1\166\1\145\1\141\1\151\1\141\1\162\1\164\1\151\1"+
        "\143\1\162\1\143\1\150\1\44\1\163\1\uffff\1\167\1\44\1\164\1\44"+
        "\1\145\1\60\1\47\1\60\1\0\1\164\1\141\1\uffff\1\141\1\44\1\uffff"+
        "\1\44\1\uffff\1\156\2\44\1\145\1\154\1\144\2\uffff\3\44\1\uffff"+
        "\1\156\1\146\1\155\1\164\2\uffff\1\145\1\143\1\164\1\143\1\147\1"+
        "\156\1\44\1\143\1\164\1\44\1\150\1\162\1\uffff\1\151\1\44\1\uffff"+
        "\1\151\1\uffff\1\44\2\60\1\44\1\143\1\156\2\uffff\1\165\2\uffff"+
        "\1\44\1\164\1\163\2\uffff\1\171\1\uffff\1\143\1\141\1\145\2\44\1"+
        "\164\1\145\1\44\1\145\1\44\1\uffff\1\44\1\146\1\uffff\1\44\1\157"+
        "\1\145\1\44\1\uffff\1\154\1\uffff\1\47\1\0\1\uffff\1\164\1\44\1"+
        "\145\1\uffff\3\44\1\145\1\143\1\156\2\uffff\1\145\1\44\1\uffff\1"+
        "\44\2\uffff\1\160\1\uffff\2\156\1\uffff\1\145\1\44\1\uffff\1\44"+
        "\3\uffff\1\157\1\145\1\164\1\144\2\uffff\1\44\1\151\1\164\1\44\2"+
        "\uffff\1\146\1\44\1\163\1\44\1\uffff\1\172\1\44\1\uffff\1\44\1\uffff"+
        "\1\44\1\uffff\1\145\3\uffff\1\144\1\44\1\uffff";
    static final String DFA51_maxS =
        "\1\ufaff\1\170\1\154\1\71\2\ufffe\1\163\1\171\2\157\1\170\2\157"+
        "\1\156\1\157\2\165\1\145\1\171\1\162\1\157\1\150\10\uffff\2\75\3"+
        "\uffff\1\75\1\174\6\75\5\uffff\2\160\1\71\1\154\2\146\4\uffff\1"+
        "\154\1\uffff\1\146\1\uffff\1\165\1\47\1\uffff\1\165\1\ufffe\2\uffff"+
        "\2\163\1\164\1\157\1\145\1\164\1\141\1\156\1\141\1\ufaff\1\146\1"+
        "\164\1\165\1\163\1\154\1\157\1\156\1\162\2\164\1\160\1\ufaff\1\156"+
        "\1\154\1\167\1\164\1\157\1\142\1\143\1\164\1\157\1\162\1\160\1\151"+
        "\1\156\1\171\1\162\1\154\1\151\32\uffff\1\160\1\71\1\160\1\71\1"+
        "\146\1\uffff\1\146\1\71\5\uffff\1\71\2\uffff\1\146\1\47\2\67\1\uffff"+
        "\1\146\3\ufffe\1\uffff\1\145\1\164\1\145\1\154\1\141\1\145\1\143"+
        "\1\162\1\164\1\163\1\142\1\uffff\1\141\1\145\1\155\1\145\1\163\2"+
        "\141\1\ufaff\1\157\1\164\1\ufaff\1\157\1\uffff\1\147\1\154\1\ufaff"+
        "\1\151\1\164\1\166\1\154\1\153\1\165\1\162\1\164\1\151\1\145\1\164"+
        "\1\143\1\145\1\156\1\ufaff\1\157\1\163\1\141\1\144\1\154\1\uffff"+
        "\1\71\1\146\1\160\2\uffff\1\71\1\146\1\71\2\146\1\67\1\47\1\uffff"+
        "\1\146\2\ufffe\2\162\1\ufaff\1\145\1\153\1\ufaff\1\150\1\ufaff\1"+
        "\151\1\164\1\163\1\154\1\165\1\156\2\ufaff\1\145\1\164\1\154\1\uffff"+
        "\1\ufaff\1\141\1\162\1\uffff\1\145\1\162\2\ufaff\1\uffff\1\166\1"+
        "\145\1\141\1\151\1\141\1\162\1\164\1\151\1\143\1\162\1\143\1\150"+
        "\1\ufaff\1\163\1\uffff\1\167\1\ufaff\1\164\1\ufaff\1\145\1\146\1"+
        "\47\1\146\1\ufffe\1\164\1\141\1\uffff\1\141\1\ufaff\1\uffff\1\ufaff"+
        "\1\uffff\1\156\2\ufaff\1\145\1\154\1\144\2\uffff\3\ufaff\1\uffff"+
        "\1\156\1\146\1\155\1\164\2\uffff\1\145\1\143\1\164\1\143\1\147\1"+
        "\156\1\ufaff\1\143\1\164\1\ufaff\1\150\1\162\1\uffff\1\151\1\ufaff"+
        "\1\uffff\1\151\1\uffff\1\ufaff\2\146\1\ufaff\1\143\1\156\2\uffff"+
        "\1\165\2\uffff\1\ufaff\1\164\1\163\2\uffff\1\171\1\uffff\1\143\1"+
        "\141\1\145\2\ufaff\1\164\1\145\1\ufaff\1\145\1\ufaff\1\uffff\1\ufaff"+
        "\1\146\1\uffff\1\ufaff\1\157\1\145\1\ufaff\1\uffff\1\154\1\uffff"+
        "\1\47\1\ufffe\1\uffff\1\164\1\ufaff\1\145\1\uffff\3\ufaff\1\145"+
        "\1\143\1\156\2\uffff\1\145\1\ufaff\1\uffff\1\ufaff\2\uffff\1\160"+
        "\1\uffff\2\156\1\uffff\1\145\1\ufaff\1\uffff\1\ufaff\3\uffff\1\157"+
        "\1\145\1\164\1\144\2\uffff\1\ufaff\1\151\1\164\1\ufaff\2\uffff\1"+
        "\146\1\ufaff\1\163\1\ufaff\1\uffff\1\172\1\ufaff\1\uffff\1\ufaff"+
        "\1\uffff\1\ufaff\1\uffff\1\145\3\uffff\1\144\1\ufaff\1\uffff";
    static final String DFA51_acceptS =
        "\26\uffff\1\74\1\75\1\76\1\77\1\100\1\101\1\102\1\103\2\uffff\1"+
        "\110\1\111\1\112\10\uffff\1\140\1\142\1\143\1\153\1\154\6\uffff"+
        "\1\2\1\1\1\3\1\4\1\uffff\1\105\1\uffff\1\104\2\uffff\1\151\2\uffff"+
        "\1\6\1\152\47\uffff\1\113\1\106\1\141\1\107\1\134\1\114\1\124\1"+
        "\135\1\115\1\125\1\130\1\116\1\120\1\131\1\117\1\121\1\132\1\122"+
        "\1\133\1\155\1\156\1\123\1\136\1\126\1\137\1\127\5\uffff\1\1\2\uffff"+
        "\1\3\2\4\1\1\1\3\1\uffff\1\4\1\3\4\uffff\1\5\4\uffff\1\6\13\uffff"+
        "\1\23\14\uffff\1\35\27\uffff\1\1\3\uffff\1\3\1\4\7\uffff\1\5\26"+
        "\uffff\1\33\3\uffff\1\41\4\uffff\1\45\16\uffff\1\65\13\uffff\1\13"+
        "\2\uffff\1\14\1\uffff\1\16\6\uffff\1\26\1\25\3\uffff\1\34\4\uffff"+
        "\1\43\1\73\14\uffff\1\71\2\uffff\1\61\1\uffff\1\66\6\uffff\1\12"+
        "\1\15\1\uffff\1\20\1\17\3\uffff\1\72\1\32\1\uffff\1\30\12\uffff"+
        "\1\53\2\uffff\1\56\4\uffff\1\62\1\uffff\1\70\2\uffff\1\10\3\uffff"+
        "\1\24\6\uffff\1\37\1\44\2\uffff\1\51\1\uffff\1\52\1\54\1\uffff\1"+
        "\57\2\uffff\1\63\2\uffff\1\11\1\uffff\1\22\1\27\1\31\4\uffff\1\47"+
        "\1\46\4\uffff\1\7\1\21\4\uffff\1\55\2\uffff\1\67\1\uffff\1\42\1"+
        "\uffff\1\50\1\uffff\1\64\1\40\1\36\2\uffff\1\60";
    static final String DFA51_specialS =
        "\u01a9\uffff}>";
    static final String[] DFA51_transitionS = {
            "\2\57\1\uffff\2\57\22\uffff\1\57\1\37\1\5\1\uffff\1\56\1\52"+
            "\1\43\1\4\1\26\1\27\1\47\1\45\1\35\1\46\1\3\1\50\1\1\11\2\1"+
            "\42\1\34\1\55\1\36\1\54\1\41\1\53\32\56\1\32\1\uffff\1\33\1"+
            "\51\1\56\1\uffff\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\56\1\15"+
            "\2\56\1\16\1\56\1\17\1\56\1\20\1\56\1\21\1\22\1\23\1\56\1\24"+
            "\1\25\3\56\1\30\1\44\1\31\1\40\101\uffff\27\56\1\uffff\37\56"+
            "\1\uffff\u1f08\56\u1040\uffff\u0150\56\u0170\uffff\u0080\56"+
            "\u0080\uffff\u092e\56\u10d2\uffff\u5200\56\u5900\uffff\u0200"+
            "\56",
            "\1\64\1\uffff\10\63\2\65\12\uffff\1\71\1\62\1\70\5\uffff\1\67"+
            "\13\uffff\1\61\13\uffff\1\71\1\62\1\70\5\uffff\1\67\13\uffff"+
            "\1\60",
            "\1\64\1\uffff\12\72\12\uffff\1\71\1\62\1\70\5\uffff\1\67\27"+
            "\uffff\1\71\1\62\1\70\5\uffff\1\67",
            "\1\73\1\uffff\12\74",
            "\12\77\1\100\2\77\1\100\31\77\1\uffff\64\77\1\76\uffa2\77",
            "\12\102\1\104\2\102\1\104\24\102\1\103\71\102\1\101\uffa2\102",
            "\1\106\20\uffff\1\105",
            "\1\110\2\uffff\1\111\6\uffff\1\107",
            "\1\112\6\uffff\1\113\3\uffff\1\115\2\uffff\1\114",
            "\1\117\11\uffff\1\116",
            "\1\122\1\uffff\1\121\11\uffff\1\120",
            "\1\123\7\uffff\1\125\2\uffff\1\124\2\uffff\1\126",
            "\1\127",
            "\1\132\6\uffff\1\131\1\130",
            "\1\133",
            "\1\136\3\uffff\1\135\17\uffff\1\134",
            "\1\141\20\uffff\1\137\2\uffff\1\140",
            "\1\142",
            "\1\143\13\uffff\1\144\1\145\1\uffff\1\146\1\uffff\1\147",
            "\1\151\11\uffff\1\150",
            "\1\152",
            "\1\153",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\154",
            "\1\156",
            "",
            "",
            "",
            "\1\161\26\uffff\1\160",
            "\1\163\76\uffff\1\164",
            "\1\167\21\uffff\1\166",
            "\1\172\17\uffff\1\171",
            "\1\174",
            "\1\177\4\uffff\1\u0080\15\uffff\1\176",
            "\1\u0082",
            "\1\u0084",
            "",
            "",
            "",
            "",
            "",
            "\1\u0088\1\uffff\12\u0086\7\uffff\6\u0086\11\uffff\1\u0087\20"+
            "\uffff\6\u0086\11\uffff\1\u0087",
            "\1\u0088\1\uffff\12\u0086\7\uffff\6\u0086\11\uffff\1\u0087\20"+
            "\uffff\6\u0086\11\uffff\1\u0087",
            "\1\u0089\1\uffff\1\u0089\2\uffff\12\u008a",
            "\1\64\1\uffff\10\63\2\65\12\uffff\1\71\1\62\1\70\5\uffff\1\u008b"+
            "\27\uffff\1\71\1\62\1\70\5\uffff\1\u008b",
            "\12\u008c\12\uffff\1\u0090\1\u008d\1\u008e\35\uffff\1\u0090"+
            "\1\u008d\1\u008e",
            "\1\64\1\uffff\12\65\12\uffff\1\71\1\62\1\70\35\uffff\1\71\1"+
            "\62\1\70",
            "",
            "",
            "",
            "",
            "\1\64\1\uffff\12\72\12\uffff\1\71\1\62\1\70\5\uffff\1\67\27"+
            "\uffff\1\71\1\62\1\70\5\uffff\1\67",
            "",
            "\12\74\12\uffff\1\u0094\1\u0093\1\u0095\35\uffff\1\u0094\1\u0093"+
            "\1\u0095",
            "",
            "\1\u0097\4\uffff\1\u0097\10\uffff\4\u0098\4\u0099\44\uffff\1"+
            "\u0097\5\uffff\1\u0097\3\uffff\1\u0097\7\uffff\1\u0097\3\uffff"+
            "\1\u0097\1\uffff\1\u0097\1\u0096",
            "\1\u009a",
            "",
            "\1\u009c\4\uffff\1\u009c\10\uffff\4\u009d\4\u009e\44\uffff\1"+
            "\u009c\5\uffff\1\u009c\3\uffff\1\u009c\7\uffff\1\u009c\3\uffff"+
            "\1\u009c\1\uffff\1\u009c\1\u009b",
            "\12\102\1\104\2\102\1\104\24\102\1\103\71\102\1\101\uffa2\102",
            "",
            "",
            "\1\u00a0",
            "\1\u00a1",
            "\1\u00a2",
            "\1\u00a3",
            "\1\u00a4",
            "\1\u00a5\1\u00a6",
            "\1\u00a7",
            "\1\u00a8",
            "\1\u00a9",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\24\56"+
            "\1\u00aa\5\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56"+
            "\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e"+
            "\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56",
            "\1\u00ac",
            "\1\u00ad",
            "\1\u00ae",
            "\1\u00af",
            "\1\u00b0",
            "\1\u00b1",
            "\1\u00b2",
            "\1\u00b3",
            "\1\u00b4",
            "\1\u00b5\1\u00b6",
            "\1\u00b7",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00b9",
            "\1\u00ba",
            "\1\u00bb",
            "\1\u00bc",
            "\1\u00be\5\uffff\1\u00bd",
            "\1\u00bf",
            "\1\u00c0",
            "\1\u00c1",
            "\1\u00c2",
            "\1\u00c3\20\uffff\1\u00c4",
            "\1\u00c5",
            "\1\u00c6",
            "\1\u00c7",
            "\1\u00c9\23\uffff\1\u00c8\3\uffff\1\u00ca",
            "\1\u00cc\10\uffff\1\u00cb",
            "\1\u00ce\2\uffff\1\u00cd",
            "\1\u00cf",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u0088\1\uffff\12\u0086\7\uffff\6\u0086\5\uffff\1\u00d0\3"+
            "\uffff\1\u0087\20\uffff\6\u0086\5\uffff\1\u00d0\3\uffff\1\u0087",
            "\1\u00d1\1\uffff\1\u00d1\2\uffff\12\u00d2",
            "\12\u00d3\7\uffff\6\u00d3\11\uffff\1\u0087\20\uffff\6\u00d3"+
            "\11\uffff\1\u0087",
            "\12\u008a",
            "\12\u008a\12\uffff\1\u00d5\1\uffff\1\u00d4\35\uffff\1\u00d5"+
            "\1\uffff\1\u00d4",
            "",
            "\12\u008c\12\uffff\1\u0090\1\u008d\1\u008e\35\uffff\1\u0090"+
            "\1\u008d\1\u008e",
            "\1\u00d6\1\uffff\1\u00d6\2\uffff\12\u00d7",
            "",
            "",
            "",
            "",
            "",
            "\1\u00d8\1\uffff\1\u00d8\2\uffff\12\u00d9",
            "",
            "",
            "\12\u00da\7\uffff\6\u00da\32\uffff\6\u00da",
            "\1\u009a",
            "\1\u009a\10\uffff\10\u00db",
            "\1\u009a\10\uffff\10\u00dc",
            "",
            "\12\u00de\7\uffff\6\u00de\32\uffff\6\u00de",
            "\12\102\1\104\2\102\1\104\24\102\1\103\71\102\1\101\uffa2\102",
            "\12\102\1\104\2\102\1\104\24\102\1\103\15\102\10\u00df\44\102"+
            "\1\101\uffa2\102",
            "\12\102\1\104\2\102\1\104\24\102\1\103\15\102\10\u00e0\44\102"+
            "\1\101\uffa2\102",
            "",
            "\1\u00e1",
            "\1\u00e2",
            "\1\u00e3",
            "\1\u00e4",
            "\1\u00e5",
            "\1\u00e6",
            "\1\u00e7",
            "\1\u00e8",
            "\1\u00ea\1\u00e9",
            "\1\u00eb",
            "\1\u00ec",
            "",
            "\1\u00ed",
            "\1\u00ee",
            "\1\u00ef",
            "\1\u00f0",
            "\1\u00f1",
            "\1\u00f2",
            "\1\u00f3",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00f5",
            "\1\u00f6",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\4\56"+
            "\1\u00f7\25\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56"+
            "\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e"+
            "\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56",
            "\1\u00f9\2\uffff\1\u00fa",
            "",
            "\1\u00fb",
            "\1\u00fc",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00fe",
            "\1\u00ff",
            "\1\u0100",
            "\1\u0101",
            "\1\u0102",
            "\1\u0103",
            "\1\u0104",
            "\1\u0105",
            "\1\u0106",
            "\1\u0107",
            "\1\u0108",
            "\1\u0109",
            "\1\u010a",
            "\1\u010b",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u010d",
            "\1\u010e",
            "\1\u010f",
            "\1\u0110",
            "\1\u0111",
            "",
            "\12\u00d2",
            "\12\u00d2\14\uffff\1\u0092\37\uffff\1\u0092",
            "\12\u00d3\7\uffff\6\u00d3\11\uffff\1\u0087\20\uffff\6\u00d3"+
            "\11\uffff\1\u0087",
            "",
            "",
            "\12\u00d7",
            "\12\u00d7\12\uffff\1\u0090\1\uffff\1\u008e\35\uffff\1\u0090"+
            "\1\uffff\1\u008e",
            "\12\u00d9",
            "\12\u00d9\12\uffff\1\u0094\1\uffff\1\u0095\35\uffff\1\u0094"+
            "\1\uffff\1\u0095",
            "\12\u0112\7\uffff\6\u0112\32\uffff\6\u0112",
            "\1\u009a\10\uffff\10\u0113",
            "\1\u009a",
            "",
            "\12\u0114\7\uffff\6\u0114\32\uffff\6\u0114",
            "\12\102\1\104\2\102\1\104\24\102\1\103\15\102\10\u0115\44\102"+
            "\1\101\uffa2\102",
            "\12\102\1\104\2\102\1\104\24\102\1\103\71\102\1\101\uffa2\102",
            "\1\u0116",
            "\1\u0117",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0119",
            "\1\u011a",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u011c",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u011e",
            "\1\u011f",
            "\1\u0120",
            "\1\u0121",
            "\1\u0122",
            "\1\u0123",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0126",
            "\1\u0127",
            "\1\u0128",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u012a",
            "\1\u012b",
            "",
            "\1\u012c",
            "\1\u012d",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\u0130",
            "\1\u0131",
            "\1\u0132",
            "\1\u0133",
            "\1\u0134",
            "\1\u0135",
            "\1\u0136",
            "\1\u0137",
            "\1\u0138",
            "\1\u0139",
            "\1\u013a",
            "\1\u013b",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u013d",
            "",
            "\1\u013e",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0140",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0142",
            "\12\u0143\7\uffff\6\u0143\32\uffff\6\u0143",
            "\1\u009a",
            "\12\u0144\7\uffff\6\u0144\32\uffff\6\u0144",
            "\12\102\1\104\2\102\1\104\24\102\1\103\71\102\1\101\uffa2\102",
            "\1\u0145",
            "\1\u0146",
            "",
            "\1\u0147",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\u014a",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u014d",
            "\1\u014e",
            "\1\u014f",
            "",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\13\56"+
            "\1\u0152\16\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56"+
            "\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e"+
            "\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\u0154",
            "\1\u0155",
            "\1\u0156",
            "\1\u0157",
            "",
            "",
            "\1\u0158",
            "\1\u0159",
            "\1\u015a",
            "\1\u015b",
            "\1\u015c",
            "\1\u015d",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u015f",
            "\1\u0160",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0162",
            "\1\u0163",
            "",
            "\1\u0164",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\22\56"+
            "\1\u0165\7\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56"+
            "\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e"+
            "\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\u0167",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\12\u0169\7\uffff\6\u0169\32\uffff\6\u0169",
            "\12\u016a\7\uffff\6\u016a\32\uffff\6\u016a",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u016c",
            "\1\u016d",
            "",
            "",
            "\1\u016e",
            "",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0170",
            "\1\u0171",
            "",
            "",
            "\1\u0172",
            "",
            "\1\u0173",
            "\1\u0174",
            "\1\u0175",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0178",
            "\1\u0179",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u017b",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u017e",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0180",
            "\1\u0181",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\u0183",
            "",
            "\1\u009a",
            "\12\102\1\104\2\102\1\104\24\102\1\103\71\102\1\101\uffa2\102",
            "",
            "\1\u0184",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0186",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u018a",
            "\1\u018b",
            "\1\u018c",
            "",
            "",
            "\1\u018d",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "",
            "\1\u0190",
            "",
            "\1\u0191",
            "\1\u0192",
            "",
            "\1\u0193",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "",
            "",
            "\1\u0196",
            "\1\u0197",
            "\1\u0198",
            "\1\u0199",
            "",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u019b",
            "\1\u019c",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "",
            "\1\u019e",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u01a0",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\u01a2",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\u01a6",
            "",
            "",
            "",
            "\1\u01a7",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32\56"+
            "\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            ""
    };

    static final short[] DFA51_eot = DFA.unpackEncodedString(DFA51_eotS);
    static final short[] DFA51_eof = DFA.unpackEncodedString(DFA51_eofS);
    static final char[] DFA51_min = DFA.unpackEncodedStringToUnsignedChars(DFA51_minS);
    static final char[] DFA51_max = DFA.unpackEncodedStringToUnsignedChars(DFA51_maxS);
    static final short[] DFA51_accept = DFA.unpackEncodedString(DFA51_acceptS);
    static final short[] DFA51_special = DFA.unpackEncodedString(DFA51_specialS);
    static final short[][] DFA51_transition;

    static {
        int numStates = DFA51_transitionS.length;
        DFA51_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA51_transition[i] = DFA.unpackEncodedString(DFA51_transitionS[i]);
        }
    }

    class DFA51 extends DFA {

        public DFA51(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 51;
            this.eot = DFA51_eot;
            this.eof = DFA51_eof;
            this.min = DFA51_min;
            this.max = DFA51_max;
            this.accept = DFA51_accept;
            this.special = DFA51_special;
            this.transition = DFA51_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( LONGLITERAL | INTLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | ABSTRACT | ASSERT | BOOLEAN | BREAK | BYTE | CASE | CATCH | CHAR | CLASS | CONST | CONTINUE | DEFAULT | DO | DOUBLE | ELSE | ENUM | EXTENDS | FINAL | FINALLY | FLOAT | FOR | GOTO | IF | IMPLEMENTS | IMPORT | INSTANCEOF | INT | INTERFACE | LONG | NATIVE | NEW | PACKAGE | PRIVATE | PROTECTED | PUBLIC | RETURN | SHORT | STATIC | STRICTFP | SUPER | SWITCH | SYNCHRONIZED | THIS | THROW | THROWS | TRANSIENT | TRY | VOID | VOLATILE | WHILE | TRUE | FALSE | NULL | LPAREN | RPAREN | LBRACE | RBRACE | LBRACKET | RBRACKET | SEMI | COMMA | DOT | ELLIPSIS | EQ | BANG | TILDE | QUES | COLON | EQEQ | AMPAMP | BARBAR | PLUSPLUS | SUBSUB | PLUS | SUB | STAR | SLASH | AMP | BAR | CARET | PERCENT | PLUSEQ | SUBEQ | STAREQ | SLASHEQ | AMPEQ | BAREQ | CARETEQ | PERCENTEQ | MONKEYS_AT | BANGEQ | GT | LT | BooleanLiteral | HexLiteral | DecimalLiteral | OctalLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | IDENTIFIER | WS | COMMENT | LINE_COMMENT );";
        }
    }
 

}