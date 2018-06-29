package jadex.bridge.fipa;

import java.io.Serializable;

import jadex.bridge.service.types.df.IProperty;


/**
 *  Java class for concept Property of beanynizer_beans_fipa_default ontology.
 */
public class Property	implements IProperty, Serializable
{
	//-------- attributes ----------

	/** Attribute for slot value. */
	protected Object value;

	/** Attribute for slot name. */
	protected String name;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>Property</code>.
	 */
	public Property()
	{
	}

	/**
	 *  Init Constructor. <br>
	 *  Create a new Property.<br>
	 *  Initializes the object with required attributes.
	 * @param name
	 * @param value
	 */
	public Property(String name, Object value)
	{
		this();
		setName(name);
		setValue(value);
	}

	//-------- accessor methods --------

	/**
	 *  Get the value of this Property.
	 * @return value
	 */
	public Object getValue()
	{
		return this.value;
	}

	/**
	 *  Set the value of this Property.
	 * @param value the value to be set
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/**
	 *  Get the name of this Property.
	 * @return name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name of this Property.
	 * @param name the value to be set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this Property.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Property(" + "name=" + getName() + ", value=" + getValue() + ")";
	}

	/** 
	 * 
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/**
	 * 
	 */
	public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		if(obj instanceof Property)
		{
			Property that = (Property)obj;
			return this.getName().equals(that.getName()) && this.getValue().equals(that.getValue());
		}
		else
			return false;
	}
}
