package jadex.wfms;

import java.util.logging.Logger;

import jadex.service.IServiceContainer;

/**
 * The Workflow Management System interface.
 */
public interface IWfms extends IServiceContainer
{
	public Logger getLogger();
}
