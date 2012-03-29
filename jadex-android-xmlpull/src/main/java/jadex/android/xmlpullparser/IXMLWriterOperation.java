package jadex.android.xmlpullparser;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamWriter;

public interface IXMLWriterOperation {
	void execute(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException;
}
