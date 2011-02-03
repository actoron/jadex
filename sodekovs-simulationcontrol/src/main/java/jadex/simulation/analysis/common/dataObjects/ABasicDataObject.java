package jadex.simulation.analysis.common.dataObjects;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ABasicDataObject implements IADataObject
{
	private UUID id = UUID.randomUUID();
	private boolean editable = true;
	protected Object mutex = new Object();

	@Override
	public void setEditable(boolean editable)
	{
		synchronized (mutex)
		{
			this.editable = editable;
		}

	}

	@Override
	public boolean isEditable()
	{
		return editable;
	}

	@Override
	public JComponent getView()
	{
		synchronized (mutex)
		{
			final JComponent component = new JPanel(new GridBagLayout());
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					JComponent freePanel = new JPanel();
					component.add(freePanel, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
				}
			});
			return component;
		}

	}

	public Object getMutex()
	{
		return mutex;
	}

	public UUID getID()
	{
		return id;
	}

}
