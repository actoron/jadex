package jadex.wfms.bdi.ontology;

import jadex.base.fipa.IComponentAction;

public class RequestProcessStart implements IComponentAction
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
