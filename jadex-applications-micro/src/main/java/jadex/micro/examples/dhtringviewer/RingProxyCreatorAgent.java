package jadex.micro.examples.dhtringviewer;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.extension.envsupport.EnvironmentService;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.examples.fireflies.MoveAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *  The firefly agent.
 */
@Agent
@RequiredServices({
	@RequiredService(name="ringnodes", type=IRingNode.class, multiple = true, binding=@Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL, dynamic = true))
})
public class RingProxyCreatorAgent
{
	protected static final long	SEARCH_DELAY	= 30000;

	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	protected IComponentManagementService cms;
	
	protected Map<IID, ProxyHolder> proxies;

	private final double	RADIUS = 7;
	
	private Future<Void> exfut = new Future<Void>();
	
	public class ProxyHolder
	{
		public IRingNode	ringNode;
		public ISpaceObject spaceObject;
		public long lastSeen;
		public ProxyHolder(ISpaceObject proxyAgentCid, IRingNode ringNode, long lastSeen)
		{
			super();
			this.spaceObject = proxyAgentCid;
			this.lastSeen = lastSeen;
			this.ringNode = ringNode;
		}
		
	}

	
	//-------- methods --------
	
	public RingProxyCreatorAgent()
	{
		proxies = new HashMap<IID, ProxyHolder>();
	}

//	/**
//	 *  Init method.
//	 */
	@AgentCreated
	public IFuture<Void> agentCreated()
	{
//		System.out.println("proxy creator created");
		exfut.addResultListener(new IResultListener<Void>()
		{
			
			@Override
			public void resultAvailable(Void result)
			{
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				
			}
		});
		IComponentManagementService cms = SServiceProvider.getLocalService(agent, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		this.cms = cms;
		return Future.DONE;
//		throw new RuntimeException();
////		return super.agentCreated();
	}
	
	/**
	 *  Execute an agent step.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final IComponentStep<Void> step = new IComponentStep<Void>()
		{

			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IComponentStep<Void> myStep = this;
				System.out.println("proxy creator searching");
				IRequiredServicesFeature componentFeature = agent.getComponentFeature(IRequiredServicesFeature.class);
				ITerminableIntermediateFuture<Object> requiredServices = componentFeature.getRequiredServices("ringnodes");
				
				requiredServices.addResultListener(new IntermediateDefaultResultListener<Object>()
				{
					
					
					@Override
					public void intermediateResultAvailable(Object result)
					{
						final IRingNode other = (IRingNode)result;
//						final IComponentIdentifier cid = ((IService)result).getServiceIdentifier().getProviderId();
						other.getId().addResultListener(new DefaultResultListener<IID>()
						{

							@Override
							public void resultAvailable(IID id)
							{
								ProxyHolder proxyHolder = proxies.get(id);
								System.out.println("found node: " + id);
								if (proxyHolder != null) {
									proxyHolder.lastSeen = System.currentTimeMillis();
								} else {
									createSpaceObject(other).addResultListener(new DefaultResultListener<ISpaceObject>()
										{
										
										@Override
										public void resultAvailable(ISpaceObject result)
										{
											ProxyHolder proxyHolder = new ProxyHolder(result, other, System.currentTimeMillis());
											proxies.put(other.getId().get(), proxyHolder);
											positionObjects();
										}
									});
								}
							}
						});
					}
					
					@Override
					public void finished()
					{
						positionObjects().addResultListener(new DefaultResultListener<Void>()
						{
							@Override
							public void resultAvailable(Void result)
							{
								refreshConnections();
							}
						});
						agent.getExternalAccess().scheduleStep(myStep, SEARCH_DELAY);
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
//				agent.getExternalAccess().scheduleStep(step, SEARCH_DELAY);
			}
		});
		
		
		
		final Future<Void>	ret	= new Future<Void>();
//		
//		EnvironmentService.getSpace(agent)
//			.addResultListener(new ExceptionDelegationResultListener<IEnvironmentSpace, Void>(ret)
//		{
//			public void customResultAvailable(IEnvironmentSpace result)
//			{
//				final ContinuousSpace2D space = (ContinuousSpace2D)result;
//					
//				IComponentStep<Void> step = new IComponentStep<Void>()
//				{
//					public IFuture<Void> execute(IInternalAccess ia)
//					{
//						if(space==null)
//							return IFuture.DONE;
//						
//						ISpaceObject avatar = space.getAvatar(agent.getComponentDescription());
//						IVector2 mypos = (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION);
//						double dir = ((Number)avatar.getProperty("direction")).doubleValue();
//						int clock = ((Number)avatar.getProperty("clock")).intValue();
//						int threshold = ((Number)avatar.getProperty("threshold")).intValue();
//						int window = ((Number)avatar.getProperty("window")).intValue();
//						int resetlevel = ((Number)avatar.getProperty("reset_level")).intValue();
//
//						int flashestoreset = ((Number)space.getProperty("flashes_to_reset")).intValue();
//						int cyclelength = ((Number)space.getProperty("cycle_length")).intValue();
//						
//						// move
//						// change direction slightly
//						double factor = 10;
//						double rotchange = Math.random()*Math.PI/factor-Math.PI/2/factor;
//						
//						double newdir = dir+rotchange;
//						if(newdir<0)
//							newdir+=Math.PI*2;
//						else if(newdir>Math.PI*2)
//							newdir-=Math.PI*2;
//						
//						// convert to vector
//						// normally x=cos(dir) and y=sin(dir)
//						// here 0 degree is 12 o'clock and the rotation right
//						double x = Math.sin(newdir);
//						double y = -Math.cos(newdir);
////											double x = Math.sin(newdir);
////											double y = Math.cos(newdir);
//						double stepwidth = 0.1;
//						IVector2 newdirvec = new Vector2Double(x*stepwidth, y*stepwidth);
//						IVector2 newpos = mypos.copy().add(newdirvec);
//						
//						// Increment clock (internal counter)
//						clock++;
//						if(clock == cyclelength)
//							clock = 0;
//						
//						if(clock>window && clock>=threshold)
//						{
//							// Look
//							// if count turtles in-radius 1 with [color = yellow] >= flashes-to-reset
//						    // [ set clock reset-level ]
//						    Set tmp = Collections.EMPTY_SET;
//						    
//						    tmp = space.getNearObjects((IVector2)avatar.getProperty(
//								Space2D.PROPERTY_POSITION), new Vector1Int(1), "firefly", new IFilter()
//								{
//									public boolean filter(Object obj)
//									{
//										ISpaceObject fly = (ISpaceObject)obj;
//										return ((Boolean)fly.getProperty("flashing")).booleanValue();
//									}
//								});
//							tmp.remove(avatar);
//							if(tmp.size()>=flashestoreset)
//							{
//								clock = resetlevel;
////													System.out.println("Reset: "+avatar.getId());
//							}
//						}
//			
//						Map params = new HashMap();
//						params.put(ISpaceAction.OBJECT_ID, avatar.getId());
//						params.put(MoveAction.PARAMETER_POSITION, newpos);
//						params.put(MoveAction.PARAMETER_DIRECTION, Double.valueOf(newdir));
//						params.put(MoveAction.PARAMETER_CLOCK, Integer.valueOf(clock));
//						space.performSpaceAction("move", params, null);
//						
//						agent.getComponentFeature(IExecutionFeature.class).waitForTick(this);
//						return IFuture.DONE;
//					}
//					
//					public String toString()
//					{
//						return "firebug.body()";
//					}
//				};
//				
//				agent.getComponentFeature(IExecutionFeature.class).waitForTick(step);
//			}
//		});				
		
		return ret; // never kill!
	}
	
	
	private IFuture<ISpaceObject> createSpaceObject(final IRingNode other)
	{
		final Future<ISpaceObject> future = new Future<ISpaceObject>();
//		System.out.println("Creating proxy for: " + other.getId().get());
		EnvironmentService.getSpace(agent).addResultListener(new DefaultResultListener<IEnvironmentSpace>()
		{

			@Override
			public void resultAvailable(IEnvironmentSpace space)
			{
//				space.getAvatar(owner)
				HashMap<String, Object> params = new HashMap<String,Object>();
				params.put("hash", other.getId().get().toString());
				params.put("ringnode", other);
//				params.put("successorDistance", "1.0");
//				params.put("successorRotation", "1.0");
				ISpaceObject spaceObject = space.createSpaceObject("ringproxy", params, null);
				future.setResult(spaceObject);
			}
		});
		
//		CreationInfo ci = new CreationInfo();
//		ci.setParent(agent.getComponentIdentifier());
		
//		cms.createComponent(RingProxyAgent.class.getName() + ".class", ci).addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map>()
//		{
//
//			@Override
//			public void firstResultAvailable(IComponentIdentifier result)
//			{
//				future.setResult(result);
//			}
//
//			@Override
//			public void secondResultAvailable(Map result)
//			{
//				ringTerminated(other);
//			}
//
//			@Override
//			public void exceptionOccurred(Exception exception)
//			{
//				future.setException(exception);
//			}
//		});
		return future;
		
//		return new Future<IComponentIdentifier>(new ComponentIdentifier("test"));
	}

	protected void ringTerminated(IRingNode other)
	{
		final ProxyHolder proxyHolder = proxies.get(other);
		if (proxyHolder != null && proxyHolder.spaceObject != null) {
			System.out.println("Removing proxy: " + proxyHolder.spaceObject);
			proxies.remove(proxyHolder);
			EnvironmentService.getSpace(agent).addResultListener(new DefaultResultListener<IEnvironmentSpace>()
			{

				@Override
				public void resultAvailable(IEnvironmentSpace space)
				{
					space.destroySpaceObject(proxyHolder.spaceObject.getId());
				}
			});
//			cms.destroyComponent(proxyHolder.spaceObject);
		}
	}
	
	private IFuture<Void> positionObjects()
	{
		final Future<Void> future = new Future<Void>();
		
		System.out.println("positioning...");
		
		final ArrayList<IID> order = new ArrayList<IID>();
		
		Set<IID> keySet = proxies.keySet();
		
		order.addAll(keySet);
		
		Collections.sort(order, new Comparator<IID>()
		{

			@Override
			public int compare(IID o1, IID o2)
			{
				return o1.compareTo(o2);
			}
		});
		
		if (!order.isEmpty()) {
			EnvironmentService.getSpace(agent).addResultListener(new DefaultResultListener<IEnvironmentSpace>()
			{
	
				@Override
				public void resultAvailable(IEnvironmentSpace space)
				{
					int size = order.size();
					for(int i = 0; i < size; i++)
					{
						IID id = order.get(i);
						double alpha = (i+1)*(2*Math.PI/size);
						double x = Math.cos(alpha) * RADIUS + RADIUS;
						double y = Math.sin(alpha) * RADIUS + RADIUS;
						
//						System.out.println(id + " - position: " + x + " / " + y);
						final Vector2Double position = new Vector2Double(x, y);
						
						final ISpaceObject so = proxies.get(id).spaceObject;
						if (so != null) {
							
							ISpaceObject spaceObject0 = ((Space2D)space).getSpaceObject0(so.getId());
							if (spaceObject0 != null) {
								// TODO: could be removed in the meantime!
	//							Map params = new HashMap();
	//							params.put(ISpaceAction.OBJECT_ID, so.getId());
	//							params.put(MoveAction.PARAMETER_POSITION, position);
	//							params.put(MoveAction.PARAMETER_DIRECTION, Double.valueOf(0));
	//							params.put(MoveAction.PARAMETER_CLOCK, Integer.valueOf(1));
	//							space.performSpaceAction("move", params, null);
								((Space2D)space).setPosition(so.getId(), position); //TODO: space object not found :(
							}
						}
					}
					
					future.setResult(null);
				};
				
			});
			
		}
		
		return future;
	}
	
	private void refreshConnections() {
		System.out.println("refreshing connections...");
		Set<IID> keySet = proxies.keySet();
		
		for(Entry<IID, ProxyHolder> entry : proxies.entrySet())
		{
			final IID id = entry.getKey();
			ProxyHolder holder = entry.getValue();
			
			if (holder.lastSeen < System.currentTimeMillis() - 3*SEARCH_DELAY) {
				System.out.println("should be removed: " + id);
			}
			
			final ISpaceObject so = holder.spaceObject;
			final IVector2 pos = (IVector2)so.getProperty(Space2D.PROPERTY_POSITION);
			
			holder.ringNode.getSuccessor().addResultListener(new MyResultListener<IFinger>(id)
			{

				
				public void resultAvailable(IFinger result)
				{
					IID sucId = result.getNodeId();
					so.setProperty("successorHash", sucId);
					ProxyHolder proxyHolder = proxies.get(sucId);
					if (proxyHolder != null) {
						// successor is known
						IVector2 sucPos = (IVector2)proxyHolder.spaceObject.getProperty(Space2D.PROPERTY_POSITION);
						
						IVector2 connectionVector = sucPos.copy().subtract(pos);
						connectionVector.subtract(connectionVector.copy().normalize());
						
						double distance = sucPos.getDistance(pos).getAsDouble();
						distance = distance - 1;
						
//						distance = connectionVector.getLength().getAsDouble()-1;
						
						double rot = connectionVector.getDirectionAsDouble() + Math.PI;
						
//						directionVector.getX();
//						directionVector.getY();
						
						System.out.println(id + " to " + sucId + " - distance: " + distance + ", rotation: " + rot);

						so.setProperty("successorDistance", distance);
						so.setProperty("successorRotation", rot);
						so.setProperty("connectionVector", connectionVector);
						
						// TODO: get finger table
						getRingService(result).get().getFingers().addResultListener(new DefaultResultListener<List<IFinger>>()
						{

							@Override
							public void resultAvailable(List<IFinger> result)
							{
								int i = 0;
								for(IFinger f: result)
								{
									so.setProperty("F" + i, f.getStart() + "("+f.getNodeId() +")");
									i++;
								}
							}
						});
						
					} else {
						// successor unknown :(
						so.setProperty("successorDistance", 0);
						so.setProperty("successorRotation", 0);
						
						so.setProperty("connectionVector", new Vector2Double(0,0));
					}
				}

			});
		}
	}
	
	abstract class MyResultListener<T> implements IResultListener<T> {

		private IID	id;

		public MyResultListener(IID id)
		{
			this.id = id;
		}
		
		@Override
		public void exceptionOccurred(Exception exception)
		{
			final ProxyHolder proxyHolder = proxies.get(id);
			if (proxyHolder != null && proxyHolder.spaceObject != null) {
				System.out.println("Removing proxy: " + proxyHolder.spaceObject);
				proxies.remove(proxyHolder);
				EnvironmentService.getSpace(agent).addResultListener(new DefaultResultListener<IEnvironmentSpace>()
				{

					@Override
					public void resultAvailable(IEnvironmentSpace space)
					{
						space.destroySpaceObject(proxyHolder.spaceObject.getId());
					}
				});
			}
		}
		
	}
	
	protected IFuture<IRingNode> getRingService(final IFinger finger) {
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
//				revalidate(finger).get();
				ret.setException(exception);
			}
		});
		return ret;
	}
	
}
