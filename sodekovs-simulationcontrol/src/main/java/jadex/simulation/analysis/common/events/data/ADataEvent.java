package jadex.simulation.analysis.common.events.data;

import jadex.simulation.analysis.common.events.AEvent;
import jadex.simulation.analysis.common.util.AConstants;

public class ADataEvent extends AEvent
{
	protected Object newValue; 
	
	@Override
	public String getEventType()
	{
		return AConstants.DATA_EVENT;
	}
    
    public ADataEvent(IADataObservable source, String eventCommand, Object newValue)
    {
    	super(source, eventCommand);
    	this.newValue = newValue;
    	
    }
    
    public Object getValue()
	{
		return newValue;
	}
    
}