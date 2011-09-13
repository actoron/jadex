package haw.mmlab.production_line.service;

import haw.mmlab.production_line.common.ConsoleMessage;

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
	public void informWPProduced(String taskId);

	/**
	 * Inform the manager that a workpiece was consumed in the given task.
	 * 
	 * @param taskId
	 *            the given taskId
	 */
	public void informWPConsumed(String taskId);

	/**
	 * Inform the manager that the given task has finished his execution.
	 * 
	 * @param taskId
	 *            the given taskId
	 */
	public void informFinished(String taskId);

	/**
	 * Handles a {@link ConsoleMessage} to by printed by the manager
	 * 
	 * @param message
	 *            the given {@link ConsoleMessage}
	 */
	public void handleConsoleMsg(ConsoleMessage message);
}