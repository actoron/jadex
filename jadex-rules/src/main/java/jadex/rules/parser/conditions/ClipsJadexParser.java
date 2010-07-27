// $ANTLR 3.1.2 C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g 2009-04-06 11:25:09

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
import java.util.ArrayList;

public class ClipsJadexParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ConstraintOperator", "StringLiteral", "CharacterLiteral", "BooleanLiteral", "FloatingPointLiteral", "HexLiteral", "OctalLiteral", "DecimalLiteral", "Identifiertoken", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "COMMENT", "LINE_COMMENT", "'('", "'and'", "')'", "'not'", "'test'", "'<-'", "'='", "'collect'", "'?'", "'$?'", "':'", "'.'", "'['", "']'", "'null'", "'+'", "'-'", "'!='", "'~'", "'>'", "'<'", "'>='", "'<='", "'contains'", "'excludes'", "'=='"
    };
    public static final int T__29=29;
    public static final int T__28=28;
    public static final int T__27=27;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int FloatTypeSuffix=16;
    public static final int OctalLiteral=10;
    public static final int CharacterLiteral=6;
    public static final int Exponent=15;
    public static final int EOF=-1;
    public static final int HexDigit=13;
    public static final int COMMENT=23;
    public static final int T__50=50;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int HexLiteral=9;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int LINE_COMMENT=24;
    public static final int IntegerTypeSuffix=14;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int DecimalLiteral=11;
    public static final int StringLiteral=5;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int WS=22;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int UnicodeEscape=18;
    public static final int ConstraintOperator=4;
    public static final int FloatingPointLiteral=8;
    public static final int JavaIDDigit=21;
    public static final int Identifiertoken=12;
    public static final int Letter=20;
    public static final int OctalEscape=19;
    public static final int EscapeSequence=17;
    public static final int BooleanLiteral=7;

    // delegates
    // delegators


        public ClipsJadexParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public ClipsJadexParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return ClipsJadexParser.tokenNames; }
    public String getGrammarFileName() { return "C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g"; }


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
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:59:1: rhs[OAVTypeModel tmodel] returns [ICondition condition] : (c= ce[tmodel, vars] )+ EOF ;
    public final ICondition rhs(OAVTypeModel tmodel) throws RecognitionException {
        ICondition condition = null;

        ICondition c = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:60:2: ( (c= ce[tmodel, vars] )+ EOF )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:61:2: (c= ce[tmodel, vars] )+ EOF
            {

            		List conds = new ArrayList();
            		Map vars = new HashMap();
            		
            	
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:66:2: (c= ce[tmodel, vars] )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==25||(LA1_0>=33 && LA1_0<=34)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:66:3: c= ce[tmodel, vars]
            	    {
            	    pushFollow(FOLLOW_ce_in_rhs53);
            	    c=ce(tmodel, vars);

            	    state._fsp--;


            	    		conds.add(c);
            	    	

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


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
        }
        return condition;
    }
    // $ANTLR end "rhs"


    // $ANTLR start "ce"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:80:1: ce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : ({...}?tmp= andce[tmodel, vars] | {...}?tmp= notce[tmodel, vars] | {...}?tmp= testce[tmodel, vars] | tmp= collectce[tmodel, vars] | {...}?tmp= objectce[tmodel, vars] );
    public final ICondition ce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        ICondition tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:81:2: ({...}?tmp= andce[tmodel, vars] | {...}?tmp= notce[tmodel, vars] | {...}?tmp= testce[tmodel, vars] | tmp= collectce[tmodel, vars] | {...}?tmp= objectce[tmodel, vars] )
            int alt2=5;
            switch ( input.LA(1) ) {
            case 25:
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
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 1, input);

                    throw nvae;
                }
                }
                break;
            case 34:
                {
                alt2=4;
                }
                break;
            case 33:
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:81:4: {...}?tmp= andce[tmodel, vars]
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:83:4: {...}?tmp= notce[tmodel, vars]
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:85:4: {...}?tmp= testce[tmodel, vars]
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:87:4: tmp= collectce[tmodel, vars]
                    {
                    pushFollow(FOLLOW_collectce_in_ce137);
                    tmp=collectce(tmodel, vars);

                    state._fsp--;

                    condition = tmp;

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:88:4: {...}?tmp= objectce[tmodel, vars]
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
        }
        return condition;
    }
    // $ANTLR end "ce"


    // $ANTLR start "andce"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:91:1: andce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : '(' 'and' (c= ce[$tmodel, vars] )+ ')' ;
    public final ICondition andce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        ICondition c = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:92:2: ( '(' 'and' (c= ce[$tmodel, vars] )+ ')' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:93:2: '(' 'and' (c= ce[$tmodel, vars] )+ ')'
            {

            		List conds = new ArrayList();
            	
            match(input,25,FOLLOW_25_in_andce175); 
            match(input,26,FOLLOW_26_in_andce177); 
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:96:12: (c= ce[$tmodel, vars] )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==25||(LA3_0>=33 && LA3_0<=34)) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:96:13: c= ce[$tmodel, vars]
            	    {
            	    pushFollow(FOLLOW_ce_in_andce182);
            	    c=ce(tmodel, vars);

            	    state._fsp--;


            	    		conds.add(c);
            	    	

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

            match(input,27,FOLLOW_27_in_andce192); 

            		condition = new AndCondition(conds);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return condition;
    }
    // $ANTLR end "andce"


    // $ANTLR start "notce"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:106:1: notce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : '(' 'not' c= ce[$tmodel, vars] ')' ;
    public final ICondition notce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        ICondition c = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:107:2: ( '(' 'not' c= ce[$tmodel, vars] ')' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:107:4: '(' 'not' c= ce[$tmodel, vars] ')'
            {
            match(input,25,FOLLOW_25_in_notce211); 
            match(input,28,FOLLOW_28_in_notce213); 
            pushFollow(FOLLOW_ce_in_notce217);
            c=ce(tmodel, vars);

            state._fsp--;

            match(input,27,FOLLOW_27_in_notce220); 

            		condition = new NotCondition(c);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return condition;
    }
    // $ANTLR end "notce"


    // $ANTLR start "testce"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:113:1: testce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : '(' 'test' (call= operatorCall[tmodel, vars] | ({...}?call= functionCall[tmodel, vars] ) ) ')' ;
    public final ICondition testce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        FunctionCall call = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:114:2: ( '(' 'test' (call= operatorCall[tmodel, vars] | ({...}?call= functionCall[tmodel, vars] ) ) ')' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:114:4: '(' 'test' (call= operatorCall[tmodel, vars] | ({...}?call= functionCall[tmodel, vars] ) ) ')'
            {
            match(input,25,FOLLOW_25_in_testce239); 
            match(input,29,FOLLOW_29_in_testce241); 
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:115:3: (call= operatorCall[tmodel, vars] | ({...}?call= functionCall[tmodel, vars] ) )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==25) ) {
                int LA4_1 = input.LA(2);

                if ( (!(((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))))) ) {
                    alt4=1;
                }
                else if ( ((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {
                    alt4=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:115:5: call= operatorCall[tmodel, vars]
                    {
                    pushFollow(FOLLOW_operatorCall_in_testce249);
                    call=operatorCall(tmodel, vars);

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:116:5: ({...}?call= functionCall[tmodel, vars] )
                    {
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:116:5: ({...}?call= functionCall[tmodel, vars] )
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:116:6: {...}?call= functionCall[tmodel, vars]
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

            match(input,27,FOLLOW_27_in_testce269); 

            		condition = new TestCondition(new PredicateConstraint(call));
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return condition;
    }
    // $ANTLR end "testce"


    // $ANTLR start "collectce"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:123:1: collectce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )? '(' 'collect' (c= ce[$tmodel, vars] )+ (pc= predicateConstraint[$tmodel, null, vars] )? ')' ;
    public final ICondition collectce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        Variable mfv = null;

        ICondition c = null;

        IConstraint pc = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:124:2: ( (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )? '(' 'collect' (c= ce[$tmodel, vars] )+ (pc= predicateConstraint[$tmodel, null, vars] )? ')' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:125:2: (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )? '(' 'collect' (c= ce[$tmodel, vars] )+ (pc= predicateConstraint[$tmodel, null, vars] )? ')'
            {

            		List conds = new ArrayList();
            	
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:128:2: (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==34) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:128:3: mfv= multiFieldVariable[null, vars] ( '<-' | '=' )
                    {
                    pushFollow(FOLLOW_multiFieldVariable_in_collectce296);
                    mfv=multiFieldVariable(null, vars);

                    state._fsp--;

                    if ( (input.LA(1)>=30 && input.LA(1)<=31) ) {
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

            match(input,25,FOLLOW_25_in_collectce311); 
            match(input,32,FOLLOW_32_in_collectce313); 
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:129:16: (c= ce[$tmodel, vars] )+
            int cnt6=0;
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==25||(LA6_0>=33 && LA6_0<=34)) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:129:17: c= ce[$tmodel, vars]
            	    {
            	    pushFollow(FOLLOW_ce_in_collectce318);
            	    c=ce(tmodel, vars);

            	    state._fsp--;


            	    		conds.add(c);
            	    	

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

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:133:8: (pc= predicateConstraint[$tmodel, null, vars] )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==35) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:133:8: pc= predicateConstraint[$tmodel, null, vars]
                    {
                    pushFollow(FOLLOW_predicateConstraint_in_collectce331);
                    pc=predicateConstraint(tmodel, null, vars);

                    state._fsp--;


                    }
                    break;

            }

            match(input,27,FOLLOW_27_in_collectce335); 

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
        }
        return condition;
    }
    // $ANTLR end "collectce"


    // $ANTLR start "objectce"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:147:1: objectce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )? '(' tn= typename (acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] )* ')' ;
    public final ICondition objectce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        Variable sfv = null;

        String tn = null;

        List acs = null;

        List mcs = null;

        List fcs = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:148:2: ( (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )? '(' tn= typename (acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] )* ')' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:149:2: (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )? '(' tn= typename (acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] )* ')'
            {

            		List consts = new ArrayList();
            	
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:152:2: (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==33) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:152:3: sfv= singleFieldVariable[null, vars] ( '<-' | '=' )
                    {
                    pushFollow(FOLLOW_singleFieldVariable_in_objectce364);
                    sfv=singleFieldVariable(null, vars);

                    state._fsp--;

                    if ( (input.LA(1)>=30 && input.LA(1)<=31) ) {
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

            match(input,25,FOLLOW_25_in_objectce379); 
            pushFollow(FOLLOW_typename_in_objectce385);
            tn=typename();

            state._fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:156:2: (acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] )*
            loop9:
            do {
                int alt9=4;
                alt9 = dfa9.predict(input);
                switch (alt9) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:156:3: acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars]
            	    {
            	    pushFollow(FOLLOW_attributeConstraint_in_objectce394);
            	    acs=attributeConstraint(tmodel, SConditions.getObjectType(tmodel, tn, imports), vars);

            	    state._fsp--;


            	    		consts.addAll(acs);
            	    	

            	    }
            	    break;
            	case 2 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:160:4: mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars]
            	    {
            	    pushFollow(FOLLOW_methodConstraint_in_objectce405);
            	    mcs=methodConstraint(tmodel, SConditions.getObjectType(tmodel, tn, imports), vars);

            	    state._fsp--;


            	    		consts.addAll(mcs);
            	    	

            	    }
            	    break;
            	case 3 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:164:4: fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars]
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
            } while (true);

            match(input,27,FOLLOW_27_in_objectce428); 

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
        }
        return condition;
    }
    // $ANTLR end "objectce"


    // $ANTLR start "attributeConstraint"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:184:1: attributeConstraint[OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints] : '(' sn= slotname cs= constraint[tmodel, SConditions.convertAttributeTypes(tmodel, otype, sn, imports), vars] ')' ;
    public final List attributeConstraint(OAVTypeModel tmodel, OAVObjectType otype, Map vars) throws RecognitionException {
        List constraints = null;

        String sn = null;

        List cs = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:185:2: ( '(' sn= slotname cs= constraint[tmodel, SConditions.convertAttributeTypes(tmodel, otype, sn, imports), vars] ')' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:185:4: '(' sn= slotname cs= constraint[tmodel, SConditions.convertAttributeTypes(tmodel, otype, sn, imports), vars] ')'
            {
            match(input,25,FOLLOW_25_in_attributeConstraint450); 
            pushFollow(FOLLOW_slotname_in_attributeConstraint454);
            sn=slotname();

            state._fsp--;

            pushFollow(FOLLOW_constraint_in_attributeConstraint458);
            cs=constraint(tmodel, SConditions.convertAttributeTypes(tmodel, otype, sn, imports), vars);

            state._fsp--;

            match(input,27,FOLLOW_27_in_attributeConstraint461); 

            		constraints = cs;
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return constraints;
    }
    // $ANTLR end "attributeConstraint"


    // $ANTLR start "methodConstraint"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:191:1: methodConstraint[OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints] : '(' mn= methodname '(' (exp= parameter[tmodel, vars] )* ')' cs= constraint[tmodel, SConditions.createMethodCall(otype, mn, exps), vars] ')' ;
    public final List methodConstraint(OAVTypeModel tmodel, OAVObjectType otype, Map vars) throws RecognitionException {
        List constraints = null;

        String mn = null;

        Object exp = null;

        List cs = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:192:2: ( '(' mn= methodname '(' (exp= parameter[tmodel, vars] )* ')' cs= constraint[tmodel, SConditions.createMethodCall(otype, mn, exps), vars] ')' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:193:2: '(' mn= methodname '(' (exp= parameter[tmodel, vars] )* ')' cs= constraint[tmodel, SConditions.createMethodCall(otype, mn, exps), vars] ')'
            {

            		List exps = new ArrayList();
            	
            match(input,25,FOLLOW_25_in_methodConstraint488); 
            pushFollow(FOLLOW_methodname_in_methodConstraint492);
            mn=methodname();

            state._fsp--;

            match(input,25,FOLLOW_25_in_methodConstraint494); 
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:196:23: (exp= parameter[tmodel, vars] )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0>=StringLiteral && LA10_0<=DecimalLiteral)||LA10_0==25||(LA10_0>=33 && LA10_0<=34)||(LA10_0>=39 && LA10_0<=41)) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:196:24: exp= parameter[tmodel, vars]
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
            } while (true);

            match(input,27,FOLLOW_27_in_methodConstraint508); 
            pushFollow(FOLLOW_constraint_in_methodConstraint512);
            cs=constraint(tmodel, SConditions.createMethodCall(otype, mn, exps), vars);

            state._fsp--;

            match(input,27,FOLLOW_27_in_methodConstraint515); 

            		constraints = cs;
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return constraints;
    }
    // $ANTLR end "methodConstraint"


    // $ANTLR start "functionConstraint"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:206:1: functionConstraint[OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints] : '(' fc= functionCall[tmodel, vars] cs= constraint[tmodel, fc, vars] ')' ;
    public final List functionConstraint(OAVTypeModel tmodel, OAVObjectType otype, Map vars) throws RecognitionException {
        List constraints = null;

        FunctionCall fc = null;

        List cs = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:207:2: ( '(' fc= functionCall[tmodel, vars] cs= constraint[tmodel, fc, vars] ')' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:208:2: '(' fc= functionCall[tmodel, vars] cs= constraint[tmodel, fc, vars] ')'
            {

            		List exps = new ArrayList();
            	
            match(input,25,FOLLOW_25_in_functionConstraint542); 
            pushFollow(FOLLOW_functionCall_in_functionConstraint546);
            fc=functionCall(tmodel, vars);

            state._fsp--;

            pushFollow(FOLLOW_constraint_in_functionConstraint551);
            cs=constraint(tmodel, fc, vars);

            state._fsp--;

            match(input,27,FOLLOW_27_in_functionConstraint554); 

            		constraints = cs;
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return constraints;
    }
    // $ANTLR end "functionConstraint"


    // $ANTLR start "constraint"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:217:1: constraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [List constraints] : ( '?' | '$?' | last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )* );
    public final List constraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        List constraints = null;

        Token ConstraintOperator1=null;
        IConstraint last = null;

        IConstraint next = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:218:2: ( '?' | '$?' | last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )* )
            int alt12=3;
            switch ( input.LA(1) ) {
            case 33:
                {
                int LA12_1 = input.LA(2);

                if ( (LA12_1==27) ) {
                    alt12=1;
                }
                else if ( (LA12_1==Identifiertoken||LA12_1==26||(LA12_1>=28 && LA12_1<=29)||LA12_1==32||(LA12_1>=48 && LA12_1<=49)) ) {
                    alt12=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 1, input);

                    throw nvae;
                }
                }
                break;
            case 34:
                {
                int LA12_2 = input.LA(2);

                if ( (LA12_2==27) ) {
                    alt12=2;
                }
                else if ( (LA12_2==Identifiertoken||LA12_2==26||(LA12_2>=28 && LA12_2<=29)||LA12_2==32||(LA12_2>=48 && LA12_2<=49)) ) {
                    alt12=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 2, input);

                    throw nvae;
                }
                }
                break;
            case StringLiteral:
            case CharacterLiteral:
            case BooleanLiteral:
            case FloatingPointLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 35:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:222:2: '?'
                    {
                    match(input,33,FOLLOW_33_in_constraint584); 

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:223:4: '$?'
                    {
                    match(input,34,FOLLOW_34_in_constraint590); 

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:224:4: last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )*
                    {
                    pushFollow(FOLLOW_singleConstraint_in_constraint598);
                    last=singleConstraint(tmodel, valuesource, vars);

                    state._fsp--;


                    		List ret = new ArrayList();
                    		List consts = new ArrayList();
                    		String op = null;
                    		if(last instanceof BoundConstraint)
                    			ret.add(last);
                    		else
                    			consts.add(last);
                    	
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:234:2: ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==ConstraintOperator) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:234:3: ConstraintOperator next= singleConstraint[tmodel, valuesource, vars]
                    	    {
                    	    ConstraintOperator1=(Token)match(input,ConstraintOperator,FOLLOW_ConstraintOperator_in_constraint607); 
                    	    pushFollow(FOLLOW_singleConstraint_in_constraint611);
                    	    next=singleConstraint(tmodel, valuesource, vars);

                    	    state._fsp--;


                    	    		// Set op if first occurrence
                    	    		if(op==null)
                    	    			op = (ConstraintOperator1!=null?ConstraintOperator1.getText():null);
                    	    	
                    	    		consts.add(next);
                    	    		if(consts.size()>1)
                    	    		{	
                    	    			if(!(ConstraintOperator1!=null?ConstraintOperator1.getText():null).equals(op))
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
                    } while (true);


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
        }
        return constraints;
    }
    // $ANTLR end "constraint"


    // $ANTLR start "singleConstraint"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:274:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );
    public final IConstraint singleConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        IConstraint tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:275:2: (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] )
            int alt13=5;
            alt13 = dfa13.predict(input);
            switch (alt13) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:275:4: tmp= literalConstraint[valuesource]
                    {
                    pushFollow(FOLLOW_literalConstraint_in_singleConstraint643);
                    tmp=literalConstraint(valuesource);

                    state._fsp--;

                    constraint = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:276:4: tmp= boundConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_boundConstraint_in_singleConstraint654);
                    tmp=boundConstraint(tmodel, valuesource, vars);

                    state._fsp--;

                    constraint = tmp;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:277:4: tmp= multiBoundConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_multiBoundConstraint_in_singleConstraint664);
                    tmp=multiBoundConstraint(tmodel, valuesource, vars);

                    state._fsp--;

                    constraint = tmp;

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:278:4: tmp= predicateConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_predicateConstraint_in_singleConstraint674);
                    tmp=predicateConstraint(tmodel, valuesource, vars);

                    state._fsp--;

                    constraint = tmp;

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:279:4: tmp= returnValueConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_returnValueConstraint_in_singleConstraint684);
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
        }
        return constraint;
    }
    // $ANTLR end "singleConstraint"


    // $ANTLR start "literalConstraint"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:282:1: literalConstraint[Object valuesource] returns [IConstraint constraint] : (op= operator )? val= constant ;
    public final IConstraint literalConstraint(Object valuesource) throws RecognitionException {
        IConstraint constraint = null;

        IOperator op = null;

        Object val = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:283:2: ( (op= operator )? val= constant )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:283:4: (op= operator )? val= constant
            {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:283:6: (op= operator )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( ((LA14_0>=42 && LA14_0<=50)) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:283:6: op= operator
                    {
                    pushFollow(FOLLOW_operator_in_literalConstraint706);
                    op=operator();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_constant_in_literalConstraint711);
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
        }
        return constraint;
    }
    // $ANTLR end "literalConstraint"


    // $ANTLR start "someBoundConstraint"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:292:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );
    public final IConstraint someBoundConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        IConstraint bc = null;

        IConstraint mbc = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:293:2: (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] )
            int alt15=2;
            alt15 = dfa15.predict(input);
            switch (alt15) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:293:4: bc= boundConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_boundConstraint_in_someBoundConstraint734);
                    bc=boundConstraint(tmodel, valuesource, vars);

                    state._fsp--;


                    		constraint = bc;
                    	

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:297:4: mbc= multiBoundConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_multiBoundConstraint_in_someBoundConstraint747);
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
        }
        return constraint;
    }
    // $ANTLR end "someBoundConstraint"


    // $ANTLR start "boundConstraint"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:303:1: boundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] ;
    public final IConstraint boundConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        IOperator op = null;

        Variable var = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:304:2: ( (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:304:4: (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars]
            {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:304:6: (op= operator )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( ((LA16_0>=42 && LA16_0<=50)) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:304:6: op= operator
                    {
                    pushFollow(FOLLOW_operator_in_boundConstraint770);
                    op=operator();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_variable_in_boundConstraint775);
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
        }
        return constraint;
    }
    // $ANTLR end "boundConstraint"


    // $ANTLR start "multiBoundConstraint"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:313:1: multiBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+ ;
    public final IConstraint multiBoundConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        IOperator op = null;

        Variable var = null;

        Variable varn = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:314:2: ( (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+ )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:315:2: (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+
            {

            		List vs = new ArrayList();
            	
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:318:4: (op= operator )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( ((LA17_0>=42 && LA17_0<=50)) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:318:4: op= operator
                    {
                    pushFollow(FOLLOW_operator_in_multiBoundConstraint803);
                    op=operator();

                    state._fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_variable_in_multiBoundConstraint808);
            var=variable(op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars);

            state._fsp--;


            		vs.add(var);
            	
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:322:2: (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+
            int cnt18=0;
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( ((LA18_0>=33 && LA18_0<=34)) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:322:3: varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars]
            	    {
            	    pushFollow(FOLLOW_variable_in_multiBoundConstraint818);
            	    varn=variable(SConditions.getValueSourceType(tmodel, valuesource), vars);

            	    state._fsp--;


            	    		vs.add(varn);
            	    	

            	    }
            	    break;

            	default :
            	    if ( cnt18 >= 1 ) break loop18;
                        EarlyExitException eee =
                            new EarlyExitException(18, input);
                        throw eee;
                }
                cnt18++;
            } while (true);


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
        }
        return constraint;
    }
    // $ANTLR end "multiBoundConstraint"


    // $ANTLR start "predicateConstraint"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:335:1: predicateConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : ':' ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] ) ;
    public final IConstraint predicateConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        FunctionCall fc = null;

        FunctionCall oc = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:336:2: ( ':' ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] ) )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:336:4: ':' ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )
            {
            match(input,35,FOLLOW_35_in_predicateConstraint846); 
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:336:8: ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==25) ) {
                int LA19_1 = input.LA(2);

                if ( ((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {
                    alt19=1;
                }
                else if ( (true) ) {
                    alt19=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 19, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:336:9: {...}?fc= functionCall[tmodel, vars]
                    {
                    if ( !((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {
                        throw new FailedPredicateException(input, "predicateConstraint", "SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)");
                    }
                    pushFollow(FOLLOW_functionCall_in_predicateConstraint853);
                    fc=functionCall(tmodel, vars);

                    state._fsp--;

                    constraint = new PredicateConstraint(fc);

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:337:4: oc= operatorCall[tmodel, vars]
                    {
                    pushFollow(FOLLOW_operatorCall_in_predicateConstraint864);
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
        }
        return constraint;
    }
    // $ANTLR end "predicateConstraint"


    // $ANTLR start "returnValueConstraint"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:342:1: returnValueConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : equalOperator ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] ) ;
    public final IConstraint returnValueConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        FunctionCall fc = null;

        FunctionCall oc = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:343:2: ( equalOperator ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] ) )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:343:4: equalOperator ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )
            {
            pushFollow(FOLLOW_equalOperator_in_returnValueConstraint888);
            equalOperator();

            state._fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:343:18: ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==25) ) {
                int LA20_1 = input.LA(2);

                if ( ((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:343:19: {...}?fc= functionCall[tmodel, vars]
                    {
                    if ( !((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {
                        throw new FailedPredicateException(input, "returnValueConstraint", "SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)");
                    }
                    pushFollow(FOLLOW_functionCall_in_returnValueConstraint895);
                    fc=functionCall(tmodel, vars);

                    state._fsp--;

                    constraint = new ValueSourceReturnValueConstraint(valuesource, fc, IOperator.EQUAL);

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:344:4: oc= operatorCall[tmodel, vars]
                    {
                    pushFollow(FOLLOW_operatorCall_in_returnValueConstraint906);
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
        }
        return constraint;
    }
    // $ANTLR end "returnValueConstraint"


    // $ANTLR start "functionCall"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:348:1: functionCall[OAVTypeModel tmodel, Map vars] returns [FunctionCall fc] : '(' fn= functionName (exp= parameter[tmodel, vars] )* ')' ;
    public final FunctionCall functionCall(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        FunctionCall fc = null;

        String fn = null;

        Object exp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:349:2: ( '(' fn= functionName (exp= parameter[tmodel, vars] )* ')' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:350:2: '(' fn= functionName (exp= parameter[tmodel, vars] )* ')'
            {

            		List exps = new ArrayList();
            	
            match(input,25,FOLLOW_25_in_functionCall936); 
            pushFollow(FOLLOW_functionName_in_functionCall940);
            fn=functionName();

            state._fsp--;

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:353:22: (exp= parameter[tmodel, vars] )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( ((LA21_0>=StringLiteral && LA21_0<=DecimalLiteral)||LA21_0==25||(LA21_0>=33 && LA21_0<=34)||(LA21_0>=39 && LA21_0<=41)) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:353:23: exp= parameter[tmodel, vars]
            	    {
            	    pushFollow(FOLLOW_parameter_in_functionCall945);
            	    exp=parameter(tmodel, vars);

            	    state._fsp--;


            	    		exps.add(exp);
            	    	

            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);

            match(input,27,FOLLOW_27_in_functionCall955); 

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
        }
        return fc;
    }
    // $ANTLR end "functionCall"


    // $ANTLR start "operatorCall"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:396:1: operatorCall[OAVTypeModel tmodel, Map vars] returns [FunctionCall fc] : '(' op= operator exp1= parameter[tmodel, vars] exp2= parameter[tmodel, vars] ')' ;
    public final FunctionCall operatorCall(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        FunctionCall fc = null;

        IOperator op = null;

        Object exp1 = null;

        Object exp2 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:397:2: ( '(' op= operator exp1= parameter[tmodel, vars] exp2= parameter[tmodel, vars] ')' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:397:4: '(' op= operator exp1= parameter[tmodel, vars] exp2= parameter[tmodel, vars] ')'
            {
            match(input,25,FOLLOW_25_in_operatorCall976); 
            pushFollow(FOLLOW_operator_in_operatorCall981);
            op=operator();

            state._fsp--;

            pushFollow(FOLLOW_parameter_in_operatorCall986);
            exp1=parameter(tmodel, vars);

            state._fsp--;

            pushFollow(FOLLOW_parameter_in_operatorCall991);
            exp2=parameter(tmodel, vars);

            state._fsp--;

            match(input,27,FOLLOW_27_in_operatorCall994); 

            		IFunction func = new OperatorFunction(op!=null? op: IOperator.EQUAL);
            		fc = new FunctionCall(func, new Object[]{exp1, exp2});
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return fc;
    }
    // $ANTLR end "operatorCall"


    // $ANTLR start "parameter"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:404:1: parameter[OAVTypeModel tmodel, Map vars] returns [Object val] : (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] );
    public final Object parameter(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        Object val = null;

        Object tmp1 = null;

        Variable tmp2 = null;

        FunctionCall tmp3 = null;

        FunctionCall tmp4 = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:405:2: (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] )
            int alt22=4;
            alt22 = dfa22.predict(input);
            switch (alt22) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:405:4: tmp1= constant
                    {
                    pushFollow(FOLLOW_constant_in_parameter1017);
                    tmp1=constant();

                    state._fsp--;

                    val = new Constant(tmp1);

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:406:4: tmp2= variable[null, vars]
                    {
                    pushFollow(FOLLOW_variable_in_parameter1027);
                    tmp2=variable(null, vars);

                    state._fsp--;

                    val = tmp2;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:407:4: {...}?tmp3= functionCall[tmodel, vars]
                    {
                    if ( !((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {
                        throw new FailedPredicateException(input, "parameter", "SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)");
                    }
                    pushFollow(FOLLOW_functionCall_in_parameter1039);
                    tmp3=functionCall(tmodel, vars);

                    state._fsp--;

                    val = tmp3;

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:408:4: tmp4= operatorCall[tmodel, vars]
                    {
                    pushFollow(FOLLOW_operatorCall_in_parameter1049);
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
        }
        return val;
    }
    // $ANTLR end "parameter"


    // $ANTLR start "constant"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:411:1: constant returns [Object val] : tmp= literal ;
    public final Object constant() throws RecognitionException {
        Object val = null;

        Object tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:412:2: (tmp= literal )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:412:4: tmp= literal
            {
            pushFollow(FOLLOW_literal_in_constant1070);
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
        }
        return val;
    }
    // $ANTLR end "constant"


    // $ANTLR start "variable"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:415:1: variable[OAVObjectType type, Map vars] returns [Variable var] : (tmp= singleFieldVariable[type, vars] | tmp= multiFieldVariable[type, vars] );
    public final Variable variable(OAVObjectType type, Map vars) throws RecognitionException {
        Variable var = null;

        Variable tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:416:2: (tmp= singleFieldVariable[type, vars] | tmp= multiFieldVariable[type, vars] )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==33) ) {
                alt23=1;
            }
            else if ( (LA23_0==34) ) {
                alt23=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:416:4: tmp= singleFieldVariable[type, vars]
                    {
                    pushFollow(FOLLOW_singleFieldVariable_in_variable1093);
                    tmp=singleFieldVariable(type, vars);

                    state._fsp--;

                    var = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:417:4: tmp= multiFieldVariable[type, vars]
                    {
                    pushFollow(FOLLOW_multiFieldVariable_in_variable1103);
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
        }
        return var;
    }
    // $ANTLR end "variable"


    // $ANTLR start "singleFieldVariable"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:420:1: singleFieldVariable[OAVObjectType type, Map vars] returns [Variable var] : '?' id= identifier ;
    public final Variable singleFieldVariable(OAVObjectType type, Map vars) throws RecognitionException {
        Variable var = null;

        Token id = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:421:2: ( '?' id= identifier )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:421:4: '?' id= identifier
            {
            match(input,33,FOLLOW_33_in_singleFieldVariable1123); 
            pushFollow(FOLLOW_identifier_in_singleFieldVariable1127);
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
        }
        return var;
    }
    // $ANTLR end "singleFieldVariable"


    // $ANTLR start "multiFieldVariable"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:437:1: multiFieldVariable[OAVObjectType type, Map vars] returns [Variable var] : '$?' id= identifier ;
    public final Variable multiFieldVariable(OAVObjectType type, Map vars) throws RecognitionException {
        Variable var = null;

        Token id = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:438:2: ( '$?' id= identifier )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:438:4: '$?' id= identifier
            {
            match(input,34,FOLLOW_34_in_multiFieldVariable1149); 
            pushFollow(FOLLOW_identifier_in_multiFieldVariable1153);
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
        }
        return var;
    }
    // $ANTLR end "multiFieldVariable"


    // $ANTLR start "typename"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:454:1: typename returns [String id] : tmp= identifier ( '.' tmp= identifier )* ;
    public final String typename() throws RecognitionException {
        String id = null;

        Token tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:455:2: (tmp= identifier ( '.' tmp= identifier )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:456:2: tmp= identifier ( '.' tmp= identifier )*
            {

            		StringBuffer buf = new StringBuffer();
            	
            pushFollow(FOLLOW_identifier_in_typename1179);
            tmp=identifier();

            state._fsp--;


            		buf.append(tmp.getText());
            	
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:463:2: ( '.' tmp= identifier )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==36) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:463:3: '.' tmp= identifier
            	    {
            	    match(input,36,FOLLOW_36_in_typename1187); 
            	    pushFollow(FOLLOW_identifier_in_typename1191);
            	    tmp=identifier();

            	    state._fsp--;


            	    		buf.append(".").append(tmp.getText());
            	    	

            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);


            		id = buf.toString();
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return id;
    }
    // $ANTLR end "typename"


    // $ANTLR start "slotname"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:480:1: slotname returns [String id] : tmp= identifier ( '.' tmp= identifier | ':' tmp= identifier | ( '[' tmp= identifier ']' ) | ( '[' StringLiteral ']' ) )* ;
    public final String slotname() throws RecognitionException {
        String id = null;

        Token StringLiteral2=null;
        Token tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:481:2: (tmp= identifier ( '.' tmp= identifier | ':' tmp= identifier | ( '[' tmp= identifier ']' ) | ( '[' StringLiteral ']' ) )* )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:482:2: tmp= identifier ( '.' tmp= identifier | ':' tmp= identifier | ( '[' tmp= identifier ']' ) | ( '[' StringLiteral ']' ) )*
            {

            		StringBuffer buf = new StringBuffer();
            	
            pushFollow(FOLLOW_identifier_in_slotname1226);
            tmp=identifier();

            state._fsp--;


            		buf.append(tmp.getText());
            	
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:489:2: ( '.' tmp= identifier | ':' tmp= identifier | ( '[' tmp= identifier ']' ) | ( '[' StringLiteral ']' ) )*
            loop25:
            do {
                int alt25=5;
                switch ( input.LA(1) ) {
                case 35:
                    {
                    int LA25_2 = input.LA(2);

                    if ( (LA25_2==Identifiertoken||LA25_2==26||(LA25_2>=28 && LA25_2<=29)||LA25_2==32||(LA25_2>=48 && LA25_2<=49)) ) {
                        alt25=2;
                    }


                    }
                    break;
                case 36:
                    {
                    alt25=1;
                    }
                    break;
                case 37:
                    {
                    int LA25_4 = input.LA(2);

                    if ( (LA25_4==StringLiteral) ) {
                        alt25=4;
                    }
                    else if ( (LA25_4==Identifiertoken||LA25_4==26||(LA25_4>=28 && LA25_4<=29)||LA25_4==32||(LA25_4>=48 && LA25_4<=49)) ) {
                        alt25=3;
                    }


                    }
                    break;

                }

                switch (alt25) {
            	case 1 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:489:3: '.' tmp= identifier
            	    {
            	    match(input,36,FOLLOW_36_in_slotname1234); 
            	    pushFollow(FOLLOW_identifier_in_slotname1238);
            	    tmp=identifier();

            	    state._fsp--;


            	    		buf.append(".").append(tmp.getText());
            	    	

            	    }
            	    break;
            	case 2 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:493:3: ':' tmp= identifier
            	    {
            	    match(input,35,FOLLOW_35_in_slotname1245); 
            	    pushFollow(FOLLOW_identifier_in_slotname1249);
            	    tmp=identifier();

            	    state._fsp--;


            	    		buf.append(":").append(tmp.getText());
            	    	

            	    }
            	    break;
            	case 3 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:497:3: ( '[' tmp= identifier ']' )
            	    {
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:497:3: ( '[' tmp= identifier ']' )
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:497:4: '[' tmp= identifier ']'
            	    {
            	    match(input,37,FOLLOW_37_in_slotname1257); 
            	    pushFollow(FOLLOW_identifier_in_slotname1261);
            	    tmp=identifier();

            	    state._fsp--;

            	    match(input,38,FOLLOW_38_in_slotname1263); 

            	    }


            	    		buf.append("[").append(tmp.getText()).append("]");
            	    	

            	    }
            	    break;
            	case 4 :
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:501:3: ( '[' StringLiteral ']' )
            	    {
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:501:3: ( '[' StringLiteral ']' )
            	    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:501:4: '[' StringLiteral ']'
            	    {
            	    match(input,37,FOLLOW_37_in_slotname1273); 
            	    StringLiteral2=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_slotname1275); 
            	    match(input,38,FOLLOW_38_in_slotname1277); 

            	    }


            	    		buf.append("[").append((StringLiteral2!=null?StringLiteral2.getText():null).substring(1, (StringLiteral2!=null?StringLiteral2.getText():null).length()-1)).append("]");
            	    	

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);


            		id = buf.toString();
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return id;
    }
    // $ANTLR end "slotname"


    // $ANTLR start "methodname"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:512:1: methodname returns [String id] : tmp= identifier ;
    public final String methodname() throws RecognitionException {
        String id = null;

        Token tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:513:2: (tmp= identifier )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:513:4: tmp= identifier
            {
            pushFollow(FOLLOW_identifier_in_methodname1308);
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
        }
        return id;
    }
    // $ANTLR end "methodname"


    // $ANTLR start "functionName"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:516:1: functionName returns [String id] : tmp= typename ;
    public final String functionName() throws RecognitionException {
        String id = null;

        String tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:517:2: (tmp= typename )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:517:4: tmp= typename
            {
            pushFollow(FOLLOW_typename_in_functionName1327);
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
        }
        return id;
    }
    // $ANTLR end "functionName"


    // $ANTLR start "literal"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:520:1: literal returns [Object val] : (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );
    public final Object literal() throws RecognitionException {
        Object val = null;

        Token CharacterLiteral3=null;
        Token StringLiteral4=null;
        Token BooleanLiteral5=null;
        Object lit = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:521:2: (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' )
            int alt26=6;
            switch ( input.LA(1) ) {
            case 40:
            case 41:
                {
                int LA26_1 = input.LA(2);

                if ( ((LA26_1>=HexLiteral && LA26_1<=DecimalLiteral)) ) {
                    alt26=2;
                }
                else if ( (LA26_1==FloatingPointLiteral) ) {
                    alt26=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 1, input);

                    throw nvae;
                }
                }
                break;
            case FloatingPointLiteral:
                {
                alt26=1;
                }
                break;
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
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
            case 39:
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:521:4: lit= floatingPointLiteral
                    {
                    pushFollow(FOLLOW_floatingPointLiteral_in_literal1347);
                    lit=floatingPointLiteral();

                    state._fsp--;

                    val = lit;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:522:4: lit= integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal1356);
                    lit=integerLiteral();

                    state._fsp--;

                    val = lit;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:523:4: CharacterLiteral
                    {
                    CharacterLiteral3=(Token)match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal1363); 
                    val = new Character((CharacterLiteral3!=null?CharacterLiteral3.getText():null).charAt(0));

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:524:4: StringLiteral
                    {
                    StringLiteral4=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_literal1370); 
                    val = (StringLiteral4!=null?StringLiteral4.getText():null).substring(1, (StringLiteral4!=null?StringLiteral4.getText():null).length()-1);

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:525:4: BooleanLiteral
                    {
                    BooleanLiteral5=(Token)match(input,BooleanLiteral,FOLLOW_BooleanLiteral_in_literal1377); 
                    val = (BooleanLiteral5!=null?BooleanLiteral5.getText():null).equals("true")? Boolean.TRUE: Boolean.FALSE;

                    }
                    break;
                case 6 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:526:4: 'null'
                    {
                    match(input,39,FOLLOW_39_in_literal1384); 
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
    // $ANTLR end "literal"


    // $ANTLR start "floatingPointLiteral"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:529:1: floatingPointLiteral returns [Object val] : (sign= ( '+' | '-' ) )? FloatingPointLiteral ;
    public final Object floatingPointLiteral() throws RecognitionException {
        Object val = null;

        Token sign=null;
        Token FloatingPointLiteral6=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:530:2: ( (sign= ( '+' | '-' ) )? FloatingPointLiteral )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:530:4: (sign= ( '+' | '-' ) )? FloatingPointLiteral
            {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:530:8: (sign= ( '+' | '-' ) )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( ((LA27_0>=40 && LA27_0<=41)) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:530:8: sign= ( '+' | '-' )
                    {
                    sign=(Token)input.LT(1);
                    if ( (input.LA(1)>=40 && input.LA(1)<=41) ) {
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
            val = sign!=null && "-".equals(sign.getText())? new Double("-"+(FloatingPointLiteral6!=null?FloatingPointLiteral6.getText():null)): new Double((FloatingPointLiteral6!=null?FloatingPointLiteral6.getText():null));

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
    // $ANTLR end "floatingPointLiteral"


    // $ANTLR start "integerLiteral"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:533:1: integerLiteral returns [Object val] : (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral ) ;
    public final Object integerLiteral() throws RecognitionException {
        Object val = null;

        Token sign=null;
        Token HexLiteral7=null;
        Token OctalLiteral8=null;
        Token DecimalLiteral9=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:534:2: ( (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral ) )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:534:4: (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral )
            {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:534:8: (sign= ( '+' | '-' ) )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( ((LA28_0>=40 && LA28_0<=41)) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:534:8: sign= ( '+' | '-' )
                    {
                    sign=(Token)input.LT(1);
                    if ( (input.LA(1)>=40 && input.LA(1)<=41) ) {
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

            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:534:20: ( HexLiteral | OctalLiteral | DecimalLiteral )
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:534:21: HexLiteral
                    {
                    HexLiteral7=(Token)match(input,HexLiteral,FOLLOW_HexLiteral_in_integerLiteral1438); 
                    val = sign!=null && "-".equals(sign.getText())? new Integer("-"+(HexLiteral7!=null?HexLiteral7.getText():null)): new Integer((HexLiteral7!=null?HexLiteral7.getText():null));

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:535:4: OctalLiteral
                    {
                    OctalLiteral8=(Token)match(input,OctalLiteral,FOLLOW_OctalLiteral_in_integerLiteral1445); 
                    val = sign!=null && "-".equals(sign.getText())? new Integer("-"+(OctalLiteral8!=null?OctalLiteral8.getText():null)): new Integer((OctalLiteral8!=null?OctalLiteral8.getText():null));

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:536:4: DecimalLiteral
                    {
                    DecimalLiteral9=(Token)match(input,DecimalLiteral,FOLLOW_DecimalLiteral_in_integerLiteral1452); 
                    val = sign!=null && "-".equals(sign.getText())? new Integer("-"+(DecimalLiteral9!=null?DecimalLiteral9.getText():null)): new Integer((DecimalLiteral9!=null?DecimalLiteral9.getText():null));

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
    // $ANTLR end "integerLiteral"


    // $ANTLR start "operator"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:539:1: operator returns [IOperator operator] : (tmp= equalOperator | '!=' | '~' | '>' | '<' | '>=' | '<=' | 'contains' | 'excludes' );
    public final IOperator operator() throws RecognitionException {
        IOperator operator = null;

        IOperator tmp = null;


        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:540:2: (tmp= equalOperator | '!=' | '~' | '>' | '<' | '>=' | '<=' | 'contains' | 'excludes' )
            int alt30=9;
            switch ( input.LA(1) ) {
            case 50:
                {
                alt30=1;
                }
                break;
            case 42:
                {
                alt30=2;
                }
                break;
            case 43:
                {
                alt30=3;
                }
                break;
            case 44:
                {
                alt30=4;
                }
                break;
            case 45:
                {
                alt30=5;
                }
                break;
            case 46:
                {
                alt30=6;
                }
                break;
            case 47:
                {
                alt30=7;
                }
                break;
            case 48:
                {
                alt30=8;
                }
                break;
            case 49:
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:540:4: tmp= equalOperator
                    {
                    pushFollow(FOLLOW_equalOperator_in_operator1472);
                    tmp=equalOperator();

                    state._fsp--;

                    operator = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:541:4: '!='
                    {
                    match(input,42,FOLLOW_42_in_operator1480); 
                    operator = IOperator.NOTEQUAL;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:542:4: '~'
                    {
                    match(input,43,FOLLOW_43_in_operator1487); 
                    operator = IOperator.NOTEQUAL;

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:543:4: '>'
                    {
                    match(input,44,FOLLOW_44_in_operator1494); 
                    operator = IOperator.GREATER;

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:544:4: '<'
                    {
                    match(input,45,FOLLOW_45_in_operator1501); 
                    operator = IOperator.LESS;

                    }
                    break;
                case 6 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:545:4: '>='
                    {
                    match(input,46,FOLLOW_46_in_operator1508); 
                    operator = IOperator.GREATEROREQUAL;

                    }
                    break;
                case 7 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:546:4: '<='
                    {
                    match(input,47,FOLLOW_47_in_operator1515); 
                    operator = IOperator.LESSOREQUAL;

                    }
                    break;
                case 8 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:547:4: 'contains'
                    {
                    match(input,48,FOLLOW_48_in_operator1522); 
                    operator = IOperator.CONTAINS;

                    }
                    break;
                case 9 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:548:4: 'excludes'
                    {
                    match(input,49,FOLLOW_49_in_operator1529); 
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
        }
        return operator;
    }
    // $ANTLR end "operator"


    // $ANTLR start "equalOperator"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:551:1: equalOperator returns [IOperator operator] : '==' ;
    public final IOperator equalOperator() throws RecognitionException {
        IOperator operator = null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:552:2: ( '==' )
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:552:4: '=='
            {
            match(input,50,FOLLOW_50_in_equalOperator1546); 
            operator = IOperator.EQUAL;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return operator;
    }
    // $ANTLR end "equalOperator"


    // $ANTLR start "identifier"
    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:555:1: identifier returns [Token identifier] : (tmp= Identifiertoken | tmp= 'test' | tmp= 'not' | tmp= 'and' | tmp= 'collect' | tmp= 'contains' | tmp= 'excludes' );
    public final Token identifier() throws RecognitionException {
        Token identifier = null;

        Token tmp=null;

        try {
            // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:556:2: (tmp= Identifiertoken | tmp= 'test' | tmp= 'not' | tmp= 'and' | tmp= 'collect' | tmp= 'contains' | tmp= 'excludes' )
            int alt31=7;
            switch ( input.LA(1) ) {
            case Identifiertoken:
                {
                alt31=1;
                }
                break;
            case 29:
                {
                alt31=2;
                }
                break;
            case 28:
                {
                alt31=3;
                }
                break;
            case 26:
                {
                alt31=4;
                }
                break;
            case 32:
                {
                alt31=5;
                }
                break;
            case 48:
                {
                alt31=6;
                }
                break;
            case 49:
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
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:556:4: tmp= Identifiertoken
                    {
                    tmp=(Token)match(input,Identifiertoken,FOLLOW_Identifiertoken_in_identifier1566); 
                    identifier = tmp;

                    }
                    break;
                case 2 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:557:4: tmp= 'test'
                    {
                    tmp=(Token)match(input,29,FOLLOW_29_in_identifier1575); 
                    identifier = tmp;

                    }
                    break;
                case 3 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:558:4: tmp= 'not'
                    {
                    tmp=(Token)match(input,28,FOLLOW_28_in_identifier1584); 
                    identifier = tmp;

                    }
                    break;
                case 4 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:559:4: tmp= 'and'
                    {
                    tmp=(Token)match(input,26,FOLLOW_26_in_identifier1593); 
                    identifier = tmp;

                    }
                    break;
                case 5 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:560:4: tmp= 'collect'
                    {
                    tmp=(Token)match(input,32,FOLLOW_32_in_identifier1602); 
                    identifier = tmp;

                    }
                    break;
                case 6 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:561:4: tmp= 'contains'
                    {
                    tmp=(Token)match(input,48,FOLLOW_48_in_identifier1611); 
                    identifier = tmp;

                    }
                    break;
                case 7 :
                    // C:\\Files\\Checkouts\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:562:4: tmp= 'excludes'
                    {
                    tmp=(Token)match(input,49,FOLLOW_49_in_identifier1620); 
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
        }
        return identifier;
    }
    // $ANTLR end "identifier"

    // Delegated rules


    protected DFA9 dfa9 = new DFA9(this);
    protected DFA13 dfa13 = new DFA13(this);
    protected DFA15 dfa15 = new DFA15(this);
    protected DFA22 dfa22 = new DFA22(this);
    static final String DFA9_eotS =
        "\15\uffff";
    static final String DFA9_eofS =
        "\15\uffff";
    static final String DFA9_minS =
        "\1\31\1\uffff\1\14\7\5\3\uffff";
    static final String DFA9_maxS =
        "\1\33\1\uffff\1\61\7\62\3\uffff";
    static final String DFA9_acceptS =
        "\1\uffff\1\4\10\uffff\1\3\1\1\1\2";
    static final String DFA9_specialS =
        "\15\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\2\1\uffff\1\1",
            "",
            "\1\3\14\uffff\1\12\1\6\1\uffff\1\5\1\4\2\uffff\1\7\17\uffff"+
            "\1\10\1\11",
            "\7\13\15\uffff\1\14\7\uffff\5\13\1\uffff\14\13",
            "\7\13\15\uffff\1\14\7\uffff\5\13\1\uffff\14\13",
            "\7\13\15\uffff\1\14\7\uffff\5\13\1\uffff\14\13",
            "\7\13\15\uffff\1\14\7\uffff\5\13\1\uffff\14\13",
            "\7\13\15\uffff\1\14\7\uffff\5\13\1\uffff\14\13",
            "\7\13\15\uffff\1\14\7\uffff\5\13\1\uffff\14\13",
            "\7\13\15\uffff\1\14\7\uffff\5\13\1\uffff\14\13",
            "",
            "",
            ""
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "()* loopback of 156:2: (acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] )*";
        }
    }
    static final String DFA13_eotS =
        "\37\uffff";
    static final String DFA13_eofS =
        "\37\uffff";
    static final String DFA13_minS =
        "\12\5\1\uffff\2\14\2\uffff\16\4\2\uffff";
    static final String DFA13_maxS =
        "\1\62\11\51\1\uffff\2\61\2\uffff\16\42\2\uffff";
    static final String DFA13_acceptS =
        "\12\uffff\1\1\2\uffff\1\4\1\5\16\uffff\1\2\1\3";
    static final String DFA13_specialS =
        "\37\uffff}>";
    static final String[] DFA13_transitionS = {
            "\7\12\25\uffff\1\13\1\14\1\15\3\uffff\3\12\1\2\1\3\1\4\1\5"+
            "\1\6\1\7\1\10\1\11\1\1",
            "\7\12\15\uffff\1\16\7\uffff\1\13\1\14\4\uffff\3\12",
            "\7\12\25\uffff\1\13\1\14\4\uffff\3\12",
            "\7\12\25\uffff\1\13\1\14\4\uffff\3\12",
            "\7\12\25\uffff\1\13\1\14\4\uffff\3\12",
            "\7\12\25\uffff\1\13\1\14\4\uffff\3\12",
            "\7\12\25\uffff\1\13\1\14\4\uffff\3\12",
            "\7\12\25\uffff\1\13\1\14\4\uffff\3\12",
            "\7\12\25\uffff\1\13\1\14\4\uffff\3\12",
            "\7\12\25\uffff\1\13\1\14\4\uffff\3\12",
            "",
            "\1\17\15\uffff\1\22\1\uffff\1\21\1\20\2\uffff\1\23\17\uffff"+
            "\1\24\1\25",
            "\1\26\15\uffff\1\31\1\uffff\1\30\1\27\2\uffff\1\32\17\uffff"+
            "\1\33\1\34",
            "",
            "",
            "\1\35\26\uffff\1\35\5\uffff\2\36",
            "\1\35\26\uffff\1\35\5\uffff\2\36",
            "\1\35\26\uffff\1\35\5\uffff\2\36",
            "\1\35\26\uffff\1\35\5\uffff\2\36",
            "\1\35\26\uffff\1\35\5\uffff\2\36",
            "\1\35\26\uffff\1\35\5\uffff\2\36",
            "\1\35\26\uffff\1\35\5\uffff\2\36",
            "\1\35\26\uffff\1\35\5\uffff\2\36",
            "\1\35\26\uffff\1\35\5\uffff\2\36",
            "\1\35\26\uffff\1\35\5\uffff\2\36",
            "\1\35\26\uffff\1\35\5\uffff\2\36",
            "\1\35\26\uffff\1\35\5\uffff\2\36",
            "\1\35\26\uffff\1\35\5\uffff\2\36",
            "\1\35\26\uffff\1\35\5\uffff\2\36",
            "",
            ""
    };

    static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
    static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
    static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
    static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
    static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
    static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }
        public String getDescription() {
            return "274:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );";
        }
    }
    static final String DFA15_eotS =
        "\34\uffff";
    static final String DFA15_eofS =
        "\14\uffff\16\32\2\uffff";
    static final String DFA15_minS =
        "\12\41\2\14\16\41\2\uffff";
    static final String DFA15_maxS =
        "\1\62\11\42\2\61\16\42\2\uffff";
    static final String DFA15_acceptS =
        "\32\uffff\1\1\1\2";
    static final String DFA15_specialS =
        "\34\uffff}>";
    static final String[] DFA15_transitionS = {
            "\1\12\1\13\7\uffff\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\1",
            "\1\12\1\13",
            "\1\12\1\13",
            "\1\12\1\13",
            "\1\12\1\13",
            "\1\12\1\13",
            "\1\12\1\13",
            "\1\12\1\13",
            "\1\12\1\13",
            "\1\12\1\13",
            "\1\14\15\uffff\1\17\1\uffff\1\16\1\15\2\uffff\1\20\17\uffff"+
            "\1\21\1\22",
            "\1\23\15\uffff\1\26\1\uffff\1\25\1\24\2\uffff\1\27\17\uffff"+
            "\1\30\1\31",
            "\2\33",
            "\2\33",
            "\2\33",
            "\2\33",
            "\2\33",
            "\2\33",
            "\2\33",
            "\2\33",
            "\2\33",
            "\2\33",
            "\2\33",
            "\2\33",
            "\2\33",
            "\2\33",
            "",
            ""
    };

    static final short[] DFA15_eot = DFA.unpackEncodedString(DFA15_eotS);
    static final short[] DFA15_eof = DFA.unpackEncodedString(DFA15_eofS);
    static final char[] DFA15_min = DFA.unpackEncodedStringToUnsignedChars(DFA15_minS);
    static final char[] DFA15_max = DFA.unpackEncodedStringToUnsignedChars(DFA15_maxS);
    static final short[] DFA15_accept = DFA.unpackEncodedString(DFA15_acceptS);
    static final short[] DFA15_special = DFA.unpackEncodedString(DFA15_specialS);
    static final short[][] DFA15_transition;

    static {
        int numStates = DFA15_transitionS.length;
        DFA15_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA15_transition[i] = DFA.unpackEncodedString(DFA15_transitionS[i]);
        }
    }

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = DFA15_eot;
            this.eof = DFA15_eof;
            this.min = DFA15_min;
            this.max = DFA15_max;
            this.accept = DFA15_accept;
            this.special = DFA15_special;
            this.transition = DFA15_transition;
        }
        public String getDescription() {
            return "292:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );";
        }
    }
    static final String DFA22_eotS =
        "\17\uffff";
    static final String DFA22_eofS =
        "\17\uffff";
    static final String DFA22_minS =
        "\1\5\13\uffff\1\0\2\uffff";
    static final String DFA22_maxS =
        "\1\51\13\uffff\1\0\2\uffff";
    static final String DFA22_acceptS =
        "\1\uffff\1\1\10\uffff\1\2\2\uffff\1\3\1\4";
    static final String DFA22_specialS =
        "\14\uffff\1\0\2\uffff}>";
    static final String[] DFA22_transitionS = {
            "\7\1\15\uffff\1\14\7\uffff\2\12\4\uffff\3\1",
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
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "404:1: parameter[OAVTypeModel tmodel, Map vars] returns [Object val] : (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA22_12 = input.LA(1);

                         
                        int index22_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input))) ) {s = 13;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index22_12);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 22, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_ce_in_rhs53 = new BitSet(new long[]{0x0000000602000000L});
    public static final BitSet FOLLOW_EOF_in_rhs68 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_andce_in_ce93 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_notce_in_ce109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_testce_in_ce125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collectce_in_ce137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_objectce_in_ce151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_andce175 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_andce177 = new BitSet(new long[]{0x0000000602000000L});
    public static final BitSet FOLLOW_ce_in_andce182 = new BitSet(new long[]{0x000000060A000000L});
    public static final BitSet FOLLOW_27_in_andce192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_notce211 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_notce213 = new BitSet(new long[]{0x0000000602000000L});
    public static final BitSet FOLLOW_ce_in_notce217 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_notce220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_testce239 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_testce241 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_operatorCall_in_testce249 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_functionCall_in_testce261 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_testce269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiFieldVariable_in_collectce296 = new BitSet(new long[]{0x00000000C0000000L});
    public static final BitSet FOLLOW_set_in_collectce299 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_collectce311 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_collectce313 = new BitSet(new long[]{0x0000000602000000L});
    public static final BitSet FOLLOW_ce_in_collectce318 = new BitSet(new long[]{0x0000000E0A000000L});
    public static final BitSet FOLLOW_predicateConstraint_in_collectce331 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_collectce335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleFieldVariable_in_objectce364 = new BitSet(new long[]{0x00000000C0000000L});
    public static final BitSet FOLLOW_set_in_objectce367 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_objectce379 = new BitSet(new long[]{0x0003000134001000L});
    public static final BitSet FOLLOW_typename_in_objectce385 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_attributeConstraint_in_objectce394 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_methodConstraint_in_objectce405 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_functionConstraint_in_objectce416 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_27_in_objectce428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_attributeConstraint450 = new BitSet(new long[]{0x0003000134001000L});
    public static final BitSet FOLLOW_slotname_in_attributeConstraint454 = new BitSet(new long[]{0x0007FF8E00000FE0L});
    public static final BitSet FOLLOW_constraint_in_attributeConstraint458 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_attributeConstraint461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_methodConstraint488 = new BitSet(new long[]{0x0003000134001000L});
    public static final BitSet FOLLOW_methodname_in_methodConstraint492 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_methodConstraint494 = new BitSet(new long[]{0x0007FF860A000FE0L});
    public static final BitSet FOLLOW_parameter_in_methodConstraint498 = new BitSet(new long[]{0x0007FF860A000FE0L});
    public static final BitSet FOLLOW_27_in_methodConstraint508 = new BitSet(new long[]{0x0007FF8E00000FE0L});
    public static final BitSet FOLLOW_constraint_in_methodConstraint512 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_methodConstraint515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_functionConstraint542 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_functionCall_in_functionConstraint546 = new BitSet(new long[]{0x0007FF8E00000FE0L});
    public static final BitSet FOLLOW_constraint_in_functionConstraint551 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_functionConstraint554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_constraint584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_constraint590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleConstraint_in_constraint598 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_ConstraintOperator_in_constraint607 = new BitSet(new long[]{0x0007FF8E00000FE0L});
    public static final BitSet FOLLOW_singleConstraint_in_constraint611 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_literalConstraint_in_singleConstraint643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boundConstraint_in_singleConstraint654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiBoundConstraint_in_singleConstraint664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicateConstraint_in_singleConstraint674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_returnValueConstraint_in_singleConstraint684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_in_literalConstraint706 = new BitSet(new long[]{0x0007FF8000000FE0L});
    public static final BitSet FOLLOW_constant_in_literalConstraint711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boundConstraint_in_someBoundConstraint734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiBoundConstraint_in_someBoundConstraint747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_in_boundConstraint770 = new BitSet(new long[]{0x0007FC0600000000L});
    public static final BitSet FOLLOW_variable_in_boundConstraint775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_in_multiBoundConstraint803 = new BitSet(new long[]{0x0007FC0600000000L});
    public static final BitSet FOLLOW_variable_in_multiBoundConstraint808 = new BitSet(new long[]{0x0007FC0600000000L});
    public static final BitSet FOLLOW_variable_in_multiBoundConstraint818 = new BitSet(new long[]{0x0007FC0600000002L});
    public static final BitSet FOLLOW_35_in_predicateConstraint846 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_functionCall_in_predicateConstraint853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operatorCall_in_predicateConstraint864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_equalOperator_in_returnValueConstraint888 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_functionCall_in_returnValueConstraint895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operatorCall_in_returnValueConstraint906 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_functionCall936 = new BitSet(new long[]{0x0003000134001000L});
    public static final BitSet FOLLOW_functionName_in_functionCall940 = new BitSet(new long[]{0x0007FF860A000FE0L});
    public static final BitSet FOLLOW_parameter_in_functionCall945 = new BitSet(new long[]{0x0007FF860A000FE0L});
    public static final BitSet FOLLOW_27_in_functionCall955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_operatorCall976 = new BitSet(new long[]{0x0007FC0000000000L});
    public static final BitSet FOLLOW_operator_in_operatorCall981 = new BitSet(new long[]{0x0007FF8602000FE0L});
    public static final BitSet FOLLOW_parameter_in_operatorCall986 = new BitSet(new long[]{0x0007FF8602000FE0L});
    public static final BitSet FOLLOW_parameter_in_operatorCall991 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_operatorCall994 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_in_parameter1017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_parameter1027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionCall_in_parameter1039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operatorCall_in_parameter1049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_constant1070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleFieldVariable_in_variable1093 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiFieldVariable_in_variable1103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_singleFieldVariable1123 = new BitSet(new long[]{0x0003000134001000L});
    public static final BitSet FOLLOW_identifier_in_singleFieldVariable1127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_multiFieldVariable1149 = new BitSet(new long[]{0x0003000134001000L});
    public static final BitSet FOLLOW_identifier_in_multiFieldVariable1153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_typename1179 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_typename1187 = new BitSet(new long[]{0x0003000134001000L});
    public static final BitSet FOLLOW_identifier_in_typename1191 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_identifier_in_slotname1226 = new BitSet(new long[]{0x0000003800000002L});
    public static final BitSet FOLLOW_36_in_slotname1234 = new BitSet(new long[]{0x0003000134001000L});
    public static final BitSet FOLLOW_identifier_in_slotname1238 = new BitSet(new long[]{0x0000003800000002L});
    public static final BitSet FOLLOW_35_in_slotname1245 = new BitSet(new long[]{0x0003000134001000L});
    public static final BitSet FOLLOW_identifier_in_slotname1249 = new BitSet(new long[]{0x0000003800000002L});
    public static final BitSet FOLLOW_37_in_slotname1257 = new BitSet(new long[]{0x0003000134001000L});
    public static final BitSet FOLLOW_identifier_in_slotname1261 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_slotname1263 = new BitSet(new long[]{0x0000003800000002L});
    public static final BitSet FOLLOW_37_in_slotname1273 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_StringLiteral_in_slotname1275 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_slotname1277 = new BitSet(new long[]{0x0000003800000002L});
    public static final BitSet FOLLOW_identifier_in_methodname1308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typename_in_functionName1327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_floatingPointLiteral_in_literal1347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal1356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal1363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal1370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BooleanLiteral_in_literal1377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_literal1384 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_floatingPointLiteral1403 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral1430 = new BitSet(new long[]{0x0000000000000E00L});
    public static final BitSet FOLLOW_HexLiteral_in_integerLiteral1438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OctalLiteral_in_integerLiteral1445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DecimalLiteral_in_integerLiteral1452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_equalOperator_in_operator1472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_operator1480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_operator1487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_operator1494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_operator1501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_operator1508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_operator1515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_operator1522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_operator1529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_equalOperator1546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifiertoken_in_identifier1566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifier1575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifier1584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_identifier1593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_identifier1602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_identifier1611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_identifier1620 = new BitSet(new long[]{0x0000000000000002L});

}