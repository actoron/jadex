package jadex.android.xmlpullparser;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

/**
 * startTag Operation
 */
public class XMLWriterStartTagCommand implements IXMLWriterCommand {
	
	private String name;
	private String namespace;

	/**
	 * Constructor
	 * @param namespace
	 * @param name
	 */
	public XMLWriterStartTagCommand(String namespace, String name) {
		this.namespace = namespace;
		this.name = name;
	}

	@Override
	public void execute(XmlSerializer writer) throws IllegalArgumentException, IllegalStateException, IOException {
		writer.startTag(namespace, name);
	}

}
