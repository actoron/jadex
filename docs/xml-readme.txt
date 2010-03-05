Jadex XML is a XML databinding framework supporting two main use cases

a) reading and writing arbitrary XML documents according to mapping information
b) serializing and deserializing Java beans to and from its own XML representation

Usage for a)
Define the type mappings and create reader/writer
Reader xmlreader = new Reader(new BeanObjectReaderHandler(typeinfos));
Object object = xmlreader.read(is, null, null);
or
Writer xmlwriter = new Writer(new BeanObjectWriterHandler(typeinfos, false, true), false);
String xml = xmlwriter.objectToXML(xmlwriter, object, null);

Usage for b)
String xml = JavaWriter.objectToXML(object, null);
or
Object ro = JavaReader.objectFromXML(xml, null);

		
		


