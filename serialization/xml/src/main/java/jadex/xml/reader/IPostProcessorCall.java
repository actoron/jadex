package jadex.xml.reader;

/* if_not[android] */
//import javax.xml.stream.XMLStreamException;
/* else[android]
import javaxx.xml.stream.XMLStreamException;
end[android] */

/**
 *  Interface used to schedule post processor calls.
 */
// Required because Runnable doesn't allow checked exceptions.
public interface IPostProcessorCall
{
//	public void	callPostProcessor()	throws XMLStreamException;
	public void	callPostProcessor()	throws Exception;
}
