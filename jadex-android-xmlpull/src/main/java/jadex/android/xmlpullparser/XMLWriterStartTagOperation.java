package jadex.android.xmlpullparser;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamWriter;

public class XMLWriterStartTagOperation implements IXMLWriterOperation {
	
	private String name;
	private String namespace;

	public XMLWriterStartTagOperation(String namespace, String name) {
		this.namespace = namespace;
		this.name = name;
	}

	@Override
	public void execute(XmlSerializer writer) throws IllegalArgumentException, IllegalStateException, IOException {
		writer.startTag(namespace, name);
	}

}
