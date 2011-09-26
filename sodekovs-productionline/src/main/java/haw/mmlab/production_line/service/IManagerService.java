package haw.mmlab.production_line.service;

import haw.mmlab.production_line.common.ConsoleMessage;
import jadex.commons.future.IFuture;

/**
 * The manager service interface.
 * 
 * @author thomas
 */
public interface IManagerService {

	/**
	 * Inform the manager that a workpiece was produced in the given task.
	 * 
	 * @param taskId
	 *            the given taskId
	 */
	public IFuture<Void> informWPProduced(String taskId);

	/**
	 * Inform the manager that a workpiece was consumed in the given task.
	 * 
	 * @param taskId
	 *            the given taskId
	 */
	public IFuture<Void> informWPConsumed(String taskId);

	/**
	 * Inform the manager that the given task has finished his execution.
	 * 
	 * @param taskId
	 *            the given taskId
	 */
	public IFuture<Void> informFinished(String taskId);

	/**
	 * Handles a {@link ConsoleMessage} to by printed by the manager
	 * 
	 * @param message
	 *            the given {@link ConsoleMessage}
	 */
	public IFuture<Void> handleConsoleMsg(ConsoleMessage message);

	/**
	 * Inform the manager that the reconfiguration failed.
	 */
	public IFuture<Void> informReconfError();
}