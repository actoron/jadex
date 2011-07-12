package jadex.bdi.runtime;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;

/**
 *  The interface for external threads.
 */
@Reference
public interface IBDIExternalAccess extends IExternalAccess
{
	/**
	 *  Get subcapability names.
	 *  @return The future with array of subcapability names.
	 */
	public IFuture getSubcapabilityNames();
	
	/**
	 *  Get external access of subcapability.
	 *  @param name The capability name.
	 *  @return The future with external access.
	 */
	public IFuture getExternalAccess(final String name);
}
