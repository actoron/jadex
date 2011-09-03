package jadex.simulation.analysis.common.superClasses.events.data;

import jadex.simulation.analysis.common.superClasses.events.AEvent;
import jadex.simulation.analysis.common.superClasses.events.IAObservable;
import jadex.simulation.analysis.common.util.AConstants;

public class ADataEvent extends AEvent
{
	protected Object newValue; 
	
	@Override
	public String getEventType()
	{
		return AConstants.DATA_EVENT;
	}
    
    public ADataEvent(IAObservable source, String eventCommand, Object newValue)
    {
    	super(source, eventCommand);
    	this.newValue = newValue;
    	
    }
    
    public Object getValue()
	{
		return newValue;
	}
    
}