package jadex.tools.ontology;


/**
 *  Java class for concept ChangeAttribute of jadex.tools.introspector ontology.
 */
public class ChangeAttribute extends ElementAction
{
	//-------- attributes ----------

	/** The name of the attribute. */
	protected String	attributename;

	/** Attribute for slot value2. */
	protected String	value;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new ChangeAttribute.
	 */
	public ChangeAttribute()
	{
	}

	/**
	 *  Init Constructor.
	 *  Create a new ChangeAttribute.
	 *  Initializes the object with required attributes.
	 * @param attributename
	 * @param elementname
	 * @param elementtype
	 * @param tooltype
	 * @param value
	 */
	public ChangeAttribute(String attributename, String elementname, String elementtype, String tooltype, String value)
	{
		this();
		setAttributeName(attributename);
		setElementName(elementname);
		setElementType(elementtype);
		setToolType(tooltype);
		setValue(value);
	}

	//-------- accessor methods --------

	/**
	 *  Get the attribute-name of this ChangeAttribute.
	 *  The name of the attribute.
	 * @return attribute-name
	 */
	public String getAttributeName()
	{
		return this.attributename;
	}

	/**
	 *  Set the attribute-name of this ChangeAttribute.
	 *  The name of the attribute.
	 * @param attributename the value to be set
	 */
	public void setAttributeName(String attributename)
	{
		this.attributename = attributename;
	}

	/**
	 *  Get the value2 of this ChangeAttribute.
	 * @return value2
	 */
	public String getValue()
	{
		return this.value;
	}

	/**
	 *  Set the value2 of this ChangeAttribute.
	 * @param value the value to be set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this ChangeAttribute.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ChangeAttribute(" + "attributename=" + getAttributeName() + ", elementname=" + getElementName() + ", elementtype=" + getElementType() + ", tooltype=" + getToolType() + ", value="
				+ getValue() + ")";
	}

}
