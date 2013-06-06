package jadex.bdiv3.model;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 */
public class MCondition extends MElement
{
	/** The events this condition depends on. */
	protected Set<String> events;
	
	/** The target method. */
	protected MethodInfo mtarget;
	
	/** The target constructor. */
	protected ConstructorInfo ctarget;
	
	/**
	 *  Create a new mcondition. 
	 */
	public MCondition(String name, String[] events)
	{
		super(name);
		if(events!=null && events.length>0)
		{
			this.events = new HashSet<String>();
			for(String ev: events)
			{
				this.events.add(ev);
			}
		}
	}

	/**
	 *  Get the mtarget.
	 *  @return The mtarget.
	 */
	public MethodInfo getMethodTarget()
	{
		return mtarget;
	}

	/**
	 *  Set the mtarget.
	 *  @param mtarget The mtarget to set.
	 */
	public void setMethodTarget(MethodInfo mtarget)
	{
		this.mtarget = mtarget;
	}

	/**
	 *  Get the ctarget.
	 *  @return The ctarget.
	 */
	public ConstructorInfo getConstructorTarget()
	{
		return ctarget;
	}

	/**
	 *  Set the ctarget.
	 *  @param ctarget The ctarget to set.
	 */
	public void setConstructorTarget(ConstructorInfo ctarget)
	{
		this.ctarget = ctarget;
	}

	/**
	 *  Get the events.
	 *  @return The events.
	 */
	public Set<String> getEvents()
	{
		return events;
	}
	
	
}
