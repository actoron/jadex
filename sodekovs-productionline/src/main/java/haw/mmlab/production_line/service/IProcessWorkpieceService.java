package haw.mmlab.production_line.service;

import haw.mmlab.production_line.configuration.Workpiece;
import haw.mmlab.production_line.dropout.config.Action;
import jadex.commons.future.IFuture;

/**
 * The process workpiece interface for processing workpieces.
 * 
 * @author thomas
 */
public interface IProcessWorkpieceService {

	/**
	 * Get the services id.
	 * 
	 * @return the services id
	 */
	public String getId();

	/**
	 * Get the type of the agent offering this service.
	 * 
	 * @return the type of the agent offering this service
	 */
	public String getType();

	/**
	 * Processes of the given workpiece.
	 * 
	 * @param workpiece
	 *            the given workpiece
	 * @param senderId
	 *            the agent id of the sender
	 * @return
	 */
	public IFuture<Boolean> process(Workpiece workpiece, String senderId);

	/**
	 * Executes a Dropout-{@link Action}.
	 * 
	 * @param action
	 *            the {@link Action} to be executed
	 */
	public void executeDropoutAction(Action action);
}