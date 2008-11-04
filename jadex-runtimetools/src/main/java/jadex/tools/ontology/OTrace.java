package jadex.tools.ontology;


/**
 *  Java class for concept OTrace of jadex.tools.tracer ontology.
 */
public class OTrace extends Predicate
{
	//-------- attributes ----------

	/** Attribute for slot stack. */
	protected java.util.List	stack;

	/** Get the sequence number. Orignally a >long< Value Type. */
	protected String			seq;

	/** Attribute for slot value2. */
	protected String			value;

	/** Get an identifier for the thread where the message originated. */
	protected String			thread;

	/** Attribute for slot name. */
	protected String			name;

	/** Attribute for slot cause. */
	protected String			cause;

	/** Get event time in milliseconds since 1970. Orignally a >long< Value Type. */
	protected String			time;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new OTrace.
	 */
	public OTrace()
	{
		this.stack = new java.util.ArrayList();
	}

	/**
	 *  Init Constructor. <br>
	 *  Create a new OTrace.<br>
	 *  Initializes the object with required attributes.
	 * @param name
	 * @param seq
	 * @param thread
	 * @param time
	 * @param value
	 */
	public OTrace(String name, String seq, String thread, String time, String value)
	{
		this();
		setName(name);
		setSeq(seq);
		setThread(thread);
		setTime(time);
		setValue(value);
	}

	//-------- accessor methods --------

	/**
	 *  Get the stack of this OTrace.
	 * @return stack
	 */
	public String[] getStack()
	{
		return (String[])stack.toArray(new String[stack.size()]);
	}

	/**
	 *  Set the stack of this OTrace.
	 * @param stack the value to be set
	 */
	public void setStack(String[] stack)
	{
		this.stack.clear();
		for(int i = 0; i < stack.length; i++)
			this.stack.add(stack[i]);
	}

	/**
	 *  Get an stack of this OTrace.
	 *  @param idx The index.
	 *  @return stack
	 */
	public String getStack(int idx)
	{
		return (String)this.stack.get(idx);
	}

	/**
	 *  Set a stack to this OTrace.
	 *  @param idx The index.
	 *  @param stack a value to be added
	 */
	public void setStack(int idx, String stack)
	{
		this.stack.set(idx, stack);
	}

	/**
	 *  Add a stack to this OTrace.
	 *  @param stack a value to be removed
	 */
	public void addStack(String stack)
	{
		this.stack.add(stack);
	}

	/**
	 *  Remove a stack from this OTrace.
	 *  @param stack a value to be removed
	 *  @return  True when the stack have changed.
	 */
	public boolean removeStack(String stack)
	{
		return this.stack.remove(stack);
	}


	/**
	 *  Get the seq of this OTrace.
	 *  Get the sequence number. Orignally a >long< Value Type.
	 * @return seq
	 */
	public String getSeq()
	{
		return this.seq;
	}

	/**
	 *  Set the seq of this OTrace.
	 *  Get the sequence number. Orignally a >long< Value Type.
	 * @param seq the value to be set
	 */
	public void setSeq(String seq)
	{
		this.seq = seq;
	}

	/**
	 *  Get the value2 of this OTrace.
	 * @return value2
	 */
	public String getValue()
	{
		return this.value;
	}

	/**
	 *  Set the value2 of this OTrace.
	 * @param value the value to be set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 *  Get the thread of this OTrace.
	 *  Get an identifier for the thread where the message originated.
	 * @return thread
	 */
	public String getThread()
	{
		return this.thread;
	}

	/**
	 *  Set the thread of this OTrace.
	 *  Get an identifier for the thread where the message originated.
	 * @param thread the value to be set
	 */
	public void setThread(String thread)
	{
		this.thread = thread;
	}

	/**
	 *  Get the name of this OTrace.
	 * @return name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name of this OTrace.
	 * @param name the value to be set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the cause of this OTrace.
	 * @return cause
	 */
	public String getCause()
	{
		return this.cause;
	}

	/**
	 *  Set the cause of this OTrace.
	 * @param cause the value to be set
	 */
	public void setCause(String cause)
	{
		this.cause = cause;
	}

	/**
	 *  Get the time of this OTrace.
	 *  Get event time in milliseconds since 1970. Orignally a >long< Value Type.
	 * @return time
	 */
	public String getTime()
	{
		return this.time;
	}

	/**
	 *  Set the time of this OTrace.
	 *  Get event time in milliseconds since 1970. Orignally a >long< Value Type.
	 * @param time the value to be set
	 */
	public void setTime(String time)
	{
		this.time = time;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this OTrace.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "OTrace(" + "name=" + getName() + ", seq=" + getSeq() + ", thread=" + getThread() + ", time=" + getTime() + ", value=" + getValue() + ")";
	}

}
