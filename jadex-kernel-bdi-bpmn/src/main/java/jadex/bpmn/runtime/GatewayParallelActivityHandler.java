package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
	public void execute(MActivity activity, BpmnInstance instance, ProcessThread thread)
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
					instance.getThreads().add(newthread);
				}
			}
		}
		
		// Join
		else if(incoming!=null && incoming.size()>1 && outgoing!=null && outgoing.size()==1)
		{
			// Try to find threads for all incoming edges.
			Set	edges	= new HashSet(incoming);
			Set	threads	= new HashSet();	// Threads to be deleted.
			edges.remove(thread.getLastEdge());	// Edge of current thread not required.
			
			for(Iterator it=instance.getThreads().iterator(); !edges.isEmpty() && it.hasNext(); )
			{
				ProcessThread	oldthread	= (ProcessThread) it.next();
				if(edges.contains(oldthread.getLastEdge()))
				{
					threads.add(oldthread);
					edges.remove(oldthread.getLastEdge());
				}
			}
			
			if(edges.isEmpty())
			{
				thread.setLastEdge((MSequenceEdge) outgoing.get(0));
				instance.getThreads().removeAll(threads);
			}
			else
			{
				thread.setWaiting(true);
			}
		}
		
		else
		{
			throw new UnsupportedOperationException("Invalid number of edges for parallel split/join: "+activity+", "+instance);
		}
	}
}
