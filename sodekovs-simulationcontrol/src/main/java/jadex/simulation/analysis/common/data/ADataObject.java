package jadex.simulation.analysis.common.data;

import jadex.bridge.service.annotation.Reference;
import jadex.simulation.analysis.common.events.data.ADataEvent;
import jadex.simulation.analysis.common.events.data.ADataObservable;
import jadex.simulation.analysis.common.util.AConstants;

import java.util.UUID;

@Reference
public class ADataObject extends ADataObservable implements IADataObject
{
	private UUID id = UUID.randomUUID();
	protected Boolean editable = Boolean.TRUE;
	protected String name = "defaultName";
	protected IADataView view;

	public ADataObject(String name)
	{
		synchronized (mutex)
		{
			setName(name);
			view = new ADataObjectView(this);
		}
	}

	// Name
	@Override
	public void setName(String name)
	{
		synchronized (mutex)
		{
			this.name = name;
		}
		dataChanged(new ADataEvent(this, AConstants.DATA_NAME, name));
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setEditable(Boolean editable)
	{
		synchronized (mutex)
		{
			this.editable = editable;
		}
		dataChanged(new ADataEvent(this, AConstants.DATA_EDITABLE, editable));
	}

	@Override
	public Boolean isEditable()
	{
		return editable;
	}

	@Override
	public UUID getID()
	{
		return id;
	}

	@Override
	public IADataView getView()
	{
		return view;
	}

	@Override
	public ADataObject clonen()
	{
		ADataObject clone = new ADataObject(name);
		clone.setEditable(editable);
		return clone;
	}
}
