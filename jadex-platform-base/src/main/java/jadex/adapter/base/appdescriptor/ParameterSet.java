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
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public List getValues()
	{
		return this.values;
	}

	/**
	 * @param value the value to add
	 */
	public void addValue(String value)
	{
		this.values.add(value);
	}

}
