package jadex.transformation.jsonserializer.processors;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Deals with strings during writing.
 */
public class JsonToStringProcessor extends AbstractJsonProcessor
{
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	protected boolean isApplicable(Object object, Type type, ClassLoader targetcl, JsonReadContext context)
	{
		// Not applicable during read
		return false;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	protected boolean isApplicable(Object object, Type type, ClassLoader targetcl, JsonWriteContext context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isStringConvertableType(clazz) 
			|| SReflect.isSupertype(URL.class, clazz)
			|| SReflect.isSupertype(URI.class, clazz);
	}
	
	/**
	 *  Read an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object readObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonReadContext context)
	{
		// Not applicable during read
		return null;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object writeObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonWriteContext wr)
	{
		if(object instanceof String)
		{
			wr.writeString(object.toString());
		}
		else
		{
			if(object instanceof URI || object instanceof URL)
				wr.addObject(wr.getCurrentInputObject());
			
			// Allow write boolean directly without class
			if(!wr.isWriteClass() || object instanceof Boolean || SReflect.isBasicType(SReflect.getClass(type)))// && !wr.isWriteId())
			{
				if(object instanceof Number
					|| object instanceof Boolean
					|| SReflect.isBasicType(SReflect.getClass(type)))
				{
					wr.write(object.toString());
				}
				else
				{
					wr.writeString(object.toString());
				}
			}
			else
			{
				wr.write("{");
				if(object instanceof Number
					|| object instanceof Boolean)
				{
					wr.write("\"value\":"+object.toString());
				}
				else
				{
					wr.writeNameString("value", object.toString());
				}
				if(wr.isWriteClass())
					wr.write(",").writeClass(object.getClass());
//				if(wr.isWriteId())
//					wr.write(",").writeId();
				wr.write("}");
			}
		}
		
		return object;
	}
}
