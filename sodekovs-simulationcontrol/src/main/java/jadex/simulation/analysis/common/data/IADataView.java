package jadex.simulation.analysis.common.data;


import jadex.simulation.analysis.common.superClasses.events.IAListener;
import jadex.simulation.analysis.common.superClasses.events.IAObservable;

import javax.swing.JComponent;

public interface IADataView extends IAListener
{
	/**
	 * Returns the dataObject which is displayed
	 * @return a {@link IAObservable}
	 */
	public IAObservable getDisplayedObject();
	
	/**
	 * Sets the dataObject to display
	 * @param a {@link IAObservable} to display
	 */
	public void setDisplayedObject(IAObservable dataObject);
	
	/**
	 * Returns the generalComp which displays the dataObject
	 * @return the {@link JComponent} to display
	 */
	public JComponent getComponent();
}
