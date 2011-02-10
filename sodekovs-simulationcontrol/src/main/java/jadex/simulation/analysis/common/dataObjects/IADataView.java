package jadex.simulation.analysis.common.dataObjects;


import jadex.simulation.analysis.common.events.IADataObservable;
import jadex.simulation.analysis.common.events.IADataListener;

import javax.swing.JComponent;

public interface IADataView extends IADataListener
{
	/**
	 * Synchronize Object of the {@link IADataView}
	 * 
	 * @return Mutex of the {@link IADataView}
	 */
	public Object getMutex();
	
	/**
	 * Returns the dataObject which is displayed
	 * @return a {@link IADataObject}
	 */
	public IADataObservable getDisplayedObject();
	
	/**
	 * Sets the dataObject to display
	 * @param a {@link IADataObject}
	 */
	public void setDisplayedObject(IADataObservable dataObject);
	
	/**
	 * Returns the component which displays the dataObject
	 * @return the {@link JComponent} to display
	 */
	public JComponent getComponent();
}
