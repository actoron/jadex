package jadex.base.relay;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  Facade to the relay handler with operations to feed the dashboard, i.e. status page.
 */
public class RelayDashboard
{
	//-------- constants --------
	
	/** The JSON excludes. */
	protected static final Map<Class<?>, Set<String>>	JSON_EXCLUDES;
	
	static
	{
		Map<Class<?>, Set<String>>	excludes	= new LinkedHashMap<Class<?>, Set<String>>();
		excludes.put(PlatformInfo.class, Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList("awarenessInfo", "properties"))));
		JSON_EXCLUDES	= Collections.unmodifiableMap(excludes);
	}
	
	//-------- attributes --------
	
	/** The relay handler. */
	protected RelayHandler	handler;
	
	//-------- constructors --------
	
	/**
	 *  Create a dashboard.
	 */
	public RelayDashboard(RelayHandler handler)
	{
		this.handler	= handler;
	}
	
	//-------- methods --------
	
	/**
	 *  Write the currently connected platforms (including peer-connected platforms)
	 *  as JSON to the given output stream.
	 *  @param out	The output stream.
	 */
	public void	writeAllPlatforms(OutputStream out)	throws IOException
	{
		byte[]	val	= JsonTraverser.objectToByteArray(handler.getCurrentPlatforms(), null, "UTF-8", false, JSON_EXCLUDES);
		out.write(val);
	}
}
