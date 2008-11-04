package jadex.tools.ontology;


/**
 *  Java class for concept OPlan of jadex.tools.tracer ontology.
 */
public class OPlan extends OTrace
{
	//-------- constants ----------

	/** Predefined value "STARTED" for slot plan_state. */
	public static String	PLANSTATE_STARTED		= "STARTED";

	/** Predefined value "TERMINATED" for slot plan_state. */
	public static String	PLANSTATE_TERMINATED	= "TERMINATED";

	//-------- attributes ----------

	/** Attribute for slot plan_state. */
	protected String		planstate;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new OPlan.
	 */
	public OPlan()
	{
	}

	/**
	 *  Init Constructor. <br>
	 *  Create a new OPlan.<br>
	 *  Initializes the object with required attributes.
	 * @param name
	 * @param planstate
	 * @param seq
	 * @param thread
	 * @param time
	 * @param value
	 */
	public OPlan(String name, String planstate, String seq, String thread, String time, String value)
	{
		this();
		setName(name);
		setPlanState(planstate);
		setSeq(seq);
		setThread(thread);
		setTime(time);
		setValue(value);
	}

	//-------- accessor methods --------

	/**
	 *  Get the plan_state of this OPlan.
	 * @return plan_state
	 */
	public String getPlanState()
	{
		return this.planstate;
	}

	/**
	 *  Set the plan_state of this OPlan.
	 * @param planstate the value to be set
	 */
	public void setPlanState(String planstate)
	{
		this.planstate = planstate;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this OPlan.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "OPlan(" + "name=" + getName() + ", planstate=" + getPlanState() + ", seq=" + getSeq() + ", thread=" + getThread() + ", time=" + getTime() + ", value=" + getValue() + ")";
	}

}
