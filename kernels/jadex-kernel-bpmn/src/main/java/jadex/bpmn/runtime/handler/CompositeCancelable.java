package jadex.bpmn.runtime.handler;

import java.util.List;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class CompositeCancelable implements ICancelable
{
	protected List<MSequenceEdge> outgoing;
	protected ProcessThread thread;
	protected IInternalAccess instance;
	protected ICancelable[] subcancelinfos;
	
	/**
	 *  Create a new CompositeCancelable.
	 */
	public CompositeCancelable(List<MSequenceEdge> outgoing, ProcessThread thread, IInternalAccess instance, ICancelable[] subcancelinfos)
	{
		this.outgoing = outgoing;
		this.thread = thread;
		this.instance = instance;
		this.subcancelinfos = subcancelinfos;
	}
	
	/**
	 *  Cancel the activity.
	 */
	public IFuture<Void> cancel()
	{
//		List<MSequenceEdge> outgoing = activity.getOutgoingSequenceEdges();
//		Object[] waitinfos = (Object[])thread.getWaitInfo();
		
//		if(waitinfos==null)
//			System.out.println("here");
	
		for(int i=0; i<outgoing.size(); i++)
		{
			MSequenceEdge next = (MSequenceEdge)outgoing.get(i);
			MActivity act = next.getTarget();
			thread.setWaitInfo(subcancelinfos[i]);
			DefaultActivityHandler.getBpmnFeature(instance).getActivityHandler(act).cancel(act, instance, thread);
		}
		
		return IFuture.DONE;
	}

	/**
	 *  Get the subcancelinfos.
	 *  return The subcancelinfos.
	 */
	public ICancelable[] getSubcancelInfos()
	{
		return subcancelinfos;
	}
}