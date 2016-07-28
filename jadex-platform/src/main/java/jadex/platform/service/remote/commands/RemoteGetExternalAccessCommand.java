package jadex.platform.service.remote.commands;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.transformation.annotations.Alias;
import jadex.platform.service.remote.IRemoteCommand;
import jadex.platform.service.remote.RemoteServiceManagementService;

/**
 *  Command for getting a remote external access.
 */
@Alias("jadex.base.service.remote.commands.RemoteGetExternalAccessCommand")
public class RemoteGetExternalAccessCommand extends AbstractRemoteCommand
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
	 *  Get the security level of the request.
	 */
	public String	getSecurityLevel()
	{
		// Todo: customize security levels for external accesses?
		// Specify security level at component start!?
		return Security.PASSWORD;
	}
	
	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IIntermediateFuture<IRemoteCommand> execute(final IExternalAccess component, RemoteServiceManagementService rsms)
	{
		final IntermediateFuture<IRemoteCommand> ret = new IntermediateFuture<IRemoteCommand>();
		
		// fetch component via provider/component id
		final IComponentIdentifier compid = cid!=null? 
			(IComponentIdentifier)cid: component.getComponentIdentifier();
			
		SServiceProvider.getService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new IResultListener<IComponentManagementService>()
//			.addResultListener(component.createResultListener(new IResultListener()
		{
			public void resultAvailable(IComponentManagementService cms)
			{
//				ServiceCall	next	= ServiceCall.getOrCreateNextInvocation();
//				next.setProperty("debugsource", "RemoteGetExternalAccessCommand.execute()");
				
//				IComponentManagementService cms = (IComponentManagementService)result;
				cms.getExternalAccess(compid).addResultListener(new IResultListener<IExternalAccess>()
				{
					public void resultAvailable(IExternalAccess exta)
					{
//						IExternalAccess exta = (IExternalAccess)result;
//						ProxyInfo pi = RemoteServiceManagementService.getProxyInfo(component.getComponentIdentifier(), cid, exta);
//						ret.setResult(new RemoteResultCommand(exta, null, callid, true));
						ret.addIntermediateResult(new RemoteResultCommand(null, getSender(), exta, null, callid, 
							true, null, getNonFunctionalProperties()));
						ret.setFinished();
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						ret.setResult(new RemoteResultCommand(null, exception, callid, false));
						ret.addIntermediateResult(new RemoteResultCommand(null, getSender(), null, exception, callid, false, 
							null, getNonFunctionalProperties()));
						ret.setFinished();
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				ret.setResult(new RemoteResultCommand(null, exception, callid, false));
				ret.addIntermediateResult(new RemoteResultCommand(null, getSender(), null, exception, callid, false, 
					null, getNonFunctionalProperties()));
				ret.setFinished();
			}
		});
		
		return ret;
	}

	/**
	 *  Get the target id.
	 *  @return the target id.
	 */
	public IComponentIdentifier getTargetId()
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