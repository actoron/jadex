package jadex.examples.presentationtimer.remotecontrol.ui;

import jadex.examples.presentationtimer.common.ICountdownService;

import javax.swing.DefaultListModel;


public class CDListModel extends DefaultListModel<ICountdownService>
{
	public CDListModel()
	{
		super();
	}

	@Override
	public void addElement(ICountdownService element)
	{
		if (!contains(element)) {
			super.addElement(element);
		}
	}

	@Override
	public void add(int index, ICountdownService element)
	{
		if (!contains(element)) {
			super.add(index, element);
		}
	}

}
