package jadex.tools.deployer;

import jadex.base.gui.filetree.DefaultFileFilter;
import jadex.base.gui.filetree.DefaultFileFilterMenuItemConstructor;
import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.bridge.IExternalAccess;
import jadex.commons.gui.PopupBuilder;

import java.awt.BorderLayout;
import java.io.File;

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
	public DeployerPanel(IExternalAccess exta)
	{
		this.exta = exta;
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		setLayout(new BorderLayout());
		add(split, BorderLayout.CENTER);

		// Local view on the left
		FileTreePanel left = new FileTreePanel(exta);
		DefaultFileFilterMenuItemConstructor mic = new DefaultFileFilterMenuItemConstructor(left.getModel());
		left.setPopupBuilder(new PopupBuilder(new Object[]{mic}));
		DefaultFileFilter ff = new DefaultFileFilter(mic);
		left.setFileFilter(ff);
		left.addNodeHandler(new DefaultNodeHandler(left.getTree()));
		
//		FileSystemView view = FileSystemView.getFileSystemView();
//		File[] roots = view.getRoots();
		File[] roots = File.listRoots();
		for(int i=0; i<roots.length; i++)
		{
			left.addTopLevelNode(roots[i]);
		}
		split.add(new JScrollPane(left));
		
		// Remote view on the right
	}
	
	
}
