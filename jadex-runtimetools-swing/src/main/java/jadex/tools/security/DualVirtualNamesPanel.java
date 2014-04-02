package jadex.tools.security;

import jadex.base.gui.CMSUpdateHandler;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.gui.JSplitPanel;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultTreeModel;

/**
 * 
 */
public class DualVirtualNamesPanel extends JPanel
{
	/**
	 *  Create a new panel. 
	 */
	public DualVirtualNamesPanel(final IExternalAccess ea, final ISecurityService secser, final CMSUpdateHandler cmshandler)
	{
		setLayout(new BorderLayout());
		
		JSplitPanel sp = new JSplitPanel(JSplitPane.HORIZONTAL_SPLIT);
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(0.5);
		final VirtualNamesPanel vnp1 = new VirtualNamesPanel(ea, secser, cmshandler, false);
		final VirtualNamesPanel vnp2 = new VirtualNamesPanel(ea, secser, cmshandler, true);
		
		vnp1.addChangeListener(new IChangeListener<String>()
		{
			public void changeOccurred(ChangeEvent<String> event)
			{
				((DefaultTreeModel)vnp2.getTree().getModel()).reload();
			}
		});
		
		vnp2.addChangeListener(new IChangeListener<String>()
		{
			public void changeOccurred(ChangeEvent<String> event)
			{
				((DefaultTreeModel)vnp1.getTree().getModel()).reload();
			}
		});
		
		sp.add(vnp1);
		sp.add(vnp2);
		
		add(sp);
	}
}
