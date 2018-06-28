grammar Clips;

//options {k=2; backtrack=true; memoize=true;}

// ?z <- (Block (color ?c & :(?c ~"red")))
// (Numberbox (numbers $?c1 ?x ?y $?c2))
// (client (city ~Bangor&~Portland))

// Parser

rhs	: ce+ EOF;

// Conditional elements
ce	: andce | notce | testce | objectce;

andce	: '(' 'and' ce+ ')';

notce 	: '(' 'not' ce ')';

testce	: '(' 'test' functionCall ')';

// Introduced '=' as alternative for XML, as < cannot be used
objectce: (singleFieldVariable ('<-' | '='))? '(' typename attributeConstraint+ ')';

// Constraints
attributeConstraint	
	: '(' slotname constraint ')'; 

constraint	
	: '?' 
	| '$?' 
	| singleConstraint (ConstraintOperator? singleConstraint)*;

singleConstraint	
	: literalConstraint
	| boundConstraint
	| predicateConstraint
	| returnValueConstraint
	;

literalConstraint
	: Operator? constant; 
	
boundConstraint
	: Operator? variable;
	
multiBoundConstraint
	: Operator? variable variable+;
	
predicateConstraint
	: ':' (functionCall | operatorCall);
	
returnValueConstraint
	: '=' (functionCall | operatorCall);

// Helper stuff
functionCall	
	: '(' functionName expression* ')';

operatorCall	
	: '(' variable Operator expression ')';

expression	
	: constant | variable | functionCall;

constant: literal;

singleFieldVariable	
	: '?' Identifier;

multiFieldVariable	
	: '$?' Identifier;

variable 	
	: singleFieldVariable | multiFieldVariable;

typename: Identifier;

slotname: Identifier;

functionName
	: Identifier;


literal	
	: integerLiteral
	| FloatingPointLiteral
	| CharacterLiteral
	| StringLiteral
	| BooleanLiteral
	| 'null'
	;

integerLiteral
	: HexLiteral
	| OctalLiteral
	| DecimalLiteral
	;

// Lexxer

Operator
	: '==' | '!=' | '>' | '<' | '>=' | '<=' | 'contains' | 'memberof' | '~';

ConstraintOperator	
	: '&' | '|';

BooleanLiteral
	:   'true' | 'false';

HexLiteral 
	: '0' ('x'|'X') HexDigit+ IntegerTypeSuffix?;

DecimalLiteral 
	: ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix?;

OctalLiteral 
	: '0' ('0'..'7')+ IntegerTypeSuffix?;

fragment
HexDigit: ('0'..'9'|'a'..'f'|'A'..'F');

fragment
IntegerTypeSuffix 
	: ('l'|'L');

FloatingPointLiteral
    	:   ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
 	|   '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
 	|   ('0'..'9')+ Exponent FloatTypeSuffix?
	|   ('0'..'9')+ Exponent? FloatTypeSuffix
	;

fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
FloatTypeSuffix 
	: ('f'|'F'|'d'|'D') ;

CharacterLiteral
	:   '\'' ( EscapeSequence | ~('\''|'\\') ) '\'';

StringLiteral
	:  '"' ( EscapeSequence | ~('\\'|'"') )* '"';

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
	:   '\\' 'u' HexDigit HexDigit HexDigit HexDigit;
	
Identifier 
	:   Letter (Letter|JavaIDDigit)*;

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

WS 	:  (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;};

COMMENT
	:   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;};

LINE_COMMENT
	: '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;};

