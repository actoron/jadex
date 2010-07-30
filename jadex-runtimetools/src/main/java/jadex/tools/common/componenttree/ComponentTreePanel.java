package jadex.tools.common.componenttree;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

/**
 *  A panel displaying components on the platform as tree.
 */
public class ComponentTreePanel extends JPanel
{
	//-------- constructors --------
	
	/**
	 *  Create a new component tree panel.
	 */
	public ComponentTreePanel(final IServiceProvider provider)
	{
		final ComponentTreeModel	model	= new ComponentTreeModel();
		final JTree	tree	= new JTree(model);
		tree.setCellRenderer(new ComponentTreeCellRenderer());
		tree.setShowsRootHandles(true);
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(tree));

		SServiceProvider.getServiceUpwards(provider, IComponentManagementService.class).addResultListener(new SwingDefaultResultListener(this)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final IComponentManagementService	cms	= (IComponentManagementService)result;
				final ComponentIconCache	cic	= new ComponentIconCache(provider, tree);
				
				// Hack!!! How to find root node?
				cms.getComponentDescriptions().addResultListener(new SwingDefaultResultListener(ComponentTreePanel.this)
				{
					public void customResultAvailable(Object source, Object result)
					{
						IComponentDescription[]	descriptions	= (IComponentDescription[])result;
						IComponentDescription	root	= null;
						for(int i=0; root==null && i<descriptions.length; i++)
						{
							if(descriptions[i].getParent()==null)
							{
								root	= descriptions[i];
							}
						}
						model.setRoot(new ComponentTreeNode(null, model, root, cms, ComponentTreePanel.this, cic));
					}
				});
				
				cms.addComponentListener(null, new IComponentListener()
				{
					public void componentRemoved(final IComponentDescription desc, Map results)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								IComponentTreeNode	node	= model.getNode(desc.getName());
								model.deregisterNode(desc.getName());
								if(node.getParent()!=null)
								{
									((AbstractComponentTreeNode)node.getParent()).removeChild(node);
								}
							}
						});
					}
					
					public void componentChanged(IComponentDescription desc)
					{
					}
					
					public void componentAdded(final IComponentDescription desc)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								ComponentTreeNode	parent	= null;
								if(desc.getParent()!=null)
								{
									parent	= (ComponentTreeNode)model.getNode(desc.getParent());
								}
								
								IComponentTreeNode	node	= new ComponentTreeNode(parent, model, desc, cms, ComponentTreePanel.this, cic);
								if(parent!=null)
								{
									parent.addChild(node);
								}
							}
						});
					}
				});				
			}
		});
	}
}
