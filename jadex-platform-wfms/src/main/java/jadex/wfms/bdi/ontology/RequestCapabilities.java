package jadex.wfms.bdi.ontology;

import java.util.Set;

import jadex.base.fipa.IComponentAction;

/**
 * A request for client capabilities.
 *
 */
public class RequestCapabilities implements IComponentAction
{
	/** Capabilities of the user of the client */
	private Set capabilities;
	
	/**
	 * Returns the capabilities.
	 * @return the capabilities
	 */
	public Set getCapabilities()
	{
		return capabilities;
	}
	
	/**
	 * Sets the capabilities
	 * @param capabilities the capabilities
	 */
	public void setCapabilities(Set capabilities)
	{
		this.capabilities = capabilities;
	}
}
