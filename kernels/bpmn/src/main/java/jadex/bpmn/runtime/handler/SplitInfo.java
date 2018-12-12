package jadex.bpmn.runtime.handler;

import java.util.Set;

/**
 *  Keep information about a thread spawned at a split gateway used later when processing
 *  the corresponding join.
 */
public class SplitInfo
{
	//-------- attributes --------
	
	/** The split id (the same for all threads spawned at the same split). */
	protected String	splitid;
	
	/** The path id (unique for each spawned thread). */
	protected String	pathid;
	
	/** All path ids belonging to the split id. */
	protected Set<String>	pathids;
	
	//-------- constructors --------
	
	public SplitInfo()
	{
	}
	
	/**
	 *  Create a new split info.
	 */
	public SplitInfo(String splitid, String pathid, Set<String> pathids)
	{
		this.splitid	= splitid;
		this.pathid	= pathid;
		this.pathids	= pathids;
	}
	
	//-------- methods --------

	/**
	 *  Get the split id.
	 */
	public String	getSplitId()
	{
		return splitid;
	}
	
	/**
	 *  Get the path id.
	 */
	public String	getPathId()
	{
		return pathid;
	}
	
	/**
	 *  Get the path ids of the split.
	 */
	public Set<String>	getPathIds()
	{
		return pathids;
	}

	/**
	 *  Gets the splitid.
	 *
	 *  @return The splitid.
	 */
	public String getSplitid()
	{
		return splitid;
	}

	/**
	 *  Sets the splitid.
	 *
	 *  @param splitid The splitid to set.
	 */
	public void setSplitid(String splitid)
	{
		this.splitid = splitid;
	}

	/**
	 *  Gets the pathid.
	 *
	 *  @return The pathid.
	 */
	public String getPathid()
	{
		return pathid;
	}

	/**
	 *  Sets the pathid.
	 *
	 *  @param pathid The pathid to set.
	 */
	public void setPathid(String pathid)
	{
		this.pathid = pathid;
	}

	/**
	 *  Gets the pathids.
	 *
	 *  @return The pathids.
	 */
	public Set<String> getPathids()
	{
		return pathids;
	}

	/**
	 *  Sets the pathids.
	 *
	 *  @param pathids The pathids to set.
	 */
	public void setPathids(Set<String> pathids)
	{
		this.pathids = pathids;
	}
}