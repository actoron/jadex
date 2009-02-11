package jadex.adapter.base.appdescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ApplicationType
{
	//-------- attributes --------
	
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
}
