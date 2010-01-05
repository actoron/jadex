package jadex.bdi.wfms.ontology;

import jadex.adapter.base.fipa.IAgentAction;

public class RequestAddProcess implements IAgentAction
{
	private String processPath;
	
	public RequestAddProcess()
	{
	}
	
	public RequestAddProcess(String processPath)
	{
		this.processPath = processPath;
	}
	
	public void setProcessPath(String processPath)
	{
		this.processPath = processPath;
	}
	
	public String getProcessPath()
	{
		return processPath;
	}
}
