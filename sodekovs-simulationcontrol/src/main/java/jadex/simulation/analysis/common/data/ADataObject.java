package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.superClasses.events.AObservable;
import jadex.simulation.analysis.common.superClasses.events.data.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

import java.util.UUID;

/**
 * ADataObject Implementation
 * 
 * @author 5Haubeck
 * 
 */
public class ADataObject extends AObservable implements IADataObject {

	private String id = UUID.randomUUID().toString();
	protected Boolean editable = Boolean.TRUE;
	protected String name = "defaultName";
	protected IADataView view;

	public ADataObject() {
		synchronized (mutex) {
//			view = new ADataObjectView(this);
		}
	}

	public ADataObject(String name) {
		synchronized (mutex) {
			setName(name);
//			view = new ADataObjectView(this);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		synchronized (mutex) {
			this.id = id;
		}
	}

	public Boolean getEditable() {
		return editable;
	}

	// Name
	@Override
	public void setName(String name) {
		synchronized (mutex) {
			this.name = name;
		}
		notify(new ADataEvent(this, AConstants.DATA_NAME, name));
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setEditable(Boolean editable) {
		synchronized (mutex) {
			this.editable = editable;
		}
		notify(new ADataEvent(this, AConstants.DATA_EDITABLE, editable));
	}

	@Override
	public Boolean isEditable() {
		return editable;
	}

	@Override
	public IADataView getView() {
		return view;
	}
	
	

	public void setView(IADataView view) {
		synchronized (mutex) {
		this.view = view;
		}
	}

	@Override
	public ADataObject clonen() {
		synchronized (mutex) {
			ADataObject clone = new ADataObject(name);
			clone.setEditable(editable);
			return clone;
		}
	}
}
