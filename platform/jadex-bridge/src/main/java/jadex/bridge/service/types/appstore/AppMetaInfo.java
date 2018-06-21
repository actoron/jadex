package jadex.bridge.service.types.appstore;

import jadex.bridge.IResourceIdentifier;

/**
 * 
 */
public class AppMetaInfo
{
	/** The application name. */
	protected String name;
	
	/** The application provider. */
	protected String provider;
	
	/** The application description. */
	protected String description;
	
	/** The application version. */
	protected String version;
	
	/** The image. */
	protected byte[] image;
	
	/** The download resource identifier. */
	protected IResourceIdentifier rid;

	/**
	 *  Create a new app meta info.
	 */
	public AppMetaInfo()
	{
	}
	
	/**
	 *  Create a new app meta info.
	 *  @param name
	 *  @param provider
	 *  @param description
	 *  @param version
	 *  @param rid
	 */
	public AppMetaInfo(String name, String provider, String description,
		String version, byte[] image, IResourceIdentifier rid)
	{
		this.name = name;
		this.provider = provider;
		this.description = description;
		this.version = version;
		this.image = image;
		this.rid = rid;
	}

	/**
	 *  Get the name.
	 *  @return the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the provider.
	 *  @return the provider.
	 */
	public String getProvider()
	{
		return provider;
	}

	/**
	 *  Set the provider.
	 *  @param provider The provider to set.
	 */
	public void setProvider(String provider)
	{
		this.provider = provider;
	}

	/**
	 *  Get the description.
	 *  @return the description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 *  Set the description.
	 *  @param description The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 *  Get the version.
	 *  @return the version.
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 *  Set the version.
	 *  @param version The version to set.
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}

	/**
	 *  Get the image.
	 *  @return the image.
	 */
	public byte[] getImage()
	{
		return image;
	}

	/**
	 *  Set the image.
	 *  @param image The image to set.
	 */
	public void setImage(byte[] image)
	{
		this.image = image;
	}

	/**
	 *  Get the resourceIdentifier.
	 *  @return the resourceIdentifier.
	 */
	public IResourceIdentifier getResourceIdentifier()
	{
		return rid;
	}

	/**
	 *  Set the resourceIdentifier.
	 *  @param resourceIdentifier The resourceIdentifier to set.
	 */
	public void setResourceIdentifier(IResourceIdentifier resourceIdentifier)
	{
		this.rid = resourceIdentifier;
	}
}
