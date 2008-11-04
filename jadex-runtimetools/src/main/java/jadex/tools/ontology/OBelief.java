package jadex.tools.ontology;


/**
 *  Java class for concept OBelief of jadex.tools.tracer ontology.
 */
public class OBelief extends OTrace
{
	//-------- constants ----------

	/** Predefined value "READ" for slot access. */
	public static String	ACCESS_READ		= "READ";

	/** Predefined value "WRITE" for slot access. */
	public static String	ACCESS_WRITE	= "WRITE";

	//-------- attributes ----------

	/** Attribute for slot access. */
	protected String		access;

	//-------- constructors --------

	/**
	 *  Default Constructor. 
	 *  Create a new OBelief.
	 */
	public OBelief()
	{
	}

	/**
	 *  Init Constructor.
	 *  Create a new OBelief.
	 *  Initializes the object with required attributes.
	 * @param name
	 * @param seq
	 * @param thread
	 * @param time
	 * @param value
	 */
	public OBelief(String name, String seq, String thread, String time, String value)
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
	 *  Get the access of this OBelief.
	 * @return access
	 */
	public String getAccess()
	{
		return this.access;
	}

	/**
	 *  Set the access of this OBelief.
	 * @param access the value to be set
	 */
	public void setAccess(String access)
	{
		this.access = access;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this OBelief.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "OBelief(" + "name=" + getName() + ", seq=" + getSeq() + ", thread=" + getThread() + ", time=" + getTime() + ", value=" + getValue() + ")";
	}

}
