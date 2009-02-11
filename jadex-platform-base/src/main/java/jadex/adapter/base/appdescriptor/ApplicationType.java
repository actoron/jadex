package jadex.adapter.base.appdescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ApplicationType
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The list of contained agent types. */
	protected List agenttypes;
	
	/** The list of contained application descriptions. */
	protected List applications;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public ApplicationType()
	{
		this.agenttypes = new ArrayList();
		this.applications = new ArrayList();
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
	 * 
	 */
	public void addAgentType(AgentType agenttype)
	{
		this.agenttypes.add(agenttype);
	}

	/**
	 * 
	 */
	public void addApplication(Application application)
	{
		this.applications.add(application);
	}

	/**
	 * @return the agenttypes
	 */
	public List getAgentTypes()
	{
		return this.agenttypes;
	}

	/**
	 * @return the applications
	 */
	public List getApplications()
	{
		return this.applications;
	}
}
