package jadex.commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;

/**
 *  Extension of the standard object input stream, which does
 *  not provide any means to set the classloader to use.
 *  
 *  cf. bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4340158
 */
public class CLObjectInputStream extends java.io.ObjectInputStream 
{
	//-------- attributes --------
	
	/** The classloader. */
	protected ClassLoader classloader;

	//-------- constructors --------
	
	/**
	 *  Create a new object input stream
	 *  @param in The in stream.
	 *  @param classloader The classloader.
	 *  @throws IOException The exception.
	 */
	public CLObjectInputStream(InputStream in, ClassLoader classloader) throws IOException 
	{
		super(in);
		this.classloader = classloader;
	}

	//-------- methods --------
	
	/**
	 *  Resolve a class.
	 *  @param desc The object stream class.
	 */
	protected Class resolveClass(ObjectStreamClass desc) throws ClassNotFoundException 
	{
		return Class.forName(desc.getName(), false, classloader);
	}
}
