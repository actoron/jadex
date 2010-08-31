package jadex.bdi.planlib;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
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
public class DefaultBDIViewerPanel extends AbstractComponentViewerPanel
{
	//-------- constants --------
	
	/** The constant for the agent optional viewerclass. */
	public static final String PROPERTY_AGENTVIEWERCLASS = "bdiviewerpanel.agentviewerclass";
	
	/** The constant for the agent optional viewerclass. */
	public static final String PROPERTY_INCLUDESUBCAPABILITIES = "bdiviewerpanel.includesubcapabilities";
	
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
		final IBDIExternalAccess bdiagent = (IBDIExternalAccess)component;
		
		super.init(jcc, component).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				SServiceProvider.getService(jcc.getServiceProvider(), ILibraryService.class)
					.addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						final ILibraryService ls = (ILibraryService)result;
						
						bdiagent.getPropertybase().getProperty(PROPERTY_INCLUDESUBCAPABILITIES)
							.addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								if(result!=null)
								{
									String[] subcapnames = (String[])result;
									createPanels(subcapnames, ls, ret);
								}
								else
								{
									bdiagent.getSubcapabilityNames().addResultListener(new IResultListener()
									{
										public void resultAvailable(Object source, Object result)
										{
											String[] subcapnames = (String[])result;
											createPanels(subcapnames, ls, ret);
										}
										
										public void exceptionOccurred(Object source, Exception exception)
										{
											ret.setException(exception);
										}
									});
								}
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								ret.setException(exception);
							}
						});
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Create the panels.
	 */
	protected void createPanels(final String[] subcapnames, final ILibraryService ls, final Future ret)
	{
		final IBDIExternalAccess bdiagent = (IBDIExternalAccess)getActiveComponent();
		final List panels = new ArrayList();
		
		final CollectionResultListener lis = new CollectionResultListener(
			subcapnames.length+1, true, new DelegationResultListener(ret)
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
						tp.addTab((String)tmp[0], ((IComponentViewerPanel)tmp[1]).getComponent());
					}
					panel.add(tp, BorderLayout.CENTER);
				}
				super.customResultAvailable(source, result);
			}	
		});
		
		// Agent panel.
		bdiagent.getPropertybase().getProperty(PROPERTY_AGENTVIEWERCLASS)
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				String clname = (String)result;
				if(clname!=null)
				{
					try
					{
						Class clazz	= SReflect.classForName(clname, ls.getClassLoader());
						IComponentViewerPanel panel = (IComponentViewerPanel)clazz.newInstance();
						panels.add(new Object[]{"agent", panel});
						panel.init(jcc, bdiagent).addResultListener(lis);
					}
					catch(Exception e)
					{
						lis.exceptionOccurred(source, e);
//							ret.setException(new RuntimeException("Could not init viewer class: "+clname));
					}
				}
				else
				{
					lis.exceptionOccurred(source, null);
				}
			}
			public void exceptionOccurred(Object source, Exception exception)
			{
				lis.exceptionOccurred(source, exception);
			}
		});
		
		// Capability panels.
		if(subcapnames!=null)
		{
			for(int i=0; i<subcapnames.length; i++)
			{
				final String subcapaname = subcapnames[i];
				bdiagent.getExternalAccess(subcapnames[i])
					.addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						final IBDIExternalAccess subcap = (IBDIExternalAccess)result;
						subcap.getPropertybase().getProperty(IAbstractViewerPanel.PROPERTY_VIEWERCLASS).addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								String clname = (String)result;
								try
								{
									Class clazz	= SReflect.classForName(clname, ls.getClassLoader());
									IComponentViewerPanel panel = (IComponentViewerPanel)clazz.newInstance();
									panels.add(new Object[]{subcapaname, panel});
									panel.init(jcc, subcap).addResultListener(lis);
								}
								catch(Exception e)
								{
									lis.exceptionOccurred(source, e);
//									ret.setException(new RuntimeException("Could not init viewer class: "+clname));
								}
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								lis.exceptionOccurred(source, exception);
//								ret.setException(exception);
							}
						});
						
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setExceptionIfUndone(exception);
					}
				});
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
