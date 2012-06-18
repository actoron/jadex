package jadex.base.gui.componentviewer;

import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SReflect;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.transformation.annotations.Classname;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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
		IFuture	fut	= super.init(jcc, component);
		assert fut.isDone();
		
		component.scheduleStep(new IComponentStep<List>()
		{
			@Classname("Step")
//			public static final String XML_CLASSNAME = "Step"; 
			
			public IFuture<List> execute(final IInternalAccess ia)
			{
				IFuture ret = SServiceProvider.getDeclaredServices(ia.getServiceContainer());
				return ret;
			}
		}).addResultListener(new IResultListener<List>()
		{
			public void resultAvailable(List result)
			{
				createPanels(component, result).addResultListener(new DelegationResultListener(ret));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Create the panels.
	 */
	protected IFuture<Void> createPanels(final IExternalAccess exta, final List services)
	{
		final Future<Void> ret = new Future<Void>();
		
		AbstractJCCPlugin.getClassLoader(exta.getComponentIdentifier(), jcc)
			.addResultListener(new DefaultResultListener<ClassLoader>()
		{
			public void resultAvailable(final ClassLoader cl)
			{
				final List panels = new ArrayList();
				
				final CollectionResultListener lis = new CollectionResultListener(
					services.size()+1, true, new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result) 
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
						super.customResultAvailable(result);
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
					for(int i=0; i<services.size(); i++)
					{
						IService ser = (IService)services.get(i);
						classes	= getGuiClasses(ser.getPropertyMap().get(IAbstractViewerPanel.PROPERTY_VIEWERCLASS), cl);
						found	= false;
						for(int j=0; !found && j<classes.length; j++)
						{
							try
							{
								IServiceViewerPanel panel = (IServiceViewerPanel)classes[j].newInstance();
								found	= true;
//								panels.add(new Object[]{SReflect.getInnerClassName(ser.getServiceIdentifier().getServiceType()), panel});
								panels.add(new Object[]{SReflect.getUnqualifiedTypeName(ser.getServiceIdentifier()
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
