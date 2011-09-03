package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.IAObservable;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class ADataObjectView implements IADataView
{
	protected JComponent component;
	protected IAObservable displayedDataObject;

	public ADataObjectView(IAObservable dataObject)
	{
		displayedDataObject = dataObject;
		dataObject.addListener(this);
		//TODO: Swing Thread
		component = new JPanel(new GridBagLayout());
		JComponent freePanel = new JPanel();
		component.add(freePanel, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
	}

	@Override
	public JComponent getComponent()
	{
		return component;
	}

	@Override
	public IAObservable getDisplayedObject()
	{
		return displayedDataObject;
	}

	@Override
	public void setDisplayedObject(IAObservable dataObject)
	{
		this.displayedDataObject = dataObject;
	}

	// -------- IAListener --------

	@Override
	public void update(IAEvent event)
	{
	// omit
	}
}
