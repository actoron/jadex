package jadex.adapter.base.appdescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 *  Application type representation.
 */
public class ApplicationType
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The list of contained structuring types. */
	protected List structuringtypes;
	
	/** The list of contained agent types. */
	protected List agenttypes;
	
	/** The list of contained application descriptions. */
	protected List applications;
	
	//-------- constructors --------
	
	/**
	 *  Create a new application type.
	 */
	public ApplicationType()
	{
		this.structuringtypes = new ArrayList();
		this.agenttypes = new ArrayList();
		this.applications = new ArrayList();
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
	 *  Add an agent type.
	 *  @param agenttype The agent type.
	 */
	public void addAgentType(AgentType agenttype)
	{
		this.agenttypes.add(agenttype);
	}
	
	/**
	 *  Add a structuring type.
	 *  @param agenttype The structuring type.
	 */
	public void addStructuringType(StructuringType structuringtype)
	{
		this.structuringtypes.add(structuringtype);
	}

	/**
	 *  Add an application.
	 *  @param application The application.
	 */
	public void addApplication(Application application)
	{
		this.applications.add(application);
	}

	/**
	 *  Get the agenttypes.
	 *  @return The agenttypes.
	 */
	public List getAgentTypes()
	{
		return this.agenttypes;
	}
	
	/**
	 *  Get the structuring types.
	 *  @return The structuringtypes.
	 */
	public List getStructuringTypes()
	{
		return this.structuringtypes;
	}

	/**
	 *  Get a named agenttype.
	 *  @return The agenttype (if any).
	 */
	public AgentType getAgentType(String name)
	{
		AgentType	ret	= null;
		for(int i=0; ret==null && i<agenttypes.size(); i++)
		{
			AgentType	at	= (AgentType)agenttypes.get(i);
			if(at.getName().equals(name))
				ret	= at;
		}
		return ret;
	}

	/**
	 *  Get the applications.
	 *  @return The applications.
	 */
	public List getApplications()
	{
		return this.applications;
	}
	
	
}
