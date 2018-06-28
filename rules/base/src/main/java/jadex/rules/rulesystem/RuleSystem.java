package jadex.rules.rulesystem;

import jadex.rules.state.IOAVState;

/**
 *  Rule system is a container for state, rule base,
 *  and pattern matcher with agenda.
 */
public class RuleSystem
{
	//-------- attributes --------
	
	/** The working memory containing all facts. */
	protected IOAVState state;
	
	/** The rule base containing all the rules. */
	protected IRulebase rulebase;
	
	/** The pattern matcher functionality for evaluating rules. */
	protected IPatternMatcherFunctionality matcherfunc;
	
	/** The matcher state. */
	protected IPatternMatcherState matcherstate;
	
	/** Flag to check if the rule system was already initialized. */
	protected boolean	inited;
			
	//-------- constructors --------
	
	/**
	 *  Create a new rule system.
	 */
	public RuleSystem(IOAVState state, IRulebase rulebase, IPatternMatcherFunctionality matcherfunc)
	{
		this(state, rulebase, matcherfunc, new FIFOAgenda());
	}
	
	/**
	 *  Create a new rule system.
	 */
	public RuleSystem(IOAVState state, IRulebase rulebase, IPatternMatcherFunctionality matcherfunc, AbstractAgenda agenda)
	{
		this.state	= state;
		this.rulebase	= rulebase;		
		this.matcherfunc	= matcherfunc;
		this.matcherstate = matcherfunc.createMatcherState(state, agenda);		
	}

	//-------- methods --------
	
	/**
	 *  Initialize the rule system.
	 *  The rule system needs to be initialized once, before
	 *  rules can be fired.
	 *  When trying to initialize the rule system twice, an exception is thrown.
	 *  Exceptions are also thrown, when trying to fire rules of a not yet
	 *  initialized rule system
	 */
	public void init()
	{
		if(inited)
			throw new RuntimeException("Cannot initialize already initialized rule system.");
		
		this.inited	= true;
		
		// Initialize matcher with existing objects.
		matcherstate.init();
	}
	
	/**
	 *  Get the inited.
	 *  @return the inited.
	 */
	public boolean isInited()
	{
		return inited;
	}

	/**
	 *  Get the memory.
	 *  @return The memory.
	 */
	public IOAVState getState()
	{
		return state;
	}

	/**
	 *  Get the rulebase.
	 *  @return The rulebase.
	 */
	public IRulebase getRulebase()
	{
		return rulebase;
	}

	/**
	 *  Get the agenda.
	 *  The agenda can only be accessed, after the rule system
	 *  has been initialized with {@link #init()}.
	 *  @return The agenda.
	 */
	public IAgenda getAgenda()
	{
		// This is a convenience check for users, which forget to init().
		if(!inited)
			throw new RuntimeException("Cannot access agenda before rule system is inited.");

		return matcherstate.getAgenda();
	}
	
	/**
	 *  Fire all rules until quiescence.
	 */
	public void	fireAllRules()
	{
		IAgenda	agenda	= getAgenda(); 
		state.notifyEventListeners();
		do
		{
			agenda.fireRule();
			state.expungeStaleObjects();
			state.notifyEventListeners();
		}
		while(!agenda.isEmpty());
	}

	//-------- internal methods --------
	
	/**
	 *  Get the matcher functionality.
	 *  @return The matcher functionality.
	 */
	public IPatternMatcherFunctionality getMatcherFunctionality()
	{
		return matcherfunc;
	}
	
	/**
	 *  Get the matcher state.
	 *  @return The matcher state.
	 */
	public IPatternMatcherState getMatcherState()
	{
		return matcherstate;
	}
}
