package jadex.tools.common.componenttree;

import jadex.bridge.ComponentFactorySelector;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;

/**
 *  Renderer for component tree cells.
 */
public class ComponentTreeCellRenderer	extends DefaultTreeCellRenderer
{
	//-------- attributes --------
	
	/** The service provider used for finding component factories. */
	private final IServiceProvider	provider;
	
	/** The UI component used for displaying error messages. */
	// Todo: status bar for longer lasting actions?
	private final Component	ui;
	
	/** Cached icons for node types. */
	private final Map	icons;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component tree cell renderer.
	 */
	public ComponentTreeCellRenderer(IServiceProvider provider, Component ui)
	{
		this.provider	= provider;
		this.ui	= ui;
		this.icons	= new HashMap();
	}
	
	//-------- TreeCellRenderer interface --------
	
	/**
	 *  Get the cell renderer for a node.
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
		boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		// Change icons depending on node type.
		IComponentDescription	node	= (IComponentDescription)value;
		if(node.getType()!=null)
		{
			Icon	icon	= getIcon(node.getType(), node, tree);
			if(icon!=null)
			{
				setOpenIcon(icon);
				setClosedIcon(icon);
				setLeafIcon(icon);
			}
			else
			{
				setOpenIcon(getDefaultOpenIcon());
				setClosedIcon(getDefaultClosedIcon());
				setLeafIcon(getDefaultLeafIcon());
			}
		}

		JComponent	comp	= (JComponent)super.getTreeCellRendererComponent(
			tree, value, selected, expanded, leaf, row, hasFocus);
		
		setText(node.getName().getName());

		return comp;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the icon for a node type.
	 */
	public Icon	getIcon(final String type, final IComponentDescription node, final JTree tree)
	{
		Icon	ret	= null;
		
		if(icons.containsKey(type))
		{
			ret	= (Icon)icons.get(type);
		}
		else
		{
			SServiceProvider.getService(provider, new ComponentFactorySelector(type)).addResultListener(new SwingDefaultResultListener(ui)
			{
				public void customResultAvailable(Object source, Object result)
				{
					IComponentFactory	fac	= (IComponentFactory)result;
					icons.put(type, fac.getComponentTypeIcon(type));
					TreeModel	model	= tree.getModel();
					if(model instanceof ComponentTreeModel)
					{
						((ComponentTreeModel)model).fireNodeChanged(node);
					}
					else
					{
						tree.repaint();
					}
				}
			});
		}
		
		return ret;
	}
}
