package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.IComponentChangeEvent;
import jadex.commons.SUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;


/**
 *  Handler for parallel split and join gateways.
 */
public class GatewayParallelActivityHandler implements IActivityHandler
{
	/** The split id counter. */
	protected int splitidcnt;
	
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
			int splitid = getNextSplitId();
			
			for(int i=0; i<outgoing.size(); i++)
			{
				if(i==0)
				{
					thread.setLastEdge((MSequenceEdge)outgoing.get(i));
					thread.pushSplitInfo(splitid, outgoing.size());
				}
				else
				{
					ProcessThread	newthread	= thread.createCopy();
					newthread.setLastEdge((MSequenceEdge)outgoing.get(i));
					newthread.pushSplitInfo(splitid, outgoing.size());
					thread.getThreadContext().addThread(newthread);
//					instance.notifyListeners(BpmnInterpreter.EVENT_THREAD_ADDED, newthread);
					ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, BpmnInterpreter.TYPE_THREAD, thread.getClass().getName(), 
						thread.getId(), instance.getComponentIdentifier(), instance.createProcessThreadInfo(newthread));
					instance.notifyListeners(cce);
				}
			}
		}
		
		// Join
		else if(incoming!=null && incoming.size()>1 && outgoing!=null && outgoing.size()==1)
		{
			// Try to find threads for all incoming edges.
			Set	edges	= new HashSet(incoming);
			Set	threads	= new LinkedHashSet();	// Threads to be deleted.
			edges.remove(thread.getLastEdge());	// Edge of current thread not required.
			
			for(Iterator it=thread.getThreadContext().getThreads().iterator(); !edges.isEmpty() && it.hasNext(); )
			{
				ProcessThread oldthread	= (ProcessThread)it.next();
				if(oldthread.getSplitId()==thread.getSplitId() && edges.contains(oldthread.getLastEdge()))
				{
					threads.add(oldthread);
					edges.remove(oldthread.getLastEdge());
				}
			}
			
			if(edges.isEmpty())
			{
				// Find surviving thread (incoming thread has deepest stack).
				ProcessThread tmp = thread;
				for(Iterator it=threads.iterator(); it.hasNext(); )
				{
					ProcessThread pt = (ProcessThread)it.next();
					if(pt.getSplitDepth()>tmp.getSplitDepth())
					{
						tmp = pt;
					}
				}
				if(!tmp.equals(thread))
				{
					thread.setSplitInfos(tmp.getSplitInfos());
//					threads.remove(tmp);
//					threads.add(thread);
//					thread = tmp;
				}
				
				// Reset split settings.
				thread.popSplitInfo();
				
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
				
				for(Iterator it=threads.iterator(); it.hasNext(); )
				{
					ProcessThread pt = (ProcessThread)it.next();
					pt.popSplitInfo();
					
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
				}
			}
			else
			{
//				thread.setWaitingState(ProcessThread.WAITING_FOR_JOIN);
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
	 *  Get next split id.
	 */
	protected int getNextSplitId()
	{
		return ++splitidcnt;
	}

}
