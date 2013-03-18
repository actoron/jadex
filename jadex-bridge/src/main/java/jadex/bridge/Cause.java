package jadex.bridge;

import java.util.UUID;

import jadex.commons.SUtil;

/**
 * 
 */
public class Cause
{
	/** The call id. Identical for all calls of the same origin. */
	protected String callid;

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
		this(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 
			UUID.randomUUID().toString() , sourcename, targetname);
	}
	
	/**
	 *  Create a new cause.
	 */
	public Cause(String sourceid, String targetid, String sourcename, String targetname)
	{
		this(UUID.randomUUID().toString(), sourceid, targetid, sourcename, targetname);
	}
	
	/**
	 *  Create a new cause.
	 */
	public Cause(String callid, String sourceid, String targetid, String sourcename, String targetname)
	{
		this.callid = callid;
		this.sourceid = sourceid;
		this.targetid = targetid;
		this.sourcename = sourcename;
		this.targetname = targetname;
	}
	
	/**
	 *  Create a new cause.
	 */
	public Cause(Cause old, String targetname)
	{
		this.callid = old!=null? old.getCallId(): UUID.randomUUID().toString();
		this.sourceid = old!=null? old.getTargetId(): null;
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
	 *  Get the callid.
	 *  @return The callid.
	 */
	public String getCallId()
	{
		return callid;
	}

	/**
	 *  Set the callid.
	 *  @param callid The callid to set.
	 */
	public void setCallId(String callid)
	{
		this.callid = callid;
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
		return "Cause(callid=" + callid + ", sourceid=" + sourceid
			+ ", targetid=" + targetid + ", sourcename=" + sourcename
			+ ", targetname=" + targetname + ")";
	}
	
}
