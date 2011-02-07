package jadex.base.service.remote.commands;

import jadex.base.service.remote.IRemoteCommand;
import jadex.base.service.remote.RemoteServiceManagementService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.micro.IMicroExternalAccess;

/**
 *  Command for getting a remote external access.
 */
public class RemoteGetExternalAccessCommand implements IRemoteCommand
{
	//-------- attributes --------
	
	/** The providerid (i.e. the component to start with searching). */
	protected IComponentIdentifier cid;
	
	/** The callid. */
	protected String callid;
	
	//-------- constructors --------
	
	/**
	 *  Create a new remote search command.
	 */
	public RemoteGetExternalAccessCommand()
	{
	}

	/**
	 *  Create a new remote search command.
	 */
	public RemoteGetExternalAccessCommand(IComponentIdentifier cid, String callid)
	{
		this.cid = cid;
		this.callid = callid;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IFuture execute(final IMicroExternalAccess component, RemoteServiceManagementService rsms)
	{
		final Future ret = new Future();
		
		// fetch component via provider/component id
		final IComponentIdentifier compid = cid!=null? 
			(IComponentIdentifier)cid: component.getComponentIdentifier();
			
		SServiceProvider.getServiceUpwards(component.getServiceProvider(), IComponentManagementService.class)
			.addResultListener(new IResultListener()
//			.addResultListener(component.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				cms.getExternalAccess(compid).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						IExternalAccess exta = (IExternalAccess)result;
//						ProxyInfo pi = RemoteServiceManagementService.getProxyInfo(component.getComponentIdentifier(), cid, exta);
						ret.setResult(new RemoteResultCommand(exta, null, callid));
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setResult(new RemoteResultCommand(null, exception, callid));
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(new RemoteResultCommand(null, exception, callid));
			}
		});
		
		return ret;
	}

	/**
	 *  Get the target id.
	 *  @return the target id.
	 */
	public Object getTargetId()
	{
		return cid;
	}

	/**
	 *  Set the target id.
	 *  @param providerid The target id to set.
	 */
	public void setTargetId(IComponentIdentifier providerid)
	{
		this.cid = providerid;
	}

	/**
	 *  Get the callid.
	 *  @return the callid.
	 */
	public String getCallId()
	{
		return callid;
	}

	/**
	 *  Set the callid.
	 *  @param callid The callid to set.
	 */
	public void setCallId(String callid)
	{
		this.callid = callid;
	}

}