package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ProcessThreadValueFetcher;
import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.IComponentChangeEvent;
import jadex.commons.SUtil;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.IValueFetcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
		//				instance.notifyListeners(BpmnInterpreter.EVENT_THREAD_ADDED, newthread);
						ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, BpmnInterpreter.TYPE_THREAD, thread.getClass().getName(), 
							thread.getId(), instance.getComponentIdentifier(), instance.createProcessThreadInfo(newthread));
						instance.notifyListeners(cce);
						threads.add(newthread);
					}
				}
			}
			
			int splitid = getNextSplitId();
			for(int i=0; i<threads.size(); i++)
			{
				ProcessThread pt = (ProcessThread)threads.get(i);
				pt.setSplitId(splitid);
				pt.setSplitCount(threads.size());
			}
		}
		
		// Join
		else if(incoming!=null && incoming.size()>1 && outgoing!=null && outgoing.size()==1)
		{
			// Try to find threads for all incoming edges.
			Set	edges	= new HashSet(incoming);
			Set	threads	= new LinkedHashSet();	// Threads to be deleted.
			edges.remove(thread.getLastEdge());	// Edge of current thread not required.
			
			// Find threads that belong to my split and have arrived at gate.
			for(Iterator it=thread.getThreadContext().getThreads().iterator(); !edges.isEmpty() && it.hasNext(); )
			{
				ProcessThread oldthread	= (ProcessThread)it.next();
				if(oldthread.getSplitId()==thread.getSplitId() && edges.contains(oldthread.getLastEdge()))
				{
					threads.add(oldthread);
					edges.remove(oldthread.getLastEdge());
				}
			}
			
			if(threads.size()==thread.getSplitCount()-1)
			{
				// Reset split settings.
				thread.setSplitCount(0);
				thread.setSplitId(0);
				
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
				for(Iterator it=threads.iterator(); it.hasNext(); )
				{
					ProcessThread pt = (ProcessThread)it.next();
					
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
						thread.getId(), instance.getComponentIdentifier(), instance.createProcessThreadInfo(pt));
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
	
	/**
	 *  Get next split id.
	 */
	protected int getNextSplitId()
	{
		return ++splitidcnt;
	}
}
	