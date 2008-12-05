package jadex.tools.introspector;

import jadex.bdi.interpreter.AgentRules;
import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.OAVBDIFetcher;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IKernelAgent;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IToolAdapter;
import jadex.commons.ICommand;
import jadex.rules.rulesystem.Activation;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.ISteppable;
import jadex.rules.state.IOAVState;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *  Adapter for BDI introspector / debugger.
 */
public class IntrospectorAdapter implements IToolAdapter, ISteppable
{
	//-------- attibutes -------
	
	/** The agent. */
	protected BDIInterpreter	agent;
	
	/** Step mode flag. */
	protected boolean	step;
	
	/** Flag indicating that a single step should be performed. */
	protected boolean	dostep;
	
	/** The breakpoints (i.e. rules that set the interpreter to step mode, when activated). */
	protected Set breakpoints;
	
	/** The breakpoint commands. */
	protected ICommand[]	breakpointcommands;
	
	//-------- constructors --------
	
	/**
	 *  Create a new introspector adapter.
	 */
	public void init(IKernelAgent agent)
	{
		this.agent	= (BDIInterpreter)agent;

		// HACK!!! Should use runtime properties. Problem: runtime properties are initialized within start agent action.
		IOAVState	state	= this.agent.getState();
		Object	magent	= state.getAttributeValue(this.agent.getAgent(), OAVBDIRuntimeModel.element_has_model);
		Object mdebugging	= state.getAttributeValue(magent, OAVBDIMetaModel.capability_has_properties, "debugging");
		if(mdebugging!=null)
		{
			Boolean	step	= (Boolean)AgentRules.evaluateExpression(state, mdebugging, new OAVBDIFetcher(state, this.agent.getAgent()));
			this.step	= step!=null && step.booleanValue();
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Set the step mode.
	 */
	public boolean	isStepmode()
	{
		return this.step;
	}
	
	/**
	 *  Set the step mode.
	 */
	public void	setStepmode(boolean step)
	{
		this.step	= step;
		if(!step)
		{
			dostep	= false;
			agent.getAgentAdapter().wakeup();
		}
	}
	
	/**
	 *  Do a single step.
	 */
	public void	doStep()
	{
		if(!step)
			throw new RuntimeException("Only possible in step mode.");
		this.dostep	= true;
		agent.getAgentAdapter().wakeup();
	}
	
	/**
	 *  Add a breakpoint to the interpreter.
	 */
	public void	addBreakpoint(IRule rule)
	{
		if(breakpoints==null)
			breakpoints	= new HashSet();
		breakpoints.add(rule);
	}
	
	/**
	 *  Remove a breakpoint from the interpreter.
	 */
	public void	removeBreakpoint(IRule rule)
	{
		if(breakpoints.remove(rule) && breakpoints.isEmpty())
			breakpoints	= null;
	}
	
	/**
	 *  Check if a rule is a breakpoint for the interpreter.
	 */
	public boolean	isBreakpoint(IRule rule)
	{
		return breakpoints!=null && breakpoints.contains(rule);
	}
	
	/**
	 *  Add a command to be executed, when a breakpoint is reached.
	 */
	public void	addBreakpointCommand(ICommand command)
	{
		if(breakpointcommands==null)
		{
			breakpointcommands	= new ICommand[]{command};
		}
		else
		{
			ICommand[]	newarray	= new ICommand[breakpointcommands.length+1];
			System.arraycopy(breakpointcommands, 0, newarray, 0, breakpointcommands.length);
			newarray[breakpointcommands.length]	= command;
			breakpointcommands	= newarray;
		}
	}
	
	//-------- IToolAdapter interface --------
	
	/**
	 *  Called when the agent sent a message.
	 */
	public void	messageSent(IMessageAdapter msg)
	{
		// NOP.
	}
	
	/**
	 *  Called when the agent receives a message.
	 *  May be called from external (i.e. non-agent) threads.
	 *  The methods return value indicates if the message is
	 *  handled by the tool ("tool message") and
	 *    should not be propagated to the agent itself.
	 *  @return True, when the message was handled by the tool and
	 *    should not be propagated to the agent itself.
	 */
	public boolean	messageReceived(IMessageAdapter msg)
	{
		return false;
	}

	/**
	 *  Called when the agent is about to execute a step
	 *  ("agenda action").
	 *  Always called on the agent thread.
	 *  The methods return value indicates if the agent
	 *  is allowed to execute a step. If some tool
	 *  prevents the execution of steps, the agent will be
	 *  blocked until it is released by the tool.
	 *  
	 *  Messages are still received by tools and a blocking
	 *  tool should call wakeup() on the agent, once the
	 *  agent may continue to run.
	 *  
	 *  @return True, when the agent is allowed to execute
	 *    i.e. not blocked.
	 */
	public boolean executeAction()
	{
		// Check for breakpoints, if any.
		if(!step && breakpoints!=null)
		{
//			System.out.println("+++ Testing breakpoints: "+breakpoints+", "+rulesystem.getAgenda().getActivations());
			Iterator	it	= agent.getRuleSystem().getAgenda().getActivations().iterator();
			while(it.hasNext())
			{
				IRule	rule	= ((Activation)it.next()).getRule();
				if(breakpoints.contains(rule))
				{
//					System.out.println("+++ Breakpoint: "+rule.getName());
					step	= true;
					
					// Notify listeners
					if(breakpointcommands!=null)
					{
						for(int i=0; i<breakpointcommands.length; i++)
						{
							breakpointcommands[i].execute(rule);
						}
					}
					break;
				}
			}
		}

		boolean	ret	= !step || dostep;
		dostep	= false;
		return ret;
	}
}
