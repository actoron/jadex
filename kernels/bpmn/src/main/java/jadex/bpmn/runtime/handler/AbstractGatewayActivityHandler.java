package jadex.bpmn.runtime.handler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.commons.SReflect;
import jadex.commons.SUtil;


/**
 *  Common functionality for and/or split and join gateways.
 */
public abstract class AbstractGatewayActivityHandler
{
	//-------- attributes --------
	
	/** Counter for generating unique ids. */
	protected int	idcnt;
	
	//-------- methods --------
	
	/**
	 *  Perform a split.
	 *  @return All resulting threads after the split.
	 */
	protected abstract Collection<ProcessThread>	performSplit(MActivity activity, IInternalAccess instance, ProcessThread thread);
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(MActivity activity, IInternalAccess instance, ProcessThread thread)
	{
		// Notify listeners as gateways are not followed by step handler execution
		if(instance.getFeature0(IMonitoringComponentFeature.class)!=null && instance.getFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
		{
			instance.getFeature(IMonitoringComponentFeature.class).publishEvent(DefaultActivityHandler.getBpmnFeature(instance).createActivityEvent(IMonitoringEvent.EVENT_TYPE_DISPOSAL, thread, activity), PublishTarget.TOALL);
		}
		
		List<MSequenceEdge>	incoming	= activity.getIncomingSequenceEdges();
		List<MSequenceEdge>	outgoing	= activity.getOutgoingSequenceEdges();
		
		// Split
		if(incoming!=null && incoming.size()==1 && outgoing!=null && outgoing.size()>1)
		{
			Collection<ProcessThread>	threads	= performSplit(activity, instance, thread);
			addSplitInfos(threads);
		}
		
		// Join
		else if(incoming!=null && incoming.size()>1 && outgoing!=null && outgoing.size()==1)
		{
			// Try to find threads for all incoming edges.
			boolean	joined	= performJoin(activity, thread);
			
			if(!joined)
			{
	//			thread.setWaitingState(ProcessThread.WAITING_FOR_JOIN);
				thread.setWaiting(true);
			}
		}
		else
		{
			throw new UnsupportedOperationException("Invalid number of edges for parallel split/join: "+activity+", "+instance);
		}		
	}
	
	/**
	 *  Cancel an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread The process thread.
	 */
	public void cancel(MActivity activity, IInternalAccess instance, ProcessThread thread)
	{
	}
	
	/**
	 *  Generate a unique id.
	 */
	protected String	generateId()
	{
		return SReflect.getInnerClassName(getClass()) + (++idcnt);
	}
	
	/**
	 *  Add split infos to a set of threads.
	 */
	protected void	addSplitInfos(Collection<ProcessThread> threads)
	{
		String	splitid	= generateId();
//		System.out.println("Split: "+splitid+", "+threads);
		Set<String>	pathids	= new LinkedHashSet<String>();
		for(int i=0; i<threads.size(); i++)
		{
			pathids.add(generateId());
		}
		Iterator<ProcessThread>	threadit	= threads.iterator();
		for(Iterator<String> pathit=pathids.iterator(); pathit.hasNext(); )
		{
			threadit.next().addSplitInfo(new SplitInfo(splitid, pathit.next(), pathids));
		}
	}
	
	/**
	 * 	Perform a join, if possible.
	 *  @return True, if a join was performed. 
	 */
	protected boolean	performJoin(MActivity activity, ProcessThread thread)
	{
		List<MSequenceEdge>	incoming	= activity.getIncomingSequenceEdges();
		List<MSequenceEdge>	outgoing	= activity.getOutgoingSequenceEdges();
		
		// Try to find threads for all incoming edges.
		Map<String, Set<MSequenceEdge>>	edges	= new LinkedHashMap<String, Set<MSequenceEdge>>();
		Map<String, Set<String>>	pathids	= new LinkedHashMap<String, Set<String>>();
		Map<String, Set<ProcessThread>>	threads	= new LinkedHashMap<String, Set<ProcessThread>>();
		for(Iterator<SplitInfo> it=thread.getSplitInfos().iterator(); it.hasNext(); )
		{
			SplitInfo	spi	= it.next();
			edges.put(spi.getSplitId(), new HashSet<MSequenceEdge>(incoming));	// edges to be found
			edges.get(spi.getSplitId()).remove(thread.getLastEdge());	// Edge of current thread not required.
			pathids.put(spi.getSplitId(), new HashSet<String>(spi.getPathIds()));	// pathids to be found
			pathids.get(spi.getSplitId()).remove(spi.getPathId());	// Pathid of current thread not required.
			threads.put(spi.getSplitId(), new LinkedHashSet<ProcessThread>());	// found threads to be deleted.
		}
			
		SplitInfo	joinspi	= null;
		for(Iterator<ProcessThread> it=thread.getParent().getSubthreads().iterator(); joinspi==null && it.hasNext(); )
		{
			ProcessThread oldthread	= (ProcessThread)it.next();
			// Is the thread waiting at an incoming edge? 
			if(oldthread.isWaiting() && incoming.contains(oldthread.getLastEdge()))
			{
				// Update lists for matching split infos
				for(Iterator<SplitInfo> it2=thread.getSplitInfos().iterator(); joinspi==null && it2.hasNext(); )
				{
					SplitInfo	spi	= it2.next();
					if(oldthread.getSplitInfo(spi.getSplitId())!=null
						&& edges.get(spi.getSplitId()).contains(oldthread.getLastEdge())
						&& pathids.get(spi.getSplitId()).contains(oldthread.getSplitInfo(spi.getSplitId()).getPathId()))
					{
						edges.get(spi.getSplitId()).remove(oldthread.getLastEdge());					
						pathids.get(spi.getSplitId()).remove(oldthread.getSplitInfo(spi.getSplitId()).getPathId());					
						threads.get(spi.getSplitId()).add(oldthread);
						
						// Todo: also check that all edges are filled for AND
						// Todo: check inner splits before outer (inverse iterator???)
						
						// Test if complete join is found.
						if(pathids.get(spi.getSplitId()).isEmpty())
						{
							joinspi	= spi;
//							System.out.println("Join: "+spi.getSplitId()+", "+threads);
						}
					}
				}
			}
		}
			
		if(joinspi!=null)
		{
			// Add additional split infos from joined threads (required for unbalanced split/join situations).
			// Todo: what about same split ids but different path ids!?
			for(Iterator<ProcessThread> it=threads.get(joinspi.getSplitId()).iterator(); it.hasNext(); )
			{
				ProcessThread pt = it.next();
				for(Iterator<SplitInfo> it2=pt.getSplitInfos().iterator(); it2.hasNext(); )
				{
					SplitInfo	spi	= it2.next();
					if(thread.getSplitInfo(spi.getSplitId())==null)
					{
						thread.addSplitInfo(spi);
					}
				}
			}
			
			// Remove completed split.
			thread.removeSplitInfo(joinspi);

			// Handle parameter merging of incoming values.
			Set<String>	ignore	= null;
			if(thread.hasPropertyValue("ignore"))
			{
				ignore	= new HashSet<String>();
				String	ignores	= (String)thread.getPropertyValue("ignore");
				StringTokenizer	stok	= new StringTokenizer(ignores, ", \t\r\n");
				while(stok.hasMoreTokens())
				{
					String	ign	= stok.nextToken();
					ignore.add(ign);
					thread.removeParameterValue(ign);
				}
			}
			
			thread.setLastEdge((MSequenceEdge)outgoing.get(0));
			for(Iterator<ProcessThread> it=threads.get(joinspi.getSplitId()).iterator(); it.hasNext(); )
			{
				ProcessThread pt = (ProcessThread)it.next();
				if(pt!=thread)
				{
					Map<String, Object> data = pt.getData();
					if(data!=null)
					{
						for(Iterator<String> keys=data.keySet().iterator(); keys.hasNext(); )
						{
							String key = keys.next();
							if(ignore==null || !ignore.contains(key))
							{
								Object value = data.get(key);
								
								if(thread.hasParameterValue(key))
								{
									Object origval =thread.getParameterValue(key);
									if(!SUtil.equals(origval, value))
									{
//										System.out.println("origact: "+thread.getModelElement());
//										System.out.println("act: "+pt.getModelElement());
										throw new RuntimeException("Inconsistent parameter values from threads cannot be unified in AND/OR join: "+key+" "+value+" "+origval+" "+activity);
									}
								}
								else
								{
									thread.setOrCreateParameterValue(key, value);
								}
							}
						}
					}
					
					Map<String, Object> dataedges = pt.getDataEdges();
					if(dataedges!=null)
					{
						for(Iterator<String> keys=dataedges.keySet().iterator(); keys.hasNext(); )
						{
							String key = keys.next();
							if(ignore==null || !ignore.contains(key))
							{
								Object value = dataedges.get(key);
								
								if(thread.getDataEdges().get(key)!=null)
								{
									Object origval = thread.getDataEdges().get(key);
									if(!SUtil.equals(origval, value))
									{
//										System.out.println("origact: "+thread.getModelElement());
//										System.out.println("act: "+pt.getModelElement());
										throw new RuntimeException("Inconsistent data edge values from threads cannot be unified in AND/OR join: "+key+" "+value+" "+origval+" "+activity);
									}
								}
								else
								{
									thread.setDataEdgeValue(key, value);
									
//									System.out.println("set data edge: "+key+" "+value);
								}
							}
						}
					}
					
					thread.getParent().removeThread(pt);
//					ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL, BpmnInterpreter.TYPE_THREAD, thread.getClass().getName(), 
//						thread.getId(), instance.getComponentIdentifier(), instance.getCreationTime(), instance.createProcessThreadInfo(pt));
//					instance.notifyListeners(cce);
				}
			}
		}
		
		return joinspi!=null;
	}
}
	