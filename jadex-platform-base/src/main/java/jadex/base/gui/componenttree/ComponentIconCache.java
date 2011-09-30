package jadex.base.gui.componenttree;

import jadex.base.gui.CMSUpdateHandler;
import jadex.base.gui.SwingDefaultResultListener;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.component.ComponentFactorySelector;
import jadex.commons.future.IFuture;
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
	private final Map<String, Icon>	icons;
	
	/** The cms handler. */
	private final CMSUpdateHandler cmshandler;
	
	/** The tree. */
	private final JTree	tree;
	
	//-------- constructors --------
	
	/**
	 *  Create an icon cache.
	 */
	public ComponentIconCache(CMSUpdateHandler cmshandler, JTree tree)
	{
		this.icons	= new HashMap<String, Icon>();
		this.cmshandler	= cmshandler;
		this.tree	= tree;
	}
	
	//-------- methods --------
	
	/**
	 *  Get an icon.
	 */
	public Icon	getIcon(final IActiveComponentTreeNode node, final String type)
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
			
			cmshandler.getLocalCMS().addResultListener(new SwingDefaultResultListener<IComponentManagementService>()
			{
				public void customResultAvailable(IComponentManagementService cms)
				{
					cms.getExternalAccess(node.getComponentIdentifier()).addResultListener(new SwingDefaultResultListener<IExternalAccess>()
					{
						public void customResultAvailable(IExternalAccess exta)
						{
							final String	remtype	= type;	// inner final variable for remote step.
							exta.scheduleStep(new IComponentStep<IComponentFactory>()
							{
								@XMLClassname("getFactoryService")
								public IFuture<IComponentFactory> execute(IInternalAccess ia)
								{
									IFuture<IComponentFactory> ret = SServiceProvider.getService(ia.getServiceContainer(), new ComponentFactorySelector(remtype));
									return ret;
								}
							}).addResultListener(new SwingDefaultResultListener<IComponentFactory>()
							{
								public void customResultAvailable(IComponentFactory fac)
								{
									try
									{
										fac.getComponentTypeIcon(type)
											.addResultListener(new SwingDefaultResultListener<Icon>()
										{
											public void customResultAvailable(Icon result)
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
											
											public void customExceptionOccurred(Exception exception)
											{
												// Todo: remember failed searches for efficiency?
											}
										});
									}
									catch(Exception e)
									{
										// could be UnsupportedOpEx in case of remote factory
									}
								}
								
								public void customExceptionOccurred(Exception exception)
								{
									// Todo: remember failed searches for efficiency?
								}
							});
						}
						
						public void customExceptionOccurred(Exception exception)
						{
							// Todo: remember failed searches for efficiency?
						}
					});
				}
				
				public void customExceptionOccurred(Exception exception)
				{
					// Todo: remember failed searches for efficiency?
				}
			});			
		}
		
		return ret;
	}
}
