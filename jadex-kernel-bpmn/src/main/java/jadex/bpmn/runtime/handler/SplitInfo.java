package jadex.bpmn.runtime.handler;

/**
 *  Keep information about a thread spawned at a split gateway used later when processing
 *  the corresponding join.
 */
public class SplitInfo
{
	//-------- static part --------
	
	/** The id counter. */
	protected static int	IDCNT;
	
	//-------- attributes --------
	
	/** The split id. */
	protected int	id;
	
	/** The split count (i.e. number of outgoing threads). */
	protected int	count;
	
	//-------- constructors --------
	
	/**
	 *  Create a new split info.
	 */
	public SplitInfo(int count)
	{
		// Not thread safe, but doesn't matter as ids need only be unique inside component.
		this.id	= IDCNT++;
		this.count	= count;
	}
	
	//-------- methods --------

	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof SplitInfo && id==((SplitInfo)obj).id;
	}

	/**
	 *  Get the hash code.  
	 */
	public int hashCode()
	{
		return 31 + id;
	}
	
	/**
	 *  Get the split count.
	 */
	public int getSplitCount()
	{
		return count;
	}
}
