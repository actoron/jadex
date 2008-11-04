package jadex.tools.ontology;

import java.util.ArrayList;
import java.util.List;


/**
 *  Java class for concept PerformAction of jadex.tools.introspector ontology.
 */
public class PerformAction extends ElementAction
{
	//-------- attributes ----------

	/** The name of the operation to invoke. */
	protected String	actionname;

	/** Attribute for slot action-arguments. */
	protected List		arguments;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new PerformAction.
	 */
	public PerformAction()
	{
		this.arguments = new ArrayList();
	}

	/**
	 *  Init Constructor. <br>
	 *  Create a new PerformAction.<br>
	 *  Initializes the object with required attributes.
	 * @param actionname
	 * @param elementname
	 * @param elementtype
	 * @param tooltype
	 */
	public PerformAction(String actionname, String elementname, String elementtype, String tooltype)
	{
		this();
		setActionName(actionname);
		setElementName(elementname);
		setElementType(elementtype);
		setToolType(tooltype);
	}

	//-------- accessor methods --------

	/**
	 *  Get the action-name of this PerformAction.
	 *  The name of the operation to invoke.
	 * @return action-name
	 */
	public String getActionName()
	{
		return this.actionname;
	}

	/**
	 *  Set the action-name of this PerformAction.
	 *  The name of the operation to invoke.
	 * @param actionname the value to be set
	 */
	public void setActionName(String actionname)
	{
		this.actionname = actionname;
	}

	/**
	 *  Get the action-arguments of this PerformAction.
	 * @return action-arguments
	 */
	public String[] getArguments()
	{
		return (String[])arguments.toArray(new String[arguments.size()]);
	}

	/**
	 *  Set the action-arguments of this PerformAction.
	 * @param arguments the value to be set
	 */
	public void setArguments(String[] arguments)
	{
		this.arguments.clear();
		for(int i = 0; i < arguments.length; i++)
			this.arguments.add(arguments[i]);
	}

	/**
	 *  Get an action-arguments of this PerformAction.
	 *  @param idx The index.
	 *  @return action-arguments
	 */
	public String getArgument(int idx)
	{
		return (String)this.arguments.get(idx);
	}

	/**
	 *  Set a action-argument to this PerformAction.
	 *  @param idx The index.
	 *  @param argument a value to be added
	 */
	public void setArgument(int idx, String argument)
	{
		this.arguments.set(idx, argument);
	}

	/**
	 *  Add a action-argument to this PerformAction.
	 *  @param argument a value to be removed
	 */
	public void addArgument(String argument)
	{
		this.arguments.add(argument);
	}

	/**
	 *  Remove a action-argument from this PerformAction.
	 *  @param argument a value to be removed
	 *  @return  True when the action-arguments have changed.
	 */
	public boolean removeArgument(String argument)
	{
		return this.arguments.remove(argument);
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this PerformAction.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "PerformAction(" + "actionname=" + getActionName() + ", elementname=" + getElementName() + ", elementtype=" + getElementType() + ", tooltype=" + getToolType() + ")";
	}

}
