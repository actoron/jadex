package jadex.rules.state.io.xml;

import jadex.rules.state.IOAVState;

import java.io.OutputStream;

/**
 *  Class for writing OAV states to streams as XML.
 */
public class Writer
{
	/**
	 *  Write the given object from the given state
	 *  as XML to the given stream.
	 *  @param out	The output stream.
	 *  @param state	The OAV state.
	 *  @param object	The root object to export.
	 *  @param xmlmapping	The xml mapping.
	 */
	// Todo: support non-tree state structures (currently recurring objects are just rewritten as copies)
	public void	write(OutputStream out, IOAVState state, Object object)
	{
//		OAVObjectType	type	= state.getType(object);
//		while(type!=null)
//		{
//			for(Iterator)
	}
}
