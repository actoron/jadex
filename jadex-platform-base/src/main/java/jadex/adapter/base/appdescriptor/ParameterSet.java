package jadex.adapter.base.appdescriptor;

import java.util.ArrayList;
import java.util.List;

public class ParameterSet
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The values. */
	protected List values;

	//-------- constructors --------

	/**
	 * 
	 */
	public ParameterSet()
	{
		this.values = new ArrayList();
	}

	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the values.
	 *  @return The values.
	 */
	public List getValues()
	{
		return this.values;
	}

	/**
	 *  Add a value.
	 *  @param value The values to add.
	 */
	public void addValue(String value)
	{
		this.values.add(value);
	}

}
