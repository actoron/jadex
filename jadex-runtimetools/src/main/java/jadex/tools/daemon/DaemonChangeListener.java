package jadex.tools.daemon;

import jadex.commons.ChangeEvent;
import jadex.commons.jtable.ObjectTableModel;

import javax.swing.JTable;

/**
 * 
 */
public class DaemonChangeListener implements IRemoteChangeListener
{
	/** The platform table. */
	protected JTable platformt;
	
	/**
	 * 
	 */
	public DaemonChangeListener(JTable platformt)
	{
		this.platformt = platformt;
	}
	
	/**
	 * 
	 */
	public void changeOccurred(ChangeEvent event)
	{
		if(IDaemonService.ADDED.equals(event.getType()))
		{
			((ObjectTableModel)platformt.getModel()).addRow(new Object[]{event.getValue()}, event.getValue());
		}
		else if(IDaemonService.REMOVED.equals(event.getType()))
		{
			((ObjectTableModel)platformt.getModel()).removeRow(event.getValue());
		}
	}
}
