package jadex.commons.collection;

import java.lang.ref.ReferenceQueue;

/**
 *  A weak entry is a reference with an additional argument
 *  that can be inspected the referent is garbage collected.
 *  E.g. the action reference queue interprets the argument
 *  as a runnable to be executed.
 *  @see ActionReferenceQueue
 */
public class WeakEntry extends WeakObject
{
	//-------- attributes --------
	
	/** Optional command argument. */
	protected Object	arg;
	
	//-------- constructors --------

	/**
	 *  Construct a new reference.
	 *  @param object Object to reference.
	 *  @param arg The argument.
	 */
	public WeakEntry(Object object, Object arg)
	{
		super(object);
		this.arg = arg;
	}


	/**
	 *  Construct a <tt>WeakObject</tt>.
	 *  @param object Object to reference.
	 *  @param arg The argument.
	 *  @param queue Reference queue.
	 */
	public WeakEntry(Object object, Object arg, ReferenceQueue queue)
	{
		super(object, queue);
		this.arg = arg;
	}

	//-------- methods --------

	/**
	 *  Get the argument.
	 *  @return The argument.
	 */
	public Object getArgument()
	{
		return arg;
	}
}
