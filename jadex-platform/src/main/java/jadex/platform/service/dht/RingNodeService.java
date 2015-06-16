package jadex.platform.service.dht;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNodeDebugService;
import jadex.bridge.service.types.dht.IRingNodeService;
import jadex.bridge.service.types.dht.RingNodeEvent;
import jadex.commons.DebugException;
import jadex.commons.concurrent.ThreadPool;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.platform.service.dht.Fingertable.FingerTableListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This service provides a ring node, which connects to other ring nodes and
 * forms a circular hierarchy using the Chord DHT protocol.
 */
@Service
public class RingNodeService implements IRingNodeService, IRingNodeDebugService
{
	/** Delay in ms between two fixfinger runs **/
	protected static final long	FIX_DELAY			= 90 * 1000;

	/** Delay in ms between two stabilize runs **/
	protected static final long	STABILIZE_DELAY		= 60 * 1000;

	/** Delay in ms to wait before restarting the search for other ring nodes **/
	protected static final long	RETRY_SEARCH_DELAY	= 30 * 1000;

	/** Delay in ms to wait before retrying any remote calls **/
	protected static final long	RETRY_OTHER_DELAY	= 5 * 1000;


	/** State of this ring node. **/
	private State				state				= State.UNJOINED;

	/** The agent. */
	@ServiceComponent
	protected IInternalAccess	agent;

	/** ID of this ring node **/
	protected IID				myId;

	/** The local fingertable **/
	protected Fingertable		fingertable;

	/** The logger. **/
	protected Logger			logger;

	/** Event subscriptions. **/
	protected List<SubscriptionIntermediateFuture<RingNodeEvent>> subscriptions;

	/** Ring overlay identifier. **/
	protected String	overlayId;

	/** Flag that indicates whether this Service is already usable. */
	protected boolean	initialized;
	
	private ThreadPool pool = new ThreadPool();
	
	/**
	 * Constructor.
	 */
	public RingNodeService(String overlayId)
	{
		this.overlayId = overlayId;
		logger = Logger.getLogger(this.getClass().getName());
		subscriptions = new ArrayList<SubscriptionIntermediateFuture<RingNodeEvent>>();
	}
	
	/**
	 * Sets the initialized flag.
	 */
	public void setInitialized(boolean value)
	{
		this.initialized = value;
	}
	
	/**
	 * Gets the initialized flag.
	 */
	public boolean isInitialized()
	{
		return initialized;
	}
	
	@ServiceStart
	public void onStart()
	{
		if(this.myId != null)
		{
			return;
		}
//		System.out.println("Ringservice started");
		init(ID.get(agent.getComponentIdentifier()));

		agent.getExternalAccess().scheduleStep(searchStep);
	}
	
	/** Component step to search for other ringnodes. **/
	IComponentStep<Void> searchStep = new IComponentStep<Void>()
	{
		@Override
		public IFuture<Void> execute(IInternalAccess ia)
		{
			if (state == State.JOINED) {
				return Future.DONE;
			}
			
//			log("Searching for other nodes, because state != JOINED");
			
			ITerminableIntermediateFuture<IRingNodeService> requiredServices = SServiceProvider.getServices(agent, IRingNodeService.class, RequiredServiceInfo.SCOPE_GLOBAL, new OverlayIdFilter(overlayId));
			
			requiredServices.addResultListener(new IntermediateDefaultResultListener<IRingNodeService>()
			{
				Boolean found = false;
				
				@Override
				public void intermediateResultAvailable(IRingNodeService result)
				{
//					System.out.println("Found ringnode to join: " + result);
					IRingNodeService other = (IRingNodeService)result;
					if (!found && state != State.JOINED) {
						if (!other.getId().get().equals(myId)) {
							found = true;
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

	/**
	 * Initialize the node with its own id.
	 * 
	 * @param id
	 */
	public void init(IID id)
	{
		this.myId = id;
		IRingNodeService me = agent.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(IRingNodeService.class);
		fingertable = new Fingertable(((IService)me).getServiceIdentifier(), myId, new FingerTableListener()
		{
			
			@Override
			public void successorChanged(IFinger oldFinger, IFinger newFinger)
			{
				notifySubscribers(RingNodeEvent.successorChange(myId, oldFinger, newFinger));
			}
			
			@Override
			public void predecessorChanged(IFinger oldFinger, IFinger newFinger)
			{
				notifySubscribers(RingNodeEvent.predecessorChange(myId, oldFinger, newFinger));
			}
			
			@Override
			public void fingerChanged(int index, IFinger oldFinger, IFinger newFinger)
			{
				notifySubscribers(RingNodeEvent.fingerChange(myId, index, oldFinger, newFinger));
			}
		});
	}
	
	public String getOverlayId() {
		return overlayId;
	}

	/**
	 * Return own ID.
	 * 
	 * @return own ID.
	 */
	public IFuture<IID> getId()
	{
		return new Future<IID>(myId);
	}
	
	/**
	 * ComponentStep to find the successor of a given ID.
	 */
	class FindSuccessorStep implements IComponentStep<IFinger> {

		private IID	id;

		public FindSuccessorStep(IID id)
		{
			this.id = id;
		}
		
		public IFuture<IFinger> execute(IInternalAccess ia)
		{
			return findSuccessor(id);
		}
	}

	/**
	 * Find the successor of a given ID in the ring.
	 * 
	 * @param id ID to find the successor of.
	 * @return The finger entry of the best closest successor.
	 */
	public IFuture<IFinger> findSuccessor(final IID id) {
		final Future<IFinger> ret = new Future<IFinger>();
		if (!initialized) {
			ret.setException(new IllegalStateException("RingNode not yet initialized!"));
			return ret;
		}
		
		pool.execute(new Runnable()
		{

			@Override
			public void run()
			{
				final IFinger nDash = findPredecessor(id).get();
				getRingService(nDash).addResultListener(new InvalidateFingerAndTryAgainListener<IRingNodeService, IFinger>(nDash, new FindSuccessorStep(id), ret, "findSuccessor")
				{
					@Override
					public void resultAvailable(IRingNodeService result)
					{
						result.getSuccessor().addResultListener(new InvalidateFingerAndTryAgainListener<IFinger, IFinger>(nDash, new FindSuccessorStep(id), ret, "findSuccessor")
						{
							@Override
							public void resultAvailable(IFinger result)
							{
								ret.setResult(result);
							}
						});
					}
				});

			}
		});
		
		return ret;
	}

	/**
	 * Find the predecessor of a given ID in the ring.
	 * 
	 * @param id the ID.
	 * @return The closest predecessor of the given ID in the ring.
	 */
	protected IFuture<IFinger> findPredecessor(final IID id)
	{
//		log("findPredecessor for: " + id);
		if (!initialized) {
			return new Future<IFinger>(new IllegalStateException("RingNode not yet initialized!"));
		}
		final IFinger beginDash = fingertable.getSelf();
		final IRingNodeService beginDashRing = this;
		
		// TODO: fail-over here:
		
		IComponentStep<IFinger> step = new IComponentStep<IFinger>()
		{

			IComponentStep<IFinger> thisStep = this;
			IFinger nDash = beginDash;
			IRingNodeService nDashRing = beginDashRing;
			
			public IFuture<IFinger> execute(IInternalAccess ia)
			{
				final Future<IFinger> ret = new Future<IFinger>();
				nDashRing.getSuccessor().addResultListener(new DefaultResultListener<IFinger>()
				{
					
					public void resultAvailable(IFinger successor)
					{
						IID sucId = successor.getNodeId();
						if (!id.isInInterval(nDash.getNodeId(), sucId, false, true)) {
							nDashRing.getClosestPrecedingFinger(id).addResultListener(new DefaultResultListener<IFinger>()
							{
								
								public void resultAvailable(IFinger result)
								{
									nDash = result;
									if (nDash == null) {
										nDash = fingertable.getSelf();
										ret.setResult(nDash);
									} else {
										getRingService(nDash).addResultListener(new DefaultResultListener<IRingNodeService>() {
											
											public void resultAvailable(IRingNodeService result)
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
	
	/**
	 * Return the successor of this node.
	 * 
	 * @return finger entry of the successor.
	 */
	public IFuture<IFinger> getSuccessor()
	{
		return new Future<IFinger>(fingertable.getSuccessor());
	}

	/**
	 * Return the predecessor of this node.
	 * 
	 * @return finger entry of the predecessor.
	 */
	public IFuture<IFinger> getPredecessor()
	{
		return new Future<IFinger>(fingertable.getPredecessor());
	}

	/**
	 * Set the predecessor of this node.
	 * 
	 * @param predecessor Finger entry of the new predecessor.
	 */
	public IFuture<Void> setPredecessor(IFinger predecessor)
	{
		this.fingertable.setPredecessor(predecessor);
		setState(State.JOINED);
		return Future.DONE;
	}
	
	/**
	 * Returns the current state of this ring node.
	 * @return State
	 */
	public State getState()
	{
		return state;
	}

	/**
	 * Return the finger that preceeds the given ID and is closest to it in the
	 * local finger table.
	 * 
	 * @param key the ID
	 * @return {@link IFinger} The finger that is closest preceeding the given
	 *         key.
	 */
	public IFuture<IFinger> getClosestPrecedingFinger(IID id)
	{
		// log("getClosestPrecedingFinger for " + id);
		return fingertable.getClosestPrecedingFinger(id);
	}

	/**
	 * Return the CID of the provider of this Service.
	 * 
	 * @return CID.
	 */
//	public IFuture<IComponentIdentifier> getCID()
//	{
//		return new Future<IComponentIdentifier>(this.agent.getComponentIdentifier());
//	}

	/**
	 * Component Step to join the ring with a known other ring node.
	 */
	class JoinStep implements IComponentStep<Boolean>
	{

		private IRingNodeService	nDashRing;

		public JoinStep(IRingNodeService nDashRing)
		{
			this.nDashRing = nDashRing;
		}

		@Override
		public IFuture<Boolean> execute(IInternalAccess ia)
		{
			return join(nDashRing);
		}
	}
	
	/**
	 * Join the ring.
	 * @param nDashRing Another known ringnode
	 * 
	 * @return true, if the join was successful, else false.
	 */
	public IFuture<Boolean> join(final IRingNodeService nDashRing) {
//		log("joining " + nDashRing);
		final Future<Boolean> future = new Future<Boolean>();
		fingertable.setPredecessor(null);
		if(nDashRing != null)
		{
			nDashRing.findSuccessor(myId).addResultListener(new IResultListener<IFinger>()
			{
				
				public void resultAvailable(IFinger suc)
				{
					fingertable.setSuccessor(suc);
					log("Join complete with successor: " + suc.getNodeId());
					setState(State.JOINED);
					getRingService(suc).addResultListener(new IResultListener<IRingNodeService>()
					{

						@Override
						public void resultAvailable(IRingNodeService result)
						{
//							future.setResult(true);
							// for faster propagation, notify the node about me
							result.notify(fingertable.getSelf()).addResultListener(new IResultListener<Void>()
							{

								@Override
								public void resultAvailable(Void result)
								{
									future.setResult(true);
								}

								@Override
								public void exceptionOccurred(Exception exception)
								{
									exception.printStackTrace();
									future.setResult(false);
								}
							});
						}

						@Override
						public void exceptionOccurred(Exception exception)
						{
							exception.printStackTrace();
							future.setResult(false);	
						}
					});
					
				}

				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
					future.setResult(false);
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
	
	/**
	 * Set a new state and handle the transition.
	 * @param state
	 */
	private void setState(State state)
	{
		if (state == State.JOINED) {
			notifySubscribers(RingNodeEvent.join(myId, fingertable.getSuccessor()));
			agent.getExternalAccess().scheduleStep(fixStep, FIX_DELAY);
			agent.getExternalAccess().scheduleStep(stabilizeStep, STABILIZE_DELAY);
		} else {
			notifySubscribers(RingNodeEvent.part(myId, fingertable.getSuccessor()));
			// reschedule search
			log("State set to unjoined, initiating search");
			agent.getExternalAccess().scheduleStep(searchStep, RETRY_SEARCH_DELAY);
		}
		this.state = state;
	}
	
	/**
	 * Subscribes for RingNodeEvents.
	 * @return subscription
	 */
	public ISubscriptionIntermediateFuture<RingNodeEvent> subscribeForEvents() {
		final SubscriptionIntermediateFuture<RingNodeEvent> sub = new SubscriptionIntermediateFuture<RingNodeEvent>();
		TerminationCommand terminate = new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				reason.printStackTrace();
				subscriptions.remove(sub);
			}
		};
		sub.setTerminationCommand(terminate);
		this.subscriptions.add(sub);
		return sub;
	}
	
	/**
	 * Notify all subscribers about events.
	 * @param e
	 */
	protected void notifySubscribers(RingNodeEvent e) {
//		System.out.println(e);
		for(SubscriptionIntermediateFuture<RingNodeEvent> sub : subscriptions)
		{
			sub.addIntermediateResult(e);
		}
	}
	
	/**
	 * Notifies this node about a possible new predecessor.
	 * 
	 * @param nDash possible new predecessor
	 */
	public IFuture<Void> notify(IFinger nDash)
	{
		final Future<Void> future = new Future<Void>();
		IFinger pre = getPredecessor().get();
		if (pre == null || nDash.getNodeId().isInInterval(pre.getNodeId(), myId)) {
			setPredecessor(nDash).addResultListener(new ExceptionDelegationResultListener<Void, Void>(future) {
				@Override
				public void customResultAvailable(Void result)
				{
					// non-chord: my successor could be wrong, ask my predecessor about it:
					// this speeds up the stabilizing process
					stabilize().addResultListener(new DelegationResultListener<Void>(future));
				}
			});
		} else {
			future.setResult(null);
		}
		return future;
	}
	
	/**
	 * Notifies this node about another node that may be bad.
	 * 
	 * @param x possible bad node.
	 */
	@Override
	public IFuture<Void> notifyBad(final IFinger x) {
		final Future<Void> fut = new Future<Void>();
		getRingService(x).addResultListener(new IResultListener<IRingNodeService>() {

			@Override
			public void resultAvailable(IRingNodeService result) {
				fut.setResult(null);
			}

			@Override
			public void exceptionOccurred(Exception exception) {
				revalidate(x).addResultListener(new DelegationResultListener<Void>(fut));
			}
		});
		return fut;
	}

	/**
	 * Check if my successor is correct. If not, set a new one.
	 * 
	 * @return void
	 */
	public IFuture<Void> stabilize() {
		final Future<Void> ret = new Future<Void>();
		
		final CounterResultListener<Void> counter = new CounterResultListener<Void>(2, new DelegationResultListener<Void>(ret));
		
		final IFinger successor = fingertable.getSuccessor();
		log("Stabilizing");
//		log("Stabilizing (suc: " 
//			+ (fingertable.getSuccessor() != null ? fingertable.getSuccessor().getNodeId(): "null") + ", pre: "
//			+ (fingertable.getPredecessor() != null ? fingertable.getPredecessor().getNodeId(): "null") + ")");
		getRingService(successor).addResultListener(new InvalidateFingerAndTryAgainListener<IRingNodeService, Void>(successor, stabilizeRetryStep, ret, "stabilize")
		{

			@Override
			public void resultAvailable(final IRingNodeService successorRing)
			{
//				log("Got ring service " + successorRing);
				if (successorRing != null) {
					IFuture<IFinger> predecessor = successorRing.getPredecessor();
					predecessor.addResultListener(new InvalidateFingerAndTryAgainListener<IFinger, Void>(successor, stabilizeRetryStep, ret, "stabilize")
					{
						@Override
						public void resultAvailable(final IFinger x)
						{
//							log("Got predecessor finger " + x);
							if(x != null && x.getNodeId().isInInterval(myId, successor.getNodeId()))
							{
								getRingService(x).addResultListener(new DefaultResultListener<IRingNodeService>()
								{
									@Override
									public void resultAvailable(IRingNodeService result)
									{
										fingertable.setSuccessor(x);
										counter.resultAvailable(null);
//										log("Got service for finger " + x);
									}
									
									public void exceptionOccurred(Exception exception) {
										counter.resultAvailable(null);
										log("Could not get service for finger " + x + ", informing " + successor);
										// when my own successor is bad and i get a new one, the new one may have the BAD node as predecessor.
										// in this case, i inform my successor to avoid repeated errors.
										successorRing.notifyBad(x);
									};
								});
							} else {
								counter.resultAvailable(null);
//								log("finger didnt fit: " + x );
							}
							
//							log("notifying");
							IFuture<Void> notify = successorRing.notify(fingertable.getSelf());
							notify.addResultListener(new InvalidateFingerAndTryAgainListener<Void, Void>(successor, stabilizeRetryStep, ret, "stabilize")
							{

								@Override
								public void resultAvailable(Void result)
								{
//									ret.setResult(result);
									counter.resultAvailable(null);
//									log("notify successful");
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
				log("Stabilize done.");
			}
			
		});
		return ret;
	}
	
	/**
	 * Disable stabilize, fix and search for debug purposes.
	 */
	public void disableSchedules()
	{
		stabilizeStep = new IComponentStep<Void>()
		{

			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				return null;
			}
		};
		
		fixStep = new IComponentStep<Void>()
		{

			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
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

	/**
	 * Run the fixfingers algorithm. This implementation iterates over all
	 * fingers and checks if there is a better candidate.
	 * 
	 * @return void
	 */
	public IFuture<Void> fixFingers()
	{
		log("FixFingers");
		Future<Void> future = new Future<Void>();
		Finger[] fingers = fingertable.getFingers();
		final CounterResultListener<Void> counter = new CounterResultListener<Void>(fingers.length, new DelegationResultListener<Void>(future));
		for(int i = 0; i < fingers.length; i++)
		{
			final Finger finger = fingers[i];
			final int index = i;
			findSuccessor(finger.getStart()).addResultListener(new DefaultResultListener<IFinger>()
			{
				public void resultAvailable(IFinger result)
				{
					if (!result.getNodeId().equals(finger.getNodeId())) {
						Finger oldFinger = finger.clone();
						finger.set(result);
						if (index == 0) {
							notifySubscribers(RingNodeEvent.successorChange(myId, oldFinger, finger));
						} else {
							notifySubscribers(RingNodeEvent.fingerChange(myId, index, oldFinger, finger));
						}
						counter.resultAvailable(null);
					}
				}
			});
		}
		return future;

	}


	/** Helper methods **/
	
	private void log(String message) {
		logger.log(Level.INFO, myId + ": " + message);
	}

	/**
	 * Returns a List of all fingers.
	 */
	public IFuture<List<IFinger>> getFingers()
	{
		Finger[] fingers = fingertable.getFingers();
		ArrayList<IFinger> arrayList = new ArrayList<IFinger>(fingers.length);
		for(int i = 0; i < fingers.length; i++)
		{
			arrayList.add(fingers[i]);
		}
		return new Future<List<IFinger>>(arrayList);
	}

	/**
	 * Component step to execute a stabilize run.
	 */
	IComponentStep<Void> stabilizeRetryStep = new IComponentStep<Void>()
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
	
	/**
	 * Component step to execute a stabilize run.
	 */
	IComponentStep<Void> stabilizeStep = new IComponentStep<Void>()
	{

		@Override
		public IFuture<Void> execute(IInternalAccess ia)
		{
			IFuture<Void> stabilize = stabilize();
			ia.getExternalAccess().scheduleStep(stabilizeStep, STABILIZE_DELAY);
			return stabilize;
		}
	};
	
	/**
	 * Component step to execute a fixfingers run.
	 */
	IComponentStep<Void> fixStep = new IComponentStep<Void>()
	{

		@Override
		public IFuture<Void> execute(IInternalAccess ia)
		{
//			log("fixfingers");
			fixFingers();
			ia.getExternalAccess().scheduleStep(fixStep, FIX_DELAY);
			return Future.DONE;
		}
	};
	
	/**
	 * ResultListener that invalidates a finger when exceptions occur. 
	 *
	 * @param <T> Type of the normal result returned by the asynchronous call.
	 * @param <E> Return type of the asynchronous retry step.
	 */
	abstract class InvalidateFingerAndTryAgainListener<T, E> implements IResultListener<T> {

		private IComponentStep< E >	tryAgainStep;
		private String	name;
		private Future<E> delegationFuture;
		
		private IFinger	rn;
		private Exception ex = new DebugException();

		/**
		 * Constructor
		 * @param rn The finger to revalidate on exceptions.
		 * @param tryAgainStep The step to execute after revalidation of the failed finger.
		 * @param delegationRet The future to delegate the result of the tryAgainStep to.
		 * @param name Name of the executed method, for logging purposes.
		 */
		public InvalidateFingerAndTryAgainListener(IFinger rn, IComponentStep<E> tryAgainStep, Future<E> delegationRet, String name)
		{
			this.rn = rn;
			this.tryAgainStep = tryAgainStep;
			this.name = name;
			this.delegationFuture = delegationRet;
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
						// since we're still in the ring, retry with the given step.
//						log("Retrying: " + name +".");
						agent.getComponentFeature(IExecutionFeature.class).waitForDelay(RETRY_OTHER_DELAY, tryAgainStep).addResultListener(new DefaultResultListener<E>() {

							@Override
							public void resultAvailable(E result)
							{
//								log("Retry result available, passing through...");
								delegationFuture.setResult(result);							
							}
							
						});
					} else {
						// we have left the ring, don't retry.
//						log("Not trying: " + name +" again, state is unjoined.");
						// instead, search for other ringnodes to re-join
						agent.getComponentFeature(IExecutionFeature.class).waitForDelay(RETRY_SEARCH_DELAY, searchStep);
						// and pass the exception to the delegation future.
						delegationFuture.setException(exception);
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
	
	/**
	 * Set the given finger invalid and check what action has to be taken to
	 * re-validate this node and its fingertable.
	 * @param lostFinger the bad finger entry.
	 * @return
	 */
	private IFuture<Void> revalidate(IFinger lostFinger)
	{
		final Future<Void> ret = new Future<Void>();
		fingertable.setInvalid(lostFinger);
//		System.out.println("new fingertable");
//		System.out.println(finger);
		IFinger successor = fingertable.getSuccessor();
		if (successor.getSid() == null || successor.getSid().equals(fingertable.getSelf().getSid())) {
			// now we have no successor :(
			if (fingertable.getPredecessor() == null || fingertable.getPredecessor().getSid() == null) {
				// and no predecessor, so no connection at all.
				log("No predecessor.. :(");
				setState(State.UNJOINED);
				ret.setResult(null);
//				ret.setException(new UnjoinedException());
			} else {
				// because we still have our predecessor, we can find a new successor.
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
	
	/**
	 * Get the RingNode service for a given finger entry.
	 * @param finger
	 * @return ringnode service
	 */
	protected IFuture<IRingNodeService> getRingService(final IFinger finger) {
		final Future<IRingNodeService> ret = new Future<IRingNodeService>();
		IComponentIdentifier providerId = finger.getSid().getProviderId();
		
		IFuture<IRingNodeService> searchService = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IRingNodeService.class, providerId);
		searchService.addResultListener(new DefaultResultListener<IRingNodeService>()
		{

			@Override
			public void resultAvailable(IRingNodeService result)
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

	/**
	 * Get the finger table as String for debugging purposes.
	 * 
	 * @return String
	 */
	public IFuture<String> getFingerTableString()
	{
		return new Future<String>(fingertable.toString());
	}
	
	@Override
	public String toString()
	{
		return overlayId + " - Ringnode (" + myId + ")";
	}
}
