package jadex.bridge;

/**
 *  An error thrown to abort the thread execution of a blocked component step.
 */
public class StepAborted extends ThreadDeath 
{
	IComponentIdentifier	cid;
	
	public StepAborted(IComponentIdentifier cid)
	{
		this.cid	= cid;
	}
	
	@Override
	public String toString()
	{
		return super.toString()+"("+cid+")";
	}
}
