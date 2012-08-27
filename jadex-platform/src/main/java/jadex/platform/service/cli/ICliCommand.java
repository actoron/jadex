package jadex.platform.service.cli;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface ICliCommand
{
	/**
	 *  Get the argument info.
	 */
	public ArgumentInfo[] getArgumentInfos();

	/**
	 *  Get the result info.
	 */
	public ResultInfo getResultInfo();
	
//	/**
//	 *  Invoke the command.
//	 */
//	public Object invokeCommand(Object context, Object[] args);
	
	/**
	 *  Invoke the command.
	 */
	public IFuture<String> invokeCommand(Object context, String[] args);

}
