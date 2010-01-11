package jadex.wfms.bdi.ontology;

import jadex.adapter.base.fipa.IAgentAction;

public class PerformHeartbeat implements IAgentAction
{
	private boolean performed;
	
	public PerformHeartbeat()
	{
	}
	
	public PerformHeartbeat(boolean performed)
	{
		this.performed = performed;
	}
	
	public boolean isPerformed()
	{
		return performed;
	}
	
	public void setPerformed(boolean performed)
	{
		this.performed = performed;
	}
}
