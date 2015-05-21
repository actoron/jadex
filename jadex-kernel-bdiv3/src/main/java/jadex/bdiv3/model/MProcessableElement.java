package jadex.bdiv3.model;

import java.util.HashMap;
import java.util.Map;

/**
 *  Base class for all elements that can be processed with means-end reasoning.
 */
public class MProcessableElement extends MParameterElement
{
	public static Map<String, ExcludeMode> modes = new HashMap<String, ExcludeMode>();
	
	/** The message direction. */
	public enum ExcludeMode
	{
		Never("never"),
		WhenTried("when_tried"),
		WhenFailed("when_failed"),
		WhenSucceeded("when_succeeded");
		
		protected String str;
		
		/**
		 *  Create a new direction
		 */
		ExcludeMode(String str)
		{
			this.str = str;
			modes.put(str, this);
		} 
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String getString()
		{
			return str;
		}
		
		/**
		 * 
		 */
		public static ExcludeMode getDirection(String name)
		{
			return modes.get(name);
		}
	}
	
//	/** Never exclude plan candidates from apl. */
//	public static final String EXCLUDE_NEVER = "never";
//
//	/** Exclude tried plan candidates from apl. */ 
//	public static final String EXCLUDE_WHEN_TRIED = "when_tried";
//	
//	/** Exclude failed plan candidates from apl. */
//	public static final String EXCLUDE_WHEN_FAILED = "when_failed";
//
//	/** Exclude succeeded plan candidates from apl. */
//	public static final String EXCLUDE_WHEN_SUCCEEDED = "when_succeeded";

	
	/** Post to all flag. */
	protected boolean posttoall;
	
	/** Random selection flag. */
	protected boolean randomselection;
	
	/** The rebuild mode. */
	protected boolean rebuild;

	/** The exclude mode. */
	protected ExcludeMode excludemode;
	
	/**
	 *	Bean Constructor. 
	 */
	public MProcessableElement()
	{
		// used by xml reader
		this.excludemode = excludemode==null? ExcludeMode.WhenTried: excludemode;
	}
	
	/**
	 * 
	 */
	public MProcessableElement(String name, boolean posttoall, boolean randomselection, ExcludeMode excludemode)
	{
		super(name);
		this.posttoall = posttoall;
		this.randomselection = randomselection;
		this.excludemode = excludemode==null? ExcludeMode.WhenTried: excludemode;
	}
	
	/**
	 *  Test if is posttoall.
	 *  @return True, if posttoaall.
	 */
	public boolean isPostToAll()
	{
		return posttoall;
	}
	
	/**
	 *  Test if is random selection.
	 *  @return True, if is random selection.
	 */
	public boolean isRandomSelection()
	{
		return randomselection;
	}

	/**
	 *  Set the posttoall.
	 *  @param posttoall The posttoall to set.
	 */
	public void setPostToAll(boolean posttoall)
	{
		this.posttoall = posttoall;
	}

	/**
	 *  Set the randomselection.
	 *  @param randomselection The randomselection to set.
	 */
	public void setRandomSelection(boolean randomselection)
	{
		this.randomselection = randomselection;
	}
	
	/**
	 *  Test if rebuild APL.
	 *  @return True, if rebuild.
	 */
	public boolean isRebuild()
	{
		return rebuild;
	}
	
	/**
	 *  Set the rebuild.
	 *  @param rebuild The rebuild to set.
	 */
	public void setRebuild(boolean rebuild)
	{
		this.rebuild = rebuild;
	}

	/**
	 *  Get the excludemode.
	 *  @return The excludemode.
	 */
	public ExcludeMode getExcludeMode()
	{
		return excludemode;
	}

	/**
	 *  Set the excludemode.
	 *  @param excludemode The excludemode to set.
	 */
	public void setExcludeMode(ExcludeMode excludemode)
	{
		this.excludemode = excludemode;
	}
	
}
