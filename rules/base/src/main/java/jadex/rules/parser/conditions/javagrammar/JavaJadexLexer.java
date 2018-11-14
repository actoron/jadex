// $ANTLR 3.5.1 C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g 2013-12-10 14:06:21

package jadex.rules.parser.conditions.javagrammar;


import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;

@SuppressWarnings("all")
public class JavaJadexLexer extends Lexer {
	public static final int EOF=-1;
	public static final int T__24=24;
	public static final int T__25=25;
	public static final int T__26=26;
	public static final int T__27=27;
	public static final int T__28=28;
	public static final int T__29=29;
	public static final int T__30=30;
	public static final int T__31=31;
	public static final int T__32=32;
	public static final int T__33=33;
	public static final int T__34=34;
	public static final int T__35=35;
	public static final int T__36=36;
	public static final int T__37=37;
	public static final int T__38=38;
	public static final int T__39=39;
	public static final int T__40=40;
	public static final int T__41=41;
	public static final int T__42=42;
	public static final int T__43=43;
	public static final int T__44=44;
	public static final int T__45=45;
	public static final int T__46=46;
	public static final int T__47=47;
	public static final int T__48=48;
	public static final int BooleanLiteral=4;
	public static final int COMMENT=5;
	public static final int CharacterLiteral=6;
	public static final int DecimalLiteral=7;
	public static final int EscapeSequence=8;
	public static final int Exponent=9;
	public static final int FloatTypeSuffix=10;
	public static final int FloatingPointLiteral=11;
	public static final int HexDigit=12;
	public static final int HexLiteral=13;
	public static final int IDENTIFIER=14;
	public static final int IntegerTypeSuffix=15;
	public static final int JavaIDDigit=16;
	public static final int LINE_COMMENT=17;
	public static final int Letter=18;
	public static final int OctalEscape=19;
	public static final int OctalLiteral=20;
	public static final int StringLiteral=21;
	public static final int UnicodeEscape=22;
	public static final int WS=23;

	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public JavaJadexLexer() {} 
	public JavaJadexLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public JavaJadexLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g"; }

	// $ANTLR start "T__24"
	public final void mT__24() throws RecognitionException {
		try {
			int _type = T__24;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:6:7: ( '!' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:6:9: '!'
			{
			match('!'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__24"

	// $ANTLR start "T__25"
	public final void mT__25() throws RecognitionException {
		try {
			int _type = T__25;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:7:7: ( '!=' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:7:9: '!='
			{
			match("!="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__25"

	// $ANTLR start "T__26"
	public final void mT__26() throws RecognitionException {
		try {
			int _type = T__26;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:8:7: ( '%' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:8:9: '%'
			{
			match('%'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__26"

	// $ANTLR start "T__27"
	public final void mT__27() throws RecognitionException {
		try {
			int _type = T__27;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:9:7: ( '&&' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:9:9: '&&'
			{
			match("&&"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__27"

	// $ANTLR start "T__28"
	public final void mT__28() throws RecognitionException {
		try {
			int _type = T__28;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:10:7: ( '(' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:10:9: '('
			{
			match('('); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__28"

	// $ANTLR start "T__29"
	public final void mT__29() throws RecognitionException {
		try {
			int _type = T__29;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:11:7: ( ')' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:11:9: ')'
			{
			match(')'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__29"

	// $ANTLR start "T__30"
	public final void mT__30() throws RecognitionException {
		try {
			int _type = T__30;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:12:7: ( '*' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:12:9: '*'
			{
			match('*'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__30"

	// $ANTLR start "T__31"
	public final void mT__31() throws RecognitionException {
		try {
			int _type = T__31;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:13:7: ( '+' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:13:9: '+'
			{
			match('+'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__31"

	// $ANTLR start "T__32"
	public final void mT__32() throws RecognitionException {
		try {
			int _type = T__32;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:14:7: ( ',' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:14:9: ','
			{
			match(','); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__32"

	// $ANTLR start "T__33"
	public final void mT__33() throws RecognitionException {
		try {
			int _type = T__33;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:15:7: ( '-' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:15:9: '-'
			{
			match('-'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__33"

	// $ANTLR start "T__34"
	public final void mT__34() throws RecognitionException {
		try {
			int _type = T__34;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:16:7: ( '.' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:16:9: '.'
			{
			match('.'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__34"

	// $ANTLR start "T__35"
	public final void mT__35() throws RecognitionException {
		try {
			int _type = T__35;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:17:7: ( '/' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:17:9: '/'
			{
			match('/'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__35"

	// $ANTLR start "T__36"
	public final void mT__36() throws RecognitionException {
		try {
			int _type = T__36;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:18:7: ( ':' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:18:9: ':'
			{
			match(':'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__36"

	// $ANTLR start "T__37"
	public final void mT__37() throws RecognitionException {
		try {
			int _type = T__37;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:19:7: ( '<' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:19:9: '<'
			{
			match('<'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__37"

	// $ANTLR start "T__38"
	public final void mT__38() throws RecognitionException {
		try {
			int _type = T__38;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:20:7: ( '<=' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:20:9: '<='
			{
			match("<="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__38"

	// $ANTLR start "T__39"
	public final void mT__39() throws RecognitionException {
		try {
			int _type = T__39;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:21:7: ( '==' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:21:9: '=='
			{
			match("=="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__39"

	// $ANTLR start "T__40"
	public final void mT__40() throws RecognitionException {
		try {
			int _type = T__40;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:22:7: ( '>' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:22:9: '>'
			{
			match('>'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__40"

	// $ANTLR start "T__41"
	public final void mT__41() throws RecognitionException {
		try {
			int _type = T__41;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:23:7: ( '>=' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:23:9: '>='
			{
			match(">="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__41"

	// $ANTLR start "T__42"
	public final void mT__42() throws RecognitionException {
		try {
			int _type = T__42;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:24:7: ( '?' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:24:9: '?'
			{
			match('?'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__42"

	// $ANTLR start "T__43"
	public final void mT__43() throws RecognitionException {
		try {
			int _type = T__43;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:25:7: ( '[' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:25:9: '['
			{
			match('['); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__43"

	// $ANTLR start "T__44"
	public final void mT__44() throws RecognitionException {
		try {
			int _type = T__44;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:26:7: ( ']' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:26:9: ']'
			{
			match(']'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__44"

	// $ANTLR start "T__45"
	public final void mT__45() throws RecognitionException {
		try {
			int _type = T__45;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:27:7: ( 'collect(' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:27:9: 'collect('
			{
			match("collect("); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__45"

	// $ANTLR start "T__46"
	public final void mT__46() throws RecognitionException {
		try {
			int _type = T__46;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:28:7: ( 'null' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:28:9: 'null'
			{
			match("null"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__46"

	// $ANTLR start "T__47"
	public final void mT__47() throws RecognitionException {
		try {
			int _type = T__47;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:29:7: ( '||' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:29:9: '||'
			{
			match("||"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__47"

	// $ANTLR start "T__48"
	public final void mT__48() throws RecognitionException {
		try {
			int _type = T__48;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:30:7: ( '~' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:30:9: '~'
			{
			match('~'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__48"

	// $ANTLR start "BooleanLiteral"
	public final void mBooleanLiteral() throws RecognitionException {
		try {
			int _type = BooleanLiteral;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:464:2: ( 'true' | 'false' )
			int alt1=2;
			int LA1_0 = input.LA(1);
			if ( (LA1_0=='t') ) {
				alt1=1;
			}
			else if ( (LA1_0=='f') ) {
				alt1=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 1, 0, input);
				throw nvae;
			}

			switch (alt1) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:464:6: 'true'
					{
					match("true"); 

					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:464:15: 'false'
					{
					match("false"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "BooleanLiteral"

	// $ANTLR start "HexLiteral"
	public final void mHexLiteral() throws RecognitionException {
		try {
			int _type = HexLiteral;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:468:2: ( '0' ( 'x' | 'X' ) ( HexDigit )+ ( IntegerTypeSuffix )? )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:468:4: '0' ( 'x' | 'X' ) ( HexDigit )+ ( IntegerTypeSuffix )?
			{
			match('0'); 
			if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:468:18: ( HexDigit )+
			int cnt2=0;
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( ((LA2_0 >= '0' && LA2_0 <= '9')||(LA2_0 >= 'A' && LA2_0 <= 'F')||(LA2_0 >= 'a' && LA2_0 <= 'f')) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt2 >= 1 ) break loop2;
					EarlyExitException eee = new EarlyExitException(2, input);
					throw eee;
				}
				cnt2++;
			}

			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:468:28: ( IntegerTypeSuffix )?
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0=='L'||LA3_0=='l') ) {
				alt3=1;
			}
			switch (alt3) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
					{
					if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "HexLiteral"

	// $ANTLR start "DecimalLiteral"
	public final void mDecimalLiteral() throws RecognitionException {
		try {
			int _type = DecimalLiteral;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:472:2: ( ( '0' | '1' .. '9' ( '0' .. '9' )* ) ( IntegerTypeSuffix )? )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:472:4: ( '0' | '1' .. '9' ( '0' .. '9' )* ) ( IntegerTypeSuffix )?
			{
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:472:4: ( '0' | '1' .. '9' ( '0' .. '9' )* )
			int alt5=2;
			int LA5_0 = input.LA(1);
			if ( (LA5_0=='0') ) {
				alt5=1;
			}
			else if ( ((LA5_0 >= '1' && LA5_0 <= '9')) ) {
				alt5=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 5, 0, input);
				throw nvae;
			}

			switch (alt5) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:472:5: '0'
					{
					match('0'); 
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:472:11: '1' .. '9' ( '0' .. '9' )*
					{
					matchRange('1','9'); 
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:472:20: ( '0' .. '9' )*
					loop4:
					while (true) {
						int alt4=2;
						int LA4_0 = input.LA(1);
						if ( ((LA4_0 >= '0' && LA4_0 <= '9')) ) {
							alt4=1;
						}

						switch (alt4) {
						case 1 :
							// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							break loop4;
						}
					}

					}
					break;

			}

			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:472:31: ( IntegerTypeSuffix )?
			int alt6=2;
			int LA6_0 = input.LA(1);
			if ( (LA6_0=='L'||LA6_0=='l') ) {
				alt6=1;
			}
			switch (alt6) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
					{
					if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DecimalLiteral"

	// $ANTLR start "OctalLiteral"
	public final void mOctalLiteral() throws RecognitionException {
		try {
			int _type = OctalLiteral;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:476:2: ( '0' ( '0' .. '7' )+ ( IntegerTypeSuffix )? )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:476:4: '0' ( '0' .. '7' )+ ( IntegerTypeSuffix )?
			{
			match('0'); 
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:476:8: ( '0' .. '7' )+
			int cnt7=0;
			loop7:
			while (true) {
				int alt7=2;
				int LA7_0 = input.LA(1);
				if ( ((LA7_0 >= '0' && LA7_0 <= '7')) ) {
					alt7=1;
				}

				switch (alt7) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt7 >= 1 ) break loop7;
					EarlyExitException eee = new EarlyExitException(7, input);
					throw eee;
				}
				cnt7++;
			}

			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:476:20: ( IntegerTypeSuffix )?
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0=='L'||LA8_0=='l') ) {
				alt8=1;
			}
			switch (alt8) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
					{
					if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OctalLiteral"

	// $ANTLR start "HexDigit"
	public final void mHexDigit() throws RecognitionException {
		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:481:9: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
			{
			if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "HexDigit"

	// $ANTLR start "IntegerTypeSuffix"
	public final void mIntegerTypeSuffix() throws RecognitionException {
		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:486:2: ( ( 'l' | 'L' ) )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
			{
			if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "IntegerTypeSuffix"

	// $ANTLR start "FloatingPointLiteral"
	public final void mFloatingPointLiteral() throws RecognitionException {
		try {
			int _type = FloatingPointLiteral;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:489:6: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )? | '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )? | ( '0' .. '9' )+ Exponent FloatTypeSuffix | ( '0' .. '9' )+ Exponent | ( '0' .. '9' )+ FloatTypeSuffix )
			int alt19=5;
			alt19 = dfa19.predict(input);
			switch (alt19) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:489:10: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )?
					{
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:489:10: ( '0' .. '9' )+
					int cnt9=0;
					loop9:
					while (true) {
						int alt9=2;
						int LA9_0 = input.LA(1);
						if ( ((LA9_0 >= '0' && LA9_0 <= '9')) ) {
							alt9=1;
						}

						switch (alt9) {
						case 1 :
							// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt9 >= 1 ) break loop9;
							EarlyExitException eee = new EarlyExitException(9, input);
							throw eee;
						}
						cnt9++;
					}

					match('.'); 
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:489:26: ( '0' .. '9' )*
					loop10:
					while (true) {
						int alt10=2;
						int LA10_0 = input.LA(1);
						if ( ((LA10_0 >= '0' && LA10_0 <= '9')) ) {
							alt10=1;
						}

						switch (alt10) {
						case 1 :
							// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							break loop10;
						}
					}

					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:489:38: ( Exponent )?
					int alt11=2;
					int LA11_0 = input.LA(1);
					if ( (LA11_0=='E'||LA11_0=='e') ) {
						alt11=1;
					}
					switch (alt11) {
						case 1 :
							// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:489:38: Exponent
							{
							mExponent(); 

							}
							break;

					}

					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:489:48: ( FloatTypeSuffix )?
					int alt12=2;
					int LA12_0 = input.LA(1);
					if ( (LA12_0=='D'||LA12_0=='F'||LA12_0=='d'||LA12_0=='f') ) {
						alt12=1;
					}
					switch (alt12) {
						case 1 :
							// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
							{
							if ( input.LA(1)=='D'||input.LA(1)=='F'||input.LA(1)=='d'||input.LA(1)=='f' ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

					}

					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:490:7: '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )?
					{
					match('.'); 
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:490:11: ( '0' .. '9' )+
					int cnt13=0;
					loop13:
					while (true) {
						int alt13=2;
						int LA13_0 = input.LA(1);
						if ( ((LA13_0 >= '0' && LA13_0 <= '9')) ) {
							alt13=1;
						}

						switch (alt13) {
						case 1 :
							// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt13 >= 1 ) break loop13;
							EarlyExitException eee = new EarlyExitException(13, input);
							throw eee;
						}
						cnt13++;
					}

					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:490:23: ( Exponent )?
					int alt14=2;
					int LA14_0 = input.LA(1);
					if ( (LA14_0=='E'||LA14_0=='e') ) {
						alt14=1;
					}
					switch (alt14) {
						case 1 :
							// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:490:23: Exponent
							{
							mExponent(); 

							}
							break;

					}

					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:490:33: ( FloatTypeSuffix )?
					int alt15=2;
					int LA15_0 = input.LA(1);
					if ( (LA15_0=='D'||LA15_0=='F'||LA15_0=='d'||LA15_0=='f') ) {
						alt15=1;
					}
					switch (alt15) {
						case 1 :
							// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
							{
							if ( input.LA(1)=='D'||input.LA(1)=='F'||input.LA(1)=='d'||input.LA(1)=='f' ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

					}

					}
					break;
				case 3 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:491:7: ( '0' .. '9' )+ Exponent FloatTypeSuffix
					{
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:491:7: ( '0' .. '9' )+
					int cnt16=0;
					loop16:
					while (true) {
						int alt16=2;
						int LA16_0 = input.LA(1);
						if ( ((LA16_0 >= '0' && LA16_0 <= '9')) ) {
							alt16=1;
						}

						switch (alt16) {
						case 1 :
							// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt16 >= 1 ) break loop16;
							EarlyExitException eee = new EarlyExitException(16, input);
							throw eee;
						}
						cnt16++;
					}

					mExponent(); 

					mFloatTypeSuffix(); 

					}
					break;
				case 4 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:492:7: ( '0' .. '9' )+ Exponent
					{
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:492:7: ( '0' .. '9' )+
					int cnt17=0;
					loop17:
					while (true) {
						int alt17=2;
						int LA17_0 = input.LA(1);
						if ( ((LA17_0 >= '0' && LA17_0 <= '9')) ) {
							alt17=1;
						}

						switch (alt17) {
						case 1 :
							// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt17 >= 1 ) break loop17;
							EarlyExitException eee = new EarlyExitException(17, input);
							throw eee;
						}
						cnt17++;
					}

					mExponent(); 

					}
					break;
				case 5 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:493:6: ( '0' .. '9' )+ FloatTypeSuffix
					{
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:493:6: ( '0' .. '9' )+
					int cnt18=0;
					loop18:
					while (true) {
						int alt18=2;
						int LA18_0 = input.LA(1);
						if ( ((LA18_0 >= '0' && LA18_0 <= '9')) ) {
							alt18=1;
						}

						switch (alt18) {
						case 1 :
							// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt18 >= 1 ) break loop18;
							EarlyExitException eee = new EarlyExitException(18, input);
							throw eee;
						}
						cnt18++;
					}

					mFloatTypeSuffix(); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FloatingPointLiteral"

	// $ANTLR start "Exponent"
	public final void mExponent() throws RecognitionException {
		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:498:9: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:498:11: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
			{
			if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:498:21: ( '+' | '-' )?
			int alt20=2;
			int LA20_0 = input.LA(1);
			if ( (LA20_0=='+'||LA20_0=='-') ) {
				alt20=1;
			}
			switch (alt20) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
					{
					if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

			}

			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:498:32: ( '0' .. '9' )+
			int cnt21=0;
			loop21:
			while (true) {
				int alt21=2;
				int LA21_0 = input.LA(1);
				if ( ((LA21_0 >= '0' && LA21_0 <= '9')) ) {
					alt21=1;
				}

				switch (alt21) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt21 >= 1 ) break loop21;
					EarlyExitException eee = new EarlyExitException(21, input);
					throw eee;
				}
				cnt21++;
			}

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Exponent"

	// $ANTLR start "FloatTypeSuffix"
	public final void mFloatTypeSuffix() throws RecognitionException {
		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:503:2: ( ( 'f' | 'F' | 'd' | 'D' ) )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
			{
			if ( input.LA(1)=='D'||input.LA(1)=='F'||input.LA(1)=='d'||input.LA(1)=='f' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FloatTypeSuffix"

	// $ANTLR start "CharacterLiteral"
	public final void mCharacterLiteral() throws RecognitionException {
		try {
			int _type = CharacterLiteral;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:506:2: ( '\\'' ( EscapeSequence |~ ( '\\'' | '\\\\' ) ) '\\'' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:506:6: '\\'' ( EscapeSequence |~ ( '\\'' | '\\\\' ) ) '\\''
			{
			match('\''); 
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:506:11: ( EscapeSequence |~ ( '\\'' | '\\\\' ) )
			int alt22=2;
			int LA22_0 = input.LA(1);
			if ( (LA22_0=='\\') ) {
				alt22=1;
			}
			else if ( ((LA22_0 >= '\u0000' && LA22_0 <= '&')||(LA22_0 >= '(' && LA22_0 <= '[')||(LA22_0 >= ']' && LA22_0 <= '\uFFFF')) ) {
				alt22=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 22, 0, input);
				throw nvae;
			}

			switch (alt22) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:506:13: EscapeSequence
					{
					mEscapeSequence(); 

					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:506:30: ~ ( '\\'' | '\\\\' )
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

			}

			match('\''); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CharacterLiteral"

	// $ANTLR start "StringLiteral"
	public final void mStringLiteral() throws RecognitionException {
		try {
			int _type = StringLiteral;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			CommonToken text=null;

			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:510:2: ( '\"' (text= EscapeSequence |~ ( '\\\\' | '\"' ) )* '\"' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:510:5: '\"' (text= EscapeSequence |~ ( '\\\\' | '\"' ) )* '\"'
			{
			match('\"'); 
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:510:9: (text= EscapeSequence |~ ( '\\\\' | '\"' ) )*
			loop23:
			while (true) {
				int alt23=3;
				int LA23_0 = input.LA(1);
				if ( (LA23_0=='\\') ) {
					alt23=1;
				}
				else if ( ((LA23_0 >= '\u0000' && LA23_0 <= '!')||(LA23_0 >= '#' && LA23_0 <= '[')||(LA23_0 >= ']' && LA23_0 <= '\uFFFF')) ) {
					alt23=2;
				}

				switch (alt23) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:510:11: text= EscapeSequence
					{
					int textStart536 = getCharIndex();
					int textStartLine536 = getLine();
					int textStartCharPos536 = getCharPositionInLine();
					mEscapeSequence(); 
					text = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, textStart536, getCharIndex()-1);
					text.setLine(textStartLine536);
					text.setCharPositionInLine(textStartCharPos536);

					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:510:33: ~ ( '\\\\' | '\"' )
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop23;
				}
			}

			match('\"'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "StringLiteral"

	// $ANTLR start "EscapeSequence"
	public final void mEscapeSequence() throws RecognitionException {
		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:516:2: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape )
			int alt24=3;
			int LA24_0 = input.LA(1);
			if ( (LA24_0=='\\') ) {
				switch ( input.LA(2) ) {
				case '\"':
				case '\'':
				case '\\':
				case 'b':
				case 'f':
				case 'n':
				case 'r':
				case 't':
					{
					alt24=1;
					}
					break;
				case 'u':
					{
					alt24=2;
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
					alt24=3;
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 24, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 24, 0, input);
				throw nvae;
			}

			switch (alt24) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:516:6: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
					{
					match('\\'); 
					if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:517:6: UnicodeEscape
					{
					mUnicodeEscape(); 

					}
					break;
				case 3 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:518:6: OctalEscape
					{
					mOctalEscape(); 

					}
					break;

			}
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EscapeSequence"

	// $ANTLR start "OctalEscape"
	public final void mOctalEscape() throws RecognitionException {
		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:523:2: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
			int alt25=3;
			int LA25_0 = input.LA(1);
			if ( (LA25_0=='\\') ) {
				int LA25_1 = input.LA(2);
				if ( ((LA25_1 >= '0' && LA25_1 <= '3')) ) {
					int LA25_2 = input.LA(3);
					if ( ((LA25_2 >= '0' && LA25_2 <= '7')) ) {
						int LA25_4 = input.LA(4);
						if ( ((LA25_4 >= '0' && LA25_4 <= '7')) ) {
							alt25=1;
						}

						else {
							alt25=2;
						}

					}

					else {
						alt25=3;
					}

				}
				else if ( ((LA25_1 >= '4' && LA25_1 <= '7')) ) {
					int LA25_3 = input.LA(3);
					if ( ((LA25_3 >= '0' && LA25_3 <= '7')) ) {
						alt25=2;
					}

					else {
						alt25=3;
					}

				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 25, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 25, 0, input);
				throw nvae;
			}

			switch (alt25) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:523:6: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
					{
					match('\\'); 
					if ( (input.LA(1) >= '0' && input.LA(1) <= '3') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:524:6: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
					{
					match('\\'); 
					if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;
				case 3 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:525:6: '\\\\' ( '0' .. '7' )
					{
					match('\\'); 
					if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

			}
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OctalEscape"

	// $ANTLR start "UnicodeEscape"
	public final void mUnicodeEscape() throws RecognitionException {
		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:530:2: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:530:6: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
			// do for sure before leaving
		}
	}
	// $ANTLR end "UnicodeEscape"

	// $ANTLR start "IDENTIFIER"
	public final void mIDENTIFIER() throws RecognitionException {
		try {
			int _type = IDENTIFIER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:533:2: ( Letter ( Letter | JavaIDDigit )* )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:533:6: Letter ( Letter | JavaIDDigit )*
			{
			mLetter(); 

			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:533:13: ( Letter | JavaIDDigit )*
			loop26:
			while (true) {
				int alt26=2;
				int LA26_0 = input.LA(1);
				if ( (LA26_0=='$'||(LA26_0 >= '0' && LA26_0 <= '9')||(LA26_0 >= 'A' && LA26_0 <= 'Z')||LA26_0=='_'||(LA26_0 >= 'a' && LA26_0 <= 'z')||(LA26_0 >= '\u00C0' && LA26_0 <= '\u00D6')||(LA26_0 >= '\u00D8' && LA26_0 <= '\u00F6')||(LA26_0 >= '\u00F8' && LA26_0 <= '\u1FFF')||(LA26_0 >= '\u3040' && LA26_0 <= '\u318F')||(LA26_0 >= '\u3300' && LA26_0 <= '\u337F')||(LA26_0 >= '\u3400' && LA26_0 <= '\u3D2D')||(LA26_0 >= '\u4E00' && LA26_0 <= '\u9FFF')||(LA26_0 >= '\uF900' && LA26_0 <= '\uFAFF')) ) {
					alt26=1;
				}

				switch (alt26) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
					{
					if ( input.LA(1)=='$'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||(input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6')||(input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00F6')||(input.LA(1) >= '\u00F8' && input.LA(1) <= '\u1FFF')||(input.LA(1) >= '\u3040' && input.LA(1) <= '\u318F')||(input.LA(1) >= '\u3300' && input.LA(1) <= '\u337F')||(input.LA(1) >= '\u3400' && input.LA(1) <= '\u3D2D')||(input.LA(1) >= '\u4E00' && input.LA(1) <= '\u9FFF')||(input.LA(1) >= '\uF900' && input.LA(1) <= '\uFAFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop26;
				}
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "IDENTIFIER"

	// $ANTLR start "Letter"
	public final void mLetter() throws RecognitionException {
		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:543:2: ( '\\u0024' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u00ff' | '\\u0100' .. '\\u1fff' | '\\u3040' .. '\\u318f' | '\\u3300' .. '\\u337f' | '\\u3400' .. '\\u3d2d' | '\\u4e00' .. '\\u9fff' | '\\uf900' .. '\\ufaff' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
			{
			if ( input.LA(1)=='$'||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||(input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6')||(input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00F6')||(input.LA(1) >= '\u00F8' && input.LA(1) <= '\u1FFF')||(input.LA(1) >= '\u3040' && input.LA(1) <= '\u318F')||(input.LA(1) >= '\u3300' && input.LA(1) <= '\u337F')||(input.LA(1) >= '\u3400' && input.LA(1) <= '\u3D2D')||(input.LA(1) >= '\u4E00' && input.LA(1) <= '\u9FFF')||(input.LA(1) >= '\uF900' && input.LA(1) <= '\uFAFF') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Letter"

	// $ANTLR start "JavaIDDigit"
	public final void mJavaIDDigit() throws RecognitionException {
		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:560:2: ( '\\u0030' .. '\\u0039' | '\\u0660' .. '\\u0669' | '\\u06f0' .. '\\u06f9' | '\\u0966' .. '\\u096f' | '\\u09e6' .. '\\u09ef' | '\\u0a66' .. '\\u0a6f' | '\\u0ae6' .. '\\u0aef' | '\\u0b66' .. '\\u0b6f' | '\\u0be7' .. '\\u0bef' | '\\u0c66' .. '\\u0c6f' | '\\u0ce6' .. '\\u0cef' | '\\u0d66' .. '\\u0d6f' | '\\u0e50' .. '\\u0e59' | '\\u0ed0' .. '\\u0ed9' | '\\u1040' .. '\\u1049' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
			{
			if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= '\u0660' && input.LA(1) <= '\u0669')||(input.LA(1) >= '\u06F0' && input.LA(1) <= '\u06F9')||(input.LA(1) >= '\u0966' && input.LA(1) <= '\u096F')||(input.LA(1) >= '\u09E6' && input.LA(1) <= '\u09EF')||(input.LA(1) >= '\u0A66' && input.LA(1) <= '\u0A6F')||(input.LA(1) >= '\u0AE6' && input.LA(1) <= '\u0AEF')||(input.LA(1) >= '\u0B66' && input.LA(1) <= '\u0B6F')||(input.LA(1) >= '\u0BE7' && input.LA(1) <= '\u0BEF')||(input.LA(1) >= '\u0C66' && input.LA(1) <= '\u0C6F')||(input.LA(1) >= '\u0CE6' && input.LA(1) <= '\u0CEF')||(input.LA(1) >= '\u0D66' && input.LA(1) <= '\u0D6F')||(input.LA(1) >= '\u0E50' && input.LA(1) <= '\u0E59')||(input.LA(1) >= '\u0ED0' && input.LA(1) <= '\u0ED9')||(input.LA(1) >= '\u1040' && input.LA(1) <= '\u1049') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "JavaIDDigit"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:576:5: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:576:8: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
			{
			if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			_channel=HIDDEN;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WS"

	// $ANTLR start "COMMENT"
	public final void mCOMMENT() throws RecognitionException {
		try {
			int _type = COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:580:2: ( '/*' ( options {greedy=false; } : . )* '*/' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:580:6: '/*' ( options {greedy=false; } : . )* '*/'
			{
			match("/*"); 

			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:580:11: ( options {greedy=false; } : . )*
			loop27:
			while (true) {
				int alt27=2;
				int LA27_0 = input.LA(1);
				if ( (LA27_0=='*') ) {
					int LA27_1 = input.LA(2);
					if ( (LA27_1=='/') ) {
						alt27=2;
					}
					else if ( ((LA27_1 >= '\u0000' && LA27_1 <= '.')||(LA27_1 >= '0' && LA27_1 <= '\uFFFF')) ) {
						alt27=1;
					}

				}
				else if ( ((LA27_0 >= '\u0000' && LA27_0 <= ')')||(LA27_0 >= '+' && LA27_0 <= '\uFFFF')) ) {
					alt27=1;
				}

				switch (alt27) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:580:39: .
					{
					matchAny(); 
					}
					break;

				default :
					break loop27;
				}
			}

			match("*/"); 

			_channel=HIDDEN;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COMMENT"

	// $ANTLR start "LINE_COMMENT"
	public final void mLINE_COMMENT() throws RecognitionException {
		try {
			int _type = LINE_COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:584:2: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:584:4: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
			{
			match("//"); 

			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:584:9: (~ ( '\\n' | '\\r' ) )*
			loop28:
			while (true) {
				int alt28=2;
				int LA28_0 = input.LA(1);
				if ( ((LA28_0 >= '\u0000' && LA28_0 <= '\t')||(LA28_0 >= '\u000B' && LA28_0 <= '\f')||(LA28_0 >= '\u000E' && LA28_0 <= '\uFFFF')) ) {
					alt28=1;
				}

				switch (alt28) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop28;
				}
			}

			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:584:23: ( '\\r' )?
			int alt29=2;
			int LA29_0 = input.LA(1);
			if ( (LA29_0=='\r') ) {
				alt29=1;
			}
			switch (alt29) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:584:23: '\\r'
					{
					match('\r'); 
					}
					break;

			}

			match('\n'); 
			_channel=HIDDEN;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LINE_COMMENT"

	@Override
	public void mTokens() throws RecognitionException {
		// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:8: ( T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | BooleanLiteral | HexLiteral | DecimalLiteral | OctalLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | IDENTIFIER | WS | COMMENT | LINE_COMMENT )
		int alt30=36;
		alt30 = dfa30.predict(input);
		switch (alt30) {
			case 1 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:10: T__24
				{
				mT__24(); 

				}
				break;
			case 2 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:16: T__25
				{
				mT__25(); 

				}
				break;
			case 3 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:22: T__26
				{
				mT__26(); 

				}
				break;
			case 4 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:28: T__27
				{
				mT__27(); 

				}
				break;
			case 5 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:34: T__28
				{
				mT__28(); 

				}
				break;
			case 6 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:40: T__29
				{
				mT__29(); 

				}
				break;
			case 7 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:46: T__30
				{
				mT__30(); 

				}
				break;
			case 8 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:52: T__31
				{
				mT__31(); 

				}
				break;
			case 9 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:58: T__32
				{
				mT__32(); 

				}
				break;
			case 10 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:64: T__33
				{
				mT__33(); 

				}
				break;
			case 11 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:70: T__34
				{
				mT__34(); 

				}
				break;
			case 12 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:76: T__35
				{
				mT__35(); 

				}
				break;
			case 13 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:82: T__36
				{
				mT__36(); 

				}
				break;
			case 14 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:88: T__37
				{
				mT__37(); 

				}
				break;
			case 15 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:94: T__38
				{
				mT__38(); 

				}
				break;
			case 16 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:100: T__39
				{
				mT__39(); 

				}
				break;
			case 17 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:106: T__40
				{
				mT__40(); 

				}
				break;
			case 18 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:112: T__41
				{
				mT__41(); 

				}
				break;
			case 19 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:118: T__42
				{
				mT__42(); 

				}
				break;
			case 20 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:124: T__43
				{
				mT__43(); 

				}
				break;
			case 21 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:130: T__44
				{
				mT__44(); 

				}
				break;
			case 22 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:136: T__45
				{
				mT__45(); 

				}
				break;
			case 23 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:142: T__46
				{
				mT__46(); 

				}
				break;
			case 24 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:148: T__47
				{
				mT__47(); 

				}
				break;
			case 25 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:154: T__48
				{
				mT__48(); 

				}
				break;
			case 26 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:160: BooleanLiteral
				{
				mBooleanLiteral(); 

				}
				break;
			case 27 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:175: HexLiteral
				{
				mHexLiteral(); 

				}
				break;
			case 28 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:186: DecimalLiteral
				{
				mDecimalLiteral(); 

				}
				break;
			case 29 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:201: OctalLiteral
				{
				mOctalLiteral(); 

				}
				break;
			case 30 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:214: FloatingPointLiteral
				{
				mFloatingPointLiteral(); 

				}
				break;
			case 31 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:235: CharacterLiteral
				{
				mCharacterLiteral(); 

				}
				break;
			case 32 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:252: StringLiteral
				{
				mStringLiteral(); 

				}
				break;
			case 33 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:266: IDENTIFIER
				{
				mIDENTIFIER(); 

				}
				break;
			case 34 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:277: WS
				{
				mWS(); 

				}
				break;
			case 35 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:280: COMMENT
				{
				mCOMMENT(); 

				}
				break;
			case 36 :
				// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\javagrammar\\JavaJadex.g:1:288: LINE_COMMENT
				{
				mLINE_COMMENT(); 

				}
				break;

		}
	}


	protected DFA19 dfa19 = new DFA19(this);
	protected DFA30 dfa30 = new DFA30(this);
	static final String DFA19_eotS =
		"\7\uffff\1\10\2\uffff";
	static final String DFA19_eofS =
		"\12\uffff";
	static final String DFA19_minS =
		"\2\56\2\uffff\1\53\1\uffff\2\60\2\uffff";
	static final String DFA19_maxS =
		"\1\71\1\146\2\uffff\1\71\1\uffff\1\71\1\146\2\uffff";
	static final String DFA19_acceptS =
		"\2\uffff\1\2\1\1\1\uffff\1\5\2\uffff\1\4\1\3";
	static final String DFA19_specialS =
		"\12\uffff}>";
	static final String[] DFA19_transitionS = {
			"\1\2\1\uffff\12\1",
			"\1\3\1\uffff\12\1\12\uffff\1\5\1\4\1\5\35\uffff\1\5\1\4\1\5",
			"",
			"",
			"\1\6\1\uffff\1\6\2\uffff\12\7",
			"",
			"\12\7",
			"\12\7\12\uffff\1\11\1\uffff\1\11\35\uffff\1\11\1\uffff\1\11",
			"",
			""
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

	protected class DFA19 extends DFA {

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
		@Override
		public String getDescription() {
			return "488:1: FloatingPointLiteral : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )? | '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )? | ( '0' .. '9' )+ Exponent FloatTypeSuffix | ( '0' .. '9' )+ Exponent | ( '0' .. '9' )+ FloatTypeSuffix );";
		}
	}

	static final String DFA30_eotS =
		"\1\uffff\1\40\10\uffff\1\41\1\45\1\uffff\1\47\1\uffff\1\51\3\uffff\2\35"+
		"\2\uffff\2\35\2\57\17\uffff\4\35\2\uffff\1\66\1\57\4\35\1\uffff\1\35\1"+
		"\74\1\75\2\35\2\uffff\1\75\2\35\1\uffff";
	static final String DFA30_eofS =
		"\102\uffff";
	static final String DFA30_minS =
		"\1\11\1\75\10\uffff\1\60\1\52\1\uffff\1\75\1\uffff\1\75\3\uffff\1\157"+
		"\1\165\2\uffff\1\162\1\141\2\56\17\uffff\2\154\1\165\1\154\2\uffff\2\56"+
		"\2\154\1\145\1\163\1\uffff\1\145\2\44\1\145\1\143\2\uffff\1\44\1\164\1"+
		"\50\1\uffff";
	static final String DFA30_maxS =
		"\1\ufaff\1\75\10\uffff\1\71\1\57\1\uffff\1\75\1\uffff\1\75\3\uffff\1\157"+
		"\1\165\2\uffff\1\162\1\141\1\170\1\146\17\uffff\2\154\1\165\1\154\2\uffff"+
		"\2\146\2\154\1\145\1\163\1\uffff\1\145\2\ufaff\1\145\1\143\2\uffff\1\ufaff"+
		"\1\164\1\50\1\uffff";
	static final String DFA30_acceptS =
		"\2\uffff\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\2\uffff\1\15\1\uffff\1\20"+
		"\1\uffff\1\23\1\24\1\25\2\uffff\1\30\1\31\4\uffff\1\37\1\40\1\41\1\42"+
		"\1\2\1\1\1\13\1\36\1\43\1\44\1\14\1\17\1\16\1\22\1\21\4\uffff\1\33\1\34"+
		"\6\uffff\1\35\5\uffff\1\27\1\32\3\uffff\1\26";
	static final String DFA30_specialS =
		"\102\uffff}>";
	static final String[] DFA30_transitionS = {
			"\2\36\1\uffff\2\36\22\uffff\1\36\1\1\1\34\1\uffff\1\35\1\2\1\3\1\33\1"+
			"\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\31\11\32\1\14\1\uffff\1\15\1\16"+
			"\1\17\1\20\1\uffff\32\35\1\21\1\uffff\1\22\1\uffff\1\35\1\uffff\2\35"+
			"\1\23\2\35\1\30\7\35\1\24\5\35\1\27\6\35\1\uffff\1\25\1\uffff\1\26\101"+
			"\uffff\27\35\1\uffff\37\35\1\uffff\u1f08\35\u1040\uffff\u0150\35\u0170"+
			"\uffff\u0080\35\u0080\uffff\u092e\35\u10d2\uffff\u5200\35\u5900\uffff"+
			"\u0200\35",
			"\1\37",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\12\42",
			"\1\43\4\uffff\1\44",
			"",
			"\1\46",
			"",
			"\1\50",
			"",
			"",
			"",
			"\1\52",
			"\1\53",
			"",
			"",
			"\1\54",
			"\1\55",
			"\1\42\1\uffff\10\60\2\42\12\uffff\3\42\21\uffff\1\56\13\uffff\3\42\21"+
			"\uffff\1\56",
			"\1\42\1\uffff\12\61\12\uffff\3\42\35\uffff\3\42",
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
			"\1\62",
			"\1\63",
			"\1\64",
			"\1\65",
			"",
			"",
			"\1\42\1\uffff\10\60\2\42\12\uffff\3\42\35\uffff\3\42",
			"\1\42\1\uffff\12\61\12\uffff\3\42\35\uffff\3\42",
			"\1\67",
			"\1\70",
			"\1\71",
			"\1\72",
			"",
			"\1\73",
			"\1\35\13\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\32\35\105\uffff"+
			"\27\35\1\uffff\37\35\1\uffff\u1f08\35\u1040\uffff\u0150\35\u0170\uffff"+
			"\u0080\35\u0080\uffff\u092e\35\u10d2\uffff\u5200\35\u5900\uffff\u0200"+
			"\35",
			"\1\35\13\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\32\35\105\uffff"+
			"\27\35\1\uffff\37\35\1\uffff\u1f08\35\u1040\uffff\u0150\35\u0170\uffff"+
			"\u0080\35\u0080\uffff\u092e\35\u10d2\uffff\u5200\35\u5900\uffff\u0200"+
			"\35",
			"\1\76",
			"\1\77",
			"",
			"",
			"\1\35\13\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\32\35\105\uffff"+
			"\27\35\1\uffff\37\35\1\uffff\u1f08\35\u1040\uffff\u0150\35\u0170\uffff"+
			"\u0080\35\u0080\uffff\u092e\35\u10d2\uffff\u5200\35\u5900\uffff\u0200"+
			"\35",
			"\1\100",
			"\1\101",
			""
	};

	static final short[] DFA30_eot = DFA.unpackEncodedString(DFA30_eotS);
	static final short[] DFA30_eof = DFA.unpackEncodedString(DFA30_eofS);
	static final char[] DFA30_min = DFA.unpackEncodedStringToUnsignedChars(DFA30_minS);
	static final char[] DFA30_max = DFA.unpackEncodedStringToUnsignedChars(DFA30_maxS);
	static final short[] DFA30_accept = DFA.unpackEncodedString(DFA30_acceptS);
	static final short[] DFA30_special = DFA.unpackEncodedString(DFA30_specialS);
	static final short[][] DFA30_transition;

	static {
		int numStates = DFA30_transitionS.length;
		DFA30_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA30_transition[i] = DFA.unpackEncodedString(DFA30_transitionS[i]);
		}
	}

	protected class DFA30 extends DFA {

		public DFA30(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 30;
			this.eot = DFA30_eot;
			this.eof = DFA30_eof;
			this.min = DFA30_min;
			this.max = DFA30_max;
			this.accept = DFA30_accept;
			this.special = DFA30_special;
			this.transition = DFA30_transition;
		}
		@Override
		public String getDescription() {
			return "1:1: Tokens : ( T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | BooleanLiteral | HexLiteral | DecimalLiteral | OctalLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | IDENTIFIER | WS | COMMENT | LINE_COMMENT );";
		}
	}

}
