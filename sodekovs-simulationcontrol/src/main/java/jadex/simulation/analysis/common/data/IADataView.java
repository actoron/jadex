package jadex.simulation.analysis.common.data;


import jadex.simulation.analysis.common.events.data.IADataListener;
import jadex.simulation.analysis.common.events.data.IADataObservable;

import javax.swing.JComponent;

public interface IADataView extends IADataListener
{
	/**
	 * Synchronize Object
	 * 
	 * @return Mutex
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
	 * Returns the generalComp which displays the dataObject
	 * @return the {@link JComponent} to display
	 */
	public JComponent getComponent();
}
