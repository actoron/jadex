package jadex.platform.service.message;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.platform.service.transport.intravm.IntravmTransportAgent;

/**
 *  Agent that implements TCP message transport.
 */
@Agent
public class IntravmTestTransportAgent extends IntravmTransportAgent
{
	/** Future to indicate agent init is done. */
	protected Future<Void>	initing	= new Future<Void>();
	
	/** Future to indicate service start is done. */
	protected IFuture<Void>	starting;
	
	/** Future to kick off service init. */
	protected Future<Void>	dostart	= new Future<Void>();
	
	/** Future to return from service init. */
	protected Future<Void>	doret	= new Future<Void>();
	
	//-------- state methods --------
	
	/**
	 *  Check if agent init is done.
	 */
	public IFuture<Void> initing()
	{
		return initing;
	}
	
	/**
	 *  Check if service start is done.
	 */
	public IFuture<Void> starting()
	{
		return starting;
	}
	
	/**
	 *  Kick off service start and wait.
	 */
	public IFuture<Void>	kickoff()
	{
		dostart.setResult(null);
		return starting;
	}
	
	/**
	 *  Let agent return from service init.
	 */
	public void	doReturn()
	{
		doret.setResult(null);
	}
	
	//-------- impl --------
	
	@Override
	public IFuture<Void> start()
	{
		return super.start();
	}
	
	/**
	 *  Heisendebug handshake issue
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IFuture<Void> startService()
	{
		// Called twice due to self impl!?
		if(starting==null)
		{
			initing.setResult(null);
			starting	= (IFuture<Void>) dostart.then(it -> super.startService());
			return doret;
		}
		else
		{
			return IFuture.DONE;
		}
	}
}
