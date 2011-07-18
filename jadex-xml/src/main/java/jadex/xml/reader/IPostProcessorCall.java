package jadex.xml.reader;

/* $if !android $ */
import javax.xml.stream.XMLStreamException;
/* $else $
import javaxx.xml.stream.XMLStreamException;
$endif $ */

/**
 *  Interface used to schedule post processor calls.
 */
// Required because Runnable doesn't allow checked exceptions.
public interface IPostProcessorCall
{
	public void	callPostProcessor()	throws XMLStreamException;
}
