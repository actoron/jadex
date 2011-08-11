package jadex.simulation.analysis.common.events.service;

import jadex.simulation.analysis.common.events.AEvent;
import jadex.simulation.analysis.common.events.data.IADataObservable;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;

public class AServiceEvent extends AEvent
{
protected Object newValue; 
	
	@Override
	public String getEventType()
	{
		return AConstants.SERVICE_EVENT;
	}
    
    public AServiceEvent(IAnalysisService source, String eventCommand, Object newValue)
    {
    	super(source, eventCommand);
    	this.newValue = newValue;
    }
    
    public Object getValue()
	{
		return newValue;
	}
}
