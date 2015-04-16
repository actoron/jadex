package jadex.platform.service.dht;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RingNode implements IRingNode
{
	protected static final long	FIX_DELAY	= 10000;

	protected static final long	STABILIZE_DELAY	= 5000;

	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	private IID	myId;
	
	private Fingertable finger;
	
	private Logger logger;
	
	public RingNode()
	{
//		LogManager.getLogManager().reset();
		logger = Logger.getLogger(this.getClass().getName());
//		logger.setUseParentHandlers(false);
//		logger.addHandler(new ConsoleHandler() {
//			
//		});
	}
	
	@ServiceStart
	public void onStart() {
		init(ID.get(agent.getComponentIdentifier()));
		
		log("Started ringnode");
		
		IComponentStep<Void> step = new IComponentStep<Void>()
		{

			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IRequiredServicesFeature componentFeature = agent.getComponentFeature(IRequiredServicesFeature.class);
				ITerminableIntermediateFuture<Object> requiredServices = componentFeature.getRequiredServices("ringnodes");
				
				requiredServices.addResultListener(new IntermediateDefaultResultListener<Object>()
				{
					List<IRingNode> currentStores = new ArrayList<IRingNode>();
					
					boolean join = false;
					
					@Override
					public void intermediateResultAvailable(Object result)
					{
						IRingNode other = (IRingNode)result;
						final IComponentIdentifier cid = ((IService)result).getServiceIdentifier().getProviderId();
						currentStores.add(other);
						if (!join) {
							if (!other.getId().get().equals(myId)) {
								join(other);
								join = true;
							}
						}
					}
					
					@Override
					public void finished()
					{
						log("finish");
						if (!join) {
							join(null);
						}
						super.finished();
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("Error: ");
						exception.printStackTrace();
						super.exceptionOccurred(exception);
					}
				});
			return Future.DONE;
			}
		};
		
		agent.getExternalAccess().scheduleStep(step).addResultListener(new DefaultResultListener<Void>()
		{

			@Override
			public void resultAvailable(Void result)
			{
				agent.getExternalAccess().scheduleStep(fixStep, FIX_DELAY);
				agent.getExternalAccess().scheduleStep(stabilizeStep, STABILIZE_DELAY);
			}
		});
	}

	void init(IID id)
	{
		this.myId = id;
		finger = new Fingertable(this);
	}
	
	
	
	@Override
	public IFuture<IID> getId()
	{
		return new Future<IID>(myId);
	}
	
	public IFuture<IRingNode> findSuccessor(IID id) {
//		log("findSuccessor for: " + id);		
		IRingNode nDash = findPredecessor(id);
//		IRingNode suc = nDash.findSuccessor(id).get();
		return nDash.getSuccessor();
	}
	
	protected IRingNode findPredecessor(IID id)
	{
//		log("findPredecessor for: " + id);
		IRingNode nDash = this;
		while (!id.isInInterval(ID.get(nDash), ID.get(nDash.getSuccessor().get()), false, true)) {
//			log("findPredecessor while loop id " + id + " not in interval: " + ID.get(nDash) + " , " + (ID.get(nDash.getSuccessor().get())));
			nDash = nDash.getClosestPrecedingFinger(id).get();
			if (nDash == null) {
				nDash = this;
				break;
			}
		}
		return nDash;
	}
	
	@Override
	public IFuture<IRingNode> getSuccessor()
	{
		return new Future<IRingNode>(finger.getSuccessor());
	}
	
	public IFuture<IRingNode> getPredecessor()
	{
		return new Future<IRingNode>(finger.getPredecessor());
	}

	public IFuture<Void> setPredecessor(IRingNode predecessor)
	{
		this.finger.setPredecessor(predecessor);
		return Future.DONE;
	}

	@Override
	public IFuture<IRingNode> getClosestPrecedingFinger(IID id)
	{
//		log("getClosestPrecedingFinger for " + id);
		return finger.getClosestPrecedingFinger(id);
	}
	
	
	@Override
	public IFuture<IComponentIdentifier> getCID()
	{
		return new Future<IComponentIdentifier>(this.agent.getComponentIdentifier());
	}

	protected IFuture<Void> join(IRingNode nDash) {
//		if (nDash != null) {
//			log("joining " + nDash);
//			finger.init(nDash);
//			finger.updateOthers();
//			// TODO move keys in (predecessor,n] from successor
//		} else {
//			log("joining (no peers)");
//			finger.setPredecessor(this);
//		}
		final Future<Void> future = new Future<Void>();
		finger.setPredecessor(null);
		if (nDash != null) {
			nDash.findSuccessor(myId).addResultListener(new MyResultListener<IRingNode>(nDash)
			{

				@Override
				public void resultAvailable(IRingNode suc)
				{
					finger.setSuccessor(suc);
					log("Join complete with successor: " + suc);
					future.setResult(null);
				}
			});
		} else {
			log("Join complete without successor.");
			future.setResult(null);
		}
		return future;
	}
	

	@Override
	public IFuture<Void> notify(IRingNode nDash)
	{
		Future<Void> future = new Future<Void>();
		IRingNode pre = getPredecessor().get();
		if (pre == null || nDash.getId().get().isInInterval(pre.getId().get(), myId)) {
			setPredecessor(nDash).addResultListener(new DelegationResultListener<Void>(future));
		} else {
			future.setResult(null);
		}
		return future;
	}
	
	/**
	 * Informs about new successor relations to this node.
	 * @return
	 */
	protected IFuture<Void> stabilize() {
		final Future<Void> ret = new Future<Void>();
		final IRingNode successor = finger.getSuccessor();
		successor.getPredecessor().addResultListener(new MyResultListener<IRingNode>(successor)
		{

			@Override
			public void resultAvailable(IRingNode x)
			{
				if (x != null && x.getId().get().isInInterval(myId, successor.getId().get())) {
					finger.setSuccessor(x);
				}
				successor.notify(RingNode.this).addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		return ret;
	}
	
	protected IFuture<Void> fixFingers() {
		return finger.fixFingers();
	}

//	@Override
//	public IFuture<Void> updateFingerTable(IRingNode s, int i)
//	{
	// if s is the ith finger of me, update my finger table with s
//		finger.updateFingerTable(s, i);
//		return Future.DONE;
//	}

	/** Helper methods **/
	
	private void log(String message) {
		logger.log(Level.INFO, myId + ": " + message);
	}

	public IFuture<List<IFinger>> getFingers()
	{
		ArrayList<IFinger> arrayList = new ArrayList<IFinger>(finger.fingers.length);
		for(int i = 0; i < finger.fingers.length; i++)
		{
			arrayList.add(finger.fingers[i]);
		}
		return new Future<List<IFinger>>(arrayList);
	}

	public void setFinger(Fingertable finger)
	{
		this.finger = finger;
	}
	
	@Override
	public String toString()
	{
		return "Ringnode (" + myId + ")";
	}
	
	IComponentStep<Void> stabilizeStep = new IComponentStep<Void>()
	{

		@Override
		public IFuture<Void> execute(IInternalAccess ia)
		{
//			log("executing stabilize");
			stabilize();
			ia.getExternalAccess().scheduleStep(stabilizeStep, STABILIZE_DELAY);
			return Future.DONE;
		}
	};
	
	IComponentStep<Void> fixStep = new IComponentStep<Void>()
	{

		@Override
		public IFuture<Void> execute(IInternalAccess ia)
		{
//			log("executing fixfingers");
			fixFingers();
			ia.getExternalAccess().scheduleStep(fixStep, FIX_DELAY);
			return Future.DONE;
		}
	};
	
	
	abstract class MyResultListener<T> implements IResultListener<T> {

		private IRingNode	rn;

		public MyResultListener(IRingNode rn)
		{
			this.rn = rn;
		}
		
		@Override
		public void exceptionOccurred(Exception exception)
		{
//			final ProxyHolder proxyHolder = proxies.get(id);
//			if (proxyHolder != null && proxyHolder.spaceObject != null) {
//				System.out.println("Removing proxy: " + proxyHolder.spaceObject);
//				proxies.remove(proxyHolder);
//				EnvironmentService.getSpace(agent).addResultListener(new DefaultResultListener<IEnvironmentSpace>()
//				{
//
//					@Override
//					public void resultAvailable(IEnvironmentSpace space)
//					{
//						space.destroySpaceObject(proxyHolder.spaceObject.getId());
//					}
//				});
//			}
		}
		
	}


	public Fingertable getFingerTable()
	{
		return finger;
	}
}
