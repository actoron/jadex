package jadex.platform.service.remote.commands;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Security;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.transformation.annotations.Alias;
import jadex.platform.service.remote.IRemoteCommand;
import jadex.platform.service.remote.RemoteReference;
import jadex.platform.service.remote.RemoteServiceManagementService;

/**
 *  Distributed garbage collection 'removeRef(rr)' command.
 */
@Alias("jadex.base.service.remote.commands.RemoteDGCRemoveReferenceCommand")
public class RemoteDGCRemoveReferenceCommand extends AbstractRemoteCommand
{
	//-------- attributes --------
	
	/** The remote reference. */
	protected RemoteReference rr;
	
	/** The reference holder (rms). */
	protected IComponentIdentifier holder;

	/** The callid. */
	protected String callid;
	
	//-------- constructors --------
	
	/**
	 *  Create a new add reference command.
	 */
	public RemoteDGCRemoveReferenceCommand()
	{
	}

	/**
	 *  Create a new add reference command.
	 */
	public RemoteDGCRemoveReferenceCommand(RemoteReference rr, IComponentIdentifier holder, String callid)
	{
//		System.out.println("result command: "+result);
		this.rr = rr;
		this.holder = holder;
		this.callid = callid;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the security level of the request.
	 */
	public String	getSecurityLevel()
	{
		// No security issues here.
		return Security.UNRESTRICTED;
	}
	
	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IIntermediateFuture<IRemoteCommand> execute(IExternalAccess component, RemoteServiceManagementService rsms)
	{
		final IntermediateFuture<IRemoteCommand> ret = new IntermediateFuture<IRemoteCommand>();
		try
		{
			rsms.getRemoteReferenceModule().removeRemoteReference(rr, holder);
//			ret.setResult(new RemoteResultCommand(null, null, callid, false));
			ret.addIntermediateResult(new RemoteResultCommand(null, null, null, null, callid, 
				false, null, getNonFunctionalProperties()));
			ret.setFinished();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ret.setException(e);
		}
		return ret;
	}
	
	//-------- getter/setter methods --------
	
	/**
	 *  Get the remote reference.
	 *  @return The remote reference.
	 */
	public RemoteReference getRemoteReference()
	{
		return rr;
	}

	/**
	 *  Set the rr.
	 *  @param rr The rr to set.
	 */
	public void setRemoteReference(RemoteReference rr)
	{
		this.rr = rr;
	}
	
	/**
	 *  Get the holder.
	 *  @return the holder.
	 */
	public IComponentIdentifier getHolder()
	{
		return holder;
	}

	/**
	 *  Set the holder.
	 *  @param holder The holder to set.
	 */
	public void setHolder(IComponentIdentifier holder)
	{
		this.holder = holder;
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
