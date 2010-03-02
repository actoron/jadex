package jadex.wfms.bdi.ontology;

import jadex.base.fipa.IComponentAction;

public class PerformHeartbeat implements IComponentAction
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
