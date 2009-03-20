grammar JavaJadex;

//options {k=2; backtrack=true; memoize=true;}

@header 
{
package jadex.rules.parser.conditions.javagrammar;
}

@lexer::header 
{
package jadex.rules.parser.conditions.javagrammar;
}

@members
{
	/** The stack of elements generated during parsing. */
	protected List	stack	= new ArrayList();
	
	/**
	 *  Get the elements from the stack.
	 */
	public List	getStack()
	{
		return stack;
	}
}

// Parser

/**
 *  Left hand side. Start rule for parser.
 */
lhs
	: expression EOF
	;

/**
 *  An expression is some Java code that can be evaluated to
 *  a value (left hand side of an assignment).
 */
expression
	: logicalAndExpression
	;


/**
 *  An equality comparison between two values.
 */
logicalAndExpression
	: equalityExpression
        (
        	'&''&' equalityExpression
        )*
	;
	
/**
 *  An equality comparison between two values.
 */
equalityExpression
	: relationalExpression
        (
		{
			String	operator	= null;
		}
	        ('=''=' {operator="==";}
        	|'!''=' {operator="!=";}
        	) relationalExpression
	        {
	        	// Pop values from stack and add constraint.
	        	UnaryExpression	right	= (UnaryExpression)stack.remove(stack.size()-1);
	        	UnaryExpression	left	= (UnaryExpression)stack.remove(stack.size()-1);
	        	stack.add(new Constraint(left, right, operator));
	        }
	)?
	;

/**
 *  A comparison between two values.
 */
relationalExpression
	: unaryExpression
        (
		{
			String	operator	= null;
		}
        	('<' {operator="<";}
        	|'<''=' {operator="<=";}
        	|'>' {operator=">";}
        	|'>''=' {operator=">=";}
        	) unaryExpression
	        {
	        	// Pop values from stack and add constraint.
	        	UnaryExpression	right	= (UnaryExpression)stack.remove(stack.size()-1);
	        	UnaryExpression	left	= (UnaryExpression)stack.remove(stack.size()-1);
	        	stack.add(new Constraint(left, right, operator));
	        }
        )?
	;
	
/**
 *  An unary expression produces a single value
 */
unaryExpression
	:
	primary
	(suffix
	)*
	{
		List	suffs	= null;
		while(stack.get(stack.size()-1) instanceof Suffix)
		{
			if(suffs==null)
				suffs	= new ArrayList();
			suffs.add(0, stack.remove(stack.size()-1));
		}
		Primary	prim	= (Primary)stack.remove(stack.size()-1);
		stack.add(new UnaryExpression(prim, suffs==null ? null
			: (Suffix[])suffs.toArray(new Suffix[suffs.size()])));
	}
	;

/**
 *  Primary part of a expression, i.e. a direct representation of a value.
 */
primary
	: /*'(' expression ')'	// Todo
	|*/ literal
	| variable
	;

/**
 *  Continuations on a value, i.e. field or method access.
 */
suffix
	: fieldAccess
	| methodAccess
	;

/**
 *  Read a field of an object.
 */
fieldAccess
	: '.' tmp = IDENTIFIER {stack.add(new FieldAccess(tmp.getText()));}
	;

/**
 *  Invoke a method on an object.
 */
methodAccess
	: '.' tmp = IDENTIFIER '(' ')' {stack.add(new MethodAccess(tmp.getText(), null));}
	| '.' tmp = IDENTIFIER '(' unaryExpression	// Todo: expression
	(',' unaryExpression	// Todo: expression
	)* ')'
	{
		List	parexs	= null;
		while(stack.get(stack.size()-1) instanceof UnaryExpression)
		{
			if(parexs==null)
				parexs	= new ArrayList();
			parexs.add(0, stack.remove(stack.size()-1));
		}
		stack.add(new MethodAccess(tmp.getText(), (UnaryExpression[])parexs.toArray(new UnaryExpression[parexs.size()])));
	}
	;

/**
 *  A variable represents a value provided from the outside.
 */
variable
	: tmp = IDENTIFIER {stack.add(new Variable(tmp.getText()));}
	;

literal
	: lit=floatingPointLiteral
	| lit=integerLiteral
	| CharacterLiteral {stack.add(new Literal(new Character($CharacterLiteral.text.charAt(0))));}
	| StringLiteral {stack.add(new Literal($StringLiteral.text.substring(1, $StringLiteral.text.length()-1)));}
	| BooleanLiteral {stack.add(new Literal($BooleanLiteral.text.equals("true")? Boolean.TRUE: Boolean.FALSE));}
	| 'null' {stack.add(new Literal(null));}
	;

floatingPointLiteral
	: sign=('+'|'-')? FloatingPointLiteral {stack.add(new Literal(sign!=null && "-".equals(sign.getText())? new Double("-"+$FloatingPointLiteral.text): new Double($FloatingPointLiteral.text)));}
	;

integerLiteral
	 returns [Object val]
	: sign=('+'|'-')? (HexLiteral {stack.add(new Literal(sign!=null && "-".equals(sign.getText())? new Integer("-"+$HexLiteral.text): new Integer($HexLiteral.text)));}
	| OctalLiteral {stack.add(new Literal((sign!=null && "-".equals(sign.getText())? new Integer("-"+$OctalLiteral.text): new Integer($OctalLiteral.text))));}
	| DecimalLiteral {stack.add(new Literal(sign!=null && "-".equals(sign.getText())? new Integer("-"+$DecimalLiteral.text): new Integer($DecimalLiteral.text)));})
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
