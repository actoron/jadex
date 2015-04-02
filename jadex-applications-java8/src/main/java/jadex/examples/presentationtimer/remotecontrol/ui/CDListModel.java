package jadex.examples.presentationtimer.remotecontrol.ui;

import javax.swing.DefaultListModel;


public class CDListModel extends DefaultListModel<CDListItem>
{
	public CDListModel()
	{
		super();
	}

	@Override
	public void addElement(CDListItem element)
	{
		if(!contains(element))
		{
			super.addElement(element);
		}
		else
		{
			int indexOf = indexOf(element);
			CDListItem oldElement = get(indexOf);
			oldElement.setTime(element.getTime());
			oldElement.setStatus(element.getStatus());
		}
	}

	@Override
	public void add(int index, CDListItem element)
	{
		if(!contains(element))
		{
			super.add(index, element);
		}
	}

}
