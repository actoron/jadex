package jadex.tools.common.componenttree;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.service.IServiceProvider;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 *  A panel displaying components on the platform as tree.
 */
public class ComponentTreePanel extends JPanel
{
	//-------- constructors --------
	
	/**
	 *  Create a new component tree panel.
	 */
	public ComponentTreePanel(IComponentManagementService cms, IServiceProvider provider, IComponentDescription root)
	{
		JTree	tree	= new JTree(new ComponentTreeModel(cms, root, this));
		tree.setCellRenderer(new ComponentTreeCellRenderer(provider, this));
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(tree));
	}
}
