package jadex.simulation.analysis.common.events;

import jadex.simulation.analysis.common.dataObjects.IADataObject;
import jadex.simulation.analysis.common.util.AConstants;

public class ADataEvent extends ABasicEvent
{
	@Override
	public String getEventType()
	{
		return AConstants.DATA_EVENT;
	}
    
    public ADataEvent(IADataObject source, String eventCommand)
    {
    	super(source, eventCommand);
    }
}