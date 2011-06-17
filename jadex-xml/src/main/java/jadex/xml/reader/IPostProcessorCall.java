package jadex.xml.reader;

import javax.xml.stream.XMLStreamException;

/**
 *  Interface used to schedule post processor calls.
 */
// Required because Runnable doesn't allow checked exceptions.
public interface IPostProcessorCall
{
	public void	callPostProcessor()	throws XMLStreamException;
}
