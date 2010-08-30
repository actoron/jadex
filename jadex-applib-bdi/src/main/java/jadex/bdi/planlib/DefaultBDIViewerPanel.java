package jadex.bdi.planlib;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Properties;
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
 * 
 */
public class DefaultBDIViewerPanel extends JPanel implements IComponentViewerPanel
{
	/** The jcc. */
	protected IControlCenter jcc;
	
	/** The external access. */
	protected IBDIExternalAccess component;
	
	/**
	 * 
	 */
	public DefaultBDIViewerPanel()
	{
		
	}
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public IFuture init(final IControlCenter jcc, IExternalAccess component)
	{
		final Future ret = new Future();
		
		this.jcc = jcc;
		this.component = (IBDIExternalAccess)component;
		
		SServiceProvider.getService(jcc.getServiceProvider(), ILibraryService.class)
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				DefaultBDIViewerPanel.this.component.getSubcapabilityNames().addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						String[] subcapnames = (String[])result;
						if(subcapnames!=null)
						{
							final List subpanels = new ArrayList();
							final CollectionResultListener lis = new CollectionResultListener(
								subcapnames.length, true, new DelegationResultListener(ret)
							{
								public void customResultAvailable(Object source, Object result) 
								{
									if(subpanels.size()==1)
									{
										Object[] tmp = (Object[])subpanels.get(0);
										add(((IComponentViewerPanel)tmp[1]).getComponent(), BorderLayout.CENTER);
									}
									else if(subpanels.size()>1)
									{
										JTabbedPane tp = new JTabbedPane();
										for(int i=0; i<subpanels.size(); i++)
										{
											Object[] tmp = (Object[])subpanels.get(i);
											tp.addTab((String)tmp[0], ((IComponentViewerPanel)tmp[1]).getComponent());
										}
										add(tp, BorderLayout.CENTER);
									}
									super.customResultAvailable(source, result);
								}	
							});
							for(int i=0; i<subcapnames.length; i++)
							{
								final String subcapaname = subcapnames[i];
								DefaultBDIViewerPanel.this.component.getExternalAccess(subcapnames[i])
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
													subpanels.add(new Object[]{subcapaname, panel});
													panel.init(jcc, subcap).addResultListener(lis);
												}
												catch(Exception e)
												{
													lis.exceptionOccurred(source, e);
//													ret.setException(new RuntimeException("Could not init viewer class: "+clname));
												}
											}
											
											public void exceptionOccurred(Object source, Exception exception)
											{
												lis.exceptionOccurred(source, exception);
//												ret.setException(exception);
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
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture shutdown()
	{
		return new Future(null);
	}

	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return "default_bdi";
	}

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		return this;
	}

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public void setProperties(Properties ps)
	{
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public Properties	getProperties()
	{
		return null;
	}
}
