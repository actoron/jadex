package jadex.rules.rulesystem;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jadex.commons.ICommand;
import jadex.commons.ISteppable;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPoolFactory;

/**
 *  A rule system executor can execute rule systems on a separate thread.
 */
public class RuleSystemExecutor implements ISteppable
{
	//-------- attributes --------

	/** The stepmode flag. */
	protected boolean stepmode;
	
	/** Flag indicating that a single step should be performed. */
	protected boolean dostep;
	
	/** The agenda. */
	protected RuleSystem rulesystem;
	
	/** The executor. */
	protected Executor executor;
	
	/** The breakpoints (i.e. rules that set the interpreter to step mode, when activated). */
	protected Set breakpoints;
	
	/** The breakpoint commands. */
	protected ICommand[]	breakpointcommands;
	
	//-------- constructors --------
	
	/**
	 *  Executor for rule systems.
	 */
	public RuleSystemExecutor(final RuleSystem rulesystem, boolean stepmode)
	{
		this(rulesystem, stepmode, null);
	}
	
	/**
	 *  Executor for rule systems.
	 */
	public RuleSystemExecutor(final RuleSystem rulesystem, boolean stepmode, IThreadPool threadpool)
	{
		this.rulesystem = rulesystem;
		this.executor = new Executor(threadpool!=null? threadpool: ThreadPoolFactory.createThreadPool(), 
			new IExecutable()
		{
			public boolean execute()
			{
				// Check for breakpoints, if any.
				if(breakpoints!=null)
				{
					Iterator	it	= rulesystem.getAgenda().getActivations().iterator();
					while(it.hasNext())
					{
						IRule	rule	= ((Activation)it.next()).getRule();
						if(breakpoints.contains(rule.getName()))
						{
							setStepmode(true);
							
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

				if(!isStepmode() || RuleSystemExecutor.this.dostep)
				{
					RuleSystemExecutor.this.dostep = false;
					// synchronized(monitor)
					{
						rulesystem.getAgenda().fireRule();
						rulesystem.getState().expungeStaleObjects();
						rulesystem.getState().notifyEventListeners();
						
//						for(Iterator it= rulesystem.getState().getObjects(); it.hasNext(); )
//							System.out.println(it.next());
					}
				}
				
				return !rulesystem.getAgenda().isEmpty() && !isStepmode();
			}
		});
		
		setStepmode(stepmode);
	}
	
	//-------- steppable interface --------
	
	/**
	 *  Execute a step.
	 */
	public void doStep()
	{
		dostep = true;
		if(stepmode)
			executor.execute();
	}
	
	/**
	 *  Set the stepmode.
	 *  @param stepmode True for stepmode.
	 */
	public void setStepmode(boolean stepmode)
	{
		this.stepmode = stepmode;
		if(!stepmode)
			executor.execute();
	}
	
	/**
	 *  Test if in stepmode.
	 *  @return True, if in stepmode.
	 */
	public boolean isStepmode()
	{
		return this.stepmode;
	}
	
	/**
	 *  Add a breakpoint to the interpreter.
	 */
	public void	addBreakpoint(Object rule)
	{
		if(breakpoints==null)
			breakpoints	= new HashSet();
		breakpoints.add(rule);
	}
	
	/**
	 *  Remove a breakpoint from the interpreter.
	 */
	public void	removeBreakpoint(Object rule)
	{
		if(breakpoints.remove(rule) && breakpoints.isEmpty())
			breakpoints	= null;
	}
	
	/**
	 *  Check if a rule is a breakpoint for the interpreter.
	 */
	public boolean	isBreakpoint(Object rule)
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
	
	//-------- methods --------
	
	/**
	 *  Get the rule system.
	 *  @return The rule system. 
	 */
	public RuleSystem getRulesystem()
	{
		return this.rulesystem;
	}
}
