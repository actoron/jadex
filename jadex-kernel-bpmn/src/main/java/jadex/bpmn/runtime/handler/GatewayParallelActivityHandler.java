package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.commons.SUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *  Handler for parallel split and join gateways.
 */
public class GatewayParallelActivityHandler implements IActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
		List	incoming	= activity.getIncomingSequenceEdges();
		List	outgoing	= activity.getOutgoingSequenceEdges();
		
		// Split
		if(incoming!=null && incoming.size()==1 && outgoing!=null && outgoing.size()>1)
		{
			for(int i=0; i<outgoing.size(); i++)
			{
				if(i==0)
				{
					thread.setLastEdge((MSequenceEdge)outgoing.get(i));
				}
				else
				{
					ProcessThread	newthread	= thread.createCopy();
					newthread.setLastEdge((MSequenceEdge)outgoing.get(i));
					thread.getThreadContext().addThread(newthread);
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
				if(edges.contains(oldthread.getLastEdge()))
				{
					threads.add(oldthread);
					edges.remove(oldthread.getLastEdge());
				}
			}
			
			if(edges.isEmpty())
			{
				thread.setLastEdge((MSequenceEdge) outgoing.get(0));
				for(Iterator it=threads.iterator(); it.hasNext(); )
				{
					ProcessThread pt = (ProcessThread)it.next();
					
					Map data = pt.getData();
					for(Iterator keys=data.keySet().iterator(); keys.hasNext(); )
					{
						String key = (String)keys.next();
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

}
