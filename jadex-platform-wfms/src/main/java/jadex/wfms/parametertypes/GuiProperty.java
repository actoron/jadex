package jadex.wfms.parametertypes;


/**
 * A class representing a GUI-property of a parameter.
 */
public class GuiProperty
{
	/** Name of the property */
	private String name;
	
	/** Value of the property */
	private Object value;
	
	/**
	 * Creates a new GUI-property.
	 * @param name name of the property
	 * @param value value of the property
	 */
	public GuiProperty(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Returns the name of the GUI-property
	 * @return the name of the property
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the value of the GUI-property
	 * @return the value of the property
	 */
	public Object getValue()
	{
		return value;
	}
}
