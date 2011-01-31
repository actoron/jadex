package jadex.base.gui.componenttree;

import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.ComponentFactorySelector;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.xml.annotation.XMLClassname;

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
	private final IExternalAccess exta;
	
	/** The tree. */
	private final JTree	tree;
	
	//-------- constructors --------
	
	/**
	 *  Create an icon cache.
	 */
	public ComponentIconCache(IExternalAccess exta, JTree tree)
	{
		this.icons	= new HashMap();
		this.exta	= exta;
		this.tree	= tree;
	}
	
	//-------- methods --------
	
	/**
	 *  Get an icon.
	 */
	public Icon	getIcon(final ITreeNode node, final String type)
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
			
			exta.scheduleStep(new IComponentStep()
			{
				@XMLClassname("getFactoryService")
				public Object execute(IInternalAccess ia)
				{
					final Future ret = new Future();
					SServiceProvider.getService(ia.getServiceProvider(), new ComponentFactorySelector(type))
						.addResultListener(new DelegationResultListener(ret));
					return ret;
				}
			}).addResultListener(new SwingDefaultResultListener()
			{
				public void customResultAvailable(Object result)
				{
					try
					{
						IComponentFactory	fac	= (IComponentFactory)result;
						fac.getComponentTypeIcon(type)
							.addResultListener(new SwingDefaultResultListener()
						{
							public void customResultAvailable(Object result)
							{
								icons.put(type, result);
								TreeModel	model	= tree.getModel();
								if(model instanceof AsyncTreeModel)
								{
									((AsyncTreeModel)model).fireNodeChanged(node);
								}
								else
								{
									tree.repaint();
								}
							}
						});
					}
					catch(Exception e)
					{
						// could be UnsupportedOpEx in case of remote factory
					}
				}
			});
		}
		
		return ret;
	}
}
