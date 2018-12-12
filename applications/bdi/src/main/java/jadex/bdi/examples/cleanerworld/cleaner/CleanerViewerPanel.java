package jadex.bdi.examples.cleanerworld.cleaner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  The cleaner panel allows including the cleaner GUI in the JCC.
 */
public class CleanerViewerPanel extends AbstractComponentViewerPanel
{
	//-------- attributes --------
	
	/** The cleaner panel. */
	protected JComponent	panel;
	
	//-------- constructors --------
	
	/**
	 *  Called to initialize the panel.
	 */
	public IFuture<Void> init(IControlCenter jcc, final IExternalAccess component)
	{
		IFuture<Void>	fut	= super.init(jcc, component);
		assert fut.isDone();
		
		final Future<Void>	ret	= new Future<Void>();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				CleanerViewerPanel.this.panel	= new CleanerPanel(component);
				Timer	timer	= new Timer(50, new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						CleanerViewerPanel.this.panel.invalidate();
						CleanerViewerPanel.this.panel.repaint();
					}
				});
				timer.start();
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	//-------- AbstractComponentViewerPanel methods --------
	
	/**
	 *  Provide the panel.
	 */
	public JComponent getComponent()
	{
		return panel;
	}
}
