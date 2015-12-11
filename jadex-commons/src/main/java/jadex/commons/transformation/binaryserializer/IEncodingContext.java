package jadex.commons.transformation.binaryserializer;

import java.util.List;
import java.util.Set;

import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  Encoding context interface.
 *
 */
public interface IEncodingContext
{
	/**
	 *  Returns the preprocessors.
	 *  @return The preprocessors
	 */
	public List<ITraverseProcessor> getPreprocessors();
	
	/**
	 * Gets the classloader.
	 * @return The classloader.
	 */
	public ClassLoader getClassLoader();
	
	/**
	 *  Get the rootobject.
	 *  @return the rootobject.
	 */
	public Object getRootObject();
	
	/**
	 *  Returns the user context.
	 *  @return The user context.
	 */
	public Object getUserContext();
	
	/**
	 *  Returns the non-inner class cache.
	 *  @return The non-inner class cache.
	 */
	public Set<Class> getNonInnerClassCache();
	
	/**
	 *  Puts the context in a state where the next call to
	 *  writeClass is ignored.
	 *  
	 *  @param state If true, the next class write will be ignored and the state reset.
	 */
	public void setIgnoreNextClassWrite(boolean state);
	
	/**
	 *  Writes a byte.
	 *  @param b The byte.
	 */
	public void writeByte(byte b);
	
	/**
	 *  Writes a byte array.
	 *  @param b The byte array.
	 */
	public void write(byte[] b);
	
	/**
	 *  Writes a boolean value.
	 *  
	 *  @param bool The value.
	 */
	public void writeBoolean(boolean bool);
	
	/**
	 *  Writes a string to the context.
	 * 
	 *  @param string The string.
	 */
	public void writeString(String string);
	
	/**
	 * Writes a variable integer to the encoding context.
	 * 
	 * @param value The value.
	 */
	public void writeVarInt(long value);
	
	/**
	 *  Writes a signed variable integer to the encoding context.
	 *  
	 *  @param value The value.
	 */
	public void writeSignedVarInt(long value);
	
	/**
	 * Writes a class to the context.
	 * 
	 * @param clazz The class.
	 */
	public void writeClass(Class<?> clazz);
	
	/**
	 *  Writes the name of a class.
	 *  
	 *  @param name The name of the class.
	 */
	public int writeClassname(String name);
	
	/**
	 *  Returns the number of bytes written.
	 *  
	 *  @return The number of bytes written.
	 */
	public long getWrittenBytes();
}
