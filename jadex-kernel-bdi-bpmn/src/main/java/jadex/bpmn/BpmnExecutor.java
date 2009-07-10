package jadex.bpmn;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ThreadContext;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.ICommand;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPoolFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *  A rule system executor can execute rule systems on a separate thread.
 */
public class BpmnExecutor //implements ISteppable
{
	//-------- attributes --------

	/** The stepmode flag. */
	protected boolean stepmode;
	
	/** Flag indicating that a single step should be performed. */
	protected boolean dostep;
	
	/** The bpmn instance. */
	protected BpmnInstance instance;
	
	/** The executor. */
	protected Executor executor;
	
	/** The breakpoints (i.e. rules that set the interpreter to step mode, when activated). */
	protected Set breakpoints;
	
	/** The breakpoint commands. */
	protected ICommand[]	breakpointcommands;

	
	//-------- constructors --------
	
	/**
	 *  Executor for bpmn instances.
	 */
	public BpmnExecutor(final BpmnInstance instance, boolean stepmode)
	{
		this(instance, stepmode, null);
	}
	
	/**
	 *  Executor for bpmn instances.
	 */
	public BpmnExecutor(final BpmnInstance bpmninstance, boolean stepmode, IThreadPool threadpool)
	{
		this.instance = bpmninstance;
		final IThreadPool pool = threadpool!=null? threadpool: ThreadPoolFactory.createThreadPool();
		this.executor = new Executor(pool, 
			new IExecutable()
			{
				public boolean execute()
				{
					// Check for breakpoints, if any.
					if(breakpoints!=null)
					{
						ThreadContext context = instance.getThreadContext();
						Set threads = context.getAllThreads();
						for(Iterator it = threads.iterator(); it.hasNext(); )
						{
							ProcessThread pc = (ProcessThread)it.next();
							if(breakpoints.contains(pc.getNextActivity()))
							{
								setStepmode(true);
								
								// Notify listeners
								if(breakpointcommands!=null)
								{
									for(int i=0; i<breakpointcommands.length; i++)
									{
										breakpointcommands[i].execute(pc);
									}
								}
								break;
							}
						}
					}
					
					if(instance.isReady())
					{						
						System.out.println("Executing step: "+instance);
						
						if(!isStepmode() || BpmnExecutor.this.dostep)
						{
							BpmnExecutor.this.dostep = false;
							{
								instance.executeStep();
							}
						}
					}
						
					if(instance.isFinished())
					{
						System.out.println("Finished: "+instance);
						executor.shutdown(null);
						pool.dispose();
					}
						
					return instance.isReady() && !isStepmode();
				}
			}
		);
		
		instance.addChangeListener(new IChangeListener()
		{				
			public void changeOccurred(ChangeEvent event)
			{
				executor.execute();
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
	public void	addBreakpoint(MActivity activity)
	{
		if(breakpoints==null)
			breakpoints	= new HashSet();
		breakpoints.add(activity);
	}
	
	/**
	 *  Remove a breakpoint from the interpreter.
	 */
	public void	removeBreakpoint(MActivity rule)
	{
		if(breakpoints.remove(rule) && breakpoints.isEmpty())
			breakpoints	= null;
	}
	
	/**
	 *  Check if a rule is a breakpoint for the interpreter.
	 */
	public boolean	isBreakpoint(MActivity rule)
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
	 *  Get the bpmn instance.
	 *  @return The bpmn instance. 
	 */
	public BpmnInstance getBpmnInstance()
	{
		return this.instance;
	}
}
