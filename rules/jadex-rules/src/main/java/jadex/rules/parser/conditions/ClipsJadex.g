grammar ClipsJadex;

//options {k=2; backtrack=true; memoize=true;}

@header 
{
package jadex.rules.parser.conditions;

import jadex.rules.rulesystem.rules.*;
import jadex.rules.rulesystem.rules.functions.*;
import jadex.rules.rulesystem.*;
import jadex.rules.state.*;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
}

@lexer::header 
{
package jadex.rules.parser.conditions;
}

@members
{
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
}

// Parser

rhs[OAVTypeModel tmodel] returns [ICondition condition]  
	: 
	{
		List conds = new ArrayList();
		Map vars = new HashMap();
		
	}
	(c=ce[tmodel, vars] 
	{
		conds.add(c);
	}
	)+
	{
		if(conds.size()>1)
			$condition = new AndCondition(conds);
		else
			$condition = (ICondition)conds.get(0);
	}
	EOF;

// Conditional elements
ce[OAVTypeModel tmodel, Map vars] returns [ICondition condition] 	
	: {"and".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)}?
		tmp = andce[tmodel, vars] {$condition = tmp;}
	| {"not".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)}?
		tmp = notce[tmodel, vars] {$condition = tmp;}
	| {"test".equals(ClipsJadexParser.this.input.LT(2).getText()) && !SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)}?
		tmp = testce[tmodel, vars] {$condition = tmp;}
	| tmp = collectce[tmodel, vars] {$condition = tmp;}
	| {SConditions.lookaheadObjectCE(ClipsJadexParser.this.input)}? tmp = objectce[tmodel, vars] {$condition = tmp;}
	;

andce[OAVTypeModel tmodel, Map vars] returns [ICondition condition]
	: 
	{
		List conds = new ArrayList();
	}
	'(' 'and' (c=ce[$tmodel, vars]
	{
		conds.add(c);
	}
	)+ ')'
	{
		$condition = new AndCondition(conds);
	}
	;

notce[OAVTypeModel tmodel, Map vars] returns [ICondition condition]
	: '(' 'not' c=ce[$tmodel, vars] ')'
	{
		$condition = new NotCondition(c);
	}
	;

testce[OAVTypeModel tmodel, Map vars] returns [ICondition condition]
	: '(' 'test'
		( call=operatorCall[tmodel, vars]
		| ({SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)}? call=functionCall[tmodel, vars])
		) ')'
	{
		$condition = new TestCondition(new PredicateConstraint(call));
	}
	;

collectce[OAVTypeModel tmodel, Map vars] returns [ICondition condition]
	: 
	{
		List conds = new ArrayList();
	}
	(mfv=multiFieldVariable[null, vars] ('<-' | '='))? 
	'(' 'collect' (c=ce[$tmodel, vars]
	{
		conds.add(c);
	}
	)+  pc=predicateConstraint[$tmodel, null, vars]? ')' // null correct?
	{
		ObjectCondition first = (ObjectCondition)conds.get(0);
		mfv.setType(first.getObjectType());
		List consts = new ArrayList();
		consts.add(new BoundConstraint(null, mfv));
		if(pc!=null)
			consts.add(pc);
		CollectCondition ccond = new CollectCondition(conds, consts);
		$condition = ccond;
	}
	;

// Introduced '=' as alternative for XML, as < cannot be used
objectce[OAVTypeModel tmodel, Map vars] returns [ICondition condition]
	: 
	{
		List consts = new ArrayList();
	}
	(sfv=singleFieldVariable[null, vars] ('<-' | '='))? 
	'(' 
	tn=typename 
	
	(acs=attributeConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars]
	{
		consts.addAll(acs);
	}
	| mcs=methodConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars]
	{
		consts.addAll(mcs);
	}
	| fcs=functionConstraint[tmodel, SConditions.getObjectType(tmodel, tn, imports), vars]
	{
		consts.addAll(fcs);
	}
	)* 
	')'
	{
		// Set variable type if still unknown/unprecise
		if(sfv!=null)
			SConditions.adaptConditionType(sfv, SConditions.getObjectType(tmodel, tn, imports));
		
		OAVObjectType otype = SConditions.getObjectType(tmodel, tn, imports);
		ObjectCondition ocond = new ObjectCondition(otype, consts);
		if(sfv!=null)
			ocond.addConstraint(new BoundConstraint(null, sfv));
		$condition = ocond;
	}
	;

// Constraints
attributeConstraint [OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints]	
	: '(' sn=slotname cs=constraint[tmodel, SConditions.convertAttributeTypes(tmodel, otype, sn, imports), vars] ')'
	{
		$constraints = cs;
	}
	; 
	
methodConstraint [OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints]	
	:
	{
		List exps = new ArrayList();
	}
	'(' mn=methodname '('(exp=parameter[tmodel, vars]
	{
		exps.add(exp);
	}
	)* ')' cs=constraint[tmodel, SConditions.createMethodCall(otype, mn, exps), vars] ')'
	{
		$constraints = cs;
	}
	; 
	
functionConstraint [OAVTypeModel tmodel, OAVObjectType otype, Map vars] returns [List constraints]	
	:
	'(' fc=functionCall[tmodel, vars] cs=constraint[tmodel, fc, vars] ')'
	{
		$constraints = cs;
	}
	; 

constraint [OAVTypeModel tmodel, Object valuesource, Map vars] returns [List constraints]		
	:
	// todo: enable () and support '?' and '$?'
	// Code is unclean here due to the problem that first constraint could be bound constraint
	// which should NOT be included into the combined constraint
	'?' 
	| '$?' 
	| last=singleConstraint[tmodel, valuesource, vars] 
	{
		List ret = new ArrayList();
		List consts = new ArrayList();
		String op = null;
		if(last instanceof BoundConstraint)
			ret.add(last);
		else
			consts.add(last);
	}
	(ConstraintOperator next=singleConstraint[tmodel, valuesource, vars]
	{
		// Set op if first occurrence
		if(op==null)
		{
			op = $ConstraintOperator.text;
		}
	
		consts.add(next);
		if(consts.size()>1)
		{	
			if(!SUtil.equals($ConstraintOperator.text, op))
			{
				if(op.equals("&"))
					last = new AndConstraint((IConstraint[])consts.toArray(new IConstraint[consts.size()]));
				else
					last = new OrConstraint((IConstraint[])consts.toArray(new IConstraint[consts.size()]));
				
				op = $ConstraintOperator.text;	
				consts.clear();
				consts.add(last);
			}
		}
	}
	)*
	{
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
			
		$constraints = ret;
	}
	;

singleConstraint [OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint]		
	: tmp=literalConstraint[valuesource] {$constraint = tmp;} 
	| tmp=boundConstraint[tmodel, valuesource, vars] {$constraint = tmp;}
	| tmp=multiBoundConstraint[tmodel, valuesource, vars] {$constraint = tmp;}
	| tmp=predicateConstraint[tmodel, valuesource, vars] {$constraint = tmp;}
	| tmp=returnValueConstraint[tmodel, valuesource, vars] {$constraint = tmp;}
	;

literalConstraint [Object valuesource] returns [IConstraint constraint]
	: op=operator? val=constant
	{
		if(op!=null)	
			$constraint = new LiteralConstraint($valuesource, val, op);
		else
			$constraint = new LiteralConstraint($valuesource, val);
	}
	;
	
someBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint]	
	: bc=boundConstraint[tmodel, valuesource, vars] 
	{
		$constraint = bc;
	}
	| mbc =multiBoundConstraint[tmodel, valuesource, vars]
	{
		$constraint = mbc;
	}
	;
	
boundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint]
	: op=operator? var=variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars]
	{
		if(op!=null)
			$constraint = new BoundConstraint($valuesource, var, op);
		else
			$constraint = new BoundConstraint($valuesource, var);
	}
	;
	
multiBoundConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint]
	: 
	{
		List vs = new ArrayList();
	}
	op=operator? var=variable[op==null || op.equals(IOperator.EQUAL)? SConditions.getValueSourceType(tmodel, valuesource): null, vars]
	{
		vs.add(var);
	}
	(varn=variable[SConditions.getValueSourceType(tmodel, valuesource), vars]
	{
		vs.add(varn);
	}
	)+
	{
		//if(op!=null)
			$constraint = new BoundConstraint($valuesource, vs, op==null? IOperator.EQUAL: op); // Hack? one operator per variable?
		//else
		//	$constraint = new BoundConstraint($valuesource, vars);
	}
	;
	
predicateConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint]
	: ':' ({SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)}? fc=functionCall[tmodel, vars] {$constraint = new PredicateConstraint(fc);} 
	| oc=operatorCall[tmodel, vars] {$constraint = new PredicateConstraint(oc);}
	)
	;
	
// todo: support other operators than =
returnValueConstraint[OAVTypeModel tmodel, Object valuesource, Map vars] returns [IConstraint constraint]
	: equalOperator ({SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)}? fc=functionCall[tmodel, vars] {$constraint = new ValueSourceReturnValueConstraint(valuesource, fc, IOperator.EQUAL);} 
	| oc=operatorCall[tmodel, vars] {$constraint = new ValueSourceReturnValueConstraint(valuesource, oc, IOperator.EQUAL);} 
	) 
	;

functionCall [OAVTypeModel tmodel, Map vars] returns [FunctionCall fc]
	: 
	{
		List exps = new ArrayList();
	}
	'(' fn=functionName (exp=parameter[tmodel, vars]
	{
		exps.add(exp);
	}
	)* ')'
	{
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
	;

operatorCall [OAVTypeModel tmodel, Map vars] returns [FunctionCall fc]	
	: '('  op=operator/*?*/ exp1=parameter[tmodel, vars] exp2=parameter[tmodel, vars] ')'
	{
		IFunction func = new OperatorFunction(op!=null? op: IOperator.EQUAL);
		$fc = new FunctionCall(func, new Object[]{exp1, exp2});
	}
	;

parameter [OAVTypeModel tmodel, Map vars] returns [Object val]	
	: tmp1=constant {$val = new Constant(tmp1);} 
	| tmp2=variable[null, vars] {$val = tmp2;}
	| {SConditions.lookaheadFunctionCall(ClipsJadexParser.this.input)}? tmp3=functionCall[tmodel, vars] {$val = tmp3;}
	| tmp4=operatorCall[tmodel, vars] {$val = tmp4;}
	;

constant returns [Object val]	
	: tmp=literal {$val = tmp;}
	;

variable [OAVObjectType type, Map vars] returns [Variable var]		
	: tmp=singleFieldVariable[type, vars] {$var = tmp;}
	| tmp=multiFieldVariable[type, vars] {$var = tmp;}
	;

singleFieldVariable [OAVObjectType type, Map vars] returns [Variable var]
	: '?' id=identifier 
	{	
		String vn = "?"+id.getText();
		$var = (Variable)vars.get(vn);
		if($var==null)
		{
			$var = new Variable(vn, type);
			vars.put(vn, $var);
		}
		else if(type!=null)
		{
			SConditions.adaptConditionType(var, type);
		}
	}
	;

multiFieldVariable [OAVObjectType type, Map vars] returns [Variable var]	
	: '$?' id=identifier 
	{
		String vn = "$?"+id.getText();
		$var = (Variable)vars.get(vn);
		if($var==null)
		{
			$var = new Variable(vn, type, true, false);
			vars.put(vn, $var);
		}
		else if(type!=null)
		{
			SConditions.adaptConditionType(var, type);
		}
	}
	;

typename returns [String id]
	: 
	{
		StringBuffer buf = new StringBuffer();
	}
	tmp=identifier 
	{
		buf.append(tmp.getText());
	}
	('.' tmp=identifier
	{
		buf.append(".").append(tmp.getText());
	}
	)*
	{
		$id = buf.toString();
	}
	;

/*slotname returns [String id]
	: tmp=Identifiertoken
	{
		$id = tmp.getText();
	}
	;*/
	
slotname returns [String id]
	:
	{
		StringBuffer buf = new StringBuffer();
	}
	tmp=identifier 
	{
		buf.append(tmp.getText());
	}
	('.' tmp=identifier
	{
		buf.append(".").append(tmp.getText());
	}
	|':' tmp=identifier
	{
		buf.append(":").append(tmp.getText());
	}
	|('[' tmp=identifier ']') 
	{
		buf.append("[").append(tmp.getText()).append("]");
	}
	|('[' StringLiteral ']') 
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $StringLiteral.text;
		buf.append("[").append(text!=null ? text.substring(1, text.length()-1) : null).append("]");
	}
	)*
	{
		$id = buf.toString();
	}
	;

	
methodname returns [String id]
	: tmp=identifier {$id = tmp.getText();}
	;

functionName returns [String id]
	: tmp=typename {$id = tmp;}
	;

literal	returns [Object val]	
	: lit=floatingPointLiteral {$val = lit;}
	| lit=integerLiteral {$val = lit;}
	| CharacterLiteral
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $CharacterLiteral.text;
		$val = text==null ? null : Character.valueOf(text.charAt(0));
	}
	| StringLiteral
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $StringLiteral.text;
		$val = text==null ? null : text.substring(1, text.length()-1);
	}
	| BooleanLiteral
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $BooleanLiteral.text;
		$val = text==null ? null : text.equals("true")? Boolean.TRUE: Boolean.FALSE;
	}
	| 'null' {$val = null;}
	;

floatingPointLiteral returns [Object val]
	: sign=('+'|'-')? FloatingPointLiteral
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $FloatingPointLiteral.text;
		$val = text==null ? null : sign!=null && "-".equals(sign.getText())? Double.valueOf("-"+text): Double.valueOf(text);
	}
	;
	
integerLiteral returns [Object val]
	: sign=('+'|'-')?
	( HexLiteral
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $HexLiteral.text;
		$val = text==null ? null : sign!=null && "-".equals(sign.getText())? Integer.valueOf("-"+text): Integer.valueOf(text);
	}
	| OctalLiteral
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $OctalLiteral.text;
		$val = text==null ? null : sign!=null && "-".equals(sign.getText())? Integer.valueOf("-"+text): Integer.valueOf(text);
	}
	| DecimalLiteral
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $DecimalLiteral.text;
		$val = text==null ? null : sign!=null && "-".equals(sign.getText())? Integer.valueOf("-"+text): Integer.valueOf(text);
	})
	;

operator returns [IOperator operator]
	: tmp=equalOperator {$operator = tmp;} 
	| '!=' {$operator = IOperator.NOTEQUAL;}
	| '~' {$operator = IOperator.NOTEQUAL;}
	| '>' {$operator = IOperator.GREATER;}
	| '<' {$operator = IOperator.LESS;}
	| '>=' {$operator = IOperator.GREATEROREQUAL;}
	| '<=' {$operator = IOperator.LESSOREQUAL;}
	| 'contains' {$operator = IOperator.CONTAINS;}
	| 'excludes' {$operator = IOperator.EXCLUDES;}
	;

equalOperator returns [IOperator operator]
	: '==' {$operator = IOperator.EQUAL;} 
	;

identifier returns [Token identifier]
	: tmp=Identifiertoken {$identifier = tmp;}
	| tmp='test' {$identifier = tmp;}
	| tmp='not' {$identifier = tmp;}
	| tmp='and' {$identifier = tmp;}
	| tmp='collect' {$identifier = tmp;}
	| tmp='contains' {$identifier = tmp;}
	| tmp='excludes' {$identifier = tmp;}
	;
	
// Lexxer

ConstraintOperator	
	: '&' | '|'
	;

BooleanLiteral
	:   'true' | 'false'
	;

HexLiteral 
	: '0' ('x'|'X') HexDigit+ IntegerTypeSuffix?
	;

DecimalLiteral 
	: ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix?
	;

OctalLiteral 
	: '0' ('0'..'7')+ IntegerTypeSuffix?
	;

fragment
HexDigit: ('0'..'9'|'a'..'f'|'A'..'F')
	;

fragment
IntegerTypeSuffix 
	: ('l'|'L')
	;

FloatingPointLiteral
    	:   ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
 	|   '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
 	|   ('0'..'9')+ Exponent FloatTypeSuffix?
	|   ('0'..'9')+ FloatTypeSuffix
	;

fragment
Exponent: ('e'|'E') ('+'|'-')? ('0'..'9')+ 
	;

fragment
FloatTypeSuffix 
	: ('f'|'F'|'d'|'D') 
	;

CharacterLiteral
	:   '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
	;

StringLiteral
	:  '"' ( text=EscapeSequence | ~('\\'|'"') )* '"'
	;

fragment
EscapeSequence
	:   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
	|   UnicodeEscape
	|   OctalEscape
	;

fragment
OctalEscape
	:   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
	|   '\\' ('0'..'7') ('0'..'7')
	|   '\\' ('0'..'7')
	;

fragment
UnicodeEscape
	:   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
	;
	
Identifiertoken 
	:   Letter (Letter|JavaIDDigit)*
	;
	
/*ExtendedIdentifiertoken 
	:   Letter (Letter|JavaIDDigit|'['|']'|'"'|'.')*
	;*/

fragment
Letter
	:  '\u0024' |
	'\u0041'..'\u005a' |
	'\u005f' |
	'\u0061'..'\u007a' |
	'\u00c0'..'\u00d6' |
	'\u00d8'..'\u00f6' |
	'\u00f8'..'\u00ff' |
	'\u0100'..'\u1fff' |
	'\u3040'..'\u318f' |
	'\u3300'..'\u337f' |
	'\u3400'..'\u3d2d' |
	'\u4e00'..'\u9fff' |
	'\uf900'..'\ufaff'
	;

fragment
JavaIDDigit
	:  '\u0030'..'\u0039' |
	'\u0660'..'\u0669' |
	'\u06f0'..'\u06f9' |
	'\u0966'..'\u096f' |
	'\u09e6'..'\u09ef' |
	'\u0a66'..'\u0a6f' |
	'\u0ae6'..'\u0aef' |
	'\u0b66'..'\u0b6f' |
	'\u0be7'..'\u0bef' |
	'\u0c66'..'\u0c6f' |
	'\u0ce6'..'\u0cef' |
	'\u0d66'..'\u0d6f' |
	'\u0e50'..'\u0e59' |
	'\u0ed0'..'\u0ed9' |
	'\u1040'..'\u1049'
	;

WS 	:  (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;}
	;

COMMENT
	:   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
	;

LINE_COMMENT
	: '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
	;

