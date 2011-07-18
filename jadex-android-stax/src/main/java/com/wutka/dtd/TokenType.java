package com.wutka.dtd;

/** Enumerated value representing the type of a token
 *
 * @author Mark Wutka
 * @version $Revision$ $Date$ by $Author$
 */
class TokenType
{
	public int value;
	public String name;

	public TokenType(int aValue, String aName)
	{
		value = aValue;
		name = aName;
	}

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof TokenType)) return false;

		TokenType other = (TokenType) o;
		if (other.value == value) return true;
		return false;
	}

	public int hashCode()
	{
		return name.hashCode();
	}
}
