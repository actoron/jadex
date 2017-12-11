package jadex.bytecode.fastinvocation;

/**
 *  Interface used to byte-engineer an accessor handler.
 *
 */
public interface IMethodInvoker
{
	/**
	 *  Invokes a method on an object.
	 *  
	 *  @param object The object
	 *  @param methodid The ID of the method.
	 *  @param args The method arguments.
	 *  @return The result, null if void.
	 */
	public Object invoke(Object object, Object... args);
}
