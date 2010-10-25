package jadex.application.model;

import jadex.javaparser.IParsedExpression;

import java.util.ArrayList;
import java.util.List;

/**
 *  Component instance representation. 
 */
public class MComponentInstance
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The type name. */
	protected String typename;
	
	/** The configuration. */
	protected String configuration;

	/** The start flag. */
	protected boolean start;
	
	/** The number of components. */
	protected IParsedExpression number;
	
	/** The master flag. */
	protected boolean master;
	
	/** The suspended flag. */
	protected boolean suspended;
	
	/** The daemon flag. */
	protected boolean daemon;
	
	/** The list of contained arguments. */
	protected List arguments;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component.
	 */
	public MComponentInstance()
	{
		this.arguments = new ArrayList();
		this.start = true;
//		this.number = 1;
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
	 *  Get the type name.
	 *  @return The type name.
	 */
	public String getTypeName()
	{
		return this.typename;
	}

	/**
	 *  Set the type name.
	 *  @param type The type name to set.
	 */
	public void setTypeName(String typename)
	{
		this.typename = typename;
	}

	/**
	 *  Get the configuration.
	 *  @return The configuration.
	 */
	public String getConfiguration()
	{
		return this.configuration;
	}

	/**
	 *  Set the configuration.
	 *  @param configuration The configuration to set.
	 */
	public void setConfiguration(String configuration)
	{
		this.configuration = configuration;
	}
	
	/**
	 *  Test if component should be started (not only created).
	 *  @return True, if should be started.
	 */
	public boolean isStart()
	{
		return this.start;
	}

	/**
	 *  Set if the component should also be started.
	 *  @param start The start flag to set.
	 */
	public void setStart(boolean start)
	{
		this.start = start;
	}
	
	/**
	 *  Get the master flag.
	 *  @return True, if master.
	 */
	public boolean isMaster()
	{
		return this.master;
	}

	/**
	 *  Set the master flag..
	 *  @param start The master flag.
	 */
	public void setMaster(boolean master)
	{
		this.master = master;
	}
	
	/**
	 *  Get the daemon.
	 *  @return The daemon.
	 */
	public boolean isDaemon()
	{
		return this.daemon;
	}

	/**
	 *  Set the daemon.
	 *  @param daemon The daemon to set.
	 */
	public void setDaemon(boolean daemon)
	{
		this.daemon = daemon;
	}

	/**
	 *  Get the suspended.
	 *  @return The suspended.
	 */
	public boolean isSuspended()
	{
		return this.suspended;
	}

	/**
	 *  Set the suspended.
	 *  @param suspended The suspended to set.
	 */
	public void setSuspended(boolean suspended)
	{
		this.suspended = suspended;
	}

	/**
	 *  Set the number text.
	 *  @param numbertext The number text.
	 */
	public void setNumber(IParsedExpression number)
	{
		this.number = number;
	}
	
	/**
	 *  Get the number (expression).
	 *  @return The number.
	 */
	public IParsedExpression getNumber()
	{
		return this.number;
	}
	
	/**
	 *  Get the number of components to start.
	 *  @return The number.
	 * /
	public int getNumber()
	{
		return this.number;
	}*/

	/**
	 *  Set the number of components.
	 *  @param number The number to set.
	 * /
	public void setNumber(int number)
	{
//		this.number = number;
	}*/

	/**
	 *  Add an argument.
	 *  @param arg The argument.
	 */
	public void addArgument(MExpressionType arg)
	{
		this.arguments.add(arg);
	}

	/**
	 *  Get the list of arguments.
	 *  @return The arguments.
	 */
	public List getArguments()
	{
		return this.arguments;
	}
	
	/**
	 *  Get the model of the component instance.
	 *  @param apptype The application type this component is used in.
	 *  @return The name of the component type.
	 */
	public MComponentType getType(MApplicationType apptype)
	{
		MComponentType ret = null;
		List componenttypes = apptype.getMComponentTypes();
		for(int i=0; ret==null && i<componenttypes.size(); i++)
		{
			MComponentType at = (MComponentType)componenttypes.get(i);
			if(at.getName().equals(getTypeName()))
				ret = at;
		}
		return ret;
	}
	
	/**
	 *  A string of this object.
	 */
	public String toString()
	{
		return "MComponentInstance(typename="+typename+
			(number!=null ? ", number="+number.getExpressionText(): "")+")";
	}
}
