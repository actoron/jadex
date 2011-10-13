package jadex.bridge;

import jadex.commons.Tuple2;

import java.net.URL;

/**
 *  Interface for resource identification.
 *  Contains a local identifier and a global identifier
 *  that can be used to find the resource.
 */
public interface IResourceIdentifier
{
	/**
	 *  Get the local identifier.
	 *  The local identifier consists of the platform 
	 *  component identifier and the URL of the resource. 
	 *  @return The local identifier. 
	 */
	public Tuple2<IComponentIdentifier, URL> getLocalIdentifier();
	
	/**
	 *  Get the global identifier.
	 *  @return The global identifier.
	 */
	public String getGlobalIdentifier();
}
