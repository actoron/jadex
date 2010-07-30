package jadex.tools.common.componenttree;

import jadex.bridge.ComponentFactorySelector;
import jadex.bridge.IComponentFactory;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;

/**
 *  Cache for component icons.
 *  Asynchronously loads icons and updates tree.
 */
public class ComponentIconCache
{
	//-------- attributes --------
	
	/** The icon cache. */
	private final Map	icons;
	
	/** The service provider. */
	private final IServiceProvider	provider;
	
	/** The tree. */
	private final JTree	tree;
	
	//-------- constructors --------
	
	/**
	 *  Create an icon cache.
	 */
	public ComponentIconCache(IServiceProvider provider, JTree tree)
	{
		this.icons	= new HashMap();
		this.provider	= provider;
		this.tree	= tree;
	}
	
	//-------- methods --------
	
	/**
	 *  Get an icon.
	 */
	public Icon	getIcon(final IComponentTreeNode node, final String type)
	{
		Icon	ret	= null;
		
		if(icons.containsKey(type))
		{
			ret	= (Icon)icons.get(type);
		}
		else
		{
			// Todo: remember ongoing searches for efficiency?
//			System.out.println("getIcon: "+type);
			SServiceProvider.getService(provider, new ComponentFactorySelector(type)).addResultListener(new SwingDefaultResultListener(tree)
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
