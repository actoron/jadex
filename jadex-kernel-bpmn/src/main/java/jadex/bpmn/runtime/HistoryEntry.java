package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;

/**
 *  History entry for saving the process execution history. 
 */
public class HistoryEntry
{
	//-------- attributes --------
	
	/** The step number. */
	protected int stepnumber;
	
	/** The thread that executed this activity. */
	protected String threadid;
	
	/** The model element of the activity. */
	protected MActivity activity;

	//-------- constructors --------
	
	/**
	 *  Create a new entry.
	 */
	public HistoryEntry(int stepnumber, String threadid, MActivity activity)
	{
		this.stepnumber = stepnumber;
		this.threadid = threadid;
		this.activity = activity;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the stepnumber.
	 *  @return The stepnumber.
	 */
	public int getStepNumber()
	{
		return this.stepnumber;
	}
	
	/**
	 *  Get the activity.
	 *  @return The activity.
	 */
	public MActivity getActivity()
	{
		return this.activity;
	}

	/**
	 *  Get the thread id.
	 *  @return The thread id.
	 */
	public String getThreadId()
	{
		return this.threadid;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "HistoryEntryactivity(" + this.activity + ", stepnumber="
				+ this.stepnumber + ", threadid=" + this.threadid + ")";
	}

	
}
