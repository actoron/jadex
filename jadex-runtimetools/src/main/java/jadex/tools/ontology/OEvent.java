package jadex.tools.ontology;


/**
 *  Java class for concept OEvent of jadex.tools.tracer ontology.
 */
public class OEvent extends OTrace
{
	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new OEvent.
	 */
	public OEvent()
	{
	}

	/**
	 *  Init Constructor. <br>
	 *  Create a new OEvent.<br>
	 *  Initializes the object with required attributes.
	 * @param name
	 * @param seq
	 * @param thread
	 * @param time
	 * @param value
	 */
	public OEvent(String name, String seq, String thread, String time, String value)
	{
		this();
		setName(name);
		setSeq(seq);
		setThread(thread);
		setTime(time);
		setValue(value);
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this OEvent.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "OEvent(" + "name=" + getName() + ", seq=" + getSeq() + ", thread=" + getThread() + ", time=" + getTime() + ", value=" + getValue() + ")";
	}

}
