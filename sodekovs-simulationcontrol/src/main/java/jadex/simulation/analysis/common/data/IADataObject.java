package jadex.simulation.analysis.common.data;


import jadex.simulation.analysis.common.events.data.IADataObservable;

import java.util.UUID;

import javax.swing.JComponent;

public interface IADataObject extends IADataObservable
{

	/**
	 * Marks the {@link IADataObject} editable or not editable
	 * 
	 * @param editable
	 *            Flag for editable
	 */
	public void setEditable(Boolean editable);

	/**
	 * Returns if this {@link IADataObject} is editable. Default is true.
	 * 
	 * @return Flag for editable field
	 */
	public Boolean isEditable();

	/**
	 * Returns a ID for the dataObject
	 * 
	 * @return ID as UUID
	 */
	public UUID getID();

	public String getName();

	public void setName(String name);
	
	public IADataView getView();
	
	public ADataObject clonen();
}
