package jadex.base.gui.componentviewer;

import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.IService;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.library.ILibraryService;

import java.awt.BorderLayout;
import java.util.ArrayList;
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
	
	/** The panel. */
	protected JPanel panel;
	
	//-------- methods --------
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public IFuture init(final IControlCenter jcc, IExternalAccess component)
	{
		final Future ret = new Future();
		
		this.panel = new JPanel(new BorderLayout());
		
		// Init interface is asynchronous but super implementation is not.
		IFuture	fut	= super.init(jcc, component);
		assert fut.isDone();
		
		component.scheduleStep(new IComponentStep()
		{
			public Object execute(final IInternalAccess ia)
			{
				SServiceProvider.getDeclaredService(ia.getServiceProvider(), ILibraryService.class)
					.addResultListener(ia.createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						final ILibraryService libservice = (ILibraryService)result;
						SServiceProvider.getDeclaredServices(ia.getServiceProvider())
							.addResultListener(ia.createResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								List services = (List)result;
								createPanels(libservice, ia, services, ret);
							}
						}));
					}
				}));
				
				return null;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Create the panels.
	 */
	protected void createPanels(ILibraryService libservice, IInternalAccess ia, List services, Future ret)
	{
		final List panels = new ArrayList();
		
		final CollectionResultListener lis = new CollectionResultListener(
			//services.size()+1, true, new DelegationResultListener(ret)
			services.size(), true, new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result) 
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
						tp.addTab((String)tmp[0], ((IServiceViewerPanel)tmp[1]).getComponent());
					}
					panel.add(tp, BorderLayout.CENTER);
				}
				super.customResultAvailable(source, result);
			}	
		});
		
		// Component panel.
//		String clname = (String)ia.getModel().getProperties().get(IAbstractViewerPanel.PROPERTY_VIEWERCLASS);
//		if(clname!=null)
//		{
//			try
//			{
//				Class clazz	= SReflect.classForName(clname, ia.getModel().getClassLoader());
//				IComponentViewerPanel panel = (IComponentViewerPanel)clazz.newInstance();
//				panels.add(new Object[]{"component", panel});
//				panel.init(jcc, getActiveComponent()).addResultListener(lis);
//			}
//			catch(Exception e)
//			{
//				lis.exceptionOccurred(this, e);
//			}
//		}
//		else
//		{
//			lis.exceptionOccurred(this, new RuntimeException("Could not init viewer class: "+clname));
//		}
		
		// Service panels.
		if(services!=null)
		{
			for(int i=0; i<services.size(); i++)
			{
				IService ser = (IService)services.get(i);
				String clname = (String)ser.getPropertyMap().get(IAbstractViewerPanel.PROPERTY_VIEWERCLASS);
				if(clname!=null)
				{
					try
					{
						Class clazz	= SReflect.classForName(clname, libservice.getClassLoader());
						IServiceViewerPanel panel = (IServiceViewerPanel)clazz.newInstance();
						panels.add(new Object[]{SReflect.getInnerClassName(ser.getServiceIdentifier().getServiceType()), panel});
						panel.init(jcc, ser).addResultListener(lis);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						lis.exceptionOccurred(null, e);
					}
				}
				else
				{
					lis.exceptionOccurred(null, null);
				}
			}
		}
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
}
