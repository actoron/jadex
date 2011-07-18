package com.wutka.dtd;

/** Token returned by the lexical scanner
 *
 * @author Mark Wutka
 * @version $Revision$ $Date$ by $Author$
 */
class Token
{
	public TokenType type;
	public String value;

	public Token(TokenType aType)
	{
		type = aType;
		value = null;
	}

	public Token(TokenType aType, String aValue)
	{
		type = aType;
		value = aValue;
	}
}
