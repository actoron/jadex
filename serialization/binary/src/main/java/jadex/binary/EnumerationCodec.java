package jadex.binary;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  An enumeration processor allows for traversing enumerations.
 */
public class EnumerationCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return SReflect.isSupertype(Enumeration.class, clazz);
	}
	
	/**
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	public Object createObject(Class<?> clazz, IDecodingContext context)
	{
		Vector vec = new Vector();
		int length = (int) context.readVarInt();
		int count = 0;
		
		//FIXME: Filling in subobjects now since adding them later is not possible.
		// May not result in correct behavior but fix would require special support.
		while (count < length)
		{
			int index = (int) context.readVarInt();
			while (count < index)
			{
				vec.add(null);
				++count;
			}
			Object element = SBinarySerializer.decodeObject(context);
			vec.add(element);
			++count;
		}
		return vec.elements();
	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		Enumeration en = (Enumeration)object;
		
		int count = 0;
		Vector copy = new Vector();
		for(; en.hasMoreElements(); )
		{
			Object val = en.nextElement();
			++count;
			copy.add(val);
		}
		
		ec.writeVarInt(count);
		
		count = 0;
		for (Object val : copy)
		{
			if (val != null)
			{
				ec.writeVarInt(count);
				Class valclazz = val.getClass();
				traverser.doTraverse(val, valclazz, preprocessors, processors, null, null, ec);
			}
			++count;
		}
		
		Enumeration ret = copy.elements();
		
		return ret;
	}
}
