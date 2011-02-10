package jadex.simulation.analysis.common.events;

import jadex.simulation.analysis.common.util.AConstants;

public class ADataEvent extends AEvent
{
	@Override
	public String getEventType()
	{
		return AConstants.DATA_EVENT;
	}
    
    public ADataEvent(IADataObservable source, String eventCommand)
    {
    	super(source, eventCommand);
    }
}