package jadex.bridge;

import java.util.UUID;

/**
 * 
 */
public class Cause
{
	/** The id. Identical for all calls of the same origin. */
	protected String chainid;

	/** The source id. */
	protected String sourceid;
	
	/** The target id. */
	protected String targetid;
	
	/** The source name. */
	protected String sourcename;
	
	/** The target name. */
	protected String targetname;

	/**
	 *  Create a new cause.
	 */
	public Cause()
	{
		// bean constructor
	}
	
	/**
	 *  Create a new cause.
	 */
	public Cause(String sourcename, String targetname)
	{
		this(null, null, null, sourcename, targetname);
	}
	
	/**
	 *  Create a new cause.
	 */
	public Cause(String sourceid, String targetid, String sourcename, String targetname)
	{
		this(null, sourceid, targetid, sourcename, targetname);
	}
	
	/**
	 *  Create a new cause.
	 */
	public Cause(String chainid, String sourceid, String targetid, String sourcename, String targetname)
	{
		this.sourceid = sourceid==null? UUID.randomUUID().toString(): sourceid;
		this.targetid = targetid==null? UUID.randomUUID().toString(): targetid;
		
		// If chainid is null it will be set to sourceid
		// This allows to check if an event is top-level
		this.chainid = chainid==null? this.sourceid: chainid;
		
		this.sourcename = sourcename;
		this.targetname = targetname;
	}
	
	/**
	 *  Create a new cause.
	 */
	public Cause(Cause old, String targetname)
	{
		this.chainid = old!=null? old.getChainId(): UUID.randomUUID().toString();
		this.sourceid = old!=null? old.getTargetId(): UUID.randomUUID().toString();
		this.sourcename = old!=null? old.getTargetName(): null;
		this.targetid = UUID.randomUUID().toString();
		this.targetname = targetname;
	}
	
//	/**
//	 *  Create a new cause.
//	 */
//	public Cause(Cause old, String targedid, String targetname)
//	{
//		this.callid = old!=null? old.getCallId(): SUtil.createUniqueId("callid");
//		this.sourceid = old!=null? old.getTargetId(): null;
//		this.sourcename = old!=null? old.getTargetName(): null;
//		this.targetid = targedid;
//		this.targetname = targetname;
//	}
	
	/**
	 *  Get the chain id.
	 *  @return The chain id.
	 */
	public String getChainId()
	{
		return chainid;
	}

	/**
	 *  Set the chain id.
	 *  @param chainid The chainid to set.
	 */
	public void setChainId(String callid)
	{
		this.chainid = callid;
	}

	/**
	 *  Get the sourceId.
	 *  @return The sourceId.
	 */
	public String getSourceId()
	{
		return sourceid;
	}

	/**
	 *  Set the sourceId.
	 *  @param sourceid The sourceId to set.
	 */
	public void setSourceId(String sourceid)
	{
		this.sourceid = sourceid;
	}

	/**
	 *  Get the targetId.
	 *  @return The targetId.
	 */
	public String getTargetId()
	{
		return targetid;
	}

	/**
	 *  Set the targetId.
	 *  @param targetid The targetId to set.
	 */
	public void setTargetId(String targetid)
	{
		this.targetid = targetid;
	}

	/**
	 *  Get the sourceName.
	 *  @return The sourceName.
	 */
	public String getSourceName()
	{
		return sourcename;
	}

	/**
	 *  Set the sourceName.
	 *  @param sourcename The sourceName to set.
	 */
	public void setSourceName(String sourcename)
	{
		this.sourcename = sourcename;
	}

	/**
	 *  Get the targetName.
	 *  @return The targetName.
	 */
	public String getTargetName()
	{
		return targetname;
	}

	/**
	 *  Set the targetName.
	 *  @param targetname The targetName to set.
	 */
	public void setTargetName(String targetname)
	{
		this.targetname = targetname;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "Cause(chainid=" + chainid + ", sourceid=" + sourceid
			+ ", targetid=" + targetid + ", sourcename=" + sourcename
			+ ", targetname=" + targetname + ")";
	}
	
}
