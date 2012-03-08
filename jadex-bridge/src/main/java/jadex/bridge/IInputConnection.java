package jadex.bridge;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IInputConnection
{
	/**
	 * 
	 */
	public IFuture<Void> read(byte[] buffer);
	
	/**
	 * 
	 */
	public IFuture<Void> close();
}
