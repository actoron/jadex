package jadex.bdi.planlib;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.ICapability;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.SReflect;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;

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
	
	//-------- attributes --------
	
	/** The panel. */
	protected JPanel panel;
	
	/** The classloader for the component. */
	protected ClassLoader cl;
	
	//-------- methods --------
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public IFuture<Void> init(final IControlCenter jcc, final IExternalAccess component)
	{
		final Future<Void> ret = new Future<Void>();
		
		this.panel = new JPanel(new BorderLayout());
		
		// Init interface is asynchronous but super implementation is not.
		IFuture<Void>	fut	= super.init(jcc, component);
		assert fut.isDone();
		
		jcc.getClassLoader(component.getModel().getResourceIdentifier())
			.addResultListener(new ExceptionDelegationResultListener<ClassLoader, Void>(ret)
		{
			public void customResultAvailable(ClassLoader result)
			{
				cl	= result;
				component.scheduleStep(new IComponentStep<Void>()
				{
					@Classname("createPanels")
					public IFuture<Void> execute(IInternalAccess ia)
					{
						final IBDIInternalAccess scope	= (IBDIInternalAccess)ia;
						String[] subcapnames = (String[])scope.getModel().getProperty(PROPERTY_INCLUDESUBCAPABILITIES, cl);
						if(subcapnames==null)
						{
							subcapnames = (String[])scope.getSubcapabilityNames();
						}
						createPanels(scope, subcapnames, ret);
					
						return IFuture.DONE;
					}
				}).addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Create the panels.
	 */
	protected void createPanels(IBDIInternalAccess scope, String[] subcapnames, Future<Void> ret)
	{
		final List<Object[]> panels = new ArrayList<Object[]>();
		
		final CounterResultListener<Void> lis = new CounterResultListener<Void>(
			subcapnames.length+1, true, new DelegationResultListener<Void>(ret)
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
						tp.addTab((String)tmp[0], ((IComponentViewerPanel)tmp[1]).getComponent());
					}
					panel.add(tp, BorderLayout.CENTER);
				}
				super.customResultAvailable(result);
			}	
		});
		
		// Agent panel.
		String clname = (String)scope.getModel().getProperty(PROPERTY_AGENTVIEWERCLASS, cl);
		if(clname!=null)
		{
			try
			{
				Class<?> clazz	= SReflect.classForName(clname, scope.getClassLoader());
				IComponentViewerPanel panel = (IComponentViewerPanel)clazz.newInstance();
				panels.add(new Object[]{"agent", panel});
				panel.init(jcc, getActiveComponent()).addResultListener(lis);
			}
			catch(Exception e)
			{
				lis.exceptionOccurred(e);
			}
		}
		else
		{
			lis.exceptionOccurred(new RuntimeException("No viewerclass: "+cl));
		}
		
		// Capability panels.
		for(int i=0; i<subcapnames.length; i++)
		{
			ICapability subcap = (ICapability)scope.getSubcapability(subcapnames[i]);
			Object clid = subcap.getModel().getProperty(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, cl);
			Class<?> clazz	= null;
			if(clid instanceof Class)
			{
				clazz	= (Class<?>)clid;
			}
			else if(clid instanceof String)
			{
				clazz	= SReflect.classForName0((String)clid, subcap.getClassLoader());
			}
			
			if(clazz!=null)
			{
				try
				{
					IComponentViewerPanel panel = (IComponentViewerPanel)clazz.newInstance();
					panels.add(new Object[]{subcapnames[i], panel});
					panel.init(jcc, subcap.getExternalAccess()).addResultListener(lis);
				}
				catch(Exception e)
				{
					lis.exceptionOccurred(e);
				}
			}
			else if(clid!=null)
			{
				lis.exceptionOccurred(new RuntimeException("Cannot create panel: "+clid));
			}
			else
			{
				lis.resultAvailable(null);
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
