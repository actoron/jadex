package jadex.tools.ontology;


/**
 *  Java class for concept OGoal of jadex.tools.tracer ontology.
 */
public class OGoal extends OTrace
{
	//-------- constants ----------

	/** Predefined value "ADOPTED" for slot goal_state. */
	public static String	GOALSTATE_ADOPTED	= "ADOPTED";

	/** Predefined value "REMOVED" for slot goal_state. */
	public static String	GOALSTATE_REMOVED	= "REMOVED";

	/** Predefined value "achieve" for slot goal-kind. */
	public static String	GOALKIND_ACHIEVE	= "achieve";

	/** Predefined value "maintain" for slot goal-kind. */
	public static String	GOALKIND_MAINTAIN	= "maintain";

	/** Predefined value "perform" for slot goal-kind. */
	public static String	GOALKIND_PERFORM	= "perform";

	/** Predefined value "query" for slot goal-kind. */
	public static String	GOALKIND_QUERY		= "query";

	/** Predefined value "meta" for slot goal-kind. */
	public static String	GOALKIND_META		= "meta";

	//-------- attributes ----------

	/** Attribute for slot goal_state. */
	protected String		goalstate;

	/** Attribute for slot goal-kind. */
	protected String		goalkind;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new OGoal.
	 */
	public OGoal()
	{
	}

	/**
	 *  Init Constructor.
	 *  Create a new OGoal.
	 *  Initializes the object with required attributes.
	 * @param goalstate
	 * @param name
	 * @param seq
	 * @param thread
	 * @param time
	 * @param value
	 */
	public OGoal(String goalstate, String name, String seq, String thread, String time, String value)
	{
		this();
		setGoalState(goalstate);
		setName(name);
		setSeq(seq);
		setThread(thread);
		setTime(time);
		setValue(value);
	}

	//-------- accessor methods --------

	/**
	 *  Get the goal_state of this OGoal.
	 * @return goal_state
	 */
	public String getGoalState()
	{
		return this.goalstate;
	}

	/**
	 *  Set the goal_state of this OGoal.
	 * @param goalstate the value to be set
	 */
	public void setGoalState(String goalstate)
	{
		this.goalstate = goalstate;
	}

	/**
	 *  Get the goal-kind of this OGoal.
	 * @return goal-kind
	 */
	public String getGoalKind()
	{
		return this.goalkind;
	}

	/**
	 *  Set the goal-kind of this OGoal.
	 * @param goalkind the value to be set
	 */
	public void setGoalKind(String goalkind)
	{
		this.goalkind = goalkind;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this OGoal.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "OGoal(" + "goalstate=" + getGoalState() + ", name=" + getName() + ", seq=" + getSeq() + ", thread=" + getThread() + ", time=" + getTime() + ", value=" + getValue() + ")";
	}

}
