package jadex.base.service.remote.commands;

import jadex.base.service.remote.RemoteReference;
import jadex.base.service.remote.RemoteServiceManagementService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.IMicroExternalAccess;

/**
 *  Distributed garbage collection 'removeRef(rr)' command.
 */
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
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IFuture execute(IMicroExternalAccess component, RemoteServiceManagementService rsms)
	{
		final Future ret = new Future();
		try
		{
			rsms.getRemoteReferenceModule().removeRemoteReference(rr, holder);
			ret.setResult(new RemoteResultCommand(null, null, callid, false));
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
