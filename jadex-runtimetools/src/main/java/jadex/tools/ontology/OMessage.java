package jadex.tools.ontology;


/**
 *  Java class for concept OMessage of jadex.tools.tracer ontology.
 */
public class OMessage extends OTrace
{
	//-------- attributes ----------

	/** Attribute for slot to. */
	protected java.util.List	to;

	/** Attribute for slot from. */
	protected String			from;

	/** Attribute for slot incoming. */
	protected boolean			incoming;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new OMessage.
	 */
	public OMessage()
	{
		this.to = new java.util.ArrayList();
	}

	/**
	 *  Init Constructor. <br>
	 *  Create a new OMessage.<br>
	 *  Initializes the object with required attributes.
	 * @param from
	 * @param incoming
	 * @param name
	 * @param seq
	 * @param thread
	 * @param time
	 * @param value
	 */
	public OMessage(String from, boolean incoming, String name, String seq, String thread, String time, String value)
	{
		this();
		setFrom(from);
		setIncoming(incoming);
		setName(name);
		setSeq(seq);
		setThread(thread);
		setTime(time);
		setValue(value);
	}

	//-------- accessor methods --------

	/**
	 *  Get the to of this OMessage.
	 * @return to
	 */
	public String[] getTo()
	{
		return (String[])to.toArray(new String[to.size()]);
	}

	/**
	 *  Set the to of this OMessage.
	 * @param to the value to be set
	 */
	public void setTo(String[] to)
	{
		this.to.clear();
		for(int i = 0; i < to.length; i++)
			this.to.add(to[i]);
	}

	/**
	 *  Get an to of this OMessage.
	 *  @param idx The index.
	 *  @return to
	 */
	public String getTo(int idx)
	{
		return (String)this.to.get(idx);
	}

	/**
	 *  Set a to to this OMessage.
	 *  @param idx The index.
	 *  @param to a value to be added
	 */
	public void setTo(int idx, String to)
	{
		this.to.set(idx, to);
	}

	/**
	 *  Add a to to this OMessage.
	 *  @param to a value to be removed
	 */
	public void addTo(String to)
	{
		this.to.add(to);
	}

	/**
	 *  Remove a to from this OMessage.
	 *  @param to a value to be removed
	 *  @return  True when the to have changed.
	 */
	public boolean removeTo(String to)
	{
		return this.to.remove(to);
	}


	/**
	 *  Get the from of this OMessage.
	 * @return from
	 */
	public String getFrom()
	{
		return this.from;
	}

	/**
	 *  Set the from of this OMessage.
	 * @param from the value to be set
	 */
	public void setFrom(String from)
	{
		this.from = from;
	}

	/**
	 *  Get the incoming of this OMessage.
	 * @return incoming
	 */
	public boolean isIncoming()
	{
		return this.incoming;
	}

	/**
	 *  Set the incoming of this OMessage.
	 * @param incoming the value to be set
	 */
	public void setIncoming(boolean incoming)
	{
		this.incoming = incoming;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this OMessage.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "OMessage(" + "from=" + getFrom() + ", incoming=" + isIncoming() + ", name=" + getName() + ", seq=" + getSeq() + ", thread=" + getThread() + ", time=" + getTime() + ", value="
				+ getValue() + ")";
	}

}
