package jadex.tools.ontology;


/**
 *  Java class for concept ElementAction of jadex.tools.introspector ontology.
 */
public class ElementAction extends ToolAction
{
	//-------- attributes ----------

	/** The scope of the element. */
	protected String	scope;

	/** The name of the element. */
	protected String	elementname;

	/** The type of the element
	(e.g. "goal"). */
	protected String	elementtype;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new ElementAction.
	 */
	public ElementAction()
	{
	}

	/**
	 *  Init Constructor.
	 *  Create a new ElementAction.
	 *  Initializes the object with required attributes.
	 * @param elementname
	 * @param elementtype
	 * @param tooltype
	 */
	public ElementAction(String elementname, String elementtype, String tooltype)
	{
		this();
		setElementName(elementname);
		setElementType(elementtype);
		setToolType(tooltype);
	}

	//-------- accessor methods --------

	/**
	 *  Get the scope of this ElementAction.
	 *  The scope of the element.
	 * @return scope
	 */
	public String getScope()
	{
		return this.scope;
	}

	/**
	 *  Set the scope of this ElementAction.
	 *  The scope of the element.
	 * @param scope the value to be set
	 */
	public void setScope(String scope)
	{
		this.scope = scope;
	}

	/**
	 *  Get the element-name of this ElementAction.
	 *  The name of the element.
	 * @return element-name
	 */
	public String getElementName()
	{
		return this.elementname;
	}

	/**
	 *  Set the element-name of this ElementAction.
	 *  The name of the element.
	 * @param elementname the value to be set
	 */
	public void setElementName(String elementname)
	{
		this.elementname = elementname;
	}

	/**
	 *  Get the element-type of this ElementAction.
	 *  The type of the element
	(e.g. "goal").
	 * @return element-type
	 */
	public String getElementType()
	{
		return this.elementtype;
	}

	/**
	 *  Set the element-type of this ElementAction.
	 *  The type of the element
	(e.g. "goal").
	 * @param elementtype the value to be set
	 */
	public void setElementType(String elementtype)
	{
		this.elementtype = elementtype;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this ElementAction.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ElementAction(" + "elementname=" + getElementName() + ", elementtype=" + getElementType() + ", tooltype=" + getToolType() + ")";
	}

}
