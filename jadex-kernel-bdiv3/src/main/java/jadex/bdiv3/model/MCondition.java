package jadex.bdiv3.model;

import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.MethodInfo;
import jadex.rules.eca.EventType;

import java.util.List;

/**
 * 
 */
public class MCondition extends MElement
{
	/** The events this condition depends on. */
//	protected Set<String> events;
	protected List<EventType> events;
	
	//-------- pojo part --------
	
	/** The target method. */
	protected MethodInfo mtarget;
	
	/** The target constructor. */
	protected ConstructorInfo ctarget;
	
	//-------- additional xml properties --------
	
	/** Expression. */
	protected UnparsedExpression expression;
	
	/**
	 *	Bean Constructor. 
	 */
	public MCondition()
	{
	}
	
	/**
	 *  Create a new mcondition. 
	 */
	public MCondition(String name, List<EventType> events)
	{
		super(name);
		this.events = events;
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
	public List<EventType> getEvents()
	{
		return events;
	}
	
	/**
	 *  Get the expression.
	 */
	public UnparsedExpression getExpression()
	{
		return expression;
	}
	
	/**
	 *  Set the expression.
	 */
	public void setExpression(UnparsedExpression expression)
	{
		this.expression = expression;
	}
}
