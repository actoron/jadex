package jadex.wfms.bdi.ontology;

import jadex.base.fipa.IComponentAction;

import java.net.URL;

/**
 * Request to remove a process model from the repository.
 *
 */
public class RequestRemoveModelResource implements IComponentAction
{
	/** The resource URL */
	private URL url;
	
	/**
	 * Creates a new RequestRemoveProcess.
	 */
	public RequestRemoveModelResource()
	{
	}

	/**
	 *  Get the url.
	 *  @return The url.
	 */
	public URL getUrl()
	{
		return url;
	}

	/**
	 *  Set the url.
	 *  @param url The url to set.
	 */
	public void setUrl(URL url)
	{
		this.url = url;
	}
}
