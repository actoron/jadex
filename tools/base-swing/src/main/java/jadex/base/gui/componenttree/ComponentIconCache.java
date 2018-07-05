package jadex.base.gui.componenttree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;

import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;

/**
 *  Cache for component icons.
 *  Asynchronously loads icons and updates tree.
 */
public class ComponentIconCache
{
	//-------- constants --------
	
	/** The default component icons. */
	protected static final UIDefaults	ICONS	= new UIDefaults(new Object[]
	{
		"component", SGUI.makeIcon(ComponentIconCache.class, "/jadex/base/gui/images/component.png")
	});
	
	//-------- attributes --------
	
	/** The icon cache. */
	private final Map<String, Icon>	icons;
	
	/** The ongoing icon lookups (type -> (icon future, platform list)). */
	private final Map<String, Tuple2<IFuture<Icon>, List<IComponentIdentifier>>>	lookups;
	
	/** The local jcc platform access. */
	private final IExternalAccess jccaccess;
	
	//-------- constructors --------
	
	/**
	 *  Create an icon cache.
	 */
	public ComponentIconCache(IExternalAccess jccaccess)
	{
		this.icons	= new HashMap<String, Icon>();
		this.lookups	= new HashMap<String, Tuple2<IFuture<Icon>, List<IComponentIdentifier>>>();
		this.jccaccess	= jccaccess;
	}
	
	//-------- methods --------
	
	/**
	 *  Get an icon.
	 */
	public Icon	getIcon(final String type, final IActiveComponentTreeNode node, final AsyncSwingTreeModel model)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		Icon	ret	= null;
		IFuture<Icon>	fut	= null;
		
		// Use cached icon, if available.
		if(icons.containsKey(type))
		{
			ret	= (Icon)icons.get(type);
		}

		else if(node.getComponentIdentifier()!=null)	// Might by null initially for proxy node.
		{
			// Add listener to ongoing search, if any.
			if(lookups.containsKey(type))
			{
				Tuple2<IFuture<Icon>, List<IComponentIdentifier>>	lookup	= lookups.get(type);
				if(!lookup.getSecondEntity().contains(node.getComponentIdentifier().getRoot()))
					lookup.getSecondEntity().add(node.getComponentIdentifier().getRoot());
				fut	= lookup.getFirstEntity();
			}
			
			// Start new search.
			else
			{
				List<IComponentIdentifier>	todo	= new ArrayList<IComponentIdentifier>();
				todo.add(jccaccess.getComponentIdentifier().getRoot());	// Search local first.
				if(!jccaccess.getComponentIdentifier().getRoot().equals(node.getComponentIdentifier().getRoot()))
					todo.add(node.getComponentIdentifier().getRoot());	// Search remote if not found locally.
				
				Future<Icon>	ifut	= new Future<Icon>();
				doSearch(ifut, type, todo, 0);
				lookups.put(type, new Tuple2<IFuture<Icon>, List<IComponentIdentifier>>(ifut, todo));
				fut	= ifut;			
			}
		}
		
		// Update node if icon is found.
		if(fut!=null)
		{
			fut.addResultListener(new SwingDefaultResultListener<Icon>()
			{
				public void customResultAvailable(Icon result)
				{
					model.fireNodeChanged(node);
				}
				
				public void customExceptionOccurred(Exception exception)
				{
					// icon not available ignore.
					// todo: cache unavailable icons!?
				}
			});
		}
		
		return ret!=null ? ret: ICONS.getIcon("component");
	}
		
	//-------- helper methods --------
	
	/**
	 *  Start/continue a search for a component type icon with an initial platform todo list.
	 */
	protected void	doSearch(final Future<Icon> ret, final String type, final List<IComponentIdentifier> todo, final int i)
	{
		jccaccess.searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Icon>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.getExternalAccess(todo.get(i)).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Icon>(ret)
				{
					public void customResultAvailable(IExternalAccess exta)
					{
//						System.out.println("Searching for icon: "+type+" at "+exta);
						SComponentFactory.getFileTypeIcon(exta, type)
							.addResultListener(new SwingExceptionDelegationResultListener<byte[], Icon>(ret)
						{
							public void customResultAvailable(byte[] result)
							{
								if(result!=null)
								{
									Icon	icon	= new ImageIcon(result);
									icons.put(type, icon);
									ret.setResult(icon);
									
//									JFrame f = new JFrame();
//									f.add(new JLabel(icon), BorderLayout.CENTER);
//									f.pack();
//									f.show();
								}
								else
								{
									customExceptionOccurred(new RuntimeException("Icon "+type+" not found."));
								}
							}
							
							public void customExceptionOccurred(Exception exception)
							{
								if(i+1<todo.size())
								{
									doSearch(ret, type, todo, i+1);
								}
								else
								{
									super.customExceptionOccurred(exception);
								}
							}
						});
					}
				});
			}			
		});
	}
}
