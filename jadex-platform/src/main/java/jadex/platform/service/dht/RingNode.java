package jadex.platform.service.dht;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
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
import jadex.commons.future.CounterResultListener;
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

	private static final long	REMOTE_TIMEOUT	= 10000;
	
	protected static final long	RETRY_SEARCH_DELAY	= 5000;
	
	protected static final long	RETRY_OTHER_DELAY	= 5000;
	
//	private static final long	REMOTE_LONG_TIMEOUT	= REMOTE_TIMEOUT * 2;

	private State state = State.UNJOINED;
	
	enum State {
		JOINED, UNJOINED
	}

	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	private IID	myId;
	
	private Fingertable fingertable;
	
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
		
		agent.getExternalAccess().scheduleStep(searchStep).addResultListener(new DefaultResultListener<Void>()
		{

			@Override
			public void resultAvailable(Void result)
			{
			}
		});
	}
	
	IComponentStep<Void> searchStep = new IComponentStep<Void>()
	{
		@Override
		public IFuture<Void> execute(IInternalAccess ia)
		{
			if (state == State.JOINED) {
				return Future.DONE;
			}
			
			log("Searching for other nodes, because state != JOINED");
			IRequiredServicesFeature componentFeature = agent.getComponentFeature(IRequiredServicesFeature.class);
			ITerminableIntermediateFuture<Object> requiredServices = componentFeature.getRequiredServices("ringnodes");
			
			requiredServices.addResultListener(new IntermediateDefaultResultListener<Object>()
			{
				Boolean join = false;
				
				@Override
				public void intermediateResultAvailable(Object result)
				{
					System.out.println("Found service " + result);
					IRingNode other = (IRingNode)result;
					if (!join && state != State.JOINED) {
						if (!other.getId().get().equals(myId)) {
							join = true;
							join(other);
						}
					}
				}
				
				@Override
				public void finished()
				{
					// schedule again in case we didn't join successfully.
					agent.getExternalAccess().scheduleStep(searchStep, FIX_DELAY);
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

	void init(IID id)
	{
		this.myId = id;
		IRingNode me = agent.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(IRingNode.class);
		
		fingertable = new Fingertable(((IService) me).getServiceIdentifier(), myId, this);
	}
	
	@Override
	public IFuture<IID> getId()
	{
		return new Future<IID>(myId);
	}
	
	class FindSuccessorStep implements IComponentStep<IFinger> {

		private IID	id;

		public FindSuccessorStep(IID id)
		{
			this.id = id;
		}
		
		@Override
		public IFuture<IFinger> execute(IInternalAccess ia)
		{
			return findSuccessor(id);
		}
	}
	
	public IFuture<IFinger> findSuccessor(final IID id) {
		final Future<IFinger> ret = new Future<IFinger>();
//		log("findSuccessor for: " + id);		
		final IFinger nDash = findPredecessor(id).get();
//		IRingNode suc = nDash.findSuccessor(id).get();
		getRingService(nDash).addResultListener(new InvalidateFingerAndTryAgainListener<IRingNode, IFinger>(nDash, new FindSuccessorStep(id), ret, "findSuccessor")
		{
			@Override
			public void resultAvailable(IRingNode result)
			{
//				log("Got ring node");
				result.getSuccessor().addResultListener(new InvalidateFingerAndTryAgainListener<IFinger, IFinger>(nDash, new FindSuccessorStep(id), ret, "findSuccessor")
				{
					@Override
					public void resultAvailable(IFinger result)
					{
//						log("Got finger");
						ret.setResult(result);
					}
				});
			}
		});
		return ret;
	}
	
	protected IFuture<IFinger> findPredecessor(final IID id)
	{
//		Future<IFinger> ret = new Future<IFinger>();
//		log("findPredecessor for: " + id);
		final IFinger beginDash = fingertable.getSelf();
		final IRingNode beginDashRing = this;
		
		// TODO: NPE here:
		// TODO: fail-over here:
		
		IComponentStep<IFinger> step = new IComponentStep<IFinger>()
		{

			IComponentStep<IFinger> thisStep = this;
			IFinger nDash = beginDash;
			IRingNode nDashRing = beginDashRing;
			private boolean	wasNull;
			
			public IFuture<IFinger> execute(IInternalAccess ia)
			{
				final Future<IFinger> ret = new Future<IFinger>();
				nDashRing.getSuccessor().addResultListener(new DefaultResultListener<IFinger>()
				{
					
					public void resultAvailable(IFinger successor)
					{
						IID sucId = successor.getNodeId();
						if (id.isInInterval(nDash.getNodeId(), sucId, false, true)) {
							nDashRing.getClosestPrecedingFinger(id).addResultListener(new DefaultResultListener<IFinger>()
							{
								
								public void resultAvailable(IFinger result)
								{
									nDash = result;
									if (nDash == null) {
										nDash = fingertable.getSelf();
										wasNull = true;
										ret.setResult(nDash);
									} else {
										getRingService(nDash).addResultListener(new DefaultResultListener<IRingNode>() {
											
											public void resultAvailable(IRingNode result)
											{
												nDashRing = result;
												agent.getExternalAccess().scheduleStep(thisStep).addResultListener(new DelegationResultListener<IFinger>(ret));
											}
											
											@Override
											public void exceptionOccurred(Exception exception)
											{
												super.exceptionOccurred(exception);
												log("findPredecessor: could not get RingService from " + nDash.getNodeId());
												ret.setResult(fingertable.getSelf());
											}
										});
									}
								}
								
								@Override
								public void exceptionOccurred(Exception exception)
								{
									super.exceptionOccurred(exception);
									log("findPredecessor: could not get ClosestPrecedingFinger from " + nDash.getNodeId());
									ret.setResult(fingertable.getSelf());
								}
								
							});
						} else {
							ret.setResult(nDash);
						}
					}

					@Override
					public void exceptionOccurred(Exception exception)
					{
						super.exceptionOccurred(exception);
						log("findPredecessor: could not get successor of " + nDash.getNodeId());
						ret.setResult(fingertable.getSelf());
					}
					
					
				});
				
				
				return ret;
			}
		};
		
		return agent.getExternalAccess().scheduleStep(step);
		
//		while (!id.isInInterval(nDash.getNodeId(), nDashRing.getSuccessor().get(REMOTE_TIMEOUT).getNodeId(), false, true)) {
//			nDash = nDashRing.getClosestPrecedingFinger(id).get(REMOTE_TIMEOUT);
//			if (nDash == null) {
//				nDash = fingertable.getSelf();
//				break;
//			}
//			nDashRing = getRingService(nDash).get(REMOTE_TIMEOUT);
//		}
		
//		ret.setResult(nDash);
//		
//		return ret;
	}
	
	@Override
	public IFuture<IFinger> getSuccessor()
	{
		return new Future<IFinger>(fingertable.getSuccessor());
	}
	
	public IFuture<IFinger> getPredecessor()
	{
		return new Future<IFinger>(fingertable.getPredecessor());
	}

	public IFuture<Void> setPredecessor(IFinger predecessor)
	{
		this.fingertable.setPredecessor(predecessor);
		setState(State.JOINED);
		return Future.DONE;
	}

	@Override
	public IFuture<IFinger> getClosestPrecedingFinger(IID id)
	{
//		log("getClosestPrecedingFinger for " + id);
		return fingertable.getClosestPrecedingFinger(id);
	}
	
	
	@Override
	public IFuture<IComponentIdentifier> getCID()
	{
		return new Future<IComponentIdentifier>(this.agent.getComponentIdentifier());
	}

	class JoinStep implements IComponentStep<Boolean> {

		private IRingNode	nDashRing;

		public JoinStep(IRingNode nDashRing)
		{
			this.nDashRing = nDashRing;
		}
		
		@Override
		public IFuture<Boolean> execute(IInternalAccess ia)
		{
			return join(nDashRing);
		}
	}
	
	public IFuture<Boolean> join(final IRingNode nDashRing) {
		log("joining " + nDashRing);
		final Future<Boolean> future = new Future<Boolean>();
		fingertable.setPredecessor(null);
		if(nDashRing != null)
		{
			nDashRing.findSuccessor(myId).addResultListener(new InvalidateFingerAndTryAgainListener<IFinger, Boolean>(new Finger(nDashRing, null), new JoinStep(nDashRing), future, "join")
			{

				@Override
				public void resultAvailable(IFinger suc)
				{
					fingertable.setSuccessor(suc);
					log("Join complete with successor: " + suc.getNodeId());
					setState(State.JOINED);
					future.setResult(true);
				}
			});
		}
		else
		{
			log("Join complete without successor.");
			future.setResult(false);
			setState(State.UNJOINED);
		}
		return future;
	}
	

	private void setState(State state)
	{
		if (state == State.JOINED) {
			agent.getExternalAccess().scheduleStep(fixStep, FIX_DELAY);
			agent.getExternalAccess().scheduleStep(stabilizeStep, STABILIZE_DELAY);
		} else {
			// reschedule search
			log("State set to unjoined, initiating search");
			agent.getExternalAccess().scheduleStep(searchStep, RETRY_SEARCH_DELAY);
		}
		this.state = state;
	}
	
	// TODO: periodic predecessor/finger availability check!!

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
		
		final CounterResultListener<Void> counter = new CounterResultListener<Void>(2, new DelegationResultListener<Void>(ret));
		
		final IFinger successor = fingertable.getSuccessor();
		
		log("Stabilizing (suc: " 
			+ (fingertable.getSuccessor() != null ? fingertable.getSuccessor().getNodeId(): "null") + ", pre: "
			+ (fingertable.getPredecessor() != null ? fingertable.getPredecessor().getNodeId(): "null") + ")");
		getRingService(successor).addResultListener(new InvalidateFingerAndTryAgainListener<IRingNode, Void>(successor, stabilizeDelayedStep, ret, "stabilize")
		{

			@Override
			public void resultAvailable(final IRingNode successorRing)
			{
				log("Got ring service " + successorRing);
				if (successorRing != null) {
					IFuture<IFinger> predecessor = successorRing.getPredecessor();
					predecessor.addResultListener(new InvalidateFingerAndTryAgainListener<IFinger, Void>(successor, stabilizeDelayedStep, ret, "stabilize")
					{
						@Override
						public void resultAvailable(final IFinger x)
						{
							log("Got predecessor finger " + x);
							if(x != null && x.getNodeId().isInInterval(myId, successor.getNodeId()))
							{
								// TODO: when my own successor is bad and i get a new one, the new one has the BAD node as predecessor.
								// i will get this here again -> boom :(
								getRingService(x).addResultListener(new DefaultResultListener<IRingNode>()
								{
									@Override
									public void resultAvailable(IRingNode result)
									{
										fingertable.setSuccessor(x);
										counter.resultAvailable(null);
										log("Got service for finger " + x + " , counter: " + counter.getCnt());
									}
									
									public void exceptionOccurred(Exception exception) {
										exception.printStackTrace();
										counter.resultAvailable(null);
									};
								});
							} else {
								counter.resultAvailable(null);
								log("finger didnt fit: " + x + " , counter: " + counter.getCnt());
							}
							
//							log("notifying");
							IFuture<Void> notify = successorRing.notify(fingertable.getSelf());
							notify.addResultListener(new InvalidateFingerAndTryAgainListener<Void, Void>(successor, stabilizeDelayedStep, ret, "stabilize")
							{

								@Override
								public void resultAvailable(Void result)
								{
//									ret.setResult(result);
									counter.resultAvailable(null);
									log("notify successful, counter: " + counter.getCnt());
								}
							});
						}
					});
				} else {
					ret.setResult(null);
				}
			}

		});
		
		ret.addResultListener(new DefaultResultListener<Void>() {

			@Override
			public void resultAvailable(Void result)
			{
				log("stabilize ret has result");
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
		
		searchStep = new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				return null;
			}
		};
	}

	public IFuture<Void> fixFingers() {
		return fingertable.fixFingers();
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
		ArrayList<IFinger> arrayList = new ArrayList<IFinger>(fingertable.fingers.length);
		for(int i = 0; i < fingertable.fingers.length; i++)
		{
			arrayList.add(fingertable.fingers[i]);
		}
		return new Future<List<IFinger>>(arrayList);
	}

	public void setFinger(Fingertable finger)
	{
		this.fingertable = finger;
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
			IFuture<Void> stabilize = stabilize();
			ia.getExternalAccess().scheduleStep(stabilizeStep, STABILIZE_DELAY);
			return stabilize;
		}
	};
	
	IComponentStep<Void> stabilizeDelayedStep = new IComponentStep<Void>()
	{

		@Override
		public IFuture<Void> execute(IInternalAccess ia)
		{
			return ia.getExternalAccess().scheduleStep(new IComponentStep<Void>()
			{

				@Override
				public IFuture<Void> execute(IInternalAccess ia)
				{
					return stabilize();
				}
			}, STABILIZE_DELAY);
		}
	};
	
	IComponentStep<Void> fixStep = new IComponentStep<Void>()
	{

		@Override
		public IFuture<Void> execute(IInternalAccess ia)
		{
			log("fixfingers");
			fixFingers();
			ia.getExternalAccess().scheduleStep(fixStep, FIX_DELAY);
			return Future.DONE;
		}
	};
	
	abstract class InvalidateFingerAndTryAgainListener<T, E> implements IResultListener<T> {

		private IComponentStep< E >	tryAgainStep;
		private String	name;
		private Future<E> exceptionRet;
		
		private IFinger	rn;
		private Exception ex = new DebugException();

		public InvalidateFingerAndTryAgainListener(IFinger rn, IComponentStep<E> tryAgainStep, Future<E> exceptionRet, String name)
		{
			this.rn = rn;
			this.tryAgainStep = tryAgainStep;
			this.name = name;
			this.exceptionRet = exceptionRet;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void exceptionOccurred(final Exception exception)
		{
			log("exception in Ringnode rpc on node: " + rn.getNodeId());
			// invalidate node and maybe check for new successor
			revalidate(rn).addResultListener(new DefaultResultListener<Void>()
			{

				@Override
				public void resultAvailable(Void result)
				{
					if (state == State.JOINED) {
						log("Retrying: " + name +".");
						agent.getComponentFeature(IExecutionFeature.class).waitForDelay(RETRY_OTHER_DELAY, tryAgainStep).addResultListener(new DefaultResultListener<E>() {

							@Override
							public void resultAvailable(E result)
							{
								log("Retry result available, passing through...");
								exceptionRet.setResult(result);							
							}
							
						});
					} else {
						log("Not trying: " + name +" again, state is unjoined.");
						agent.getComponentFeature(IExecutionFeature.class).waitForDelay(RETRY_SEARCH_DELAY, searchStep);
						exceptionRet.setException(exception);
					}
				}
				
				@Override
				public void exceptionOccurred(Exception exception)
				{
				}
			});
		}

		@Override
		public abstract void resultAvailable(T result);
	}
	
	private IFuture<Void> revalidate(IFinger lostFinger)
	{
		final Future<Void> ret = new Future<Void>();
		fingertable.setInvalid(lostFinger);
//		System.out.println("new fingertable");
//		System.out.println(finger);
		IFinger successor = fingertable.getSuccessor();
		if (successor.getSid() == null || successor.getSid().equals(fingertable.getSelf().getSid())) {
			// no successor :(
			if (fingertable.getPredecessor() == null || fingertable.getPredecessor().getSid() == null) {
				// and no predecessor, so no connection at all.
				log("No predecessor.. :(");
				setState(State.UNJOINED);
				ret.setResult(null);
//				ret.setException(new UnjoinedException());
			} else {
				findSuccessor(myId).addResultListener(new DefaultResultListener<IFinger>()
				{

					@Override
					public void resultAvailable(IFinger result)
					{
						log("Got new successor: " + result);
						fingertable.setSuccessor(result);
						ret.setResult(null);
					}

					@Override
					public void exceptionOccurred(Exception exception)
					{
						log("couldnt find new successor :(");
						setState(State.UNJOINED);
						ret.setResult(null);
//						ret.setException(new UnjoinedException());
					}
				});
			}
		} else {
			ret.setResult(null);
		}
		return ret;
	}
	
	protected IFuture<IRingNode> getRingService(final IFinger finger) {
//		if (finger.getNodeId().equals(Finger.killedId)) {
//			throw new RuntimeException("Trying to contact dead finger");
//		}
		final Future<IRingNode> ret = new Future<IRingNode>();
		IComponentIdentifier providerId = finger.getSid().getProviderId();
		
		IFuture<IRingNode> searchService = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IRingNode.class, providerId);
		searchService.addResultListener(new DefaultResultListener<IRingNode>()
		{

			@Override
			public void resultAvailable(IRingNode result)
			{
				ret.setResult(result);
			}

			@Override
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		return ret;
	}
	
	protected Fingertable getFingerTable()
	{
		return fingertable;
	}

	@Override
	public IFuture<String> getFingerTableString()
	{
		return new Future<String>(fingertable.toString());
	}
	
	
}
