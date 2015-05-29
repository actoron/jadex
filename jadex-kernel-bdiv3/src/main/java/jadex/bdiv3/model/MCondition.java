package jadex.bdiv3.model;

import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.MethodInfo;
import jadex.javaparser.javaccimpl.ExpressionNode;
import jadex.javaparser.javaccimpl.Node;
import jadex.javaparser.javaccimpl.ParameterNode;
import jadex.javaparser.javaccimpl.ReflectNode;
import jadex.rules.eca.EventType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public void	initEvents(MElement pe)
	{
		if(expression!=null && expression.getParsed() instanceof ExpressionNode)
		{
			Set<String>	done	= new HashSet<String>();
			ParameterNode[]	params	= ((ExpressionNode)expression.getParsed()).getUnboundParameterNodes();
			for(ParameterNode param: params)
			{
				if("$beliefbase".equals(param.getText()))
				{
					Node parent	= param.jjtGetParent();
					if(parent instanceof ReflectNode)
					{
						ReflectNode	ref	= (ReflectNode)parent;
						if(ref.getType()==ReflectNode.FIELD)
						{
							// Todo: differentiate between beliefs/sets
							addEvent(new EventType(ChangeEvent.BELIEFCHANGED, ref.getText()));
							addEvent(new EventType(ChangeEvent.FACTCHANGED, ref.getText()));
							addEvent(new EventType(ChangeEvent.FACTADDED, ref.getText()));
							addEvent(new EventType(ChangeEvent.FACTREMOVED, ref.getText()));
						}
						
						else if(ref.getType()==ReflectNode.METHOD)
						{
							ExpressionNode	arg	= (ExpressionNode)ref.jjtGetChild(1).jjtGetChild(0);
							if("getBelief".equals(ref.getText()) && arg.isConstant() && arg.getConstantValue() instanceof String)
							{
								String	name	= (String)arg.getConstantValue();
								addEvent(new EventType(ChangeEvent.BELIEFCHANGED, ref.getText()));
								addEvent(new EventType(ChangeEvent.FACTCHANGED, name));
							}
							else if("getBeliefSet".equals(ref.getText()) && arg.isConstant() && arg.getConstantValue() instanceof String)
							{
								String	name	= (String)arg.getConstantValue();
								addEvent(new EventType(ChangeEvent.BELIEFCHANGED, ref.getText()));
								addEvent(new EventType(ChangeEvent.FACTCHANGED, name));
								addEvent(new EventType(ChangeEvent.FACTADDED, name));
								addEvent(new EventType(ChangeEvent.FACTREMOVED, name));
							}
						}
					}
				}
				
				else if("$goal".equals(param.getText()) || "$plan".equals(param.getText()))
				{
					Node parent	= param.jjtGetParent();
					if(parent instanceof ReflectNode)
					{
						ReflectNode	ref	= (ReflectNode)parent;
						if(ref.getType()==ReflectNode.FIELD && !done.contains(ref.getText()))
						{
							// Todo: differentiate between parameters/sets
							addEvent(new EventType(ChangeEvent.PARAMETERCHANGED, pe.getName(), ref.getText()));
							addEvent(new EventType(ChangeEvent.VALUECHANGED, pe.getName(), ref.getText()));
							addEvent(new EventType(ChangeEvent.VALUEADDED, pe.getName(), ref.getText()));
							addEvent(new EventType(ChangeEvent.VALUEREMOVED, pe.getName(), ref.getText()));
						}
						
						else if(ref.getType()==ReflectNode.METHOD)
						{
							ExpressionNode	arg	= (ExpressionNode)ref.jjtGetChild(1).jjtGetChild(0);
							if("getParameter".equals(ref.getText()) && arg.isConstant() && arg.getConstantValue() instanceof String)
							{
								String	name	= (String)arg.getConstantValue();
								addEvent(new EventType(ChangeEvent.PARAMETERCHANGED, pe.getName(), name));
								addEvent(new EventType(ChangeEvent.VALUECHANGED, pe.getName(), name));
							}
							else if("getParameterSet".equals(ref.getText()) && arg.isConstant() && arg.getConstantValue() instanceof String)
							{
								String	name	= (String)arg.getConstantValue();
								addEvent(new EventType(ChangeEvent.PARAMETERCHANGED, pe.getName(), name));
								addEvent(new EventType(ChangeEvent.VALUECHANGED, pe.getName(), name));
								addEvent(new EventType(ChangeEvent.VALUEADDED, pe.getName(), name));
								addEvent(new EventType(ChangeEvent.VALUEREMOVED, pe.getName(), name));
							}
						}
					}
				}
			}
		}

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
