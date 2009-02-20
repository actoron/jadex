package jadex.adapter.base.appdescriptor.reader;

import javax.xml.stream.XMLStreamReader;

/**
 * 
 */
public interface IObjectHandler
{
	/**
	 * 
	 */
	public Object createObject(XMLStreamReader parser) throws Exception;
	
	/**
	 * 
	 */
	public void linkObject(XMLStreamReader parser, Object elem, Object parent) throws Exception;
}
