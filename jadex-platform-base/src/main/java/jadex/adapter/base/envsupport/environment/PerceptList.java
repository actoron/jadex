package jadex.adapter.base.envsupport.environment;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.IFilter;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 *  The list of scheduled agent percepts and convenience methods for
 *  processing selected percepts.
 *  This implementation is not thread-safe, i.e. methods
 *  should only be called from threads that are already synchronized
 *  with the environment space monitor.
 */
public class PerceptList 
{
	//-------- attributes --------

	/** The environment space. */
	protected IEnvironmentSpace	space;
	
	/** The scheduled percepts. */
	protected Set	percepts;
	
	//-------- constructors --------
	
	/**
	 *  Create a new percept list.
	 */
	public PerceptList(IEnvironmentSpace space)
	{
		this.space	= space;
	}
	
	//-------- methods --------
	
	/**
	 *  Schedules a percept.
	 *  @param type	The percept type.
	 *  @param data	The content of the percept (if any).
	 *  @param agent	The agent that should receive the percept.
	 *  @param avatar	The avatar of the agent (if any).
	 *  @param processor	The percept processor.
	 */
	public void schedulePercept(String type, Object data, IComponentIdentifier agent, ISpaceObject avatar, IPerceptProcessor processor)
	{
		if(percepts==null)
			percepts	= new LinkedHashSet();
		
		percepts.add(new PerceptEntry(type, data, agent, avatar, processor));
	}

	/**
	 *  Set an ordering used for executing actions.
	 *  @param comp	The comparator representing the ordering.
	 */
	public void	setOrdering(Comparator comp)
	{
		if(percepts!=null)
		{
			Set	tmp	= new TreeSet(comp);
			tmp.addAll(percepts);
			percepts	= tmp;
		}
		else
		{
			percepts	= new TreeSet(comp);
		}
	}
	
	/**
	 *  Process scheduled percepts. Should be called on environment thread only.
	 *  @param filter	A filter to select only a subset of percepts (or null for all percepts).
	 */
	public void processPercepts(IFilter filter)
	{
		if(percepts!=null && !(percepts.isEmpty()))
		{
			for(Iterator it=percepts.iterator(); it.hasNext(); )
			{
				PerceptEntry entry = (PerceptEntry)it.next();
				try
				{
					if(filter==null || filter.filter(entry))
					{
						it.remove();
						try
						{
							entry.processor.processPercept(space, entry.type, entry.data, entry.agent, entry.avatar);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	//-------- helper classes --------
	
	/**
	 *  Entry for a scheduled percept.
	 */
	public static class PerceptEntry
	{
		//-------- attributes --------
		
		/** The percept type. */
		public String	type;
		
		/** The percept content (if any). */
		public Object	data;
		
		/** The receiving agent. */
		public IComponentIdentifier	agent;
		
		/** The avatar of the agent (if any). */
		public ISpaceObject	avatar;
		
		/** The processor. */
		public IPerceptProcessor	processor;
		
		//-------- constructors --------
		
		/**
		 *  Convenience constructor for inline entry creation.
		 */
		public PerceptEntry(String type, Object data, IComponentIdentifier agent, ISpaceObject avatar, IPerceptProcessor processor)
		{
			this.type	= type;
			this.data	= data;
			this.agent	= agent;
			this.avatar	= avatar;
			this.processor	= processor;
		}
	}
}
