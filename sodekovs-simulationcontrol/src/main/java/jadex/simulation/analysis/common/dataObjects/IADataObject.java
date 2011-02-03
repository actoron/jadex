package jadex.simulation.analysis.common.dataObjects;

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
	public void setEditable(boolean editable);

	/**
	 * Returns if this {@link IADataObject} is editable. Default is true.
	 * 
	 * @return Flag for editable field
	 */
	public boolean isEditable();

	/**
	 * Returns the view of the dataObject. May be null, if no view is supported
	 * 
	 * @return View of the dataObject
	 */
	public JComponent getView();

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

}
