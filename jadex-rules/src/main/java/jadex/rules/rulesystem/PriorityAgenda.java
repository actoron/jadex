package jadex.rules.rulesystem;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 *  An agenda that sorts activations according to their priority.
 */
public class PriorityAgenda extends AbstractAgenda
{
	//-------- attributes --------
	
	/** The activations. */
	protected TreeSet activations;
	
	/** The next activation (selected by strategy or manually form outside for debugging). */
	protected Activation next;
	
	/** The map containing the activation order count (for FIFO in case two activations are equal). */
	protected Map counts;
	
	/** The current count. */
	protected long count;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agenda.
	 */
	public PriorityAgenda()
	{
		this.counts = new HashMap();
		
		this.activations = new TreeSet(new Comparator()
		{
			public int compare(Object arg0, Object arg1)
			{
				Activation act0 = (Activation)arg0;
				Activation act1 = (Activation)arg1;
				
//				if(act0==null || act1==null)
//					System.out.println("test");
				
				int ret = act0.getPriority()-act1.getPriority();
				if(ret==0)
				{
					Long tmp0 = (Long)counts.get(act0);
					Long tmp1 = (Long)counts.get(act1);
					long cnt0 = tmp0==null? 0: tmp0.longValue();
					long cnt1 = tmp1==null? 0: tmp1.longValue();
					
					// FIFO
					ret = (int)(cnt1-cnt0);
				}
				
				return ret;
			}
		});
	}
	
//	long	total	= 0;
//	long	last	= 0;
//	long	lasttime	= 0;
	
	/**
	 *  Add a new activation.
	 *  @param act The activation.
	 */
	public void addActivation(Activation act)
	{
//		total	-= System.nanoTime();
//		System.out.println("Add: "+act);
		this.counts.put(act, Long.valueOf(count++));
		this.activations.add(act);
		
		state++;
		notifyListeners();

//		total	+= System.nanoTime();
//		long	currenttime	 = System.currentTimeMillis();
//		if(currenttime>lasttime+1000)
//		{
//			if(lasttime!=0)
//				System.out.println("Total agenda time: "+((int)(total/1000000.0))+" ms, "+((int)((total-last)/1000.0)/(currenttime-lasttime))/10.0+"% in last step.");
//			last	= total;
//			lasttime	= currenttime;
//		}		
	}
	
	/**
	 *  Remove an activation
	 *  @param act The activation.
	 */
	public void removeActivation(Activation act)
	{
//		total	-= System.nanoTime();
//		System.out.println("Remove: "+act);
		
		this.activations.remove(act);
		this.counts.remove(act);
		
		if(next==act)
			next = null;
		
//		state++;
		notifyListeners();

//		total	+= System.nanoTime();
//		long	currenttime	 = System.currentTimeMillis();
//		if(currenttime>lasttime+1000)
//		{
//			if(lasttime!=0)
//				System.out.println("Total agenda time: "+((int)(total/1000000.0))+" ms, "+((int)((total-last)/1000.0)/(currenttime-lasttime))/10.0+"% in last step.");
//			lasttime	= currenttime;
//			last	= total;
//		}		
	}
	
	/**
	 *  Get the current activations.
	 *  @return The activations.
	 */
	public Collection getActivations()
	{
		return activations;
	}
	
	/**
	 *  Test if the agenda is empty.
	 *  @return True if is empty.
	 */
	public boolean isEmpty()
	{
		return activations.isEmpty();
	}
	
	/**
	 *  Get the next activation.
	 *  @return The next activation.
	 */ 
	public Activation getNextActivation()
	{
		Activation ret	= next;
		if(ret==null && activations.size()>0)
		{
			ret	= (Activation)activations.last();
		}
		
		assert next==null || activations.contains(next);
		
		return ret;
	}

	/**
	 *  Set the next activation.
	 */ 
	public void setNextActivation(Activation next)
	{
//		System.out.println("Set next: "+next);
		
		if(activations.contains(next))
		{
			this.next	= next;
		}
		else
		{
			throw new RuntimeException("Activation not in agenda: "+next+", "+this);
		}
		notifyListeners();
	}
}
