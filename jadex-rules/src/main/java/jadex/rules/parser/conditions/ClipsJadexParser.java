// $ANTLR 3.0.1 C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g 2008-12-16 15:48:54

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ConstraintOperator", "CharacterLiteral", "StringLiteral", "BooleanLiteral", "FloatingPointLiteral", "HexLiteral", "OctalLiteral", "DecimalLiteral", "Identifiertoken", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "COMMENT", "LINE_COMMENT", "'('", "'and'", "')'", "'not'", "'test'", "'<-'", "'='", "'collect'", "'?'", "'$?'", "':'", "'.'", "'null'", "'+'", "'-'", "'!='", "'~'", "'>'", "'<'", "'>='", "'<='", "'contains'", "'excludes'", "'=='"
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
    public String getGrammarFileName() { return "C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g"; }

    
    protected static void adaptConditionType(Variable var, OAVObjectType type)
    {
    	if(type==null)
        		throw new RuntimeException("Type must not be null.");
        	
        	OAVObjectType otype = var.getType();
        	//System.out.println("Having: "+otype+" "+type);
        	if(otype==null)
        	{
        		var.setType(type);
        	}
        	else if(!otype.equals(type))
        	{
        		// Check compatibility and use most specific type
        		if(otype instanceof OAVJavaType)
        		{
        			Class oclazz = ((OAVJavaType)otype).getClazz();
        			Class clazz = ((OAVJavaType)type).getClazz();
        			if(oclazz.isAssignableFrom(clazz))
        			{
        				var.setType(type);
        				//System.out.println("Setting: "+type);
        			}
        			else if(!clazz.isAssignableFrom(oclazz))
        				throw new RuntimeException("Incompatible variable types: "+var+" "+oclazz+" "+clazz);
        		}
        		else
        		{
        			if(type.isSubtype(otype))
        			{
        				var.setType(type);
        				//System.out.println("Setting: "+type);
        			}
        			else if(!otype.isSubtype(type))
        				throw new RuntimeException("Incompatible variable types: "+var+" "+otype+" "+type);
        		}
        	}
    }
    
    protected static MethodCall	createMethodCall(OAVObjectType otype, String name, List params)
    {
    	if(!(otype instanceof OAVJavaType))
    	{
    		throw new RuntimeException("Method calls only supported for java types: "+otype+"."+name+params);
    	}
    	OAVJavaType	jtype	= (OAVJavaType)otype;
    	Class	clazz	= jtype.getClazz();
    	
    	Method[] methods	= SReflect.getMethods(clazz, name);
    	Method	method	= null;
    	
    	// Find one matching regardless of param types (hack???).
    	boolean	found	= false;
    	for(int i=0; i<methods.length; i++)
    	{
    		if(methods[i].getParameterTypes().length==params.size())
    		{
    			// First match.
    			if(!found)
    			{
    				found	= true;
    				method	= methods[0];
    			}
    			
    			// More than one match.
    			else
    			{
    				found	= false;
    				break;
    			}
    		}
    	}
    	
    	if(!found)
    	{
        	Class[]	argtypes	= new Class[params.size()];
        	for(int i=0; i<argtypes.length; i++)
        	{
        		if(params.get(i) instanceof Variable)
        		{
        			OAVObjectType	optype	= ((Variable)params.get(i)).getType();
        			if(!(optype instanceof OAVJavaType))
        			{
        				throw new RuntimeException("Method calls only supported for java types: "+otype+"."+name+params+", "+optype);
        			}
        			argtypes[i]	= ((OAVJavaType)optype).getClazz();
        		}
        		else if(params.get(i) instanceof FunctionCall)
        		{
        			FunctionCall	funcall	= (FunctionCall)params.get(i);
        			argtypes[i]	= funcall.getFunction().getReturnType();
        		}
        		else	// Literal
        		{
        			argtypes[i]	= params.get(i)!=null ? params.get(i).getClass() : null;
        		}
        	}
        	
        	Class[][]	paramtypes	= new Class[methods.length][];
        	for(int i=0; i<methods.length; i++)
        	{
        		paramtypes[i]	= methods[i].getParameterTypes();
        	}
        	int[]	results	= SReflect.matchArgumentTypes(argtypes, paramtypes);
        	
        	if(results.length==0)
        	{
        		throw new RuntimeException("No matching method found: "+otype+"."+name+params);
        	}
        	else if(results.length>1)
        	{
        		System.out.println("Warning: ambiguous methods: "+otype+"."+name+params);
        	}
        	method	= methods[results[0]];
    	}
    
    	return new MethodCall(jtype, method, params);
    }
    
    protected static OAVObjectType getValueSourceType(OAVTypeModel tmodel, Object valuesource)
    {
    	OAVObjectType ret = null;
    	
    	if(valuesource instanceof OAVAttributeType)
    	{
    		ret = ((OAVAttributeType)valuesource).getType();
    	}
    	else if(valuesource instanceof MethodCall)
    	{
    		Class rettype = ((MethodCall)valuesource).getMethod().getReturnType();
    		if(rettype!=null)
    			ret = tmodel.getJavaType(rettype);
    	}
    	else if(valuesource instanceof FunctionCall)
    	{
    		Class rettype = ((FunctionCall)valuesource).getFunction().getReturnType();
    		if(rettype!=null)
    			ret = tmodel.getJavaType(rettype);
    	}
    	
    	return ret;
    }
    



    // $ANTLR start rhs
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:174:1: rhs[OAVTypeModel tmodel] returns [ICondition condition] : (c= ce[tmodel, vars] )+ EOF ;
    public final ICondition rhs(OAVTypeModel tmodel) throws RecognitionException {
        ICondition condition = null;

        ICondition c = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:175:2: ( (c= ce[tmodel, vars] )+ EOF )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:176:2: (c= ce[tmodel, vars] )+ EOF
            {
            
            		List conds = new ArrayList();
            		Map vars = new HashMap();
            		
            	
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:181:2: (c= ce[tmodel, vars] )+
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
            	    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:181:3: c= ce[tmodel, vars]
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:195:1: ce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : (tmp= andce[tmodel, vars] | tmp= notce[tmodel, vars] | tmp= testce[tmodel, vars] | tmp= collectce[tmodel, vars] | {...}?tmp= objectce[tmodel, vars] );
    public final ICondition ce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        ICondition tmp = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:196:2: (tmp= andce[tmodel, vars] | tmp= notce[tmodel, vars] | tmp= testce[tmodel, vars] | tmp= collectce[tmodel, vars] | {...}?tmp= objectce[tmodel, vars] )
            int alt2=5;
            alt2 = dfa2.predict(input);
            switch (alt2) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:196:4: tmp= andce[tmodel, vars]
                    {
                    pushFollow(FOLLOW_andce_in_ce89);
                    tmp=andce(tmodel,  vars);
                    _fsp--;

                    condition = tmp;

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:197:4: tmp= notce[tmodel, vars]
                    {
                    pushFollow(FOLLOW_notce_in_ce101);
                    tmp=notce(tmodel,  vars);
                    _fsp--;

                    condition = tmp;

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:198:4: tmp= testce[tmodel, vars]
                    {
                    pushFollow(FOLLOW_testce_in_ce113);
                    tmp=testce(tmodel,  vars);
                    _fsp--;

                    condition = tmp;

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:199:4: tmp= collectce[tmodel, vars]
                    {
                    pushFollow(FOLLOW_collectce_in_ce125);
                    tmp=collectce(tmodel,  vars);
                    _fsp--;

                    condition = tmp;

                    }
                    break;
                case 5 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:200:4: {...}?tmp= objectce[tmodel, vars]
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:203:1: andce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : '(' 'and' (c= ce[$tmodel, vars] )+ ')' ;
    public final ICondition andce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        ICondition c = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:204:2: ( '(' 'and' (c= ce[$tmodel, vars] )+ ')' )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:205:2: '(' 'and' (c= ce[$tmodel, vars] )+ ')'
            {
            
            		List conds = new ArrayList();
            	
            match(input,25,FOLLOW_25_in_andce163); 
            match(input,26,FOLLOW_26_in_andce165); 
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:208:12: (c= ce[$tmodel, vars] )+
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
            	    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:208:13: c= ce[$tmodel, vars]
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:218:1: notce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : '(' 'not' c= ce[$tmodel, vars] ')' ;
    public final ICondition notce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        ICondition c = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:219:2: ( '(' 'not' c= ce[$tmodel, vars] ')' )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:219:4: '(' 'not' c= ce[$tmodel, vars] ')'
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:225:1: testce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : '(' 'test' ( ({...}?call= functionCall[tmodel, vars] ) | call= operatorCall[tmodel, vars] ) ')' ;
    public final ICondition testce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        FunctionCall call = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:226:2: ( '(' 'test' ( ({...}?call= functionCall[tmodel, vars] ) | call= operatorCall[tmodel, vars] ) ')' )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:226:4: '(' 'test' ( ({...}?call= functionCall[tmodel, vars] ) | call= operatorCall[tmodel, vars] ) ')'
            {
            match(input,25,FOLLOW_25_in_testce227); 
            match(input,29,FOLLOW_29_in_testce229); 
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:226:15: ( ({...}?call= functionCall[tmodel, vars] ) | call= operatorCall[tmodel, vars] )
            int alt4=2;
            alt4 = dfa4.predict(input);
            switch (alt4) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:226:16: ({...}?call= functionCall[tmodel, vars] )
                    {
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:226:16: ({...}?call= functionCall[tmodel, vars] )
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:226:17: {...}?call= functionCall[tmodel, vars]
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
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:226:119: call= operatorCall[tmodel, vars]
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:232:1: collectce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )? '(' 'collect' (c= ce[$tmodel, vars] )+ (pc= predicateConstraint[$tmodel, null, vars] )? ')' ;
    public final ICondition collectce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        Variable mfv = null;

        ICondition c = null;

        IConstraint pc = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:233:2: ( (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )? '(' 'collect' (c= ce[$tmodel, vars] )+ (pc= predicateConstraint[$tmodel, null, vars] )? ')' )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:234:2: (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )? '(' 'collect' (c= ce[$tmodel, vars] )+ (pc= predicateConstraint[$tmodel, null, vars] )? ')'
            {
            
            		List conds = new ArrayList();
            	
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:237:2: (mfv= multiFieldVariable[null, vars] ( '<-' | '=' ) )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==34) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:237:3: mfv= multiFieldVariable[null, vars] ( '<-' | '=' )
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
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:238:16: (c= ce[$tmodel, vars] )+
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
            	    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:238:17: c= ce[$tmodel, vars]
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

            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:242:8: (pc= predicateConstraint[$tmodel, null, vars] )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==35) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:242:8: pc= predicateConstraint[$tmodel, null, vars]
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:256:1: objectce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )? '(' tn= typename (acs= attributeConstraint[tmodel, tmodel.getObjectType(tn), vars] | mcs= methodConstraint[tmodel, tmodel.getObjectType(tn), vars] | fcs= functionConstraint[tmodel, tmodel.getObjectType(tn), vars] )* ')' ;
    public final ICondition objectce(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        ICondition condition = null;

        Variable sfv = null;

        String tn = null;

        List acs = null;

        List mcs = null;

        List fcs = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:257:2: ( (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )? '(' tn= typename (acs= attributeConstraint[tmodel, tmodel.getObjectType(tn), vars] | mcs= methodConstraint[tmodel, tmodel.getObjectType(tn), vars] | fcs= functionConstraint[tmodel, tmodel.getObjectType(tn), vars] )* ')' )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:258:2: (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )? '(' tn= typename (acs= attributeConstraint[tmodel, tmodel.getObjectType(tn), vars] | mcs= methodConstraint[tmodel, tmodel.getObjectType(tn), vars] | fcs= functionConstraint[tmodel, tmodel.getObjectType(tn), vars] )* ')'
            {
            
            		List consts = new ArrayList();
            	
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:261:2: (sfv= singleFieldVariable[null, vars] ( '<-' | '=' ) )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==33) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:261:3: sfv= singleFieldVariable[null, vars] ( '<-' | '=' )
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

            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:265:2: (acs= attributeConstraint[tmodel, tmodel.getObjectType(tn), vars] | mcs= methodConstraint[tmodel, tmodel.getObjectType(tn), vars] | fcs= functionConstraint[tmodel, tmodel.getObjectType(tn), vars] )*
            loop9:
            do {
                int alt9=4;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==25) ) {
                    switch ( input.LA(2) ) {
                    case Identifiertoken:
                        {
                        int LA9_3 = input.LA(3);

                        if ( ((LA9_3>=CharacterLiteral && LA9_3<=DecimalLiteral)||(LA9_3>=33 && LA9_3<=35)||(LA9_3>=37 && LA9_3<=48)) ) {
                            alt9=1;
                        }
                        else if ( (LA9_3==25) ) {
                            alt9=2;
                        }


                        }
                        break;
                    case 29:
                        {
                        int LA9_4 = input.LA(3);

                        if ( ((LA9_4>=CharacterLiteral && LA9_4<=DecimalLiteral)||(LA9_4>=33 && LA9_4<=35)||(LA9_4>=37 && LA9_4<=48)) ) {
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

                        if ( ((LA9_5>=CharacterLiteral && LA9_5<=DecimalLiteral)||(LA9_5>=33 && LA9_5<=35)||(LA9_5>=37 && LA9_5<=48)) ) {
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

                        if ( (LA9_6==25) ) {
                            alt9=2;
                        }
                        else if ( ((LA9_6>=CharacterLiteral && LA9_6<=DecimalLiteral)||(LA9_6>=33 && LA9_6<=35)||(LA9_6>=37 && LA9_6<=48)) ) {
                            alt9=1;
                        }


                        }
                        break;
                    case 46:
                        {
                        int LA9_7 = input.LA(3);

                        if ( ((LA9_7>=CharacterLiteral && LA9_7<=DecimalLiteral)||(LA9_7>=33 && LA9_7<=35)||(LA9_7>=37 && LA9_7<=48)) ) {
                            alt9=1;
                        }
                        else if ( (LA9_7==25) ) {
                            alt9=2;
                        }


                        }
                        break;
                    case 47:
                        {
                        int LA9_8 = input.LA(3);

                        if ( (LA9_8==25) ) {
                            alt9=2;
                        }
                        else if ( ((LA9_8>=CharacterLiteral && LA9_8<=DecimalLiteral)||(LA9_8>=33 && LA9_8<=35)||(LA9_8>=37 && LA9_8<=48)) ) {
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
            	    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:265:3: acs= attributeConstraint[tmodel, tmodel.getObjectType(tn), vars]
            	    {
            	    pushFollow(FOLLOW_attributeConstraint_in_objectce375);
            	    acs=attributeConstraint(tmodel,  tmodel.getObjectType(tn),  vars);
            	    _fsp--;

            	    
            	    		consts.addAll(acs);
            	    	

            	    }
            	    break;
            	case 2 :
            	    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:269:4: mcs= methodConstraint[tmodel, tmodel.getObjectType(tn), vars]
            	    {
            	    pushFollow(FOLLOW_methodConstraint_in_objectce386);
            	    mcs=methodConstraint(tmodel,  tmodel.getObjectType(tn),  vars);
            	    _fsp--;

            	    
            	    		consts.addAll(mcs);
            	    	

            	    }
            	    break;
            	case 3 :
            	    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:273:4: fcs= functionConstraint[tmodel, tmodel.getObjectType(tn), vars]
            	    {
            	    pushFollow(FOLLOW_functionConstraint_in_objectce397);
            	    fcs=functionConstraint(tmodel,  tmodel.getObjectType(tn),  vars);
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
            			SConditions.adaptConditionType(sfv, tmodel.getObjectType(tn));
            		
            		OAVObjectType otype = tmodel.getObjectType(tn);
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:293:1: attributeConstraint[OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints] : '(' sn= slotname cs= constraint[tmodel, otype.getAttributeType(sn), vars] ')' ;
    public final List attributeConstraint(OAVTypeModel tmodel, OAVObjectType otype, Map vars) throws RecognitionException {
        List constraints = null;

        String sn = null;

        List cs = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:294:2: ( '(' sn= slotname cs= constraint[tmodel, otype.getAttributeType(sn), vars] ')' )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:294:4: '(' sn= slotname cs= constraint[tmodel, otype.getAttributeType(sn), vars] ')'
            {
            match(input,25,FOLLOW_25_in_attributeConstraint431); 
            pushFollow(FOLLOW_slotname_in_attributeConstraint435);
            sn=slotname();
            _fsp--;

            pushFollow(FOLLOW_constraint_in_attributeConstraint439);
            cs=constraint(tmodel,  otype.getAttributeType(sn),  vars);
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:300:1: methodConstraint[OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints] : '(' mn= methodname '(' (exp= parameter[tmodel, vars] )* ')' cs= constraint[tmodel, SConditions.createMethodCall(otype, mn, exps), vars] ')' ;
    public final List methodConstraint(OAVTypeModel tmodel, OAVObjectType otype, Map vars) throws RecognitionException {
        List constraints = null;

        String mn = null;

        Object exp = null;

        List cs = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:301:2: ( '(' mn= methodname '(' (exp= parameter[tmodel, vars] )* ')' cs= constraint[tmodel, SConditions.createMethodCall(otype, mn, exps), vars] ')' )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:302:2: '(' mn= methodname '(' (exp= parameter[tmodel, vars] )* ')' cs= constraint[tmodel, SConditions.createMethodCall(otype, mn, exps), vars] ')'
            {
            
            		List exps = new ArrayList();
            	
            match(input,25,FOLLOW_25_in_methodConstraint469); 
            pushFollow(FOLLOW_methodname_in_methodConstraint473);
            mn=methodname();
            _fsp--;

            match(input,25,FOLLOW_25_in_methodConstraint475); 
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:305:23: (exp= parameter[tmodel, vars] )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0>=CharacterLiteral && LA10_0<=DecimalLiteral)||LA10_0==25||(LA10_0>=33 && LA10_0<=34)||(LA10_0>=37 && LA10_0<=39)) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:305:24: exp= parameter[tmodel, vars]
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:315:1: functionConstraint[OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints] : '(' fc= functionCall[tmodel, vars] cs= constraint[tmodel, fc, vars] ')' ;
    public final List functionConstraint(OAVTypeModel tmodel, OAVObjectType otype, Map vars) throws RecognitionException {
        List constraints = null;

        FunctionCall fc = null;

        List cs = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:316:2: ( '(' fc= functionCall[tmodel, vars] cs= constraint[tmodel, fc, vars] ')' )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:317:2: '(' fc= functionCall[tmodel, vars] cs= constraint[tmodel, fc, vars] ')'
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:326:1: constraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [List constraints] : ( '?' | '$?' | last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )* );
    public final List constraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        List constraints = null;

        Token ConstraintOperator1=null;
        IConstraint last = null;

        IConstraint next = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:327:2: ( '?' | '$?' | last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )* )
            int alt12=3;
            switch ( input.LA(1) ) {
            case 33:
                {
                int LA12_1 = input.LA(2);

                if ( (LA12_1==Identifiertoken||LA12_1==26||(LA12_1>=28 && LA12_1<=29)||(LA12_1>=46 && LA12_1<=47)) ) {
                    alt12=3;
                }
                else if ( (LA12_1==27) ) {
                    alt12=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("326:1: constraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [List constraints] : ( '?' | '$?' | last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )* );", 12, 1, input);

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
                else if ( (LA12_2==Identifiertoken||LA12_2==26||(LA12_2>=28 && LA12_2<=29)||(LA12_2>=46 && LA12_2<=47)) ) {
                    alt12=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("326:1: constraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [List constraints] : ( '?' | '$?' | last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )* );", 12, 2, input);

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
            case 37:
            case 38:
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
                {
                alt12=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("326:1: constraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [List constraints] : ( '?' | '$?' | last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )* );", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:331:2: '?'
                    {
                    match(input,33,FOLLOW_33_in_constraint565); 

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:332:4: '$?'
                    {
                    match(input,34,FOLLOW_34_in_constraint571); 

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:333:4: last= singleConstraint[tmodel, valuesource, vars] ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )*
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
                    	
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:343:2: ( ConstraintOperator next= singleConstraint[tmodel, valuesource, vars] )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==ConstraintOperator) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:343:3: ConstraintOperator next= singleConstraint[tmodel, valuesource, vars]
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );
    public final IConstraint singleConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        IConstraint tmp = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:384:2: (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] )
            int alt13=5;
            switch ( input.LA(1) ) {
            case 48:
                {
                switch ( input.LA(2) ) {
                case 33:
                    {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA13_15 = input.LA(4);

                        if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA13_25 = input.LA(4);

                        if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

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
                case 37:
                case 38:
                case 39:
                    {
                    alt13=1;
                    }
                    break;
                case 25:
                    {
                    alt13=5;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 1, input);

                    throw nvae;
                }

                }
                break;
            case 40:
                {
                switch ( input.LA(2) ) {
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 37:
                case 38:
                case 39:
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

                        if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA13_25 = input.LA(4);

                        if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 2, input);

                    throw nvae;
                }

                }
                break;
            case 41:
                {
                switch ( input.LA(2) ) {
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 37:
                case 38:
                case 39:
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

                        if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA13_25 = input.LA(4);

                        if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 3, input);

                    throw nvae;
                }

                }
                break;
            case 42:
                {
                switch ( input.LA(2) ) {
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 37:
                case 38:
                case 39:
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

                        if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA13_25 = input.LA(4);

                        if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 4, input);

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

                        if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA13_25 = input.LA(4);

                        if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

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
                case 37:
                case 38:
                case 39:
                    {
                    alt13=1;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 5, input);

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

                        if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA13_25 = input.LA(4);

                        if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

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
                case 37:
                case 38:
                case 39:
                    {
                    alt13=1;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 6, input);

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
                case 37:
                case 38:
                case 39:
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

                        if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA13_25 = input.LA(4);

                        if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 7, input);

                    throw nvae;
                }

                }
                break;
            case 46:
                {
                switch ( input.LA(2) ) {
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 37:
                case 38:
                case 39:
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

                        if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA13_25 = input.LA(4);

                        if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 8, input);

                    throw nvae;
                }

                }
                break;
            case 47:
                {
                switch ( input.LA(2) ) {
                case CharacterLiteral:
                case StringLiteral:
                case BooleanLiteral:
                case FloatingPointLiteral:
                case HexLiteral:
                case OctalLiteral:
                case DecimalLiteral:
                case 37:
                case 38:
                case 39:
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

                        if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 29:
                        {
                        int LA13_16 = input.LA(4);

                        if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA13_17 = input.LA(4);

                        if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA13_18 = input.LA(4);

                        if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA13_25 = input.LA(4);

                        if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                            alt13=2;
                        }
                        else if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                            alt13=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

                        throw nvae;
                    }

                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 9, input);

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
            case 37:
            case 38:
            case 39:
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

                    if ( (LA13_15==ConstraintOperator||LA13_15==27) ) {
                        alt13=2;
                    }
                    else if ( ((LA13_15>=33 && LA13_15<=34)) ) {
                        alt13=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 15, input);

                        throw nvae;
                    }
                    }
                    break;
                case 29:
                    {
                    int LA13_16 = input.LA(3);

                    if ( (LA13_16==ConstraintOperator||LA13_16==27) ) {
                        alt13=2;
                    }
                    else if ( ((LA13_16>=33 && LA13_16<=34)) ) {
                        alt13=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 16, input);

                        throw nvae;
                    }
                    }
                    break;
                case 28:
                    {
                    int LA13_17 = input.LA(3);

                    if ( (LA13_17==ConstraintOperator||LA13_17==27) ) {
                        alt13=2;
                    }
                    else if ( ((LA13_17>=33 && LA13_17<=34)) ) {
                        alt13=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 17, input);

                        throw nvae;
                    }
                    }
                    break;
                case 26:
                    {
                    int LA13_18 = input.LA(3);

                    if ( (LA13_18==ConstraintOperator||LA13_18==27) ) {
                        alt13=2;
                    }
                    else if ( ((LA13_18>=33 && LA13_18<=34)) ) {
                        alt13=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 18, input);

                        throw nvae;
                    }
                    }
                    break;
                case 46:
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
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 19, input);

                        throw nvae;
                    }
                    }
                    break;
                case 47:
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
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 20, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 11, input);

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
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 21, input);

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
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 22, input);

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
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 23, input);

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
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 24, input);

                        throw nvae;
                    }
                    }
                    break;
                case 46:
                    {
                    int LA13_25 = input.LA(3);

                    if ( (LA13_25==ConstraintOperator||LA13_25==27) ) {
                        alt13=2;
                    }
                    else if ( ((LA13_25>=33 && LA13_25<=34)) ) {
                        alt13=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 25, input);

                        throw nvae;
                    }
                    }
                    break;
                case 47:
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
                            new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 26, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 12, input);

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
                    new NoViableAltException("383:1: singleConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (tmp= literalConstraint[valuesource] | tmp= boundConstraint[tmodel, valuesource, vars] | tmp= multiBoundConstraint[tmodel, valuesource, vars] | tmp= predicateConstraint[tmodel, valuesource, vars] | tmp= returnValueConstraint[tmodel, valuesource, vars] );", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:384:4: tmp= literalConstraint[valuesource]
                    {
                    pushFollow(FOLLOW_literalConstraint_in_singleConstraint624);
                    tmp=literalConstraint(valuesource);
                    _fsp--;

                    constraint = tmp;

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:385:4: tmp= boundConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_boundConstraint_in_singleConstraint635);
                    tmp=boundConstraint(tmodel,  valuesource,  vars);
                    _fsp--;

                    constraint = tmp;

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:386:4: tmp= multiBoundConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_multiBoundConstraint_in_singleConstraint645);
                    tmp=multiBoundConstraint(tmodel,  valuesource,  vars);
                    _fsp--;

                    constraint = tmp;

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:387:4: tmp= predicateConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_predicateConstraint_in_singleConstraint655);
                    tmp=predicateConstraint(tmodel,  valuesource,  vars);
                    _fsp--;

                    constraint = tmp;

                    }
                    break;
                case 5 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:388:4: tmp= returnValueConstraint[tmodel, valuesource, vars]
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:391:1: literalConstraint[Object valuesource] returns [IConstraint constraint] : (op= operator )? val= constant ;
    public final IConstraint literalConstraint(Object valuesource) throws RecognitionException {
        IConstraint constraint = null;

        IOperator op = null;

        Object val = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:392:2: ( (op= operator )? val= constant )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:392:4: (op= operator )? val= constant
            {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:392:6: (op= operator )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( ((LA14_0>=40 && LA14_0<=48)) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:392:6: op= operator
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );
    public final IConstraint someBoundConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        IConstraint bc = null;

        IConstraint mbc = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:402:2: (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] )
            int alt15=2;
            switch ( input.LA(1) ) {
            case 48:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_16 = input.LA(4);

                        if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_1==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_22 = input.LA(4);

                        if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
                        {
                        int LA15_23 = input.LA(4);

                        if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 1, input);

                    throw nvae;
                }
                }
                break;
            case 40:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_16 = input.LA(4);

                        if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_2==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_22 = input.LA(4);

                        if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
                        {
                        int LA15_23 = input.LA(4);

                        if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 2, input);

                    throw nvae;
                }
                }
                break;
            case 41:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_16 = input.LA(4);

                        if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_3==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_22 = input.LA(4);

                        if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
                        {
                        int LA15_23 = input.LA(4);

                        if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 3, input);

                    throw nvae;
                }
                }
                break;
            case 42:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_16 = input.LA(4);

                        if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_4==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_22 = input.LA(4);

                        if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
                        {
                        int LA15_23 = input.LA(4);

                        if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 4, input);

                    throw nvae;
                }
                }
                break;
            case 43:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_16 = input.LA(4);

                        if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_5==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_22 = input.LA(4);

                        if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
                        {
                        int LA15_23 = input.LA(4);

                        if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 5, input);

                    throw nvae;
                }
                }
                break;
            case 44:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_16 = input.LA(4);

                        if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_6==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_22 = input.LA(4);

                        if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
                        {
                        int LA15_23 = input.LA(4);

                        if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 6, input);

                    throw nvae;
                }
                }
                break;
            case 45:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_16 = input.LA(4);

                        if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_7==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_22 = input.LA(4);

                        if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
                        {
                        int LA15_23 = input.LA(4);

                        if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 7, input);

                    throw nvae;
                }
                }
                break;
            case 46:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_16 = input.LA(4);

                        if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_8==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_22 = input.LA(4);

                        if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
                        {
                        int LA15_23 = input.LA(4);

                        if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 8, input);

                    throw nvae;
                }
                }
                break;
            case 47:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_14 = input.LA(4);

                        if ( (LA15_14==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_16 = input.LA(4);

                        if ( (LA15_16==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

                        throw nvae;
                    }

                }
                else if ( (LA15_9==34) ) {
                    switch ( input.LA(3) ) {
                    case Identifiertoken:
                        {
                        int LA15_18 = input.LA(4);

                        if ( (LA15_18==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

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
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 28:
                        {
                        int LA15_20 = input.LA(4);

                        if ( (LA15_20==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 26:
                        {
                        int LA15_21 = input.LA(4);

                        if ( (LA15_21==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 46:
                        {
                        int LA15_22 = input.LA(4);

                        if ( (LA15_22==EOF) ) {
                            alt15=1;
                        }
                        else if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                            alt15=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 47:
                        {
                        int LA15_23 = input.LA(4);

                        if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                            alt15=2;
                        }
                        else if ( (LA15_23==EOF) ) {
                            alt15=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 9, input);

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
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 12, input);

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
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 13, input);

                        throw nvae;
                    }
                    }
                    break;
                case 28:
                    {
                    int LA15_14 = input.LA(3);

                    if ( (LA15_14==EOF) ) {
                        alt15=1;
                    }
                    else if ( ((LA15_14>=33 && LA15_14<=34)) ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 14, input);

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
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 15, input);

                        throw nvae;
                    }
                    }
                    break;
                case 46:
                    {
                    int LA15_16 = input.LA(3);

                    if ( (LA15_16==EOF) ) {
                        alt15=1;
                    }
                    else if ( ((LA15_16>=33 && LA15_16<=34)) ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 16, input);

                        throw nvae;
                    }
                    }
                    break;
                case 47:
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
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 17, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 10, input);

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

                    if ( (LA15_18==EOF) ) {
                        alt15=1;
                    }
                    else if ( ((LA15_18>=33 && LA15_18<=34)) ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 18, input);

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
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 19, input);

                        throw nvae;
                    }
                    }
                    break;
                case 28:
                    {
                    int LA15_20 = input.LA(3);

                    if ( (LA15_20==EOF) ) {
                        alt15=1;
                    }
                    else if ( ((LA15_20>=33 && LA15_20<=34)) ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 20, input);

                        throw nvae;
                    }
                    }
                    break;
                case 26:
                    {
                    int LA15_21 = input.LA(3);

                    if ( (LA15_21==EOF) ) {
                        alt15=1;
                    }
                    else if ( ((LA15_21>=33 && LA15_21<=34)) ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 21, input);

                        throw nvae;
                    }
                    }
                    break;
                case 46:
                    {
                    int LA15_22 = input.LA(3);

                    if ( (LA15_22==EOF) ) {
                        alt15=1;
                    }
                    else if ( ((LA15_22>=33 && LA15_22<=34)) ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 22, input);

                        throw nvae;
                    }
                    }
                    break;
                case 47:
                    {
                    int LA15_23 = input.LA(3);

                    if ( ((LA15_23>=33 && LA15_23<=34)) ) {
                        alt15=2;
                    }
                    else if ( (LA15_23==EOF) ) {
                        alt15=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 23, input);

                        throw nvae;
                    }
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 11, input);

                    throw nvae;
                }

                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("401:1: someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (bc= boundConstraint[tmodel, valuesource, vars] | mbc= multiBoundConstraint[tmodel, valuesource, vars] );", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:402:4: bc= boundConstraint[tmodel, valuesource, vars]
                    {
                    pushFollow(FOLLOW_boundConstraint_in_someBoundConstraint715);
                    bc=boundConstraint(tmodel,  valuesource,  vars);
                    _fsp--;

                    
                    		constraint = bc;
                    	

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:406:4: mbc= multiBoundConstraint[tmodel, valuesource, vars]
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:412:1: boundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] ;
    public final IConstraint boundConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        IOperator op = null;

        Variable var = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:413:2: ( (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:413:4: (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars]
            {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:413:6: (op= operator )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( ((LA16_0>=40 && LA16_0<=48)) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:413:6: op= operator
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:422:1: multiBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+ ;
    public final IConstraint multiBoundConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        IOperator op = null;

        Variable var = null;

        Variable varn = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:423:2: ( (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+ )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:424:2: (op= operator )? var= variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars] (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+
            {
            
            		List vs = new ArrayList();
            	
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:427:4: (op= operator )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( ((LA17_0>=40 && LA17_0<=48)) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:427:4: op= operator
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
            	
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:431:2: (varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars] )+
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
            	    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:431:3: varn= variable[SConditions.getValueSourceType(tmodel, valuesource), vars]
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:444:1: predicateConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : ':' ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] ) ;
    public final IConstraint predicateConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        FunctionCall fc = null;

        FunctionCall oc = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:445:2: ( ':' ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] ) )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:445:4: ':' ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )
            {
            match(input,35,FOLLOW_35_in_predicateConstraint827); 
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:445:8: ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )
            int alt19=2;
            alt19 = dfa19.predict(input);
            switch (alt19) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:445:9: {...}?fc= functionCall[tmodel, vars]
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
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:446:4: oc= operatorCall[tmodel, vars]
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:451:1: returnValueConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint] : equalOperator ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] ) ;
    public final IConstraint returnValueConstraint(OAVTypeModel tmodel, Object valuesource, Map vars) throws RecognitionException {
        IConstraint constraint = null;

        FunctionCall fc = null;

        FunctionCall oc = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:452:2: ( equalOperator ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] ) )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:452:4: equalOperator ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )
            {
            pushFollow(FOLLOW_equalOperator_in_returnValueConstraint869);
            equalOperator();
            _fsp--;

            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:452:18: ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )
            int alt20=2;
            alt20 = dfa20.predict(input);
            switch (alt20) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:452:19: {...}?fc= functionCall[tmodel, vars]
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
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:453:4: oc= operatorCall[tmodel, vars]
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:457:1: functionCall[OAVTypeModel tmodel, Map vars] returns [FunctionCall fc] : '(' fn= functionName (exp= parameter[tmodel, vars] )* ')' ;
    public final FunctionCall functionCall(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        FunctionCall fc = null;

        String fn = null;

        Object exp = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:458:2: ( '(' fn= functionName (exp= parameter[tmodel, vars] )* ')' )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:459:2: '(' fn= functionName (exp= parameter[tmodel, vars] )* ')'
            {
            
            		List exps = new ArrayList();
            	
            match(input,25,FOLLOW_25_in_functionCall917); 
            pushFollow(FOLLOW_functionName_in_functionCall921);
            fn=functionName();
            _fsp--;

            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:462:22: (exp= parameter[tmodel, vars] )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( ((LA21_0>=CharacterLiteral && LA21_0<=DecimalLiteral)||LA21_0==25||(LA21_0>=33 && LA21_0<=34)||(LA21_0>=37 && LA21_0<=39)) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:462:23: exp= parameter[tmodel, vars]
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
            
            		IFunction func = null;
                        	if("jadex.rules.rulesystem.rules.functions.MethodCallFunction".equals(fn))
                        	{
                        		String clazzname = (String)exps.remove(0);
                        		String methodname = (String)exps.remove(0);
                        				
                        		Class clazz = SReflect.classForName0(clazzname, tmodel.getClassLoader());
                        		Method[] methods	= SReflect.getMethods(clazz, methodname);
                        		Method	method = null;
                        	
                        		// Find one matching regardless of param types (hack???). 
                        		// First param is object on which function will be called. 
                        		for(int i=0; i<methods.length && method==null; i++)
                        		{
                        			if(methods[i].getParameterTypes().length==exps.size()-1)
                        			{
                        				method	= methods[i];
                        			}
                        		}
                        				
                        		if(method!=null)	
                        			func = new MethodCallFunction(method);
                        	}
                        	else
                        	{
                        		try
                        		{
                        			func = (IFunction)SReflect.classForName0(fn, tmodel.getClassLoader()).newInstance();
                        		}
                        		catch(Exception e)
                        		{
                        			// nop.
                        		}
                        	}
                        			
                        	if(func==null)
                        		throw new RuntimeException("Function not found: "+fn);
                        			
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:503:1: operatorCall[OAVTypeModel tmodel, Map vars] returns [FunctionCall fc] : '(' (op= operator )? exp1= parameter[tmodel, vars] exp2= parameter[tmodel, vars] ')' ;
    public final FunctionCall operatorCall(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        FunctionCall fc = null;

        IOperator op = null;

        Object exp1 = null;

        Object exp2 = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:504:2: ( '(' (op= operator )? exp1= parameter[tmodel, vars] exp2= parameter[tmodel, vars] ')' )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:504:4: '(' (op= operator )? exp1= parameter[tmodel, vars] exp2= parameter[tmodel, vars] ')'
            {
            match(input,25,FOLLOW_25_in_operatorCall957); 
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:504:11: (op= operator )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( ((LA22_0>=40 && LA22_0<=48)) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:504:11: op= operator
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:511:1: parameter[OAVTypeModel tmodel, Map vars] returns [Object val] : (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] );
    public final Object parameter(OAVTypeModel tmodel, Map vars) throws RecognitionException {
        Object val = null;

        Object tmp1 = null;

        Variable tmp2 = null;

        FunctionCall tmp3 = null;

        FunctionCall tmp4 = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:512:2: (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] )
            int alt23=4;
            switch ( input.LA(1) ) {
            case CharacterLiteral:
            case StringLiteral:
            case BooleanLiteral:
            case FloatingPointLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 37:
            case 38:
            case 39:
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
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 48:
                    {
                    alt23=4;
                    }
                    break;
                case 46:
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
                            new NoViableAltException("511:1: parameter[OAVTypeModel tmodel, Map vars] returns [Object val] : (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] );", 23, 5, input);

                        throw nvae;
                    }
                    }
                    break;
                case 47:
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
                            new NoViableAltException("511:1: parameter[OAVTypeModel tmodel, Map vars] returns [Object val] : (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] );", 23, 6, input);

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
                        new NoViableAltException("511:1: parameter[OAVTypeModel tmodel, Map vars] returns [Object val] : (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] );", 23, 3, input);

                    throw nvae;
                }

                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("511:1: parameter[OAVTypeModel tmodel, Map vars] returns [Object val] : (tmp1= constant | tmp2= variable[null, vars] | {...}?tmp3= functionCall[tmodel, vars] | tmp4= operatorCall[tmodel, vars] );", 23, 0, input);

                throw nvae;
            }

            switch (alt23) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:512:4: tmp1= constant
                    {
                    pushFollow(FOLLOW_constant_in_parameter998);
                    tmp1=constant();
                    _fsp--;

                    val = tmp1;

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:513:4: tmp2= variable[null, vars]
                    {
                    pushFollow(FOLLOW_variable_in_parameter1008);
                    tmp2=variable(null,  vars);
                    _fsp--;

                    val = tmp2;

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:514:4: {...}?tmp3= functionCall[tmodel, vars]
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
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:515:4: tmp4= operatorCall[tmodel, vars]
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:518:1: constant returns [Object val] : tmp= literal ;
    public final Object constant() throws RecognitionException {
        Object val = null;

        Object tmp = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:519:2: (tmp= literal )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:519:4: tmp= literal
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:522:1: variable[OAVObjectType type, Map vars] returns [Variable var] : (tmp= singleFieldVariable[type, vars] | tmp= multiFieldVariable[type, vars] );
    public final Variable variable(OAVObjectType type, Map vars) throws RecognitionException {
        Variable var = null;

        Variable tmp = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:523:2: (tmp= singleFieldVariable[type, vars] | tmp= multiFieldVariable[type, vars] )
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
                    new NoViableAltException("522:1: variable[OAVObjectType type, Map vars] returns [Variable var] : (tmp= singleFieldVariable[type, vars] | tmp= multiFieldVariable[type, vars] );", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:523:4: tmp= singleFieldVariable[type, vars]
                    {
                    pushFollow(FOLLOW_singleFieldVariable_in_variable1074);
                    tmp=singleFieldVariable(type,  vars);
                    _fsp--;

                    var = tmp;

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:524:4: tmp= multiFieldVariable[type, vars]
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:527:1: singleFieldVariable[OAVObjectType type, Map vars] returns [Variable var] : '?' id= identifier ;
    public final Variable singleFieldVariable(OAVObjectType type, Map vars) throws RecognitionException {
        Variable var = null;

        Token id = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:528:2: ( '?' id= identifier )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:528:4: '?' id= identifier
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:544:1: multiFieldVariable[OAVObjectType type, Map vars] returns [Variable var] : '$?' id= identifier ;
    public final Variable multiFieldVariable(OAVObjectType type, Map vars) throws RecognitionException {
        Variable var = null;

        Token id = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:545:2: ( '$?' id= identifier )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:545:4: '$?' id= identifier
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:561:1: typename returns [String id] : tmp= identifier ( '.' tmp= identifier )* ;
    public final String typename() throws RecognitionException {
        String id = null;

        Token tmp = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:562:2: (tmp= identifier ( '.' tmp= identifier )* )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:563:2: tmp= identifier ( '.' tmp= identifier )*
            {
            
            		StringBuffer buf = new StringBuffer();
            	
            pushFollow(FOLLOW_identifier_in_typename1160);
            tmp=identifier();
            _fsp--;

            
            		buf.append(tmp.getText());
            	
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:570:2: ( '.' tmp= identifier )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==36) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:570:3: '.' tmp= identifier
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:580:1: slotname returns [String id] : tmp= identifier ;
    public final String slotname() throws RecognitionException {
        String id = null;

        Token tmp = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:581:2: (tmp= identifier )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:581:4: tmp= identifier
            {
            pushFollow(FOLLOW_identifier_in_slotname1199);
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
    // $ANTLR end slotname


    // $ANTLR start methodname
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:584:1: methodname returns [String id] : tmp= identifier ;
    public final String methodname() throws RecognitionException {
        String id = null;

        Token tmp = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:585:2: (tmp= identifier )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:585:4: tmp= identifier
            {
            pushFollow(FOLLOW_identifier_in_methodname1219);
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:588:1: functionName returns [String id] : tmp= typename ;
    public final String functionName() throws RecognitionException {
        String id = null;

        String tmp = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:589:2: (tmp= typename )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:589:4: tmp= typename
            {
            pushFollow(FOLLOW_typename_in_functionName1238);
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:592:1: literal returns [Object val] : (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );
    public final Object literal() throws RecognitionException {
        Object val = null;

        Token CharacterLiteral2=null;
        Token StringLiteral3=null;
        Token BooleanLiteral4=null;
        Object lit = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:593:2: (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' )
            int alt26=6;
            switch ( input.LA(1) ) {
            case 38:
            case 39:
                {
                int LA26_1 = input.LA(2);

                if ( (LA26_1==FloatingPointLiteral) ) {
                    alt26=1;
                }
                else if ( ((LA26_1>=HexLiteral && LA26_1<=DecimalLiteral)) ) {
                    alt26=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("592:1: literal returns [Object val] : (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );", 26, 1, input);

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
            case 37:
                {
                alt26=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("592:1: literal returns [Object val] : (lit= floatingPointLiteral | lit= integerLiteral | CharacterLiteral | StringLiteral | BooleanLiteral | 'null' );", 26, 0, input);

                throw nvae;
            }

            switch (alt26) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:593:4: lit= floatingPointLiteral
                    {
                    pushFollow(FOLLOW_floatingPointLiteral_in_literal1258);
                    lit=floatingPointLiteral();
                    _fsp--;

                    val = lit;

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:594:4: lit= integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal1267);
                    lit=integerLiteral();
                    _fsp--;

                    val = lit;

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:595:4: CharacterLiteral
                    {
                    CharacterLiteral2=(Token)input.LT(1);
                    match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal1274); 
                    val = new Character(CharacterLiteral2.getText().charAt(0));

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:596:4: StringLiteral
                    {
                    StringLiteral3=(Token)input.LT(1);
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_literal1281); 
                    val = StringLiteral3.getText().substring(1, StringLiteral3.getText().length()-1);

                    }
                    break;
                case 5 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:597:4: BooleanLiteral
                    {
                    BooleanLiteral4=(Token)input.LT(1);
                    match(input,BooleanLiteral,FOLLOW_BooleanLiteral_in_literal1288); 
                    val = BooleanLiteral4.getText().equals("true")? Boolean.TRUE: Boolean.FALSE;

                    }
                    break;
                case 6 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:598:4: 'null'
                    {
                    match(input,37,FOLLOW_37_in_literal1295); 
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:601:1: floatingPointLiteral returns [Object val] : (sign= ( '+' | '-' ) )? FloatingPointLiteral ;
    public final Object floatingPointLiteral() throws RecognitionException {
        Object val = null;

        Token sign=null;
        Token FloatingPointLiteral5=null;

        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:602:2: ( (sign= ( '+' | '-' ) )? FloatingPointLiteral )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:602:4: (sign= ( '+' | '-' ) )? FloatingPointLiteral
            {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:602:8: (sign= ( '+' | '-' ) )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( ((LA27_0>=38 && LA27_0<=39)) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:602:8: sign= ( '+' | '-' )
                    {
                    sign=(Token)input.LT(1);
                    if ( (input.LA(1)>=38 && input.LA(1)<=39) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_floatingPointLiteral1314);    throw mse;
                    }


                    }
                    break;

            }

            FloatingPointLiteral5=(Token)input.LT(1);
            match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1321); 
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:605:1: integerLiteral returns [Object val] : (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral ) ;
    public final Object integerLiteral() throws RecognitionException {
        Object val = null;

        Token sign=null;
        Token HexLiteral6=null;
        Token OctalLiteral7=null;
        Token DecimalLiteral8=null;

        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:606:2: ( (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral ) )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:606:4: (sign= ( '+' | '-' ) )? ( HexLiteral | OctalLiteral | DecimalLiteral )
            {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:606:8: (sign= ( '+' | '-' ) )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( ((LA28_0>=38 && LA28_0<=39)) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:606:8: sign= ( '+' | '-' )
                    {
                    sign=(Token)input.LT(1);
                    if ( (input.LA(1)>=38 && input.LA(1)<=39) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_integerLiteral1341);    throw mse;
                    }


                    }
                    break;

            }

            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:606:20: ( HexLiteral | OctalLiteral | DecimalLiteral )
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
                    new NoViableAltException("606:20: ( HexLiteral | OctalLiteral | DecimalLiteral )", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:606:21: HexLiteral
                    {
                    HexLiteral6=(Token)input.LT(1);
                    match(input,HexLiteral,FOLLOW_HexLiteral_in_integerLiteral1349); 
                    val = sign!=null && "-".equals(sign.getText())? new Integer("-"+HexLiteral6.getText()): new Integer(HexLiteral6.getText());

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:607:4: OctalLiteral
                    {
                    OctalLiteral7=(Token)input.LT(1);
                    match(input,OctalLiteral,FOLLOW_OctalLiteral_in_integerLiteral1356); 
                    val = sign!=null && "-".equals(sign.getText())? new Integer("-"+OctalLiteral7.getText()): new Integer(OctalLiteral7.getText());

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:608:4: DecimalLiteral
                    {
                    DecimalLiteral8=(Token)input.LT(1);
                    match(input,DecimalLiteral,FOLLOW_DecimalLiteral_in_integerLiteral1363); 
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:611:1: operator returns [IOperator operator] : (tmp= equalOperator | '!=' | '~' | '>' | '<' | '>=' | '<=' | 'contains' | 'excludes' );
    public final IOperator operator() throws RecognitionException {
        IOperator operator = null;

        IOperator tmp = null;


        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:612:2: (tmp= equalOperator | '!=' | '~' | '>' | '<' | '>=' | '<=' | 'contains' | 'excludes' )
            int alt30=9;
            switch ( input.LA(1) ) {
            case 48:
                {
                alt30=1;
                }
                break;
            case 40:
                {
                alt30=2;
                }
                break;
            case 41:
                {
                alt30=3;
                }
                break;
            case 42:
                {
                alt30=4;
                }
                break;
            case 43:
                {
                alt30=5;
                }
                break;
            case 44:
                {
                alt30=6;
                }
                break;
            case 45:
                {
                alt30=7;
                }
                break;
            case 46:
                {
                alt30=8;
                }
                break;
            case 47:
                {
                alt30=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("611:1: operator returns [IOperator operator] : (tmp= equalOperator | '!=' | '~' | '>' | '<' | '>=' | '<=' | 'contains' | 'excludes' );", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:612:4: tmp= equalOperator
                    {
                    pushFollow(FOLLOW_equalOperator_in_operator1383);
                    tmp=equalOperator();
                    _fsp--;

                    operator = tmp;

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:613:4: '!='
                    {
                    match(input,40,FOLLOW_40_in_operator1391); 
                    operator = IOperator.NOTEQUAL;

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:614:4: '~'
                    {
                    match(input,41,FOLLOW_41_in_operator1398); 
                    operator = IOperator.NOTEQUAL;

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:615:4: '>'
                    {
                    match(input,42,FOLLOW_42_in_operator1405); 
                    operator = IOperator.GREATER;

                    }
                    break;
                case 5 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:616:4: '<'
                    {
                    match(input,43,FOLLOW_43_in_operator1412); 
                    operator = IOperator.LESS;

                    }
                    break;
                case 6 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:617:4: '>='
                    {
                    match(input,44,FOLLOW_44_in_operator1419); 
                    operator = IOperator.GREATEROREQUAL;

                    }
                    break;
                case 7 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:618:4: '<='
                    {
                    match(input,45,FOLLOW_45_in_operator1426); 
                    operator = IOperator.LESSOREQUAL;

                    }
                    break;
                case 8 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:619:4: 'contains'
                    {
                    match(input,46,FOLLOW_46_in_operator1433); 
                    operator = IOperator.CONTAINS;

                    }
                    break;
                case 9 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:620:4: 'excludes'
                    {
                    match(input,47,FOLLOW_47_in_operator1440); 
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:623:1: equalOperator returns [IOperator operator] : '==' ;
    public final IOperator equalOperator() throws RecognitionException {
        IOperator operator = null;

        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:624:2: ( '==' )
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:624:4: '=='
            {
            match(input,48,FOLLOW_48_in_equalOperator1457); 
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
    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:627:1: identifier returns [Token identifier] : (tmp= Identifiertoken | tmp= 'test' | tmp= 'not' | tmp= 'and' | tmp= 'contains' | tmp= 'excludes' );
    public final Token identifier() throws RecognitionException {
        Token identifier = null;

        Token tmp=null;

        try {
            // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:628:2: (tmp= Identifiertoken | tmp= 'test' | tmp= 'not' | tmp= 'and' | tmp= 'contains' | tmp= 'excludes' )
            int alt31=6;
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
            case 46:
                {
                alt31=5;
                }
                break;
            case 47:
                {
                alt31=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("627:1: identifier returns [Token identifier] : (tmp= Identifiertoken | tmp= 'test' | tmp= 'not' | tmp= 'and' | tmp= 'contains' | tmp= 'excludes' );", 31, 0, input);

                throw nvae;
            }

            switch (alt31) {
                case 1 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:628:4: tmp= Identifiertoken
                    {
                    tmp=(Token)input.LT(1);
                    match(input,Identifiertoken,FOLLOW_Identifiertoken_in_identifier1477); 
                    identifier = tmp;

                    }
                    break;
                case 2 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:629:4: tmp= 'test'
                    {
                    tmp=(Token)input.LT(1);
                    match(input,29,FOLLOW_29_in_identifier1486); 
                    identifier = tmp;

                    }
                    break;
                case 3 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:630:4: tmp= 'not'
                    {
                    tmp=(Token)input.LT(1);
                    match(input,28,FOLLOW_28_in_identifier1495); 
                    identifier = tmp;

                    }
                    break;
                case 4 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:631:4: tmp= 'and'
                    {
                    tmp=(Token)input.LT(1);
                    match(input,26,FOLLOW_26_in_identifier1504); 
                    identifier = tmp;

                    }
                    break;
                case 5 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:632:4: tmp= 'contains'
                    {
                    tmp=(Token)input.LT(1);
                    match(input,46,FOLLOW_46_in_identifier1513); 
                    identifier = tmp;

                    }
                    break;
                case 6 :
                    // C:\\projects\\jadex\\jadex_v2\\microkernel\\src\\jadex\\rules\\parser\\conditions\\ClipsJadex.g:633:4: tmp= 'excludes'
                    {
                    tmp=(Token)input.LT(1);
                    match(input,47,FOLLOW_47_in_identifier1522); 
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
        "\u00b4\uffff";
    static final String DFA2_eofS =
        "\u00b4\uffff";
    static final String DFA2_minS =
        "\1\31\1\14\2\uffff\2\31\1\0\1\5\1\14\3\uffff\2\5\1\0\4\5\1\0\1\5"+
        "\1\0\3\5\1\0\2\14\1\10\10\4\1\0\1\10\10\4\2\14\4\0\2\5\14\4\1\31"+
        "\14\4\1\0\1\10\10\5\2\14\1\0\2\14\1\0\2\14\1\10\10\5\2\14\10\0\14"+
        "\5\30\4\15\5\14\0\1\31\1\0";
    static final String DFA2_maxS =
        "\1\42\1\57\2\uffff\2\44\1\0\1\60\1\57\3\uffff\2\60\1\0\4\60\1\0"+
        "\1\60\1\0\3\60\1\0\2\57\1\13\10\47\1\0\1\13\10\47\2\57\4\0\1\60"+
        "\1\57\14\47\1\33\14\47\1\0\1\13\10\47\2\57\1\0\2\57\1\0\2\57\1\13"+
        "\10\47\2\57\10\0\44\47\1\60\14\47\14\0\1\33\1\0";
    static final String DFA2_acceptS =
        "\2\uffff\1\4\1\5\5\uffff\1\1\1\2\1\3\u00a8\uffff";
    static final String DFA2_specialS =
        "\6\uffff\1\37\7\uffff\1\0\4\uffff\1\41\1\uffff\1\25\3\uffff\1\35"+
        "\13\uffff\1\34\13\uffff\1\30\1\31\1\32\1\33\33\uffff\1\36\13\uffff"+
        "\1\40\2\uffff\1\27\15\uffff\1\24\1\23\1\22\1\21\1\20\1\17\1\16\1"+
        "\15\61\uffff\1\14\1\13\1\12\1\11\1\10\1\7\1\6\1\5\1\4\1\3\1\2\1"+
        "\1\1\uffff\1\26}>";
    static final String[] DFA2_transitionS = {
            "\1\1\7\uffff\1\3\1\2",
            "\1\3\15\uffff\1\5\1\uffff\1\6\1\4\2\uffff\1\2\15\uffff\2\3",
            "",
            "",
            "\1\7\1\uffff\1\3\10\uffff\1\3",
            "\1\10\1\uffff\1\3\5\uffff\2\11\1\uffff\1\3",
            "\1\uffff",
            "\7\13\1\17\14\uffff\1\16\1\22\1\uffff\1\21\1\20\3\uffff\2\13"+
            "\2\uffff\11\13\1\14\1\15\1\13",
            "\1\26\14\uffff\1\3\1\25\1\uffff\1\23\1\24\2\uffff\1\11\15\uffff"+
            "\1\27\1\30",
            "",
            "",
            "",
            "\1\41\1\42\1\43\1\35\1\36\1\37\1\40\15\uffff\1\31\1\uffff\1"+
            "\13\5\uffff\1\32\1\33\1\3\1\13\1\44\2\34\11\3",
            "\1\41\1\42\1\43\1\35\1\36\1\37\1\40\15\uffff\1\45\1\uffff\1"+
            "\13\5\uffff\1\32\1\33\1\3\1\13\1\44\2\34\11\3",
            "\1\uffff",
            "\1\53\1\54\1\55\1\47\1\50\1\51\1\52\15\uffff\1\61\1\uffff\1"+
            "\13\5\uffff\1\57\1\60\1\3\1\13\1\56\2\46\11\3",
            "\1\53\1\54\1\55\1\47\1\50\1\51\1\52\15\uffff\1\62\1\uffff\1"+
            "\13\5\uffff\1\57\1\60\1\3\1\13\1\56\2\46\11\3",
            "\1\53\1\54\1\55\1\47\1\50\1\51\1\52\15\uffff\1\63\1\uffff\1"+
            "\13\5\uffff\1\57\1\60\1\3\1\13\1\56\2\46\11\3",
            "\1\53\1\54\1\55\1\47\1\50\1\51\1\52\15\uffff\1\64\1\uffff\1"+
            "\13\5\uffff\1\57\1\60\1\3\1\13\1\56\2\46\11\3",
            "\1\uffff",
            "\7\3\15\uffff\1\65\1\uffff\1\11\5\uffff\3\3\1\11\14\3",
            "\1\uffff",
            "\7\3\15\uffff\1\66\1\uffff\1\11\5\uffff\3\3\1\11\14\3",
            "\7\3\15\uffff\1\66\1\uffff\1\11\5\uffff\3\3\1\11\14\3",
            "\7\3\15\uffff\1\66\1\uffff\1\11\5\uffff\3\3\1\11\14\3",
            "\1\uffff",
            "\1\67\15\uffff\1\72\1\3\1\71\1\70\20\uffff\1\73\1\74",
            "\1\75\15\uffff\1\100\1\3\1\77\1\76\20\uffff\1\101\1\102",
            "\1\35\1\36\1\37\1\40",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\uffff",
            "\1\47\1\50\1\51\1\52",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\2\13\2\uffff\3"+
            "\13",
            "\1\104\15\uffff\1\107\1\3\1\106\1\105\20\uffff\1\110\1\111",
            "\1\112\15\uffff\1\115\1\3\1\114\1\113\20\uffff\1\116\1\117",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\126\1\127\1\130\1\122\1\123\1\124\1\125\1\11\14\uffff\1\120"+
            "\1\11\1\3\2\11\3\uffff\1\132\1\133\2\uffff\1\131\2\121\11\11",
            "\7\3\1\11\14\uffff\1\134\1\11\1\3\2\11\3\uffff\2\3\2\uffff\3"+
            "\3\6\uffff\2\11",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\135\1\136\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\135\1\136\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\135\1\136\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\135\1\136\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\135\1\136\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\135\1\136\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\135\1\136\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\135\1\136\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\135\1\136\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\135\1\136\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\135\1\136\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\135\1\136\2"+
            "\uffff\3\13",
            "\1\3\1\uffff\1\137",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\uffff",
            "\1\122\1\123\1\124\1\125",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\155\1"+
            "\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\156\1"+
            "\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\157\1"+
            "\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\160\1"+
            "\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\161\1"+
            "\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\162\1"+
            "\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\163\1"+
            "\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\164\1"+
            "\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\165\15\uffff\1\170\1\uffff\1\167\1\166\20\uffff\1\171\1\172",
            "\1\173\15\uffff\1\176\1\uffff\1\175\1\174\20\uffff\1\177\1\u0080",
            "\1\uffff",
            "\1\u0081\15\uffff\1\u0084\1\uffff\1\u0083\1\u0082\20\uffff\1"+
            "\u0085\1\u0086",
            "\1\u0087\15\uffff\1\u008a\1\uffff\1\u0089\1\u0088\20\uffff\1"+
            "\u008b\1\u008c",
            "\1\uffff",
            "\1\u008d\15\uffff\1\u0090\1\uffff\1\u008f\1\u008e\20\uffff\1"+
            "\u0091\1\u0092",
            "\1\u0093\15\uffff\1\u0096\1\uffff\1\u0095\1\u0094\20\uffff\1"+
            "\u0097\1\u0098",
            "\1\143\1\144\1\145\1\146",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\1\u009a\15\uffff\1\u009d\1\uffff\1\u009c\1\u009b\20\uffff\1"+
            "\u009e\1\u009f",
            "\1\u00a0\15\uffff\1\u00a3\1\uffff\1\u00a2\1\u00a1\20\uffff\1"+
            "\u00a4\1\u00a5",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\u00a6"+
            "\1\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\u00a7"+
            "\1\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\u00a8"+
            "\1\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\u00a9"+
            "\1\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\u00aa"+
            "\1\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\u00ab"+
            "\1\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\u00ac"+
            "\1\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\u00ad"+
            "\1\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\u00ae"+
            "\1\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\u00af"+
            "\1\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\u00b0"+
            "\1\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\147\1\150\1\151\1\143\1\144\1\145\1\146\15\uffff\1\u00b1"+
            "\1\uffff\1\3\5\uffff\1\153\1\154\2\uffff\1\152\2\142",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00b2\5\uffff\1\140\1\141"+
            "\2\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00b2\5\uffff\1\140\1\141"+
            "\2\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00b2\5\uffff\1\140\1\141"+
            "\2\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00b2\5\uffff\1\140\1\141"+
            "\2\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00b2\5\uffff\1\140\1\141"+
            "\2\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00b2\5\uffff\1\140\1\141"+
            "\2\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00b2\5\uffff\1\140\1\141"+
            "\2\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00b2\5\uffff\1\140\1\141"+
            "\2\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00b2\5\uffff\1\140\1\141"+
            "\2\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00b2\5\uffff\1\140\1\141"+
            "\2\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00b2\5\uffff\1\140\1\141"+
            "\2\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\u00b2\5\uffff\1\140\1\141"+
            "\2\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\1\3\7\13\15\uffff\1\13\1\uffff\1\103\5\uffff\1\140\1\141\2"+
            "\uffff\3\13",
            "\7\3\17\uffff\1\11\5\uffff\3\3\1\uffff\14\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
            "\7\3\15\uffff\1\3\1\uffff\1\u0099\5\uffff\2\3\2\uffff\3\3",
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
            "\1\3\1\uffff\1\u00b3",
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
            return "195:1: ce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] : (tmp= andce[tmodel, vars] | tmp= notce[tmodel, vars] | tmp= testce[tmodel, vars] | tmp= collectce[tmodel, vars] | {...}?tmp= objectce[tmodel, vars] );";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA2_14 = input.LA(1);

                         
                        int index2_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_14);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA2_177 = input.LA(1);

                         
                        int index2_177 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_177);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA2_176 = input.LA(1);

                         
                        int index2_176 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_176);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA2_175 = input.LA(1);

                         
                        int index2_175 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_175);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA2_174 = input.LA(1);

                         
                        int index2_174 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_174);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA2_173 = input.LA(1);

                         
                        int index2_173 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_173);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA2_172 = input.LA(1);

                         
                        int index2_172 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_172);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA2_171 = input.LA(1);

                         
                        int index2_171 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_171);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA2_170 = input.LA(1);

                         
                        int index2_170 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_170);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA2_169 = input.LA(1);

                         
                        int index2_169 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_169);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA2_168 = input.LA(1);

                         
                        int index2_168 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_168);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA2_167 = input.LA(1);

                         
                        int index2_167 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_167);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA2_166 = input.LA(1);

                         
                        int index2_166 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_166);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA2_116 = input.LA(1);

                         
                        int index2_116 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_116);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA2_115 = input.LA(1);

                         
                        int index2_115 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_115);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA2_114 = input.LA(1);

                         
                        int index2_114 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_114);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA2_113 = input.LA(1);

                         
                        int index2_113 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_113);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA2_112 = input.LA(1);

                         
                        int index2_112 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_112);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA2_111 = input.LA(1);

                         
                        int index2_111 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_111);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA2_110 = input.LA(1);

                         
                        int index2_110 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_110);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA2_109 = input.LA(1);

                         
                        int index2_109 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_109);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA2_21 = input.LA(1);

                         
                        int index2_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_21);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA2_179 = input.LA(1);

                         
                        int index2_179 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_179);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA2_95 = input.LA(1);

                         
                        int index2_95 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_95);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA2_49 = input.LA(1);

                         
                        int index2_49 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_49);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA2_50 = input.LA(1);

                         
                        int index2_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_50);
                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA2_51 = input.LA(1);

                         
                        int index2_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_51);
                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA2_52 = input.LA(1);

                         
                        int index2_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_52);
                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA2_37 = input.LA(1);

                         
                        int index2_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_37);
                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA2_25 = input.LA(1);

                         
                        int index2_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 11;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_25);
                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA2_80 = input.LA(1);

                         
                        int index2_80 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_80);
                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA2_6 = input.LA(1);

                         
                        int index2_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 10;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_6);
                        if ( s>=0 ) return s;
                        break;
                    case 32 : 
                        int LA2_92 = input.LA(1);

                         
                        int index2_92 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_92);
                        if ( s>=0 ) return s;
                        break;
                    case 33 : 
                        int LA2_19 = input.LA(1);

                         
                        int index2_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(SConditions.lookaheadObjectCE(ClipsJadexParser.this.input))) ) {s = 9;}

                        else if ( (SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)) ) {s = 3;}

                         
                        input.seek(index2_19);
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
        "\1\31\1\60\1\uffff\2\47\1\uffff\1\13\2\47\26\0";
    static final String DFA4_acceptS =
        "\2\uffff\1\2\2\uffff\1\1\31\uffff";
    static final String DFA4_specialS =
        "\37\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\1",
            "\7\2\1\5\14\uffff\1\2\1\5\1\uffff\2\5\3\uffff\2\2\2\uffff\11"+
            "\2\1\3\1\4\1\2",
            "",
            "\1\13\1\14\1\15\1\7\1\10\1\11\1\12\15\uffff\1\21\1\uffff\1\5"+
            "\5\uffff\1\17\1\20\1\uffff\1\5\1\16\2\6",
            "\1\13\1\14\1\15\1\7\1\10\1\11\1\12\15\uffff\1\22\1\uffff\1\5"+
            "\5\uffff\1\17\1\20\1\uffff\1\5\1\16\2\6",
            "",
            "\1\7\1\10\1\11\1\12",
            "\1\30\1\31\1\32\1\24\1\25\1\26\1\27\15\uffff\1\36\1\uffff\1"+
            "\5\5\uffff\1\34\1\35\2\uffff\1\33\2\23",
            "\1\30\1\31\1\32\1\24\1\25\1\26\1\27\15\uffff\1\36\1\uffff\1"+
            "\5\5\uffff\1\34\1\35\2\uffff\1\33\2\23",
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
            return "226:15: ( ({...}?call= functionCall[tmodel, vars] ) | call= operatorCall[tmodel, vars] )";
        }
    }
    static final String DFA19_eotS =
        "\37\uffff";
    static final String DFA19_eofS =
        "\37\uffff";
    static final String DFA19_minS =
        "\1\31\1\5\1\uffff\2\5\1\uffff\1\10\2\5\26\0";
    static final String DFA19_maxS =
        "\1\31\1\60\1\uffff\2\47\1\uffff\1\13\2\47\26\0";
    static final String DFA19_acceptS =
        "\2\uffff\1\2\2\uffff\1\1\31\uffff";
    static final String DFA19_specialS =
        "\37\uffff}>";
    static final String[] DFA19_transitionS = {
            "\1\1",
            "\7\2\1\5\14\uffff\1\2\1\5\1\uffff\2\5\3\uffff\2\2\2\uffff\11"+
            "\2\1\3\1\4\1\2",
            "",
            "\1\13\1\14\1\15\1\7\1\10\1\11\1\12\15\uffff\1\21\1\uffff\1\5"+
            "\5\uffff\1\17\1\20\1\uffff\1\5\1\16\2\6",
            "\1\13\1\14\1\15\1\7\1\10\1\11\1\12\15\uffff\1\22\1\uffff\1\5"+
            "\5\uffff\1\17\1\20\1\uffff\1\5\1\16\2\6",
            "",
            "\1\7\1\10\1\11\1\12",
            "\1\30\1\31\1\32\1\24\1\25\1\26\1\27\15\uffff\1\36\1\uffff\1"+
            "\5\5\uffff\1\34\1\35\2\uffff\1\33\2\23",
            "\1\30\1\31\1\32\1\24\1\25\1\26\1\27\15\uffff\1\36\1\uffff\1"+
            "\5\5\uffff\1\34\1\35\2\uffff\1\33\2\23",
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
            return "445:8: ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )";
        }
    }
    static final String DFA20_eotS =
        "\37\uffff";
    static final String DFA20_eofS =
        "\37\uffff";
    static final String DFA20_minS =
        "\1\31\1\5\1\uffff\2\5\1\uffff\1\10\2\5\26\0";
    static final String DFA20_maxS =
        "\1\31\1\60\1\uffff\2\47\1\uffff\1\13\2\47\26\0";
    static final String DFA20_acceptS =
        "\2\uffff\1\2\2\uffff\1\1\31\uffff";
    static final String DFA20_specialS =
        "\37\uffff}>";
    static final String[] DFA20_transitionS = {
            "\1\1",
            "\7\2\1\5\14\uffff\1\2\1\5\1\uffff\2\5\3\uffff\2\2\2\uffff\11"+
            "\2\1\3\1\4\1\2",
            "",
            "\1\13\1\14\1\15\1\7\1\10\1\11\1\12\15\uffff\1\21\1\uffff\1\5"+
            "\5\uffff\1\17\1\20\1\uffff\1\5\1\16\2\6",
            "\1\13\1\14\1\15\1\7\1\10\1\11\1\12\15\uffff\1\22\1\uffff\1\5"+
            "\5\uffff\1\17\1\20\1\uffff\1\5\1\16\2\6",
            "",
            "\1\7\1\10\1\11\1\12",
            "\1\30\1\31\1\32\1\24\1\25\1\26\1\27\15\uffff\1\36\1\uffff\1"+
            "\5\5\uffff\1\34\1\35\2\uffff\1\33\2\23",
            "\1\30\1\31\1\32\1\24\1\25\1\26\1\27\15\uffff\1\36\1\uffff\1"+
            "\5\5\uffff\1\34\1\35\2\uffff\1\33\2\23",
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
            return "452:18: ({...}?fc= functionCall[tmodel, vars] | oc= operatorCall[tmodel, vars] )";
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
    public static final BitSet FOLLOW_25_in_objectce360 = new BitSet(new long[]{0x0000C00034001000L});
    public static final BitSet FOLLOW_typename_in_objectce366 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_attributeConstraint_in_objectce375 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_methodConstraint_in_objectce386 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_functionConstraint_in_objectce397 = new BitSet(new long[]{0x000000000A000000L});
    public static final BitSet FOLLOW_27_in_objectce409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_attributeConstraint431 = new BitSet(new long[]{0x0000C00034001000L});
    public static final BitSet FOLLOW_slotname_in_attributeConstraint435 = new BitSet(new long[]{0x0001FFEE00000FE0L});
    public static final BitSet FOLLOW_constraint_in_attributeConstraint439 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_attributeConstraint442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_methodConstraint469 = new BitSet(new long[]{0x0000C00034001000L});
    public static final BitSet FOLLOW_methodname_in_methodConstraint473 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_methodConstraint475 = new BitSet(new long[]{0x000000E60A000FE0L});
    public static final BitSet FOLLOW_parameter_in_methodConstraint479 = new BitSet(new long[]{0x000000E60A000FE0L});
    public static final BitSet FOLLOW_27_in_methodConstraint489 = new BitSet(new long[]{0x0001FFEE00000FE0L});
    public static final BitSet FOLLOW_constraint_in_methodConstraint493 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_methodConstraint496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_functionConstraint523 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_functionCall_in_functionConstraint527 = new BitSet(new long[]{0x0001FFEE00000FE0L});
    public static final BitSet FOLLOW_constraint_in_functionConstraint532 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_functionConstraint535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_constraint565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_constraint571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleConstraint_in_constraint579 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_ConstraintOperator_in_constraint588 = new BitSet(new long[]{0x0001FFEE00000FE0L});
    public static final BitSet FOLLOW_singleConstraint_in_constraint592 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_literalConstraint_in_singleConstraint624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boundConstraint_in_singleConstraint635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiBoundConstraint_in_singleConstraint645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicateConstraint_in_singleConstraint655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_returnValueConstraint_in_singleConstraint665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_in_literalConstraint687 = new BitSet(new long[]{0x000000E000000FE0L});
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
    public static final BitSet FOLLOW_25_in_functionCall917 = new BitSet(new long[]{0x0000C00034001000L});
    public static final BitSet FOLLOW_functionName_in_functionCall921 = new BitSet(new long[]{0x000000E60A000FE0L});
    public static final BitSet FOLLOW_parameter_in_functionCall926 = new BitSet(new long[]{0x000000E60A000FE0L});
    public static final BitSet FOLLOW_27_in_functionCall936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_operatorCall957 = new BitSet(new long[]{0x0001FFE602000FE0L});
    public static final BitSet FOLLOW_operator_in_operatorCall962 = new BitSet(new long[]{0x000000E602000FE0L});
    public static final BitSet FOLLOW_parameter_in_operatorCall967 = new BitSet(new long[]{0x000000E602000FE0L});
    public static final BitSet FOLLOW_parameter_in_operatorCall972 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_operatorCall975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_in_parameter998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_parameter1008 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionCall_in_parameter1020 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operatorCall_in_parameter1030 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_constant1051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleFieldVariable_in_variable1074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiFieldVariable_in_variable1084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_singleFieldVariable1104 = new BitSet(new long[]{0x0000C00034001000L});
    public static final BitSet FOLLOW_identifier_in_singleFieldVariable1108 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_multiFieldVariable1130 = new BitSet(new long[]{0x0000C00034001000L});
    public static final BitSet FOLLOW_identifier_in_multiFieldVariable1134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_typename1160 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_typename1168 = new BitSet(new long[]{0x0000C00034001000L});
    public static final BitSet FOLLOW_identifier_in_typename1172 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_identifier_in_slotname1199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_methodname1219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typename_in_functionName1238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_floatingPointLiteral_in_literal1258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal1267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal1274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal1281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BooleanLiteral_in_literal1288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_literal1295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_floatingPointLiteral1314 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_floatingPointLiteral1321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral1341 = new BitSet(new long[]{0x0000000000000E00L});
    public static final BitSet FOLLOW_HexLiteral_in_integerLiteral1349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OctalLiteral_in_integerLiteral1356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DecimalLiteral_in_integerLiteral1363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_equalOperator_in_operator1383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_operator1391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_operator1398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_operator1405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_operator1412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_operator1419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_operator1426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_operator1433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_operator1440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_equalOperator1457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifiertoken_in_identifier1477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_identifier1486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_identifier1495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_identifier1504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_identifier1513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_identifier1522 = new BitSet(new long[]{0x0000000000000002L});

}