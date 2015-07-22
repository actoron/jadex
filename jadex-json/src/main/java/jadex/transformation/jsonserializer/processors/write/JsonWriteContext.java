package jadex.transformation.jsonserializer.processors.write;

import jadex.commons.SReflect;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * 
 */
public class JsonWriteContext
{
	protected StringBuffer buffer = new StringBuffer();
	
	protected boolean writeclass = true;
	
	/**
	 * 
	 */
	public JsonWriteContext(boolean writeclass)
	{
		this.writeclass = writeclass;
	}

	/**
	 *  Write a string to the buffer.
	 */
	public JsonWriteContext write(String str)
	{
		buffer.append(str);
		return this;
	}
	
	/**
	 *  Write the classname.
	 *  @param object
	 */
	public void writeClass(Class<?> clazz)
	{
		write("\"").write(JsonTraverser.CLASSNAME_MARKER).write("\"");
		write(":");
		write("\"").write(SReflect.getClassName(clazz)).write("\"");
	}
	
	/**
	 * 
	 */
	public String getString()
	{
		return buffer.toString();
	}

	/**
	 *  Get the writeclass. 
	 *  @return The writeclass
	 */
	public boolean isWriteClass()
	{
		return writeclass;
	}
	
	
}
