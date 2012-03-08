package jadex.bridge;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IOutputConnection
{
	/**
	 * 
	 */
	public IFuture<Void> send(byte[] data);
	
	/**
	 * 
	 */
	public IFuture<Void> close();
}
