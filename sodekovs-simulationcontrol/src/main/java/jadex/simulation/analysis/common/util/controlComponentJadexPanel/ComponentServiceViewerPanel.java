package jadex.simulation.analysis.common.util.controlComponentJadexPanel;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.Properties;
import jadex.commons.SReflect;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.xml.annotation.XMLClassname;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Jadex:
 *  Default panel for viewing BDI agents that include viewable capabilities. 
 *  
 *  Extended by 5haubeck
 */
public class ComponentServiceViewerPanel extends AbstractComponentViewerPanel
{
	//-------- attributes --------

	/** The constant for the optional componentP viewerclass. */
	public static final String PROPERTY_COMPONENTVIEWERCLASS = "viewerpanel.componentviewerclass";
	
	//-------- attributes --------
	
	/** The panel. */
	protected JPanel panel;
	
	//-------- methods --------
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param componentP The componentP.
	 */
	public IFuture init(final IControlCenter jcc, final IExternalAccess component)
	{
		this.panel = new JPanel(new BorderLayout());
		final Future ret = new Future();
		
		// Init interface is asynchronous but super implementation is not.
		IFuture	fut	= super.init(jcc, component);
		assert fut.isDone();
		
		component.scheduleStep(new IComponentStep()
		{
			@XMLClassname("Step")
//			public static final String XML_CLASSNAME = "Step"; 
			
			public Object execute(final IInternalAccess ia)
			{
				final Future ret = new Future();
				SServiceProvider.getDeclaredServices(ia.getServiceContainer())
					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)));
				return ret;
			}
		}).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				createPanels(component, (List)result).addResultListener(new DelegationResultListener(ret));
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
	protected IFuture createPanels(final IExternalAccess exta, final List services)
	{
		final Future ret = new Future();
		
		AbstractJCCPlugin.getClassLoader(exta.getComponentIdentifier(), jcc)
			.addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				final ClassLoader cl = (ClassLoader)result;
		
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
//							
							panel.add(tp, BorderLayout.CENTER);
						}
						super.customResultAvailable(result);
					}	
				});
				
				// Component panel.
				Object clid = exta.getModel().getProperty(PROPERTY_COMPONENTVIEWERCLASS);
				Class clazz = clid instanceof Class? (Class)clid: clid instanceof String? SReflect.classForName0((String)clid, cl): null;
				if(clid!=null)
				{
					try
					{
						IComponentViewerPanel panel = (IComponentViewerPanel)clazz.newInstance();
						panels.add(new Object[]{"Kontrollkomponente", panel});
						panel.init(jcc, getActiveComponent()).addResultListener(lis);
					}
					catch(Exception e)
					{
						lis.exceptionOccurred(e);
					}
				}
				else 
				{
					lis.exceptionOccurred(new RuntimeException("No viewerclass: "+clid));
				}
				
				// Service panels.
				if(services!=null)
				{
					for(int i=0; i<services.size(); i++)
					{
						IService ser = (IService)services.get(i);
						clid = ser.getPropertyMap()!=null ? ser.getPropertyMap().get(IAbstractViewerPanel.PROPERTY_VIEWERCLASS) : null;
						clazz = clid instanceof Class? (Class)clid: clid instanceof String? SReflect.classForName0((String)clid, cl): null;
						if(clid!=null)
						{
							try
							{
								IServiceViewerPanel panel = (IServiceViewerPanel)clazz.newInstance();
								panels.add(new Object[]{SReflect.getInnerClassName(ser.getServiceIdentifier().getServiceType()), panel});
								panel.init(jcc, ser).addResultListener(lis);
							}
							catch(Exception e)
							{
								System.out.println("err: "+clid+" "+clazz);
								e.printStackTrace();
								lis.exceptionOccurred(e);
							}
						}
						else
						{
							lis.exceptionOccurred(null);
						}
					}
				}
			}
		});
		return ret;
	}
	
	@Override
	public IFuture getProperties()
	{
		return new Future(null);
	}

	/**
	 *  The componentP to be shown in the gui.
	 *  @return	The componentP to be displayed.
	 */
	public JComponent getComponent()
	{
		return panel;
	}
	
	@Override
	public IFuture setProperties(Properties ps)
	{
		return new Future(null);
	}
	
	
}
