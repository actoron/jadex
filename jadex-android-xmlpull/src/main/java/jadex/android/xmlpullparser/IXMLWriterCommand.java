package jadex.android.xmlpullparser;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

/**
 * Commands implementing this interface can execute a cached XMLWriter
 * Operation.
 */
public interface IXMLWriterCommand {
	/**
	 * Execute the Operation using the given XMLSerializer.
	 * 
	 * @param serializer
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	void execute(XmlSerializer serializer) throws IllegalArgumentException,
			IllegalStateException, IOException;
}
