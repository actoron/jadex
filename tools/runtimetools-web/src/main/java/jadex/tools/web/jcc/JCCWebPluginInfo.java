package jadex.tools.web.jcc;

import jadex.bridge.service.IServiceIdentifier;

/**
 *  Infos about a JCC web plugin.
 */
public class JCCWebPluginInfo
{
	/** The plugin name. */
	protected String name;
	
	/** The plugin icon. */
	protected byte[] icon;
	
	/** Can the plugin be accessed without login? */
	protected boolean unrestricted;
	
	/** The plugin priority. */
	protected int priority;
	
	/** The sid. */
	protected IServiceIdentifier sid;

	/**
	 *  Create a new plugin info.
	 */
	public JCCWebPluginInfo()
	{
		// bean const.
	}

	/**
	 *  Create a new plugin info.
	 */
	public JCCWebPluginInfo(String name, byte[] icon, int priority, boolean unrestricted, IServiceIdentifier sid)
	{
		this.name = name;
		this.icon = icon;
		this.priority = priority;
		this.unrestricted = unrestricted;
		this.sid = sid;
	}
	
	/**
	 *  Get the priority.
	 *  @return The priority
	 */
	public int getPriority()
	{
		return priority;
	}

	/**
	 *  Set the priority.
	 *  @param priority The priority to set
	 */
	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	/**
	 *  Get the sid.
	 *  @return The sid
	 */
	public IServiceIdentifier getSid()
	{
		return sid;
	}

	/**
	 *  Set the sid.
	 *  @param sid The sid to set
	 */
	public void setSid(IServiceIdentifier sid)
	{
		this.sid = sid;
	}

	/**
	 *  Get the name.
	 *  @return The name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the icon.
	 *  @return The icon
	 */
	public byte[] getIcon()
	{
		return icon;
	}

	/**
	 *  Set the icon.
	 *  @param icon The icon to set
	 */
	public void setIcon(byte[] icon)
	{
		this.icon = icon;
	}

	/**
	 *  Get the unrestricted.
	 *  @return The unrestricted
	 */
	public boolean isUnrestricted()
	{
		return unrestricted;
	}

	/**
	 *  Set the unrestricted.
	 *  @param unrestricted The unrestricted to set
	 */
	public void setUnrestricted(boolean unrestricted)
	{
		this.unrestricted = unrestricted;
	}
}
