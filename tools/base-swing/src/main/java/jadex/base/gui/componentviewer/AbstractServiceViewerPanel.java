package jadex.base.gui.componentviewer;

import javax.swing.JComponent;

import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Properties;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Simple default viewer panel.
 */
public abstract class AbstractServiceViewerPanel<T> implements IServiceViewerPanel
{
	//-------- attributes --------
	
	/** The jcc. */
	protected IControlCenter jcc;
	
	/** The service. */
	protected T service;
	
	/** True, after shutdown. */
	protected boolean	shutdown;
	
	//-------- methods --------
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public IFuture<Void> init(IControlCenter jcc, IService service)
	{
		this.jcc = jcc;
		this.service = (T) service;
		return IFuture.DONE;
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture<Void> shutdown()
	{
		assert !shutdown;
		this.shutdown	= true;
		return IFuture.DONE;
	}

	/**
	 *  Test if the panel is already shut down.
	 */
	public boolean	isShutdown()
	{
		return shutdown;
	}
	
	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return toString();
	}

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public abstract JComponent getComponent();

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public IFuture<Void> setProperties(Properties ps)
	{
		return IFuture.DONE;
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public IFuture<Properties> getProperties()
	{
		return Future.getEmptyFuture();
	}

	/**
	 *  Get the jcc.
	 *  @return the jcc.
	 */
	public IControlCenter getJCC()
	{
		return jcc;
	}
	
	/**
	 *  Test if the service is a local service.
	 */
	public boolean	isLocal()
	{
		return ((IService)getService()).getId().getProviderId().getRoot()
			.equals(getJCC().getJCCAccess().getId().getRoot());
	}
	
	/**
	 *  Get the external access of the component providing the service.
	 *  Might refer to a different platform than jcc access abnd platform access!
	 */
	public IFuture<IExternalAccess>	getServiceAccess()
	{
		final Future<IExternalAccess>	ret	= new Future<IExternalAccess>();
		getJCC().getJCCAccess().searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IExternalAccess>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.getExternalAccess(((IService)getService()).getId().getProviderId())
					.addResultListener(new DelegationResultListener<IExternalAccess>(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Get the service.
	 */
	public T getService()
	{
		return service;
	}
}
