package jadex.platform.service.ecarules;

/**
 * 
 */
public class FinishedEvent extends  ARulebaseEvent
{
	/**
	 *  Create a new rule event.
	 */
	public FinishedEvent()
	{
	}
	
	/**
	 *  Create a new rule event.
	 */
	public FinishedEvent(int callid, int id)
	{
		super(callid);
		this.id = id;
	}
}
