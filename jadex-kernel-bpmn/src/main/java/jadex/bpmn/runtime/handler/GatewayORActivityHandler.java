package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ProcessThreadValueFetcher;
import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.IComponentChangeEvent;
import jadex.commons.IValueFetcher;
import jadex.commons.SUtil;
import jadex.javaparser.IParsedExpression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


/**
 *  Handler for or split and join gateways.
 */
public class GatewayORActivityHandler implements IActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
		// Notify listeners as gateways are not followed by step handler execution
		instance.notifyListeners(instance.createActivityEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL, thread, activity));

		List	incoming	= activity.getIncomingSequenceEdges();
		List	outgoing	= activity.getOutgoingSequenceEdges();
		
		// Split
		if(incoming!=null && incoming.size()==1 && outgoing!=null && outgoing.size()>1)
		{
			List threads = new ArrayList();
			IValueFetcher fetcher = new ProcessThreadValueFetcher(thread, false, instance.getFetcher());
			for(int i=0; i<outgoing.size(); i++)
			{
				MSequenceEdge edge = (MSequenceEdge)outgoing.get(i);
				IParsedExpression exp = edge.getCondition();
				boolean follow = true;
				if(exp!=null)
				{
					if(edge.isDefault())
						throw new RuntimeException("Default edge must not have a condition: "+activity+", "+instance+", "+thread+", "+exp);
					follow = isValid(thread, exp, fetcher);
				}

				if(follow)
				{
					if(threads.size()==0)
					{
						thread.setLastEdge((MSequenceEdge)outgoing.get(i));
						threads.add(thread);
					}
					else
					{
						ProcessThread	newthread	= thread.createCopy();
						newthread.setLastEdge((MSequenceEdge)outgoing.get(i));
						thread.getThreadContext().addThread(newthread);
//						ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, BpmnInterpreter.TYPE_THREAD, thread.getClass().getName(), 
//							thread.getId(), instance.getComponentIdentifier(), instance.getCreationTime(), instance.createProcessThreadInfo(newthread));
//						instance.notifyListeners(cce);
						threads.add(newthread);
					}
				}
			}
			
			SplitInfo	spi	= new SplitInfo(threads.size());
			for(int i=0; i<threads.size(); i++)
			{
				ProcessThread pt = (ProcessThread)threads.get(i);
				pt.addSplitInfo(spi);
			}
		}
		
		// Join
		else if(incoming!=null && incoming.size()>1 && outgoing!=null && outgoing.size()==1)
		{
			// Try to find threads for all incoming edges.
			Map<SplitInfo, Set<MSequenceEdge>>	edges	= new LinkedHashMap<SplitInfo, Set<MSequenceEdge>>();
			Map<SplitInfo, Set<ProcessThread>>	threads	= new LinkedHashMap<SplitInfo, Set<ProcessThread>>();
			for(Iterator<SplitInfo> it=thread.getSplitInfos().iterator(); it.hasNext(); )
			{
				SplitInfo	spi	= it.next();
				edges.put(spi, new HashSet<MSequenceEdge>(incoming));	// edges to be found
				edges.get(spi).remove(thread.getLastEdge());	// Edge of current thread not required.
				threads.put(spi, new LinkedHashSet<ProcessThread>());	// found threads to be deleted.
			}
			
			SplitInfo	joinspi	= null;
			for(Iterator<ProcessThread> it=thread.getThreadContext().getThreads().iterator(); joinspi==null && it.hasNext(); )
			{
				ProcessThread oldthread	= (ProcessThread)it.next();
				// Is the thread waiting at an incoming edge? 
				if(oldthread.isWaiting() && incoming.contains(oldthread.getLastEdge()))
				{
					// Update lists for matching split infos
					for(Iterator<SplitInfo> it2=thread.getSplitInfos().iterator(); joinspi==null && it2.hasNext(); )
					{
						SplitInfo	spi	= it2.next();
						if(oldthread.getSplitInfos().contains(spi) && edges.get(spi).contains(oldthread.getLastEdge()))
						{
							edges.get(spi).remove(oldthread.getLastEdge());							
							threads.get(spi).add(oldthread);
							
							// Found a completed join?
							if(threads.get(spi).size()==spi.getSplitCount()-1)
							{
								joinspi	= spi;
							}
						}
					}
				}
			}
			
			if(joinspi!=null)
			{
				// Add additional split infos from joined threads (required for unbalanced split/join situations).
				for(Iterator<ProcessThread> it=threads.get(joinspi).iterator(); it.hasNext(); )
				{
					ProcessThread pt = it.next();
					for(Iterator<SplitInfo> it2=pt.getSplitInfos().iterator(); it2.hasNext(); )
					{
						SplitInfo	spi	= it2.next();
						if(!thread.getSplitInfos().contains(spi))
						{
							thread.addSplitInfo(spi);
						}
					}
				}
				
				// Remove completed split.
				thread.removeSplitInfo(joinspi);
				
				// Handle parameter merging of incoming values.
				Set	ignore	= null;
				if(thread.hasPropertyValue("ignore"))
				{
					ignore	= new HashSet();
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
				for(Iterator it=threads.get(joinspi).iterator(); it.hasNext(); )
				{
					ProcessThread pt = (ProcessThread)it.next();
					pt.removeSplitInfo(joinspi);
					
					Map data = pt.getData();
					if(data!=null)
					{
						for(Iterator keys=data.keySet().iterator(); keys.hasNext(); )
						{
							String key = (String)keys.next();
							if(ignore==null || !ignore.contains(key))
							{
								Object value = data.get(key);
								
								if(thread.hasParameterValue(key))
								{
									Object origval =thread.getParameterValue(key);
									if(!SUtil.equals(origval, value))
									{
		//								System.out.println("origact: "+thread.getModelElement());
		//								System.out.println("act: "+pt.getModelElement());
										throw new RuntimeException("Inconsistent parameter values from threads cannot be unified in AND join: "+key+" "+value+" "+origval);
									}
								}
								else
								{
									thread.setParameterValue(key, value);
								}
							}
						}
					}
					
					thread.getThreadContext().removeThread(pt);
	//				instance.notifyListeners(BpmnInterpreter.EVENT_THREAD_REMOVED, pt);
					ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL, BpmnInterpreter.TYPE_THREAD, thread.getClass().getName(), 
						thread.getId(), instance.getComponentIdentifier(), instance.getCreationTime(), instance.createProcessThreadInfo(pt));
					instance.notifyListeners(cce);
				}
			}
			else
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
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread The process thread.
	 *  @param info The info object.
	 */
	public void cancel(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
	}
	
	/**
	 *  Safely evaluate a branch expression.
	 */
	protected boolean isValid(ProcessThread thread, IParsedExpression exp, IValueFetcher fetcher)
	{
		boolean ret = false;
		try
		{
//			System.out.println("Evaluating: "+thread.getInstance()+", "+exp.getExpressionText());
			ret = ((Boolean)exp.getValue(fetcher)).booleanValue();
//			System.out.println("Evaluated: "+ret);
		}
		catch(Exception e)
		{
			thread.getInstance().getLogger().warning("Error in branch condition: "+thread+", "+exp+", "+e);
//			e.printStackTrace();
		}
		return ret;
	}
}
	