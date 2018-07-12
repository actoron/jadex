package jadex.base.gui.componentviewer;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import jadex.base.SRemoteGui;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.SReflect;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.future.SwingDefaultResultListener;

/**
 *  Default panel for viewing BDI agents that include viewable capabilities. 
 */
public class DefaultComponentServiceViewerPanel extends AbstractComponentViewerPanel
{
	//-------- attributes --------

	/** The constant for the optional component viewerclass. */
	public static final String PROPERTY_COMPONENTVIEWERCLASS = "viewerpanel.componentviewerclass";
	
	//-------- attributes --------
	
	/** The panel. */
	protected JPanel panel;
	
	//-------- methods --------
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public IFuture<Void> init(final IControlCenter jcc, final IExternalAccess component)
	{
		this.panel = new JPanel(new BorderLayout());
		final Future<Void> ret = new Future<Void>();
		
		// Init interface is asynchronous but super implementation is not.
		IFuture<Void>	fut	= super.init(jcc, component);
		assert fut.isDone();
		
		// TODO: should use SRemoteGui for observing minimal platforms
//		SServiceProvider.getDeclaredServices(component)
//			.addResultListener(new IResultListener<Collection<IService>>()
//		{
//			public void resultAvailable(Collection<IService> result)
//			{
//				createPanels(component, result).addResultListener(new DelegationResultListener<Void>(ret));
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				ret.setException(exception);
//			}
//		});
		
		return ret;
	}
	
	/**
	 *  Create the panels.
	 */
	protected IFuture<Void> createPanels(final IExternalAccess exta, final Collection<IService> services)
	{
		final Future<Void> ret = new Future<Void>();
		
		AbstractJCCPlugin.getClassLoader(exta.getId(), jcc)
			.addResultListener(new SwingDefaultResultListener<ClassLoader>()
		{
			public void customResultAvailable(final ClassLoader cl)
			{
				final List<Object[]> panels = new ArrayList<Object[]>();
				
				final CounterResultListener<Void> lis = new CounterResultListener<Void>(
					services!=null ? services.size()+1 : 1, true, new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result) 
					{
		//				if(subpanels.size()==1)
		//				{
		//					Object[] tmp = (Object[])subpanels.get(0);
		//					add(((IComponentViewerPanel)tmp[1]).getComponent(), BorderLayout.CENTER);
		//				}
		//				else if(subpanels.size()>1)
						{
							JTabbedPane tp = new JTabbedPane();
							for(int i=0; i<panels.size(); i++)
							{
								Object[] tmp = (Object[])panels.get(i);
								tp.addTab((String)tmp[0], ((IAbstractViewerPanel)tmp[1]).getComponent());
							}
							panel.add(tp, BorderLayout.CENTER);
						}
						super.customResultAvailable(null);
					}	
				});
				
				// Component panel.
				Class<?>[]	classes	= getGuiClasses(exta.getModel().getProperty(PROPERTY_COMPONENTVIEWERCLASS, cl), cl);
				boolean	found	= false;
				for(int i=0; !found && i<classes.length; i++)
				{
					try
					{
						IComponentViewerPanel panel = (IComponentViewerPanel)classes[i].newInstance();
						found	= true;
						panels.add(new Object[]{"component", panel});
						panel.init(jcc, getActiveComponent()).addResultListener(lis);
					}
					catch(Exception e)
					{
						if(found)
						{
							lis.exceptionOccurred(e);
						}
					}
				}
				
				if(!found) 
				{
					lis.exceptionOccurred(new RuntimeException("No viewerclass: "+exta.getModel().getProperty(PROPERTY_COMPONENTVIEWERCLASS, cl)));
				}
				
				// Service panels.
				if(services!=null)
				{
					for(IService ser: services)
					{
						classes	= getGuiClasses(ser.getPropertyMap().get(IAbstractViewerPanel.PROPERTY_VIEWERCLASS), cl);
						found	= false;
						for(int j=0; !found && j<classes.length; j++)
						{
							try
							{
								IServiceViewerPanel panel = (IServiceViewerPanel)classes[j].newInstance();
								found	= true;
//								panels.add(new Object[]{SReflect.getInnerClassName(ser.getServiceIdentifier().getServiceType()), panel});
								panels.add(new Object[]{SReflect.getUnqualifiedTypeName(ser.getId()
									.getServiceType().getTypeName()), panel});
								panel.init(jcc, ser).addResultListener(lis);
							}
							catch(Exception e)
							{
								if(found)
								{
									lis.exceptionOccurred(e);
								}
							}
						}
						
						if(!found) 
						{
							lis.exceptionOccurred(new RuntimeException("No viewerclass: "+ser.getPropertyMap().get(IAbstractViewerPanel.PROPERTY_VIEWERCLASS)));
						}
					}
				}
			}
		});
		return ret;
	}
	
	/**
	 *  The id used for mapping properties.
	 * /
	public String getId()
	{
		return "default_bdi_viewer_panel";
	}*/

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		return panel;
	}
	
	/**
	 *  Get the gui classes for a property.
	 */
	protected static Class<?>[]	getGuiClasses(Object prop, ClassLoader cl)
	{
		List<Class<?>>	classes	= new ArrayList<Class<?>>();
		
		if(SReflect.isIterable(prop))
		{
			for(Iterator<?>	it=SReflect.getIterator(prop); it.hasNext(); )
			{
				Object	tmp	= it.next();
				Class<?>	clazz	= tmp instanceof Class? (Class<?>)tmp: tmp instanceof String? SReflect.classForName0((String)tmp, cl): null;
				if(clazz!=null)
				{
					classes.add(clazz);
				}
			}			
		}
		else
		{
			Class<?>	clazz	= prop instanceof Class? (Class<?>)prop: prop instanceof String? SReflect.classForName0((String)prop, cl): null;
			if(clazz!=null)
			{
				classes.add(clazz);
			}			
		}

		return classes.toArray(new Class<?>[0]);
	}
}
