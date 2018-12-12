package jadex.tools.daemon;

import javax.swing.JTable;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.daemon.IDaemonService;
import jadex.commons.ChangeEvent;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.future.IFuture;
import jadex.commons.gui.jtable.ObjectTableModel;

/**
 * 
 */
public class DaemonChangeListener implements IRemoteChangeListener<IComponentIdentifier>
{
	/** The platform table. */
	protected JTable platformt;
	
	/**
	 *  Create a new change listener.
	 */
	public DaemonChangeListener(JTable platformt)
	{
		this.platformt = platformt;
	}
	
	/**
	 *  Invoked when a change occurs.
	 */
	public IFuture<Void> changeOccurred(ChangeEvent<IComponentIdentifier> event)
	{
		if(IDaemonService.ADDED.equals(event.getType()))
		{
			((ObjectTableModel)platformt.getModel()).addRow(new Object[]{event.getValue()}, event.getValue());
		}
		else if(IDaemonService.REMOVED.equals(event.getType()))
		{
			((ObjectTableModel)platformt.getModel()).removeRow(event.getValue());
		}
		return IFuture.DONE;
	}
}
