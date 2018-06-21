package jadex.bdiv3.model;

import java.util.ArrayList;
import java.util.List;

import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.MethodInfo;
import jadex.rules.eca.EventType;

/**
 *  Model element for conditions.
 */
public class MCondition extends MElement
{
	/** The events this condition depends on. */
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
	 *	Create a new mcondition. 
	 */
	public MCondition(UnparsedExpression exp)
	{
		super(exp.getName());
		this.expression = exp;
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
	 *  Init the event, when loaded from xml.
	 */
	public void	initEvents(MParameterElement owner)
	{
		if(events==null)
			events = new ArrayList<EventType>();
		BDIAgentFeature.addExpressionEvents(expression, events, owner);
	}
	
	/**
	 *  The events to set.
	 *  @param events The events to set
	 */
	public void setEvents(List<EventType> events)
	{
		this.events = events;
	}
	
	/**
	 *  Add an event.
	 *  @param event The event.
	 */
	public void addEvent(EventType event)
	{
		if(events==null)
			events = new ArrayList<EventType>();
		if(!events.contains(event))
			events.add(event);
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
