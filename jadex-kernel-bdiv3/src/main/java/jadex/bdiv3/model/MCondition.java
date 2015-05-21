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
	 *  Init the event, when loaded from xml.
	 */
	public void	initEvents(MElement pe)
	{
		if(expression!=null && expression.getParsed() instanceof ExpressionNode)
		{
			Set<String>	done	= new HashSet<String>();
			events	= new ArrayList<EventType>();
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
							addEvent(events, done, ChangeEvent.FACTCHANGED, ref.getText());
							addEvent(events, done, ChangeEvent.FACTADDED, ref.getText());
							addEvent(events, done, ChangeEvent.FACTREMOVED, ref.getText());
						}
						
						else if(ref.getType()==ReflectNode.METHOD)
						{
							ExpressionNode	arg	= (ExpressionNode)ref.jjtGetChild(1).jjtGetChild(0);
							if("getBelief".equals(ref.getText()) && arg.isConstant() && arg.getConstantValue() instanceof String)
							{
								String	name	= (String)arg.getConstantValue();
								addEvent(events, done, ChangeEvent.FACTCHANGED, name);
							}
							else if("getBeliefSet".equals(ref.getText()) && arg.isConstant() && arg.getConstantValue() instanceof String)
							{
								String	name	= (String)arg.getConstantValue();
								addEvent(events, done, ChangeEvent.FACTCHANGED, name);
								addEvent(events, done, ChangeEvent.FACTADDED, name);
								addEvent(events, done, ChangeEvent.FACTREMOVED, name);
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
							addEvent(events, done, ChangeEvent.VALUECHANGED, pe.getName(), ref.getText());
							addEvent(events, done, ChangeEvent.VALUEADDED, pe.getName(), ref.getText());
							addEvent(events, done, ChangeEvent.VALUEREMOVED, pe.getName(), ref.getText());
						}
						
						else if(ref.getType()==ReflectNode.METHOD)
						{
							ExpressionNode	arg	= (ExpressionNode)ref.jjtGetChild(1).jjtGetChild(0);
							if("getParameter".equals(ref.getText()) && arg.isConstant() && arg.getConstantValue() instanceof String)
							{
								String	name	= (String)arg.getConstantValue();
								addEvent(events, done, ChangeEvent.VALUECHANGED, pe.getName(), name);
							}
							else if("getParameterSet".equals(ref.getText()) && arg.isConstant() && arg.getConstantValue() instanceof String)
							{
								String	name	= (String)arg.getConstantValue();
								addEvent(events, done, ChangeEvent.VALUECHANGED, pe.getName(), name);
								addEvent(events, done, ChangeEvent.VALUEADDED, pe.getName(), name);
								addEvent(events, done, ChangeEvent.VALUEREMOVED, pe.getName(), name);
							}
						}
					}
				}
			}
		}

	}
	
	/**
	 *  Add event if not already added.
	 */
	private void	addEvent(List<EventType> events, Set<String> done, String... event)
	{
		String	ev	= null;
		for(String part: event)
		{
			if(ev==null)
			{
				ev	= part;
			}
			else
			{
				ev	+= "." + part;
			}
		}
		
		if(!done.contains(ev))
		{
			events.add(new EventType(ev));	
			done.add(ev);
		}
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
