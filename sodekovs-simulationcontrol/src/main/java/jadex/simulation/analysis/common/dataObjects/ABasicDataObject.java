package jadex.simulation.analysis.common.dataObjects;

import jadex.simulation.analysis.common.events.ADataEvent;
import jadex.simulation.analysis.common.events.ADataListener;
import jadex.simulation.analysis.common.util.AConstants;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ABasicDataObject implements IADataObject
{
	private UUID id = UUID.randomUUID();
	private Boolean editable = Boolean.TRUE;
	protected Object mutex = new Object();
	protected Set<ADataListener> listeners = new HashSet<ADataListener>();

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

	public void addDataListener(ADataListener listener)
	{
		synchronized (mutex)
		{
			listeners.add(listener);
		}
	}

	public void removeDataListener(ADataListener listener)
	{
		synchronized (mutex)
		{
			listeners.remove(listener);
		}
	}

	public void dataChanged(ADataEvent e)
	{
		synchronized (mutex)
		{
			for (ADataListener listener : listeners)
			{
				listener.dataEventOccur(e);
			}
		}
	}
}
