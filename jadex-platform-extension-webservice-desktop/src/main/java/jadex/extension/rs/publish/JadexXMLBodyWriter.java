package jadex.extension.rs.publish;

import jadex.xml.bean.JavaWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 *  Body writer for Jersey. Allows to use the Jadex XML Codec
 *  for producing XML for parameters. 
 */
@Provider
public class JadexXMLBodyWriter implements MessageBodyWriter<Object>
{
	/**
	 *  Test if the writer can handle the content.
	 */
	public boolean isWriteable(Class<?> type, Type gtype,
		Annotation[] annotations, MediaType mediatype)
	{
		return mediatype.equals(MediaType.APPLICATION_XML_TYPE);
//			||  mediatype.equals(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
	}
	
	/**
	 *  Write the object to the output stream.
	 */
	public void writeTo(Object t, Class<?> type, Type generictype,
		Annotation[] annotations, MediaType mediatype,
		MultivaluedMap<String, Object> httpheaders,
		OutputStream entityStream) throws IOException, WebApplicationException
	{
		JavaWriter.objectToOutputStream(t, entityStream, Thread.currentThread().getContextClassLoader());
	}
	
	/**
	 *  Get the size of the result.
	 */
	public long getSize(Object t, Class<?> type, Type genericType,
		Annotation[] annotations, MediaType mediaType)
	{
		return JavaWriter.objectToByteArray(t, Thread.currentThread().getContextClassLoader()).length;
	}
}
