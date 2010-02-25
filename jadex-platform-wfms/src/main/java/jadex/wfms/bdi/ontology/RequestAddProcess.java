package jadex.wfms.bdi.ontology;

import jadex.adapter.base.fipa.IComponentAction;

public class RequestAddProcess implements IComponentAction
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
