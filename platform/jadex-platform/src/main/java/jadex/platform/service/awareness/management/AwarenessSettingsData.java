package jadex.platform.service.awareness.management;

import java.net.InetAddress;

import jadex.commons.transformation.annotations.IncludeFields;

/**
 * The awareness settings transferred between GUI and agent.
 */
@IncludeFields
public class AwarenessSettingsData {
	/** The inet address. */
	public InetAddress address;

	/** The port. */
	public int port;

	/** The delay. */
	public long delay;

	/** The fast awareness flag. */
	public boolean fast;

	/** The autocreate flag. */
	public boolean autocreate;

	/** The autocreate flag. */
	public boolean autodelete;

	/** The includes list. */
	public String[] includes;

	/** The excludes list. */
	public String[] excludes;
}