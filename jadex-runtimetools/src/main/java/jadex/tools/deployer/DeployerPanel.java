package jadex.tools.deployer;

import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.base.gui.filetree.DefaultFileFilter;
import jadex.base.gui.filetree.DefaultFileFilterMenuItemConstructor;
import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IExternalAccess;
import jadex.commons.Properties;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.PopupBuilder;
import jadex.tools.generic.AbstractComponentSelectorPanel;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/**
 *  Panel for showing a file transfer view.
 */
public class DeployerPanel extends JPanel
{
	/** The local external access. */
	protected IExternalAccess exta;
	
	/**
	 *  Create a new deloyer panel.
	 */
	public DeployerPanel(final IExternalAccess exta)
	{
		this.exta = exta;
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		setLayout(new BorderLayout());
		add(split, BorderLayout.CENTER);

		// Local view on the left
		FileTreePanel left = createFileTreePanel(exta, exta);
		split.add(new JScrollPane(left));
		
		// Remote view on the right
		AbstractComponentSelectorPanel right = new AbstractComponentSelectorPanel(exta, "jadex.standalone.Platform")
		{
			public IFuture createComponentPanel(IExternalAccess component)
			{
				final FileTreePanel ftp = createFileTreePanel(exta, component);
				
				IComponentViewerPanel cvp = new IComponentViewerPanel()
				{
					public IFuture setProperties(Properties props)
					{
						return ftp.setProperties(props);
					}
					
					public IFuture getProperties()
					{
						return ftp.getProperties();
					}
					
					public IFuture shutdown()
					{
						return new Future(null);
					}
					
					public String getId()
					{
						return ""+ftp.hashCode();
					}
					
					public JComponent getComponent()
					{
						return ftp;
					}
					
					public IFuture init(IControlCenter jcc, IExternalAccess component)
					{
						return new Future(null);
					}
				};
				
				return new Future(cvp);
			}
		};
		
		split.add(right);
	}
	
	/**
	 *  Create a file tree panel. 
	 */
	public FileTreePanel createFileTreePanel(IExternalAccess exta, IExternalAccess component)
	{
		boolean remote = !exta.getComponentIdentifier().getPlatformName().equals(component.getComponentIdentifier().getPlatformName());
		final FileTreePanel ret = new FileTreePanel(component, remote);
		DefaultFileFilterMenuItemConstructor mic = new DefaultFileFilterMenuItemConstructor(ret.getModel());
		ret.setPopupBuilder(new PopupBuilder(new Object[]{mic}));
		DefaultFileFilter ff = new DefaultFileFilter(mic);
		ret.setFileFilter(ff);
		ret.addNodeHandler(new DefaultNodeHandler(ret.getTree()));
		File[] roots = File.listRoots();
		for(int i=0; i<roots.length; i++)
		{
			ret.addTopLevelNode(roots[i]);
		}
		return ret;
	}
}
