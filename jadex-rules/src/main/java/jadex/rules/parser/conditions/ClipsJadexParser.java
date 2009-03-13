// $ANTLR 3.0.1 C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g 2009-03-13 11:09:06

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

public class ClipsJadexParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ConstraintOperator", "CharacterLiteral", "StringLiteral", "BooleanLiteral", "FloatingPointLiteral", "HexLiteral", "OctalLiteral", "DecimalLiteral", "Identifiertoken", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "COMMENT", "LINE_COMMENT", "'('", "'and'", "')'", "'not'", "'test'", "'<-'", "'='", "'collect'", "'?'", "'$?'", "':'", "'.'", "'['", "']'", "'null'", "'+'", "'-'", "'!='", "'~'", "'>'", "'<'", "'>='", "'<='", "'contains'", "'excludes'", "'=='"
    };
    public static final int HexLiteral=9;
    public static final int LINE_COMMENT=24;
    public static final int FloatTypeSuffix=16;
    public static final int OctalLiteral=10;
    public static final int IntegerTypeSuffix=14;
    public static final int CharacterLiteral=5;
    public static final int Exponent=15;
    public static final int EOF=-1;
    public static final int DecimalLiteral=11;
    public static final int HexDigit=13;
    public static final int StringLiteral=6;
    public static final int WS=22;
    public static final int UnicodeEscape=18;
    public static final int ConstraintOperator=4;
    public static final int FloatingPointLiteral=8;
    public static final int JavaIDDigit=21;
    public static final int COMMENT=23;
    public static final int Identifiertoken=12;
    public static final int Letter=20;
    public static final int EscapeSequence=17;
    public static final int OctalEscape=19;
    public static final int BooleanLiteral=7;

        public ClipsJadexParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g"; }

    
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



    // $ANTLR start rhs
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:59:1: rhs[OAVTypeModel tmodel] returns [ICondition condition] : (c= ce[tmodel, vars] )+ EOF ;
    public final ICondition rhs(OAVTypeModel tmodel) throws RecognitionException {
        ICondition condition = null;

        ICondition c = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:60:2: ( (c= ce[tmodel, vars] )+ EOF )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:61:2: (c= ce[tmodel, vars] )+ EOF
            {
            
            		List conds = new ArrayList();
            		Map vars = new HashMap();
            		
            	
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:66:2: (c= ce[tmodel, vars] )+
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
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:66:3: c= ce[tmodel, vars]
            	    {
            	    pushFollow(FOLLOW_ce_in_rhs53);
            	    c=ce(tmodel,  vars);
            	    _fsp--;

            	    
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
    // $ANTLR end rhs


    // $ANTLR start ce
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:80:1: ce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : (tmp= andce[tmodel, vars] | tmp= notce[tmodel, vars] | tmp= testce[tmodel, vars] | tmp= collectce[tmodel, vars] | {...}?tmp= objectce[tmodel, vars] );
    public final ICondition ce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        ICondition tmp = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:81:2: (tmp= andce[tmodel, vars] | tmp= notce[tmodel, vars] | tmp= testce[tmodel, vars] | tmp= collectce[tmodel, vars] | {...}?tmp= objectce[tmodel, vars] )
            int alt2=5;
            alt2 = dfa2.predict(input);
            switch (alt2) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:81:4: tmp= andce[tmodel, vars]
                    {
                    pushFollow(FOLLOW_andce_in_ce89);
                    tmp=andce(tmodel,  vars);
                    _fsp--;

                    condition = tmp;

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:82:4: tmp= notce[tmodel, vars]
                    {
                    pushFollow(FOLLOW_notce_in_ce101);
                    tmp=notce(tmodel,  vars);
                    _fsp--;

                    condition = tmp;

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:83:4: tmp= testce[tmodel, vars]
                    {
                    pushFollow(FOLLOW_testce_in_ce113);
                    tmp=testce(tmodel,  vars);
                    _fsp--;

                    condition = tmp;

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:84:4: tmp= collectce[tmodel, vars]
                    {
                    pushFollow(FOLLOW_collectce_in_ce125);
                    tmp=collectce(tmodel,  vars);
                    _fsp--;

                    condition = tmp;

                    }
                    break;
                case 5 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:85:4: {...}?tmp= objectce[tmodel, vars]
                    {
                    if ( !(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {
                        throw new FailedPredicateException(input, "ce", "SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)");
                    }
                    pushFollow(FOLLOW_objectce_in_ce139);
                    tmp=objectce(tmodel,  vars);
                    _fsp--;

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
    // $ANTLR end ce


    // $ANTLR start andce
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:88:1: andce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : '(' 'and' (c= ce[$tmodel, vars] )+ ')' ;
    public final ICondition andce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        ICondition c = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:89:2: ( '(' 'and' (c= ce[$tmodel, vars] )+ ')' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:90:2: '(' 'and' (c= ce[$tmodel, vars] )+ ')'
            {
            
            		List conds = new ArrayList();
            	
            match(input,25,FOLLOW_25_in_andce163); 
            match(input,26,FOLLOW_26_in_andce165); 
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:93:12: (c= ce[$tmodel, vars] )+
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
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:93:13: c= ce[$tmodel, vars]
            	    {
            	    pushFollow(FOLLOW_ce_in_andce170);
            	    c=ce(tmodel,  vars);
            	    _fsp--;

            	    
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

            match(input,27,FOLLOW_27_in_andce180); 
            
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
    // $ANTLR end andce


    // $ANTLR start notce
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:103:1: notce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : '(' 'not' c= ce[$tmodel, vars] ')' ;
    public final ICondition notce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        ICondition c = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:104:2: ( '(' 'not' c= ce[$tmodel, vars] ')' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:104:4: '(' 'not' c= ce[$tmodel, vars] ')'
            {
            match(input,25,FOLLOW_25_in_notce199); 
            match(input,28,FOLLOW_28_in_notce201); 
            pushFollow(FOLLOW_ce_in_notce205);
            c=ce(tmodel,  vars);
            _fsp--;

            match(input,27,FOLLOW_27_in_notce208); 
            
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
    // $ANTLR end notce


    // $ANTLR start testce
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:110:1: testce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : '(' 'test' ( ({...}?call= functionCall[tmodel, vars] ) | call= operatorCall[tmodel, vars] ) ')' ;
    public final ICondition testce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        FunctionCall call = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:111:2: ( '(' 'test' ( ({...}?call= functionCall[tmodel, vars] ) | call= operatorCall[tmodel, vars] ) ')' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:111:4: '(' 'test' ( ({...}?call= functionCall[tmodel, vars] ) | call= operatorCall[tmodel, vars] ) ')'
            {
            match(input,25,FOLLOW_25_in_testce227); 
            match(input,29,FOLLOW_29_in_testce229); 
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:111:15: ( ({...}?call= functionCall[tmodel, vars] ) | call= operatorCall[tmodel, vars] )
            int alt4=2;
            alt4 = dfa4.predict(input);
            switch (alt4) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:111:16: ({...}?call= functionCall[tmodel, vars] )
                    {
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:111:16: ({...}?call= functionCall[tmodel, vars] )
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:111:17: {...}?call= functionCall[tmodel, vars]
                    {
                    if ( !(SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)) ) {
                        throw new FailedPredicateException(input, "testce", "SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)");
                    }
                    pushFollow(FOLLOW_functionCall_in_testce237);
                    call=functionCall(tmodel,  vars);
                    _fsp--;


                    }


                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:111:119: call= operatorCall[tmodel, vars]
                    {
                    pushFollow(FOLLOW_operatorCall_in_testce246);
                    call=operatorCall(tmodel,  vars);
                    _fsp--;


                    }
                    break;

            }

            match(input,27,FOLLOW_27_in_testce250); 
            
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
    // $ANTLR end testce


    // $ANTLR start collectce
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:117:1: collectce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )? '(' 'collect' (c= ce[$tmodel, vars] )+ (pc= predicateConstraint[$tmodel, null, vars] )? ')' ;
    public final ICondition collectce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        Variable mfv = null;

        ICondition c = null;

        IConstraint pc = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:118:2: ( (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )? '(' 'collect' (c= ce[$tmodel, vars] )+ (pc= predicateConstraint[$tmodel, null, vars] )? ')' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:119:2: (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )? '(' 'collect' (c= ce[$tmodel, vars] )+ (pc= predicateConstraint[$tmodel, null, vars] )? ')'
            {
            
            		List conds = new ArrayList();
            	
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:122:2: (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==34) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:122:3: mfv= multiFieldVariable[null, vars] ( '<-' | '=' )
                    {
                    pushFollow(FOLLOW_multiFieldVariable_in_collectce277);
                    mfv=multiFieldVariable(null,  vars);
                    _fsp--;

                    if ( (input.LA(1)>=30 && input.LA(1)<=31) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_collectce280);    throw mse;
                    }


                    }
                    break;

            }

            match(input,25,FOLLOW_25_in_collectce292); 
            match(input,32,FOLLOW_32_in_collectce294); 
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:123:16: (c= ce[$tmodel, vars] )+
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
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:123:17: c= ce[$tmodel, vars]
            	    {
            	    pushFollow(FOLLOW_ce_in_collectce299);
            	    c=ce(tmodel,  vars);
            	    _fsp--;

            	    
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

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:127:8: (pc= predicateConstraint[$tmodel, null, vars] )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==35) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:127:8: pc= predicateConstraint[$tmodel, null, vars]
                    {
                    pushFollow(FOLLOW_predicateConstraint_in_collectce312);
                    pc=predicateConstraint(tmodel,  null,  vars);
                    _fsp--;


                    }
                    break;

            }

            match(input,27,FOLLOW_27_in_collectce316); 
            
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
    // $ANTLR end collectce


    // $ANTLR start objectce
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:141:1: objectce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )? '(' tn= typename (acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] )* ')' ;
    public final ICondition objectce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        Variable sfv = null;

        String tn = null;

        List acs = null;

        List mcs = null;

        List fcs = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:142:2: ( (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )? '(' tn= typename (acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] )* ')' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:143:2: (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )? '(' tn= typename (acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] )* ')'
            {
            
            		List consts = new ArrayList();
            	
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:146:2: (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==33) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:146:3: sfv= singleFieldVariable[null, vars] ( '<-' | '=' )
                    {
                    pushFollow(FOLLOW_singleFieldVariable_in_objectce345);
                    sfv=singleFieldVariable(null,  vars);
                    _fsp--;

                    if ( (input.LA(1)>=30 && input.LA(1)<=31) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_objectce348);    throw mse;
                    }


                    }
                    break;

            }

            match(input,25,FOLLOW_25_in_objectce360); 
            pushFollow(FOLLOW_typename_in_objectce366);
            tn=typename();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:150:2: (acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] | fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars] )*
            loop9:
            do {
                int alt9=4;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==25) ) {
                    switch ( input.LA(2) ) {
                    case Identifiertoken:
                        {
                        int LA9_3 = input.LA(3);

                        if ( (LA9_3==25) ) {
                            alt9=2;
                        }
                        else if ( ((LA9_3>=CharacterLiteral && LA9_3<=DecimalLiteral)||(LA9_3>=33 && LA9_3<=50)) ) {
                            alt9=1;
                        }


                        }
                        break;
                    case 29:
                        {
                        int LA9_4 = input.LA(3);

                        if ( ((LA9_4>=CharacterLiteral && LA9_4<=DecimalLiteral)||(LA9_4>=33 && LA9_4<=50)) ) {
                            alt9=1;
                        }
                        else if ( (LA9_4==25) ) {
                            alt9=2;
                        }


                        }
                        break;
                    case 28:
                        {
                        int LA9_5 = input.LA(3);

                        if ( ((LA9_5>=CharacterLiteral && LA9_5<=DecimalLiteral)||(LA9_5>=33 && LA9_5<=50)) ) {
                            alt9=1;
                        }
                        else if ( (LA9_5==25) ) {
                            alt9=2;
                        }


                        }
                        break;
                    case 26:
                        {
                        int LA9_6 = input.LA(3);

                        if ( ((LA9_6>=CharacterLiteral && LA9_6<=DecimalLiteral)||(LA9_6>=33 && LA9_6<=50)) ) {
                            alt9=1;
                        }
                        else if ( (LA9_6==25) ) {
                            alt9=2;
                        }


                        }
                        break;
                    case 48:
                        {
                        int LA9_7 = input.LA(3);

                        if ( (LA9_7==25) ) {
                            alt9=2;
                        }
                        else if ( ((LA9_7>=CharacterLiteral && LA9_7<=DecimalLiteral)||(LA9_7>=33 && LA9_7<=50)) ) {
                            alt9=1;
                        }


                        }
                        break;
                    case 49:
                        {
                        int LA9_8 = input.LA(3);

                        if ( (LA9_8==25) ) {
                            alt9=2;
                        }
                        else if ( ((LA9_8>=CharacterLiteral && LA9_8<=DecimalLiteral)||(LA9_8>=33 && LA9_8<=50)) ) {
                            alt9=1;
                        }


                        }
                        break;
                    case 25:
                        {
                        alt9=3;
                        }
                        break;

                    }

                }


                switch (alt9) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:150:3: acs= attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars]
            	    {
            	    pushFollow(FOLLOW_attributeConstraint_in_objectce375);
            	    acs=attributeConstraint(tmodel,  SConditions.getObjectType(tmodel,  tn,  imports),  vars);
            	    _fsp--;

            	    
            	    		consts.addAll(acs);
            	    	

            	    }
            	    break;
            	case 2 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:154:4: mcs= methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars]
            	    {
            	    pushFollow(FOLLOW_methodConstraint_in_objectce386);
            	    mcs=methodConstraint(tmodel,  SConditions.getObjectType(tmodel,  tn,  imports),  vars);
            	    _fsp--;

            	    
            	    		consts.addAll(mcs);
            	    	

            	    }
            	    break;
            	case 3 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:158:4: fcs= functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars]
            	    {
            	    pushFollow(FOLLOW_functionConstraint_in_objectce397);
            	    fcs=functionConstraint(tmodel,  SConditions.getObjectType(tmodel,  tn,  imports),  vars);
            	    _fsp--;

            	    
            	    		consts.addAll(fcs);
            	    	

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            match(input,27,FOLLOW_27_in_objectce409); 
            
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
    // $ANTLR end objectce


    // $ANTLR start attributeConstraint
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:178:1: attributeConstraint[OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints] : '(' sn= slotname cs= constraint[tmodel, SConditions.convertAttributeTypes(tmodel, otype, sn, imports), vars] ')' ;
    public final List attributeConstraint(OAVTypeModel tmodel, OAVObjectType otype, Map vars) throws RecognitionException {
        List constraints = null;

        String sn = null;

        List cs = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:179:2: ( '(' sn= slotname cs= constraint[tmodel, SConditions.convertAttributeTypes(tmodel, otype, sn, imports), vars] ')' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:179:4: '(' sn= slotname cs= constraint[tmodel, SConditions.convertAttributeTypes(tmodel, otype, sn, imports), vars] ')'
            {
            match(input,25,FOLLOW_25_in_attributeConstraint431); 
            pushFollow(FOLLOW_slotname_in_attributeConstraint435);
            sn=slotname();
            _fsp--;

            pushFollow(FOLLOW_constraint_in_attributeConstraint439);
            cs=constraint(tmodel,  SConditions.convertAttributeTypes(tmodel,  otype,  sn,  imports),  vars);
            _fsp--;

            match(input,27,FOLLOW_27_in_attributeConstraint442); 
            
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
    // $ANTLR end attributeConstraint


    // $ANTLR start methodConstraint
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:185:1: methodConstraint[OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints] : '(' mn= methodname '(' (exp= parameter[tmodel, vars] )* ')' cs= constraint[tmodel, SConditions.createMethodCall(otype, mn, exps), vars] ')' ;
    public final List methodConstraint(OAVTypeModel tmodel, OAVObjectType otype, Map vars) throws RecognitionException {
        List constraints = null;

        String mn = null;

        Object exp = null;

        List cs = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:186:2: ( '(' mn= methodname '(' (exp= parameter[tmodel, vars] )* ')' cs= constraint[tmodel, SConditions.createMethodCall(otype, mn, exps), vars] ')' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:187:2: '(' mn= methodname '(' (exp= parameter[tmodel, vars] )* ')' cs= constraint[tmodel, SConditions.createMethodCall(otype, mn, exps), vars] ')'
            {
            
            		List exps = new ArrayList();
            	
            match(input,25,FOLLOW_25_in_methodConstraint469); 
            pushFollow(FOLLOW_methodname_in_methodConstraint473);
            mn=methodname();
            _fsp--;

            match(input,25,FOLLOW_25_in_methodConstraint475); 
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:190:23: (exp= parameter[tmodel, vars] )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0>=CharacterLiteral && LA10_0<=DecimalLiteral)||LA10_0==25||(LA10_0>=33 && LA10_0<=34)||(LA10_0>=39 && LA10_0<=41)) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:190:24: exp= parameter[tmodel, vars]
            	    {
            	    pushFollow(FOLLOW_parameter_in_methodConstraint479);
            	    exp=parameter(tmodel,  vars);
            	    _fsp--;

            	    
            	    		exps.add(exp);
            	    	

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            match(input,27,FOLLOW_27_in_methodConstraint489); 
            pushFollow(FOLLOW_constraint_in_methodConstraint493);
            cs=constraint(tmodel,  SConditions.createMethodCall(otype,  mn,  exps),  vars);
            _fsp--;

            match(input,27,FOLLOW_27_in_methodConstraint496); 
            
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
    // $ANTLR end methodConstraint


    // $ANTLR start functionConstraint
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:200:1: functionConstraint[OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints] : '(' fc= functionCall[tmodel, vars] cs= constraint[tmodel, fc, vars] ')' ;
    public final List functionConstraint(OAVTypeModel tmodel, OAVObjectType otype, Map vars) throws RecognitionException {
        List constraints = null;

        FunctionCall fc = null;

        List cs = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:201:2: ( '(' fc= functionCall[tmodel, vars] cs= constraint[tmodel, fc, vars] ')' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:202:2: '(' fc= functionCall[tmodel, vars] cs= constraint[tmodel, fc, vars] ')'
            {
            
            		List exps = new ArrayList();
            	
            match(input,25,FOLLOW_25_in_functionConstraint523); 
            pushFollow(FOLLOW_functionCall_in_functionConstraint527);
            fc=functionCall(tmodel,  vars);
            _fsp--;

            pushFollow(FOLLOW_constraint_in_functionConstraint532);
            cs=constraint(tmodel,  fc,  vars);
            _fsp--;

            match(input,27,FOLLOW_27_in_functionConstraint535); 
            
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
    // $ANTLR end functionConstraint


    // $ANTLR start constraint
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:211:1: constraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [List constraints] : ( '?' | '$?' | last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )* );
    public final List constraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        List constraints = null;

        Token ConstraintOperator1=null;
        IConstraint last = null;

        IConstraint next = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:212:2: ( '?' | '$?' | last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )* )
            int alt12=3;
            switch ( input.LA(1) ) {
            case 33:
                {
                int LA12_1 = input.LA(2);

                if ( (LA12_1==Identifiertoken||LA12_1==26||(LA12_1>=28 && LA12_1<=29)||(LA12_1>=48 && LA12_1<=49)) ) {
                    alt12=3;
                }
                else if ( (LA12_1==27) ) {
                    alt12=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("211:1: constraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [List constraints] : ( '?' | '$?' | last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )* );", 12, 1, input);

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
                else if ( (LA12_2==Identifiertoken||LA12_2==26||(LA12_2>=28 && LA12_2<=29)||(LA12_2>=48 && LA12_2<=49)) ) {
                    alt12=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("211:1: constraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [List constraints] : ( '?' | '$?' | last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )* );", 12, 2, input);

                    throw nvae;
                }
                }
                break;
            case CharacterLiteral:
            case StringLiteral:
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
                    new NoViableAltException("211:1: constraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [List constraints] : ( '?' | '$?' | last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )* );", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:216:2: '?'
                    {
                    match(input,33,FOLLOW_33_in_constraint565); 

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:217:4: '$?'
                    {
                    match(input,34,FOLLOW_34_in_constraint571); 

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:218:4: last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )*
                    {
                    pushFollow(FOLLOW_singleConstraint_in_constraint579);
                    last=singleConstraint(tmodel,  valuesource,  vars);
                    _fsp--;

                    
                    		List ret = new ArrayList();
                    		List consts = new ArrayList();
                    		String op = null;
                    		if(last instanceof BoundConstraint)
                    			ret.add(last);
                    		else
                    			consts.add(last);
                    	
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:228:2: ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==ConstraintOperator) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:228:3: ConstraintOperator next= singleConstraint[tmodel, valuesource, vars]
                    	    {
                    	    ConstraintOperator1=(Token)input.LT(1);
                    	    match(input,ConstraintOperator,FOLLOW_ConstraintOperator_in_constraint588); 
                    	    pushFollow(FOLLOW_singleConstraint_in_constraint592);
                    	    next=singleConstraint(tmodel,  valuesource,  vars);
                    	    _fsp--;

                    	    
                    	    		// Set op if first occurrence
                    	    		if(op==null)
                    	    			op = ConstraintOperator1.getText();
                    	    	
                    	    		consts.add(next);
                    	    		if(consts.size()>1)
                    	    		{	
                    	    			if(!ConstraintOperator1.getText().equals(op))
                    	    			{
                    	    				if(op.equals("&"))
                    	    					last = new AndConstraint((IConstraint[])consts.toArray(new IConstraint[consts.size()]));
                    	    				else
                    	    					last = new OrConstraint((IConstraint[])consts.toArray(new IConstraint[consts.size()]));
                    	    				
                    	    				op = ConstraintOperator1.getText();	
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
    // $ANTLR end constraint


    // $ANTLR start singleConstraint
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );
    public final IConstraint singleConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        IConstraint tmp = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:269:2: (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] )
            int alt13=5;
            switch ( input.LA(1) ) {
            case 50:
                {
                switch ( input.LA(2) ) {
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 39:
                case 40:
                case 41:
                    {
                    alt13=1;
                    }
                    break;
                case 33:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_15 = input.LA(4);

                        if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_19 = input.LA(4);

                        if ( (LA13_19==ConstraintOperator||LA13_19==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_19>=33 && LA13_19<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_20 = input.LA(4);

                        if ( (LA13_20==ConstraintOperator||LA13_20==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_20>=33 && LA13_20<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

                        throw nvae;
                    }

                    }
                    break;
                case 34:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_21 = input.LA(4);

                        if ( (LA13_21==ConstraintOperator||LA13_21==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_21>=33 && LA13_21<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_22 = input.LA(4);

                        if ( (LA13_22==ConstraintOperator||LA13_22==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_22>=33 && LA13_22<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_23 = input.LA(4);

                        if ( ((LA13_23>=33 && LA13_23<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_23==ConstraintOperator||LA13_23==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_24 = input.LA(4);

                        if ( ((LA13_24>=33 && LA13_24<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_24==ConstraintOperator||LA13_24==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_25 = input.LA(4);

                        if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_26 = input.LA(4);

                        if ( ((LA13_26>=33 && LA13_26<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_26==ConstraintOperator||LA13_26==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                case 25:
                    {
                    alt13=5;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 1, input);

                    throw nvae;
                }

                }
                break;
            case 42:
                {
                switch ( input.LA(2) ) {
                case 33:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_15 = input.LA(4);

                        if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_19 = input.LA(4);

                        if ( (LA13_19==ConstraintOperator||LA13_19==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_19>=33 && LA13_19<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_20 = input.LA(4);

                        if ( (LA13_20==ConstraintOperator||LA13_20==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_20>=33 && LA13_20<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

                        throw nvae;
                    }

                    }
                    break;
                case 34:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_21 = input.LA(4);

                        if ( (LA13_21==ConstraintOperator||LA13_21==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_21>=33 && LA13_21<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_22 = input.LA(4);

                        if ( (LA13_22==ConstraintOperator||LA13_22==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_22>=33 && LA13_22<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_23 = input.LA(4);

                        if ( ((LA13_23>=33 && LA13_23<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_23==ConstraintOperator||LA13_23==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_24 = input.LA(4);

                        if ( ((LA13_24>=33 && LA13_24<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_24==ConstraintOperator||LA13_24==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_25 = input.LA(4);

                        if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_26 = input.LA(4);

                        if ( ((LA13_26>=33 && LA13_26<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_26==ConstraintOperator||LA13_26==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 39:
                case 40:
                case 41:
                    {
                    alt13=1;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 2, input);

                    throw nvae;
                }

                }
                break;
            case 43:
                {
                switch ( input.LA(2) ) {
                case 33:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_15 = input.LA(4);

                        if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_19 = input.LA(4);

                        if ( (LA13_19==ConstraintOperator||LA13_19==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_19>=33 && LA13_19<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_20 = input.LA(4);

                        if ( (LA13_20==ConstraintOperator||LA13_20==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_20>=33 && LA13_20<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

                        throw nvae;
                    }

                    }
                    break;
                case 34:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_21 = input.LA(4);

                        if ( (LA13_21==ConstraintOperator||LA13_21==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_21>=33 && LA13_21<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_22 = input.LA(4);

                        if ( (LA13_22==ConstraintOperator||LA13_22==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_22>=33 && LA13_22<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_23 = input.LA(4);

                        if ( ((LA13_23>=33 && LA13_23<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_23==ConstraintOperator||LA13_23==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_24 = input.LA(4);

                        if ( ((LA13_24>=33 && LA13_24<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_24==ConstraintOperator||LA13_24==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_25 = input.LA(4);

                        if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_26 = input.LA(4);

                        if ( ((LA13_26>=33 && LA13_26<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_26==ConstraintOperator||LA13_26==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 39:
                case 40:
                case 41:
                    {
                    alt13=1;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 3, input);

                    throw nvae;
                }

                }
                break;
            case 44:
                {
                switch ( input.LA(2) ) {
                case 33:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_15 = input.LA(4);

                        if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_19 = input.LA(4);

                        if ( (LA13_19==ConstraintOperator||LA13_19==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_19>=33 && LA13_19<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_20 = input.LA(4);

                        if ( (LA13_20==ConstraintOperator||LA13_20==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_20>=33 && LA13_20<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

                        throw nvae;
                    }

                    }
                    break;
                case 34:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_21 = input.LA(4);

                        if ( (LA13_21==ConstraintOperator||LA13_21==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_21>=33 && LA13_21<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_22 = input.LA(4);

                        if ( (LA13_22==ConstraintOperator||LA13_22==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_22>=33 && LA13_22<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_23 = input.LA(4);

                        if ( ((LA13_23>=33 && LA13_23<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_23==ConstraintOperator||LA13_23==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_24 = input.LA(4);

                        if ( ((LA13_24>=33 && LA13_24<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_24==ConstraintOperator||LA13_24==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_25 = input.LA(4);

                        if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_26 = input.LA(4);

                        if ( ((LA13_26>=33 && LA13_26<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_26==ConstraintOperator||LA13_26==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 39:
                case 40:
                case 41:
                    {
                    alt13=1;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 4, input);

                    throw nvae;
                }

                }
                break;
            case 45:
                {
                switch ( input.LA(2) ) {
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 39:
                case 40:
                case 41:
                    {
                    alt13=1;
                    }
                    break;
                case 33:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_15 = input.LA(4);

                        if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_19 = input.LA(4);

                        if ( (LA13_19==ConstraintOperator||LA13_19==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_19>=33 && LA13_19<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_20 = input.LA(4);

                        if ( (LA13_20==ConstraintOperator||LA13_20==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_20>=33 && LA13_20<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

                        throw nvae;
                    }

                    }
                    break;
                case 34:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_21 = input.LA(4);

                        if ( (LA13_21==ConstraintOperator||LA13_21==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_21>=33 && LA13_21<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_22 = input.LA(4);

                        if ( (LA13_22==ConstraintOperator||LA13_22==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_22>=33 && LA13_22<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_23 = input.LA(4);

                        if ( ((LA13_23>=33 && LA13_23<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_23==ConstraintOperator||LA13_23==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_24 = input.LA(4);

                        if ( ((LA13_24>=33 && LA13_24<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_24==ConstraintOperator||LA13_24==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_25 = input.LA(4);

                        if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_26 = input.LA(4);

                        if ( ((LA13_26>=33 && LA13_26<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_26==ConstraintOperator||LA13_26==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 5, input);

                    throw nvae;
                }

                }
                break;
            case 46:
                {
                switch ( input.LA(2) ) {
                case 33:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_15 = input.LA(4);

                        if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_19 = input.LA(4);

                        if ( (LA13_19==ConstraintOperator||LA13_19==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_19>=33 && LA13_19<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_20 = input.LA(4);

                        if ( (LA13_20==ConstraintOperator||LA13_20==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_20>=33 && LA13_20<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

                        throw nvae;
                    }

                    }
                    break;
                case 34:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_21 = input.LA(4);

                        if ( (LA13_21==ConstraintOperator||LA13_21==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_21>=33 && LA13_21<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_22 = input.LA(4);

                        if ( (LA13_22==ConstraintOperator||LA13_22==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_22>=33 && LA13_22<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_23 = input.LA(4);

                        if ( ((LA13_23>=33 && LA13_23<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_23==ConstraintOperator||LA13_23==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_24 = input.LA(4);

                        if ( ((LA13_24>=33 && LA13_24<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_24==ConstraintOperator||LA13_24==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_25 = input.LA(4);

                        if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_26 = input.LA(4);

                        if ( ((LA13_26>=33 && LA13_26<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_26==ConstraintOperator||LA13_26==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 39:
                case 40:
                case 41:
                    {
                    alt13=1;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 6, input);

                    throw nvae;
                }

                }
                break;
            case 47:
                {
                switch ( input.LA(2) ) {
                case 33:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_15 = input.LA(4);

                        if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_19 = input.LA(4);

                        if ( (LA13_19==ConstraintOperator||LA13_19==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_19>=33 && LA13_19<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_20 = input.LA(4);

                        if ( (LA13_20==ConstraintOperator||LA13_20==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_20>=33 && LA13_20<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

                        throw nvae;
                    }

                    }
                    break;
                case 34:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_21 = input.LA(4);

                        if ( (LA13_21==ConstraintOperator||LA13_21==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_21>=33 && LA13_21<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_22 = input.LA(4);

                        if ( (LA13_22==ConstraintOperator||LA13_22==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_22>=33 && LA13_22<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_23 = input.LA(4);

                        if ( ((LA13_23>=33 && LA13_23<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_23==ConstraintOperator||LA13_23==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_24 = input.LA(4);

                        if ( ((LA13_24>=33 && LA13_24<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_24==ConstraintOperator||LA13_24==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_25 = input.LA(4);

                        if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_26 = input.LA(4);

                        if ( ((LA13_26>=33 && LA13_26<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_26==ConstraintOperator||LA13_26==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 39:
                case 40:
                case 41:
                    {
                    alt13=1;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 7, input);

                    throw nvae;
                }

                }
                break;
            case 48:
                {
                switch ( input.LA(2) ) {
                case 33:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_15 = input.LA(4);

                        if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_19 = input.LA(4);

                        if ( (LA13_19==ConstraintOperator||LA13_19==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_19>=33 && LA13_19<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_20 = input.LA(4);

                        if ( (LA13_20==ConstraintOperator||LA13_20==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_20>=33 && LA13_20<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

                        throw nvae;
                    }

                    }
                    break;
                case 34:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_21 = input.LA(4);

                        if ( (LA13_21==ConstraintOperator||LA13_21==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_21>=33 && LA13_21<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_22 = input.LA(4);

                        if ( (LA13_22==ConstraintOperator||LA13_22==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_22>=33 && LA13_22<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_23 = input.LA(4);

                        if ( ((LA13_23>=33 && LA13_23<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_23==ConstraintOperator||LA13_23==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_24 = input.LA(4);

                        if ( ((LA13_24>=33 && LA13_24<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_24==ConstraintOperator||LA13_24==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_25 = input.LA(4);

                        if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_26 = input.LA(4);

                        if ( ((LA13_26>=33 && LA13_26<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_26==ConstraintOperator||LA13_26==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 39:
                case 40:
                case 41:
                    {
                    alt13=1;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 8, input);

                    throw nvae;
                }

                }
                break;
            case 49:
                {
                switch ( input.LA(2) ) {
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 39:
                case 40:
                case 41:
                    {
                    alt13=1;
                    }
                    break;
                case 33:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_15 = input.LA(4);

                        if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_19 = input.LA(4);

                        if ( (LA13_19==ConstraintOperator||LA13_19==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_19>=33 && LA13_19<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_20 = input.LA(4);

                        if ( (LA13_20==ConstraintOperator||LA13_20==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_20>=33 && LA13_20<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

                        throw nvae;
                    }

                    }
                    break;
                case 34:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_21 = input.LA(4);

                        if ( (LA13_21==ConstraintOperator||LA13_21==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_21>=33 && LA13_21<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_22 = input.LA(4);

                        if ( (LA13_22==ConstraintOperator||LA13_22==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_22>=33 && LA13_22<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_23 = input.LA(4);

                        if ( ((LA13_23>=33 && LA13_23<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_23==ConstraintOperator||LA13_23==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_24 = input.LA(4);

                        if ( ((LA13_24>=33 && LA13_24<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_24==ConstraintOperator||LA13_24==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA13_25 = input.LA(4);

                        if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA13_26 = input.LA(4);

                        if ( ((LA13_26>=33 && LA13_26<=34)) ) {
                            alt13=3;
                        }
                        else if ( (LA13_26==ConstraintOperator||LA13_26==27) ) {
                            alt13=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 9, input);

                    throw nvae;
                }

                }
                break;
            case CharacterLiteral:
            case StringLiteral:
            case BooleanLiteral:
            case FloatingPointLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 39:
            case 40:
            case 41:
                {
                alt13=1;
                }
                break;
            case 33:
                {
                switch ( input.LA(2) ) {
                case Identifiertoken:
                    {
                    int LA13_15 = input.LA(3);

                    if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                        alt13=3;
                    }
                    else if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                        alt13=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                        throw nvae;
                    }
                    }
                    break;
                case 29:
                    {
                    int LA13_16 = input.LA(3);

                    if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                        alt13=3;
                    }
                    else if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                        alt13=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                        throw nvae;
                    }
                    }
                    break;
                case 28:
                    {
                    int LA13_17 = input.LA(3);

                    if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                        alt13=3;
                    }
                    else if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                        alt13=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                        throw nvae;
                    }
                    }
                    break;
                case 26:
                    {
                    int LA13_18 = input.LA(3);

                    if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                        alt13=3;
                    }
                    else if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                        alt13=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                        throw nvae;
                    }
                    }
                    break;
                case 48:
                    {
                    int LA13_19 = input.LA(3);

                    if ( (LA13_19==ConstraintOperator||LA13_19==27) ) {
                        alt13=2;
                    }
                    else if ( ((LA13_19>=33 && LA13_19<=34)) ) {
                        alt13=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                        throw nvae;
                    }
                    }
                    break;
                case 49:
                    {
                    int LA13_20 = input.LA(3);

                    if ( (LA13_20==ConstraintOperator||LA13_20==27) ) {
                        alt13=2;
                    }
                    else if ( ((LA13_20>=33 && LA13_20<=34)) ) {
                        alt13=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

                    throw nvae;
                }

                }
                break;
            case 34:
                {
                switch ( input.LA(2) ) {
                case Identifiertoken:
                    {
                    int LA13_21 = input.LA(3);

                    if ( (LA13_21==ConstraintOperator||LA13_21==27) ) {
                        alt13=2;
                    }
                    else if ( ((LA13_21>=33 && LA13_21<=34)) ) {
                        alt13=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

                        throw nvae;
                    }
                    }
                    break;
                case 29:
                    {
                    int LA13_22 = input.LA(3);

                    if ( (LA13_22==ConstraintOperator||LA13_22==27) ) {
                        alt13=2;
                    }
                    else if ( ((LA13_22>=33 && LA13_22<=34)) ) {
                        alt13=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

                        throw nvae;
                    }
                    }
                    break;
                case 28:
                    {
                    int LA13_23 = input.LA(3);

                    if ( ((LA13_23>=33 && LA13_23<=34)) ) {
                        alt13=3;
                    }
                    else if ( (LA13_23==ConstraintOperator||LA13_23==27) ) {
                        alt13=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

                        throw nvae;
                    }
                    }
                    break;
                case 26:
                    {
                    int LA13_24 = input.LA(3);

                    if ( ((LA13_24>=33 && LA13_24<=34)) ) {
                        alt13=3;
                    }
                    else if ( (LA13_24==ConstraintOperator||LA13_24==27) ) {
                        alt13=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                        throw nvae;
                    }
                    }
                    break;
                case 48:
                    {
                    int LA13_25 = input.LA(3);

                    if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                        alt13=3;
                    }
                    else if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                        alt13=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                        throw nvae;
                    }
                    }
                    break;
                case 49:
                    {
                    int LA13_26 = input.LA(3);

                    if ( ((LA13_26>=33 && LA13_26<=34)) ) {
                        alt13=3;
                    }
                    else if ( (LA13_26==ConstraintOperator||LA13_26==27) ) {
                        alt13=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                    throw nvae;
                }

                }
                break;
            case 35:
                {
                alt13=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("268:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:269:4: tmp= literalConstraint[valuesource]
                    {
                    pushFollow(FOLLOW_literalConstraint_in_singleConstraint624);
                    tmp=literalConstraint(valuesource);
                    _fsp--;

                    constraint = tmp;

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:270:4: tmp= boundConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_boundConstraint_in_singleConstraint635);
                    tmp=boundConstraint(tmodel,  valuesource,  vars);
                    _fsp--;

                    constraint = tmp;

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:271:4: tmp= multiBoundConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_multiBoundConstraint_in_singleConstraint645);
                    tmp=multiBoundConstraint(tmodel,  valuesource,  vars);
                    _fsp--;

                    constraint = tmp;

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:272:4: tmp= predicateConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_predicateConstraint_in_singleConstraint655);
                    tmp=predicateConstraint(tmodel,  valuesource,  vars);
                    _fsp--;

                    constraint = tmp;

                    }
                    break;
                case 5 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:273:4: tmp= returnValueConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_returnValueConstraint_in_singleConstraint665);
                    tmp=returnValueConstraint(tmodel,  valuesource,  vars);
                    _fsp--;

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
    // $ANTLR end singleConstraint


    // $ANTLR start literalConstraint
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:276:1: literalConstraint[Object valuesource] returns [IConstraint constraint] : (op= operator )? val= constant ;
    public final IConstraint literalConstraint(Object valuesource) throws RecognitionException {
        IConstraint constraint = null;

        IOperator op = null;

        Object val = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:277:2: ( (op= operator )? val= constant )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:277:4: (op= operator )? val= constant
            {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:277:6: (op= operator )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( ((LA14_0>=42 && LA14_0<=50)) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:277:6: op= operator
                    {
                    pushFollow(FOLLOW_operator_in_literalConstraint687);
                    op=operator();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_constant_in_literalConstraint692);
            val=constant();
            _fsp--;

            
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
    // $ANTLR end literalConstraint


    // $ANTLR start someBoundConstraint
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );
    public final IConstraint someBoundConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        IConstraint bc = null;

        IConstraint mbc = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:287:2: (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] )
            int alt15=2;
            switch ( input.LA(1) ) {
            case 50:
                {
                int LA15_1 = input.LA(2);

                if ( (LA15_1==33) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_12 = input.LA(4);

                        if ( ((LA15_12>=33 && LA15_12<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_12==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_13 = input.LA(4);

                        if ( (LA15_13==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_13>=33 && LA15_13<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_15 = input.LA(4);

                        if ( ((LA15_15>=33 && LA15_15<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_15==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_16 = input.LA(4);

                        if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_17 = input.LA(4);

                        if ( ((LA15_17>=33 && LA15_17<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_17==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_1==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_19 = input.LA(4);

                        if ( ((LA15_19>=33 && LA15_19<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_19==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_22 = input.LA(4);

                        if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_23 = input.LA(4);

                        if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 1, input);

                    throw nvae;
                }
                }
                break;
            case 42:
                {
                int LA15_2 = input.LA(2);

                if ( (LA15_2==33) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_12 = input.LA(4);

                        if ( ((LA15_12>=33 && LA15_12<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_12==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_13 = input.LA(4);

                        if ( (LA15_13==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_13>=33 && LA15_13<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_15 = input.LA(4);

                        if ( ((LA15_15>=33 && LA15_15<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_15==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_16 = input.LA(4);

                        if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_17 = input.LA(4);

                        if ( ((LA15_17>=33 && LA15_17<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_17==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_2==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_19 = input.LA(4);

                        if ( ((LA15_19>=33 && LA15_19<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_19==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_22 = input.LA(4);

                        if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_23 = input.LA(4);

                        if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 2, input);

                    throw nvae;
                }
                }
                break;
            case 43:
                {
                int LA15_3 = input.LA(2);

                if ( (LA15_3==33) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_12 = input.LA(4);

                        if ( ((LA15_12>=33 && LA15_12<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_12==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_13 = input.LA(4);

                        if ( (LA15_13==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_13>=33 && LA15_13<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_15 = input.LA(4);

                        if ( ((LA15_15>=33 && LA15_15<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_15==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_16 = input.LA(4);

                        if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_17 = input.LA(4);

                        if ( ((LA15_17>=33 && LA15_17<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_17==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_3==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_19 = input.LA(4);

                        if ( ((LA15_19>=33 && LA15_19<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_19==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_22 = input.LA(4);

                        if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_23 = input.LA(4);

                        if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 3, input);

                    throw nvae;
                }
                }
                break;
            case 44:
                {
                int LA15_4 = input.LA(2);

                if ( (LA15_4==33) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_12 = input.LA(4);

                        if ( ((LA15_12>=33 && LA15_12<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_12==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_13 = input.LA(4);

                        if ( (LA15_13==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_13>=33 && LA15_13<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_15 = input.LA(4);

                        if ( ((LA15_15>=33 && LA15_15<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_15==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_16 = input.LA(4);

                        if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_17 = input.LA(4);

                        if ( ((LA15_17>=33 && LA15_17<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_17==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_4==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_19 = input.LA(4);

                        if ( ((LA15_19>=33 && LA15_19<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_19==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_22 = input.LA(4);

                        if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_23 = input.LA(4);

                        if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 4, input);

                    throw nvae;
                }
                }
                break;
            case 45:
                {
                int LA15_5 = input.LA(2);

                if ( (LA15_5==33) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_12 = input.LA(4);

                        if ( ((LA15_12>=33 && LA15_12<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_12==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_13 = input.LA(4);

                        if ( (LA15_13==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_13>=33 && LA15_13<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_15 = input.LA(4);

                        if ( ((LA15_15>=33 && LA15_15<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_15==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_16 = input.LA(4);

                        if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_17 = input.LA(4);

                        if ( ((LA15_17>=33 && LA15_17<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_17==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_5==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_19 = input.LA(4);

                        if ( ((LA15_19>=33 && LA15_19<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_19==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_22 = input.LA(4);

                        if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_23 = input.LA(4);

                        if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 5, input);

                    throw nvae;
                }
                }
                break;
            case 46:
                {
                int LA15_6 = input.LA(2);

                if ( (LA15_6==33) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_12 = input.LA(4);

                        if ( ((LA15_12>=33 && LA15_12<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_12==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_13 = input.LA(4);

                        if ( (LA15_13==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_13>=33 && LA15_13<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_15 = input.LA(4);

                        if ( ((LA15_15>=33 && LA15_15<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_15==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_16 = input.LA(4);

                        if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_17 = input.LA(4);

                        if ( ((LA15_17>=33 && LA15_17<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_17==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_6==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_19 = input.LA(4);

                        if ( ((LA15_19>=33 && LA15_19<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_19==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_22 = input.LA(4);

                        if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_23 = input.LA(4);

                        if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 6, input);

                    throw nvae;
                }
                }
                break;
            case 47:
                {
                int LA15_7 = input.LA(2);

                if ( (LA15_7==33) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_12 = input.LA(4);

                        if ( ((LA15_12>=33 && LA15_12<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_12==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_13 = input.LA(4);

                        if ( (LA15_13==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_13>=33 && LA15_13<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_15 = input.LA(4);

                        if ( ((LA15_15>=33 && LA15_15<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_15==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_16 = input.LA(4);

                        if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_17 = input.LA(4);

                        if ( ((LA15_17>=33 && LA15_17<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_17==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_7==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_19 = input.LA(4);

                        if ( ((LA15_19>=33 && LA15_19<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_19==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_22 = input.LA(4);

                        if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_23 = input.LA(4);

                        if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 7, input);

                    throw nvae;
                }
                }
                break;
            case 48:
                {
                int LA15_8 = input.LA(2);

                if ( (LA15_8==33) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_12 = input.LA(4);

                        if ( ((LA15_12>=33 && LA15_12<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_12==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_13 = input.LA(4);

                        if ( (LA15_13==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_13>=33 && LA15_13<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_15 = input.LA(4);

                        if ( ((LA15_15>=33 && LA15_15<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_15==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_16 = input.LA(4);

                        if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_17 = input.LA(4);

                        if ( ((LA15_17>=33 && LA15_17<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_17==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_8==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_19 = input.LA(4);

                        if ( ((LA15_19>=33 && LA15_19<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_19==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_22 = input.LA(4);

                        if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_23 = input.LA(4);

                        if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 8, input);

                    throw nvae;
                }
                }
                break;
            case 49:
                {
                int LA15_9 = input.LA(2);

                if ( (LA15_9==33) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_12 = input.LA(4);

                        if ( ((LA15_12>=33 && LA15_12<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_12==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_13 = input.LA(4);

                        if ( (LA15_13==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_13>=33 && LA15_13<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_15 = input.LA(4);

                        if ( ((LA15_15>=33 && LA15_15<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_15==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_16 = input.LA(4);

                        if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_17 = input.LA(4);

                        if ( ((LA15_17>=33 && LA15_17<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_17==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_9==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA15_19 = input.LA(4);

                        if ( ((LA15_19>=33 && LA15_19<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_19==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 48:
                        {
                        int LA15_22 = input.LA(4);

                        if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 49:
                        {
                        int LA15_23 = input.LA(4);

                        if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 9, input);

                    throw nvae;
                }
                }
                break;
            case 33:
                {
                switch ( input.LA(2) ) {
                case Identifiertoken:
                    {
                    int LA15_12 = input.LA(3);

                    if ( ((LA15_12>=33 && LA15_12<=34)) ) {
                        alt15=2;
                    }
                    else if ( (LA15_12==EOF) ) {
                        alt15=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

                        throw nvae;
                    }
                    }
                    break;
                case 29:
                    {
                    int LA15_13 = input.LA(3);

                    if ( (LA15_13==EOF) ) {
                        alt15=1;
                    }
                    else if ( ((LA15_13>=33 && LA15_13<=34)) ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                        throw nvae;
                    }
                    }
                    break;
                case 28:
                    {
                    int LA15_14 = input.LA(3);

                    if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                        alt15=2;
                    }
                    else if ( (LA15_14==EOF) ) {
                        alt15=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

                        throw nvae;
                    }
                    }
                    break;
                case 26:
                    {
                    int LA15_15 = input.LA(3);

                    if ( ((LA15_15>=33 && LA15_15<=34)) ) {
                        alt15=2;
                    }
                    else if ( (LA15_15==EOF) ) {
                        alt15=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                        throw nvae;
                    }
                    }
                    break;
                case 48:
                    {
                    int LA15_16 = input.LA(3);

                    if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                        alt15=2;
                    }
                    else if ( (LA15_16==EOF) ) {
                        alt15=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                        throw nvae;
                    }
                    }
                    break;
                case 49:
                    {
                    int LA15_17 = input.LA(3);

                    if ( ((LA15_17>=33 && LA15_17<=34)) ) {
                        alt15=2;
                    }
                    else if ( (LA15_17==EOF) ) {
                        alt15=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                    throw nvae;
                }

                }
                break;
            case 34:
                {
                switch ( input.LA(2) ) {
                case Identifiertoken:
                    {
                    int LA15_18 = input.LA(3);

                    if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                        alt15=2;
                    }
                    else if ( (LA15_18==EOF) ) {
                        alt15=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

                        throw nvae;
                    }
                    }
                    break;
                case 29:
                    {
                    int LA15_19 = input.LA(3);

                    if ( ((LA15_19>=33 && LA15_19<=34)) ) {
                        alt15=2;
                    }
                    else if ( (LA15_19==EOF) ) {
                        alt15=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                        throw nvae;
                    }
                    }
                    break;
                case 28:
                    {
                    int LA15_20 = input.LA(3);

                    if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                        alt15=2;
                    }
                    else if ( (LA15_20==EOF) ) {
                        alt15=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                        throw nvae;
                    }
                    }
                    break;
                case 26:
                    {
                    int LA15_21 = input.LA(3);

                    if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                        alt15=2;
                    }
                    else if ( (LA15_21==EOF) ) {
                        alt15=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                        throw nvae;
                    }
                    }
                    break;
                case 48:
                    {
                    int LA15_22 = input.LA(3);

                    if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                        alt15=2;
                    }
                    else if ( (LA15_22==EOF) ) {
                        alt15=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                        throw nvae;
                    }
                    }
                    break;
                case 49:
                    {
                    int LA15_23 = input.LA(3);

                    if ( (LA15_23==EOF) ) {
                        alt15=1;
                    }
                    else if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                    throw nvae;
                }

                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("286:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:287:4: bc= boundConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_boundConstraint_in_someBoundConstraint715);
                    bc=boundConstraint(tmodel,  valuesource,  vars);
                    _fsp--;

                    
                    		constraint = bc;
                    	

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:291:4: mbc= multiBoundConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_multiBoundConstraint_in_someBoundConstraint728);
                    mbc=multiBoundConstraint(tmodel,  valuesource,  vars);
                    _fsp--;

                    
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
    // $ANTLR end someBoundConstraint


    // $ANTLR start boundConstraint
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:297:1: boundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] ;
    public final IConstraint boundConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        IOperator op = null;

        Variable var = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:298:2: ( (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:298:4: (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars]
            {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:298:6: (op= operator )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( ((LA16_0>=42 && LA16_0<=50)) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:298:6: op= operator
                    {
                    pushFollow(FOLLOW_operator_in_boundConstraint751);
                    op=operator();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_variable_in_boundConstraint756);
            var=variable(op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel,  valuesource): null,  vars);
            _fsp--;

            
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
    // $ANTLR end boundConstraint


    // $ANTLR start multiBoundConstraint
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:307:1: multiBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+ ;
    public final IConstraint multiBoundConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        IOperator op = null;

        Variable var = null;

        Variable varn = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:308:2: ( (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+ )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:309:2: (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+
            {
            
            		List vs = new ArrayList();
            	
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:312:4: (op= operator )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( ((LA17_0>=42 && LA17_0<=50)) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:312:4: op= operator
                    {
                    pushFollow(FOLLOW_operator_in_multiBoundConstraint784);
                    op=operator();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_variable_in_multiBoundConstraint789);
            var=variable(op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel,  valuesource): null,  vars);
            _fsp--;

            
            		vs.add(var);
            	
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:316:2: (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+
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
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:316:3: varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars]
            	    {
            	    pushFollow(FOLLOW_variable_in_multiBoundConstraint799);
            	    varn=variable(SConditions.getValueSourceType(tmodel,  valuesource),  vars);
            	    _fsp--;

            	    
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
    // $ANTLR end multiBoundConstraint


    // $ANTLR start predicateConstraint
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:329:1: predicateConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : ':' ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] ) ;
    public final IConstraint predicateConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        FunctionCall fc = null;

        FunctionCall oc = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:330:2: ( ':' ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] ) )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:330:4: ':' ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )
            {
            match(input,35,FOLLOW_35_in_predicateConstraint827); 
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:330:8: ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )
            int alt19=2;
            alt19 = dfa19.predict(input);
            switch (alt19) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:330:9: {...}?fc= functionCall[tmodel, vars]
                    {
                    if ( !(SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)) ) {
                        throw new FailedPredicateException(input, "predicateConstraint", "SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)");
                    }
                    pushFollow(FOLLOW_functionCall_in_predicateConstraint834);
                    fc=functionCall(tmodel,  vars);
                    _fsp--;

                    constraint = new PredicateConstraint(fc);

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:331:4: oc= operatorCall[tmodel, vars]
                    {
                    pushFollow(FOLLOW_operatorCall_in_predicateConstraint845);
                    oc=operatorCall(tmodel,  vars);
                    _fsp--;

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
    // $ANTLR end predicateConstraint


    // $ANTLR start returnValueConstraint
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:336:1: returnValueConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : equalOperator ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] ) ;
    public final IConstraint returnValueConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        FunctionCall fc = null;

        FunctionCall oc = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:337:2: ( equalOperator ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] ) )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:337:4: equalOperator ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )
            {
            pushFollow(FOLLOW_equalOperator_in_returnValueConstraint869);
            equalOperator();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:337:18: ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )
            int alt20=2;
            alt20 = dfa20.predict(input);
            switch (alt20) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:337:19: {...}?fc= functionCall[tmodel, vars]
                    {
                    if ( !(SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)) ) {
                        throw new FailedPredicateException(input, "returnValueConstraint", "SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)");
                    }
                    pushFollow(FOLLOW_functionCall_in_returnValueConstraint876);
                    fc=functionCall(tmodel,  vars);
                    _fsp--;

                    constraint = new ValueSourceReturnValueConstraint(valuesource, fc, IOperator.EQUAL);

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:338:4: oc= operatorCall[tmodel, vars]
                    {
                    pushFollow(FOLLOW_operatorCall_in_returnValueConstraint887);
                    oc=operatorCall(tmodel,  vars);
                    _fsp--;

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
    // $ANTLR end returnValueConstraint


    // $ANTLR start functionCall
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:342:1: functionCall[OAVTypeModel tmodel, Map vars] returns [FunctionCall fc] : '(' fn= functionName (exp= parameter[tmodel, vars] )* ')' ;
    public final FunctionCall functionCall(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        FunctionCall fc = null;

        String fn = null;

        Object exp = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:343:2: ( '(' fn= functionName (exp= parameter[tmodel, vars] )* ')' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:344:2: '(' fn= functionName (exp= parameter[tmodel, vars] )* ')'
            {
            
            		List exps = new ArrayList();
            	
            match(input,25,FOLLOW_25_in_functionCall917); 
            pushFollow(FOLLOW_functionName_in_functionCall921);
            fn=functionName();
            _fsp--;

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:347:22: (exp= parameter[tmodel, vars] )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( ((LA21_0>=CharacterLiteral && LA21_0<=DecimalLiteral)||LA21_0==25||(LA21_0>=33 && LA21_0<=34)||(LA21_0>=39 && LA21_0<=41)) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:347:23: exp= parameter[tmodel, vars]
            	    {
            	    pushFollow(FOLLOW_parameter_in_functionCall926);
            	    exp=parameter(tmodel,  vars);
            	    _fsp--;

            	    
            	    		exps.add(exp);
            	    	

            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);

            match(input,27,FOLLOW_27_in_functionCall936); 
            
            				Class	clazz	= SReflect.findClass0(fn, imports, tmodel.getClassLoader());
                        	IFunction func = null;
            				if(MethodCallFunction.class.equals(clazz))
            				{
            					String clazzname = (String)exps.remove(0);
            					String methodname = (String)exps.remove(0);
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
    // $ANTLR end functionCall


    // $ANTLR start operatorCall
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:390:1: operatorCall[OAVTypeModel tmodel, Map vars] returns [FunctionCall fc] : '(' (op= operator )? exp1= parameter[tmodel, vars] exp2= parameter[tmodel, vars] ')' ;
    public final FunctionCall operatorCall(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        FunctionCall fc = null;

        IOperator op = null;

        Object exp1 = null;

        Object exp2 = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:391:2: ( '(' (op= operator )? exp1= parameter[tmodel, vars] exp2= parameter[tmodel, vars] ')' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:391:4: '(' (op= operator )? exp1= parameter[tmodel, vars] exp2= parameter[tmodel, vars] ')'
            {
            match(input,25,FOLLOW_25_in_operatorCall957); 
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:391:11: (op= operator )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( ((LA22_0>=42 && LA22_0<=50)) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:391:11: op= operator
                    {
                    pushFollow(FOLLOW_operator_in_operatorCall962);
                    op=operator();
                    _fsp--;


                    }
                    break;

            }

            pushFollow(FOLLOW_parameter_in_operatorCall967);
            exp1=parameter(tmodel,  vars);
            _fsp--;

            pushFollow(FOLLOW_parameter_in_operatorCall972);
            exp2=parameter(tmodel,  vars);
            _fsp--;

            match(input,27,FOLLOW_27_in_operatorCall975); 
            
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
    // $ANTLR end operatorCall


    // $ANTLR start parameter
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:398:1: parameter[OAVTypeModel tmodel, Map vars] returns [Object val] : (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] );
    public final Object parameter(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        Object val = null;

        Object tmp1 = null;

        Variable tmp2 = null;

        FunctionCall tmp3 = null;

        FunctionCall tmp4 = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:399:2: (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] )
            int alt23=4;
            switch ( input.LA(1) ) {
            case CharacterLiteral:
            case StringLiteral:
            case BooleanLiteral:
            case FloatingPointLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 39:
            case 40:
            case 41:
                {
                alt23=1;
                }
                break;
            case 33:
            case 34:
                {
                alt23=2;
                }
                break;
            case 25:
                {
                switch ( input.LA(2) ) {
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 25:
                case 33:
                case 34:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 50:
                    {
                    alt23=4;
                    }
                    break;
                case 48:
                    {
                    int LA23_5 = input.LA(3);

                    if ( (SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)) ) {
                        alt23=3;
                    }
                    else if ( (true) ) {
                        alt23=4;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("398:1: parameter[OAVTypeModel tmodel, Map vars] returns [Object val] : (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] );", 23, 5, input);

                        throw nvae;
                    }
                    }
                    break;
                case 49:
                    {
                    int LA23_6 = input.LA(3);

                    if ( (SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)) ) {
                        alt23=3;
                    }
                    else if ( (true) ) {
                        alt23=4;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("398:1: parameter[OAVTypeModel tmodel, Map vars] returns [Object val] : (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] );", 23, 6, input);

                        throw nvae;
                    }
                    }
                    break;
                case Identifiertoken:
                case 26:
                case 28:
                case 29:
                    {
                    alt23=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("398:1: parameter[OAVTypeModel tmodel, Map vars] returns [Object val] : (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] );", 23, 3, input);

                    throw nvae;
                }

                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("398:1: parameter[OAVTypeModel tmodel, Map vars] returns [Object val] : (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] );", 23, 0, input);

                throw nvae;
            }

            switch (alt23) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:399:4: tmp1= constant
                    {
                    pushFollow(FOLLOW_constant_in_parameter998);
                    tmp1=constant();
                    _fsp--;

                    val = tmp1;

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:400:4: tmp2= variable[null, vars]
                    {
                    pushFollow(FOLLOW_variable_in_parameter1008);
                    tmp2=variable(null,  vars);
                    _fsp--;

                    val = tmp2;

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:401:4: {...}?tmp3= functionCall[tmodel, vars]
                    {
                    if ( !(SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)) ) {
                        throw new FailedPredicateException(input, "parameter", "SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)");
                    }
                    pushFollow(FOLLOW_functionCall_in_parameter1020);
                    tmp3=functionCall(tmodel,  vars);
                    _fsp--;

                    val = tmp3;

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:402:4: tmp4= operatorCall[tmodel, vars]
                    {
                    pushFollow(FOLLOW_operatorCall_in_parameter1030);
                    tmp4=operatorCall(tmodel,  vars);
                    _fsp--;

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
    // $ANTLR end parameter


    // $ANTLR start constant
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:405:1: constant returns [Object val] : tmp= literal ;
    public final Object constant() throws RecognitionException {
        Object val = null;

        Object tmp = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:406:2: (tmp= literal )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:406:4: tmp= literal
            {
            pushFollow(FOLLOW_literal_in_constant1051);
            tmp=literal();
            _fsp--;

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
    // $ANTLR end constant


    // $ANTLR start variable
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:409:1: variable[OAVObjectType type, Map vars] returns [Variable var] : (tmp= singleFieldVariable[type, vars] | tmp= multiFieldVariable[type, vars] );
    public final Variable variable(OAVObjectType type, Map vars) throws RecognitionException {
        Variable var = null;

        Variable tmp = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:410:2: (tmp= singleFieldVariable[type, vars] | tmp= multiFieldVariable[type, vars] )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==33) ) {
                alt24=1;
            }
            else if ( (LA24_0==34) ) {
                alt24=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("409:1: variable[OAVObjectType type, Map vars] returns [Variable var] : (tmp= singleFieldVariable[type, vars] | tmp= multiFieldVariable[type, vars] );", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:410:4: tmp= singleFieldVariable[type, vars]
                    {
                    pushFollow(FOLLOW_singleFieldVariable_in_variable1074);
                    tmp=singleFieldVariable(type,  vars);
                    _fsp--;

                    var = tmp;

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:411:4: tmp= multiFieldVariable[type, vars]
                    {
                    pushFollow(FOLLOW_multiFieldVariable_in_variable1084);
                    tmp=multiFieldVariable(type,  vars);
                    _fsp--;

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
    // $ANTLR end variable


    // $ANTLR start singleFieldVariable
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:414:1: singleFieldVariable[OAVObjectType type, Map vars] returns [Variable var] : '?' id= identifier ;
    public final Variable singleFieldVariable(OAVObjectType type, Map vars) throws RecognitionException {
        Variable var = null;

        Token id = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:415:2: ( '?' id= identifier )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:415:4: '?' id= identifier
            {
            match(input,33,FOLLOW_33_in_singleFieldVariable1104); 
            pushFollow(FOLLOW_identifier_in_singleFieldVariable1108);
            id=identifier();
            _fsp--;

            	
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
    // $ANTLR end singleFieldVariable


    // $ANTLR start multiFieldVariable
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:431:1: multiFieldVariable[OAVObjectType type, Map vars] returns [Variable var] : '$?' id= identifier ;
    public final Variable multiFieldVariable(OAVObjectType type, Map vars) throws RecognitionException {
        Variable var = null;

        Token id = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:432:2: ( '$?' id= identifier )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:432:4: '$?' id= identifier
            {
            match(input,34,FOLLOW_34_in_multiFieldVariable1130); 
            pushFollow(FOLLOW_identifier_in_multiFieldVariable1134);
            id=identifier();
            _fsp--;

            
            		String vn = "$?"+id.getText();
            		var = (Variable)vars.get(vn);
            		if(var==null)
            		{
            			var = new Variable(vn, type, true);
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
    // $ANTLR end multiFieldVariable


    // $ANTLR start typename
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:448:1: typename returns [String id] : tmp= identifier ( '.' tmp= identifier )* ;
    public final String typename() throws RecognitionException {
        String id = null;

        Token tmp = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:449:2: (tmp= identifier ( '.' tmp= identifier )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:450:2: tmp= identifier ( '.' tmp= identifier )*
            {
            
            		StringBuffer buf = new StringBuffer();
            	
            pushFollow(FOLLOW_identifier_in_typename1160);
            tmp=identifier();
            _fsp--;

            
            		buf.append(tmp.getText());
            	
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:457:2: ( '.' tmp= identifier )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==36) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:457:3: '.' tmp= identifier
            	    {
            	    match(input,36,FOLLOW_36_in_typename1168); 
            	    pushFollow(FOLLOW_identifier_in_typename1172);
            	    tmp=identifier();
            	    _fsp--;

            	    
            	    		buf.append(".").append(tmp.getText());
            	    	

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
    // $ANTLR end typename


    // $ANTLR start slotname
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:474:1: slotname returns [String id] : tmp= identifier ( '.' tmp= identifier | ':' tmp= identifier | '[' tmp= identifier | ']' tmp= identifier )* ;
    public final String slotname() throws RecognitionException {
        String id = null;

        Token tmp = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:475:2: (tmp= identifier ( '.' tmp= identifier | ':' tmp= identifier | '[' tmp= identifier | ']' tmp= identifier )* )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:476:2: tmp= identifier ( '.' tmp= identifier | ':' tmp= identifier | '[' tmp= identifier | ']' tmp= identifier )*
            {
            
            		StringBuffer buf = new StringBuffer();
            	
            pushFollow(FOLLOW_identifier_in_slotname1207);
            tmp=identifier();
            _fsp--;

            
            		buf.append(tmp.getText());
            	
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:483:2: ( '.' tmp= identifier | ':' tmp= identifier | '[' tmp= identifier | ']' tmp= identifier )*
            loop26:
            do {
                int alt26=5;
                switch ( input.LA(1) ) {
                case 35:
                    {
                    int LA26_2 = input.LA(2);

                    if ( (LA26_2==Identifiertoken||LA26_2==26||(LA26_2>=28 && LA26_2<=29)||(LA26_2>=48 && LA26_2<=49)) ) {
                        alt26=2;
                    }


                    }
                    break;
                case 36:
                    {
                    alt26=1;
                    }
                    break;
                case 37:
                    {
                    alt26=3;
                    }
                    break;
                case 38:
                    {
                    alt26=4;
                    }
                    break;

                }

                switch (alt26) {
            	case 1 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:483:3: '.' tmp= identifier
            	    {
            	    match(input,36,FOLLOW_36_in_slotname1215); 
            	    pushFollow(FOLLOW_identifier_in_slotname1219);
            	    tmp=identifier();
            	    _fsp--;

            	    
            	    		buf.append(".");
            	    		if(tmp!=null)
            	    			buf.append(tmp.getText());
            	    	

            	    }
            	    break;
            	case 2 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:489:3: ':' tmp= identifier
            	    {
            	    match(input,35,FOLLOW_35_in_slotname1226); 
            	    pushFollow(FOLLOW_identifier_in_slotname1230);
            	    tmp=identifier();
            	    _fsp--;

            	    
            	    		buf.append(":");
            	    		if(tmp!=null)
            	    			buf.append(tmp.getText());
            	    	

            	    }
            	    break;
            	case 3 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:495:3: '[' tmp= identifier
            	    {
            	    match(input,37,FOLLOW_37_in_slotname1237); 
            	    pushFollow(FOLLOW_identifier_in_slotname1241);
            	    tmp=identifier();
            	    _fsp--;

            	    
            	    		buf.append("[");
            	    		if(tmp!=null)
            	    			buf.append(tmp.getText());
            	    	

            	    }
            	    break;
            	case 4 :
            	    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:501:3: ']' tmp= identifier
            	    {
            	    match(input,38,FOLLOW_38_in_slotname1248); 
            	    pushFollow(FOLLOW_identifier_in_slotname1252);
            	    tmp=identifier();
            	    _fsp--;

            	    
            	    		buf.append("]");
            	    		if(tmp!=null)
            	    			buf.append(tmp.getText());
            	    	

            	    }
            	    break;

            	default :
            	    break loop26;
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
    // $ANTLR end slotname


    // $ANTLR start methodname
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:518:1: methodname returns [String id] : tmp= identifier ;
    public final String methodname() throws RecognitionException {
        String id = null;

        Token tmp = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:519:2: (tmp= identifier )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:519:4: tmp= identifier
            {
            pushFollow(FOLLOW_identifier_in_methodname1284);
            tmp=identifier();
            _fsp--;

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
    // $ANTLR end methodname


    // $ANTLR start functionName
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:522:1: functionName returns [String id] : tmp= typename ;
    public final String functionName() throws RecognitionException {
        String id = null;

        String tmp = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:523:2: (tmp= typename )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:523:4: tmp= typename
            {
            pushFollow(FOLLOW_typename_in_functionName1303);
            tmp=typename();
            _fsp--;

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
    // $ANTLR end functionName


    // $ANTLR start literal
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:526:1: literal returns [Object val] : (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );
    public final Object literal() throws RecognitionException {
        Object val = null;

        Token CharacterLiteral2=null;
        Token StringLiteral3=null;
        Token BooleanLiteral4=null;
        Object lit = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:527:2: (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' )
            int alt27=6;
            switch ( input.LA(1) ) {
            case 40:
            case 41:
                {
                int LA27_1 = input.LA(2);

                if ( (LA27_1==FloatingPointLiteral) ) {
                    alt27=1;
                }
                else if ( ((LA27_1>=HexLiteral && LA27_1<=DecimalLiteral)) ) {
                    alt27=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("526:1: literal returns [Object val] : (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );", 27, 1, input);

                    throw nvae;
                }
                }
                break;
            case FloatingPointLiteral:
                {
                alt27=1;
                }
                break;
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
                {
                alt27=2;
                }
                break;
            case CharacterLiteral:
                {
                alt27=3;
                }
                break;
            case StringLiteral:
                {
                alt27=4;
                }
                break;
            case BooleanLiteral:
                {
                alt27=5;
                }
                break;
            case 39:
                {
                alt27=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("526:1: literal returns [Object val] : (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );", 27, 0, input);

                throw nvae;
            }

            switch (alt27) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:527:4: lit= floatingPointLiteral
                    {
                    pushFollow(FOLLOW_floatingPointLiteral_in_literal1323);
                    lit=floatingPointLiteral();
                    _fsp--;

                    val = lit;

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:528:4: lit= integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal1332);
                    lit=integerLiteral();
                    _fsp--;

                    val = lit;

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:529:4: CharacterLiteral
                    {
                    CharacterLiteral2=(Token)input.LT(1);
                    match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal1339); 
                    val = new Character(CharacterLiteral2.getText().charAt(0));

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:530:4: StringLiteral
                    {
                    StringLiteral3=(Token)input.LT(1);
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_literal1346); 
                    val = StringLiteral3.getText().substring(1, StringLiteral3.getText().length()-1);

                    }
                    break;
                case 5 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:531:4: BooleanLiteral
                    {
                    BooleanLiteral4=(Token)input.LT(1);
                    match(input,BooleanLiteral,FOLLOW_BooleanLiteral_in_literal1353); 
                    val = BooleanLiteral4.getText().equals("true")? Boolean.TRUE: Boolean.FALSE;

                    }
                    break;
                case 6 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:532:4: 'null'
                    {
                    match(input,39,FOLLOW_39_in_literal1360); 
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
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:535:1: floatingPointLiteral returns [Object val] : (sign= ( '+' | '-' ) )? FloatingPointLiteral ;
    public final Object floatingPointLiteral() throws RecognitionException {
        Object val = null;

        Token sign=null;
        Token FloatingPointLiteral5=null;

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:536:2: ( (sign= ( '+' | '-' ) )? FloatingPointLiteral )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:536:4: (sign= ( '+' | '-' ) )? FloatingPointLiteral
            {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:536:8: (sign= ( '+' | '-' ) )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( ((LA28_0>=40 && LA28_0<=41)) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:536:8: sign= ( '+' | '-' )
                    {
                    sign=(Token)input.LT(1);
                    if ( (input.LA(1)>=40 && input.LA(1)<=41) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_floatingPointLiteral1379);    throw mse;
                    }


                    }
                    break;

            }

            FloatingPointLiteral5=(Token)input.LT(1);
            match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1386); 
            val = sign!=null && "-".equals(sign.getText())? new Double("-"+FloatingPointLiteral5.getText()): new Double(FloatingPointLiteral5.getText());

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
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:539:1: integerLiteral returns [Object val] : (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral ) ;
    public final Object integerLiteral() throws RecognitionException {
        Object val = null;

        Token sign=null;
        Token HexLiteral6=null;
        Token OctalLiteral7=null;
        Token DecimalLiteral8=null;

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:540:2: ( (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral ) )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:540:4: (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral )
            {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:540:8: (sign= ( '+' | '-' ) )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( ((LA29_0>=40 && LA29_0<=41)) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:540:8: sign= ( '+' | '-' )
                    {
                    sign=(Token)input.LT(1);
                    if ( (input.LA(1)>=40 && input.LA(1)<=41) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_integerLiteral1406);    throw mse;
                    }


                    }
                    break;

            }

            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:540:20: ( HexLiteral | OctalLiteral | DecimalLiteral )
            int alt30=3;
            switch ( input.LA(1) ) {
            case HexLiteral:
                {
                alt30=1;
                }
                break;
            case OctalLiteral:
                {
                alt30=2;
                }
                break;
            case DecimalLiteral:
                {
                alt30=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("540:20: ( HexLiteral | OctalLiteral | DecimalLiteral )", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:540:21: HexLiteral
                    {
                    HexLiteral6=(Token)input.LT(1);
                    match(input,HexLiteral,FOLLOW_HexLiteral_in_integerLiteral1414); 
                    val = sign!=null && "-".equals(sign.getText())? new Integer("-"+HexLiteral6.getText()): new Integer(HexLiteral6.getText());

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:541:4: OctalLiteral
                    {
                    OctalLiteral7=(Token)input.LT(1);
                    match(input,OctalLiteral,FOLLOW_OctalLiteral_in_integerLiteral1421); 
                    val = sign!=null && "-".equals(sign.getText())? new Integer("-"+OctalLiteral7.getText()): new Integer(OctalLiteral7.getText());

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:542:4: DecimalLiteral
                    {
                    DecimalLiteral8=(Token)input.LT(1);
                    match(input,DecimalLiteral,FOLLOW_DecimalLiteral_in_integerLiteral1428); 
                    val = sign!=null && "-".equals(sign.getText())? new Integer("-"+DecimalLiteral8.getText()): new Integer(DecimalLiteral8.getText());

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


    // $ANTLR start operator
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:545:1: operator returns [IOperator operator] : (tmp= equalOperator | '!=' | '~' | '>' | '<' | '>=' | '<=' | 'contains' | 'excludes' );
    public final IOperator operator() throws RecognitionException {
        IOperator operator = null;

        IOperator tmp = null;


        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:546:2: (tmp= equalOperator | '!=' | '~' | '>' | '<' | '>=' | '<=' | 'contains' | 'excludes' )
            int alt31=9;
            switch ( input.LA(1) ) {
            case 50:
                {
                alt31=1;
                }
                break;
            case 42:
                {
                alt31=2;
                }
                break;
            case 43:
                {
                alt31=3;
                }
                break;
            case 44:
                {
                alt31=4;
                }
                break;
            case 45:
                {
                alt31=5;
                }
                break;
            case 46:
                {
                alt31=6;
                }
                break;
            case 47:
                {
                alt31=7;
                }
                break;
            case 48:
                {
                alt31=8;
                }
                break;
            case 49:
                {
                alt31=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("545:1: operator returns [IOperator operator] : (tmp= equalOperator | '!=' | '~' | '>' | '<' | '>=' | '<=' | 'contains' | 'excludes' );", 31, 0, input);

                throw nvae;
            }

            switch (alt31) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:546:4: tmp= equalOperator
                    {
                    pushFollow(FOLLOW_equalOperator_in_operator1448);
                    tmp=equalOperator();
                    _fsp--;

                    operator = tmp;

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:547:4: '!='
                    {
                    match(input,42,FOLLOW_42_in_operator1456); 
                    operator = IOperator.NOTEQUAL;

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:548:4: '~'
                    {
                    match(input,43,FOLLOW_43_in_operator1463); 
                    operator = IOperator.NOTEQUAL;

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:549:4: '>'
                    {
                    match(input,44,FOLLOW_44_in_operator1470); 
                    operator = IOperator.GREATER;

                    }
                    break;
                case 5 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:550:4: '<'
                    {
                    match(input,45,FOLLOW_45_in_operator1477); 
                    operator = IOperator.LESS;

                    }
                    break;
                case 6 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:551:4: '>='
                    {
                    match(input,46,FOLLOW_46_in_operator1484); 
                    operator = IOperator.GREATEROREQUAL;

                    }
                    break;
                case 7 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:552:4: '<='
                    {
                    match(input,47,FOLLOW_47_in_operator1491); 
                    operator = IOperator.LESSOREQUAL;

                    }
                    break;
                case 8 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:553:4: 'contains'
                    {
                    match(input,48,FOLLOW_48_in_operator1498); 
                    operator = IOperator.CONTAINS;

                    }
                    break;
                case 9 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:554:4: 'excludes'
                    {
                    match(input,49,FOLLOW_49_in_operator1505); 
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
    // $ANTLR end operator


    // $ANTLR start equalOperator
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:557:1: equalOperator returns [IOperator operator] : '==' ;
    public final IOperator equalOperator() throws RecognitionException {
        IOperator operator = null;

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:558:2: ( '==' )
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:558:4: '=='
            {
            match(input,50,FOLLOW_50_in_equalOperator1522); 
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
    // $ANTLR end equalOperator


    // $ANTLR start identifier
    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:561:1: identifier returns [Token identifier] : (tmp= Identifiertoken | tmp= 'test' | tmp= 'not' | tmp= 'and' | tmp= 'contains' | tmp= 'excludes' );
    public final Token identifier() throws RecognitionException {
        Token identifier = null;

        Token tmp=null;

        try {
            // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:562:2: (tmp= Identifiertoken | tmp= 'test' | tmp= 'not' | tmp= 'and' | tmp= 'contains' | tmp= 'excludes' )
            int alt32=6;
            switch ( input.LA(1) ) {
            case Identifiertoken:
                {
                alt32=1;
                }
                break;
            case 29:
                {
                alt32=2;
                }
                break;
            case 28:
                {
                alt32=3;
                }
                break;
            case 26:
                {
                alt32=4;
                }
                break;
            case 48:
                {
                alt32=5;
                }
                break;
            case 49:
                {
                alt32=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("561:1: identifier returns [Token identifier] : (tmp= Identifiertoken | tmp= 'test' | tmp= 'not' | tmp= 'and' | tmp= 'contains' | tmp= 'excludes' );", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:562:4: tmp= Identifiertoken
                    {
                    tmp=(Token)input.LT(1);
                    match(input,Identifiertoken,FOLLOW_Identifiertoken_in_identifier1542); 
                    identifier = tmp;

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:563:4: tmp= 'test'
                    {
                    tmp=(Token)input.LT(1);
                    match(input,29,FOLLOW_29_in_identifier1551); 
                    identifier = tmp;

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:564:4: tmp= 'not'
                    {
                    tmp=(Token)input.LT(1);
                    match(input,28,FOLLOW_28_in_identifier1560); 
                    identifier = tmp;

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:565:4: tmp= 'and'
                    {
                    tmp=(Token)input.LT(1);
                    match(input,26,FOLLOW_26_in_identifier1569); 
                    identifier = tmp;

                    }
                    break;
                case 5 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:566:4: tmp= 'contains'
                    {
                    tmp=(Token)input.LT(1);
                    match(input,48,FOLLOW_48_in_identifier1578); 
                    identifier = tmp;

                    }
                    break;
                case 6 :
                    // C:\\projects\\jadexv2\\jadex-rules\\src\\main\\java\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:567:4: tmp= 'excludes'
                    {
                    tmp=(Token)input.LT(1);
                    match(input,49,FOLLOW_49_in_identifier1587); 
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
    // $ANTLR end identifier


    protected DFA2 dfa2 = new DFA2(this);
    protected DFA4 dfa4 = new DFA4(this);
    protected DFA19 dfa19 = new DFA19(this);
    protected DFA20 dfa20 = new DFA20(this);
    static final String DFA2_eotS =
        "\u00c2\uffff";
    static final String DFA2_eofS =
        "\u00c2\uffff";
    static final String DFA2_minS =
        "\1\31\1\14\2\uffff\2\31\1\0\1\5\1\14\3\uffff\2\5\1\0\4\5\1\0\1\5"+
        "\1\0\3\5\1\10\10\4\2\14\1\0\1\14\1\0\1\10\10\4\2\14\4\0\1\14\2\5"+
        "\1\31\14\4\6\5\14\4\6\5\1\0\1\10\10\5\2\14\2\0\4\14\1\10\10\5\2"+
        "\14\10\0\14\5\30\4\15\5\14\0\1\31\1\0";
    static final String DFA2_maxS =
        "\1\42\1\61\2\uffff\2\44\1\0\1\62\1\61\3\uffff\2\62\1\0\4\62\1\0"+
        "\1\62\1\0\3\62\1\13\10\51\2\61\1\0\1\61\1\0\1\13\10\51\2\61\4\0"+
        "\1\61\1\62\1\61\1\33\14\51\6\62\14\51\6\62\1\0\1\13\10\51\2\61\2"+
        "\0\4\61\1\13\10\51\2\61\10\0\44\51\1\62\14\51\14\0\1\33\1\0";
    static final String DFA2_acceptS =
        "\2\uffff\1\4\1\5\5\uffff\1\1\1\2\1\3\u00b6\uffff";
    static final String DFA2_specialS =
        "\6\uffff\1\0\7\uffff\1\36\4\uffff\1\6\1\uffff\1\41\16\uffff\1\37"+
        "\1\uffff\1\40\13\uffff\1\4\1\3\1\2\1\1\50\uffff\1\35\13\uffff\1"+
        "\5\1\10\17\uffff\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\20\61\uffff"+
        "\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\33\1\34\1\uffff"+
        "\1\7}>";
    static final String[] DFA2_transitionS = {
            "\1\1\7\uffff\1\3\1\2",
            "\1\3\15\uffff\1\5\1\uffff\1\6\1\4\2\uffff\1\2\17\uffff\2\3",
            "",
            "",
            "\1\7\1\uffff\1\3\10\uffff\1\3",
            "\1\10\1\uffff\1\3\5\uffff\2\11\1\uffff\1\3",
            "\1\uffff",
            "\7\13\1\17\14\uffff\1\16\1\22\1\uffff\1\21\1\20\3\uffff\2\13"+
            "\4\uffff\11\13\1\14\1\15\1\13",
            "\1\26\14\uffff\1\3\1\25\1\uffff\1\23\1\24\2\uffff\1\11\17\uffff"+
            "\1\27\1\30",
            "",
            "",
            "",
            "\1\36\1\37\1\40\1\32\1\33\1\34\1\35\15\uffff\1\44\1\uffff\1"+
            "\13\5\uffff\1\42\1\43\1\3\1\45\2\3\1\41\2\31\11\3",
            "\1\36\1\37\1\40\1\32\1\33\1\34\1\35\15\uffff\1\46\1\uffff\1"+
            "\13\5\uffff\1\42\1\43\1\3\1\45\2\3\1\41\2\31\11\3",
            "\1\uffff",
            "\1\54\1\55\1\56\1\50\1\51\1\52\1\53\15\uffff\1\62\1\uffff\1"+
            "\13\5\uffff\1\60\1\61\1\3\1\45\2\3\1\57\2\47\11\3",
            "\1\54\1\55\1\56\1\50\1\51\1\52\1\53\15\uffff\1\63\1\uffff\1"+
            "\13\5\uffff\1\60\1\61\1\3\1\45\2\3\1\57\2\47\11\3",
            "\1\54\1\55\1\56\1\50\1\51\1\52\1\53\15\uffff\1\64\1\uffff\1"+
            "\13\5\uffff\1\60\1\61\1\3\1\45\2\3\1\57\2\47\11\3",
            "\1\54\1\55\1\56\1\50\1\51\1\52\1\53\15\uffff\1\65\1\uffff\1"+
            "\13\5\uffff\1\60\1\61\1\3\1\45\2\3\1\57\2\47\11\3",
            "\1\uffff",
            "\7\3\15\uffff\1\67\1\uffff\1\11\5\uffff\3\3\1\66\16\3",
            "\1\uffff",
            "\7\3\15\uffff\1\70\1\uffff\1\11\5\uffff\3\3\1\66\16\3",
            "\7\3\15\uffff\1\70\1\uffff\1\11\5\uffff\3\3\1\66\16\3",
            "\7\3\15\uffff\1\70\1\uffff\1\11\5\uffff\3\3\1\66\16\3",
            "\1\32\1\33\1\34\1\35",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\72\15\uffff\1\75\1\3\1\74\1\73\22\uffff\1\76\1\77",
            "\1\100\15\uffff\1\103\1\3\1\102\1\101\22\uffff\1\104\1\105",
            "\1\uffff",
            "\1\106\15\uffff\1\111\1\uffff\1\110\1\107\22\uffff\1\112\1\113",
            "\1\uffff",
            "\1\50\1\51\1\52\1\53",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\2\13\4\uffff\3"+
            "\13",
            "\1\114\15\uffff\1\117\1\3\1\116\1\115\22\uffff\1\120\1\121",
            "\1\122\15\uffff\1\125\1\3\1\124\1\123\22\uffff\1\126\1\127",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\130\15\uffff\1\133\1\uffff\1\132\1\131\22\uffff\1\134\1\135",
            "\1\144\1\145\1\146\1\140\1\141\1\142\1\143\1\11\14\uffff\1\136"+
            "\1\11\1\3\2\11\3\uffff\1\150\1\151\4\uffff\1\147\2\137\11\11",
            "\7\3\1\11\14\uffff\1\152\1\11\1\3\2\11\3\uffff\2\3\4\uffff\3"+
            "\3\6\uffff\2\11",
            "\1\3\1\uffff\1\153",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\154\1\155\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\154\1\155\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\154\1\155\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\154\1\155\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\154\1\155\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\154\1\155\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\154\1\155\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\154\1\155\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\154\1\155\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\154\1\155\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\154\1\155\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\154\1\155\4\uffff"+
            "\3\13",
            "\1\54\1\55\1\56\1\50\1\51\1\52\1\53\15\uffff\1\13\1\uffff\1"+
            "\13\5\uffff\1\60\1\61\1\3\1\45\2\3\1\57\2\47\11\3",
            "\1\54\1\55\1\56\1\50\1\51\1\52\1\53\15\uffff\1\13\1\uffff\1"+
            "\13\5\uffff\1\60\1\61\1\3\1\45\2\3\1\57\2\47\11\3",
            "\1\54\1\55\1\56\1\50\1\51\1\52\1\53\15\uffff\1\13\1\uffff\1"+
            "\13\5\uffff\1\60\1\61\1\3\1\45\2\3\1\57\2\47\11\3",
            "\1\54\1\55\1\56\1\50\1\51\1\52\1\53\15\uffff\1\13\1\uffff\1"+
            "\13\5\uffff\1\60\1\61\1\3\1\45\2\3\1\57\2\47\11\3",
            "\1\54\1\55\1\56\1\50\1\51\1\52\1\53\15\uffff\1\13\1\uffff\1"+
            "\13\5\uffff\1\60\1\61\1\3\1\45\2\3\1\57\2\47\11\3",
            "\1\54\1\55\1\56\1\50\1\51\1\52\1\53\15\uffff\1\13\1\uffff\1"+
            "\13\5\uffff\1\60\1\61\1\3\1\45\2\3\1\57\2\47\11\3",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\7\3\15\uffff\1\11\1\uffff\1\11\5\uffff\3\3\1\66\16\3",
            "\7\3\15\uffff\1\11\1\uffff\1\11\5\uffff\3\3\1\66\16\3",
            "\7\3\15\uffff\1\11\1\uffff\1\11\5\uffff\3\3\1\66\16\3",
            "\7\3\15\uffff\1\11\1\uffff\1\11\5\uffff\3\3\1\66\16\3",
            "\7\3\15\uffff\1\11\1\uffff\1\11\5\uffff\3\3\1\66\16\3",
            "\7\3\15\uffff\1\11\1\uffff\1\11\5\uffff\3\3\1\66\16\3",
            "\1\uffff",
            "\1\140\1\141\1\142\1\143",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\173\1"+
            "\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\174\1"+
            "\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\175\1"+
            "\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\176\1"+
            "\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\177\1"+
            "\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u0080"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u0081"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u0082"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\u0083\15\uffff\1\u0086\1\uffff\1\u0085\1\u0084\22\uffff\1"+
            "\u0087\1\u0088",
            "\1\u0089\15\uffff\1\u008c\1\uffff\1\u008b\1\u008a\22\uffff\1"+
            "\u008d\1\u008e",
            "\1\uffff",
            "\1\uffff",
            "\1\u008f\15\uffff\1\u0092\1\uffff\1\u0091\1\u0090\22\uffff\1"+
            "\u0093\1\u0094",
            "\1\u0095\15\uffff\1\u0098\1\uffff\1\u0097\1\u0096\22\uffff\1"+
            "\u0099\1\u009a",
            "\1\u009b\15\uffff\1\u009e\1\uffff\1\u009d\1\u009c\22\uffff\1"+
            "\u009f\1\u00a0",
            "\1\u00a1\15\uffff\1\u00a4\1\uffff\1\u00a3\1\u00a2\22\uffff\1"+
            "\u00a5\1\u00a6",
            "\1\161\1\162\1\163\1\164",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\1\u00a8\15\uffff\1\u00ab\1\uffff\1\u00aa\1\u00a9\22\uffff\1"+
            "\u00ac\1\u00ad",
            "\1\u00ae\15\uffff\1\u00b1\1\uffff\1\u00b0\1\u00af\22\uffff\1"+
            "\u00b2\1\u00b3",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u00b4"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u00b5"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u00b6"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u00b7"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u00b8"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u00b9"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u00ba"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u00bb"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u00bc"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u00bd"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u00be"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\165\1\166\1\167\1\161\1\162\1\163\1\164\15\uffff\1\u00bf"+
            "\1\uffff\1\3\5\uffff\1\171\1\172\4\uffff\1\170\2\160",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00c0\5\uffff\1\156\1\157"+
            "\4\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00c0\5\uffff\1\156\1\157"+
            "\4\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00c0\5\uffff\1\156\1\157"+
            "\4\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00c0\5\uffff\1\156\1\157"+
            "\4\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00c0\5\uffff\1\156\1\157"+
            "\4\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00c0\5\uffff\1\156\1\157"+
            "\4\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00c0\5\uffff\1\156\1\157"+
            "\4\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00c0\5\uffff\1\156\1\157"+
            "\4\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00c0\5\uffff\1\156\1\157"+
            "\4\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00c0\5\uffff\1\156\1\157"+
            "\4\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00c0\5\uffff\1\156\1\157"+
            "\4\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00c0\5\uffff\1\156\1\157"+
            "\4\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\71\5\uffff\1\156\1\157\4\uffff"+
            "\3\13",
            "\7\3\17\uffff\1\11\5\uffff\3\3\3\uffff\14\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u00a7\5\uffff\2\3\4\uffff\3\3",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\3\1\uffff\1\u00c1",
            "\1\uffff"
    };

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }
        public String getDescription() {
            return "80:1: ce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : (tmp= andce[tmodel, vars] | tmp= notce[tmodel, vars] | tmp= testce[tmodel, vars] | tmp= collectce[tmodel, vars] | {...}?tmp= objectce[tmodel, vars] );";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA2_6 = input.LA(1);

                         
                        int index2_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 10;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_6);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA2_53 = input.LA(1);

                         
                        int index2_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_53);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA2_52 = input.LA(1);

                         
                        int index2_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_52);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA2_51 = input.LA(1);

                         
                        int index2_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_51);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA2_50 = input.LA(1);

                         
                        int index2_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_50);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA2_106 = input.LA(1);

                         
                        int index2_106 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_106);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA2_19 = input.LA(1);

                         
                        int index2_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_19);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA2_193 = input.LA(1);

                         
                        int index2_193 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_193);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA2_107 = input.LA(1);

                         
                        int index2_107 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_107);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA2_123 = input.LA(1);

                         
                        int index2_123 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_123);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA2_124 = input.LA(1);

                         
                        int index2_124 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_124);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA2_125 = input.LA(1);

                         
                        int index2_125 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_125);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA2_126 = input.LA(1);

                         
                        int index2_126 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_126);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA2_127 = input.LA(1);

                         
                        int index2_127 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_127);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA2_128 = input.LA(1);

                         
                        int index2_128 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_128);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA2_129 = input.LA(1);

                         
                        int index2_129 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_129);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA2_130 = input.LA(1);

                         
                        int index2_130 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_130);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA2_180 = input.LA(1);

                         
                        int index2_180 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_180);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA2_181 = input.LA(1);

                         
                        int index2_181 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_181);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA2_182 = input.LA(1);

                         
                        int index2_182 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_182);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA2_183 = input.LA(1);

                         
                        int index2_183 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_183);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA2_184 = input.LA(1);

                         
                        int index2_184 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_184);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA2_185 = input.LA(1);

                         
                        int index2_185 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_185);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA2_186 = input.LA(1);

                         
                        int index2_186 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_186);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA2_187 = input.LA(1);

                         
                        int index2_187 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_187);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA2_188 = input.LA(1);

                         
                        int index2_188 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_188);
                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA2_189 = input.LA(1);

                         
                        int index2_189 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_189);
                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA2_190 = input.LA(1);

                         
                        int index2_190 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_190);
                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA2_191 = input.LA(1);

                         
                        int index2_191 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_191);
                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA2_94 = input.LA(1);

                         
                        int index2_94 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_94);
                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA2_14 = input.LA(1);

                         
                        int index2_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_14);
                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA2_36 = input.LA(1);

                         
                        int index2_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_36);
                        if ( s>=0 ) return s;
                        break;
                    case 32 : 
                        int LA2_38 = input.LA(1);

                         
                        int index2_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_38);
                        if ( s>=0 ) return s;
                        break;
                    case 33 : 
                        int LA2_21 = input.LA(1);

                         
                        int index2_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_21);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 2, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA4_eotS =
        "\37\uffff";
    static final String DFA4_eofS =
        "\37\uffff";
    static final String DFA4_minS =
        "\1\31\1\5\1\uffff\2\5\1\uffff\1\10\2\5\26\0";
    static final String DFA4_maxS =
        "\1\31\1\62\1\uffff\2\51\1\uffff\1\13\2\51\26\0";
    static final String DFA4_acceptS =
        "\2\uffff\1\2\2\uffff\1\1\31\uffff";
    static final String DFA4_specialS =
        "\37\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\1",
            "\7\2\1\5\14\uffff\1\2\1\5\1\uffff\2\5\3\uffff\2\2\4\uffff\11"+
            "\2\1\3\1\4\1\2",
            "",
            "\1\13\1\14\1\15\1\7\1\10\1\11\1\12\15\uffff\1\21\1\uffff\1\5"+
            "\5\uffff\1\17\1\20\1\uffff\1\5\2\uffff\1\16\2\6",
            "\1\13\1\14\1\15\1\7\1\10\1\11\1\12\15\uffff\1\22\1\uffff\1\5"+
            "\5\uffff\1\17\1\20\1\uffff\1\5\2\uffff\1\16\2\6",
            "",
            "\1\7\1\10\1\11\1\12",
            "\1\30\1\31\1\32\1\24\1\25\1\26\1\27\15\uffff\1\36\1\uffff\1"+
            "\5\5\uffff\1\34\1\35\4\uffff\1\33\2\23",
            "\1\30\1\31\1\32\1\24\1\25\1\26\1\27\15\uffff\1\36\1\uffff\1"+
            "\5\5\uffff\1\34\1\35\4\uffff\1\33\2\23",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "111:15: ( ({...}?call= functionCall[tmodel, vars] ) | call= operatorCall[tmodel, vars] )";
        }
    }
    static final String DFA19_eotS =
        "\37\uffff";
    static final String DFA19_eofS =
        "\37\uffff";
    static final String DFA19_minS =
        "\1\31\1\5\1\uffff\2\5\1\uffff\1\10\2\5\26\0";
    static final String DFA19_maxS =
        "\1\31\1\62\1\uffff\2\51\1\uffff\1\13\2\51\26\0";
    static final String DFA19_acceptS =
        "\2\uffff\1\2\2\uffff\1\1\31\uffff";
    static final String DFA19_specialS =
        "\37\uffff}>";
    static final String[] DFA19_transitionS = {
            "\1\1",
            "\7\2\1\5\14\uffff\1\2\1\5\1\uffff\2\5\3\uffff\2\2\4\uffff\11"+
            "\2\1\3\1\4\1\2",
            "",
            "\1\13\1\14\1\15\1\7\1\10\1\11\1\12\15\uffff\1\21\1\uffff\1\5"+
            "\5\uffff\1\17\1\20\1\uffff\1\5\2\uffff\1\16\2\6",
            "\1\13\1\14\1\15\1\7\1\10\1\11\1\12\15\uffff\1\22\1\uffff\1\5"+
            "\5\uffff\1\17\1\20\1\uffff\1\5\2\uffff\1\16\2\6",
            "",
            "\1\7\1\10\1\11\1\12",
            "\1\30\1\31\1\32\1\24\1\25\1\26\1\27\15\uffff\1\36\1\uffff\1"+
            "\5\5\uffff\1\34\1\35\4\uffff\1\33\2\23",
            "\1\30\1\31\1\32\1\24\1\25\1\26\1\27\15\uffff\1\36\1\uffff\1"+
            "\5\5\uffff\1\34\1\35\4\uffff\1\33\2\23",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
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
            return "330:8: ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )";
        }
    }
    static final String DFA20_eotS =
        "\37\uffff";
    static final String DFA20_eofS =
        "\37\uffff";
    static final String DFA20_minS =
        "\1\31\1\5\1\uffff\2\5\1\uffff\1\10\2\5\26\0";
    static final String DFA20_maxS =
        "\1\31\1\62\1\uffff\2\51\1\uffff\1\13\2\51\26\0";
    static final String DFA20_acceptS =
        "\2\uffff\1\2\2\uffff\1\1\31\uffff";
    static final String DFA20_specialS =
        "\37\uffff}>";
    static final String[] DFA20_transitionS = {
            "\1\1",
            "\7\2\1\5\14\uffff\1\2\1\5\1\uffff\2\5\3\uffff\2\2\4\uffff\11"+
            "\2\1\3\1\4\1\2",
            "",
            "\1\13\1\14\1\15\1\7\1\10\1\11\1\12\15\uffff\1\21\1\uffff\1\5"+
            "\5\uffff\1\17\1\20\1\uffff\1\5\2\uffff\1\16\2\6",
            "\1\13\1\14\1\15\1\7\1\10\1\11\1\12\15\uffff\1\22\1\uffff\1\5"+
            "\5\uffff\1\17\1\20\1\uffff\1\5\2\uffff\1\16\2\6",
            "",
            "\1\7\1\10\1\11\1\12",
            "\1\30\1\31\1\32\1\24\1\25\1\26\1\27\15\uffff\1\36\1\uffff\1"+
            "\5\5\uffff\1\34\1\35\4\uffff\1\33\2\23",
            "\1\30\1\31\1\32\1\24\1\25\1\26\1\27\15\uffff\1\36\1\uffff\1"+
            "\5\5\uffff\1\34\1\35\4\uffff\1\33\2\23",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA20_eot = DFA.unpackEncodedString(DFA20_eotS);
    static final short[] DFA20_eof = DFA.unpackEncodedString(DFA20_eofS);
    static final char[] DFA20_min = DFA.unpackEncodedStringToUnsignedChars(DFA20_minS);
    static final char[] DFA20_max = DFA.unpackEncodedStringToUnsignedChars(DFA20_maxS);
    static final short[] DFA20_accept = DFA.unpackEncodedString(DFA20_acceptS);
    static final short[] DFA20_special = DFA.unpackEncodedString(DFA20_specialS);
    static final short[][] DFA20_transition;

    static {
        int numStates = DFA20_transitionS.length;
        DFA20_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA20_transition[i] = DFA.unpackEncodedString(DFA20_transitionS[i]);
        }
    }

    class DFA20 extends DFA {

        public DFA20(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 20;
            this.eot = DFA20_eot;
            this.eof = DFA20_eof;
            this.min = DFA20_min;
            this.max = DFA20_max;
            this.accept = DFA20_accept;
            this.special = DFA20_special;
            this.transition = DFA20_transition;
        }
        public String getDescription() {
            return "337:18: ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )";
        }
    }
 

    public static final BitSet FOLLOW_ce_in_rhs53 = new BitSet(new long[]{0x0000000602000000L});
    public static final BitSet FOLLOW_EOF_in_rhs68 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_andce_in_ce89 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_notce_in_ce101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_testce_in_ce113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collectce_in_ce125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_objectce_in_ce139 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_andce163 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_andce165 = new BitSet(new long[]{0x0000000602000000L});
    public static final BitSet FOLLOW_ce_in_andce170 = new BitSet(new long[]{0x000000060A000000L});
    public static final BitSet FOLLOW_27_in_andce180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_notce199 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_notce201 = new BitSet(new long[]{0x0000000602000000L});
    public static final BitSet FOLLOW_ce_in_notce205 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_notce208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_testce227 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_testce229 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_functionCall_in_testce237 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_operatorCall_in_testce246 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_testce250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiFieldVariable_in_collectce277 = new BitSet(new long[]{0x00000000C0000000L});
    public static final BitSet FOLLOW_set_in_collectce280 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_collectce292 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_collectce294 = new BitSet(new long[]{0x0000000602000000L});
    public static final BitSet FOLLOW_ce_in_collectce299 = new BitSet(new long[]{0x0000000E0A000000L});
    public static final BitSet FOLLOW_predicateConstraint_in_collectce312 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_collectce316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleFieldVariable_in_objectce345 = new BitSet(new long[]{0x00000000C0000000L});
    public static final BitSet FOLLOW_set_in_objectce348 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_objectce360 = new BitSet(new long[]{0x0003000034001000L});
    public static final BitSet FOLLOW_typename_in_objectce366 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_attributeConstraint_in_objectce375 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_methodConstraint_in_objectce386 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_functionConstraint_in_objectce397 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_27_in_objectce409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_attributeConstraint431 = new BitSet(new long[]{0x0003000034001000L});
    public static final BitSet FOLLOW_slotname_in_attributeConstraint435 = new BitSet(new long[]{0x0007FF8E00000FE0L});
    public static final BitSet FOLLOW_constraint_in_attributeConstraint439 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_attributeConstraint442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_methodConstraint469 = new BitSet(new long[]{0x0003000034001000L});
    public static final BitSet FOLLOW_methodname_in_methodConstraint473 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_methodConstraint475 = new BitSet(new long[]{0x000003860A000FE0L});
    public static final BitSet FOLLOW_parameter_in_methodConstraint479 = new BitSet(new long[]{0x000003860A000FE0L});
    public static final BitSet FOLLOW_27_in_methodConstraint489 = new BitSet(new long[]{0x0007FF8E00000FE0L});
    public static final BitSet FOLLOW_constraint_in_methodConstraint493 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_methodConstraint496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_functionConstraint523 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_functionCall_in_functionConstraint527 = new BitSet(new long[]{0x0007FF8E00000FE0L});
    public static final BitSet FOLLOW_constraint_in_functionConstraint532 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_functionConstraint535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_constraint565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_constraint571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleConstraint_in_constraint579 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_ConstraintOperator_in_constraint588 = new BitSet(new long[]{0x0007FF8E00000FE0L});
    public static final BitSet FOLLOW_singleConstraint_in_constraint592 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_literalConstraint_in_singleConstraint624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boundConstraint_in_singleConstraint635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiBoundConstraint_in_singleConstraint645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicateConstraint_in_singleConstraint655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_returnValueConstraint_in_singleConstraint665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_in_literalConstraint687 = new BitSet(new long[]{0x0000038000000FE0L});
    public static final BitSet FOLLOW_constant_in_literalConstraint692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boundConstraint_in_someBoundConstraint715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiBoundConstraint_in_someBoundConstraint728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_in_boundConstraint751 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_variable_in_boundConstraint756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_in_multiBoundConstraint784 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_variable_in_multiBoundConstraint789 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_variable_in_multiBoundConstraint799 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_35_in_predicateConstraint827 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_functionCall_in_predicateConstraint834 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operatorCall_in_predicateConstraint845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_equalOperator_in_returnValueConstraint869 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_functionCall_in_returnValueConstraint876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operatorCall_in_returnValueConstraint887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_functionCall917 = new BitSet(new long[]{0x0003000034001000L});
    public static final BitSet FOLLOW_functionName_in_functionCall921 = new BitSet(new long[]{0x000003860A000FE0L});
    public static final BitSet FOLLOW_parameter_in_functionCall926 = new BitSet(new long[]{0x000003860A000FE0L});
    public static final BitSet FOLLOW_27_in_functionCall936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_operatorCall957 = new BitSet(new long[]{0x0007FF8602000FE0L});
    public static final BitSet FOLLOW_operator_in_operatorCall962 = new BitSet(new long[]{0x0000038602000FE0L});
    public static final BitSet FOLLOW_parameter_in_operatorCall967 = new BitSet(new long[]{0x0000038602000FE0L});
    public static final BitSet FOLLOW_parameter_in_operatorCall972 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_operatorCall975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_in_parameter998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_parameter1008 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionCall_in_parameter1020 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operatorCall_in_parameter1030 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_constant1051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleFieldVariable_in_variable1074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiFieldVariable_in_variable1084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_singleFieldVariable1104 = new BitSet(new long[]{0x0003000034001000L});
    public static final BitSet FOLLOW_identifier_in_singleFieldVariable1108 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_multiFieldVariable1130 = new BitSet(new long[]{0x0003000034001000L});
    public static final BitSet FOLLOW_identifier_in_multiFieldVariable1134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_typename1160 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_typename1168 = new BitSet(new long[]{0x0003000034001000L});
    public static final BitSet FOLLOW_identifier_in_typename1172 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_identifier_in_slotname1207 = new BitSet(new long[]{0x0000007800000002L});
    public static final BitSet FOLLOW_36_in_slotname1215 = new BitSet(new long[]{0x0003000034001000L});
    public static final BitSet FOLLOW_identifier_in_slotname1219 = new BitSet(new long[]{0x0000007800000002L});
    public static final BitSet FOLLOW_35_in_slotname1226 = new BitSet(new long[]{0x0003000034001000L});
    public static final BitSet FOLLOW_identifier_in_slotname1230 = new BitSet(new long[]{0x0000007800000002L});
    public static final BitSet FOLLOW_37_in_slotname1237 = new BitSet(new long[]{0x0003000034001000L});
    public static final BitSet FOLLOW_identifier_in_slotname1241 = new BitSet(new long[]{0x0000007800000002L});
    public static final BitSet FOLLOW_38_in_slotname1248 = new BitSet(new long[]{0x0003000034001000L});
    public static final BitSet FOLLOW_identifier_in_slotname1252 = new BitSet(new long[]{0x0000007800000002L});
    public static final BitSet FOLLOW_identifier_in_methodname1284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typename_in_functionName1303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_floatingPointLiteral_in_literal1323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal1332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal1339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal1346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BooleanLiteral_in_literal1353 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_literal1360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_floatingPointLiteral1379 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral1406 = new BitSet(new long[]{0x0000000000000E00L});
    public static final BitSet FOLLOW_HexLiteral_in_integerLiteral1414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OctalLiteral_in_integerLiteral1421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DecimalLiteral_in_integerLiteral1428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_equalOperator_in_operator1448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_operator1456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_operator1463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_operator1470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_operator1477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_operator1484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_operator1491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_operator1498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_operator1505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_equalOperator1522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifiertoken_in_identifier1542 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifier1551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifier1560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_identifier1569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_identifier1578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_identifier1587 = new BitSet(new long[]{0x0000000000000002L});

}