package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.superClasses.events.IAObservable;

import java.util.UUID;

/**
 * Basic Object of DataObject
 * @author 5Haubeck
 *
 */
public interface IADataObject extends IAObservable
{

	/**
	 * Marks the {@link IADataObject} editable or not editable
	 * @param  Flag for editable
	 */
	public void setEditable(Boolean editable);

	/**
	 * Returns if this {@link IADataObject} is editable. Default is true.
	 * @return Flag for editable field
	 */
	public Boolean isEditable();

	/**
	 * Returns a ID for the dataObject
	 * @return ID as UUID
	 */
	public UUID getId();

	/**
	 * Returns the name for the dataObject
	 * @return String name
	 */
	public String getName();

	/**
	 * Sets the name for the dataObject
	 *  @param String as name
	 */
	public void setName(String name);
	
	/**
	 * Returns the view of the dataObject
	 *  @param {@link IADataView} as view
	 */
	public IADataView getView();
	

	/**
	 * Clones the dataObject with its properties
	 *  @param cloned {@link ADataObject}
	 */
	public ADataObject clonen();
}
