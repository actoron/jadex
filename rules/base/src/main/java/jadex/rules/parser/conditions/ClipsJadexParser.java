// $ANTLR 3.5.1 C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g 2013-12-10 14:02:36

package jadex.rules.parser.conditions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.AndConstraint;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.CollectCondition;
import jadex.rules.rulesystem.rules.Constant;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.OrConstraint;
import jadex.rules.rulesystem.rules.PredicateConstraint;
import jadex.rules.rulesystem.rules.TestCondition;
import jadex.rules.rulesystem.rules.ValueSourceReturnValueConstraint;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.IFunction;
import jadex.rules.rulesystem.rules.functions.MethodCallFunction;
import jadex.rules.rulesystem.rules.functions.OperatorFunction;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;

@SuppressWarnings("all")
public class ClipsJadexParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "BooleanLiteral", "COMMENT", "CharacterLiteral", 
		"ConstraintOperator", "DecimalLiteral", "EscapeSequence", "Exponent", 
		"FloatTypeSuffix", "FloatingPointLiteral", "HexDigit", "HexLiteral", "Identifiertoken", 
		"IntegerTypeSuffix", "JavaIDDigit", "LINE_COMMENT", "Letter", "OctalEscape", 
		"OctalLiteral", "StringLiteral", "UnicodeEscape", "WS", "'!='", "'$?'", 
		"'('", "')'", "'+'", "'-'", "'.'", "':'", "'<'", "'<-'", "'<='", "'='", 
		"'=='", "'>'", "'>='", "'?'", "'['", "']'", "'and'", "'collect'", "'contains'", 
		"'excludes'", "'not'", "'null'", "'test'", "'~'"
	};
	public static final int EOF=-1;
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
	public static final int T__49=49;
	public static final int T__50=50;
	public static final int BooleanLiteral=4;
	public static final int COMMENT=5;
	public static final int CharacterLiteral=6;
	public static final int ConstraintOperator=7;
	public static final int DecimalLiteral=8;
	public static final int EscapeSequence=9;
	public static final int Exponent=10;
	public static final int FloatTypeSuffix=11;
	public static final int FloatingPointLiteral=12;
	public static final int HexDigit=13;
	public static final int HexLiteral=14;
	public static final int Identifiertoken=15;
	public static final int IntegerTypeSuffix=16;
	public static final int JavaIDDigit=17;
	public static final int LINE_COMMENT=18;
	public static final int Letter=19;
	public static final int OctalEscape=20;
	public static final int OctalLiteral=21;
	public static final int StringLiteral=22;
	public static final int UnicodeEscape=23;
	public static final int WS=24;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public ClipsJadexParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public ClipsJadexParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return ClipsJadexParser.tokenNames; }
	@Override public String getGrammarFileName() { return "C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g"; }


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



	// $ANTLR start "rhs"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:60:1: rhs[OAVTypeModel tmodel] returns [ICondition condition] : (c= ce[tmodel, vars] )+ EOF ;
	public final ICondition rhs(OAVTypeModel tmodel) throws RecognitionException {
		ICondition condition = null;


		ICondition c =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:61:2: ( (c= ce[tmodel, vars] )+ EOF )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:62:2: (c= ce[tmodel, vars] )+ EOF
			{

					List conds = new ArrayList();
					Map vars = new HashMap();
					
				
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:67:2: (c= ce[tmodel, vars] )+
			int cnt1=0;
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( ((LA1_0 >= 26 && LA1_0 <= 27)||LA1_0==40) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:67:3: c= ce[tmodel, vars]
					{
					pushFollow(FOLLOW_ce_in_rhs53);
					c=ce(tmodel, vars);
					state._fsp--;


							conds.add(c);
						
					}
					break;

				default :
					if ( cnt1 >= 1 ) break loop1;
					EarlyExitException eee = new EarlyExitException(1, input);
					throw eee;
				}
				cnt1++;
			}


					if(conds.size()>1)
						condition = new AndCondition(conds);
					else
						condition = (ICondition)conds.get(0);
				
			match(input,EOF,FOLLOW_EOF_in_rhs68); 
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return condition;
	}
	// $ANTLR end "rhs"



	// $ANTLR start "ce"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:81:1: ce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : ({...}?tmp= andce[tmodel, vars] |{...}?tmp= notce[tmodel, vars] |{...}?tmp= testce[tmodel, vars] |tmp= collectce[tmodel, vars] |{...}?tmp= objectce[tmodel, vars] );
	public final ICondition ce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
		ICondition condition = null;


		ICondition tmp =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:82:2: ({...}?tmp= andce[tmodel, vars] |{...}?tmp= notce[tmodel, vars] |{...}?tmp= testce[tmodel, vars] |tmp= collectce[tmodel, vars] |{...}?tmp= objectce[tmodel, vars] )
			int alt2=5;
			switch ( input.LA(1) ) {
			case 27:
				{
				int LA2_1 = input.LA(2);
				if ( (("and".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {
					alt2=1;
				}
				else if ( (("not".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {
					alt2=2;
				}
				else if ( (("test".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {
					alt2=3;
				}
				else if ( (!(((("and".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))||("not".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))||(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))||("test".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)))))) ) {
					alt2=4;
				}
				else if ( ((SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {
					alt2=5;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 2, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 26:
				{
				alt2=4;
				}
				break;
			case 40:
				{
				alt2=5;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 2, 0, input);
				throw nvae;
			}
			switch (alt2) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:82:4: {...}?tmp= andce[tmodel, vars]
					{
					if ( !(("and".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {
						throw new FailedPredicateException(input, "ce", "\"and\".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)");
					}
					pushFollow(FOLLOW_andce_in_ce93);
					tmp=andce(tmodel, vars);
					state._fsp--;

					condition = tmp;
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:84:4: {...}?tmp= notce[tmodel, vars]
					{
					if ( !(("not".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {
						throw new FailedPredicateException(input, "ce", "\"not\".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)");
					}
					pushFollow(FOLLOW_notce_in_ce109);
					tmp=notce(tmodel, vars);
					state._fsp--;

					condition = tmp;
					}
					break;
				case 3 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:86:4: {...}?tmp= testce[tmodel, vars]
					{
					if ( !(("test".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {
						throw new FailedPredicateException(input, "ce", "\"test\".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)");
					}
					pushFollow(FOLLOW_testce_in_ce125);
					tmp=testce(tmodel, vars);
					state._fsp--;

					condition = tmp;
					}
					break;
				case 4 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:88:4: tmp= collectce[tmodel, vars]
					{
					pushFollow(FOLLOW_collectce_in_ce137);
					tmp=collectce(tmodel, vars);
					state._fsp--;

					condition = tmp;
					}
					break;
				case 5 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:89:4: {...}?tmp= objectce[tmodel, vars]
					{
					if ( !((SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {
						throw new FailedPredicateException(input, "ce", "SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)");
					}
					pushFollow(FOLLOW_objectce_in_ce151);
					tmp=objectce(tmodel, vars);
					state._fsp--;

					condition = tmp;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return condition;
	}
	// $ANTLR end "ce"



	// $ANTLR start "andce"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:92:1: andce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : '(' 'and' (c= ce[$tmodel, vars] )+ ')' ;
	public final ICondition andce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
		ICondition condition = null;


		ICondition c =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:93:2: ( '(' 'and' (c= ce[$tmodel, vars] )+ ')' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:94:2: '(' 'and' (c= ce[$tmodel, vars] )+ ')'
			{

					List conds = new ArrayList();
				
			match(input,27,FOLLOW_27_in_andce175); 
			match(input,43,FOLLOW_43_in_andce177); 
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:97:12: (c= ce[$tmodel, vars] )+
			int cnt3=0;
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( ((LA3_0 >= 26 && LA3_0 <= 27)||LA3_0==40) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:97:13: c= ce[$tmodel, vars]
					{
					pushFollow(FOLLOW_ce_in_andce182);
					c=ce(tmodel, vars);
					state._fsp--;


							conds.add(c);
						
					}
					break;

				default :
					if ( cnt3 >= 1 ) break loop3;
					EarlyExitException eee = new EarlyExitException(3, input);
					throw eee;
				}
				cnt3++;
			}

			match(input,28,FOLLOW_28_in_andce192); 

					condition = new AndCondition(conds);
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return condition;
	}
	// $ANTLR end "andce"



	// $ANTLR start "notce"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:107:1: notce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : '(' 'not' c= ce[$tmodel, vars] ')' ;
	public final ICondition notce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
		ICondition condition = null;


		ICondition c =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:108:2: ( '(' 'not' c= ce[$tmodel, vars] ')' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:108:4: '(' 'not' c= ce[$tmodel, vars] ')'
			{
			match(input,27,FOLLOW_27_in_notce211); 
			match(input,47,FOLLOW_47_in_notce213); 
			pushFollow(FOLLOW_ce_in_notce217);
			c=ce(tmodel, vars);
			state._fsp--;

			match(input,28,FOLLOW_28_in_notce220); 

					condition = new NotCondition(c);
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return condition;
	}
	// $ANTLR end "notce"



	// $ANTLR start "testce"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:114:1: testce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : '(' 'test' (call= operatorCall[tmodel, vars] | ({...}?call= functionCall[tmodel, vars] ) ) ')' ;
	public final ICondition testce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
		ICondition condition = null;


		FunctionCall call =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:115:2: ( '(' 'test' (call= operatorCall[tmodel, vars] | ({...}?call= functionCall[tmodel, vars] ) ) ')' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:115:4: '(' 'test' (call= operatorCall[tmodel, vars] | ({...}?call= functionCall[tmodel, vars] ) ) ')'
			{
			match(input,27,FOLLOW_27_in_testce239); 
			match(input,49,FOLLOW_49_in_testce241); 
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:116:3: (call= operatorCall[tmodel, vars] | ({...}?call= functionCall[tmodel, vars] ) )
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( (LA4_0==27) ) {
				int LA4_1 = input.LA(2);
				if ( (!(((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))))) ) {
					alt4=1;
				}
				else if ( ((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {
					alt4=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 4, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 4, 0, input);
				throw nvae;
			}

			switch (alt4) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:116:5: call= operatorCall[tmodel, vars]
					{
					pushFollow(FOLLOW_operatorCall_in_testce249);
					call=operatorCall(tmodel, vars);
					state._fsp--;

					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:117:5: ({...}?call= functionCall[tmodel, vars] )
					{
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:117:5: ({...}?call= functionCall[tmodel, vars] )
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:117:6: {...}?call= functionCall[tmodel, vars]
					{
					if ( !((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {
						throw new FailedPredicateException(input, "testce", "SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)");
					}
					pushFollow(FOLLOW_functionCall_in_testce261);
					call=functionCall(tmodel, vars);
					state._fsp--;

					}

					}
					break;

			}

			match(input,28,FOLLOW_28_in_testce269); 

					condition = new TestCondition(new PredicateConstraint(call));
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return condition;
	}
	// $ANTLR end "testce"



	// $ANTLR start "collectce"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:124:1: collectce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )? '(' 'collect' (c= ce[$tmodel, vars] )+ (pc= predicateConstraint[$tmodel, null, vars] )? ')' ;
	public final ICondition collectce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
		ICondition condition = null;


		Variable mfv =null;
		ICondition c =null;
		IConstraint pc =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:125:2: ( (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )? '(' 'collect' (c= ce[$tmodel, vars] )+ (pc= predicateConstraint[$tmodel, null, vars] )? ')' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:126:2: (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )? '(' 'collect' (c= ce[$tmodel, vars] )+ (pc= predicateConstraint[$tmodel, null, vars] )? ')'
			{

					List conds = new ArrayList();
				
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:129:2: (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )?
			int alt5=2;
			int LA5_0 = input.LA(1);
			if ( (LA5_0==26) ) {
				alt5=1;
			}
			switch (alt5) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:129:3: mfv= multiFieldVariable[null, vars] ( '<-' | '=' )
					{
					pushFollow(FOLLOW_multiFieldVariable_in_collectce296);
					mfv=multiFieldVariable(null, vars);
					state._fsp--;

					if ( input.LA(1)==34||input.LA(1)==36 ) {
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

			match(input,27,FOLLOW_27_in_collectce311); 
			match(input,44,FOLLOW_44_in_collectce313); 
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:130:16: (c= ce[$tmodel, vars] )+
			int cnt6=0;
			loop6:
			while (true) {
				int alt6=2;
				int LA6_0 = input.LA(1);
				if ( ((LA6_0 >= 26 && LA6_0 <= 27)||LA6_0==40) ) {
					alt6=1;
				}

				switch (alt6) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:130:17: c= ce[$tmodel, vars]
					{
					pushFollow(FOLLOW_ce_in_collectce318);
					c=ce(tmodel, vars);
					state._fsp--;


							conds.add(c);
						
					}
					break;

				default :
					if ( cnt6 >= 1 ) break loop6;
					EarlyExitException eee = new EarlyExitException(6, input);
					throw eee;
				}
				cnt6++;
			}

			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:134:8: (pc= predicateConstraint[$tmodel, null, vars] )?
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0==32) ) {
				alt7=1;
			}
			switch (alt7) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:134:8: pc= predicateConstraint[$tmodel, null, vars]
					{
					pushFollow(FOLLOW_predicateConstraint_in_collectce331);
					pc=predicateConstraint(tmodel, null, vars);
					state._fsp--;

					}
					break;

			}

			match(input,28,FOLLOW_28_in_collectce335); 

					ObjectCondition first = (ObjectCondition)conds.get(0);
					mfv.setType(first.getObjectType());
					List consts = new ArrayList();
					consts.add(new BoundConstraint(null, mfv));
					if(pc!=null)
						consts.add(pc);
					CollectCondition ccond = new CollectCondition(conds, consts);
					condition = ccond;
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return condition;
	}
	// $ANTLR end "collectce"



	// $ANTLR start "objectce"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:148:1: objectce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )? '(' tn= typename (acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] |mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] |fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] )* ')' ;
	public final ICondition objectce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
		ICondition condition = null;


		Variable sfv =null;
		String tn =null;
		List acs =null;
		List mcs =null;
		List fcs =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:149:2: ( (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )? '(' tn= typename (acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] |mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] |fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] )* ')' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:150:2: (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )? '(' tn= typename (acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] |mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] |fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] )* ')'
			{

					List consts = new ArrayList();
				
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:153:2: (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )?
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0==40) ) {
				alt8=1;
			}
			switch (alt8) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:153:3: sfv= singleFieldVariable[null, vars] ( '<-' | '=' )
					{
					pushFollow(FOLLOW_singleFieldVariable_in_objectce364);
					sfv=singleFieldVariable(null, vars);
					state._fsp--;

					if ( input.LA(1)==34||input.LA(1)==36 ) {
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

			match(input,27,FOLLOW_27_in_objectce379); 
			pushFollow(FOLLOW_typename_in_objectce385);
			tn=typename();
			state._fsp--;

			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:157:2: (acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] |mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] |fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] )*
			loop9:
			while (true) {
				int alt9=4;
				int LA9_0 = input.LA(1);
				if ( (LA9_0==27) ) {
					switch ( input.LA(2) ) {
					case Identifiertoken:
						{
						int LA9_3 = input.LA(3);
						if ( (LA9_3==BooleanLiteral||LA9_3==CharacterLiteral||LA9_3==DecimalLiteral||LA9_3==FloatingPointLiteral||LA9_3==HexLiteral||(LA9_3 >= OctalLiteral && LA9_3 <= StringLiteral)||(LA9_3 >= 25 && LA9_3 <= 26)||(LA9_3 >= 29 && LA9_3 <= 33)||LA9_3==35||(LA9_3 >= 37 && LA9_3 <= 41)||(LA9_3 >= 45 && LA9_3 <= 46)||LA9_3==48||LA9_3==50) ) {
							alt9=1;
						}
						else if ( (LA9_3==27) ) {
							alt9=2;
						}

						}
						break;
					case 49:
						{
						int LA9_4 = input.LA(3);
						if ( (LA9_4==BooleanLiteral||LA9_4==CharacterLiteral||LA9_4==DecimalLiteral||LA9_4==FloatingPointLiteral||LA9_4==HexLiteral||(LA9_4 >= OctalLiteral && LA9_4 <= StringLiteral)||(LA9_4 >= 25 && LA9_4 <= 26)||(LA9_4 >= 29 && LA9_4 <= 33)||LA9_4==35||(LA9_4 >= 37 && LA9_4 <= 41)||(LA9_4 >= 45 && LA9_4 <= 46)||LA9_4==48||LA9_4==50) ) {
							alt9=1;
						}
						else if ( (LA9_4==27) ) {
							alt9=2;
						}

						}
						break;
					case 47:
						{
						int LA9_5 = input.LA(3);
						if ( (LA9_5==BooleanLiteral||LA9_5==CharacterLiteral||LA9_5==DecimalLiteral||LA9_5==FloatingPointLiteral||LA9_5==HexLiteral||(LA9_5 >= OctalLiteral && LA9_5 <= StringLiteral)||(LA9_5 >= 25 && LA9_5 <= 26)||(LA9_5 >= 29 && LA9_5 <= 33)||LA9_5==35||(LA9_5 >= 37 && LA9_5 <= 41)||(LA9_5 >= 45 && LA9_5 <= 46)||LA9_5==48||LA9_5==50) ) {
							alt9=1;
						}
						else if ( (LA9_5==27) ) {
							alt9=2;
						}

						}
						break;
					case 43:
						{
						int LA9_6 = input.LA(3);
						if ( (LA9_6==BooleanLiteral||LA9_6==CharacterLiteral||LA9_6==DecimalLiteral||LA9_6==FloatingPointLiteral||LA9_6==HexLiteral||(LA9_6 >= OctalLiteral && LA9_6 <= StringLiteral)||(LA9_6 >= 25 && LA9_6 <= 26)||(LA9_6 >= 29 && LA9_6 <= 33)||LA9_6==35||(LA9_6 >= 37 && LA9_6 <= 41)||(LA9_6 >= 45 && LA9_6 <= 46)||LA9_6==48||LA9_6==50) ) {
							alt9=1;
						}
						else if ( (LA9_6==27) ) {
							alt9=2;
						}

						}
						break;
					case 44:
						{
						int LA9_7 = input.LA(3);
						if ( (LA9_7==BooleanLiteral||LA9_7==CharacterLiteral||LA9_7==DecimalLiteral||LA9_7==FloatingPointLiteral||LA9_7==HexLiteral||(LA9_7 >= OctalLiteral && LA9_7 <= StringLiteral)||(LA9_7 >= 25 && LA9_7 <= 26)||(LA9_7 >= 29 && LA9_7 <= 33)||LA9_7==35||(LA9_7 >= 37 && LA9_7 <= 41)||(LA9_7 >= 45 && LA9_7 <= 46)||LA9_7==48||LA9_7==50) ) {
							alt9=1;
						}
						else if ( (LA9_7==27) ) {
							alt9=2;
						}

						}
						break;
					case 45:
						{
						int LA9_8 = input.LA(3);
						if ( (LA9_8==BooleanLiteral||LA9_8==CharacterLiteral||LA9_8==DecimalLiteral||LA9_8==FloatingPointLiteral||LA9_8==HexLiteral||(LA9_8 >= OctalLiteral && LA9_8 <= StringLiteral)||(LA9_8 >= 25 && LA9_8 <= 26)||(LA9_8 >= 29 && LA9_8 <= 33)||LA9_8==35||(LA9_8 >= 37 && LA9_8 <= 41)||(LA9_8 >= 45 && LA9_8 <= 46)||LA9_8==48||LA9_8==50) ) {
							alt9=1;
						}
						else if ( (LA9_8==27) ) {
							alt9=2;
						}

						}
						break;
					case 46:
						{
						int LA9_9 = input.LA(3);
						if ( (LA9_9==BooleanLiteral||LA9_9==CharacterLiteral||LA9_9==DecimalLiteral||LA9_9==FloatingPointLiteral||LA9_9==HexLiteral||(LA9_9 >= OctalLiteral && LA9_9 <= StringLiteral)||(LA9_9 >= 25 && LA9_9 <= 26)||(LA9_9 >= 29 && LA9_9 <= 33)||LA9_9==35||(LA9_9 >= 37 && LA9_9 <= 41)||(LA9_9 >= 45 && LA9_9 <= 46)||LA9_9==48||LA9_9==50) ) {
							alt9=1;
						}
						else if ( (LA9_9==27) ) {
							alt9=2;
						}

						}
						break;
					case 27:
						{
						alt9=3;
						}
						break;
					}
				}

				switch (alt9) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:157:3: acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars]
					{
					pushFollow(FOLLOW_attributeConstraint_in_objectce394);
					acs=attributeConstraint(tmodel, SConditions.getObjectType(tmodel, tn, imports), vars);
					state._fsp--;


							consts.addAll(acs);
						
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:161:4: mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars]
					{
					pushFollow(FOLLOW_methodConstraint_in_objectce405);
					mcs=methodConstraint(tmodel, SConditions.getObjectType(tmodel, tn, imports), vars);
					state._fsp--;


							consts.addAll(mcs);
						
					}
					break;
				case 3 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:165:4: fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars]
					{
					pushFollow(FOLLOW_functionConstraint_in_objectce416);
					fcs=functionConstraint(tmodel, SConditions.getObjectType(tmodel, tn, imports), vars);
					state._fsp--;


							consts.addAll(fcs);
						
					}
					break;

				default :
					break loop9;
				}
			}

			match(input,28,FOLLOW_28_in_objectce428); 

					// Set variable type if still unknown/unprecise
					if(sfv!=null)
						SConditions.adaptConditionType(sfv, SConditions.getObjectType(tmodel, tn, imports));
					
					OAVObjectType otype = SConditions.getObjectType(tmodel, tn, imports);
					ObjectCondition ocond = new ObjectCondition(otype, consts);
					if(sfv!=null)
						ocond.addConstraint(new BoundConstraint(null, sfv));
					condition = ocond;
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return condition;
	}
	// $ANTLR end "objectce"



	// $ANTLR start "attributeConstraint"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:185:1: attributeConstraint[OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints] : '(' sn= slotname cs= constraint[tmodel, SConditions.convertAttributeTypes(tmodel, otype, sn, imports), vars] ')' ;
	public final List attributeConstraint(OAVTypeModel tmodel, OAVObjectType otype, Map vars) throws RecognitionException {
		List constraints = null;


		String sn =null;
		List cs =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:186:2: ( '(' sn= slotname cs= constraint[tmodel, SConditions.convertAttributeTypes(tmodel, otype, sn, imports), vars] ')' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:186:4: '(' sn= slotname cs= constraint[tmodel, SConditions.convertAttributeTypes(tmodel, otype, sn, imports), vars] ')'
			{
			match(input,27,FOLLOW_27_in_attributeConstraint450); 
			pushFollow(FOLLOW_slotname_in_attributeConstraint454);
			sn=slotname();
			state._fsp--;

			pushFollow(FOLLOW_constraint_in_attributeConstraint458);
			cs=constraint(tmodel, SConditions.convertAttributeTypes(tmodel, otype, sn, imports), vars);
			state._fsp--;

			match(input,28,FOLLOW_28_in_attributeConstraint461); 

					constraints = cs;
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return constraints;
	}
	// $ANTLR end "attributeConstraint"



	// $ANTLR start "methodConstraint"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:192:1: methodConstraint[OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints] : '(' mn= methodname '(' (exp= parameter[tmodel, vars] )* ')' cs= constraint[tmodel, SConditions.createMethodCall(otype, mn, exps), vars] ')' ;
	public final List methodConstraint(OAVTypeModel tmodel, OAVObjectType otype, Map vars) throws RecognitionException {
		List constraints = null;


		String mn =null;
		Object exp =null;
		List cs =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:193:2: ( '(' mn= methodname '(' (exp= parameter[tmodel, vars] )* ')' cs= constraint[tmodel, SConditions.createMethodCall(otype, mn, exps), vars] ')' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:194:2: '(' mn= methodname '(' (exp= parameter[tmodel, vars] )* ')' cs= constraint[tmodel, SConditions.createMethodCall(otype, mn, exps), vars] ')'
			{

					List exps = new ArrayList();
				
			match(input,27,FOLLOW_27_in_methodConstraint488); 
			pushFollow(FOLLOW_methodname_in_methodConstraint492);
			mn=methodname();
			state._fsp--;

			match(input,27,FOLLOW_27_in_methodConstraint494); 
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:197:23: (exp= parameter[tmodel, vars] )*
			loop10:
			while (true) {
				int alt10=2;
				int LA10_0 = input.LA(1);
				if ( (LA10_0==BooleanLiteral||LA10_0==CharacterLiteral||LA10_0==DecimalLiteral||LA10_0==FloatingPointLiteral||LA10_0==HexLiteral||(LA10_0 >= OctalLiteral && LA10_0 <= StringLiteral)||(LA10_0 >= 26 && LA10_0 <= 27)||(LA10_0 >= 29 && LA10_0 <= 30)||LA10_0==40||LA10_0==48) ) {
					alt10=1;
				}

				switch (alt10) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:197:24: exp= parameter[tmodel, vars]
					{
					pushFollow(FOLLOW_parameter_in_methodConstraint498);
					exp=parameter(tmodel, vars);
					state._fsp--;


							exps.add(exp);
						
					}
					break;

				default :
					break loop10;
				}
			}

			match(input,28,FOLLOW_28_in_methodConstraint508); 
			pushFollow(FOLLOW_constraint_in_methodConstraint512);
			cs=constraint(tmodel, SConditions.createMethodCall(otype, mn, exps), vars);
			state._fsp--;

			match(input,28,FOLLOW_28_in_methodConstraint515); 

					constraints = cs;
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return constraints;
	}
	// $ANTLR end "methodConstraint"



	// $ANTLR start "functionConstraint"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:207:1: functionConstraint[OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints] : '(' fc= functionCall[tmodel, vars] cs= constraint[tmodel, fc, vars] ')' ;
	public final List functionConstraint(OAVTypeModel tmodel, OAVObjectType otype, Map vars) throws RecognitionException {
		List constraints = null;


		FunctionCall fc =null;
		List cs =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:208:2: ( '(' fc= functionCall[tmodel, vars] cs= constraint[tmodel, fc, vars] ')' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:209:2: '(' fc= functionCall[tmodel, vars] cs= constraint[tmodel, fc, vars] ')'
			{
			match(input,27,FOLLOW_27_in_functionConstraint539); 
			pushFollow(FOLLOW_functionCall_in_functionConstraint543);
			fc=functionCall(tmodel, vars);
			state._fsp--;

			pushFollow(FOLLOW_constraint_in_functionConstraint548);
			cs=constraint(tmodel, fc, vars);
			state._fsp--;

			match(input,28,FOLLOW_28_in_functionConstraint551); 

					constraints = cs;
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return constraints;
	}
	// $ANTLR end "functionConstraint"



	// $ANTLR start "constraint"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:215:1: constraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [List constraints] : ( '?' | '$?' |last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )* );
	public final List constraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
		List constraints = null;


		Token ConstraintOperator1=null;
		IConstraint last =null;
		IConstraint next =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:216:2: ( '?' | '$?' |last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )* )
			int alt12=3;
			switch ( input.LA(1) ) {
			case 40:
				{
				int LA12_1 = input.LA(2);
				if ( (LA12_1==28) ) {
					alt12=1;
				}
				else if ( (LA12_1==Identifiertoken||(LA12_1 >= 43 && LA12_1 <= 47)||LA12_1==49) ) {
					alt12=3;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 12, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 26:
				{
				int LA12_2 = input.LA(2);
				if ( (LA12_2==28) ) {
					alt12=2;
				}
				else if ( (LA12_2==Identifiertoken||(LA12_2 >= 43 && LA12_2 <= 47)||LA12_2==49) ) {
					alt12=3;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 12, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case BooleanLiteral:
			case CharacterLiteral:
			case DecimalLiteral:
			case FloatingPointLiteral:
			case HexLiteral:
			case OctalLiteral:
			case StringLiteral:
			case 25:
			case 29:
			case 30:
			case 32:
			case 33:
			case 35:
			case 37:
			case 38:
			case 39:
			case 45:
			case 46:
			case 48:
			case 50:
				{
				alt12=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 12, 0, input);
				throw nvae;
			}
			switch (alt12) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:220:2: '?'
					{
					match(input,40,FOLLOW_40_in_constraint581); 
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:221:4: '$?'
					{
					match(input,26,FOLLOW_26_in_constraint587); 
					}
					break;
				case 3 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:222:4: last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )*
					{
					pushFollow(FOLLOW_singleConstraint_in_constraint595);
					last=singleConstraint(tmodel, valuesource, vars);
					state._fsp--;


							List ret = new ArrayList();
							List consts = new ArrayList();
							String op = null;
							if(last instanceof BoundConstraint)
								ret.add(last);
							else
								consts.add(last);
						
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:232:2: ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )*
					loop11:
					while (true) {
						int alt11=2;
						int LA11_0 = input.LA(1);
						if ( (LA11_0==ConstraintOperator) ) {
							alt11=1;
						}

						switch (alt11) {
						case 1 :
							// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:232:3: ConstraintOperator next= singleConstraint[tmodel, valuesource, vars]
							{
							ConstraintOperator1=(Token)match(input,ConstraintOperator,FOLLOW_ConstraintOperator_in_constraint604); 
							pushFollow(FOLLOW_singleConstraint_in_constraint608);
							next=singleConstraint(tmodel, valuesource, vars);
							state._fsp--;


									// Set op if first occurrence
									if(op==null)
									{
										op = (ConstraintOperator1!=null?ConstraintOperator1.getText():null);
									}
								
									consts.add(next);
									if(consts.size()>1)
									{	
										if(!SUtil.equals((ConstraintOperator1!=null?ConstraintOperator1.getText():null), op))
										{
											if(op.equals("&"))
												last = new AndConstraint((IConstraint[])consts.toArray(new IConstraint[consts.size()]));
											else
												last = new OrConstraint((IConstraint[])consts.toArray(new IConstraint[consts.size()]));
											
											op = (ConstraintOperator1!=null?ConstraintOperator1.getText():null);	
											consts.clear();
											consts.add(last);
										}
									}
								
							}
							break;

						default :
							break loop11;
						}
					}


							if(consts.size()>1)
							{
								if(op.equals("&"))
									ret.add(new AndConstraint((IConstraint[])consts.toArray(new IConstraint[consts.size()])));
								else
									ret.add(new OrConstraint((IConstraint[])consts.toArray(new IConstraint[consts.size()])));
							}
							else if(consts.size()==1)
							{
								ret.add(consts.get(0));
							}
								
							constraints = ret;
						
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return constraints;
	}
	// $ANTLR end "constraint"



	// $ANTLR start "singleConstraint"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:274:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] |tmp= boundConstraint[tmodel, valuesource, vars] |tmp= multiBoundConstraint[tmodel, valuesource, vars] |tmp= predicateConstraint[tmodel, valuesource, vars] |tmp= returnValueConstraint[tmodel, valuesource, vars] );
	public final IConstraint singleConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
		IConstraint constraint = null;


		IConstraint tmp =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:275:2: (tmp= literalConstraint[valuesource] |tmp= boundConstraint[tmodel, valuesource, vars] |tmp= multiBoundConstraint[tmodel, valuesource, vars] |tmp= predicateConstraint[tmodel, valuesource, vars] |tmp= returnValueConstraint[tmodel, valuesource, vars] )
			int alt13=5;
			switch ( input.LA(1) ) {
			case 37:
				{
				switch ( input.LA(2) ) {
				case BooleanLiteral:
				case CharacterLiteral:
				case DecimalLiteral:
				case FloatingPointLiteral:
				case HexLiteral:
				case OctalLiteral:
				case StringLiteral:
				case 29:
				case 30:
				case 48:
					{
					alt13=1;
					}
					break;
				case 40:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_15 = input.LA(4);
						if ( (LA13_15==ConstraintOperator||LA13_15==28) ) {
							alt13=2;
						}
						else if ( (LA13_15==26||LA13_15==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_16 = input.LA(4);
						if ( (LA13_16==ConstraintOperator||LA13_16==28) ) {
							alt13=2;
						}
						else if ( (LA13_16==26||LA13_16==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_17 = input.LA(4);
						if ( (LA13_17==ConstraintOperator||LA13_17==28) ) {
							alt13=2;
						}
						else if ( (LA13_17==26||LA13_17==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_18 = input.LA(4);
						if ( (LA13_18==ConstraintOperator||LA13_18==28) ) {
							alt13=2;
						}
						else if ( (LA13_18==26||LA13_18==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_19 = input.LA(4);
						if ( (LA13_19==ConstraintOperator||LA13_19==28) ) {
							alt13=2;
						}
						else if ( (LA13_19==26||LA13_19==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_20 = input.LA(4);
						if ( (LA13_20==ConstraintOperator||LA13_20==28) ) {
							alt13=2;
						}
						else if ( (LA13_20==26||LA13_20==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_21 = input.LA(4);
						if ( (LA13_21==ConstraintOperator||LA13_21==28) ) {
							alt13=2;
						}
						else if ( (LA13_21==26||LA13_21==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case 26:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_22 = input.LA(4);
						if ( (LA13_22==ConstraintOperator||LA13_22==28) ) {
							alt13=2;
						}
						else if ( (LA13_22==26||LA13_22==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_23 = input.LA(4);
						if ( (LA13_23==ConstraintOperator||LA13_23==28) ) {
							alt13=2;
						}
						else if ( (LA13_23==26||LA13_23==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_24 = input.LA(4);
						if ( (LA13_24==ConstraintOperator||LA13_24==28) ) {
							alt13=2;
						}
						else if ( (LA13_24==26||LA13_24==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_25 = input.LA(4);
						if ( (LA13_25==ConstraintOperator||LA13_25==28) ) {
							alt13=2;
						}
						else if ( (LA13_25==26||LA13_25==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_26 = input.LA(4);
						if ( (LA13_26==ConstraintOperator||LA13_26==28) ) {
							alt13=2;
						}
						else if ( (LA13_26==26||LA13_26==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 26, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_27 = input.LA(4);
						if ( (LA13_27==ConstraintOperator||LA13_27==28) ) {
							alt13=2;
						}
						else if ( (LA13_27==26||LA13_27==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 27, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_28 = input.LA(4);
						if ( (LA13_28==ConstraintOperator||LA13_28==28) ) {
							alt13=2;
						}
						else if ( (LA13_28==26||LA13_28==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 28, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 12, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case 27:
					{
					alt13=5;
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 13, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 25:
				{
				switch ( input.LA(2) ) {
				case BooleanLiteral:
				case CharacterLiteral:
				case DecimalLiteral:
				case FloatingPointLiteral:
				case HexLiteral:
				case OctalLiteral:
				case StringLiteral:
				case 29:
				case 30:
				case 48:
					{
					alt13=1;
					}
					break;
				case 40:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_15 = input.LA(4);
						if ( (LA13_15==ConstraintOperator||LA13_15==28) ) {
							alt13=2;
						}
						else if ( (LA13_15==26||LA13_15==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_16 = input.LA(4);
						if ( (LA13_16==ConstraintOperator||LA13_16==28) ) {
							alt13=2;
						}
						else if ( (LA13_16==26||LA13_16==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_17 = input.LA(4);
						if ( (LA13_17==ConstraintOperator||LA13_17==28) ) {
							alt13=2;
						}
						else if ( (LA13_17==26||LA13_17==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_18 = input.LA(4);
						if ( (LA13_18==ConstraintOperator||LA13_18==28) ) {
							alt13=2;
						}
						else if ( (LA13_18==26||LA13_18==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_19 = input.LA(4);
						if ( (LA13_19==ConstraintOperator||LA13_19==28) ) {
							alt13=2;
						}
						else if ( (LA13_19==26||LA13_19==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_20 = input.LA(4);
						if ( (LA13_20==ConstraintOperator||LA13_20==28) ) {
							alt13=2;
						}
						else if ( (LA13_20==26||LA13_20==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_21 = input.LA(4);
						if ( (LA13_21==ConstraintOperator||LA13_21==28) ) {
							alt13=2;
						}
						else if ( (LA13_21==26||LA13_21==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case 26:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_22 = input.LA(4);
						if ( (LA13_22==ConstraintOperator||LA13_22==28) ) {
							alt13=2;
						}
						else if ( (LA13_22==26||LA13_22==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_23 = input.LA(4);
						if ( (LA13_23==ConstraintOperator||LA13_23==28) ) {
							alt13=2;
						}
						else if ( (LA13_23==26||LA13_23==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_24 = input.LA(4);
						if ( (LA13_24==ConstraintOperator||LA13_24==28) ) {
							alt13=2;
						}
						else if ( (LA13_24==26||LA13_24==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_25 = input.LA(4);
						if ( (LA13_25==ConstraintOperator||LA13_25==28) ) {
							alt13=2;
						}
						else if ( (LA13_25==26||LA13_25==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_26 = input.LA(4);
						if ( (LA13_26==ConstraintOperator||LA13_26==28) ) {
							alt13=2;
						}
						else if ( (LA13_26==26||LA13_26==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 26, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_27 = input.LA(4);
						if ( (LA13_27==ConstraintOperator||LA13_27==28) ) {
							alt13=2;
						}
						else if ( (LA13_27==26||LA13_27==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 27, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_28 = input.LA(4);
						if ( (LA13_28==ConstraintOperator||LA13_28==28) ) {
							alt13=2;
						}
						else if ( (LA13_28==26||LA13_28==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 28, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 12, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 13, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 50:
				{
				switch ( input.LA(2) ) {
				case BooleanLiteral:
				case CharacterLiteral:
				case DecimalLiteral:
				case FloatingPointLiteral:
				case HexLiteral:
				case OctalLiteral:
				case StringLiteral:
				case 29:
				case 30:
				case 48:
					{
					alt13=1;
					}
					break;
				case 40:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_15 = input.LA(4);
						if ( (LA13_15==ConstraintOperator||LA13_15==28) ) {
							alt13=2;
						}
						else if ( (LA13_15==26||LA13_15==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_16 = input.LA(4);
						if ( (LA13_16==ConstraintOperator||LA13_16==28) ) {
							alt13=2;
						}
						else if ( (LA13_16==26||LA13_16==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_17 = input.LA(4);
						if ( (LA13_17==ConstraintOperator||LA13_17==28) ) {
							alt13=2;
						}
						else if ( (LA13_17==26||LA13_17==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_18 = input.LA(4);
						if ( (LA13_18==ConstraintOperator||LA13_18==28) ) {
							alt13=2;
						}
						else if ( (LA13_18==26||LA13_18==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_19 = input.LA(4);
						if ( (LA13_19==ConstraintOperator||LA13_19==28) ) {
							alt13=2;
						}
						else if ( (LA13_19==26||LA13_19==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_20 = input.LA(4);
						if ( (LA13_20==ConstraintOperator||LA13_20==28) ) {
							alt13=2;
						}
						else if ( (LA13_20==26||LA13_20==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_21 = input.LA(4);
						if ( (LA13_21==ConstraintOperator||LA13_21==28) ) {
							alt13=2;
						}
						else if ( (LA13_21==26||LA13_21==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case 26:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_22 = input.LA(4);
						if ( (LA13_22==ConstraintOperator||LA13_22==28) ) {
							alt13=2;
						}
						else if ( (LA13_22==26||LA13_22==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_23 = input.LA(4);
						if ( (LA13_23==ConstraintOperator||LA13_23==28) ) {
							alt13=2;
						}
						else if ( (LA13_23==26||LA13_23==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_24 = input.LA(4);
						if ( (LA13_24==ConstraintOperator||LA13_24==28) ) {
							alt13=2;
						}
						else if ( (LA13_24==26||LA13_24==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_25 = input.LA(4);
						if ( (LA13_25==ConstraintOperator||LA13_25==28) ) {
							alt13=2;
						}
						else if ( (LA13_25==26||LA13_25==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_26 = input.LA(4);
						if ( (LA13_26==ConstraintOperator||LA13_26==28) ) {
							alt13=2;
						}
						else if ( (LA13_26==26||LA13_26==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 26, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_27 = input.LA(4);
						if ( (LA13_27==ConstraintOperator||LA13_27==28) ) {
							alt13=2;
						}
						else if ( (LA13_27==26||LA13_27==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 27, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_28 = input.LA(4);
						if ( (LA13_28==ConstraintOperator||LA13_28==28) ) {
							alt13=2;
						}
						else if ( (LA13_28==26||LA13_28==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 28, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 12, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 13, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 38:
				{
				switch ( input.LA(2) ) {
				case BooleanLiteral:
				case CharacterLiteral:
				case DecimalLiteral:
				case FloatingPointLiteral:
				case HexLiteral:
				case OctalLiteral:
				case StringLiteral:
				case 29:
				case 30:
				case 48:
					{
					alt13=1;
					}
					break;
				case 40:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_15 = input.LA(4);
						if ( (LA13_15==ConstraintOperator||LA13_15==28) ) {
							alt13=2;
						}
						else if ( (LA13_15==26||LA13_15==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_16 = input.LA(4);
						if ( (LA13_16==ConstraintOperator||LA13_16==28) ) {
							alt13=2;
						}
						else if ( (LA13_16==26||LA13_16==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_17 = input.LA(4);
						if ( (LA13_17==ConstraintOperator||LA13_17==28) ) {
							alt13=2;
						}
						else if ( (LA13_17==26||LA13_17==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_18 = input.LA(4);
						if ( (LA13_18==ConstraintOperator||LA13_18==28) ) {
							alt13=2;
						}
						else if ( (LA13_18==26||LA13_18==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_19 = input.LA(4);
						if ( (LA13_19==ConstraintOperator||LA13_19==28) ) {
							alt13=2;
						}
						else if ( (LA13_19==26||LA13_19==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_20 = input.LA(4);
						if ( (LA13_20==ConstraintOperator||LA13_20==28) ) {
							alt13=2;
						}
						else if ( (LA13_20==26||LA13_20==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_21 = input.LA(4);
						if ( (LA13_21==ConstraintOperator||LA13_21==28) ) {
							alt13=2;
						}
						else if ( (LA13_21==26||LA13_21==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case 26:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_22 = input.LA(4);
						if ( (LA13_22==ConstraintOperator||LA13_22==28) ) {
							alt13=2;
						}
						else if ( (LA13_22==26||LA13_22==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_23 = input.LA(4);
						if ( (LA13_23==ConstraintOperator||LA13_23==28) ) {
							alt13=2;
						}
						else if ( (LA13_23==26||LA13_23==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_24 = input.LA(4);
						if ( (LA13_24==ConstraintOperator||LA13_24==28) ) {
							alt13=2;
						}
						else if ( (LA13_24==26||LA13_24==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_25 = input.LA(4);
						if ( (LA13_25==ConstraintOperator||LA13_25==28) ) {
							alt13=2;
						}
						else if ( (LA13_25==26||LA13_25==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_26 = input.LA(4);
						if ( (LA13_26==ConstraintOperator||LA13_26==28) ) {
							alt13=2;
						}
						else if ( (LA13_26==26||LA13_26==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 26, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_27 = input.LA(4);
						if ( (LA13_27==ConstraintOperator||LA13_27==28) ) {
							alt13=2;
						}
						else if ( (LA13_27==26||LA13_27==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 27, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_28 = input.LA(4);
						if ( (LA13_28==ConstraintOperator||LA13_28==28) ) {
							alt13=2;
						}
						else if ( (LA13_28==26||LA13_28==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 28, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 12, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 13, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 33:
				{
				switch ( input.LA(2) ) {
				case BooleanLiteral:
				case CharacterLiteral:
				case DecimalLiteral:
				case FloatingPointLiteral:
				case HexLiteral:
				case OctalLiteral:
				case StringLiteral:
				case 29:
				case 30:
				case 48:
					{
					alt13=1;
					}
					break;
				case 40:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_15 = input.LA(4);
						if ( (LA13_15==ConstraintOperator||LA13_15==28) ) {
							alt13=2;
						}
						else if ( (LA13_15==26||LA13_15==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_16 = input.LA(4);
						if ( (LA13_16==ConstraintOperator||LA13_16==28) ) {
							alt13=2;
						}
						else if ( (LA13_16==26||LA13_16==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_17 = input.LA(4);
						if ( (LA13_17==ConstraintOperator||LA13_17==28) ) {
							alt13=2;
						}
						else if ( (LA13_17==26||LA13_17==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_18 = input.LA(4);
						if ( (LA13_18==ConstraintOperator||LA13_18==28) ) {
							alt13=2;
						}
						else if ( (LA13_18==26||LA13_18==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_19 = input.LA(4);
						if ( (LA13_19==ConstraintOperator||LA13_19==28) ) {
							alt13=2;
						}
						else if ( (LA13_19==26||LA13_19==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_20 = input.LA(4);
						if ( (LA13_20==ConstraintOperator||LA13_20==28) ) {
							alt13=2;
						}
						else if ( (LA13_20==26||LA13_20==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_21 = input.LA(4);
						if ( (LA13_21==ConstraintOperator||LA13_21==28) ) {
							alt13=2;
						}
						else if ( (LA13_21==26||LA13_21==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case 26:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_22 = input.LA(4);
						if ( (LA13_22==ConstraintOperator||LA13_22==28) ) {
							alt13=2;
						}
						else if ( (LA13_22==26||LA13_22==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_23 = input.LA(4);
						if ( (LA13_23==ConstraintOperator||LA13_23==28) ) {
							alt13=2;
						}
						else if ( (LA13_23==26||LA13_23==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_24 = input.LA(4);
						if ( (LA13_24==ConstraintOperator||LA13_24==28) ) {
							alt13=2;
						}
						else if ( (LA13_24==26||LA13_24==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_25 = input.LA(4);
						if ( (LA13_25==ConstraintOperator||LA13_25==28) ) {
							alt13=2;
						}
						else if ( (LA13_25==26||LA13_25==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_26 = input.LA(4);
						if ( (LA13_26==ConstraintOperator||LA13_26==28) ) {
							alt13=2;
						}
						else if ( (LA13_26==26||LA13_26==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 26, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_27 = input.LA(4);
						if ( (LA13_27==ConstraintOperator||LA13_27==28) ) {
							alt13=2;
						}
						else if ( (LA13_27==26||LA13_27==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 27, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_28 = input.LA(4);
						if ( (LA13_28==ConstraintOperator||LA13_28==28) ) {
							alt13=2;
						}
						else if ( (LA13_28==26||LA13_28==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 28, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 12, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 13, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 39:
				{
				switch ( input.LA(2) ) {
				case BooleanLiteral:
				case CharacterLiteral:
				case DecimalLiteral:
				case FloatingPointLiteral:
				case HexLiteral:
				case OctalLiteral:
				case StringLiteral:
				case 29:
				case 30:
				case 48:
					{
					alt13=1;
					}
					break;
				case 40:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_15 = input.LA(4);
						if ( (LA13_15==ConstraintOperator||LA13_15==28) ) {
							alt13=2;
						}
						else if ( (LA13_15==26||LA13_15==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_16 = input.LA(4);
						if ( (LA13_16==ConstraintOperator||LA13_16==28) ) {
							alt13=2;
						}
						else if ( (LA13_16==26||LA13_16==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_17 = input.LA(4);
						if ( (LA13_17==ConstraintOperator||LA13_17==28) ) {
							alt13=2;
						}
						else if ( (LA13_17==26||LA13_17==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_18 = input.LA(4);
						if ( (LA13_18==ConstraintOperator||LA13_18==28) ) {
							alt13=2;
						}
						else if ( (LA13_18==26||LA13_18==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_19 = input.LA(4);
						if ( (LA13_19==ConstraintOperator||LA13_19==28) ) {
							alt13=2;
						}
						else if ( (LA13_19==26||LA13_19==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_20 = input.LA(4);
						if ( (LA13_20==ConstraintOperator||LA13_20==28) ) {
							alt13=2;
						}
						else if ( (LA13_20==26||LA13_20==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_21 = input.LA(4);
						if ( (LA13_21==ConstraintOperator||LA13_21==28) ) {
							alt13=2;
						}
						else if ( (LA13_21==26||LA13_21==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case 26:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_22 = input.LA(4);
						if ( (LA13_22==ConstraintOperator||LA13_22==28) ) {
							alt13=2;
						}
						else if ( (LA13_22==26||LA13_22==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_23 = input.LA(4);
						if ( (LA13_23==ConstraintOperator||LA13_23==28) ) {
							alt13=2;
						}
						else if ( (LA13_23==26||LA13_23==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_24 = input.LA(4);
						if ( (LA13_24==ConstraintOperator||LA13_24==28) ) {
							alt13=2;
						}
						else if ( (LA13_24==26||LA13_24==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_25 = input.LA(4);
						if ( (LA13_25==ConstraintOperator||LA13_25==28) ) {
							alt13=2;
						}
						else if ( (LA13_25==26||LA13_25==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_26 = input.LA(4);
						if ( (LA13_26==ConstraintOperator||LA13_26==28) ) {
							alt13=2;
						}
						else if ( (LA13_26==26||LA13_26==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 26, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_27 = input.LA(4);
						if ( (LA13_27==ConstraintOperator||LA13_27==28) ) {
							alt13=2;
						}
						else if ( (LA13_27==26||LA13_27==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 27, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_28 = input.LA(4);
						if ( (LA13_28==ConstraintOperator||LA13_28==28) ) {
							alt13=2;
						}
						else if ( (LA13_28==26||LA13_28==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 28, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 12, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 13, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 35:
				{
				switch ( input.LA(2) ) {
				case BooleanLiteral:
				case CharacterLiteral:
				case DecimalLiteral:
				case FloatingPointLiteral:
				case HexLiteral:
				case OctalLiteral:
				case StringLiteral:
				case 29:
				case 30:
				case 48:
					{
					alt13=1;
					}
					break;
				case 40:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_15 = input.LA(4);
						if ( (LA13_15==ConstraintOperator||LA13_15==28) ) {
							alt13=2;
						}
						else if ( (LA13_15==26||LA13_15==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_16 = input.LA(4);
						if ( (LA13_16==ConstraintOperator||LA13_16==28) ) {
							alt13=2;
						}
						else if ( (LA13_16==26||LA13_16==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_17 = input.LA(4);
						if ( (LA13_17==ConstraintOperator||LA13_17==28) ) {
							alt13=2;
						}
						else if ( (LA13_17==26||LA13_17==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_18 = input.LA(4);
						if ( (LA13_18==ConstraintOperator||LA13_18==28) ) {
							alt13=2;
						}
						else if ( (LA13_18==26||LA13_18==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_19 = input.LA(4);
						if ( (LA13_19==ConstraintOperator||LA13_19==28) ) {
							alt13=2;
						}
						else if ( (LA13_19==26||LA13_19==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_20 = input.LA(4);
						if ( (LA13_20==ConstraintOperator||LA13_20==28) ) {
							alt13=2;
						}
						else if ( (LA13_20==26||LA13_20==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_21 = input.LA(4);
						if ( (LA13_21==ConstraintOperator||LA13_21==28) ) {
							alt13=2;
						}
						else if ( (LA13_21==26||LA13_21==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case 26:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_22 = input.LA(4);
						if ( (LA13_22==ConstraintOperator||LA13_22==28) ) {
							alt13=2;
						}
						else if ( (LA13_22==26||LA13_22==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_23 = input.LA(4);
						if ( (LA13_23==ConstraintOperator||LA13_23==28) ) {
							alt13=2;
						}
						else if ( (LA13_23==26||LA13_23==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_24 = input.LA(4);
						if ( (LA13_24==ConstraintOperator||LA13_24==28) ) {
							alt13=2;
						}
						else if ( (LA13_24==26||LA13_24==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_25 = input.LA(4);
						if ( (LA13_25==ConstraintOperator||LA13_25==28) ) {
							alt13=2;
						}
						else if ( (LA13_25==26||LA13_25==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_26 = input.LA(4);
						if ( (LA13_26==ConstraintOperator||LA13_26==28) ) {
							alt13=2;
						}
						else if ( (LA13_26==26||LA13_26==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 26, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_27 = input.LA(4);
						if ( (LA13_27==ConstraintOperator||LA13_27==28) ) {
							alt13=2;
						}
						else if ( (LA13_27==26||LA13_27==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 27, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_28 = input.LA(4);
						if ( (LA13_28==ConstraintOperator||LA13_28==28) ) {
							alt13=2;
						}
						else if ( (LA13_28==26||LA13_28==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 28, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 12, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 13, 7, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 45:
				{
				switch ( input.LA(2) ) {
				case BooleanLiteral:
				case CharacterLiteral:
				case DecimalLiteral:
				case FloatingPointLiteral:
				case HexLiteral:
				case OctalLiteral:
				case StringLiteral:
				case 29:
				case 30:
				case 48:
					{
					alt13=1;
					}
					break;
				case 40:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_15 = input.LA(4);
						if ( (LA13_15==ConstraintOperator||LA13_15==28) ) {
							alt13=2;
						}
						else if ( (LA13_15==26||LA13_15==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_16 = input.LA(4);
						if ( (LA13_16==ConstraintOperator||LA13_16==28) ) {
							alt13=2;
						}
						else if ( (LA13_16==26||LA13_16==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_17 = input.LA(4);
						if ( (LA13_17==ConstraintOperator||LA13_17==28) ) {
							alt13=2;
						}
						else if ( (LA13_17==26||LA13_17==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_18 = input.LA(4);
						if ( (LA13_18==ConstraintOperator||LA13_18==28) ) {
							alt13=2;
						}
						else if ( (LA13_18==26||LA13_18==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_19 = input.LA(4);
						if ( (LA13_19==ConstraintOperator||LA13_19==28) ) {
							alt13=2;
						}
						else if ( (LA13_19==26||LA13_19==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_20 = input.LA(4);
						if ( (LA13_20==ConstraintOperator||LA13_20==28) ) {
							alt13=2;
						}
						else if ( (LA13_20==26||LA13_20==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_21 = input.LA(4);
						if ( (LA13_21==ConstraintOperator||LA13_21==28) ) {
							alt13=2;
						}
						else if ( (LA13_21==26||LA13_21==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case 26:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_22 = input.LA(4);
						if ( (LA13_22==ConstraintOperator||LA13_22==28) ) {
							alt13=2;
						}
						else if ( (LA13_22==26||LA13_22==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_23 = input.LA(4);
						if ( (LA13_23==ConstraintOperator||LA13_23==28) ) {
							alt13=2;
						}
						else if ( (LA13_23==26||LA13_23==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_24 = input.LA(4);
						if ( (LA13_24==ConstraintOperator||LA13_24==28) ) {
							alt13=2;
						}
						else if ( (LA13_24==26||LA13_24==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_25 = input.LA(4);
						if ( (LA13_25==ConstraintOperator||LA13_25==28) ) {
							alt13=2;
						}
						else if ( (LA13_25==26||LA13_25==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_26 = input.LA(4);
						if ( (LA13_26==ConstraintOperator||LA13_26==28) ) {
							alt13=2;
						}
						else if ( (LA13_26==26||LA13_26==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 26, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_27 = input.LA(4);
						if ( (LA13_27==ConstraintOperator||LA13_27==28) ) {
							alt13=2;
						}
						else if ( (LA13_27==26||LA13_27==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 27, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_28 = input.LA(4);
						if ( (LA13_28==ConstraintOperator||LA13_28==28) ) {
							alt13=2;
						}
						else if ( (LA13_28==26||LA13_28==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 28, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 12, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 13, 8, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 46:
				{
				switch ( input.LA(2) ) {
				case BooleanLiteral:
				case CharacterLiteral:
				case DecimalLiteral:
				case FloatingPointLiteral:
				case HexLiteral:
				case OctalLiteral:
				case StringLiteral:
				case 29:
				case 30:
				case 48:
					{
					alt13=1;
					}
					break;
				case 40:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_15 = input.LA(4);
						if ( (LA13_15==ConstraintOperator||LA13_15==28) ) {
							alt13=2;
						}
						else if ( (LA13_15==26||LA13_15==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_16 = input.LA(4);
						if ( (LA13_16==ConstraintOperator||LA13_16==28) ) {
							alt13=2;
						}
						else if ( (LA13_16==26||LA13_16==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_17 = input.LA(4);
						if ( (LA13_17==ConstraintOperator||LA13_17==28) ) {
							alt13=2;
						}
						else if ( (LA13_17==26||LA13_17==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_18 = input.LA(4);
						if ( (LA13_18==ConstraintOperator||LA13_18==28) ) {
							alt13=2;
						}
						else if ( (LA13_18==26||LA13_18==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_19 = input.LA(4);
						if ( (LA13_19==ConstraintOperator||LA13_19==28) ) {
							alt13=2;
						}
						else if ( (LA13_19==26||LA13_19==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_20 = input.LA(4);
						if ( (LA13_20==ConstraintOperator||LA13_20==28) ) {
							alt13=2;
						}
						else if ( (LA13_20==26||LA13_20==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_21 = input.LA(4);
						if ( (LA13_21==ConstraintOperator||LA13_21==28) ) {
							alt13=2;
						}
						else if ( (LA13_21==26||LA13_21==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case 26:
					{
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA13_22 = input.LA(4);
						if ( (LA13_22==ConstraintOperator||LA13_22==28) ) {
							alt13=2;
						}
						else if ( (LA13_22==26||LA13_22==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA13_23 = input.LA(4);
						if ( (LA13_23==ConstraintOperator||LA13_23==28) ) {
							alt13=2;
						}
						else if ( (LA13_23==26||LA13_23==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA13_24 = input.LA(4);
						if ( (LA13_24==ConstraintOperator||LA13_24==28) ) {
							alt13=2;
						}
						else if ( (LA13_24==26||LA13_24==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA13_25 = input.LA(4);
						if ( (LA13_25==ConstraintOperator||LA13_25==28) ) {
							alt13=2;
						}
						else if ( (LA13_25==26||LA13_25==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA13_26 = input.LA(4);
						if ( (LA13_26==ConstraintOperator||LA13_26==28) ) {
							alt13=2;
						}
						else if ( (LA13_26==26||LA13_26==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 26, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA13_27 = input.LA(4);
						if ( (LA13_27==ConstraintOperator||LA13_27==28) ) {
							alt13=2;
						}
						else if ( (LA13_27==26||LA13_27==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 27, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA13_28 = input.LA(4);
						if ( (LA13_28==ConstraintOperator||LA13_28==28) ) {
							alt13=2;
						}
						else if ( (LA13_28==26||LA13_28==40) ) {
							alt13=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 28, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 12, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 13, 9, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case BooleanLiteral:
			case CharacterLiteral:
			case DecimalLiteral:
			case FloatingPointLiteral:
			case HexLiteral:
			case OctalLiteral:
			case StringLiteral:
			case 29:
			case 30:
			case 48:
				{
				alt13=1;
				}
				break;
			case 40:
				{
				switch ( input.LA(2) ) {
				case Identifiertoken:
					{
					int LA13_15 = input.LA(3);
					if ( (LA13_15==ConstraintOperator||LA13_15==28) ) {
						alt13=2;
					}
					else if ( (LA13_15==26||LA13_15==40) ) {
						alt13=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 15, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 49:
					{
					int LA13_16 = input.LA(3);
					if ( (LA13_16==ConstraintOperator||LA13_16==28) ) {
						alt13=2;
					}
					else if ( (LA13_16==26||LA13_16==40) ) {
						alt13=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 16, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 47:
					{
					int LA13_17 = input.LA(3);
					if ( (LA13_17==ConstraintOperator||LA13_17==28) ) {
						alt13=2;
					}
					else if ( (LA13_17==26||LA13_17==40) ) {
						alt13=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 17, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 43:
					{
					int LA13_18 = input.LA(3);
					if ( (LA13_18==ConstraintOperator||LA13_18==28) ) {
						alt13=2;
					}
					else if ( (LA13_18==26||LA13_18==40) ) {
						alt13=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 18, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 44:
					{
					int LA13_19 = input.LA(3);
					if ( (LA13_19==ConstraintOperator||LA13_19==28) ) {
						alt13=2;
					}
					else if ( (LA13_19==26||LA13_19==40) ) {
						alt13=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 19, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 45:
					{
					int LA13_20 = input.LA(3);
					if ( (LA13_20==ConstraintOperator||LA13_20==28) ) {
						alt13=2;
					}
					else if ( (LA13_20==26||LA13_20==40) ) {
						alt13=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 20, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 46:
					{
					int LA13_21 = input.LA(3);
					if ( (LA13_21==ConstraintOperator||LA13_21==28) ) {
						alt13=2;
					}
					else if ( (LA13_21==26||LA13_21==40) ) {
						alt13=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 21, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 13, 11, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 26:
				{
				switch ( input.LA(2) ) {
				case Identifiertoken:
					{
					int LA13_22 = input.LA(3);
					if ( (LA13_22==ConstraintOperator||LA13_22==28) ) {
						alt13=2;
					}
					else if ( (LA13_22==26||LA13_22==40) ) {
						alt13=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 22, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 49:
					{
					int LA13_23 = input.LA(3);
					if ( (LA13_23==ConstraintOperator||LA13_23==28) ) {
						alt13=2;
					}
					else if ( (LA13_23==26||LA13_23==40) ) {
						alt13=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 23, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 47:
					{
					int LA13_24 = input.LA(3);
					if ( (LA13_24==ConstraintOperator||LA13_24==28) ) {
						alt13=2;
					}
					else if ( (LA13_24==26||LA13_24==40) ) {
						alt13=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 24, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 43:
					{
					int LA13_25 = input.LA(3);
					if ( (LA13_25==ConstraintOperator||LA13_25==28) ) {
						alt13=2;
					}
					else if ( (LA13_25==26||LA13_25==40) ) {
						alt13=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 25, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 44:
					{
					int LA13_26 = input.LA(3);
					if ( (LA13_26==ConstraintOperator||LA13_26==28) ) {
						alt13=2;
					}
					else if ( (LA13_26==26||LA13_26==40) ) {
						alt13=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 26, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 45:
					{
					int LA13_27 = input.LA(3);
					if ( (LA13_27==ConstraintOperator||LA13_27==28) ) {
						alt13=2;
					}
					else if ( (LA13_27==26||LA13_27==40) ) {
						alt13=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 27, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 46:
					{
					int LA13_28 = input.LA(3);
					if ( (LA13_28==ConstraintOperator||LA13_28==28) ) {
						alt13=2;
					}
					else if ( (LA13_28==26||LA13_28==40) ) {
						alt13=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 28, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 13, 12, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 32:
				{
				alt13=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 13, 0, input);
				throw nvae;
			}
			switch (alt13) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:275:4: tmp= literalConstraint[valuesource]
					{
					pushFollow(FOLLOW_literalConstraint_in_singleConstraint640);
					tmp=literalConstraint(valuesource);
					state._fsp--;

					constraint = tmp;
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:276:4: tmp= boundConstraint[tmodel, valuesource, vars]
					{
					pushFollow(FOLLOW_boundConstraint_in_singleConstraint651);
					tmp=boundConstraint(tmodel, valuesource, vars);
					state._fsp--;

					constraint = tmp;
					}
					break;
				case 3 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:277:4: tmp= multiBoundConstraint[tmodel, valuesource, vars]
					{
					pushFollow(FOLLOW_multiBoundConstraint_in_singleConstraint661);
					tmp=multiBoundConstraint(tmodel, valuesource, vars);
					state._fsp--;

					constraint = tmp;
					}
					break;
				case 4 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:278:4: tmp= predicateConstraint[tmodel, valuesource, vars]
					{
					pushFollow(FOLLOW_predicateConstraint_in_singleConstraint671);
					tmp=predicateConstraint(tmodel, valuesource, vars);
					state._fsp--;

					constraint = tmp;
					}
					break;
				case 5 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:279:4: tmp= returnValueConstraint[tmodel, valuesource, vars]
					{
					pushFollow(FOLLOW_returnValueConstraint_in_singleConstraint681);
					tmp=returnValueConstraint(tmodel, valuesource, vars);
					state._fsp--;

					constraint = tmp;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return constraint;
	}
	// $ANTLR end "singleConstraint"



	// $ANTLR start "literalConstraint"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:282:1: literalConstraint[Object valuesource] returns [IConstraint constraint] : (op= operator )? val= constant ;
	public final IConstraint literalConstraint(Object valuesource) throws RecognitionException {
		IConstraint constraint = null;


		IOperator op =null;
		Object val =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:283:2: ( (op= operator )? val= constant )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:283:4: (op= operator )? val= constant
			{
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:283:6: (op= operator )?
			int alt14=2;
			int LA14_0 = input.LA(1);
			if ( (LA14_0==25||LA14_0==33||LA14_0==35||(LA14_0 >= 37 && LA14_0 <= 39)||(LA14_0 >= 45 && LA14_0 <= 46)||LA14_0==50) ) {
				alt14=1;
			}
			switch (alt14) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:283:6: op= operator
					{
					pushFollow(FOLLOW_operator_in_literalConstraint703);
					op=operator();
					state._fsp--;

					}
					break;

			}

			pushFollow(FOLLOW_constant_in_literalConstraint708);
			val=constant();
			state._fsp--;


					if(op!=null)	
						constraint = new LiteralConstraint(valuesource, val, op);
					else
						constraint = new LiteralConstraint(valuesource, val);
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return constraint;
	}
	// $ANTLR end "literalConstraint"



	// $ANTLR start "someBoundConstraint"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:292:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] |mbc= multiBoundConstraint[tmodel, valuesource, vars] );
	public final IConstraint someBoundConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
		IConstraint constraint = null;


		IConstraint bc =null;
		IConstraint mbc =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:293:2: (bc= boundConstraint[tmodel, valuesource, vars] |mbc= multiBoundConstraint[tmodel, valuesource, vars] )
			int alt15=2;
			switch ( input.LA(1) ) {
			case 37:
				{
				int LA15_1 = input.LA(2);
				if ( (LA15_1==40) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_12 = input.LA(4);
						if ( (LA15_12==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_12==26||LA15_12==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_13 = input.LA(4);
						if ( (LA15_13==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_13==26||LA15_13==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 13, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_14 = input.LA(4);
						if ( (LA15_14==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_14==26||LA15_14==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 14, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_15 = input.LA(4);
						if ( (LA15_15==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_15==26||LA15_15==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_16 = input.LA(4);
						if ( (LA15_16==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_16==26||LA15_16==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_17 = input.LA(4);
						if ( (LA15_17==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_17==26||LA15_17==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_18 = input.LA(4);
						if ( (LA15_18==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_18==26||LA15_18==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}
				else if ( (LA15_1==26) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_19 = input.LA(4);
						if ( (LA15_19==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_19==26||LA15_19==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_20 = input.LA(4);
						if ( (LA15_20==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_20==26||LA15_20==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_21 = input.LA(4);
						if ( (LA15_21==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_21==26||LA15_21==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_22 = input.LA(4);
						if ( (LA15_22==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_22==26||LA15_22==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_23 = input.LA(4);
						if ( (LA15_23==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_23==26||LA15_23==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_24 = input.LA(4);
						if ( (LA15_24==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_24==26||LA15_24==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_25 = input.LA(4);
						if ( (LA15_25==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_25==26||LA15_25==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 15, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 25:
				{
				int LA15_2 = input.LA(2);
				if ( (LA15_2==40) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_12 = input.LA(4);
						if ( (LA15_12==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_12==26||LA15_12==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_13 = input.LA(4);
						if ( (LA15_13==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_13==26||LA15_13==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 13, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_14 = input.LA(4);
						if ( (LA15_14==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_14==26||LA15_14==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 14, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_15 = input.LA(4);
						if ( (LA15_15==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_15==26||LA15_15==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_16 = input.LA(4);
						if ( (LA15_16==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_16==26||LA15_16==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_17 = input.LA(4);
						if ( (LA15_17==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_17==26||LA15_17==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_18 = input.LA(4);
						if ( (LA15_18==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_18==26||LA15_18==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}
				else if ( (LA15_2==26) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_19 = input.LA(4);
						if ( (LA15_19==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_19==26||LA15_19==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_20 = input.LA(4);
						if ( (LA15_20==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_20==26||LA15_20==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_21 = input.LA(4);
						if ( (LA15_21==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_21==26||LA15_21==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_22 = input.LA(4);
						if ( (LA15_22==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_22==26||LA15_22==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_23 = input.LA(4);
						if ( (LA15_23==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_23==26||LA15_23==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_24 = input.LA(4);
						if ( (LA15_24==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_24==26||LA15_24==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_25 = input.LA(4);
						if ( (LA15_25==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_25==26||LA15_25==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 15, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 50:
				{
				int LA15_3 = input.LA(2);
				if ( (LA15_3==40) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_12 = input.LA(4);
						if ( (LA15_12==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_12==26||LA15_12==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_13 = input.LA(4);
						if ( (LA15_13==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_13==26||LA15_13==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 13, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_14 = input.LA(4);
						if ( (LA15_14==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_14==26||LA15_14==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 14, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_15 = input.LA(4);
						if ( (LA15_15==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_15==26||LA15_15==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_16 = input.LA(4);
						if ( (LA15_16==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_16==26||LA15_16==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_17 = input.LA(4);
						if ( (LA15_17==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_17==26||LA15_17==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_18 = input.LA(4);
						if ( (LA15_18==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_18==26||LA15_18==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}
				else if ( (LA15_3==26) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_19 = input.LA(4);
						if ( (LA15_19==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_19==26||LA15_19==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_20 = input.LA(4);
						if ( (LA15_20==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_20==26||LA15_20==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_21 = input.LA(4);
						if ( (LA15_21==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_21==26||LA15_21==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_22 = input.LA(4);
						if ( (LA15_22==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_22==26||LA15_22==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_23 = input.LA(4);
						if ( (LA15_23==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_23==26||LA15_23==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_24 = input.LA(4);
						if ( (LA15_24==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_24==26||LA15_24==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_25 = input.LA(4);
						if ( (LA15_25==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_25==26||LA15_25==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 15, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 38:
				{
				int LA15_4 = input.LA(2);
				if ( (LA15_4==40) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_12 = input.LA(4);
						if ( (LA15_12==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_12==26||LA15_12==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_13 = input.LA(4);
						if ( (LA15_13==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_13==26||LA15_13==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 13, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_14 = input.LA(4);
						if ( (LA15_14==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_14==26||LA15_14==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 14, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_15 = input.LA(4);
						if ( (LA15_15==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_15==26||LA15_15==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_16 = input.LA(4);
						if ( (LA15_16==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_16==26||LA15_16==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_17 = input.LA(4);
						if ( (LA15_17==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_17==26||LA15_17==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_18 = input.LA(4);
						if ( (LA15_18==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_18==26||LA15_18==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}
				else if ( (LA15_4==26) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_19 = input.LA(4);
						if ( (LA15_19==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_19==26||LA15_19==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_20 = input.LA(4);
						if ( (LA15_20==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_20==26||LA15_20==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_21 = input.LA(4);
						if ( (LA15_21==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_21==26||LA15_21==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_22 = input.LA(4);
						if ( (LA15_22==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_22==26||LA15_22==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_23 = input.LA(4);
						if ( (LA15_23==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_23==26||LA15_23==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_24 = input.LA(4);
						if ( (LA15_24==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_24==26||LA15_24==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_25 = input.LA(4);
						if ( (LA15_25==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_25==26||LA15_25==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 15, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 33:
				{
				int LA15_5 = input.LA(2);
				if ( (LA15_5==40) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_12 = input.LA(4);
						if ( (LA15_12==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_12==26||LA15_12==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_13 = input.LA(4);
						if ( (LA15_13==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_13==26||LA15_13==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 13, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_14 = input.LA(4);
						if ( (LA15_14==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_14==26||LA15_14==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 14, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_15 = input.LA(4);
						if ( (LA15_15==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_15==26||LA15_15==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_16 = input.LA(4);
						if ( (LA15_16==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_16==26||LA15_16==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_17 = input.LA(4);
						if ( (LA15_17==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_17==26||LA15_17==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_18 = input.LA(4);
						if ( (LA15_18==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_18==26||LA15_18==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}
				else if ( (LA15_5==26) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_19 = input.LA(4);
						if ( (LA15_19==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_19==26||LA15_19==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_20 = input.LA(4);
						if ( (LA15_20==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_20==26||LA15_20==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_21 = input.LA(4);
						if ( (LA15_21==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_21==26||LA15_21==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_22 = input.LA(4);
						if ( (LA15_22==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_22==26||LA15_22==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_23 = input.LA(4);
						if ( (LA15_23==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_23==26||LA15_23==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_24 = input.LA(4);
						if ( (LA15_24==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_24==26||LA15_24==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_25 = input.LA(4);
						if ( (LA15_25==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_25==26||LA15_25==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 15, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 39:
				{
				int LA15_6 = input.LA(2);
				if ( (LA15_6==40) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_12 = input.LA(4);
						if ( (LA15_12==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_12==26||LA15_12==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_13 = input.LA(4);
						if ( (LA15_13==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_13==26||LA15_13==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 13, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_14 = input.LA(4);
						if ( (LA15_14==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_14==26||LA15_14==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 14, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_15 = input.LA(4);
						if ( (LA15_15==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_15==26||LA15_15==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_16 = input.LA(4);
						if ( (LA15_16==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_16==26||LA15_16==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_17 = input.LA(4);
						if ( (LA15_17==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_17==26||LA15_17==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_18 = input.LA(4);
						if ( (LA15_18==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_18==26||LA15_18==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}
				else if ( (LA15_6==26) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_19 = input.LA(4);
						if ( (LA15_19==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_19==26||LA15_19==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_20 = input.LA(4);
						if ( (LA15_20==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_20==26||LA15_20==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_21 = input.LA(4);
						if ( (LA15_21==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_21==26||LA15_21==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_22 = input.LA(4);
						if ( (LA15_22==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_22==26||LA15_22==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_23 = input.LA(4);
						if ( (LA15_23==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_23==26||LA15_23==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_24 = input.LA(4);
						if ( (LA15_24==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_24==26||LA15_24==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_25 = input.LA(4);
						if ( (LA15_25==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_25==26||LA15_25==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 15, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 35:
				{
				int LA15_7 = input.LA(2);
				if ( (LA15_7==40) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_12 = input.LA(4);
						if ( (LA15_12==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_12==26||LA15_12==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_13 = input.LA(4);
						if ( (LA15_13==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_13==26||LA15_13==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 13, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_14 = input.LA(4);
						if ( (LA15_14==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_14==26||LA15_14==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 14, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_15 = input.LA(4);
						if ( (LA15_15==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_15==26||LA15_15==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_16 = input.LA(4);
						if ( (LA15_16==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_16==26||LA15_16==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_17 = input.LA(4);
						if ( (LA15_17==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_17==26||LA15_17==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_18 = input.LA(4);
						if ( (LA15_18==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_18==26||LA15_18==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}
				else if ( (LA15_7==26) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_19 = input.LA(4);
						if ( (LA15_19==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_19==26||LA15_19==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_20 = input.LA(4);
						if ( (LA15_20==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_20==26||LA15_20==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_21 = input.LA(4);
						if ( (LA15_21==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_21==26||LA15_21==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_22 = input.LA(4);
						if ( (LA15_22==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_22==26||LA15_22==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_23 = input.LA(4);
						if ( (LA15_23==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_23==26||LA15_23==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_24 = input.LA(4);
						if ( (LA15_24==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_24==26||LA15_24==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_25 = input.LA(4);
						if ( (LA15_25==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_25==26||LA15_25==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 15, 7, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 45:
				{
				int LA15_8 = input.LA(2);
				if ( (LA15_8==40) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_12 = input.LA(4);
						if ( (LA15_12==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_12==26||LA15_12==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_13 = input.LA(4);
						if ( (LA15_13==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_13==26||LA15_13==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 13, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_14 = input.LA(4);
						if ( (LA15_14==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_14==26||LA15_14==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 14, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_15 = input.LA(4);
						if ( (LA15_15==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_15==26||LA15_15==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_16 = input.LA(4);
						if ( (LA15_16==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_16==26||LA15_16==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_17 = input.LA(4);
						if ( (LA15_17==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_17==26||LA15_17==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_18 = input.LA(4);
						if ( (LA15_18==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_18==26||LA15_18==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}
				else if ( (LA15_8==26) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_19 = input.LA(4);
						if ( (LA15_19==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_19==26||LA15_19==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_20 = input.LA(4);
						if ( (LA15_20==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_20==26||LA15_20==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_21 = input.LA(4);
						if ( (LA15_21==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_21==26||LA15_21==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_22 = input.LA(4);
						if ( (LA15_22==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_22==26||LA15_22==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_23 = input.LA(4);
						if ( (LA15_23==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_23==26||LA15_23==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_24 = input.LA(4);
						if ( (LA15_24==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_24==26||LA15_24==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_25 = input.LA(4);
						if ( (LA15_25==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_25==26||LA15_25==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 15, 8, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 46:
				{
				int LA15_9 = input.LA(2);
				if ( (LA15_9==40) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_12 = input.LA(4);
						if ( (LA15_12==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_12==26||LA15_12==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_13 = input.LA(4);
						if ( (LA15_13==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_13==26||LA15_13==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 13, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_14 = input.LA(4);
						if ( (LA15_14==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_14==26||LA15_14==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 14, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_15 = input.LA(4);
						if ( (LA15_15==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_15==26||LA15_15==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_16 = input.LA(4);
						if ( (LA15_16==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_16==26||LA15_16==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_17 = input.LA(4);
						if ( (LA15_17==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_17==26||LA15_17==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 17, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_18 = input.LA(4);
						if ( (LA15_18==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_18==26||LA15_18==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}
				else if ( (LA15_9==26) ) {
					switch ( input.LA(3) ) {
					case Identifiertoken:
						{
						int LA15_19 = input.LA(4);
						if ( (LA15_19==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_19==26||LA15_19==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 19, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 49:
						{
						int LA15_20 = input.LA(4);
						if ( (LA15_20==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_20==26||LA15_20==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 20, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 47:
						{
						int LA15_21 = input.LA(4);
						if ( (LA15_21==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_21==26||LA15_21==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 21, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 43:
						{
						int LA15_22 = input.LA(4);
						if ( (LA15_22==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_22==26||LA15_22==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 44:
						{
						int LA15_23 = input.LA(4);
						if ( (LA15_23==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_23==26||LA15_23==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 45:
						{
						int LA15_24 = input.LA(4);
						if ( (LA15_24==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_24==26||LA15_24==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case 46:
						{
						int LA15_25 = input.LA(4);
						if ( (LA15_25==EOF) ) {
							alt15=1;
						}
						else if ( (LA15_25==26||LA15_25==40) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 15, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 11, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 15, 9, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case 40:
				{
				switch ( input.LA(2) ) {
				case Identifiertoken:
					{
					int LA15_12 = input.LA(3);
					if ( (LA15_12==EOF) ) {
						alt15=1;
					}
					else if ( (LA15_12==26||LA15_12==40) ) {
						alt15=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 12, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 49:
					{
					int LA15_13 = input.LA(3);
					if ( (LA15_13==EOF) ) {
						alt15=1;
					}
					else if ( (LA15_13==26||LA15_13==40) ) {
						alt15=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 13, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 47:
					{
					int LA15_14 = input.LA(3);
					if ( (LA15_14==EOF) ) {
						alt15=1;
					}
					else if ( (LA15_14==26||LA15_14==40) ) {
						alt15=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 14, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 43:
					{
					int LA15_15 = input.LA(3);
					if ( (LA15_15==EOF) ) {
						alt15=1;
					}
					else if ( (LA15_15==26||LA15_15==40) ) {
						alt15=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 15, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 44:
					{
					int LA15_16 = input.LA(3);
					if ( (LA15_16==EOF) ) {
						alt15=1;
					}
					else if ( (LA15_16==26||LA15_16==40) ) {
						alt15=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 16, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 45:
					{
					int LA15_17 = input.LA(3);
					if ( (LA15_17==EOF) ) {
						alt15=1;
					}
					else if ( (LA15_17==26||LA15_17==40) ) {
						alt15=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 17, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 46:
					{
					int LA15_18 = input.LA(3);
					if ( (LA15_18==EOF) ) {
						alt15=1;
					}
					else if ( (LA15_18==26||LA15_18==40) ) {
						alt15=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 18, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 15, 10, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case 26:
				{
				switch ( input.LA(2) ) {
				case Identifiertoken:
					{
					int LA15_19 = input.LA(3);
					if ( (LA15_19==EOF) ) {
						alt15=1;
					}
					else if ( (LA15_19==26||LA15_19==40) ) {
						alt15=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 19, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 49:
					{
					int LA15_20 = input.LA(3);
					if ( (LA15_20==EOF) ) {
						alt15=1;
					}
					else if ( (LA15_20==26||LA15_20==40) ) {
						alt15=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 20, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 47:
					{
					int LA15_21 = input.LA(3);
					if ( (LA15_21==EOF) ) {
						alt15=1;
					}
					else if ( (LA15_21==26||LA15_21==40) ) {
						alt15=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 21, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 43:
					{
					int LA15_22 = input.LA(3);
					if ( (LA15_22==EOF) ) {
						alt15=1;
					}
					else if ( (LA15_22==26||LA15_22==40) ) {
						alt15=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 22, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 44:
					{
					int LA15_23 = input.LA(3);
					if ( (LA15_23==EOF) ) {
						alt15=1;
					}
					else if ( (LA15_23==26||LA15_23==40) ) {
						alt15=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 23, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 45:
					{
					int LA15_24 = input.LA(3);
					if ( (LA15_24==EOF) ) {
						alt15=1;
					}
					else if ( (LA15_24==26||LA15_24==40) ) {
						alt15=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 24, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case 46:
					{
					int LA15_25 = input.LA(3);
					if ( (LA15_25==EOF) ) {
						alt15=1;
					}
					else if ( (LA15_25==26||LA15_25==40) ) {
						alt15=2;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 15, 25, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 15, 11, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 15, 0, input);
				throw nvae;
			}
			switch (alt15) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:293:4: bc= boundConstraint[tmodel, valuesource, vars]
					{
					pushFollow(FOLLOW_boundConstraint_in_someBoundConstraint731);
					bc=boundConstraint(tmodel, valuesource, vars);
					state._fsp--;


							constraint = bc;
						
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:297:4: mbc= multiBoundConstraint[tmodel, valuesource, vars]
					{
					pushFollow(FOLLOW_multiBoundConstraint_in_someBoundConstraint744);
					mbc=multiBoundConstraint(tmodel, valuesource, vars);
					state._fsp--;


							constraint = mbc;
						
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return constraint;
	}
	// $ANTLR end "someBoundConstraint"



	// $ANTLR start "boundConstraint"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:303:1: boundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] ;
	public final IConstraint boundConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
		IConstraint constraint = null;


		IOperator op =null;
		Variable var =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:304:2: ( (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:304:4: (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars]
			{
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:304:6: (op= operator )?
			int alt16=2;
			int LA16_0 = input.LA(1);
			if ( (LA16_0==25||LA16_0==33||LA16_0==35||(LA16_0 >= 37 && LA16_0 <= 39)||(LA16_0 >= 45 && LA16_0 <= 46)||LA16_0==50) ) {
				alt16=1;
			}
			switch (alt16) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:304:6: op= operator
					{
					pushFollow(FOLLOW_operator_in_boundConstraint767);
					op=operator();
					state._fsp--;

					}
					break;

			}

			pushFollow(FOLLOW_variable_in_boundConstraint772);
			var=variable(op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars);
			state._fsp--;


					if(op!=null)
						constraint = new BoundConstraint(valuesource, var, op);
					else
						constraint = new BoundConstraint(valuesource, var);
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return constraint;
	}
	// $ANTLR end "boundConstraint"



	// $ANTLR start "multiBoundConstraint"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:313:1: multiBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+ ;
	public final IConstraint multiBoundConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
		IConstraint constraint = null;


		IOperator op =null;
		Variable var =null;
		Variable varn =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:314:2: ( (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+ )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:315:2: (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+
			{

					List vs = new ArrayList();
				
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:318:4: (op= operator )?
			int alt17=2;
			int LA17_0 = input.LA(1);
			if ( (LA17_0==25||LA17_0==33||LA17_0==35||(LA17_0 >= 37 && LA17_0 <= 39)||(LA17_0 >= 45 && LA17_0 <= 46)||LA17_0==50) ) {
				alt17=1;
			}
			switch (alt17) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:318:4: op= operator
					{
					pushFollow(FOLLOW_operator_in_multiBoundConstraint800);
					op=operator();
					state._fsp--;

					}
					break;

			}

			pushFollow(FOLLOW_variable_in_multiBoundConstraint805);
			var=variable(op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars);
			state._fsp--;


					vs.add(var);
				
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:322:2: (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+
			int cnt18=0;
			loop18:
			while (true) {
				int alt18=2;
				int LA18_0 = input.LA(1);
				if ( (LA18_0==26||LA18_0==40) ) {
					alt18=1;
				}

				switch (alt18) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:322:3: varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars]
					{
					pushFollow(FOLLOW_variable_in_multiBoundConstraint815);
					varn=variable(SConditions.getValueSourceType(tmodel, valuesource), vars);
					state._fsp--;


							vs.add(varn);
						
					}
					break;

				default :
					if ( cnt18 >= 1 ) break loop18;
					EarlyExitException eee = new EarlyExitException(18, input);
					throw eee;
				}
				cnt18++;
			}


					//if(op!=null)
						constraint = new BoundConstraint(valuesource, vs, op==null? IOperator.EQUAL: op); // Hack? one operator per variable?
					//else
					//	constraint = new BoundConstraint(valuesource, vars);
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return constraint;
	}
	// $ANTLR end "multiBoundConstraint"



	// $ANTLR start "predicateConstraint"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:335:1: predicateConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : ':' ({...}?fc= functionCall[tmodel, vars] |oc= operatorCall[tmodel, vars] ) ;
	public final IConstraint predicateConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
		IConstraint constraint = null;


		FunctionCall fc =null;
		FunctionCall oc =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:336:2: ( ':' ({...}?fc= functionCall[tmodel, vars] |oc= operatorCall[tmodel, vars] ) )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:336:4: ':' ({...}?fc= functionCall[tmodel, vars] |oc= operatorCall[tmodel, vars] )
			{
			match(input,32,FOLLOW_32_in_predicateConstraint843); 
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:336:8: ({...}?fc= functionCall[tmodel, vars] |oc= operatorCall[tmodel, vars] )
			int alt19=2;
			int LA19_0 = input.LA(1);
			if ( (LA19_0==27) ) {
				int LA19_1 = input.LA(2);
				if ( ((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {
					alt19=1;
				}
				else if ( (true) ) {
					alt19=2;
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 19, 0, input);
				throw nvae;
			}

			switch (alt19) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:336:9: {...}?fc= functionCall[tmodel, vars]
					{
					if ( !((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {
						throw new FailedPredicateException(input, "predicateConstraint", "SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)");
					}
					pushFollow(FOLLOW_functionCall_in_predicateConstraint850);
					fc=functionCall(tmodel, vars);
					state._fsp--;

					constraint = new PredicateConstraint(fc);
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:337:4: oc= operatorCall[tmodel, vars]
					{
					pushFollow(FOLLOW_operatorCall_in_predicateConstraint861);
					oc=operatorCall(tmodel, vars);
					state._fsp--;

					constraint = new PredicateConstraint(oc);
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
			// do for sure before leaving
		}
		return constraint;
	}
	// $ANTLR end "predicateConstraint"



	// $ANTLR start "returnValueConstraint"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:342:1: returnValueConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : equalOperator ({...}?fc= functionCall[tmodel, vars] |oc= operatorCall[tmodel, vars] ) ;
	public final IConstraint returnValueConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
		IConstraint constraint = null;


		FunctionCall fc =null;
		FunctionCall oc =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:343:2: ( equalOperator ({...}?fc= functionCall[tmodel, vars] |oc= operatorCall[tmodel, vars] ) )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:343:4: equalOperator ({...}?fc= functionCall[tmodel, vars] |oc= operatorCall[tmodel, vars] )
			{
			pushFollow(FOLLOW_equalOperator_in_returnValueConstraint885);
			equalOperator();
			state._fsp--;

			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:343:18: ({...}?fc= functionCall[tmodel, vars] |oc= operatorCall[tmodel, vars] )
			int alt20=2;
			int LA20_0 = input.LA(1);
			if ( (LA20_0==27) ) {
				int LA20_1 = input.LA(2);
				if ( ((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {
					alt20=1;
				}
				else if ( (true) ) {
					alt20=2;
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 20, 0, input);
				throw nvae;
			}

			switch (alt20) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:343:19: {...}?fc= functionCall[tmodel, vars]
					{
					if ( !((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {
						throw new FailedPredicateException(input, "returnValueConstraint", "SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)");
					}
					pushFollow(FOLLOW_functionCall_in_returnValueConstraint892);
					fc=functionCall(tmodel, vars);
					state._fsp--;

					constraint = new ValueSourceReturnValueConstraint(valuesource, fc, IOperator.EQUAL);
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:344:4: oc= operatorCall[tmodel, vars]
					{
					pushFollow(FOLLOW_operatorCall_in_returnValueConstraint903);
					oc=operatorCall(tmodel, vars);
					state._fsp--;

					constraint = new ValueSourceReturnValueConstraint(valuesource, oc, IOperator.EQUAL);
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
			// do for sure before leaving
		}
		return constraint;
	}
	// $ANTLR end "returnValueConstraint"



	// $ANTLR start "functionCall"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:348:1: functionCall[OAVTypeModel tmodel, Map vars] returns [FunctionCall fc] : '(' fn= functionName (exp= parameter[tmodel, vars] )* ')' ;
	public final FunctionCall functionCall(OAVTypeModel tmodel, Map vars) throws RecognitionException {
		FunctionCall fc = null;


		String fn =null;
		Object exp =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:349:2: ( '(' fn= functionName (exp= parameter[tmodel, vars] )* ')' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:350:2: '(' fn= functionName (exp= parameter[tmodel, vars] )* ')'
			{

					List exps = new ArrayList();
				
			match(input,27,FOLLOW_27_in_functionCall933); 
			pushFollow(FOLLOW_functionName_in_functionCall937);
			fn=functionName();
			state._fsp--;

			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:353:22: (exp= parameter[tmodel, vars] )*
			loop21:
			while (true) {
				int alt21=2;
				int LA21_0 = input.LA(1);
				if ( (LA21_0==BooleanLiteral||LA21_0==CharacterLiteral||LA21_0==DecimalLiteral||LA21_0==FloatingPointLiteral||LA21_0==HexLiteral||(LA21_0 >= OctalLiteral && LA21_0 <= StringLiteral)||(LA21_0 >= 26 && LA21_0 <= 27)||(LA21_0 >= 29 && LA21_0 <= 30)||LA21_0==40||LA21_0==48) ) {
					alt21=1;
				}

				switch (alt21) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:353:23: exp= parameter[tmodel, vars]
					{
					pushFollow(FOLLOW_parameter_in_functionCall942);
					exp=parameter(tmodel, vars);
					state._fsp--;


							exps.add(exp);
						
					}
					break;

				default :
					break loop21;
				}
			}

			match(input,28,FOLLOW_28_in_functionCall952); 

							Class	clazz	= SReflect.findClass0(fn, imports, tmodel.getClassLoader());
			            	IFunction func = null;
							if(MethodCallFunction.class.equals(clazz))
							{
								String clazzname = (String)((Constant)exps.remove(0)).getValue();
								String methodname = (String)((Constant)exps.remove(0)).getValue();
								clazz = SReflect.findClass0(clazzname, imports, tmodel.getClassLoader());
								Method[] methods = SReflect.getMethods(clazz, methodname);
								Method method = null;
								// Find one matching regardless of param types (hack???).
								// First param is object on which function will be called.
								for(int i = 0; i < methods.length && method == null; i++)
								{
									if(methods[i].getParameterTypes().length == exps.size() - 1)
									{
										method = methods[i];
									}
								}
								if(method != null)
									func = new MethodCallFunction(method);
							}
							else
							{
								try
								{
									func = (IFunction)clazz.newInstance();
								}
								catch(Exception e)
								{
								}
							}
							if(func == null)
								throw new RuntimeException("Function not found: " + fn);
							fc = new FunctionCall(func, exps); 
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return fc;
	}
	// $ANTLR end "functionCall"



	// $ANTLR start "operatorCall"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:396:1: operatorCall[OAVTypeModel tmodel, Map vars] returns [FunctionCall fc] : '(' op= operator exp1= parameter[tmodel, vars] exp2= parameter[tmodel, vars] ')' ;
	public final FunctionCall operatorCall(OAVTypeModel tmodel, Map vars) throws RecognitionException {
		FunctionCall fc = null;


		IOperator op =null;
		Object exp1 =null;
		Object exp2 =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:397:2: ( '(' op= operator exp1= parameter[tmodel, vars] exp2= parameter[tmodel, vars] ')' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:397:4: '(' op= operator exp1= parameter[tmodel, vars] exp2= parameter[tmodel, vars] ')'
			{
			match(input,27,FOLLOW_27_in_operatorCall973); 
			pushFollow(FOLLOW_operator_in_operatorCall978);
			op=operator();
			state._fsp--;

			pushFollow(FOLLOW_parameter_in_operatorCall983);
			exp1=parameter(tmodel, vars);
			state._fsp--;

			pushFollow(FOLLOW_parameter_in_operatorCall988);
			exp2=parameter(tmodel, vars);
			state._fsp--;

			match(input,28,FOLLOW_28_in_operatorCall991); 

					IFunction func = new OperatorFunction(op!=null? op: IOperator.EQUAL);
					fc = new FunctionCall(func, new Object[]{exp1, exp2});
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return fc;
	}
	// $ANTLR end "operatorCall"



	// $ANTLR start "parameter"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:404:1: parameter[OAVTypeModel tmodel, Map vars] returns [Object val] : (tmp1= constant |tmp2= variable[null, vars] |{...}?tmp3= functionCall[tmodel, vars] |tmp4= operatorCall[tmodel, vars] );
	public final Object parameter(OAVTypeModel tmodel, Map vars) throws RecognitionException {
		Object val = null;


		Object tmp1 =null;
		Variable tmp2 =null;
		FunctionCall tmp3 =null;
		FunctionCall tmp4 =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:405:2: (tmp1= constant |tmp2= variable[null, vars] |{...}?tmp3= functionCall[tmodel, vars] |tmp4= operatorCall[tmodel, vars] )
			int alt22=4;
			switch ( input.LA(1) ) {
			case BooleanLiteral:
			case CharacterLiteral:
			case DecimalLiteral:
			case FloatingPointLiteral:
			case HexLiteral:
			case OctalLiteral:
			case StringLiteral:
			case 29:
			case 30:
			case 48:
				{
				alt22=1;
				}
				break;
			case 26:
			case 40:
				{
				alt22=2;
				}
				break;
			case 27:
				{
				int LA22_12 = input.LA(2);
				if ( ((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {
					alt22=3;
				}
				else if ( (true) ) {
					alt22=4;
				}

				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 22, 0, input);
				throw nvae;
			}
			switch (alt22) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:405:4: tmp1= constant
					{
					pushFollow(FOLLOW_constant_in_parameter1014);
					tmp1=constant();
					state._fsp--;

					val = new Constant(tmp1);
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:406:4: tmp2= variable[null, vars]
					{
					pushFollow(FOLLOW_variable_in_parameter1024);
					tmp2=variable(null, vars);
					state._fsp--;

					val = tmp2;
					}
					break;
				case 3 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:407:4: {...}?tmp3= functionCall[tmodel, vars]
					{
					if ( !((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {
						throw new FailedPredicateException(input, "parameter", "SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)");
					}
					pushFollow(FOLLOW_functionCall_in_parameter1036);
					tmp3=functionCall(tmodel, vars);
					state._fsp--;

					val = tmp3;
					}
					break;
				case 4 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:408:4: tmp4= operatorCall[tmodel, vars]
					{
					pushFollow(FOLLOW_operatorCall_in_parameter1046);
					tmp4=operatorCall(tmodel, vars);
					state._fsp--;

					val = tmp4;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return val;
	}
	// $ANTLR end "parameter"



	// $ANTLR start "constant"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:411:1: constant returns [Object val] : tmp= literal ;
	public final Object constant() throws RecognitionException {
		Object val = null;


		Object tmp =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:412:2: (tmp= literal )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:412:4: tmp= literal
			{
			pushFollow(FOLLOW_literal_in_constant1067);
			tmp=literal();
			state._fsp--;

			val = tmp;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return val;
	}
	// $ANTLR end "constant"



	// $ANTLR start "variable"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:415:1: variable[OAVObjectType type, Map vars] returns [Variable var] : (tmp= singleFieldVariable[type, vars] |tmp= multiFieldVariable[type, vars] );
	public final Variable variable(OAVObjectType type, Map vars) throws RecognitionException {
		Variable var = null;


		Variable tmp =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:416:2: (tmp= singleFieldVariable[type, vars] |tmp= multiFieldVariable[type, vars] )
			int alt23=2;
			int LA23_0 = input.LA(1);
			if ( (LA23_0==40) ) {
				alt23=1;
			}
			else if ( (LA23_0==26) ) {
				alt23=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 23, 0, input);
				throw nvae;
			}

			switch (alt23) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:416:4: tmp= singleFieldVariable[type, vars]
					{
					pushFollow(FOLLOW_singleFieldVariable_in_variable1090);
					tmp=singleFieldVariable(type, vars);
					state._fsp--;

					var = tmp;
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:417:4: tmp= multiFieldVariable[type, vars]
					{
					pushFollow(FOLLOW_multiFieldVariable_in_variable1100);
					tmp=multiFieldVariable(type, vars);
					state._fsp--;

					var = tmp;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return var;
	}
	// $ANTLR end "variable"



	// $ANTLR start "singleFieldVariable"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:420:1: singleFieldVariable[OAVObjectType type, Map vars] returns [Variable var] : '?' id= identifier ;
	public final Variable singleFieldVariable(OAVObjectType type, Map vars) throws RecognitionException {
		Variable var = null;


		Token id =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:421:2: ( '?' id= identifier )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:421:4: '?' id= identifier
			{
			match(input,40,FOLLOW_40_in_singleFieldVariable1120); 
			pushFollow(FOLLOW_identifier_in_singleFieldVariable1124);
			id=identifier();
			state._fsp--;

				
					String vn = "?"+id.getText();
					var = (Variable)vars.get(vn);
					if(var==null)
					{
						var = new Variable(vn, type);
						vars.put(vn, var);
					}
					else if(type!=null)
					{
						SConditions.adaptConditionType(var, type);
					}
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return var;
	}
	// $ANTLR end "singleFieldVariable"



	// $ANTLR start "multiFieldVariable"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:437:1: multiFieldVariable[OAVObjectType type, Map vars] returns [Variable var] : '$?' id= identifier ;
	public final Variable multiFieldVariable(OAVObjectType type, Map vars) throws RecognitionException {
		Variable var = null;


		Token id =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:438:2: ( '$?' id= identifier )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:438:4: '$?' id= identifier
			{
			match(input,26,FOLLOW_26_in_multiFieldVariable1146); 
			pushFollow(FOLLOW_identifier_in_multiFieldVariable1150);
			id=identifier();
			state._fsp--;


					String vn = "$?"+id.getText();
					var = (Variable)vars.get(vn);
					if(var==null)
					{
						var = new Variable(vn, type, true, false);
						vars.put(vn, var);
					}
					else if(type!=null)
					{
						SConditions.adaptConditionType(var, type);
					}
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return var;
	}
	// $ANTLR end "multiFieldVariable"



	// $ANTLR start "typename"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:454:1: typename returns [String id] :tmp= identifier ( '.' tmp= identifier )* ;
	public final String typename() throws RecognitionException {
		String id = null;


		Token tmp =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:455:2: (tmp= identifier ( '.' tmp= identifier )* )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:456:2: tmp= identifier ( '.' tmp= identifier )*
			{

					StringBuffer buf = new StringBuffer();
				
			pushFollow(FOLLOW_identifier_in_typename1176);
			tmp=identifier();
			state._fsp--;


					buf.append(tmp.getText());
				
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:463:2: ( '.' tmp= identifier )*
			loop24:
			while (true) {
				int alt24=2;
				int LA24_0 = input.LA(1);
				if ( (LA24_0==31) ) {
					alt24=1;
				}

				switch (alt24) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:463:3: '.' tmp= identifier
					{
					match(input,31,FOLLOW_31_in_typename1184); 
					pushFollow(FOLLOW_identifier_in_typename1188);
					tmp=identifier();
					state._fsp--;


							buf.append(".").append(tmp.getText());
						
					}
					break;

				default :
					break loop24;
				}
			}


					id = buf.toString();
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return id;
	}
	// $ANTLR end "typename"



	// $ANTLR start "slotname"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:480:1: slotname returns [String id] :tmp= identifier ( '.' tmp= identifier | ':' tmp= identifier | ( '[' tmp= identifier ']' ) | ( '[' StringLiteral ']' ) )* ;
	public final String slotname() throws RecognitionException {
		String id = null;


		Token StringLiteral2=null;
		Token tmp =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:481:2: (tmp= identifier ( '.' tmp= identifier | ':' tmp= identifier | ( '[' tmp= identifier ']' ) | ( '[' StringLiteral ']' ) )* )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:482:2: tmp= identifier ( '.' tmp= identifier | ':' tmp= identifier | ( '[' tmp= identifier ']' ) | ( '[' StringLiteral ']' ) )*
			{

					StringBuffer buf = new StringBuffer();
				
			pushFollow(FOLLOW_identifier_in_slotname1223);
			tmp=identifier();
			state._fsp--;


					buf.append(tmp.getText());
				
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:489:2: ( '.' tmp= identifier | ':' tmp= identifier | ( '[' tmp= identifier ']' ) | ( '[' StringLiteral ']' ) )*
			loop25:
			while (true) {
				int alt25=5;
				switch ( input.LA(1) ) {
				case 32:
					{
					int LA25_2 = input.LA(2);
					if ( (LA25_2==Identifiertoken||(LA25_2 >= 43 && LA25_2 <= 47)||LA25_2==49) ) {
						alt25=2;
					}

					}
					break;
				case 31:
					{
					alt25=1;
					}
					break;
				case 41:
					{
					int LA25_4 = input.LA(2);
					if ( (LA25_4==StringLiteral) ) {
						alt25=4;
					}
					else if ( (LA25_4==Identifiertoken||(LA25_4 >= 43 && LA25_4 <= 47)||LA25_4==49) ) {
						alt25=3;
					}

					}
					break;
				}
				switch (alt25) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:489:3: '.' tmp= identifier
					{
					match(input,31,FOLLOW_31_in_slotname1231); 
					pushFollow(FOLLOW_identifier_in_slotname1235);
					tmp=identifier();
					state._fsp--;


							buf.append(".").append(tmp.getText());
						
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:493:3: ':' tmp= identifier
					{
					match(input,32,FOLLOW_32_in_slotname1242); 
					pushFollow(FOLLOW_identifier_in_slotname1246);
					tmp=identifier();
					state._fsp--;


							buf.append(":").append(tmp.getText());
						
					}
					break;
				case 3 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:497:3: ( '[' tmp= identifier ']' )
					{
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:497:3: ( '[' tmp= identifier ']' )
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:497:4: '[' tmp= identifier ']'
					{
					match(input,41,FOLLOW_41_in_slotname1254); 
					pushFollow(FOLLOW_identifier_in_slotname1258);
					tmp=identifier();
					state._fsp--;

					match(input,42,FOLLOW_42_in_slotname1260); 
					}


							buf.append("[").append(tmp.getText()).append("]");
						
					}
					break;
				case 4 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:501:3: ( '[' StringLiteral ']' )
					{
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:501:3: ( '[' StringLiteral ']' )
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:501:4: '[' StringLiteral ']'
					{
					match(input,41,FOLLOW_41_in_slotname1270); 
					StringLiteral2=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_slotname1272); 
					match(input,42,FOLLOW_42_in_slotname1274); 
					}


							// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
							String	text	= (StringLiteral2!=null?StringLiteral2.getText():null);
							buf.append("[").append(text!=null ? text.substring(1, text.length()-1) : null).append("]");
						
					}
					break;

				default :
					break loop25;
				}
			}


					id = buf.toString();
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return id;
	}
	// $ANTLR end "slotname"



	// $ANTLR start "methodname"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:514:1: methodname returns [String id] : tmp= identifier ;
	public final String methodname() throws RecognitionException {
		String id = null;


		Token tmp =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:515:2: (tmp= identifier )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:515:4: tmp= identifier
			{
			pushFollow(FOLLOW_identifier_in_methodname1305);
			tmp=identifier();
			state._fsp--;

			id = tmp.getText();
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return id;
	}
	// $ANTLR end "methodname"



	// $ANTLR start "functionName"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:518:1: functionName returns [String id] : tmp= typename ;
	public final String functionName() throws RecognitionException {
		String id = null;


		String tmp =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:519:2: (tmp= typename )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:519:4: tmp= typename
			{
			pushFollow(FOLLOW_typename_in_functionName1324);
			tmp=typename();
			state._fsp--;

			id = tmp;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return id;
	}
	// $ANTLR end "functionName"



	// $ANTLR start "literal"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:522:1: literal returns [Object val] : (lit= floatingPointLiteral |lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );
	public final Object literal() throws RecognitionException {
		Object val = null;


		Token CharacterLiteral3=null;
		Token StringLiteral4=null;
		Token BooleanLiteral5=null;
		Object lit =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:523:2: (lit= floatingPointLiteral |lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' )
			int alt26=6;
			switch ( input.LA(1) ) {
			case 29:
			case 30:
				{
				int LA26_1 = input.LA(2);
				if ( (LA26_1==FloatingPointLiteral) ) {
					alt26=1;
				}
				else if ( (LA26_1==DecimalLiteral||LA26_1==HexLiteral||LA26_1==OctalLiteral) ) {
					alt26=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 26, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case FloatingPointLiteral:
				{
				alt26=1;
				}
				break;
			case DecimalLiteral:
			case HexLiteral:
			case OctalLiteral:
				{
				alt26=2;
				}
				break;
			case CharacterLiteral:
				{
				alt26=3;
				}
				break;
			case StringLiteral:
				{
				alt26=4;
				}
				break;
			case BooleanLiteral:
				{
				alt26=5;
				}
				break;
			case 48:
				{
				alt26=6;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 26, 0, input);
				throw nvae;
			}
			switch (alt26) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:523:4: lit= floatingPointLiteral
					{
					pushFollow(FOLLOW_floatingPointLiteral_in_literal1344);
					lit=floatingPointLiteral();
					state._fsp--;

					val = lit;
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:524:4: lit= integerLiteral
					{
					pushFollow(FOLLOW_integerLiteral_in_literal1353);
					lit=integerLiteral();
					state._fsp--;

					val = lit;
					}
					break;
				case 3 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:525:4: CharacterLiteral
					{
					CharacterLiteral3=(Token)match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal1360); 

							// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
							String	text	= (CharacterLiteral3!=null?CharacterLiteral3.getText():null);
							val = text==null ? null : Character.valueOf(text.charAt(0));
						
					}
					break;
				case 4 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:531:4: StringLiteral
					{
					StringLiteral4=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_literal1368); 

							// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
							String	text	= (StringLiteral4!=null?StringLiteral4.getText():null);
							val = text==null ? null : text.substring(1, text.length()-1);
						
					}
					break;
				case 5 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:537:4: BooleanLiteral
					{
					BooleanLiteral5=(Token)match(input,BooleanLiteral,FOLLOW_BooleanLiteral_in_literal1376); 

							// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
							String	text	= (BooleanLiteral5!=null?BooleanLiteral5.getText():null);
							val = text==null ? null : text.equals("true")? Boolean.TRUE: Boolean.FALSE;
						
					}
					break;
				case 6 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:543:4: 'null'
					{
					match(input,48,FOLLOW_48_in_literal1384); 
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
			// do for sure before leaving
		}
		return val;
	}
	// $ANTLR end "literal"



	// $ANTLR start "floatingPointLiteral"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:546:1: floatingPointLiteral returns [Object val] : sign= ( '+' | '-' )? FloatingPointLiteral ;
	public final Object floatingPointLiteral() throws RecognitionException {
		Object val = null;


		Token sign=null;
		Token FloatingPointLiteral6=null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:547:2: (sign= ( '+' | '-' )? FloatingPointLiteral )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:547:4: sign= ( '+' | '-' )? FloatingPointLiteral
			{
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:547:9: ( '+' | '-' )?
			int alt27=2;
			int LA27_0 = input.LA(1);
			if ( ((LA27_0 >= 29 && LA27_0 <= 30)) ) {
				alt27=1;
			}
			switch (alt27) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:
					{
					if ( (input.LA(1) >= 29 && input.LA(1) <= 30) ) {
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

			FloatingPointLiteral6=(Token)match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1410); 

					// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
					String	text	= (FloatingPointLiteral6!=null?FloatingPointLiteral6.getText():null);
					val = text==null ? null : sign!=null && "-".equals(sign.getText())? Double.valueOf("-"+text): Double.valueOf(text);
				
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return val;
	}
	// $ANTLR end "floatingPointLiteral"



	// $ANTLR start "integerLiteral"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:555:1: integerLiteral returns [Object val] : sign= ( '+' | '-' )? ( HexLiteral | OctalLiteral | DecimalLiteral ) ;
	public final Object integerLiteral() throws RecognitionException {
		Object val = null;


		Token sign=null;
		Token HexLiteral7=null;
		Token OctalLiteral8=null;
		Token DecimalLiteral9=null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:556:2: (sign= ( '+' | '-' )? ( HexLiteral | OctalLiteral | DecimalLiteral ) )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:556:4: sign= ( '+' | '-' )? ( HexLiteral | OctalLiteral | DecimalLiteral )
			{
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:556:9: ( '+' | '-' )?
			int alt28=2;
			int LA28_0 = input.LA(1);
			if ( ((LA28_0 >= 29 && LA28_0 <= 30)) ) {
				alt28=1;
			}
			switch (alt28) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:
					{
					if ( (input.LA(1) >= 29 && input.LA(1) <= 30) ) {
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

			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:557:2: ( HexLiteral | OctalLiteral | DecimalLiteral )
			int alt29=3;
			switch ( input.LA(1) ) {
			case HexLiteral:
				{
				alt29=1;
				}
				break;
			case OctalLiteral:
				{
				alt29=2;
				}
				break;
			case DecimalLiteral:
				{
				alt29=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 29, 0, input);
				throw nvae;
			}
			switch (alt29) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:557:4: HexLiteral
					{
					HexLiteral7=(Token)match(input,HexLiteral,FOLLOW_HexLiteral_in_integerLiteral1441); 

							// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
							String	text	= (HexLiteral7!=null?HexLiteral7.getText():null);
							val = text==null ? null : sign!=null && "-".equals(sign.getText())? Integer.valueOf("-"+text): Integer.valueOf(text);
						
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:563:4: OctalLiteral
					{
					OctalLiteral8=(Token)match(input,OctalLiteral,FOLLOW_OctalLiteral_in_integerLiteral1449); 

							// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
							String	text	= (OctalLiteral8!=null?OctalLiteral8.getText():null);
							val = text==null ? null : sign!=null && "-".equals(sign.getText())? Integer.valueOf("-"+text): Integer.valueOf(text);
						
					}
					break;
				case 3 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:569:4: DecimalLiteral
					{
					DecimalLiteral9=(Token)match(input,DecimalLiteral,FOLLOW_DecimalLiteral_in_integerLiteral1457); 

							// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
							String	text	= (DecimalLiteral9!=null?DecimalLiteral9.getText():null);
							val = text==null ? null : sign!=null && "-".equals(sign.getText())? Integer.valueOf("-"+text): Integer.valueOf(text);
						
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
			// do for sure before leaving
		}
		return val;
	}
	// $ANTLR end "integerLiteral"



	// $ANTLR start "operator"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:577:1: operator returns [IOperator operator] : (tmp= equalOperator | '!=' | '~' | '>' | '<' | '>=' | '<=' | 'contains' | 'excludes' );
	public final IOperator operator() throws RecognitionException {
		IOperator operator = null;


		IOperator tmp =null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:578:2: (tmp= equalOperator | '!=' | '~' | '>' | '<' | '>=' | '<=' | 'contains' | 'excludes' )
			int alt30=9;
			switch ( input.LA(1) ) {
			case 37:
				{
				alt30=1;
				}
				break;
			case 25:
				{
				alt30=2;
				}
				break;
			case 50:
				{
				alt30=3;
				}
				break;
			case 38:
				{
				alt30=4;
				}
				break;
			case 33:
				{
				alt30=5;
				}
				break;
			case 39:
				{
				alt30=6;
				}
				break;
			case 35:
				{
				alt30=7;
				}
				break;
			case 45:
				{
				alt30=8;
				}
				break;
			case 46:
				{
				alt30=9;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 30, 0, input);
				throw nvae;
			}
			switch (alt30) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:578:4: tmp= equalOperator
					{
					pushFollow(FOLLOW_equalOperator_in_operator1478);
					tmp=equalOperator();
					state._fsp--;

					operator = tmp;
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:579:4: '!='
					{
					match(input,25,FOLLOW_25_in_operator1486); 
					operator = IOperator.NOTEQUAL;
					}
					break;
				case 3 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:580:4: '~'
					{
					match(input,50,FOLLOW_50_in_operator1493); 
					operator = IOperator.NOTEQUAL;
					}
					break;
				case 4 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:581:4: '>'
					{
					match(input,38,FOLLOW_38_in_operator1500); 
					operator = IOperator.GREATER;
					}
					break;
				case 5 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:582:4: '<'
					{
					match(input,33,FOLLOW_33_in_operator1507); 
					operator = IOperator.LESS;
					}
					break;
				case 6 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:583:4: '>='
					{
					match(input,39,FOLLOW_39_in_operator1514); 
					operator = IOperator.GREATEROREQUAL;
					}
					break;
				case 7 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:584:4: '<='
					{
					match(input,35,FOLLOW_35_in_operator1521); 
					operator = IOperator.LESSOREQUAL;
					}
					break;
				case 8 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:585:4: 'contains'
					{
					match(input,45,FOLLOW_45_in_operator1528); 
					operator = IOperator.CONTAINS;
					}
					break;
				case 9 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:586:4: 'excludes'
					{
					match(input,46,FOLLOW_46_in_operator1535); 
					operator = IOperator.EXCLUDES;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return operator;
	}
	// $ANTLR end "operator"



	// $ANTLR start "equalOperator"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:589:1: equalOperator returns [IOperator operator] : '==' ;
	public final IOperator equalOperator() throws RecognitionException {
		IOperator operator = null;


		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:590:2: ( '==' )
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:590:4: '=='
			{
			match(input,37,FOLLOW_37_in_equalOperator1552); 
			operator = IOperator.EQUAL;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return operator;
	}
	// $ANTLR end "equalOperator"



	// $ANTLR start "identifier"
	// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:593:1: identifier returns [Token identifier] : (tmp= Identifiertoken |tmp= 'test' |tmp= 'not' |tmp= 'and' |tmp= 'collect' |tmp= 'contains' |tmp= 'excludes' );
	public final Token identifier() throws RecognitionException {
		Token identifier = null;


		Token tmp=null;

		try {
			// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:594:2: (tmp= Identifiertoken |tmp= 'test' |tmp= 'not' |tmp= 'and' |tmp= 'collect' |tmp= 'contains' |tmp= 'excludes' )
			int alt31=7;
			switch ( input.LA(1) ) {
			case Identifiertoken:
				{
				alt31=1;
				}
				break;
			case 49:
				{
				alt31=2;
				}
				break;
			case 47:
				{
				alt31=3;
				}
				break;
			case 43:
				{
				alt31=4;
				}
				break;
			case 44:
				{
				alt31=5;
				}
				break;
			case 45:
				{
				alt31=6;
				}
				break;
			case 46:
				{
				alt31=7;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 31, 0, input);
				throw nvae;
			}
			switch (alt31) {
				case 1 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:594:4: tmp= Identifiertoken
					{
					tmp=(Token)match(input,Identifiertoken,FOLLOW_Identifiertoken_in_identifier1572); 
					identifier = tmp;
					}
					break;
				case 2 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:595:4: tmp= 'test'
					{
					tmp=(Token)match(input,49,FOLLOW_49_in_identifier1581); 
					identifier = tmp;
					}
					break;
				case 3 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:596:4: tmp= 'not'
					{
					tmp=(Token)match(input,47,FOLLOW_47_in_identifier1590); 
					identifier = tmp;
					}
					break;
				case 4 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:597:4: tmp= 'and'
					{
					tmp=(Token)match(input,43,FOLLOW_43_in_identifier1599); 
					identifier = tmp;
					}
					break;
				case 5 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:598:4: tmp= 'collect'
					{
					tmp=(Token)match(input,44,FOLLOW_44_in_identifier1608); 
					identifier = tmp;
					}
					break;
				case 6 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:599:4: tmp= 'contains'
					{
					tmp=(Token)match(input,45,FOLLOW_45_in_identifier1617); 
					identifier = tmp;
					}
					break;
				case 7 :
					// C:\\Files\\Checkouts\\jadex\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:600:4: tmp= 'excludes'
					{
					tmp=(Token)match(input,46,FOLLOW_46_in_identifier1626); 
					identifier = tmp;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return identifier;
	}
	// $ANTLR end "identifier"

	// Delegated rules



	public static final BitSet FOLLOW_ce_in_rhs53 = new BitSet(new long[]{0x000001000C000000L});
	public static final BitSet FOLLOW_EOF_in_rhs68 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_andce_in_ce93 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_notce_in_ce109 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_testce_in_ce125 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_collectce_in_ce137 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_objectce_in_ce151 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_27_in_andce175 = new BitSet(new long[]{0x0000080000000000L});
	public static final BitSet FOLLOW_43_in_andce177 = new BitSet(new long[]{0x000001000C000000L});
	public static final BitSet FOLLOW_ce_in_andce182 = new BitSet(new long[]{0x000001001C000000L});
	public static final BitSet FOLLOW_28_in_andce192 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_27_in_notce211 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_notce213 = new BitSet(new long[]{0x000001000C000000L});
	public static final BitSet FOLLOW_ce_in_notce217 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_28_in_notce220 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_27_in_testce239 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_49_in_testce241 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_operatorCall_in_testce249 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_functionCall_in_testce261 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_28_in_testce269 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_multiFieldVariable_in_collectce296 = new BitSet(new long[]{0x0000001400000000L});
	public static final BitSet FOLLOW_set_in_collectce299 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_27_in_collectce311 = new BitSet(new long[]{0x0000100000000000L});
	public static final BitSet FOLLOW_44_in_collectce313 = new BitSet(new long[]{0x000001000C000000L});
	public static final BitSet FOLLOW_ce_in_collectce318 = new BitSet(new long[]{0x000001011C000000L});
	public static final BitSet FOLLOW_predicateConstraint_in_collectce331 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_28_in_collectce335 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_singleFieldVariable_in_objectce364 = new BitSet(new long[]{0x0000001400000000L});
	public static final BitSet FOLLOW_set_in_objectce367 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_27_in_objectce379 = new BitSet(new long[]{0x0002F80000008000L});
	public static final BitSet FOLLOW_typename_in_objectce385 = new BitSet(new long[]{0x0000000018000000L});
	public static final BitSet FOLLOW_attributeConstraint_in_objectce394 = new BitSet(new long[]{0x0000000018000000L});
	public static final BitSet FOLLOW_methodConstraint_in_objectce405 = new BitSet(new long[]{0x0000000018000000L});
	public static final BitSet FOLLOW_functionConstraint_in_objectce416 = new BitSet(new long[]{0x0000000018000000L});
	public static final BitSet FOLLOW_28_in_objectce428 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_27_in_attributeConstraint450 = new BitSet(new long[]{0x0002F80000008000L});
	public static final BitSet FOLLOW_slotname_in_attributeConstraint454 = new BitSet(new long[]{0x000561EB66605150L});
	public static final BitSet FOLLOW_constraint_in_attributeConstraint458 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_28_in_attributeConstraint461 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_27_in_methodConstraint488 = new BitSet(new long[]{0x0002F80000008000L});
	public static final BitSet FOLLOW_methodname_in_methodConstraint492 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_27_in_methodConstraint494 = new BitSet(new long[]{0x000101007C605150L});
	public static final BitSet FOLLOW_parameter_in_methodConstraint498 = new BitSet(new long[]{0x000101007C605150L});
	public static final BitSet FOLLOW_28_in_methodConstraint508 = new BitSet(new long[]{0x000561EB66605150L});
	public static final BitSet FOLLOW_constraint_in_methodConstraint512 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_28_in_methodConstraint515 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_27_in_functionConstraint539 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_functionCall_in_functionConstraint543 = new BitSet(new long[]{0x000561EB66605150L});
	public static final BitSet FOLLOW_constraint_in_functionConstraint548 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_28_in_functionConstraint551 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_40_in_constraint581 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_26_in_constraint587 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_singleConstraint_in_constraint595 = new BitSet(new long[]{0x0000000000000082L});
	public static final BitSet FOLLOW_ConstraintOperator_in_constraint604 = new BitSet(new long[]{0x000561EB66605150L});
	public static final BitSet FOLLOW_singleConstraint_in_constraint608 = new BitSet(new long[]{0x0000000000000082L});
	public static final BitSet FOLLOW_literalConstraint_in_singleConstraint640 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_boundConstraint_in_singleConstraint651 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_multiBoundConstraint_in_singleConstraint661 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_predicateConstraint_in_singleConstraint671 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_returnValueConstraint_in_singleConstraint681 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_operator_in_literalConstraint703 = new BitSet(new long[]{0x0001000060605150L});
	public static final BitSet FOLLOW_constant_in_literalConstraint708 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_boundConstraint_in_someBoundConstraint731 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_multiBoundConstraint_in_someBoundConstraint744 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_operator_in_boundConstraint767 = new BitSet(new long[]{0x0000010004000000L});
	public static final BitSet FOLLOW_variable_in_boundConstraint772 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_operator_in_multiBoundConstraint800 = new BitSet(new long[]{0x0000010004000000L});
	public static final BitSet FOLLOW_variable_in_multiBoundConstraint805 = new BitSet(new long[]{0x0000010004000000L});
	public static final BitSet FOLLOW_variable_in_multiBoundConstraint815 = new BitSet(new long[]{0x0000010004000002L});
	public static final BitSet FOLLOW_32_in_predicateConstraint843 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_functionCall_in_predicateConstraint850 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_operatorCall_in_predicateConstraint861 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_equalOperator_in_returnValueConstraint885 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_functionCall_in_returnValueConstraint892 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_operatorCall_in_returnValueConstraint903 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_27_in_functionCall933 = new BitSet(new long[]{0x0002F80000008000L});
	public static final BitSet FOLLOW_functionName_in_functionCall937 = new BitSet(new long[]{0x000101007C605150L});
	public static final BitSet FOLLOW_parameter_in_functionCall942 = new BitSet(new long[]{0x000101007C605150L});
	public static final BitSet FOLLOW_28_in_functionCall952 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_27_in_operatorCall973 = new BitSet(new long[]{0x000460EA02000000L});
	public static final BitSet FOLLOW_operator_in_operatorCall978 = new BitSet(new long[]{0x000101006C605150L});
	public static final BitSet FOLLOW_parameter_in_operatorCall983 = new BitSet(new long[]{0x000101006C605150L});
	public static final BitSet FOLLOW_parameter_in_operatorCall988 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_28_in_operatorCall991 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_constant_in_parameter1014 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_variable_in_parameter1024 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_functionCall_in_parameter1036 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_operatorCall_in_parameter1046 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_literal_in_constant1067 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_singleFieldVariable_in_variable1090 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_multiFieldVariable_in_variable1100 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_40_in_singleFieldVariable1120 = new BitSet(new long[]{0x0002F80000008000L});
	public static final BitSet FOLLOW_identifier_in_singleFieldVariable1124 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_26_in_multiFieldVariable1146 = new BitSet(new long[]{0x0002F80000008000L});
	public static final BitSet FOLLOW_identifier_in_multiFieldVariable1150 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifier_in_typename1176 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_31_in_typename1184 = new BitSet(new long[]{0x0002F80000008000L});
	public static final BitSet FOLLOW_identifier_in_typename1188 = new BitSet(new long[]{0x0000000080000002L});
	public static final BitSet FOLLOW_identifier_in_slotname1223 = new BitSet(new long[]{0x0000020180000002L});
	public static final BitSet FOLLOW_31_in_slotname1231 = new BitSet(new long[]{0x0002F80000008000L});
	public static final BitSet FOLLOW_identifier_in_slotname1235 = new BitSet(new long[]{0x0000020180000002L});
	public static final BitSet FOLLOW_32_in_slotname1242 = new BitSet(new long[]{0x0002F80000008000L});
	public static final BitSet FOLLOW_identifier_in_slotname1246 = new BitSet(new long[]{0x0000020180000002L});
	public static final BitSet FOLLOW_41_in_slotname1254 = new BitSet(new long[]{0x0002F80000008000L});
	public static final BitSet FOLLOW_identifier_in_slotname1258 = new BitSet(new long[]{0x0000040000000000L});
	public static final BitSet FOLLOW_42_in_slotname1260 = new BitSet(new long[]{0x0000020180000002L});
	public static final BitSet FOLLOW_41_in_slotname1270 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_StringLiteral_in_slotname1272 = new BitSet(new long[]{0x0000040000000000L});
	public static final BitSet FOLLOW_42_in_slotname1274 = new BitSet(new long[]{0x0000020180000002L});
	public static final BitSet FOLLOW_identifier_in_methodname1305 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_typename_in_functionName1324 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_floatingPointLiteral_in_literal1344 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_integerLiteral_in_literal1353 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CharacterLiteral_in_literal1360 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_StringLiteral_in_literal1368 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BooleanLiteral_in_literal1376 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_48_in_literal1384 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1410 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_HexLiteral_in_integerLiteral1441 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OctalLiteral_in_integerLiteral1449 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DecimalLiteral_in_integerLiteral1457 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_equalOperator_in_operator1478 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_25_in_operator1486 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_50_in_operator1493 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_38_in_operator1500 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_33_in_operator1507 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_39_in_operator1514 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_35_in_operator1521 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_45_in_operator1528 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_46_in_operator1535 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_37_in_equalOperator1552 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Identifiertoken_in_identifier1572 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_49_in_identifier1581 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_47_in_identifier1590 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_43_in_identifier1599 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_44_in_identifier1608 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_45_in_identifier1617 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_46_in_identifier1626 = new BitSet(new long[]{0x0000000000000002L});
}
