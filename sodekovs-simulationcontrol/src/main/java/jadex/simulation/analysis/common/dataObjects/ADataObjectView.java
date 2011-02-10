package jadex.simulation.analysis.common.dataObjects;

import jadex.simulation.analysis.common.events.ADataEvent;
import jadex.simulation.analysis.common.events.IADataObservable;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class ADataObjectView implements IADataView
{

	protected JComponent component;
	protected IADataObservable displayedDataObject;
	protected Object mutex = new Object();

	public ADataObjectView(IADataObservable dataObject)
	{
		synchronized (mutex)
		{
			displayedDataObject = dataObject;
			dataObject.addDataListener(this);
			component = new JPanel(new GridBagLayout());
			JComponent freePanel = new JPanel();
			component.add(freePanel, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		}
	}

	@Override
	public JComponent getComponent()
	{
		return component;
	}

	@Override
	public IADataObservable getDisplayedObject()
	{
		return displayedDataObject;
	}

	@Override
	public Object getMutex()
	{
		return mutex;
	}

	@Override
	public void setDisplayedObject(IADataObservable dataObject)
	{
		this.displayedDataObject = dataObject;

	}
	
	// -------- IADataListener --------

	@Override
	public void dataEventOccur(ADataEvent event)
	{
		//omit
	}

}
