grammar JadexJavaRules;

//options {k=2; backtrack=true; memoize=true;}

@header 
{
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
    
    protected JavaRulesContext context;
    public void	setContext(JavaRulesContext context)
    {
    	this.context	= context;
    }
}

// Parser

rhs
	: expression EOF
	;
	
expression
	: conditionalExpression
        //(assignmentOperator expression
        //)?
	;

conditionalExpression
	: conditionalOrExpression
    	('?' expression ':' conditionalExpression)?
	;

conditionalOrExpression
	: conditionalAndExpression
        ('||' conditionalAndExpression)*
	;

conditionalAndExpression 
	: inclusiveOrExpression
        ('&&' inclusiveOrExpression)*
	;

inclusiveOrExpression 
    	: exclusiveOrExpression
        ('|' exclusiveOrExpression)*
	;

exclusiveOrExpression 
	: andExpression
        ('^' andExpression)*
	;

andExpression
	: equalityExpression
        ('&' equalityExpression)*
	;

equalityExpression 
    	: instanceOfExpression
        ( ('==' 
	{
		System.out.println("Found: ==");
        }
        | '!=')
	{
		System.out.println("Found: !=");
	}		
            instanceOfExpression
        )*
	;

instanceOfExpression
	: relationalExpression
        ('instanceof' type)?
	;

relationalExpression
	: additiveExpression //shiftExpression
        (relationalOp additiveExpression //shiftExpression 
        )*
	;

relationalOp 
	:    '<' '='
	{
		System.out.println("Found: <=");
	}
	|    '>' '='
	{
		System.out.println("Found: >=");
	}
	|   '<'
	{
		System.out.println("Found: <");
	}
	|   '>'
	{
		System.out.println("Found: >");
	}
	;

/*shiftExpression 
    :   additiveExpression
        (shiftOp additiveExpression
        )*
    ;

shiftOp 
    :    '<' '<'
    |    '>' '>' '>'
    |    '>' '>'
    ;*/

additiveExpression
	: multiplicativeExpression
        (   
            (   '+'
            |   '-'
            )
            multiplicativeExpression
        )*
        ;

multiplicativeExpression
	: unaryExpression
        (   
            (   '*'
            |   '/'
            |   '%'
            )
            unaryExpression
        )*
	;

//parExpression 
 //   :   '(' expression ')'
  //  ;

expressionList 
	: expression
        (',' expression)*
	;

/**
 * NOTE: for '+' and '-', if the next token is int or long interal, then it's not a unary expression.
 *       it's a literal with signed value. INTLTERAL AND LONG LITERAL are added here for this.
 */
unaryExpression
	:   '+' unaryExpression
	|   '-' unaryExpression
	|   '++' unaryExpression
	|   '--' unaryExpression
	|   unaryExpressionNotPlusMinus
	;

unaryExpressionNotPlusMinus 
	:   //'~' unaryExpression
	//|   '!' unaryExpression
	//|   
	//castExpression
	//|
	primary
        (selector)*
        (   '++'
        |   '--'
        )?
	;

castExpression
	:
	 '(' primitiveType | type ')' unaryExpression
    	//:   '(' primitiveType ')' unaryExpression
    	//|   '(' type ')' unaryExpressionNotPlusMinus
	;

/**
 * have to use scope here, parameter passing isn't well supported in antlr.
 */
primary  
	:   //expression//parExpression            
	//|   
    	'this' /* ('.' IDENTIFIER)* */ (identifierSuffix)?
	| IDENTIFIER /* ('.' IDENTIFIER)* */ (identifierSuffix)?
	| 'super' superSuffix
	| literal
	| creator
	| primitiveType ('[' ']')* '.' 'class'
	| 'void' '.' 'class'
	;
    

superSuffix 
	: arguments
	| '.' (typeArguments)? IDENTIFIER (arguments)?
	;

identifierSuffix
	: ('[' ']')+
        '.' 'class'
	//|   ('[' expression ']')+
	| arguments
	| '.' 'class'
	// |   '.' nonWildcardTypeArguments IDENTIFIER arguments
	| '.' 'this'
	//|   '.' 'super' arguments
	// |   innerCreator
	;


selector
	: '.' IDENTIFIER (arguments)?
	| '.' 'this'
	| '.' 'super' superSuffix
	// |   innerCreator
	| '[' expression ']'
	;

creator 
	:   //'new' nonWildcardTypeArguments classOrInterfaceType classCreatorRest
	//|
	'new' classOrInterfaceType classCreatorRest
	//|   arrayCreator
	;


typeList 
	: type (',' type)*
	;

classCreatorRest  
	: arguments
        //(classBody
        //)?
	;

/*arrayCreator 
    :   'new' createdName
        '[' ']'
        ('[' ']'
        )*
        arrayInitializer

    |   'new' createdName
        '[' expression
        ']'
        (   '[' expression
            ']'
        )*
        ('[' ']'
        )*
    ;*/

variableInitializer 
	:   //arrayInitializer
	//|  
	expression
	;

/*arrayInitializer 
    :   '{' 
            (variableInitializer
                (',' variableInitializer
                )*
            )? 
            (',')? 
        '}'             //Yang's fix, position change.
    ;*/


createdName 
	: classOrInterfaceType
	| primitiveType
	;

/*innerCreator  
    :   '.' 'new'
        (nonWildcardTypeArguments
        )?
        IDENTIFIER
        (typeArguments
        )?
        classCreatorRest
    ;*/


/*classCreatorRest 
    :   arguments
        (classBody
        )?
    ;*/


/*nonWildcardTypeArguments 
    :   '<' typeList
        '>'
    ;*/

arguments 
	: '(' (expressionList)? ')'
	;

type 
	: classOrInterfaceType ('[' ']' )*
	|   primitiveType ('[' ']')*
	;


classOrInterfaceType 
	: IDENTIFIER (typeArguments)? ('.' IDENTIFIER (typeArguments)?)*
	;

typeArguments 
	: '<' typeArgument (',' typeArgument)* '>'
	;

typeArgument 
	:   type
	|   '?'
        (
            ('extends'
            |'super'
            )
            type
        )?
	;


primitiveType  
	:   'boolean'
	|   'char'
	|   'byte'
	|   'short'
	|   'int'
	|   'long'
	|   'float'
	|   'double'
	;

literal 
	:   INTLITERAL
	|   LONGLITERAL
	|   FLOATLITERAL
	|   DOUBLELITERAL
	|   CHARLITERAL
	|   STRINGLITERAL
	|   TRUE
	|   FALSE
	|   NULL
 	;

// Lexxer
/********************************************************************************************
                  Lexer section
*********************************************************************************************/

LONGLITERAL
    :   IntegerNumber LongSuffix
    ;

    
INTLITERAL
    :   IntegerNumber 
    ;
    
fragment
IntegerNumber
    :   '0' 
    |   '1'..'9' ('0'..'9')*    
    |   '0' ('0'..'7')+         
    |   HexPrefix HexDigit+        
    ;

fragment
HexPrefix
    :   '0x' | '0X'
    ;

fragment
LongSuffix
    :   'l' | 'L'
    ;


fragment
NonIntegerNumber
    :   ('0' .. '9')+ '.' ('0' .. '9')* Exponent?  
    |   '.' ( '0' .. '9' )+ Exponent?  
    |   ('0' .. '9')+ Exponent  
    |   ('0' .. '9')+ 
    |   
        HexPrefix (HexDigit )* 
        (    () 
        |    ('.' (HexDigit )* ) 
        ) 
        ( 'p' | 'P' ) 
        ( '+' | '-' )? 
        ( '0' .. '9' )+
        ;
    
fragment 
FloatSuffix
    :   'f' | 'F' 
    ;     

fragment
DoubleSuffix
    :   'd' | 'D'
    ;
        
FLOATLITERAL
    :   NonIntegerNumber FloatSuffix
    ;
    
DOUBLELITERAL
    :   NonIntegerNumber DoubleSuffix?
    ;

CHARLITERAL
    :   '\'' 
        (   EscapeSequence 
        |   ~( '\'' | '\\' | '\r' | '\n' )
        ) 
        '\''
    ; 

STRINGLITERAL
    :   '"' 
        (   EscapeSequence
        |   ~( '\\' | '"' | '\r' | '\n' )        
        )* 
        '"' 
    ;
        
ABSTRACT
    :   'abstract'
    ;
    
ASSERT
    :   'assert'
    ;
    
BOOLEAN
    :   'boolean'
    ;
    
BREAK
    :   'break'
    ;
    
BYTE
    :   'byte'
    ;
    
CASE
    :   'case'
    ;
    
CATCH
    :   'catch'
    ;
    
CHAR
    :   'char'
    ;
    
CLASS
    :   'class'
    ;
    
CONST
    :   'const'
    ;

CONTINUE
    :   'continue'
    ;

DEFAULT
    :   'default'
    ;

DO
    :   'do'
    ;

DOUBLE
    :   'double'
    ;

ELSE
    :   'else'
    ;

ENUM
    :   'enum'
    ;             

EXTENDS
    :   'extends'
    ;

FINAL
    :   'final'
    ;

FINALLY
    :   'finally'
    ;

FLOAT
    :   'float'
    ;

FOR
    :   'for'
    ;

GOTO
    :   'goto'
    ;

IF
    :   'if'
    ;

IMPLEMENTS
    :   'implements'
    ;

IMPORT
    :   'import'
    ;

INSTANCEOF
    :   'instanceof'
    ;

INT
    :   'int'
    ;

INTERFACE
    :   'interface'
    ;

LONG
    :   'long'
    ;

NATIVE
    :   'native'
    ;

NEW
    :   'new'
    ;

PACKAGE
    :   'package'
    ;

PRIVATE
    :   'private'
    ;

PROTECTED
    :   'protected'
    ;

PUBLIC
    :   'public'
    ;

RETURN
    :   'return'
    ;

SHORT
    :   'short'
    ;

STATIC
    :   'static'
    ;

STRICTFP
    :   'strictfp'
    ;

SUPER
    :   'super'
    ;

SWITCH
    :   'switch'
    ;

SYNCHRONIZED
    :   'synchronized'
    ;

THIS
    :   'this'
    ;

THROW
    :   'throw'
    ;

THROWS
    :   'throws'
    ;

TRANSIENT
    :   'transient'
    ;

TRY
    :   'try'
    ;

VOID
    :   'void'
    ;

VOLATILE
    :   'volatile'
    ;

WHILE
    :   'while'
    ;

TRUE
    :   'true'
    ;

FALSE
    :   'false'
    ;

NULL
    :   'null'
    ;

LPAREN
    :   '('
    ;

RPAREN
    :   ')'
    ;

LBRACE
    :   '{'
    ;

RBRACE
    :   '}'
    ;

LBRACKET
    :   '['
    ;

RBRACKET
    :   ']'
    ;

SEMI
    :   ';'
    ;

COMMA
    :   ','
    ;

DOT
    :   '.'
    ;

ELLIPSIS
    :   '...'
    ;

EQ
    :   '='
    ;

BANG
    :   '!'
    ;

TILDE
    :   '~'
    ;

QUES
    :   '?'
    ;

COLON
    :   ':'
    ;

EQEQ
    :   '=='
    ;

AMPAMP
    :   '&&'
    ;

BARBAR
    :   '||'
    ;

PLUSPLUS
    :   '++'
    ;

SUBSUB
    :   '--'
    ;

PLUS
    :   '+'
    ;

SUB
    :   '-'
    ;

STAR
    :   '*'
    ;

SLASH
    :   '/'
    ;

AMP
    :   '&'
    ;

BAR
    :   '|'
    ;

CARET
    :   '^'
    ;

PERCENT
    :   '%'
    ;

PLUSEQ
    :   '+='
    ; 
    
SUBEQ
    :   '-='
    ;

STAREQ
    :   '*='
    ;

SLASHEQ
    :   '/='
    ;

AMPEQ
    :   '&='
    ;

BAREQ
    :   '|='
    ;

CARETEQ
    :   '^='
    ;

PERCENTEQ
    :   '%='
    ;

MONKEYS_AT
    :   '@'
    ;

BANGEQ
    :   '!='
    ;

GT
    :   '>'
    ;

LT
    :   '<'
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
	|   ('0'..'9')+ Exponent? FloatTypeSuffix
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

