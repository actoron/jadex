package jadex.wfms.bdi.ontology;

import jadex.adapter.base.fipa.IAgentAction;

public class RequestProcessStart implements IAgentAction
{
	private String processName;
	
	public RequestProcessStart()
	{
	}
	
	public RequestProcessStart(String processName)
	{
		this.processName = processName;
	}
	
	public void setProcessName(String processName)
	{
		this.processName = processName;
	}
	
	public String getProcessName()
	{
		return processName;
	}
}
