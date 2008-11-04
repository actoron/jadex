package jadex.tools.ontology;


/**
 *  Java class for concept OAction of jadex.tools.tracer ontology.
 */
public class OAction extends OTrace
{
	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new OAction.
	 */
	public OAction()
	{
	}

	/**
	 *  Init Constructor.
	 *  Create a new OAction.
	 *  Initializes the object with required attributes.
	 * @param name
	 * @param seq
	 * @param thread
	 * @param time
	 * @param value
	 */
	public OAction(String name, String seq, String thread, String time, String value)
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
	 *  Get a string representation of this OAction.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "OAction(" + "name=" + getName() + ", seq=" + getSeq() + ", thread=" + getThread() + ", time=" + getTime() + ", value=" + getValue() + ")";
	}

}
