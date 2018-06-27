package jadex.bridge;

/**
 *  Can be used as external action, which will only be executed when
 *  isValid() is true. Further, it is guaranteed that cleanup() will
 *  be executed. 
 *  
 *  Contract is:
 *  if(isValid())
 *  	run();
 *  cleanup();
 */
public abstract class CheckedAction implements Runnable
{
	//-------- attributes --------
	
	/** The valid flag. */
	protected boolean valid;
	
	//-------- constructors --------
	
	/**
	 *  Create a new action.
	 */
	public CheckedAction()
	{
		valid = true;
	}
	
	//-------- methods --------
	
	/**
	 *  Cleanup will be executed at the end of the action.
	 *  Override if cleanup actions are necessary.
	 */
	public void cleanup()
	{
	}
	
	/**
	 *  Test if the timed object is valid.
	 *  @return True, if entry is valid.
	 */
	public boolean isValid()
	{
		return valid;
	}
	
	/**
	 *  Set the valid state.
	 *  @valid The valid state.
	 */
	public void setValid(boolean valid)
	{
		this.valid = valid;
	}
}
