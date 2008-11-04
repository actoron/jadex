package jadex.tools.ontology;

/**
 *  Editable Java class for concept <code>Tracing</code> of tool_management ontology.
 */
public class Tracing	extends Predicate
{
	//-------- attributes ----------

	/** Attribute for slot belief_writes. */
	protected boolean	beliefwrites	= false;

	/** Attribute for slot belief_reads. */
	protected boolean	beliefreads		= false;

	/** Attribute for slot actions. */
	protected boolean	actions			= false;

	/** Attribute for slot plans. */
	protected boolean	plans			= false;

	/** Attribute for slot goals. */
	protected boolean	goals			= false;

	/** Attribute for slot messages. */
	protected boolean	messages		= false;

	/** Attribute for slot events. */
	protected boolean	events			= false;

   //-------- constructors --------

   /** 
    *  Default Constructor. <br>
    *  Create a new <code>Tracing</code>.
    */
   public Tracing()
   {
   // Empty constructor required for JavaBeans (do not remove).
   }

   /** 
    *  Init Constructor. <br>
    *  Create a new <code>Tracing</code>.
    * @param messages    
    * @param events    
    * @param goals    
    * @param actions    
    * @param plans    
    * @param beliefreads    
    * @param beliefwrites    
    */
   public Tracing(boolean messages, boolean events, boolean goals, boolean actions, boolean plans, boolean beliefreads,
         boolean beliefwrites)
   {
      // Constructor using required slots (change if desired).
      setMessages(messages);
      setEvents(events);
      setGoals(goals);
      setActions(actions);
      setPlans(plans);
      setBeliefReads(beliefreads);
      setBeliefWrites(beliefwrites);
   }

   /** 
    *  Clone Constructor. <br>
    *  Create a new <code>Tracing</code>.<br>
    *  Copy all attributes from <code>proto</code> to this instance.
    *
    *  @param proto The prototype instance.
    */
   public Tracing(Tracing proto)
   {
      this(proto.messages, proto.events, proto.goals, proto.actions, proto.plans, proto.beliefreads, proto.beliefwrites);
   }

   /** 
    * @return true if this object indicates that any of the agent concepts are traced
    */
   public boolean isTracing()
   {
      return messages || events || goals || plans || beliefreads || beliefwrites;
   }
   
   
   /** 
    * @param tr
    * @return true if the trace passes this tracing filter
    */
   public boolean isTracing(OTrace tr) {
      if (tr instanceof OBelief)
      {
         OBelief b = (OBelief) tr;
         return (beliefreads && OBelief.ACCESS_READ.equals(b.getAccess())) ||
                (beliefwrites && OBelief.ACCESS_WRITE.equals(b.getAccess()));
      }
      return (plans && tr instanceof OPlan) ||
             (goals && tr instanceof OGoal) ||
             (messages && tr instanceof OMessage) ||
             (actions  && tr instanceof OAction) ||
             (events   && tr instanceof OEvent);
   }
  

   /** Adds the tracing information (what concept are traced) to this Tracing object
    * @param t
    */
   public void or(Tracing t)
   {
      messages |= t.messages;
      events |= t.events;
      goals |= t.goals;
      plans |= t.plans;
      beliefreads |= t.beliefreads;
      beliefwrites |= t.beliefwrites;
   }

	//-------- accessor methods --------

	/**
	 *  Get the belief_writes of this Tracing.
	 * @return belief_writes
	 */
	public boolean isBeliefWrites()
	{
		return this.beliefwrites;
	}

	/**
	 *  Set the belief_writes of this Tracing.
	 * @param beliefwrites the value to be set
	 */
	public void setBeliefWrites(boolean beliefwrites)
	{
		this.beliefwrites = beliefwrites;
	}

	/**
	 *  Get the belief_reads of this Tracing.
	 * @return belief_reads
	 */
	public boolean isBeliefReads()
	{
		return this.beliefreads;
	}

	/**
	 *  Set the belief_reads of this Tracing.
	 * @param beliefreads the value to be set
	 */
	public void setBeliefReads(boolean beliefreads)
	{
		this.beliefreads = beliefreads;
	}

	/**
	 *  Get the actions of this Tracing.
	 * @return actions
	 */
	public boolean isActions()
	{
		return this.actions;
	}

	/**
	 *  Set the actions of this Tracing.
	 * @param actions the value to be set
	 */
	public void setActions(boolean actions)
	{
		this.actions = actions;
	}

	/**
	 *  Get the plans of this Tracing.
	 * @return plans
	 */
	public boolean isPlans()
	{
		return this.plans;
	}

	/**
	 *  Set the plans of this Tracing.
	 * @param plans the value to be set
	 */
	public void setPlans(boolean plans)
	{
		this.plans = plans;
	}

	/**
	 *  Get the goals of this Tracing.
	 * @return goals
	 */
	public boolean isGoals()
	{
		return this.goals;
	}

	/**
	 *  Set the goals of this Tracing.
	 * @param goals the value to be set
	 */
	public void setGoals(boolean goals)
	{
		this.goals = goals;
	}

	/**
	 *  Get the messages of this Tracing.
	 * @return messages
	 */
	public boolean isMessages()
	{
		return this.messages;
	}

	/**
	 *  Set the messages of this Tracing.
	 * @param messages the value to be set
	 */
	public void setMessages(boolean messages)
	{
		this.messages = messages;
	}

	/**
	 *  Get the events of this Tracing.
	 * @return events
	 */
	public boolean isEvents()
	{
		return this.events;
	}

	/**
	 *  Set the events of this Tracing.
	 * @param events the value to be set
	 */
	public void setEvents(boolean events)
	{
		this.events = events;
	}

	//-------- Object methods -----

   /** Get a string representation of this <code>Tracing</code>.
    *  @return The string representation.
    */
   public String toString()
   {
      return "Tracing(" + "messages=" + isMessages() + ", events=" + isEvents() + ", goals=" + isGoals() + ", actions="
            + isActions() + ", plans=" + isPlans() + ", beliefreads=" + isBeliefReads() + ", beliefwrites="
            + isBeliefWrites() + ")";
   }

   /** 
    *  Get a clone of this <code>Tracing</code>.
    *  @return a shalow copy of this instance.
    */
   public Object clone()
   {
      return new Tracing(this);
   }

   /** 
    *  Test the equality of this <code>Tracing</code> 
    *  and an object <code>obj</code>.
    *
    *  @param obj the object this test will be performed with
    *  @return false if <code>obj</code> is not of <code>Tracing</code> class,
    *          true if all attributes are equal.   
    */
   public boolean equals(Object obj)
   {
      if (obj instanceof Tracing)
      {
         Tracing cmp = (Tracing) obj;
         return messages == cmp.messages && events == cmp.events && goals == cmp.goals && actions == cmp.actions
               && plans == cmp.plans && beliefreads == cmp.beliefreads && beliefwrites == cmp.beliefwrites;
      }
      return false;
   }
}
