grammar JavaJadex;

//options {k=2; backtrack=true; memoize=true;}

@header 
{
package jadex.rules.parser.conditions.javagrammar;

import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.functions.IFunction;
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
        		$exp = new OperationExpression(tmp, tmp2, OperationExpression.OPERATOR_OR);
        	}
        )?
	;
	
/**
 *  An equality comparison between two values.
 */
logicalAndExpression returns [Expression exp]
	: tmp = equalityExpression {$exp = tmp;}
        (
        	'&&' tmp2 = equalityExpression
        	{
        		$exp = new OperationExpression(tmp, tmp2, OperationExpression.OPERATOR_AND);
        	}
        )?
	;
	
/**
 *  An equality comparison between two values.
 */
equalityExpression returns [Expression exp]
	: tmp = relationalExpression {$exp = tmp;}
        (
		{
			IOperator	operator	= null;
		}
	        ('==' {operator=IOperator.EQUAL;}
        	|'!=' {operator=IOperator.NOTEQUAL;}
        	) tmp2 = relationalExpression
	        {
	        	$exp = new OperationExpression(tmp, tmp2, operator);
	        }
	)?
	;

/**
 *  A comparison between two values.
 */
relationalExpression returns [Expression exp]
	: tmp = additiveExpression {$exp = tmp;}
        (
		{
			IOperator	operator	= null;
		}
        	('<' {operator=IOperator.LESS;}
        	|'<=' {operator=IOperator.LESSOREQUAL;}
        	|'>' {operator=IOperator.GREATER;}
        	|'>=' {operator=IOperator.GREATEROREQUAL;}
        	) tmp2 = additiveExpression
	        {
	        	$exp = new OperationExpression(tmp, tmp2, operator);
	        }
        )?
	;

/**
 *  An additive expression adds or subtracts two values.
 */
additiveExpression returns [Expression exp]
	: tmp = multiplicativeExpression {$exp = tmp;}
        (
		{
			IFunction	operator	= null;
		}
	        ('+' {operator=IFunction.SUM;}
        	|'-' {operator=IFunction.SUB;}
        	) tmp2 = multiplicativeExpression
	        {
	        	$exp = new OperationExpression(tmp, tmp2, operator);
	        }
	)?
	;

/**
 *  A multiplicative expression multiplies or divides two values.
 */
multiplicativeExpression returns [Expression exp]
	: tmp = unaryExpression {$exp = tmp;}
        (
		{
			IFunction	operator	= null;
		}
	        ('*' {operator=IFunction.MULT;}
        	|'/' {operator=IFunction.DIV;}
        	|'%' {operator=IFunction.MOD;}
        	) tmp2 = unaryExpression
	        {
	        	$exp = new OperationExpression(tmp, tmp2, operator);
	        }
	)?
	;
	
/**
 *  An unary expression produces a single value
 */
unaryExpression returns [Expression exp]
	:
	tmp = primary {List suffs = null;}
	(tmp2 = suffix
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
primary returns [Expression exp]
	: '(' tmp = expression ')' {$exp = tmp;}
	| tmp = literal {$exp = tmp;}
	| {helper.isPseudoVariable(JavaJadexParser.this.input.LT(1).getText())}? tmp = pseudovariable {$exp = tmp;}
	| tmp = variable {$exp = tmp;}
	;

/**
 *  Continuations on a value, i.e. field or method access.
 */
suffix returns [Suffix suff]
	: tmp = fieldAccess {$suff = tmp;}
	| tmp = methodAccess {$suff = tmp;}
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
	: '.' tmp1 = IDENTIFIER '(' ')' {$suff = new MethodAccess(tmp1.getText(), null);}
	| '.' tmp2 = IDENTIFIER '(' p1 = expression
	{
		List params	= new ArrayList();
		params.add(p1);
	}
	(',' p2 = expression {params.add(p2);}
	)* ')'
	{
		$suff	= new MethodAccess(tmp2.getText(), (Expression[])params.toArray(new Expression[params.size()]));
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
			throw new RuntimeException("No such variable: "+name);
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
	| CharacterLiteral {$exp = new LiteralExpression(new Character($CharacterLiteral.text.charAt(0)));}
	| StringLiteral {$exp = new LiteralExpression($StringLiteral.text.substring(1, $StringLiteral.text.length()-1));}
	| BooleanLiteral {$exp = new LiteralExpression($BooleanLiteral.text.equals("true")? Boolean.TRUE: Boolean.FALSE);}
	| 'null' {$exp = new LiteralExpression(null);}
	;

floatingPointLiteral returns [Expression exp]
	: sign=('+'|'-')? FloatingPointLiteral {$exp = new LiteralExpression(sign!=null && "-".equals(sign.getText())? new Double("-"+$FloatingPointLiteral.text): new Double($FloatingPointLiteral.text));}
	;

integerLiteral returns [Expression exp]
	: sign=('+'|'-')? (HexLiteral {$exp = new LiteralExpression(sign!=null && "-".equals(sign.getText())? new Integer("-"+$HexLiteral.text): new Integer($HexLiteral.text));}
	| OctalLiteral {$exp = new LiteralExpression((sign!=null && "-".equals(sign.getText())? new Integer("-"+$OctalLiteral.text): new Integer($OctalLiteral.text)));}
	| DecimalLiteral {$exp = new LiteralExpression(sign!=null && "-".equals(sign.getText())? new Integer("-"+$DecimalLiteral.text): new Integer($DecimalLiteral.text));})
	;



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
