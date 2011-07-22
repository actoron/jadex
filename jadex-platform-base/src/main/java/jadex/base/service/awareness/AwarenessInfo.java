package jadex.base.service.awareness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;

/**
 *  Simple info object that is sent between awareness agents.
 */
public class AwarenessInfo
{
	//-------- constants --------
	
	/** State indicating that a component is currently online. */
	public static final String	STATE_ONLINE	= "online";
	
	/** State indicating that a component is going offline. */
	public static final String	STATE_OFFLINE	= "offline";
	
	//-------- attributes --------
	
	/** The sending component's identifier. */
	protected IComponentIdentifier	sender;

	/** The flag if should not be answered. */
	protected boolean ignore;

	/** The component state. */
	protected String state;
	
	/** The current send time delay (interval). */
	protected long	delay;
	
	/** The includes list. */
	protected String[] includes;
	
	/** The excludes list. */
	protected String[] excludes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new awareness info.
	 */
	public AwarenessInfo()
	{
	}
	
	/**
	 *  Create a new awareness info.
	 */
	public AwarenessInfo(IComponentIdentifier sender, String state, long delay)
	{
		this(sender, state, delay, null, null, false);
	}
	
	/**
	 *  Create a new awareness info.
	 */
	public AwarenessInfo(IComponentIdentifier sender, String state, long delay, String[] includes, String[] excludes, boolean ignore)
	{
		this.sender = sender;
		this.state = state;
		this.delay = delay;
		this.includes	= includes;
		this.excludes	= excludes;
		this.ignore = ignore;
	}
	
	//-------- methods --------

	/**
	 *  Get the sender.
	 *  @return the sender.
	 */
	public IComponentIdentifier getSender()
	{
		return sender;
	}

	/**
	 *  Set the sender.
	 *  @param sender The sender to set.
	 */
	public void setSender(IComponentIdentifier sender)
	{
		this.sender = sender;
	}
	
	/**
	 *  Get the state.
	 *  @return the state.
	 */
	public String	getState()
	{
		return state;
	}

	/**
	 *  Set the state.
	 *  @param state The state to set.
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	/**
	 *  Get the delay.
	 *  @return the delay.
	 */
	public long getDelay()
	{
		return delay;
	}

	/**
	 *  Set the delay.
	 *  @param delay The delay to set.
	 */
	public void setDelay(long delay)
	{
		this.delay = delay;
	}
	
	/**
	 *  Get the includes.
	 *  @return the includes.
	 */
	public String[] getIncludes()
	{
		return includes!=null? includes: SUtil.EMPTY_STRING_ARRAY;
	}

	/**
	 *  Set the includes.
	 *  @param includes The includes to set.
	 */
	public void setIncludes(String[] includes)
	{
		this.includes	= includes;
	}

	/**
	 *  Set the excludes.
	 *  @param excludes The excludes to set.
	 */
	public void setExcludes(String[] excludes)
	{
		this.excludes	= excludes;
	}
	
	/**
	 *  Get the excludes.
	 *  @return the excludes.
	 */
	public String[] getExcludes()
	{
		return excludes!=null? excludes: SUtil.EMPTY_STRING_ARRAY;
	}
	
	/**
	 *  Get the ignore.
	 *  @return the ignore.
	 */
	public boolean isIgnore()
	{
		return ignore;
	}

	/**
	 *  Set the ignore.
	 *  @param ignore The ignore to set.
	 */
	public void setIgnore(boolean ignore)
	{
		this.ignore = ignore;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "AwarenessInfo(sender="+sender+", state="+state+", delay="+delay+")";
	}
}
