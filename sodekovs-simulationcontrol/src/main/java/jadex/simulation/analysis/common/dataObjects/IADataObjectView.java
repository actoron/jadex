package jadex.simulation.analysis.common.dataObjects;


import jadex.simulation.analysis.common.events.ADataListener;

import javax.swing.JComponent;

public interface IADataObjectView extends ADataListener
{
	/**
	 * Synchronize Object of the {@link IADataObjectView}
	 * 
	 * @return Mutex of the {@link IADataObjectView}
	 */
	public Object getMutex();
	
	/**
	 * Returns the dataObject which is displayed
	 * @return a {@link IADataObject}
	 */
	public IADataObject getDisplayedObject();
	
	/**
	 * Sets the dataObject to display
	 * @param a {@link IADataObject}
	 */
	public void setDisplayedObject(IADataObject dataObject);
	
	/**
	 * Returns the component which displays the dataObject
	 * @return the {@link JComponent} to display
	 */
	public JComponent getComponent();
}
