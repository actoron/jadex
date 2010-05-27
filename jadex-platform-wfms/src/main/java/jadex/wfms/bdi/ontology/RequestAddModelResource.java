package jadex.wfms.bdi.ontology;

import java.io.UnsupportedEncodingException;

import jadex.base.fipa.IComponentAction;
import jadex.commons.Base64;

public class RequestAddModelResource implements IComponentAction
{
	private String resourceName;
	private String encodedResource;
	
	public RequestAddModelResource()
	{
	}
	
	public byte[] decodeResource()
	{
		try
		{
			return Base64.decode(encodedResource.getBytes("US-ASCII"));
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void encodeResource(byte[] resource)
	{
		try
		{
			encodedResource = new String(Base64.encode(resource), "US-ASCII");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 *  Get the encodedResource.
	 *  @return The encodedResource.
	 */
	public String getEncodedResource()
	{
		return encodedResource;
	}

	/**
	 *  Set the encodedResource.
	 *  @param encodedResource The encodedResource to set.
	 */
	public void setEncodedResource(String encodedResource)
	{
		this.encodedResource = encodedResource;
	}

	/**
	 *  Get the resourceName.
	 *  @return The resourceName.
	 */
	public String getResourceName()
	{
		return resourceName;
	}

	/**
	 *  Set the resourceName.
	 *  @param resourceName The resourceName to set.
	 */
	public void setResourceName(String resourceName)
	{
		this.resourceName = resourceName;
	}
	
	
}
