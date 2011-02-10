package jadex.simulation.analysis.common.dataObjects;

import jadex.simulation.analysis.common.events.ADataEvent;
import jadex.simulation.analysis.common.events.ADataObservable;
import jadex.simulation.analysis.common.util.AConstants;

import java.util.UUID;

public class ADataObject extends ADataObservable implements IADataObject
{
	private UUID id = UUID.randomUUID();
	private Boolean editable = Boolean.TRUE;
	@Override
	public void setEditable(Boolean editable)
	{
		synchronized (mutex)
		{
			this.editable = editable;
		}
		dataChanged(new ADataEvent(this, AConstants.DATA_EDITABLE));
	}

	@Override
	public Boolean isEditable()
	{
		return editable;
	}

	public Object getMutex()
	{
		return mutex;
	}

	public UUID getID()
	{
		return id;
	}
}
