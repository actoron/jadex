package jadex.platform.service.dht;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.dht.IDebugRingNode;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.commons.DebugException;
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
public class RingNode implements IRingNode, IDebugRingNode
{
	protected static final long	FIX_DELAY	= 10000;

	protected static final long	STABILIZE_DELAY	= 5000;

	private static final long	REMOTE_TIMEOUT	= 2000;
	
//	private static final long	REMOTE_LONG_TIMEOUT	= REMOTE_TIMEOUT * 2;

	private State state = State.UNJOINED;
	
	enum State {
		JOINED, UNJOINED
	}

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
		
		logger.log(Level.INFO, "created ringnode");
	}
	
	@ServiceStart
	public void onStart() {
		if (this.myId != null) {
			return;
		}
		init(ID.get(agent.getComponentIdentifier()));
		
		log("Started ringnode");
		
		IComponentStep<Void> searchStep = new IComponentStep<Void>()
		{
			
			IComponentStep<Void> thisStep = this;

			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IRequiredServicesFeature componentFeature = agent.getComponentFeature(IRequiredServicesFeature.class);
				ITerminableIntermediateFuture<Object> requiredServices = componentFeature.getRequiredServices("ringnodes");
				
				requiredServices.addResultListener(new IntermediateDefaultResultListener<Object>()
				{
					boolean join = false;
					
					@Override
					public void intermediateResultAvailable(Object result)
					{
						IRingNode other = (IRingNode)result;
//						final IComponentIdentifier cid = ((IService)result).getServiceIdentifier().getProviderId();
						if (!join) {
							if (!other.getId().get().equals(myId)) {
								join = join(other).get(REMOTE_TIMEOUT);
							}
						}
					}
					
					@Override
					public void finished()
					{
						log("finish");
						if (!join && state != State.JOINED) {
//							join(null);
							// re-schedule search
							agent.getExternalAccess().scheduleStep(thisStep, FIX_DELAY);
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
		
		agent.getExternalAccess().scheduleStep(searchStep).addResultListener(new DefaultResultListener<Void>()
		{

			@Override
			public void resultAvailable(Void result)
			{
			}
		});
	}

	void init(IID id)
	{
		this.myId = id;
		IRingNode me = agent.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(IRingNode.class);
		
		finger = new Fingertable(((IService) me).getServiceIdentifier(), myId, this);
	}
	
	@Override
	public IFuture<IID> getId()
	{
		return new Future<IID>(myId);
	}
	
	public IFuture<IFinger> findSuccessor(IID id) {
		final Future<IFinger> ret = new Future<IFinger>();
//		log("findSuccessor for: " + id);		
		final IFinger nDash = findPredecessor(id).get();
//		IRingNode suc = nDash.findSuccessor(id).get();
		getRingService(nDash).addResultListener(new MyResultListener<IRingNode>(nDash, ret, null)
		{

			@Override
			public void resultAvailable(IRingNode result)
			{
				result.getSuccessor().addResultListener(new MyResultListener<IFinger>(nDash, ret, null)
				{

					@Override
					public void resultAvailable(IFinger result)
					{
						ret.setResult(result);
					}
				});
			}
		});
		return ret;
	}
	
	protected IFuture<IFinger> findPredecessor(IID id)
	{
		Future<IFinger> ret = new Future<IFinger>();
//		log("findPredecessor for: " + id);
		IFinger nDash = finger.getSelf();
		
		IRingNode nDashRing = getRingService(nDash).get(REMOTE_TIMEOUT);
		
		IFinger firstSuc = nDashRing.getSuccessor().get();
		
//		System.out.println("Got successor: " + firstSuc);
		
		// TODO: NPE here:
		while (!id.isInInterval(nDash.getNodeId(), nDashRing.getSuccessor().get(REMOTE_TIMEOUT).getNodeId(), false, true)) {
			nDash = nDashRing.getClosestPrecedingFinger(id).get(REMOTE_TIMEOUT);
			if (nDash == null) {
				nDash = finger.getSelf();
				break;
			}
			nDashRing = getRingService(nDash).get(REMOTE_TIMEOUT);
		}
		
//		while (!id.isInInterval(nDash.getNodeID(), ID.get(nDash.getSuccessor().get(REMOTE_TIMEOUT)), false, true)) {
//			nDash = nDash.getClosestPrecedingFinger(id).get(REMOTE_TIMEOUT);
//			if (nDash == null) {
//				nDash = finger.getSelf();
//				break;
//			}
//		}
		ret.setResult(nDash);
		
		return ret;
	}
	
	@Override
	public IFuture<IFinger> getSuccessor()
	{
		return new Future<IFinger>(finger.getSuccessor());
	}
	
	public IFuture<IFinger> getPredecessor()
	{
		return new Future<IFinger>(finger.getPredecessor());
	}

	public IFuture<Void> setPredecessor(IFinger predecessor)
	{
		this.finger.setPredecessor(predecessor);
		setState(State.JOINED);
		return Future.DONE;
	}

	@Override
	public IFuture<IFinger> getClosestPrecedingFinger(IID id)
	{
//		log("getClosestPrecedingFinger for " + id);
		return finger.getClosestPrecedingFinger(id);
	}
	
	
	@Override
	public IFuture<IComponentIdentifier> getCID()
	{
		return new Future<IComponentIdentifier>(this.agent.getComponentIdentifier());
	}

	public IFuture<Boolean> join(final IRingNode nDashRing) {
//		if (nDash != null) {
//			log("joining " + nDash);
//			finger.init(nDash);
//			finger.updateOthers();
//			// TODO move keys in (predecessor,n] from successor
//		} else {
//			log("joining (no peers)");
//			finger.setPredecessor(this);
//		}
		final Future<Boolean> future = new Future<Boolean>();
		finger.setPredecessor(null);
		if(nDashRing != null)
		{
//			getRingService(nDash).addResultListener(new MyResultListener<IRingNode>(nDash, future, Boolean.FALSE)
//			{
//				@Override
//				public void resultAvailable(IRingNode nDashRing)
//				{
					nDashRing.findSuccessor(myId).addResultListener(new MyResultListener<IFinger>(new Finger(nDashRing, null), future, Boolean.FALSE)
					{

						@Override
						public void resultAvailable(IFinger suc)
						{
							finger.setSuccessor(suc);
							log("Join complete with successor: " + suc.getNodeId());
							setState(State.JOINED);
							future.setResult(true);
						}
					});
//				}
//
//			});
		}
		else
		{
			log("Join complete without successor.");
			future.setResult(false);
		}
		return future;
	}
	

	private void setState(State state)
	{
		if (state == State.JOINED) {
			agent.getExternalAccess().scheduleStep(fixStep, FIX_DELAY);
			agent.getExternalAccess().scheduleStep(stabilizeStep, STABILIZE_DELAY);
		} else {
			
		}
		this.state = state;
	}

	@Override
	/**
	 * Notifies this node about a possible new predecessor.
	 */
	public IFuture<Void> notify(IFinger nDash)
	{
		Future<Void> future = new Future<Void>();
		IFinger pre = getPredecessor().get();
		if (pre == null || nDash.getNodeId().isInInterval(pre.getNodeId(), myId)) {
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
	public IFuture<Void> stabilize() {
		final Future<Void> ret = new Future<Void>();
		final IFinger successor = finger.getSuccessor();
//		ServiceCall.getOrCreateNextInvocation()
		if (successor instanceof IService) {
			Boolean valid = ((IService) successor).isValid().get();
			if (!valid) {
				System.out.println("not valid: " + successor.getNodeId());
			}
		}
		getRingService(successor).addResultListener(new MyResultListener<IRingNode>(successor, ret)
		{

			@Override
			public void resultAvailable(final IRingNode successorRing)
			{
				if (successorRing != null) {
					IFuture<IFinger> predecessor = successorRing.getPredecessor();
					predecessor.addResultListener(new MyResultListener<IFinger>(successor, ret)
					{
						@Override
						public void resultAvailable(IFinger x)
						{
							if(x != null && x.getNodeId().isInInterval(myId, successor.getNodeId()))
							// TODO: problem when successor is null
							{
								finger.setSuccessor(x);
							}
							IFuture<Void> notify = successorRing.notify(finger.getSelf());
							notify.addResultListener(new MyResultListener<Void>(successor, ret)
							{

								@Override
								public void resultAvailable(Void result)
								{
									ret.setResult(result);
								}
							});
						}
					});
				} else {
					ret.setResult(null);
				}
			}
		});
		return ret;
	}
	
	
	
	@Override
	public void disableSchedules()
	{
		stabilizeStep = new IComponentStep<Void>()
		{

			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		fixStep = new IComponentStep<Void>()
		{

			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	public IFuture<Void> fixFingers() {
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
//		logger.log(Level.INFO, myId + ": " + message);
		System.out.println(myId + ": " + message);
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

		private IFinger	rn;
		@SuppressWarnings("rawtypes")
		private Future	retFuture;
		private Object retValue = null;
		
		private Exception ex = new DebugException();

		public MyResultListener(IFinger rn, Future<Void> ret)
		{
			this.rn = rn;
			this.retFuture = ret;
		}
		
		public MyResultListener(IFinger rn, Future<?> ret, Object value)
		{
			this.rn = rn;
			this.retFuture = ret;
			this.retValue = value;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void exceptionOccurred(Exception exception)
		{
			log("exception in Ringnode rpc on node: " + rn.getNodeId());
			ex.printStackTrace();
//			exception.printStackTrace();
			
			finger.setInvalid(rn);
//			System.out.println("new fingertable");
//			System.out.println(finger);
			IFinger successor = finger.getSuccessor();
			if (successor.getSid() == null || successor.getSid().equals(finger.getSelf().getSid())) {
				// no successor :(
				if (finger.getPredecessor() == null || finger.getPredecessor().getSid() == null) {
					// and no predecessor, so no connection at all.
					log("No predecessor.. :(");
					setState(State.UNJOINED);
				} else {
					// will clear up during stabilize
//					stabilize().get();
//					findSuccessor(myId).addResultListener(new DefaultResultListener<IFinger>()
//					{
//
//						@Override
//						public void resultAvailable(IFinger result)
//						{
//							System.out.println("Got new successor: " + result);
//							finger.setSuccessor(result);
//						}
//					});
				}
				if (retFuture != null) {
					retFuture.setResult(retValue);
				}
			}
			
			
			
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
	
	protected IFuture<IRingNode> getRingService(IFinger finger) {
		final Future<IRingNode> ret = new Future<IRingNode>();
		IComponentIdentifier providerId = finger.getSid().getProviderId();
		
		IFuture<IRingNode> searchService = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IRingNode.class, providerId);
		searchService.addResultListener(new MyResultListener<IRingNode>(finger, ret, null)
		{

			@Override
			public void resultAvailable(IRingNode result)
			{
				ret.setResult(result);
			}
		});
		return ret;
	}
	
//	protected IFuture<IRingNode> getRingService(final IServiceIdentifier sid) {
//		final Future<IRingNode> ret = new Future<IRingNode>();
//		IComponentIdentifier providerId = sid.getProviderId();
//		
//		IFuture<IRingNode> searchService = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IRingNode.class, providerId);
		
//		IFuture<Object> getService = SServiceProvider.getService(agent, sid);
//		searchService.addResultListener(new DefaultResultListener<IRingNode>()
//		{
//
//			@Override
//			public void resultAvailable(IRingNode result)
//			{
//				ret.setResult((IRingNode)result);
//			}
//			
//			@Override
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("could not find service: " + sid + ", i am: " + agent);				
//				exception.printStackTrace();
//			}
//		});
		
//		return ret;
//	}

	protected Fingertable getFingerTable()
	{
		return finger;
	}

	@Override
	public IFuture<String> getFingerTableString()
	{
		return new Future<String>(finger.toString());
	}
	
	
}
