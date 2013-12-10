grammar JavaJadex;

//options {k=2; backtrack=true; memoize=true;}

@header 
{
package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.functions.IFunction;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVJavaType;

import jadex.commons.SReflect;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
}

@lexer::header 
{
package jadex.rules.parser.conditions.javagrammar;
}

@members
{
	/** The parser helper provides additional information (e.g. local variables). */
	protected IParserHelper	helper;
	
	/**
	 *  Set the parser helper.
	 */
	public void	setParserHelper(IParserHelper helper)
	{
		this.helper	= helper;
	}

	/** The imports (if any). */
	protected String[]	imports;
	
	/**
	 *  Set the imports.
	 */
	public void	setImports(String[] imports)
	{
		this.imports	= imports;
	}
}

// Parser

/**
 *  Left hand side. Start rule for parser.
 */
lhs returns [Expression exp]
	: tmp = expression EOF {$exp = tmp;}
	;

/**
 *  An expression is some Java code that can be evaluated to
 *  a value (left hand side of an assignment).
 */
expression returns [Expression exp]
	: tmp = conditionalExpression {$exp = tmp;}
	;



/**
 *  An equality comparison between two values.
 */
conditionalExpression returns [Expression exp]
	: tmp = logicalOrExpression {$exp = tmp;}
        (
        	'?' tmp2 = conditionalExpression ':' tmp3 = conditionalExpression
        	{
        		$exp = new ConditionalExpression(tmp, tmp2, tmp3);
        	}
        )?
	;
	
/**
 *  An equality comparison between two values.
 */
logicalOrExpression returns [Expression exp]
	: tmp = logicalAndExpression {$exp = tmp;}
        (
        	'||' tmp2 = logicalAndExpression
        	{
        		$exp = new OperationExpression($exp, tmp2, OperationExpression.OPERATOR_OR);
        	}
        )*
	;
	
/**
 *  An equality comparison between two values.
 */
logicalAndExpression returns [Expression exp]
	: tmp = equalityExpression {$exp = tmp;}
        (
        	'&&' tmp2 = equalityExpression
        	{
        		$exp = new OperationExpression($exp, tmp2, OperationExpression.OPERATOR_AND);
        	}
        )*
	;
	
/**
 *  An equality comparison between two values.
 */
equalityExpression returns [Expression exp]
	@init{IOperator	operator = null;}
	: tmp = relationalExpression {$exp = tmp;}
        (
	        ('==' {operator=IOperator.EQUAL;}
        	|'!=' {operator=IOperator.NOTEQUAL;}
        	) tmp2 = relationalExpression
	        {
	        	$exp = new OperationExpression($exp, tmp2, operator);
	        }
	)*
	;

/**
 *  A comparison between two values.
 */
relationalExpression returns [Expression exp]
	@init{IOperator	operator = null;}
	: tmp = additiveExpression {$exp = tmp;}
        (
        	('<' {operator=IOperator.LESS;}
        	|'<=' {operator=IOperator.LESSOREQUAL;}
        	|'>' {operator=IOperator.GREATER;}
        	|'>=' {operator=IOperator.GREATEROREQUAL;}
        	) tmp2 = additiveExpression
	        {
	        	$exp = new OperationExpression($exp, tmp2, operator);
	        }
        )*
	;

/**
 *  An additive expression adds or subtracts two values.
 */
additiveExpression returns [Expression exp]
	@init{IFunction	operator = null;}
	: tmp = multiplicativeExpression {$exp = tmp;}
        (
	        ('+' {operator=IFunction.SUM;}
        	|'-' {operator=IFunction.SUB;}
        	) tmp2 = multiplicativeExpression
	        {
	        	$exp = new OperationExpression($exp, tmp2, operator);
	        }
	)*
	;

/**
 *  A multiplicative expression multiplies or divides two values.
 */
multiplicativeExpression returns [Expression exp]
	@init{IFunction	operator = null;}
	: tmp = unaryExpression {$exp = tmp;}
        (
	        ('*' {operator=IFunction.MULT;}
        	|'/' {operator=IFunction.DIV;}
        	|'%' {operator=IFunction.MOD;}
        	) tmp2 = unaryExpression
	        {
	        	$exp = new OperationExpression($exp, tmp2, operator);
	        }
	)*
	;
	
/**
 *  An unary expression operates on a single value.
 */
unaryExpression returns [Expression exp]
	: '+' tmp = unaryExpression {$exp = tmp;}
	| '-' tmp = unaryExpression {$exp = new UnaryExpression(tmp, UnaryExpression.OPERATOR_MINUS);}
	| '!' tmp = unaryExpression {$exp = new UnaryExpression(tmp, UnaryExpression.OPERATOR_NOT);}
	| '~' tmp = unaryExpression {$exp = new UnaryExpression(tmp, UnaryExpression.OPERATOR_BNOT);}
	| {SJavaParser.lookaheadCast(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)}? '(' tmp1 = type ')' tmp2 = unaryExpression {$exp = new CastExpression(tmp1, tmp2);}
	| {!SJavaParser.lookaheadCast(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)}? tmp = primaryExpression {$exp = tmp;}
	;
	
/**
 *  A primary expression produces a single value
 */
primaryExpression returns [Expression exp]
	@init{List suffs = null;}
	: tmp = primaryPrefix
	(tmp2 = primarySuffix
		{
			if(suffs==null)
				suffs	= new ArrayList();
			suffs.add(tmp2);
		}
	)*
	{
		if(suffs==null)
			$exp	= tmp;
		else
			$exp	= new PrimaryExpression(tmp, (Suffix[])suffs.toArray(new Suffix[suffs.size()]));
	}
	;

/**
 *  Primary part of a expression, i.e. a direct representation of a value.
 */
primaryPrefix returns [Expression exp]
	: '(' tmp = expression ')' {$exp = tmp;}
	| tmp = literal {$exp = tmp;}
	| tmp = collectExpression {$exp = tmp;}
	| {SJavaParser.lookaheadType(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)!=-1}? tmp = typePrimary {$exp = tmp;}
	| {SJavaParser.lookaheadType(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)==-1}? tmp = nontypePrimary {$exp = tmp;}
	;

/**
 * Primary expression starting with a type.
 */
typePrimary returns [Expression exp]
	: {SJavaParser.lookaheadStaticMethod(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)}? tmp = staticMethod {$exp = tmp;}
	| {SJavaParser.lookaheadStaticField(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)}? tmp = staticField {$exp = tmp;}
	| {SJavaParser.lookaheadExistential(JavaJadexParser.this.input, helper.getBuildContext().getTypeModel(), imports)}? tmp = existentialDeclaration {$exp = tmp;}
	;

/**
 * Primary expression starting with a non-type identifier.
 */
nontypePrimary returns [Expression exp]
	: {helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText()) && !"(".equals(JavaJadexParser.this.input.LT(4).getText())}? tmp = pseudovariable {$exp = tmp;}
	| {helper.getVariable(JavaJadexParser.this.input.LT(1).getText())!=null}? tmp = variable {$exp = tmp;}
	;

/**
 *  Invoke a static method
 */
staticMethod returns [Expression exp]
	@init{List params = new ArrayList();}
	: otype = type '.' tmp1 = IDENTIFIER '(' ')' {$exp = new StaticMethodAccess((OAVJavaType)otype, tmp1.getText(), null);}
	| otype = type '.' tmp2 = IDENTIFIER '(' p1 = expression {params.add(p1);}
	(',' p2 = expression {params.add(p2);}
	)* ')'
	{
		$exp	= new StaticMethodAccess((OAVJavaType)otype, tmp2.getText(), (Expression[])params.toArray(new Expression[params.size()]));
	}
	;

/**
 *  Read a field of a class.
 */
staticField returns [Expression exp]
	: otype = type '.' field = IDENTIFIER
	{
		try
		{
			Class	clazz	= ((OAVJavaType)otype).getClazz();
			Field	f	= clazz.getField(field.getText());
			$exp	= new LiteralExpression(f.get(null));
			if((f.getModifiers()&Modifier.FINAL)==0)
				System.out.println("Warning: static field should be final: "+clazz+", "+field.getText());
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	;

/**
 *  Demand the existence of an object and bind an instance to a variable.
 */
existentialDeclaration returns [Expression exp]
	: otype = type varname = IDENTIFIER
	{
		Variable	var	= new Variable(varname.getText(), otype);
		$exp	= new ExistentialDeclaration(otype, var);
		helper.addVariable(var);
	}
	;

/**
 *  Collect objects and return a collection of them.
 */
collectExpression returns [Expression exp]
	: 'collect(' /*varname = IDENTIFIER ','*/ tmp = expression ')'
	{
		//Variable	var	= helper.getBuildContext().getVariable(varname.getText());
		$exp	= new CollectExpression(/*var,*/ tmp);
	}
	;

/**
 *  An oav type or java class.
 */
type returns [OAVObjectType otype]
	@init{String name = null;}
	: tmp = IDENTIFIER
	{
		name	= tmp.getText();
		try
		{
			$otype	= helper.getBuildContext().getTypeModel().getObjectType(name);
		}
		catch(Exception e)
		{
			Class	clazz	= SReflect.findClass0(name, imports, helper.getBuildContext().getTypeModel().getClassLoader());
			if(clazz!=null)
				$otype	= helper.getBuildContext().getTypeModel().getJavaType(clazz);
		}
	}
	(	{$otype==null}?
		'.' tmp2 = IDENTIFIER
		{
			name += "."+tmp2.getText();
 			Class	clazz	= SReflect.findClass0(name, imports, helper.getBuildContext().getTypeModel().getClassLoader());
			if(clazz!=null)
				$otype	= helper.getBuildContext().getTypeModel().getJavaType(clazz);
 		}
	)*
	;
	
/**
 *  Continuations on a value, i.e. field or method access.
 */
primarySuffix returns [Suffix suff]
	: tmp = fieldAccess {$suff = tmp;}
	| tmp = methodAccess {$suff = tmp;}
	| tmp = arrayAccess {$suff = tmp;}
	;

/**
 *  Read a field of an object.
 */
fieldAccess returns [Suffix suff]
	: '.' tmp = IDENTIFIER {$suff = new FieldAccess(tmp.getText());}
	;

/**
 *  Invoke a method on an object.
 */
methodAccess returns [Suffix suff]
	@init{List params = new ArrayList();}
	: '.' tmp1 = IDENTIFIER '(' ')' {$suff = new MethodAccess(tmp1.getText(), null);}
	| '.' tmp2 = IDENTIFIER '(' p1 = expression {params.add(p1);}
	(',' p2 = expression {params.add(p2);}
	)* ')'
	{
		$suff	= new MethodAccess(tmp2.getText(), (Expression[])params.toArray(new Expression[params.size()]));
	}
	;

/**
 *  Access an element of an array.
 */
arrayAccess returns [Suffix suff]
	: '[' tmp = expression ']'
	{
		$suff = new ArrayAccess(tmp);
	}
	;

/**
 *  A variable represents a value provided from the outside.
 */
variable returns [Expression exp]
	: tmp = IDENTIFIER
	{
		String	name	= tmp.getText();
		Variable	var	= helper.getVariable(name);
		if(var==null)
		{
			throw new RuntimeException("No such variable: "+name);
		}
		$exp	= new VariableExpression(var);
	}
	;

/**
 *  A pseudo variable represents a value provided from the outside.
 */
pseudovariable returns [Expression exp]
	: tmp = IDENTIFIER '.' tmp2=IDENTIFIER
	{
		String name	= tmp.getText()+"."+tmp2.getText();
		Variable	var	= helper.getVariable(name);
		if(var==null)
			throw new RuntimeException("No such variable: "+name);
		$exp	= new VariableExpression(var);
	}
	;

literal returns [Expression exp]
	: tmp = floatingPointLiteral {$exp = tmp;}
	| tmp = integerLiteral {$exp = tmp;}
	| CharacterLiteral
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $CharacterLiteral.text;
		$exp = new LiteralExpression(text==null ? null : Character.valueOf(text.charAt(0)));
	}
	| StringLiteral
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $StringLiteral.text;
		$exp = new LiteralExpression(text==null ? null : text.substring(1, text.length()-1));
	}
	| BooleanLiteral
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $BooleanLiteral.text;
		$exp = new LiteralExpression(text==null ? null : text.equals("true")? Boolean.TRUE: Boolean.FALSE);
	}
	| 'null' {$exp = new LiteralExpression(null);}
	;

floatingPointLiteral returns [Expression exp]
	: FloatingPointLiteral
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $FloatingPointLiteral.text;
		$exp = new LiteralExpression(text==null ? null : Double.valueOf(text));
	}
	;

integerLiteral returns [Expression exp]
	: HexLiteral
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $HexLiteral.text;
		$exp = new LiteralExpression(text==null ? null : Integer.valueOf(text));
	}
	| OctalLiteral
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $OctalLiteral.text;
		$exp = new LiteralExpression(text==null ? null : Integer.valueOf(text));
	}
	| DecimalLiteral
	{
		// Auto-generated non-null check on literal produces scary findbugs warning when used inline.
		String	text	= $DecimalLiteral.text;
		$exp = new LiteralExpression(text==null ? null : Integer.valueOf(text));
	}
	;
/*
floatingPointLiteral returns [Expression exp]
	: sign=('+'|'-')? FloatingPointLiteral {$exp = new LiteralExpression(sign!=null && "-".equals(sign.getText())? new Double("-"+$FloatingPointLiteral.text): new Double($FloatingPointLiteral.text));}
	;

integerLiteral returns [Expression exp]
	: sign=('+'|'-')? (HexLiteral {$exp = new LiteralExpression(sign!=null && "-".equals(sign.getText())? new Integer("-"+$HexLiteral.text): new Integer($HexLiteral.text));}
	| OctalLiteral {$exp = new LiteralExpression((sign!=null && "-".equals(sign.getText())? new Integer("-"+$OctalLiteral.text): new Integer($OctalLiteral.text)));}
	| DecimalLiteral {$exp = new LiteralExpression(sign!=null && "-".equals(sign.getText())? new Integer("-"+$DecimalLiteral.text): new Integer($DecimalLiteral.text));})
	;
*/


// Lexxer

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
 	|   ('0'..'9')+ Exponent FloatTypeSuffix
 	|   ('0'..'9')+ Exponent
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
	
IDENTIFIER 
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
