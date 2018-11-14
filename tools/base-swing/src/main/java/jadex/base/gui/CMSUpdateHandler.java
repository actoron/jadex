package jadex.base.gui;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 *  A change handler which receives remote CMS events and delegates to the registered listeners.
 */
public class CMSUpdateHandler
{
	//-------- attributes --------

	/** The local external access. */
	protected IExternalAccess	access;
	
	/** The subscriptions for the remote CMSs (cms cid->(sub, cnt)). */
	protected Map<IComponentIdentifier, Tuple2<ISubscriptionIntermediateFuture<CMSStatusEvent>, Integer>>	listeners;
	
	//-------- constructors --------
	
	/**
	 *  Create a CMS update handler.
	 */
	public CMSUpdateHandler(IExternalAccess access)
	{
		this.access	= access;
	}
	
	//-------- methods --------
	
	/**
	 *  Dispose the handler for triggering remote listener removal.
	 */
	public IFuture<Void>	dispose()
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		for(Tuple2<ISubscriptionIntermediateFuture<CMSStatusEvent>, Integer> tup: SUtil.notNull(listeners).values())
		{
			tup.getFirstEntity().terminate();
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Add a CMS listener.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> addCMSListener(final IComponentIdentifier cid)
	{
		//Thread.dumpStack();
		
		assert SwingUtilities.isEventDispatchThread();
		ISubscriptionIntermediateFuture<CMSStatusEvent>	ret;
		
		if(listeners==null)
			listeners	= new LinkedHashMap<IComponentIdentifier, Tuple2<ISubscriptionIntermediateFuture<CMSStatusEvent>,Integer>>();
		Tuple2<ISubscriptionIntermediateFuture<CMSStatusEvent>, Integer> tup = listeners.get(cid);
		
		// If exists increase counter.
		if(tup!=null)
		{
			ret	= tup.getFirstEntity();
			Integer	cnt	= Integer.valueOf((tup.getSecondEntity().intValue()+1));
			tup	= new Tuple2<ISubscriptionIntermediateFuture<CMSStatusEvent>, Integer>(ret, cnt);
			listeners.put(cid, tup);
		}
		
		// Else register new.
		else
		{
			final SubscriptionIntermediateFuture<CMSStatusEvent>	fut	= new SubscriptionIntermediateFuture<CMSStatusEvent>();
			fut.setTerminationCommand(new ITerminationCommand()
			{
				@Override
				public boolean checkTermination(Exception reason)
				{
					Tuple2<ISubscriptionIntermediateFuture<CMSStatusEvent>, Integer>	tup	= null;
					if(listeners!=null)
					{
						tup	= listeners.get(cid);
						Integer	cnt	= Integer.valueOf((tup.getSecondEntity().intValue()-1));
						tup	= new Tuple2<ISubscriptionIntermediateFuture<CMSStatusEvent>, Integer>(tup.getFirstEntity(), cnt);
						listeners.put(cid, tup);						
					}
					boolean	remove	= tup==null || tup.getSecondEntity().intValue()<=0;
					
					if(remove && listeners!=null)
					{
						listeners.remove(cid);
					}
					return remove;
				}
				
				@Override
				public void terminated(Exception reason)
				{
					// NOP, done in check
				}
			});
			ret	= fut;
			Integer	cnt	= Integer.valueOf(1);
			tup	= new Tuple2<ISubscriptionIntermediateFuture<CMSStatusEvent>, Integer>(ret, cnt);
			listeners.put(cid, tup);
			// cannot invoke method on remote proxy as send message is called then on wrong thread (not component)
			//cms.listenToAll().addResultListener(new IntermediateDelegationResultListener<CMSStatusEvent>(fut));
			access.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
//					cms.listenToAll().addResultListener(new IntermediateDelegationResultListener<CMSStatusEvent>(fut));
					SComponentManagementService.listenToAll(ia).addResultListener(new IntermediateDelegationResultListener<CMSStatusEvent>(fut));
					
					return IFuture.DONE;
				}
			});
		}
		
		return ret;
	}
}
