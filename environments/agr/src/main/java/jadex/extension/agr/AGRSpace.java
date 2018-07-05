package jadex.extension.agr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jadex.application.IExtensionInstance;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.IFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFutureCommandResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  An AGR (agent-group-role) space.
 */
public class AGRSpace	implements IExtensionInstance
{
	//-------- attributes --------
	
	/** The groups. */
	protected Map<String, Group> groups;
	
	/** The external access. */
	protected IExternalAccess exta;
	
	/** The config. */
	protected MAGRSpaceInstance	config;
	
	//-------- constructors --------
	
	public AGRSpace(IExternalAccess exta, MAGRSpaceInstance config)
	{
		this.exta	= exta;
		this.config	= config;
	}
	
	//-------- methods --------
	
	/**
	 *  Add a group to the space.
	 *  @param group	The group to add. 
	 */
	public synchronized	void addGroup(Group group)
	{
		if(groups==null)
			groups	= new HashMap<String, Group>();
		
		groups.put(group.getName(), group);
	}
	
	/**
	 *  Get a group by name.
	 *  @param name	The name of the group.
	 *  @return	The group (if any).
	 */
	public synchronized Group	getGroup(String name)
	{
		return groups!=null ? (Group)groups.get(name) : null;
	}	

	/**
	 *  Called from application component, when a component was added.
	 *  @param cid	The id of the added component.
	 */
	public synchronized void componentAdded(IComponentDescription desc)//, String type)
	{
		if(groups!=null)
		{
			for(Iterator<Group> it=groups.values().iterator(); it.hasNext(); )
			{
				Group	group	= it.next();
				String type = desc.getLocalType();
				String[]	roles	= group.getRolesForType(type);
				for(int r=0; roles!=null && r<roles.length; r++)
				{
					group.assignRole(desc.getName(), roles[r]);
				}
			}
		}
	}

	/**
	 *  Called from application component, when a component was removed.
	 *  @param cid	The id of the removed component.
	 */
	public synchronized void componentRemoved(IComponentDescription cid)
	{
		// nothing to do.
	}
	
	/**
	 *  Initialize the extension.
	 *  Called once, when the extension is created.
	 */
	public IFuture<Void> init()
	{
//		this.application = application;
		
		final Future<Void> ret = new Future<Void>();
		
//		System.out.println("init agr space: "+exta);
		
		try
		{
			MGroupInstance[]	mgroups	= ((MAGRSpaceInstance)config).getMGroupInstances();
			for(int g=0; g<mgroups.length; g++)
			{
				Group	group	= new Group(mgroups[g].getName());
				this.addGroup(group);
				
				MPosition[]	positions	= mgroups[g].getMPositions();
				for(int p=0; positions!=null && p<positions.length; p++)
				{
					String	at	= positions[p].getComponentType();
					String	rt	= positions[p].getRoleType();
					group.addRoleForType(at, rt);
				}
			}
			
			exta.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
//					ia.addComponentListener(new IComponentListener()
//					{
//						IFilter filter = new IFilter()
//						{
//							public boolean filter(Object obj)
//							{
//								IComponentChangeEvent event = (IComponentChangeEvent)obj;
//								return event.getSourceCategory().equals(StatelessAbstractInterpreter.TYPE_COMPONENT);
//							}
//						};
//						public IFilter getFilter()
//						{
//							return filter;
//						}
//						
//						public IFuture<Void> eventOccured(IComponentChangeEvent cce)
//						{
//							if(cce.getEventType().equals(IComponentChangeEvent.EVENT_TYPE_CREATION))
//							{
////								System.out.println("add: "+cce.getDetails());
//								componentAdded((IComponentDescription)cce.getDetails());
//							}
//							else if(cce.getEventType().equals(IComponentChangeEvent.EVENT_TYPE_DISPOSAL))
//							{
////								System.out.println("rem: "+cce.getComponent());
//								componentRemoved((IComponentDescription)cce.getDetails());
//							}
//							return IFuture.DONE;
//						}
//					});
					
					final ISubscriptionIntermediateFuture<IMonitoringEvent> sub = ia.getFeature(IMonitoringComponentFeature.class).subscribeToEvents(new IFilter<IMonitoringEvent>()
					{
						public boolean filter(IMonitoringEvent obj)
						{
							return obj.getType().endsWith(IMonitoringEvent.SOURCE_CATEGORY_COMPONENT)
								|| obj.getType().equals(IMonitoringEvent.TYPE_SUBSCRIPTION_START);
						}
					}, false, PublishEventLevel.COARSE);
					
					sub.addResultListener(new IIntermediateFutureCommandResultListener<IMonitoringEvent>()
					{
						public void resultAvailable(Collection<IMonitoringEvent> result)
						{
						}
						
						public void intermediateResultAvailable(IMonitoringEvent result)
						{
							if(result.getType().equals(IMonitoringEvent.TYPE_SUBSCRIPTION_START))
							{
//								System.out.println("agr space subscribed");
								ret.setResult(null);
							}	
							else if(result.getType().startsWith(IMonitoringEvent.EVENT_TYPE_CREATION))
							{
//								System.out.println("add: "+cce.getDetails());
								componentAdded((IComponentDescription)result.getProperty("details"));	
							}
							else if(result.getType().startsWith(IMonitoringEvent.EVENT_TYPE_DISPOSAL))
							{
								componentRemoved((IComponentDescription)result.getProperty("details"));
							}
						}
						
					    public void finished()
					    {
					    }
					    
					    public void exceptionOccurred(Exception e)
					    {
					    	e.printStackTrace();
					    }
					    
					    public void commandAvailable(Object command)
					    {
					    	// ignore timer updates
					    }
					});
					
					return IFuture.DONE;
				}
			});//.addResultListener(new DelegationResultListener<Void>(ret));
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			ret.setException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Initialize the extension.
	 *  Called once, when the extension is terminate.
	 */
	public IFuture<Void> terminate()
	{
		return IFuture.DONE;
	}
}
