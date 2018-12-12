package jadex.bdi.examples.blackjack;

/**
 * Card information.
 */
public class Card
{
	//-------- attributes ----------

	/** Attribute for slot col. */
	protected String col;

	/** Attribute for slot type. */
	protected String type;

	/** Attribute for slot val. */
	protected int val;

	//-------- constructors --------

	/**
	 * Default Constructor. <br>
	 * Create a new <code>Card</code>.
	 */
	public Card()
	{
		// Empty constructor required for JavaBeans (do not remove).
	}

	/**
	 * Default Constructor. <br>
	 * Create a new <code>Card</code>.
	 */
	public Card(String type, String color, int value)
	{
		setType(type);
		setColor(color);
		setValue(value);
	}

	/**
	 * Clone Constructor. <br>
	 * Create a new <code>Card</code>.<br>
	 * Copy all attributes from <code>proto</code> to this instance.
	 * @param proto The prototype instance.
	 */
	public Card(Card proto)
	{
		setColor(proto.getColor());
		setType(proto.getType());
		setValue(proto.getValue());
	}

	//-------- accessor methods --------

	/**
	 *  Get the col of this Card.
	 * @return col
	 */
	public String getColor()
	{
		return this.col;
	}

	/**
	 *  Set the col of this Card.
	 * @param col the value to be set
	 */
	public void setColor(String col)
	{
		this.col = col;
	}

	/**
	 *  Get the type of this Card.
	 * @return type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Set the type of this Card.
	 * @param type the value to be set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the val of this Card.
	 * @return val
	 */
	public int getValue()
	{
		return this.val;
	}

	/**
	 *  Set the val of this Card.
	 * @param val the value to be set
	 */
	public void setValue(int val)
	{
		this.val = val;
	}

	//-------- Object methods -----

	/**
	 * Get a string representation of this <code>Card</code>.
	 * @return The string representation.
	 */
	public String toString()
	{
		return getType() + " " + getColor();
	}

	/**
	 * Get a clone of this <code>Card</code>.
	 * @return a shalow copy of this instance.
	 */
	public Object clone()
	{
		return new Card(this);
	}

	/**
	 * Test the equality of this <code>Card</code>
	 * and an object <code>obj</code>.
	 * @param obj the object this test will be performed with
	 * @return false if <code>obj</code> is not of <code>Card</code> class,
	 *         true if all attributes are equal.
	 */
	public boolean equals(Object obj)
	{
		if(obj instanceof Card)
		{
			Card cmp = (Card)obj;
			if(getColor() == null || !getColor().equals(cmp.getColor()))
				return false;
			if(getType() == null || !getType().equals(cmp.getType()))
				return false;
			if(getValue() != cmp.getValue())
				return false;
			return true;
		}
		return false;
	}
	
	/** 
	 * 
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((col == null) ? 0 : col.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + val;
		return result;
	}
}
