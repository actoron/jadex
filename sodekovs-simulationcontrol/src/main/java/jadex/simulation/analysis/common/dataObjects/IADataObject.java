package jadex.simulation.analysis.common.dataObjects;

import jadex.simulation.analysis.common.events.ADataEvent;
import jadex.simulation.analysis.common.events.ADataListener;

import java.util.UUID;

import javax.swing.JComponent;

public interface IADataObject
{

	/**
	 * Marks the {@link IADataObject} editable or not editable
	 * 
	 * @param editable
	 *            Flag for editable
	 */
	void setEditable(Boolean editable);

	/**
	 * Returns if this {@link IADataObject} is editable. Default is true.
	 * 
	 * @return Flag for editable field
	 */
	public Boolean isEditable();

	/**
	 * Synchronize Object of the {@link IADataObject}
	 * 
	 * @return Mutex of the dataObject
	 */
	public Object getMutex();

	/**
	 * Returns a ID for the dataObject
	 * 
	 * @return ID as UUID
	 */
	public UUID getID();

	/**
	 * Adds a Listener, who observe the state of the object
	 * @param listener the {@link ADataListener} to add
	 */
	public void addDataListener(ADataListener listener);
	
	/**
	 * Removes a Listener, who observe the state of the object
	 * @param listener the {@link ADataListener} to remove
	 */
	public void removeDataListener(ADataListener listener);
	
	/**
	 * Indicates a change in data
	 * @param event of the change
	 */
	public void dataChanged(ADataEvent e);
}
