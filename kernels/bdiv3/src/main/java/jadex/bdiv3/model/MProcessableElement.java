package jadex.bdiv3.model;

import java.util.HashMap;
import java.util.Map;

/**
 *  Base class for all elements that can be processed with means-end reasoning.
 */
public class MProcessableElement extends MParameterElement
{
	public static Map<String, ExcludeMode> modes = new HashMap<String, ExcludeMode>();
	
	/** The exclude mode determines when and if a plan is removed from the applicable plans list (APL). */
	public enum ExcludeMode
	{
		/** The plan is never removed. */
		Never("never"),

		/** The plan is removed after it has been executed once, regardless of success or failure or abortion. */
		WhenTried("when_tried"),
		
		/** The plan is removed after it has been executed once, but only when it exited with an exception. */
		WhenFailed("when_failed"),
		
		/** The plan is removed after it has been executed once, but only when it exited without an exception. */
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
		 *  Convert from string to enum.
		 */
		public static ExcludeMode getExcludeMode(String name)
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

	// default values for xml reader
	
	/** Post to all flag. */
	protected boolean posttoall = false;
	
	/** Random selection flag. */
	protected boolean randomselection = false;
	
	/** The rebuild mode. */
	protected boolean rebuild = false;

	/** The exclude mode. */
	protected ExcludeMode excludemode = ExcludeMode.WhenTried;
	
	// additional xml attributes
	
	/** The exported flag. */
	protected boolean exported;
	
	/**
	 *	Bean Constructor. 
	 */
	public MProcessableElement()
	{
	}
	
	/**
	 *  Create a new element.
	 */
	// todo: add rebuild?
	public MProcessableElement(String name, boolean posttoall, boolean rebuild, boolean randomselection, ExcludeMode excludemode)
	{
		super(name);
		this.posttoall = posttoall;
		this.rebuild = rebuild;
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
	
	
	/**
	 *  Get the exported flag.
	 *  @return The exported flag.
	 */
	public boolean isExported()
	{
		return exported;
	}

	/**
	 *  Set the exported flag.
	 *  @param exported The exported to set.
	 */
	public void setExported(boolean exported)
	{
		this.exported = exported;
	}
}
