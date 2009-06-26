package jadex.bridge;

import jadex.commons.xml.BeanObjectHandler;
import jadex.commons.xml.LinkInfo;
import jadex.commons.xml.Reader;
import jadex.commons.xml.TypeInfo;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *  Read properties from xml.
 */
public class XMLPropertiesReader
{
//-------- attributes --------
	
	/** The singleton reader instance. */
	protected static Reader	reader;
	
	// Initialize reader instance.
	static
	{
		Set typeinfos = new HashSet();
		typeinfos.add(new TypeInfo("property", Property.class, null, "value"));
		typeinfos.add(new TypeInfo("properties", Properties.class));
		
		Set linkinfos = new HashSet();
		linkinfos.add(new LinkInfo("properties", "subproperties"));
		
		Set ignoredattrs = new HashSet();
		ignoredattrs.add("schemaLocation");
		
		reader = new Reader(new BeanObjectHandler(), typeinfos, linkinfos, ignoredattrs);
	}
	
	/**
	 *  Read properties from xml.
 	 */
	public static Properties readProperties(InputStream input, ClassLoader classloader) throws Exception
	{
		Properties ret = (Properties)reader.read(input, classloader, null);
		return ret;
	}
	
	/**
	 *  Read properties from xml.
 	 * /
	public static Properties readProperties(InputStream input, ClassLoader classloader) throws Exception
	{
		XMLInputFactory	factory	= XMLInputFactory.newInstance();
		XMLStreamReader	parser	= factory.createXMLStreamReader(input);
		Properties	root	= null;
		Map idprops = new HashMap();
		List stack = new ArrayList();
		List refs = new ArrayList();
		
		while(parser.hasNext())
		{
			int	next	= parser.next();
			switch(next)
			{
				case XMLStreamReader.START_ELEMENT:
					if(parser.getLocalName().equals("properties"))
					{
						String name	= parser.getAttributeValue(null, "name");
						String type	= parser.getAttributeValue(null, "type");
						String id	= parser.getAttributeValue(null, "id");
						Properties	props	= new Properties(name, type, id);
						if(id!=null && id.length()>0)
							idprops.put(id, props);
//						System.out.println("Found properties: "+props);
						
						if(root==null)
							root	= props;
						if(!stack.isEmpty())
							((Properties)stack.get(stack.size()-1)).addSubproperties(props);
						stack.add(props);
					}
					else if(parser.getLocalName().equals("property"))
					{
						String	name	= parser.getAttributeValue(null, "name");
						String	type	= parser.getAttributeValue(null, "type");
						String	value	= parser.getElementText();
						Property	prop	= new Property(name, type, value);
//						System.out.println("Found property: "+prop);
						
						if(!stack.isEmpty())
							((Properties)stack.get(stack.size()-1)).addProperty(prop);
					}
					else if(parser.getLocalName().equals("propertiesref"))
					{
						String	name	= parser.getAttributeValue(null, "name");
						String	id	= parser.getAttributeValue(null, "ref");
						String	file = parser.getAttributeValue(null, "file");
						Properties props = (Properties)stack.get(stack.size()-1);
//						System.out.println("Found propref: "+name+" "+id+" "+file);
						
						refs.add(new Object[]{props, name, id, file});
					}
					break;
				case XMLStreamReader.END_ELEMENT:
					if(parser.getLocalName().equals("properties"))
					{
						stack.remove(stack.size()-1);
					}
					break;
			}
		}
		parser.close();
		
		for(int i=0; i<refs.size(); i++)
		{
			Object[] ref = (Object[])refs.get(i);
			Properties props = (Properties)ref[0];
//			String name = (String)ref[1];
			String id = (String)ref[2];
			String file = (String)ref[3];
			
			if(id!=null)
			{
				Properties refprops = (Properties)idprops.get(id);
				if(refprops==null)
					throw new RuntimeException("Referenced properties not found: "+id);
				
				// todo: support special refprops which may have a new name
				props.addSubproperties(refprops);
			}
			else if(file!=null)
			{
				Properties refprops = readProperties(SUtil.getResource0(file, classloader), classloader);
				
				// todo: support special refprops which may have a new name
				props.addSubproperties(refprops);
			}
		}
		
		return root;
	}*/
	
	/**
	 *  Write properties to XML.
	 */
	public static void	writeProperties(Properties props, OutputStream out)	throws Exception
	{
		XMLOutputFactory	factory	= XMLOutputFactory.newInstance();
		XMLStreamWriter	writer	= factory.createXMLStreamWriter(out);
		writer.writeStartDocument();
		internalWriteProperties(props, writer, 0);
		writer.writeEndDocument();
		writer.close();
	}

	protected static void internalWriteProperties(Properties props, XMLStreamWriter writer, int indent) throws XMLStreamException
	{
		String lf = (String)System.getProperty("line.separator");
		writer.writeCharacters(lf);
		for(int i=0; i<indent; i++)
			writer.writeCharacters("\t");
		writer.writeStartElement("properties");
		if(props.getId()!=null)
			writer.writeAttribute("id", props.getId());
		if(props.getType()!=null)
			writer.writeAttribute("type", props.getType());
		if(props.getName()!=null)
			writer.writeAttribute("name", props.getName());

		Property[]	ps	= props.getProperties();
		for(int i=0; i<ps.length; i++)
		{
			writer.writeCharacters(lf);
			for(int j=0; j<=indent; j++)
				writer.writeCharacters("\t");
			writer.writeStartElement("property");
			if(ps[i].getType()!=null)
				writer.writeAttribute("type", ps[i].getType());
			if(ps[i].getName()!=null)
				writer.writeAttribute("name", ps[i].getName());
			if(ps[i].getValue()!=null)
			{
				// Use CData for XML values,
				// as it produces output that looks better. 
				if(ps[i].getValue().indexOf('<')!=-1)
					writer.writeCData(ps[i].getValue());
				else
					writer.writeCharacters(ps[i].getValue());
			}
			writer.writeEndElement();
		}

		Properties[]	subs	= props.getSubproperties();
		for(int i=0; i<subs.length; i++)
		{
			internalWriteProperties(subs[i], writer, indent+1);
		}

		writer.writeCharacters(lf);
		for(int i=0; i<indent; i++)
			writer.writeCharacters("\t");
		writer.writeEndElement();
	}

	/**
	 * 
	 *  @param args
	 *  @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		InputStream	input	= new FileInputStream(args[0]);
		Properties	props	= readProperties(input, null);
		System.out.println(props);
	}
}
