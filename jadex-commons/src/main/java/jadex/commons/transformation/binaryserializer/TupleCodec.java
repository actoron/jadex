package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.Tuple;
import jadex.commons.Tuple2;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.util.List;
import java.util.Map;

/**
 *  Codec for encoding and decoding URL objects.
 *
 */
public class TupleCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class clazz)
	{
		return SReflect.isSupertype(Tuple.class, clazz);
	}
	
	/**
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	public Object createObject(Class clazz, DecodingContext context)
	{
		// FIXME: Incorrect behavior in case of self-referencing tuples, similar to the MultiMap problem.
		Object[] entities = (Object[]) BinarySerializer.decodeObject(context);
		Tuple ret = null;
		if (clazz.equals(Tuple2.class))
			ret = new Tuple2(entities[0], entities[1]);
		else
			ret =  new Tuple(entities);
		return ret;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return isApplicable(clazz);
	}
	
	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
			Traverser traverser, Map<Object, Object> traversed, boolean clone, EncodingContext ec)
	{
		Object[] entities = ((Tuple) object).getEntities();
		traverser.traverse(entities, entities.getClass(), traversed, processors, clone, ec);
		
		return object;
	}
}
