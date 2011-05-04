package haw.mmlab.production_line.service;

import haw.mmlab.production_line.common.ConsoleMessage;
import haw.mmlab.production_line.manager.ManagerService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.IResultListener;

import java.util.logging.Logger;

/**
 * Helper class for handling service calls.
 * 
 * @author thomas
 */
public class ServiceHelper {

	/**
	 * Sends the given {@link ConsoleMessage} to the {@link ManagerService} by calling {@link ManagerService#handleConsoleMsg(ConsoleMessage)}.
	 * 
	 * @param sp
	 * @param msg
	 *            the given {@link ConsoleMessage}
	 * @param logger
	 */
	public static void handleConsoleMsg(final IServiceProvider sp, final ConsoleMessage msg, final Logger logger) {
		logger.info(msg.getOutMsg());

		SServiceProvider.getService(sp, IManagerService.class).addResultListener(new IResultListener() {

			public void resultAvailable(Object result) {
				IManagerService service = (IManagerService) result;
				service.handleConsoleMsg(msg);
			}

			public void exceptionOccurred(Exception exception) {
				logger.severe(exception.getMessage());
			}
		});
	}
}